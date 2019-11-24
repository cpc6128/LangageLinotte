package org.linotte.greffons.java.interne;

import java.math.BigDecimal;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.java.interne.a.MethodeInterne;
import org.linotte.greffons.outils.ObjetLinotteHelper;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

public class ExtraireMethodeInterne extends MethodeInterne {

	public ExtraireMethodeInterne(Acteur acteur) {
		super(acteur);
	}

	@Override
	public ObjetLinotte appeler(Greffon greffon, ObjetLinotte... parametres) throws Exception {
		try {

			// Transformation des paramètres si le developpeur utilise des types Java :
			if (parametres.length != 2) {
				throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
			}

			int i1 = ((BigDecimal) transformerObjet(BigDecimal.class, parametres[0])).intValue();
			int i2 = ((BigDecimal) transformerObjet(BigDecimal.class, parametres[1])).intValue();

			String s = (String) acteur.getValeur();

			String retour;

			try {
				retour = s.substring(i1, i2);
			} catch (Exception e) {
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXTRAIRE);
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
		return "( à partir de , jusqu'à )";
	}

	public String nom() {
		return "extraire";
	}

}
