package org.linotte.moteur.xml.operation.ternaire;

import java.math.BigDecimal;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.analyse.Mathematiques.Temoin;
import org.linotte.moteur.xml.operation.i.OperationTernaire;

public class Ter extends OperationTernaire {

	public Object calculer(Job moteur, Temoin temoin, Object token_courant) throws Exception {
		BigDecimal bd1;
		Object bd2, bd3;

		Object o1;
		if (valeurBrute1 instanceof Object[])
			o1 = Mathematiques.shrink((Object[]) valeurBrute1, moteur, temoin);
		else
			o1 = Mathematiques.shrink(valeurBrute1, moteur, temoin);

		if (o1 instanceof BigDecimal) {
			bd1 = (BigDecimal) o1;
		} else if (o1 instanceof Acteur && ((Acteur) o1).getRole() == Role.NOMBRE) {
			bd1 = (BigDecimal) ((Acteur) o1).getValeur();
		} else {
			throw new Exception("Opération mathématique '" + token_courant + "' interdite avec " + o1);
		}

		if (bd1.intValue() > 0) {
			Object o2;
			if (valeurBrute2 instanceof Object[])
				o2 = Mathematiques.shrink((Object[]) valeurBrute2, moteur, temoin);
			else
				o2 = Mathematiques.shrink(valeurBrute2, moteur, temoin);

			if (o2 instanceof BigDecimal) {
				bd2 = (BigDecimal) o2;
			} else if (o2 instanceof Acteur && ((Acteur) o2).getRole() == Role.NOMBRE) {
				bd2 = (BigDecimal) ((Acteur) o2).getValeur();
			} else if (o2 instanceof Acteur && ((Acteur) o2).getRole() == Role.TEXTE) {
				bd2 = (String) ((Acteur) o2).getValeur();
			} else if (o2 instanceof String) {
				bd2 = (String) o2;
			} else {
				throw new Exception("Opération mathématique '" + token_courant + "' interdite avec " + o2);
			}
			return bd2;
		} else {
			Object o3;
			if (valeurBrute3 instanceof Object[])
				o3 = Mathematiques.shrink((Object[]) valeurBrute3, moteur, temoin);
			else
				o3 = Mathematiques.shrink(valeurBrute3, moteur, temoin);

			if (o3 instanceof BigDecimal) {
				bd3 = (BigDecimal) o3;
			} else if (o3 instanceof Acteur && ((Acteur) o3).getRole() == Role.NOMBRE) {
				bd3 = (BigDecimal) ((Acteur) o3).getValeur();
			} else if (o3 instanceof Acteur && ((Acteur) o3).getRole() == Role.TEXTE) {
				bd3 = (String) ((Acteur) o3).getValeur();
			} else if (o3 instanceof String) {
				bd3 = (String) o3;
			} else {
				throw new Exception("Opération mathématique '" + token_courant + "' interdite avec " + o3);
			}
			return bd3;

		}

	}

}
