package org.linotte.frame.gui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI;

public class WindowsMetroTaskPaneUI extends BasicTaskPaneUI {
	public static ComponentUI createUI(JComponent c) {
		return new WindowsMetroTaskPaneUI();
	}

	protected Border createPaneBorder() {
		return new XPPaneBorder();
	}

	public void update(Graphics g, JComponent c) {
		if (c.isOpaque()) {
			g.setColor(c.getParent().getBackground());
			g.fillRect(0, 0, c.getWidth(), c.getHeight());
			g.setColor(c.getBackground());
			g.fillRect(0, getRoundHeight(), c.getWidth(), c.getHeight() - getRoundHeight());
		}
		paint(g, c);
	}

	class XPPaneBorder extends BasicTaskPaneUI.PaneBorder {
		XPPaneBorder() {
			super();
		}

		protected void paintTitleBackground(JXTaskPane group, Graphics g) {
			if (group.isSpecial()) {
				g.setColor(this.specialTitleBackground);
				g.fillRoundRect(0, 0, group.getWidth(), getRoundHeight() * 2, 0, 0);

				g.fillRect(0, getRoundHeight(), group.getWidth(), getTitleHeight(group) - getRoundHeight());
			} else {
				Paint oldPaint = ((Graphics2D) g).getPaint();
				GradientPaint gradient = new GradientPaint(0.0F, group.getWidth() / 2,
						group.getComponentOrientation().isLeftToRight() ? this.titleBackgroundGradientStart : this.titleBackgroundGradientEnd,
						group.getWidth(), getTitleHeight(group), group.getComponentOrientation().isLeftToRight() ? this.titleBackgroundGradientEnd
								: this.titleBackgroundGradientStart);

				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				((Graphics2D) g).setPaint(gradient);
				g.fillRoundRect(0, 0, group.getWidth(), getRoundHeight() * 2, 0, 0);

				g.fillRect(0, getRoundHeight(), group.getWidth(), getTitleHeight(group) - getRoundHeight());

				((Graphics2D) g).setPaint(oldPaint);
			}
		}

		protected void paintOvalAroundControls(JXTaskPane group, Graphics g, int x, int y, int width, int height) {
			if (group.isSpecial()) {
				g.setColor(this.specialTitleBackground.brighter());
				g.drawRect(x, y, width, height);
			} else {
				// g.setColor(this.titleBackgroundGradientStart);
				// g.fillRect(x, y, width, height);

				g.setColor(this.titleBackgroundGradientEnd.brighter());
				g.drawRect(x, y, width, width);
			}
		}

		protected void paintExpandedControls(JXTaskPane group, Graphics g, int x, int y, int width, int height) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			paintOvalAroundControls(group, g, x, y, width, height);
			g.setColor(Color.WHITE);
			paintChevronControls(group, g, x, y, width, height);

			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		protected boolean isMouseOverBorder() {
			return true;
		}
	}
}