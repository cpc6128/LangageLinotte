package org.linotte.frame.cahier.timbre.ui.i;

import java.awt.Shape;

import org.linotte.frame.cahier.timbre.entite.Timbre;

public interface TimbreUI extends UI {

	Shape forme();

	/**
	 * Est-ce que ce compostant peut être attaché au composant timbreGraphiqueCible
	 * @param timbreGraphique cible a attacher
	 * @return
	 */
	boolean accepteZoneTimbre(Timbre timbreGraphiqueCible);

	Shape ancreArriere();

	Shape ancreAvant();

	void repositionnerTousLesTimbres();

}
