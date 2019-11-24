package org.linotte.greffons.impl.swing;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.linotte.greffons.externe.Composant;

/**
 * 
 * @author R.M
 * 
 */
public class Scrolleur extends ComposantDeplacable {

	JScrollPane scrolleur;

	@Override
	public void initialisation() throws GreffonException {
		if (scrolleur != null) {
			super.initEvenement();
			return;
		}
		setVisible(getAttributeAsString("visible").equals("oui"));
		scrolleur = new JScrollPane();

		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		// Dimensions :
		int hauteur = getAttributeAsBigDecimal("hauteur").intValue();
		int largeur = getAttributeAsBigDecimal("largeur").intValue();
		scrolleur.setPreferredSize(new Dimension(largeur, hauteur));
		scrolleur.setMaximumSize(new Dimension(largeur, hauteur));

		super.initEvenement();
	}

	@Override
	public void ajouterComposant(Composant pcomposant) throws GreffonException {

		ComposantSwing composant = (ComposantSwing) pcomposant;

		ComposantDeplacable composantDeplacable = (ComposantDeplacable) composant;

		try {
			scrolleur.setViewportView(composantDeplacable.getJComponent());
		} catch (GreffonException e) {
			e.printStackTrace();
		}
		composantDeplacable.setParent(this);

	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return scrolleur;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (scrolleur != null) {
			scrolleur = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

}
