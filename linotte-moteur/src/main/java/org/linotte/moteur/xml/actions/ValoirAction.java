/***********************************************************************
 * Linotte                                                             *
 * Version release date : Febrary 04, 2008                             *
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

package org.linotte.moteur.xml.actions;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.RoleException;
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ValoirAction extends Action implements IProduitCartesien {

	public ValoirAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe valoir";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {
		// linotte.debug("on copie == ...");

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_VALOIR, linotte, valeurs);
		Acteur acteur1 = acteurs[1];
		Acteur acteur2 = acteurs[0];

		if (acteur1.getRole() == Role.NOMBRE && acteur2.getRole() == Role.TEXTE) {
			// On peut copier un nombre dans un texte
			acteur2.setValeur(acteur1.getValeur().toString());
		} else if (acteur1.getRole() == Role.ESPECE && acteur2.getRole() == Role.ESPECE
				&& ((Prototype) acteur1).getType().equals(((Prototype) acteur2).getType())) {
			Prototype e1 = (Prototype) acteur1;
			Prototype e2 = (Prototype) acteur2;
			e2.copier(e1);
		} else {

			if (acteur1.getRole() != acteur2.getRole())
				throw new RoleException(Constantes.SYNTAXE_PARAMETRE_VALOIR, acteur1.toString(), acteur2.getRole(), acteur1.getRole());

			if (acteur1.getRole() == Role.CASIER) {
				if (!(((Casier) acteur2).roleContenant() == ((Casier) acteur1).roleContenant()))
					throw new RoleException(Constantes.SYNTAXE_PARAMETRE_VALOIR, acteur1.toString(), ((Casier) acteur2).roleContenant(),
							((Casier) acteur1).roleContenant());
				((Casier) acteur2).toutEffacer();
				try {
					((Casier) acteur2).addAll((Casier) ((Casier) acteur1).clone());
				} catch (SyntaxeException e) {
					e.printStackTrace();
				}
			} else
				acteur2.setValeur(acteur1.getValeur());
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

}
