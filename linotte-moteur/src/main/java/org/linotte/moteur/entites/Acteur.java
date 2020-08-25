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

package org.linotte.moteur.entites;

import org.linotte.frame.latoile.LaToileListener;
import org.linotte.greffons.api.AKMethod;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.java.interne.*;
import org.linotte.greffons.java.interne.a.MethodeInterne;
import org.linotte.greffons.outils.ObjetLinotteHelper;
import org.linotte.moteur.entites.ecouteurs.ActeurParent;
import org.linotte.moteur.entites.ecouteurs.ActeurParentGraphique;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.outils.LinkedHashMap;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.api.Librairie;
import org.linotte.moteur.xml.appels.Boucle;

import java.math.BigDecimal;
import java.util.*;

/**
 * Un acteur est une variable. Il possède un {@link Role} (un type).
 */
public class Acteur {

	private static final String PAS_DE_NOM = "'acteur anonyme'";
	private static final String VIDE = "";

	private Chaine nom = null;

	private Role role = null;

	public Object valeur = null;

	public Librairie<?> librairie = null;

	public List<ActeurParent> evenement;

	private Fichier lefichier = null;

	private boolean acteurSystem = false;

	public Boucle boucleMere = null;

	// Un acteur est un prototype :
	protected Map<String, Processus> slots;// = new LinkedHashMap<String, Processus>();
	protected Map<String, AKMethod> slotsGreffon;// = new LinkedHashMap<String, AKMethod>();
	protected Set<String> contratsPrototype;// = new HashSet<String>();
	private boolean acteurToprototype = false;

	/** 
	 * Ce boolean indique si la valeur est calculée ou pas.
	 * Ce n'est pas basé sur un pattern mais ça à le mérite d'être rapide.
	 * Voir la méthode {@code getValeur}
	 **/
	private boolean valeurCalculee = false;

	public Acteur(Role r, Object o) {
		nom = null;
		role = r;
		librairie = null;
		valeur = o;
	}

	public Acteur(Librairie<?> lib, String pnom, Role r, Object o, Fichier fichier) {
		this(lib, Chaine.produire(pnom), r, o, fichier);
	}

	public Acteur(Librairie<?> lib, Chaine pnom, Role r, Object o, Fichier fichier) {
		nom = pnom;
		role = r;
		librairie = lib;
		lefichier = fichier;
		valeur = o;
	}

	@Override
	public String toString() {
		return nom != null ? nom.toString() : (valeur == null ? PAS_DE_NOM : valeur) + " / " + getRole();
	}

	public Chaine getNom() {
		return nom;
	}

	public Role getRole() {
		return role;
	}

	/**
	 * @return La valeur du rôle
	 * @throws ErreurException
	 */
	public Object getValeur() throws ErreurException {
		// Va servir à recharger les valeurs entre les paragraphes :
		if (librairie != null && lefichier == null)
			if (getNom() != null)
				librairie.reCharger(this);
		if (isValeurCalculee() && evenement != null) {
			for (ActeurParent e : evenement) {
				if (e instanceof ActeurParentGraphique) {
					if (nom != null && nom.equals("sourisx") && ((PrototypeGraphique) e.getActeur()).getToile() != null) {
						return new BigDecimal(((LaToileListener) ((PrototypeGraphique) e.getActeur()).getToile().getPanelLaToile()).getSouris_x());
					} else if (nom != null && nom.equals("sourisy") && ((PrototypeGraphique) e.getActeur()).getToile() != null) {
						return new BigDecimal(((LaToileListener) ((PrototypeGraphique) e.getActeur()).getToile().getPanelLaToile()).getSouris_y());
					}
				}
			}
		}
		if (valeur == null)
			if (role == Role.NOMBRE)
				return BigDecimal.ZERO;
			else if (role == Role.TEXTE)
				return VIDE;
		return valeur;
	}

	/**
	 * Assigne au rôle la valeur passée en paramètre
	 * @param object
	 * @throws ErreurException
	 */
	public void setValeur(Object object) throws ErreurException {
		//if ((valeur == null && object != null) || (valeur != null && !valeur.equals(object))) { // Plus utile avec Linotte 2
		valeur = object;
		if (evenement != null)
			for (ActeurParent e : evenement) {
				if (e instanceof ActeurParentGraphique) {
					if (nom != null)
						//https://code.google.com/p/langagelinotte/issues/detail?id=73
						e.mettreAjour(nom.toString().toLowerCase(), object);
				} else {
					e.mettreAjour(null);
				}
			}
		if (librairie != null)
			librairie.mettre(this);
		//}
	}

