package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.outils.ObjetLinotteFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;

public class TCPClient extends Greffon {

	private ZipSocket socket;
	private boolean debug = true;

	@Slot
	public boolean connexion(String url, int port) throws GreffonException {
		try {
			if (socket != null) {
				try {
					socket.close();
				} catch (Exception e) {
					if (debug)
						e.printStackTrace();
				}
			}
			//throw new GreffonException("Le socket est déjà ouvert");
			socket = new ZipSocket(url, port);
			return true;
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			throw new GreffonException("Impossible de se connecter au serveur : " + e.getMessage());
		}
	}

	@Slot
	public boolean fermer() throws GreffonException {
		verifierSocket();
		try {
			socket.close();
			socket = null;
			return true;
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			throw new GreffonException("Impossible de fermer la connexion : " + e.getMessage());
		}
	}

	private void verifierSocket() throws GreffonException {
		if (socket == null)
			throw new GreffonException("Le socket n'est pas ouverte");
	}

	@Slot
	public boolean envoyer(ObjetLinotte acteur) throws GreffonException {
		verifierSocket();
		ObjetLinotte aEnvoyer;
		try {
			if (acteur instanceof Espece) {
				// Il ne faut pas envoyer le CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT :
				Espece e = (Espece) acteur;
				aEnvoyer = e.clone();
				Acteur a_sup = null;
				for (Acteur a : (Espece)aEnvoyer) {
					if (a.getClef().equalsIgnoreCase(ObjetLinotteFactory.CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT)) {
						a_sup = a;
					}
				}
				if ( a_sup !=null) 
					((Espece)aEnvoyer).remove(a_sup);
			} else {
				aEnvoyer = acteur;
			}
			OutputStream os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(aEnvoyer);
			os.flush();
			return true;
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			throw new GreffonException("Impossible d'envoyer l'acteur : " + e.getMessage());
		}
	}

	@Slot
	public ObjetLinotte recevoir() throws GreffonException {
		verifierSocket();
		try {
			ObjectInputStream oos = new ObjectInputStream(socket.getInputStream());
			ObjetLinotte objetLinotte = (ObjetLinotte) oos.readObject();
			return objetLinotte;
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			throw new GreffonException("Impossible de recevoir l'acteur : " + e.getMessage());
		}
	}

	@Slot
	public String nommachine() throws GreffonException {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			return "localhost";
		}
	}

	@Slot(nom = "débogage")
	public boolean debogage(boolean debog) throws GreffonException {
		debug = debog;
		return false;
	}
}
