package com.goblintracker.branding;

public enum WarToneMode
{
	UNHINGED_PROPHET("Unhinged prophet"),
	GRIM_BUREAUCRATIC("Grim + bureaucratic"),
	STRICT_SERIOUS("Strictly serious"),
	NEUTRAL_SIMPLE("Neutral/simple");

	private final String displayName;

	WarToneMode(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
