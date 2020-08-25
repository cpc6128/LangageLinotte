package org.linotte.moteur.xml.appels;

import org.linotte.moteur.xml.actions.ConditionAction;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.analyse.ItemXML;

public class TantQue extends Boucle {

	private String param;
	private ItemXML[] valeurs;

	public TantQue(Processus courant, String pparam, ItemXML[] pvaleurs) {
		super(courant, null);
		param = pparam;
		valeurs = pvaleurs;
	}

	@Override
	public boolean suiteBoucle(Job job) throws Exception {
		return ConditionAction.analyseCondition(param, job, valeurs);
	}

}
