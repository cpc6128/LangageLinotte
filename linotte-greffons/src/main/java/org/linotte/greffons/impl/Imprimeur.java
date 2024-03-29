package org.linotte.greffons.impl;

import org.linotte.frame.latoile.JPanelLaToile;
import org.linotte.frame.latoile.LaToile;
import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.externe.Greffon;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class Imprimeur extends Greffon {

	@Slot(nom = "imprimertoile")
	public boolean imprimerToile() throws GreffonException {
		try {
			LaToile toile = LinotteFacade.getToile();
			JPanelLaToile panel = toile.getPanelLaToile();
			ImprimeurToile imprimeurToile = new ImprimeurToile(panel);

			PrinterJob job = PrinterJob.getPrinterJob();
			PageFormat mPageFormat = job.defaultPage();

			job.setPrintable(imprimeurToile, mPageFormat);

			boolean doPrint = job.printDialog();

			if (doPrint) {
				try {
					Cursor cursor = panel.getCursor();
					panel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					job.print();
					panel.setCursor(cursor);
				} catch (PrinterException e) {
					e.printStackTrace();
					return false;
				}
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new GreffonException("Impossible d'imprimer la toile");
		}
	}
}
