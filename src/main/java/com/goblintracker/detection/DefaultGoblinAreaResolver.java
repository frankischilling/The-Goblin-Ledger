package com.goblintracker.detection;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

@Singleton
public class DefaultGoblinAreaResolver implements GoblinAreaResolver
{
	private static final String AREA_UNKNOWN = "Unknown";
	private static final NamedArea[] AREAS = new NamedArea[]
	{
		area("Lumbridge", rect(3192, 3263, 3263, 3189)),
		area("Draynor Village", rect(3072, 3295, 3160, 3225)),
		area("Port Sarim", rect(3009, 3263, 3066, 3179)),
		area("South Falador Farm", rect(3008, 3325, 3069, 3263)),
		area("Draynor Manor", rect(3067, 3391, 3136, 3329)),
		area("Falador", rect(3062, 3315, 2934, 3393), rect(2934, 3325, 2866, 3393)),
		area("Falador East Bank", rect(3018, 3362, 3027, 3355)),
		area("Falador West Bank", rect(2948, 3372, 2955, 3363)),
		area("Musa Point", rect(2873, 3197, 2963, 3135)),
		area("Lumbridge Swamp", rect(3239, 3142, 3140, 3201)),
		area("Al Kharid", rect(3264, 3199, 3327, 3136)),
		area("Shanty Pass", rect(3283, 3133, 3320, 3109)),
		area("Grand Exchange", rect(3138, 3516, 3192, 3468)),
		area("Varrock", rect(3288, 3375, 3191, 3518), rect(3139, 3464, 3187, 3377)),
		area("Varrock East Bank", rect(3250, 3423, 3257, 3416)),
		area("Varrock West Bank", rect(3182, 3446, 3189, 3438)),
		area("Rimmington", rect(2987, 3194, 2916, 3235)),
		area("Ice Mountain", rect(2992, 3507, 3030, 3458)),
		area("Goblin Village", rect(2942, 3519, 2977, 3449)),
		area("Barbarian Village", rect(3037, 3459, 3125, 3393)),
		area("Taverley", rect(2880, 3484, 2941, 3412)),
		area("Catherby", rect(2780, 3479, 2868, 3404)),
		area("Camelot Castle", rect(2740, 3522, 2783, 3473)),
		area("Seers Village", rect(2677, 3520, 2743, 3456)),
		area("Seers Bank", rect(2718, 3497, 2731, 3486)),
		area("Burthorpe", rect(2879, 3584, 2934, 3524)),
		area("Canifis", rect(3457, 3519, 3518, 3456)),
		area("Motherlode Mine", rect(3703, 5704, 3788, 5623)),
		area("Edgeville", rect(3072, 3521, 3135, 3456)),
		area("Edgeville Bank", rect(3087, 3501, 3101, 3485)),
		area("Barbarian Fishing Spot", rect(3091, 3439, 3112, 3421)),
		area("East Ardougne", rect(2559, 3343, 2686, 3255)),
		area("East Ardougne Bank", rect(2611, 3337, 2622, 3328)),
		area("West Ardougne", rect(2434, 3340, 2559, 3261)),
		area("Yanille", rect(2531, 3135, 2622, 3072)),
		area("Yanille Bank", rect(2607, 3098, 2618, 3086)),
		area("Tree Gnome Stronghold", rect(2367, 3519, 2494, 3393)),
		area("Tree Gnome Stronghold Bank", rect(2437, 3490, 2443, 3485), rect(2446, 3483, 2452, 3476)),
		area("Hosidius", rect(1712, 3646, 1802, 3520)),
		area("Vinery", rect(1791, 3584, 1855, 3521)),
		area("Kourend Castle", rect(1588, 3704, 1692, 3643)),
		area("Farming Guild", rect(1215, 3775, 1278, 3712)),
		area("Lumbridge Farm", rect(3134, 3346, 3213, 3255)),
		area("Gem Crab", rect(1267, 3181, 1289, 3148), rect(1341, 3129, 1371, 3088), rect(1224, 3063, 1255, 3026)),
		area("Wintertodt", rect(1600, 4031, 1663, 3965)),
		area("Mining Guild", rect(3008, 3350, 3029, 3330)),
		area("Crafting Guild", rect(2913, 3294, 2943, 3265)),
		area("Fishing Guild", rect(2624, 3455, 2642, 3392), rect(2623, 3392, 2579, 3455)),
		area("Cooking Guild", rect(3136, 3455, 3149, 3443)),
		area("Heroes Guild", rect(2880, 3519, 2907, 3501)),
		area("Legends Guild", rect(2714, 3390, 2742, 3349)),
		area("Rangers Guild", rect(2653, 3439, 2667, 3425)),
		area("Warriors Guild", rect(2878, 3529, 2835, 3559)),
		area("Magic Guild", rect(2560, 9534, 2623, 9472)),
		area("Woodcutting Guild", rect(1560, 3519, 1659, 3467)),
		area("Slayer Tower", rect(3401, 3581, 3453, 3528)),
		area("Grotesque Guardians", rect(3397, 3583, 3459, 3525, 2)),
		area("Wilderness Level 1-10", rect(2944, 3594, 3327, 3522)),
		area("Wilderness Level 11-20", rect(2944, 3595, 3333, 3670)),
		area("Wilderness Level 21-30", rect(2944, 3674, 3341, 3752)),
		area("Wilderness Level 31-40", rect(2944, 3775, 3348, 3850)),
		area("Wilderness Level 50", rect(3346, 3848, 2945, 3914)),
		area("Wilderness Volcano", rect(3331, 3967, 3392, 3904)),
		area("Wilderness Bandit Camp", rect(3071, 3648, 3008, 3711)),
		area("Wilderness Resource Area", rect(3173, 3944, 3197, 3923)),
		area("Wilderness Agility Course", rect(2986, 3966, 3012, 3926), rect(2982, 3966, 3012, 3922)),
		area("KBD Lair", rect(3008, 3901, 3135, 3809)),
		area("Chaos Elemental", rect(3256, 3943, 3273, 3916)),
		area("Vet'ion", rect(3143, 3691, 3190, 3653), rect(3212, 3794, 3235, 3778)),
		area("Callisto", rect(3102, 3683, 3128, 3665), rect(3279, 3856, 3305, 3840)),
		area("Venenatis", rect(3307, 3809, 3334, 3784)),
		area("Crazy Archaeologist", rect(2952, 3709, 3001, 3676)),
		area("Scorpia", rect(3215, 3960, 3260, 3932)),
		area("Wilderness God Wars Dungeon", rect(3012, 3743, 3022, 3734)),
		area("Lumbridge Swamp Dungeon", rect(3165, 3177, 3175, 3169)),
		area("Edgeville Dungeon", rect(3082, 3484, 3103, 3461), rect(3111, 3454, 3119, 3448)),
		area("Dwarven Mine", rect(3005, 3455, 3026, 3438)),
		area("Varrock Sewers", rect(3234, 3462, 3241, 3454)),
		area("Taverley Dungeon", rect(2879, 3402, 2890, 3392)),
		area("Moss Giant Island", rect(2687, 3220, 2706, 3199)),
		area("Asgarnian Ice Dungeon", rect(3003, 3157, 3012, 3143)),
		area("Brimhaven Dungeon", rect(2739, 3157, 2749, 3145), rect(2753, 3070, 2766, 3058)),
		area("Smoke Dungeon", rect(3305, 2965, 3314, 2959)),
		area("Gnome Agility Course", rect(2466, 3446, 2492, 3407)),
		area("Barbarian Agility Course", rect(2519, 3585, 2558, 3527)),
		area("Penguin Agility Course", rect(2625, 4093, 2687, 3966)),
		area("Ape Atoll Agility Course", rect(2742, 2757, 2766, 2720)),
		area("Draynor Rooftop Course", rect(3096, 3282, 3106, 3274)),
		area("Al Kharid Rooftop Course", rect(3270, 3198, 3277, 3194)),
		area("Varrock Rooftop Course", rect(3211, 3422, 3222, 3407)),
		area("Canifis Rooftop Course", rect(3503, 3499, 3512, 3488)),
		area("Falador Rooftop Course", rect(3033, 3348, 3042, 3340)),
		area("Seers Rooftop Course", rect(2717, 3498, 2732, 3484)),
		area("Pollnivneach Rooftop Course", rect(3345, 2970, 3353, 2961)),
		area("Rellekka Rooftop Course", rect(2620, 3679, 2627, 3670)),
		area("Shayzien Agility Course", rect(1549, 3635, 1555, 3628)),
		area("Pollnivneach", rect(3391, 2944, 3328, 3007)),
		area("Nardah", rect(3392, 2943, 3455, 2880)),
		area("Sophanem", rect(3264, 2815, 3327, 2747)),
		area("Port Phasmatys", rect(3648, 3519, 3711, 3456)),
		area("Mort'ton", rect(3470, 3309, 3508, 3265)),
		area("Mos Le'Harmless", rect(3648, 3070, 3853, 2924)),
		area("Tai Bwo Wannai", rect(2757, 3112, 2815, 3029)),
		area("Shilo Village", rect(2815, 3007, 2879, 2944)),
		area("Jatizso", rect(2368, 3838, 2431, 3776)),
		area("Neitiznot", rect(2303, 3839, 2367, 3772)),
		area("Rellekka", rect(2611, 3711, 2687, 3648)),
		area("Piscatoris", rect(2305, 3703, 2359, 3648)),
		area("Zanaris", rect(3200, 3172, 3206, 3165)),
		area("Waterbirth Island", rect(2496, 3775, 2559, 3712)),
		area("Miscellania", rect(2491, 3902, 2624, 3839)),
		area("Entrana", rect(2803, 3390, 2879, 3327)),
		area("Karamja", rect(2751, 3132, 3006, 2879)),
		area("Crandor", rect(2813, 3326, 2863, 3222)),
		area("Kandarin", rect(2544, 3391, 2634, 3337)),
		area("Misthalin", rect(3200, 3600, 3500, 3200)),
		area("Desert", rect(3206, 3131, 3505, 2884), rect(3148, 3071, 3206, 2880), rect(3141, 2878, 3442, 2669)),
		area("Fremennik", rect(2600, 3900, 2900, 3500)),
		area("Tirannwn", rect(2150, 3150, 2400, 2900)),
		area("Morytania", rect(3400, 3900, 3800, 3200)),
		area("Lava Maze", rect(3009, 3901, 3134, 3801)),
		area("Mage Arena", rect(3073, 3967, 3134, 3904)),
		area("Mage Bank", rect(3087, 3959, 3097, 3953)),
		area("Corporeal Beast", rect(3200, 3684, 3207, 3677)),
		area("Zulrah", rect(2255, 3082, 2285, 3065)),
		area("Vorkath", rect(2235, 4099, 2303, 4033)),
		area("Gauntlet", rect(3224, 6120, 3232, 6111)),
		area("Corrupted Gauntlet", rect(3224, 6120, 3232, 6111)),
		area("Theatre of Blood", rect(3648, 3240, 3699, 3199)),
		area("Chambers of Xeric", rect(1192, 3607, 1300, 3517)),
		area("Tombs of Amascut", rect(3313, 2750, 3395, 2687)),
		area("Nightmare Zone", rect(2582, 3131, 2623, 3111)),
		area("Stronghold of Security", rect(3078, 3422, 3083, 3417)),
		area("TzHaar Fight Caves", rect(2852, 3172, 2859, 3165)),
		area("Inferno", rect(2852, 3172, 2859, 3165)),
		area("Fishing Trawler", rect(2693, 3142, 2635, 3176)),
		area("Digsite", rect(3326, 3445, 3394, 3373)),
		area("Volcanic Mine", rect(3739, 3841, 3830, 3728)),
		area("Gnome Stronghold Agility", rect(3327, 3263, 3391, 3200)),
		area("Duel Arena", rect(3347, 3264, 3377, 3235)),
		area("Pest Control", rect(2627, 2682, 2687, 2624)),
		area("Barrows", rect(3519, 3327, 3583, 3264)),
		area("Mos Le'Harmless Cave", rect(3745, 2976, 3750, 2970)),
		area("Chasm of Fire", rect(1429, 3679, 1446, 3661)),
		area("Catacombs of Kourend", rect(1632, 3676, 1640, 3670)),
		area("Lizardman Canyon", rect(1474, 3725, 1533, 3680)),
		area("Fossil Island", rect(3622, 3905, 3861, 3691)),
		area("Wyvern Cave", rect(3680, 3852, 3674, 3857)),
		area("Dorgesh-Kaan", rect(2670, 5300, 2820, 5410))
	};

