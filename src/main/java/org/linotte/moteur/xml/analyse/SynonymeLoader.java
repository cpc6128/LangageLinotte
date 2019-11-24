package org.linotte.moteur.xml.analyse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;

/**
 * charge le fichier de synonymes
 *
 */
public class SynonymeLoader {

	private static String FICHIER = "synonymes.def";

	private static String TITRE = "titre=";

	public static boolean charger = false;

	public static String chargerSynonymes() {
		String titre = null;
		File pere = Ressources.getdirectory("/");
		if (pere != null) {
			File fichier = new File(pere, FICHIER);
			if (!fichier.exists())
				fichier = new File(Preference.getIntance().getHome() + File.separator + Preference.REPERTOIRE + File.separator + FICHIER);
			if (!fichier.exists())
				return null;
			InputStream stream = null;
			try {
				stream = new FileInputStream(fichier);
			} catch (FileNotFoundException e) {
			}
			if (stream != null) {
				charger = true;
				Scanner scanner = new Scanner(stream);
				while (scanner.hasNext()) {
					String ligne = scanner.nextLine();
					if (ligne.startsWith(TITRE)) {
						titre = ligne.substring(TITRE.length());
					} else {
						extraireSynonymes(ligne);
					}
				}
				scanner.close();
			}
		}
		return titre;
	}

	private static void extraireSynonymes(String ligne) {
		Scanner scanner = new Scanner(ligne);
		List<Chaine> l = new ArrayList<Chaine>();
		while (scanner.hasNext()) {
			l.add(Chaine.produire(scanner.next()));
		}
		Synonyme.addSynonyme(l);
		scanner.close();
	}

}
