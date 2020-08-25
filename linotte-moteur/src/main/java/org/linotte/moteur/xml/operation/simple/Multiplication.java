package org.linotte.moteur.xml.operation.simple;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.operation.i.OperationSimple;

import java.math.BigDecimal;
import java.math.MathContext;

public class Multiplication implements OperationSimple {

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
				// A effectuer si c'est la valeur par défaut : 
				if (context == MathContext.DECIMAL128)
					context = Mathematiques.ameliorerPrecision(context, n1, n2);
				return Mathematiques.stripTrailingZerosIfNecessary(n1.multiply(n2, context));
			} else if (total instanceof String) {
				// Linotte 2.3
				String copie = (String) total;
				int max = ((BigDecimal) valeur_transformee).intValue();
				total = "";
				for (int i = 0; i < max; i++) {
					total = (String) total + copie;
				}
				return total;
			} else {
				throw new Exception("Impossible de multiplier un nombre avec " + total);
			}
		} else {
			if (valeur_transformee instanceof String && total instanceof BigDecimal) {
				// Linotte 2.3
				String copie = (String) valeur_transformee;
				int max = ((BigDecimal) total).intValue();
				total = "";
				for (int i = 0; i < max; i++) {
					total = (String) total + copie;
				}
				return total;
			} else
				throw new Exception("Impossible de multiplier une chaine avec " + valeur_transformee);
		}
	}

}