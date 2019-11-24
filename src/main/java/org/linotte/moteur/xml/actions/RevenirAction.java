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

import java.util.EmptyStackException;

import org.alize.kernel.AKProcessus;
import org.linotte.moteur.entites.Livre;
import org.linotte.moteur.entites.Paragraphe;
import org.linotte.moteur.exception.RetourException;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.i.ActionDynamic;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.appels.Boucle;
import org.linotte.moteur.xml.appels.Fonction;

public class RevenirAction extends ActionDynamic {

	public RevenirAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe revenir";
	}

	@Override
	public AKProcessus analyseDynamic(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();
		try {
			// Fonction: 
			Fonction fonction = jobContext.getLivre().getKernelStack().recupereDerniereFonction();
			if (fonction != null) {
				if (fonction.getBoucle() != null) {
					Boucle boucle = fonction.getBoucle();
					if (boucle instanceof Boucle) {
						Boucle b = boucle;
						Livre livre = jobContext.getLivre();
						// Patch pour prendre en compte la boucle FOR avec un pas dynamique :
						// Le pas dynamique doit être récupéré du paragraphe père :
						Paragraphe sav = livre.getParagraphe();
						if (fonction.getParagraphe() != null) // Linnet, on peut être hors fonction
							livre.setParagraphe(fonction.getParagraphe());
						boolean retour = b.suiteBoucle(job);
						livre.setParagraphe(sav);
						// fin patch
						if (retour) {
							// On retourne vers le verbe indiqué par le verbe parcourir
							jobContext.getLivre().getKernelStack().fermeCalqueParagraphe();
							return ((ProcessusDispatcher) fonction.getProcessusRetour()).getParcourir();
						} else {
							// On retourne vers le verbe indiqué par le verbe parcourir
							livre.getKernelStack().fermeFonction();
							livre.getKernelStack().fermeBoucle();
							if (fonction.getParagraphe() != null) // Linnet, on peut être hors fonction
								livre.setParagraphe(fonction.getParagraphe());
							if ( fonction.getProcessusRetour() !=null ) {
								return fonction.getProcessusRetour().getNextProcess();
							}
							else {
								throw new RetourException();
							}
						}
					}
				} else {
					Livre livre = jobContext.getLivre();
					livre.getKernelStack().fermeFonction();
					livre.setParagraphe(fonction.getParagraphe());
					if ( fonction.getProcessusRetour() !=null ) {
						return fonction.getProcessusRetour().getNextProcess();
					}
					else {
						throw new RetourException();
					}
				}
			} else {
				throw new RetourException();
			}
		} catch (EmptyStackException e) {
		}
		throw new RetourException();
	}

}