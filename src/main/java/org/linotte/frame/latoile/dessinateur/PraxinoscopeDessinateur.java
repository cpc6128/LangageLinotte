/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 17, 2010                             *
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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.linotte.frame.latoile.LaToile;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Ressources;

public class PraxinoscopeDessinateur implements Dessinateur {

	public void projette(LaToile toile, Graphics2D g2, PrototypeGraphique espece) throws ErreurException {

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

		// Fichier image :
		String fichier = (String) espece.retourneAttribut("image" + espece.trame).getValeur();

		Image image = (Image) espece.getObject();

		if (fichier != null) {
			if (image == null) {
				// On charge la première fois l'image
				image = Ressources.chargementImage(fichier);
				if (image != null) {
					espece.setNomObject(fichier);
					espece.setObject(image);
				} else {
					espece.setNomObject(null);
					espece.setObject(null);

				}
			} else if (!fichier.equals(espece.getNomObject())) {
				image = Ressources.chargementImage(fichier);
				if (image != null) {
					espece.setNomObject(fichier);
					espece.setObject(image);
				} else {
					espece.setNomObject(null);
					espece.setObject(null);
				}

			}
		} else {
			image = null;
			espece.setNomObject(null);
			espece.setObject(null);
		}

		if (image == null)
			throw new ErreurException(Constantes.LECTURE_FICHIER);

		// Gestion de la position :
		double x = espece.x;
		double y = espece.y;
		double largeur = image.getWidth(null);
		double hauteur = image.getHeight(null);

		// Gestion de la rotation :
		AffineTransform rotator = new AffineTransform();
		rotator.translate(x, y);
		if (espece.taille != 0)
			rotator.scale((largeur + espece.taille) / largeur, (hauteur + espece.taille) / hauteur);
		if (espece.angle != 0) {
			double angle = Math.toRadians(espece.angle);
			rotator.rotate(angle, (largeur / 2), (hauteur / 2));
		}

		Shape shape = new Rectangle2D.Double(0, 0, largeur, hauteur);
		espece.setShape(rotator.createTransformedShape(shape));
		if (g2 != null) {
			g2.drawImage(image, rotator, null);
		}

	}

	public PrototypeGraphique.TYPE_GRAPHIQUE clef() {
		return PrototypeGraphique.TYPE_GRAPHIQUE.PRAXINOSCOPE;
	}

}
