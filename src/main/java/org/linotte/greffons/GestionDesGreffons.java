/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 5, 2011                              *
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

package org.linotte.greffons;

import org.linotte.frame.latoile.LaToile;
import org.linotte.greffons.api.Greffon;
import org.linotte.greffons.externe.Graphique;
import org.linotte.greffons.java.GreffonPrototype;
import org.linotte.greffons.java.GreffonPrototype.Entrée;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.api.Librairie;

import java.math.BigDecimal;
import java.util.List;

/**
 * Chargement de greffons depuis la commande tilde greffon
 *
 * @author CPC
 */
public class GestionDesGreffons {

    /**
     * TODO ce bout de code doit être déplacé ailleur.
     *
     * @param lib
     * @param linotte
     */
    public static void initGreffons(Librairie<?> lib, Linotte linotte) {
        Acteur visible = new Acteur(null, "visible", Role.TEXTE, "non", null);
        Acteur position = new Acteur(null, "position", Role.NOMBRE, new BigDecimal(0), null);
        Acteur attributtoile = new Acteur(null, "toile", Role.TEXTE, null, null);
        final LaToile toile = null;

        // Init des greffons :
        try {
            List<Greffon> greffons = GreffonsChargeur.getInstance().getGreffons();
            for (Greffon greffon : greffons) {
                try {
                    if (greffon != null) {
                        if (greffon.getObjet() instanceof Graphique) {
                            PrototypeGraphique espece = new PrototypeGraphique(toile, lib, greffon.getId(), Role.ESPECE, greffon.getId(), null,
                                    PrototypeGraphique.TYPE_GRAPHIQUE.GREFFON, GreffonsChargeur.getInstance().newInstance(greffon.getId()));
                            if (!PrototypeGraphique.isEspecesGraphique_(greffon.getId()))
                                linotte.especeModeleMap.add(espece);
                            GreffonPrototype.chargeAttributGreffon(espece, greffon);
                            espece.addAttribut(visible);
                            espece.addAttribut(position);
                            espece.addAttribut(attributtoile);
                            // Ajout des méthodes fonctionnelles :
                            for (Entrée entrée : greffon.getSlots()) {
                                espece.ajouterSlotGreffon(entrée.getKey(), entrée.getValue());
                            }
                        } else if (greffon.getObjet() instanceof org.linotte.greffons.externe.Greffon) {
                            Prototype espece = new Prototype(lib, greffon.getId(), Role.ESPECE, greffon.getId(), null,
                                    GreffonsChargeur.getInstance().newInstance(greffon.getId()));
                            if (!PrototypeGraphique.isEspecesGraphique_(greffon.getId()))
                                linotte.especeModeleMap.add(espece);
                            GreffonPrototype.chargeAttributGreffon(espece, greffon);
                            // Ajout des méthodes fonctionnelles :
                            for (Entrée entrée : greffon.getSlots()) {
                                espece.ajouterSlotGreffon(entrée.getKey(), entrée.getValue());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (java.security.AccessControlException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
