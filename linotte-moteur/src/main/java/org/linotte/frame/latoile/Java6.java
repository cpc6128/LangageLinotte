package org.linotte.frame.latoile;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

	public static void appliquerIcone(Image className)
	{
		try {
			Class util = Class.forName("com.apple.eawt.Application");
			Method getApplication = util.getMethod("getApplication", new Class[0]);
			Object application = getApplication.invoke(util);
			Class params[] = new Class[1];
			params[0] = Image.class;
			Method setDockIconImage = util.getMethod("setDockIconImage", params);
			setDockIconImage.invoke(application, className);
		} catch (ClassNotFoundException e) {
			// log exception
		} catch (NoSuchMethodException e) {
			// log exception
		} catch (InvocationTargetException e) {
			// log exception
		} catch (IllegalAccessException e) {
			// log exception
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
