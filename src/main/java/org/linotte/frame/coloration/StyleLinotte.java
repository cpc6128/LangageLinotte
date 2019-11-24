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

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.linotte.frame.coloration.StyleBuilder.STYLE;

public class StyleLinotte {

	public static SimpleAttributeSet style_article = null;

	public static SimpleAttributeSet style_token = null;

	public static SimpleAttributeSet style_systeme = null;

	public static SimpleAttributeSet style_error = null;

	public static SimpleAttributeSet style_commentaire = null;

	public static SimpleAttributeSet style_texte = null;

	public static SimpleAttributeSet style_doublure = null;

	public static SimpleAttributeSet style_variable_locale = null;

	public static SimpleAttributeSet style_variable_doublure = null;

	public static SimpleAttributeSet style_variable_systeme = null;

	public static SimpleAttributeSet style_structure = null;

	public static SimpleAttributeSet style_math = null;

	public static SimpleAttributeSet style_livre = null;

	public static SimpleAttributeSet style_sous_paragraphe = null;

	public static SimpleAttributeSet style_string = null;

	public static SimpleAttributeSet style_nombre = null;

	// Style par défaut :

	public static Style styleRacine = null;

	public StyleLinotte() {
		super();
		initStyles();

		for (STYLE s : StyleBuilder.STYLE.values()) {
			StyleBuilder.appliquerStyle(s);
		}

	}

	public static void initStyles() {
		// Style par défaut :
		style_article = new SimpleAttributeSet();
		StyleConstants.setForeground(style_article, new Color(201, 105, 84));
		StyleConstants.setItalic(style_article, false);

		style_token = new SimpleAttributeSet();
		StyleConstants.setForeground(style_token, new Color(127, 0, 85));
		StyleConstants.setItalic(style_token, false);

		style_commentaire = new SimpleAttributeSet();
		StyleConstants.setForeground(style_commentaire, new Color(79, 135, 107));
		StyleConstants.setItalic(style_commentaire, false);

		style_texte = new SimpleAttributeSet();
		StyleConstants.setForeground(style_texte, Color.BLACK);

		style_string = new SimpleAttributeSet();
		StyleConstants.setForeground(style_string, new Color(128, 128, 128));
		//StyleConstants.setBold(style_string, true);

		style_nombre = new SimpleAttributeSet();
		StyleConstants.setForeground(style_nombre, new Color(0, 127, 174));

		style_systeme = new SimpleAttributeSet();
		StyleConstants.setForeground(style_systeme, Color.MAGENTA);
		StyleConstants.setItalic(style_systeme, false);

		style_error = new SimpleAttributeSet();
		StyleConstants.setForeground(style_error, Color.RED);
		StyleConstants.setItalic(style_error, false);

		styleRacine = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		StyleConstants.setForeground(styleRacine, Color.BLUE);
		StyleConstants.setItalic(styleRacine, false);

		style_doublure = new SimpleAttributeSet();
		StyleConstants.setForeground(style_doublure, Color.GRAY);
		StyleConstants.setItalic(style_doublure, false);

		style_variable_locale = new SimpleAttributeSet(styleRacine);
		StyleConstants.setUnderline(style_variable_locale, true);

		style_variable_doublure = new SimpleAttributeSet(styleRacine);
		StyleConstants.setUnderline(style_variable_doublure, true);

		style_variable_systeme = new SimpleAttributeSet(styleRacine);
		StyleConstants.setItalic(style_variable_systeme, true);

		style_math = new SimpleAttributeSet();
		StyleConstants.setForeground(style_math, new Color(127, 100, 85));
		StyleConstants.setItalic(style_math, false);

		style_structure = new SimpleAttributeSet();
		StyleConstants.setForeground(style_structure, new Color(149, 125, 71));
		StyleConstants.setBold(style_structure, false);

		style_livre = new SimpleAttributeSet();
		StyleConstants.setForeground(style_livre, Color.BLACK);
		//StyleConstants.setBold(style_livre, true);

		style_sous_paragraphe = new SimpleAttributeSet();
		StyleConstants.setForeground(style_sous_paragraphe, new Color(250, 172, 172));
		StyleConstants.setBold(style_sous_paragraphe, false);
	}
}
