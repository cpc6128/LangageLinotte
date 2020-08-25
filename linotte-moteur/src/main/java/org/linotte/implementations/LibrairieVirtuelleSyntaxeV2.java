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

package org.linotte.implementations;

import org.linotte.frame.latoile.LaToile;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.api.Librairie;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LibrairieVirtuelleSyntaxeV2 implements Librairie<LibrairieVirtuelleSyntaxeV2> {

	private Map<Chaine, Acteur> librairie;

	private Map<Chaine, Acteur> acteurs_pour_espece_map = new Hashtable<Chaine, Acteur>(100);

	private Map<String, Acteur> espece_map = new Hashtable<String, Acteur>(100);

	private LaToile toilePrincipale;

	private Map<String, LaToile> toiles = new Hashtable<String, LaToile>(1);

	public LibrairieVirtuelleSyntaxeV2() {
		librairie = new ConcurrentHashMap<Chaine, Acteur>(50);
	}

	public void mettre(Acteur a) throws ErreurException {
		// Gestion des fichiers :
		if (a.retourneFichier() != null) {
			a.retourneFichier().ecrire(a);
		} else
			librairie.put(a.getNom(), a);
	}

	@Override
	public void supprime(Acteur a) throws ErreurException {
		librairie.remove(a.getNom());
	}

	public void reCharger(Acteur a) throws ErreurException {
		// Gestion des fichiers :
		if (a.retourneFichier() != null) {
			a.retourneFichier().lire(a);
		}
	}

	public void addActeurPourEspece(Acteur ac) {
		acteurs_pour_espece_map.put(ac.getNom(), ac);
	}

	public Acteur getActeurPourEspece(Chaine ac) {
		return (Acteur) acteurs_pour_espece_map.get(ac);
	}

	public Prototype creationEspece(String espece, String nom, String type_heritage) throws ErreurException {
		Prototype modele = (Prototype) espece_map.get(espece);
		if (modele == null)
			throw new ErreurException(Constantes.SYNTAXE_ESPECE_INCONNUE, espece);
		return modele.creationEspece(nom, null, type_heritage, this);

	}

	public void cleanEspece(Linotte linotte) {
		espece_map.clear();
		// On copie les espèces graphiques :
		Iterator<Prototype> i = linotte.especeModeleMap.iterator();
		while (i.hasNext()) {
			Prototype es = i.next();
			espece_map.put(es.getNom().toString(), es);
		}
	}

	public void addEspece(Prototype espece) throws ErreurException {
		espece_map.put(espece.getNom().toString(), espece);
	}

	public Prototype getEspece(String espece) {
		return (Prototype) espece_map.get(espece);
	}

	// Debuggage :

	public Map<String, Acteur> getActeurs() {
		Map<String, Acteur> list = new HashMap<String, Acteur>();
		Set<Chaine> set = librairie.keySet();
		for (Object object : set) {
			list.put(((Chaine) object).toString().toLowerCase(), (Acteur) librairie.get(object));
		}
		return list;
	}

	public void vider() {
		librairie.clear();
	}

	public LaToile getToile() {
		return toilePrincipale;
	}

	public void setToile(LaToile latoile) {
		toilePrincipale = latoile;
	}

	@Override
	public void ajouterValeur(Acteur pere, Acteur fils) {
	}

	@Override
	public LibrairieVirtuelleSyntaxeV2 cloneMoi() {
		synchronized (this) {
			LibrairieVirtuelleSyntaxeV2 copie = new LibrairieVirtuelleSyntaxeV2();
			copie.espece_map = new Hashtable<String, Acteur>(100);
			copie.espece_map.putAll(espece_map);
			copie.toilePrincipale = toilePrincipale;
			//copie.toiles = new Hashtable<String, LaToile>();
			return copie;
		}
	}

	@Override
	public Collection<LaToile> getToiles() {
		return toiles.values();
	}

	@Override
	public LaToile getToilePrincipale() {
		return toilePrincipale;
	}

	@Override
	public void ajouterToile(String clef, LaToile toile) {
		toiles.put(clef, toile);
	}

	@Override
	public LaToile recupererToile(String clef) {
		if (toiles.containsKey(clef))
			return toiles.get(clef);
		else
			return toilePrincipale;
	}

	@Override
	public boolean existeTElleToile(String clef) {
		return toiles.containsKey(clef);
	}

}