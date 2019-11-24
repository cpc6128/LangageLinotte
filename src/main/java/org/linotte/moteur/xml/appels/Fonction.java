package org.linotte.moteur.xml.appels;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Paragraphe;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.FonctionDoublureException;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;

public class Fonction implements Appel {

	private Processus processusRetour;

	private Paragraphe paragraphe;

	private Boucle boucle;

	private Acteur[] doublure = null;

	private int taille = 0;

	public Fonction(Boucle boucle, Processus processus, Paragraphe paragraphe) {
		super();
		this.boucle = boucle;
		this.paragraphe = paragraphe;
		this.processusRetour = processus;
	}

	public Fonction(Processus processus, Paragraphe paragraphe) {
		super();
		this.processusRetour = processus;
		this.paragraphe = paragraphe;
	}

	public Fonction(Processus processus, Paragraphe paragraphe, Acteur[] acteurs) {
		super();
		this.processusRetour = processus;
		this.paragraphe = paragraphe;
		this.doublure = acteurs;
		calculerTaille();
	}

	public Fonction(Boucle boucle, Processus processus, Paragraphe paragraphe, Acteur[] acteurs) {
		this.boucle = boucle;
		this.paragraphe = paragraphe;
		this.processusRetour = processus;
		this.doublure = acteurs;
		calculerTaille();
	}

	public Paragraphe getParagraphe() {
		return paragraphe;
	}

	public Boucle getBoucle() {
		return boucle;
	}

	public Acteur getDoublure(int i) throws ErreurException {
		if (doublure == null)
			return null;
		if (i >= doublure.length)
			throw new FonctionDoublureException(Constantes.ERREUR_DOUBLURE);
		else {
			// Traitement particulier pour le joker :
			// Il faut toujours récupérer la valeur calculée par la boucle (issue 156).
			if (doublure[i] == null) {
				throw new FonctionDoublureException(Constantes.ERREUR_DOUBLURE);
			}
			Acteur retour = null;
			if (doublure[i].isJokerDunCasier() && doublure[i].boucleMere.retourJoker() != null) {
				retour = doublure[i].boucleMere.retourJoker();
			}
			if (retour == null) {
				retour = doublure[i];
			}
			return retour;
		}
	}

	@Override
	public String toString() {
		return "Fonction";
	}

	public Processus getProcessusRetour() {
		return processusRetour;
	}

	@Override
	public APPEL getType() {
		return APPEL.FONCTION;
	}

	public int nombreParametres() {
		return taille;
	}

	/**
	 * Précalculer la taille des paramètres. 
	 */
	private void calculerTaille() {
		if (doublure != null) {
			// Cette gestion est à revoir...
			for (Acteur acteur : doublure) {
				if (acteur != null)
					taille++;
				else
					break;
			}

		}
	}

}