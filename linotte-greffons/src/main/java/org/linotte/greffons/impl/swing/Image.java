package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author R.M
 * 
 */
public class Image extends ComposantDeplacable {

	private JLabel image;
	private int hauteur;
	private int largeur;
	private Map<String, ImageIcon> cacheImage = new HashMap<String, ImageIcon>();

	@Override
	public void initialisation() throws GreffonException {
		if (image != null) {
			super.initEvenement();
			return;
		}
		image = new JLabel();
		setVisible(getAttributeAsBigDecimal("visible").intValue()==1);

		// Dimensions :
		hauteur = getAttributeAsBigDecimal("hauteur").intValue();
		largeur = getAttributeAsBigDecimal("largeur").intValue();

		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();

		// Image :
		String source = getAttributeAsString("source");
		chargerImage(source);

		image.setPreferredSize(new Dimension(largeur, hauteur));
		image.setMaximumSize(new Dimension(largeur, hauteur));
		image.setVisible(isVisible());

		super.initEvenement();
	}

	/**
	 * @param hauteur
	 * @param largeur
	 * @param source
	 */
	private void chargerImage(String source) {
		source = getRessourceManager().analyserChemin(source);
		ImageIcon ii = cacheImage.get(source);
		if (ii == null)
			try {
				BufferedImage img = ImageIO.read(new File(source));
				if (img != null) {
					ii = new ImageIcon(img.getScaledInstance(largeur, hauteur, java.awt.Image.SCALE_SMOOTH));
					cacheImage.put(source, ii);
				}
			} catch (IOException e1) {
			}
		if (ii != null)
			image.setIcon(ii);
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return image;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (image != null && "source".equals(clef)) {
				String source = getAttributeAsString("source");
				chargerImage(source);
				return true;
			}
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (image != null) {
			image = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}
}
