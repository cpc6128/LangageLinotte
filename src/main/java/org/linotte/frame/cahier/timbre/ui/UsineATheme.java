package org.linotte.frame.cahier.timbre.ui;

import org.linotte.frame.cahier.timbre.entite.Acteur;
import org.linotte.frame.cahier.timbre.entite.BlocFin;
import org.linotte.frame.cahier.timbre.entite.Bouton;
import org.linotte.frame.cahier.timbre.entite.Commentaire;
import org.linotte.frame.cahier.timbre.entite.Condition;
import org.linotte.frame.cahier.timbre.entite.Debut;
import org.linotte.frame.cahier.timbre.entite.Fonction;
import org.linotte.frame.cahier.timbre.entite.Pour;
import org.linotte.frame.cahier.timbre.entite.TantQue;
import org.linotte.frame.cahier.timbre.entite.Textuel;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.Verbe;
import org.linotte.frame.cahier.timbre.ui.i.ActeurUI;
import org.linotte.frame.cahier.timbre.ui.i.BlocFinUI;
import org.linotte.frame.cahier.timbre.ui.i.BoutonUI;
import org.linotte.frame.cahier.timbre.ui.i.CommentaireUI;
import org.linotte.frame.cahier.timbre.ui.i.ConditionUI;
import org.linotte.frame.cahier.timbre.ui.i.DebutUI;
import org.linotte.frame.cahier.timbre.ui.i.FonctionUI;
import org.linotte.frame.cahier.timbre.ui.i.PourUI;
import org.linotte.frame.cahier.timbre.ui.i.TantQueUI;
import org.linotte.frame.cahier.timbre.ui.i.TextuelUI;
import org.linotte.frame.cahier.timbre.ui.i.ThemeUI;
import org.linotte.frame.cahier.timbre.ui.i.TimbreUI;
import org.linotte.frame.cahier.timbre.ui.i.VerbeUI;
import org.linotte.frame.cahier.timbre.ui.theme.simple.ThemeSimpleUI;

public final class UsineATheme {

	enum THEME {
		simple
	};

	private static ThemeUI simpleUI = new ThemeSimpleUI();

	public static THEME themeTimbre = THEME.simple;

	private UsineATheme() {
		throw new RuntimeException("interdit");
	}

	public static ActeurUI getActeurUI(Acteur acteur) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getActeurUI(acteur);
		default:
			return simpleUI.getActeurUI(acteur);
		}
	}

	public static TextuelUI getTextuelUI(Textuel acteur) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getTextuelUI(acteur);
		default:
			return simpleUI.getTextuelUI(acteur);
		}
	}

	public static TimbreUI getTimbreUI(Timbre acteur) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getTimbreUI(acteur);
		default:
			return simpleUI.getTimbreUI(acteur);
		}
	}

	public static VerbeUI getVerbeUI(Verbe verbe) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getTimbreVerbeUI(verbe);
		default:
			return simpleUI.getTimbreVerbeUI(verbe);
		}
	}

	public static DebutUI getDebutUI(Debut debut) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getVerbeUI(debut);
		default:
			return simpleUI.getVerbeUI(debut);
		}
	}

	public static ConditionUI getConditionUI(Condition condition) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getConditionUI(condition);
		default:
			return simpleUI.getConditionUI(condition);
		}
	}

	public static BlocFinUI getBlocFinUI(BlocFin blocfin) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getBlocFinUI(blocfin);
		default:
			return simpleUI.getBlocFinUI(blocfin);
		}
	}

	public static BoutonUI getBoutonUI(Bouton bouton) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getBoutonUI(bouton);
		default:
			return simpleUI.getBoutonUI(bouton);
		}
	}

	public static TantQueUI getTantQueUI(TantQue tantQue) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getTantQueUI(tantQue);
		default:
			return simpleUI.getTantQueUI(tantQue);
		}
	}

	public static PourUI getPourUI(Pour pour) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getPourUI(pour);
		default:
			return simpleUI.getPourUI(pour);
		}
	}

	public static CommentaireUI getCommentaireUI(Commentaire commentaire) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getCommentaireUI(commentaire);
		default:
			return simpleUI.getCommentaireUI(commentaire);
		}
	}

	public static FonctionUI getFonctionUI(Fonction fonction) {
		switch (themeTimbre) {
		case simple:
			return simpleUI.getFonctionUI(fonction);
		default:
			return simpleUI.getFonctionUI(fonction);
		}
	}

}