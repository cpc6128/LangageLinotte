package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

import javax.swing.*;

/**
 * 
 * @author R.M
 * 
 */
public class Barre extends ComposantDeplacable {

	private JToolBar barre;

	@Override
	public void initialisation() throws GreffonException {
		if (barre != null) {
			initEvenement();
			return;
		}
		setVisible(getAttributeAsString("visible").equals("oui"));
		barre = new JToolBar();
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		barre.setVisible(isVisible());
		initEvenement();
	}

	@Override
	public void ajouterComposant(Composant pcomposant) throws GreffonException {
		ComposantSwing composant = (ComposantSwing) pcomposant;

		if (composant instanceof Bouton) {
			barre.add((JButton) composant.getJComponent());
		}

	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return barre;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
		}
		return false;
	}

	@Override
	public void initEvenement() throws GreffonException {
		clic_souris.clear();
		souris_entrante.clear();

	}

	@Override
	public void destruction() throws GreffonException {
		if (barre != null) {
			barre = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@Slot(nom = "séparateur")
	public boolean separateur() {
		barre.addSeparator();
		return true;
	}

	@Slot(nom = "espace")
	public boolean espace(int largeur) {
		barre.add(Box.createHorizontalStrut(largeur));
		return true;
	}

}
