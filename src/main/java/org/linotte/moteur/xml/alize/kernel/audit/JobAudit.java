package org.linotte.moteur.xml.alize.kernel.audit;

import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.alize.kernel.util.i.AKAudit;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;

public class JobAudit implements AKAudit {

	@Override
	public void run() {
		for (AKRuntime runtime : AKPatrol.runtimes) {
			if (!runtime.getJob().isDead()) {
				try {
					AKPatrol.LOG(this, "\t" + runtime.getJob() + " - Nombre de jobs fils = " + runtime.getJob().countSons());
				} catch (Exception e) {
					AKPatrol.LOG(this, "\t" + runtime.getJob() + " - Nombre de jobs fils, impossible à déterminer !");
					//					e.printStackTrace();
				}
				RuntimeContext runtimeContext = (RuntimeContext) runtime.getContext();
				AKPatrol.LOG(this, "\t" + runtime.getJob() + " - Nombre d'évènements = " + runtimeContext.getEvenements().size());
				AKPatrol.LOG(this, "\t" + runtime.getJob() + " - Nombre de threads lancés JAVA = " + runtimeContext.getThreads().size());
				AKPatrol.LOG(this, "\t" + runtime.getJob() + " - Nombre de composants = " + runtimeContext.getComposants().size());
				AKPatrol.LOG(this, "\t" + runtime.getJob() + " - Nombre de tubes = " + runtimeContext.getTubes().size());
				AKPatrol.LOG(this, "\t" + runtime.getJob() + " - Taille de la pile des appels = "
						+ ((JobContext) ((Job) runtime.getJob()).getContext()).getLivre().getKernelStack().size());
			}
		}
	}

}
