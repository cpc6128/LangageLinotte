/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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

package org.linotte.moteur.xml.actions;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class OterAction extends Action implements IProduitCartesien {

	public OterAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe ôter";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur[] liste = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_OTER, linotte, valeurs);

		Acteur a1 = liste[0];
		Acteur a2 = liste[1];

		if (!(a2.getRole() == Role.CASIER)) {
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_OTER, a2.toString());
		}

		if (!(a1.getRole() == ((Casier) a2).roleContenant())) {
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_OTER, a1.toString());
		}

		try {
			a1.oterDe(a2);
		} catch (Exception e) {
			throw new ErreurException(Constantes.CASIER_ATTENDU, a1.toString());
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
