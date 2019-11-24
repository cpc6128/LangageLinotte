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

package org.linotte.frame.latoile;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Version;

public class IconSysTray {

	private static TrayIcon trayIcon;

	public static void init() {

		if (!SystemTray.isSupported()) {
			System.out.println("SystemTray is not supported");
			return;
		}

		PopupMenu popup = new PopupMenu();
		trayIcon = new TrayIcon(Ressources.getImageIcon("linotte-tray.png").getImage());
		trayIcon.setImageAutoSize(true);
		trayIcon.setToolTip("Une linotte est dans votre ordinateur...");
		MenuItem exitItem = new MenuItem("Quitter Linotte");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				System.exit(0);
			};
		});
		MenuItem aproposItem = new MenuItem("A propos");
		aproposItem.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Linotte " + Version.getVersion());
			};
		});
		popup.add(aproposItem);
		popup.add(exitItem);

		trayIcon.setPopupMenu(popup);

		SystemTray tray = SystemTray.getSystemTray();
		try {
			tray.add(trayIcon);
		} catch (AWTException e) {
			System.out.println("TrayIcon could not be added.");
		}

	}

	public static void setTitre(String titre) {
		if (trayIcon != null)
			trayIcon.setToolTip("Lecture du livre " + titre);
	}

}
