package org.linotte.greffons.impl.swing;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

/**
 * 
 * @author R.M
 * 
 */
public abstract class ComposantDeplacable extends ComposantSwing {

	protected int x, y;

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			Component composant = getJComponent();

			if (composant != null && "x".equals(clef)) {
				setX(((BigDecimal) ((Acteur) getAttribute(clef)).getValeur()).intValue());
				ComposantSwing pere = (ComposantSwing) getParent();
				if (pere != null) {
					Component comp = pere.getJComponent();
					JPanel jpanel = (JPanel) comp;
					if (jpanel != null) {
						MigLayout layout = (MigLayout) jpanel.getLayout();
						String contrainte = "pos " + getX() + " " + getY();
						// System.out.println(contrainte);
						layout.setComponentConstraints(composant, contrainte);
						composant.invalidate();
						composant.getParent().validate();
					}
				}
				return true;
			}
			if (composant != null && "y".equals(clef)) {
				setY(((BigDecimal) ((Acteur) getAttribute(clef)).getValeur()).intValue());
				ComposantSwing pere = (ComposantSwing) getParent();
				if (pere != null) {
					Component comp = pere.getJComponent();
					JPanel jpanel = (JPanel) comp;
					if (jpanel != null) {
						MigLayout layout = (MigLayout) jpanel.getLayout();
						String contrainte = "pos " + getX() + " " + getY();
						// System.out.println(contrainte);
						layout.setComponentConstraints(composant, contrainte);
						composant.invalidate();
						composant.getParent().validate();
					}
				}
				return true;
			}
		}
		return false;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public void setX(int px) {
		x = px;
	}

	public void setY(int py) {
		y = py;
	}

}
