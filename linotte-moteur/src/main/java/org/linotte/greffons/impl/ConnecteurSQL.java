package org.linotte.greffons.impl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ronan Mounes
 *
 */
public class ConnecteurSQL {

	private String jdbcDrider, url, user, password;
	private Connection connexion = null;
	private ResultSet resultSet;
	private Statement statement;
	private boolean next;

	public ConnecteurSQL(String jdbcDrider, String url, String user, String password) throws Exception {
		this.jdbcDrider = jdbcDrider;
		this.url = url;
		this.user = user;
		this.password = password;
		chargerDriver();
		preparerConnexion();
		resultSet = null;
		statement = null;
		next = false;
	}

	private void chargerDriver() throws Exception {
		try {
			Class.forName(jdbcDrider).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private synchronized Connection preparerConnexion() throws SQLException {
		if (connexion == null)
			connexion = DriverManager.getConnection(url, user, password);
		return connexion;
	}

	public boolean charger(String query) throws Exception {
		statement = connexion.createStatement();
		resultSet = statement.executeQuery(query);
		next = resultSet.next();
		return next;
	}

	public boolean hasnext() throws Exception {
		return next;
	}

	public List<String> next() throws Exception {
		int length = resultSet.getMetaData().getColumnCount();
		List<String> retour = new ArrayList<String>();
		if (next) {
			for (int i = 0; i < length; i++) {
				retour.add(resultSet.getString(i + 1));
			}
		} else {
			new Exception("Plus de données à lire");
		}
		next = resultSet.next();
		return retour;
	}

	public boolean close() throws Exception {
		if (statement != null)
			statement.close();
		else {
			statement = connexion.createStatement();
			statement.close();
		}
		return true;
	}

	public boolean ajouter(String query, String[] valeurs) throws Exception {
		// Attributs
		PreparedStatement ps = connexion.prepareStatement(query.toString());
		// Valeurs
		for (int i = 1; i <= valeurs.length; i++) {
			ps.setString(i, valeurs[i - 1]);
		}

		ps.executeUpdate();
		ps.close();
		return true;
	}

}
