package org.linotte.greffons.java.interne.a;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.java.JavaMethod;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

public abstract class MethodeInterne extends JavaMethod {

	public MethodeInterne() {
		super(null);
	}

	public Greffon.ObjetLinotte appeler(Acteur moi, Greffon greffon, Greffon.ObjetLinotte... parametres) throws Exception {
		throw new ErreurException(Constantes.ERREUR_GREFFON);
	}

	@Override
	public String parametres() {
		return "";
	}

	public abstract String nom();

}