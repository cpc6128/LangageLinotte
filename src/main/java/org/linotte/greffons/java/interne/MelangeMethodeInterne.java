package org.linotte.greffons.java.interne;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.java.interne.a.MethodeInterne;
import org.linotte.greffons.outils.ObjetLinotteHelper;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

public class MelangeMethodeInterne extends MethodeInterne {

	public MelangeMethodeInterne(Acteur acteur) {
		super(acteur);
	}

	@Override
	public ObjetLinotte appeler(Greffon greffon, ObjetLinotte... parametres) throws Exception {
		try {

			// Transformation des paramètres si le developpeur utilise des types Java :
			if (parametres.length != 0) {
				throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
			}

			String s = (String) acteur.getValeur();

			int taille = s.length();
			List<String> nouvelle = new ArrayList<String>(taille);
			for (int i = 0; i < taille; i++)
				nouvelle.add(s.substring(i, i + 1));
			Collections.shuffle(nouvelle);
			StringBuilder buffer = new StringBuilder(taille);
			Iterator<String> i = nouvelle.iterator();
			while (i.hasNext())
				buffer.append(i.next());

			String retour = buffer.toString();

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
		return "";
	}

	public String nom() {
		return "mélanger";
	}

}
