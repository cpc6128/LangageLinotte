package org.linotte.frame.atelier;

import javax.swing.SwingConstants;

public class AbaqueAtelier {

	public static AbaqueAtelier theme = new AbaqueAtelier();

	public int buttonTextHorizontale = SwingConstants.BOTTOM;
	public int buttonTextVerticale = SwingConstants.CENTER;
	public int taille_split_tableau = 300;
	public int icone_taille = 32;
	public boolean petitmenu = false;

	public void setPetitMenu() {

		buttonTextHorizontale = SwingConstants.TOP;
		buttonTextVerticale = SwingConstants.RIGHT;
		taille_split_tableau = 200;
		icone_taille = 16;
		petitmenu = true;

	}
}
