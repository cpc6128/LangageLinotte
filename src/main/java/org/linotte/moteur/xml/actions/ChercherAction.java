/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 14, 2006                           *
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

import java.math.BigDecimal;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.ChaineOutils;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ChercherAction extends Action implements IProduitCartesien {

	public ChercherAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe chercher";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		//		linotte.debug("On cherche : ");

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_CHERCHER, linotte, valeurs);
		Acteur a1 = acteurs[0];
		Acteur a2 = acteurs[1];
		Acteur a3 = acteurs[2];

		if (!(a1.getRole() == Role.NOMBRE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CHERCHER, a1.toString());

		if (a2.getRole() == Role.NOMBRE) {
			if (!(a3.getRole() == Role.CASIER))
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CHERCHER, a3.toString());
			Casier c = (Casier) a3;
			if (!(c.roleContenant() == Role.NOMBRE))
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CHERCHER, a3.toString());
		} else if (a2.getRole() == Role.TEXTE) {
			if (a3.getRole() == Role.CASIER) {
				Casier c = (Casier) a3;
				if (!(c.roleContenant() == Role.TEXTE))
					throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CHERCHER, a3.toString());
			} else if (!(a3.getRole() == Role.TEXTE))
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CHERCHER, a3.toString());
		} else
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CHERCHER, a2.toString());

		if (a3.getRole() == Role.TEXTE) {
			String s3 = (String) a3.getValeur();
			String s2 = (String) a2.getValeur();
			int pos = ChaineOutils.indexOfIgnoreCase(s3, s2, 0);
			a1.setValeur(new BigDecimal(pos));
		} else {
			Casier casier = (Casier) a3;
			a1.setValeur(new BigDecimal(casier.recherche(a2)));
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

}
