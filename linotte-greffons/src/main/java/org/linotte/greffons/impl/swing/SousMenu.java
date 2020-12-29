package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

import javax.imageio.ImageIO;
import javax.swing.*;
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
public class SousMenu extends ComposantSwing {

	private Map<String, ImageIcon> cacheImage = new HashMap<String, ImageIcon>();
	private JMenuItem menu;

	@Override
	public void initialisation() throws GreffonException {
		if (menu != null) {
			super.initEvenement();
			menu.setVisible(isVisible());
			return;
		}
		setVisible(getAttributeAsString("visible").equals("oui"));

		String texte = getAttributeAsString("texte");
		String icone = getAttributeAsString("icône");
		menu = new JMenuItem(texte);
		super.initaccessibilite(menu);
		chargerImage(icone);
		menu.setVisible(isVisible());
		super.initEvenement();
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return menu;
	}

	@Override
	public void destruction() throws GreffonException {
		if (menu != null) {
			//menu.dispose();
			menu = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (menu != null && "texte".equals(clef) &&
			// Patch de mise à jour intenpestive
					!menu.getText().equals(((Acteur) getAttribute(clef)).getValeur())) {
				menu.setText((String) ((Acteur) getAttribute(clef)).getValeur());
				return true;
			}
		}
		return false;
	}

	/**
	 * @param source
	 */
	private void chargerImage(String source) {
		source = getRessourceManager().analyserChemin(source);
		ImageIcon ii = cacheImage.get(source);
		if (ii == null)
			try {
				BufferedImage img = ImageIO.read(new File(source));
				if (img != null) {
					ii = new ImageIcon(img);
					cacheImage.put(source, ii);
				}
			} catch (IOException e1) {
			}
		if (ii != null)
			menu.setIcon(ii);
	}
}
