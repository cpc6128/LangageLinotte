package org.linotte.frame.outils;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * http://stackoverflow.com/questions/18118536/how-can-i-add-to-or-change-a-label-on-a-jmenuitem
 *
 */
public class ArrowIcon implements Icon, SwingConstants {

	private static final int DEFAULT_SIZE = 11;
	//private static final int DEFAULT_SIZE = 5;
	private int size;
	private int iconSize;
	private int direction;
	private boolean isEnabled;
	private BasicArrowButton iconRenderer;

	public ArrowIcon(int direction, boolean isPressedView) {
		this(DEFAULT_SIZE, direction, isPressedView);
	}

	public ArrowIcon(int iconSize, int direction, boolean isEnabled) {
		this.size = iconSize / 2;
		this.iconSize = iconSize;
		this.direction = direction;
		this.isEnabled = isEnabled;
		iconRenderer = new BasicArrowButton(direction);
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		iconRenderer.paintTriangle(g, x, y, size, direction, isEnabled);
	}

	@Override
	public int getIconWidth() {
		//int retCode;
		switch (direction) {
		case NORTH:
		case SOUTH:
			return iconSize;
		case EAST:
		case WEST:
			return size;
		}
		return iconSize;
	}

	@Override
	public int getIconHeight() {
		switch (direction) {
		case NORTH:
		case SOUTH:
			return size;
		case EAST:
		case WEST:
			return iconSize;
		}
		return size;
	}
}
