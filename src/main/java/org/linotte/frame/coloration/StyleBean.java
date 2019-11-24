package org.linotte.frame.coloration;

import java.awt.Color;

public class StyleBean {

	private Color couleur;

	private boolean italique;
	private boolean souligne;
	private boolean gras;
	private boolean barre;

	private int size;

	private String police;

	public String getPolice() {
		return police;
	}

	public void setPolice(String police) {
		this.police = police;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Color getCouleur() {
		return couleur;
	}

	public void setCouleur(Color couleur) {
		this.couleur = couleur;
	}

	public boolean isItalique() {
		return italique;
	}

	public void setItalique(boolean italique) {
		this.italique = italique;
	}

	public boolean isSouligne() {
		return souligne;
	}

	public void setSouligne(boolean souligne) {
		this.souligne = souligne;
	}

	public boolean isGras() {
		return gras;
	}

	public void setGras(boolean gras) {
		this.gras = gras;
	}

	public boolean isBarre() {
		return barre;
	}

	public void setBarre(boolean barre) {
		this.barre = barre;
	}

}
