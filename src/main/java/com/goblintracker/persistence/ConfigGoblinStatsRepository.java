package com.goblintracker.persistence;

import com.goblintracker.GoblinKillTrackerPlugin;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.config.ConfigManager;

@Singleton
public class ConfigGoblinStatsRepository implements GoblinStatsRepository
{
	private static final String LIFETIME_KILLS_KEY = "lifetimeGoblinKills";
	private static final String LIFETIME_LOOT_TOTALS_KEY = "lifetimeLootTotals";
	private static final String TODAY_LOOT_DATE_KEY = "todayLootDate";
	private static final String TODAY_LOOT_TOTALS_KEY = "todayLootTotals";
	private static final String MILESTONE_REACHED_AT_MS_KEY = "milestoneReachedAtMs";
	private static final String DAILY_KILL_COUNTS_KEY = "dailyKillCounts";
	private static final String AREA_KILL_COUNTS_KEY = "areaKillCounts";

	private final ConfigManager configManager;

	@Inject
	public ConfigGoblinStatsRepository(ConfigManager configManager)
	{
		this.configManager = configManager;
	}

	@Override
	public int loadLifetimeKills()
	{
		Integer storedKills = configManager.getRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			LIFETIME_KILLS_KEY,
			Integer.class);
		return storedKills == null ? 0 : storedKills;
	}

	@Override
	public void saveLifetimeKills(int lifetimeKills)
	{
		configManager.setRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			LIFETIME_KILLS_KEY,
			Math.max(0, lifetimeKills));
	}

	@Override
	public Map<Integer, Long> loadLifetimeLootTotals()
	{
		String serialized = configManager.getRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			LIFETIME_LOOT_TOTALS_KEY,
			String.class);
		return deserializeLootTotals(serialized);
	}

	@Override
	public void saveLifetimeLootTotals(Map<Integer, Long> lootTotals)
	{
		configManager.setRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			LIFETIME_LOOT_TOTALS_KEY,
			serializeLootTotals(lootTotals));
	}

	@Override
	public Map<Integer, Long> loadTodayLootTotals(String dateKey)
	{
		String storedDate = configManager.getRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			TODAY_LOOT_DATE_KEY,
			String.class);

		if (dateKey == null || dateKey.isBlank() || storedDate == null || !dateKey.equals(storedDate))
		{
			return Map.of();
		}

		String serialized = configManager.getRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			TODAY_LOOT_TOTALS_KEY,
			String.class);
		return deserializeLootTotals(serialized);
	}

	@Override
	public void saveTodayLootTotals(String dateKey, Map<Integer, Long> lootTotals)
	{
		if (dateKey == null || dateKey.isBlank())
		{
			return;
		}

		configManager.setRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			TODAY_LOOT_DATE_KEY,
			dateKey);

		configManager.setRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			TODAY_LOOT_TOTALS_KEY,
			serializeLootTotals(lootTotals));
	}

	@Override
	public Map<Integer, Long> loadMilestoneReachedAtMs()
	{
		String serialized = configManager.getRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			MILESTONE_REACHED_AT_MS_KEY,
			String.class);
		return deserializeLootTotals(serialized);
	}

	@Override
	public void saveMilestoneReachedAtMs(Map<Integer, Long> milestoneTimes)
	{
		configManager.setRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			MILESTONE_REACHED_AT_MS_KEY,
			serializeLootTotals(milestoneTimes));
	}

	@Override
	public Map<String, Integer> loadDailyKillCounts()
	{
		String serialized = configManager.getRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			DAILY_KILL_COUNTS_KEY,
			String.class);
		return deserializeDailyKillCounts(serialized);
	}

	@Override
	public void saveDailyKillCounts(Map<String, Integer> dailyKillCounts)
	{
		configManager.setRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			DAILY_KILL_COUNTS_KEY,
			serializeDailyKillCounts(dailyKillCounts));
	}

	@Override
	public Map<String, Integer> loadAreaKillCounts()
	{
		String serialized = configManager.getRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			AREA_KILL_COUNTS_KEY,
			String.class);
		return deserializeAreaKillCounts(serialized);
	}

	@Override
	public void saveAreaKillCounts(Map<String, Integer> areaKillCounts)
	{
		configManager.setRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			AREA_KILL_COUNTS_KEY,
			serializeAreaKillCounts(areaKillCounts));
	}

	private static String serializeLootTotals(Map<Integer, Long> lootTotals)
	{
		if (lootTotals == null || lootTotals.isEmpty())
		{
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (Map.Entry<Integer, Long> entry : lootTotals.entrySet())
		{
			if (entry == null || entry.getKey() == null)
			{
				continue;
			}

			long quantity = Math.max(0L, entry.getValue() == null ? 0L : entry.getValue());
			if (builder.length() > 0)
			{
				builder.append(',');
			}
			builder.append(entry.getKey()).append(':').append(quantity);
		}
		return builder.toString();
	}

	private static Map<Integer, Long> deserializeLootTotals(String serialized)
	{
		if (serialized == null || serialized.isBlank())
		{
			return Map.of();
		}

		Map<Integer, Long> totals = new HashMap<>();
		String[] parts = serialized.split(",");
		for (String part : parts)
		{
			if (part == null || part.isBlank())
			{
				continue;
			}

			String[] kv = part.split(":", 2);
			if (kv.length != 2)
			{
				continue;
			}

			try
			{
				int itemId = Integer.parseInt(kv[0].trim());
				long quantity = Long.parseLong(kv[1].trim());
				if (itemId <= 0)
				{
					continue;
				}

				totals.merge(itemId, Math.max(0L, quantity), Long::sum);
			}
			catch (NumberFormatException ignored)
			{
				// Ignore malformed entries so a bad token does not corrupt all persisted loot.
			}
		}

		if (totals.isEmpty())
		{
			return Map.of();
		}

		return totals;
	}

	private static String serializeDailyKillCounts(Map<String, Integer> dailyKillCounts)
	{
		if (dailyKillCounts == null || dailyKillCounts.isEmpty())
		{
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, Integer> entry : dailyKillCounts.entrySet())
		{
			if (entry == null || entry.getKey() == null || entry.getValue() == null)
			{
				continue;
			}

			String dateKey = entry.getKey().trim();
			if (dateKey.isBlank())
			{
				continue;
			}

			if (builder.length() > 0)
			{
				builder.append(',');
			}
			builder.append(dateKey).append(':').append(Math.max(0, entry.getValue()));
		}
		return builder.toString();
	}

	private static Map<String, Integer> deserializeDailyKillCounts(String serialized)
	{
		if (serialized == null || serialized.isBlank())
		{
			return Map.of();
		}

		Map<String, Integer> counts = new HashMap<>();
		String[] parts = serialized.split(",");
		for (String part : parts)
		{
			if (part == null || part.isBlank())
			{
				continue;
			}

			String[] kv = part.split(":", 2);
			if (kv.length != 2)
			{
				continue;
			}

			try
			{
				String dateKey = kv[0].trim();
				int count = Integer.parseInt(kv[1].trim());
				if (dateKey.isBlank() || count < 0)
				{
					continue;
				}

				counts.merge(dateKey, count, Integer::sum);
			}
			catch (NumberFormatException ignored)
			{
				// Ignore malformed daily count entries.
			}
		}

		if (counts.isEmpty())
		{
			return Map.of();
		}

		return counts;
	}

	private static String serializeAreaKillCounts(Map<String, Integer> areaKillCounts)
	{
		if (areaKillCounts == null || areaKillCounts.isEmpty())
		{
			return "";
		}

		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, Integer> entry : areaKillCounts.entrySet())
		{
			if (entry == null || entry.getKey() == null || entry.getValue() == null)
			{
				continue;
			}

			String areaName = entry.getKey().trim();
			if (areaName.isBlank())
			{
				continue;
			}

			String encodedArea = Base64.getUrlEncoder().withoutPadding()
				.encodeToString(areaName.getBytes(StandardCharsets.UTF_8));
			if (builder.length() > 0)
			{
				builder.append(',');
			}
			builder.append(encodedArea).append(':').append(Math.max(0, entry.getValue()));
		}

		return builder.toString();
	}

	private static Map<String, Integer> deserializeAreaKillCounts(String serialized)
	{
		if (serialized == null || serialized.isBlank())
		{
			return Map.of();
		}

		Map<String, Integer> counts = new HashMap<>();
		String[] parts = serialized.split(",");
		for (String part : parts)
		{
			if (part == null || part.isBlank())
			{
				continue;
			}

			String[] kv = part.split(":", 2);
			if (kv.length != 2)
			{
				continue;
			}

			try
			{
				String areaName = new String(Base64.getUrlDecoder().decode(kv[0].trim()), StandardCharsets.UTF_8).trim();
				int count = Integer.parseInt(kv[1].trim());
				if (areaName.isBlank() || count < 0)
				{
					continue;
				}

				counts.merge(areaName, count, Integer::sum);
			}
			catch (IllegalArgumentException ignored)
			{
				// Ignore malformed area count entries.
			}
		}

		if (counts.isEmpty())
		{
			return Map.of();
		}

		return counts;
	}
}
