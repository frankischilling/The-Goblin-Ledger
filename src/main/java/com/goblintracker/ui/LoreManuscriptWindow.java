package com.goblintracker.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.DefaultCaret;

final class LoreManuscriptWindow
{
	private static final Color OUTER_TOP = new Color(58, 46, 34);
	private static final Color OUTER_BOTTOM = new Color(44, 34, 25);
	private static final Color PAGE_TOP = new Color(244, 232, 206);
	private static final Color PAGE_BOTTOM = new Color(225, 204, 170);
	private static final Color BORDER_DARK = new Color(95, 66, 36);
	private static final Color BORDER_LIGHT = new Color(170, 130, 74);
	private static final Color INK = new Color(46, 33, 20);
	private static final Color ACCENT = new Color(120, 76, 34);
	private static final Font TITLE_FONT = new Font(Font.SERIF, Font.BOLD, 28);
	private static final Font SUBTITLE_FONT = new Font(Font.SERIF, Font.ITALIC, 15);
	private static final Font BODY_FONT = new Font(Font.SERIF, Font.PLAIN, 17);
	private static final Font FOOTER_FONT = new Font(Font.SERIF, Font.PLAIN, 12);

	private LoreManuscriptWindow()
	{
	}

	static void open(String frameTitle, String title, String subtitle, String text)
	{
		JFrame frame = new JFrame(frameTitle == null || frameTitle.isBlank() ? "Manuscript" : frameTitle);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		ManuscriptPanel panel = new ManuscriptPanel(title, subtitle, text);
		frame.setContentPane(panel);
		frame.setMinimumSize(new Dimension(560, 680));
		frame.setSize(760, 920);
		frame.setLocationByPlatform(true);
		frame.getRootPane().registerKeyboardAction(
			e -> frame.dispose(),
			KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
			JComponent.WHEN_IN_FOCUSED_WINDOW);
		frame.setVisible(true);
		panel.focusText();
	}

	private static final class ManuscriptPanel extends JPanel
	{
		private final JTextArea bodyArea;

		private ManuscriptPanel(String title, String subtitle, String text)
		{
			setLayout(new BorderLayout(0, 12));
			setBorder(new EmptyBorder(26, 26, 20, 26));
			setOpaque(true);

			JLabel titleLabel = new JLabel(safeTitle(title), SwingConstants.CENTER);
			titleLabel.setFont(TITLE_FONT);
			titleLabel.setForeground(INK);

			JLabel subtitleLabel = new JLabel(safeSubtitle(subtitle), SwingConstants.CENTER);
			subtitleLabel.setFont(SUBTITLE_FONT);
			subtitleLabel.setForeground(ACCENT);
			subtitleLabel.setBorder(new EmptyBorder(2, 6, 0, 6));

			JPanel header = new JPanel(new BorderLayout(0, 4));
			header.setOpaque(false);
			header.setBorder(new CompoundBorder(
				new MatteBorder(0, 0, 2, 0, BORDER_LIGHT),
				new EmptyBorder(0, 8, 10, 8)));
			header.add(titleLabel, BorderLayout.NORTH);
			header.add(subtitleLabel, BorderLayout.CENTER);

			bodyArea = new JTextArea(text == null ? "" : text);
			bodyArea.setEditable(false);
			bodyArea.setLineWrap(true);
			bodyArea.setWrapStyleWord(true);
			bodyArea.setFont(BODY_FONT);
			bodyArea.setForeground(INK);
			bodyArea.setCaretColor(INK);
			bodyArea.setOpaque(false);
			bodyArea.setMargin(new EmptyBorder(18, 20, 18, 20).getBorderInsets(this));
			if (bodyArea.getCaret() instanceof DefaultCaret)
			{
				((DefaultCaret) bodyArea.getCaret()).setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
			}

			JScrollPane scrollPane = new JScrollPane(bodyArea);
			scrollPane.setBorder(new CompoundBorder(
				new MatteBorder(1, 1, 1, 1, BORDER_DARK),
				new EmptyBorder(8, 8, 8, 8)));
			scrollPane.setOpaque(false);
			scrollPane.getViewport().setOpaque(false);
			scrollPane.getVerticalScrollBar().setUnitIncrement(20);
			scrollPane.getVerticalScrollBar().setBlockIncrement(140);

			JLabel footer = new JLabel("Right-click chapter entries in Lore Reader to open more pages.", SwingConstants.CENTER);
			footer.setFont(FOOTER_FONT);
			footer.setForeground(ACCENT);
			footer.setBorder(new EmptyBorder(4, 0, 0, 0));

			add(header, BorderLayout.NORTH);
			add(scrollPane, BorderLayout.CENTER);
			add(footer, BorderLayout.SOUTH);
		}

		@Override
		protected void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			Graphics2D g = (Graphics2D) graphics.create();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setPaint(new GradientPaint(0, 0, OUTER_TOP, 0, getHeight(), OUTER_BOTTOM));
			g.fillRect(0, 0, getWidth(), getHeight());

			int inset = 8;
			int pageWidth = Math.max(1, getWidth() - inset * 2);
			int pageHeight = Math.max(1, getHeight() - inset * 2);
			g.setPaint(new GradientPaint(inset, inset, PAGE_TOP, inset, inset + pageHeight, PAGE_BOTTOM));
			g.fillRoundRect(inset, inset, pageWidth, pageHeight, 28, 28);

			g.setColor(BORDER_DARK);
			g.drawRoundRect(inset, inset, pageWidth - 1, pageHeight - 1, 28, 28);
			g.setColor(BORDER_LIGHT);
			g.drawRoundRect(inset + 4, inset + 4, pageWidth - 9, pageHeight - 9, 24, 24);
			g.setColor(new Color(138, 103, 61));
			g.drawRoundRect(inset + 10, inset + 10, pageWidth - 21, pageHeight - 21, 20, 20);

			drawCornerOrnament(g, inset + 20, inset + 20, 1, 1);
			drawCornerOrnament(g, getWidth() - inset - 20, inset + 20, -1, 1);
			drawCornerOrnament(g, inset + 20, getHeight() - inset - 20, 1, -1);
			drawCornerOrnament(g, getWidth() - inset - 20, getHeight() - inset - 20, -1, -1);
			g.dispose();
		}

		private static void drawCornerOrnament(Graphics2D g, int x, int y, int dx, int dy)
		{
			g.setColor(BORDER_DARK);
			g.drawLine(x, y, x + (18 * dx), y);
			g.drawLine(x, y, x, y + (18 * dy));
			g.setColor(BORDER_LIGHT);
			g.drawLine(x + (2 * dx), y + (2 * dy), x + (12 * dx), y + (2 * dy));
			g.drawLine(x + (2 * dx), y + (2 * dy), x + (2 * dx), y + (12 * dy));
		}

		private static String safeTitle(String value)
		{
			if (value == null || value.isBlank())
			{
				return "THE BRONZE COUNT";
			}
			return value;
		}

		private static String safeSubtitle(String value)
		{
			if (value == null || value.isBlank())
			{
				return "Chronicle Manuscript";
			}
			return value;
		}

		private void focusText()
		{
			SwingUtilities.invokeLater(bodyArea::requestFocusInWindow);
		}
	}
}
