package org.linotte.moteur.xml.alize.test;

import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.api.IHM;

public class TestIHM implements IHM {

	private Tests tests;
	private IHM ihmpere;

	public TestIHM(Tests tests, IHM ihmpere) {
		this.tests = tests;
		this.ihmpere = ihmpere;
	}

	@Override
	public boolean afficher(String arg0, Role arg1) throws StopException {
		if (tests.hasNext()) {
			Test test = tests.next();
			if (test instanceof TestOut) {
				if (arg0.equals(test.valeur)) {
					ihmpere.afficher("Le test n° " + test.numero + " a réussi : Afficher \"" + test.valeur + "\"", Role.TEXTE);
					return true;
				} else if (test.valeur.equals("*")) {
					ihmpere.afficher("Le test n° " + test.numero + " a réussi : Afficher \"" + arg0 + "\"", Role.TEXTE);
					return true;
				} else {
					afficherErreur("Le test n° " + test.numero + " a échoué : la valeur ne correspond pas \"" + test.valeur + "\"");
					throw new StopException();
				}
			} else {
				afficherErreur("Le test n° " + test.numero + " a échoué : impossible d'afficher \"" + test.valeur + "\"");
				throw new StopException();
			}
		} else {
			afficherErreur("Les tests ont échoués : plus de test pour le verbe afficher");
			throw new StopException();
		}
	}

	@Override
	public boolean afficherErreur(String arg0) {
		return ihmpere.afficherErreur(arg0);
	}

	@Override
	public String demander(Role arg0, String arg1) throws StopException {
		if (tests.hasNext()) {
			Test test = tests.next();
			if (test instanceof TestIn) {
				ihmpere.afficher("Le test n° " + test.numero + " a réussi : Demander \"" + test.valeur + "\"", Role.TEXTE);
				return test.valeur;
			} else {
				afficherErreur("Le test n° " + test.numero + " a échoué : impossible de demander \"" + test.valeur + "\"");
				throw new StopException();
			}
		} else {
			afficherErreur("Les tests ont échoués : plus de test pour le verbe afficher");
			throw new StopException();
		}
	}

	@Override
	public boolean effacer() throws StopException {
		return false;
	}

	@Override
	public String questionne(String arg0, Role arg1, String arg2) throws StopException {
		afficher(arg0, Role.TEXTE);
		return demander(arg1, arg2);
	}

}