/***********************************************************************
 * Linotte                                                             *
 * Version release date : February 14, 2013                            *
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
 *                 Pivot Syntaxique                                    *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.xml.alize.parseur;

import java.util.ArrayList;
import java.util.List;

import org.linotte.moteur.xml.actions.ActeurAction;
import org.linotte.moteur.xml.actions.EspeceAction;
import org.linotte.moteur.xml.actions.GreffonsAction;
import org.linotte.moteur.xml.actions.ImportLivreAction;
import org.linotte.moteur.xml.actions.ImportationAction;
import org.linotte.moteur.xml.actions.ParagrapheAction;
import org.linotte.moteur.xml.actions.ProposerAction;
import org.linotte.moteur.xml.actions.StructureDebutAction;
import org.linotte.moteur.xml.actions.StructureGlobaleAction;
import org.linotte.moteur.xml.actions.TestUnitaireAction;
import org.linotte.moteur.xml.actions.TestUnitaireInAction;
import org.linotte.moteur.xml.actions.TestUnitaireOutAction;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusFactory;
import org.linotte.moteur.xml.analyse.ItemXML;

/**
 * Cette classe va vérifier si le livre a une structure cohérente.
 * 
 *  Voici les régles vérifiées :
 *  
 *  - [X] si structure sans paragraphe au début du livre, s'arréter avant le premier paragraphe
 *  
 *  Voici les optimisations effectuées :
 *  
 *  - 
 *
 */
public class PivotSyntaxiqueSimple {

	private Job jobRacine;
	private ParserContext pc;
	private List<Processus> deja = new ArrayList<Processus>();

	private enum ETAT {
		INITIALISATION, FIN_TRAITEMENT
	}

	public PivotSyntaxiqueSimple(ParserContext pc, Job jobRacine) {
		this.jobRacine = jobRacine;
		this.pc = pc;
	}

	public void verifier() throws Exception {
		verifier((Processus) jobRacine.getFirstProcessus(), ETAT.INITIALISATION);
	}

	private void verifier(Processus pere, ETAT etat) throws Exception {
		boolean verbe = false;
		Processus p = pere;
		Processus precedent = null;
		Action action = null;
		while (true) {
			precedent = p;
			p = (Processus) p.getNextProcess();
			if (p == null)
				etat = ETAT.FIN_TRAITEMENT;
			else
				action = p.getAction();
			switch (etat) {
			case INITIALISATION:
				if (action instanceof GreffonsAction || action instanceof TestUnitaireAction || action instanceof EspeceAction || action instanceof ActeurAction
						|| action instanceof ProposerAction || action instanceof ImportationAction || action instanceof TestUnitaireOutAction
						|| action instanceof TestUnitaireInAction || action instanceof ImportLivreAction || action instanceof StructureGlobaleAction
						|| action instanceof StructureDebutAction) {
					break; // Rien à faire
				} else if (action instanceof ParagrapheAction) {
					etat = ETAT.FIN_TRAITEMENT;
					deja.add(p);
					if (verbe) {
						Processus processus = ProcessusFactory.createProcessus("fin", new ArrayList<ItemXML>(), new ArrayList(), pc.linotte.getRegistreDesEtats()
								.getActionArreter(), p.getPosition());
						precedent.setNextProcess(processus);
					}
					break;
				} else {
					// verbe ?
					verbe = true;
					break;
				}
			case FIN_TRAITEMENT:
				return;
			default:
				break;
			}
		}
	}

}