/***********************************************************************
 * Linotte                                                             *
 * Version release date : August 6, 2014                               *
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

package org.linotte.web.transformateur;

import org.alize.http.i.WebTransformateur;

/**
 * @author ??
 *
 */
public class HTMLFrancaisTransformateur implements WebTransformateur {

	@Override
	public boolean accepter(String extention) {
		return extention.toLowerCase().endsWith(".wliv") || extention.toLowerCase().endsWith(".liv");
	}

	@Override
	public StringBuilder traiter(StringBuilder in) {
		// POC (Preuve de concept), code très très moche à remplacer !
		String flux = in.toString();
		flux = flux.replaceAll("<corps>", "<body>");
		flux = flux.replaceAll("</corps>", "</body>");
		flux = flux.replaceAll("<tête>", "<head>");
		flux = flux.replaceAll("</tête>", "</head>");
		flux = flux.replaceAll("<ligne>", "<hr/>");
		flux = flux.replaceAll("<gras>", "<b>");
		flux = flux.replaceAll("</gras>", "</b>");
		return new StringBuilder(flux);
	}

}
