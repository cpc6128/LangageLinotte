/***********************************************************************
 * Linotte                                                             *
 * Version release date : Febrary 23 2015                              *
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

package org.linotte.moteur.xml.analyse.multilangage;

import org.linotte.moteur.xml.analyse.multilangage.algonotte.MathematiqueAlgonotte;
import org.linotte.moteur.xml.analyse.multilangage.linnet.MathematiqueLinnet;
import org.linotte.moteur.xml.analyse.multilangage.linotte2.MathematiqueLinotte;

public enum Langage {

    Linotte2("Linotte 2", "linotte2.xml", ";", false, new String[]{"{", "}"}, new char[]{'[', ']'}, "exemples", MathematiqueLinotte.values(), MathematiqueLinotte.de, false),
    Linnet("Linnet - Algorithmique en anglais", "linnet.xml", ";", false, new String[]{"[", "]"}, new char[]{'¤', '¤'}, "exemples/c_linnet", MathematiqueLinnet.values(), MathematiqueLinnet.de, true),
    Lyre("Lyre - Algorithmique en français", "lyre.xml", ";", false, new String[]{"[", "]"}, new char[]{'¤', '¤'}, "exemples/d_lyre", MathematiqueAlgonotte.values(), MathematiqueAlgonotte.de, true),
    Linotte1X("Linotte 1 - première version du langage", "linotte1.xml", ";", true, new String[]{"{", "}"}, new char[]{'[', ']'}, "exemples/b_linotte_1x", MathematiqueLinotte.values(), MathematiqueLinotte.de, false),
    ;

    private String nom;

    private String fichier;

    private String separateurLigne;

	private boolean legacy;

	private char crochetDebut;

	private char crochetFin;

	private String pointeurDebut;

	private String pointeurFin;
	
	private String cheminExemple;
	
	private MathematiqueOperation[] operation;

	private MathematiqueOperation attributEspece;

	private boolean forceParametreEnligne;

	/**
	 * @param pnom
	 * @param pfichier
	 * @param pSeparateurLigne
	 * @param plegacy true si on veut la gestion de la mémoire comme Linotte 1.X
	 * @param pointeur
	 * @param crochet
	 * @param pcheminExemple
	 * @param poperation
	 * @param pattributEspece
	 * @param forceParametreEnligne
	 */
	private Langage(String pnom, String pfichier, String pSeparateurLigne, boolean plegacy, String[] pointeur, char[] crochet, String pcheminExemple, MathematiqueOperation[] poperation, MathematiqueOperation pattributEspece, boolean pforceParametreEnligne) {
		nom = pnom;
		fichier = pfichier;
		legacy = plegacy;
		separateurLigne = pSeparateurLigne;
		pointeurDebut = pointeur[0];
		pointeurFin = pointeur[1];
		cheminExemple = pcheminExemple;
		operation = poperation;
		attributEspece = pattributEspece;
		crochetDebut = crochet[0];
		crochetFin = crochet[1];
		forceParametreEnligne = pforceParametreEnligne;
	}

	public String getNom() {
		return nom;
	}

	public String getFichier() {
		return fichier;
	}

	public boolean isLegacy() {
		return legacy;
	}

	public boolean isForceParametreEnligne() {
		return forceParametreEnligne;
	}

	public String getSeparateurLigne() {
		return separateurLigne;
	}

	public String getPointeurDebut() {
		return pointeurDebut;
	}

	public String getPointeurFin() {
		return pointeurFin;
	}

	public char getCrochetDebut() {
		return crochetDebut;
	}

	public char getCrochetFin() {
		return crochetFin;
	}

	public String getCheminExemple() {
		return cheminExemple;
	}

	public MathematiqueOperation[] getOperations() {
		return operation;
	}

	public MathematiqueOperation getAttributEspece() {
		return attributEspece;
	}

}
