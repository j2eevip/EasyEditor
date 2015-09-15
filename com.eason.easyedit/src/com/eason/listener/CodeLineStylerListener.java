package com.eason.listener;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GlyphMetrics;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

class CodeLineStylerListener implements LineStyleListener {
	private CodeScanner scanner;
	private int[] tokenColors;
	private Color[] colors;

	public CodeLineStylerListener() {
		initializeColors();
		scanner = new CodeScanner();
	}

	private Color getColor(int type) {
		if (type < 0 || type >= tokenColors.length) {
			return null;
		}
		return colors[tokenColors[type]];
	}

	private final int inBlockComment(final String line) {
		final int length = line.length();
		for (int i = 0; i < length ; i++) {
			char c = line.charAt(i);
			if (c == ';' && c != Constant.EOF) {
				return i;
			}
		}
		return -1;
	}

	private void initializeColors() {
		Display display = Display.getDefault();
		colors = new Color[] { new Color(display, new RGB(0, 0, 0)), // black
				new Color(display, new RGB(0, 0, 255)), // blue
				new Color(display, new RGB(0, 255, 0)), // green
				new Color(display, new RGB(127, 0, 85)), // red
				new Color(display, new RGB(251, 171, 225)) };
		tokenColors = new int[Constant.MAXIMUM_TOKEN];
		tokenColors[Constant.WORD] = 0;
		tokenColors[Constant.WHITE] = 0;
		tokenColors[Constant.KEY] = 3;
		tokenColors[Constant.COMMENT] = 1;
		tokenColors[Constant.STRING] = 2;
		tokenColors[Constant.OTHER] = 0;
		tokenColors[Constant.NUMBER] = 0;
		tokenColors[Constant.VAR] = 4;
	}

	public void disposeColors() {
		for (int i = 0; i < colors.length; i++) {
			colors[i].dispose();
		}
	}

	public void lineGetStyle(LineStyleEvent event) {
		StyledText text = (StyledText) event.widget;
		StyleRange styleRange = new StyleRange();
		styleRange.foreground = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
		int maxLine = text.getLineCount();
		int bulletLength = Integer.toString(maxLine).length();
		int bulletWidth = (bulletLength + 1) * text.getLineHeight() / 2;
		styleRange.metrics = new GlyphMetrics(0, 0, bulletWidth);
		event.bullet = new Bullet(ST.BULLET_TEXT, styleRange);
		int bulletLine = text.getLineAtOffset(event.lineOffset) + 1;
		event.bullet.text = String.format("%" + bulletLength + "s", bulletLine);

		Vector<StyleRange> styles = new Vector<StyleRange>();
		int token;
		StyleRange lastStyle;
		int blockCommentStart = inBlockComment(event.lineText);
		if (event.lineText.length() > 0 &&  blockCommentStart >= 0) {
			styles.addElement(new StyleRange(event.lineOffset + blockCommentStart, event.lineText.length(), getColor(Constant.COMMENT), null));
			event.styles = new StyleRange[styles.size()];
			styles.copyInto(event.styles);
			if (blockCommentStart == 0) {
				return;
			}
		}
		Color defaultFgColor = ((Control) event.widget).getForeground();
		scanner.setRange(event.lineText);
		token = scanner.nextToken();
		while (token != Constant.EOF) {
			if (token == Constant.OTHER) {
			} else if (token == Constant.COMMENT){
				styles.addElement(new StyleRange(event.lineOffset, event.lineText.length(), getColor(Constant.COMMENT), null));
				event.styles = new StyleRange[styles.size()];
				styles.copyInto(event.styles);
			} else if (token != Constant.WHITE) {
				Color color = getColor(token);
				if ((!color.equals(defaultFgColor)) || (token == Constant.KEY)) {
					StyleRange style = new StyleRange(scanner.getStartOffset() + event.lineOffset, scanner.getLength(), color, null);
					if (token == Constant.KEY) {
						style.fontStyle = SWT.BOLD;
					}
					if (styles.isEmpty()) {
						styles.addElement(style);
					} else {
						lastStyle = (StyleRange) styles.lastElement();
						if (lastStyle.similarTo(style) && (lastStyle.start + lastStyle.length == style.start)) {
							lastStyle.length += style.length;
						} else {
							styles.addElement(style);
						}
					}
				}
			} else if ((!styles.isEmpty()) && ((lastStyle = (StyleRange) styles.lastElement()).fontStyle == SWT.BOLD)) {
				int start = scanner.getStartOffset() + event.lineOffset;
				lastStyle = (StyleRange) styles.lastElement();
				if (lastStyle.start + lastStyle.length == start) {
					lastStyle.length += scanner.getLength();
				}
			}
			token = scanner.nextToken();
		}
		event.styles = new StyleRange[styles.size()];
		styles.copyInto(event.styles);
	}
}