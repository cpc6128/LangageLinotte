/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 8, 2007                                 *
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

package org.linotte.frame.coloration;

import java.awt.*;

public class StyleItem {

	public static enum STYLE {
		ARTICLE, TOKEN, VALEUR, SYSTEME, CHAINE, DOUBLURE, VARIABLE_LOCALE, VARIABLE_SYSTEME, MATH, STRUCTURE, VARIABLE_DOUBLURE, LIVRE, SOUS_PARAGRAPHE, STRING, NOMBRE
	};

	private STYLE type;

	private int debut;

	/**
	 * 
	 */
	private int taille;

	private String url;

	private Color souligne;

	public StyleItem(STYLE type, int debut, int taille) {
		super();
		this.type = type;
		this.debut = debut;
		this.taille = taille;
	}

	public StyleItem(STYLE type, int debut, int taille, Color souligne) {
		super();
		this.type = type;
		this.debut = debut;
		this.taille = taille;
		this.setSouligne(souligne);
	}

	public StyleItem(STYLE type, int debut, int taille, String url) {
		super();
		this.type = type;
		this.debut = debut;
		this.taille = taille;
		this.url = url;
	}

	public int getDebut() {
		return debut;
	}

	public int getTaille() {
		return taille;
	}

	public STYLE getType() {
		return type;
	}

	@Override
	public String toString() {
		return type.name();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setType(STYLE type) {
		this.type = type;
	}

	public Color getSouligne() {
		return souligne;
	}

	public void setSouligne(Color souligne) {
		this.souligne = souligne;
	}

}
