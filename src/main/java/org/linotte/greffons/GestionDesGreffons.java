/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 5, 2011                              *
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

import static org.linotte.moteur.xml.actions.TildeAction.STATUS.A_JOUR;
import static org.linotte.moteur.xml.actions.TildeAction.STATUS.METTRE_A_JOUR;
import static org.linotte.moteur.xml.actions.TildeAction.STATUS.NOUVEAU;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import org.alize.kernel.AKRuntime;
import org.linotte.frame.Atelier;
import org.linotte.frame.latoile.LaToile;
import org.linotte.greffons.api.Greffon;
import org.linotte.greffons.externe.Graphique;
import org.linotte.greffons.java.GreffonPrototype;
import org.linotte.greffons.java.GreffonPrototype.Entrée;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.outils.FichierOutils;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.RegistreDesActions;
import org.linotte.moteur.xml.actions.TildeAction.STATUS;
import org.linotte.moteur.xml.alize.kernel.ContextHelper;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.Parseur;
import org.linotte.moteur.xml.api.Librairie;

/**
 * Chargement de greffons depuis la commande tilde greffon
 * @author CPC
 *
 */
public class GestionDesGreffons {

	private static final Map<File, Long> cacheGreffonsLinotte = new HashMap<>();
	private static Timer timer;

	public static void chargerGreffon(final String commande, final RuntimeContext runtimeContext) throws Exception {
		chargerGreffon(commande, runtimeContext.getLinotte(), true);
	}

