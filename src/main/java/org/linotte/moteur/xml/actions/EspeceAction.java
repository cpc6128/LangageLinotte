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

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.latoile.LaToileJDialog;
import org.linotte.frame.latoile.Toile;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Fichier;
import org.linotte.moteur.entites.Livre;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.ArrayIterator;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.outils.ChaineOutils;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.api.Librairie;
import org.linotte.moteur.xml.appels.Appel;
import org.linotte.moteur.xml.appels.CalqueParagraphe;
import org.linotte.moteur.xml.appels.Fonction;
import org.linotte.moteur.xml.appels.SousParagraphe;

public class EspeceAction extends Action implements IProduitCartesien {

	public static final String NOM_ETAT = "creation espèce";

	public EspeceAction() {
		super();
	}

	@Override
	public String clef() {
		return NOM_ETAT;
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		
		if ("debut".equals(param) || "fin".equals(param))
			jobContext.setCreationespece("debut".equals(param));
		else {
			if (runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
				throw new ErreurException(Constantes.MODE_CONSOLE);			
			}			
		}
		Iterator<ItemXML> i1 = new ArrayIterator<ItemXML>(valeurs);
		boolean fichier = ChaineOutils.findInArray(annotations, "fichier");
		boolean creation_dynamique = ChaineOutils.findInArray(annotations, "creation");
		boolean heritage = ChaineOutils.findInArray(annotations, "héritage");
		boolean forcerLivre = ChaineOutils.findInArray(annotations, "livre");
		Fichier lefichier = null;

		if ("creation".equals(param)) {
			String nom_espece = i1.next().toString().toLowerCase();
			Prototype espece;
			if (heritage) {
				String especeParente = i1.next().toString().toLowerCase();
				// System.out.println(nom_espece + " extends " + especeParente);
				espece = runtimeContext.getLibrairie().creationEspece(especeParente, nom_espece, nom_espece);
			} else {
				espece = new Prototype(runtimeContext.getLibrairie(), nom_espece, Role.ESPECE, nom_espece, null);
			}
			while (i1.hasNext()) {
				String nom = i1.next().toString();
				Acteur acteur = runtimeContext.getLibrairie().getActeurPourEspece(Chaine.produire(nom));
				if (acteur == null)
					throw new ErreurException(Constantes.SYNTAXE_ACTEUR_INCONNU, nom);
				espece.addAttribut(acteur);
			}

			// Espece creee, faut-il y ajouter des méthodes fonctionnelles ? (Linotte 2.2)		
			// Linotte 2.1, on ajoute les methodes fonctionnelles statiques définies dans le livre :
			Map<String, Processus> methodes = runtimeContext.getEnvironnment().getParagraphesPourPrototype(nom_espece);
			if (methodes != null && !methodes.isEmpty()) {
				for (Entry<String, Processus> entree : methodes.entrySet()) {
					espece.ajouterSlot(entree.getKey(), entree.getValue());
				}
			}

			runtimeContext.getLibrairie().addEspece(espece);
			// linotte.debug("Définition de l'espèce : " + espece.getNom());
		} else if ("instantiation".equals(param)) {

			Livre livre = jobContext.getLivre();
			// boolean charger = !annotations.contains("vide");
			ItemXML test = i1.next();
			String nom;
			if (creation_dynamique && test.isActeur()) {
				Acteur a = extractionDesActeursSansErreur(Constantes.SYNTAXE_ESPECE_INCONNUE, job, valeurs)[0];
				if (a == null) {
					throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CREER);
				}
				if (!(a.getRole() == Role.TEXTE))
					throw new ErreurException(Constantes.SYNTAXE_NOMBRE_NON_VALIDE);
				nom = (String) a.getValeur();
			} else
				nom = test.getValeurBrute();

			// Gestion variables locales :
			boolean variable_locale = nom.startsWith("§");
			if (variable_locale) {
				nom = nom.substring(2);
				if (livre.getParagraphe() == null) {
					throw new ErreurException(Constantes.ERREUR_ACTEURS_LOCAUX);
				}
			}

			/**
			 * Si le context contient l'habilitation pour gérer la mémoire comme une pile  (Linotte 2.0):
			 */
			if (runtimeContext.canDo(Habilitation.STACK_MEMORY_MANAGEMENT)) {
				// Si paragraphe == null, nous sommes dans la section "globale"
				variable_locale = livre.getParagraphe() != null;
			}

			Librairie<?> librairie = variable_locale ? null : runtimeContext.getLibrairie();

			String espece = i1.next().toString().toLowerCase();

			Prototype modele = runtimeContext.getLibrairie().getEspece(espece);
			// moteur.espece_map.get(espece);

			if (modele == null)
				throw new ErreurException(Constantes.SYNTAXE_ESPECE_INCONNUE, espece);

			// Ajout de la gestion des doublures :
			boolean doublure = nom.startsWith("*");
			if (doublure) {
				nom = nom.substring(2);
				if (livre.getParagraphe() == null) {
					// Pas de double dans la section globale
					throw new ErreurException(Constantes.ERREUR_DOUBLURE);
				}
				int numero = livre.getParagraphe().getPosition(nom);
				Fonction fonction = livre.getKernelStack().recupereDerniereFonction();
				// vérifier paragraphe
				// {
				Acteur acteur = null;
				if (fonction != null) {
					acteur = fonction.getDoublure(numero);
				} else {
					// Recharcher depuis le moteur (doublure d'un appel
					// de fonction)
					acteur = jobContext.getDoublure(numero);
				}
				if (acteur != null) {
					// Vérifier les types :
					if (!(acteur.getRole() == Role.ESPECE))
						throw new ErreurException(Constantes.ERREUR_DOUBLURE_ROLE);
					if (!((Prototype) acteur).getType().equals(espece))
						throw new ErreurException(Constantes.ERREUR_DOUBLURE_ROLE);
					//
					if (i1.hasNext()) {
						throw new ErreurException(Constantes.ERREUR_DOUBLURE);
					}
					livre.getParagraphe().addDoublure(nom.toLowerCase(), acteur);
					CalqueParagraphe calqueParagraphe = livre.getKernelStack().recupereDernierCalqueParagraphe();
					calqueParagraphe.addActeurLocalDoublure(Chaine.produire(nom), acteur);
				} else {
					throw new ErreurException(Constantes.ERREUR_DOUBLURE);
				}
			} else {

				Prototype creation = null;

				// Gestion des fichiers :
				if (fichier) {
					ItemXML item = i1.next();
					Object valeurObjet = item.retourneValeurTransformee(job);
					String nomFichier;
					if (valeurObjet instanceof String) {
						nomFichier = (String) valeurObjet;
					} else {
						nomFichier = String.valueOf((Acteur) valeurObjet);
					}
					lefichier = new Fichier(nomFichier);
					creation = modele.creationEspece(nom, lefichier, null, librairie);
				} else {
					creation = modele.creationEspece(nom, null, null, librairie);

					runtimeContext.getLibrairie().reCharger(creation);

					while (i1.hasNext()) {

						ItemXML item = i1.next();
						String xml_acteur;
						Object xml_valeur;
						if (item.isActeur()) {
							xml_acteur = item.getValeurBrute();
							ItemXML item2 = i1.next();
							xml_valeur = item2.retourneValeurTransformee(job);
						} else {
							xml_valeur = item.retourneValeurTransformee(job);
							xml_acteur = i1.next().toString();
						}

						Acteur a = creation.retourneAttribut(xml_acteur);

						if (a == null)
							throw new ErreurException(Constantes.SYNTAXE_ACTEUR_INCONNU, xml_acteur);

						if (xml_valeur instanceof Acteur) {
							a.setValeur(((Acteur) xml_valeur).getValeur());
						} else {
							a.setValeur(xml_valeur);
						}

					}
				}

				if (jobContext.isCreationespece() && livre.getParagraphe() == null) {
					// Linotte 2.1
					runtimeContext.getLibrairie().addActeurPourEspece(creation);
				} else {

					if (variable_locale) {
						if (livre.getParagraphe().getActeur(creation.getNom()) != null) {
							if (livre.getParagraphe().getActeur(creation.getNom()).isActeurSystem())
								throw new ErreurException(Constantes.SYNTAXE_ACTEUR_PARTICULIER, creation.getNom().toString());
							else
								throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, creation.getNom().toString());
						}
						/**
						 * Gestion des acteurs locaux à un paragraphe et à un sous-paragraphe.
						 */

						Appel appel = livre.getKernelStack().regarderDernierAppel();
						if (appel instanceof CalqueParagraphe) {
							livre.getParagraphe().addActeur(creation);
							CalqueParagraphe calqueParagraphe = (CalqueParagraphe) appel;
							calqueParagraphe.addActeurLocal(creation);
						} else if (appel instanceof SousParagraphe) {
							Acteur tt = livre.getKernelStack().rechercheActeurDansSousParagraphe(creation.getNom());
							if (tt != null)
								throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, creation.getNom().toString());
							SousParagraphe calqueParagraphe = (SousParagraphe) appel;
							calqueParagraphe.addActeurLocal(creation);
						}
					} else {

						if (forcerLivre || livre.getParagraphe() == null || creation_dynamique) {
							if (livre.getActeur(creation.getNom(), job) != null) {
								if (livre.getActeur(creation.getNom(), job).isActeurSystem())
									throw new ErreurException(Constantes.SYNTAXE_ACTEUR_PARTICULIER, creation.getNom().toString());
								else
									throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, creation.getNom().toString());
							}
							livre.addActeur(creation);
							//moteur.getLivre().setCreation(creation.getNom());
						} else {
							if (livre.getActeur(creation.getNom(), job) != null) {
								// Bogue de Pat sur le damier.liv :
								// On ne peut pas créer un acteur dans un paragraphe
								// déjà existant au niveau du livre !
								throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, creation.getNom().toString());
							} else if (livre.getParagraphe().getActeur(creation.getNom()) != null) {
								if (livre.getParagraphe().getActeur(creation.getNom()).isActeurSystem())
									throw new ErreurException(Constantes.SYNTAXE_ACTEUR_PARTICULIER, creation.getNom().toString());
								else
									throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, creation.getNom().toString());
							}
							livre.getParagraphe().addEspece(creation);
						}

					}
					// linotte.debug("création de l'espèce : " +
					// creation.getNom());
					livre.setCreation(creation.getNom());
					// Ajout sur la toile :

					if (creation.isEspeceGraphique()) {
						
						//TODO Librairie doit être alimentée., c'est moche, à modifier.
						librairie = runtimeContext.getLibrairie();
						PrototypeGraphique eg = ((PrototypeGraphique) creation);
						// Dans le cas du webonotte, on force la clé à toileweb
						String clef_cache_toile = eg.getNom().toString().toLowerCase();						

						// Traitement particulier lié au Webonotte :
						if (runtimeContext.canDo(Habilitation.TOILE_INVISIBLE)) {
							if ("toile".equalsIgnoreCase(eg.getType()) && !"non".equals(eg.retourneAttribut("principale").getValeur().toString())) {
								eg.retourneAttribut("principale").setValeur("non");
								clef_cache_toile = Toile.TOILEWEB;
							} else {
								if (eg.retourneAttribut("toile") != null) {
									eg.retourneAttribut("toile").setValeur(Toile.TOILEWEB);
								}
							}
						}
						
						
						
						// Gestion du multi-toile Linotte 2.2
						LaToile toile;
						if ("toile".equalsIgnoreCase(eg.getType()) && "non".equals(eg.retourneAttribut("principale").getValeur().toString())) {
							// Dabort vérifier si la toile n'existe pas :
							if (librairie.existeTElleToile(eg.getNom().toString().toLowerCase())) {
								toile = librairie.recupererToile(eg.getNom().toString().toLowerCase());// 
							} else {
								toile = Toile.initToile(runtimeContext.getLinotte(), eg.getNom().toString().toLowerCase(),
										librairie.getToilePrincipale() == null ? null : librairie.getToilePrincipale().getFrameParent());
								librairie.ajouterToile(clef_cache_toile, toile);
							}
						} else {
							if (eg.retourneAttribut("toile") != null && !eg.retourneAttribut("toile").estIlVide()) {
								toile = librairie.recupererToile(eg.retourneAttribut("toile").getValeur().toString().toLowerCase());// 
								if (toile == null) {
									toile = librairie.getToilePrincipale();
								}
							} else {
								toile = librairie.getToilePrincipale();
							}
						}
						eg.setToile(toile);
						if (toile == null) {
							throw new ErreurException(Constantes.MODE_CONSOLE);
						}
						eg.getToile().getPanelLaToile().addActeursAAfficher((PrototypeGraphique) creation, variable_locale);
						eg.getToile().getPanelLaToile().setChangement();

						// Pour le webonotte, la toile ne doit jamais être visible !!
						if (runtimeContext.canDo(Habilitation.TOILE_INVISIBLE) && (eg.getToile() instanceof LaToileJDialog)) {
							((LaToileJDialog) eg.getToile()).setInvisible();
						}
						if (!eg.getToile().isVisible())
							eg.getToile().setVisible(true);
					}
				}
			}
		}

		return ETAT.PAS_DE_CHANGEMENT;

	}

}