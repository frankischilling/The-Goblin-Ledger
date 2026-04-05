package com.goblintracker;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.goblintracker.branding.WarToneMode;
import com.goblintracker.model.GoblinKillRecord;
import com.goblintracker.ui.GoblinPanel;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.junit.Test;
import org.mockito.Mockito;

public class GoblinPanelTest
{
	@Test
	public void refreshHistoryHandlesMissingRecordFields() throws Exception
	{
		GoblinKillTrackerPlugin plugin = mock(GoblinKillTrackerPlugin.class);
		when(plugin.getAreaKillCounts()).thenReturn(Map.of());
		when(plugin.getLootTotals()).thenReturn(Map.of());
		when(plugin.getRecentKills()).thenReturn(List.of(new GoblinKillRecord(1, "Goblin", 1, null, null, null, -3, 0L)));

		GoblinPanel panel = new GoblinPanel(plugin);
		panel.refresh();
		flushEdt();

		String history = getText(panel, "historyArea");
		assertTrue(history.contains("--:--:-- | Unknown | UNKNOWN | loot items: 0"));
	}

	@Test
	public void refreshHistoryShowsMostRecentKillFirst() throws Exception
	{
		GoblinKillTrackerPlugin plugin = mock(GoblinKillTrackerPlugin.class);
		when(plugin.getAreaKillCounts()).thenReturn(Map.of());
		when(plugin.getLootTotals()).thenReturn(Map.of());
		when(plugin.getRecentKills()).thenReturn(List.of(
			new GoblinKillRecord(1, "Goblin", 1, Instant.ofEpochSecond(10), com.goblintracker.detection.KillSource.LOOT, "Lumbridge", 2, 2L),
			new GoblinKillRecord(2, "Goblin", 2, Instant.ofEpochSecond(20), com.goblintracker.detection.KillSource.DESPAWN, "Falador", 0, 0L)));

		GoblinPanel panel = new GoblinPanel(plugin);
		panel.refresh();
		flushEdt();

		String history = getText(panel, "historyArea");
		String[] lines = history.split("\\R");
		assertTrue(lines[0].contains("Falador | DESPAWN"));
		assertTrue(lines[1].contains("Lumbridge | LOOT"));
	}

	@Test
	public void refreshLootResolvesItemNamesOffEdt() throws Exception
	{
		GoblinKillTrackerPlugin plugin = mock(GoblinKillTrackerPlugin.class);
		when(plugin.getAreaKillCounts()).thenReturn(Map.of());
		when(plugin.getLootTotals()).thenReturn(Map.of(526, 1L));
		when(plugin.getRecentKills()).thenReturn(List.of());
		Mockito.doAnswer(invocation -> {
			assertFalse(SwingUtilities.isEventDispatchThread());
			return "Bones";
		}).when(plugin).getItemName(526);

		GoblinPanel panel = new GoblinPanel(plugin);
		panel.refresh();
		flushEdt();

		String loot = getText(panel, "lootArea");
		assertTrue(loot.contains("Bones (526): 1"));
	}

	@Test
	public void refreshOverviewShowsMilestonesAndNextTarget() throws Exception
	{
		GoblinKillTrackerPlugin plugin = mock(GoblinKillTrackerPlugin.class);
		GoblinKillTrackerConfig config = mock(GoblinKillTrackerConfig.class);

		when(plugin.getConfig()).thenReturn(config);
		when(config.toneMode()).thenReturn(WarToneMode.STRICT_SERIOUS);
		when(config.showFlavorText()).thenReturn(false);
		when(config.flavorLineStride()).thenReturn(25);
		when(plugin.getSessionGoblinKills()).thenReturn(12);
		when(plugin.getTripGoblinKills()).thenReturn(4);
		when(plugin.getLifetimeGoblinKills()).thenReturn(1200);
		when(plugin.getSessionKillsPerHour()).thenReturn(300);
		when(plugin.getActiveProfileName()).thenReturn("Frank");
		when(plugin.getAreaKillCounts()).thenReturn(Map.of());
		when(plugin.getLootTotals()).thenReturn(Map.of());
		when(plugin.getRecentKills()).thenReturn(List.of());

		GoblinPanel panel = new GoblinPanel(plugin);
		panel.refresh();
		flushEdt();

		String overview = getText(panel, "overviewArea");
		assertTrue(overview.contains("Overall Charge:"));
		assertTrue(overview.contains("Session Eliminations: 12"));
		assertTrue(overview.contains("Campaign Milestones:"));
		assertTrue(overview.contains("Projected Completion:"));
		assertTrue(overview.contains("Current Title:"));
		assertTrue(overview.contains("[x] 100 - First Blood"));
		assertTrue(overview.contains("[x] 1,000 - Goblin Bane"));
		assertTrue(overview.contains("Next Milestone: 5,000 - Village Scourge (3,800 remaining)"));
		assertTrue(overview.contains("Milestone ETA:"));

		JProgressBar milestoneBar = getProgressBar(panel, "milestoneProgressBar");
		JProgressBar campaignBar = getProgressBar(panel, "campaignProgressBar");
		JLabel milestoneEta = getLabel(panel, "milestoneEtaLabel");
		assertEquals(5, milestoneBar.getValue());
		assertEquals("5%", milestoneBar.getString());
		assertEquals("0%", campaignBar.getString());
		assertTrue(milestoneBar.getToolTipText().contains("toward 5,000"));
		assertTrue(campaignBar.getToolTipText().contains("1,200 / 1,000,000"));
		assertTrue(milestoneEta.getText().contains("Milestone ETA:"));
		assertTrue(milestoneEta.getText().contains("13h to 5,000"));

		JTabbedPane tabs = getTabs(panel);
		assertEquals("Overview", tabs.getTitleAt(0));
		assertEquals("Areas", tabs.getTitleAt(1));
		assertEquals("Loot", tabs.getTitleAt(2));
		assertEquals("History", tabs.getTitleAt(3));
	}

	private static String getText(GoblinPanel panel, String fieldName) throws Exception
	{
		Field field = GoblinPanel.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		return ((JTextArea) field.get(panel)).getText();
	}

	private static JTabbedPane getTabs(GoblinPanel panel) throws Exception
	{
		Field field = GoblinPanel.class.getDeclaredField("tabs");
		field.setAccessible(true);
		return (JTabbedPane) field.get(panel);
	}

	private static JProgressBar getProgressBar(GoblinPanel panel, String fieldName) throws Exception
	{
		Field field = GoblinPanel.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		return (JProgressBar) field.get(panel);
	}

	private static JLabel getLabel(GoblinPanel panel, String fieldName) throws Exception
	{
		Field field = GoblinPanel.class.getDeclaredField(fieldName);
		field.setAccessible(true);
		return (JLabel) field.get(panel);
	}

	private static void flushEdt() throws Exception
	{
		SwingUtilities.invokeAndWait(() -> {
			// Ensures pending refresh work has completed on the EDT.
		});
	}
}

