package org.linotte.greffon;

import org.linotte.greffons.externe.Greffon;

import java.math.BigDecimal;

public class Boulier extends Greffon {

	@Slot()
	public BigDecimal addition(BigDecimal nombre1, BigDecimal nombre2) {
		return nombre1.add(nombre2);
	}

}
