package org.linotte.greffons.java.interne;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.java.interne.a.MethodeInterneDirecte;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

import java.math.BigDecimal;

public class ExtraireMethodeInterne extends MethodeInterneDirecte {

	public ExtraireMethodeInterne() {
		super();
	}

	@Override
	public Acteur appeler(Acteur acteur, Greffon greffon, Acteur... parametres) throws Exception {

		if (parametres.length != 2) {
			throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
		}

		try {

			Acteur a1 = acteur;
			Acteur a2 = parametres[0];
			Acteur a3 = parametres[1];

			int pos1, pos2;

			if (!(a1.getRole() == Role.CASIER || a1.getRole() == Role.TEXTE))
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE, a1.toString());

			if (a2.getRole() != Role.NOMBRE)
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE, a2.toString());
			pos1 = ((BigDecimal) a2.getValeur()).intValueExact();

			if (a3.getRole() != Role.NOMBRE)
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE, a3.toString());
			pos2 = ((BigDecimal) a3.getValeur()).intValueExact();

			Acteur retour;

			if (a1.getRole() == Role.TEXTE) {
				String s1 = (String) a1.getValeur();
				try {
					s1 = s1.substring(pos1, pos2);
				} catch (Exception e) {
					throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE);
				}
				retour = new Acteur(Role.TEXTE, s1);
			} else {
				try {
					retour = new Casier(a1.retourneLibrairie(), null, a1.getRole(), ((Casier) a1).getTypeespece(), null);
					((Casier) retour).extraire(a1, pos1 - 1, pos2);
				} catch (Exception e) {
					throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE);
				}
			}

			return retour;

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
		return "( à partir de , jusqu'à )";
	}

	public String nom() {
		return "extraire";
	}

}
