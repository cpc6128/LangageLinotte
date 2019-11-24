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
import java.awt.geom.Rectangle2D;

import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.outils.FontHelper;
import org.linotte.moteur.entites.PrototypeGraphique;

public class TexteDessinateur implements Dessinateur {

	public void projette(LaToile toile, Graphics2D g2, PrototypeGraphique espece) throws Exception {
		if (espece.isModifie()) {
			espece.setModifie(false);

			// Affichage du texte :
			String texte = espece.texte;
			if (texte.length() == 0) {
				espece.setShape(null);
				return;
			}

			// Gestion de la taille :
			Font font = FontHelper.createFont(espece.police, (int) espece.taille);
			//		if (g2 != null)
			//			g2.setFont(font);

			FontRenderContext context = toile.getPanelLaToile().getImageGraphics().getFontRenderContext();// g2.getFontRenderContext();

			TextLayout text = new TextLayout(texte, font, context);

			// Gestion de la rotation :
			double angle = Math.toRadians(espece.angle);
			AffineTransform rotator = new AffineTransform();
			rotator.translate(espece.x, espece.y);
			if (espece.angle != 0) {
				Rectangle2D rectangle2D = text.getBounds();
				rotator.rotate(angle, rectangle2D.getCenterX(), rectangle2D.getCenterY());
			}

			espece.setShape(text.getOutline(rotator));

		}
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

		// Gestion de la couleur :
		if (g2 != null)
			g2.setColor(espece.couleur);

		if (g2 != null && espece.getShape() != null) {
			g2.fill(espece.getShape());
		}

	}

	public PrototypeGraphique.TYPE_GRAPHIQUE clef() {
		return PrototypeGraphique.TYPE_GRAPHIQUE.GRAFFITI;
	}

}
