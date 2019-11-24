package org.linotte.greffons.java.interne;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.java.interne.a.MethodeInterne;
import org.linotte.greffons.outils.ObjetLinotteHelper;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

public class CapitaliserMethodeInterne extends MethodeInterne {

	public CapitaliserMethodeInterne(Acteur acteur) {
		super(acteur);
	}

	@Override
	public ObjetLinotte appeler(Greffon greffon, ObjetLinotte... parametres) throws Exception {
		try {

			// Transformation des paramètres si le developpeur utilise des types Java :
			if (parametres.length != 0) {
				throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
			}

			String chaine = (String) acteur.getValeur() + "*"; // Pour palier le problème du dernier espace !
			StringBuffer retour = new StringBuffer();

			String[] tableau = chaine.split("\\s");
			int pos = 0;
			for (String item : tableau) {
				char[] minitableau = item.toLowerCase().toCharArray();
				if (minitableau.length > 0) {
					minitableau[0] = Character.toUpperCase(minitableau[0]);
					item = new String(minitableau);
				}
				retour.append(item);
				if (pos != tableau.length - 1)
					retour.append(" ");
				pos++;
			}

			return ObjetLinotteHelper.copy(retour.subSequence(0, retour.length()-1).toString());

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
		return "capitaliser";
	}

}
