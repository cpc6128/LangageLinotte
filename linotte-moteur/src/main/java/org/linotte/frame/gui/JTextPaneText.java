/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

package org.linotte.frame.gui;

import org.linotte.frame.cahier.CurrentLineHighlighter;
import org.linotte.frame.tableau.LimitLinesDocumentListener;

import javax.swing.*;
import javax.swing.text.StyledDocument;
import java.awt.*;

@SuppressWarnings("serial")
public class JTextPaneText extends JTextPane {

	private CurrentLineHighlighter lineHighlighter;

	public JTextPaneText(StyledDocument doc, boolean highlighter) {
		super(doc);
		if (highlighter)
			lineHighlighter = new CurrentLineHighlighter(this);
		doc.addDocumentListener(new LimitLinesDocumentListener(6000));
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		super.paintComponent(g);
	}

	public CurrentLineHighlighter getLineHighlighter() {
		return lineHighlighter;
	}

}
