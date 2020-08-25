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

import org.alize.security.Habilitation;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.RoleException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

import java.math.BigDecimal;

public class AttendreMilliSecondeAction extends Action implements IProduitCartesien {

	public AttendreMilliSecondeAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe attendre milliseconde";
	}

	@Override
	public ETAT analyse(String param, Job runtime, ItemXML[] valeurs, String[] annotations) throws Exception {

		RuntimeContext runtimeContext = (RuntimeContext) runtime.getRuntimeContext();
		if (runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
			throw new ErreurException(Constantes.MODE_CONSOLE);
		}
		
		Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_ATTENDRE, runtime, valeurs)[0];

		if (!(a.getRole() == Role.NOMBRE))
			throw new RoleException(Constantes.SYNTAXE_PARAMETRE_ATTENDRE, a.toString(),Role.NOMBRE, a.getRole());

		try {
			long valeur = ((BigDecimal) a.getValeur()).longValue();
			if (valeur > 0)
				Thread.sleep(valeur);
		} catch (InterruptedException e) {
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
