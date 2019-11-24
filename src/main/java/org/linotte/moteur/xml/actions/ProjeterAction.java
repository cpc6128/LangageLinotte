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

import org.linotte.greffons.externe.Composant;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ProjeterAction extends Action implements IProduitCartesien {

	public ProjeterAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe projeter";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_PROJETER, job, valeurs)[0];

		if (!(a.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_PROJETER, a.toString());

		Prototype e = (Prototype) a;

		if (e.getGreffon() != null && e.getGreffon() instanceof Composant) {
			try {
				Composant composant = (Composant) e.getGreffon();
				composant.initialisation();
			} catch (GreffonException e1) {
				throw new ErreurException(Constantes.ERREUR_GREFFON, e1.getMessage());
			}
		} else {
			if (!e.isEspeceGraphique())
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_PROJETER, a.toString());

			PrototypeGraphique eg = (PrototypeGraphique) e;

			eg.retourneAttribut("visible").setValeur("oui");

			// Si c'est la toile :
			if (eg.getType().equals("toile")) {

				if ("non".equals(eg.retourneAttribut("principale").getValeur())) {
					// Création d'une nouvelle toile
				} else {
					eg.getToile().getPanelLaToile().addActeursAAfficher(eg, false);
					eg.getToile().getPanelLaToile().focus();
				}
			}

			eg.getToile().getPanelLaToile().setChangement();
			// LaToileListener.getThis().focus();
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

}
