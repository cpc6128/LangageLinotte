package org.linotte.greffons.impl;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.outils.ObjetLinotteFactory;

public class TCPServeur extends Greffon {

	private ServerSocket serveur;
	private Map<Integer, Socket> clients;
	private int id = 0;
	private boolean debug = true;

	@Slot(nom = "démarrer")
	public boolean demarrer(int port) throws GreffonException {
		try {
			if (serveur != null)
				throw new GreffonException("Le serveur est déjà démarré");
			serveur = new ZipServerSocket(port);
			clients = Collections.synchronizedMap(new HashMap<Integer, Socket>());
			return true;
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			throw new GreffonException("Impossible de démarrer le serveur : " + e.getMessage());
		}
	}

	@Slot(nom = "arrêter")
	public boolean arreter() throws GreffonException {
		verifierServeur();
		try {
			synchronized (clients) {
				for (Socket socket : clients.values()) {
					try {
						socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				clients.clear();
			}
			serveur.close();
			serveur = null;
			return true;
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			throw new GreffonException("Impossible d'arrêter le serveur : " + e.getMessage());
		}
	}

	@Slot(nom = "attendreconnexion")
	public Integer attendreclient() throws GreffonException {
		verifierServeur();
		try {
			Socket s = serveur.accept();
			int numero = nouveaunumero();
			clients.put(numero, s);
			return numero;
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			throw new GreffonException("Impossible d'attendre une nouvelle connexion : " + e.getMessage());
		}
	}

	@Slot(nom = "envoyer")
	public boolean envoyer(Integer numero, ObjetLinotte acteur) throws GreffonException {
		verifierServeur();
		try {
			ZipSocket socket = (ZipSocket) clients.get(numero);
			verifierSocket(socket);
			OutputStream os = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			
			ObjetLinotte aEnvoyer;
			if (acteur instanceof Espece) {
				// Il ne faut pas envoyer le CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT :
				Espece e = (Espece) acteur;
				aEnvoyer = e.clone();
				//Espece aEnvoyer
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
			
			oos.writeObject(aEnvoyer);
			os.flush();
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			throw new GreffonException("Impossible d'envoyer l'acteur : " + e.getMessage());
		}
		return false;
	}

	private void verifierSocket(Socket s) throws GreffonException {
		if (s == null)
			throw new GreffonException("Le socket n'existe pas");
	}

	private void verifierServeur() throws GreffonException {
		if (serveur == null)
			throw new GreffonException("Le serveur n'est pas démarré");
	}

	@Slot(nom = "recevoir")
	public ObjetLinotte recevoir(Integer numero) throws GreffonException {
		try {
			Socket s = clients.get(numero);
			verifierSocket(s);
			ObjectInputStream oos = new ObjectInputStream(s.getInputStream());
			ObjetLinotte objetLinotte = (ObjetLinotte) oos.readObject();
			return objetLinotte;
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			throw new GreffonException("Impossible de recevoir l'acteur : " + e.getMessage());
		}
	}

	@Slot(nom = "fermer")
	public boolean fermer(Integer numero) throws GreffonException {
		try {
			Socket s = clients.get(numero);
			clients.remove(numero);
			verifierSocket(s);
			s.close();
			return true;
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			throw new GreffonException("Impossible de fermer : " + e.getMessage());
		}
	}

	@Slot(nom = "propager")
	public boolean propager(ObjetLinotte acteur) throws GreffonException {
		boolean retour = true;
		try {
			for (int socket : clients.keySet()) {
				try {
					envoyer(socket, acteur);
				} catch (Exception e) {
					retour = false;
					if (debug)
						e.printStackTrace();
				}
			}
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
		}
		return retour;
	}

	private synchronized int nouveaunumero() {
		id++;
		return id;
	}

	@Slot
	public String ip() throws GreffonException {
		try {
			return InetAddress.getLocalHost().getHostAddress();
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
