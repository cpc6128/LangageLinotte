/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                             *
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

package org.linotte.frame.latoile.dessinateur;

import java.awt.AlphaComposite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.Calendar;

import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.outils.FontHelper;
import org.linotte.moteur.entites.PrototypeGraphique;

public class ScribeDessinateur implements Dessinateur {

	public void projette(LaToile toile, Graphics2D g2, PrototypeGraphique espece) throws Exception {

		if (g2 != null) {
			// Gestion de la transparence :
			float transparence = espece.transparence;
			if (transparence > 1 || transparence < 0)
				transparence = 1;
			// Drag n drop plus sympa
			if (toile.getElementDragAndDrop() == espece)
				transparence = transparence / 2;
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparence));
		}

		// Gestion de la taille :
		Font font = FontHelper.createFont(espece.police, (int) espece.taille);
		//		if (g2 != null)
		//			g2.setFont(font);

		// Gestion de la couleur :
		if (g2 != null) {
			g2.setColor(espece.couleur);
		}

		// Gestion de la position :
		String texte = espece.texte;
		// Actif ?
		if (toile != null) {
			if (toile.getPanelLaToile().getRecepteur().equals(espece)) {
				if (toile.getPanelLaToile().getRecepteur().isActif()) {
					if (Calendar.getInstance().get(Calendar.MILLISECOND) > 500)
						texte += "|";
					else
						texte += " ";
				}
			}
		}

		if (texte.length() == 0) {
			texte += " ";
		}

		FontRenderContext context = g2.getFontRenderContext();
		TextLayout text = new TextLayout(texte, font, context);

		// Gestion de la rotation :
		AffineTransform rotator = new AffineTransform();
		rotator.translate(espece.x, espece.y);
		if (espece.angle != 0) {
			double angle = Math.toRadians(espece.angle);
			rotator.rotate(angle, 0, 0);
		}

		espece.setShape(text.getOutline(rotator));
		if (g2 != null) {
			g2.fill(espece.getShape());
		}

	}

	public PrototypeGraphique.TYPE_GRAPHIQUE clef() {
		return PrototypeGraphique.TYPE_GRAPHIQUE.SCRIBE;
	}

}
