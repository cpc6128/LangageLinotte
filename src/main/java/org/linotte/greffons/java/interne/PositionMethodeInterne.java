package org.linotte.greffons.java.interne;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.java.interne.a.MethodeInterneDirecte;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.ChaineOutils;

import java.math.BigDecimal;

public class PositionMethodeInterne extends MethodeInterneDirecte {

    public PositionMethodeInterne(Acteur acteur) {
        super(acteur);
    }

    @Override
    public Acteur appeler(Greffon greffon, Acteur... parametres) throws Exception {
        Acteur retour = new Acteur(Role.NOMBRE, null);
        try {
            int p1 = ((BigDecimal) parametres[0].getValeur()).intValue();
            String p2 = (String) (parametres[1].getValeur());
            String s = (String) acteur.getValeur();
            int pos = ChaineOutils.indexOfIgnoreCase(s, p2, p1);
            retour.setValeur(new BigDecimal(pos));

        } catch (java.lang.IndexOutOfBoundsException e) {
            throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
        } catch (java.lang.IllegalArgumentException e) {
            throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
        } catch (ClassCastException e) {
            throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
        }
        return retour;

    }

    @Override
    public String parametres() {
        return "( quoi )";
    }

    public String nom() {
        return "position";
    }

}