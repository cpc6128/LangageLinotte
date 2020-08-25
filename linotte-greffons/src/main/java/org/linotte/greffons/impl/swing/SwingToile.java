package org.linotte.greffons.impl.swing;

import org.linotte.frame.latoile.LaToileJDialog;
import org.linotte.frame.latoile.LaToileListener;
import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.externe.Composant;

import javax.swing.*;

/**
 * 
 * @author R.M
 * 
 */
public class SwingToile extends ComposantDeplacable {

	private LaToileListener champ;
	private LaToileJDialog toile = null;

	@Override
	public void initialisation() throws GreffonException {
		if (champ != null) {
			super.initEvenement();
			return;
		}
		setVisible(getAttributeAsString("visible").equals("oui"));
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		String nomtoile = getAttributeAsString("toile");

		toile = (LaToileJDialog) LinotteFacade.getToile(nomtoile);
		toile.setAffichageToileDeleguee(true);

		champ = toile.getPanelLaToile();
		champ.setVisible(isVisible());

		super.initEvenement();
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return champ;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		toile.setAffichageToileDeleguee(false);
		if (champ != null) {
			removeMouseListener();
			toile.getContentPane().add(champ);
			champ = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}
}
