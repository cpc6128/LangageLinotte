/***********************************************************************
 * Linotte                                                             *
 * Version release date : January 11, 2006                             *
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
 *                 Lexer                                               *  
 *                                                                     *
 *       Ce lexer est compatible avec la syntaxe Linotte 1.X           *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.xml.analyse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.linotte.frame.coloration.Style;
import org.linotte.frame.coloration.StyleBuffer;
import org.linotte.frame.coloration.StyleLinotte;
import org.linotte.moteur.exception.FinException;
import org.linotte.moteur.xml.alize.parseur.a.Lexer;
import org.linotte.moteur.xml.exception.NouvelleLigne;

public class LegacyDecoupeDunLivre implements Lexer {

	private static final String CHAR_RETOUR = "\n";

	private static final String CHAR_ESPACE = " ";

	private static final String[] _CHAR_PARTICULIERS = { String.valueOf((char) 160), String.valueOf((char) 65279) };

	private static final List<String> CHAR_PARTICULIERS = Arrays.asList(_CHAR_PARTICULIERS);

	private static final List<String> CHANE_PARTICULIERS = Arrays.asList("==", "<=", ">=", ">>", "<<", "<-", "::", "!=");

	private static final String CHAR_TAB = "\t";

	private static final String DOT = ".";

	private static final String POINT_VIRGULE = ";";

	private static final String CHAR_APOSTROPHE = "'";

	private static final int BUFFER_MIN = 40000;

	private String mot = null;

	private String[] caches_Mots = new String[BUFFER_MIN]; // Mot ou reste ligne

	private int[] caches_Position = new int[BUFFER_MIN]; // Position

	private boolean[] caches_Fin = new boolean[BUFFER_MIN]; // Fin

	private int[] caches_Last = new int[BUFFER_MIN]; // derniere position

	private boolean[] caches_Debut = new boolean[BUFFER_MIN]; // Debut

	private int[] caches_Ligne = new int[BUFFER_MIN]; // derniere position

	private int tailleMax = 0;

	private int[] relation_position_mot;

	private int[] historique;

	private int position = 0;

	private int positionAvantCommentaire = 0;

	private boolean finDeLigne = false;

	private boolean debutDeLigne = true;

	private StringBuilder flux = null;

	private boolean faireExeptionFinDeLigne = false;

	private List<String> findeligne2 = null;

	private int fichierTailleMax;

	private int lastPosition = 0;

	private int ligneCourante = 0;

	private String debutchaine = null;

	private StyleBuffer styleBuffer;

	private Grammaire grammaire;

	private String CD = "(";
	private String CF = ")";
	private String CC = "\"";

	public LegacyDecoupeDunLivre(Grammaire grammaire, StringBuilder pflux, StyleBuffer pstyleBuffer) {
		this.grammaire = grammaire;
		fichierTailleMax = pflux.length();
		flux = pflux;
		relation_position_mot = new int[fichierTailleMax + 1];
		historique = new int[fichierTailleMax + 1];
		Arrays.fill(relation_position_mot, -1);
		findeligne2 = new ArrayList<String>();
		findeligne2.add("\n");
		findeligne2.add(" ");
		findeligne2.add("\t");
		findeligne2.add(CD);
		findeligne2.add(CF);
		findeligne2.add(POINT_VIRGULE);
		debutchaine = ItemXML.getChaineDebut();
		styleBuffer = pstyleBuffer;
	}

	private void addMot(String mot, int pos, boolean fin, boolean debut, int last, int ligne) {

		if (relation_position_mot[last] != -1)
			return;

		caches_Mots[tailleMax] = mot;
		caches_Position[tailleMax] = pos;
		caches_Fin[tailleMax] = fin;
		caches_Last[tailleMax] = last;
		caches_Debut[tailleMax] = debut;
		caches_Ligne[tailleMax] = ligne;
		relation_position_mot[last] = tailleMax;
		historique[position] = last;
		agrandirChaine();
	}

	private String motSuivantEnCache() throws FinException {

		int position_lecture = relation_position_mot[position];
		if (position_lecture == -1)
			return null;

		mot = caches_Mots[position_lecture];
		position = caches_Position[position_lecture];
		finDeLigne = caches_Fin[position_lecture];
		lastPosition = caches_Last[position_lecture];
		debutDeLigne = caches_Debut[position_lecture];
		ligneCourante = caches_Ligne[position_lecture];

		faireExeptionFinDeLigne = finDeLigne;

		return mot;
	}

	private void agrandirChaine() {
		tailleMax++;
		if (tailleMax >= caches_Mots.length) {
			int d = caches_Mots.length, d2 = d * 2;

			String[] c1 = new String[d2];
			int[] c2 = new int[d2];
			boolean[] c3 = new boolean[d2];
			int[] c4 = new int[d2];
			boolean[] c5 = new boolean[d2];
			int[] c6 = new int[d2];

			System.arraycopy(caches_Mots, 0, c1, 0, d);
			System.arraycopy(caches_Position, 0, c2, 0, d);
			System.arraycopy(caches_Fin, 0, c3, 0, d);
			System.arraycopy(caches_Last, 0, c4, 0, d);
			System.arraycopy(caches_Debut, 0, c5, 0, d);
			System.arraycopy(caches_Ligne, 0, c6, 0, d);

			caches_Mots = c1;
			caches_Position = c2;
			caches_Fin = c3;
			caches_Last = c4;
			caches_Debut = c5;
			caches_Ligne = c6;
		}
	}

	public String motSuivant() throws FinException {

		verifierLigne();

		if (motSuivantEnCache() != null)
			return mot;

		lastPosition = position;
		boolean chaine = false;
		int position_debut_commentaire = position;
		int dot = 0;
		int lastDot = 0;
		do {
			String c = null;
			debutDeLigne = finDeLigne;
			finDeLigne = false;
			while (!findeligne2.contains(c)) {

				position++;

				if (position > fichierTailleMax)
					throw new FinException();

				c = Character.toString(flux.charAt(position - 1));
				//System.out.println((int) c.charAt(0) + ":" + c);
				if (!chaine) {
					chaine = c.equals(debutchaine);
				} else if (c.equals(debutchaine))
					chaine = false;

				lastDot = dot;
				if (c.equals(DOT)) {
					dot++;
				} else {
					dot = 0;
				}

				if (!finDeLigne) {
					if (c.equals(CHAR_RETOUR)) {
						ligneCourante++;
					}
					finDeLigne = isNewLine(c, lastDot);
				}

				if (!chaine && grammaire.getFindeligne().contains(c)) {
					// On veut laisser passer les formes : !=, ==, <=, >=
					String t = c;
					boolean passe = true;
					if ((position + 1) < fichierTailleMax) {
						t = t + flux.charAt(position);
						if (CHANE_PARTICULIERS.indexOf(t) > -1) {
							if (position - 1 == lastPosition)
								// a ::nombre
								position++;
							else
								// a::nombre
								position--;
							break;
						}
					}
					if (passe) {
						if ((lastPosition + 1) != position && !c.equals(CHAR_APOSTROPHE))
							position--;
						break;
					}
				}

			}
			mot = flux.substring(lastPosition, position).trim();

			// Linotte ne sait pas interpréter le point-virgule, on le supprime et on le remplace par un caractère vide.
			if (!chaine && mot.endsWith(POINT_VIRGULE)) {
				mot = mot.substring(0, mot.length() - 1);
				while (mot.length() < (position - lastPosition))
					mot += " ";
				replace(lastPosition, position, mot);
				mot = mot.trim();
			}

			if (!chaine && lastDot == 3) {
				// Je sais... c'est moche... mais je n'ai pas le courage
				// de le faire autrement pour l'instant !
				mot = mot.replace("...", " ");
				String mot_remplacer = mot;
				while (mot_remplacer.length() < (position - lastPosition))
					mot_remplacer += " ";
				replace(lastPosition, position, mot_remplacer);
			}
		} while (mot.length() == 0 || chaine);

		// Position avant commentaire;
		positionAvantCommentaire = position;
		if (!finDeLigne) {
			String c = null;
			String cplus = null;
			String cmoins = null;
			boolean commentaire2 = false;
			for (; position <= fichierTailleMax; position++) {
				// C
				c = Character.toString(flux.charAt(position));
				// C+
				if ((position + 1) < fichierTailleMax)
					cplus = Character.toString(flux.charAt(position + 1));
				else
					cplus = null;
				// C-
				if (position > 0)
					cmoins = Character.toString(flux.charAt(position - 1));
				else
					cmoins = null;
				if (c.equals(CD) && CC.equals(cplus)) {
					commentaire2 = true;
					position_debut_commentaire = position;
				} else if (c.equals(CF) && CC.equals(cmoins)) {
					commentaire2 = false;
					setFinCommentaire(position_debut_commentaire, position);
				} else if (!commentaire2) {
					if (isNewLine(c, lastDot)) {
						finDeLigne = true;
						break;
					} else if (!(c.equals(CHAR_ESPACE) || Character.isWhitespace(c.charAt(0)) || CHAR_PARTICULIERS.contains(c) || c.equals(CHAR_TAB))) {
						break;
					}
				}
				if (c.equals(CHAR_RETOUR)) {
					ligneCourante++;
				}
			}
		}

		if (finDeLigne) {
			String c = null;
			String cplus = null;
			String cmoins = null;
			boolean commentaire2 = false;
			try {
				for (; position <= fichierTailleMax; position++) {
					c = Character.toString(flux.charAt(position));
					// C+
					if ((position + 1) < fichierTailleMax)
						cplus = Character.toString(flux.charAt(position + 1));
					else
						cplus = null;
					// C-
					if (position > 0)
						cmoins = Character.toString(flux.charAt(position - 1));
					else
						cmoins = null;
					if (c.equals(CD) && CC.equals(cplus)) {
						commentaire2 = true;
						position_debut_commentaire = position;
					} else if (c.equals(CF) && CC.equals(cmoins)) {
						commentaire2 = false;
						setFinCommentaire(position_debut_commentaire, position);
					} else if (!commentaire2) {
						if (!(c.equals(CHAR_ESPACE) || Character.isWhitespace(c.charAt(0)) || CHAR_PARTICULIERS.contains(c) || c.equals(CHAR_TAB) || isNewLine(
								c, lastDot))) {
							break;
						}
					}
					if (c.equals(CHAR_RETOUR)) {
						ligneCourante++;
					}
				}
			} catch (Exception e) {
			}
		}

		addMot(mot, position, finDeLigne, debutDeLigne, lastPosition, ligneCourante);

		faireExeptionFinDeLigne = finDeLigne;

		return mot;
	}

	private void setFinCommentaire(int debut, int fin) {
		if (styleBuffer != null) {
			styleBuffer.ajouteStyle(new Style(debut, fin - debut + 1, StyleLinotte.style_commentaire, null));
		}
	}

	private void verifierLigne() throws NouvelleLigne {
		if (faireExeptionFinDeLigne) {
			faireExeptionFinDeLigne = false;
			throw new NouvelleLigne();
		}
	}

	public String resteLigne() throws FinException {

		verifierLigne();

		StringBuilder ligne = new StringBuilder();
		try {
			while (true) {
				ligne.append(motSuivant()).append(CHAR_ESPACE);
			}
		} catch (FinException e) {
		}

		mot = ligne.toString().trim();
		faireExeptionFinDeLigne = true;

		return mot;
	}

	public String getMot() {
		return mot;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int pos) throws FinException {
		if (pos >= historique.length)
			throw new FinException();

		this.position = historique[pos];
		// Bogue si la position est le début du livre
		if (pos != 0)
			try {
				setFaireExeptionFinDeLigne(false);
				motSuivant();
			} catch (FinException e) {
			}
	}

	public boolean isFinDeLigne() {
		return finDeLigne;
	}

	public int getLastPosition() {
		return lastPosition;
	}

	public void retour() {
		int avant = historique[position];
		position = historique[avant];
		setFaireExeptionFinDeLigne(false);
		try {
			motSuivant();
		} catch (FinException e) {
		}
	}

	/**
	 * Vérifier s'il y a le ...\n ou le ; avant
	 * @param c
	 * @param dot
	 * @return
	 */
	private boolean isNewLine(String c, int dot) {
		return dot != 3 && (c.equals(CHAR_RETOUR) || c.equals(POINT_VIRGULE));
	}

	public void setFaireExeptionFinDeLigne(boolean faireExeptionFinDeLigne) {
		this.faireExeptionFinDeLigne = faireExeptionFinDeLigne;
	}

	public String subString(int start, int end) {
		return flux.substring(start, end);
	}

	private void replace(int start, int end, String newWord) {
		flux = flux.replace(start, end, newWord);
	}

	public int getLigneCourante() {
		return ligneCourante;
	}

	public void setPositionAvantCommentaire(int positionAvantCommentaire) {
		this.positionAvantCommentaire = positionAvantCommentaire;
	}

	@Override
	public int getPositionAvantCommentaire() {
		return positionAvantCommentaire;
	}

	@Override
	public boolean isFaireExeptionFinDeLigne() {
		return faireExeptionFinDeLigne;
	}

}
