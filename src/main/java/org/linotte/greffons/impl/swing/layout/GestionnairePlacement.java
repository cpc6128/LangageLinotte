package org.linotte.greffons.impl.swing.layout;

import java.awt.LayoutManager;

import org.linotte.greffons.impl.swing.ComposantSwing;

public interface GestionnairePlacement {

	LayoutManager retourneGestionnairePlacement();

	void fairePere(ComposantSwing formulaire);

}
