package org.linotte.moteur.xml.analyse.multilangage;

import org.linotte.frame.coloration.StyleItem;
import org.linotte.moteur.xml.operation.i.Operation;

public interface MathematiqueOperation {

	String getTexte();

	Class<? extends Operation> getOperation();

	StyleItem.STYLE getStyle();

	TypeSyntaxe getTypeSyntaxe();

	MathematiqueOperation[] operations();

}