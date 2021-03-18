package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Graphique;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

/**
 * Ce greffon IMAGE offre des services en plus par rapport à l'espèce GRAPHIQUE
 * - Gestion des sprites
 * - Sélectionneur de fichier (selectionneretouvrir)
 * - Retourne la largeur et hauteur
 * Effets :
 * - négatif
 * TODO :
 * Faire documentation pour greffon
 * @author Cpc
 *
 */
public class ImageX extends Graphique {

	private Shape shape;

	// Cache pour optimiser les calculs :
	private Image image;

	private String nomImage;

	private static Outils outils = new Outils();;

	private static JFileChooser fileChooserOpen = null;

	private static JFileChooser fileChooserClose = null;

	static {
		fileChooserOpen = new JFileChooser();
		fileChooserClose = new JFileChooser();
	}

	// Pour la gestion des sprites :
	private int sp_dx = -1, sp_dy = -1, sp_largeur = -1, sp_hauteur = -1;

	public ImageX() {
	}

	public void projette(Graphics2D g2) throws GreffonException {

		try {
			if (g2 != null) {
				// Gestion de la transparence :
				float transparence = getAttributeAsBigDecimal("transparence").intValue() / 100f;
				if (transparence > 1 || transparence < 0)
					transparence = 1;
				// Drag n drop plus sympa
				//if (toile.getElementDragAndDrop() == espece)
				//	transparence = transparence / 2;
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparence));
			}

			// Fichier image :
			String fichier = getAttributeAsString("image");

			chargerImage(fichier);

			if (image == null)
				return;

			// Gestion du sprite
			int n_sp_dx = getAttributeAsBigDecimal("dx").intValue();
			int n_sp_dy = getAttributeAsBigDecimal("dy").intValue();
			int n_sp_largeur = getAttributeAsBigDecimal("largeur").intValue();
			int n_sp_hauteur = getAttributeAsBigDecimal("hauteur").intValue();

			if (n_sp_dx != sp_dx || n_sp_dy != sp_dy || n_sp_largeur != sp_largeur || n_sp_hauteur != sp_hauteur) {
				sp_dx = n_sp_dx;
				sp_dy = n_sp_dy;
				sp_largeur = n_sp_largeur;
				sp_hauteur = n_sp_hauteur;
			}
			boolean sprite = !(n_sp_dx <= 0 && n_sp_dy <= 0);

			// Gestion de la position :
			double x = getAttributeAsBigDecimal("x").doubleValue();
			double y = getAttributeAsBigDecimal("y").doubleValue();

