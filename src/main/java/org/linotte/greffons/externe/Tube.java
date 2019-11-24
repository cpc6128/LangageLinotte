package org.linotte.greffons.externe;

/**
 * Ce greffon permet de créer des tubes
 * 
 * @author Ronan Mounès
 * 
 */
public abstract class Tube extends Greffon {

	/**
	 * Tu ouvres EspeceTube avec "état" depuis "paramètres"
	 * 
	 * @param état
	 * @param paramètres
	 * @return
	 */
	public abstract boolean ouvrir(String état, String paramètres) throws GreffonException;

	/**
	 * Tu fermes EspeceTube
	 * 
	 * @param état
	 * @param paramètres
	 * @return
	 */
	public abstract boolean fermer() throws GreffonException;

	/**
	 * Tu charges Acteur dans EspeceTube
	 * 
	 * @param parametre
	 * @param valeurs
	 * @return
	 */
	public abstract boolean charger(ObjetLinotte valeurs) throws GreffonException;

	/**
	 * Tu charges Acteur dans EspeceTube avec "paramètres"
	 * 
	 * @param paramètres
	 * @param valeurs
	 * @return
	 */
	public abstract boolean charger(String paramètres, ObjetLinotte valeurs) throws GreffonException;

	/**
	 * Tu décharges Acteur depuis EspeceTube
	 * 
	 * @return
	 */
	public abstract ObjetLinotte decharger(ObjetLinotte structure) throws GreffonException;

	/**
	 * Tu décharges Acteur depuis EspeceTube avec "paramètres"
	 * 
	 * @param paramètres
	 * @return
	 */
	public abstract ObjetLinotte decharger(String paramètres, ObjetLinotte structure) throws GreffonException;

	/**
	 * Tu configures Acteur avec "paramètres"
	 * 
	 * @param paramètres
	 * @return
	 */
	public abstract boolean configurer(String paramètres) throws GreffonException;

}
