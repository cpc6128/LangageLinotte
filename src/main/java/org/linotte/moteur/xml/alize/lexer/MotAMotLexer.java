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
 ***********************************************************************/

package org.linotte.moteur.xml.alize.lexer;

import org.linotte.frame.coloration.Style;
import org.linotte.frame.coloration.StyleBuffer;
import org.linotte.frame.coloration.StyleLinotte;
import org.linotte.moteur.exception.FinException;
import org.linotte.moteur.xml.alize.parseur.a.Lexer;
import org.linotte.moteur.xml.analyse.Grammaire;

/**
 *
 * Ce lexer "maison" extrait les mots du livre que seront analysés grâce au
 * fichier grammaire.xml.<br>
 * Il ignore les commentaires et prend en compte certaines des spécifications du
 * langage Linotte.<br>
 * Ce @Lexer est utilisé par le @Parseur. Il ne sait pas analyser les formules
 * mathématiques, juste des phrases en français ! Ce travail est délégué à la
 * classe @Mathematiques
 * 
 * @author cpc
 *
 */
public class MotAMotLexer extends MotAMotLexerDefinition implements Lexer {

	// Mot trouvé à analyser
	private String mot;

	// Livre
	private StringBuilder fluxLivre;

	// Taille du livre
	private int livreTaille;

	// Cache des mots et positions des mots
	private MotAMotCache cacheMots;

	// Patch pour l'affichage des styles dans l'atelier :
	private int positionAvantCommentaire = 0;
	private StyleBuffer styleBuffer;
	//private int debutPositionStyle = 0;
	public int positionDebutPremierMotDepose = -1;
	// fin patch

	public MotAMotLexer(Grammaire grammaire, StringBuilder pfluxLivre, StyleBuffer pstyleBuffer) {
		super(grammaire);
		fluxLivre = pfluxLivre;
		livreTaille = fluxLivre.length();
		cacheMots = new MotAMotCache(livreTaille);
		styleBuffer = pstyleBuffer;
	}

