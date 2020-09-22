package org.linotte.greffons.impl;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import org.linotte.greffons.externe.Graphique;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRCode extends Graphique {

	@Override
	public void projette(Graphics2D graphics) throws GreffonException {
		// http://crunchify.com/java-simple-qr-code-generator-example/
		String myCodeText = getAttributeAsString("texte");
		int size = getAttributeAsBigDecimal("taille").intValue();
		int x = getAttributeAsBigDecimal("x").intValue();
		int y = getAttributeAsBigDecimal("y").intValue();
		try {
			Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
			hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
			int CrunchifyWidth = byteMatrix.getWidth();
			BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth, BufferedImage.TYPE_INT_RGB);
			image.createGraphics();
			graphics.setColor(Color.BLACK);

			for (int i = 0; i < CrunchifyWidth; i++) {
				for (int j = 0; j < CrunchifyWidth; j++) {
					if (byteMatrix.get(i, j)) {
						graphics.fillRect(x + i, y + j, 1, 1);
					}
				}
			}

		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Shape getShape() {
		return null;
	}

}