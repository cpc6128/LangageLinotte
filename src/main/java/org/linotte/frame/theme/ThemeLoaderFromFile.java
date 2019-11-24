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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ThemeLoaderFromFile implements ThemeLoader {

	InputStream is = null;

	String id = null;

	/**
	 *  
	 */
	public ThemeLoaderFromFile(File fichier) {
		super();
		id = fichier.getName();
		try {
			is = new FileInputStream(fichier);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jcubitainer.display.theme.ThemeLoader#getInputStream()
	 */
	public InputStream getInputStream() {
		return is;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jcubitainer.display.theme.ThemeLoader#getID()
	 */
	public String getID() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public String getNom() {
		String nom = id;
		nom = id.substring(0, nom.lastIndexOf("."));
		return nom;
	}

}