/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
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

package org.linotte.implementations;

import org.linotte.moteur.entites.Role;
import org.linotte.moteur.xml.api.IHM;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;

public class ConsoleTexte implements IHM {

    Console console = System.console(); // Mode standard
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));// A utiliser si console est null

    static boolean debug = true;

    public boolean debug(String parametre) {
        if (debug)
            System.err.println(parametre);
        return true;
    }

    public String questionne(String message, Role type, String acteur) {
        afficher(message, type);
        return demander(type, acteur);
    }

    public String demander(Role type, String acteur) {
        String message;
        try {
            if (type == Role.NOMBRE) {
                BigDecimal result = null;
                do {
                    message = demanderProxy(">"); //in.readLine();
                    try {
                        result = new BigDecimal(message);
                    } catch (NumberFormatException e1) {
                        ecrireProxy("!Nombre non valide!");
                    }
                } while (result == null);
                message = result.toString();
            } else
                message = demanderProxy(">");// in.readLine();
        } catch (Exception e) {
            message = "0";
        }
        return message;
    }

    public boolean afficher(String afficher, Role type) {
        ecrireProxy(afficher);
        return true;
    }

    public static void setDEBUG(boolean b) {
        debug = b;
    }

    public boolean effacer() {
        return false;
    }

    public boolean afficherErreur(String afficher) {
        System.err.println(afficher);
        return true;
    }

    private void ecrireProxy(String afficher) {
        if (console != null)
            console.printf(afficher + "\n");
        else
            System.out.println(afficher);
    }

    private String demanderProxy(String prompt) throws IOException {
        if (console != null)
            return console.readLine(prompt);
        else
            return in.readLine();
    }

}
