/***********************************************************************
 * Linotte                                                             *
 * Version release date : Febrary 04, 2008                             *
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

import java.math.BigDecimal;

import org.linotte.frame.latoile.dessinateur.CrayonDessinateur;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class DeplacerVersBasAction extends Action implements IProduitCartesien {

	public DeplacerVersBasAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe déplacer vers bas";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_DEPLACER, job, valeurs);
		Acteur acteur1 = acteurs[0];
		Acteur acteur2 = acteurs[1];

		if (!(acteur2.getRole() == Role.NOMBRE) || !(acteur1.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_DEPLACER);

		Prototype e = (Prototype) acteur1;
		if (!e.isEspeceGraphique())
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_DEPLACER, e.toString());

		PrototypeGraphique eg = (PrototypeGraphique) e;

		if (eg.retourneAttribut("x") != null) {
			// Crayon ?
			BigDecimal x = ((BigDecimal) eg.retourneAttribut("x").getValeur());
			BigDecimal y = ((BigDecimal) eg.retourneAttribut("y").getValeur()).add(((BigDecimal) acteur2.getValeur()));
			if (eg.getTypeGraphique().equals(PrototypeGraphique.TYPE_GRAPHIQUE.CRAYON)) {
				CrayonDessinateur.dessineLigne(eg, x.intValue(), y.intValue());
			}
			eg.retourneAttribut("y").setValeur(y);
		}
		if (eg.retourneAttribut("x1") != null)
			eg.retourneAttribut("y1").setValeur(((BigDecimal) eg.retourneAttribut("y1").getValeur()).add(((BigDecimal) acteur2.getValeur())));
		if (eg.retourneAttribut("x2") != null)
			eg.retourneAttribut("y2").setValeur(((BigDecimal) eg.retourneAttribut("y2").getValeur()).add(((BigDecimal) acteur2.getValeur())));

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
