package org.linotte.greffons.impl;

import java.util.Iterator;

import org.linotte.greffons.externe.Greffon;

public class Rss extends Greffon {
	private RSSReader reader = new RSSReader();
	private Article courant = null;
	Iterator<Article> iterateur = null;

	@Slot()
	public boolean chargerarticles(String url) throws GreffonException {
		try {
			iterateur = (reader.parse(url)).iterator();
			courant = null;
		} catch (Exception e) {
			e.printStackTrace();
			throw new GreffonException(e.getMessage());
		}
		return true;
	}

	@Slot()
	public boolean articlesuivant() throws GreffonException {
		if (iterateur.hasNext()) {
			courant = iterateur.next();
			return true;
		}
		return false;
	}

	@Slot()
	public String titre() throws GreffonException {
		if (courant != null) {
			return courant.getTitre();
		}
		return "";
	}

	@Slot()
	public String date() throws GreffonException {
		if (courant != null) {
			return courant.getDate();
		}
		return "";
	}

	@Slot()
	public String description() throws GreffonException {
		if (courant != null) {
			return courant.getDescription();
		}
		return "";
	}

	@Slot()
	public String lien() throws GreffonException {
		if (courant != null) {
			return courant.getLien();
		}
		return "";
	}

	@Slot()
	public String image() throws GreffonException {
		if (courant != null) {
			return courant.getImage();
		}
		return "";
	}
}
