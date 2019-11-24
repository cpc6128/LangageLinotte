package org.linotte.moteur.xml.alize.kernel.processus;

import org.alize.kernel.AKJob;
import org.alize.kernel.AKProcessus;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Action.ETAT;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.Trace;

/**
 * 
 * Attention, un processus peut être partagé par plusieurs jobs
 * 
 * @author ronan
 *
 */
public final class ProcessusSimple extends Processus {

	public ProcessusSimple(Action petat, String pparam, Object pvaleurs, String[] pannontations, int pligne) {
		super(petat, pparam, pvaleurs, pannontations, pligne, false);
	}

	@Override
	public AKProcessus execute(AKJob job) throws Exception {
		etat.analyse(param, (Job) job, valeurs, annotations);
		if (Trace.active)
			Trace.getInstance().debug(getPosition(), etat, (Job) job, valeurs, ETAT.PAS_DE_CHANGEMENT.toString());
		return getNextProcess();
	}

}
