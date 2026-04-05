package com.goblintracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.goblintracker.branding.WarBranding;
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
	public void milestoneMessageUsesCanonicalTemplate()
	{
		String message = WarBranding.milestoneMessage(1000);

		assertTrue(message.contains("Prophecy mark reached:"));
		assertTrue(message.contains("1,000") || message.contains("1000"));
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
	public void panelLabelsUseCanonicalLoreVoice()
	{
		assertEquals("Kills this day: ", WarBranding.overviewSessionLabel());
		assertEquals("War-book oath: ", WarBranding.overviewOverallWritingLabel());
		assertEquals("Next prophecy ETA: ", WarBranding.overviewMilestoneEtaLabel());
	}

	@Test
	public void milestoneProgressSummaryRendersTierProgressBar()
	{
		String progress = WarBranding.milestoneProgressSummary(1200);
		String completed = WarBranding.milestoneProgressSummary(1_000_000);
		String eta = WarBranding.milestoneEtaSummary(1200, 300);
		String etaUnknown = WarBranding.milestoneEtaSummary(1200, 0);

		assertTrue(progress.contains("toward 5,000"));
		assertTrue(progress.contains("1,000 -> 5,000"));
		assertTrue(progress.contains("["));
		assertTrue(progress.contains("]"));
		assertTrue(completed.contains("100.00% complete"));
		assertTrue(eta.contains("13h to 5,000"));
		assertTrue(etaUnknown.contains("Unknown"));
		assertEquals(5, WarBranding.milestoneProgressPercent(1200));
		assertEquals("1,000 -> 5,000 (3,800 remaining)", WarBranding.milestoneWindowText(1200));
		assertEquals("Next prophecy ETA: ", WarBranding.overviewMilestoneEtaLabel());
	}

	@Test
	public void campaignProgressAndProjectionProvideUsefulOutput()
	{
		String campaign = WarBranding.campaignProgressSummary(1200);
		String unknownProjection = WarBranding.projectedCompletionSummary(1200, 0);
		String projected = WarBranding.projectedCompletionSummary(1200, 300);

		assertTrue(campaign.contains("1,200 / 1,000,000"));
		assertTrue(campaign.contains("["));
		assertTrue(unknownProjection.contains("Unknown"));
		assertTrue(projected.contains("~"));
		assertTrue(projected.contains("current war speed"));
		assertEquals(25, WarBranding.campaignProgressPercent(250_000));
	}

	@Test
	public void rotatingTaglineAndTitleEvolveWithProgress()
	{
		String firstTagline = WarBranding.rotatingTagline(0, 25);
		String laterTagline = WarBranding.rotatingTagline(250, 25);
		String earlyTitle = WarBranding.operativeTitle(50);
		String advancedTitle = WarBranding.operativeTitle(100_000);

		assertTrue(!firstTagline.equals(laterTagline));
		assertEquals("New War Scribe", earlyTitle);
		assertEquals("Temple War Captain", advancedTitle);
	}

	@Test
	public void overallWritingReturnsSingleDistinctLine()
	{
		String writing = WarBranding.overallWriting(1200, 25);

		assertTrue(writing.contains(" | "));
		assertEquals("War-book oath: ", WarBranding.overviewOverallWritingLabel());
	}

	@Test
	public void identityConstantsMatchLedgerBrand()
	{
		assertEquals("The Goblin Ledger", WarBranding.PLUGIN_NAME);
		assertEquals("Big High War God count every goblin.", WarBranding.PLUGIN_TAGLINE);
	}

	@Test
	public void canonBookTabAndLoreTextAreExposed()
	{
		String loreText = WarBranding.bronzeCountLoreText();

		assertEquals("Canon Book", WarBranding.tabCanonBookLabel());
		assertTrue(loreText.contains("THE BRONZE COUNT"));
		assertTrue(loreText.contains("A Goblin Chronicle of War, Prophecy, and the Million Dead"));
		assertTrue(loreText.contains("or building the throne?"));
	}
}
