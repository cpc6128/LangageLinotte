/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                        *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                            *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.exception;

public class InconnuException extends FinException {

	int messageErreur;
	int position;

	private static final long serialVersionUID = -959399439101211781L;

	public InconnuException(int erreur, int position, String token) {
		super();
		this.position = position;
		this.erreur = erreur;
		this.message = token;
	}

	public int getMessageErreur() {
		return messageErreur;
	}

	public int getPosition() {
		return position;
	}

}
