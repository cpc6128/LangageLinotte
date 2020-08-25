package org.linotte.greffons.impl;

import com.itextpdf.text.pdf.codec.Base64;
import org.linotte.frame.latoile.JPanelLaToile;
import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.latoile.Toile;
import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.externe.Greffon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class Webonotte extends Greffon {

	@Slot(nom = "toileenbase64")
	public String toileenbase64() throws GreffonException {
		try {
			LaToile toile = LinotteFacade.getToile(Toile.TOILEWEB);
			JPanelLaToile panel = toile.getPanelLaToile();
			BufferedImage br;
			br = extractImageFromJPanel(panel);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(br, "png", baos);
			baos.flush();

			return Base64.encodeBytes(baos.toByteArray());

		} catch (Exception e) {
			e.printStackTrace();
			throw new GreffonException("Impossible d'encoder la toile en base 64 : " + e.getMessage());
		}

	}

	/**
	 * @param panel
	 * @return
	 * @throws InterruptedException
	 * @throws Exception 
	 */
	private BufferedImage extractImageFromJPanel(JPanelLaToile panel) throws Exception {
		panel.recalculeTaille();
		BufferedImage br = new BufferedImage(panel.getWidth(), panel.getHeight(), java.awt.image.BufferedImage.TYPE_INT_RGB);
		Graphics g = br.getGraphics();
		panel.paint(g);
		Thread.sleep(100);
		g.dispose();
		return br;
	}

}