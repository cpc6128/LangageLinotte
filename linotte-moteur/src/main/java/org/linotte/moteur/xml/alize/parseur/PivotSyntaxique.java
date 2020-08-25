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

import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.xml.actions.*;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe va vérifier si le livre a une structure cohérente.
 * 
 *  Voici les régles vérifiées :
 *  
 *  - [X] Pas d'action hors des fonctions
 *  - [ ] Token 'début' présent
 *  - [ ] Définition des acteurs avant le mot clé début dans une fonction
 *  - [ ] Reviens obligatoire avec le verbe parcourir
 *  
 *  Voici les optimisations effectuées :
 *  
 *  - [X] On supprime l'action Début 
 *
 */
public class PivotSyntaxique {

	private Job jobRacine;
	private ParserEnvironnement environnement;
	private List<Processus> deja = new ArrayList<Processus>();

	//private Map<Processus, Processus> parcourir = new HashMap<Processus, Processus>();
	//private List<Processus> revenir = new ArrayList<Processus>();

	private enum ETAT {
		INITIALISATION, GLOBALE, PARAGRAPHE_INIT, PARAGRAPHE_CORPS, PARAGRAPHE_INIT_BLOC, FIN_PARAGRAPHE
	}

	public PivotSyntaxique(ParserEnvironnement environnement, Job jobRacine) {
		this.jobRacine = jobRacine;
		this.environnement = environnement;
	}

	public void verifier() throws Exception {
		// Vérification de la structure du livre :
		verifier((Processus) jobRacine.getFirstProcessus(), ETAT.INITIALISATION);
		// Vérification de tous les paragraphes :
		for (Processus p : environnement.paragraphes()) {
			if (!deja.contains(p)) {
				verifier(p, ETAT.PARAGRAPHE_INIT);
				deja.add(p);
			}
		}
		// Vérification des verbes parcourir VS revenir :
		/*
		for (Entry<Processus, Processus> entry : parcourir.entrySet()) {
			if (!revenir.contains(entry.getValue())) {
				throw new LectureException(new ErreurException(Constantes.STRUCTURE_LIVRE, "La fonction appelée doit terminer par le verbe revenir"), entry.getKey()
						.getPosition());
			}
		}
		throw new ErreurException(Constantes.STRUCTURE_LIVRE, "OK");
		*/
	}

	private void verifier(Processus pere, ETAT etat) throws Exception {
		Processus p = pere;
		Processus precedent = null;
		//Action action_precedente = null;
		Action action = null;
		//boolean pasForcerTokenDebut = true;
		try {
			while (true) {
				//action_precedente = e;
				precedent = p;
				p = (Processus) p.getNextProcess();
				if (p == null)
					etat = ETAT.FIN_PARAGRAPHE;
				else
					action = p.getAction();
				switch (etat) {
					case INITIALISATION:
						if (action instanceof TestUnitaireAction || action instanceof EspeceAction || action instanceof ActeurAction
								|| action instanceof ProposerAction || action instanceof ImportationAction || action instanceof TestUnitaireOutAction
								|| action instanceof TestUnitaireInAction || action instanceof ImportLivreAction) {
							break;
						} else if (action instanceof StructureGlobaleAction) {
							etat = ETAT.GLOBALE;
							if (precedent != null) {
								precedent.setNextProcess(p.getNextProcess());
							}
							break;
						} else if (action instanceof ParagrapheAction) {
						etat = ETAT.PARAGRAPHE_INIT;
						//pasForcerTokenDebut = true;
						deja.add(p);
						break;
					} else
						throw new ErreurException(Constantes.STRUCTURE_LIVRE, "la structure de votre livre n'est pas correcte");
				case GLOBALE:
					if (action instanceof ImportationAction) {
						break;
					} else if (action instanceof StructureGlobaleAction || action instanceof EspeceAction || action instanceof ActeurAction) {
						break;
					} else if (action instanceof ParagrapheAction) {
						etat = ETAT.PARAGRAPHE_INIT;
						//pasForcerTokenDebut = true;
						deja.add(p);
						break;
					} else
						throw new ErreurException(Constantes.STRUCTURE_LIVRE, "la structure de votre livre n'est pas correcte");
				case PARAGRAPHE_INIT:
					if (action instanceof EspeceAction || action instanceof ActeurAction) {
						//pasForcerTokenDebut = false;
						break;
					} else if (false) { //|| pasForcerTokenDebut
						etat = ETAT.PARAGRAPHE_CORPS;
						if (action instanceof ActionDispatcher) { //&& pasForcerTokenDebut
							ProcessusDispatcher pd = (ProcessusDispatcher) p;
							if (pd.getProcessusSecondaire() != null && pd.getProcessusSecondaire().getAction() instanceof LireAction) {
								etat = ETAT.PARAGRAPHE_INIT_BLOC;
							}
						}
						if (precedent != null && false) { // && !pasForcerTokenDebut
							precedent.setNextProcess(p.getNextProcess());
						}
						break;
					}
					break;
					//throw new ErreurException(Constantes.STRUCTURE_LIVRE, "le mot 'début' est manquant");
				case PARAGRAPHE_CORPS:
					/**if (action instanceof EspeceAction || action instanceof ActeurAction) {
						throw new ErreurException(Constantes.STRUCTURE_LIVRE, "la déclaration des acteurs doit être effectuée en début de fonction ou du bloc");
					} else **/
					if (action instanceof ParagrapheAction) {
						// Après un verbe Aller ?
						etat = ETAT.FIN_PARAGRAPHE;
						//pasForcerTokenDebut = true;
						return;
					} else if (action instanceof ParcourirAction) {
						/*ProcessusDispatcher pd = (ProcessusDispatcher) p;
						if (pd.getParcourir() != null) {
							parcourir.put(p, pd.getParcourir());
						}*/
						break;
					} else if (action instanceof ActionDispatcher) {
						ProcessusDispatcher pd = (ProcessusDispatcher) p;
						if (pd.getProcessusSecondaire() != null && pd.getProcessusSecondaire().getAction() instanceof LireAction) {
							etat = ETAT.PARAGRAPHE_INIT_BLOC;
						}
						break;
					}
					break;
				case PARAGRAPHE_INIT_BLOC:
					if (action instanceof ActionDispatcher) {
						ProcessusDispatcher pd = (ProcessusDispatcher) p;
						if (pd.getProcessusSecondaire() != null && pd.getProcessusSecondaire().getAction() instanceof LireAction) {
							// Imbrication de boucle
							etat = ETAT.PARAGRAPHE_INIT_BLOC;
							break;
						}
					}
					if (action instanceof EspeceAction || action instanceof ActeurAction) {
						break;
					}
					etat = ETAT.PARAGRAPHE_CORPS;
					break;
				case FIN_PARAGRAPHE:
					/*if (action_precedente != null && action_precedente instanceof EtatRevenir) {
						revenir.add(pere);
					}*/
					return;
				default:
					break;
				}
			}
		} catch (ErreurException e) {
			throw new LectureException(e, p.getPosition());
		}
	}

}