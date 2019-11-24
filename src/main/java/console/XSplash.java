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

package console;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class XSplash extends JWindow implements Runnable {

	private int delai;

	public XSplash(String chemin, int d) {
		delai = d;
		// ajoute la progress bar
		java.net.URL imgURL;
		try {
			imgURL = new File(chemin).toURI().toURL();
			// cree un label avec notre image
			JLabel image = new JLabel(new ImageIcon(imgURL));
			// ajoute le label au panel
			getContentPane().add(image, BorderLayout.NORTH);
			pack();
			// centre le splash screen
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension labelSize = image.getPreferredSize();
			setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));
			setVisible(true);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			Thread.sleep(delai);
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					setVisible(false);
					dispose();
				}
			});
		} catch (InterruptedException e) {
		} catch (InvocationTargetException e) {
		}

	}
}