	@Override
	public String motSuivant() throws FinException {

		// Bogue, on ne traite pas le cas où l'on commence par des commentaires!
		// Algo : on cherche le mot et ensuite les commentaires

		cacheMots.verifierLigne();

		if ((mot = cacheMots.motSuivantEnCache()) != null)
			return mot;

		boolean modeChaine = false;
		short nbParentheses = 0;
		short nbCrochets = 0;
		int nombrePoint = 0;
		int nombrePointPrecedent = 0;
		int debutMot;
		cacheMots.dernierePosition = cacheMots.position;
		do {
			int positionDebutCommentaire = cacheMots.position;
			String c = null;
			cacheMots.debutDeLigne = cacheMots.finDeLigne;
			cacheMots.finDeLigne = false;
			while (!MARQUEUR_FIN_LIGNE.contains(c)) {

				cacheMots.position++;

				if (cacheMots.position > livreTaille)
					throw new FinException();

				c = Character.toString(fluxLivre.charAt(cacheMots.position - 1));

				// **************************************
				// Est-ce une chaine "toto" ?
				// **************************************
				if (!modeChaine) {
					modeChaine = c.equals(debutchaine);
				} else if (c.equals(debutchaine))
					modeChaine = false;

				// **************************************
				// Gestion des parentheses ((1+1)*3):
				// **************************************
				if (!modeChaine) {
					if (c.equals(PARENTHESE_OUVRANTE)) {
						nbParentheses++;
					} else if (c.equals(PARENTHESE_FERMANTE)) {
						nbParentheses--;
					} else if (c.equals(COMMENTAIRE_DEBUT)) {
						if ((cacheMots.position) < livreTaille) {
							String cplus = Character.toString(fluxLivre.charAt(cacheMots.position));
							if (COMMENTAIRE_LONG.equals(cplus) || COMMENTAIRE_DEBUT.equals(cplus)) {
								cacheMots.position--;
								break;
							}
						}

					}
				}

				// **************************************
				// Est-ce une valeur dynamique ? <acteur>
				// **************************************
				if (!modeChaine) {
					if (c.equals(pointeurOuvrant)) {
						nbCrochets++;
					} else if (c.equals(pointeurFermant)) {
						nbCrochets--;
					}
				}

				// **************************************
				// Est-ce une liste de points ? ...
				// **************************************
				nombrePointPrecedent = nombrePoint;
				if (!modeChaine && c.equals(POINT)) {
					nombrePoint++;
				} else {
					nombrePoint = 0;
				}

				// **************************************
				// Est-ce un retour chariot ?
				// **************************************
				if (!cacheMots.finDeLigne) {
					if (c.equals(RETOUR)) {
						cacheMots.ligneCourante++;
					}
					cacheMots.finDeLigne = isNewLine(c, nombrePointPrecedent);
				}

				// **************************************
				// Est-ce la syntaxe a::1 ou 1<2 ?
				// **************************************

				// J'ai ajouté && c.equals("-") &&
				// Pour prendre en compte la nouvelle syntaxe a -= 1 et ne pas
				// perdre la syntaxe 1 < -10 (Linotte 2.7)
				if (!modeChaine && (grammaire.getFindeligne().contains(c) || c.equals("-")) && nbParentheses == 0 && nbCrochets == 0) {
					// On veut laisser passer les formes : !=, ==, <=, >=
					String t = c;
					boolean passe = true;
					if ((cacheMots.position + 1) < livreTaille) {
						t = t + fluxLivre.charAt(cacheMots.position);
						if (CHAINE_PARTICULIERS.indexOf(t) > -1) {
							if (cacheMots.position - 1 == cacheMots.dernierePosition)
								// a ::nombre
								cacheMots.position++;
							else
								// a::nombre
								cacheMots.position--;
							break;
						} else {
							if (c.equals("-"))
								passe = false;
						}
					}
					if (passe) {
						if ((cacheMots.dernierePosition + 1) != cacheMots.position && !c.equals(APOSTROPHE))
							cacheMots.position--;
						break;
					}
				}

			}
			// Supprime les espaces avant pour ne pas gêner la gestion des
			// couleurs dans l'Atelier :

			while (((cacheMots.position + 1) < livreTaille)
					&& (fluxLivre.charAt(cacheMots.dernierePosition) <= ' ' && cacheMots.dernierePosition < cacheMots.position)) {
				cacheMots.dernierePosition++;
			}
			// On tronc les 3 petits points s'ils sont présents :
			mot = fluxLivre.substring(cacheMots.dernierePosition, (nombrePointPrecedent == 3) ? (cacheMots.position - 4) : cacheMots.position).trim();

			if (mot.length() == 0 && nombrePointPrecedent == 3) {
				cacheMots.dernierePosition = cacheMots.position - 1;
			}

			debutMot = cacheMots.dernierePosition;

			// Linotte ne sait pas interpréter le point-virgule, on le supprime
			// et on le remplace par un caractère vide.
			if (!modeChaine && mot.endsWith(pointVirgule)) {
				mot = mot.substring(0, mot.length() - 1);
				while (mot.length() < (cacheMots.position - cacheMots.dernierePosition))
					mot += " ";
				replace(cacheMots.dernierePosition, cacheMots.position, mot);
				mot = mot.trim();
			}

			// ***********************************************
			// * Analyse des commentaires qui sont à ignorer :
			// *
			// ***********************************************
			// Position avant commentaire

			setPositionAvantCommentaire(cacheMots.position);

			positionDebutCommentaire = passerCommentaire(nombrePointPrecedent, positionDebutCommentaire, !cacheMots.finDeLigne, false);
			passerCommentaire(nombrePointPrecedent, positionDebutCommentaire, cacheMots.finDeLigne, true);

		} while (mot.length() == 0 || modeChaine || nbParentheses > 0 || nbCrochets > 0);

		// Patch pour l'affichage dans le cahier.
		int positionDebutDuMot = (positionDebutPremierMotDepose == -1) ? 0 : debutMot;

		cacheMots.addMot(mot, positionDebutDuMot);

		if (positionDebutPremierMotDepose == -1) {
			// Cette variable ne doit plus être mise à jour
			positionDebutPremierMotDepose = debutMot;
		}

		cacheMots.faireExeptionFinDeLigne = cacheMots.finDeLigne;
		return mot;
	}

