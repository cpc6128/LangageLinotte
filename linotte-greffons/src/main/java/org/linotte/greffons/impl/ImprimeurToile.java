package org.linotte.greffons.impl;

import org.linotte.frame.latoile.JPanelLaToile;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * http://download.oracle.com/javase/tutorial/2d/printing/printable.html
 * http://manu.kegtux.org/Java/Tutoriels/AWT/Impression.html
 * 
 * @author Ronan Mounès
 *
 */
public class ImprimeurToile implements Printable {

	private JPanelLaToile panel;

	public ImprimeurToile(JPanelLaToile panel) {
		this.panel = panel;
	}

	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {

		if (page > 0) {
			return NO_SUCH_PAGE;
		}
		Graphics2D g2d = (Graphics2D) g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());

		panel.setChangement();
		panel.paint(g2d);

		return PAGE_EXISTS;
	}
}
