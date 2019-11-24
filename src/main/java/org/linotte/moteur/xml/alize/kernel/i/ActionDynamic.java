package org.linotte.moteur.xml.alize.kernel.i;

import org.alize.kernel.AKProcessus;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.analyse.ItemXML;

public abstract class ActionDynamic extends Action {

	@Override
	public final ETAT analyse(String param, Job runtime, ItemXML[] valeurs, String[] annotations) throws Exception {
		return null;
	}

	public abstract AKProcessus analyseDynamic(String param, Job runtime, ItemXML[] valeurs, String[] annotations) throws Exception;

}
