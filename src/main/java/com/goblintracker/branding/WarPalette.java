package com.goblintracker.branding;

import java.awt.Color;

public final class WarPalette
{
	public static final int ICON_SIZE = 16;
	public static final long FLASH_DURATION_MS = 1500L;

	private final Color overlayBackground;
	private final Color overlayFlashBackground;
	private final Color overlayHeadingColor;
	private final Color iconBackground;
	private final Color iconText;

	private WarPalette(
		Color overlayBackground,
		Color overlayFlashBackground,
		Color overlayHeadingColor,
		Color iconBackground,
		Color iconText)
	{
		this.overlayBackground = overlayBackground;
		this.overlayFlashBackground = overlayFlashBackground;
		this.overlayHeadingColor = overlayHeadingColor;
		this.iconBackground = iconBackground;
		this.iconText = iconText;
	}

	public static WarPalette forTheme(WarThemeMode mode)
	{
		WarThemeMode resolvedMode = mode == null ? WarThemeMode.LEDGER_PARCHMENT : mode;
		switch (resolvedMode)
		{
			case IRON_SIEGE:
				return new WarPalette(
					new Color(57, 64, 72, 174),
					new Color(108, 133, 98, 208),
					new Color(201, 216, 226),
					new Color(83, 99, 112),
					new Color(234, 239, 244));
			case RED_CAMPAIGN:
				return new WarPalette(
					new Color(89, 58, 53, 172),
					new Color(160, 83, 67, 208),
					new Color(236, 199, 141),
					new Color(138, 77, 62),
					new Color(249, 235, 211));
			case LEDGER_PARCHMENT:
			default:
				return new WarPalette(
					new Color(74, 62, 49, 166),
					new Color(47, 125, 73, 198),
					new Color(211, 186, 132),
					new Color(46, 107, 61),
					new Color(243, 235, 218));
		}
	}

	public Color getOverlayBackground()
	{
		return overlayBackground;
	}

	public Color getOverlayFlashBackground()
	{
		return overlayFlashBackground;
	}

	public Color getOverlayHeadingColor()
	{
		return overlayHeadingColor;
	}

	public Color getIconBackground()
	{
		return iconBackground;
	}

	public Color getIconText()
	{
		return iconText;
	}
}
