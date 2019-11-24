/***********************************************************************
 * Linotte                                                             *
 * Version release date : November 22, 2013                            *
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

package org.linotte.frame.latoile.dessinateur;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.linotte.frame.latoile.Couleur;
import org.linotte.frame.latoile.LaToile;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.ErreurException;

public class MozaiqueDessinateur implements Dessinateur {

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

		BufferedImage image = (BufferedImage) espece.getObject();

		if (image == null) {
			// Palette :
			Casier palette = (Casier) espece.retourneAttribut("palette");
			// Modèle :
			Casier modeleActeurs = (Casier) espece.retourneAttribut("modèle");
			int hauteur = modeleActeurs.taille();
			List<List<Integer>> matriceEntiers = new ArrayList<List<Integer>>(hauteur);
			int max = 0;
			for (int i = 0; i < hauteur; i++) {
				List<Integer> ligneEntiers = new ArrayList<Integer>();
				matriceEntiers.add(ligneEntiers);
				Casier ligneActeurs = (Casier) modeleActeurs.retourne(i);
				int largeur = ligneActeurs.taille();
				if (largeur > max)
					max = largeur;
				for (int j = 0; j < largeur; j++) {
					Acteur acteur = ligneActeurs.retourne(j);
					ligneEntiers.add(((BigDecimal) acteur.getValeur()).intValue());
				}

			}

			image = new BufferedImage(max, matriceEntiers.size(), BufferedImage.TYPE_INT_ARGB);
			for (int y = 0; y < matriceEntiers.size(); y++) {
				List<Integer> ligne = matriceEntiers.get(y);
				for (int x = 0; x < ligne.size(); x++) {
					int index = ligne.get(x);
					if (index > -1) {
						if (index < palette.taille()) {
							String c = (String) palette.retourne(index).getValeur();
							image.setRGB(x, y, Couleur.retourneCouleur(c).getRGB());
						} else {
							image.setRGB(x, y, Color.BLACK.getRGB());
						}
					}
				}
			}
			espece.setObject(image);
		}

		// Gestion de la position :
		double largeur = image.getWidth(null);
		double hauteur = image.getHeight(null);

		AffineTransform rotator = new AffineTransform();
		rotator.translate(espece.x, espece.y);
		if (espece.taille != 0)
			rotator.scale((largeur + espece.taille) / largeur, (largeur + espece.taille) / largeur);
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
		return PrototypeGraphique.TYPE_GRAPHIQUE.MOZAIQUE;
	}

}
