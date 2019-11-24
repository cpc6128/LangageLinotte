package org.linotte.frame.cahier.timbre.ui.i;

public interface UI {

	/**
	 * Pré traitement ou affichage du timbre avant la méthode <code>dessine(Planche UI)</code>
	 * @param planche
	 */
	void pre_dessine(PlancheUI planche);

	/**
	 * Dessine le timbre sur la planche
	 * @param pui
	 */
	void dessine(PlancheUI pui);

	/**
	 * @param x
	 * @param y
	 */
	boolean clique(int x, int y);

	/**
	 * Est-ce que le composant contient les coordonnées de la souris de l'utilisateur
	 * @param x
	 * @param y
	 * @return
	 */
	boolean contient(int x, int y);

	void positionnerProchainTimbre();

}
