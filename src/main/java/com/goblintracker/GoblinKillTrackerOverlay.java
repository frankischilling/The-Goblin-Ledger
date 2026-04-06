package com.goblintracker;

import com.goblintracker.branding.WarBranding;
import com.goblintracker.branding.WarPalette;
import com.goblintracker.branding.WarThemeMode;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

public class GoblinKillTrackerOverlay extends OverlayPanel
{
	private final GoblinKillTrackerPlugin plugin;
	private long flashUntilMs;

	@Inject
	public GoblinKillTrackerOverlay(GoblinKillTrackerPlugin plugin)
	{
		super(plugin);
		this.plugin = plugin;
		setPosition(OverlayPosition.TOP_LEFT);
		setClearChildren(false);
		addMenuEntry(MenuAction.RUNELITE_OVERLAY, "Reset session", WarBranding.PLUGIN_NAME, menuEntry -> plugin.resetSessionCount());
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.getConfig().showOverlay())
		{
			return null;
		}

		WarThemeMode themeMode = plugin.getConfig().visualTheme();
		WarPalette palette = WarPalette.forTheme(themeMode);
		panelComponent.setBackgroundColor(isFlashActive() ? palette.getOverlayFlashBackground() : palette.getOverlayBackground());

		panelComponent.getChildren().clear();
		panelComponent.getChildren().add(LineComponent.builder()
			.left(WarBranding.OVERLAY_SESSION_LABEL)
			.right(String.valueOf(plugin.getTodayGoblinKills()))
			.leftColor(palette.getOverlayHeadingColor())
			.build());
		panelComponent.getChildren().add(LineComponent.builder()
			.left(WarBranding.OVERLAY_LIFETIME_LABEL)
			.right(String.valueOf(plugin.getLifetimeGoblinKills()))
			.build());

		if (plugin.getConfig().showTripStats())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left(WarBranding.OVERLAY_TRIP_LABEL)
				.right(String.valueOf(plugin.getTripGoblinKills()))
				.build());
		}

		if (plugin.getConfig().showRate())
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left(WarBranding.OVERLAY_RATE_LABEL)
				.right(String.valueOf(plugin.getSessionKillsPerHour()))
				.build());
		}

		return super.render(graphics);
	}

	public void triggerFlash()
	{
		flashUntilMs = System.currentTimeMillis() + WarPalette.FLASH_DURATION_MS;
	}

	private boolean isFlashActive()
	{
		return System.currentTimeMillis() <= flashUntilMs;
	}
}
