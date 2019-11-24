package org.linotte.greffons.impl;

import java.util.List;

import org.linotte.greffons.GreffonsChargeur;
import org.linotte.greffons.externe.Greffon;

public class SQL extends Greffon {

	private ConnecteurSQL connecteurSQL;

	@Slot()
	public boolean open(String jdbcDrider, String url, String user, String password) throws GreffonException {
		try {
			connecteurSQL = new ConnecteurSQL(jdbcDrider, url, user, password);
		} catch (java.lang.ClassNotFoundException c) {
			c.printStackTrace();
			throw new GreffonException("Le pilote n'est pas disponible, " + c.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			throw new GreffonException("Impossible de se connecter à la base de données : " + e.getMessage());
		}
		return true;
	}

	@Slot()
	public boolean select(String requete) throws GreffonException {
		if (connecteurSQL != null) {
			try {
				return connecteurSQL.charger(requete);
			} catch (Exception e) {
				e.printStackTrace();
				throw new GreffonException("Impossible d'exécuter la requête : " + e.getMessage());
			}
		} else {
			throw new GreffonException("Il faut déjà ouvrir une connexion vers une base de données");
		}
	}

	@Slot()
	/**
	 * Bogue de l'Atelier Linotte, on ne peut pas utiliser le paramètre typé : String []
	 */
	public boolean execute(String requete, Casier valeurs) throws GreffonException {
		if (connecteurSQL != null) {
			try {
				String[] parametres = new String[valeurs.size()];
				for (int i = 0; i < valeurs.size(); i++) {
					if (((Acteur) valeurs.get(i)).getRole() == ROLE.TEXTE) {
						parametres[i] = (String) ((Acteur) valeurs.get(i)).getValeur();
					} else {
						parametres[i] = String.valueOf(((Acteur) valeurs.get(i)).getValeur());
					}
				}
				return connecteurSQL.ajouter(requete, parametres);
			} catch (Exception e) {
				e.printStackTrace();
				throw new GreffonException("Impossible d'exécuter la requête : " + e.getMessage());
			}
		} else {
			throw new GreffonException("Il faut déjà ouvrir une connexion vers une base de données");
		}
	}

	@Slot()
	public List<String> next() throws GreffonException {
		if (connecteurSQL != null) {
			try {
				return connecteurSQL.next();
			} catch (Exception e) {
				e.printStackTrace();
				throw new GreffonException("Impossible d'exécuter la requête : " + e.getMessage());
			}
		} else {
			throw new GreffonException("Il faut déjà ouvrir une connexion vers une base de données");
		}
	}

	@Slot()
	public boolean hasnext() throws GreffonException {
		if (connecteurSQL != null) {
			try {
				return connecteurSQL.hasnext();
			} catch (Exception e) {
				e.printStackTrace();
				throw new GreffonException("Impossible d'exécuter la requête : " + e.getMessage());
			}
		} else {
			throw new GreffonException("Il faut déjà ouvrir une connexion vers une base de données");
		}
	}

	@Slot()
	public boolean close() throws GreffonException {
		if (connecteurSQL != null) {
			try {
				return connecteurSQL.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new GreffonException("Impossible d'exécuter la requête : " + e.getMessage());
			}
		} else {
			throw new GreffonException("Il faut déjà ouvrir une connexion vers une base de données");
		}
	}

	@Slot()
	public boolean pilote(String chemin) {
		String cheminValide = getRessourceManager().analyserChemin(chemin);
		GreffonsChargeur.getInstance().ajouterJar(cheminValide);
		return true;
	}
}
