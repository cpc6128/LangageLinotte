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

import org.linotte.frame.moteur.FrameProcess;

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

/*
 * http://www.camick.com/java/source/LinePainter.java
 * 
 *  Track the movement of the Caret by painting a background line at the
 *  current caret position.
 *
 * Classe utilisée lors du déboggage
 */
public class DebogueLineHighlighter implements Highlighter.HighlightPainter, CaretListener, MouseListener, MouseMotionListener {
	private JTextComponent component;

	private Rectangle lastView;

	private Color colorDeboguage = new Color(107, 169, 128).brighter();

	// Pour surligner la ligne déboguée :
	private int position_deboguage = -1;

	/*
	 * The line color will be calculated automatically by attempting to make the
	 * current selection lighter by a factor of 1.2.
	 * 
	 * @param component text component that requires background line painting
	 */
	public DebogueLineHighlighter(JTextComponent component) {
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
	public DebogueLineHighlighter(JTextComponent component, Color color) {
		this.component = component;
		setColor(color);

		// Add listeners so we know when to change highlighting

		component.addCaretListener(this);
		component.addMouseListener(this);
		component.addMouseMotionListener(this);

		// Turn highlighting on by adding a dummy highlight

		try {
			component.getHighlighter().addHighlight(0, 0, this);
		} catch (BadLocationException ble) {
		}
	}

	/*
	 * You can reset the line color at any time
	 * 
	 * @param color the color of the background line
	 */
	public void setColor(Color color) {
		// this.colorDeboguage = color;
	}

	/*
	 * Calculate the line color by making the selection color lighter
	 * 
	 * @return the color of the background line
	 */
	public void setLighter(Color color) {
		setColor(colorDeboguage);
	}

	// Paint the background highlight

	public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
		try {
			if (position_deboguage != -1 && FrameProcess.applet != null) {
				Rectangle r = c.modelToView(position_deboguage);
				if (FrameProcess.applet.getInspecteur().enAttente)
					g.setColor(colorDeboguage);
				else
					g.setColor(colorDeboguage.brighter());
				g.drawRoundRect(0, r.y, c.getWidth(), r.height - 2, 15, 15);
				if (lastView == null)
					lastView = r;
			}
		} catch (BadLocationException ble) {
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
					int offset = position_deboguage;
					Rectangle currentView = null;
					if (position_deboguage != -1)
						currentView = component.modelToView(offset);
					// Remove the highlighting from the previously highlighted
					// line

					if (lastView != null)
						component.repaint(0, lastView.y, component.getWidth(), lastView.height);
					lastView = currentView;
				} catch (Exception ble) {
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
		this.position_deboguage = position_deboguage;
		resetHighlight();
	}

}