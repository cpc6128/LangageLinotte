package org.linotte.moteur.xml.analyse;

import org.linotte.moteur.xml.alize.parseur.a.Noeud;

public class CloneException extends Exception {

	private static final long serialVersionUID = 1L;

	public Noeud clone;

	public CloneException(Noeud clone) {
		this.clone = clone;
	}

}
