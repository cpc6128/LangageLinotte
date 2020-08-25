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

package org.linotte.moteur.xml.appels;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.analyse.ItemXML;

import java.math.BigDecimal;
import java.util.HashMap;

public class Boucle implements Appel {

	private int boucle = 0;

	private Acteur laliste1 = null;

	private Acteur laliste2 = null;

	private Acteur ACTEUR = null;

	protected static Chaine joker = null;

	private boolean sens = true;

	// Pour la boucle FOR :

	private Acteur joker_for = null;

	private ItemXML item_for = null;

	private boolean initialisation = false;

	// true = +1
	// true = -1

	// Ajout de Alizé :

	private Processus processus = null;

	public Boucle(Processus courant, Acteur t) {
		laliste1 = t;
		boucle = 0;
		processus = courant;
	}

	public Boucle(Processus courant, Acteur t, Acteur t2) throws ErreurException {
		processus = courant;
		laliste1 = t;
		laliste2 = t2;
		boucle = ((BigDecimal) laliste1.getValeur()).intValue();
		if (boucle > ((BigDecimal) laliste2.getValeur()).intValue()) {
			sens = false;
		}
	}

	public Boucle(Processus courant, Acteur t, Acteur t2, Acteur a1) throws ErreurException {
		processus = courant;
		joker_for = a1;
		laliste1 = t;
		laliste2 = t2;
		boucle = ((BigDecimal) laliste1.getValeur()).intValue();
		if (boucle > ((BigDecimal) laliste2.getValeur()).intValue()) {
			sens = false;
		}
	}

	public Boucle(Processus courant, Acteur t, Acteur t2, Acteur a1, ItemXML item) throws Exception {
		processus = courant;
		joker_for = a1;
		laliste1 = t;
		laliste2 = t2;
		boucle = ((BigDecimal) laliste1.getValeur()).intValue();
		item_for = item;
		joker_for.setValeur(laliste1.getValeur());
	}

	public boolean suiteBoucle(Job moteur) throws Exception {

		if (laliste2 == null) {
			// Boucle "pour chaque"
			if (laliste1.getRole() == Role.CASIER) {
				Casier casier = (Casier) laliste1;
				ACTEUR = null;
				while (ACTEUR == null) {
					if (casier.taille() <= boucle) {
						return false;
					} else {
						Acteur temp = casier.retourne(boucle);
						boucle++;
						ACTEUR = temp;
						// issue 156
						temp.setJokerDunCasier(this);
					}
				}
				return true;
			} else if (laliste1.getRole() == Role.NOMBRE) {
				if (((BigDecimal) laliste1.getValeur()).intValue() <= boucle) {
					return false;
				} else {
					ACTEUR = new Acteur(null, joker, Role.NOMBRE, new BigDecimal(boucle), null);
					// linotte.getLivre().setJoker(ACTEUR);
					boucle++;
					// issue 156
					ACTEUR.setJokerDunCasier(this);
					return true;
				}
			} else if (laliste1.getRole() == Role.TEXTE) {
				if (((String) laliste1.getValeur()).length() <= boucle) {
					return false;
				} else {
					ACTEUR = new Acteur(null, joker, Role.TEXTE, Character.toString(((String) laliste1.getValeur()).charAt(boucle)), null);
					// linotte.getLivre().setJoker(ACTEUR);
					boucle++;
					// issue 156
					ACTEUR.setJokerDunCasier(this);
					return true;
				}
			}
		} else {
			// Boucle : De ... à
			if (laliste1.getRole() == Role.NOMBRE && laliste2.getRole() == Role.NOMBRE) {
				if (item_for != null) {
					if (!initialisation)
						initialisation = true;
					else
						joker_for.setValeur(item_for.retourneValeurTransformee(moteur));
					if ((((BigDecimal) laliste2.getValeur()).compareTo((BigDecimal) joker_for.getValeur())) < 0
							|| (((BigDecimal) laliste1.getValeur()).compareTo((BigDecimal) joker_for.getValeur())) > 0) {
						return false;
					} else
						return true;

				} else {
					if ((sens && ((BigDecimal) laliste2.getValeur()).intValue() <= (boucle - 1))
							|| (!sens && ((BigDecimal) laliste2.getValeur()).intValue() >= (boucle + 1))) {
						return false;
					} else {
						if (joker_for != null)
							joker_for.setValeur(new BigDecimal(boucle));
						else
							ACTEUR = new Acteur(null, joker, Role.NOMBRE, new BigDecimal(boucle), null);
						// linotte.getLivre().setJoker(ACTEUR);
						if (sens)
							boucle++;
						else
							boucle--;
						return true;
					}
				}

			} else {
				// TODO Lancer erreur
			}
		}
		return false;
	}

	public Acteur retourJoker() {
		return ACTEUR;
	}

	public static void setJoker(Chaine joker) {
		Boucle.joker = joker;
	}

	public boolean retire(Acteur acteur) throws ErreurException {
		if (laliste1.getRole() == Role.CASIER) {
			Casier casier = (Casier) laliste1;
			int pos = casier.oterActeur(acteur);
			if (pos > -1) {
				if (pos <= (boucle - 1)) {
					boucle--;
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "Boucle";
	}

	public Processus getProcessus() {
		return processus;
	}

	@Override
	public APPEL getType() {
		return APPEL.BOUCLE;
	}

	public HashMap<Chaine, Acteur> debogage() {
		HashMap<Chaine, Acteur> retour = new HashMap<Chaine, Acteur>();
		if (ACTEUR != null)
			retour.put(joker, ACTEUR);
		else
			return null;
		return retour;
	}

}