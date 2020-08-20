package org.linotte.frame.projet;

import com.formdev.flatlaf.swingx.ui.FlatTaskPaneUI;
import com.formdev.flatlaf.ui.FlatUIUtils;
import com.formdev.flatlaf.util.UIScale;
import org.jdesktop.swingx.JXTaskPane;

import javax.swing.border.Border;
import java.awt.*;

public class XFlatTaskOPaneUI extends FlatTaskPaneUI {

    @Override
    protected int getTitleHeight(Component c) {
        return 5;
    }

    @Override
    protected Border createPaneBorder() {
        return new XFlatPaneBorder();
    }

    private class XFlatPaneBorder
            extends PaneBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            FlatUIUtils.setRenderingHints((Graphics2D) g);

            super.paintBorder(c, g, x, y, width, height);
        }

        @Override
        protected void paintTitleBackground(JXTaskPane group, Graphics g) {
            int width = group.getWidth();
            int height = getTitleHeight(group);
            float arc = UIScale.scale((float) roundHeight);
            float lineWidth = UIScale.scale(1f);

            // paint background
            g.setColor(group.isSpecial() ? specialTitleBackground : titleBackgroundGradientStart);
            ((Graphics2D) g).fill(FlatUIUtils.createRoundRectanglePath(lineWidth, lineWidth,
                    width - (lineWidth * 2), height - (lineWidth * 2), arc - lineWidth, arc - lineWidth, 0, 0));

            // paint border
            if (borderColor != null) {
                g.setColor(borderColor);
                ((Graphics2D) g).fill(FlatUIUtils.createRoundRectangle(0, 0, width, height, lineWidth, arc, arc, 0, 0));
            }
        }

        @Override
        protected void paintExpandedControls(JXTaskPane group, Graphics g, int x, int y, int width, int height) {
        }

        @Override
        protected void paintChevronControls(JXTaskPane group, Graphics g, int x, int y, int width, int height) {
        }

        @Override
        protected void paintTitle(JXTaskPane group, Graphics g, Color textColor,
                                  int x, int y, int width, int height) {
            // scale title position
            int titleX = UIScale.scale(3);
            int titleWidth = group.getWidth() - getTitleHeight(group) - titleX;
            if (!group.getComponentOrientation().isLeftToRight()) {
                // right-to-left
                titleX = group.getWidth() - titleX - titleWidth;
            }

            super.paintTitle(group, g, textColor, titleX, y, titleWidth, height);
        }

        @Override
        protected void paintFocus(Graphics g, Color paintColor, int x, int y, int width, int height) {
            // scale focus rectangle
            int sx = UIScale.scale(x);
            int sy = UIScale.scale(y);
            int swidth = width - (sx - x) * 2;
            int sheight = height - (sy - y) * 2;

            super.paintFocus(g, paintColor, sx, sy, swidth, sheight);
        }

        @Override
        protected boolean isMouseOverBorder() {
            return false;
        }
    }
}
