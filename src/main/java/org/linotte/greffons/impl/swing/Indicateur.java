package org.linotte.greffons.impl.swing;

import java.awt.Dimension;
import java.math.BigDecimal;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import org.linotte.greffons.externe.Composant;

/**
 * 
 * @author R.M
 * 
 */
public class Indicateur extends ComposantDeplacable {

	JProgressBar progressBar;
	int min;
	int max;
	int valeur;
	int largeur, hauteur;
	boolean orientation_horizontal = true;
	private static final String HORIZONTALE = "horizontal";

	@Override
	public void initialisation() throws GreffonException {
		if (progressBar != null) {
			return;
		}
		setVisible(getAttributeAsString("visible").equals("oui"));
		progressBar = new JProgressBar();
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		largeur = getAttributeAsBigDecimal("largeur").intValue();
		hauteur = getAttributeAsBigDecimal("hauteur").intValue();
		valeur = getAttributeAsBigDecimal("valeur").intValue();
		min = getAttributeAsBigDecimal("minimum").intValue();
		max = getAttributeAsBigDecimal("maximum").intValue();
		progressBar.setMaximum(max);
		progressBar.setMinimum(min);
		progressBar.setPreferredSize(new Dimension(largeur, hauteur));
		progressBar.setMinimum(min);

		// sens
		String sorientation = getAttributeAsString("orientation");
		if (sorientation == null)
			sorientation = HORIZONTALE;
		else
			sorientation = sorientation.toLowerCase();
		orientation_horizontal = sorientation.contains(HORIZONTALE);
		if (orientation_horizontal) {
			progressBar.setOrientation(SwingConstants.HORIZONTAL);
		} else {
			progressBar.setOrientation(SwingConstants.VERTICAL);
		}

		progressBar.setValue(valeur);

		progressBar.setVisible(isVisible());
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return progressBar;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (progressBar != null && "maximum".equals(clef)) {
				progressBar.setMaximum(((BigDecimal) ((Acteur) getAttribute(clef)).getValeur()).intValue());
				return true;
			}
			if (progressBar != null && "minimum".equals(clef)) {
				progressBar.setMinimum(((BigDecimal) ((Acteur) getAttribute(clef)).getValeur()).intValue());
				return true;
			}
			if (progressBar != null && "valeur".equals(clef)) {
				progressBar.setValue(((BigDecimal) ((Acteur) getAttribute(clef)).getValeur()).intValue());
				return true;
			}
			if (progressBar != null && "largeur".equals(clef)) {
				largeur = ((BigDecimal) ((Acteur) getAttribute(clef)).getValeur()).intValue();
				progressBar.setValue(largeur);
				progressBar.setPreferredSize(new Dimension(largeur, hauteur));
				return true;
			}
			if (progressBar != null && "hauteur".equals(clef)) {
				hauteur = ((BigDecimal) ((Acteur) getAttribute(clef)).getValeur()).intValue();
				progressBar.setValue(hauteur);
				progressBar.setPreferredSize(new Dimension(largeur, hauteur));
				return true;
			}
			if (progressBar != null && "orientation".equals(clef)) {
				orientation_horizontal = HORIZONTALE.equalsIgnoreCase((String) ((Acteur) getAttribute(clef)).getValeur());
				if (orientation_horizontal) {
					progressBar.setOrientation(SwingConstants.HORIZONTAL);
				} else {
					progressBar.setOrientation(SwingConstants.VERTICAL);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (progressBar != null) {
			//menu.dispose();
			progressBar = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

}