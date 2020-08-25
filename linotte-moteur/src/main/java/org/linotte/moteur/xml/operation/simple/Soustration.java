package org.linotte.moteur.xml.operation.simple;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.operation.i.OperationSimple;

import java.math.BigDecimal;
import java.math.MathContext;

public class Soustration implements OperationSimple {

	@Override
	public Object action(Object total, Object valeur_transformee, MathContext context) throws Exception {
		if (total instanceof Acteur && !(total instanceof Prototype) && !(total instanceof Casier)) {
			total = ((Acteur) total).getValeur();
		}
		if (valeur_transformee instanceof BigDecimal) {
			if (total instanceof BigDecimal) {
				BigDecimal n1 = ((BigDecimal) total);
				BigDecimal n2 = (BigDecimal) valeur_transformee;
				context = Mathematiques.ameliorerPrecision(context, n1, n2);
				return Mathematiques.stripTrailingZerosIfNecessary(n1.subtract(n2, context));
			} else {
				throw new Exception("Impossible de soustraire un nombre à " + total);
			}
		} else {
			throw new Exception("Impossible de soustraire une chaine à " + valeur_transformee);
		}
	}

}