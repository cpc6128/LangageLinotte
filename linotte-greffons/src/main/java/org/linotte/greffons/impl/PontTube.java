package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Tube;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class PontTube extends Tube {

	private ServerSocket service;
	private Socket connexion;
	private InputStream entree;
	private OutputStream sortie;

	public static final int PORT = 8777;

	private static final String ERREUR = "Ce verbe ne s'utilise pas avec un 'pont' !";

	private static final String ERREUR_CONNEXION = "La connexion n'est pas ouverte !";

	@Override
	public boolean charger(ObjetLinotte valeurs) throws GreffonException {
		try {
			if (sortie != null) {
				ObjectOutputStream oos = new ObjectOutputStream(sortie);
				oos.writeObject(valeurs);
			} else {
				throw new GreffonException(ERREUR_CONNEXION);
			}
		} catch (IOException e) {
			setValeur("état", new BigDecimal(-4));
			e.printStackTrace();
		} catch (Exception e) {
			setValeur("état", new BigDecimal(-9));
			e.printStackTrace();
		}
		return true;
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
			if (entree != null) {
				ObjectInputStream oos = new ObjectInputStream(entree);
				return (ObjetLinotte) oos.readObject();
			} else {
				throw new GreffonException(ERREUR_CONNEXION);
			}
		} catch (IOException e) {
			setValeur("état", new BigDecimal(-5));
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			setValeur("état", new BigDecimal(-6));
			e.printStackTrace();
		} catch (Exception e) {
			setValeur("état", new BigDecimal(-7));
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ObjetLinotte decharger(String paramètres, ObjetLinotte structure) throws GreffonException {
		throw new GreffonException(ERREUR);
	}

	@Override
	public boolean fermer() throws GreffonException {
		try {
			if (entree != null)
				entree.close();
			if (sortie != null)
				sortie.close();
			if (connexion != null)
				connexion.close();
			if (service != null)
				service.close();

		} catch (IOException e) {
			e.printStackTrace();
			setValeur("état", new BigDecimal(-3));
		} catch (Exception e) {
			e.printStackTrace();
			setValeur("état", new BigDecimal(-10));
		}
		return false;
	}

	@Override
	public boolean ouvrir(String état, String paramètres) throws GreffonException {
		if (((Acteur) getAttribute("adresse")).getValeur() == null || (((String) ((Acteur) getAttribute("adresse")).getValeur())).trim().length() == 0) {
			System.out.println("Mode serveur...");
			System.out.println("Port : " + getAttribute("port"));
			BigDecimal port = ((BigDecimal) ((Acteur) getAttribute("port")).getValeur());
			try {
				service = new ServerSocket(port == null ? PORT : port.intValue());
				connexion = service.accept();
				entree = connexion.getInputStream();
				sortie = connexion.getOutputStream();
				setValeur("état", new BigDecimal(1));
			} catch (Exception e) {
				e.printStackTrace();
				setValeur("état", new BigDecimal(-1));
			}
		} else {
			System.out.println("Mode client...");
			System.out.println("Adresse : " + getAttribute("adresse"));
			System.out.println("Port : " + getAttribute("port"));
			BigDecimal port = ((BigDecimal) ((Acteur) getAttribute("port")).getValeur());
			try {
				connexion = new Socket((String) ((Acteur) getAttribute("adresse")).getValeur(), port == null ? PORT : port.intValue());
				entree = connexion.getInputStream();
				sortie = connexion.getOutputStream();
				setValeur("état", new BigDecimal(1));
			} catch (UnknownHostException e) {
				setValeur("état", new BigDecimal(-3));
				e.printStackTrace();
			} catch (Exception e) {
				setValeur("état", new BigDecimal(-1));
				e.printStackTrace();
			}
		}
		return false;
	}

}
