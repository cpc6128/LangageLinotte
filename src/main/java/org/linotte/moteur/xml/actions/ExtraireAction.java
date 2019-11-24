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

import java.math.BigDecimal;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ExtraireAction extends Action implements IProduitCartesien {

	public ExtraireAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe extraire";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		//		linotte.debug("On extrait : ");

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE, linotte, valeurs);
		Acteur a1 = acteurs[0];
		Acteur a2 = acteurs[1];
		Acteur a3 = acteurs[2];
		Acteur a4 = acteurs[3];

		int pos1, pos2;

		if (!(a1.getRole() == Role.CASIER || a1.getRole() == Role.TEXTE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE, a1.toString());

		if (!(a4.getRole() == Role.CASIER || a4.getRole() == Role.TEXTE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE, a4.toString());

		if (!(a1.getRole() == a4.getRole()))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE, a4.toString());

		if (!(a2.getRole() == Role.NOMBRE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE, a2.toString());
		pos1 = ((BigDecimal) a2.getValeur()).intValueExact();

		if (!(a3.getRole() == Role.NOMBRE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE, a3.toString());
		pos2 = ((BigDecimal) a3.getValeur()).intValueExact();

		if (a1.getRole() == Role.TEXTE) {
			String s1 = (String) a1.getValeur();
			try {
				s1 = s1.substring(pos1 - 1, pos2);
			} catch (Exception e) {
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE);
			}
			a4.setValeur(s1);
		} else {
			try {
				((Casier) a4).extraire(a1, pos1 - 1, pos2);
			} catch (Exception e) {
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE);
			}
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
