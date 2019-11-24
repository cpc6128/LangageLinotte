package org.linotte.moteur.xml.appels;

import java.util.HashMap;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.outils.Chaine;

public class CalqueParagraphe implements Appel {

	public int position;

	// Pour le debogage
	public String nomFonction;

	public CalqueParagraphe(int position, String nomFonction) {
		this.position = position;
		this.nomFonction = nomFonction;
	}

	// Contient les acteurs locaux 
	private HashMap<Chaine, Acteur> acteurs_map;

	public void addActeurLocal(Acteur ac) {
		initDoublure();
		acteurs_map.put(ac.getNom(), ac);
	}

	public void addActeurLocalDoublure(Chaine nom, Acteur ac) {
		initDoublure();
		acteurs_map.put(nom, ac);
	}

	public Acteur getActeurLocal(Chaine ac) {
		if (acteurs_map == null)
			return null;
		return acteurs_map.get(ac);
	}

	@Override
	public String toString() {
		return "CalqueParagraphe";
	}

	@Override
	public APPEL getType() {
		return APPEL.CALQUEPARAGRAPHE;
	}

	private void initDoublure() {
		if (acteurs_map == null) {
			acteurs_map = new HashMap<Chaine, Acteur>(5);
		}
	}

	public HashMap<Chaine, Acteur> debogage() {
		return acteurs_map;
	}

}