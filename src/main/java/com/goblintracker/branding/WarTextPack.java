package com.goblintracker.branding;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Centralized war-branding phrase pack for UI text, popups, and lore flavor.
 * Lists are ordered for deterministic rotation behavior.
 */
public final class WarTextPack
{
	private static final List<String> BRAND_NAMES = List.of(
		"The Bronze Count",
		"One Million Goblins",
		"Goblin War Ledger",
		"The Long Count",
		"The Bronze Throne",
		"The Goblin Reckoning",
		"Bandos' Tally",
		"The Mudplain Count",
		"The Last Goblin War",
		"The Tally-Maker");

	private static final List<String> SUBTITLE_IDEAS = List.of(
		"Every kill is a mark upon the throne.",
		"The goblins are counting too.",
		"One million dead. One prophecy fulfilled.",
		"A war without banners. A count without mercy.",
		"The Long Count has begun.",
		"Not a grind. A campaign.",
		"Each goblin slain is another plate of bronze.",
		"Kill. Count. Ascend.");

	private static final List<String> SHORT_SLOGANS = List.of(
		"COUNT THE DEAD",
		"MARCH TO A MILLION",
		"THE THRONE IS HUNGRY",
		"NO MERCY. ONLY NUMBERS.",
		"GOBLINS FALL. THE COUNT RISES.",
		"ANOTHER BODY FOR THE BRONZE THRONE",
		"THE LONG COUNT CONTINUES",
		"EVERY KILL ECHOES",
		"THE WAR NEVER ENDED",
		"ONE MORE GOBLIN",
		"THE LEDGER DEMANDS MORE");

	private static final List<String> GENERAL_KILL_POPUPS = List.of(
		"Another mark for the Bronze Count.",
		"Another goblin enters the ledger.",
		"The throne grows heavier.",
		"The tally deepens.",
		"The Long Count advances.",
		"Another body for the mud.",
		"The war remembers.",
		"The ledger accepts another offering.",
		"One more scratch in bronze.",
		"The unseen tally rises.",
		"Another name lost. Another number gained.",
		"The Count does not tire.",
		"The mud drinks again.",
		"Another life added to prophecy.",
		"Another skull for the plain.",
		"The warpath remains fed.",
		"Another green body for the throne.",
		"Another whisper spreads through the caves.");

	private static final List<String> RARE_OMENS = List.of(
		"The number is starting to feel back.",
		"The tally is no longer passive.",
		"At this scale, obsession begins to resemble ritual.",
		"There are now tribes born entirely beneath your number.",
		"Your campaign has outgrown motive.",
		"The throne seems closer than it should.",
		"The dead are becoming architecture.",
		"The tally outlives the motive.",
		"Prophecy loves repetition.",
		"The war ended. The counting did not.",
		"The number is starting to look back.",
		"The caves have learned your arithmetic.",
		"The Count is how the war learned to survive peace.");

	private static final List<String> SESSION_START_LINES = List.of(
		"The campaign resumes.",
		"The Bronze Count awakens.",
		"The ledger opens.",
		"Another march begins.",
		"The goblin war resumes.",
		"The Count remembers where you left off.",
		"The throne waits for fresh iron.",
		"Your warpath is active once more.",
		"Another day for goblinkind to suffer.",
		"The caves do not know you have returned yet.");

	private static final List<String> SESSION_END_LINES = List.of(
		"The campaign goes quiet.",
		"The ledger closes for now.",
		"The Count sleeps, but not forever.",
		"The throne remains unfinished.",
		"Today's slaughter is sealed into the Long Count.",
		"The warpath cools.",
		"The caves breathe again.",
		"The tribes count their missing.");

	private static final List<String> AMBIENT_LORE = List.of(
		"Somewhere in Gielinor, a goblin cave wall now bears your shape in charcoal.",
		"A tribe you have never visited has heard of you anyway.",
		"The goblins no longer think of you as one person.",
		"The Bronze Count is bigger than any single battlefield now.",
		"In some caves, mothers use your name to quiet their young.",
		"The old prophecies were not ready for numbers this large.",
		"Goblin scribes argue whether the Count is curse, commander, or god.",
		"Each fresh kill pushes the myth further away from denial.",
		"You are no longer merely killing goblins. You are editing their future.",
		"The tribes measure doom in your progress bar.");

	private static final List<String> BANDOS_WAR_LINES = List.of(
		"Strength is proven by the count.",
		"Mercy rots the edge.",
		"The weak fill the ledger first.",
		"War is truth. The tally is proof.",
		"Soft tribes vanish. Strong numbers remain.",
		"Every corpse hardens the campaign.",
		"To count the slain is to master them.",
		"The throne takes shape through discipline.",
		"Kill until the number itself becomes a weapon.");

