/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 8, 2009                                 *
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
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CheminDessinateur implements Dessinateur {

	/**
	 * // draw GeneralPath (polyline) int x2Points[] = {0, 100, 0, 100}; int
	 * y2Points[] = {0, 50, 50, 0}; GeneralPath polyline = new
	 * GeneralPath(GeneralPath.WIND_EVEN_ODD, x2Points.length);
	 * 
	 * polyline.moveTo (x2Points[0], y2Points[0]);
	 * 
	 * for (int index = 1; index < x2Points.length; index++) {
	 * polyline.lineTo(x2Points[index], y2Points[index]); };
	 * 
	 * g2.draw(polyline);
	 * 
	 * 
	 */

	public void projette(LaToile toile, Graphics2D g2, PrototypeGraphique espece) throws ErreurException {

		BufferedImage image = (BufferedImage) espece.getObject2();

		if (espece.isModifie()) {
			espece.setModifie(false);

			Stroke stroke = new BasicStroke(Math.abs(espece.taille), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);

			Shape tempShape = (Shape) espece.getObject();

			if (tempShape == null) {

				// Construction des coordonnées :
				List<Integer> xx = new ArrayList<Integer>();
				xx.add((int) espece.x);
				List<Integer> yy = new ArrayList<Integer>();
				yy.add((int) espece.y);
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
					xx.add(xn.intValue() + (int) espece.x);
					yy.add(yn.intValue() + (int) espece.y);
					i++;
				} while (true);

				tempShape = new GeneralPath(Path2D.WIND_EVEN_ODD, xx.size());

				((GeneralPath) tempShape).moveTo(xx.get(0), yy.get(0));

				for (int index = 1; index < yy.size(); index++) {
					((GeneralPath) tempShape).lineTo(xx.get(index), yy.get(index));
				}

				espece.setObject(tempShape);

			}

			AffineTransform rotator = null;
			if (espece.angle != 0) {
				double angle = Math.toRadians(espece.angle);
				rotator = new AffineTransform();
				Rectangle2D rectangle2D = tempShape.getBounds2D();
				rotator.rotate(angle, rectangle2D.getCenterX(), rectangle2D.getCenterY());
			}
			Shape shape;

			if (espece.angle == 0)
				shape = stroke.createStrokedShape(tempShape);
			else
				shape = rotator.createTransformedShape(stroke.createStrokedShape(tempShape));

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
		return PrototypeGraphique.TYPE_GRAPHIQUE.CHEMIN;
	}

}
