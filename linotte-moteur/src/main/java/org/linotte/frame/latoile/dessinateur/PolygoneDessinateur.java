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
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Ressources;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PolygoneDessinateur implements Dessinateur {

	public void projette(LaToile toile, Graphics2D g2, PrototypeGraphique espece) throws ErreurException {

		BufferedImage image = (BufferedImage) espece.getObject2();

		if (espece.isModifie()) {
			espece.setModifie(false);

			Stroke stroke = new BasicStroke(Math.abs(espece.taille), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);

			// Gestion de la position :
			int x = (int) espece.x;
			int y = (int) espece.y;

			Shape tempShape = (Shape) espece.getObject();

			if (tempShape == null) {

				// Construction des coordonnées :
				List<Integer> xx = new ArrayList<Integer>();
				xx.add(x);
				List<Integer> yy = new ArrayList<Integer>();
				yy.add(y);
				int i = 1;
				do {
					String clefx = "dx" + i;
					String clefy = "dy" + i;
					Acteur a1 = espece.retourneAttribut(clefx);
					Acteur a2 = espece.retourneAttribut(clefy);
					if (a1.estIlVraimentVide() || a2.estIlVraimentVide())
						break;
					BigDecimal xn = (BigDecimal) a1.getValeur();
					BigDecimal yn = (BigDecimal) a2.getValeur();
					xx.add(xn.intValue() + x);
					yy.add(yn.intValue() + y);
					i++;
				} while (true);

				int[] txx = new int[xx.size()];
				int[] tyy = new int[yy.size()];
				int s = xx.size();
				for (int p = 0; p < s; p++) {
					txx[p] = xx.get(p);
					tyy[p] = yy.get(p);
				}

				tempShape = new Polygon(txx, tyy, txx.length);
				espece.setObject(tempShape);

			}

			// Gestion de la rotation :
			AffineTransform rotator = null;
			if (espece.angle != 0) {
				double angle = Math.toRadians(espece.angle);
				rotator = new AffineTransform();
				Rectangle2D rectangle2D = tempShape.getBounds2D();
				rotator.rotate(angle, rectangle2D.getCenterX(), rectangle2D.getCenterY());
			}
			Shape shape;

			if (espece.plein) {
				if (espece.angle == 0)
					shape = tempShape;
				else
					shape = rotator.createTransformedShape(tempShape);
			} else {
				if (espece.angle == 0)
					shape = stroke.createStrokedShape(tempShape);
				else
					shape = rotator.createTransformedShape(stroke.createStrokedShape(tempShape));
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

				//                AffineTransform rotator = new AffineTransform();
				//                rotator.translate(espece.x, espece.y);
				//                if (espece.taille != 0)
				//                        rotator.scale((largeur + espece.taille) / largeur, (hauteur + espece.taille) / hauteur);
				//                if (espece.angle != 0) {
				//                        double angle = Math.toRadians(espece.angle);
				//                        rotator.rotate(angle, (largeur / 2), (hauteur / 2));
				//                }

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
			// Drag n drop plus sympa
			if (toile.getElementDragAndDrop() == espece)
				transparence = transparence / 2;
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
		return PrototypeGraphique.TYPE_GRAPHIQUE.POLYGONE;
	}

}
