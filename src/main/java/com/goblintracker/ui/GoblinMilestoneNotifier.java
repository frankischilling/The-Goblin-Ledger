package com.goblintracker.ui;

import com.goblintracker.GoblinKillTrackerConfig;
import com.goblintracker.branding.WarBranding;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.Notifier;

@Singleton
public class GoblinMilestoneNotifier
{
	private final GoblinKillTrackerConfig config;
	private final Notifier notifier;
	private final Client client;

	private int lastMilestoneLifetime;

	@Inject
	public GoblinMilestoneNotifier(GoblinKillTrackerConfig config, Notifier notifier, Client client)
	{
		this.config = config;
		this.notifier = notifier;
		this.client = client;
	}

	public void reset(int lifetimeKills)
	{
		lastMilestoneLifetime = Math.max(0, lifetimeKills);
	}

	public boolean checkAndNotify(int lifetimeKills)
	{
		int interval = Math.max(1, config.milestoneInterval());
		if (lifetimeKills <= 0 || lifetimeKills % interval != 0 || lifetimeKills <= lastMilestoneLifetime)
		{
			return false;
		}

		lastMilestoneLifetime = lifetimeKills;
		String message = WarBranding.milestoneMessage(lifetimeKills);

		if (config.showFlavorText())
		{
			int stride = config.flavorLineStride();
			message = message
				+ " " + WarBranding.overallWriting(lifetimeKills, stride);
		}

		if (config.notifyWithPopup())
		{
			notifier.notify(message);
		}
		if (config.notifyInChat())
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", message, null);
		}

		return config.notifyOverlayFlash();
	}
}
