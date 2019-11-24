package org.linotte.frame.cahier.timbre.entite;

import org.linotte.frame.cahier.timbre.entite.e.Role;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.ActeurUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class Espece extends Acteur {

	public transient ActeurUI ui = UsineATheme.getActeurUI(this);

	public Espece(String nom, Role role, PlancheUI planche) {
		super(nom, role, null, planche);
		if (texte == null && planche != null) {
			texte = "variable " + ((Planche) planche).compteurSuivant();
		}
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
	public boolean contient(int x2, int y2) {
		boolean b = ui.contient(x2, y2);
		if (b) {
			return true;
		} else {
			return super.contient(x2, y2);
		}
	}

	@Override
	public Timbre clonerPourCreation() {
		return new Espece(null, role, planche);
	}

	@Override
	public void pre_dessine() {
		ui.pre_dessine(planche);
		super.pre_dessine();
	}

	// Bean

	public Espece() {
		super();
	}

	@Override
	public Timbre clonerPourDuplication() {
		Espece clone = new Espece(texte, role, planche);
		super.clonerPourDuplication(clone);
		return clone;
	}

}
