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

import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.outils.FontHelper;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.ErreurException;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class ParcheminDessinateur implements Dessinateur {

	public void projette(LaToile toile, Graphics2D g2, PrototypeGraphique espece) throws ErreurException {

		if (g2 != null) {
			// Affichage du texte :
			String texte = espece.texte;
			if (texte.length() == 0) {
				espece.setShape(null);
				return;
			}

			Font font = FontHelper.createFont(espece.police, (int) espece.taille);

			// Gestion de la transparence :
			float transparence = espece.transparence;
			if (transparence > 1 || transparence < 0)
				transparence = 1;
			// Drag n drop plus sympa
			if (toile != null && toile.getElementDragAndDrop() == espece)
				transparence = transparence / 2;
			if (g2 != null)
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparence));

			// largeur du parchemin :
			float largeur = (float) espece.largeur;

			AttributedString attribString = new AttributedString(texte);
			attribString.addAttribute(TextAttribute.FOREGROUND, espece.couleur, 0, texte.length());
			attribString.addAttribute(TextAttribute.FONT, font, 0, texte.length());

			AttributedCharacterIterator attribCharIterator = attribString.getIterator();

			FontRenderContext frc = new FontRenderContext(null, true, false);
			LineBreakMeasurer lbm = new LineBreakMeasurer(attribCharIterator, frc);

			int debut = 0;
			int suite;
			TextLayout layout;
			String temp;
			int positionligne;
			float x = (float) espece.x;
			float y = (float) espece.y;
			while (lbm.getPosition() < texte.length()) {
				suite = lbm.nextOffset(largeur);
				temp = texte.substring(debut, suite);
				positionligne = temp.indexOf('\n');
				if (positionligne != -1) {
					layout = lbm.nextLayout(largeur, debut + positionligne, false);
				} else {
					layout = lbm.nextLayout(largeur);
				}
				debut = lbm.getPosition() + 1;
				y += layout.getAscent();
				layout.draw(g2, x, y);
				y += layout.getDescent() + layout.getLeading();
			}
		}

	}

	public PrototypeGraphique.TYPE_GRAPHIQUE clef() {
		return PrototypeGraphique.TYPE_GRAPHIQUE.PARCHEMIN;
	}

}
