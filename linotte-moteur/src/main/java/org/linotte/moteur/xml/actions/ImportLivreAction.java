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

import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.alize.security.Habilitation;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.FichierOutils;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.ContextHelper;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.Parseur;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ImportLivreAction extends Action {

	public ImportLivreAction() {
		super();
	}

	@Override
	public String clef() {
		return "import livre";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		if (runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
			throw new ErreurException(Constantes.MODE_CONSOLE);			
		}			

		ItemXML p = valeurs[0];
		ItemXML l = valeurs[1];
		String nom = p.getValeurBrute();
		String livre = l.getValeurBrute();

		// bogue 87 : https://code.google.com/p/langagelinotte/issues/detail?id=87
		try {
			if (livre.startsWith("\"")) {
				livre = (String) l.retourneValeurTransformee(job, runtimeContext.getLinotte().getLangage());
			}
		} catch (Exception e1) {
			throw new ErreurException(Constantes.IMPOSSIBLE_LIRE_LIVRE, livre);
		}

		String chemin = Ressources.construireChemin(livre);
		StringBuilder buffer;
		try {
			buffer = new StringBuilder(FichierOutils.lire(chemin));
		} catch (Exception e) {
			throw new ErreurException(Constantes.IMPOSSIBLE_LIRE_LIVRE, livre);
		}
		AKRuntime runtimeFils;
		Parseur moteurXML = new Parseur();
		ParserContext atelierOutils = new ParserContext(MODE.GENERATION_RUNTIME);
		atelierOutils.linotte = runtimeContext.getLinotte();
		runtimeFils = moteurXML.parseLivre(buffer, atelierOutils);
		RuntimeContext runtimeContextFils = (RuntimeContext) runtimeFils.getContext();
		/**
		 * On execute une première fois pour initialiser les acteurs, espèces.
		 */
		runtimeContextFils.setImportationProcess(true);
		ContextHelper.populate(runtimeContextFils, runtimeContext.getLinotte(), null, null);
		// On force à travailler sur la même librairie :
		runtimeContextFils.setLibrairie(runtimeContext.getLibrairie());
		runtimeFils.execute();
		runtimeContextFils.setImportationProcess(false);
		//Patch pour ne pas être visible dans l'audit
		AKPatrol.runtimes.remove(runtimeFils);

		if (!runtimeContext.ajouterLivre(nom, runtimeFils)) {
			throw new ErreurException(Constantes.LIVRE_DEJA_PRESENT, livre);
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

}