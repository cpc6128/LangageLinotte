package console;

/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 28, 2011                                *
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

import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

public class Xinotte {

	public static void main(String... in) {

		try {

			// Lecture du fichier parametres.conf
			Properties properties = new Properties();
			properties.load(new FileReader(new File("lanceur.conf")));
			String livre = properties.getProperty("livre");
			if (livre == null)
				throw new Exception("merci d'indiquer le livre !");
			String parametres = properties.getProperty("parametres");

			String splash = properties.getProperty("splash");
			if (splash != null) {
				String sdelai = properties.getProperty("delai", "3");
				int d = Integer.parseInt(sdelai) * 1000;
				new Thread(new XSplash(splash, d)).start();
			}

			if (parametres == null)
				parametres = "";
			List<String> p = new ArrayList<String>();
			p.addAll(Arrays.asList(parametres.split(" ")));
			p.add(livre);
			List<String> p2 = new ArrayList<String>();
			// On copie les éléments non vides :
			for (String s : p) {
				if (s.trim().length() > 0)
					p2.add(s);
			}
			System.out.println("Ligne de commande : " + p2);
			Jinotte.main(p2.toArray(new String[p2.size()]));
		} catch (Throwable e) {
			System.err.println("Impossible de lire le livre");
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Impossible de lire le livre :\n" + stackTraceToString(e), "Erreur", JOptionPane.ERROR_MESSAGE);

		}
	}

	/**
	 *  converts an exception's stack trace to a string
	 */
	private static String stackTraceToString(Throwable e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}

}
