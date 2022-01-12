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

import org.linotte.frame.latoile.Toile;
import org.linotte.greffons.GreffonsChargeur;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.greffons.outils.ObjetLinotteFactory;
import org.linotte.moteur.entites.ecouteurs.ActeurParent;
import org.linotte.moteur.entites.ecouteurs.ActeurParentGraphique;
import org.linotte.moteur.entites.ecouteurs.GreffonValeurListenerImpl;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.outils.LinkedHashMap;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.api.Librairie;

import java.math.BigDecimal;
import java.util.*;

/**
 * Cette classe représente une espèce qui est {@link Acteur}.
 * L'espèce peut contenir des attributs
 * 
 * @author CPC
 *
 */
public class Prototype extends Acteur {

	// Bloc static :
	
	private static Long compteurPrototypeID = 0L;
	
	public long prototypeID;
	
	{
		// Si on utilise TCPLinotte, ce comportement ne fonctionne plus, il faut générer un nombre aléatoire.
		synchronized (compteurPrototypeID) {
			prototypeID = compteurPrototypeID;
			compteurPrototypeID++;
		}
	}
	
	private LinkedHashMap<String, Acteur> valeurs = new LinkedHashMap<String, Acteur>();

	private String type = null;

	protected Greffon greffon = null;

	// Il doit être chargé qu'une fois de la mémoire.
	private boolean charger = false;

	public Prototype(Librairie<?> lib, String pnom, Role r, String t, Fichier f) {
		super(lib, pnom, r, null, f);
		type = t;
	}

	public Prototype(Librairie<?> lib, String pnom, Role r, String t, Fichier f, Greffon g) {
		super(lib, pnom, r, null, f);
		type = t;
		// Bogue avec héritage, on a perdu le type du père....
		greffon = creationGreffon(g);
	}

	public void addAttribut(Acteur e) {
		e.setLibrairie(null);
		e.clearCasierEvenement();
		ActeurParentGraphique apg = new ActeurParentGraphique(e.getNom().toString().toLowerCase(), this);
		e.addCasierEvenement(apg);
		try {
			apg.mettreAjour(e.getNom().toString().toLowerCase(), e.getValeur());
		} catch (ErreurException e1) {
			e1.printStackTrace();
		}
		valeurs.put(e.getNom().toString().toLowerCase(), e);
	}

	public Acteur retourneAttribut(String nom) throws ErreurException {
		if (!charger && librairie != null) {
			librairie.reCharger(this);
			charger = true;
		}
		return (Acteur) valeurs.get(nom.toLowerCase());
	}

	public Acteur retourneAttributSimple(String nom) {
		return (Acteur) valeurs.get(nom.toLowerCase());
	}

	public Prototype creationEspece(String name, Fichier f, String type_heritage, Librairie<?> pLibrairie) {
		Prototype retour;
		Greffon temp = null;//
		if (greffon != null)
			temp = creationGreffon(greffon);
		if (isEspeceGraphique()) {
			retour = new PrototypeGraphique(null, pLibrairie, name, Role.ESPECE, type_heritage == null ? type : type_heritage, f,
					((PrototypeGraphique) this).getTypeGraphique(), temp);
		} else
			retour = new Prototype(pLibrairie, name, Role.ESPECE, type_heritage == null ? type : type_heritage, f, temp);
		// Copie des attributs :
		Iterator<String> i = valeurs.getElements().iterator();
		while (i.hasNext()) {
			String chaine = i.next().toString();
			Acteur a = ((Acteur) valeurs.get(chaine)).copierCommeAttribut(retour);
			retour.addAttribut(a);
			if (retour.greffon != null) {
				// Préchargement des valeurs dans le greffon :
				// TODO A améliorer :		
				try {
					retour.greffon.valeurs.put(chaine, ObjetLinotteFactory.copy(a));
				} catch (ErreurException e) {
					e.printStackTrace();
				} catch (GreffonException e) {
					e.printStackTrace();
				}
			}
		}
		// Si c'est un greffon :
		retour.slots = slots;
		retour.slotsGreffon = slotsGreffon;
		retour.contratsPrototype = contratsPrototype;
		return retour;
	}

