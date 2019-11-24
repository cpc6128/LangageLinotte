/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 16, 2007                                *
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

package org.linotte.moteur.entites;

import java.applet.Applet;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.linotte.frame.latoile.Toile;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.FichierOutils;
import org.linotte.moteur.outils.Ressources;

public class Fichier {

	private String url = null;

	private static final String RETOUR_CHARIOT = System.getProperty("line.separator");

	// Fichier depuis internet :
	private boolean internet;

	public Fichier(String url) {
		this.url = url;
		internet = Ressources.getInstance().isInternetUrl(url);
	}

	public String getUrl() {
		return Ressources.construireChemin(url);
	}

	public synchronized void lire(Acteur a) throws ErreurException {
		Reader reader = null;
		try {
			reader = getReader(a);

			Scanner scanner = new Scanner(reader);

			if (!(a instanceof Casier)) {
				// Si c'est une espèce, on écrit les attributs par ordre
				// alphabétique :
				if (a instanceof Prototype) {
					lire((Prototype) a, scanner);
				} else {
					// Texte ou nombre :
					String valeur = scanner.nextLine();
					Object o = null;
					if (a.getRole() == Role.NOMBRE) {
						o = new BigDecimal(valeur);
					} else {
						o = valeur;
					}
					a.setValeurSimple(o);
				}
			} else {
				Casier casier = (Casier) a;
				// Casier :

				if (casier.roleContenant() == Role.ESPECE) {
					boolean lu;
					do {
						Prototype temp = a.librairie.creationEspece(casier.getTypeespece(), "", null);
						lu = lire(temp, scanner);
						if (lu)
							casier.ajouterValeur(temp, false);
					} while (lu);
				} else {
					while (scanner.hasNext()) {
						String valeur = scanner.nextLine();
						//
						Object o = null;
						if (casier.roleContenant() == Role.NOMBRE) {
							o = new BigDecimal(valeur);
						} else if (casier.roleContenant() == Role.TEXTE) {
							o = valeur;
						}
						Acteur acteur = new Acteur(casier.roleContenant(), o);
						casier.ajouterValeur(acteur, false);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// throw new ErreurException(Constantes.LECTURE_FICHIER);
		} catch (Exception e) {
			throw new ErreurException(Constantes.LECTURE_FICHIER);
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}
	}

	private Reader getReader(Acteur a) throws ErreurException, IOException, MalformedURLException, UnsupportedEncodingException, FileNotFoundException {
		Reader reader;
		if (getUrl() == null)
			throw new ErreurException(Constantes.LECTURE_FICHIER);
		if (internet) {
			URLConnection conn = new URL(url).openConnection();
			conn.connect();
			reader = new InputStreamReader(conn.getInputStream(), FichierOutils.getENCODAGE());
		} else {
			if (!Toile.isApplet()) {
				InputStreamReader is = new InputStreamReader(new FileInputStream(getUrl()), FichierOutils.getENCODAGE());
				reader = new BufferedReader(is);
			} else {
				reader = new InputStreamReader((new URL(((Applet) a.librairie.getToilePrincipale()).getCodeBase(), url)).openStream(),
						FichierOutils.getENCODAGE());
			}
		}
		return reader;
	}

	private boolean lire(Prototype espece, Scanner scanner) throws IOException {
		// Si c'est une espèce, on écrit les attributs par ordre
		// alphabétique :
		Map<String, Acteur> m = espece.retourAttributsMap();
		Object[] clefs = m.keySet().toArray();
		Arrays.sort(clefs);
		Iterator<?> liste_clefs = Arrays.asList(clefs).iterator();
		boolean valide = false;
		while (scanner.hasNext() && liste_clefs.hasNext()) {
			String clef = (String) liste_clefs.next();
			String valeur = scanner.nextLine();
			Acteur attribut = espece.retourneAttributSimple(clef);
			Object o = null;
			if (attribut.getRole() == Role.NOMBRE) {
				o = new BigDecimal(valeur);
			} else if (attribut.getRole() == Role.TEXTE) {
				o = valeur;
			}
			attribut.setValeurSimple(o);
			valide = true;
		}
		return valide;
	}

	public synchronized void ecrire(Acteur a) throws ErreurException {
		Writer writer = null;

		try {

			writer = getWriter();

			if (!(a instanceof Casier)) {
				if (a instanceof Prototype) {
					ecrire((Prototype) a, writer);
				} else {
					// Texte ou nombre :
					Object o = a.getValeur();
					if (o instanceof BigDecimal)
						writer.write(String.valueOf(o));
					else
						writer.write((String) o);
				}
			} else {
				// Casier :
				Iterator<?> i = ((List<?>) a.getValeur()).iterator();
				while (i.hasNext()) {
					Acteur temp = (Acteur) i.next();
					if (temp instanceof Casier) {
						ecrire((Casier) temp, writer);
					} else if (temp instanceof Prototype) {
						ecrire((Prototype) temp, writer);
					} else {
						Object o = temp.getValeur();
						if (o instanceof BigDecimal)
							writer.write(String.valueOf(o));
						else {
							if (o != null)
								writer.write((String) o);
						}
						writer.write(RETOUR_CHARIOT);
					}
				}
			}
			writer.close();
		} catch (FileNotFoundException e) {
			throw new ErreurException(Constantes.ECRITURE_FICHIER);
		} catch (IOException e) {
			throw new ErreurException(Constantes.ECRITURE_FICHIER);
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {
				}
		}
	}

	private Writer getWriter() throws ErreurException, UnsupportedEncodingException, FileNotFoundException {
		Writer writer;
		if (internet) {
			throw new ErreurException(Constantes.ERREUR_MODIFICATION_INTERNET, url);
		}
		if (getUrl() == null)
			throw new ErreurException(Constantes.ECRITURE_FICHIER);

		OutputStreamWriter is = new OutputStreamWriter(new FileOutputStream(getUrl()), FichierOutils.getENCODAGE());
		writer = new BufferedWriter(is);
		return writer;
	}

	private void ecrire(Prototype espece, Writer writer) throws ErreurException, IOException {
		// Si c'est une espèce, on écrit les attributs par ordre
		// alphabétique :
		Map<String, Acteur> m = espece.retourAttributsMap();
		Object[] clefs = m.keySet().toArray();
		Arrays.sort(clefs);
		for (Object clef : clefs) {
			Object o = espece.retourneAttribut((String) clef).getValeur();
			if (o instanceof BigDecimal)
				writer.write(String.valueOf(o));
			else {
				if (o instanceof String)
					writer.write((String) o);
				else {
					throw new ErreurException(Constantes.ECRITURE_FICHIER, (String) clef);
				}
			}
			writer.write(RETOUR_CHARIOT);
		}
	}

	private void ecrire(Casier casier, Writer writer) throws ErreurException, IOException {
		// Casier :
		Iterator<?> i = ((List<?>) casier.getValeur()).iterator();
		while (i.hasNext()) {
			Acteur temp = (Acteur) i.next();
			if (temp instanceof Casier) {
				ecrire((Casier) temp, writer);
			} else if (temp instanceof Prototype) {
				ecrire((Prototype) temp, writer);
			} else {
				Object o = temp.getValeur();
				if (o instanceof BigDecimal)
					writer.write(String.valueOf(o));
				else
					writer.write((String) o);
				writer.write(RETOUR_CHARIOT);
			}
		}
	}
}
