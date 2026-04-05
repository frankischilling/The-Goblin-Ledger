package com.goblintracker.detection;

import com.goblintracker.GoblinKillTrackerConfig;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.NPC;

@Singleton
public class DefaultGoblinTargetMatcher implements GoblinTargetMatcher
{
	private static final String BASE_GOBLIN_NAME = "goblin";

	private final GoblinKillTrackerConfig config;

	@Inject
	public DefaultGoblinTargetMatcher(GoblinKillTrackerConfig config)
	{
		this.config = config;
	}

	@Override
	public boolean matches(NPC npc)
	{
		if (npc == null || npc.getName() == null)
		{
			return false;
		}

		String normalizedName = npc.getName().trim().toLowerCase(Locale.ROOT);
		if (normalizedName.isEmpty())
		{
			return false;
		}

		if (parseNameSet(config.excludeNpcNames()).contains(normalizedName))
		{
			return false;
		}

		if (parseIdSet(config.includeNpcIds()).contains(npc.getId()))
		{
			return true;
		}

		return normalizedName.equals(BASE_GOBLIN_NAME) || normalizedName.startsWith(BASE_GOBLIN_NAME + " ");
	}

	private Set<String> parseNameSet(String raw)
	{
		if (raw == null || raw.isBlank())
		{
			return Set.of();
		}

		return Arrays.stream(raw.split(","))
			.map(value -> value == null ? "" : value.trim().toLowerCase(Locale.ROOT))
			.filter(value -> !value.isEmpty())
			.collect(Collectors.toSet());
	}

	private Set<Integer> parseIdSet(String raw)
	{
		if (raw == null || raw.isBlank())
		{
			return Set.of();
		}

		return Arrays.stream(raw.split(","))
			.map(String::trim)
			.filter(value -> !value.isEmpty())
			.map(this::tryParseInt)
			.filter(value -> value != null)
			.collect(Collectors.toSet());
	}

	private Integer tryParseInt(String value)
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException ex)
		{
			return null;
		}
	}
}
