package console;

/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
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

import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alize.kernel.AKRuntime;
import org.linotte.frame.atelier.FrameIHM;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.latoile.Toile;
import org.linotte.implementations.ConsoleTexte;
import org.linotte.implementations.LibrairieVirtuelle;
import org.linotte.implementations.LibrairieVirtuelleSyntaxeV2;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.exception.Messages;
import org.linotte.moteur.exception.RetournerException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.outils.FichierOutils;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.actions.AfficherAction;
import org.linotte.moteur.xml.alize.kernel.ContextHelper;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.Parseur;
import org.linotte.moteur.xml.analyse.multilangage.Langage;
import org.linotte.moteur.xml.analyse.timbre.TraducteurTimbreEnTexte;
import org.linotte.moteur.xml.api.IHM;
import org.linotte.moteur.xml.api.Librairie;

public class Jinotte {

	private static final String[] OPTIONS = { "v", "m", "x", "b", "t", "d", "h", "x", "s", "w", "r", "1" };

	private static final List<String> LISTE_OPTIONS = Arrays.asList(OPTIONS);

	private Linotte interpreteur;

	public static AKRuntime runtime;

	private boolean verbose;

	private boolean showtime;

	private IHM ihm = null;

	private LaToile toile;

	private Set<String> options;

	private int posParametre = 0;

	private String[] parametre;

	public Jinotte() {

	}

	public Jinotte(LaToile ptoile) {
		// Toile déjà créée
		toile = ptoile;
	}

	public static void main(String p[]) {
		Jinotte jinotte = new Jinotte();
		jinotte.preparation(p);
		jinotte.lecture();
	}

	public void preparation(String[] pparametre) throws Error {
		parametre = pparametre;
		if (parametre.length == 0)
			exit();

		Librairie lib = null;
		options = new HashSet<String>();
		String nomIHM = null;
		String nomConnecteur = null;

		while (posParametre < parametre.length) {
			if (parametre[posParametre].startsWith("-i:")) {
				nomIHM = parametre[posParametre].substring(3);
			} else if (parametre[posParametre].startsWith("-l:")) {
				nomConnecteur = parametre[posParametre].substring(3);
			} else if (parametre[posParametre].startsWith("-")) {
				for (int i = 1; i < parametre[posParametre].length(); i++) {
					String op = parametre[posParametre].substring(i, i + 1);
					if (LISTE_OPTIONS.contains(op)) {
						options.add(op);
					} else {
						System.err.println(op + " : option inconnue !");
						exit();
					}
				}
			} else
				break;
			posParametre++;
		}

		// Choix connecteur :
		if (nomConnecteur != null) {
			try {
				lib = (Librairie) nouvelleInstance(nomConnecteur);
			} catch (Exception e) {
			}
			if (lib == null) {
				System.err.println("Impossible de charger le connecteur de librairie : " + nomConnecteur);
				exit();
			}
		} else {
			if (!options.contains("1"))
				lib = new LibrairieVirtuelleSyntaxeV2();
			else
				lib = new LibrairieVirtuelle("bibliothèque");

		}

		// Choix IHM :
		interpreteur = null;
		if (nomIHM != null) {
			try {
				ihm = (IHM) nouvelleInstance(nomIHM);
			} catch (Exception e) {
			}
			if (ihm == null) {
				System.err.println("Impossible de charger l'ihm : " + nomIHM);
				exit();
			}
		} else {
			ihm = new ConsoleTexte();
		}

		verbose = options.contains("b");
		showtime = options.contains("t");
		ConsoleTexte.setDEBUG(options.contains("d"));
		if (options.contains("v"))
			showversion();
		if (options.contains("h"))
			showhelp();

		interpreteur = new Linotte(lib, ihm, Langage.Linotte2);//options.contains("1"));

		if (options.contains("x")) {
			// Activation du mode graphique
			toile = Toile.initToile(getInterpreteur(), false, null, options.contains("s"), options.contains("w"), false, 0, 0);
			ihm = new FrameIHM((Frame) toile.getParent());
			interpreteur.setIhm(ihm);
			FrameIHM.setPopupMessage(true);
		}

		if (toile != null) {
			lib.setToile(toile);
		}

	}

