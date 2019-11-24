package org.linotte.frame.cahier.timbre.ui.theme.simple;

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

public class ThemeSimpleUI implements ThemeUI {

	public ThemeSimpleUI() {
	}

	@Override
	public ActeurUI getActeurUI(Acteur acteur) {
		return new ActeurSimpleUI(acteur);
	}

	@Override
	public TextuelUI getTextuelUI(Textuel acteur) {
		return new TextuelSimpleUI(acteur);
	}

	@Override
	public TimbreUI getTimbreUI(Timbre acteur) {
		return new TimbreSimpleUI(acteur);
	}

	@Override
	public VerbeSimpleUI getTimbreVerbeUI(Verbe verbe) {
		return new VerbeSimpleUI(verbe);
	}

	@Override
	public DebutUI getVerbeUI(Debut debut) {
		return new DebutSimpleUI(debut);
	}

	@Override
	public ConditionUI getConditionUI(Condition condition) {
		return new ConditionSimpleUI(condition);
	}

	@Override
	public BoutonUI getBoutonUI(Bouton bouton) {
		return new BoutonSimpleUI(bouton);
	}

	@Override
	public BlocFinUI getBlocFinUI(BlocFin blocfin) {
		return new BlocFinSimpleUI(blocfin);
	}

	@Override
	public TantQueUI getTantQueUI(TantQue tantque) {
		return new TantQueSimpleUI(tantque);
	}

	@Override
	public PourUI getPourUI(Pour pour) {
		return new PourSimpleUI(pour);
	}

	@Override
	public CommentaireUI getCommentaireUI(Commentaire commentaire) {
		return new CommentaireSimpleUI(commentaire);
	}

	@Override
	public FonctionUI getFonctionUI(Fonction fonction) {
		return new FonctionSimpleUI(fonction);
	}

}