			if (!sprite) {
				double largeur = image.getWidth(null);
				double hauteur = image.getHeight(null);

				// Gestion de la taille :
				float taille = getAttributeAsBigDecimal("taille").floatValue();

				// Gestion de la rotation :
				double rotation = getAttributeAsBigDecimal("angle").doubleValue();
				AffineTransform rotator = new AffineTransform();
				rotator.translate(x, y);
				if (taille != 0)
					rotator.scale((largeur + taille) / largeur, (hauteur + taille) / hauteur);
				if (rotation != 0) {
					double angle = Math.toRadians(rotation);
					rotator.rotate(angle, (largeur / 2), (hauteur / 2));
				}
				Shape shape = new Rectangle2D.Double(0, 0, largeur, hauteur);
				this.shape = rotator.createTransformedShape(shape);
				if (g2 != null) {
					g2.drawImage(image, rotator, null);
				}
			} else {
				// MODE SPRITE :
				AffineTransform translate = new AffineTransform();
				translate.translate(x, y);
				Shape shape = new Rectangle2D.Double(0, 0, sp_largeur, sp_hauteur);
				this.shape = translate.createTransformedShape(shape);
				if (g2 != null) {
					g2.drawImage(image, (int) x, (int) y, (int) (x + sp_largeur), (int) (y + sp_hauteur), (int) sp_dx, (int) sp_dy, (int) (sp_dx + sp_largeur),
							(int) (sp_dy + sp_hauteur), null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param fichier
	 * @return
	 */
	private void chargerImage(String fichier) {

		if (fichier != null) {
			if (image == null) {
				// On charge la première fois l'image
				image = outils.chargementImage(fichier);
				if (image != null) {
					nomImage = fichier;
				} else {
					nomImage = null;
					image = null;
				}
			} else if (!fichier.equals(nomImage)) {
				image = outils.chargementImage(fichier);
				if (image != null) {
					nomImage = fichier;
				} else {
					nomImage = null;
					image = null;
				}

			}
		} else {
			image = null;
			nomImage = null;
		}
	}

	@Override
	public Shape getShape() {
		return shape;
	}

	@Override
	public boolean fireProperty(String attribut) {
		// Forcer le recalcule de l'image :
		if (attribut.equals("hauteur") || attribut.equals("largeur")) {
			return true;
		}
		return false;
	}

	@Slot(nom = "négatif")
	public boolean negatif() throws GreffonException {
		chargerImage(getAttributeAsString("image"));
		if (image == null)
			throw new GreffonException("Pas d'image");
		image = outils.algoNegatif(image);
		return true;
	}

	@Slot(nom = "sélectionneretouvrir")
	public boolean selectionneretouvrir() throws GreffonException {
		int returnVal = fileChooserOpen.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File fichier = fileChooserOpen.getSelectedFile();
				setAttribute("image", fichier.getCanonicalPath());
				return true;
			} catch (Exception e1) {
			}
		}
		return false;
	}

	@Slot(nom = "sélectionneretenregistrer")
	public boolean selectionneretecrire() throws GreffonException {
		int returnVal = fileChooserClose.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				File fichier = fileChooserClose.getSelectedFile();
				nomImage = fichier.toString();
				setAttribute("image", nomImage);
				ImageIO.write(outils.toBufferedImage(image), "png", fichier);
			} catch (Exception e1) {
			}
		}
		return false;
	}

	@Slot()
	public int largeur() throws GreffonException {
		chargerImage(getAttributeAsString("image"));
		if (image == null)
			throw new GreffonException("Pas d'image");
		return image.getWidth(null);
	}

	@Slot()
	public boolean coller() throws GreffonException {
		nomImage = "presse-papier";
		setAttribute("image", nomImage);
		image = outils.getFromClipboard();
		if (image == null)
			throw new GreffonException("Pas d'image dans le presse-papier");
		getRessourceManager().setChangement();
		return true;
	}

	@Slot()
	public int hauteur() throws GreffonException {
		chargerImage(getAttributeAsString("image"));
		if (image == null)
			throw new GreffonException("Pas d'image");
		return image.getHeight(null);
	}

	/**
	 * @return
	 * @throws GreffonException
	 */
	@Slot(nom = "retournerouge")
	public int retourneRouge(int x, int y) throws GreffonException {
		try {
			int clr = getColor(x, y);
			int red = (clr & 0x00ff0000) >> 16;
			return red;
		} catch (Exception e) {
			throw new GreffonException("Pfff, la pipette ne fonctionne pas !");
		}
	}

	/**
	 * @return
	 * @throws GreffonException
	 */
	@Slot(nom = "retournevert")
	public int retourneVert(int x, int y) throws GreffonException {
		try {
			int clr = getColor(x, y);
			int green = (clr & 0x0000ff00) >> 8;
			return green;
		} catch (Exception e) {
			throw new GreffonException("Pfff, la pipette ne fonctionne pas !");
		}
	}

	/**
	 * @return
	 * @throws GreffonException
	 */
	@Slot(nom = "retournebleu")
	public int retourneBleu(int x, int y) throws GreffonException {
		try {
			int clr = getColor(x, y);
			int blue = clr & 0x000000ff;
			return blue;
		} catch (Exception e) {
			throw new GreffonException("Pfff, la pipette ne fonctionne pas !");
		}
	}

	/**
	 * @return
	 * @throws GreffonException
	 */
	@Slot(nom = "retournecouleur")
	public String retourneCouleur(int x, int y) throws GreffonException {
		try {
			int clr = getColor(x, y);
			int red = (clr & 0x00ff0000) >> 16;
			int green = (clr & 0x0000ff00) >> 8;
			int blue = clr & 0x000000ff;
			return red + " " + green + " " + blue;
		} catch (Exception e) {
			throw new GreffonException("Pfff, la pipette ne fonctionne pas !");
		}
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private int getColor(int x, int y) {
		chargerImage(getAttributeAsString("image"));
		int clr = ((BufferedImage) image).getRGB(x, y);
		return clr;
	}

	@Slot
	public boolean filtre(String filtre) throws GreffonException {
		chargerImage(getAttributeAsString("image"));
		if (image == null)
			throw new GreffonException("Pas d'image");
		image = outils.processImage(image, filtre);
		return true;
	}

	@Slot
	public List<String> filtres() throws GreffonException {
		return outils.filtres();
	}
}