	public void lecture() throws Error {

		//
		int erreurs = 0;
		List<Integer> numerolignes = new ArrayList<Integer>();
		for (; posParametre < parametre.length; posParametre++) {

			String file = parametre[posParametre];

			if (parametre.length > 1 && verbose)
				System.out.println("Lecture de : " + file);

			StringBuilder flux = null;
			File fichier = null;

			fichier = new File(file);
			try {
				
				// Prise en compte des fichiers graphiques :  **********************************************
				PushbackInputStream pis = new PushbackInputStream(new FileInputStream(fichier),2);		// *
				if (FichierOutils.estCeUnLivreVisuel(pis)) {											// *
					List<Timbre> timbres = FichierOutils.lireLivreGraphique(pis);						// *
					TraducteurTimbreEnTexte traducteur = new TraducteurTimbreEnTexte();					// *
					flux = new StringBuilder(traducteur.traiter(timbres, interpreteur.getLangage()));	// *
				} else {																				// *
					flux = FichierOutils.lire(fichier, numerolignes);									// *
				}																						// *
				// *****************************************************************************************
				
			} catch (IOException e) {
			}

			if (flux == null || flux.length() == 0) {
				System.err.println(file + " : fichier vide ou inexistant !");
				exit();
			}

			StringBuilder erreur = new StringBuilder();
			try {
				long time = System.currentTimeMillis();
				Parseur moteurXML = new Parseur();
				ParserContext atelierOutils = new ParserContext(MODE.GENERATION_RUNTIME);
				atelierOutils.linotte = interpreteur;
				runtime = moteurXML.parseLivre(flux, atelierOutils);
				List<Habilitation> habilitations = new ArrayList<Habilitation>();
				// Si applet, on peut accéder au cookie
				if (toile != null && Toile.isApplet())
					habilitations.add(Habilitation.COOKIES_ACCESS);
				ContextHelper.populate(runtime.getContext(), interpreteur, fichier, habilitations);
				try {
					runtime.execute();
				} catch (RetournerException e) {
					try {
						AfficherAction.afficher(e.getActeur(), interpreteur.getIhm());
					} catch (Exception e1) {
					}
				} catch (StopException s) {
				}
				long time2 = System.currentTimeMillis();
				if (showtime)
					System.out.println("Livre executé en (ms)" + (time2 - time));
			} catch (LectureException e) {
				erreurs++;
				if (e.getLivre() != null) {
					erreur.append("Livre : " + e.getLivre() + "\n");
				}
				erreur.append(Messages.retourneErreur(String.valueOf(e.getErreur())));
				if (e.getException().getToken() != null) {
					erreur.append(" : " + e.getException().getToken());
				}
				erreur.append(".\n");
				erreur.append("Ligne : " + retourneLaLigne(numerolignes, e.getPosition()));
			} catch (Exception e) {
				if (options.contains("d"))
					e.printStackTrace();
				erreur.append(Messages.retourneErreur(String.valueOf(e.getMessage())));
				erreur.append("\nErreur dans l'interpréteur !\n" + "Merci d'envoyer un email à ronan.mounes@amstrad.eu "
						+ "avec votre fichier et le jeu de données.");
			}
			if (erreur.length() > 0)
				ihm.afficherErreur(erreur.toString());
			numerolignes.clear();
		}

		if (erreurs > 0 && verbose) {
			System.err.println(erreurs + " erreur" + (erreurs == 1 ? "" : "s") + " !");
		}

		// Ne pas faire en mode applet :
		Preference.getIntance().save();
		System.exit((erreurs > 0) ? 1 : 0);
	}

	public void showversion() {
		System.out.println("Jinotte * version : " + Version.getVersion());
	}

	public static void showhelp() {
		System.out.println("Usage : Jinotte [option]* [chemin_vers_nom_livre.liv]+");
		// System.out.println(" -m Ne pas écrire les acteurs sur disque");
		System.out.println("        -x Autoriser l'affichage de la toile");
		//System.out.println("        -r Activation du mode Braille (en test)");
		System.out.println("        -s Affichage d'un icone dans le sysTray");
		System.out.println("        -w Affiche la fenetre dans la barre des tâches");
		System.out.println("        -v Affiche la version du programme");
		System.out.println("        -b Mode bavard");
		System.out.println("        -h Affiche les commandes disponibles");
		System.out.println("        -d Affichage des traces DEBUG");
		System.out.println("        -t Affiche le temps d'execution en milliseconde");
		System.out.println("        -1 Force la compatibilite Linotte 1.X");
		// System.out.println("        -g:fichier.xml Charge une autre grammaire");
		// System.out.println("        -l:classe.class Charge un autre connecteur de librairie");
		// /System.out.println("        -i:classe.class Charge une autre IHM");
		System.out.println();
		System.out.println(Version.LICENCE);
	}

	public void exit() {
		showversion();
		showhelp();
		System.exit(0);
	}

	public static int retourneLaLigne(List<Integer> numeros, int position) {
		int ligne = 0;
		for (int entier : numeros) {
			ligne++;
			if (entier > position)
				break;

		}
		return ligne;
	}

	public static Object nouvelleInstance(String classe) {
		Object o = null;
		try {
			o = Class.forName(classe).newInstance();
		} catch (Exception e) {
		} catch (NoClassDefFoundError e) {
		}
		return o;
	}

	public void setInterpreteur(Linotte interpreteur) {
		this.interpreteur = interpreteur;
	}

	public Linotte getInterpreteur() {
		return interpreteur;
	}

	public AKRuntime getRuntime() {
		return runtime;
	}

}