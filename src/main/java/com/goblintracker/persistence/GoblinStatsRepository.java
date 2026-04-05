package com.goblintracker.persistence;

public interface GoblinStatsRepository
{
	int loadLifetimeKills();

	void saveLifetimeKills(int lifetimeKills);
}
