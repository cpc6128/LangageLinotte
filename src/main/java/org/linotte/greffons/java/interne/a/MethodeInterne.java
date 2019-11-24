package org.linotte.greffons.java.interne.a;

import org.linotte.greffons.java.JavaMethod;
import org.linotte.moteur.entites.Acteur;

public abstract class MethodeInterne extends JavaMethod {

	protected Acteur acteur;

	public MethodeInterne(Acteur pacteur) {
		super(null);
		acteur = pacteur;
	}

	@Override
	public String parametres() {
		return "";
	}

	public abstract String nom();

}