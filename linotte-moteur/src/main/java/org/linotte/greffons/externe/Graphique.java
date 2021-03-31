package org.linotte.greffons.externe;

import java.awt.*;

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

}
