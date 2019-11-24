package org.linotte.greffons.impl;

public class Article {

	private String titre, date, description, lien, image;

	public Article(String titre, String date, String description, String lien, String image) {
		this.titre = titre;
		this.date = date;
		this.description = description;
		this.lien = lien;
		this.image = image;
	}

	public String getTitre() {
		return titre;
	}

	public String getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public String getLien() {
		return lien;
	}

	public String getImage() {
		return image;
	}

}
