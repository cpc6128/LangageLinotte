package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Greffon;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class Police extends Greffon {

	@Slot
	public boolean charger(String nom_police) throws GreffonException {
		RessourceManager ressourceManager = getRessourceManager();

		if (ressourceManager != null)
			nom_police = ressourceManager.analyserChemin(nom_police);
		InputStream is = null;
		try {
			is = new FileInputStream(nom_police);
		} catch (FileNotFoundException e1) {
			throw new GreffonException("Impossible de trouver la police : " + nom_police);
		}

		try {
			Font font = Font.createFont(Font.TRUETYPE_FONT, is);
			GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(font);
		} catch (Exception e) {
			throw new GreffonException("Impossible de charger la police : " + nom_police + " / " + e.toString());
		}
		return true;
	}
}
