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

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.ImageIcon;

public class Theme {

	// On stocke les images :
	Map<String, Object> images_ressource = new Hashtable<String, Object>();

	public Theme(InputStream is) throws Exception {
		load(is);
	}

	public void load(InputStream is) throws Exception {
		// Chargement de tous les fichiers
		ZipInputStream zis = new ZipInputStream(is);

		try {
			ZipEntry ze = null;
			int size = 0;
			while ((ze = zis.getNextEntry()) != null) {
				if (!ze.isDirectory()) {
					size = (int) ze.getSize();
					// Extraction du fichier :

					byte[] data = new byte[size];
					int debut = 0;
					int reste = 0;
					while ((size - debut) > 0) {
						reste = zis.read(data, debut, size - debut);
						if (reste == -1) {
							break;
						}
						debut += reste;
					}
					images_ressource.put(ze.getName(), new ImageIcon(data).getImage());
				}
			}
			zis.closeEntry();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FileNotFoundException("Impossible de lire le fichier zip :" + e.toString());
		} finally {
			try {
				is.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
	}

	public Image getImage(String nom) {
		return (Image) images_ressource.get(nom);
	}
}