/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                             *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                        *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                            *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.xml.appels;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.alize.kernel.Job;

import java.util.HashMap;

public class SousParagraphe implements Appel {

	private Boucle boucle;

	public int position;

	public SousParagraphe(int position) {
		this.position = position;
	}

	public SousParagraphe(Boucle boucle, int position) {
		super();
		this.boucle = boucle;
		this.position = position;
	}

	// Contient les acteurs locaux 
	private HashMap<Chaine, Acteur> acteurs_map;

	public void addActeurLocal(Acteur ac) {
		initDoublure();
		acteurs_map.put(ac.getNom(), ac);
	}

	public Acteur getActeurLocal(Chaine ac) {
		if (acteurs_map == null)
			return null;
		return acteurs_map.get(ac);
	}

	public boolean next(Job moteur) throws Exception {
		if (boucle == null)
			return false;
		else
			return boucle.suiteBoucle(moteur);
	}

	public boolean isBoucle() {
		return boucle != null;
	}

	public void viderActeurs() {
		if (acteurs_map != null)
			acteurs_map.clear();
	}

	@Override
	public String toString() {
		return "SousParagraphe";
	}

	@Override
	public APPEL getType() {
		return APPEL.SOUSPARAGRAPHE;
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