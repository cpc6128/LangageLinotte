package org.linotte.greffons.api;

import org.linotte.greffons.java.GreffonPrototype.Entrée;

import java.util.*;

public class Greffon {

	private Class<? extends org.linotte.greffons.externe.Greffon> class_;

	private Object objet;

	private String id;

	private String version;

	private String auteur;

	private String description;

	private String nom;

	private String espece;
	
	private String lang;

	private Map<String, Attribut> attributs = new HashMap<String, Attribut>();

	private List<Entrée> slots = null;

	public static class Attribut {

		private String type;

		private String valeur;

		public Attribut(String pType) {
			type = pType;
		}

		public Attribut(String pType, String pValeur) {
			type = pType;
			valeur = pValeur;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getValeur() {
			return valeur;
		}

		public void setValeur(String valeur) {
			this.valeur = valeur;
		}

	}

	public Class<? extends org.linotte.greffons.externe.Greffon> getClass_() {
		return class_;
	}

	public void setClass_(Class<? extends org.linotte.greffons.externe.Greffon> class_) {
		this.class_ = class_;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toTexte() {
		StringBuilder builder = new StringBuilder("? est un ").append(espece.toLowerCase());
		List<String> keys = new ArrayList<String>(attributs.keySet());
		Collections.sort(keys);
		for (String string : keys) {
			builder.append(", ").append(string).append(" vaut ");
			Attribut attribut = attributs.get(string);
			String valeur = "?";
			if (attribut.getValeur() != null) {
				if ("texte".equalsIgnoreCase(attribut.getType())) {
					valeur = "\"" + attribut.getValeur() + "\"";
				} else {
					valeur = attribut.getValeur();
				}
			}
			builder.append(valeur);
		}
		return builder.toString();
	}

	@Override
	public String toString() {
		return nom + " ( version " + version + " ). " + description + ". Auteur : " + auteur;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getNom() {
		return nom;
	}

	public void setAttributs(Map<String, Attribut> p) {
		attributs = p;
	}

	public Map<String, Attribut> getAttributs() {
		return attributs;
	}

	public void setObjet(Object objet) {
		this.objet = objet;
	}

	public Object getObjet() {
		return objet;
	}

	public void setEspece(String pespece) {
		espece = pespece;
	}

	public void setSlots(List<Entrée> methodes) {
		slots = methodes;
	}

	public List<Entrée> getSlots() {
		return slots;
	}

	public org.linotte.greffons.externe.Greffon newInstance() {
		org.linotte.greffons.externe.Greffon temp;
		try {
			temp = (org.linotte.greffons.externe.Greffon) getClass_().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		} catch (SecurityException e) {
			e.printStackTrace();
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			return null;
		}
		return temp;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

}
