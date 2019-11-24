/***********************************************************************
 * Linotte                                                             *
 * Version release date : January 11, 2006                             *
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
 *                 Parseur                                             *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.xml.alize.parseur;

import org.alize.kernel.AKLoader;
import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.linotte.frame.coloration.Style;
import org.linotte.frame.coloration.StyleLinotte;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.FinException;
import org.linotte.moteur.exception.InconnuException;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.exception.LinotteException;
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.ParserHandler;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.lexer.MotAMotLexer;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.noeud.NNoeud;
import org.linotte.moteur.xml.analyse.LegacyDecoupeDunLivre;

public class Parseur {

	public Parseur() {
	}

	public AKRuntime parseLivre(StringBuilder flux, ParserContext parserContext) throws Exception {

		assert parserContext != null : "L'objet Atelier outils ne doit pas être nul !";

		AKRuntime runtime = null;
		// Noeud racine pour parser un livre :
		NNoeud nodeRacine = parserContext.linotte.getGrammaire().retourneGrammaire();
		nodeRacine.getFils().recommencer();
		if (parserContext.linotte.getLangage().isLegacy())
			parserContext.lexer = new LegacyDecoupeDunLivre(parserContext.linotte.getGrammaire(), flux, parserContext.styleBuffer);
		else {
			parserContext.lexer = new MotAMotLexer(parserContext.linotte.getGrammaire(), flux, parserContext.styleBuffer);
		}
		RuntimeContext context = null;

		if (parserContext.mode == MODE.GENERATION_RUNTIME) {
			parserContext.jobRacine = new Job(new JobContext());
			context = new RuntimeContext();
			runtime = AKLoader.produceRuntime();
			runtime.setContext(context);
			runtime.setJob(parserContext.jobRacine);
		}

		try {
			try {
				if (parserContext.mode == MODE.COLORATION) {
					parserContext.sommaire.viderNoeuds();
				}
				nodeRacine.parse(parserContext);
				if (parserContext.mode == MODE.GENERATION_RUNTIME) {
					for (Processus processus : parserContext.awares) {
						((ParserHandler) processus.getAction()).postAnalyse(processus, parserContext.environnement);
					}
					// Utilisé plus tard pour les fonctions mathématiques...
					context.setEnvironnment(parserContext.environnement);

					// Pivot syntaxique : (Linotte 2.1) 
					if (!parserContext.linotte.getLangage().isLegacy() && !parserContext.linotte.getLangage().isForceParametreEnligne()
							&& !parserContext.webonotte) {
						new PivotSyntaxique(parserContext.environnement, parserContext.jobRacine).verifier();
					} else {
						if (parserContext.linotte.getLangage().isForceParametreEnligne()) {
							// Pour les langages basés sur Linnet
							new PivotSyntaxiqueSimple(parserContext, parserContext.jobRacine).verifier();
						}
					}

				}
			} catch (Exception e) {
				// Sinon, le runtime reste dans le cas d'un erreur de syntaxe...
				AKPatrol.runtimes.remove(runtime);
				throw e;
			}
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw new LectureException(new FinException(), 0);
		} catch (ErreurException e) {
			// Bug sous paragraphe
			throw new LectureException(e, parserContext.lexer.getLastPosition());
		} catch (SyntaxeException e) {
			if (parserContext.mode == MODE.COLORATION) {
				parserContext.sommaire.setErreurSyntaxe();
			}
			// Patch pour affiner le message d'erreur...
			if (parserContext.derniere_erreur != null) {
				throw new LectureException(parserContext.derniere_erreur, parserContext.derniere_erreur.getErreurnumeroligne());
			} else
				throw new LectureException(e, e.getErreurnumeroligne());
		} catch (InconnuException e) {
			if (Version.isBeta())
				e.printStackTrace();
			throw new LectureException(e, e.getPosition());
		} catch (FinException e) {
			throw new LectureException(e, parserContext.lexer.getLastPosition());
		} catch (LinotteException e) {
			if (Version.isBeta())
				e.printStackTrace();
			throw e;// new LectureException(e, decoupe.getLastPosition());
		} finally {
			nodeRacine.getFils().recommencer();
			parserContext.groupes.clear();
			parserContext.valeurs.clear();
			parserContext.phrase.clear();
			parserContext.annotations.clear();
			if (parserContext.mode == MODE.COLORATION) {
				if (parserContext.styleBuffer != null) {
					parserContext.styleBuffer.ajouteStyleEnPremier(new Style(parserContext.stylePosition, -1, StyleLinotte.styleRacine, null));
					parserContext.sommaire.afficher();
				}
			}
		}
		return runtime;
	}
}