	private static final List<String> RANK_NAMES = List.of(
		"Mud-Scratcher",
		"Campfire Menace",
		"The Counter",
		"Ledger-Keeper",
		"Long Count Bearer",
		"Thronebuilder",
		"Bronze Hand",
		"Commander of the Last War",
		"The Bronze Throne",
		"The Million Dead");

	private static final List<String> MODE_NAMES = List.of(
		"War Mode",
		"Temple Mode",
		"Goblin Panic Mode",
		"Bronze Mode",
		"Prophecy Mode",
		"Campaign Mode",
		"Bandos Mode",
		"Long Count Mode",
		"Shaman Mode",
		"Throne Mode");

	private static final List<String> MILESTONE_TITLES = List.of(
		"The First Scratches",
		"The Campfire Warning",
		"The Tenth Banner Falls",
		"The Long Count",
		"Halfway to the Throne",
		"The Bronze Throne");

	private static final Map<Integer, List<String>> MILESTONE_BURSTS = createMilestoneBursts();

	private static final List<JourneyPhase> JOURNEY_PHASES = List.of(
		new JourneyPhase(0, 999, "The First Scratches"),
		new JourneyPhase(1_000, 9_999, "The Campfire Warning"),
		new JourneyPhase(10_000, 99_999, "The Tenth Banner"),
		new JourneyPhase(100_000, 499_999, "The Long Count"),
		new JourneyPhase(500_000, 999_999, "Halfway to the Throne"),
		new JourneyPhase(1_000_000, Integer.MAX_VALUE, "The Bronze Throne"));

	private WarTextPack()
	{
	}

	public static List<String> brandNames()
	{
		return BRAND_NAMES;
	}

	public static List<String> subtitleIdeas()
	{
		return SUBTITLE_IDEAS;
	}

	public static List<String> shortSlogans()
	{
		return SHORT_SLOGANS;
	}

	public static List<String> generalKillPopups()
	{
		return GENERAL_KILL_POPUPS;
	}

	public static List<String> rareOmens()
	{
		return RARE_OMENS;
	}

	public static List<String> ambientLore()
	{
		return AMBIENT_LORE;
	}

	public static List<String> sessionStartLines()
	{
		return SESSION_START_LINES;
	}

	public static List<String> sessionEndLines()
	{
		return SESSION_END_LINES;
	}

	public static List<String> bandosWarLines()
	{
		return BANDOS_WAR_LINES;
	}

	public static List<String> rankNames()
	{
		return RANK_NAMES;
	}

	public static List<String> modeNames()
	{
		return MODE_NAMES;
	}

	public static List<String> milestoneTitles()
	{
		return MILESTONE_TITLES;
	}

	public static String milestoneBurstLine(int milestone)
	{
		List<String> lines = MILESTONE_BURSTS.get(milestone);
		if (lines == null || lines.isEmpty())
		{
			return "";
		}
		return lines.get(0);
	}

	public static String phaseNameForKills(int kills)
	{
		int boundedKills = Math.max(0, kills);
		for (JourneyPhase phase : JOURNEY_PHASES)
		{
			if (boundedKills >= phase.minKills && boundedKills <= phase.maxKills)
			{
				return phase.name;
			}
		}
		return "The First Scratches";
	}

	private static Map<Integer, List<String>> createMilestoneBursts()
	{
		Map<Integer, List<String>> bursts = new LinkedHashMap<>();
		bursts.put(100, List.of(
			"A pattern has formed.",
			"The first scratches cut into bronze.",
			"The Count is no longer chance."));
		bursts.put(1_000, List.of(
			"The campfires are talking.",
			"Your tally now has witnesses.",
			"The Counter has become a story."));
		bursts.put(10_000, List.of(
			"The story has hardened into belief.",
			"The tribes name this prophecy.",
			"Ten thousand dead have weight."));
		bursts.put(100_000, List.of(
			"The Long Count begins to eclipse memory.",
			"A hundred thousand dead bend myth around them.",
			"To goblinkind, you are architecture."));
		bursts.put(500_000, List.of(
			"Half the road to legend. Half the road to horror.",
			"The throne is half-built.",
			"The Count has become too large to dismiss."));
		bursts.put(1_000_000, List.of(
			"THE BRONZE THRONE STANDS COMPLETE.",
			"THE LONG COUNT IS FULFILLED.",
			"ONE MILLION DEAD. ONE NAME REMEMBERED."));
		return Map.copyOf(bursts);
	}

	private static final class JourneyPhase
	{
		private final int minKills;
		private final int maxKills;
		private final String name;

		private JourneyPhase(int minKills, int maxKills, String name)
		{
			this.minKills = minKills;
			this.maxKills = maxKills;
			this.name = name;
		}
	}
}
