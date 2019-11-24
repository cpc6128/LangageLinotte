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

package org.linotte.moteur.entites;

import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linotte.frame.latoile.Couleur;
import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.latoile.Toile;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.KernelStack;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.appels.Boucle;
import org.linotte.moteur.xml.outils.MathematiquesConstantes;

@SuppressWarnings("serial")
public class Livre implements Serializable {

	// DEB - Gestion des acteurs systèmes

	private static abstract class Traitement {

		Chaine COD;

		Traitement(Chaine COD) {
			this.COD = COD;
		}

		abstract Acteur analyse(Job job);

	}

	private static Map<Chaine, Traitement> acteursSysteme = new HashMap<Chaine, Traitement>(10);

	// FIN - Gestion des acteurs systèmes

	protected KernelStack pileAppels = null;

	private String nom = null;

	private Map<Chaine, Acteur> acteurs_grands_roles_map = new HashMap<Chaine, Acteur>(10);

	protected Paragraphe pg = null;

	// Cette table associe la position du paragraphe et son nom.
	protected Map<String, Paragraphe> table_des_paragraphes = new HashMap<String, Paragraphe>(10);

	private Chaine creation = null;

	//	private Acteur joker;

	public Paragraphe getParagraphe() {
		return pg;
	}

	public void setParagraphe(Paragraphe pg) {
		this.pg = pg;
		if (pg != null) // Cas Linnet : action hors paragraphe
			table_des_paragraphes.put(pg.getNom().toLowerCase(), pg);
	}

	public Livre() {
		super();
		pileAppels = new KernelStack();
	}

	public void setNom(String n, boolean frame, LaToile laToile) {
		nom = n;
		if (frame)
			Toile.setTitre(nom, laToile);
	}

	public String getNom() {
		return nom;
	}

	public void addActeur(Acteur ac) {
		acteurs_grands_roles_map.put(ac.getNom(), ac);
	}

	public void removeActeur(Acteur ac) {
		acteurs_grands_roles_map.remove(ac.getNom());
	}

	public Acteur getActeur(Chaine ac, Job moteur) {
		// Gestion des acteurs systèmes :
		Acteur a = getActeurSystem(ac, moteur);
		if (a == null)
			return (Acteur) acteurs_grands_roles_map.get(ac);
		else
			return a;
	}

	public Paragraphe retourneParagraphe(String para) {
		return (Paragraphe) table_des_paragraphes.get(para.toLowerCase());
	}

	private Acteur getActeurSystem(Chaine COD, Job job) {
		Traitement traitement = acteursSysteme.get(COD);
		if (traitement != null) {
			return traitement.analyse(job);
		} else
			return null;
	}

	public void setCreation(Chaine creation) {
		this.creation = creation;
	}

	@Override
	public Object clone() {
		Livre livre = new Livre();
		livre.pileAppels = (KernelStack) pileAppels.produceKernel();
		livre.nom = nom;
		livre.acteurs_grands_roles_map = acteurs_grands_roles_map;
		if (pg != null)
			//VERSION2.2
			livre.pg = new Paragraphe(pg.getNom());
		livre.table_des_paragraphes = table_des_paragraphes;
		livre.creation = creation;
		return livre;
	}

	public KernelStack getKernelStack() {
		return pileAppels;
	}

	// Méthodes statiques :

	public static boolean isActeurSystem(Chaine COD) {
		return acteursSysteme.containsKey(COD);
	}

