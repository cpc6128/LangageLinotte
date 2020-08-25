package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Tube;

import java.util.EmptyStackException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Queue extends Tube {

	private java.util.Queue<ObjetLinotte> pile = new ConcurrentLinkedQueue<ObjetLinotte>();

	private static final String ERREUR = "Ce verbe ne s'utilise pas avec une 'pile' !";

	@Slot
	public int taille() throws GreffonException {
		return pile.size();
	}

	@Override
	public boolean charger(ObjetLinotte valeurs) throws GreffonException {
		return pile.offer(valeurs);
	}

	@Override
	public boolean charger(String paramètres, ObjetLinotte valeurs) throws GreffonException {
		throw new GreffonException(ERREUR);
	}

	@Override
	public boolean configurer(String paramètres) throws GreffonException {
		throw new GreffonException(ERREUR);
	}

	@Override
	public ObjetLinotte decharger(ObjetLinotte structure) throws GreffonException {
		try {
			return pile.poll();
		} catch (EmptyStackException e) {
			return null;
		}
	}

	@Override
	public ObjetLinotte decharger(String paramètres, ObjetLinotte structure) throws GreffonException {
		throw new GreffonException(ERREUR);
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
