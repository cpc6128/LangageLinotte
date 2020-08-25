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

import org.alize.security.Habilitation;
import org.linotte.moteur.entites.*;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.RoleException;
import org.linotte.moteur.outils.ArrayIterator;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.outils.ChaineOutils;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.alize.kernel.i.ParserHandler;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.api.IHM;
import org.linotte.moteur.xml.api.Librairie;
import org.linotte.moteur.xml.appels.Appel;
import org.linotte.moteur.xml.appels.CalqueParagraphe;
import org.linotte.moteur.xml.appels.Fonction;
import org.linotte.moteur.xml.appels.SousParagraphe;

import java.math.BigDecimal;
import java.util.Iterator;

public class ActeurAction extends Action implements IProduitCartesien, ParserHandler {

	public static final String NOM_ETAT = "creation acteur";

	public ActeurAction() {
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

		Iterator<ItemXML> i1 = new ArrayIterator<ItemXML>(valeurs);
		// Item préchargé par le parserHandler :
		ItemXML itemStatique = i1.next();

		if (itemStatique.fichier && runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
			throw new ErreurException(Constantes.MODE_CONSOLE);			
		}	
		
		Livre livre = jobContext.getLivre();
		Paragraphe paragraphe = livre.getParagraphe();

		// Ajout de la gestion des doublures :
		// ***********************************

		if (itemStatique.doublure) {
			// Pas d'acteur typé dynamiquement
			if (itemStatique.role_acteur == null) {
				throw new ErreurException(Constantes.ERREUR_DOUBLURE);
			}
			if (paragraphe == null) {
				throw new ErreurException(Constantes.ERREUR_DOUBLURE);
			}
			int numero = paragraphe.getPosition(itemStatique.nom_acteur);
			Fonction fonction = livre.getKernelStack().recupereDerniereFonction();
			// vérifier paragraphe
			Acteur acteur = null;
			if (fonction != null) {
				acteur = fonction.getDoublure(numero);
			} else {
				// Recharger depuis le moteur (doublure d'un appel de fonction)
				acteur = jobContext.getDoublure(numero);
			}
			if (acteur != null) {
				// Vérifier les types :
				if (!(acteur.getRole() == itemStatique.role_acteur))
					throw new ErreurException(Constantes.ERREUR_DOUBLURE_ROLE);
				if (itemStatique.role_acteur == Role.CASIER && itemStatique.contenant == Role.ESPECE) {
					if (i1.hasNext()) {
						String modele = (i1.next()).toString();
						if (itemStatique.role_acteur == Role.CASIER && itemStatique.contenant == Role.ESPECE
								&& !modele.equals(((Casier) acteur).getTypeespece()))
							throw new ErreurException(Constantes.ERREUR_DOUBLURE_ROLE);
					} else {
						throw new ErreurException(Constantes.ERREUR_DOUBLURE_ROLE);
					}
				}
				//
				if (i1.hasNext()) {
					throw new ErreurException(Constantes.ERREUR_DOUBLURE);
				}
				paragraphe.addDoublure(itemStatique.nom_acteur.toLowerCase(), acteur);
				CalqueParagraphe calqueParagraphe = livre.getKernelStack().recupereDernierCalqueParagraphe();
				calqueParagraphe.addActeurLocalDoublure(Chaine.produire(itemStatique.nom_acteur), acteur);
			} else {
				//Est-ce le premier paragraphe ?
				if (runtimeContext.paragrapheExecute == 1) {
					IHM ihm = runtimeContext.getIhm();
					String retour = ihm.questionne("Quelle est la valeur de l'acteur '" + itemStatique.nom_acteur + "' ?", Role.TEXTE, itemStatique.nom_acteur);
					BigDecimal v = (retour != null && retour.length() > 0) ? Mathematiques.isBigDecimal(retour) : null;
					if (v != null) {
						acteur = new Acteur(Role.NOMBRE, v);
					} else {
						acteur = new Acteur(Role.TEXTE, retour);
					}
					paragraphe.addDoublure(itemStatique.nom_acteur.toLowerCase(), acteur);
					CalqueParagraphe calqueParagraphe = livre.getKernelStack().recupereDernierCalqueParagraphe();
					calqueParagraphe.addActeurLocalDoublure(Chaine.produire(itemStatique.nom_acteur), acteur);
				} else {
					throw new ErreurException(Constantes.ERREUR_DOUBLURE);
				}
			}
		} else {

			Fichier lefichier = null;
			Librairie librairie = null;

			boolean variableLocale = itemStatique.variable_locale;
			boolean variableSousParagraphe = itemStatique.variable_locale;

			/**
			 * Si le context contient l'habilitation pour gérer la mémoire comme une pile  (Linotte 2.0):
			 */
			if (runtimeContext.canDo(Habilitation.STACK_MEMORY_MANAGEMENT)) {
				// Si paragraphe == null, nous sommes dans la section "globale"
				// variableLocale = paragraphe != null;
				if (livre.getKernelStack().size() > 0) {
					Appel appel = livre.getKernelStack().regarderDernierAppel();
					variableSousParagraphe = appel instanceof SousParagraphe;
					variableLocale = (appel instanceof CalqueParagraphe) || variableSousParagraphe;
				} else {
					// Variables globales :
					variableSousParagraphe = variableLocale = false;
				}

			}

			// Gestion variables locales :
			if (variableLocale) {
				if (runtimeContext.canDo(Habilitation.STACK_MEMORY_MANAGEMENT)) {
					if (paragraphe == null && !variableSousParagraphe) {
						throw new ErreurException(Constantes.ERREUR_ACTEURS_LOCAUX);
					}
				} else {
					if (paragraphe == null) {
						throw new ErreurException(Constantes.ERREUR_ACTEURS_LOCAUX);
					}
				}
			} else {
				librairie = variableLocale ? null : runtimeContext.getLibrairie();
				if (itemStatique.creation_dynamique && itemStatique.isActeur()) {
					Acteur a = extractionDesActeursSansErreur(Constantes.SYNTAXE_ACTEUR_INCONNU, job, valeurs)[0];
					if (a == null)
						throw new ErreurException(Constantes.SYNTAXE_ACTEUR_INCONNU, itemStatique.getValeurBrute());
					if (!(a.getRole() == Role.TEXTE))
						throw new RoleException(Constantes.SYNTAXE_PARAMETRE_CREER, a.getNom().toString(), Role.TEXTE, a.getRole());
					itemStatique.nom_acteur = (String) a.getValeur();
				}
			}

			Object o = null;
			Acteur acteur = null;

			if (itemStatique.role_acteur == Role.CASIER) {
				// if (contenant == Role.Espece) {
				// Type de l'espèce
				String type = null;
				if (!itemStatique.fichier) {
					if (itemStatique.contenant == Role.ESPECE)
						type = (i1.next()).toString();
					// Il y a une valeur affecfée, on ne charge pas de la
					// mémoire.
					// charger = false;
				} else {
					// Gestion des fichiers et mode SQL :
					if (itemStatique.contenant == Role.ESPECE)
						type = (i1.next()).toString();
					ItemXML nom_fichier = i1.next();

					Object valeurObjet = nom_fichier.retourneValeurTransformee(job);
					String nomFichier;
					if (valeurObjet instanceof String) {
						nomFichier = (String) valeurObjet;
					} else {
						nomFichier = String.valueOf((Acteur) valeurObjet);
					}

					lefichier = new Fichier(nomFichier);
				}
				if (type != null && runtimeContext.getLibrairie().getEspece(type.toLowerCase()) == null) {
					throw new ErreurException(Constantes.SYNTAXE_ESPECE_INCONNUE, type);
				}

				if (acteur == null)
					acteur = new Casier(librairie, itemStatique.nom_acteur.toString(), itemStatique.contenant, type, lefichier);

				Casier tab = (Casier) acteur;

				if (itemStatique.contenant == null && !i1.hasNext()) {
					throw new ErreurException(Constantes.CASIER_MAUVAISE_ESPECE);
				}

				while (i1.hasNext() && lefichier == null) {
					ItemXML item = i1.next();
					Object valeur = null;
					if (itemStatique.contenant == Role.ESPECE) {
						// Recherche acteur
						Acteur a;
						o = item.retourneValeurTransformee(job);
						if (!(o instanceof Acteur)) {
							throw new ErreurException(Constantes.SYNTAXE_ACTEUR_INCONNU, item.getValeurBrute());
						} else {
							a = (Acteur) o;
						}
						tab.ajouterValeur(a, true);
						continue;
					} else if (itemStatique.contenant == Role.CASIER) {
						Acteur a;
						o = item.retourneValeurTransformee(job);
						if (!(o instanceof Acteur)) {
							throw new ErreurException(Constantes.SYNTAXE_ACTEUR_INCONNU, item.getValeurBrute());
						} else {
							a = (Acteur) o;
						}
						tab.ajouterValeur(a, true);
						continue;
					} else {
						valeur = item.retourneValeurTransformee(job);
					}
					if (valeur instanceof Acteur) {
						tab.ajouterValeur((Acteur) valeur, true);
					} else {
						if (itemStatique.contenant == null) {
							if (valeur instanceof BigDecimal) {
								itemStatique.contenant = Role.NOMBRE;
							} else {
								itemStatique.contenant = Role.TEXTE;
							}
						} else {
							// Vérification de la coherence des types :
							if (valeur instanceof BigDecimal && itemStatique.contenant != Role.NOMBRE) {
								throw new RoleException(Constantes.CASIER_MAUVAISE_ESPECE, item.getValeurBrute(), Role.NOMBRE.name());
							} else if (valeur instanceof String && itemStatique.contenant != Role.TEXTE) {
								throw new RoleException(Constantes.CASIER_MAUVAISE_ESPECE, item.getValeurBrute(), Role.TEXTE.name());
							}
						}
						Acteur temp = new Acteur(itemStatique.contenant, valeur);
						tab.ajouterValeur(temp, true);
					}
				}
			} else {
				Role role = itemStatique.role_acteur;
				if (i1.hasNext()) {
					o = (i1.next()).retourneValeurTransformee(job);
					if (o instanceof Acteur) {
						o = ((Acteur) o).getValeur();
					}
					if (!itemStatique.fichier) {
						// Il y a une valeur affecfée, on ne charge pas de la
						// mémoire.
						itemStatique.charger = false;
					} else {
						// Gestion des fichiers :
						lefichier = new Fichier((String) o);
					}
					// Type chargé dynamiquement :
					if (itemStatique.role_acteur == null) {
						role = (o instanceof BigDecimal) ? Role.NOMBRE : Role.TEXTE;
					}

					if (lefichier == null && role == Role.NOMBRE && !(o instanceof BigDecimal)) {
						throw new ErreurException(Constantes.SYNTAXE_NOMBRE_NON_VALIDE, (String) o);
					}
					if (role == Role.TEXTE && o instanceof BigDecimal) {
						o = String.valueOf(o);
					}
					if (role == Role.TEXTE && !(o instanceof String)) {
						throw new ErreurException(Constantes.SYNTAXE_CHAINE_NON_VALIDE, String.valueOf(o));
					}
				}
				acteur = new Acteur(librairie, itemStatique.nom_acteur, role, o, lefichier);
			}

			if (jobContext.isCreationespece() && paragraphe == null) {

				runtimeContext.getLibrairie().addActeurPourEspece(acteur);

			} else {
				if (variableLocale) {
					/**
					 * Gestion des acteurs locaux à un paragraphe et à un sous-paragraphe.
					 */

					Appel appel = livre.getKernelStack().regarderDernierAppel();
					if (appel instanceof CalqueParagraphe) {

						if (paragraphe.getActeur(acteur.getNom()) != null) {
							throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, acteur.getNom().toString());
						}

						paragraphe.addActeur(acteur);
						CalqueParagraphe calqueParagraphe = (CalqueParagraphe) appel;
						calqueParagraphe.addActeurLocal(acteur);
					} else if (appel instanceof SousParagraphe) {
						/*
						Acteur tt = livre.rechercheActeurDansSousParagraphe(acteur.getNom());
						if (tt != null)
							throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, acteur.getNom().toString());
						*/
						SousParagraphe calqueParagraphe = (SousParagraphe) appel;
						if (calqueParagraphe.getActeurLocal(acteur.getNom()) != null) {
							throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, acteur.getNom().toString());
						}
						calqueParagraphe.addActeurLocal(acteur);
					}

					// Si c'est un fichier, il faut le recharger :
					if (itemStatique.charger && acteur.retourneFichier() != null)
						acteur.retourneFichier().lire(acteur);

					if (acteur.isActeurSystem())
						throw new ErreurException(Constantes.SYNTAXE_ACTEUR_PARTICULIER, acteur.getNom().toString());

				} else {

					if (acteur.retourneLibrairie() != null)
						if (itemStatique.charger)
							acteur.retourneLibrairie().reCharger(acteur);
						else
							acteur.retourneLibrairie().mettre(acteur);

					if (itemStatique.forcerLivre || paragraphe == null || itemStatique.creation_dynamique) {
						if (livre.getActeur(acteur.getNom(), job) != null) {
							if (livre.getActeur(acteur.getNom(), job).isActeurSystem())
								throw new ErreurException(Constantes.SYNTAXE_ACTEUR_PARTICULIER, acteur.getNom().toString());
							else
								throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, acteur.getNom().toString());
						}
						livre.addActeur(acteur);
						livre.setCreation(acteur.getNom());
					} else {
						Chaine nom = acteur.getNom();
						if (livre.getActeur(nom, job) != null) {
							if (livre.getActeur(nom, job).isActeurSystem())
								throw new ErreurException(Constantes.SYNTAXE_ACTEUR_PARTICULIER, nom.toString());
							else
								throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, nom.toString());
						} else if (paragraphe.getActeur(nom) != null) {
							throw new ErreurException(Constantes.SYNTAXE_ACTEUR_DEJA_PRESENT, nom.toString());
						}
						paragraphe.addActeur(acteur);
						livre.setCreation(nom);
					}
				}
			}
		}

		return ETAT.PAS_DE_CHANGEMENT;

	}

	@Override
	public void postAnalyse(Processus processus, ParserEnvironnement context) throws Exception {
		ItemXML[] valeurs = processus.getValeurs();
		if (valeurs == null) {
			for (ItemXML[] items : processus.getMatrice()) {
				ItemXML acteur = items[0];
				processItemXML(processus, acteur);
			}
		} else {
			ItemXML acteur = valeurs[0];
			processItemXML(processus, acteur);
		}

	}

	private void processItemXML(Processus processus, ItemXML acteur) {
		String[] annotations = processus.getAnnotations();
		String param = processus.getParam();
		acteur.creation_dynamique = ChaineOutils.findInArray(annotations, "creation");
		acteur.nom_acteur = acteur.getValeurBrute();
		char charRole = param.charAt(0);
		acteur.role_acteur = charRole == 'd' ? null : (charRole == 'n' ? Role.NOMBRE : charRole == 't' ? Role.TEXTE : Role.CASIER);
		if (acteur.role_acteur == Role.CASIER) {
			acteur.contenant = "casier".equals(param) ? null : "casier de texte".equals(param) ? Role.TEXTE : "casier de casier".equals(param) ? Role.CASIER
					: "casier d'espèce".equals(param) ? Role.ESPECE : Role.NOMBRE;
		}
		char premierCaractere = acteur.nom_acteur.charAt(0);
		acteur.variable_locale = premierCaractere == '§';
		acteur.doublure = premierCaractere == '*';
		acteur.charger = !ChaineOutils.findInArray(annotations, "vide");
		acteur.fichier = ChaineOutils.findInArray(annotations, "fichier");
		acteur.forcerLivre = ChaineOutils.findInArray(annotations, "livre");
		if (acteur.doublure)
			acteur.nom_acteur = acteur.nom_acteur.substring(2);
		if (acteur.variable_locale)
			acteur.nom_acteur = acteur.nom_acteur.substring(2);
	}
}