package com.goblintracker.detection;

import net.runelite.api.NPC;

public interface GoblinTargetMatcher
{
	boolean matches(NPC npc);
}