	public static void ajouterActeursSystemes(String attribut, String string) {
		final Chaine chaine = Chaine.produire(string);
		if ("ANNEE".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(Calendar.getInstance().get(Calendar.YEAR)), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("HEURE".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("MOIS".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(Calendar.getInstance().get(Calendar.MONTH) + 1), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("JOUR".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("MINUTE".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(Calendar.getInstance().get(Calendar.MINUTE)), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("SECONDE".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(Calendar.getInstance().get(Calendar.SECOND)), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("VERSION".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.TEXTE, Version.getVersion(), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("AUTEUR".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.TEXTE, Version.AUTEUR, null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("SPEC".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.TEXTE, ((RuntimeContext) job.getRuntimeContext()).getLinotte().getGrammaire().getSPEC(), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("JOKER".equals(attribut)) {
			// joker
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					JobContext c = (JobContext) job.getContext();
					return c.getLivre().pileAppels.rechercheJokerDansBoucle(0);
				}
			});
			// joker'
			final Chaine chaineJokerPrime = Chaine.produire(string + "'");
			acteursSysteme.put(chaineJokerPrime, new Traitement(chaineJokerPrime) {
				@Override
				public Acteur analyse(Job job) {
					JobContext c = (JobContext) job.getContext();
					return c.getLivre().pileAppels.rechercheJokerDansBoucle(1);
				}
			});			
			// joker''
			final Chaine chaineJokerDoublePrime = Chaine.produire(string + "''");
			acteursSysteme.put(chaineJokerDoublePrime, new Traitement(chaineJokerDoublePrime) {
				@Override
				public Acteur analyse(Job job) {
					JobContext c = (JobContext) job.getContext();
					return c.getLivre().pileAppels.rechercheJokerDansBoucle(2);
				}
			});
			// joker'''
			final Chaine chaineJokerTriplePrime = Chaine.produire(string + "'''");
			acteursSysteme.put(chaineJokerTriplePrime, new Traitement(chaineJokerTriplePrime) {
				@Override
				public Acteur analyse(Job job) {
					JobContext c = (JobContext) job.getContext();
					return c.getLivre().pileAppels.rechercheJokerDansBoucle(3);
				}
			});
			Boucle.setJoker(chaine);
		} else if ("POLICES".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Casier(null, COD.toString(), Role.TEXTE, null);
					String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
					for (int i = 0; i < fonts.length; i++) {
						try {
							((Casier) retour).ajouterValeur(new Acteur(null, "", Role.TEXTE, fonts[i], null), false);
						} catch (ErreurException e) {
						}
					}

					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("SOURIS_X".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(((RuntimeContext) job.getRuntimeContext()).getLibrairie()
							.getToilePrincipale().getPanelLaToile().getSouris_x()), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("SOURIS_Y".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(((RuntimeContext) job.getRuntimeContext()).getLibrairie()
							.getToilePrincipale().getPanelLaToile().getSouris_y()), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("TOUCHE".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.TEXTE, ((RuntimeContext) job.getRuntimeContext()).getLibrairie().getToilePrincipale()
							.getPanelLaToile().getDerniereTouche(), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("COULEURS".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Casier(null, COD.toString(), Role.TEXTE, null);
					List<String> couleurs = Couleur.retourneCouleurs();

					for (String string : couleurs) {
						try {
							((Casier) retour).ajouterValeur(new Acteur(null, "", Role.TEXTE, string, null), false);
						} catch (ErreurException e) {
						}
					}
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("CREATION".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					JobContext c = (JobContext) job.getContext();
					Acteur retour = c.getLivre().getActeur(c.getLivre().creation, job);
					return retour;
				}
			});
		else if ("ECRANV".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(Toolkit.getDefaultToolkit().getScreenSize().height), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("ECRANH".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(Toolkit.getDefaultToolkit().getScreenSize().width), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("LIVRE".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					JobContext c = (JobContext) job.getContext();
					Acteur retour = new Acteur(null, COD, Role.TEXTE, c.getLivre().getNom(), null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("PI".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, MathematiquesConstantes.PI, null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("E".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.NOMBRE, MathematiquesConstantes.E, null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
		else if ("VRAI".equals(attribut)) {
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur VRAI = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(1), null);
					VRAI.setActeurSystem(true);
					return VRAI;
				}
			});
		} else if ("FAUX".equals(attribut)) {
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur FAUX = new Acteur(null, COD, Role.NOMBRE, new BigDecimal(0), null);
					FAUX.setActeurSystem(true);
					return FAUX;
				}
			});
		} else if ("RETOUR_CHARIOT".equals(attribut))
			acteursSysteme.put(chaine, new Traitement(chaine) {
				@Override
				public Acteur analyse(Job job) {
					Acteur retour = new Acteur(null, COD, Role.TEXTE, "\n", null);
					retour.setActeurSystem(true);
					return retour;
				}
			});
	}

	public void nettoyerMemoire() {
		pileAppels.clear();
		acteurs_grands_roles_map.clear();
		pg = null;
		table_des_paragraphes.clear();
	}

	public Map<Chaine, Acteur> getActeurs() {
		return acteurs_grands_roles_map;
	}

}