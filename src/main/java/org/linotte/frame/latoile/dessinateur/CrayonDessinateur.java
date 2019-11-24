/***********************************************************************
 * Linotte                                                             *
 * Version release date : November 25, 2008                            *
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
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.linotte.frame.latoile.LaToile;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Ressources;

public class CrayonDessinateur implements Dessinateur {

	private static Image imageTortue;

	static {
		imageTortue = Ressources.getImageIcon("applications-graphics.png").getImage();
	}

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

			Image image = (Image) espece.getObject();

			if (image == null) {
				image = creationImageVide(espece, toile, null);
			}

			// Vérifier si la toile n'a pas changé de taille :
			if (image.getHeight(null) != toile.getPanelLaToile().getHeight() && image.getWidth(null) != toile.getPanelLaToile().getWidth()) {
				image = creationImageVide(espece, toile, image);
			}

			if (g2 != null) {
				g2.drawImage(image, null, null);
				// Affichage tortue ?
				if (espece.pointe) {
					int x = (int) (espece.x - 2);
					int y = (int) (espece.y - 22);
					g2.drawImage(imageTortue, x, y, null);
				}
			}
		}

	}

	private static Image creationImageVide(PrototypeGraphique espece, LaToile toile, Image precedente) {
		Image image;
		int width = toile.getPanelLaToile().getWidth();
		int height = toile.getPanelLaToile().getHeight();
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		// Si changement de taille de la toile, on a agrandi la taille et on
		// recopie l'ancienne image
		if (precedente != null) {
			// System.out.println("Shrink !");
			g2d.drawImage(precedente, 0, 0, null);
		}

		image = bufferedImage;
		espece.setObject(image);
		Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, width, height);
		Area area = new Area(rect);
		area.reset();
		espece.setShape(area);
		return image;
	}

	public static void dessineLigne(PrototypeGraphique crayon, int x2, int y2) throws ErreurException {

		LaToile toile = crayon.getToile();

		if (crayon.pose) {
			BufferedImage bufferedImage = (BufferedImage) crayon.getObject();
			BufferedImage image = (BufferedImage) crayon.getObject2();

			if (bufferedImage == null) {
				bufferedImage = (BufferedImage) creationImageVide(crayon, toile, null);
			}
			Graphics2D g2 = (Graphics2D) bufferedImage.getGraphics();
			Stroke stroke = new BasicStroke(Math.abs(crayon.taille), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
			// Gestion de la position :
			double x1 = crayon.x;
			double y1 = crayon.y;

			Shape shape = stroke.createStrokedShape(new Line2D.Double(x1, y1, x2, y2));

			// Gestion des textures :
			String fichier = crayon.texture;
			if (crayon.texture != null && crayon.texture.length() > 0) {
				if (image == null) {
					// On charge la première fois l'image
					image = Ressources.toBufferedImage(Ressources.chargementImage(fichier));
					if (image != null) {
						crayon.setNomObject(fichier);
						crayon.setObject2(image);
					} else {
						crayon.setNomObject(null);
						crayon.setObject2(null);

					}
				} else if (!fichier.equals(crayon.getNomObject())) {
					image = Ressources.toBufferedImage(Ressources.chargementImage(fichier));
					if (image != null) {
						crayon.setNomObject(fichier);
						crayon.setObject2(image);
					} else {
						crayon.setNomObject(null);
						crayon.setObject2(null);
					}

				}
			} else {
				crayon.setNomObject(null);
				crayon.setObject2(null);
				image = null;
			}

			// Gestion de la couleur :
			g2.setColor(crayon.couleur);
			// On dessine :
			// Affichage de la texture :
			if (image != null) {
				TexturePaint texturePaint = new TexturePaint(image, new Rectangle2D.Double(crayon.x, crayon.y, image.getWidth(), image.getHeight()));
				g2.setPaint(texturePaint);
			}

			g2.fill(shape);

			// On ajoute pour la collision :
			if (crayon.collision && crayon.getShape() != null) {
				Area area = new Area(crayon.getShape());
				Area area2 = new Area(shape);
				area.add(area2);
				crayon.setShape(area);
			}
		}

	}

	public static void dessinePoint(PrototypeGraphique crayon, int x2, int y2, LaToile toile) throws ErreurException {

		BufferedImage bufferedImage = (BufferedImage) crayon.getObject();
		BufferedImage image = (BufferedImage) crayon.getObject2();
		if (bufferedImage == null) {
			bufferedImage = (BufferedImage) creationImageVide(crayon, toile, null);
		}
		Graphics2D g2 = (Graphics2D) bufferedImage.getGraphics();
		Stroke stroke = new BasicStroke(Math.abs(crayon.taille), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);

		Shape shape = stroke.createStrokedShape(new Line2D.Double(x2, y2, x2, y2));

		// Gestion des textures :
		String fichier = crayon.texture;
		if (crayon.texture != null && crayon.texture.length() > 0) {
			if (image == null) {
				// On charge la première fois l'image
				image = Ressources.toBufferedImage(Ressources.chargementImage(fichier));
				if (image != null) {
					crayon.setNomObject(fichier);
					crayon.setObject2(image);
				} else {
					crayon.setNomObject(null);
					crayon.setObject2(null);

				}
			} else if (!fichier.equals(crayon.getNomObject())) {
				image = Ressources.toBufferedImage(Ressources.chargementImage(fichier));
				if (image != null) {
					crayon.setNomObject(fichier);
					crayon.setObject2(image);
				} else {
					crayon.setNomObject(null);
					crayon.setObject2(null);
				}

			}
		} else {
			crayon.setNomObject(null);
			crayon.setObject2(null);
			image = null;
		}
		// Gestion de la couleur :
		g2.setColor(crayon.couleur);

		// Affichage de la texture :
		if (image != null) {
			TexturePaint texturePaint = new TexturePaint(image, new Rectangle2D.Double(crayon.x, crayon.y, image.getWidth(), image.getHeight()));
			g2.setPaint(texturePaint);
		}

		// On dessine :
		g2.fill(shape);

		// On ajoute pour la collision :
		if (crayon.collision && crayon.getShape() != null) {
			Area area = new Area(crayon.getShape());
			Area area2 = new Area(shape);
			area.add(area2);
			crayon.setShape(area);
		}

	}

	public PrototypeGraphique.TYPE_GRAPHIQUE clef() {
		return PrototypeGraphique.TYPE_GRAPHIQUE.CRAYON;
	}

}