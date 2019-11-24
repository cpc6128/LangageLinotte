package org.linotte.moteur.xml.operation;

public enum OPERATION {
	ESPECE("espèce", false), CASIERANONYME("casier anonyme", true), POINTEUR("pointeur", true), CROCHET("crochet", true), CHEVRON("chevron", true), CARRE(
			"carré", true), COS("cosinus", true), SIN("sinus", true), LOG("logarithme", true), ABS("valeur absolue", true), LOGN("logarithme népérien", true), RACINE(
			"racine carrée", true), CUBE("cube", true), ARRONDI("arrondi", true), PUISS("puissance", true), MOD("modulo", true), CLONE("clonage", true), PARENTHESE(
			"parenthèse", true), ET("et logique", true), OU("ou logique", true), XOU("xou", true), NON("non", true), ASIN("arc sinus", true), ACOS(
			"arc cosinus", true), TAN("tangente", true), ATAN("arc tangente", true), RG("rg", true), RD("rd", true), NOP("nop", true), ENTIER("entier", true), DECIMAL(
			"décimal", true), EXP("exponentielle", true), UNICODE("unicode", true), SUP("supérieur", true), INF("inférieur", true), SUP_OU_EGAL(
			"supérieur ou égal", true), INF_OU_EGAL("inférieur ou égal", true), EGAL("égal", true), DIFF("diff", true), ASCII("ascii", true), MESSAGE(
			"message", false), HASARD("hasard", true), CHR("chr", true), TER("ter", true), NEG("NEG", true), ATAN2("arc tangente 2", true);

	private boolean operationSubtitution = false;

	private String libelle;

	OPERATION(String libelle, boolean v) {
		this.libelle = libelle;
		operationSubtitution = v;
	}

	public boolean isOperationSubtitution() {
		return operationSubtitution;
	}

	public String toString() {
		return libelle;
	}

}