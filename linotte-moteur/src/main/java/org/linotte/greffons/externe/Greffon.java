/***********************************************************************
 * Linotte                                                             *
 * Version release date : January 11, 2009                             *
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
 *                                                                     *
 ***********************************************************************/

package org.linotte.greffons.externe;

import java.awt.*;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;

/**
 * 
 * Bus de communication
 * 
 * @author Ronan Mounès
 * @version 1.5
 * 
 */
public abstract class Greffon {

	public List<GreffonValeurListener> listeners = new ArrayList<GreffonValeurListener>();

	public Map<String, ObjetLinotte> valeurs = new HashMap<String, ObjetLinotte>();

	public static interface ObjetLinotte {

		ObjetLinotte clone();

	}

	public enum ROLE {
		NOMBRE, TEXTE, DRAPEAU, CASIER, ESPECE, NON_VALORISE
	};

	/*
	 * Annotations :
	 */

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface DocumentationHTML {

		String value();

	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface Slot {

		String nom() default "";

		ROLE sortie() default ROLE.NON_VALORISE;

		ROLE[] entree() default ROLE.NON_VALORISE;

	}

	/*
	 * Interfaces :
	 */

	public interface GreffonValeurListener {

		void setValeur(String clef, Object valeur);

	}

	public interface ListenerGreffons {

		void execute();

	}

	/*
	 * Classes :
	 */

	public static abstract class Couple<K, V> implements ObjetLinotte, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		K clef;

		V valeur;

		public Couple() {
		}

		public Couple(K k, V v) {
			clef = k;
			valeur = v;
		}

		public K getClef() {
			return clef;
		}

		public void setClef(K clef) {
			this.clef = clef;
		}

		public V getValeur() {
			return valeur;
		}

		public void setValeur(V valeur) throws GreffonException {
			this.valeur = valeur;
		}

		@Override
		public abstract ObjetLinotte clone();

	}

	/**
	 * TODO A documenter
	 * 
	 */
	public static class Acteur extends Couple<String, Object> implements ObjetLinotte {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private ROLE role;

		public Acteur(ROLE r) {
			super();
			role = r;
		}

		public Acteur(ROLE r, String k, Object v) throws GreffonException {
			super(k, v);
			role = r;
			if (role == ROLE.TEXTE || role == ROLE.NOMBRE || role == ROLE.DRAPEAU) {
				if (v != null) {
					if (role == ROLE.TEXTE && !(v instanceof String)) {
						throw new GreffonException("Rôle incorrect pour la valeur : " + v);
					} else if ((role == ROLE.NOMBRE || role == ROLE.DRAPEAU) && !(v instanceof BigDecimal)) {
						throw new GreffonException("Rôle incorrect pour la valeur : " + v);
					}
				}
			} else {
				throw new GreffonException("Rôle incorrect  : " + r.name());
			}
		}

		@Override
		public String toString() {
			return getClef() + "/" + getValeur();
		}

		@Override
		public void setValeur(Object valeur) throws GreffonException {
			if (valeur != null) {
				if (role == ROLE.TEXTE && (valeur instanceof BigDecimal)) {
					super.setValeur(String.valueOf(valeur));
					return;
				} else if (role == ROLE.TEXTE && !(valeur instanceof String)) {
					throw new GreffonException("rôle incorrect pour la valeur : " + valeur);
				} else if ((role == ROLE.NOMBRE || role == ROLE.DRAPEAU) && !(valeur instanceof BigDecimal)) {
					throw new GreffonException("rôle incorrect pour la valeur : " + valeur);
				}
			}
			super.setValeur(valeur);
		}

		@Override
		public ObjetLinotte clone() {
			try {
				return new Acteur(role, clef, valeur);
			} catch (GreffonException e) {
				// Impossible d'arriver ici !
				return null;
			}
		}

		public ROLE getRole() {
			return role;
		}

	}

	public static abstract class ListeClonable<K> extends ArrayList<K> implements ObjetLinotte {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		@SuppressWarnings("unchecked")
		public ObjetLinotte clone() {
			ListeClonable<K> cloneur = (ListeClonable<K>) super.clone();
			cloneur.clear();
			Iterator<K> iterator = this.iterator();
			while (iterator.hasNext()) {
				cloneur.add((K) ((ObjetLinotte) iterator.next()).clone());
			}
			return cloneur;
		}
	}

