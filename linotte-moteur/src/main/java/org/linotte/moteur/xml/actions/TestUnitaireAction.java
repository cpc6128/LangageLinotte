package org.linotte.moteur.xml.actions;

import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.test.TestIHM;
import org.linotte.moteur.xml.analyse.ItemXML;

public class TestUnitaireAction extends Action {

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws ErreurException, StopException {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		// Doit-on effectuer des tests unitaires ?
		if (runtimeContext.doTest && runtimeContext.tests.hasNext()) {
			runtimeContext.setIhm(new TestIHM(runtimeContext.tests, runtimeContext.getIhm()));
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}

	@Override
	public String clef() {
		return "test unitaire";
	}

}
