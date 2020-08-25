package org.linotte.greffons.java.interne.a;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

/**
 * Cette classe permet de ne pas passer par l'API des greffons.
 *
 * @author ronan.mounes@gmail.com
 */
public abstract class MethodeInterneDirecte extends MethodeInterne {

    public MethodeInterneDirecte() {
        super();
    }

    @Override
    public final ObjetLinotte appeler(Greffon greffon, final ObjetLinotte... parametres) throws Exception {
        throw new ErreurException(Constantes.ERREUR_GREFFON);
    }

    public abstract Acteur appeler(Acteur moi, Greffon greffon, final Acteur... parametres) throws Exception;

}