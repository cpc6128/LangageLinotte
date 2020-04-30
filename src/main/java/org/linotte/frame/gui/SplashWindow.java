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
import java.awt.*;

/**
 * "Splash window" avec barre de progression
 * http://www.labo-sun.com/resource-fr-codesamples-1126-0-java-gui-splash-screen-avec-progress-bar.htm
 */
@SuppressWarnings("serial")
public class SplashWindow extends JWindow {
    private static final String nomFichierImage = "splashlinotte.png";
    // thread pour fermer le splash screen
    private final Runnable closerRunner = new Runnable() {
        public void run() {
            setVisible(false);
            dispose();
        }
    };

    /**
     * @param parent La fenêtre parente
     */
    public SplashWindow(Frame parent) {
        super(parent);

        UIManager.put("ProgressBar.repaintInterval", new Integer(10));
        UIManager.put("ProgressBar.cycleTime", new Integer(6000));

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

    public void fermer() {
        SwingUtilities.invokeLater(closerRunner);
    }

}
