/***********************************************************************
 * Linotte                                                             *
 * Version release date : August 06, 2013                              *
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

package org.linotte.greffons;

import org.alize.kernel.AKRuntime;
import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.outils.ITransparence;
import org.linotte.greffons.api.Greffon;
import org.linotte.greffons.api.Greffon.Attribut;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.greffons.java.GreffonPrototype;
import org.linotte.greffons.java.GreffonPrototype.Entrée;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.ThreadLinotte;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Facade d'accès aux objets techniques du moteur d'exécution du langage Linotte
 * 
 * @author cpc
 *
 */
public final class LinotteFacade {

	private LinotteFacade() throws Exception {
		throw new InstantiationException();
	}

	/**
	 * Retourne la toile du job courant
	 * 
	 * @return
	 * @throws GreffonException
	 */
	public static LaToile getToile() throws GreffonException {
		verifierContexteThread();
		return ((RuntimeContext) ((Job) AKRuntime.staticCurrentJob.get()).getRuntimeContext()).getLibrairie().getToilePrincipale();
	}

	/**
	 * Retourne la toile du job courant
	 * 
	 * @return
	 * @throws GreffonException
	 */
	public static LaToile getToile(String nom) throws GreffonException {
		verifierContexteThread();
		return ((RuntimeContext) ((Job) AKRuntime.staticCurrentJob.get()).getRuntimeContext()).getLibrairie().recupererToile(nom);
	}

	/**
	 * Retourne le job courant moteur d'exécution
	 * 
	 * @return
	 * @throws GreffonException
	 */
	public static Job getJob() throws GreffonException {
		return (Job) AKRuntime.staticCurrentJob.get();
	}

	private static void verifierContexteThread() throws GreffonException {
		if (AKRuntime.staticCurrentJob.get() == null) {
			for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
				System.out.println(ste);
			}
			throw new org.linotte.greffons.externe.Greffon.GreffonException("Le thread courant doit être un Job : contexte nul");
		}
	}

	/**
	 * Retourne le contexte du moteur d'exécution
	 * 
	 * @throws GreffonException
	 * @returnS
	 */
	public static RuntimeContext getRuntimeContext() throws GreffonException {
		verifierContexteThread();
		return (RuntimeContext) ((Job) AKRuntime.staticCurrentJob.get()).getRuntimeContext();
	}

	/**
	 * Retourne le processus lie au paragraphe
	 * 
	 * @param paragraphe
	 * @return
	 * @throws GreffonException
	 */
	public static Processus rechercherProcessus(String paragraphe) throws GreffonException {
		RuntimeContext runtimeContext = getRuntimeContext();
		ParserEnvironnement environnement = runtimeContext.getEnvironnment();
		Processus processus = environnement.getParagraphe(paragraphe.toLowerCase());
		if (processus == null) {
			throw new GreffonException("Fonction inconnue : " + paragraphe);
		}
		return processus;
	}

	/**
	 * Execute le processus
	 * 
	 * @param paragraphe
	 * @param processus
	 * @throws GreffonException
	 */
	public static void executionProcessus(String paragraphe, Processus processus) throws GreffonException {
		executionProcessus(getJob(), paragraphe, processus);
	}

	/**
	 * Execute le processus
	 * 
	 * @param paragraphe
	 * @param processus
	 * @throws GreffonException
	 */
	public static void executionProcessus(Job job, String paragraphe, Processus processus) throws GreffonException {
		if (job.isRunning() && paragraphe != null) {
			RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
			ThreadLinotte threadLinotte = new ThreadLinotte(job, processus, null, paragraphe);
			if (runtimeContext != null) {
				Map<String, List<ThreadLinotte>> threads = runtimeContext.getThreads();
				synchronized (threads) {
					List<ThreadLinotte> list = threads.get(paragraphe);
					if (list == null) {
						list = new ArrayList<ThreadLinotte>();
					}
					list.add(threadLinotte);
					threads.put(paragraphe, list);
				}
				new Thread(threadLinotte).start();
			}
		}
	}

	/**
	 * Retourne le service permettant de gérer la transparence
	 * 
	 * @return service de gestion de la tranparence
	 */
	public static ITransparence getTransparence() {
		return ITransparence.getTransparence();
	}

	/**
	 * Création d'un prototype depuis un greffon
	 * 
	 * @param attributs
	 *            Les attributs du prototypes
	 * @param greffon
	 *            Greffon
	 * @param type
	 *            Type du greffon
	 * @return Le prototype peuplé avec les informations du greffon
	 * @throws Exception
	 */
	public static Prototype creationPrototype(Map<String, Attribut> xattributs, org.linotte.greffons.externe.Greffon greffon, String type) throws Exception {

		try {
			Prototype prototype = new Prototype(null, type, Role.ESPECE, type, null, greffon.getClass().newInstance());

			Greffon g = GreffonsChargeur.getInstance().ajouterGreffon(type, greffon.getClass().getName(), "java");
			if (g != null) {
				g.setEspece(type);
				g.setNom(Character.toUpperCase(type.charAt(0)) + type.substring(1));
				g.setAuteur("cpc");
				g.setDescription("greffon interne");
				g.setVersion("1.0");
				g.setAttributs(xattributs == null ? new HashMap<String, Attribut>() : xattributs);

				// Ajout des méthodes fonctionnelles :
				List<Entrée> slots = GreffonPrototype.chargerSlotJava(greffon.getClass());
				for (Entrée entrée : slots) {
					prototype.ajouterSlotGreffon(entrée.getKey(), entrée.getValue());
				}
				
				GreffonPrototype.chargeAttributGreffon(prototype, g);

			}
			return prototype;
		} catch (Throwable e) {
			System.out.println("Impossible de créer le prototype : " + type + ", " + e.getMessage());
			e.printStackTrace();
			return null;
		}

	}

}