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

package org.linotte.frame.gui;

import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

/**
 * "Splash window" avec barre de progression
 * http://www.labo-sun.com/resource-fr-codesamples-1126-0-java-gui-splash-screen-avec-progress-bar.htm
 */
@SuppressWarnings("serial")
public class SplashWindow extends JWindow {
	private JProgressBar progressBar = null;
	private static JLabel jMessage = null;
	private int maxValue = 0;

	/**
	 * 
	 * @param nomFichierImage Le nom de l'image affichée en arrière plan
	 * @param parent La fenêtre parente
	 * @param intProgressMaxValue La valeur max de la barre de progression
	 */
	public SplashWindow(String nomFichierImage, Frame parent, int intProgressMaxValue) {
		super(parent);

		// initialise la valeur a laquelle le splash screen doit etre fermé
		this.maxValue = intProgressMaxValue;

		//System.out.println(UIManager.get("ProgressBar.repaintInterval"));
		//System.out.println(UIManager.get("ProgressBar.cycleTime"));

		UIManager.put("ProgressBar.repaintInterval", new Integer(10));
		UIManager.put("ProgressBar.cycleTime", new Integer(6000));

		// ajoute la progress bar
		progressBar = new JProgressBar(0, intProgressMaxValue);
		progressBar.setBorderPainted(false);
		progressBar.setBackground(new Color(63, 72, 204));
		progressBar.setUI(new CPCProgressUI());
		progressBar.setIndeterminate(true);

		jMessage = new JLabel("Initialisation de l'environnement");
		//getContentPane().add(jMessage, BorderLayout.CENTER);
		getContentPane().add(progressBar, BorderLayout.SOUTH);

		// crée un label avec notre image
		JLabel image = new JLabel(Ressources.getImageIcon(nomFichierImage));
		// ajoute le label au panel
		getContentPane().add(image, BorderLayout.NORTH);
		pack();

		// centre le splash screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension labelSize = image.getPreferredSize();
		setLocation(screenSize.width / 2 - (labelSize.width / 2), screenSize.height / 2 - (labelSize.height / 2));

		setVisible(!Preference.getIntance().getBoolean(Preference.P_NO_SPLASH));
	}

	/** Change le texte affiché.
	 * 
	 * @param message Le nouveau texte
	 */
	public static void setProgressValue(String message) {
	}

	/** Change la valeur de la barre de progression, et le texte affiché.
	 * 
	 * @param valeur Entier compris entre 0 et la valeur maximale 
	 * @param message
	 */
	public void setProgressValue(int valeur, String message) {
		// if (Version.isBeta())
		// System.out.println(message);
		// progressBar.setValue(5);
		// jMessage.setText(message);
		// si est arrivé a la valeur max : ferme le splash screen en lancant le
		// thread
		if (valeur >= maxValue) {
			try {
				SwingUtilities.invokeAndWait(closerRunner);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	// thread pour fermer le splash screen
	private final Runnable closerRunner = new Runnable() {
		public void run() {
			setVisible(false);
			dispose();
		}
	};

	private static class CPCProgressUI extends BasicProgressBarUI {

		private Rectangle r = new Rectangle();

		@Override
		protected void paintIndeterminate(Graphics g, JComponent c) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			r = getBox(r);
			g.setColor(new Color(255, 255, 255, 255));
			g.fillRect(r.x, r.y, r.width, 2);
		}
	}

}
