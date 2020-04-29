package org.linotte.moteur.xml;

import java.util.ResourceBundle;

public class Version {

	private static ResourceBundle bundle;

	static {
		bundle = ResourceBundle.getBundle("version");
	}

	private final static String VERSION_RELEASE = bundle.getString("VERSION_RELEASE");
	private final static String URL_HOME = bundle.getString("URL_HOME");
	private final static boolean BETA = Boolean.parseBoolean(bundle.getString("BETA"));
	public final static String AUTEUR = bundle.getString("AUTEUR");
	public final static String LICENCE = bundle.getString("LICENCE");
	public final static String DATE = bundle.getString("DATE");

	public static String getVersion() {
		return VERSION_RELEASE;
	}

	public static String getURL() {
		return URL_HOME;
	}

	public static boolean isBeta() {
		return BETA;
	}

}