package org.linotte.greffons.impl;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.linotte.frame.latoile.JPanelLaToile;
import org.linotte.frame.latoile.LaToile;
import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.DocumentationHTML;

/**
 * 
 * Gestion du presse papier
 * 
 * Pour la Toile : http://kb.yworks.com/article14.html
 * Pour le HTML : http://elliotth.blogspot.com/2005/01/copying-html-to-clipboard-from-java.html
 * 
 * @author Mounès Ronan
 * 
 */
@DocumentationHTML("" + "Le <i>PressePapier</i> est une espèce proposant des méthodes pour manipuler le presse papier du système d'exploitation.<br>"
		+ "<u>Les méthodes fonctionnelles proposées par le prototype PressePapier sont :</u><br>" + "<ul>"
		+ "<li>acteur<texte> pressepapier.coller() : Récupère le texte présent dans le presse-papier du système</li>"
		+ "<li>pressepapier.copier(acteur<texte>) : Stocke dans le presse-papier du système d'exploitation la valeur de l'acteur</li>"
		+ "<li>pressepapier.copierhtml(acteur<texte>) : Stocke dans le presse-papier du système d'exploitation la valeur de l'acteur en HTML</li>"
		+ "<li>pressepapier.copiertoile() : Stocke dans le presse-papier du système d'exploitation l'image de la toile</li>"
		+ "<li>pressepapier.copierZoneToile() : doc à faire</li>" + "</ul>")
public class PressePapier extends Greffon {

	private static Acteur VRAI, FAUX;

	static {
		try {
			VRAI = new Acteur(ROLE.DRAPEAU, "boolean", new BigDecimal(1));
			FAUX = new Acteur(ROLE.DRAPEAU, "boolean", new BigDecimal(0));
		} catch (GreffonException e) {
			e.printStackTrace();
		}
	}

	@Slot(nom = "coller", entree = {}, sortie = ROLE.TEXTE)
	public ObjetLinotte collerTexte() throws GreffonException {
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			Transferable clipData = clipboard.getContents(this);
			String s;
			try {
				s = (String) (clipData.getTransferData(DataFlavor.stringFlavor));
			} catch (Exception ex) {
				s = ex.toString();
			}
			Acteur retour = new Acteur(ROLE.TEXTE, "presse-papier", s);
			return retour;
		} catch (Exception e) {
			throw new GreffonException("Impossible d'accéder au presse papier du système");
		}
	}

	@Slot(nom = "copier", entree = { ROLE.TEXTE }, sortie = ROLE.DRAPEAU)
	public ObjetLinotte copierTexte(ObjetLinotte obj) throws GreffonException {
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			if (obj instanceof Acteur) {
				Acteur acteur = (Acteur) obj;
				StringSelection data = new StringSelection(String.valueOf(acteur.getValeur()));
				clipboard.setContents(data, data);
				return VRAI;
			}
			return FAUX;
		} catch (Exception e) {
			throw new GreffonException("Impossible d'accéder au presse papier du système");
		}
	}

	@Slot(nom = "copierhtml", entree = { ROLE.TEXTE }, sortie = ROLE.DRAPEAU)
	public ObjetLinotte copierHTML(ObjetLinotte obj) throws GreffonException {
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			if (obj instanceof Acteur) {
				Acteur acteur = (Acteur) obj;
				HtmlSelection data = new HtmlSelection(String.valueOf(acteur.getValeur()));
				clipboard.setContents(data, null);
				return VRAI;
			}
			return FAUX;
		} catch (Exception e) {
			throw new GreffonException("Impossible d'accéder au presse papier du système");
		}
	}

	@Slot(nom = "copiertoile", entree = {}, sortie = ROLE.DRAPEAU)
	public ObjetLinotte copierToile() throws GreffonException {
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			LaToile toile = LinotteFacade.getToile();
			JPanelLaToile panel = toile.getPanelLaToile();
			BufferedImage br = extractImageFromJPanel(panel);
			ImageTransferable it = new ImageTransferable(br);
			clipboard.setContents(it, null);
			return VRAI;
		} catch (Exception e) {
			throw new GreffonException("Impossible d'accéder au presse papier du système");
		}
	}

	@Slot(nom = "copierzonetoile")
	public boolean copierZoneToile(int x, int y, int w, int h) throws GreffonException {
		try {
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			LaToile toile = LinotteFacade.getToile();
			JPanelLaToile panel = toile.getPanelLaToile();
			BufferedImage br = extractImageFromJPanel(panel);
			Image cible = br.getSubimage(x, y, w, h);
			ImageTransferable it = new ImageTransferable(cible);
			clipboard.setContents(it, null);
			return true;
		} catch (Exception e) {
			throw new GreffonException("Impossible de copier l'image, vérifier vos paramètres");
		}
	}

	/**
	 * @param panel
	 * @return
	 * @throws InterruptedException
	 */
	private BufferedImage extractImageFromJPanel(JPanel panel) throws InterruptedException {
		BufferedImage br = new BufferedImage(panel.getWidth(), panel.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
		Graphics g = br.getGraphics();
		panel.paint(g);
		// Il faut attendre que tous les éléments s'affichent :
		Thread.sleep(500);
		g.dispose();
		return br;
	}

	/**
	   * Transferable used to communicate with the system clipboard.
	   */
	static class ImageTransferable implements Transferable {
		Image image;

		ImageTransferable(Image img) {
			this.image = img;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return new DataFlavor[] { DataFlavor.imageFlavor };
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return DataFlavor.imageFlavor.equals(flavor);
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			return image;
		}

	}

	private static class HtmlSelection implements Transferable {

		private static List<DataFlavor> htmlFlavors = new ArrayList<DataFlavor>();

		static {

			try {

				htmlFlavors.add(new DataFlavor("text/html;class=java.lang.String"));

				htmlFlavors.add(new DataFlavor("text/html;class=java.io.Reader"));

				htmlFlavors.add(new DataFlavor("text/html;charset=unicode;class=java.io.InputStream"));

			} catch (ClassNotFoundException ex) {

				ex.printStackTrace();

			}

		}

		private String html;

		public HtmlSelection(String html) {

			this.html = html;

		}

		public DataFlavor[] getTransferDataFlavors() {

			return (DataFlavor[]) htmlFlavors.toArray(new DataFlavor[htmlFlavors.size()]);

		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {

			return htmlFlavors.contains(flavor);

		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {

			if (String.class.equals(flavor.getRepresentationClass())) {

				return html;

			} else if (Reader.class.equals(flavor.getRepresentationClass())) {

				return new StringReader(html);

			} else if (InputStream.class.equals(flavor.getRepresentationClass())) {

				return new StringReader(html);

			}

			throw new UnsupportedFlavorException(flavor);

		}

	}

	/**
	 * TODO Les méthodes suivantes sont présentes dans la classe Outils du greffon Image
	 * 
	 * @param i
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */

	public BufferedImage extractImage(Image i, int x, int y, int w, int h) {
		//http://stackoverflow.com/questions/8662349/convert-negative-image-to-positive
		//http://www.exampledepot.com/egs/java.awt.image/image2buf.html
		BufferedImage bugImg = toBufferedImage(i);
		return bugImg.getSubimage(x, y, w, h);
	}

	private BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see Determining If an Image Has Transparent Pixels
		boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	// This method returns true if the specified image has transparent pixels
	private boolean hasAlpha(Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		// Get the image's color model
		ColorModel cm = pg.getColorModel();
		return cm.hasAlpha();
	}

}
