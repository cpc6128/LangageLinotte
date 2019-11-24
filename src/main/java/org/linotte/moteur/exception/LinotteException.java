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

import org.linotte.moteur.xml.alize.kernel.KernelStack;

@SuppressWarnings("serial")
public abstract class LinotteException extends Exception {

	private KernelStack kernelStack;

	protected int erreur;
	protected String message;

	public LinotteException(int numeroErreur) {
		super();
		erreur = numeroErreur;
	}

	public LinotteException(int numeroErreur, String m) {
		super(m);
		erreur = numeroErreur;
		message = m;
	}

	public int getErreur() {
		return erreur;
	}

	public String getToken() {
		return message;
	}

	public KernelStack getKernelStack() {
		return kernelStack;
	}

	public void setKernelStack(KernelStack kernelStack) {
		this.kernelStack = kernelStack;
	}

}
