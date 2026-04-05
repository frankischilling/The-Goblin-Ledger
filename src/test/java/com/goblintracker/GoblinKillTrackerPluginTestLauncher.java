package com.goblintracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class GoblinKillTrackerPluginTestLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(GoblinKillTrackerPlugin.class);
		RuneLite.main(args);
	}
}