	public void oterDe(Acteur a) throws ErreurException {
		if (evenement != null) {
			List<ActeurParent> supprimer = new ArrayList<ActeurParent>();
			for (ActeurParent e : evenement) {
				// Même nom ou même objet
				if (!(e instanceof ActeurParentGraphique)
						&& ((e.getActeur().getNom() != null && e.getActeur().getNom().equals(a.getNom())) || e.getActeur() == a)) {
					e.oter(this);
					supprimer.add(e);
					e.mettreAjour(null);
				}
			}
			evenement.removeAll(supprimer);
			if (supprimer.size() == 0)
				throw new ErreurException(Constantes.CASIER_ATTENDU, getNom().toString());
		}

	}

	// pour la gestion des fichiers.
	public void setValeurSimple(Object object) {
		valeur = object;
	}

	public Librairie<?> retourneLibrairie() {
		return librairie;
	}

	public boolean estIlVraimentVide() {
		return valeur == null;
	}

	/**
	 * Vide au sens du langage Linotte ?
	 * @return
	 */
	public boolean estIlVide() {
		if (valeur != null) {
			if (role == Role.NOMBRE)
				return BigDecimal.ZERO.equals(valeur);
			else if (role == Role.TEXTE)
				return VIDE.equals(valeur);
		} else if (role == Role.ESPECE)
			return false;
		return true;
	}

	protected void clearCasierEvenement() {
		if (evenement != null)
			evenement.clear();
	}

	public void addCasierEvenement(ActeurParent ce) {
		if (evenement == null)
			evenement = new ArrayList<ActeurParent>(1);
		evenement.add(ce);
	}

	public void copier(Acteur a) {
		if (a.evenement != null && this.evenement != null) {
			this.evenement.addAll(a.evenement);
		} else
			this.evenement = null;
		this.librairie = a.librairie;
		this.nom = a.nom;
		// PATCH bug avec les casiers d'espères et le joker.
		this.role = a.role;
		this.lefichier = a.lefichier;
		this.valeur = a.valeur;
		this.slots = a.slots;
		this.slotsGreffon = a.slotsGreffon;
		this.contratsPrototype = a.contratsPrototype;
		this.valeurCalculee = a.valeurCalculee;
	}

