package linotte.greffon;

import java.math.BigDecimal;

import org.linotte.greffons.externe.Greffon;

public class Boulier extends Greffon {

	@Slot()
	public BigDecimal addition(BigDecimal nombre1, BigDecimal nombre2) {
		return nombre1.add(nombre2);
	}

}
