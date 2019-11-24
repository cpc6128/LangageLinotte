package org.linotte.moteur.xml.alize.kernel;

import java.io.File;
import java.util.List;

import org.alize.kernel.AKRuntimeContextI;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;

public class ContextHelper {

	public static void populate(AKRuntimeContextI pcontext, Linotte linotte, File cheminReference, List<Habilitation> habilitations) {
		RuntimeContext context = (RuntimeContext) pcontext;
		context.setLinotte(linotte);
		context.setLibrairie(linotte.getLibrairie().cloneMoi());
		context.setIhm(linotte.getIhm());
		context.setReference(cheminReference);
		if (habilitations != null) {
			for (Habilitation habilitation : habilitations) {
				context.addHabilitation(habilitation);
			}
		}
	}

}