	public static boolean chargerGreffon(final String commande, final Linotte linotte, boolean texteLivre) throws Exception {
		Scanner scanner = new Scanner(commande);
		try {
			@SuppressWarnings("unused")
			int version_protocole = scanner.nextInt();
			//System.out.println("GDG : Version protocole " + version_protocole);

			String nom = scanner.next();
			Greffon greffon_temp = GreffonsChargeur.getInstance().getGreffon(nom);
			double versionDemandee = Double.valueOf(scanner.next());
			STATUS etat;
			if (greffon_temp == null) {
				// Greffon n'est pas present :
				etat = NOUVEAU;
			} else {
				// Mise à jour d'un greffon :
				double versionActuelle = Double.valueOf(greffon_temp.getVersion());
				etat = versionDemandee > versionActuelle ? METTRE_A_JOUR : A_JOUR;
				//runtimeContext.getIhm().afficherErreur("Demande de mise à jour du greffon !");
			}

			if (etat != A_JOUR) {
				StringBuffer t = new StringBuffer();
				while (scanner.hasNext()) {
					t.append(scanner.next());
				}
				// On supprime les guillemets :
				URL url = new URL(t.substring(1, t.length() - 1));
				Object[] options = { etat == NOUVEAU ? "Installer" : "Mettre à jour", "Non, merci" };
				int n = JOptionPane
						.showOptionDialog(null,
								(texteLivre ? ("Ce livre a besoin " + (etat == NOUVEAU ? "de l'installation d'un nouveau" : "d'une mise à jour d'un")
										+ " greffon.\n") : "") + "Voulez-vous télécharger la dernière version du greffon " + nom.toUpperCase()
								+ " depuis l'URL :\n" + url.toURI().toString(), "Gestionnaire des greffons", JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.WARNING_MESSAGE, Ressources.getImageIcon("applications-other.png"), options, options[1]);

				if (n == 0) {
					URLConnection connection = url.openConnection();
					int length = connection.getContentLength();
					//System.out.println("GDG : Taille " + length);
					//System.out.println("GDG : Type " + connection.getContentType());
					String f = extractFileName(connection.getURL().getFile());
					//System.out.println("GDG : Nom du fichier " + f);

					File pere = new File(
							Preference.getIntance().getHome() + File.separator + Preference.REPERTOIRE + File.separator + GreffonsHandler.DIR_USER);
					pere.mkdirs();

					File out = new File(pere, f);

					try {

						java.io.BufferedInputStream in = new java.io.BufferedInputStream(connection.getInputStream());
						java.io.FileOutputStream fos = new java.io.FileOutputStream(out);
						java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
						// Téléchargement
						byte[] data = new byte[1024];
						int x = 0;
						while ((x = in.read(data, 0, 1024)) >= 0) {
							bout.write(data, 0, x);
							Thread.sleep(10); // smooth it !
						}
						bout.close();
						in.close();

					} finally {
					}

					Librairie lib = linotte.getLibrairie();

					if (etat == NOUVEAU) {
						List<Greffon> greffons = GreffonsHandler.chargerGreffonGZL(out);
						for (Greffon greffon : greffons) {

							greffon = GreffonsChargeur.getInstance().getGreffon(greffon.getId().toLowerCase());

							// On enregistre ce greffon au niveau de l'interprete :
							try {
								if (greffon.getObjet() instanceof Graphique) {
									Acteur position = new Acteur(null, "position", Role.NOMBRE, new BigDecimal(0), null);
									Acteur visible = new Acteur(null, "visible", Role.TEXTE, "non", null);

									PrototypeGraphique espece = new PrototypeGraphique(null, lib, greffon.getId(), Role.ESPECE, greffon.getId(), null,
											PrototypeGraphique.TYPE_GRAPHIQUE.GREFFON, GreffonsChargeur.getInstance().newInstance(greffon.getId()));
									if (!PrototypeGraphique.isEspecesGraphique_(greffon.getId()))
										linotte.especeModeleMap.add(espece);
									lib.addEspece(espece);
									GreffonPrototype.chargeAttributGreffon(espece, greffon);
									espece.addAttribut(visible);
									espece.addAttribut(position);
									// Ajout des méthodes fonctionnelles :
									for (Entrée entrée : greffon.getSlots()) {
										espece.ajouterSlotGreffon(entrée.getKey(), entrée.getValue());
									}
								} else if (greffon.getObjet() instanceof org.linotte.greffons.externe.Greffon) {
									Prototype espece = new Prototype(lib, greffon.getId(), Role.ESPECE, greffon.getId(), null,
											GreffonsChargeur.getInstance().newInstance(greffon.getId()));
									if (!PrototypeGraphique.isEspecesGraphique_(greffon.getId()))
										linotte.especeModeleMap.add(espece);
									lib.addEspece(espece);
									GreffonPrototype.chargeAttributGreffon(espece, greffon);
									// Ajout des méthodes fonctionnelles :
									for (Entrée entrée : greffon.getSlots()) {
										espece.ajouterSlotGreffon(entrée.getKey(), entrée.getValue());
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}

					} else {
						JOptionPane.showMessageDialog(null,
								"Le téléchargement a été effectué. Linotte va fermer.\nMerci de le relancer pour prendre en compte le nouveau greffon.");
						Frame frame = null;

						try {
							LaToile toile = lib.getToilePrincipale();
							frame = toile.getFrameParent();
						} catch (Exception e) {
						}

						if (frame != null) {
							WindowEvent wev = new WindowEvent(frame, WindowEvent.WINDOW_CLOSING);
							Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(wev);
						} else {
							System.exit(0);
						}
					}
				} else {
					return false;
				}

			}
		} finally {
			scanner.close();
		}
		return true;
	}

	private static String extractFileName(String path) {

		if (path == null) {
			return null;
		}
		String newpath = path.replace('\\', '/');
		int start = newpath.lastIndexOf("/");
		if (start == -1) {
			start = 0;
		} else {
			start = start + 1;
		}
		String pageName = newpath.substring(start, newpath.length());

		return pageName;
	}

	/**
	 * TODO ce bout de code doit être déplacé ailleur.
	 * @param lib
	 * @param linotte
	 */
	public static void initGreffons(Librairie<?> lib, Linotte linotte) {
		Acteur visible = new Acteur(null, "visible", Role.TEXTE, "non", null);
		Acteur position = new Acteur(null, "position", Role.NOMBRE, new BigDecimal(0), null);
		Acteur attributtoile = new Acteur(null, "toile", Role.TEXTE, null, null);
		final LaToile toile = null;

		// Init des greffons :
		try {
			List<Greffon> greffons = GreffonsChargeur.getInstance().getGreffons();
			for (Greffon greffon : greffons) {
				try {
					if (greffon != null) {
						if (greffon.getObjet() instanceof Graphique) {
							PrototypeGraphique espece = new PrototypeGraphique(toile, lib, greffon.getId(), Role.ESPECE, greffon.getId(), null,
									PrototypeGraphique.TYPE_GRAPHIQUE.GREFFON, GreffonsChargeur.getInstance().newInstance(greffon.getId()));
							if (!PrototypeGraphique.isEspecesGraphique_(greffon.getId()))
								linotte.especeModeleMap.add(espece);
							GreffonPrototype.chargeAttributGreffon(espece, greffon);
							espece.addAttribut(visible);
							espece.addAttribut(position);
							espece.addAttribut(attributtoile);
							// Ajout des méthodes fonctionnelles :
							for (Entrée entrée : greffon.getSlots()) {
								espece.ajouterSlotGreffon(entrée.getKey(), entrée.getValue());
							}
						} else if (greffon.getObjet() instanceof org.linotte.greffons.externe.Greffon) {
							Prototype espece = new Prototype(lib, greffon.getId(), Role.ESPECE, greffon.getId(), null,
									GreffonsChargeur.getInstance().newInstance(greffon.getId()));
							if (!PrototypeGraphique.isEspecesGraphique_(greffon.getId()))
								linotte.especeModeleMap.add(espece);
							GreffonPrototype.chargeAttributGreffon(espece, greffon);
							// Ajout des méthodes fonctionnelles :
							for (Entrée entrée : greffon.getSlots()) {
								espece.ajouterSlotGreffon(entrée.getKey(), entrée.getValue());
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (java.security.AccessControlException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Chargement des livres Linotte pour être utilisés en tant que greffon
	 * @param linotte
	 */
	public static void initGreffonsLinotte(Linotte linotte) {

		try {
			URL urls[] = { new File(Preference.getIntance().getHome() + File.separator + Preference.REPERTOIRE + File.separator + GreffonsHandler.DIR_USER)
					.toURI().toURL(), Ressources.getGreffons().toURI().toURL() };

			for (URL url : urls) {
				// Est-ce un répertoire ?
				if (new File(url.toURI()).isDirectory())
					for (File file : new File(url.toURI()).listFiles(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String fileName) {
							return fileName.endsWith(".liv");
						}
					})) {
						chargerGreffonLinotte(linotte, file, false);
					}
				;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param linotte
	 * @param file
	 */
	private static void chargerGreffonLinotte(Linotte linotte, File file, boolean rechargement) {
		try {
			cacheGreffonsLinotte.put(file, file.lastModified());
			// On parse :
			Parseur moteurXML = new Parseur();
			ParserContext atelierOutils = new ParserContext(MODE.GENERATION_RUNTIME);
			atelierOutils.linotte = linotte;
			AKRuntime runtime = moteurXML.parseLivre(FichierOutils.lire(file, new ArrayList<Integer>()), atelierOutils);
			// On prépare l'environnement :
			ContextHelper.populate(runtime.getContext(), linotte, file, null);
			RuntimeContext rcontext = (RuntimeContext) runtime.getContext();
			// Pour ne lire que les définitions des espèces :
			rcontext.setImportationProcess(true);
			rcontext.setLibrairie(linotte.getLibrairie().cloneMoi());
			// On exécute !
			runtime.execute();
			// On récupère l'espèce :
			String nom = file.getName().substring(0, file.getName().lastIndexOf('.'));
			Prototype espece = rcontext.getLibrairie().getEspece(nom);
			if (espece != null) {
				// On supprime le greffon Linotte déjà présent dans le cas d'un rechargement :
				for (Iterator iterator = linotte.especeModeleMap.iterator(); iterator.hasNext();) {
					Prototype type = (Prototype) iterator.next();
					if (type.getNom().equals(espece.getNom())) {
						linotte.especeModeleMap.remove(type);
						break;
					}
				}
				linotte.especeModeleMap.add(espece);
				//RegistreDesActions.ajouterErreur("Greffon Linotte " + (rechargement ? "re-" : "") + "chargé : " + nom);
			} else {
				RegistreDesActions.ajouterErreur("Erreur, greffon Linotte non trouvé : " + nom);
			}
		} catch (Exception e) {
			//e.printStackTrace();
			RegistreDesActions.ajouterErreur("Erreur, greffon Linotte non chargé : " + file.getName());
		}
	}

	/**
	 * Cette méthode va vérifier périodiquement si un greffon java a été modifié, et le cas échant, le recharger.
	 * @param atelier
	 */
	public static void verifierGreffons(final Atelier atelier) {
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				/**
				 * On supprime du cache les livres modifiés
				 */
				Set<File> greffonmodifies = new HashSet<>();
				synchronized (cacheGreffonsLinotte) {
					for (File f : cacheGreffonsLinotte.keySet()) {
						long age = cacheGreffonsLinotte.get(f);
						if (f.lastModified() != age) {
							greffonmodifies.add(f);
							chargerGreffonLinotte(Atelier.linotte, f, true);

						}
					}
				}
				for (File file : greffonmodifies) {
					cacheGreffonsLinotte.put(file, file.lastModified());
				}
				// Affichage des messages d'erreur
				Iterator<String> erreurs = RegistreDesActions.retourneErreurs();
				while (erreurs.hasNext()) {
					atelier.ecrireErreurTableau_(erreurs.next());
					atelier.ecrirelnTableau("");
				}
				RegistreDesActions.effaceErreurs();

			}
		}, 10000, 5000);
	}

}
