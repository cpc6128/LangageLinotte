package org.linotte.moteur.xml.operation.simple;

import java.math.BigDecimal;
import java.math.MathContext;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.operation.i.OperationSimple;

public class Division implements OperationSimple {

	@Override
	public Object action(Object total, Object valeur_transformee, MathContext context) throws Exception {
		if (total instanceof Acteur && !(total instanceof Prototype) && !(total instanceof Casier)) {
			total = ((Acteur) total).getValeur();
		}
		if (valeur_transformee instanceof BigDecimal) {
			if (total instanceof BigDecimal) {
				// Amélioration de la precision mathématique
				BigDecimal n1 = (BigDecimal) total;
				BigDecimal n2 = (BigDecimal) valeur_transformee;
				context = Mathematiques.ameliorerPrecision(context, n1, n2);
				return Mathematiques.stripTrailingZerosIfNecessary(n1.divide(n2, context));
			} else {
				throw new Exception("Impossible de diviser un nombre avec " + total);
			}
		} else {
			throw new Exception("Impossible de diviser une chaine avec " + valeur_transformee);
		}
	}

}