package org.linotte.frame.cahier.timbre.entite;

import java.util.ArrayList;
import java.util.List;

import org.linotte.frame.cahier.timbre.entite.i.Cloneur;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;

public class PorteurFonction implements Deposable {

	public Fonction fonction;

	public List<Deposable> parametres;

	@Override
	public Cloneur clonerPourDuplication() {
		PorteurFonction clone = new PorteurFonction();
		clone.fonction = fonction;
		clone.parametres = new ArrayList<>();
		clone.parametres.addAll(parametres);
		return clone;
	}

	@Override
	public Cloneur clonerPourCreation() {
		return null;
	}

	/**
	 * On supprime les paramètres en plus
	 * @return 
	 */
	public boolean verifierFonction() {
		if (fonction.parametres.size() < parametres.size()) {
			parametres.subList(0, parametres.size() - 1);
			return false;
		}
		return true;
	}

}
