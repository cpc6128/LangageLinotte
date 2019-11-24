package org.linotte.moteur.xml.alize.kernel.audit;

import org.alize.kernel.AKPatrol;
import org.alize.kernel.util.i.AKAudit;
import org.linotte.frame.moteur.FrameProcess;

public class ChronoAudit implements AKAudit {

	@Override
	public void run() {
		long chrono;
		if (FrameProcess.t1 == 0) {
			if (FrameProcess.t2 != 0) {
				chrono = FrameProcess.t2;
			} else {
				chrono = 0;
			}
		} else {
			if (FrameProcess.t2 != 0) {
				chrono = FrameProcess.t2;
			} else {
				chrono = System.currentTimeMillis() - FrameProcess.t1;
			}
		}
		AKPatrol.LOG(this, "Chronomètre = " + chrono + " ms");
	}
}
