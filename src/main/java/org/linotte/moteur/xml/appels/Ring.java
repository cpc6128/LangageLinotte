package org.linotte.moteur.xml.appels;

import org.linotte.moteur.xml.alize.kernel.processus.Processus;

public class Ring implements Appel {

	private Processus processus;

	public Ring(Processus pprocessus) {
		processus = pprocessus;
	}

	public void setProcessus(Processus processus) {
		this.processus = processus;
	}

	public Processus getProcessus() {
		return processus;
	}

	@Override
	public String toString() {
		return "CodeProtege";
	}

	@Override
	public APPEL getType() {
		return APPEL.RING;
	}
}
