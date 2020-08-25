package org.linotte.greffons.impl;

import com.itextpdf.text.pdf.codec.Base64;
import org.linotte.frame.latoile.JPanelLaToile;
import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.latoile.Toile;
import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.externe.Greffon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.net.MalformedURLException;

public class Tweak extends Greffon {

	@Slot(nom = "changericônetoile")
	public boolean changericonetoile(String icone) throws GreffonException {
		String chemin = getRessourceManager().analyserChemin(icone);
		File file = new File(chemin);
		try {
			LinotteFacade.getToile().getFrameParent().setIconImage(new ImageIcon(file.toURI().toURL()).getImage());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Slot(nom = "changericône")
	public boolean changericone(String icone) throws GreffonException {
		try {
			String chemin = getRessourceManager().analyserChemin(icone);
			File file = new File(chemin);
			((JFrame) LinotteFacade.getToile().getFrameParent()).setIconImage(new ImageIcon(file.toURI().toURL()).getImage());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Slot
	public boolean transparencetoile(BigDecimal alpha) throws GreffonException {
		LinotteFacade.getTransparence().setWindowOpacity(LinotteFacade.getToile().getFrameParent(), new BigDecimal(alpha.floatValue() / 100));
		return true;
	}

	@Slot
	public boolean toileinvisible() throws GreffonException {
		LinotteFacade.getToile().getPanelLaToile().activeTranslucencyCapable();
		return true;
	}

	@Slot(nom = "vidermémoire")
	public boolean vidermemoire() throws GreffonException {
		//RuntimeContext context = LinotteFacade.getRuntimeContext();
		//context.getLibrairie().getToile().getPanelLaToile().effacer();
		//context.getLibrairie().vider();
		return true;
	}

	@Slot(nom = "changerésolution")
	public boolean changeresolution(int largeur, int hauteur) throws GreffonException {
		try {
			GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			DisplayMode dm = device.getDisplayMode();
			DisplayMode newDM = new DisplayMode(largeur, hauteur, dm.getBitDepth(), dm.getRefreshRate());
			device.setDisplayMode(newDM);
		} catch (Exception e) {
			throw new GreffonException(e.getMessage());
		}
		return true;
	}

	@Slot(nom = "toileenbase64")
	@Deprecated
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