package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Greffon;

/**
 * Exemple d'utilisation du greffon 
 * 
 * principale : 
 *         messager est un majordome, serviteur vaut "Ronan" 
 *           début
 *           affiche messager.présentation("Jules") 
 *
 * Et on obtient à l'affichage :
 * "Bonjour, mon cher Jules, je suis votre serviteur Ronan"
 * 
 */

public class Majordome extends Greffon {

	@Slot(nom = "présentation")
	public String presentation(String prenom) throws GreffonException {
		String serviteur = getAttributeAsString("serviteur");
		return "Bonsoir, mon cher " + prenom + ", je suis votre serviteur " + serviteur;
	}

}
