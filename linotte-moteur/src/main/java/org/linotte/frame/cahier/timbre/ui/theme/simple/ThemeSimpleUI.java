package org.linotte.frame.cahier.timbre.ui.theme.simple;

import org.linotte.frame.cahier.timbre.entite.*;
import org.linotte.frame.cahier.timbre.ui.i.*;

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
