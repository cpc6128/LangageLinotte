package org.linotte.greffons.externe;

/**
 * Ce greffon permet de créer des composants de l'Interface Utilisateur Linotte
 * 
 * Pattern Composant
 * 
 * @author Ronan Mounès
 * 
 */
public abstract class Composant extends Greffon {

	/**
	 * Initialisation du composant
	 * Méthode appelée lors de l'utilisation du verbe Ajouter
	 * 
	 * @param espece
	 * @throws GreffonException
	 */
	public abstract void initialisation() throws GreffonException;

	/**
	 * Descruction du composant
	 * 
	 * @param espece
	 * @throws GreffonException
	 */
	public void destruction() throws GreffonException {

	}

	/**
	 * Ajoute un composant dans un autre
	 * Méthode appelée lors de l'utilisation du verbe Ajouter
	 * 
	 * @param espece
	 * @param composant
	 * @throws GreffonException
	 */
	public abstract void ajouterComposant(Composant composant) throws GreffonException;

	/**
	 * Retourner true si linotte doit appeler la méthode destruction à la fin
	 * d'un livre
	 * 
	 * @return
	 */
	public boolean enregistrerPourDestruction() {
		return false;
	}

	/*
	 * Listeners pour l'ajout
	 */
	public void addClicListener(ListenerGreffons listener) {
	}

	public void addClicDroitListener(ListenerGreffons listener) {
	}

	public void addEnterListeners(ListenerGreffons listener) {
	}

	public void ajouterSortieListeners(ListenerGreffons listener) {
	}

	public void ajouterDoubleCliqueListeners(ListenerGreffons listener) {
	}

	public void ajouterDragAndDropListeners(ListenerGreffons listener) {
	}

	public void ajouterToucheListeners(ListenerGreffons listener) {
	}

	/*
	 * Listeners pour suppression
	 */

	public void cleanClicListener() {
	}

	public void cleanClicDroitListener() {
	}

	public void cleanEnterListeners() {
	}

	public void cleanSortieListeners() {
	}

	public void cleanDoubleCliqueListeners() {
	}

	public void cleanDragAndDropListeners() {
	}

	public void cleanToucheListeners() {
	}

}
