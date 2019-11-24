package org.linotte.web.serveur;

/***********************************************************************
 * Linotte                                                             *
 * Version release date : August 01, 2014                              *
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

import org.linotte.implementations.LibrairieVirtuelleSyntaxeV2;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.xml.Version;
import org.linotte.web.Run;

/**
 * Cette classe démarre le serveur HTTP
 * @author CPC
 *
 */
public class Webonotte {

	private static final String NOM_SERVEUR = "Webonotte";
	private static final String TYPE_SERVEUR = "jetty";

	public static void main(String... strings) {

		try {
			int port_webonotte = 7777;
			if (0 != Preference.getIntance().getInt(Preference.P_WEBONOTTE_PORT)) {
				port_webonotte = Preference.getIntance().getInt(Preference.P_WEBONOTTE_PORT);
			}
			String root = Preference.getIntance().getWebRoot();
			if (null != Preference.getIntance().getProperty(Preference.P_WEBONOTTE_DIR)) {
				root = Preference.getIntance().getProperty(Preference.P_WEBONOTTE_DIR);
			}
			Run.runStandEmbededServer(TYPE_SERVEUR, NOM_SERVEUR, new LibrairieVirtuelleSyntaxeV2(), port_webonotte, new File(root));
			System.out.println("Le web est à vous sur le port : " + port_webonotte);
			JettyHttpServer.DEBUG = true;
		} catch (Throwable e) {
			if (Version.isBeta())
				e.printStackTrace();
			System.out.println("Le serveur ne peut démarrer : " + e.toString());
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}

	}

}