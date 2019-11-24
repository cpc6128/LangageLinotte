package org.linotte.moteur.xml.operation.i;

import java.math.MathContext;

public interface OperationSimple {

	Object action(Object total, Object valeur_transformee, MathContext context) throws Exception;

}
