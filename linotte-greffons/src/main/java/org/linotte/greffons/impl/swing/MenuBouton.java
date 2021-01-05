package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

import javax.swing.*;

/**
 * 
 * @author R.M
 * 
 */
public class MenuBouton extends ComposantSwing {

	JButton button;

	@Override
	public void initialisation() throws GreffonException {

		if (button != null) {
			initEvenement();
			return;
		}

		setVisible(getAttributeAsString("visible").equals("oui"));

		String texte = getAttributeAsString("texte");
		button = new JButton(texte);
		super.initaccessibilite(button);

		initEvenement();
		button.setVisible(isVisible());
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

}
