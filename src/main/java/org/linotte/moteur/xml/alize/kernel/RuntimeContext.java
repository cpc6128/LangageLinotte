/***********************************************************************
 * Linotte                                                             *
 * Version release date : December 07, 2008                            *
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

package org.linotte.moteur.xml.alize.kernel;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.io.File;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.alize.kernel.AKRuntime;
import org.alize.kernel.AKRuntimeContextI;
import org.linotte.frame.cahier.Cahier;
import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.outils.ITransparence;
import org.linotte.greffons.externe.Composant;
import org.linotte.greffons.externe.Tube;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.actions.SonAction;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.i.AKDebugger;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;
import org.linotte.moteur.xml.alize.test.Tests;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.analyse.Mathematiques.ANGLE;
import org.linotte.moteur.xml.api.IHM;
import org.linotte.moteur.xml.api.Librairie;

public class RuntimeContext extends AKRuntimeContextI {

	// Runtime Context
	
	public int compteurExecution = 0; // Pour le mode LITTLE_BIRD

	private Librairie<?> librairie;

	private Linotte linotte;

	// Job initiale :
	public Job jobPere = null;

	private IHM ihm = null;

	private File reference;

	private Map<String, List<ThreadLinotte>> threads = new ConcurrentHashMap<String, List<ThreadLinotte>>();

	private List<Tube> tubes = new ArrayList<Tube>();

	private List<Composant> composants = new ArrayList<Composant>();

	// Evenements à nettoyer :
	private List<PrototypeGraphique> evenements = new CopyOnWriteArrayList<PrototypeGraphique>();

	private ParserEnvironnement environnment;

	// Import de livres :
	private Map<String, AKRuntime> tableRuntime = new ConcurrentHashMap<String, AKRuntime>();

	private boolean importationProcess = false;

	private Set<Habilitation> habilitations = new HashSet<Habilitation>();

	// Pour les tests unitaires :
	public boolean doTest = false;

	public Tests tests = new Tests();

	// Pour le mode pas à pas
	public AKDebugger debugger = null;
	public int delayPasAPas = -1;
	public Cahier cahier;
	public StringBuilder buffer;
	
	public int paragrapheExecute = 0;

	public Librairie<?> getLibrairie() {
		return librairie;
	}

	public void setLibrairie(Librairie<?> librairie) {
		this.librairie = librairie;
	}

	public IHM getIhm() {
		return ihm;
	}

	public void setIhm(IHM ihm) {
		this.ihm = ihm;
	}

	public Linotte getLinotte() {
		return linotte;
	}

	public void setLinotte(Linotte linotte) {
		this.linotte = linotte;
	}

	public boolean ajouterLivre(String nom, AKRuntime runtime) {
		if (tableRuntime.containsKey(nom)) {
			return false;
		}
		tableRuntime.put(nom, runtime);
		return true;
	}

	public AKRuntime retourLivre(String nom) {
		return tableRuntime.get(nom);
	}

	public void tuerTousLesMoteurs() {
		for (AKRuntime m : tableRuntime.values()) {
			try {
				m.stopAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void reveille() {
		synchronized (linotte.getRegistreDesEtats().getPause()) {
			linotte.getRegistreDesEtats().getPause().notifyAll();
		}
		synchronized (linotte.getRegistreDesEtats().getTemporiser()) {
			linotte.getRegistreDesEtats().getTemporiser().notify();
		}
		synchronized (linotte.getRegistreDesEtats().getAttendreMilliSeconde()) {
			linotte.getRegistreDesEtats().getAttendreMilliSeconde().notify();
		}
		synchronized (linotte.getRegistreDesEtats().getAttendreSeconde()) {
			linotte.getRegistreDesEtats().getAttendreSeconde().notify();
		}
	}

	private void setStopLecture() {

		List<ThreadLinotte> all = new ArrayList<ThreadLinotte>();

		synchronized (threads) {
			for (List<ThreadLinotte> l : threads.values()) {
				synchronized (l) {
					all.addAll(l);
				}
			}
		}
		for (ThreadLinotte thread : all) {
			try {
				thread.forceToStop = true;
				thread.getJob().stop();
				// pour arrêter les verbes "attendre"
				thread.threadCourant.interrupt();
			} catch (NullPointerException e) {
				// Si getJob est null ? il sera arrêté grace à forceToStop =
				// true
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// On vide les evenements de la toile :
		for (PrototypeGraphique eg : evenements) {
			eg.cleanListener();
		}

		reveille();
		// On force la fermeture des tubes :
		for (Tube tube : tubes) {
			try {
				tube.fermer();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// On ferme les composants :
		for (Composant composant : composants) {
			try {
				composant.destruction();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		tuerTousLesMoteurs();
		// Appel de fonctions :
		// TODO Alizé
		// if (moteurPere != null)
		// moteurPere.setStopLecture(stopLecture);

		// On coupe le son :
		SonAction.closeChannel();

	}

	@Override
	public void initializeRuntime(AKRuntime akRuntime) {
		for (Object otoile : librairie.getToiles()) {
			LaToile toile = ((LaToile) otoile);
			if (toile != null && toile.getPanelLaToile() != null)
				toile.getPanelLaToile().annulerBuffer();
		}

		librairie.cleanEspece(getLinotte());
		Ressources.getInstance().toile = librairie.getToilePrincipale();
		if (!linotte.getLangage().isLegacy())
			habilitations.add(Habilitation.STACK_MEMORY_MANAGEMENT);
		// Evolution Linotte 2.0
		// Patch Pat pour le télétype :
		if (!habilitations.contains(Habilitation.MEMORY_FULL_ACCESS)) {
			for (Object otoile : librairie.getToiles()) {
				LaToile toile = ((LaToile) otoile);
				if (toile != null && toile.getPanelLaToile() != null)
					toile.getPanelLaToile().effacer();
			}
			if (librairie.getToilePrincipale() != null)
				librairie.getToilePrincipale().getPanelLaToile().effacer();
			librairie.vider();
		}
		try {
			Ressources.setCheminReference(reference);
			Ressources.clearCacheImages();
		} catch (Exception e) {
		}
		// Pour le webonotte et les évènements :
		jobPere = (Job) akRuntime.getJob();
		if (jobPere.getPere() != null && jobPere.getPere().isDead()) {
			jobPere.getPere().setRunning(true);
		}
		// On réinitialise des comportements systèmes :
		Mathematiques.context = MathContext.DECIMAL128;
		Mathematiques.angle = ANGLE.DEGREE;
	}

	@Override
	public void closeRuntime(AKRuntime akRuntime) {
		if (importationProcess)
			return;
		// Il faut supprimer le recepteur de la toile !
		for (Object otoile : librairie.getToiles()) {
			LaToile toile = ((LaToile) otoile);
			if (toile != null && toile.getPanelLaToile() != null)
				toile.getPanelLaToile().setRecepteur(null);
		}
		try {
			setStopLecture();
			boolean pleinecran = false; // Pour corriger le bogue de la toile
										// plein écran avec l'horloge douce
			if (librairie.getToilePrincipale() != null) {
				// Sortir du plein écran :
				GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
				pleinecran = device.getFullScreenWindow() == null ? false : true;
				Window element = device.getFullScreenWindow();
				if (device.isFullScreenSupported()) {
					device.setFullScreenWindow(null);
				}
				// A faire seulement si plein écran !!!
				if ( element == librairie.getToilePrincipale().getPanelLaToile().getToileParent()) {
					//System.out.println("fermeture");
					librairie.getToilePrincipale().setUndecorated(false);// empeche la toile de prendre toute la place de l'écran
				}
			}
			for (Object otoile : librairie.getToiles()) {
				LaToile toile = ((LaToile) otoile);
				// Afficheer la bordure :
				if (toile != null && toile.getFrameParent() != null) {
					ITransparence.getTransparence().setOpaque(toile.getFrameParent(), true);
				}
				if (toile != null && toile.isUndecorated()) {
					toile.dispose();
					toile.setUndecorated(false);
					if (!pleinecran)
						toile.setVisible(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Ressources.clearCacheImages();
			nettoyerMemoire();
		}

	}

	private void nettoyerMemoire() {
		((JobContext) jobPere.getContext()).nettoyerMemoire();
		librairie = null;
		// linotte = null;
		jobPere = null;
		ihm = null;
		reference = null;
		threads.clear();
		threads = null;
		tubes.clear();
		tubes = null;
		composants.clear();
		composants = null;
		evenements.clear();
		evenements = null;
		environnment = null;
		tableRuntime.clear();
		tableRuntime = null;
		habilitations.clear();
		habilitations = null;
		tests = null;
	}

	public void setContinuerLecture() {
		synchronized (linotte.getRegistreDesEtats().getPause()) {
			linotte.getRegistreDesEtats().getPause().notifyAll();
		}
	}

	public List<Composant> getComposants() {
		return composants;
	}

	public List<PrototypeGraphique> getEvenements() {
		return evenements;
	}

	public void setEvenements(List<PrototypeGraphique> evenements) {
		this.evenements = evenements;
	}

	public List<Tube> getTubes() {
		return tubes;
	}

	public void setTubes(List<Tube> tubes) {
		this.tubes = tubes;
	}

	public Map<String, List<ThreadLinotte>> getThreads() {
		return threads;
	}

	public File getReference() {
		return reference;
	}

	public void setReference(File reference) {
		this.reference = reference;
	}

	public void setEnvironnment(ParserEnvironnement environnment) {
		this.environnment = environnment;
	}

	public ParserEnvironnement getEnvironnment() {
		return environnment;
	}

	public void setImportationProcess(boolean pimportationProcess) {
		importationProcess = pimportationProcess;
	}

	public boolean isImportationProcess() {
		return importationProcess;
	}

	@Override
	public Object clone() {
		RuntimeContext context = new RuntimeContext();
		context.librairie = librairie.cloneMoi();
		context.linotte = linotte;
		context.ihm = ihm;
		context.reference = reference;
		context.environnment = environnment;
		context.debugger = debugger;
		if (habilitations != null && !habilitations.isEmpty())
			context.habilitations.addAll(habilitations);
		return context;
	}

	protected boolean addHabilitation(Habilitation habilitation) {
		return habilitations.add(habilitation);
	}

	public boolean canDo(Habilitation habilitation) {
		return habilitations.contains(habilitation);
	}

	public boolean removeHabilitation(Habilitation habilitation) {
		return habilitations.remove(habilitation);
	}
}