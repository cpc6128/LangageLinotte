package org.linotte.greffons.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Properties;

import org.linotte.greffons.externe.Greffon;
import org.linotte.moteur.outils.Preference;

public class Configuration extends Greffon {

	private static final String DOMAINE = "domaine";
	private Properties configuration = null;
	private File file = null;

	@Slot(nom = "récupérer")
	public String recuperer(String propriete) throws GreffonException {
		charger();
		return configuration.getProperty(propriete);
	}

	@Slot(nom = "stocker")
	public boolean stocker(String propriete, String valeur) throws GreffonException {
		charger();
		configuration.setProperty(propriete, valeur);
		sauvegarder();
		return true;
	}

	@Slot(nom = "vérifier")
	public boolean vérifier(String propriete) throws GreffonException {
		charger();
		return configuration.containsKey(propriete);
	}

	private void sauvegarder() throws GreffonException {
		try {
			FileWriter fw = new FileWriter(file);
			configuration.store(fw, "Linotte Configuration Système");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GreffonException("impossible de créer le fichier de configuration : " + e.getMessage());
		}

	}

	private synchronized void charger() throws GreffonException {
		if (configuration == null) {
			try {
				String nom_fichier = getAttributeAsString(DOMAINE);
				if (nom_fichier == null || nom_fichier.trim().length() == 0) {
					throw new GreffonException("le domaine n'est pas indiqué");
				}
				Properties configuration = new Properties();
				file = new File(new File(System.getProperty("user.home"), Preference.REPERTOIRE), "_" + nom_fichier + ".cfg");
				if (file.exists()) {
					FileReader fr = new FileReader(file);
					configuration.load(fr);
					fr.close();
				}
				this.configuration = configuration;
			} catch (Exception e) {
				e.printStackTrace();
				throw new GreffonException("impossible de charger  le fichier de configuration : " + e.getMessage());
			}
		}
	}

}
