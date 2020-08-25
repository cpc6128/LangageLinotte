package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.math.BigDecimal;

/**
 * 
 * @author R.M
 * 
 */
public class Slider extends ComposantDeplacable {

	JSlider slider;
	private ChangeListener changeListener;

	@Override
	public void initialisation() throws GreffonException {
		if (slider != null) {
			//initEvenement();
			return;
		}
		int min, max, valeur;
		setVisible(getAttributeAsString("visible").equals("oui"));
		slider = new JSlider();
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		valeur = getAttributeAsBigDecimal("valeur").intValue();
		min = getAttributeAsBigDecimal("minimum").intValue();
		max = getAttributeAsBigDecimal("maximum").intValue();
		slider.setMaximum(max);
		slider.setMinimum(min);
		slider.setValue(valeur);
		slider.setSnapToTicks(true);
		slider.setVisible(isVisible());
		initEvenement();
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return slider;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (slider != null && "maximum".equals(clef)) {
				slider.setMaximum(((BigDecimal) ((Acteur) getAttribute(clef)).getValeur()).intValue());
				return true;
			}
			if (slider != null && "minimum".equals(clef)) {
				slider.setMinimum(((BigDecimal) ((Acteur) getAttribute(clef)).getValeur()).intValue());
				return true;
			}
			if (slider != null && "valeur".equals(clef)) {
				slider.setValue(((BigDecimal) ((Acteur) getAttribute(clef)).getValeur()).intValue());
				return true;
			}
		}
		return false;
	}

	@Override
	public void initEvenement() throws GreffonException {
		clic_souris.clear();
		souris_entrante.clear();

		slider.removeChangeListener(changeListener);
		if (changeListener == null) {
			changeListener = new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider) e.getSource();
					if (!source.getValueIsAdjusting()) {
						setValeur("valeur", new BigDecimal(source.getValue()));
						for (ListenerGreffons listener : clic_souris) {
							listener.execute();
						}
					}
				}
			};
			slider.addChangeListener(changeListener);
		}
		//super.initEvenement();
	}

	@Override
	public void destruction() throws GreffonException {
		if (slider != null) {
			//menu.dispose();
			slider = null;
			changeListener = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}
}
