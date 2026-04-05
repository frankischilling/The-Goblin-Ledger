package com.goblintracker.branding;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class WarBranding
{
	public static final String PLUGIN_NAME = "The Goblin Ledger";
	public static final String PLUGIN_TAGLINE = "Every goblin counts.";
	public static final int GOAL_KILLS = 1_000_000;

	public static final String SIDEBAR_TOOLTIP = "The Goblin Ledger";
	public static final String OVERLAY_SESSION_LABEL = "Session";
	public static final String OVERLAY_LIFETIME_LABEL = "Ledger";
	public static final String OVERLAY_TRIP_LABEL = "Field";
	public static final String OVERLAY_RATE_LABEL = "Purge/hr";
	private static final int MILESTONE_PROGRESS_BAR_WIDTH = 20;
	private static final int CAMPAIGN_PROGRESS_BAR_WIDTH = 24;

	private static final int[] MILESTONE_TARGETS = {
		100,
		1_000,
		5_000,
		10_000,
		50_000,
		100_000,
		250_000,
		500_000,
		750_000,
		900_000,
		GOAL_KILLS
	};

	private static final String[] FLAVOR_LINES = {
		"Another page filled.",
		"The count rises.",
		"Every goblin counts.",
		"The vow remains unbroken.",
		"One million was never a metaphor.",
		"The green tide recedes.",
		"The ledger remembers all.",
		"The work continues.",
		"No goblin left uncounted.",
		"The million draws closer."
	};

	private static final String[] TAGLINES = {
		"Every goblin counts.",
		"Track the war.",
		"Make the joke real.",
		"The grind becomes scripture.",
		"A million names. One hunter.",
		"The count never forgets.",
		"One million or nothing.",
		"From nuisance to extinction."
	};

	private static final String[] PROPHET_DIRECTIVES = {
		"The ledger demands another page.",
		"No village sleeps while the count is unfinished.",
		"The number is sacred. Continue.",
		"The green tide breaks one kill at a time.",
		"The vow survives another night."
	};

	private static final String[] BUREAU_DIRECTIVES = {
		"Regional reduction targets remain active.",
		"Compliance accepted. Continue standard operations.",
		"Outstanding goblin balance remains above threshold.",
		"Campaign has not met annualized extinction quota.",
		"Additional eliminations are required this session."
	};

	private static final String[] SERIOUS_DIRECTIVES = {
		"The war is unfinished.",
		"Stay on target until the million is complete.",
		"The campaign advances with every goblin felled.",
		"Hold the line. Keep counting.",
		"No retreat. No reset."
	};

	private WarBranding()
	{
	}

	public static String tabCampaignLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Campaign", "Summary", "Overview", "Overview");
	}

	public static String tabFrontsLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Fronts", "Districts", "Areas", "Areas");
	}

	public static String tabSpoilsLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Spoils", "Loot Ledger", "Loot", "Loot");
	}

	public static String tabChronicleLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Chronicle", "Incident Log", "History", "History");
	}

	public static String overviewSessionLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Today's Eliminations: ", "Session Processed: ", "Session Eliminations: ", "Session Kills: ");
	}

	public static String overviewTripLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Field Eliminations: ", "Field Processed: ", "Trip Eliminations: ", "Trip Kills: ");
	}

	public static String overviewLifetimeLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Ledger Total: ", "Lifetime Processed: ", "Total Eliminations: ", "Lifetime Kills: ");
	}

	public static String overviewRateLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Current Purge Rate: ", "Annualized Reduction: ", "Eliminations/hr: ", "Kills/hr: ");
	}

	public static String overviewCompletionLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Campaign Completion: ", "Quota Completion: ", "Campaign Completion: ", "Progress: ");
	}

	public static String overviewRemainingLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Hostiles Remaining: ", "Outstanding Balance: ", "Hostiles Remaining: ", "Remaining: ");
	}

	public static String overviewMilestoneProgressLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Milestone Progress: ", "Threshold Progress: ", "Milestone Progress: ", "Milestone Progress: ");
	}

	public static String overviewMilestoneEtaLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Milestone ETA: ", "Threshold ETA: ", "Milestone ETA: ", "Milestone ETA: ");
	}

	public static String overviewProfileLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Acting Auditor: ", "Assigned Operator: ", "Active Profile: ", "Profile: ");
	}

	public static String overviewProfileNoneLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "None", "Unassigned", "None", "None");
	}

	public static String overviewTitleLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Current Title: ", "Designation: ", "Current Title: ", "Title: ");
	}

	public static String overviewTaglineLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Campaign Motto: ", "Public Directive: ", "Campaign Motto: ", "Tagline: ");
	}

	public static String overviewCampaignProgressLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Million Progress: ", "Quota Progress: ", "Million Progress: ", "Goal Progress: ");
	}

	public static String overviewProjectionLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Estimated Date of Extinction: ", "Projected Eradication Window: ", "Projected Completion: ", "Projected Completion: ");
	}

	public static String overviewDirectiveLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Campaign Decree: ", "Current Directive: ", "Campaign Directive: ", "Directive: ");
	}

	public static String overviewOverallWritingLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Overall Oath: ", "Overall Mandate: ", "Overall Charge: ", "Overall Focus: ");
	}

	public static String overviewFlavorLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "War Bulletin: ", "Directive: ", "Dispatch: ", "Note: ");
	}

	public static String overviewMilestonesLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Milestones Reached:", "Thresholds Logged:", "Campaign Milestones:", "Milestones:");
	}

	public static String overviewNoMilestonesLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "No milestones breached yet.", "No thresholds logged.", "No milestones reached.", "No milestones yet.");
	}

	public static String overviewNextTargetLabel(WarToneMode toneMode)
	{
		return toneText(toneMode, "Next Oath Threshold: ", "Next Compliance Threshold: ", "Next Milestone: ", "Next Target: ");
	}

	public static String emptyAreasText(WarToneMode toneMode)
	{
		return toneText(
			toneMode,
			"No fronts have reported eliminations yet.",
			"No districts have filed elimination records.",
			"No area kills recorded yet.",
			"No kills recorded yet.");
	}

	public static String emptyLootText(WarToneMode toneMode)
	{
		return toneText(
			toneMode,
			"No spoils have been logged yet.",
			"No loot entries in the campaign ledger.",
			"No goblin loot captured yet.",
			"No loot recorded yet.");
	}

	public static String emptyHistoryText(WarToneMode toneMode)
	{
		return toneText(
			toneMode,
			"No entries in the war chronicle.",
			"No incidents logged.",
			"No recent goblin kills.",
			"No recent kills.");
	}

	public static String completionText(int lifetimeKills)
	{
		double boundedKills = Math.max(0, lifetimeKills);
		double completion = Math.min(100.0D, (boundedKills * 100.0D) / GOAL_KILLS);
		return String.format(Locale.US, "%.4f%%", completion);
	}

	public static int hostilesRemaining(int lifetimeKills)
	{
		return Math.max(0, GOAL_KILLS - Math.max(0, lifetimeKills));
	}

	public static String flavorLine(int lifetimeKills, int stride)
	{
		int safeStride = Math.max(1, stride);
		int index = Math.floorDiv(Math.max(0, lifetimeKills), safeStride) % FLAVOR_LINES.length;
		return FLAVOR_LINES[index];
	}

	public static String rotatingTagline(int lifetimeKills, int stride)
	{
		int safeStride = Math.max(1, stride);
		int index = Math.floorDiv(Math.max(0, lifetimeKills), safeStride) % TAGLINES.length;
		return TAGLINES[index];
	}

	public static String overallWriting(int lifetimeKills, int stride, WarToneMode toneMode)
	{
		String tagline = rotatingTagline(lifetimeKills, stride);
		String directive = campaignDirective(lifetimeKills, toneMode);
		WarToneMode resolvedTone = toneMode == null ? WarToneMode.UNHINGED_PROPHET : toneMode;
		switch (resolvedTone)
		{
			case GRIM_BUREAUCRATIC:
				return tagline + " | " + directive;
			case STRICT_SERIOUS:
				return tagline + " - " + directive;
			case NEUTRAL_SIMPLE:
				return tagline;
			case UNHINGED_PROPHET:
			default:
				return (tagline + " // " + directive).toUpperCase(Locale.US);
		}
	}

	public static String campaignDirective(int lifetimeKills, WarToneMode toneMode)
	{
		WarToneMode resolvedTone = toneMode == null ? WarToneMode.UNHINGED_PROPHET : toneMode;
		String[] directives;
		switch (resolvedTone)
		{
			case GRIM_BUREAUCRATIC:
				directives = BUREAU_DIRECTIVES;
				break;
			case STRICT_SERIOUS:
				directives = SERIOUS_DIRECTIVES;
				break;
			case NEUTRAL_SIMPLE:
				directives = SERIOUS_DIRECTIVES;
				break;
			case UNHINGED_PROPHET:
			default:
				directives = PROPHET_DIRECTIVES;
				break;
		}

		int index = Math.floorDiv(Math.max(0, lifetimeKills), 5000) % directives.length;
		return directives[index];
	}

	public static String operativeTitle(int lifetimeKills, WarToneMode toneMode)
	{
		int boundedKills = Math.max(0, lifetimeKills);
		if (boundedKills >= GOAL_KILLS)
		{
			return toneText(toneMode, "Extinction Archivist", "Objective Complete Operator", "Goblin Extinguisher", "Goal Complete");
		}
		if (boundedKills >= 900000)
		{
			return toneText(toneMode, "The End-Bringer", "Terminal Reduction Specialist", "Endgame Bane", "Near Completion");
		}
		if (boundedKills >= 500000)
		{
			return toneText(toneMode, "High Auditor of Ash", "Senior Campaign Officer", "High Marshal", "Halfway Champion");
		}
		if (boundedKills >= 100000)
		{
			return toneText(toneMode, "Keeper of the Ledger", "Regional Suppression Lead", "War Captain", "Campaign Veteran");
		}
		if (boundedKills >= 10000)
		{
			return toneText(toneMode, "Collector of Names", "Certified Reduction Agent", "Goblin Reaper", "Skilled Hunter");
		}
		if (boundedKills >= 1000)
		{
			return toneText(toneMode, "The Auditor", "Junior Compliance Officer", "Goblin Bane", "Dedicated Slayer");
		}
		if (boundedKills >= 100)
		{
			return toneText(toneMode, "Tally Keeper", "Probationary Operator", "First-Blood Scout", "Getting Started");
		}

		return toneText(toneMode, "Initiate of the Count", "Uncertified Operator", "Recruit", "Novice");
	}

	public static String milestoneMessage(int lifetimeKills, WarToneMode toneMode)
	{
		WarToneMode resolvedTone = toneMode == null ? WarToneMode.UNHINGED_PROPHET : toneMode;
		String title = milestoneTitle(lifetimeKills);
		switch (resolvedTone)
		{
			case GRIM_BUREAUCRATIC:
				return "Campaign update: " + lifetimeKills + " confirmed eliminations. Status: " + title + ".";
			case STRICT_SERIOUS:
				return "Milestone reached: " + lifetimeKills + " total goblins eliminated (" + title + ").";
			case NEUTRAL_SIMPLE:
				return "Milestone reached at " + lifetimeKills + " lifetime kills.";
			case UNHINGED_PROPHET:
			default:
				return "Milestone: " + title + " (" + lifetimeKills + "). The ledger grows heavier.";
		}
	}

	public static List<String> unlockedMilestones(int lifetimeKills)
	{
		int boundedKills = Math.max(0, lifetimeKills);
		List<String> unlocked = new ArrayList<>();
		for (int target : MILESTONE_TARGETS)
		{
			if (boundedKills >= target)
			{
				unlocked.add("[x] " + formatWholeNumber(target) + " - " + milestoneTitle(target));
			}
		}

		return unlocked;
	}

	public static String nextMilestoneSummary(int lifetimeKills)
	{
		int boundedKills = Math.max(0, lifetimeKills);
		for (int target : MILESTONE_TARGETS)
		{
			if (boundedKills < target)
			{
				int remaining = target - boundedKills;
				return formatWholeNumber(target) + " - " + milestoneTitle(target)
					+ " (" + formatWholeNumber(remaining) + " remaining)";
			}
		}

		return "All thresholds cleared.";
	}

	public static String milestoneProgressSummary(int lifetimeKills)
	{
		MilestoneWindow window = resolveMilestoneWindow(lifetimeKills);
		String progressBar = renderProgressBar(window.getProgress(), MILESTONE_PROGRESS_BAR_WIDTH);

		if (window.isCompleted())
		{
			return progressBar + " 100.00% complete";
		}

		return progressBar + " "
			+ String.format(
				Locale.US,
				"%.2f%% toward %s (%s -> %s)",
				window.getProgress() * 100.0D,
				formatWholeNumber(window.getNextTarget()),
				formatWholeNumber(window.getPreviousTarget()),
				formatWholeNumber(window.getNextTarget()));
	}

	public static int milestoneProgressPercent(int lifetimeKills)
	{
		MilestoneWindow window = resolveMilestoneWindow(lifetimeKills);
		return toPercent(window.getProgress());
	}

	public static String milestoneWindowText(int lifetimeKills)
	{
		int boundedKills = Math.max(0, lifetimeKills);
		MilestoneWindow window = resolveMilestoneWindow(boundedKills);
		if (window.isCompleted())
		{
			return "All thresholds cleared.";
		}

		int remaining = Math.max(0, window.getNextTarget() - boundedKills);
		return formatWholeNumber(window.getPreviousTarget())
			+ " -> " + formatWholeNumber(window.getNextTarget())
			+ " (" + formatWholeNumber(remaining) + " remaining)";
	}

	public static String milestoneEtaSummary(int lifetimeKills, int killsPerHour, WarToneMode toneMode)
	{
		int boundedKills = Math.max(0, lifetimeKills);
		MilestoneWindow window = resolveMilestoneWindow(boundedKills);
		if (window.isCompleted())
		{
			return toneText(toneMode, "All milestone thresholds cleared.", "All milestone thresholds closed.", "All milestone thresholds complete.", "Complete.");
		}

		int remaining = Math.max(0, window.getNextTarget() - boundedKills);
		int safeRate = Math.max(0, killsPerHour);
		if (remaining <= 0)
		{
			return toneText(toneMode, "Threshold reached.", "Threshold reached.", "Threshold reached.", "Reached.");
		}

		if (safeRate <= 0)
		{
			return toneText(
				toneMode,
				"Unknown. Raise purge rate to project milestone.",
				"Unavailable. Insufficient throughput sample.",
				"Unavailable at current rate.",
				"Unknown until rate is positive.");
		}

		long hours = (long) Math.ceil(remaining / (double) safeRate);
		long days = hours / 24;
		long remHours = hours % 24;
		return "~" + days + "d " + remHours + "h to " + formatWholeNumber(window.getNextTarget());
	}

	public static String campaignProgressSummary(int lifetimeKills)
	{
		int boundedKills = Math.max(0, lifetimeKills);
		double progress = safeRatio(boundedKills, GOAL_KILLS);
		String progressBar = renderProgressBar(progress, CAMPAIGN_PROGRESS_BAR_WIDTH);
		return progressBar + " "
			+ String.format(
				Locale.US,
				"%s / %s (%.4f%%)",
				formatWholeNumber(boundedKills),
				formatWholeNumber(GOAL_KILLS),
				progress * 100.0D);
	}

	public static int campaignProgressPercent(int lifetimeKills)
	{
		int boundedKills = Math.max(0, lifetimeKills);
		return toPercent(safeRatio(boundedKills, GOAL_KILLS));
	}

	public static String projectedCompletionSummary(int lifetimeKills, int killsPerHour, WarToneMode toneMode)
	{
		int remaining = hostilesRemaining(lifetimeKills);
		if (remaining <= 0)
		{
			return toneText(toneMode, "Objective complete. No hostiles remain.", "Objective closed. No outstanding balance.", "Objective complete.", "Goal complete.");
		}

		int safeRate = Math.max(0, killsPerHour);
		if (safeRate <= 0)
		{
			return toneText(
				toneMode,
				"Unknown. The ledger requires a purge rate.",
				"Unavailable. Insufficient rate sample.",
				"Unavailable at current rate.",
				"Unknown until rate is positive.");
		}

		long hours = (long) Math.ceil(remaining / (double) safeRate);
		long days = hours / 24;
		long remHours = hours % 24;
		return toneText(
			toneMode,
			"~" + days + "d " + remHours + "h at current purge rate.",
			"~" + days + "d " + remHours + "h pending current throughput.",
			"~" + days + "d " + remHours + "h at current pace.",
			"~" + days + "d " + remHours + "h at current pace.");
	}

	public static String milestoneTitle(int lifetimeKills)
	{
		switch (lifetimeKills)
		{
			case 100:
				return "First Blood";
			case 1000:
				return "Goblin Bane";
			case 5000:
				return "Village Scourge";
			case 10000:
				return "Green Reaper";
			case 50000:
				return "Campbreaker";
			case 100000:
				return "Goblin Menace";
			case 250000:
				return "The Long War";
			case 500000:
				return "Half the Work";
			case 750000:
				return "No Turning Back";
			case 900000:
				return "The End Nears";
			case GOAL_KILLS:
				return "Extinction Event";
			default:
				return "Threshold Broken";
		}
	}

	private static String toneText(
		WarToneMode toneMode,
		String unhinged,
		String bureaucratic,
		String serious,
		String neutral)
	{
		WarToneMode resolvedTone = toneMode == null ? WarToneMode.UNHINGED_PROPHET : toneMode;
		switch (resolvedTone)
		{
			case GRIM_BUREAUCRATIC:
				return bureaucratic;
			case STRICT_SERIOUS:
				return serious;
			case NEUTRAL_SIMPLE:
				return neutral;
			case UNHINGED_PROPHET:
			default:
				return unhinged;
		}
	}

	private static MilestoneWindow resolveMilestoneWindow(int lifetimeKills)
	{
		int boundedKills = Math.max(0, lifetimeKills);
		int previousTarget = 0;

		for (int target : MILESTONE_TARGETS)
		{
			if (boundedKills < target)
			{
				double progress = safeRatio(boundedKills - previousTarget, target - previousTarget);
				return new MilestoneWindow(previousTarget, target, progress, false);
			}

			previousTarget = target;
		}

		return new MilestoneWindow(GOAL_KILLS, GOAL_KILLS, 1.0D, true);
	}

	private static double safeRatio(int numerator, int denominator)
	{
		if (denominator <= 0)
		{
			return 1.0D;
		}

		double ratio = Math.max(0.0D, numerator) / (double) denominator;
		return Math.max(0.0D, Math.min(1.0D, ratio));
	}

	private static int toPercent(double progress)
	{
		double boundedProgress = Math.max(0.0D, Math.min(1.0D, progress));
		return (int) Math.round(boundedProgress * 100.0D);
	}

	private static String renderProgressBar(double progress, int width)
	{
		int boundedWidth = Math.max(5, width);
		double boundedProgress = Math.max(0.0D, Math.min(1.0D, progress));
		int filled = (int) Math.round(boundedProgress * boundedWidth);
		filled = Math.max(0, Math.min(boundedWidth, filled));
		return "[" + repeat('#', filled) + repeat('-', boundedWidth - filled) + "]";
	}

	private static String repeat(char c, int count)
	{
		int boundedCount = Math.max(0, count);
		StringBuilder builder = new StringBuilder(boundedCount);
		for (int i = 0; i < boundedCount; i++)
		{
			builder.append(c);
		}
		return builder.toString();
	}

	private static String formatWholeNumber(int value)
	{
		return NumberFormat.getIntegerInstance(Locale.US).format(Math.max(0, value));
	}

	private static final class MilestoneWindow
	{
		private final int previousTarget;
		private final int nextTarget;
		private final double progress;
		private final boolean completed;

		private MilestoneWindow(int previousTarget, int nextTarget, double progress, boolean completed)
		{
			this.previousTarget = previousTarget;
			this.nextTarget = nextTarget;
			this.progress = progress;
			this.completed = completed;
		}

		private int getPreviousTarget()
		{
			return previousTarget;
		}

		private int getNextTarget()
		{
			return nextTarget;
		}

		private double getProgress()
		{
			return progress;
		}

		private boolean isCompleted()
		{
			return completed;
		}
	}
}
