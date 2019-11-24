package org.linotte.moteur.xml.alize.kernel.audit;

import org.alize.kernel.AKPatrol;
import org.alize.kernel.util.i.AKAudit;
import org.linotte.moteur.xml.api.Librairie;

public class LibrairieVirtuelleAudit implements AKAudit {

	private Librairie<?> librairie;

	public LibrairieVirtuelleAudit(Librairie<?> librairie) {
		this.librairie = librairie;
	}

	@Override
	public void run() {
		AKPatrol.LOG(this, "Nombre de d'acteurs = " + librairie.getActeurs().size());

	}

}
