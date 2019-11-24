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
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.linotte.frame.latoile.LaToile;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Ressources;

public class CercleDessinateur implements Dessinateur {

	public void projette(LaToile toile, Graphics2D g2, PrototypeGraphique espece) throws ErreurException {

		BufferedImage image = (BufferedImage) espece.getObject2();

		if (espece.isModifie()) {
			espece.setModifie(false);

			// Gestion du rayon :
			double rayon = espece.rayon;

			// Gestion de la taille :
			Stroke stroke = new BasicStroke(Math.abs(espece.taille), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

			// Gestion de la position :

			// On calcule les nouvelles positions :
			double longueur = rayon * 2d;
			double x = espece.x - rayon;
			double y = espece.y - rayon;

			Shape shape;

			// Gestion du mode plein :
			if (espece.plein) {
				shape = new Ellipse2D.Double(x, y, longueur, longueur);
			} else {
				shape = stroke.createStrokedShape(new Ellipse2D.Double(x, y, longueur, longueur));
			}
			espece.setShape(shape);

			// Gestion des textures :
			String fichier = espece.texture;
			if (espece.texture != null && espece.texture.length() > 0) {
				if (image == null) {
					// On charge la première fois l'image
					image = Ressources.toBufferedImage(Ressources.chargementImage(fichier));
					if (image != null) {
						espece.setNomObject(fichier);
						espece.setObject2(image);
					} else {
						espece.setNomObject(null);
						espece.setObject2(null);

					}
				} else if (!fichier.equals(espece.getNomObject())) {
					image = Ressources.toBufferedImage(Ressources.chargementImage(fichier));
					if (image != null) {
						espece.setNomObject(fichier);
						espece.setObject2(image);
					} else {
						espece.setNomObject(null);
						espece.setObject2(null);
					}

				}
			} else {
				espece.setNomObject(null);
				espece.setObject2(null);
				image = null;
			}
		}
		if (g2 != null) {
			// Gestion de la transparence :
			float transparence = espece.transparence;
			if (transparence > 1 || transparence < 0)
				transparence = 1;

			if (toile.getElementDragAndDrop() == espece)
				transparence = transparence / 2f;

			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparence));
			g2.setColor(espece.couleur);

			// Affichage de la texture :
			if (image != null) {
				TexturePaint texturePaint = new TexturePaint(image, new Rectangle2D.Double(espece.x, espece.y, image.getWidth(), image.getHeight()));
				g2.setPaint(texturePaint);
			}

			g2.fill(espece.getShape());
		}

	}

	public PrototypeGraphique.TYPE_GRAPHIQUE clef() {
		return PrototypeGraphique.TYPE_GRAPHIQUE.CERCLE;
	}

}
