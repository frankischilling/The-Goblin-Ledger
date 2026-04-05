package com.goblintracker;

import com.goblintracker.detection.DefaultGoblinAreaResolver;
import com.goblintracker.detection.DefaultGoblinTargetMatcher;
import com.goblintracker.detection.GoblinAreaResolver;
import com.goblintracker.detection.GoblinKillService;
import com.goblintracker.detection.GoblinTargetMatcher;
import com.goblintracker.model.GoblinKillRecord;
import com.goblintracker.model.GoblinStatsState;
import com.goblintracker.persistence.ConfigGoblinStatsRepository;
import com.goblintracker.persistence.GoblinStatsRepository;
import com.goblintracker.ui.GoblinMilestoneNotifier;
import com.goblintracker.ui.GoblinNavigation;
import com.goblintracker.ui.GoblinPanel;
import com.google.inject.Provides;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ItemComposition;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.events.NpcLootReceived;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
	name = "The Goblin Ledger",
	description = "Campaign ledger for the one million goblin objective",
	tags = {"goblin", "ledger", "tracker", "milestone", "combat", "war"}
)
public class GoblinKillTrackerPlugin extends Plugin
{
	public static final String CONFIG_GROUP = "goblinkilltracker";
	private static final String RESET_SESSION_COUNT_KEY = "resetSessionCount";
	private static final String RESET_TRIP_COUNT_KEY = "resetTripCount";
	private static final String SHOW_SIDEBAR_KEY = "showSidebar";

	@Inject
	private Client client;

	@Inject
	private GoblinKillTrackerOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private GoblinKillTrackerConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private ItemManager itemManager;

	@Inject
	private GoblinKillService killService;

	@Inject
	private GoblinAreaResolver areaResolver;

	@Inject
	private GoblinStatsRepository statsRepository;

	@Inject
	private GoblinPanel panel;

	@Inject
	private GoblinNavigation navigation;

	@Inject
	private GoblinMilestoneNotifier milestoneNotifier;

	private final GoblinStatsState statsState = new GoblinStatsState();

	private String activeProfileName;
	private boolean resetSessionOnNextLogin;
	private NavigationButton navigationButton;

	@Override
	protected void startUp()
	{
		statsState.resetAll();
		killService.clear();
		resetSessionOnNextLogin = false;
		syncPlayerProfile();
		milestoneNotifier.reset(statsState.getLifetimeKills());
		overlayManager.add(overlay);
		updateSidebarRegistration();
		refreshUi();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		removeSidebar();
		statsState.resetAll();
		activeProfileName = null;
		resetSessionOnNextLogin = false;
		killService.clear();
		milestoneNotifier.reset(0);
	}

