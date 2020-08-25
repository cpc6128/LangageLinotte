/***********************************************************************
 * Linotte                                                             *
 * Version release date : April 7, 2011                                *
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

package org.linotte.moteur.xml.actions;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.ActionTest;
import org.linotte.moteur.xml.alize.test.Test;
import org.linotte.moteur.xml.alize.test.TestIn;
import org.linotte.moteur.xml.analyse.ItemXML;

public class TestUnitaireInAction extends Action implements ActionTest {

	public static final String NOM_ETAT = "test unitaire in";

	public TestUnitaireInAction() {
		super();
	}

	@Override
	public String clef() {
		return NOM_ETAT;
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		Acteur a = extractionDesActeurs(1, job, valeurs)[0];
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		Test test = new TestIn();
		test.valeur = String.valueOf(a.getValeur());
		runtimeContext.tests.add(test);
		return ETAT.PAS_DE_CHANGEMENT;
	}
}
