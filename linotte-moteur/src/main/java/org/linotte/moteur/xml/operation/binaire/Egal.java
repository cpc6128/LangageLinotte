/***********************************************************************
 * Linotte                                                             *
 * Version release date : April 1, 2011                                *
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

package org.linotte.moteur.xml.operation.binaire;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.xml.actions.ConditionAction;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.analyse.Mathematiques.Temoin;
import org.linotte.moteur.xml.operation.i.OperationBinaire;
import org.linotte.moteur.xml.outils.MathematiquesConstantes;

import java.math.BigDecimal;

public class Egal extends OperationBinaire {

	@Override
	public BigDecimal calculer(BigDecimal bd1, BigDecimal bd2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object calculer(Job moteur, Temoin temoin, Object token_courant) throws Exception {
		Object bd1;
		Object bd2;

		boolean operationChaine = false;
		boolean operationPrototype = false;

		Object o1;
		if (valeurBrute1 instanceof Object[])
			o1 = Mathematiques.shrink((Object[]) valeurBrute1, moteur, temoin);
		else
			o1 = Mathematiques.shrink(valeurBrute1, moteur, temoin);

		Object o2;
		if (valeurBrute2 instanceof Object[])
			o2 = Mathematiques.shrink((Object[]) valeurBrute2, moteur, temoin);
		else
			o2 = Mathematiques.shrink(valeurBrute2, moteur, temoin);

		if (o1 instanceof Prototype) {
			bd1 = o1;
			operationPrototype = true;
		} else if (o1 instanceof Acteur && ((Acteur) o1).getRole() == Role.NOMBRE) {
			bd1 = (BigDecimal) ((Acteur) o1).getValeur();
		} else if (o1 instanceof BigDecimal) {
			bd1 = (BigDecimal) o1;
		} else if (o1 instanceof Acteur && ((Acteur) o1).getRole() == Role.TEXTE) {
			bd1 = (String) ((Acteur) o1).getValeur();
			operationChaine = true;
		} else if (o1 instanceof String) {
			bd1 = (String) o1;
			operationChaine = true;
		} else {
			throw new Exception("Opération mathématique 'égal' interdite avec " + o1);
		}

		if (o2 instanceof Prototype) {
			bd2 = o2;
			operationPrototype = true;
		} else if (o2 instanceof Acteur && ((Acteur) o2).getRole() == Role.NOMBRE) {
			bd2 = (BigDecimal) ((Acteur) o2).getValeur();
		} else if (o2 instanceof BigDecimal) {
			bd2 = (BigDecimal) o2;
		} else if (o2 instanceof Acteur && ((Acteur) o2).getRole() == Role.TEXTE) {
			bd2 = (String) ((Acteur) o2).getValeur();
			operationChaine = true;
		} else if (o2 instanceof String) {
			bd2 = (String) o2;
			operationChaine = true;
		} else {
			throw new Exception("Opération mathématique 'égal' interdite avec " + o2);
		}

		if (operationPrototype) {
			if (!(bd1 instanceof Prototype))
				throw new Exception("Opération mathématique 'égal' interdite avec " + bd1);
			if (!(bd2 instanceof Prototype))
				throw new Exception("Opération mathématique 'égal' interdite avec " + bd2);
			return (ConditionAction.equals((Acteur) bd1, (Acteur) bd2) ? MathematiquesConstantes.VRAI : MathematiquesConstantes.FAUX);
		} else if (operationChaine)
			return (((String) bd1).equalsIgnoreCase((String) bd2) ? MathematiquesConstantes.VRAI : MathematiquesConstantes.FAUX);
		else
			return (((BigDecimal) bd1).compareTo((BigDecimal) bd2) == 0 ? MathematiquesConstantes.VRAI : MathematiquesConstantes.FAUX);

	}
}