	@Provides
	GoblinKillTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GoblinKillTrackerConfig.class);
	}

	@Provides
	GoblinTargetMatcher provideTargetMatcher(DefaultGoblinTargetMatcher targetMatcher)
	{
		return targetMatcher;
	}

	@Provides
	GoblinAreaResolver provideAreaResolver(DefaultGoblinAreaResolver areaResolver)
	{
		return areaResolver;
	}

	@Provides
	GoblinStatsRepository provideStatsRepository(ConfigGoblinStatsRepository repository)
	{
		return repository;
	}

	@Subscribe
	public void onNpcLootReceived(NpcLootReceived event)
	{
		handleLootNpc(event.getNpc(), event.getItems());
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		handleNpcDespawn(event.getNpc());
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			syncPlayerProfile();
			if (resetSessionOnNextLogin)
			{
				resetSessionCount();
				resetTripCount();
				resetSessionOnNextLogin = false;
			}
		}
		else if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			activeProfileName = null;
			statsState.setLifetimeKills(0);
			killService.clear();
			resetSessionOnNextLogin = true;
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!CONFIG_GROUP.equals(event.getGroup()))
		{
			return;
		}

		if (RESET_SESSION_COUNT_KEY.equals(event.getKey()) && Boolean.parseBoolean(event.getNewValue()))
		{
			resetSessionCount();
			configManager.setConfiguration(CONFIG_GROUP, RESET_SESSION_COUNT_KEY, false);
			return;
		}

		if (RESET_TRIP_COUNT_KEY.equals(event.getKey()) && Boolean.parseBoolean(event.getNewValue()))
		{
			resetTripCount();
			configManager.setConfiguration(CONFIG_GROUP, RESET_TRIP_COUNT_KEY, false);
			return;
		}

		if (SHOW_SIDEBAR_KEY.equals(event.getKey()))
		{
			updateSidebarRegistration();
		}

		refreshUi();
	}

	void handleLootNpc(NPC npc)
	{
		handleLootNpc(npc, null);
	}

	void handleLootNpc(NPC npc, Collection<ItemStack> lootItems)
	{
		syncPlayerProfileIfNeeded();
		killService.processLootNpc(npc).ifPresent(killRecord -> {
			Map<Integer, Long> lootTotals = summarizeLoot(lootItems);
			applyKillRecord(enrichKillRecord(npc, killRecord, lootTotals), lootTotals);
		});
	}

	void handleNpcDespawn(NPC npc)
	{
		syncPlayerProfileIfNeeded();
		killService.processDespawnNpc(npc).ifPresent(killRecord ->
			applyKillRecord(enrichKillRecord(npc, killRecord, Map.of()), Map.of()));
	}

	void syncPlayerProfile()
	{
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null || localPlayer.getName() == null || localPlayer.getName().isBlank())
		{
			activeProfileName = null;
			statsState.setLifetimeKills(0);
			milestoneNotifier.reset(0);
			refreshUi();
			return;
		}

		activeProfileName = localPlayer.getName();
		statsState.setLifetimeKills(statsRepository.loadLifetimeKills());
		milestoneNotifier.reset(statsState.getLifetimeKills());
		refreshUi();
	}

	public void resetSessionCount()
	{
		statsState.resetSession();
		refreshUi();
	}

	public void resetTripCount()
	{
		statsState.resetTrip();
		refreshUi();
	}

	public GoblinKillTrackerConfig getConfig()
	{
		return config;
	}

	public int getSessionGoblinKills()
	{
		return statsState.getSessionKills();
	}

	public int getTripGoblinKills()
	{
		return statsState.getTripKills();
	}

	public int getLifetimeGoblinKills()
	{
		return statsState.getLifetimeKills();
	}

	public int getSessionKillsPerHour()
	{
		return statsState.getSessionKillsPerHour();
	}

	public Map<String, Integer> getAreaKillCounts()
	{
		return statsState.getAreaKillCounts();
	}

	public Map<Integer, Long> getLootTotals()
	{
		return statsState.getLootTotals();
	}

	public List<GoblinKillRecord> getRecentKills()
	{
		return statsState.getRecentKills();
	}

	public String getItemName(int itemId)
	{
		try
		{
			ItemComposition composition = itemManager.getItemComposition(itemId);
			if (composition == null || composition.getName() == null || composition.getName().isBlank())
			{
				return "Item " + itemId;
			}

			return composition.getName();
		}
		catch (RuntimeException | AssertionError ex)
		{
			return "Item " + itemId;
		}
	}

	public String getActiveProfileName()
	{
		return activeProfileName;
	}

	public boolean hasActiveProfile()
	{
		return activeProfileName != null;
	}

	private void syncPlayerProfileIfNeeded()
	{
		if (!hasActiveProfile())
		{
			syncPlayerProfile();
		}
	}

	private void applyKillRecord(GoblinKillRecord killRecord, Map<Integer, Long> lootTotals)
	{
		statsState.recordKill(killRecord, lootTotals);

		if (hasActiveProfile())
		{
			statsRepository.saveLifetimeKills(statsState.getLifetimeKills());
		}

		if (milestoneNotifier.checkAndNotify(statsState.getLifetimeKills()))
		{
			overlay.triggerFlash();
		}

		refreshUi();
	}

	private GoblinKillRecord enrichKillRecord(NPC npc, GoblinKillRecord baseRecord, Map<Integer, Long> lootTotals)
	{
		String areaName = areaResolver.resolveArea(npc);
		int itemCount = lootTotals.size();
		long totalLootQuantity = lootTotals.values().stream().mapToLong(Long::longValue).sum();
		return baseRecord.withDetails(areaName, itemCount, totalLootQuantity);
	}

	private Map<Integer, Long> summarizeLoot(Collection<ItemStack> lootItems)
	{
		if (lootItems == null || lootItems.isEmpty())
		{
			return Map.of();
		}

		Map<Integer, Long> totals = new HashMap<>();
		for (ItemStack item : lootItems)
		{
			if (item == null)
			{
				continue;
			}

			totals.merge(item.getId(), (long) item.getQuantity(), Long::sum);
		}
		return totals;
	}

	private void updateSidebarRegistration()
	{
		if (config.showSidebar())
		{
			addSidebar();
			return;
		}

		removeSidebar();
	}

	private void addSidebar()
	{
		if (navigationButton != null)
		{
			return;
		}

		navigationButton = navigation.createNavigation(panel);
		clientToolbar.addNavigation(navigationButton);
	}

	private void removeSidebar()
	{
		if (navigationButton == null)
		{
			return;
		}

		clientToolbar.removeNavigation(navigationButton);
		navigationButton = null;
	}

	private void refreshUi()
	{
		panel.refresh();
	}
}