	/**
	 * TODO A documenter
	 * 
	 */

	public static class Casier extends ListeClonable<ObjetLinotte> implements ObjetLinotte {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private ROLE roleContenant;

		public Casier(ROLE rôleContenant) {
			this.roleContenant = rôleContenant;
		}

		public ROLE getRoleContenant() {
			return roleContenant;
		}

	}

	/**
	 * TODO A documenter
	 *
	 */

	public static class Espece extends ListeClonable<Acteur> implements ObjetLinotte {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private String type;
		// Utilisé pour stocker les espèces graphiques
		public transient org.linotte.moteur.entites.Acteur porteur = null;

		public Espece(String type) {
			this.type = type;
		}

		// TODO, a réécrire !
		public Acteur getAttribut(String valeur) {
			for (Acteur acteur : this) {
				if (acteur.getClef().equalsIgnoreCase(valeur)) {
					return acteur;
				}
			}
			return null;
		}

		public String getType() {
			return type;
		}

	}

	public static class GreffonException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String message;

		public GreffonException(String message) {
			this.message = message;
		}

		@Override
		public String getMessage() {
			return message;
		}

	};

	/**
	 * Chargé par l'environnement Linotte sinon à null
	 * 
	 */
	private RessourceManager ressourceManager = new RessourceManager() {
		public String analyserChemin(String chemin) {
			return chemin;
		}

		public boolean isInternetUrl(String url) {
			return url != null && url.toLowerCase().startsWith("http://");
		}

		public void setChangement() {
		}

		public Image getImage(String string) {
			return null;
		}
	};

	public static interface RessourceManager {

		String analyserChemin(String chemin);

		boolean isInternetUrl(String url);

		void setChangement();

		Image getImage(String string);

	}

	/**
	 * 
	 * @param ressourceManager
	 */
	public final void setRessourceManager(RessourceManager ressourceManager) {
		this.ressourceManager = ressourceManager;
	}

	/**
	 * 
	 * @return
	 */
	public final RessourceManager getRessourceManager() {
		return ressourceManager;
	}

	/**
	 * Recupere la valeur de l'attribut <code>clef</code>
	 * @param clef
	 * @return
	 */
	public ObjetLinotte getAttribute(String clef) {
		if (clef != null)
			return valeurs.get(clef.toLowerCase());
		else
			return null;
	}

	/**
	 * Recupere la valeur de l'attribut <code>clef</code>
	 * @param clef
	 * @return la valeur de l'attribut
	 */
	public String getAttributeAsString(String clef) {
		return (String) ((Acteur) valeurs.get(clef.toLowerCase())).getValeur();
	}

	/**
	 * Recupere la valeur de l'attribut <code>clef</code>
	 * @param clef
	 * @return la valeur de l'attribut
	 */
	public BigDecimal getAttributeAsBigDecimal(String clef) {
		return (BigDecimal) ((Acteur) valeurs.get(clef.toLowerCase())).getValeur();
	}

	/**
	 * Valorise l'attribut <code>clef</code> avec <code>valeur</code>
	 * @param clef
	 * @param valeur
	 */
	public void setAttribute(String clef, String valeur) {
		if (clef != null) {
			try {
				valeurs.put(clef.toLowerCase(), new Acteur(ROLE.TEXTE, null, valeur));
			} catch (GreffonException e) {
			}
			for (GreffonValeurListener listener : listeners) {
				listener.setValeur(clef, valeur);
			}
		}
	}

	/**
	 * Valorise l'attribut <code>clef</code> avec <code>valeur</code>
	 * @param clef
	 * @param valeur
	 */
	public void setValeur(String clef, BigDecimal valeur) {
		if (clef != null) {
			try {
				valeurs.put(clef.toLowerCase(), new Acteur(ROLE.NOMBRE, null, valeur));
			} catch (GreffonException e) {
			}
			for (GreffonValeurListener listener : listeners) {
				listener.setValeur(clef, valeur);
			}
		}
	}

	public void addGreffonValeurListener(GreffonValeurListener listener) {
		listeners.clear();
		listeners.add(listener);
	}

	/**
	 * Méthode appelée dés que la valeur 'clef' est modifiée
	 * 
	 * @param clef
	 * @throws GreffonException
	 */
	public boolean fireProperty(String clef) throws GreffonException {
		return false;
	}

}