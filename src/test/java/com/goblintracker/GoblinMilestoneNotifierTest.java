package com.goblintracker;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.goblintracker.branding.WarToneMode;
import com.goblintracker.ui.GoblinMilestoneNotifier;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import net.runelite.api.Client;
import net.runelite.client.Notifier;

public class GoblinMilestoneNotifierTest
{
	@Test
	public void checkAndNotifyBuildsBrandedMessage() throws Exception
	{
		GoblinKillTrackerConfig config = mock(GoblinKillTrackerConfig.class);
		Notifier notifier = mock(Notifier.class);
		Client client = mock(Client.class);

		when(config.milestoneInterval()).thenReturn(100);
		when(config.notifyWithPopup()).thenReturn(true);
		when(config.notifyInChat()).thenReturn(false);
		when(config.notifyOverlayFlash()).thenReturn(true);
		when(config.toneMode()).thenReturn(WarToneMode.UNHINGED_PROPHET);
		when(config.showFlavorText()).thenReturn(false);

		GoblinMilestoneNotifier milestoneNotifier = new GoblinMilestoneNotifier(config, notifier, client);
		milestoneNotifier.reset(0);
		assertTrue(milestoneNotifier.checkAndNotify(100));

		ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
		verify(notifier).notify(captor.capture());
		assertTrue(captor.getValue().contains("Milestone:"));
	}
}
