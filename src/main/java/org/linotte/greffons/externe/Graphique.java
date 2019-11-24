package org.linotte.greffons.externe;

import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * Ce greffon permet de créer un objet affichable sur la toile de Linotte
 * 
 * @author Ronan Mounès
 * 
 */
public abstract class Graphique extends Greffon {

	/**
	 * Affiche de l'objet dans la toile
	 * 
	 * @param g2 Graphics2D de la toile
	 * @param espece contenant les données du l'objet à afficher
	 * @throws GreffonException
	 */

	public abstract void projette(Graphics2D g2) throws GreffonException;

	/**
	 * Shape pour la gestion de la collision
	 * Peut être null (donc, pas de gestion de collision)
	 * 
	 * @return
	 */
	public abstract Shape getShape();

	/**
	 * n'est plus utilisée
	 * @param espece
	 * @param p1
	 * @param p2
	 * @param p3
	 * @throws GreffonException
	 */
	@Deprecated
	public void modifier(String p1, String p2, String p3) throws GreffonException {

	}

}
