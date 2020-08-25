package org.linotte.web;

import org.alize.http.i.Executeur;
import org.alize.http.i.Serveur;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.api.Librairie;
import org.linotte.web.executeur.ExecuteurLivre;

import java.io.File;

/**
 *
 * Serveur HTTP Webonotte
 *
 */
public final class Run {

	public static int PORT;
	public static File ROOT;

	/**
	 * 
	 * @param type
	 * @param name
	 * @param port
	 * @param root
	 * @throws Exception
	 */
	public static void runStandAloneServer(String type, String name, int port, File root) throws Exception {
		Executeur executeur = new ExecuteurLivre();
		name = name + " " + Version.getVersion();
		Serveur serveur = ServerFactory.createServer(type, name);
		serveur.start(port, root, executeur);
		setValues(port, root);
	}

	private static void setValues(int port, File root) {
		PORT = port;
		ROOT = root;
		root.mkdirs();
	}

	/**
	 * 
	 * @param type
	 * @param name
	 * @param linotte
	 * @param port
	 * @param root
	 * @throws Exception
	 */
	public static void runStandEmbededServer(final String type, final String name, final Librairie<?> lib, final int port, final File root) {
		Thread t = new Thread() {
			public void run() {
				try {
					Executeur executeur = new ExecuteurLivre(lib);
					Serveur serveur = ServerFactory.createServer(type, name + " " + Version.getVersion());
					if(serveur != null){
						serveur.start(port, root, executeur);
						setValues(port, root);
					}
				} catch (NoClassDefFoundError | Exception e) {
					e.printStackTrace();
				}
			}
		};
		t.start();
	}

}