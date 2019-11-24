/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
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

package org.linotte.moteur.exception;

import java.util.List;

import org.linotte.moteur.outils.Chaine;

@SuppressWarnings("serial")
public class SyntaxeException extends LinotteException {

	private int erreurnumeroligne = 0;

	private List<String> phrase = null;

	public SyntaxeException(int numeroErreur, int numeroligne) {
		super(numeroErreur);
		erreurnumeroligne = numeroligne;
	}

	public SyntaxeException(int numeroErreur, List<String> p, String m, int numeroligne) {
		super(numeroErreur, m);
		this.phrase = p;
		erreurnumeroligne = numeroligne;
	}

	public SyntaxeException(int numeroErreur, String m, int numeroligne) {
		super(numeroErreur, m);
		erreurnumeroligne = numeroligne;
	}

	public SyntaxeException(int numeroErreur, Chaine m, int numeroligne) {
		super(numeroErreur, m.toString());
		erreurnumeroligne = numeroligne;
	}

	public int getErreurnumeroligne() {
		return erreurnumeroligne;
	}

	public void setPhrase(List<String> p) {
		phrase = p;
	}

	public List<String> getPhrase() {
		return phrase;
	}

}
