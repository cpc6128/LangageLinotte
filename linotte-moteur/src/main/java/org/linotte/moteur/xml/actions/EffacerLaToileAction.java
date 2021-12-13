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

import org.alize.security.Habilitation;
import org.linotte.frame.latoile.LaToile;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.analyse.ItemXML;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

public class EffacerLaToileAction extends Action {

	public EffacerLaToileAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe effacer la tablette";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws ErreurException, StopException {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		if (runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
			throw new ErreurException(Constantes.MODE_CONSOLE);			
		}			

		if (runtimeContext.getLibrairie().getToilePrincipale() != null)
			effacerToile(runtimeContext.getLibrairie().getToilePrincipale());

		for (LaToile otoile : runtimeContext.getLibrairie().getToiles()) {
			effacerToile(otoile);
		}
		

		return ETAT.PAS_DE_CHANGEMENT;
	}

	public void effacerToile(LaToile toile) throws ErreurException {
		List<PrototypeGraphique> lists = toile.getPanelLaToile().getActeursAAfficher();
		Iterator<PrototypeGraphique> i = lists.iterator();
		while (i.hasNext()) {
			PrototypeGraphique eg = i.next();
			if (eg.getTypeGraphique().equals(PrototypeGraphique.TYPE_GRAPHIQUE.CRAYON)) {
				eg.setObject(null);
			}
			eg.retourneAttribut("visible").setValeur(BigDecimal.ZERO);
		}
		toile.getPanelLaToile().setChangement();
	}

}