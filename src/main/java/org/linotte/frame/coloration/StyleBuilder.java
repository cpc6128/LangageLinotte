package org.linotte.frame.coloration;

import static org.linotte.frame.coloration.StyleLinotte.styleRacine;
import static org.linotte.frame.coloration.StyleLinotte.style_article;
import static org.linotte.frame.coloration.StyleLinotte.style_commentaire;
import static org.linotte.frame.coloration.StyleLinotte.style_doublure;
import static org.linotte.frame.coloration.StyleLinotte.style_error;
import static org.linotte.frame.coloration.StyleLinotte.style_livre;
import static org.linotte.frame.coloration.StyleLinotte.style_math;
import static org.linotte.frame.coloration.StyleLinotte.style_nombre;
import static org.linotte.frame.coloration.StyleLinotte.style_sous_paragraphe;
import static org.linotte.frame.coloration.StyleLinotte.style_string;
import static org.linotte.frame.coloration.StyleLinotte.style_structure;
import static org.linotte.frame.coloration.StyleLinotte.style_systeme;
import static org.linotte.frame.coloration.StyleLinotte.style_texte;
import static org.linotte.frame.coloration.StyleLinotte.style_token;
import static org.linotte.frame.coloration.StyleLinotte.style_variable_doublure;
import static org.linotte.frame.coloration.StyleLinotte.style_variable_locale;
import static org.linotte.frame.coloration.StyleLinotte.style_variable_systeme;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;

@SuppressWarnings("unchecked")
public class StyleBuilder {

	private static File configuration;

	private static final String FICHIER = "couleurs.cfg";

	protected static Map<STYLE, StyleBean> mapStyle = new HashMap<STYLE, StyleBean>();

	public static enum STYLE implements Comparable<STYLE> {
		NORMAL("Acteur"), ARTICLE("Article"), TOKEN("Mot clef Linotte"), SYSTEME("Nom fonction"), ERREUR("Erreur"), COMMENTAIRE("Commentaire"), TEXTE(
				"Fonction"), PARAMETRE("Indice"), VARIABLE_LOCALE("Variable locale"), VARIABLE_DOUBLURE("Paramètre"), VARIABLE_SYSTEME("Variable système"), STRUCTURE(
				"Structure"), MATH("Symbole"), LIVRE("Livre"), FONCTION("Bloc"), CHAINE("Chaîne"), NOMBRE("Nombre");

		private String titre;

		private STYLE(String titre) {
			this.titre = titre;
		}

		String titre() {
			return titre;
		}

	};

	static {
		String final_home = Preference.getIntance().getHome();
		// Fichier utilisateur :
		configuration = new File(final_home + File.separator + Preference.REPERTOIRE + File.separator + FICHIER);
		boolean charge = false;
		if (configuration.exists()) {
			try {
				importer(new FileInputStream(configuration));
				charge = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!charge) {
			// Valeurs par défaut :
			try {
				importer(Ressources.getFromRessources("couleurs.cfg"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void importer(InputStream configuration) throws FileNotFoundException {
		XMLDecoder decoder = new XMLDecoder(configuration);
		mapStyle = (Map<STYLE, StyleBean>) decoder.readObject();
		decoder.close();
	}

	public static void appliquerStyle(STYLE style) {
		StyleBean bean = retourneStyleBean(style);
		MutableAttributeSet set = style(style);

		if (bean == null) {
			bean = chargeStyle(set);
			mapStyle.put(style, bean);
		} else {
			appliquerStyle(set, bean);
		}
	}

	public static StyleBean retourneStyleBean(STYLE style) {
		return mapStyle.get(style);
	}

	private static void appliquerStyle(MutableAttributeSet set, StyleBean bean) {
		StyleConstants.setForeground(set, bean.getCouleur());
		StyleConstants.setItalic(set, bean.isItalique());
		StyleConstants.setUnderline(set, bean.isSouligne());
		StyleConstants.setBold(set, bean.isGras());
		StyleConstants.setStrikeThrough(set, bean.isBarre());
		StyleConstants.setFontSize(set, bean.getSize());
		if (bean.getPolice() != null)
			StyleConstants.setFontFamily(set, bean.getPolice());
	}

	public static void effacerStyle(STYLE style) {
		MutableAttributeSet set = style(style);
		//StyleConstants.setForeground(set, null);
		StyleConstants.setItalic(set, false);
		StyleConstants.setUnderline(set, false);
		StyleConstants.setBold(set, false);
		StyleConstants.setStrikeThrough(set, false);
		StyleConstants.setFontSize(set, 14);
		StyleConstants.setFontFamily(set, "SansSerif");
	}

	private static StyleBean chargeStyle(MutableAttributeSet set) {
		StyleBean bean = new StyleBean();
		bean.setCouleur(StyleConstants.getForeground(set));
		bean.setItalique(StyleConstants.isItalic(set));
		bean.setSouligne(StyleConstants.isUnderline(set));
		bean.setGras(StyleConstants.isBold(set));
		bean.setBarre(StyleConstants.isStrikeThrough(set));
		bean.setSize(StyleConstants.getFontSize(set));
		bean.setPolice(StyleConstants.getFontFamily(set));
		return bean;
	}

	private static MutableAttributeSet style(STYLE style) {
		switch (style) {
		case TOKEN:
			return style_token;
		case ARTICLE:
			return style_article;
		case SYSTEME:
			return style_systeme;
		case ERREUR:
			return style_error;
		case COMMENTAIRE:
			return style_commentaire;
		case TEXTE:
			return style_texte;
		case PARAMETRE:
			return style_doublure;
		case VARIABLE_LOCALE:
			return style_variable_locale;
		case VARIABLE_DOUBLURE:
			return style_variable_doublure;
		case VARIABLE_SYSTEME:
			return style_variable_systeme;
		case STRUCTURE:
			return style_structure;
		case MATH:
			return style_math;
		case LIVRE:
			return style_livre;
		case FONCTION:
			return style_sous_paragraphe;
		case CHAINE:
			return style_string;
		case NOMBRE:
			return style_nombre;
		default:
			return styleRacine;
		}
	}

	public static void enregistrer() {
		try {
			XMLEncoder encoder = new XMLEncoder(new FileOutputStream(configuration));
			encoder.writeObject(mapStyle);
			encoder.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
