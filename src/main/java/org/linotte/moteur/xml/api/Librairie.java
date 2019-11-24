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

package org.linotte.moteur.xml.api;

import java.util.Collection;
import java.util.Map;

import org.linotte.frame.latoile.LaToile;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.Linotte;

/**
 * Représente une librairie
 *
 */
public interface Librairie<P extends Librairie<?>> {

	void mettre(Acteur a) throws ErreurException;

	void reCharger(Acteur a) throws ErreurException;

	void supprime(Acteur a) throws ErreurException;

	void addActeurPourEspece(Acteur a);

	Acteur getActeurPourEspece(Chaine a);

	Prototype creationEspece(String espece, String nom, String type_heritage) throws ErreurException;

	void cleanEspece(Linotte linotte);

	void addEspece(Prototype e) throws ErreurException;

	Prototype getEspece(String espece);

	Map<String, Acteur> getActeurs();

	void vider();

	void setToile(LaToile toile);

	void ajouterToile(String clef, LaToile toile);

	LaToile recupererToile(String clef);

	Collection<LaToile> getToiles();

	void ajouterValeur(Acteur pere, Acteur fils) throws ErreurException;

	P cloneMoi();

	LaToile getToilePrincipale();

	boolean existeTElleToile(String clef);

}
