/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : May 5, 2004                                  *
 * Author : Mounès Ronan metalm@users.berlios.de                       *
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
 ******** May 6, 2004 **************************************************
 *   - Theme ajoute par drag and drop                                  *
 ***********************************************************************/

package org.linotte.frame.theme;

import org.linotte.moteur.outils.Preference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ThemeManager {

    public static final String THEME_PREFERENCE = "THEME_ICON";

    private static List<ThemeLoader> theme = new ArrayList<ThemeLoader>();
    private static ThemeManager instance = null;
    private Theme currentTheme = null;

    /**
     * @throws Exception
     */
    private ThemeManager() {
        try {
            theme.add(new ThemeLoaderFromJar("themes/Extra-Elementary.zip"));
        } catch (Exception e) {
            e.printStackTrace();
            // Ne rien faire
        }
    }

    private static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
            try {
                instance.chargerTheme();
            } catch (Exception e) {
                // Le thème ne veut pas se charger, on va prendre celui par défaut
                e.printStackTrace();
                Preference.getIntance().setProperty(THEME_PREFERENCE, "");
                try {
                    instance.chargerTheme();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
        return instance;
    }

    /**
     * @return Returns the current.
     */
    public static Theme getCurrent() {
        return getInstance().currentTheme;
    }

    private void chargerTheme() throws Exception {
        ThemeLoader tl = null;
        // Recherche du th�me dans le fichier de configuration :
        String key = Preference.getIntance().getProperty(THEME_PREFERENCE);
        // Si on ne trouve pas le th�me :
        tl = theme.get(0);
        Iterator i = theme.iterator();
        while (i.hasNext()) {
            ThemeLoader temp = (ThemeLoader) i.next();
            if (temp.getID().equals(key))
                tl = temp;
        }

        // Chargement :
        currentTheme = new Theme(tl.getInputStream());
        Preference.getIntance().setProperty(THEME_PREFERENCE, tl.getID());
        return;
    }

}