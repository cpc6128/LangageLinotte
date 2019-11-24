package org.linotte.web.serveur;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alize.http.i.Executeur;
import org.alize.http.i.FichierTraitement;
import org.alize.http.i.Serveur;
import org.linotte.moteur.outils.FichierOutils;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;
import org.linotte.moteur.xml.analyse.multilangage.Langage;
import org.linotte.web.Run;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

public class JettyHttpServer implements Serveur {

	// Pour le deboggage seulement :
	public static boolean DEBUG = false;

	private String version;

	private static JettyHttpServer _this;

	private JettyHttpServer(String pversion) {
		version = pversion;
	}

	public static synchronized JettyHttpServer getServeur(String pversion) {
		return (_this == null) ? _this = new JettyHttpServer(pversion) : _this;
	}

	@Override
	public void start(int port, File root, Executeur executeur) throws Exception {
		Server server = new Server(port);

		// Définition des handlers :
		// Pour les images et autres fichiers statiques :
		ResourceHandler resourcehandler = new ResourceHandler();
		resourcehandler.setResourceBase(root.getAbsolutePath());
		// Pour la lecture des livres :
		Handler livreHandler = new Handler(executeur, root);
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new org.eclipse.jetty.server.Handler[] { livreHandler, resourcehandler });
		server.setHandler(handlers);

		server.start();

	}

	@Override
	public void stop() throws IOException {
	}

	private class Handler extends AbstractHandler {

		private static final String CHEMIN_NOTICE = "/notice";

		private static final String CHEMIN_ESSAYER = "/essayer";

		private static final String B_TUTORIEL = "/b_tutoriel";

		private Executeur executeur;

		private File root;

		public Handler(Executeur pexecuteur, File proot) {
			executeur = pexecuteur;
			root = proot;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void handle(String target, Request requestRaw, HttpServletRequest request, HttpServletResponse res)
				throws IOException, ServletException {

			boolean essayer = false;

			// Livre :
			String path = request.getRequestURI();
			if (DEBUG) {
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				//is client behind something?
				String ipAddress = request.getHeader("X-FORWARDED-FOR");
				if (ipAddress == null) {
					ipAddress = request.getRemoteAddr();
				}
				System.out.println(dateFormat.format(cal.getTime()) + ", path = " + path + ", query = " + request.getQueryString() + ", ip = " + ipAddress);
			}
			if ("/".equals(path)) {
				path = CHEMIN_NOTICE;
			}

			if (executeur.accepter(path)) {

				long t0 = System.currentTimeMillis();
				StringBuilder response = new StringBuilder();
				int code = HttpServletResponse.SC_OK;

				try {

					FichierTraitement livre;
					if ("/favicon.ico".equals(path)) {
						BufferedInputStream input = null;
						BufferedOutputStream output = null;
						try {
							input = new BufferedInputStream(Ressources.getFromRessources("linotte_new.ico"));
							output = new BufferedOutputStream(res.getOutputStream());
							byte[] buffer = new byte[8192];
							for (int length = 0; (length = input.read(buffer)) > 0;) {
								output.write(buffer, 0, length);
							}
						} finally {
							if (output != null)
								try {
									output.close();
								} catch (IOException i) {
								}
							if (input != null)
								try {
									input.close();
								} catch (IOException i) {
								}
						}
						res.setContentType("image/x-icon");
						res.setStatus(200);
						((Request) request).setHandled(true);
						return;
					} else if (CHEMIN_NOTICE.equals(path)) {
						// Filtre Notice :
						//TODO A mettre dans un cache
						livre = new FichierVirtuel(FichierOutils.lire(Ressources.getFromRessources("notice.liv")), root);
					} else if (CHEMIN_ESSAYER.equals(path)) {
						// Pour la fonctionnalité ESSAYER LINOTTE
						String contenu = request.getParameter("livre");
						System.out.println("Livre : " + contenu);
						livre = new FichierVirtuel(contenu, root);
						essayer = true;
					} else if (path.startsWith(B_TUTORIEL)) {
						// Filtre turoriel :
						livre = new FichierDisque(new File(Ressources.getExemples(Langage.Linotte2), path.substring(0)));
					} else {
						livre = new FichierDisque(new File(root, path));
					}

					Map<String, String> acteurs = new HashMap<String, String>();
					Enumeration<String> e = request.getParameterNames();
					while (e.hasMoreElements()) {
						String s = e.nextElement();
						acteurs.put(s, request.getParameter(s));
					}

					// Choix du langage :
					String langage = request.getParameter("langage");
					
					// Ajout des parametres du serveur :
					acteurs.put("wbt_repertoire", root.getCanonicalPath());
					acteurs.put("wbt_port", Integer.toString(Run.PORT));

					// Exécution de la requête

					Collection<Habilitation> habilitations = new ArrayList<>();
					if (essayer) {
						habilitations.add(Habilitation.LITTLE_BIRD);
					}
					response.append(executeur.traiter(livre, acteurs, habilitations, langage));

				} catch (Exception e) {
					response.delete(0, response.length());
					if (!essayer) {
						response.append("<html><head><title>Page d'erreur : " + e.toString() + "</title><head><body>");
						response.append(
								"<img style='float: left;margin-right: 10px;' src=data:image/png;base64,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 />"
										+ "<h3 style='margin-bottom: 25px;'>Oups... mais rien de bien méchant !<br ><span style='color:rgb(231, 94, 94);'>"
										+ e.getMessage() + "</span></h3><code>");
						afficherErreur(response, e);
						Throwable t = e;
						while (t.getCause() != null) {
							t = t.getCause();
							response.append("<hr/>");
							afficherErreur(response, t);
						}
						response.append("</code><p>Avez-vous lu la notice ? <a href=\"/notice\">http://localhost:" + Run.PORT + "/notice</a></p>");

						response.append("</body></html>");
					} else {
						response.append(e.getMessage());
					}
					code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
				}
				long t1 = System.currentTimeMillis() - t0;
				if (!essayer && !Preference.getIntance().getBoolean(Preference.P_WEBONOTTE_FOOTER)) {
					response.append("<hr/><div id=\"webofooter\"><p><small><a href=\"http://langagelinotte.free.fr\">" + version + "</a> - Livre lu en " + t1
							+ " ms - <a href=\"http://localhost:" + Run.PORT + path + "?wbt_source=true\">Voir le livre</a></small></p></div>");
				}
				if (essayer) {
					System.out.println("Exécuté en " + t1 + " ms");
				}
				res.setContentType("text/html");
				res.setStatus(code);
				res.getWriter().println(response.toString());
				((Request) request).setHandled(true);

			} else {
				((Request) request).setHandled(false);
				return;
			}

		}

		private void afficherErreur(StringBuilder response, Throwable e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String s = sw.toString().replaceAll("\n", "</br>");
			response.append(s);
		}

	}

}