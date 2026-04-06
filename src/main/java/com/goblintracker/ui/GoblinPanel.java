package com.goblintracker.ui;

import com.goblintracker.GoblinKillTrackerConfig;
import com.goblintracker.GoblinKillTrackerPlugin;
import com.goblintracker.branding.WarBranding;
import com.goblintracker.branding.WarPalette;
import com.goblintracker.model.GoblinKillRecord;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.DefaultCaret;
import javax.swing.text.View;
import net.runelite.client.ui.PluginPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class GoblinPanel extends PluginPanel
{
	private static final Logger log = LoggerFactory.getLogger(GoblinPanel.class);
	private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
	private static final DateTimeFormatter MILESTONE_HIT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
	private static final Font PANEL_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);
	private static final Font TAB_FONT = new Font(Font.DIALOG, Font.BOLD, 12);
	private static final Font HEADER_FONT = new Font(Font.DIALOG, Font.BOLD, 15);
	private static final Font SUBHEADER_FONT = new Font(Font.DIALOG, Font.BOLD, 12);
	private static final Font WRITING_FONT = new Font(Font.DIALOG, Font.ITALIC, 12);
	private static final Font BOOK_FONT = new Font(Font.SERIF, Font.PLAIN, 13);
	private static final Font BOOK_FONT_COMPACT = new Font(Font.SERIF, Font.PLAIN, 12);
	private static final Font TOC_FONT = new Font(Font.SERIF, Font.PLAIN, 12);
	private static final Font TOC_FONT_COMPACT = new Font(Font.SERIF, Font.PLAIN, 11);
	private static final int LABEL_WRAP_COLUMNS = 42;
	private static final int AREA_WRAP_COLUMNS = 58;
	private static final int BOOK_VIEWPORT_PREFERRED_HEIGHT = 520;
	private static final int LORE_COMPACT_WIDTH_THRESHOLD = 240;
	private static final String MANUSCRIPT_MENU_LABEL = "Open Manuscript Window";
	private static final String MANUSCRIPT_WINDOW_TITLE = "The Bronze Count Manuscript";
	private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");

	private final GoblinKillTrackerPlugin plugin;
	private final JTabbedPane tabs = new JTabbedPane();
	private Color tabUnselectedBackground = new Color(72, 66, 58);
	private Color tabSelectedBackground = new Color(120, 108, 92);
	private Color tabUnselectedForeground = new Color(226, 220, 208);
	private Color tabSelectedForeground = new Color(18, 18, 18);

	private final JPanel overviewTab = new JPanel(new BorderLayout(0, 8));
	private final JPanel overviewTopPanel = new JPanel(new BorderLayout(0, 8));
	private final JPanel overviewHeaderPanel = new JPanel(new BorderLayout(0, 4));
	private final JPanel overviewProgressPanel = new JPanel(new GridLayout(0, 1, 0, 4));
	private final JPanel loreBookTab = new JPanel(new BorderLayout(0, 8));
	private final JPanel loreBookTopPanel = new JPanel(new BorderLayout(0, 6));
	private final JPanel loreTocPanel = new JPanel(new GridLayout(0, 1, 0, 2));
	private final JPanel loreBookHeaderPanel = new JPanel(new BorderLayout(0, 2));
	private final JTabbedPane loreSubTabs = new JTabbedPane();
	private final JLabel headingLabel = new JLabel(WarBranding.PLUGIN_NAME);
	private final JLabel loreBookTitleLabel = new JLabel("THE BRONZE COUNT");
	private final JLabel loreBookSubtitleLabel = new JLabel("A Goblin Chronicle of War, Prophecy, and the Million Dead");
	private final JLabel loreTocTitleLabel = new JLabel("Table of Contents");
	private final JButton loreOpenWindowButton = new JButton("Open Full Lore Window");
	private final JLabel overallWritingValue = new JLabel(" ");
	private final JLabel campaignProgressLabel = new JLabel();
	private final JProgressBar campaignProgressBar = createProgressBar();
	private final JLabel milestoneProgressLabel = new JLabel();
	private final JProgressBar milestoneProgressBar = createProgressBar();
	private final JLabel milestoneWindowLabel = new JLabel();
	private final JLabel milestoneEtaLabel = new JLabel();

	private final JTextArea overviewArea = createReadOnlyArea();
	private final JTextArea statsArea = createReadOnlyArea();
	private final JTextArea statsWindowDetailsArea = createReadOnlyArea();
	private final JTextArea areasArea = createReadOnlyArea();
	private final JTextArea lootArea = createReadOnlyArea();
	private final JTextArea historyArea = createReadOnlyArea();
	private final JTextArea loreBookArea = createBookArea();
	private final JScrollPane loreBookScrollPane = wrapArea(loreBookArea);
	private final Map<String, String> canonSectionHeadings = createCanonSectionHeadings();
	private final Map<String, JButton> loreTocButtons = createLoreTocButtons();
	private final List<WarBranding.LoreUnlockEntry> loreUnlockEntries = WarBranding.loreUnlockEntries();
	private final Map<Integer, JTextArea> loreUnlockAreas = new LinkedHashMap<>();
	private final StatsGraphPanel statsGraphPanel = new StatsGraphPanel();
	private JFrame statsWindow;
	private JLabel statsWindowSummaryLabel;
	private StatsRange selectedStatsRange = StatsRange.THIRTY_DAYS;
	private StatsRenderMode selectedStatsRenderMode = StatsRenderMode.BARS_AND_TREND;
	private boolean showMovingAverage = true;
	private int previousSelectedMainTab = 0;
	private int pendingCanonScrollOffset = -1;

	@Inject
	public GoblinPanel(GoblinKillTrackerPlugin plugin)
	{
		this.plugin = plugin;
		setLayout(new BorderLayout());
		setBorder(new EmptyBorder(6, 6, 6, 6));
		buildOverviewTab();
		buildLoreBookTab();
		installTabUi();
		tabs.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);

		tabs.addTab("", overviewTab);
		tabs.addTab("", wrapArea(statsArea));
		tabs.addTab("", wrapArea(areasArea));
		tabs.addTab("", wrapArea(lootArea));
		tabs.addTab("", wrapArea(historyArea));
		tabs.addTab("", loreBookTab);
		tabs.addChangeListener(e -> {
			handleMainTabSelection();
			applyTabSelectionColors();
		});
		applyTabLabels();
		applyTheme(plugin.getConfig());

		add(tabs, BorderLayout.CENTER);
	}

	public void refresh()
	{
		GoblinKillTrackerConfig config = plugin.getConfig();

		String overviewText = buildOverviewText(config);
		String statsText = buildStatsText();
		String areasText = buildAreasText();
		String lootText = buildLootText();
		String historyText = buildHistoryText();
		String loreText = buildLoreText();
		int lifetimeKills = plugin.getLifetimeGoblinKills();
		int sessionRate = plugin.getSessionKillsPerHour();
		int flavorStride = config == null ? 25 : config.flavorLineStride();

		String overallWriting = WarBranding.overallWriting(lifetimeKills, flavorStride);
		String campaignProgress = WarBranding.campaignProgressSummary(lifetimeKills);
		int campaignPercent = WarBranding.campaignProgressPercent(lifetimeKills);
		String milestoneProgress = WarBranding.milestoneProgressSummary(lifetimeKills);
		int milestonePercent = WarBranding.milestoneProgressPercent(lifetimeKills);
		String milestoneWindow = WarBranding.milestoneWindowText(lifetimeKills);
		String milestoneEta = WarBranding.milestoneEtaSummary(lifetimeKills, sessionRate);

		SwingUtilities.invokeLater(() -> {
			applyTabLabels();
			applyTheme(config);
			headingLabel.setText(formatHeaderText(WarBranding.PLUGIN_NAME, "Track the war ledger"));
			overallWritingValue.setText(wrapLabelText(overallWriting));
			campaignProgressLabel.setText(wrapLabelText(WarBranding.overviewCampaignProgressLabel().trim(), true));
			campaignProgressBar.setValue(campaignPercent);
			campaignProgressBar.setString(campaignPercent + "%");
			campaignProgressBar.setToolTipText(campaignProgress);
			milestoneProgressLabel.setText(wrapLabelText(WarBranding.overviewMilestoneProgressLabel().trim(), true));
			milestoneProgressBar.setValue(milestonePercent);
			milestoneProgressBar.setString(milestonePercent + "%");
			milestoneProgressBar.setToolTipText(milestoneProgress);
			milestoneWindowLabel.setText(wrapKeyValueLabel(WarBranding.overviewNextTargetLabel(), milestoneWindow));
			milestoneEtaLabel.setText(wrapKeyValueLabel(WarBranding.overviewMilestoneEtaLabel(), milestoneEta));
			setReadableText(overviewArea, overviewText);
			setReadableText(statsArea, statsText);
			setReadableText(areasArea, areasText);
			setReadableText(lootArea, lootText);
			setReadableText(historyArea, historyText);
			setBookText(loreBookArea, loreText);
			refreshLoreUnlockTabs(lifetimeKills);
			refreshStatsWindow();
		});
	}

	private void handleMainTabSelection()
	{
		int selectedIndex = tabs.getSelectedIndex();
		if (selectedIndex == 1)
		{
			openStatsWindow();
			int fallbackIndex = resolveFallbackMainTabIndex();
			if (fallbackIndex != selectedIndex)
			{
				SwingUtilities.invokeLater(() -> tabs.setSelectedIndex(fallbackIndex));
			}
			return;
		}

		if (selectedIndex >= 0)
		{
			previousSelectedMainTab = selectedIndex;
		}
	}

	private int resolveFallbackMainTabIndex()
	{
		if (previousSelectedMainTab >= 0 && previousSelectedMainTab < tabs.getTabCount() && previousSelectedMainTab != 1)
		{
			return previousSelectedMainTab;
		}

		return 0;
	}

	private void openStatsWindow()
	{
		if (statsWindow != null && statsWindow.isDisplayable())
		{
			statsWindow.setVisible(true);
			statsWindow.toFront();
			refreshStatsWindow();
			return;
		}

		JFrame frame = new JFrame("Goblin Ledger Stats");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout(8, 8));
		frame.getRootPane().setBorder(new EmptyBorder(8, 8, 8, 8));

		JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
		controls.setOpaque(true);
		controls.setBackground(new Color(34, 32, 28));
		controls.setBorder(new EmptyBorder(6, 6, 6, 6));

		ButtonGroup rangeGroup = new ButtonGroup();
		for (StatsRange range : StatsRange.values())
		{
			JToggleButton button = new JToggleButton(range.label);
			button.setFocusable(false);
			button.setFont(PANEL_FONT);
			button.setSelected(range == selectedStatsRange);
			button.addActionListener(e -> {
				selectedStatsRange = range;
				refreshStatsWindow();
			});
			rangeGroup.add(button);
			controls.add(button);
		}

		ButtonGroup modeGroup = new ButtonGroup();
		for (StatsRenderMode mode : StatsRenderMode.values())
		{
			JToggleButton button = new JToggleButton(mode.label);
			button.setFocusable(false);
			button.setFont(PANEL_FONT);
			button.setSelected(mode == selectedStatsRenderMode);
			button.addActionListener(e -> {
				selectedStatsRenderMode = mode;
				refreshStatsWindow();
			});
			modeGroup.add(button);
			controls.add(button);
		}

		JToggleButton movingAverageButton = new JToggleButton("7D AVG");
		movingAverageButton.setFocusable(false);
		movingAverageButton.setFont(PANEL_FONT);
		movingAverageButton.setSelected(showMovingAverage);
		movingAverageButton.addActionListener(e -> {
			showMovingAverage = movingAverageButton.isSelected();
			refreshStatsWindow();
		});
		controls.add(movingAverageButton);

		statsWindowSummaryLabel = new JLabel(" ");
		statsWindowSummaryLabel.setFont(PANEL_FONT);
		statsWindowSummaryLabel.setForeground(new Color(232, 224, 208));

		JPanel summaryPanel = new JPanel(new BorderLayout());
		summaryPanel.setOpaque(true);
		summaryPanel.setBackground(new Color(34, 32, 28));
		summaryPanel.setBorder(new EmptyBorder(2, 8, 6, 8));
		summaryPanel.add(statsWindowSummaryLabel, BorderLayout.WEST);

		statsGraphPanel.setOpaque(true);
		statsGraphPanel.setBackground(new Color(24, 22, 19));
		styleArea(statsWindowDetailsArea, new Color(28, 26, 22), new Color(224, 216, 196));

		JScrollPane detailsScroll = wrapArea(statsWindowDetailsArea);
		detailsScroll.setPreferredSize(new Dimension(360, 0));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, statsGraphPanel, detailsScroll);
		splitPane.setResizeWeight(0.68D);
		splitPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		splitPane.setContinuousLayout(true);

		frame.add(controls, BorderLayout.NORTH);
		frame.add(splitPane, BorderLayout.CENTER);
		frame.add(summaryPanel, BorderLayout.SOUTH);
		frame.setMinimumSize(new Dimension(860, 520));
		frame.setSize(980, 620);
		frame.setLocationByPlatform(true);
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosed(WindowEvent event)
			{
				statsWindow = null;
			}
		});

		statsWindow = frame;
		refreshStatsWindow();
		frame.setVisible(true);
	}

	private void refreshStatsWindow()
	{
		if (statsWindow == null || !statsWindow.isDisplayable())
		{
			return;
		}

		Map<LocalDate, Integer> daily = parseDailyKillCounts(plugin.getDailyKillCounts());
		List<StatsPoint> points = buildStatsPoints(daily, selectedStatsRange);
		statsGraphPanel.setPoints(points, selectedStatsRange, selectedStatsRenderMode, showMovingAverage);

		int total = points.stream().mapToInt(point -> point.kills).sum();
		int peak = points.stream().mapToInt(point -> point.kills).max().orElse(0);
		long activeDays = points.stream().filter(point -> point.kills > 0).count();
		double average = points.isEmpty() ? 0.0D : total / (double) points.size();
		if (statsWindowSummaryLabel != null)
		{
			statsWindowSummaryLabel.setText(
				selectedStatsRange.label + " total: " + formatCount(total)
					+ " | peak: " + formatCount(peak)
					+ " | avg/day: " + String.format(Locale.US, "%.1f", average)
					+ " | active days: " + formatCount((int) activeDays));
		}

		setReadableText(statsWindowDetailsArea, buildComprehensiveStatsText(daily, points, selectedStatsRange));
	}

	private static Map<LocalDate, Integer> parseDailyKillCounts(Map<String, Integer> dailyKillCounts)
	{
		Map<String, Integer> source = dailyKillCounts == null ? Map.of() : dailyKillCounts;
		Map<LocalDate, Integer> parsed = new TreeMap<>();
		for (Map.Entry<String, Integer> entry : source.entrySet())
		{
			if (entry == null || entry.getKey() == null || entry.getValue() == null)
			{
				continue;
			}

			try
			{
				LocalDate day = LocalDate.parse(entry.getKey().trim());
				parsed.put(day, Math.max(0, entry.getValue()));
			}
			catch (Exception ignored)
			{
				// Ignore malformed day keys.
			}
		}

		return parsed;
	}

	private static List<StatsPoint> buildStatsPoints(Map<LocalDate, Integer> dailyKillCounts, StatsRange range)
	{
		Map<LocalDate, Integer> parsed = dailyKillCounts == null ? Map.of() : dailyKillCounts;

		LocalDate today = LocalDate.now();
		LocalDate start;
		if (range.days > 0)
		{
			start = today.minusDays(range.days - 1L);
		}
		else
		{
			LocalDate earliest = parsed.keySet().stream().min(LocalDate::compareTo).orElse(today);
			long span = Math.max(1L, ChronoUnit.DAYS.between(earliest, today) + 1L);
			if (span > 730L)
			{
				earliest = today.minusDays(729L);
			}
			start = earliest;
		}

		List<StatsPoint> points = new ArrayList<>();
		for (LocalDate day = start; !day.isAfter(today); day = day.plusDays(1L))
		{
			points.add(new StatsPoint(day, Math.max(0, parsed.getOrDefault(day, 0))));
		}

		if (points.isEmpty())
		{
			points.add(new StatsPoint(today, 0));
		}
		return points;
	}

	private void buildOverviewTab()
	{
		overviewHeaderPanel.setOpaque(false);
		headingLabel.setFont(HEADER_FONT);
		overviewHeaderPanel.add(headingLabel, BorderLayout.NORTH);
		overallWritingValue.setFont(WRITING_FONT);
		overviewHeaderPanel.add(overallWritingValue, BorderLayout.CENTER);

		overviewProgressPanel.setOpaque(false);
		overviewProgressPanel.setBorder(new EmptyBorder(2, 2, 2, 2));
		campaignProgressLabel.setFont(SUBHEADER_FONT);
		milestoneProgressLabel.setFont(SUBHEADER_FONT);
		milestoneWindowLabel.setFont(PANEL_FONT);
		milestoneEtaLabel.setFont(PANEL_FONT);
		overviewProgressPanel.add(campaignProgressLabel);
		overviewProgressPanel.add(campaignProgressBar);
		overviewProgressPanel.add(milestoneProgressLabel);
		overviewProgressPanel.add(milestoneProgressBar);
		overviewProgressPanel.add(milestoneWindowLabel);
		overviewProgressPanel.add(milestoneEtaLabel);

		overviewTopPanel.setOpaque(false);
		overviewTopPanel.setBorder(new EmptyBorder(6, 8, 6, 8));
		overviewTopPanel.add(overviewHeaderPanel, BorderLayout.NORTH);
		overviewTopPanel.add(overviewProgressPanel, BorderLayout.CENTER);

		overviewTab.add(overviewTopPanel, BorderLayout.NORTH);
		overviewTab.add(wrapArea(overviewArea), BorderLayout.CENTER);
	}

	private void buildLoreBookTab()
	{
		loreBookTab.setOpaque(false);
		loreBookTopPanel.setOpaque(false);
		loreTocPanel.setOpaque(false);
		loreBookHeaderPanel.setOpaque(false);
		loreBookHeaderPanel.setBorder(new EmptyBorder(8, 10, 0, 10));

		loreBookTitleLabel.setFont(HEADER_FONT);
		loreBookSubtitleLabel.setFont(WRITING_FONT);
		loreTocTitleLabel.setFont(SUBHEADER_FONT);
		loreOpenWindowButton.setFont(PANEL_FONT);
		loreOpenWindowButton.setFocusable(false);
		loreOpenWindowButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		loreOpenWindowButton.setToolTipText("Open the full Bronze Count manuscript in a separate window");
		loreOpenWindowButton.addActionListener(event -> openChronicleManuscriptWindow());

		loreBookHeaderPanel.add(loreBookTitleLabel, BorderLayout.NORTH);
		loreBookHeaderPanel.add(loreBookSubtitleLabel, BorderLayout.CENTER);
		loreBookHeaderPanel.add(loreOpenWindowButton, BorderLayout.EAST);
		loreTocPanel.add(loreTocTitleLabel);
		for (JButton button : loreTocButtons.values())
		{
			loreTocPanel.add(button);
		}
		loreTocPanel.addMouseWheelListener(event -> {
			int direction = event.getWheelRotation();
			int delta = Math.max(1, Math.abs(direction)) * loreBookScrollPane.getVerticalScrollBar().getUnitIncrement();
			int signedDelta = direction < 0 ? -delta : delta;
			loreBookScrollPane.getVerticalScrollBar().setValue(
				loreBookScrollPane.getVerticalScrollBar().getValue() + signedDelta);
		});
		loreBookTopPanel.add(loreBookHeaderPanel, BorderLayout.NORTH);
		loreBookTopPanel.add(loreTocPanel, BorderLayout.CENTER);

		loreBookTab.add(loreBookTopPanel, BorderLayout.NORTH);
		buildLoreSubTabs();
		loreBookTab.add(loreSubTabs, BorderLayout.CENTER);
		loreBookScrollPane.getViewport().addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent event)
			{
				applyAdaptiveLoreStyling();
			}
		});
	}

	private void buildLoreSubTabs()
	{
		loreSubTabs.setFocusable(false);
		loreSubTabs.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
		installWarTabUi(loreSubTabs, new Insets(3, 8, 3, 8), new Insets(1, 1, 0, 1));
		loreSubTabs.addChangeListener(e -> {
			applyLoreSubTabSelectionColors();
			applyPendingCanonScroll();
			applyAdaptiveLoreStyling();
		});
		installLoreContextMenu(loreBookArea, this::openChronicleManuscriptWindow);
		loreSubTabs.addTab("Chronicle", loreBookScrollPane);

		for (WarBranding.LoreUnlockEntry entry : loreUnlockEntries)
		{
			JTextArea area = createBookArea();
			installLoreContextMenu(area, () -> openMilestoneManuscriptWindow(entry, area));
			loreUnlockAreas.put(entry.getMilestoneKills(), area);
			loreSubTabs.addTab(formatUnlockTabTitle(entry), wrapArea(area));
		}

		refreshLoreUnlockTabs(0);
		applyAdaptiveLoreStyling();
	}

	private String formatUnlockTabTitle(WarBranding.LoreUnlockEntry entry)
	{
		return String.format(Locale.US, "%,d Kills", entry.getMilestoneKills());
	}

	private void refreshLoreUnlockTabs(int lifetimeKills)
	{
		for (int i = 0; i < loreUnlockEntries.size(); i++)
		{
			WarBranding.LoreUnlockEntry entry = loreUnlockEntries.get(i);
			boolean unlocked = Math.max(0, lifetimeKills) >= entry.getMilestoneKills();
			int tabIndex = i + 1;
			loreSubTabs.setEnabledAt(tabIndex, unlocked);
			loreSubTabs.setToolTipTextAt(tabIndex,
				unlocked
					? "Unlocked: " + entry.getUnlockTitle()
					: "Unlocks at " + String.format(Locale.US, "%,d", entry.getMilestoneKills()) + " kills");

			JTextArea area = loreUnlockAreas.get(entry.getMilestoneKills());
			if (area != null)
			{
				setBookText(area, unlocked ? entry.toLoreText() : buildLockedMilestoneText(entry));
			}
		}

		int selectedIndex = loreSubTabs.getSelectedIndex();
		if (selectedIndex > 0 && !loreSubTabs.isEnabledAt(selectedIndex))
		{
			loreSubTabs.setSelectedIndex(0);
		}
		applyLoreSubTabSelectionColors();
	}

	private static String buildLockedMilestoneText(WarBranding.LoreUnlockEntry entry)
	{
		return "LOCKED\n\n"
			+ "Reach " + String.format(Locale.US, "%,d", entry.getMilestoneKills()) + " goblin kills to unlock this chapter.\n\n"
			+ "Unlock text:\n"
			+ entry.getUnlockTitle();
	}

	private JScrollPane wrapArea(JTextArea area)
	{
		JScrollPane scrollPane = new JScrollPane(area);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		if (BOOK_FONT.equals(area.getFont()))
		{
			scrollPane.setPreferredSize(new Dimension(0, BOOK_VIEWPORT_PREFERRED_HEIGHT));
			scrollPane.setMinimumSize(new Dimension(0, 220));
		}
		int unitIncrement = area == loreBookArea ? 18 : 14;
		int blockIncrement = area == loreBookArea ? 120 : 80;
		scrollPane.getVerticalScrollBar().setUnitIncrement(unitIncrement);
		scrollPane.getVerticalScrollBar().setBlockIncrement(blockIncrement);
		return scrollPane;
	}

	private JProgressBar createProgressBar()
	{
		JProgressBar bar = new JProgressBar(0, 100);
		bar.setStringPainted(true);
		bar.setBorderPainted(true);
		bar.setFont(PANEL_FONT);
		bar.setFocusable(false);
		return bar;
	}

	private JTextArea createReadOnlyArea()
	{
		JTextArea area = new JTextArea();
		area.setEditable(false);
		area.setLineWrap(true);
		// Character wrapping prevents very long tokens from flowing off-screen.
		area.setWrapStyleWord(false);
		area.setFont(PANEL_FONT);
		area.setMargin(new Insets(8, 10, 8, 10));
		area.setOpaque(true);
		return area;
	}

	private JTextArea createBookArea()
	{
		JTextArea area = createReadOnlyArea();
		area.setFont(BOOK_FONT);
		area.setWrapStyleWord(true);
		area.setMargin(new Insets(12, 12, 12, 12));
		if (area.getCaret() instanceof DefaultCaret)
		{
			((DefaultCaret) area.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		}
		return area;
	}

	private void applyAdaptiveLoreStyling()
	{
		int viewportWidth = loreBookScrollPane.getViewport().getWidth();
		if (viewportWidth <= 0)
		{
			viewportWidth = loreSubTabs.getWidth();
		}

		boolean compact = viewportWidth > 0 && viewportWidth <= LORE_COMPACT_WIDTH_THRESHOLD;
		Font bookFont = compact ? BOOK_FONT_COMPACT : BOOK_FONT;
		Insets bookMargin = compact
			? new Insets(8, 8, 10, 8)
			: new Insets(12, 12, 12, 12);
		loreBookArea.setFont(bookFont);
		loreBookArea.setMargin(bookMargin);
		for (JTextArea area : loreUnlockAreas.values())
		{
			area.setFont(bookFont);
			area.setMargin(bookMargin);
		}

		Font tocFont = compact ? TOC_FONT_COMPACT : TOC_FONT;
		for (JButton button : loreTocButtons.values())
		{
			button.setFont(tocFont);
		}
	}

	private Map<String, JButton> createLoreTocButtons()
	{
		Map<String, JButton> buttons = new LinkedHashMap<>();
		for (Map.Entry<String, String> entry : canonSectionHeadings.entrySet())
		{
			buttons.put(entry.getKey(), createLoreTocButton(entry.getKey(), entry.getValue()));
		}
		return Collections.unmodifiableMap(buttons);
	}

	private JButton createLoreTocButton(String chapterId, String title)
	{
		JButton button = new JButton(title);
		button.setFont(TOC_FONT);
		button.setHorizontalAlignment(SwingConstants.LEFT);
		button.setBorder(new EmptyBorder(0, 0, 0, 0));
		button.setContentAreaFilled(false);
		button.setFocusPainted(false);
		button.setOpaque(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.putClientProperty("tocTitle", title);
		button.addActionListener(e -> {
			log.debug("Lore TOC click: chapterId={}, title={}, selectedLoreTab={}",
				chapterId,
				title,
				loreSubTabs.getSelectedIndex());
			scrollCanonToChapter(chapterId);
		});
		button.addMouseWheelListener(event -> {
			int direction = event.getWheelRotation();
			int delta = Math.max(1, Math.abs(direction)) * loreBookScrollPane.getVerticalScrollBar().getUnitIncrement();
			int signedDelta = direction < 0 ? -delta : delta;
			loreBookScrollPane.getVerticalScrollBar().setValue(
				loreBookScrollPane.getVerticalScrollBar().getValue() + signedDelta);
		});
		installLoreContextMenu(button, () -> openChapterManuscriptWindow(chapterId, title));
		return button;
	}

	private void installLoreContextMenu(Component component, Runnable openAction)
	{
		if (component == null || openAction == null)
		{
			return;
		}

		component.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent event)
			{
				showLorePopupIfTriggered(event, openAction);
			}

			@Override
			public void mouseReleased(MouseEvent event)
			{
				showLorePopupIfTriggered(event, openAction);
			}
		});
	}

	private void showLorePopupIfTriggered(MouseEvent event, Runnable openAction)
	{
		if (event == null || openAction == null || !event.isPopupTrigger())
		{
			return;
		}

		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem openItem = new JMenuItem(MANUSCRIPT_MENU_LABEL);
		openItem.setFont(PANEL_FONT);
		openItem.addActionListener(e -> openAction.run());
		popupMenu.add(openItem);
		popupMenu.show(event.getComponent(), event.getX(), event.getY());
		event.consume();
	}

	private void openChronicleManuscriptWindow()
	{
		openLoreManuscriptWindow("THE BRONZE COUNT", "Complete Chronicle", resolveChronicleText());
	}

	private void openMilestoneManuscriptWindow(WarBranding.LoreUnlockEntry entry, JTextArea sourceArea)
	{
		if (entry == null)
		{
			return;
		}

		String chapterTitle = String.format(Locale.US, "%,d Kills", entry.getMilestoneKills());
		String subtitle = chapterTitle + " - " + entry.getTitle();
		String text = sourceArea == null ? "" : sourceArea.getText();
		openLoreManuscriptWindow("THE BRONZE COUNT", subtitle, text);
	}

	private void openChapterManuscriptWindow(String chapterId, String chapterTitle)
	{
		String heading = canonSectionHeadings.get(chapterId);
		String fallbackTitle = heading == null ? "Chronicle" : heading;
		String subtitle = chapterTitle == null || chapterTitle.isBlank() ? fallbackTitle : chapterTitle;
		String chapterText = extractChronicleChapterText(chapterId);
		openLoreManuscriptWindow("THE BRONZE COUNT", subtitle, chapterText);
	}

	private String resolveChronicleText()
	{
		String text = loreBookArea.getText();
		if (text == null || text.isBlank())
		{
			text = buildLoreText();
		}
		return text == null ? "" : text;
	}

	private String extractChronicleChapterText(String chapterId)
	{
		String fullText = resolveChronicleText();
		if (chapterId == null || chapterId.isBlank() || fullText.isBlank())
		{
			return fullText;
		}

		String chapterHeading = canonSectionHeadings.get(chapterId);
		if (chapterHeading == null || chapterHeading.isBlank())
		{
			return fullText;
		}

		int chapterStart = fullText.indexOf(chapterHeading);
		if (chapterStart < 0)
		{
			return fullText;
		}

		List<String> chapterIds = new ArrayList<>(canonSectionHeadings.keySet());
		int chapterIndex = chapterIds.indexOf(chapterId);
		if (chapterIndex < 0 || chapterIndex + 1 >= chapterIds.size())
		{
			return fullText.substring(chapterStart).trim();
		}

		String nextChapterHeading = canonSectionHeadings.get(chapterIds.get(chapterIndex + 1));
		if (nextChapterHeading == null || nextChapterHeading.isBlank())
		{
			return fullText.substring(chapterStart).trim();
		}

		int chapterEnd = fullText.indexOf(nextChapterHeading, chapterStart + chapterHeading.length());
		if (chapterEnd <= chapterStart)
		{
			return fullText.substring(chapterStart).trim();
		}

		return fullText.substring(chapterStart, chapterEnd).trim();
	}

	private void openLoreManuscriptWindow(String title, String subtitle, String text)
	{
		String safeText = text == null ? "" : text.trim();
		if (safeText.isBlank())
		{
			safeText = "No lore text available yet.";
		}

		final String safeTitle = title == null || title.isBlank() ? "THE BRONZE COUNT" : title;
		final String safeSubtitle = subtitle == null ? "" : subtitle;
		final String safeBody = safeText;
		Runnable opener = () -> LoreManuscriptWindow.open(MANUSCRIPT_WINDOW_TITLE, safeTitle, safeSubtitle, safeBody);
		if (SwingUtilities.isEventDispatchThread())
		{
			opener.run();
			return;
		}

		SwingUtilities.invokeLater(opener);
	}

	private static Map<String, String> createCanonSectionHeadings()
	{
		Map<String, String> headings = new LinkedHashMap<>();
		headings.put("i-first-age-of-war", "I. First Age of War");
		headings.put("ii-the-plain-of-mud", "II. The Plain of Mud");
		headings.put("iii-the-long-waiting", "III. The Long Waiting");
		headings.put("iv-the-counter-appears", "IV. The Counter Appears");
		headings.put("v-prophecy-bent-against-the-tribes", "V. Prophecy Bent Against the Tribes");
		headings.put("vi-rites-of-number-and-blood", "VI. Rites of Number and Blood");
		headings.put("vii-the-throne-of-bronze", "VII. The Throne of Bronze");
		headings.put("viii-the-last-question", "VIII. The Last Question");
		return Collections.unmodifiableMap(headings);
	}

	private void scrollCanonToChapter(String chapter)
	{
		log.debug("scrollCanonToChapter start: chapterId={}, selectedLoreTab={}", chapter, loreSubTabs.getSelectedIndex());
		String heading = canonSectionHeadings.get(chapter);
		if (heading == null)
		{
			log.debug("scrollCanonToChapter aborted: no heading for chapterId={}", chapter);
			return;
		}

		String text = loreBookArea.getText();
		int position = text.indexOf(heading);
		if (position < 0)
		{
			log.debug("scrollCanonToChapter aborted: heading not found in lore text, chapterId={}, heading={}", chapter, heading);
			return;
		}

		pendingCanonScrollOffset = position;
		log.debug("scrollCanonToChapter resolved: chapterId={}, heading={}, offset={}, textLength={}",
			chapter,
			heading,
			position,
			text.length());
		if (loreSubTabs.getSelectedIndex() != 0)
		{
			log.debug("scrollCanonToChapter switching lore tab to Chronicle from index={}", loreSubTabs.getSelectedIndex());
			loreSubTabs.setSelectedIndex(0);
		}

		applyPendingCanonScroll();
	}

	private void applyPendingCanonScroll()
	{
		if (pendingCanonScrollOffset < 0 || loreSubTabs.getSelectedIndex() != 0)
		{
			if (pendingCanonScrollOffset >= 0)
			{
				log.debug("applyPendingCanonScroll deferred: pendingOffset={}, selectedLoreTab={}",
					pendingCanonScrollOffset,
					loreSubTabs.getSelectedIndex());
			}
			return;
		}

		int position = pendingCanonScrollOffset;
		pendingCanonScrollOffset = -1;
		log.debug("applyPendingCanonScroll applying: offset={}", position);
		SwingUtilities.invokeLater(() -> scrollLoreToOffset(position));
	}

	private void scrollLoreToOffset(int position)
	{
		int boundedPosition = Math.max(0, Math.min(position, loreBookArea.getDocument().getLength()));
		attemptLoreScrollToOffset(position, boundedPosition, 0);
	}

	private void attemptLoreScrollToOffset(int requestedPosition, int boundedPosition, int attempt)
	{
		Rectangle targetRect = null;
		try
		{
			Rectangle rect = loreBookArea.modelToView2D(boundedPosition).getBounds();
			int targetY = Math.max(0, rect.y - 12);
			targetRect = new Rectangle(0, targetY, 1, Math.max(1, rect.height));
			loreBookArea.scrollRectToVisible(targetRect);
			log.debug("attemptLoreScrollToOffset mapped: attempt={}, requestedOffset={}, boundedOffset={}, rectY={}, targetY={}",
				attempt,
				requestedPosition,
				boundedPosition,
				rect.y,
				targetY);
		}
		catch (Exception ignored)
		{
			log.debug("attemptLoreScrollToOffset mapping failed: attempt={}, requestedOffset={}, boundedOffset={}",
				attempt,
				requestedPosition,
				boundedPosition);
		}

		int maxY = Math.max(0, loreBookScrollPane.getVerticalScrollBar().getMaximum() - loreBookScrollPane.getVerticalScrollBar().getVisibleAmount());
		int currentY = loreBookScrollPane.getVerticalScrollBar().getValue();
		log.debug("attemptLoreScrollToOffset state: attempt={}, maxY={}, currentY={}, viewportExtent={}x{}",
			attempt,
			maxY,
			currentY,
			loreBookScrollPane.getViewport().getExtentSize().width,
			loreBookScrollPane.getViewport().getExtentSize().height);

		if (targetRect != null && maxY > 0)
		{
			int clampedTarget = Math.min(targetRect.y, maxY);
			loreBookScrollPane.getVerticalScrollBar().setValue(clampedTarget);
			log.debug("attemptLoreScrollToOffset applied: attempt={}, clampedTargetY={}, maxY={}",
				attempt,
				clampedTarget,
				maxY);
			return;
		}

		if (attempt < 6)
		{
			int nextAttempt = attempt + 1;
			SwingUtilities.invokeLater(() -> attemptLoreScrollToOffset(requestedPosition, boundedPosition, nextAttempt));
			return;
		}

		int fallbackTarget = computeLoreTargetScrollY(boundedPosition, maxY);
		loreBookScrollPane.getVerticalScrollBar().setValue(fallbackTarget);
		log.debug("attemptLoreScrollToOffset fallback applied: requestedOffset={}, boundedOffset={}, maxY={}, fallbackTarget={}",
			requestedPosition,
			boundedPosition,
			maxY,
			fallbackTarget);
	}

	private int computeLoreTargetScrollY(int position, int maxY)
	{
		if (maxY <= 0)
		{
			return 0;
		}

		try
		{
			Rectangle rect = loreBookArea.modelToView2D(position).getBounds();
			int mappedY = Math.min(Math.max(0, rect.y - 12), maxY);
			log.debug("computeLoreTargetScrollY mapped: offset={}, rectY={}, maxY={}, mappedY={}", position, rect.y, maxY, mappedY);
			return mappedY;
		}
		catch (Exception ignored)
		{
			// Fallback by text offset if precise view mapping is unavailable.
			int length = Math.max(1, loreBookArea.getDocument().getLength());
			double ratio = position / (double) length;
			int fallbackY = (int) Math.round(ratio * maxY);
			log.debug("computeLoreTargetScrollY fallback: offset={}, length={}, ratio={}, maxY={}, fallbackY={}",
				position,
				length,
				ratio,
				maxY,
				fallbackY);
			return fallbackY;
		}
	}

	private void applyCanonTocTheme(Color backgroundColor, Color textColor, Color linkColor, Color borderColor)
	{
		loreTocPanel.setBackground(backgroundColor);
		loreTocPanel.setBorder(new CompoundBorder(
			new MatteBorder(1, 1, 1, 1, borderColor),
			new EmptyBorder(6, 8, 6, 8)));
		loreTocTitleLabel.setForeground(textColor);
		String linkHex = toHex(linkColor);
		for (JButton button : loreTocButtons.values())
		{
			Object title = button.getClientProperty("tocTitle");
			String safeTitle = title == null ? "" : escapeHtml(title.toString());
			button.setText("<html><span style='color:" + linkHex + ";'><u>" + safeTitle + "</u></span></html>");
		}
	}

	private static String toHex(Color color)
	{
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private String buildOverviewText(GoblinKillTrackerConfig config)
	{
		String profileName = plugin.getActiveProfileName() == null
			? WarBranding.overviewProfileNoneLabel()
			: plugin.getActiveProfileName();
		int lifetimeKills = plugin.getLifetimeGoblinKills();
		int sessionRate = plugin.getSessionKillsPerHour();
		boolean showFlavorText = config == null || config.showFlavorText();
		int flavorStride = config == null ? 25 : config.flavorLineStride();
		List<String> dailyStatsLines = buildDailyStatsLines(plugin.getDailyKillCounts());
		List<String> unlockedMilestones = buildUnlockedMilestoneLines(lifetimeKills, plugin.getMilestoneReachedAtMs());

		StringBuilder text = new StringBuilder();
		text.append("=== Campaign Summary ===\n");
		text.append(WarBranding.PLUGIN_NAME).append(" | Track the war ledger").append('\n');
		text.append(WarBranding.overviewOverallWritingLabel())
			.append(WarBranding.overallWriting(lifetimeKills, flavorStride)).append("\n\n");
		text.append(WarBranding.overviewSessionLabel()).append(plugin.getTodayGoblinKills()).append('\n');
		text.append(WarBranding.overviewTripLabel()).append(plugin.getTripGoblinKills()).append('\n');
		text.append(WarBranding.overviewLifetimeLabel()).append(lifetimeKills).append('\n');
		text.append(WarBranding.overviewRateLabel()).append(sessionRate).append('\n');
		text.append(WarBranding.overviewCompletionLabel()).append(WarBranding.completionText(lifetimeKills)).append('\n');
		text.append(WarBranding.overviewRemainingLabel()).append(WarBranding.hostilesRemaining(lifetimeKills)).append('\n');
		text.append(WarBranding.overviewProjectionLabel())
			.append(WarBranding.projectedCompletionSummary(lifetimeKills, sessionRate)).append('\n');
		text.append(WarBranding.overviewProfileLabel()).append(profileName).append('\n');
		text.append(WarBranding.overviewTitleLabel())
			.append(WarBranding.operativeTitle(lifetimeKills));

		text.append("\n\n");
		text.append("=== Daily War Ledger ===\n");
		for (String line : dailyStatsLines)
		{
			text.append(line).append('\n');
		}

		text.append("\n\n");
		text.append("=== Milestone Ledger ===\n");
		text.append(WarBranding.overviewMilestonesLabel()).append('\n');
		if (unlockedMilestones.isEmpty())
		{
			text.append("[ ] ").append(WarBranding.overviewNoMilestonesLabel()).append('\n');
		}
		else
		{
			for (String line : unlockedMilestones)
			{
				text.append(line).append('\n');
			}
		}
		text.append(WarBranding.overviewNextTargetLabel())
			.append(WarBranding.nextMilestoneSummary(lifetimeKills)).append('\n');
		text.append(WarBranding.overviewMilestoneEtaLabel())
			.append(WarBranding.milestoneEtaSummary(lifetimeKills, sessionRate));

		if (showFlavorText)
		{
			text.append("\n");
			text.append(WarBranding.overviewFlavorLabel())
				.append(WarBranding.flavorLine(lifetimeKills, flavorStride));
		}

		return text.toString();
	}

	private static List<String> buildDailyStatsLines(Map<String, Integer> dailyKillCounts)
	{
		if (dailyKillCounts == null || dailyKillCounts.isEmpty())
		{
			return List.of("No daily kill records yet.");
		}

		Map<String, Integer> normalized = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry : dailyKillCounts.entrySet())
		{
			if (entry == null || entry.getKey() == null || entry.getValue() == null)
			{
				continue;
			}

			String dateKey = entry.getKey().trim();
			if (dateKey.isBlank())
			{
				continue;
			}

			normalized.put(dateKey, Math.max(0, entry.getValue()));
		}

		if (normalized.isEmpty())
		{
			return List.of("No daily kill records yet.");
		}

		List<String> lines = new ArrayList<>();
		String todayKey = LocalDate.now().toString();
		int todayKills = Math.max(0, normalized.getOrDefault(todayKey, 0));
		lines.add("Today (" + todayKey + "): " + String.format(Locale.US, "%,d", todayKills));

		Map.Entry<String, Integer> highest = normalized.entrySet().stream()
			.max(Map.Entry.<String, Integer>comparingByValue()
				.thenComparing(Map.Entry.comparingByKey()))
			.orElse(null);
		if (highest != null)
		{
			lines.add("Highest day: " + highest.getKey() + " - " + String.format(Locale.US, "%,d", highest.getValue()));
		}

		lines.add("Recent days:");
		normalized.entrySet().stream()
			.sorted(Map.Entry.<String, Integer>comparingByKey().reversed())
			.limit(7)
			.forEach(entry -> lines.add(entry.getKey() + " - " + String.format(Locale.US, "%,d", entry.getValue())));

		lines.add("Top days:");
		normalized.entrySet().stream()
			.sorted((left, right) -> {
				int byCount = Integer.compare(right.getValue(), left.getValue());
				if (byCount != 0)
				{
					return byCount;
				}
				return right.getKey().compareTo(left.getKey());
			})
			.limit(5)
			.forEach(entry -> lines.add(entry.getKey() + " - " + String.format(Locale.US, "%,d", entry.getValue())));

		return lines;
	}

	private static List<String> buildUnlockedMilestoneLines(int lifetimeKills, Map<Integer, Long> milestoneReachedAtMs)
	{
		int boundedKills = Math.max(0, lifetimeKills);
		Map<Integer, Long> reachedAtMap = milestoneReachedAtMs == null ? Map.of() : milestoneReachedAtMs;
		List<String> lines = new ArrayList<>();

		for (int target : WarBranding.milestoneTargets())
		{
			if (boundedKills < target)
			{
				continue;
			}

			String line = "[x] " + String.format(Locale.US, "%,d", target) + " - " + WarBranding.milestoneTitle(target);
			Long reachedAtMs = reachedAtMap.get(target);
			if (reachedAtMs == null || reachedAtMs <= 0L)
			{
				line += " (hit: unknown)";
			}
			else
			{
				line += " (hit: " + MILESTONE_HIT_FORMATTER.format(Instant.ofEpochMilli(reachedAtMs)) + ")";
			}

			lines.add(line);
		}

		return lines;
	}

	private String buildAreasText()
	{
		Map<String, Integer> areaKills = plugin.getAreaKillCounts();
		if (areaKills.isEmpty())
		{
			return WarBranding.emptyAreasText();
		}

		return areaKills.entrySet().stream()
			.sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
			.map(entry -> entry.getKey() + ": " + entry.getValue())
			.collect(Collectors.joining("\n"));
	}

	private String buildStatsText()
	{
		Map<LocalDate, Integer> daily = parseDailyKillCounts(plugin.getDailyKillCounts());
		List<StatsPoint> defaultWindow = buildStatsPoints(daily, StatsRange.THIRTY_DAYS);

		StringBuilder text = new StringBuilder();
		text.append("Selecting this tab opens the interactive Stats window in a separate panel.\n");
		text.append("Window controls include range, chart mode, and moving average overlays.\n\n");
		text.append(buildComprehensiveStatsText(daily, defaultWindow, StatsRange.THIRTY_DAYS));
		return text.toString();
	}

	private String buildComprehensiveStatsText(Map<LocalDate, Integer> daily, List<StatsPoint> selectedWindow, StatsRange range)
	{
		Map<LocalDate, Integer> safeDaily = daily == null ? Map.of() : daily;
		List<StatsPoint> window = selectedWindow == null ? List.of() : selectedWindow;
		LocalDate today = LocalDate.now();

		int oneHour = Math.max(0, plugin.getSessionKillsPerHour());
		int oneDay = Math.max(0, safeDaily.getOrDefault(today, 0));
		int yesterday = Math.max(0, safeDaily.getOrDefault(today.minusDays(1L), 0));
		int sevenDays = sumRecentDays(safeDaily, today, 7);
		int previousSevenDays = sumRecentDays(safeDaily, today.minusDays(7L), 7);
		int thirtyDays = sumRecentDays(safeDaily, today, 30);
		int previousThirtyDays = sumRecentDays(safeDaily, today.minusDays(30L), 30);
		int ninetyDays = sumRecentDays(safeDaily, today, 90);
		int allTime = Math.max(0, plugin.getLifetimeGoblinKills());
		int session = Math.max(0, plugin.getSessionGoblinKills());
		int trip = Math.max(0, plugin.getTripGoblinKills());

		int windowTotal = window.stream().mapToInt(point -> point.kills).sum();
		int windowPeak = window.stream().mapToInt(point -> point.kills).max().orElse(0);
		long windowActiveDays = window.stream().filter(point -> point.kills > 0).count();
		double windowAvgCalendar = window.isEmpty() ? 0.0D : windowTotal / (double) window.size();
		double windowAvgActive = windowActiveDays == 0 ? 0.0D : windowTotal / (double) windowActiveDays;

		LocalDate firstTrackedDay = safeDaily.keySet().stream().min(LocalDate::compareTo).orElse(today);
		int trackedDays = Math.max(1, (int) ChronoUnit.DAYS.between(firstTrackedDay, today) + 1);
		int activeDaysAll = (int) safeDaily.values().stream().filter(value -> value != null && value > 0).count();
		int killFreeDays = Math.max(0, trackedDays - activeDaysAll);
		double activeRate = trackedDays == 0 ? 0.0D : (activeDaysAll * 100.0D) / trackedDays;
		double allTimeAvgCalendar = trackedDays == 0 ? 0.0D : allTime / (double) trackedDays;
		double allTimeAvgActive = activeDaysAll == 0 ? 0.0D : allTime / (double) activeDaysAll;

		Map.Entry<LocalDate, Integer> bestDay = safeDaily.entrySet().stream()
			.max(Map.Entry.<LocalDate, Integer>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
			.orElse(null);
		Map.Entry<LocalDate, Integer> quietestActiveDay = safeDaily.entrySet().stream()
			.filter(entry -> entry.getValue() != null && entry.getValue() > 0)
			.min(Map.Entry.<LocalDate, Integer>comparingByValue().thenComparing(Map.Entry.comparingByKey()))
			.orElse(null);

		StreakSummary streakSummary = buildStreakSummary(safeDaily, today);
		WeekdaySummary weekdaySummary = buildWeekdaySummary(safeDaily);

		Map<String, Integer> areaKills = plugin.getAreaKillCounts() == null ? Map.of() : plugin.getAreaKillCounts();
		List<Map.Entry<String, Integer>> topAreas = areaKills.entrySet().stream()
			.filter(entry -> entry != null && entry.getKey() != null && entry.getValue() != null)
			.sorted(Map.Entry.<String, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
			.limit(5)
			.collect(Collectors.toList());

		Map<Integer, Long> todayLoot = sanitizeLootTotals(plugin.getTodayLootTotals());
		Map<Integer, Long> lifetimeLoot = sanitizeLootTotals(plugin.getLifetimeLootTotals());
		long todayLootQty = todayLoot.values().stream().mapToLong(Long::longValue).sum();
		long lifetimeLootQty = lifetimeLoot.values().stream().mapToLong(Long::longValue).sum();
		List<Map.Entry<Integer, Long>> topLootItems = lifetimeLoot.entrySet().stream()
			.sorted(Map.Entry.<Integer, Long>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
			.limit(3)
			.collect(Collectors.toList());

		int[] milestoneTargets = WarBranding.milestoneTargets();
		long unlockedMilestones = 0L;
		Integer nextMilestone = null;
		for (int target : milestoneTargets)
		{
			if (target <= allTime)
			{
				unlockedMilestones++;
				continue;
			}

			if (nextMilestone == null)
			{
				nextMilestone = target;
			}
		}
		Map<Integer, Long> milestoneTimes = plugin.getMilestoneReachedAtMs() == null ? Map.of() : plugin.getMilestoneReachedAtMs();
		Map.Entry<Integer, Long> latestMilestone = milestoneTimes.entrySet().stream()
			.filter(entry -> entry != null && entry.getValue() != null && entry.getValue() > 0L)
			.max(Map.Entry.comparingByValue())
			.orElse(null);

		List<GoblinKillRecord> recentKills = plugin.getRecentKills() == null ? List.of() : plugin.getRecentKills();
		Map<String, Integer> recentSourceCounts = new LinkedHashMap<>();
		for (GoblinKillRecord record : recentKills)
		{
			if (record == null)
			{
				continue;
			}

			String source = record.getSource() == null ? "UNKNOWN" : record.getSource().name();
			recentSourceCounts.merge(source, 1, Integer::sum);
		}
		GoblinKillRecord latestRecord = recentKills.stream()
			.filter(Objects::nonNull)
			.max(Comparator.comparing(
				GoblinKillRecord::getTimestamp,
				Comparator.nullsLast(Comparator.naturalOrder())))
			.orElse(null);

		StringBuilder text = new StringBuilder();
		text.append("=== Time Window Stats ===\n");
		text.append("1h (current pace): ").append(formatCount(oneHour)).append('\n');
		text.append("1d (today): ").append(formatCount(oneDay)).append('\n');
		text.append("Yesterday: ").append(formatCount(yesterday)).append('\n');
		text.append("7d: ").append(formatCount(sevenDays)).append(" | delta vs prev 7d: ").append(formatDeltaWithPercent(sevenDays, previousSevenDays)).append('\n');
		text.append("30d: ").append(formatCount(thirtyDays)).append(" | delta vs prev 30d: ").append(formatDeltaWithPercent(thirtyDays, previousThirtyDays)).append('\n');
		text.append("90d: ").append(formatCount(ninetyDays)).append('\n');
		text.append("All-time: ").append(formatCount(allTime)).append("\n\n");

		text.append("=== Active Session ===\n");
		text.append("Session: ").append(formatCount(session)).append('\n');
		text.append("Trip: ").append(formatCount(trip)).append("\n\n");

		text.append("=== Window Analysis (").append(range.label).append(") ===\n");
		text.append("Window total: ").append(formatCount(windowTotal)).append('\n');
		text.append("Peak day: ").append(formatCount(windowPeak)).append('\n');
		text.append("Active days: ").append(formatCount((int) windowActiveDays)).append(" / ").append(formatCount(window.size())).append('\n');
		text.append("Avg/day (calendar): ").append(String.format(Locale.US, "%.1f", windowAvgCalendar)).append('\n');
		text.append("Avg/day (active): ").append(String.format(Locale.US, "%.1f", windowAvgActive)).append("\n\n");

		text.append("=== Campaign Cadence ===\n");
		text.append("Tracked since: ").append(firstTrackedDay).append(" (").append(formatCount(trackedDays)).append(" days)\n");
		text.append("Active days: ").append(formatCount(activeDaysAll)).append(" | Kill-free days: ").append(formatCount(killFreeDays)).append('\n');
		text.append("Activity rate: ").append(formatPercent(activeRate)).append('\n');
		text.append("All-time avg/day (calendar): ").append(String.format(Locale.US, "%.1f", allTimeAvgCalendar)).append('\n');
		text.append("All-time avg/day (active): ").append(String.format(Locale.US, "%.1f", allTimeAvgActive)).append("\n\n");

		text.append("=== Streak Tracker ===\n");
		text.append("Current streak: ").append(formatCount(streakSummary.currentDays)).append(" days\n");
		if (streakSummary.longestDays <= 0)
		{
			text.append("Longest streak: none\n\n");
		}
		else
		{
			text.append("Longest streak: ").append(formatCount(streakSummary.longestDays))
				.append(" days (").append(streakSummary.longestStart)
				.append(" -> ").append(streakSummary.longestEnd).append(")\n\n");
		}

		text.append("=== Daily Highlights ===\n");
		if (bestDay == null)
		{
			text.append("No daily records yet.\n\n");
		}
		else
		{
			text.append("Best day: ").append(bestDay.getKey()).append(" - ").append(formatCount(bestDay.getValue())).append('\n');
			if (quietestActiveDay != null)
			{
				text.append("Quietest active day: ").append(quietestActiveDay.getKey()).append(" - ").append(formatCount(quietestActiveDay.getValue())).append('\n');
			}
			text.append("Top days:\n");
			safeDaily.entrySet().stream()
				.sorted(Map.Entry.<LocalDate, Integer>comparingByValue().reversed().thenComparing(Map.Entry.comparingByKey()))
				.limit(5)
				.forEach(entry -> text.append(entry.getKey()).append(" - ").append(formatCount(entry.getValue())).append('\n'));
			text.append('\n');
		}

		text.append("=== Weekday Pattern ===\n");
		if (weekdaySummary.activeDayTotal <= 0)
		{
			text.append("No weekday pattern available yet.\n\n");
		}
		else
		{
			text.append("Best weekday: ").append(weekdaySummary.bestLabel).append(" (avg ")
				.append(String.format(Locale.US, "%.1f", weekdaySummary.bestAverage)).append(")\n");
			for (WeekdaySnapshot snapshot : weekdaySummary.snapshots)
			{
				text.append(snapshot.label)
					.append(": avg ").append(String.format(Locale.US, "%.1f", snapshot.average))
					.append(" | total ").append(formatCount(snapshot.totalKills))
					.append(" | active ").append(formatCount(snapshot.activeDays))
					.append('\n');
			}
			text.append('\n');
		}

		text.append("=== Frontline Breakdown ===\n");
		if (topAreas.isEmpty())
		{
			text.append("No frontline area data yet.\n\n");
		}
		else
		{
			for (Map.Entry<String, Integer> entry : topAreas)
			{
				text.append(entry.getKey()).append(": ").append(formatCount(entry.getValue())).append('\n');
			}
			text.append('\n');
		}

		text.append("=== Spoils Breakdown ===\n");
		text.append("Today: ").append(formatCount(todayLoot.size())).append(" item types, qty ").append(formatCount((int) Math.min(Integer.MAX_VALUE, todayLootQty))).append('\n');
		text.append("All-time: ").append(formatCount(lifetimeLoot.size())).append(" item types, qty ").append(formatCount((int) Math.min(Integer.MAX_VALUE, lifetimeLootQty))).append('\n');
		if (topLootItems.isEmpty())
		{
			text.append("No loot records yet.\n\n");
		}
		else
		{
			text.append("Top loot:\n");
			for (Map.Entry<Integer, Long> entry : topLootItems)
			{
				text.append(plugin.getItemName(entry.getKey()))
					.append(" (").append(entry.getKey()).append("): ")
					.append(formatCount((int) Math.min(Integer.MAX_VALUE, entry.getValue())))
					.append('\n');
			}
			text.append('\n');
		}

		text.append("=== Milestone Intelligence ===\n");
		text.append("Unlocked: ").append(formatCount((int) unlockedMilestones)).append(" / ").append(formatCount(milestoneTargets.length)).append('\n');
		if (nextMilestone == null)
		{
			text.append("Next milestone: complete (all prophecy marks reached)\n");
		}
		else
		{
			text.append("Next milestone: ").append(formatCount(nextMilestone)).append(" - ")
				.append(WarBranding.milestoneTitle(nextMilestone)).append('\n');
			text.append("Kills remaining: ").append(formatCount(Math.max(0, nextMilestone - allTime))).append('\n');
		}
		if (latestMilestone != null)
		{
			text.append("Last milestone hit: ").append(formatCount(latestMilestone.getKey()))
				.append(" at ").append(MILESTONE_HIT_FORMATTER.format(Instant.ofEpochMilli(latestMilestone.getValue()))).append('\n');
		}
		else
		{
			text.append("Last milestone hit: none recorded\n");
		}
		text.append('\n');

		text.append("=== Recent Feed Snapshot ===\n");
		text.append("Buffered records: ").append(formatCount(recentKills.size())).append('\n');
		if (latestRecord == null || latestRecord.getTimestamp() == null)
		{
			text.append("Latest record: unavailable\n");
		}
		else
		{
			text.append("Latest record: ").append(TIME_FORMATTER.format(latestRecord.getTimestamp()))
				.append(" | ").append(latestRecord.getAreaName())
				.append(" | ").append(latestRecord.getSource() == null ? "UNKNOWN" : latestRecord.getSource().name())
				.append('\n');
		}
		if (recentSourceCounts.isEmpty())
		{
			text.append("Source split: none\n");
		}
		else
		{
			text.append("Source split: ");
			boolean first = true;
			for (Map.Entry<String, Integer> entry : recentSourceCounts.entrySet())
			{
				if (!first)
				{
					text.append(", ");
				}
				text.append(entry.getKey()).append(' ').append(formatCount(entry.getValue()));
				first = false;
			}
			text.append('\n');
		}

		return text.toString();
	}

	private static StreakSummary buildStreakSummary(Map<LocalDate, Integer> dailyKills, LocalDate today)
	{
		Map<LocalDate, Integer> source = dailyKills == null ? Map.of() : dailyKills;
		if (source.isEmpty())
		{
			return new StreakSummary(0, 0, null, null);
		}

		LocalDate earliest = source.keySet().stream().min(LocalDate::compareTo).orElse(today);
		int currentStreak = 0;
		for (LocalDate day = today; !day.isBefore(earliest); day = day.minusDays(1L))
		{
			if (Math.max(0, source.getOrDefault(day, 0)) <= 0)
			{
				break;
			}
			currentStreak++;
		}

		int longestStreak = 0;
		int activeRun = 0;
		LocalDate activeRunStart = null;
		LocalDate longestStart = null;
		LocalDate longestEnd = null;
		for (LocalDate day = earliest; !day.isAfter(today); day = day.plusDays(1L))
		{
			if (Math.max(0, source.getOrDefault(day, 0)) > 0)
			{
				if (activeRun == 0)
				{
					activeRunStart = day;
				}

				activeRun++;
				if (activeRun > longestStreak)
				{
					longestStreak = activeRun;
					longestStart = activeRunStart;
					longestEnd = day;
				}
			}
			else
			{
				activeRun = 0;
				activeRunStart = null;
			}
		}

		return new StreakSummary(currentStreak, longestStreak, longestStart, longestEnd);
	}

	private static WeekdaySummary buildWeekdaySummary(Map<LocalDate, Integer> dailyKills)
	{
		Map<DayOfWeek, Integer> totals = new LinkedHashMap<>();
		Map<DayOfWeek, Integer> activeDays = new LinkedHashMap<>();
		for (DayOfWeek dayOfWeek : DayOfWeek.values())
		{
			totals.put(dayOfWeek, 0);
			activeDays.put(dayOfWeek, 0);
		}

		if (dailyKills != null)
		{
			for (Map.Entry<LocalDate, Integer> entry : dailyKills.entrySet())
			{
				if (entry == null || entry.getKey() == null || entry.getValue() == null)
				{
					continue;
				}

				int kills = Math.max(0, entry.getValue());
				DayOfWeek dayOfWeek = entry.getKey().getDayOfWeek();
				totals.put(dayOfWeek, totals.get(dayOfWeek) + kills);
				if (kills > 0)
				{
					activeDays.put(dayOfWeek, activeDays.get(dayOfWeek) + 1);
				}
			}
		}

		List<WeekdaySnapshot> snapshots = new ArrayList<>();
		for (DayOfWeek dayOfWeek : DayOfWeek.values())
		{
			int total = totals.get(dayOfWeek);
			int active = activeDays.get(dayOfWeek);
			double average = active <= 0 ? 0.0D : total / (double) active;
			snapshots.add(new WeekdaySnapshot(dayOfWeekLabel(dayOfWeek), total, active, average));
		}

		WeekdaySnapshot best = snapshots.stream()
			.filter(snapshot -> snapshot.activeDays > 0)
			.max(Comparator.comparingDouble((WeekdaySnapshot snapshot) -> snapshot.average)
				.thenComparing(snapshot -> snapshot.totalKills))
			.orElse(null);

		int activeDayTotal = activeDays.values().stream().mapToInt(Integer::intValue).sum();
		return new WeekdaySummary(snapshots, activeDayTotal, best == null ? "None" : best.label, best == null ? 0.0D : best.average);
	}

	private static String dayOfWeekLabel(DayOfWeek dayOfWeek)
	{
		switch (dayOfWeek)
		{
			case MONDAY:
				return "Mon";
			case TUESDAY:
				return "Tue";
			case WEDNESDAY:
				return "Wed";
			case THURSDAY:
				return "Thu";
			case FRIDAY:
				return "Fri";
			case SATURDAY:
				return "Sat";
			case SUNDAY:
				return "Sun";
			default:
				return dayOfWeek == null ? "?" : dayOfWeek.name();
		}
	}

	private static int sumRecentDays(Map<LocalDate, Integer> dailyKillCounts, LocalDate endDayInclusive, int dayCount)
	{
		int total = 0;
		for (int i = 0; i < Math.max(0, dayCount); i++)
		{
			LocalDate day = endDayInclusive.minusDays(i);
			total += Math.max(0, dailyKillCounts.getOrDefault(day, 0));
		}
		return total;
	}

	private static String formatDeltaWithPercent(int current, int previous)
	{
		int delta = current - previous;
		String signedDelta = (delta >= 0 ? "+" : "-") + formatCount(Math.abs(delta));
		if (previous <= 0)
		{
			return signedDelta + " (n/a)";
		}

		double percentage = (delta / (double) previous) * 100.0D;
		String signedPercent = (percentage >= 0 ? "+" : "-") + String.format(Locale.US, "%.1f", Math.abs(percentage)) + "%";
		return signedDelta + " (" + signedPercent + ")";
	}

	private static String formatPercent(double value)
	{
		return String.format(Locale.US, "%.1f%%", Math.max(0.0D, value));
	}

	private static String formatCount(int count)
	{
		return String.format(Locale.US, "%,d", Math.max(0, count));
	}

	private enum StatsRenderMode
	{
		BARS("Bars"),
		TREND("Trend"),
		BARS_AND_TREND("Combo");

		private final String label;

		StatsRenderMode(String label)
		{
			this.label = label;
		}

		private boolean showBars()
		{
			return this == BARS || this == BARS_AND_TREND;
		}

		private boolean showTrend()
		{
			return this == TREND || this == BARS_AND_TREND;
		}
	}

	private static final class StreakSummary
	{
		private final int currentDays;
		private final int longestDays;
		private final LocalDate longestStart;
		private final LocalDate longestEnd;

		private StreakSummary(int currentDays, int longestDays, LocalDate longestStart, LocalDate longestEnd)
		{
			this.currentDays = currentDays;
			this.longestDays = longestDays;
			this.longestStart = longestStart;
			this.longestEnd = longestEnd;
		}
	}

	private static final class WeekdaySnapshot
	{
		private final String label;
		private final int totalKills;
		private final int activeDays;
		private final double average;

		private WeekdaySnapshot(String label, int totalKills, int activeDays, double average)
		{
			this.label = label;
			this.totalKills = totalKills;
			this.activeDays = activeDays;
			this.average = average;
		}
	}

	private static final class WeekdaySummary
	{
		private final List<WeekdaySnapshot> snapshots;
		private final int activeDayTotal;
		private final String bestLabel;
		private final double bestAverage;

		private WeekdaySummary(List<WeekdaySnapshot> snapshots, int activeDayTotal, String bestLabel, double bestAverage)
		{
			this.snapshots = snapshots;
			this.activeDayTotal = activeDayTotal;
			this.bestLabel = bestLabel;
			this.bestAverage = bestAverage;
		}
	}

	private enum StatsRange
	{
		ONE_DAY("1D", 1),
		SEVEN_DAYS("7D", 7),
		THIRTY_DAYS("30D", 30),
		NINETY_DAYS("90D", 90),
		ALL_TIME("ALL", 0);

		private final String label;
		private final int days;

		StatsRange(String label, int days)
		{
			this.label = label;
			this.days = days;
		}
	}

	private static final class StatsPoint
	{
		private final LocalDate day;
		private final int kills;

		private StatsPoint(LocalDate day, int kills)
		{
			this.day = day;
			this.kills = kills;
		}
	}

	private static final class StatsGraphPanel extends JPanel
	{
		private List<StatsPoint> points = List.of();
		private StatsRange range = StatsRange.THIRTY_DAYS;
		private StatsRenderMode renderMode = StatsRenderMode.BARS_AND_TREND;
		private boolean showMovingAverage = true;

		private StatsGraphPanel()
		{
			ToolTipManager.sharedInstance().registerComponent(this);
		}

		private void setPoints(List<StatsPoint> points, StatsRange range, StatsRenderMode renderMode, boolean showMovingAverage)
		{
			this.points = points == null ? List.of() : points;
			this.range = range == null ? StatsRange.THIRTY_DAYS : range;
			this.renderMode = renderMode == null ? StatsRenderMode.BARS_AND_TREND : renderMode;
			this.showMovingAverage = showMovingAverage;
			repaint();
		}

		@Override
		public String getToolTipText(MouseEvent event)
		{
			if (points.isEmpty())
			{
				return null;
			}

			int width = getWidth();
			int left = 56;
			int right = 20;
			int plotWidth = Math.max(1, width - left - right);
			if (event.getX() < left || event.getX() > left + plotWidth)
			{
				return null;
			}

			int index = Math.max(0, Math.min(points.size() - 1, (int) ((event.getX() - left) / (double) plotWidth * points.size())));
			StatsPoint point = points.get(index);
			if (showMovingAverage)
			{
				double average = movingAverageAt(index, 7);
				return point.day + " - " + String.format(Locale.US, "%,d", point.kills)
					+ " kills (7D avg " + String.format(Locale.US, "%.1f", average) + ")";
			}

			return point.day + " - " + String.format(Locale.US, "%,d", point.kills) + " kills";
		}

		@Override
		protected void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int width = getWidth();
			int height = getHeight();
			int left = 56;
			int right = 20;
			int top = 18;
			int bottom = 42;
			int plotWidth = Math.max(1, width - left - right);
			int plotHeight = Math.max(1, height - top - bottom);

			g.setColor(new Color(36, 34, 30));
			g.fillRect(left, top, plotWidth, plotHeight);
			g.setColor(new Color(86, 80, 70));
			g.drawRect(left, top, plotWidth, plotHeight);

			if (points.isEmpty())
			{
				g.setColor(new Color(220, 214, 198));
				g.drawString("No data", left + 8, top + 18);
				g.dispose();
				return;
			}

			int max = Math.max(1, points.stream().mapToInt(point -> point.kills).max().orElse(1));
			for (int step = 0; step <= 4; step++)
			{
				int y = top + (int) Math.round(plotHeight * (step / 4.0D));
				g.setColor(new Color(64, 60, 52));
				g.drawLine(left, y, left + plotWidth, y);
				int value = (int) Math.round(max * (1.0D - (step / 4.0D)));
				g.setColor(new Color(198, 190, 170));
				g.drawString(String.format(Locale.US, "%,d", value), 8, y + 4);
			}

			if (renderMode.showBars())
			{
				for (int i = 0; i < points.size(); i++)
				{
					StatsPoint point = points.get(i);
					int x1 = left + (int) Math.round((i / (double) points.size()) * plotWidth);
					int x2 = left + (int) Math.round(((i + 1) / (double) points.size()) * plotWidth);
					int barWidth = Math.max(1, x2 - x1 - 1);
					int barHeight = (int) Math.round((point.kills / (double) max) * plotHeight);
					int y = top + plotHeight - barHeight;
					g.setColor(new Color(181, 132, 52));
					g.fillRect(x1, y, barWidth, barHeight);
				}
			}

			if (renderMode.showTrend() && points.size() >= 2)
			{
				g.setColor(new Color(238, 201, 125));
				g.setStroke(new BasicStroke(2.0f));
				for (int i = 1; i < points.size(); i++)
				{
					int previousX = left + (int) Math.round(((i - 1 + 0.5D) / points.size()) * plotWidth);
					int previousY = top + plotHeight - (int) Math.round((points.get(i - 1).kills / (double) max) * plotHeight);
					int currentX = left + (int) Math.round(((i + 0.5D) / points.size()) * plotWidth);
					int currentY = top + plotHeight - (int) Math.round((points.get(i).kills / (double) max) * plotHeight);
					g.drawLine(previousX, previousY, currentX, currentY);
				}
			}

			if (showMovingAverage && points.size() >= 2)
			{
				g.setColor(new Color(110, 192, 170));
				g.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[]{6.0f, 6.0f}, 0.0f));
				for (int i = 1; i < points.size(); i++)
				{
					double previousAverage = movingAverageAt(i - 1, 7);
					double currentAverage = movingAverageAt(i, 7);
					int previousX = left + (int) Math.round(((i - 1 + 0.5D) / points.size()) * plotWidth);
					int previousY = top + plotHeight - (int) Math.round((Math.max(0.0D, previousAverage) / max) * plotHeight);
					int currentX = left + (int) Math.round(((i + 0.5D) / points.size()) * plotWidth);
					int currentY = top + plotHeight - (int) Math.round((Math.max(0.0D, currentAverage) / max) * plotHeight);
					g.drawLine(previousX, previousY, currentX, currentY);
				}
			}

			g.setColor(new Color(220, 214, 198));
			int labelCount = range == StatsRange.ONE_DAY ? 1 : Math.min(6, points.size());
			for (int i = 0; i < labelCount; i++)
			{
				int index = labelCount == 1 ? points.size() - 1 : (int) Math.round(i * (points.size() - 1) / (double) (labelCount - 1));
				StatsPoint point = points.get(index);
				String label = DAY_FORMATTER.format(point.day);
				int x = left + (int) Math.round((index / (double) Math.max(1, points.size() - 1)) * plotWidth);
				g.drawString(label, Math.max(left, x - 16), top + plotHeight + 18);
			}

			StatsPoint latest = points.get(points.size() - 1);
			int latestX = left + (int) Math.round((((points.size() - 1) + 0.5D) / points.size()) * plotWidth);
			int latestY = top + plotHeight - (int) Math.round((latest.kills / (double) max) * plotHeight);
			g.setColor(new Color(244, 228, 178));
			g.fillOval(latestX - 3, latestY - 3, 6, 6);

			g.dispose();
		}

		private double movingAverageAt(int index, int windowSize)
		{
			if (points.isEmpty())
			{
				return 0.0D;
			}

			int window = Math.max(1, windowSize);
			int start = Math.max(0, index - window + 1);
			int count = 0;
			int total = 0;
			for (int i = start; i <= index && i < points.size(); i++)
			{
				total += points.get(i).kills;
				count++;
			}

			return count == 0 ? 0.0D : total / (double) count;
		}
	}

	private String buildLootText()
	{
		Map<Integer, Long> todayLootTotals = sanitizeLootTotals(plugin.getTodayLootTotals());
		Map<Integer, Long> overallLootTotals = sanitizeLootTotals(plugin.getLifetimeLootTotals());

		if (todayLootTotals.isEmpty() && overallLootTotals.isEmpty())
		{
			Map<Integer, Long> fallbackTotals = sanitizeLootTotals(plugin.getLootTotals());
			overallLootTotals = fallbackTotals;
		}

		if (todayLootTotals.isEmpty() && overallLootTotals.isEmpty())
		{
			return WarBranding.emptyLootText();
		}

		StringBuilder text = new StringBuilder();
		text.append("=== Today's Loot ===\n");
		text.append(formatLootLines(todayLootTotals));
		text.append("\n\n=== Overall Loot ===\n");
		text.append(formatLootLines(overallLootTotals));
		return text.toString();
	}

	private String formatLootLines(Map<Integer, Long> lootTotals)
	{
		if (lootTotals.isEmpty())
		{
			return "No loot recorded.";
		}

		return lootTotals.entrySet().stream()
			.sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
			.map(entry -> plugin.getItemName(entry.getKey()) + " (" + entry.getKey() + "): " + entry.getValue())
			.collect(Collectors.joining("\n"));
	}

	private static Map<Integer, Long> sanitizeLootTotals(Map<Integer, Long> lootTotals)
	{
		if (lootTotals == null || lootTotals.isEmpty())
		{
			return Map.of();
		}

		return lootTotals.entrySet().stream()
			.filter(entry -> entry != null && entry.getKey() != null && entry.getValue() != null && entry.getValue() > 0)
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
	}

	private String buildHistoryText()
	{
		List<GoblinKillRecord> recentKills = plugin.getRecentKills();
		if (recentKills == null || recentKills.isEmpty())
		{
			return WarBranding.emptyHistoryText();
		}

		String historyText = recentKills.stream()
			.filter(Objects::nonNull)
			.sorted(Comparator.comparing(
				GoblinKillRecord::getTimestamp,
				Comparator.nullsLast(Comparator.reverseOrder())))
			.map(this::formatHistoryLine)
			.collect(Collectors.joining("\n"));

		return historyText.isBlank() ? WarBranding.emptyHistoryText() : historyText;
	}

	private String buildLoreText()
	{
		return WarBranding.bronzeCountCanonBookText();
	}

	private void applyTabLabels()
	{
		tabs.setTitleAt(0, WarBranding.tabCampaignLabel());
		tabs.setTitleAt(1, WarBranding.tabStatsLabel());
		tabs.setTitleAt(2, WarBranding.tabFrontsLabel());
		tabs.setTitleAt(3, WarBranding.tabSpoilsLabel());
		tabs.setTitleAt(4, WarBranding.tabChronicleLabel());
		tabs.setTitleAt(5, WarBranding.tabLoreReaderLabel());
	}

	private void applyTheme(GoblinKillTrackerConfig config)
	{
		WarPalette palette = WarPalette.forTheme(config == null ? null : config.visualTheme());
		Color base = opaque(palette.getOverlayBackground());
		Color panelBackground = blend(base, Color.BLACK, 0.08D);
		Color topBackground = blend(base, Color.WHITE, 0.06D);
		Color areaBackground = blend(base, Color.BLACK, 0.16D);
		Color headingColor = ensureContrast(opaque(palette.getOverlayHeadingColor()), topBackground, 3.0D);
		Color textColor = ensureContrast(opaque(palette.getIconText()), areaBackground, 4.5D);
		Color progressBackground = blend(base, Color.BLACK, 0.28D);
		Color progressForeground = ensureContrast(
			blend(opaque(palette.getIconBackground()), headingColor, 0.30D),
			progressBackground,
			2.0D);
		Color borderColor = blend(headingColor, panelBackground, 0.55D);
		tabUnselectedBackground = blend(panelBackground, Color.BLACK, 0.06D);
		tabSelectedBackground = blend(panelBackground, Color.WHITE, 0.18D);
		tabUnselectedForeground = ensureContrast(blend(headingColor, panelBackground, 0.35D), tabUnselectedBackground, 4.0D);
		tabSelectedForeground = ensureContrast(textColor, tabSelectedBackground, 4.5D);

		setBackground(panelBackground);
		tabs.setBackground(tabUnselectedBackground);
		tabs.setForeground(tabUnselectedForeground);
		tabs.setFont(TAB_FONT);
		applyTabSelectionColors();
		loreSubTabs.setBackground(tabUnselectedBackground);
		loreSubTabs.setForeground(tabUnselectedForeground);
		loreSubTabs.setFont(TAB_FONT);
		applyLoreSubTabSelectionColors();

		overviewTab.setBackground(panelBackground);
		overviewTopPanel.setBackground(topBackground);
		overviewHeaderPanel.setBackground(topBackground);
		overviewProgressPanel.setBackground(topBackground);
		loreBookTab.setBackground(panelBackground);
		loreBookTopPanel.setBackground(topBackground);
		loreBookHeaderPanel.setBackground(topBackground);
		overviewTopPanel.setBorder(new CompoundBorder(
			new MatteBorder(1, 1, 1, 1, borderColor),
			new EmptyBorder(6, 8, 6, 8)));
		loreBookTopPanel.setBorder(new CompoundBorder(
			new MatteBorder(1, 1, 1, 1, borderColor),
			new EmptyBorder(8, 10, 8, 10)));
		loreBookHeaderPanel.setBorder(new CompoundBorder(
			new MatteBorder(0, 0, 1, 0, blend(borderColor, panelBackground, 0.25D)),
			new EmptyBorder(0, 0, 8, 0)));

		headingLabel.setForeground(headingColor);
		loreBookTitleLabel.setForeground(headingColor);
		loreBookSubtitleLabel.setForeground(textColor);
		overallWritingValue.setForeground(textColor);
		campaignProgressLabel.setForeground(headingColor);
		milestoneProgressLabel.setForeground(headingColor);
		milestoneWindowLabel.setForeground(textColor);
		milestoneEtaLabel.setForeground(textColor);
		loreOpenWindowButton.setForeground(tabSelectedForeground);
		loreOpenWindowButton.setBackground(tabSelectedBackground);
		loreOpenWindowButton.setBorder(new CompoundBorder(
			new MatteBorder(1, 1, 1, 1, borderColor),
			new EmptyBorder(3, 8, 3, 8)));
		loreOpenWindowButton.setOpaque(true);
		applyCanonTocTheme(topBackground, textColor, headingColor, borderColor);

		styleProgressBar(campaignProgressBar, progressBackground, progressForeground, borderColor);
		styleProgressBar(milestoneProgressBar, progressBackground, progressForeground, borderColor);
		styleArea(overviewArea, areaBackground, textColor);
		styleArea(statsArea, areaBackground, textColor);
		styleArea(areasArea, areaBackground, textColor);
		styleArea(lootArea, areaBackground, textColor);
		styleArea(historyArea, areaBackground, textColor);
		styleArea(loreBookArea, areaBackground, textColor);
		for (JTextArea area : loreUnlockAreas.values())
		{
			styleArea(area, areaBackground, textColor);
		}
	}

	private void styleProgressBar(JProgressBar bar, Color background, Color foreground, Color borderColor)
	{
		bar.setBackground(background);
		bar.setForeground(foreground);
		bar.setFont(PANEL_FONT);
		bar.setStringPainted(true);
		bar.setBorderPainted(true);
		bar.setFocusable(false);
		bar.setOpaque(true);
		bar.setBorder(new CompoundBorder(
			new MatteBorder(1, 1, 1, 1, borderColor),
			new EmptyBorder(1, 2, 1, 2)));
	}

	private void styleArea(JTextArea area, Color background, Color foreground)
	{
		area.setBackground(background);
		area.setForeground(foreground);
		area.setCaretColor(foreground);
	}

	private static Color opaque(Color color)
	{
		return new Color(color.getRed(), color.getGreen(), color.getBlue());
	}

	private static Color shiftTone(Color color, int amount)
	{
		return new Color(
			clamp(color.getRed() + amount),
			clamp(color.getGreen() + amount),
			clamp(color.getBlue() + amount));
	}

	private static Color blend(Color from, Color to, double amount)
	{
		double t = Math.max(0.0D, Math.min(1.0D, amount));
		return new Color(
			clamp((int) Math.round(from.getRed() + (to.getRed() - from.getRed()) * t)),
			clamp((int) Math.round(from.getGreen() + (to.getGreen() - from.getGreen()) * t)),
			clamp((int) Math.round(from.getBlue() + (to.getBlue() - from.getBlue()) * t)));
	}

	private static Color ensureContrast(Color preferred, Color background, double minRatio)
	{
		double preferredRatio = contrastRatio(preferred, background);
		if (preferredRatio >= minRatio)
		{
			return preferred;
		}

		Color light = new Color(245, 245, 245);
		Color dark = new Color(24, 24, 24);
		double lightRatio = contrastRatio(light, background);
		double darkRatio = contrastRatio(dark, background);
		if (lightRatio >= darkRatio)
		{
			return lightRatio >= minRatio ? light : preferred;
		}

		return darkRatio >= minRatio ? dark : preferred;
	}

	private static double contrastRatio(Color a, Color b)
	{
		double l1 = relativeLuminance(a);
		double l2 = relativeLuminance(b);
		double lighter = Math.max(l1, l2);
		double darker = Math.min(l1, l2);
		return (lighter + 0.05D) / (darker + 0.05D);
	}

	private static double relativeLuminance(Color color)
	{
		double r = toLinear(color.getRed() / 255.0D);
		double g = toLinear(color.getGreen() / 255.0D);
		double b = toLinear(color.getBlue() / 255.0D);
		return 0.2126D * r + 0.7152D * g + 0.0722D * b;
	}

	private static double toLinear(double channel)
	{
		return channel <= 0.04045D ? channel / 12.92D : Math.pow((channel + 0.055D) / 1.055D, 2.4D);
	}

	private static int clamp(int value)
	{
		return Math.max(0, Math.min(255, value));
	}

	private void applyTabSelectionColors()
	{
		int selected = tabs.getSelectedIndex();
		for (int i = 0; i < tabs.getTabCount(); i++)
		{
			boolean isSelected = i == selected;
			tabs.setBackgroundAt(i, isSelected ? tabSelectedBackground : tabUnselectedBackground);
			tabs.setForegroundAt(i, isSelected ? tabSelectedForeground : tabUnselectedForeground);
		}
		tabs.repaint();
	}

	private void applyLoreSubTabSelectionColors()
	{
		int selected = loreSubTabs.getSelectedIndex();
		for (int i = 0; i < loreSubTabs.getTabCount(); i++)
		{
			boolean isSelected = i == selected;
			boolean isEnabled = loreSubTabs.isEnabledAt(i);
			Color background = isSelected ? tabSelectedBackground : tabUnselectedBackground;
			Color foreground = isEnabled
				? (isSelected ? tabSelectedForeground : tabUnselectedForeground)
				: blend(tabUnselectedForeground, tabUnselectedBackground, 0.35D);
			loreSubTabs.setBackgroundAt(i, background);
			loreSubTabs.setForegroundAt(i, foreground);
		}
		loreSubTabs.repaint();
	}

	private void installTabUi()
	{
		installWarTabUi(tabs, new Insets(4, 10, 4, 10), new Insets(2, 2, 0, 2));
	}

	private void installWarTabUi(JTabbedPane tabPane, Insets tabPadding, Insets areaPadding)
	{
		tabPane.setFocusable(false);
		tabPane.setUI(new BasicTabbedPaneUI()
		{
			@Override
			protected void installDefaults()
			{
				super.installDefaults();
				// Keep tab geometry stable when selection changes.
				selectedTabPadInsets = new Insets(0, 0, 0, 0);
				tabInsets = tabPadding;
				tabAreaInsets = areaPadding;
				contentBorderInsets = new Insets(1, 1, 1, 1);
			}

			@Override
			protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)
			{
				g.setColor(isSelected ? tabSelectedBackground : tabUnselectedBackground);
				g.fillRect(x, y, w, h);
			}

			@Override
			protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, int x, int y, int w, int h, boolean isSelected)
			{
				g.setColor(blend(tabSelectedForeground, tabUnselectedBackground, 0.45D));
				g.drawRect(x, y, w - 1, h - 1);
			}

			@Override
			protected int getTabLabelShiftX(int tabPlacement, int tabIndex, boolean isSelected)
			{
				return 0;
			}

			@Override
			protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected)
			{
				return 0;
			}

			@Override
			protected boolean shouldRotateTabRuns(int tabPlacement)
			{
				// Keep visual tab order stable when selecting tabs.
				return false;
			}

			@Override
			protected void paintFocusIndicator(
				Graphics g,
				int tabPlacement,
				Rectangle[] rects,
				int tabIndex,
				Rectangle iconRect,
				Rectangle textRect,
				boolean isSelected)
			{
				// Intentionally disabled to avoid default blue focus highlight.
			}

			@Override
			protected void paintText(
				Graphics g,
				int tabPlacement,
				Font font,
				FontMetrics metrics,
				int tabIndex,
				String title,
				Rectangle textRect,
				boolean isSelected)
			{
				g.setColor(resolveTabTextColor(tabPane, tabIndex, isSelected));
				View v = getTextViewForTab(tabIndex);
				if (v != null)
				{
					v.paint(g, textRect);
					return;
				}

				int y = textRect.y + metrics.getAscent();
				g.setFont(font);
				g.drawString(title, textRect.x, y);
			}
		});
	}

	private Color resolveTabTextColor(JTabbedPane tabPane, int tabIndex, boolean isSelected)
	{
		if (tabPane == loreSubTabs && !loreSubTabs.isEnabledAt(tabIndex))
		{
			return blend(tabUnselectedForeground, tabUnselectedBackground, 0.35D);
		}

		return isSelected ? tabSelectedForeground : tabUnselectedForeground;
	}

	private String formatHistoryLine(GoblinKillRecord record)
	{
		String timestamp = record.getTimestamp() == null ? "--:--:--" : TIME_FORMATTER.format(record.getTimestamp());
		String area = record.getAreaName() == null || record.getAreaName().isBlank() ? "Unknown" : record.getAreaName();
		String source = record.getSource() == null ? "UNKNOWN" : record.getSource().name();
		return timestamp
			+ " | " + area
			+ " | " + source
			+ " | loot items: " + Math.max(0, record.getItemCount());
	}

	private static String wrapLabelText(String text)
	{
		return wrapLabelText(text, false);
	}

	private static String wrapLabelText(String text, boolean bold)
	{
		String safeText = escapeHtml(softWrapText(text, LABEL_WRAP_COLUMNS)).replace("\n", "<br>");
		if (bold)
		{
			return "<html><b>" + safeText + "</b></html>";
		}

		return "<html>" + safeText + "</html>";
	}

	private static String wrapKeyValueLabel(String key, String value)
	{
		String safeKey = escapeHtml(key == null ? "" : key.trim());
		String safeValue = escapeHtml(softWrapText(value, LABEL_WRAP_COLUMNS)).replace("\n", "<br>");
		return "<html><b>" + safeKey + "</b><br>" + safeValue + "</html>";
	}

	private static String formatHeaderText(String title, String subtitle)
	{
		String safeTitle = escapeHtml(title == null ? "" : title);
		String safeSubtitle = escapeHtml(softWrapText(subtitle, LABEL_WRAP_COLUMNS)).replace("\n", "<br>");
		return "<html><b>" + safeTitle + "</b><br>" + safeSubtitle + "</html>";
	}

	private static String escapeHtml(String text)
	{
		if (text == null)
		{
			return "";
		}

		return text
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;");
	}

	private void setReadableText(JTextArea area, String text)
	{
		area.setText(softWrapText(text, AREA_WRAP_COLUMNS));
		area.setCaretPosition(0);
	}

	private static void setBookText(JTextArea area, String text)
	{
		String next = text == null ? "" : text;
		if (Objects.equals(area.getText(), next))
		{
			return;
		}

		area.setText(next);
		area.setCaretPosition(0);
	}

	private static String softWrapText(String text, int maxColumns)
	{
		if (text == null || text.isEmpty())
		{
			return "";
		}

		int width = Math.max(20, maxColumns);
		String[] lines = text.split("\\R", -1);
		StringBuilder wrapped = new StringBuilder(text.length() + 32);

		for (int i = 0; i < lines.length; i++)
		{
			appendWrappedLine(wrapped, lines[i], width);
			if (i < lines.length - 1)
			{
				wrapped.append('\n');
			}
		}

		return wrapped.toString();
	}

	private static void appendWrappedLine(StringBuilder out, String line, int width)
	{
		if (line == null || line.length() <= width)
		{
			out.append(line == null ? "" : line);
			return;
		}

		int start = 0;
		while (start < line.length())
		{
			int end = Math.min(line.length(), start + width);
			if (end >= line.length())
			{
				out.append(line, start, line.length());
				return;
			}

			int split = findBreakPosition(line, start, end);
			out.append(line, start, split).append('\n');
			start = skipWhitespace(line, split);
		}
	}

	private static int findBreakPosition(String line, int start, int end)
	{
		for (int i = end; i > start; i--)
		{
			char c = line.charAt(i - 1);
			if (Character.isWhitespace(c) || c == '|' || c == ',' || c == ':' || c == ')')
			{
				return i;
			}
		}

		return end;
	}

	private static int skipWhitespace(String line, int index)
	{
		int next = index;
		while (next < line.length() && Character.isWhitespace(line.charAt(next)))
		{
			next++;
		}
		return next;
	}
}
