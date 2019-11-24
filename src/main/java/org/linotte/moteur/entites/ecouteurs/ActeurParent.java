/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
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

package org.linotte.moteur.entites.ecouteurs;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.RoleException;

public class ActeurParent {

	private Acteur acteur = null;

	public ActeurParent(Acteur c) {
		super();
		acteur = c;
	}

	public void mettreAjour(String caracteristique) throws ErreurException {
		if (acteur.retourneLibrairie() != null)
			acteur.retourneLibrairie().mettre(acteur);
	}

	public void mettreAjour(String caracteristique, Object valeur) throws ErreurException {
		if (acteur.retourneLibrairie() != null)
			acteur.retourneLibrairie().mettre(acteur);
	}

	public Acteur getActeur() {
		return acteur;
	}

	public void oter(Acteur a) throws ErreurException {
		if (acteur.getRole() == Role.CASIER) {
			((Casier) acteur).oterActeur(a);
		} else {
			throw new RoleException(Constantes.CASIER_ATTENDU, acteur.getNom().toString(),Role.CASIER,acteur.getRole());
		}
	}
}
