/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : May 5, 2004                                  *
 * Author : Moun�s Ronan metalm@users.berlios.de                       *
 *                                                                     *
 *     http://jcubitainer.berlios.de/                                  *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 * metalm@users.berlios.de                                             *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

/* History & changes **************************************************
 *                                                                     *
 ******** May 5, 2004 **************************************************
 *   - First release                                                   *
 ***********************************************************************/

package org.linotte.frame.theme;

import org.linotte.moteur.outils.Ressources;

import java.io.InputStream;

public class ThemeLoaderFromJar implements ThemeLoader {

	InputStream is = null;

	String id = null;

	/**
	 * @throws Exception
	 * 
	 */
	public ThemeLoaderFromJar(String location) throws Exception {
		is = Ressources.getFromRessources(location);
		if (is == null)
			throw new Exception("jar inexistant !");
		id = location;
	}

	public InputStream getInputStream() {
		return is;
	}

	public String getID() {
		return id;
	}

	@Override
	public String getNom() {
		String nom = id;
		nom = id.substring(0, nom.lastIndexOf("."));
		nom = nom.substring(nom.lastIndexOf("/") + 1);
		return nom;
	}

}