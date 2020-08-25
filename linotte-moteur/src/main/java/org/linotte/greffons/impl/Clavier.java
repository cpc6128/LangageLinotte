package org.linotte.greffons.impl;

import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.linotte.frame.latoile.LaToileListener;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.DocumentationHTML;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.ThreadLinotte;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.util.List;
import java.util.*;

@DocumentationHTML(""
		+ "Le greffon <i>Clavier</i> est une espèce offrant la possibilité d'écouter le clavier.<br>"
		+ "<u>Les méthodes fonctionnelles proposées par l'espèce Clavier sont:</u><br>"
		+ "<ul>"
		+ "<li>clavier.commencer() : commencer à écouter le clavier</li>"
		+ "<li>clavier.touches() : retourne un casier de textes contenant les touches enfoncées</li>"
		+ "<li>clavier.enregistremolettehaut(parag&lt;texte>) : le paragraphe nommé 'parag' est appellé à chaque fois que la molette est tournée vers le haut.</li>"
		+ "<li>clavier.enregistremolettebas(parag&lt;texte>) : le paragraphe nommé 'parag' est appellé à chaque fois que la molette est tournée vers le bas.</li>"
		+ "<li>clavier.stop() : arrête d'écouter le clavier</li>" + "</ul>")
public class Clavier extends Greffon {

	private static Set<String> touches = Collections.synchronizedSet(new HashSet<String>());

	private static String moletteUpParagraphe;
	private static String moletteDownParagraphe;
	private static Processus moletteUpProcessus;
	private static Processus moletteDownProcessus;
	private static LaToileListener toile;

	private static AWTEventListener listenerKey = new AWTEventListener() {

		@Override
		public void eventDispatched(AWTEvent event) {
			if (toile != null && event.getSource() == getToile()) {
				try {
					KeyEvent evt = (KeyEvent) event;
					if (evt.getID() == KeyEvent.KEY_PRESSED) {
						touches.add(KeyEvent.getKeyText(evt.getKeyCode()));
					} else if (evt.getID() == KeyEvent.KEY_RELEASED) {
						touches.remove(KeyEvent.getKeyText(evt.getKeyCode()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	private static AWTEventListener listenerMouse = new AWTEventListener() {
		private int clickUp = 0, clickDown = 0;

		@Override
		public void eventDispatched(AWTEvent event) {
			if (toile != null && event.getSource() == getToile()) {
				try {
					MouseWheelEvent evt = (MouseWheelEvent) event;
					int max = evt.getScrollAmount();
					if (evt.getWheelRotation() < 0) {
						clickUp -= evt.getUnitsToScroll() - 1;
						if (clickUp > max) {
							Job job = getCurrentJob();
							if (job != null)
								execution(job, moletteUpParagraphe, moletteUpProcessus);
							clickUp = 0;
						}
					} else if (evt.getWheelRotation() > 0) {
						clickDown += evt.getUnitsToScroll() + 1;
						if (clickDown > max) {
							Job job = getCurrentJob();
							if (job != null)
								execution(getCurrentJob(), moletteDownParagraphe, moletteDownProcessus);
							clickDown = 0;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};

	private static boolean init = false;

	private static synchronized void ecouter() {
		if (!init) {
			Toolkit.getDefaultToolkit().addAWTEventListener(listenerKey, AWTEvent.KEY_EVENT_MASK);
			Toolkit.getDefaultToolkit().addAWTEventListener(listenerMouse, AWTEvent.MOUSE_WHEEL_EVENT_MASK);
			init = true;
		}
	}

	private static synchronized void stopEvents() {
		Toolkit.getDefaultToolkit().removeAWTEventListener(listenerKey);
		Toolkit.getDefaultToolkit().removeAWTEventListener(listenerMouse);
		init = false;
		touches.clear();
	}

	/**
	 * http://stackoverflow.com/questions/458756/java-system-wide-keyboard-shortcut
	 * @return
	 * @throws GreffonException 
	 */
	@Slot
	public boolean commencer() throws GreffonException {
		toile = getToile();
		ecouter();
		return true;
	}

	@Slot
	public boolean stop() {
		stopEvents();
		return true;
	}

	@Slot
	public List<String> touches() {
		Object[] t = (Object[]) touches.toArray();
		List<String> retour = new ArrayList<String>();
		for (Object string : t)
			retour.add((String) string);
		return retour;
	}

	@Slot(nom = "enregistremolettehaut")
	public boolean enregistreMoletteHaut(String paragraphe) throws GreffonException {
		Job job = getCurrentJob();
		moletteUpProcessus = rechercher(job, paragraphe);
		moletteUpParagraphe = paragraphe;
		return true;
	}

	@Slot(nom = "enregistremolettebas")
	public boolean enregistreMoletteBas(String paragraphe) throws GreffonException {
		Job job = getCurrentJob();
		moletteDownProcessus = rechercher(job, paragraphe);
		moletteDownParagraphe = paragraphe;
		return true;
	}

	/*
	 * ******************
	 * METHODES PRIVEES :
	 * ******************
	 */

	private Processus rechercher(Job job, String paragraphe) throws GreffonException {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		ParserEnvironnement environnement = runtimeContext.getEnvironnment();
		Processus processus = environnement.getParagraphe(paragraphe);

		if (processus == null) {
			throw new GreffonException("Paragraphe inconnu : " + paragraphe);
		}
		return processus;
	}

	/**
	 * @param job
	 * @param paragraphe
	 * @param runtimeContext
	 * @param processus
	 */
	private static void execution(Job job, String paragraphe, Processus processus) {
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
		//Executor executor = Executors.newSingleThreadExecutor();
		//executor.execute(threadLinotte);
	}

	private static Job getCurrentJob() throws GreffonException {
		try {
			for (AKRuntime runtime : AKPatrol.runtimes) {
				return (Job) runtime.getJob();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static LaToileListener getToile() {
		try {
			for (AKRuntime runtime : AKPatrol.runtimes) {
				RuntimeContext context = (RuntimeContext) runtime.getContext();
				return context.getLibrairie().getToilePrincipale().getPanelLaToile();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
