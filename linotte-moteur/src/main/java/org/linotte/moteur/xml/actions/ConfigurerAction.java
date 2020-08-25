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
import org.linotte.greffons.externe.Tube;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ConfigurerAction extends Action implements IProduitCartesien {

	public ConfigurerAction() {
		super();
	}

	@Override
	public String clef() {
		return "configurer tube";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {
		Acteur[] liste = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_CONFIGURER, linotte, valeurs);
		Acteur a = liste[0];
		Acteur a2 = liste[1];

		if (!(a.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CONFIGURER, a.toString());

		if (!(a2.getRole() == Role.TEXTE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CONFIGURER, a2.toString());

		Prototype e = (Prototype) a;
		Greffon greffon = e.getGreffon();
		if (greffon == null)
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CONFIGURER, a.toString());

		if (!(greffon instanceof Tube)) {
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CONFIGURER, a.toString());
		}

		Tube tube = (Tube) greffon;

		try {
			tube.configurer(a2.getValeur().toString());
		} catch (GreffonException e1) {
			throw new ErreurException(Constantes.ERREUR_GREFFON, e1.getMessage());
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

}
