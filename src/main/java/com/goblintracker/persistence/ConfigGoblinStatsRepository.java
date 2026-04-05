package com.goblintracker.persistence;

import com.goblintracker.GoblinKillTrackerPlugin;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.config.ConfigManager;

@Singleton
public class ConfigGoblinStatsRepository implements GoblinStatsRepository
{
	private static final String LIFETIME_KILLS_KEY = "lifetimeGoblinKills";

	private final ConfigManager configManager;

	@Inject
	public ConfigGoblinStatsRepository(ConfigManager configManager)
	{
		this.configManager = configManager;
	}

	@Override
	public int loadLifetimeKills()
	{
		Integer storedKills = configManager.getRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			LIFETIME_KILLS_KEY,
			Integer.class);
		return storedKills == null ? 0 : storedKills;
	}

	@Override
	public void saveLifetimeKills(int lifetimeKills)
	{
		configManager.setRSProfileConfiguration(
			GoblinKillTrackerPlugin.CONFIG_GROUP,
			LIFETIME_KILLS_KEY,
			Math.max(0, lifetimeKills));
	}
}
