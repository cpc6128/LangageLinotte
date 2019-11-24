package org.linotte.moteur.xml.actions;

import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.analyse.ItemXML;

public class SouffleurAction extends Action {

	@Override
	public ETAT analyse(String param, Job runtime, ItemXML[] valeurs, String[] annotations) throws ErreurException, StopException {
		return ETAT.PAS_DE_CHANGEMENT;
	}

	@Override
	public String clef() {
		return "souffleurs xxx";
	}

}
