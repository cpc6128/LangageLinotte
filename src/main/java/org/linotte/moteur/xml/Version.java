package org.linotte.moteur.xml;

import java.util.ResourceBundle;

import org.linotte.moteur.xml.analyse.multilangage.Langage;

public class Version {

	private static ResourceBundle bundle;

	static {
		bundle = ResourceBundle.getBundle("ressources.version");
	}

	private final static String VERSION_RELEASE = bundle.getString("VERSION_RELEASE");
	private final static String VERSION_TECHNIQUE = bundle.getString("VERSION_TECHNIQUE");
	private final static String VERSION_TECHNIQUE_URL = bundle.getString("VERSION_TECHNIQUE_URL");
	private final static String URL_HOME = bundle.getString("URL_HOME");
	private final static String VERSION_WORK = bundle.getString("VERSION_WORK");
	private final static boolean BETA = Boolean.parseBoolean(bundle.getString("BETA"));
	private final static boolean PRO = Boolean.parseBoolean(bundle.getString("PRO"));
	public final static String AUTEUR = bundle.getString("AUTEUR");
	public final static String LICENCE = bundle.getString("LICENCE");
	public final static String URL_GREFFONS = bundle.getString("URL_GREFFONS");
	public final static String DATE = bundle.getString("DATE");

	public static String getVersion() {
		return (!BETA) ? VERSION_RELEASE : (VERSION_WORK);
	}

	public static boolean isBeta() {
		return BETA;
	}

	public static boolean isPro() {
		return PRO;
	}

	public static String getVERSION_TECHNIQUE() {
		return VERSION_TECHNIQUE;
	}

	public static String getVERSION_TECHNIQUE_URL() {
		return VERSION_TECHNIQUE_URL;
	}

	public static String getDocumentarium(Langage langage) {
		return bundle.getString("DOC_" + langage.name().toUpperCase());
	}

	public static String getURL() {
		return URL_HOME;
	}

}