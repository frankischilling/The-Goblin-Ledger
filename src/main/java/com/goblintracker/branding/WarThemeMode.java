package com.goblintracker.branding;

public enum WarThemeMode
{
	LEDGER_PARCHMENT("Ledger parchment"),
	IRON_SIEGE("Iron siege"),
	RED_CAMPAIGN("Red campaign");

	private final String displayName;

	WarThemeMode(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
