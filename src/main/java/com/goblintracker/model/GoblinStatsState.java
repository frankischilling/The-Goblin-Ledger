package com.goblintracker.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoblinStatsState
{
	private static final int MAX_RECENT_KILLS = 25;

	private int sessionKills;
	private int tripKills;
	private int lifetimeKills;
	private long sessionStartedAtMs;
	private long tripStartedAtMs;
	private final Map<String, Integer> areaKillCounts = new HashMap<>();
	private final Map<String, Integer> dailyKillCounts = new HashMap<>();
	private final Map<Integer, Long> todayLootTotals = new HashMap<>();
	private final Map<Integer, Long> lifetimeLootTotals = new HashMap<>();
	private final Map<Integer, Long> milestoneReachedAtMs = new HashMap<>();
	private final Deque<GoblinKillRecord> recentKills = new ArrayDeque<>();

	public GoblinStatsState()
	{
		long now = System.currentTimeMillis();
		this.sessionStartedAtMs = now;
		this.tripStartedAtMs = now;
	}

	public void resetAll()
	{
		sessionKills = 0;
		tripKills = 0;
		lifetimeKills = 0;
		long now = System.currentTimeMillis();
		sessionStartedAtMs = now;
		tripStartedAtMs = now;
		areaKillCounts.clear();
		dailyKillCounts.clear();
		todayLootTotals.clear();
		lifetimeLootTotals.clear();
		milestoneReachedAtMs.clear();
		recentKills.clear();
	}

	public void setLifetimeKills(int lifetimeKills)
	{
		this.lifetimeKills = Math.max(0, lifetimeKills);
	}

	public void setTodayLootTotals(Map<Integer, Long> lootTotals)
	{
		todayLootTotals.clear();
		mergeLoot(todayLootTotals, lootTotals);
	}

	public void setLifetimeLootTotals(Map<Integer, Long> lootTotals)
	{
		lifetimeLootTotals.clear();
		mergeLoot(lifetimeLootTotals, lootTotals);
	}

	public void setMilestoneReachedAtMs(Map<Integer, Long> milestoneTimes)
	{
		milestoneReachedAtMs.clear();
		if (milestoneTimes == null || milestoneTimes.isEmpty())
		{
			return;
		}

		for (Map.Entry<Integer, Long> entry : milestoneTimes.entrySet())
		{
			if (entry == null || entry.getKey() == null || entry.getValue() == null || entry.getKey() <= 0 || entry.getValue() <= 0L)
			{
				continue;
			}

			milestoneReachedAtMs.put(entry.getKey(), entry.getValue());
		}
	}

	public void setDailyKillCounts(Map<String, Integer> dailyCounts)
	{
		dailyKillCounts.clear();
		if (dailyCounts == null || dailyCounts.isEmpty())
		{
			return;
		}

		for (Map.Entry<String, Integer> entry : dailyCounts.entrySet())
		{
			if (entry == null || entry.getKey() == null || entry.getValue() == null)
			{
				continue;
			}

			String dateKey = entry.getKey().trim();
			if (dateKey.isBlank())
			{
				continue;
			}

			dailyKillCounts.put(dateKey, Math.max(0, entry.getValue()));
		}
	}

	public void setAreaKillCounts(Map<String, Integer> areaCounts)
	{
		areaKillCounts.clear();
		if (areaCounts == null || areaCounts.isEmpty())
		{
			return;
		}

		for (Map.Entry<String, Integer> entry : areaCounts.entrySet())
		{
			if (entry == null || entry.getKey() == null || entry.getValue() == null)
			{
				continue;
			}

			String areaName = entry.getKey().trim();
			if (areaName.isBlank())
			{
				continue;
			}

			areaKillCounts.put(areaName, Math.max(0, entry.getValue()));
		}
	}

	public boolean recordMilestoneReachedAt(int milestoneKills, long reachedAtMs)
	{
		if (milestoneKills <= 0 || reachedAtMs <= 0L || milestoneReachedAtMs.containsKey(milestoneKills))
		{
			return false;
		}

		milestoneReachedAtMs.put(milestoneKills, reachedAtMs);
		return true;
	}

	public void incrementKill()
	{
		sessionKills++;
		tripKills++;
		lifetimeKills++;
	}

	public void recordKill(GoblinKillRecord killRecord, Map<Integer, Long> killLootTotals, String dateKey)
	{
		incrementKill();

		String areaName = killRecord.getAreaName() == null || killRecord.getAreaName().isBlank()
			? "Unknown"
			: killRecord.getAreaName();
		areaKillCounts.merge(areaName, 1, Integer::sum);
		if (dateKey != null && !dateKey.isBlank())
		{
			dailyKillCounts.merge(dateKey, 1, Integer::sum);
		}

		if (killLootTotals != null)
		{
			mergeLoot(todayLootTotals, killLootTotals);
			mergeLoot(lifetimeLootTotals, killLootTotals);
		}

		recentKills.addFirst(killRecord);
		while (recentKills.size() > MAX_RECENT_KILLS)
		{
			recentKills.removeLast();
		}
	}

	public void resetSession()
	{
		sessionKills = 0;
		sessionStartedAtMs = System.currentTimeMillis();
	}

	public void resetTrip()
	{
		tripKills = 0;
		tripStartedAtMs = System.currentTimeMillis();
	}

	public int getSessionKills()
	{
		return sessionKills;
	}

	public int getTripKills()
	{
		return tripKills;
	}

	public int getLifetimeKills()
	{
		return lifetimeKills;
	}

	public int getSessionKillsPerHour()
	{
		if (sessionKills <= 0)
		{
			return 0;
		}

		long elapsedMs = Math.max(1L, System.currentTimeMillis() - sessionStartedAtMs);
		double killsPerHour = (sessionKills * 3_600_000D) / elapsedMs;
		return (int) Math.round(killsPerHour);
	}

	public Map<String, Integer> getAreaKillCounts()
	{
		return new HashMap<>(areaKillCounts);
	}

	public Map<Integer, Long> getLootTotals()
	{
		return getLifetimeLootTotals();
	}

	public Map<Integer, Long> getTodayLootTotals()
	{
		return new HashMap<>(todayLootTotals);
	}

	public Map<Integer, Long> getLifetimeLootTotals()
	{
		return new HashMap<>(lifetimeLootTotals);
	}

	public Map<Integer, Long> getMilestoneReachedAtMs()
	{
		return new HashMap<>(milestoneReachedAtMs);
	}

	public Map<String, Integer> getDailyKillCounts()
	{
		return new HashMap<>(dailyKillCounts);
	}

	public List<GoblinKillRecord> getRecentKills()
	{
		return new ArrayList<>(recentKills);
	}

	private static void mergeLoot(Map<Integer, Long> destination, Map<Integer, Long> source)
	{
		if (source == null || source.isEmpty())
		{
			return;
		}

		for (Map.Entry<Integer, Long> entry : source.entrySet())
		{
			if (entry == null || entry.getKey() == null)
			{
				continue;
			}

			destination.merge(entry.getKey(), Math.max(0L, entry.getValue() == null ? 0L : entry.getValue()), Long::sum);
		}
	}
}
