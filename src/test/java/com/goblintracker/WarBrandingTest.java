package com.goblintracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.goblintracker.branding.WarBranding;
import com.goblintracker.branding.WarToneMode;
import java.util.List;
import org.junit.Test;

public class WarBrandingTest
{
	@Test
	public void completionTextBoundsToExpectedRange()
	{
		assertEquals("0.0000%", WarBranding.completionText(0));
		assertEquals("100.0000%", WarBranding.completionText(1_500_000));
	}

	@Test
	public void flavorLineRotatesDeterministically()
	{
		String first = WarBranding.flavorLine(0, 25);
		String second = WarBranding.flavorLine(25, 25);
		assertTrue(!first.equals(second));
	}

	@Test
	public void milestoneMessageUsesToneTemplates()
	{
		String prophet = WarBranding.milestoneMessage(1000, WarToneMode.UNHINGED_PROPHET);
		String bureaucratic = WarBranding.milestoneMessage(1000, WarToneMode.GRIM_BUREAUCRATIC);

		assertTrue(prophet.contains("Milestone:"));
		assertTrue(bureaucratic.contains("Campaign update:"));
	}

	@Test
	public void milestoneHelpersReturnUnlockedAndNextTarget()
	{
		List<String> unlocked = WarBranding.unlockedMilestones(1200);
		String next = WarBranding.nextMilestoneSummary(1200);

		assertEquals(2, unlocked.size());
		assertTrue(unlocked.get(0).contains("100 - First Blood"));
		assertTrue(unlocked.get(1).contains("1,000 - Goblin Bane"));
		assertEquals("5,000 - Village Scourge (3,800 remaining)", next);
	}

	@Test
	public void panelLabelsChangeWithTone()
	{
		assertEquals("Today's Eliminations: ", WarBranding.overviewSessionLabel(WarToneMode.UNHINGED_PROPHET));
		assertEquals("Session Eliminations: ", WarBranding.overviewSessionLabel(WarToneMode.STRICT_SERIOUS));
		assertEquals("Session Kills: ", WarBranding.overviewSessionLabel(WarToneMode.NEUTRAL_SIMPLE));
	}

	@Test
	public void milestoneProgressSummaryRendersTierProgressBar()
	{
		String progress = WarBranding.milestoneProgressSummary(1200);
		String completed = WarBranding.milestoneProgressSummary(1_000_000);
		String eta = WarBranding.milestoneEtaSummary(1200, 300, WarToneMode.STRICT_SERIOUS);
		String etaUnknown = WarBranding.milestoneEtaSummary(1200, 0, WarToneMode.STRICT_SERIOUS);

		assertTrue(progress.contains("toward 5,000"));
		assertTrue(progress.contains("1,000 -> 5,000"));
		assertTrue(progress.contains("["));
		assertTrue(progress.contains("]"));
		assertTrue(completed.contains("100.00% complete"));
		assertTrue(eta.contains("13h to 5,000"));
		assertTrue(etaUnknown.contains("Unavailable"));
		assertEquals(5, WarBranding.milestoneProgressPercent(1200));
		assertEquals("1,000 -> 5,000 (3,800 remaining)", WarBranding.milestoneWindowText(1200));
		assertEquals("Milestone ETA: ", WarBranding.overviewMilestoneEtaLabel(WarToneMode.STRICT_SERIOUS));
	}

	@Test
	public void campaignProgressAndProjectionProvideUsefulOutput()
	{
		String campaign = WarBranding.campaignProgressSummary(1200);
		String unknownProjection = WarBranding.projectedCompletionSummary(1200, 0, WarToneMode.STRICT_SERIOUS);
		String projected = WarBranding.projectedCompletionSummary(1200, 300, WarToneMode.STRICT_SERIOUS);

		assertTrue(campaign.contains("1,200 / 1,000,000"));
		assertTrue(campaign.contains("["));
		assertTrue(unknownProjection.contains("Unavailable"));
		assertTrue(projected.contains("~"));
		assertTrue(projected.contains("at current pace"));
		assertEquals(25, WarBranding.campaignProgressPercent(250_000));
	}

	@Test
	public void rotatingTaglineAndTitleEvolveWithProgress()
	{
		String firstTagline = WarBranding.rotatingTagline(0, 25);
		String laterTagline = WarBranding.rotatingTagline(250, 25);
		String earlyTitle = WarBranding.operativeTitle(50, WarToneMode.UNHINGED_PROPHET);
		String advancedTitle = WarBranding.operativeTitle(100_000, WarToneMode.UNHINGED_PROPHET);

		assertTrue(!firstTagline.equals(laterTagline));
		assertEquals("Initiate of the Count", earlyTitle);
		assertEquals("Keeper of the Ledger", advancedTitle);
	}

	@Test
	public void overallWritingReturnsSingleDistinctLine()
	{
		String serious = WarBranding.overallWriting(1200, 25, WarToneMode.STRICT_SERIOUS);
		String prophet = WarBranding.overallWriting(1200, 25, WarToneMode.UNHINGED_PROPHET);

		assertTrue(serious.contains(" - "));
		assertTrue(prophet.equals(prophet.toUpperCase()));
		assertEquals("Overall Charge: ", WarBranding.overviewOverallWritingLabel(WarToneMode.STRICT_SERIOUS));
	}

	@Test
	public void identityConstantsMatchLedgerBrand()
	{
		assertEquals("The Goblin Ledger", WarBranding.PLUGIN_NAME);
		assertEquals("Every goblin counts.", WarBranding.PLUGIN_TAGLINE);
	}
}
