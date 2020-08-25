package org.linotte.moteur.xml.alize.kernel.processus;

import org.alize.kernel.AKJob;
import org.alize.kernel.AKProcessus;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.Trace;
import org.linotte.moteur.xml.alize.kernel.i.ActionDynamic;
import org.linotte.moteur.xml.analyse.ItemXML;

/**
 * 
 * Attention, un processus peut être partagé par plusieurs jobs
 * 
 * @author ronan
 *
 */
public class ProcessusDynamic extends ProcessusDispatcher {

	public ProcessusDynamic(Action petat, String pparam, Object pvaleurs, String[] pannontations, int pligne, boolean pcartesien) {
		super(petat, pparam, pvaleurs, pannontations, pligne, pcartesien);
	}

	@Override
	public AKProcessus execute(AKJob job) throws Exception {
		AKProcessus retour = null;
		if (produitCartesien) {
			for (ItemXML[] ligne : matrice) {
				retour = ((ActionDynamic) etat).analyseDynamic(param, (Job) job, ligne, annotations);
				if (Trace.active)
					Trace.getInstance().debug(getPosition(), etat, (Job) job, valeurs, retour.toString());
			}
			return retour;
		} else {
			retour = ((ActionDynamic) etat).analyseDynamic(param, (Job) job, valeurs, annotations);
			if (Trace.active)
				Trace.getInstance().debug(getPosition(), etat, (Job) job, valeurs, retour == null ? null : retour.toString());
			return retour;
		}
	}
}
