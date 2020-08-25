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

@SuppressWarnings("serial")
public class LectureException extends LinotteException {

	private int position = 0;

	private LinotteException je = null;

	private String livre = null;

	public LectureException(LinotteException e, int pos) {
		super(e.getErreur(), e.getMessage());
		je = e;
		position = pos;
		setKernelStack(e.getKernelStack());
	}

	public LectureException(LinotteException e, int pos, String livre) {
		super(e.getErreur(), e.getMessage());
		this.je = e;
		position = pos;
		this.livre = livre;
		setKernelStack(e.getKernelStack());
	}

	public String getLivre() {
		return livre;
	}

	public LinotteException getException() {
		return je;
	}

	@Override
	public LinotteException getCause() {
		return je;
	}

	public int getPosition() {
		return position;
	}

	public void forcePosition(int position) {
		this.position = position;
	}

}
