package com.goblintracker.ui;

import com.goblintracker.GoblinKillTrackerConfig;
import com.goblintracker.branding.WarBranding;
import com.goblintracker.branding.WarPalette;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.ui.PluginPanel;

@Singleton
public class GoblinNavigation
{
	private final GoblinKillTrackerConfig config;

	@Inject
	public GoblinNavigation(GoblinKillTrackerConfig config)
	{
		this.config = config;
	}

	public NavigationButton createNavigation(PluginPanel panel)
	{
		return NavigationButton.builder()
			.tooltip(WarBranding.SIDEBAR_TOOLTIP)
			.icon(createIcon())
			.panel(panel)
			.build();
	}

	private BufferedImage createIcon()
	{
		WarPalette palette = WarPalette.forTheme(config.visualTheme());
		BufferedImage image = new BufferedImage(WarPalette.ICON_SIZE, WarPalette.ICON_SIZE, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		try
		{
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setStroke(new BasicStroke(1.0f));

			// Seal base
			graphics.setColor(palette.getIconBackground());
			graphics.fillOval(0, 0, 15, 15);
			graphics.setColor(withAlpha(Color.BLACK, 70));
			graphics.drawOval(0, 0, 15, 15);
			graphics.setColor(withAlpha(palette.getIconText(), 55));
			graphics.drawOval(1, 1, 13, 13);

			// Ledger plate
			graphics.setColor(withAlpha(palette.getIconText(), 190));
			graphics.fillRoundRect(4, 3, 8, 10, 2, 2);
			graphics.setColor(withAlpha(palette.getIconBackground(), 200));
			graphics.drawRoundRect(4, 3, 8, 10, 2, 2);

			// Tally marks carved on the plate.
			graphics.setColor(withAlpha(palette.getIconBackground(), 230));
			for (int x = 5; x <= 8; x++)
			{
				graphics.drawLine(x, 5, x, 9);
			}
			graphics.drawLine(5, 10, 9, 5);

			// Wax seal dot
			graphics.setColor(withAlpha(palette.getIconBackground(), 180));
			graphics.fillOval(9, 10, 2, 2);
		}
		finally
		{
			graphics.dispose();
		}
		return image;
	}

	private static Color withAlpha(Color color, int alpha)
	{
		int boundedAlpha = Math.max(0, Math.min(255, alpha));
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), boundedAlpha);
	}
}
