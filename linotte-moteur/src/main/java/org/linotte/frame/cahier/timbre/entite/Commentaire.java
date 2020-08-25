package org.linotte.frame.cahier.timbre.entite;

import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.CommentaireUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class Commentaire extends Textuel {

	public transient CommentaireUI ui = UsineATheme.getCommentaireUI(this);

	public Commentaire(PlancheUI planche) {
		super("Un commentaire", planche);
	}

	@Override
	public void pre_dessine() {
		ui.pre_dessine(planche);
		super.pre_dessine();
	}

	@Override
	public void dessine() {
		ui.dessine(planche);
		super.dessine();
	}

	@Override
	public boolean clique(int x, int y) {
		if (ui.clique(x, y))
			return true;
		else
			return super.clique(x, y);
	}

	@Override
	public boolean contient(int x, int y) {
		boolean b = ui.contient(x, y);
		if (b) {
			return true;
		} else {
			return super.contient(x, y);
		}
	}

	@Override
	public Timbre clonerPourCreation() {
		return new Commentaire(planche);
	}

	// Bean

	public Commentaire() {
		super();
	}

	@Override
	public void validation() {
		erreurValidation = false;
	}

	@Override
	public Timbre clonerPourDuplication() {
		Commentaire clone = new Commentaire();
		super.clonerPourDuplication(clone);
		return clone;
	}
}
