package org.linotte.greffons.java.interne;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.java.interne.a.MethodeInterneDirecte;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

import java.math.BigDecimal;

public class TailleMethodeInterne extends MethodeInterneDirecte {

	public TailleMethodeInterne() {
	}

	@Override
	public Acteur appeler(Acteur moi, Greffon greffon, Acteur... parametres) throws Exception {

		Acteur a2 = new Acteur(Role.NOMBRE, null);

		try {

			Acteur a1 = moi;

			if (a1.getRole() == Role.ESPECE)
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_MESURER);
			if (a1.getRole() == Role.NOMBRE)
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_MESURER);

			if (a1.getRole() == Role.TEXTE)
				a2.setValeur(new BigDecimal(((String) a1.getValeur()).length()));
			else if (a1.getRole() == Role.CASIER)
				a2.setValeur(new BigDecimal(((Casier) a1).taille()));

		} catch (java.lang.IndexOutOfBoundsException e) {
			throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
		} catch (java.lang.IllegalArgumentException e) {
			throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
		} catch (ClassCastException e) {
			throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());

		}

		return a2;
	}

	@Override
	public String parametres() {
		return "";
	}

	public String nom() {
		return "taille";
	}

}