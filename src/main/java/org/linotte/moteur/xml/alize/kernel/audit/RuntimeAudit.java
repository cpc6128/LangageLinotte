package org.linotte.moteur.xml.alize.kernel.audit;

import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.alize.kernel.util.i.AKAudit;

public class RuntimeAudit implements AKAudit {

	@Override
	public void run() {
		AKPatrol.LOG(this, "Nombre de runtimes = " + AKPatrol.runtimes.size());
		for (AKRuntime run : AKPatrol.runtimes) {
			AKPatrol.LOG(this, "job = " + run.getJob());
		}
	}
}
