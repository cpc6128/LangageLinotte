package org.linotte.moteur.xml.alize.kernel;

import org.alize.kernel.AKRuntimeContextI;
import org.alize.security.Habilitation;
import org.linotte.moteur.xml.Linotte;

import java.io.File;
import java.util.List;

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