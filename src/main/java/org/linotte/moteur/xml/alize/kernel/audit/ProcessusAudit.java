package org.linotte.moteur.xml.alize.kernel.audit;

import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.alize.kernel.util.i.AKAudit;
import org.linotte.moteur.xml.alize.kernel.Job;

public class ProcessusAudit implements AKAudit {

	@Override
	public void run() {
		for (AKRuntime runtime : AKPatrol.runtimes) {
			if (!runtime.getJob().isDead()) {
				AKPatrol.LOG(this, runtime.getJob() + " - Action courante = " + ((Job) runtime.getJob()).getCurrentProcessus());
			}
		}
	}

}
