/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 12, 2006                           *
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

import java.math.BigDecimal;

public class InsererAction extends Action implements IProduitCartesien {

	public InsererAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe insérer";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		//		linotte.debug("On insère : ");

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_INSERER, linotte, valeurs);
		Acteur a1 = acteurs[0];
		Acteur a2 = acteurs[1];
		Acteur a3 = acteurs[2];

		if (!(a1.getRole() == Role.CASIER || a1.getRole() == Role.TEXTE || a1.getRole() == Role.NOMBRE || a1.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_INSERER, a1.toString());

		if (!(a3.getRole() == Role.CASIER || a3.getRole() == Role.TEXTE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_INSERER, a3.toString());

		// Ajout 10/12/10, amélioration de l'insertion dans un casier
		if (a3.getRole() == Role.CASIER) {
			if (a1.getRole() == Role.TEXTE && !(((Casier) a3).roleContenant() == Role.TEXTE))
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_INSERER, a3.toString());
			if (a1.getRole() == Role.NOMBRE && !(((Casier) a3).roleContenant() == Role.NOMBRE))
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_INSERER, a3.toString());
			if (a1.getRole() == Role.CASIER && !(((Casier) a3).roleContenant() == Role.CASIER))
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_INSERER, a3.toString());
		} else {
			if (!(a1.getRole() == a3.getRole()))
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_INSERER, a3.toString());
		}

		if (!(a2.getRole() == Role.NOMBRE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_INSERER, a2.toString());
		int pos1 = ((BigDecimal) a2.getValeur()).intValueExact();

		if (a3.getRole() != Role.CASIER) {
			StringBuilder s3 = new StringBuilder((String) a3.getValeur());
			s3 = s3.insert(pos1 - 1, String.valueOf(a1.getValeur()));
			a3.setValeur(s3.toString());
		} else {
			((Casier) a3).inserer(a1, pos1 - 1);
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}
}
