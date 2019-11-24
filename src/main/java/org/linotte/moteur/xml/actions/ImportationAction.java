package org.linotte.moteur.xml.actions;

import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ImportationAction extends Action implements ActionDispatcher {

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws ErreurException, StopException {
		/**
		 * On s'arrête ici si un est dans un état ImportationProcess
		 */
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		if (runtimeContext.isImportationProcess()) {
			return ETAT.FIN_IMPORTATION;
		} else {
			return ETAT.PAS_DE_CHANGEMENT;
		}
	}

	@Override
	public String clef() {
		return "fin importation";
	}

}
