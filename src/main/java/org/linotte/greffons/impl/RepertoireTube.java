package org.linotte.greffons.impl;

import java.util.HashMap;
import java.util.Map;

import org.linotte.greffons.externe.Tube;

public class RepertoireTube extends Tube {

	Map<String, ObjetLinotte> registre = new HashMap<String, ObjetLinotte>();

	private static final String ERREUR = "Ce verbe ne s'utilise pas avec un 'répertoire' !";

	@Override
	public boolean charger(ObjetLinotte valeurs) throws GreffonException {
		throw new GreffonException(ERREUR);
	}

	@Override
	public boolean charger(String paramètres, ObjetLinotte valeurs) throws GreffonException {
		return registre.put(paramètres, valeurs) == null;
	}

	@Override
	public boolean configurer(String paramètres) throws GreffonException {
		throw new GreffonException(ERREUR);
	}

	@Override
	public ObjetLinotte decharger(ObjetLinotte structure) throws GreffonException {
		throw new GreffonException(ERREUR);
	}

	@Override
	public ObjetLinotte decharger(String paramètres, ObjetLinotte structure) throws GreffonException {
		return registre.get(paramètres);
	}

	@Override
	public boolean fermer() throws GreffonException {
		throw new GreffonException(ERREUR);
	}

	@Override
	public boolean ouvrir(String état, String paramètres) throws GreffonException {
		throw new GreffonException(ERREUR);
	}

}
