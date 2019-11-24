package org.linotte.greffons.java.interne;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.java.interne.a.MethodeInterne;
import org.linotte.greffons.outils.ObjetLinotteHelper;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

public class TailleMethodeInterne extends MethodeInterne {

	public TailleMethodeInterne(Acteur acteur) {
		super(acteur);
	}

	@Override
	public ObjetLinotte appeler(Greffon greffon, ObjetLinotte... parametres) throws Exception {
		try {

			// Transformation des parametres si le developpeur utilise des types Java :
			if (parametres.length != 0) {
				throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
			}

			int retour;
			if (acteur.getRole() == Role.CASIER) {
				retour = ((Casier) acteur).taille();
			} else {
				retour = ((String) acteur.getValeur()).length();
			}

			return ObjetLinotteHelper.copy(retour);

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
		return "taille";
	}

}
