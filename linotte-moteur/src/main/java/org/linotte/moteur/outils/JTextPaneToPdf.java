/* in_action/chapter12/JTextPaneToPdf.java */

package org.linotte.moteur.outils;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.OutputStream;

/**

 * This example was written by Bill Ensley and adapter by Bruno Lowagie. It is

 * part of the book 'iText in Action' by Manning Publications. ISBN: 1932394796

 * http://www.1t3xt.com/docs/book.php http://www.manning.com/lowagie/

 */

public final class JTextPaneToPdf {

	private static final int _792 = 792;

	private static final int _612 = 612;

	private static int inch = Toolkit.getDefaultToolkit().getScreenResolution();

	private static float pixelToPoint = (float) 72 / (float) inch;

	/**

	 * Constructs a JTextPaneToPdf object. This opens a frame that can be used

	 * as text editor.

	 */

	public static void paintToPDF(JTextPane textpane, OutputStream fos, String creator) throws Exception {
		textpane.setBounds(0, 0, (int) convertToPixels(_612 - 58), (int) convertToPixels(_792 - 60));
		Rectangle max = textpane.modelToView(textpane.getDocument().getLength() - 10);
		int fin = max.y + max.height;
		com.itextpdf.text.Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, fos);
		document.addCreator(creator);
		document.setPageSize(new com.itextpdf.text.Rectangle(_612, _792));
		document.open();
		PdfContentByte contentbyte = writer.getDirectContent();
		contentbyte.concatCTM(1, 0, 0, 1, 0, 0);
		int debut = 0;
		for (; debut + fin > 0;) {
			PdfTemplate template = contentbyte.createTemplate((int) convertToPixels(_612 - 58), (int) convertToPixels(_792 - 60));
			Graphics2D g2 = new PdfGraphics2D(template, _612, _792);
			AffineTransform at = new AffineTransform();
			at.translate(convertToPixels(20), convertToPixels(20));
			at.scale(pixelToPoint, pixelToPoint);
			g2.transform(at);
			g2.setColor(Color.WHITE);
			g2.fill(textpane.getBounds());
			Rectangle alloc = getVisibleEditorRect(textpane, debut);
			textpane.getUI().getRootView(textpane).paint(g2, alloc);
			g2.setColor(Color.WHITE);
			// Construction du rebord :
			Rectangle rebord = textpane.getBounds();
			rebord.grow(58 / 2, 60 / 2);
			Stroke stroke = new BasicStroke(60, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
			Shape shape = stroke.createStrokedShape(rebord);
			g2.fill(shape);
			g2.dispose();
			contentbyte.addTemplate(template, 0, 0);
			document.newPage();
			debut -= (int) convertToPixels(_792 - 60);
		}
		document.close();
		fos.flush();
		fos.close();

	}

	private static float convertToPixels(int points) {

		return points / pixelToPoint;

	}

	private static Rectangle getVisibleEditorRect(JTextPane ta, int debut) {

		Rectangle bounds = ta.getBounds();
		if ((bounds.width > 0) && (bounds.height > 0)) {
			bounds.x = 0;
			bounds.y = debut;
			Insets insets = ta.getInsets();
			bounds.x += insets.left;
			bounds.y += insets.top;
			bounds.width -= insets.left + insets.right;
			bounds.height -= insets.top + insets.bottom;
			return bounds;
		}

		return null;

	}

	/**
	 * Permet de savoir si on peut générer un pdf ou pas
	 * @return
	 */
	public static boolean can() {
		try {
			Class.forName("com.itextpdf.text.pdf.PdfWriter");
			return true;
		} catch (Throwable ex) {
		}
		return false;
	}

}