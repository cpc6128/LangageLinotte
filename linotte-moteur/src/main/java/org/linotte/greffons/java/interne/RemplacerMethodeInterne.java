package org.linotte.greffons.java.interne;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.java.interne.a.MethodeInterne;
import org.linotte.greffons.outils.ObjetLinotteHelper;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

public class RemplacerMethodeInterne extends MethodeInterne {

	public RemplacerMethodeInterne() {
		super();
	}

	@Override
	public ObjetLinotte appeler(Acteur acteur, Greffon greffon, ObjetLinotte... parametres) throws Exception {
		try {

			// Transformation des paramètres si le developpeur utilise des types Java :
			if (parametres.length != 2) {
				throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
			}

			String p1 = (String) transformerObjet(String.class, parametres[0]);
			String p2 = (String) transformerObjet(String.class, parametres[1]);

			String chaine = (String) acteur.getValeur();

			String retour = chaine.replace(p1, p2);

			return ObjetLinotteHelper.copy((String) retour);

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
		return "( source , cible )";
	}

	public String nom() {
		return "remplacer";
	}

}
