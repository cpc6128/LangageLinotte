package org.linotte.moteur.xml.operation.simple;

import java.math.BigDecimal;
import java.math.MathContext;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.operation.i.OperationSimple;

public class Addition implements OperationSimple {

	@Override
	public Object action(Object total, Object valeur_transformee, MathContext context) throws Exception {
		if (total instanceof Acteur && !(total instanceof Prototype) && !(total instanceof Casier)) {
			total = ((Acteur) total).getValeur();
		}
		// Linotte 2.1.1
		if (total instanceof Casier) {
			total = ((Casier) total).clone();
			if (valeur_transformee instanceof Casier) {
				((Casier) total).addAll((Casier) valeur_transformee);
			} else {
				if (valeur_transformee instanceof String || valeur_transformee instanceof BigDecimal)
					valeur_transformee = new Acteur(valeur_transformee instanceof BigDecimal ? Role.NOMBRE : Role.TEXTE, valeur_transformee);
				((Casier) total).ajouterValeur((Acteur) valeur_transformee, true);
			}
			return total;
		}
		if (valeur_transformee instanceof BigDecimal) {
			if (total instanceof BigDecimal) {
				// Amélioration de la precision mathématique
				BigDecimal n1 = (BigDecimal) total;
				BigDecimal n2 = (BigDecimal) valeur_transformee;
				context = Mathematiques.ameliorerPrecision(context, n1, n2);
				total = Mathematiques.stripTrailingZerosIfNecessary(n1.add(n2, context));
			} else {
				if (total instanceof Acteur) {
					Acteur tmp = (Acteur) total;
					if (tmp.getRole() == Role.TEXTE) {
						return (String) tmp.getValeur() + valeur_transformee;
					} else if (tmp.getRole() == Role.NOMBRE) {
						// Amélioration de la precision mathématique
						BigDecimal n1 = (BigDecimal) tmp.getValeur();
						BigDecimal n2 = (BigDecimal) valeur_transformee;
						context = Mathematiques.ameliorerPrecision(context, n1, n2);
						return Mathematiques.stripTrailingZerosIfNecessary(n1.add(n2, context));
					} else
						throw new Exception("Impossible d'additionner avec " + total);
				} else
					return ((String) total) + valeur_transformee;
			}
		} else {
			if (valeur_transformee instanceof String) {
				if (total instanceof BigDecimal) {
					total = ((BigDecimal) total).toString() + valeur_transformee;
				} else {
					if (total instanceof Acteur) {
						Acteur tmp = (Acteur) total;
						if (tmp.getRole() == Role.TEXTE) {
							return (String) tmp.getValeur() + valeur_transformee;
						} else if (tmp.getRole() == Role.NOMBRE) {
							return ((BigDecimal) tmp.getValeur()).toString() + valeur_transformee;
						} else
							throw new Exception("Impossible d'additionner avec " + total);
					} else
						return ((String) total) + valeur_transformee;
				}
			} else {
				throw new Exception("Impossible d'additionner");
			}
		}
		return total;
	}

}