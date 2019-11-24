/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.util.Arrays;
import java.util.List;

import org.linotte.frame.cahier.timbre.entite.Timbre;

public class FichierOutils {

	private static String ENCODAGE = null;

	static {
		String code = Preference.getIntance().getProperty(Preference.ENCODAGE);
		if (code == null) {
			ENCODAGE = "UTF-8";
		} else {
			ENCODAGE = code;
		}
	}

	public static void ecrire(File fichier, String flux) throws IOException {
		OutputStreamWriter is = new OutputStreamWriter(new FileOutputStream(fichier), ENCODAGE);
		BufferedWriter out = new BufferedWriter(is);
		out.write(flux);
		out.close();
	}

	public static String lire(String fichier) throws IOException {
		return lire(new File(fichier));
	}

	public static String lire(File fichier, String encodage) throws IOException {
		return lire(new FileInputStream(fichier), encodage);
	}

	public static String lire(InputStream pis) throws IOException {
		return lire(pis, ENCODAGE);
	}

	public static String lire(InputStream fichier, String encodage) throws IOException {
		InputStreamReader is = new InputStreamReader(fichier, encodage);
		BufferedReader br = new BufferedReader(is);
		StringBuilder buffer = new StringBuilder();
		String str;
		boolean premier = true;
		while ((str = br.readLine()) != null) {
			if (premier)
				premier = false;
			else
				buffer.append("\n");
			buffer.append(str);
		}
		br.close();
		return buffer.toString();
	}

	public static String lire(File fichier) throws IOException {
		return lire(fichier, ENCODAGE);
	}

	public static StringBuilder lire(File fichier, List<Integer> numerolignes) throws IOException {
		FileInputStream is = new FileInputStream(fichier);
		StringBuilder buffer = lire(is, numerolignes);
		is.close();
		return buffer;
	}

	public static StringBuilder lire(InputStream is, List<Integer> numerolignes) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, ENCODAGE));
		StringBuilder buffer = new StringBuilder();
		int position = 0;
		String str;
		while ((str = br.readLine()) != null) {
			buffer.append(str);
			buffer.append("\n");
			position += str.length() + 1;
			numerolignes.add(position);
		}
		br.close();
		return buffer;
	}

	public static String getENCODAGE() {
		return ENCODAGE;
	}

	public static void ecrireLivreGraphique(File fichier, List<Timbre> objets) throws IOException {
		XMLEncoder encoder = new XMLEncoder(new FileOutputStream(fichier));
		encoder.writeObject(objets);
		encoder.close();

	}

	/**
	 * Vérification si le livre commence par l'entete xml
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static boolean estCeUnLivreVisuel(PushbackInputStream is) throws IOException {
		byte[] tampon = new byte[2];
		final byte[] entete = { '<', '?' };
		int c = is.read(tampon);
		if (c != -1) {
			is.unread(tampon);
			if (Arrays.equals(tampon, entete)) {
				return true;
			}
		}
		return false;

	}

	public static List<Timbre> lireLivreGraphique(InputStream is) {
		try {
			XMLDecoder decoder = new XMLDecoder(is);
			@SuppressWarnings("unchecked")
			List<Timbre> objets = (List<Timbre>) decoder.readObject();
			decoder.close();
			return objets;
		} catch (Exception e) {
		}
		return null;
	}

	public static void copierFichier(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1000];

		try {
			while (true) {
				synchronized (buffer) {
					int amountRead = in.read(buffer);
					if (amountRead == -1) {
						break;
					}
					out.write(buffer, 0, amountRead);
				}
			}
			//out.write('\n');
		} finally {
			if (in != null) {
				in.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

}
