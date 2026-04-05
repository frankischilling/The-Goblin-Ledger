package com.goblintracker.detection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GoblinKillDeduper
{
	private static final long DEFAULT_DEDUPE_WINDOW_MS = 5000L;

	private final long dedupeWindowMs;
	private final Map<Integer, Long> recentlyCountedNpcIndexes = new HashMap<>();

	@Inject
	public GoblinKillDeduper()
	{
		this(DEFAULT_DEDUPE_WINDOW_MS);
	}

	GoblinKillDeduper(long dedupeWindowMs)
	{
		this.dedupeWindowMs = Math.max(500L, dedupeWindowMs);
	}

	public synchronized boolean shouldCount(int npcIndex)
	{
		long now = System.currentTimeMillis();
		cleanup(now);

		Long lastCountedAt = recentlyCountedNpcIndexes.get(npcIndex);
		if (lastCountedAt != null && now - lastCountedAt < dedupeWindowMs)
		{
			return false;
		}

		recentlyCountedNpcIndexes.put(npcIndex, now);
		return true;
	}

	public synchronized void clear()
	{
		recentlyCountedNpcIndexes.clear();
	}

	private void cleanup(long now)
	{
		Iterator<Map.Entry<Integer, Long>> iterator = recentlyCountedNpcIndexes.entrySet().iterator();
		while (iterator.hasNext())
		{
			Map.Entry<Integer, Long> entry = iterator.next();
			if (now - entry.getValue() >= dedupeWindowMs)
			{
				iterator.remove();
			}
		}
	}
}
