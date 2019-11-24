package org.linotte.moteur.xml.alize;

import java.util.HashMap;
import java.util.Map;

import org.linotte.moteur.xml.alize.kernel.processus.Processus;

public class ParserEnvironnement {

	private Map<String, Processus> paragraphes = new HashMap<String, Processus>();

	private Map<String, Map<String, Processus>> methodesFonctionnelles = new HashMap<String, Map<String, Processus>>();

	private Map<Processus, Processus> sousParagraphes = new HashMap<Processus, Processus>();

	public Processus getParagraphe(String key) {
		return paragraphes.get(key);
	}

	public void setParagraphes(String s, Processus p) {
		this.paragraphes.put(s.toLowerCase(), p);
	}

	public Map<Processus, Processus> getSousParagraphes() {
		return sousParagraphes;
	}

	public void setSousParagraphes(Map<Processus, Processus> sousParagraphes) {
		this.sousParagraphes = sousParagraphes;
	}

	public int nbParagraphes() {
		return paragraphes.size();
	}

	public Iterable<Processus> paragraphes() {
		return paragraphes.values();
	}

	public void addParagraphe(String prototype, String methode, Processus p) {
		Map<String, Processus> liste = methodesFonctionnelles.get(prototype);
		if (liste == null) {
			liste = new HashMap<String, Processus>();
			methodesFonctionnelles.put(prototype, liste);
		}
		liste.put(methode.toLowerCase(), p);
	}

	public Map<String, Processus> getParagraphesPourPrototype(String prototype) {
		return methodesFonctionnelles.get(prototype);
	}

}
