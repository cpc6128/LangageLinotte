package org.linotte.web;

import org.alize.http.i.Serveur;
import org.linotte.moteur.xml.Version;
import org.linotte.web.serveur.JettyHttpServer;

final class ServerFactory {

	public static Serveur createServer(String type, String name) {
		if (nouvelleInstance("org.eclipse.jetty.server.Server") != null) {
			return JettyHttpServer.getServeur(name);
		} else {
			if (Version.isBeta())
				System.out.println("Librairies du serveur JETTY introuvable !");
			return null;
		}
	}

	public static Object nouvelleInstance(String classe) {
		Object o = null;
		try {
			o = Class.forName(classe).newInstance();
		} catch (Exception e) {
		} catch (NoClassDefFoundError e) {
		}
		return o;
	}

}