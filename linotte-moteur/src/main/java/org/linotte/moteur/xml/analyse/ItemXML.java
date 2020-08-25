/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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

package org.linotte.moteur.xml.analyse;

import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.*;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.analyse.Mathematiques.Temoin;
import org.linotte.moteur.xml.analyse.multilangage.Langage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ItemXML {

	public final static short VALEUR = 0;

	public final static short ACTEUR = 1;

	private short type;

	private String valeurBrute;

	private Object cache;

	private Object[] valeur_compile = null;

	private static String chaine_debut = null;

	private Temoin temoin = new Temoin();

	public static ItemXML factory(String v, short t) {
		if (v.indexOf('&') != -1) {
			List<ItemXML> items = new ArrayList<ItemXML>();
			boolean chaine = false;
			char c;
			int l = v.length(), debut = 0;
			for (int i = 0; i < l; i++) {
				c = v.charAt(i);
				if (c == '\"' && chaine) {
					chaine = false;
				} else if (c == '\"' && !chaine) {
					chaine = true;
				} else if (c != '&') {
				} else if (c == '&' && !chaine) {
					items.add(new ItemXML(v.substring(debut, i).trim(), t));
					debut = i + 1;
				}
			}
			// Ajouter toujours le dernier
			items.add(new ItemXML(v.substring(debut).trim(), t));
			return new GroupeItemXML(items.toArray(new ItemXML[items.size()]));
		} else
			return new ItemXML(v, t);
	}

	private ItemXML(String v, short t) {
		super();
		valeurBrute = v;
		type = t;
	}

	protected ItemXML(ItemXML itemXml) {
		super();
		valeurBrute = itemXml.valeurBrute;
		type = itemXml.type;
	}

	public String getValeurBrute() {
		return valeurBrute;
	}

	@Override
	public String toString() {
		return valeurBrute;
	}

	public boolean isActeur() {
		return type == ACTEUR;
	}

	@Override
	public ItemXML clone() {
		return new ItemXML(valeurBrute, type);
	}

	/**
	 * A terme, il faudra utiliser la méthode retourneValeurTransformee(job, langage)
	 * 
	 * @param job
	 * @return
	 * @throws Exception
	 */
	public Object retourneValeurTransformee(Job job) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		return retourneValeurTransformee(job, runtimeContext.getLinotte().getLangage()) ;
	}
	
	public Object retourneValeurTransformee(Job job, Langage langage) throws Exception {

		if (cache != null) {
			return cache;
		}

		if (valeur_compile == null) {
			try {
				valeur_compile = Mathematiques.parse(valeurBrute, null, 0, langage);
			} catch (Exception e) {
				if (Version.isBeta()) {
					e.printStackTrace();
				}
				throw new ErreurException(Constantes.SYNTAXE_MINI_FORMULE, e.getMessage());
			}
		}
		try {
			Object o = Mathematiques.shrink(valeur_compile, job, temoin);
			if (o instanceof String) {
				o = Mathematiques.interpolation(job, (String) o, temoin, langage);
			}
			if (!temoin.isActeur()) {
				cache = o;
			}
			return o;
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			throw new ErreurException(Constantes.SYNTAXE_MINI_FORMULE, "analyse impossible");
		} catch (ClassCastException e) {
			e.printStackTrace();
			throw new ErreurException(Constantes.SYNTAXE_MINI_FORMULE, "impossible d'utiliser ce rôle");
		} catch (IndexOutOfBoundsException e) {
			String msg = e.getMessage();
			msg = messageFrancais(msg);
			throw new ErreurException(Constantes.SYNTAXE_MINI_FORMULE, "la valeur du pointeur est incorrecte (" + msg + ")");
		} catch (ErreurException e) {
			throw e;
		} catch (StopException e) {
			throw e;
		} catch (LectureException e) {
			throw new FonctionException(e);
		} /*
			* catch (LinotteException e) { throw new ErreurException(e.getErreur(),
			* e.getToken()); }
			*/catch (NumberFormatException e) {
			throw new ErreurException(Constantes.SYNTAXE_MINI_FORMULE, "opération interdite !");
		} catch (ArithmeticException e) {
			//System.err.println(valeurBrute);
			//System.err.println(valeur_compile);
			//e.printStackTrace();
			throw new ErreurException(Constantes.SYNTAXE_MINI_FORMULE, "opération mathématique interdite !");
		} catch (Exception e) {
			if (Version.isBeta()) {
				System.err.println(valeurBrute);
				//System.err.println(valeur_compile);
				e.printStackTrace();
			}
			throw new ErreurException(Constantes.SYNTAXE_MINI_FORMULE, e.getMessage() == null ? valeurBrute : e.getMessage());
		}
	}

	private String messageFrancais(String msg) {
		int taille, index;
		try {
			// "Index: 6, Size: 6"
			StringTokenizer stringTokenizer = new StringTokenizer(msg, ",");
			final Scanner scanner = new Scanner(stringTokenizer.nextToken());
			scanner.next();//Index:
			index = scanner.nextInt();
			scanner.close();
			Scanner scanner2 = new Scanner(stringTokenizer.nextToken());
			scanner2.next();//Size:
			taille = scanner2.nextInt();
			msg = "pointeur = " + index + ", taille = " + taille;
			scanner2.close();
		} catch (Exception e2) {
			try {
				//String index out of range: 57
				StringTokenizer stringTokenizer = new StringTokenizer(msg, ":");
				stringTokenizer.nextToken();
				Scanner scanner = new Scanner(stringTokenizer.nextToken());
				index = scanner.nextInt();
				msg = "pointeur = " + index;
				scanner.close();
			} catch (Exception e3) {
			}
		}
		return msg;
	}

	public static void setChaineDebut(String chaine_deb) {
		chaine_debut = chaine_deb;
	}

	public static String getChaineDebut() {
		return chaine_debut;
	}

	// pour l'optimisation suite à Alizé :
	// Ces valeurs sont préparées par le parseur

	public boolean creation_dynamique;

	public Role contenant = null;

	public boolean variable_locale;

	public boolean doublure;

	public boolean charger;

	public boolean fichier;

	public boolean forcerLivre;

	public String nom_acteur;

	public Role role_acteur;

}