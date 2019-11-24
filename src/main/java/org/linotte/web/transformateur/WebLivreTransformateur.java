package org.linotte.web.transformateur;

import org.alize.http.i.WebTransformateur;

/**
 * 
 * Parseur des fichiers WebLivre (*.wliv)
 * 
 * Prend en entrée un Weblivre et le transforme en Livre
 * 
 * @author Ronan Mounès
 *
 */
public class WebLivreTransformateur implements WebTransformateur {

	private static final String TEMPLATE_DEBUT_LIVRE = " décor :\ndébut\naffiche \"";
	private static final String TEMPLATE_VERBE_AFFICHER_DEBUT = "\naffiche \"";
	private static final String TEMPLATE_VERBE_AFFICHER_FIN = "\"\n";
	private static final String TEMPLATE_FIN_LIVRE = "\ntermine";
	private static final String TEMPLATE_GUILLEMET = "\" + µ\"0022\" + \"";

	public WebLivreTransformateur() {

	}

	public boolean accepter(String extention) {
		return extention.toLowerCase().endsWith(".wliv");
	}

	public StringBuilder traiter(StringBuilder in) {
		StringBuilder out = new StringBuilder();
		out.append(TEMPLATE_DEBUT_LIVRE);
		int s = in.length();
		boolean html = true;
		int prefix = 0, suffix = 0;
		char courant;
		for (int i = 0; i < s; i++) {
			courant = in.charAt(i);
			if (courant == '<' && prefix == 0) {
				prefix = 1;
			} else if (courant == '%' && prefix == 1) {
				prefix = 0;
				html = false;
				// Fin du verbe afficher :
				out.append(TEMPLATE_VERBE_AFFICHER_FIN);
			} else if (courant == '%' && suffix == 0 && !html) {
				prefix = 0;
				suffix = 1;
			} else if (courant == '>' && suffix == 1) {
				prefix = 0;
				suffix = 0;
				html = true;
				out.append(TEMPLATE_VERBE_AFFICHER_DEBUT);
			} else if (prefix == 1) {
				out.append('<');
				out.append(courant);
				prefix = 0;
			} else if (suffix == 1) {
				out.append('%');
				out.append(courant);
				prefix = 0;
				suffix = 0;
			} else if (!html) {
				out.append(courant);
			} else if (html) {
				if (courant == '\"') {
					out.append(TEMPLATE_GUILLEMET);
				} else {
					out.append(courant);
				}
			}
		}
		if (html) {
			out.append(TEMPLATE_VERBE_AFFICHER_FIN);
		}
		out.append(TEMPLATE_FIN_LIVRE);
		return out;
	}
}
