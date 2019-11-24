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

package org.linotte.moteur.xml.actions;

import java.math.BigDecimal;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class StopperAction extends Action implements IProduitCartesien {

	public static final String NOM_ETAT = "verbe stopper";

	public StopperAction() {
		super();
	}

	@Override
	public String clef() {
		return NOM_ETAT;
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_STOPPER, job, valeurs);
		Acteur acteur1 = acteurs[0];
		Acteur acteur2 = acteurs[1];
		if (!(acteur1.getRole() == Role.NOMBRE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_STOPPER);
		if (!(acteur2.getRole() == Role.TEXTE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_STOPPER);
		int numero_erreur = ((BigDecimal) acteur1.getValeur()).intValue();
		if (numero_erreur >= 10100)
			throw new ErreurException(numero_erreur, acteur2.getValeur().toString());
		else
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_STOPPER_NUMERO);
	}

}
