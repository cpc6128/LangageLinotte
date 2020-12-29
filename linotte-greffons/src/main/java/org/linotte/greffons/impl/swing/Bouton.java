package org.linotte.greffons.impl.swing;

import org.linotte.frame.latoile.Couleur;
import org.linotte.greffons.externe.Composant;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
public class Bouton extends ComposantDeplacable {

	private Map<String, ImageIcon> cacheImage = new HashMap<String, ImageIcon>();
	private JButton button;

	@SuppressWarnings("serial")
	@Override
	public void initialisation() throws GreffonException {
		if (button != null) {
			super.initEvenement();
			return;
		}
		setVisible(getAttributeAsString("visible").equals("oui"));
		String texte = getAttributeAsString("texte");
		String touche = getAttributeAsString("touche");
		String icone = getAttributeAsString("icône");
		button = new JButton(texte);
		super.initaccessibilite(button);
		chargerImage(icone);
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		// initEvenement();
		String couleur = getAttributeAsString("couleurtexte");
		Color c = Couleur.retourneCouleur(couleur);
		button.setForeground(c);

		String couleurfond = getAttributeAsString("couleurfond");
		if (couleurfond != null && couleurfond.trim().length() > 0) {
			Color c2 = Couleur.retourneCouleur(couleurfond);
			button.setOpaque(true);
			button.setBackground(c2);
		} else
			button.setOpaque(false);

		if (touche != null && touche.trim().length() == 1) {
			button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(touche.charAt(0)), "presser");
			button.getActionMap().put("presser", new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (ListenerGreffons listener : clic_souris) {
						listener.execute();
					}
				}
			});
		}

		button.setVisible(isVisible());
		super.initEvenement();
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return button;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (button != null && "texte".equals(clef)) {
				button.setText((String) ((Acteur) getAttribute(clef)).getValeur());
				return true;
			}
			if (button != null && "icône".equals(clef)) {
				chargerImage(getAttributeAsString(clef));
				return true;
			}
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (button != null) {
			// menu.dispose();
			button = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
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
			button.setIcon(ii);
	}

}
