/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 29, 2008                           *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                           *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.outils;

import org.linotte.moteur.xml.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

@SuppressWarnings("serial")
public class Preference extends Properties {

	private static Preference preferences;
	private File conf;

	private boolean existe;
	public static final String P_X = "ATELIER_X";
	public static final String P_Y = "ATELIER_Y";
	public static final String P_LARGEUR = "LARGEUR";
	public static final String P_HAUTEUR = "HAUTEUR";
	public static final String P_TOILE = "TOILE";
	public static final String P_TOILE_X = "TOILE_X";
	public static final String P_TOILE_Y = "TOILE_Y";
	public static final String P_NO_SPLASH = "NO_SPLASH";
	public static final String P_FICHIER = "FICHIER";
	public static final String P_DIRECTORY = "DIRECTORY";
	public static final String P_MODE_VERBE = "MODE_VERBE";
	public static final String P_VERSION = "VERSION";
	public static final String P_VITESSE = "RAFRAICHISSEMENT";
	public static final String P_VITESSE_TOUCHE = "VITESSE_TOUCHE";
	public static final String P_FONT = "FONT";
	public static final String P_TAILLE_FONT = "TAILLE_FONT";
	public static final String P_CHRONO = "CHRONO";
	public static final String ENCODAGE = "ENCODAGE";
	public static final String P_WINDOW_MAX = "WINDOW_MAX";
	public static final String P_WARNING = "WARNING";
	public static final String P_MODE_SAVE_WORKSPACE = "SAVE_WORKSPACE";
	public static final String P_BULLE_AIDE_INACTIF = "BULLE_AIDE_INACTIF";
	public static final String P_MODE_BONIFIEUR = "BONIFIEUR";
	public static final String P_HISTORIQUE = "HISTORIQUE";
	public static final String P_MODE_FURTIF = "MODE_FURTIF";
	public static final String P_PAS_A_PAS = "PAS_A_PAS";
	public static final String P_LANGAGE = "LANGAGE";
	public static final String P_GANDALF = "GANDALF";
	public static final String P_TOUCHE_ENTREE = "TOUCHE_ENTREE";

	// Webonotte
	public static final String P_WEBONOTTE_PORT = "WEBONOTTE_PORT";
	public static final String P_WEBONOTTE_DIR = "WEBONOTTE_DIR";
	public static final String P_WEBONOTTE_FOOTER = "WEBONOTTE_FOOTER_HIDDEN";

	private static final String FICHIER = "atelier.cfg";

	public static final String REPERTOIRE = ".linotte";

	public static boolean CAN_WRITE = false;

	private Preference() {
		try {
			String final_home = getHome();
			conf = new File(final_home + File.separator + REPERTOIRE + File.separator + FICHIER);
			preferences = this;
			loadConf();
		} catch (java.security.AccessControlException e) {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getHome() {
		String final_home = ".";
		try {
			final_home = System.getProperty("user.home");
		} catch (Exception e) {
		}
		return final_home;
	}

	public String getWebRoot() {
		return getHome() + "/web";
	}

	public static synchronized Preference getIntance() {
		if (preferences == null) {
			preferences = new Preference();
		}
		return preferences;
	}

	private void loadConf() {
		existe = conf.canRead();
		if (conf.canRead())
			try {
				FileInputStream fileinputstream = new FileInputStream(conf);
				load(fileinputstream);
				fileinputstream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	public int getInt(String key) {
		String s = getProperty(key);
		if (s == null) {
			s = "0";
		}
		return new Integer(s);
	}

	/**
	 * 
	 * @param key
	 */
	public void nettoyer(String key) {
		key = key.toLowerCase();
		Set<String> keys = stringPropertyNames();
		for (String k : keys) {
			if (k.toLowerCase().startsWith(key)) {
				remove(k);
			}
		}
	}

	/**
	 * Retourne toutes les clefs commençant par 'key'
	 * @param key
	 * @return
	 */
	public List<String> getKeyFilter(String key) {
		List<String> retour = new ArrayList<String>();
		key = key.toLowerCase();
		Set<String> keys = stringPropertyNames();
		for (String k : keys) {
			if (k.toLowerCase().startsWith(key)) {
				retour.add(k);
			}
		}
		return retour;
	}

	public void setInt(String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	public boolean getBoolean(String key) {
		String s = getProperty(key);
		if (s == null) {
			s = Boolean.FALSE.toString();
		}
		return new Boolean(s);
	}

	public void setBoolean(String key, boolean value) {
		setProperty(key, String.valueOf(value));
	}

	public void save() {
		try {
			if (CAN_WRITE) {
				(new File(getHome() + File.separator + REPERTOIRE)).mkdirs();
				setProperty(P_VERSION, Version.getVersion());
				this.store(new FileOutputStream(conf), "!! Ne pas modifier ce fichier quand l'Atelier Linotte est lancé !!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Est-ce que le fichier de properties existe-t-il ?
	 * @return
	 */
	public boolean isExiste() {
		return existe;
	}

}
