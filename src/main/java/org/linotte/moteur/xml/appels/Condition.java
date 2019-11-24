package org.linotte.moteur.xml.appels;

public class Condition implements Appel {

	public static enum ETAT_CONDITION {
		EN_COURS, FAUX, VRAI, TERMINE
	};

	private ETAT_CONDITION etat = ETAT_CONDITION.EN_COURS;

	public void setEtat(ETAT_CONDITION etat) {
		this.etat = etat;
	}

	public ETAT_CONDITION getEtat() {
		return etat;
	}

	@Override
	public String toString() {
		return "Condition";
	}

	@Override
	public APPEL getType() {
		return APPEL.CONDITION;
	}

}
