package org.linotte.frame.cahier.timbre.ui.i;

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