	@Inject
	public DefaultGoblinAreaResolver()
	{
	}

	@Override
	public String resolveArea(NPC npc)
	{
		if (npc == null)
		{
			return AREA_UNKNOWN;
		}

		WorldPoint point = npc.getWorldLocation();
		if (point == null)
		{
			return AREA_UNKNOWN;
		}

		int x = point.getX();
		int y = point.getY();
		int plane = point.getPlane();

		for (NamedArea area : AREAS)
		{
			if (area.matches(x, y, plane))
			{
				return area.name;
			}
		}

		return AREA_UNKNOWN;
	}

	private static NamedArea area(String name, Rect... rects)
	{
		return new NamedArea(name, rects);
	}

	private static Rect rect(int x1, int y1, int x2, int y2)
	{
		return new Rect(x1, y1, x2, y2, null);
	}

	private static Rect rect(int x1, int y1, int x2, int y2, int plane)
	{
		return new Rect(x1, y1, x2, y2, plane);
	}

	private static final class NamedArea
	{
		private final String name;
		private final Rect[] rects;

		private NamedArea(String name, Rect[] rects)
		{
			this.name = name;
			this.rects = rects;
		}

		private boolean matches(int x, int y, int plane)
		{
			for (Rect rect : rects)
			{
				if (rect.contains(x, y, plane))
				{
					return true;
				}
			}
			return false;
		}
	}

	private static final class Rect
	{
		private final int minX;
		private final int maxX;
		private final int minY;
		private final int maxY;
		private final Integer plane;

		private Rect(int x1, int y1, int x2, int y2, Integer plane)
		{
			this.minX = Math.min(x1, x2);
			this.maxX = Math.max(x1, x2);
			this.minY = Math.min(y1, y2);
			this.maxY = Math.max(y1, y2);
			this.plane = plane;
		}

		private boolean contains(int x, int y, int plane)
		{
			if (this.plane != null && this.plane != plane)
			{
				return false;
			}

			return x >= minX && x <= maxX && y >= minY && y <= maxY;
		}
	}
}
