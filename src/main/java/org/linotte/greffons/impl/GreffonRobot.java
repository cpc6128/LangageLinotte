package org.linotte.greffons.impl;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import org.linotte.greffons.externe.Greffon;

/**
 * http://www.fobec.com/CMS/java/sources/deplacer-curseur-ecran-avec-awt-robot_969.html
 *
 */
public class GreffonRobot extends Greffon {

	private Robot robot = null;

	public GreffonRobot() throws AWTException {
		robot = new Robot();
		/**
		 * Fixer le delai entre chaque mouvement à 500 ms
		 */
		robot.setAutoDelay(50);
		/**
		 * Appeler OnIdle après le déplacement de la souris
		 */
		robot.setAutoWaitForIdle(false);
	}

	@Slot
	public boolean bougesouris(int x, int y) {
		if (robot != null) {
			robot.mouseMove(x, y);
			return true;
		}
		return false;
	}

	@Slot
	public boolean sourisclic() {
		if (robot != null) {
			robot.mousePress(InputEvent.BUTTON1_MASK);
			robot.mouseRelease(InputEvent.BUTTON1_MASK);
			return true;
		}
		return false;
	}

}
