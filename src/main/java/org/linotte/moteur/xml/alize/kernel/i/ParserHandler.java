package org.linotte.moteur.xml.alize.kernel.i;

import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;

public interface ParserHandler {

	void postAnalyse(Processus process, ParserEnvironnement context) throws Exception;

}
