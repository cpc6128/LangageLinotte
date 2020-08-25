/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 15, 2009                                *
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
import org.linotte.moteur.entites.Monolithe;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.ErreurException;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class MegalitheDessinateur implements Dessinateur {

	/**
	 * 
	 */

	@SuppressWarnings("unchecked")
	public void projette(LaToile toile, Graphics2D g2, PrototypeGraphique espece) throws ErreurException {

		if (espece.getObject() != null) {

			List<Monolithe> megalithes = (List<Monolithe>) espece.getObject();
			List<Shape> shapes = (List<Shape>) espece.getObject2();

			int premier = 0;
			int dernier = megalithes.size() - 1;

			if (espece.isModifie() || shapes == null) {
				espece.setModifie(false);
				// Translation :
				AffineTransform translation = new AffineTransform();
				translation.translate(espece.x, espece.y);

				Area avant_rotation = new Area();
				Area apres_rotation = new Area();
				shapes = new ArrayList<Shape>();

				for (int i = premier; i <= dernier; i++) {
					Monolithe megalithe = megalithes.get(i);
					Shape t = translation.createTransformedShape(megalithe.getShape());
					shapes.add(t);
					avant_rotation.add(new Area(t));
				}

				AffineTransform rotator = null;
				if (espece.angle != 0) {
					double angle = Math.toRadians(espece.angle);
					rotator = new AffineTransform();
					Rectangle2D rectangle2D = avant_rotation.getBounds2D();
					rotator.rotate(angle, rectangle2D.getCenterX(), rectangle2D.getCenterY());
					for (int i = 0; i < shapes.size(); i++) {
						Shape t = rotator.createTransformedShape(shapes.get(i));
						shapes.set(i, t);
						apres_rotation.add(new Area(t));
					}
					espece.setShape(apres_rotation);
				} else
					espece.setShape(avant_rotation);

				espece.setObject2(shapes);

			}

			if (g2 != null) {

				float transparence = espece.transparence;
				if (transparence > 1 || transparence < 0)
					transparence = 1;
				// Drag n drop plus sympa
				if (toile.getElementDragAndDrop() == espece)
					transparence = transparence / 2;
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparence));

				for (int i = premier; i <= dernier; i++) {
					Monolithe megalithe = megalithes.get(i);
					g2.setColor(megalithe.getColor());
					Shape p = shapes.get(i - premier);
					if (megalithe.getImage() != null) {
						BufferedImage image = megalithe.getImage();
						TexturePaint texturePaint = new TexturePaint(image, new Rectangle2D.Double(p.getBounds().x, p.getBounds().y, image.getWidth(),
								image.getHeight()));
						g2.setPaint(texturePaint);
					}
					g2.fill(p);
				}
			}
		}

	}

	public PrototypeGraphique.TYPE_GRAPHIQUE clef() {
		return PrototypeGraphique.TYPE_GRAPHIQUE.MEGALITHE;
	}

}
