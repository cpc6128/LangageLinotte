package org.linotte.greffons.impl.swing;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;

import org.linotte.greffons.externe.Composant;

/**
 * 
 * @author R.M
 * 
 */
public class GroupeBoutonRadio extends Composant {

	private ButtonGroup groupCheckBox;

	@Override
	public void initialisation() throws GreffonException {
		if (groupCheckBox != null) {
			return;
		}
		groupCheckBox = new ButtonGroup();
	}

	@Override
	public void ajouterComposant(Composant pcomposant) throws GreffonException {
		ComposantSwing composant = (ComposantSwing) pcomposant;
		JRadioButton jRadioButton = (JRadioButton) composant.getJComponent();
		if (!groupCheckBox.getElements().hasMoreElements()) {
			//jRadioButton.setSelected(true);
		}
		groupCheckBox.add(jRadioButton);
	}

	@Override
	public void destruction() throws GreffonException {
		if (groupCheckBox != null) {
			groupCheckBox = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

}
