package org.linotte.moteur.xml.alize.kernel.audit;

import org.alize.kernel.AKPatrol;
import org.alize.kernel.util.i.AKAudit;
import org.linotte.frame.latoile.LaToile;

public class ToileAudit implements AKAudit {

	private LaToile toile;

	public ToileAudit(LaToile toile) {
		this.toile = toile;
	}

	@Override
	public void run() {
		AKPatrol.LOG(this, "Nombre d'acteurs sur la toile = " + toile.getPanelLaToile().getActeursAAfficher().size());
	}

}
