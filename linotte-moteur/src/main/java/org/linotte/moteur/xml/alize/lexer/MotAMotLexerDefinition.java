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

import org.linotte.moteur.xml.analyse.Grammaire;
import org.linotte.moteur.xml.analyse.ItemXML;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MotAMotLexerDefinition {

	static final String RETOUR = "\n";

	static final String ESPACE = " ";

	static final String TABULATION = "\t";

	static final String POINT = ".";

	static final String APOSTROPHE = "'";

	static final String PARENTHESE_OUVRANTE = "(";

	static final String PARENTHESE_FERMANTE = ")";

	static final String[] _CHAR_PARTICULIERS = { String.valueOf((char) 160), String.valueOf((char) 65279) };

	static final List<String> CHAR_PARTICULIERS = Arrays.asList(_CHAR_PARTICULIERS);

	static final List<String> CHAINE_PARTICULIERS = Arrays.asList("==", "<=", ">=", ">>", "<<", "<-", "::", "!=", ":=", "+=", "-=");

	final List<String> RETOURLIGNE = new ArrayList<>(
			Arrays.asList(",", "+", "*", "&", "-", "=", ")", "(", "/", "==", "<=", ">=", "<-", "{", "[", "]", "}", "::", "!="));

	static final String COMMENTAIRE_DEBUT = "/";
	static final String COMMENTAIRE_FIN = "/";
	static final String COMMENTAIRE_LONG = "*";

	final List<String> MARQUEUR_FIN_LIGNE = new ArrayList<>(Arrays.asList(RETOUR, ESPACE, TABULATION, COMMENTAIRE_DEBUT, COMMENTAIRE_FIN));

	// Valeur dynamique Linotte 2.4 pour le multilangage
	String pointVirgule;

	String pointeurOuvrant;

	String pointeurFermant;
	// Fin : Valeur dynamique Linotte 2.4 pour le multilangage

	Grammaire grammaire;

	String debutchaine;

	public MotAMotLexerDefinition(Grammaire grammaire) {
		this.grammaire = grammaire;

		pointVirgule = grammaire.getLangage().getSeparateurLigne();
		pointeurOuvrant = grammaire.getLangage().getPointeurDebut();
		pointeurFermant = grammaire.getLangage().getPointeurFin();

		/*
		 * Il faut supprimer ces caractères de retourLigne si les pointeurs ne
		 * sont pas { ou }
		 */

		if (!pointeurOuvrant.equals("{")) {
			RETOURLIGNE.remove("{");
			RETOURLIGNE.remove("}");
		}
		if (!pointeurOuvrant.equals("[")) {
			RETOURLIGNE.remove("[");
			RETOURLIGNE.remove("]");
		}

		MARQUEUR_FIN_LIGNE.add(pointVirgule);
		debutchaine = ItemXML.getChaineDebut();

	}
}
