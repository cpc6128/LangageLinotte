/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                        *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                            *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.xml.actions;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.RetournerException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;

public class RetournerAction extends Action implements ActionDispatcher {

	public RetournerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe retourner";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_RETOURNER, linotte, valeurs)[0];
		throw new RetournerException(a);

	}

}
