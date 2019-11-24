/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                           *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.entites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linotte.moteur.outils.Chaine;

public class Paragraphe {

	private String nom = null;

	private Map<Chaine, Acteur> acteurs_map;// = new HashMap<Chaine,
											// Acteur>(10);

	// Pour connaître la position de l'acteur :
	private List<String> nom_doublure_acteur;

	public Paragraphe() {
	}

	public Paragraphe(String pnom) {
		nom = pnom;
	}

	public void addActeur(Acteur ac) {
		initDoublure();
		acteurs_map.put(ac.getNom(), ac);
	}

	public Map<Chaine, Acteur> getActeurs() {
		initDoublure();
		return acteurs_map;
	}

	public void addDoublure(String nom, Acteur ac) {
		initDoublure();
		acteurs_map.put(Chaine.produire(nom), ac);
		if (!nom_doublure_acteur.contains(nom))
			nom_doublure_acteur.add(nom);
	}

	public int getPosition(String nom) {
		initDoublure();
		int r = nom_doublure_acteur.indexOf(nom.toLowerCase());
		if (r == -1) {
			return nom_doublure_acteur.size();
		} else {
			return r;
		}
	}

	public void addEspece(Prototype ac) {
		initDoublure();
		acteurs_map.put(ac.getNom(), ac);
	}

	public Acteur getActeur(Chaine ac) {
		if (acteurs_map == null)
			return null;
		return acteurs_map.get(ac);
	}

	public void removeActeur(Chaine ac) {
		initDoublure();
		acteurs_map.remove(ac);
	}

	public String getNom() {
		return nom;
	}

	@Override
	public String toString() {
		return nom;
	}

	@Override
	public Object clone() {
		Paragraphe paragraphe = new Paragraphe();
		paragraphe.nom = nom;
		// paragraphe.souffleurs = souffleurs;
		if (nom_doublure_acteur != null) {
			paragraphe.nom_doublure_acteur = new ArrayList<String>();
			paragraphe.nom_doublure_acteur.addAll(nom_doublure_acteur);
			paragraphe.acteurs_map = new HashMap<Chaine, Acteur>(10);
		}
		return paragraphe;
	}

	private void initDoublure() {
		if (acteurs_map == null) {
			acteurs_map = new HashMap<Chaine, Acteur>(10);
			nom_doublure_acteur = new ArrayList<String>();
		}
	}

}
