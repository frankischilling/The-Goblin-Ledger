package com.goblintracker.model;

import com.goblintracker.detection.KillSource;
import java.time.Instant;

public class GoblinKillRecord
{
	private final int npcId;
	private final String npcName;
	private final int npcIndex;
	private final Instant timestamp;
	private final KillSource source;
	private final String areaName;
	private final int itemCount;
	private final long totalLootQuantity;

	public GoblinKillRecord(int npcId, String npcName, int npcIndex, Instant timestamp, KillSource source)
	{
		this(npcId, npcName, npcIndex, timestamp, source, "Unknown", 0, 0L);
	}

	public GoblinKillRecord(
		int npcId,
		String npcName,
		int npcIndex,
		Instant timestamp,
		KillSource source,
		String areaName,
		int itemCount,
		long totalLootQuantity)
	{
		this.npcId = npcId;
		this.npcName = npcName;
		this.npcIndex = npcIndex;
		this.timestamp = timestamp;
		this.source = source;
		this.areaName = areaName;
		this.itemCount = itemCount;
		this.totalLootQuantity = totalLootQuantity;
	}

	public int getNpcId()
	{
		return npcId;
	}

	public String getNpcName()
	{
		return npcName;
	}

	public int getNpcIndex()
	{
		return npcIndex;
	}

	public Instant getTimestamp()
	{
		return timestamp;
	}

	public KillSource getSource()
	{
		return source;
	}

	public String getAreaName()
	{
		return areaName;
	}

	public int getItemCount()
	{
		return itemCount;
	}

	public long getTotalLootQuantity()
	{
		return totalLootQuantity;
	}

	public GoblinKillRecord withDetails(String areaName, int itemCount, long totalLootQuantity)
	{
		return new GoblinKillRecord(
			npcId,
			npcName,
			npcIndex,
			timestamp,
			source,
			areaName == null || areaName.isBlank() ? "Unknown" : areaName,
			Math.max(0, itemCount),
			Math.max(0L, totalLootQuantity));
	}
}
