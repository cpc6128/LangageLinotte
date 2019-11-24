package org.linotte.moteur.xml.alize.parseur.a;

import org.linotte.moteur.exception.FinException;

public interface Lexer {

	String motSuivant() throws FinException;

	String resteLigne() throws FinException;

	String getMot();

	int getPosition();

	void setPosition(int pos) throws FinException;

	boolean isFinDeLigne();

	int getLastPosition();

	void retour();

	void setFaireExeptionFinDeLigne(boolean faireExeptionFinDeLigne);

	String subString(int start, int end);

	int getLigneCourante();

	int getPositionAvantCommentaire();

	boolean isFaireExeptionFinDeLigne();

}