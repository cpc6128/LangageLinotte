/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                        *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                            *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.frame.gui;

import org.linotte.moteur.outils.Ressources;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class JPanelBackGround extends JPanel {

	private Image icon = null;

	JScrollPane fix = null;

	Point p = null;

	public JPanelBackGround(String image, JScrollPane scroll) {
		super();
		if (image != null)
			icon = Ressources.getImageIcon(image).getImage();
		fix = scroll;
		//setOpaque(false);
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (icon != null)
			if (fix != null) {
				p = fix.getViewport().getViewPosition();
				g.drawImage(icon, p.x, p.y, null);
				// super.paintComponent(g);
			} else {
				g.drawImage(icon, 0, 0, null);
			}

		Color c1 = getBackground();
		Color c2 = getBackground().darker();
		((Graphics2D) g).setPaint(new GradientPaint(0, 0, c1, 500, 500, c2, false));
		Rectangle r = g.getClipBounds();
		g.fillRect(r.x, r.y, r.width, r.height);

	}
}
