# The Goblin Ledger

The Goblin Ledger is a RuneLite plugin for players who committed to an unreasonable objective:

**eliminate 1,000,000 goblins.**

This plugin tracks your campaign progress with a single canon goblin-book voice, milestone notifications, and practical stats like rate and ETA.

## Goal

Track the full journey from 0 to 1,000,000 goblin kills with clear progress and milestone feedback.

## Current Features

- Lifetime, session, and trip goblin kill tracking
- Campaign completion and hostiles remaining
- Real milestone progress bar (next threshold window)
- Milestone ETA based on current kills per hour
- Campaign progress bar toward 1,000,000
- Area breakdown (where kills happen)
- Loot tracking split into two sections:
  - Today's loot (date-scoped)
  - Overall loot (all-time persisted totals)
- Recent kill chronicle/history
- Canon Book tab with the full Bronze Count lore, readable in a scrollable book-style panel
- Milestone notifications:
  - RuneLite popup
  - In-game chat message
  - Overlay flash
- Data management tools:
  - Export counters and loot totals to a file
  - Import counters and loot totals from that file
  - Reset all counters and persisted loot history
- Single canon goblin-book lore voice
- Multiple visual theme modes
- Sidebar panel and overlay support

## Plugin Identity

- Display name: The Goblin Ledger
- Tagline: Big High War God count every goblin.
- Description: Canon war-book tracker for the one million goblin prophecy

## Canon Lore Voice

The tracker now uses one unified lore style inspired by the goblin war-book tradition:

- Creation era framing: goblins became the army when other races refused.
- Commandments framing: slay enemies, never flee, never doubt command.
- Prophecy framing: tribes must not war each other, and a new Commander will lead final conquest.

All panel labels, flavor lines, and milestone messages use this same canon voice.

## Development Setup

1. Clone this repository.
2. Open it in IntelliJ or VS Code.
3. Run tests:

```powershell
.\gradlew test
```

4. Run the development client:

```powershell
.\gradlew run
```

## Main Files

- Plugin entry: src/main/java/com/goblintracker/GoblinKillTrackerPlugin.java
- Config: src/main/java/com/goblintracker/GoblinKillTrackerConfig.java
- Overlay: src/main/java/com/goblintracker/GoblinKillTrackerOverlay.java
- Sidebar panel UI: src/main/java/com/goblintracker/ui/GoblinPanel.java
- Branding and text system: src/main/java/com/goblintracker/branding/WarBranding.java
- Theme palette: src/main/java/com/goblintracker/branding/WarPalette.java
- Plugin metadata: runelite-plugin.properties

## Configuration Overview

The plugin currently supports options for:

- Overlay visibility and displayed stats
- Session and trip reset controls
- Sidebar visibility
- Milestone interval and notification channels
- Visual theme mode
- Flavor text and cadence
- Data file path configuration for export/import
- One-click export/import toggles
- Full reset toggle (session, trip, lifetime, and loot totals)

## Data Export / Import

The plugin can write and read a simple `.properties` export file.

- Set **Data file path** in plugin config.
- Toggle **Export data** to write the current tracked data.
- Toggle **Import data** to load previously exported data.

Exported data includes:

- Lifetime goblin kill count
- Today's loot totals (date bucket)
- Overall loot totals (all-time)

Notes:

- Relative file paths resolve under your user home directory.
- If imported loot date does not match today's date, the imported loot is kept in overall totals while today's bucket starts empty.

## License

This project is licensed under the GNU General Public License v3.0.
See the LICENSE file for full terms.


## Roadmap Ideas

- Additional milestone ceremony visuals
- Optional compact panel mode
- More configurable tab layouts
- More milestone/title packs

---

If your goal is absurd, your tracker should take it seriously.
