package com.goblintracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import org.junit.Test;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;

public class GoblinKillTrackerOverlayTest
{
	@Test
	public void renderReturnsNullWhenOverlayIsHidden() throws Exception
	{
		GoblinKillTrackerPlugin plugin = mock(GoblinKillTrackerPlugin.class);
		GoblinKillTrackerConfig config = mock(GoblinKillTrackerConfig.class);
		when(plugin.getConfig()).thenReturn(config);
		when(config.showOverlay()).thenReturn(false);
		when(config.showTripStats()).thenReturn(false);
		when(config.showRate()).thenReturn(false);

		GoblinKillTrackerOverlay overlay = new GoblinKillTrackerOverlay(plugin);
		Graphics2D graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();

		try
		{
			assertNull(overlay.render(graphics));
		}
		finally
		{
			graphics.dispose();
		}
	}

	@Test
	public void renderShowsSessionAndLifetimeCounts() throws Exception
	{
		GoblinKillTrackerPlugin plugin = mock(GoblinKillTrackerPlugin.class);
		GoblinKillTrackerConfig config = mock(GoblinKillTrackerConfig.class);
		when(plugin.getConfig()).thenReturn(config);
		when(plugin.getSessionGoblinKills()).thenReturn(3);
		when(plugin.getLifetimeGoblinKills()).thenReturn(17);
		when(config.showOverlay()).thenReturn(true);
		when(config.showTripStats()).thenReturn(false);
		when(config.showRate()).thenReturn(false);

		GoblinKillTrackerOverlay overlay = new GoblinKillTrackerOverlay(plugin);
		Graphics2D graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();

		try
		{
			Dimension rendered = overlay.render(graphics);
			PanelComponent panelComponent = getPanelComponent(overlay);
			Object first = panelComponent.getChildren().get(0);
			Object second = panelComponent.getChildren().get(1);

			assertNotNull(rendered);
			assertEquals(2, panelComponent.getChildren().size());
			assertTrue(first instanceof LineComponent);
			assertTrue(second instanceof LineComponent);
			assertEquals("Session", getStringField(first, "left"));
			assertEquals("3", getStringField(first, "right"));
			assertEquals("Ledger", getStringField(second, "left"));
			assertEquals("17", getStringField(second, "right"));
		}
		finally
		{
			graphics.dispose();
		}
	}

	@Test
	public void renderShowsTripAndRateWhenEnabled() throws Exception
	{
		GoblinKillTrackerPlugin plugin = mock(GoblinKillTrackerPlugin.class);
		GoblinKillTrackerConfig config = mock(GoblinKillTrackerConfig.class);
		when(plugin.getConfig()).thenReturn(config);
		when(plugin.getSessionGoblinKills()).thenReturn(9);
		when(plugin.getLifetimeGoblinKills()).thenReturn(42);
		when(plugin.getTripGoblinKills()).thenReturn(5);
		when(plugin.getSessionKillsPerHour()).thenReturn(180);
		when(config.showOverlay()).thenReturn(true);
		when(config.showTripStats()).thenReturn(true);
		when(config.showRate()).thenReturn(true);

		GoblinKillTrackerOverlay overlay = new GoblinKillTrackerOverlay(plugin);
		Graphics2D graphics = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB).createGraphics();

		try
		{
			Dimension rendered = overlay.render(graphics);
			PanelComponent panelComponent = getPanelComponent(overlay);

			assertNotNull(rendered);
			assertEquals(4, panelComponent.getChildren().size());
			assertEquals("Field", getStringField(panelComponent.getChildren().get(2), "left"));
			assertEquals("5", getStringField(panelComponent.getChildren().get(2), "right"));
			assertEquals("Purge/hr", getStringField(panelComponent.getChildren().get(3), "left"));
			assertEquals("180", getStringField(panelComponent.getChildren().get(3), "right"));
		}
		finally
		{
			graphics.dispose();
		}
	}

	private static String getStringField(Object target, String fieldName) throws Exception
	{
		Field field = target.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		return (String) field.get(target);
	}

	private static PanelComponent getPanelComponent(GoblinKillTrackerOverlay overlay) throws Exception
	{
		Field field = OverlayPanel.class.getDeclaredField("panelComponent");
		field.setAccessible(true);
		return (PanelComponent) field.get(overlay);
	}
}
