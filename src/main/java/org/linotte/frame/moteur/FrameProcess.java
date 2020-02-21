/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : November 30, 2005                            *
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

package org.linotte.frame.moteur;

import java.awt.Color;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import org.alize.kernel.AKException;
import org.alize.kernel.AKJob;
import org.alize.kernel.AKRuntime;
import org.linotte.frame.Atelier;
import org.linotte.frame.atelier.Inspecteur;
import org.linotte.frame.cahier.Cahier;
import org.linotte.frame.coloration.ProcessStyle;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.exception.Messages;
import org.linotte.moteur.exception.RetournerException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.actions.AfficherAction;
import org.linotte.moteur.xml.alize.kernel.ContextHelper;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.AKDebugger;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.Parseur;
import org.linotte.moteur.xml.appels.Appel;
import org.linotte.moteur.xml.appels.Fonction;

import console.Jinotte;

public class FrameProcess extends Thread {

	public static final Color RUN = new Color(107, 169, 128);

	static Linotte lecteur = null;

	private StringBuilder buffer = null;

	// Les premieres versions de l'atelier etaient sous la forme d'une applet
	public static Atelier applet = null;

	boolean start = false;

	private static FrameProcess this_ = null;

	private boolean test;

	private int delay;

	ProcessStyle processStyle = null;

	private static AKRuntime runtime;

	public static long t1, t2 = 0;

	private AKDebugger debugger = null;

	public FrameProcess(Linotte param, Atelier ap) {
		super("FrameProcess");
		lecteur = param;
		applet = ap;
		debugger = new Debogueur(ap);
		// setPriority(MAX_PRIORITY);
	}

	public static void go(StringBuilder f, Linotte param, Atelier ap, boolean test, int delay) {
		this_ = new FrameProcess(param, ap);
		this_.test = test;
		this_.delay = delay;
		this_.buffer = f;
		if (!this_.start) {
			this_.start = true;
			this_.start();
		}
	}

	public static void stopProcess() throws StopException {
		if (this_ != null) {
			// Arrêt des verbes Attendre
			this_.interrupt();
			synchronized (runtime.getJob()) {
				try {
					runtime.getJob().notifyAll();
				} catch (Exception e) {
					e.printStackTrace();
				}
				runtime.getJob().stop();
			}
		} else {
			// on est passé par la ligne de commande :
			//TODO A déplacer
			synchronized (Jinotte.runtime.getJob()) {
				try {
					Jinotte.runtime.getJob().notifyAll();
				} catch (Exception e) {
					e.printStackTrace();
				}
				Jinotte.runtime.getJob().stop();
			}
		}
	}

