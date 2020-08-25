package org.linotte.frame.latoile;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

public class Java6 {

	public static Java6 getDesktop() {
		return new Java6();
	}

	public void browse(URI uri) throws IOException {
		try {
			browse_(uri);
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	public static boolean isJava9() {
		double version = Double.parseDouble(System.getProperty("java.specification.version"));
		return version >= 9;
	}

	public void mail(URI uri) throws IOException {
		try {
			mail_(uri);
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	private void browse_(URI uri) throws IOException {
		if (Desktop.isDesktopSupported())
			Desktop.getDesktop().browse(uri);
	}

	private void mail_(URI uri) throws IOException {
		if (Desktop.isDesktopSupported())
			Desktop.getDesktop().mail(uri);
	}

	public static void setIconImage(Dialog frameDialog, Image image) {
		try {
			setIconImage_(frameDialog, image);
		} catch (Error e) {
			e.printStackTrace();
		}
	}

	private static void setIconImage_(Dialog frameDialog, Image image) {
		frameDialog.setIconImage(image);
	}

}
