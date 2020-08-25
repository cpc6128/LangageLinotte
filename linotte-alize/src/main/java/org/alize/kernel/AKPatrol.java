/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 01, 2010                                *
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

package org.alize.kernel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Cette classe fait partie de la réécriture du moteur de Linotte.
 * Le moteur Alizé doit alléger le code de Linotte, booster la vitesse de l'interprète<br>
 * <br>
 * AlizeRuntime est le moteur d'exécution des états (analyse sémantique).<br>
 * Il ne doit plus contenir de grammaire.
 * Il est produit par MoteurXML (analyse syntaxique)
 */
public final class AKPatrol {

    public static boolean active = false;
    public static List<AKRuntime> runtimes = new ArrayList<AKRuntime>();
    private static AKPatrol akPatrol;
    private Timer timer = null;

    static void addRuntime(AKRuntime runtime) {
        getPatrol();
        AKPatrol.runtimes.add(runtime);
    }

    private static synchronized void getPatrol() {
        if (akPatrol == null) {
            akPatrol = new AKPatrol();
        }
    }

}