	public void action() throws InterruptedException {
		applet.getJButtonLire().setEnabled(false);
		applet.getJButtonLire().setVisible(true);
		applet.getJButtonStop().setEnabled(true);
		applet.getJButtonPause().setVisible(true);
		applet.getJButtonPause().setEnabled(false);
		applet.getJButtonTester().setEnabled(false);

		List<Image> l = new ArrayList<Image>();
		l.add(Ressources.getImageIcon("linotte_run.png").getImage());
		applet.setIconImages(l);

		Inspecteur.lock(false);
		// On vide le cache des touches :
		applet.getToile().getPanelLaToile().videTouches();
		Cahier cahierCourant = applet.getCahierCourant();
		// Linotte 2.0, pour l'utilisation des librairies
		Ressources.getInstance().viderUrlLoader();
		try {
			t2 = 0;
			t1 = System.currentTimeMillis();
			Parseur moteurXML = new Parseur();
			ParserContext atelierOutils = new ParserContext(MODE.GENERATION_RUNTIME);
			atelierOutils.linotte = lecteur;
			runtime = moteurXML.parseLivre(buffer, atelierOutils);
			ContextHelper.populate(runtime.getContext(), lecteur, cahierCourant.getFichier(), null);
			cahierCourant.setCouleur(RUN, false);
			final RuntimeContext runtimeContext = (RuntimeContext) runtime.getContext();
			runtimeContext.doTest = test;
			runtimeContext.delayPasAPas = delay;
			runtimeContext.debugger = debugger;
			runtimeContext.cahier = cahierCourant;
			runtimeContext.buffer = buffer;
			runtime.execute();
			cahierCourant.setCouleur(null, true);
		} catch (LectureException e) {

			// Afin d'affiner l'aide utilisateur, on recupère le numéro de ligne
			// :
			List<Integer> numerolignes = new ArrayList<Integer>();
			try {
				FrameProcess.lire(buffer, numerolignes);
			} catch (IOException ei) {
				// Tant pis pour le numéro de ligne..
			}
			int nbligne = Jinotte.retourneLaLigne(numerolignes, e.getPosition());

			cahierCourant.setCouleur(null, true);
			if (Version.isBeta())
				e.getException().printStackTrace();
			String erreur = "";
			if (e.getLivre() != null) {
				erreur = "Livre : " + e.getLivre() + "\n";
			}
			boolean nePasAffinerMessage = true;
			// Affichage de la phrase analysée :
			if ((e.getException() instanceof SyntaxeException) && ((SyntaxeException) e.getException()).getPhrase() != null) {
				applet.ecrireErreurTableau("Début de phrase analysée : " + ((SyntaxeException) e.getException()).getPhrase());
				nePasAffinerMessage = false;
			}

			// Affiche de la pile :
			if (e.getKernelStack() != null) {
				Iterator<Appel> i = e.getKernelStack().iterator();
				while (i.hasNext()) {
					Appel a = i.next();
					if (a instanceof Fonction) {
						// Fonction f = (Fonction) a;
						// applet.ecrireErreurTableau(f.toString());
					}
				}
			}

			if (cahierCourant.isVisualiserTimbre()) {
				cahierCourant.getjPanelTimbre().setTimbreErreur(nbligne);
			}
			if (nbligne > 0)
				erreur += "Ligne " + nbligne + ", ";

			erreur += Messages.retourneErreur(String.valueOf(e.getErreur()));
			if (e.getException().getToken() != null)
				erreur += " : " + e.getException().getToken();
			applet.ecrireErreurTableau(erreur);

			if (e.getLivre() == null) {
				try {
					String cleanDoc = cahierCourant.retourneTexteCahier();

					if (cleanDoc.length() != 0) {

						int position = e.getPosition();
						if (e.getPosition() == cleanDoc.length()) // Bug quand
																	// on est à
																	// la fin du
																	// fichier
							while (cleanDoc.charAt(position) == '\n') {
								position--;
								if (position == -1) {
									position = 0;
									break;
								}
							}

						int debut = position;
						if (nePasAffinerMessage) {
							debut = cleanDoc.lastIndexOf("\n", debut);
							if (debut == -1)
								debut = 0;
						}
						int fin = position;
						fin = cleanDoc.indexOf("\n", fin);
						if (fin == -1)
							fin = cleanDoc.length();

						cahierCourant.setSelectionCachier(debut, fin);
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		} catch (RetournerException e) {
			try {
				AfficherAction.afficher(e.getActeur(), lecteur.getIhm());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			cahierCourant.setCouleur(null, true);
		} catch (StopException e) {
			cahierCourant.setCouleur(null, true);
		} catch (AKException e) {
			cahierCourant.setCouleur(null, true);
			applet.ecrireErreurTableau(e.getMessage());
			applet.ecrirelnTableau("");
		} catch (StackOverflowError e) {
			cahierCourant.setCouleur(null, true);
			applet.ecrireErreurTableau("Trop de boucles imbriquées ! Le livre doit s'arrêter...");
		} catch (Throwable e) {
			boolean memoryLeak = e instanceof OutOfMemoryError;
			cahierCourant.setCouleur(null, true);
			if (memoryLeak)
				applet.ecrireErreurTableau("La mémoire a été saturée par votre programme !");
			else {
				applet.ecrireErreurTableau("Bravo ! vous avez trouvé un bogue dans l'interprète...");

				/*applet.ecrireErreurTableau("Merci de m'envoyer un mail avec votre livre");
				applet.ecrireErreurTableau("afin que je puisse le corriger. Merci !");
				applet.ecrireErreurTableau("mail : ronan.mounes@amstrad.eu");*/
				e.printStackTrace();

				int r = JOptionPane.showConfirmDialog(null,
						"Bravo ! Vous avez trouvé un bogue dans l'interprète Linotte... ça arrive :-(\n"
								//+ (memoryLeak ? "La mémoire a été saturée par votre programme !\n" : "")
								+ "Afin de m'aider à améliorer l'interprète, un message contenant l'erreur va m'être envoyé pour être corrigé.",
						"Bogue dans l'Atelier Linotte !", JOptionPane.ERROR_MESSAGE, JOptionPane.DEFAULT_OPTION, Ressources.getImageIcon("face-devilish.png"));
				if (r == 0) {
					boolean envoye = envoyerErreur(e, cahierCourant.getFichier());
					JOptionPane.showMessageDialog(null, envoye ? "Merci ! :-)" : "Impossible d'envoyer l'erreur :-(");
				}

			}

		} finally {
			if (cahierCourant.isVisualiserTimbre()) {
				cahierCourant.getjPanelTimbre().setTimbreDeboguage(-1);
			}
			cahierCourant.setCouleur(null, false);
			cahierCourant.getEditorPanelCahier().getLineHighlighter().setPositionDeboguage(-1);
			long t3 = System.currentTimeMillis();
			t2 = t3 - t1;
			t1 = 0;
			applet.getJButtonLire().setEnabled(true);
			applet.getJButtonLire().setVisible(true);
			applet.getJButtonStop().setEnabled(false);
			applet.getJButtonPause().setVisible(true);
			applet.getJButtonPause().setEnabled(true);
			applet.getJButtonContinuer().setVisible(false);
			applet.getJButtonTester().setEnabled(true);
			// Mode séquentiel
			if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
				applet.getJButtonPause().setToolTipText(Debogueur.TOOLTIP_BOUTON_RALENTI);
			applet.getJButtonPause().setText(Debogueur.LIRE_AU_RALENTI);
			Inspecteur.lock(true);
			runtime = null;
			this_ = null;
			cahierCourant.focus();
			List<Image> l2 = new ArrayList<Image>();
			l2.add(Ressources.getImageIcon("linotte_new.png").getImage());
			applet.setIconImages(l2);
		}
	}

	/**
	 * @param e
	 * @param file 
	 * @return
	 */
	private boolean envoyerErreur(Throwable e, File file) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String id = "";
			id += "; os.name=" + System.getProperty("os.name");
			id += "; os.version=" + System.getProperty("os.version");
			id += "; java.vendor=" + System.getProperty("java.vendor");
			id += "; java.version=" + System.getProperty("java.version");
			id += "; os.arch=" + System.getProperty("os.arch");
			id += "; livre=" + file != null ? file.getCanonicalPath() : "-";

			String date = new SimpleDateFormat("yyyy MM dd à HH:mm:ss").format(Calendar.getInstance().getTime());
			String message = encode(date + " - Linotte " + Version.getVersion() + " " + encode(e.getMessage()));
			String trace = encode(id + "\n" + sw.toString());

			URLConnection conn = new URL("http://langagelinotte.no-ip.org:8087/stockbogue.liv?message=" + message + "&trace=" + trace).openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			final int MAX_LENGTH = 20000;
			byte[] buf = new byte[MAX_LENGTH];
			int total = 0;
			while (total < MAX_LENGTH) {
				int count = is.read(buf, total, MAX_LENGTH - total);
				if (count < 0) {
					break;
				}
				total += count;
			}
			is.close();

			String xml = new String(buf, 0, total);
			System.out.println(xml);
			return xml.startsWith("ok");

		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
	}

	private String encode(String url) throws UnsupportedEncodingException {
		if (url != null)
			return URLEncoder.encode(url, "UTF-8");
		else
			return "NPE !";
	}

	@Override
	public void run() {
		if (applet.isVisible()) {
			try {
				action();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void continuerDebogage() {
		synchronized (runtime.getJob()) {
			reveillerJob(runtime, false);
		}
	}

	public static void stopDebogage() {
		synchronized (runtime.getJob()) {
			reveillerJob(runtime, true);
		}
	}

	public static void changeDelayDebogage(int delayPasAPas) {
		if (runtime != null)
			synchronized (runtime) {
				((RuntimeContext) runtime.getContext()).delayPasAPas = delayPasAPas;
			}
	}

	/**
	 * Réveille les jobs en attente lors d'un mode debug
	 * 
	 * @param r
	 * @param stop
	 * 
	 */
	private static void reveillerJob(AKRuntime r, boolean stop) {
		synchronized (r.getJob()) {
			if (stop) {
				((Job) r.getJob()).setDebogueur(false);
			}
			r.getJob().notifyAll();
		}
		// Bogue remonté par Pat sur le débogage des fonctions :
		List<AKJob> fils = r.getJob().getSons();
		if (fils != null) {
			synchronized (fils) {
				for (AKJob akJob : fils) {
					synchronized (akJob) {
						if (stop) {
							((Job) akJob).setDebogueur(false);
						}
						akJob.notifyAll();
					}
				}
			}
		}
	}

	public static void lire(StringBuilder buffer, List<Integer> numerolignes) throws IOException {
		// Probablement pas optimum au niveau gestion de la mémoire
		BufferedReader br = new BufferedReader(new StringReader(buffer.toString()));
		int position = 0;
		String str;
		while ((str = br.readLine()) != null) {
			position += str.length() + 1;
			numerolignes.add(position);
		}
		br.close();
	}

}