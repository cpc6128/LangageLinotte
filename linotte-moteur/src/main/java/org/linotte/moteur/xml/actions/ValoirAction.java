/***********************************************************************
 * Linotte                                                             *
 * Version release date : Febrary 04, 2008                             *
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

package org.linotte.moteur.xml.actions;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.RoleException;
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ValoirAction extends Action implements IProduitCartesien {

    public ValoirAction() {
        super();
    }

    @Override
    public String clef() {
        return "verbe valoir";
    }

    @Override
    public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {
        // linotte.debug("on copie == ...");

        Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_VALOIR, linotte, valeurs);
        Acteur acteurCible = acteurs[0];
        Acteur acteurSource = acteurs[1];

        // Linotte 4 :
        if (acteurCible.getRole() == Role.INCONNU)
            acteurCible.forceRole(acteurSource.getRole());

        if (acteurSource.getRole() == Role.NOMBRE && acteurCible.getRole() == Role.TEXTE) {
            // On peut copier un nombre dans un texte
            acteurCible.setValeur(acteurSource.getValeur().toString());
        } else if (acteurSource.getRole() == Role.ESPECE && acteurCible.getRole() == Role.ESPECE
                && ((Prototype) acteurSource).getType().equals(((Prototype) acteurCible).getType())) {
            Prototype e1 = (Prototype) acteurSource;
            Prototype e2 = (Prototype) acteurCible;
            e2.copier(e1);
        } else {

            if (acteurSource.getRole() != acteurCible.getRole())
                throw new RoleException(Constantes.SYNTAXE_PARAMETRE_VALOIR, acteurSource.toString(), acteurCible.getRole(), acteurSource.getRole());

            if (acteurSource.getRole() == Role.CASIER) {
                if (!(((Casier) acteurCible).roleContenant() == ((Casier) acteurSource).roleContenant()))
                    throw new RoleException(Constantes.SYNTAXE_PARAMETRE_VALOIR, acteurSource.toString(), ((Casier) acteurCible).roleContenant(),
                            ((Casier) acteurSource).roleContenant());
                ((Casier) acteurCible).toutEffacer();
                try {
                    ((Casier) acteurCible).addAll((Casier) acteurSource.clone());
                } catch (SyntaxeException e) {
                    e.printStackTrace();
                }
            } else
                acteurCible.setValeur(acteurSource.getValeur());
        }
        return ETAT.PAS_DE_CHANGEMENT;
    }

}
