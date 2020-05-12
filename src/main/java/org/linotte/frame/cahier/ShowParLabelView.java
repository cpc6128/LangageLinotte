package org.linotte.frame.cahier;

import org.linotte.moteur.outils.Preference;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.Position;
import java.awt.*;

/**
 * 
 * http://java-sl.com/showpar.html
 * 
 *
 */

public class ShowParLabelView extends LabelView {

	private static final Color COULEUR = new Color(206, 206, 206);
	private static final Color COULEUR_ACTIONS = new Color(206, 204, 247);
	private static final float dash1[] = { 3.0f };
	private static final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
	public static int decallage = 0;

	public ShowParLabelView(Element elem) {
		super(elem);
	}

	public void paint(Graphics g, Shape a) {
		boolean isShowParagraphs = Preference.getIntance().getBoolean(Preference.P_MODE_BONIFIEUR);
		if (isShowParagraphs) {
			try {
				Rectangle r = a instanceof Rectangle ? (Rectangle) a : a.getBounds();
				String labelStr = getDocument().getText(getStartOffset(), getEndOffset() - getStartOffset());
				int x0 = modelToView(getStartOffset(), new Rectangle(r.width, r.height), Position.Bias.Forward).getBounds().x;
				for (int i = 0; i < labelStr.length(); i++) {
					if (i > 0) {
						int x = modelToView(i + getStartOffset(), new Rectangle(r.width, r.height), Position.Bias.Forward).getBounds().x - x0;
						char c = labelStr.charAt(i);
						if (c == '\t') {
							if (i > 1) {
								int x2 = modelToView(i + 1 + getStartOffset(), new Rectangle(r.width, r.height), Position.Bias.Forward).getBounds().x - x0;
								Rectangle clip = new Rectangle(r.x + x, r.y, x2 - x, r.height);
								Shape oldClip = g.getClip();
								g.setClip(clip);
								g.setColor(COULEUR);
								// Patch décallage :
								g.fillRect(8 + decallage * i, r.y, 1, 15);
								g.setClip(oldClip);
							} else if (i > 0) {
								int x2 = modelToView(i + 1 + getStartOffset(), new Rectangle(r.width, r.height), Position.Bias.Forward).getBounds().x - x0;
								Rectangle clip = new Rectangle(r.x + x, r.y, x2 - x, r.height);
								Shape oldClip = g.getClip();
								g.setClip(clip);
								g.setColor(COULEUR_ACTIONS);
								// Patch décallage :
								g.fillRect(8 + decallage * i, r.y, 1, 20);
								g.setClip(oldClip);
							}
						}
					}
				}
				/*if (labelStr.indexOf("::") > -1 || labelStr.indexOf("<-") > -1 || labelStr.indexOf("hérite") > -1) {
					int i = 0;//labelStr.indexOf("::");
					int x = modelToView(i + getStartOffset(), new Rectangle(r.width, r.height), Position.Bias.Forward).getBounds().x - x0;
					int x2 = modelToView(i + 1 + getStartOffset(), new Rectangle(r.width, r.height), Position.Bias.Forward).getBounds().x - x0;
					Rectangle clip = new Rectangle(0, r.y, x2 - x, r.height);
					Shape oldClip = g.getClip();
					g.setClip(clip);
					g.setColor(COULEUR_TYPE);
					g.fillRect(0, r.y, 3, 30);
					g.setClip(oldClip);
				}*/
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		//Do whatever other painting here;
		/*Color c = (Color) getElement().getAttributes().getAttribute("Underline-Color");
		if (c == null) {
			c = Color.WHITE;
		}
		int y = a.getBounds().y + (int) getGlyphPainter().getAscent(this) + 2;
		int x1 = a.getBounds().x;
		int x2 = a.getBounds().width + x1;

		Graphics2D g2 = ((Graphics2D) g);
		Stroke stroke_bak = g2.getStroke();
		g2.setStroke(dashed);
		g.setColor(c);
		g.drawLine(x1, y, x2, y);
		g2.setStroke(stroke_bak);*/
		super.paint(g, a);
	}
}
