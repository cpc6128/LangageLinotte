/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 8, 2008                            *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                           *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.frame.cahier;

import org.linotte.moteur.xml.alize.parseur.ParserContext.Couple;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

/*
 * http://www.camick.com/java/source/LinePainter.java
 * 
 *  Track the movement of the Caret by painting a background line at the
 *  current caret position.
 */
public class CurrentLineHighlighter implements Highlighter.HighlightPainter, CaretListener, MouseListener, MouseMotionListener {
	private JTextComponent component;

	private Color color;
	private static final Color couleurSousParagraphe = new Color(206, 204, 247);

	private Rectangle lastView;

	private DebogueLineHighlighter debogueLineHighlighter;

	private List<Couple> constructionPileSousParagrapheSorted;

	/*
	 * The line color will be calculated automatically by attempting to make the
	 * current selection lighter by a factor of 1.2.
	 * 
	 * @param component text component that requires background line painting
	 */
	public CurrentLineHighlighter(JTextComponent component) {
		this(component, null);
		setLighter(component.getSelectionColor());
	}

	/*
	 * Manually control the line color
	 * 
	 * @param component text component that requires background line painting
	 * 
	 * @param color the color of the background line
	 */
	public CurrentLineHighlighter(JTextComponent component, Color color) {
		this.component = component;
		setColor(color);

		// Add listeners so we know when to change highlighting

		component.addCaretListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);

		// Turn highlighting on by adding a dummy highlight

		try {
			component.getHighlighter().addHighlight(0, 0, this);
			debogueLineHighlighter = new DebogueLineHighlighter(component);
			component.getHighlighter().addHighlight(0, 0, debogueLineHighlighter);
		} catch (BadLocationException ble) {
		}
	}

	/*
	 * You can reset the line color at any time
	 * 
	 * @param color the color of the background line
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/*
	 * Calculate the line color by making the selection color lighter
	 * 
	 * @return the color of the background line
	 */
	public void setLighter(Color color) {
		setColor(new Color(225, 236, 255, 180));
	}

	// Paint the background highlight

	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		try {
			{
				Rectangle r = c.modelToView(c.getCaretPosition());
				g.setColor(color);
				g.fillRect(0, r.y, c.getWidth(), r.height);
				if (lastView == null)
					lastView = r;
			}
			{
				Couple sousParagraphePosition = trouverCouple(c.getCaretPosition());
				if (sousParagraphePosition != null) {
					Rectangle r = c.modelToView(sousParagraphePosition.A);
					Rectangle r_fin = c.modelToView(sousParagraphePosition.B - 1);
					g.setColor(couleurSousParagraphe);
					//g.fillRect(0, r.y, c.getWidth(), 1);
					g.fillRect(0, r.y, 3, r_fin.y - r.y);
					//g.fillRect(0, r.y + r_fin.y - r.y, c.getWidth(), 1);
				}
			}
		} catch (Exception ble) {
			//ble.printStackTrace();
		}
	}

	/*
	 * Caret position has changed, remove the highlight
	 */
	private void resetHighlight() {
		// Use invokeLater to make sure updates to the Document are completed,
		// otherwise Undo processing causes the modelToView method to loop.

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					int offset = component.getCaretPosition();
					Rectangle currentView = component.modelToView(offset);

					// Remove the highlighting from the previously highlighted
					// line

					if (lastView.y != currentView.y) {
						component.repaint(0, lastView.y, component.getWidth(), lastView.height);
						lastView = currentView;
					}
					component.repaint(0, 0, 3, component.getHeight());
				} catch (Exception ble) {
					ble.fillInStackTrace();
				}
			}
		});
	}

	// Implement CaretListener

	public void caretUpdate(CaretEvent e) {
		resetHighlight();
	}

	// Implement MouseListener

	public void mousePressed(MouseEvent e) {
		resetHighlight();
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	// Implement MouseMotionListener

	public void mouseDragged(MouseEvent e) {
		resetHighlight();
	}

	public void mouseMoved(MouseEvent e) {
	}

	public void setPositionDeboguage(int position_deboguage) {
		debogueLineHighlighter.setPositionDeboguage(position_deboguage);
	}

	public List<Couple> getConstructionPileSousParagrapheSorted() {
		return constructionPileSousParagrapheSorted;
	}

	public void setConstructionPileSousParagrapheSorted(List<Couple> constructionPileSousParagrapheSorted) {
		this.constructionPileSousParagrapheSorted = constructionPileSousParagrapheSorted;
	}

	private Couple trouverCouple(int position) {
		Couple dernier = null;
		if (constructionPileSousParagrapheSorted != null) {
			for (Couple couple : constructionPileSousParagrapheSorted) {
				if (position > couple.A && position < couple.B) {
					dernier = couple;
				}
				if (dernier != null && position > dernier.B)
					break;
			}
		}
		return dernier;
	}
}