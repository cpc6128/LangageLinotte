package org.linotte.frame.favoris;

/***********************************************************************
 * Langage Linotte                                                     *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 3 or       *
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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.linotte.frame.cahier.Cahier;
import org.linotte.frame.cahier.Onglets;
import org.linotte.frame.projet.ExplorateurProjet;
import org.linotte.moteur.outils.Preference;

/**
 * Cette classe gère les favoris en mémoire.
 *
 */
@SuppressWarnings("unchecked")
public class GestionnaireFavoris {

	private Onglets onglets;

	private static GestionnaireFavoris gestionnaireFavoris;

	private static List<Favoris> image_favoris = new ArrayList<Favoris>();

	private static File configuration;

	private static final String FICHIER = "favoris.cfg";

	static {
		String final_home = Preference.getIntance().getHome();
		configuration = new File(final_home + File.separator + Preference.REPERTOIRE + File.separator + FICHIER);
		if (configuration.exists()) {
			//XMLEncoder encoder = new XMLEncoder(out)
			try {
				XMLDecoder decoder = new XMLDecoder(new FileInputStream(configuration));
				image_favoris = (List<Favoris>) decoder.readObject();
				decoder.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	private GestionnaireFavoris(Onglets ponglets) {
		onglets = ponglets;
		gestionnaireFavoris = this;
	}

	public static synchronized GestionnaireFavoris getInstance(Onglets onglets) {
		if (gestionnaireFavoris == null) {
			gestionnaireFavoris = new GestionnaireFavoris(onglets);
		}
		return gestionnaireFavoris;
	}

	public static GestionnaireFavoris getInstance() {
		return gestionnaireFavoris;
	}

	public void afficherTousLesFavoris() {
		for (Favoris favoris : image_favoris) {
			Cahier cahier = onglets.retourneCahier(new File(favoris.getFichierCible()));
			if (cahier != null)
				cahier.favoris.add(favoris);
			ExplorateurProjet.favorisTreePanel.ajouterParagraphe(favoris);
		}
	}

	public void classer(Favoris favoris) {
		Cahier cahier = onglets.retourneCahier(new File(favoris.getFichierCible()));
		if (cahier != null)
			cahier.favoris.add(favoris);
		image_favoris.add(favoris);
		ExplorateurProjet.favorisTreePanel.ajouterParagraphe(favoris);
		ExplorateurProjet.favorisTreePanel.revalidate();
	}

	public void peuplerCahier(Cahier cahier) {
		File file = cahier.getFichier();
		for (Favoris favoris : image_favoris) {
			File cible = new File(favoris.getFichierCible());
			if (cible.equals(file)) {
				cahier.favoris.add(favoris);
			}
		}
	}

	public void supprimer(Favoris favoris) {
		Cahier cahier = onglets.retourneCahier(new File(favoris.getFichierCible()));
		if (cahier != null)
			cahier.favoris.remove(favoris);
		image_favoris.remove(favoris);
		ExplorateurProjet.favorisTreePanel.supprimer(favoris);
	}

	public void enregistrerFavoris() {
		try {
			XMLEncoder encoder = new XMLEncoder(new FileOutputStream(configuration));
			encoder.writeObject(image_favoris);
			encoder.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
