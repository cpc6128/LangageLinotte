/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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

package org.linotte.moteur.exception;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;

@SuppressWarnings("serial")
public class RoleException extends ErreurException {

	public RoleException(int numeroErreur, String acteur, Role attendu, Role trouve) {
		super(numeroErreur);
		message = "\n\tacteur " + acteur;
		message += "\n\trôle trouvé " + trouve;
		message += "\n\trôle demandé " + attendu;
	}

	public RoleException(int numeroErreur, Acteur acteur, Role attendu) {
		super(numeroErreur);
		message = "\n\tacteur " + acteur.getNom();
		message += "\n\trôle trouvé " + acteur.getRole();
		message += "\n\trôle demandé " + attendu;
	}

	/**
	 * Pour les espèces
	 * @param numeroErreur
	 * @param acteur
	 * @param attendu
	 * @param trouve
	 */
	public RoleException(int numeroErreur, String acteur, String attendu, String trouve) {
		super(numeroErreur);
		message = "\n\tacteur " + acteur;
		message += "\n\trôle trouvé " + trouve;
		message += "\n\trôle demandé " + attendu;
	}

	/**
	 * @param numeroErreur
	 * @param acteur
	 * @param attendu
	 */
	public RoleException(int numeroErreur, String acteur, String attendu) {
		super(numeroErreur);
		message = "\n\tacteur " + acteur;
		message += "\n\trôle demandé " + attendu;
	}

}