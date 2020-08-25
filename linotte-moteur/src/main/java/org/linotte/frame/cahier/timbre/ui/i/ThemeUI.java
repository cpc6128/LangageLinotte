package org.linotte.frame.cahier.timbre.ui.i;

import org.linotte.frame.cahier.timbre.entite.*;

public interface ThemeUI {

	ActeurUI getActeurUI(Acteur acteur);

	TextuelUI getTextuelUI(Textuel acteur);

	TimbreUI getTimbreUI(Timbre acteur);

	VerbeUI getTimbreVerbeUI(Verbe verbe);

	DebutUI getVerbeUI(Debut debut);

	ConditionUI getConditionUI(Condition condition);

	BoutonUI getBoutonUI(Bouton bouton);

	BlocFinUI getBlocFinUI(BlocFin blocfin);

	TantQueUI getTantQueUI(TantQue tantque);

	PourUI getPourUI(Pour pour);

	CommentaireUI getCommentaireUI(Commentaire commentaire);

	FonctionUI getFonctionUI(Fonction fonction);

}
