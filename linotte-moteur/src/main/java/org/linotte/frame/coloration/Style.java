/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

import javax.swing.text.AttributeSet;
import java.awt.*;

public class Style {

	private int position;

	private int taille;

	private AttributeSet style;

	private String url;

	private Color souligne;

	public Style(int position, int taille, AttributeSet style, String url, Color souligne) {
		super();
		this.position = position;
		this.taille = taille;
		this.style = style;
		this.url = url;
		this.setSouligne(souligne);
	}

	public Style(int position, int taille, AttributeSet style, String url) {
		super();
		this.position = position;
		this.taille = taille;
		this.style = style;
		this.url = url;
	}

	public int getPosition() {
		return position;
	}

	public AttributeSet getStyle() {
		return style;
	}

	public int getTaille() {
		return taille;
	}

	public String getUrl() {
		return url;
	}

	public Color getSouligne() {
		return souligne;
	}

	public void setSouligne(Color souligne) {
		this.souligne = souligne;
	}

}
