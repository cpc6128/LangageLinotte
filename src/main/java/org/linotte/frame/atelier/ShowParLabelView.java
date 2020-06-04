package org.linotte.frame.atelier;

import javax.swing.text.Element;
import javax.swing.text.LabelView;
import java.awt.*;

/**
 * http://java-sl.com/showpar.html
 */

public class ShowParLabelView extends LabelView {

    private static final float dash1[] = {3.0f};
    private static final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

    public ShowParLabelView(Element elem) {
        super(elem);
    }

    public void paint(Graphics g, Shape a) {
        //Do whatever other painting here;
        Color c = (Color) getElement().getAttributes().getAttribute("Underline-Color");
        if (c != null && !c.equals(Color.WHITE)) {
            int y = a.getBounds().y + (int) getGlyphPainter().getAscent(this) + 2;
            int x1 = a.getBounds().x;
            int x2 = a.getBounds().width + x1;

            Graphics2D g2 = ((Graphics2D) g);
            Stroke stroke_bak = g2.getStroke();
            g2.setStroke(dashed);
            g.setColor(c);
            g.drawLine(x1, y, x2, y);
            g2.setStroke(stroke_bak);
        }
        super.paint(g, a);
    }
}