	protected Greffon creationGreffon(Greffon g) {
		if (!Toile.isApplet()) {
			Greffon temp = null;
			if (g != null) {
				try {
					// Pour garder le greffon suite héritage (on perd le type du père) 
					temp = g.getClass().getDeclaredConstructor().newInstance();
				} catch (Exception e) {
					temp = GreffonsChargeur.getInstance().newInstance(type);
				}
			} else {
				temp = GreffonsChargeur.getInstance().newInstance(type);
			}
			if (temp != null) {
				temp.setRessourceManager(Ressources.getInstance());
				temp.setValeur(ObjetLinotteFactory.CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT, new BigDecimal(prototypeID));
				GreffonsChargeur.getInstance().cache.put(new BigDecimal(prototypeID).longValue(), temp);
				ajouterGreffonListener(temp);
				return temp;
			}
		}
		return null;
	}

	private void ajouterGreffonListener(Greffon temp) {
		temp.addGreffonValeurListener(new GreffonValeurListenerImpl(this));
	}

	public String getType() {
		return type;
	}

	@Override
	public void copier(Acteur a) {
		super.copier(a);
		Prototype c = (Prototype) a;
		this.type = c.type;
		if (c.greffon != null) {
			this.greffon = c.greffon;
			//creationGreffon();
			ajouterGreffonListener(this.greffon);
		}
		this.copierAttributs(c);
	}

	@Override
	public Prototype clone() {
		Prototype temp = new Prototype(null, null, getRole(), getType(), retourneFichier());
		temp.slots = slots;
		// https://github.com/cpc6128/LangageLinotte/issues/46
		// Pour éviter d'avoir un nom null
		if (getNom() != null)
			temp.setNom(Chaine.produire(getNom().toString() + "#"));
		else
			temp.setNom(Chaine.produire("#"));
		temp.slotsGreffon = slotsGreffon;
		temp.contratsPrototype = contratsPrototype;
		temp.greffon = creationGreffon(greffon);
		temp.copierAttributs(this);
		return temp;
	}

	/**
	 * Recopie les attributs du prototype source
	 * @param source
	 */
	protected void copierAttributs(Prototype source) {
		valeurs.clear();
		Iterator<String> i = source.valeurs.getElements().iterator();
		while (i.hasNext()) {
			String chaine = i.next().toString();
			Acteur a = ((Acteur) source.valeurs.get(chaine)).copierCommeAttribut(this);
			addAttribut(a);
			if (greffon != null) {
				// Préchargement des valeurs dans le greffon :
				// TODO A améliorer :		
				try {
					greffon.valeurs.put(chaine, ObjetLinotteFactory.copy(a));
				} catch (ErreurException e) {
					e.printStackTrace();
				} catch (GreffonException e) {
					e.printStackTrace();
				}
			}
		}

	}

	@Override
	public Acteur copierCommeAttribut(Prototype pere) {
		Prototype temp = new Prototype(librairie, getNom().toString(), getRole(), getType(), retourneFichier());
		if (evenement != null) {
			temp.evenement = new ArrayList<ActeurParent>(1);
			temp.evenement.add(new ActeurParentGraphique(getNom().toString().toLowerCase(), pere));
			for (ActeurParent e : temp.evenement) {
				if (e instanceof ActeurParentGraphique) {
					try {
						if (getNom() != null)
							//https://code.google.com/p/langagelinotte/issues/detail?id=73
							e.mettreAjour(getNom().toString().toLowerCase(), valeur);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		temp.slots = slots;
		temp.slotsGreffon = slotsGreffon;
		temp.contratsPrototype = contratsPrototype;
		if (this.greffon != null) {
			temp.greffon = this.greffon;
			//creationGreffon();
			ajouterGreffonListener(temp.greffon);
		}
		temp.copierAttributs(this);
		return temp;
	}

	@Override
	public void viderActeur() throws ErreurException {
		super.viderActeur();
		Iterator<String> i = valeurs.getElements().iterator();
		while (i.hasNext()) {
			String chaine = i.next().toString();
			((Acteur) valeurs.get(chaine)).viderActeur();
		}

	}

	public Set<String> retourAttributs() {
		return valeurs.getElements();
	}

	/**
	 * Retourne une Map contenant les acteurs stockés par nom
	 * @return
	 */
	public Map<String, Acteur> retourAttributsMap() {
		return valeurs;
	}

	@Override
	public boolean isEspeceGraphique() {
		return false;
	}

	public Greffon getGreffon() {
		return greffon;
	}

	/**
	 * Retourne le prototype sous la forme "? est un P, A1 vaut ?" pour l'Atelier
	 * @return
	 */
	public String toTexte() {
		StringBuilder builder = new StringBuilder("? est un ").append(getNom().toString());
		List<String> keys = new ArrayList<String>(valeurs.getElements());
		for (String string : keys) {
			builder.append(", ").append(string).append(" vaut ?");
		}
		return builder.toString();
	}

}