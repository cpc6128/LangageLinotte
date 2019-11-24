/***********************************************************************
 * Linotte                                                             *
 * Version release date : December 02, 2008                             *
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
import org.linotte.moteur.xml.analyse.Mathematiques;

public class TournerAGaucheAction extends Action implements IProduitCartesien {

	public TournerAGaucheAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe tourner à gauche";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_TOURNER_A_GAUCHE, linotte, valeurs);
		Acteur acteur1 = acteurs[0];
		Acteur acteur2 = acteurs[1];

		if (!(acteur2.getRole() == Role.NOMBRE) && !(acteur1.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_TOURNER_A_GAUCHE);

		Prototype e = (Prototype) acteur1;
		if (!e.isEspeceGraphique())
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_TOURNER_A_GAUCHE, e.toString());

		PrototypeGraphique eg = (PrototypeGraphique) e;

		if (eg.retourneAttribut("angle") == null) {
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_TOURNER_A_GAUCHE, e.toString());
		}

		double c = ((BigDecimal) acteur2.getValeur()).doubleValue();
		double angle = ((BigDecimal) eg.retourneAttribut("angle").getValeur()).doubleValue();

		angle = Mathematiques.mod(angle - c, 360);
		eg.retourneAttribut("angle").setValeur(new BigDecimal(angle));

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
