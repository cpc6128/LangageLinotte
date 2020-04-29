package org.linotte.frame.cahier;

/***********************************************************************
 * Langage Linotte                                                     *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 3 or       *
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

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;

/**
 * A class illustrating running line number count on JTextPane. Nothing is
 * painted on the pane itself, but a separate JPanel handles painting the line
 * numbers.
 * http://www.velocityreviews.com/forums/t131969-display-line-number-in-
 * jtextpane.html
 * 
 * @author Daniel Sjöblom
 * @author Ronan Mounès 
 * @Created on Mar 3, 2004 Copyright (c) 2004
 * @version 1.1
 * 
 */
@SuppressWarnings("serial")
public class LineRenderer extends JPanel implements ActionListener {

	private boolean draw = true;

	class PopupListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	private JTextPane pane;
	private JScrollPane scrollPane;
	private JPopupMenu popup;
	private int y = 0;
	private Cahier cahier;

	public LineRenderer(Cahier pcahier) {
		super();
		setMinimumSize(new Dimension(30, 30));
		setPreferredSize(new Dimension(30, 30));
		setMinimumSize(new Dimension(30, 30));
		cahier = pcahier;
		pane = cahier.jEditorPaneCachier;
		scrollPane = cahier.scrollPan;


		popup = new JPopupMenu();
		// Add listener to components that can bring up popup menus.
		MouseListener popupListener = new PopupListener();
		this.addMouseListener(popupListener);

	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());

		if (draw) {

			// We need to properly convert the points to match the viewport
			// Read docs for viewport
			int start = pane.viewToModel(scrollPane.getViewport().getViewPosition()); // starting
			// pos
			// in
			// document
			int end = pane.viewToModel(new Point(scrollPane.getViewport().getViewPosition().x + pane.getWidth(), scrollPane.getViewport().getViewPosition().y
					+ pane.getHeight()));
			// end pos in doc

			// translate offsets to lines
			Document doc = pane.getDocument();
			int startline = doc.getDefaultRootElement().getElementIndex(start) + 1;
			int endline = doc.getDefaultRootElement().getElementIndex(end) + 1;

			int fontDesc = g.getFontMetrics(pane.getFont()).getDescent();
			int starting_y = -1;

			// ligne courant :
			int ligneCourante = doc.getDefaultRootElement().getElementIndex(pane.getCaretPosition()) + 1;

			try {
				int fontHeight = g.getFontMetrics(pane.getFont()).getHeight();
				starting_y = pane.modelToView(start).y - scrollPane.getViewport().getViewPosition().y + fontHeight - fontDesc;
			} catch (BadLocationException e1) {
			}

			for (int line = startline, y = starting_y; line <= endline; line++) {
				g.setColor(line != ligneCourante ? Color.GRAY : Color.MAGENTA);
				g.drawString(Integer.toString(line), 0, y);
				Rectangle e;
				try {
					e = pane.modelToView(doc.getDefaultRootElement().getElement(line).getStartOffset());
					// On calcule la hauteur dynamiquement car l'Atelier permet de
					// personnaliser ses styles pour chaque élément du livre
					y += e.height;
				} catch (Exception e1) {
					// e1.printStackTrace();
				}
			}
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Point pt;
		try {
			// Activer le curseur :
			pane.grabFocus();
			pt = new Point(pane.modelToView(pane.getCaretPosition()).x, scrollPane.getViewport().getViewPosition().y + y);
			int pos = pane.viewToModel(pt);
			pane.setCaretPosition(pos);
			getAction(DefaultEditorKit.selectLineAction).actionPerformed(null);
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
	}

	private Action getAction(String name) {
		Action action = null;
		Action[] actions = pane.getActions();

		for (int i = 0; i < actions.length; i++) {
			if (name.equals(actions[i].getValue(Action.NAME).toString())) {
				action = actions[i];
				break;
			}
		}

		return action;
	}

	public boolean isDraw() {
		return draw;
	}

	public void setDraw(boolean draw) {
		this.draw = draw;
	}
}