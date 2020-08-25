/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 30, 2013                                *
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

package org.linotte.frame.outils;

import java.awt.*;
import java.math.BigDecimal;

/**
 * Cette classe permet de gérer les différentes gestion de transparence entre java 6 et java 7+
 *
 */
public abstract class ITransparence {

	private static ITransparence transparence;

	static {
		//String version = ManagementFactory.getRuntimeMXBean().getVmVersion(); <== retourne 1.0 !!!
		String version = System.getProperty("java.specification.version");
		// Le code ne s'exécute qu'avec une JVM plus récente que 1.6
		if (version.equals("1.6")) {
			//setTransparence(new TransparenceJava6());
		} else {
			//System.out.println("7!");
			// C'est une JVM plus récente, enfin, je l'espère !
			setTransparence(new Transparence());
		}
	}

	public static synchronized ITransparence getTransparence() {
		return transparence;
	}

	private static void setTransparence(ITransparence transparence) {
		ITransparence.transparence = transparence;
	}

	public abstract boolean isTranslucencySupported(Window w);

	public abstract boolean setOpaque(Window w, boolean opaque);

	public abstract boolean setWindowOpacity(Window w, BigDecimal alpha);

}
