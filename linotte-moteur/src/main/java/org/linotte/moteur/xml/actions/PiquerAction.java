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

import java.math.BigDecimal;

public class PiquerAction extends Action implements IProduitCartesien {

	public PiquerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe piquer";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_PIQUER, job, valeurs);
		Acteur acteur1 = acteurs[0];
		Acteur acteur2 = acteurs[1];
		Acteur acteur3 = acteurs[2];

		if (!(acteur2.getRole() == Role.NOMBRE) && !(acteur3.getRole() == Role.NOMBRE) && !(acteur1.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_PIQUER);

		Prototype e = (Prototype) acteur1;
		if (!e.isEspeceGraphique())
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_PIQUER, e.toString());

		PrototypeGraphique eg = (PrototypeGraphique) e;

		if (!eg.getTypeGraphique().equals(PrototypeGraphique.TYPE_GRAPHIQUE.CRAYON))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_PIQUER, e.toString());

		// Crayon ?
		if (eg.getTypeGraphique().equals(PrototypeGraphique.TYPE_GRAPHIQUE.CRAYON)) {
			CrayonDessinateur.dessinePoint(eg, ((BigDecimal) acteur2.getValeur()).intValue(), ((BigDecimal) acteur3.getValeur()).intValue(), eg.getToile());
		}
		eg.retourneAttribut("x").setValeur(acteur2.getValeur());
		eg.retourneAttribut("y").setValeur(acteur3.getValeur());
		return ETAT.PAS_DE_CHANGEMENT;
	}

}