	/**
	 * Cette méthode est à utiliser lorsque l'on veut copier un acteur dans un prototype en tant qu'attribut
	 * @param pere
	 * @return
	 */
	public Acteur copierCommeAttribut(Prototype pere) {
		Acteur temp = new Acteur(librairie, nom.toString(), role, valeur, lefichier);
		if (evenement != null) {
			temp.evenement = new ArrayList<ActeurParent>(1);
			temp.evenement.add(new ActeurParentGraphique(nom.toString().toLowerCase(), pere));
			for (ActeurParent e : temp.evenement) {
				if (e instanceof ActeurParentGraphique) {
					try {
						if (nom != null)
							//https://code.google.com/p/langagelinotte/issues/detail?id=73
							e.mettreAjour(nom.toString().toLowerCase(), valeur);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		temp.slots = slots;
		temp.slotsGreffon = slotsGreffon;
		temp.contratsPrototype = contratsPrototype;
		temp.valeurCalculee = valeurCalculee;
		return temp;
	}

	public Acteur clone() {
		Acteur temp = new Acteur(role, valeur);
		temp.setNom(getNom());
		if (evenement != null) {
			temp.evenement = new ArrayList<ActeurParent>(evenement.size());
			temp.evenement.addAll(evenement);
		}
		temp.slots = slots;
		temp.slotsGreffon = slotsGreffon;
		temp.contratsPrototype = contratsPrototype;
		temp.valeurCalculee = valeurCalculee;
		return temp;
	}

	protected void setLibrairie(Librairie<?> librairie) {
		this.librairie = librairie;
	}

	public void setNom(Chaine nom) {
		this.nom = nom;
	}

	public Fichier retourneFichier() {
		return lefichier;
	}

	public boolean isActeurSystem() {
		return acteurSystem;
	}

	public void setActeurSystem(boolean acteur_system) {
		this.acteurSystem = acteur_system;
	}

	public boolean isEspeceGraphique() {
		return false;
	}

	public void viderActeur() throws ErreurException {
		setValeur(null);
	}

	/**
	 * Ajoute un slot paragraphe
	 * @param nom
	 * @param processus
	 * @return
	 */
	public boolean ajouterSlot(String nom, Processus processus) {
		initPrototype();
		initPrototype(getRole());
		return slots.put(nom, processus) == null;
	}

	/**
	 * @param nom
	 * @return
	 */
	public Processus retourneSlot(String nom) {
		initPrototype();
		initPrototype(getRole());
		return (Processus) slots.get(nom);
	}

	/**
	 * @return
	 */
	public Set<String> retourneSlotsGreffons() {
		initPrototype();
		initPrototype(getRole());
		return slotsGreffon.keySet();
	}

	public Set<String> retourneSlotsLinotte() {
		initPrototype();
		initPrototype(getRole());
		return slots.keySet();
	}

	/**
	 * Ajoute un slot paragraphe
	 * @param nom
	 * @param processus
	 * @return
	 */
	public boolean ajouterSlotGreffon(String nom, AKMethod processus) {
		initPrototype();
		initPrototype(getRole());
		return slotsGreffon.put(nom, processus) == null;
	}

	/**
	 * @param nom
	 * @return
	 */
	public AKMethod retourneSlotGreffon(String nom) {
		initPrototype();
		initPrototype(getRole());
		return (AKMethod) slotsGreffon.get(nom);
	}

	/**
	 * @param nom
	 */
	public void ajouteContratPrototype(String nom) {
		initPrototype();
		contratsPrototype.add(nom);
	}

	/**
	 * @return
	 */
	public boolean resteContratsNonNegocie() {
		initPrototype();
		return !contratsPrototype.isEmpty();
	}

	/**
	 * @param nom
	 */
	public void supprimeContratPrototype(String nom) {
		initPrototype();
		contratsPrototype.remove(nom);
	}

	public String dernierContrat() {
		initPrototype();
		return (String) contratsPrototype.toArray()[0];
	}

	protected void initPrototype() {
		if (slots == null) {
			slots = new LinkedHashMap<String, Processus>();
			slotsGreffon = new LinkedHashMap<String, AKMethod>();
			contratsPrototype = new HashSet<String>();
		}
	}

	private void initPrototype(Role prole) {
		if (!acteurToprototype) {
			acteurToprototype = true;
			if (prole == Role.CASIER) {
				ajouterSlotGreffon(new TailleMethodeInterne());
				ajouterSlotGreffon(new InverserMethodeInterne());
				ajouterSlotGreffon(new RemplacerMethodeInterne());
				ajouterSlotGreffon(new PositionMethodeInterne());
				ajouterSlotGreffon(new ExtraireMethodeInterne());
				ajouterSlotGreffon(new MelangeMethodeInterne());
			} else if (prole == Role.TEXTE) {
				ajouterSlotGreffon(new InverserMethodeInterne());
				ajouterSlotGreffon(new RemplacerMethodeInterne());
				ajouterSlotGreffon(new MajusculeMethodeInterne());
				ajouterSlotGreffon(new MinusculeMethodeInterne());
				ajouterSlotGreffon(new TailleMethodeInterne());
				ajouterSlotGreffon(new PositionMethodeInterne());
				ajouterSlotGreffon(new ExtraireMethodeInterne());
				ajouterSlotGreffon(new MelangeMethodeInterne());
				ajouterSlotGreffon(new CapitaliserMethodeInterne());
			} else if (prole == Role.NOMBRE) {
				ajouterSlotGreffon(new MethodeInterne() {

					@Override
					public ObjetLinotte appeler(Acteur moi, Greffon greffon, ObjetLinotte... parametres) throws Exception {
						try {
							if (parametres.length != 0)
								throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
							return ObjetLinotteHelper.copy(new BigDecimal(Math.floor(((BigDecimal) Acteur.this.getValeur()).doubleValue() * Math.random())));
						} catch (Exception e) {
							throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
						}
					}

					@Override
					public String nom() {
						return "hasard";
					}

				});
			} else if (prole == Role.ESPECE) {
				if (isEspeceGraphique()) {
					((PrototypeGraphique) this).initPrototypeGraphique();
				}
			}
		}
	}

	protected void ajouterSlotGreffon(MethodeInterne methodeInterne) {
		ajouterSlotGreffon(methodeInterne.nom(), methodeInterne);
	}

	private boolean isValeurCalculee() {
		return valeurCalculee;
	}

	public void setValeurCalculee() {
		this.valeurCalculee = true;
	}

	/**
	 * Traitement particulier pour le joker : il faut toujours récupérer la valeur calculée par la boucle. 
	 * issue 156
	 * @param boucle
	 */
	public void setJokerDunCasier(Boucle boucle) {
		boucleMere = boucle;
	}

	/**
	 * Traitement particulier pour le joker : il faut toujours récupérer la valeur calculée par la boucle.
	 * issue 156
	 * @return
	 */
	public boolean isJokerDunCasier() {
		return boucleMere != null;
	}

	/**
	 * @return
	 */
	public Set<String> retourneNomsSlotsGreffons() {
		initPrototype();
		initPrototype(getRole());
		Set<String> slots = new HashSet<>();
		for (String string : slotsGreffon.keySet()) {
			AKMethod o = slotsGreffon.get(string);
			if (o instanceof MethodeInterne) {
				MethodeInterne mi = (MethodeInterne) o;
				slots.add(mi.nom() + mi.parametres());
			} else {
				slots.add(string);
			}
		}
		return slots;
	}

}