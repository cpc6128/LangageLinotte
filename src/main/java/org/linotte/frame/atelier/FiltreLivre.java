/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                             *
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

package org.linotte.frame.atelier;

import java.io.File;

import javax.swing.filechooser.FileFilter;

// http://brassens.upmf-grenoble.fr/IMSS/dciss/Enseignements/PSR/Prog/Java/dialogueFichier.htm

public class FiltreLivre extends FileFilter {

	String[] lesSuffixes;

	String laDescription;

	public FiltreLivre(String[] lesSuffixes, String laDescription) {

		this.lesSuffixes = lesSuffixes;

		this.laDescription = laDescription;

	}

	boolean appartient(String suffixe) {

		for (int i = 0; i < lesSuffixes.length; ++i) {

			if (suffixe.equals(lesSuffixes[i]))
				return true;

		}

		return false;

	}

	@Override
	public boolean accept(File f) {

		if (f.isDirectory()) {

			return true;

		}

		String suffixe = null;

		String s = f.getName();

		int i = s.lastIndexOf('.');

		if (i > 0 && i < s.length() - 1) {

			suffixe = s.substring(i + 1).toLowerCase();

		}

		return suffixe != null && appartient(suffixe);

	}

	// la description du filtre

	@Override
	public String getDescription() {

		return laDescription;

	}

}
