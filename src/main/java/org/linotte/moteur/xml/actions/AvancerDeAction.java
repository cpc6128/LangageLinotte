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
import org.linotte.moteur.xml.analyse.Mathematiques;

public class AvancerDeAction extends Action implements IProduitCartesien {

	public AvancerDeAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe avancer de";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_AVANCER, job, valeurs);
		Acteur acteur1 = acteurs[0];
		Acteur acteur2 = acteurs[1];

		if (!(acteur2.getRole() == Role.NOMBRE) && !(acteur1.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_AVANCER);

		Prototype e = (Prototype) acteur1;
		if (!e.isEspeceGraphique())
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_AVANCER, e.toString());

		PrototypeGraphique eg = (PrototypeGraphique) e;

		if (eg.retourneAttribut("angle") == null) {
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_AVANCER, e.toString());
		}

		double angle = Math.toRadians(((BigDecimal) eg.retourneAttribut("angle").getValeur()).doubleValue());
		BigDecimal c = ((BigDecimal) acteur2.getValeur());

		BigDecimal x = (BigDecimal) eg.retourneAttribut("x").getValeur();
		BigDecimal y = (BigDecimal) eg.retourneAttribut("y").getValeur();

		BigDecimal x2 = x.add(c.multiply(new BigDecimal(Math.cos(angle)), Mathematiques.getPrecision()));
		BigDecimal y2 = y.add(c.multiply(new BigDecimal(Math.sin(angle)), Mathematiques.getPrecision()));
		double x_ = (x2.doubleValue());//Math.round
		double y_ = (y2.doubleValue());//Math.round
		if (eg.getTypeGraphique().equals(PrototypeGraphique.TYPE_GRAPHIQUE.CRAYON))
			CrayonDessinateur.dessineLigne(eg, (int) Math.round(x_), (int) Math.round(y_));
		eg.retourneAttribut("x").setValeur(new BigDecimal(x_));
		eg.retourneAttribut("y").setValeur(new BigDecimal(y_));

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
//