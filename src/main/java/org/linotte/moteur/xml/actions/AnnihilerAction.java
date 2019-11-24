/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 11, 2006                           *
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
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class AnnihilerAction extends Action implements IProduitCartesien {

	public AnnihilerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe annihiler";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		Acteur a = extractionDesActeurs(Constantes.SYNTAXE_ACTEUR_IMPOSSIBLE, job, valeurs)[0];
		try {
			runtimeContext.getLibrairie().supprime(a);
			jobContext.getLivre().removeActeur(a);
			jobContext.getLivre().getParagraphe().removeActeur(a.getNom());
		} catch (Exception e1) {
			throw new ErreurException(Constantes.SYNTAXE_ACTEUR_IMPOSSIBLE, String.valueOf(a.getValeur()));
		}

		if (a.isEspeceGraphique()) {
			PrototypeGraphique e = (PrototypeGraphique) a;
			e.getToile().getPanelLaToile().effacer(e);
			e.getToile().getPanelLaToile().setChangement();
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}
}
