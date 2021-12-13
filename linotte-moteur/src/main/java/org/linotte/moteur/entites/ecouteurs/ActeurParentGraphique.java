/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

package org.linotte.moteur.entites.ecouteurs;

import org.linotte.frame.latoile.Couleur;
import org.linotte.greffons.externe.Graphique;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.greffons.outils.ObjetLinotteFactory;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ActeurParentGraphique extends ActeurParent {

    // Optimisation :
    private interface Traitement {
        void run(PrototypeGraphique eg, Object valeur);
    }

    private static final Map<String, Traitement> traitements = new HashMap<>();

    private Traitement run1;
    private Traitement run2;

    public ActeurParentGraphique(String caracteristique, Prototype c) {
        super(c);
        if (c instanceof PrototypeGraphique)
            initPrototypeGraphique(caracteristique, (PrototypeGraphique) c);
    }

    @Override
    public void mettreAjour(String caracteristique, Object valeur) throws ErreurException {
        if (getActeur().retourneLibrairie() != null)
            super.mettreAjour(caracteristique, valeur);
        if (getActeur().isEspeceGraphique()) {
            if (caracteristique != null) {
                PrototypeGraphique eg = (PrototypeGraphique) getActeur();
                // Greffon :
                if (eg.getGreffon() != null) {
                    Graphique graphique = (Graphique) eg.getGreffon();
                    try {
                        // L'attribut n'a pas encore été ajouté car le greffon est en cours de construction !
                        if (eg.retourneAttribut(caracteristique) != null)
                            graphique.valeurs.put(caracteristique.toLowerCase(), ObjetLinotteFactory.copy(eg.retourneAttribut(caracteristique)));
                    } catch (GreffonException e1) {
                        throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_GREFFON, e1.getMessage());
                    }
                    try {
                        graphique.fireProperty(caracteristique.toLowerCase());
                    } catch (GreffonException e) {
                        e.printStackTrace();
                    }
                }
                eg.setModifie(true);
                if (run1 != null)
                    run1.run(eg, valeur);
                if (valeur != null && run2 != null) {
                    try {
                        run2.run(eg, valeur);
                    } catch (java.lang.ClassCastException e) {
                        throw new ErreurException(Constantes.SYNTAXE_MINI_FORMULE, "" + valeur);
                        //
                        // On affiche pas d'erreur, c'est de l'optimisation
                        //e.printStackTrace();
                    }
                }
                if (eg.getToile() != null)
                    eg.getToile().getPanelLaToile().setChangement();
            }
        } else if (((Prototype) getActeur()).getGreffon() != null) {
            Prototype eg = (Prototype) getActeur();
            Greffon greffon = eg.getGreffon();
            try {
                // L'attribut n'a pas encore été ajouté car le greffon est en cours de construction !
                if (eg.retourneAttribut(caracteristique) != null)
                    greffon.valeurs.put(caracteristique.toLowerCase(), ObjetLinotteFactory.copy(eg.retourneAttribut(caracteristique)));
            } catch (GreffonException e1) {
                throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_GREFFON, e1.getMessage());
            }
            // On prévient
            try {
                greffon.fireProperty(caracteristique.toLowerCase());
            } catch (GreffonException e) {
                e.printStackTrace();
            }
        }
    }

    private void initPrototypeGraphique(String caracteristique, PrototypeGraphique c) {
        run2 = traitements.get(caracteristique);
        if ((c.getTypeGraphique() == PrototypeGraphique.TYPE_GRAPHIQUE.POLYGONE || c.getTypeGraphique() == PrototypeGraphique.TYPE_GRAPHIQUE.CHEMIN)
                && (caracteristique.startsWith("d") || caracteristique.equals("x") || caracteristique.equals("y"))) {
            run1 = traitements.get("run1");
        }
    }

    static {
        traitements.put("run1", (eg, valeur) -> eg.setObject(null));
        traitements.put("x", (eg, valeur) -> eg.x = ((BigDecimal) valeur).doubleValue());
        traitements.put("y", (eg, valeur) -> eg.y = ((BigDecimal) valeur).doubleValue());
        traitements.put("taille", (eg, valeur) -> eg.taille = ((BigDecimal) valeur).floatValue());
        traitements.put("transparence", (eg, valeur) -> eg.transparence = ((BigDecimal) valeur).intValue() / 100f);
        traitements.put("couleur", (eg, valeur) -> eg.couleur = Couleur.retourneCouleur((String) valeur));
        traitements.put("rayon", (eg, valeur) -> eg.rayon = ((BigDecimal) valeur).doubleValue());
        traitements.put("plein", (eg, valeur) -> eg.plein = ((BigDecimal) valeur).intValue() == 1);
        traitements.put("visible", (eg, valeur) -> eg.visible = ((BigDecimal) valeur).intValue() == 1);
        traitements.put("x1", (eg, valeur) -> eg.x1 = ((BigDecimal) valeur).doubleValue());
        traitements.put("y1", (eg, valeur) -> eg.y1 = ((BigDecimal) valeur).doubleValue());
        traitements.put("x2", (eg, valeur) -> eg.x2 = ((BigDecimal) valeur).doubleValue());
        traitements.put("y2", (eg, valeur) -> eg.y2 = ((BigDecimal) valeur).doubleValue());
        traitements.put("angle", (eg, valeur) -> eg.angle = ((BigDecimal) valeur).doubleValue());
        traitements.put("hauteur", (eg, valeur) -> eg.hauteur = ((BigDecimal) valeur).doubleValue());
        traitements.put("largeur", (eg, valeur) -> eg.largeur = ((BigDecimal) valeur).doubleValue());
        traitements.put("image", (eg, valeur) -> eg.image = (String) valeur);
        traitements.put("pointe", (eg, valeur) -> eg.pointe = ((BigDecimal) valeur).intValue() == 1);
        traitements.put("posé", (eg, valeur) -> eg.pose = ((BigDecimal) valeur).intValue() == 1);
        traitements.put("collision", (eg, valeur) -> eg.collision = ((BigDecimal) valeur).intValue() == 1);
        traitements.put("texte", (eg, valeur) -> eg.texte = (String) valeur);
        traitements.put("police", (eg, valeur) -> eg.police = (String) valeur);
        traitements.put("position", (eg, valeur) -> {
            if (eg.getToile() != null) {
                boolean affiche = eg.getToile().getPanelLaToile().isAffiche(eg);
                if (affiche)
                    eg.getToile().getPanelLaToile().effacer(eg);
                eg.position = ((BigDecimal) valeur).intValue();
                if (affiche)
                    eg.getToile().getPanelLaToile().addActeursAAfficher(eg, false);
            } else {
                eg.position = ((BigDecimal) valeur).intValue();
            }
        });
        traitements.put("trame", (eg, valeur) -> eg.trame = ((BigDecimal) valeur).intValue());
        traitements.put("texture", (eg, valeur) -> eg.texture = (String) valeur);
    }

}
