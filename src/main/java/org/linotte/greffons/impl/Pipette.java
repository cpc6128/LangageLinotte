package org.linotte.greffons.impl;

import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.externe.Greffon;

public class Pipette extends Greffon {

	/**
	 * @return
	 * @throws GreffonException
	 */
	@Slot(nom = "retournerouge")
	public int retourneRouge(int x, int y) throws GreffonException {
		try {
			int clr = getColor(x, y);
			int red = (clr & 0x00ff0000) >> 16;
			return red;
		} catch (Exception e) {
			throw new GreffonException("Pfff, la pipette ne fonctionne pas !");
		}
	}

	/**
	 * @return
	 * @throws GreffonException
	 */
	@Slot(nom = "retournevert")
	public int retourneVert(int x, int y) throws GreffonException {
		try {
			int clr = getColor(x, y);
			int green = (clr & 0x0000ff00) >> 8;
			return green;
		} catch (Exception e) {
			throw new GreffonException("Pfff, la pipette ne fonctionne pas !");
		}
	}

	/**
	 * @return
	 * @throws GreffonException
	 */
	@Slot(nom = "retournebleu")
	public int retourneBleu(int x, int y) throws GreffonException {
		try {
			int clr = getColor(x, y);
			int blue = clr & 0x000000ff;
			return blue;
		} catch (Exception e) {
			throw new GreffonException("Pfff, la pipette ne fonctionne pas !");
		}
	}

	/**
	 * @return
	 * @throws GreffonException
	 */
	@Slot(nom = "retournecouleur")
	public String retourneCouleur(int x, int y) throws GreffonException {
		try {
			int clr = getColor(x, y);
			int red = (clr & 0x00ff0000) >> 16;
			int green = (clr & 0x0000ff00) >> 8;
			int blue = clr & 0x000000ff;
			return red + " " + green + " " + blue;
		} catch (Exception e) {
			throw new GreffonException("Pfff, la pipette ne fonctionne pas !");
		}
	}

	/**
	 * @param x
	 * @param y
	 * @return
	 */
	private int getColor(int x, int y) {
		int clr = 0;
		try {
			clr = LinotteFacade.getToile().getPanelLaToile().getColor(x, y).getRGB();
		} catch (GreffonException e) {
			e.printStackTrace();
		}
		return clr;
	}
}
