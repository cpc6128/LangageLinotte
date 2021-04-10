/***********************************************************************
 * Linotte                                                             *
 * Version release date : December 15, 2008                            *
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

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.externe.Tube;
import org.linotte.greffons.outils.ObjetLinotteFactory;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class DechargerAction extends Action implements IProduitCartesien {

	public DechargerAction() {
		super();
	}

	@Override
	public String clef() {
		return "décharger tube";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		Acteur[] liste = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_DECHARGER, job, valeurs);
		int nb_parametre = liste.length;
		Acteur a = liste[0];
		Acteur a1 = liste[1];
		Acteur a2 = null;
		if (nb_parametre > 2) {
			a2 = liste[2];
		}

		if (!(a1.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_DECHARGER, a.toString());

		if (a2 != null && !(a2.getRole() == Role.TEXTE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_DECHARGER, a2.toString());

		Prototype e = (Prototype) a1;
		Greffon greffon = e.getGreffon();
		if (greffon == null)
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_DECHARGER, a1.toString());

		if (!(greffon instanceof Tube)) {
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_DECHARGER, a1.toString());
		}

		Tube tube = (Tube) greffon;

		ObjetLinotte structure = ObjetLinotteFactory.createStructure(a, runtimeContext.getLibrairie());

		try {
			ObjetLinotte retour;
			if (a2 == null)
				retour = tube.decharger(structure);
			else
				retour = tube.decharger(a2.getValeur().toString(), structure);
			ObjetLinotteFactory.copy(retour, a, runtimeContext.getLibrairie());
		} catch (GreffonException e1) {
			throw new ErreurException(Constantes.ERREUR_GREFFON, e1.getMessage());
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
