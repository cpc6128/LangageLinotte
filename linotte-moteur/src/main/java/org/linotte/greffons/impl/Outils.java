package org.linotte.greffons.impl;

import org.linotte.moteur.outils.Ressources;

import javax.swing.*;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Outils {

	private static Map<String, Filtre> filtres = new HashMap<String, Outils.Filtre>();

	// Filtres :

	static class BlurFilter implements Filtre {
		public BufferedImage processImage(BufferedImage image) {
			float[] blurMatrix = { 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f, 1.0f / 9.0f };
			BufferedImageOp blurFilter = new ConvolveOp(new Kernel(3, 3, blurMatrix), ConvolveOp.EDGE_NO_OP, null);
			return blurFilter.filter(image, null);
		}
	}

	static class SharpenFilter implements Filtre {
		public BufferedImage processImage(BufferedImage image) {
			float[] sharpenMatrix = { 0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0.0f };
			BufferedImageOp sharpenFilter = new ConvolveOp(new Kernel(3, 3, sharpenMatrix), ConvolveOp.EDGE_NO_OP, null);
			return sharpenFilter.filter(image, null);
		}
	}

	static class InvertFilter implements Filtre {
		public BufferedImage processImage(BufferedImage image) {
			BufferedImageOp op = new RescaleOp(-1.0f, 255f, null);
			return op.filter(image, null);

		}
	}

	static class GrayscaleFilter implements Filtre {
		public BufferedImage processImage(BufferedImage image) {
			ColorConvertOp cop = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
			return cop.filter(image, null);
		}
	}

	static class ColorFilter implements Filtre {
		public BufferedImage processImage(BufferedImage image) {
			ConvolveOp op = new ConvolveOp(new Kernel(3, 3, new float[] { 0.0f, -0.75f, 0.0f, -0.75f, 3.0f, -0.75f, 0.0f, -0.75f, 0.0f }));
			return op.filter(image, null);
		}
	}

	static {
		filtres.put("blur", new BlurFilter());
		filtres.put("invert", new InvertFilter());
		filtres.put("sharpend", new SharpenFilter());
		filtres.put("color", new ColorFilter());
		filtres.put("gray", new GrayscaleFilter());
	}

	public BufferedImage toBufferedImage(Image image) {
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

	public Image chargementImage(String fichier) {
		return Ressources.chargementImage(fichier);
	}

	/**
	 * @return
	 */
	public BufferedImage algoNegatif(Image i) {
		//http://stackoverflow.com/questions/8662349/convert-negative-image-to-positive
		//http://www.exampledepot.com/egs/java.awt.image/image2buf.html
		BufferedImage bugImg = toBufferedImage(i);
		for (int x = 0; x < bugImg.getWidth(); x++) {
			for (int y = 0; y < bugImg.getHeight(); y++) {
				int rgba = bugImg.getRGB(x, y);
				Color col = new Color(rgba, true);
				col = new Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
				bugImg.setRGB(x, y, col.getRGB());
			}
		}
		return bugImg;
	}

	// If an image is on the system clipboard, this method returns it;
	// otherwise it returns null.
	// http://www.exampledepot.com/egs/java.awt.datatransfer/ToClipImg.html
	public Image getFromClipboard() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		try {
			return (Image) clipboard.getData(DataFlavor.imageFlavor);
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 
	 * FILTRE
	 */

	interface Filtre {
		public abstract BufferedImage processImage(BufferedImage image);
	}

	public Image processImage(Image image, String filtre) {
		BufferedImage bugImg = toBufferedImage(image);
		Filtre f = filtres.get(filtre);
		if (f != null) {
			return f.processImage(bugImg);
		}
		return null;
	}

	public List<String> filtres() {
		return new ArrayList<String>(filtres.keySet());
	}
}
