package org.linotte.greffons.java.interne;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.java.interne.a.MethodeInterneDirecte;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MelangeMethodeInterne extends MethodeInterneDirecte {

	public MelangeMethodeInterne(Acteur acteur) {
		super(acteur);
	}

	@Override
	public Acteur appeler(Greffon greffon, Acteur... parametres) throws Exception {
		try {

			Acteur a = acteur;

			if (a.getRole() == Role.ESPECE)
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_MELANGER, a.toString());

			if (a.getRole() == Role.CASIER) {
				Casier c = (Casier) a;
				c.melanger();
			} else if (a.getRole() == Role.TEXTE) {
				String s = (String) a.getValeur();
				int taille = s.length();
				List<String> nouvelle = new ArrayList<String>(taille);
				for (int i = 0; i < taille; i++)
					nouvelle.add(s.substring(i, i + 1));
				Collections.shuffle(nouvelle);
				StringBuilder buffer = new StringBuilder(taille);
				Iterator<String> i = nouvelle.iterator();
				while (i.hasNext())
					buffer.append(i.next());
				a.setValeur(buffer.toString());
			} else if (a.getRole() == Role.NOMBRE)
				a.setValeur(new BigDecimal(Math.floor(((BigDecimal) a.getValeur()).doubleValue() * Math.random())));

			return a;

		} catch (java.lang.IndexOutOfBoundsException e) {
			throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
		} catch (java.lang.IllegalArgumentException e) {
			throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
		} catch (ClassCastException e) {
			throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());

		}
	}

	@Override
	public String parametres() {
		return "";
	}

	public String nom() {
		return "mélanger";
	}

}