	/**
	 * @param nombrePointPrecedent
	 *            Forme ...
	 * @param positionDebutCommentaire
	 *            Premiere position du commentaier
	 * @param test
	 *            à true si on pense que c'est un commentaire de fin de ligne
	 * @param bascule
	 *            à true si c'est un commentaire de fin de ligne
	 * @return
	 */
	private int passerCommentaire(int nombrePointPrecedent, int positionDebutCommentaire, boolean test, boolean bascule) {
		if (test) {
			String ccourant = null;
			String cplus = null;
			String cmoins = null;
			boolean modeCommentaire = false;
			boolean commentaireUneLigne = false;
			try {
				for (; (cacheMots.position + 1) <= livreTaille; cacheMots.position++) {
					// C
					ccourant = Character.toString(fluxLivre.charAt(cacheMots.position));
					// C+
					if ((cacheMots.position + 1) < livreTaille)
						cplus = Character.toString(fluxLivre.charAt(cacheMots.position + 1));
					else
						cplus = null;
					// C-
					if (cacheMots.position > 0)
						cmoins = Character.toString(fluxLivre.charAt(cacheMots.position - 1));
					else
						cmoins = null;
					if (ccourant.equals(COMMENTAIRE_DEBUT) && COMMENTAIRE_LONG.equals(cplus)) {
						modeCommentaire = true;
						positionDebutCommentaire = cacheMots.position;
					} else if (!modeCommentaire && ccourant.equals(COMMENTAIRE_DEBUT) && COMMENTAIRE_DEBUT.equals(cplus)) {
						// Forme des commentaires : // mon message
						modeCommentaire = true;
						commentaireUneLigne = true;
						positionDebutCommentaire = cacheMots.position;
					} else if (!commentaireUneLigne && ccourant.equals(COMMENTAIRE_FIN) && COMMENTAIRE_LONG.equals(cmoins)) {
						modeCommentaire = false;
						setFinCommentaire(positionDebutCommentaire, cacheMots.position);
						cacheMots.dernierePosition = cacheMots.position + 1;
					} else if (commentaireUneLigne && isNewLine(ccourant, nombrePointPrecedent)) {
						modeCommentaire = false;
						commentaireUneLigne = false;
						setFinCommentaire(positionDebutCommentaire, cacheMots.position);
						cacheMots.dernierePosition = cacheMots.position + 1;
						cacheMots.finDeLigne = true;
					} else if (!modeCommentaire) {
						if (bascule) {
							if (!(ccourant.equals(ESPACE) || Character.isWhitespace(ccourant.charAt(0)) || CHAR_PARTICULIERS.contains(ccourant)
									|| ccourant.equals(TABULATION) || isNewLine(ccourant, nombrePointPrecedent))) {
								break;
							}
						} else {
							if (ccourant.equals(RETOUR) && RETOURLIGNE.contains(mot)) {
								cacheMots.dernierePosition = cacheMots.position + 1;
								// Il faut supprimer les caractères vides :
								// Bogue :
								// https://code.google.com/p/langagelinotte/issues/detail?id=132
								cacheMots.position++;
								String bogue = Character.toString(fluxLivre.charAt(cacheMots.position));
								while (((cacheMots.position + 1) < livreTaille) && (bogue.equals(ESPACE) || Character.isWhitespace(bogue.charAt(0))
										|| CHAR_PARTICULIERS.contains(bogue) || bogue.equals(TABULATION))) {
									cacheMots.position++;
									bogue = Character.toString(fluxLivre.charAt(cacheMots.position));
								}
								break;
							} else if (isNewLine(ccourant, nombrePointPrecedent)) {
								cacheMots.finDeLigne = true;
								break;
							} else if (!(ccourant.equals(ESPACE) || Character.isWhitespace(ccourant.charAt(0)) || CHAR_PARTICULIERS.contains(ccourant)
									|| ccourant.equals(TABULATION))) {
								break;
							}
						}
					}
					if (ccourant.equals(RETOUR)) {
						cacheMots.ligneCourante++;
					}
				}

			} catch (Exception e) {
			}
		}
		return positionDebutCommentaire;
	}

	private void setFinCommentaire(int debut, int fin) {
		if (styleBuffer != null) {
			styleBuffer.ajouteStyle(new Style(debut, fin - debut + 1, StyleLinotte.style_commentaire, null));
		}
	}

	@Override
	public String getMot() {
		return mot;
	}

	@Override
	public int getPosition() {
		return cacheMots.position;
	}

	@Override
	public void setPosition(int pos) throws FinException {
		if (pos >= cacheMots.historique.length)
			throw new FinException();

		cacheMots.position = cacheMots.historique[pos];
		// Bogue si la position est le début du livre
		if (pos != 0)
			try {
				setFaireExeptionFinDeLigne(false);
				motSuivant();
			} catch (FinException e) {
			}
	}

	@Override
	public boolean isFinDeLigne() {
		return cacheMots.finDeLigne;
	}

	@Override
	public int getLastPosition() {
		if (cacheMots.dernierePosition == 0)
			return positionDebutPremierMotDepose;
		else
			return cacheMots.dernierePosition;
	}

	@Override
	public void retour() {
		int avant = cacheMots.historique[cacheMots.position];
		cacheMots.position = cacheMots.historique[avant];
		setFaireExeptionFinDeLigne(false);
		try {
			motSuivant();
		} catch (FinException e) {
		}
	}

	/**
	 * Vérifier s'il y a le ...\n ou le ; avant
	 * 
	 * @param c
	 * @param dot
	 * @return
	 */
	private boolean isNewLine(String c, int dot) {
		return dot != 3 && (c.equals(RETOUR) || c.equals(pointVirgule));
	}

	@Override
	public String resteLigne() throws FinException {

		cacheMots.verifierLigne();

		StringBuilder ligne = new StringBuilder();
		try {
			while (true) {
				ligne.append(motSuivant()).append(ESPACE);
			}
		} catch (FinException e) {
		}

		mot = ligne.toString().trim();
		cacheMots.faireExeptionFinDeLigne = true;

		return mot;
	}

	@Override
	public void setFaireExeptionFinDeLigne(boolean faireExeptionFinDeLigne) {
		cacheMots.faireExeptionFinDeLigne = faireExeptionFinDeLigne;
	}

	@Override
	public String subString(int start, int end) {
		return fluxLivre.substring(start, end);
	}

	private void replace(int start, int end, String newWord) {
		fluxLivre = fluxLivre.replace(start, end, newWord);
	}

	@Override
	public int getLigneCourante() {
		return cacheMots.ligneCourante;
	}

	private void setPositionAvantCommentaire(int positionAvantCommentaire) {
		this.positionAvantCommentaire = positionAvantCommentaire;
	}

	@Override
	public int getPositionAvantCommentaire() {
		return positionAvantCommentaire;
	}

	@Override
	public boolean isFaireExeptionFinDeLigne() {
		return cacheMots.faireExeptionFinDeLigne;
	}

}
