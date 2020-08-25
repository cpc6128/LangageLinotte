package org.linotte.moteur.xml.appels;

public interface Appel {

	enum APPEL {
		BOUCLE, CONDITION, CALQUEPARAGRAPHE, FONCTION, RING, SOUSPARAGRAPHE
	}

	APPEL getType();

}
