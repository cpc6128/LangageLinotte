package org.linotte.moteur.xml.alize.kernel.audit;

import org.alize.kernel.util.i.AKAuditLog;
import org.linotte.frame.Atelier;

public class LogAudit implements AKAuditLog {

	private Atelier atelier;

	public LogAudit(Atelier atelier) {
		this.atelier = atelier;
	}

	@Override
	public void print(String message) {
		atelier.ecrireSimpleAudit(message, null);
	}

	@Override
	public void init() {
		atelier.effacerAudit();
	}

}
