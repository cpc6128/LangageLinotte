package org.linotte.greffons.api;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;

public interface AKMethod {

	ObjetLinotte appeler(Greffon greffon, ObjetLinotte... parametres) throws Exception;

	String parametres();

}
