package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Greffon;

import java.util.Calendar;

public class Horodatage extends Greffon {

	private Calendar temps = Calendar.getInstance();

	@Slot
	public long maintenant() throws GreffonException {
		temps = Calendar.getInstance();
		return temps.getTimeInMillis();
	}

	@Slot(nom = "année")
	public int annee() {
		return temps.get(Calendar.YEAR);
	}

	@Slot
	public int mois() {
		return temps.get(Calendar.MONTH) + 1;
	}

	@Slot
	public int jour() {
		return temps.get(Calendar.DAY_OF_MONTH);
	}

	@Slot
	public int heure() {
		return temps.get(Calendar.HOUR_OF_DAY);
	}

	@Slot
	public int minute() {
		return temps.get(Calendar.MINUTE);
	}

	@Slot
	public int seconde() {
		return temps.get(Calendar.SECOND);
	}

	@Slot
	public int milliseconde() {
		return temps.get(Calendar.MILLISECOND);
	}

}