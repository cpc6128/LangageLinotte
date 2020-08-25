package org.linotte.greffons.impl.swing;

import org.linotte.frame.latoile.Couleur;
import org.linotte.greffons.externe.Composant;

import javax.swing.*;
import java.awt.*;

/**
 * 
 * @author R.M
 * 
 */
public class Label extends ComposantDeplacable {

	JLabel label;

	@Override
	public void initialisation() throws GreffonException {
		if (label != null) {
			super.initEvenement();
			return;
		}
		setVisible(getAttributeAsString("visible").equals("oui"));
		String texte = getAttributeAsString("texte");
		label = new JLabel(texte);
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();

		label.setVisible(isVisible());

		float taille = getAttributeAsBigDecimal("taille").floatValue();
		label.setFont(label.getFont().deriveFont((float) (label.getFont().getSize() + taille)));

		String couleur = getAttributeAsString("couleurtexte");
		Color c = Couleur.retourneCouleur(couleur);
		label.setForeground(c);

		String couleurfond = getAttributeAsString("couleurfond");
		if (couleurfond != null && couleurfond.trim().length() > 0) {
			Color c2 = Couleur.retourneCouleur(couleurfond);
			label.setOpaque(true);
			label.setBackground(c2);
		} else
			label.setOpaque(false);

		super.initEvenement();
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return label;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (label != null && "texte".equals(clef)) {
				label.setText((String) ((Acteur) getAttribute(clef)).getValeur());
				return true;
			}
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (label != null) {
			// menu.dispose();
			label = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

}
