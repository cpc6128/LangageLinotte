/***********************************************************************
 * Linotte                                                             *
 * Version release date : March 22, 2010                               *
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

package org.linotte.frame.moteur;

import org.linotte.frame.cahier.Cahier;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.Parseur;

/**
 * Exéucteur utilisé pour formater les livres
 */
public class Formater {

	public static void action(Cahier cahier, Linotte lecteur) {
		if (cahier.getDocumentCahier() == null)
			return;
		String s = cahier.retourneTexteCahier() + "\n";
		StringBuilder sb = new StringBuilder(s);
		ParserContext atelierOutils = new ParserContext(ParserContext.MODE.FORMATAGE);
		atelierOutils.linotte = lecteur;
		try {
			Parseur moteur = new Parseur();
			moteur.parseLivre(sb, atelierOutils);
			cahier.effacerCahier();
			cahier.ecrireInsertCachier(atelierOutils.bufferFormatage.toString());
			cahier.setPositionCahier(0);
		} catch (LectureException e) {
		} catch (Exception e) {
			//System.out.println("Grrr : erreur analyse");
			//e.printStackTrace();
		} catch (Error e) {
			//System.out.println("Grrr : erreur analyse");
			//e.printStackTrace();
		} finally {
			cahier.setEditeurUpdate(true);
		}
	}

	public static String action(String cahier, Linotte lecteur) {
		String s = cahier + "\n";
		StringBuilder sb = new StringBuilder(s);
		ParserContext atelierOutils = new ParserContext(ParserContext.MODE.FORMATAGE);
		atelierOutils.linotte = lecteur;
		try {
			Parseur moteur = new Parseur();
			moteur.parseLivre(sb, atelierOutils);
			return atelierOutils.bufferFormatage.toString();
		} catch (LectureException e) {
		} catch (Exception e) {
		} catch (Error e) {
		} finally {
		}
		return cahier;
	}

}
