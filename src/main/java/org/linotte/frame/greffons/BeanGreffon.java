package org.linotte.frame.greffons;

import org.linotte.moteur.xml.actions.TildeAction.STATUS;

public class BeanGreffon {

	private String nom, description, version, url, homeURL, auteur;

	public String getAuteur() {
		return auteur;
	}

	public void setAuteur(String auteur) {
		this.auteur = auteur;
	}

	public String getHome() {
		return homeURL;
	}

	public void setHome(String home) {
		this.homeURL = home;
	}

	private STATUS statut;

	public STATUS getStatut() {
		return statut;
	}

	public void setStatut(STATUS statut) {
		this.statut = statut;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
