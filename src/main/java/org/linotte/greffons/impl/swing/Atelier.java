package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

public class Atelier extends Composant {

	public Atelier() {
	}

	@Override
	public void initialisation() throws GreffonException {

	}

	@Override
	public void ajouterComposant(final Composant pcomposant) throws GreffonException {
		ComposantSwing composant = (ComposantSwing) pcomposant;

		if (composant instanceof Extension) {
			Extension extension = (Extension) composant;
			org.linotte.frame.Atelier.ajouteExtension(composant.getJComponent(), extension.getTitre());
		}
	}

}
