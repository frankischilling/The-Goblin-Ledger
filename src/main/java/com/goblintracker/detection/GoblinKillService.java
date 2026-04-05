package com.goblintracker.detection;

import com.goblintracker.GoblinKillTrackerConfig;
import com.goblintracker.model.GoblinKillRecord;
import java.time.Instant;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;

@Singleton
public class GoblinKillService
{
	private final Client client;
	private final GoblinKillTrackerConfig config;
	private final GoblinTargetMatcher targetMatcher;
	private final GoblinKillDeduper deduper;

	@Inject
	public GoblinKillService(
		Client client,
		GoblinKillTrackerConfig config,
		GoblinTargetMatcher targetMatcher,
		GoblinKillDeduper deduper)
	{
		this.client = client;
		this.config = config;
		this.targetMatcher = targetMatcher;
		this.deduper = deduper;
	}

	public Optional<GoblinKillRecord> processLootNpc(NPC npc)
	{
		if (!targetMatcher.matches(npc))
		{
			return Optional.empty();
		}

		if (!deduper.shouldCount(npc.getIndex()))
		{
			return Optional.empty();
		}

		return Optional.of(createRecord(npc, KillSource.LOOT));
	}

	public Optional<GoblinKillRecord> processDespawnNpc(NPC npc)
	{
		if (!config.useDespawnFallback() || !targetMatcher.matches(npc))
		{
			return Optional.empty();
		}

		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null || npc.getInteracting() != localPlayer)
		{
			return Optional.empty();
		}

		if (!deduper.shouldCount(npc.getIndex()))
		{
			return Optional.empty();
		}

		return Optional.of(createRecord(npc, KillSource.DESPAWN));
	}

	public void clear()
	{
		deduper.clear();
	}

	private GoblinKillRecord createRecord(NPC npc, KillSource source)
	{
		return new GoblinKillRecord(npc.getId(), npc.getName(), npc.getIndex(), Instant.now(), source);
	}
}
