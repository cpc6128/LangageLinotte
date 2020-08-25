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

import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.StopException;

/**
 * 
 * Cette classe implémente les méthodes utilisées lors de l'échange avec un utilisateur lors de la lecture d'un livre 
 *
 */
public interface IHM {

	/**
	 * Interroge l'utilisateur (INPUT)
	 * @param type
	 * @param acteur
	 * @return
	 * @throws StopException
	 */
	public String demander(Role type, String acteur) throws StopException;

	/**
	 * Affiche un message à l'utilisateur (PRINT)
	 * @param afficher
	 * @param type
	 * @return
	 * @throws StopException
	 */
	public boolean afficher(String afficher, Role type) throws StopException;

	/**
	 * Affiche un message d'erreur venant de l'interprète Linotte
	 * @param afficher
	 * @return
	 */
	public boolean afficherErreur(String afficher);

	/**
	 * Efface l'IHM (CLS)
	 * @return
	 * @throws StopException
	 */
	public boolean effacer() throws StopException;

	/**
	 * Affiche un message et interroge l'utilisateur (PRINT + INPUT)
	 * 
	 * @param question
	 * @param type
	 * @param acteur
	 * @return
	 * @throws StopException
	 */
	public String questionne(String question, Role type, String acteur) throws StopException;

}
