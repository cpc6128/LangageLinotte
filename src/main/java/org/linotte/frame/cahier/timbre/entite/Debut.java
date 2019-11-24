package org.linotte.frame.cahier.timbre.entite;

import org.linotte.frame.cahier.timbre.entite.i.NonAttachable;
import org.linotte.frame.cahier.timbre.entite.i.Statique;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.DebutUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class Debut extends Timbre implements Statique, NonAttachable {

	public transient final Integer POSITION_INIT = 1;

	public Debut(PlancheUI planche) {
		super(planche);
		position = POSITION_INIT;
		x = 20; //TODO doit être géré par le thème
		y = 20;
	}

	public DebutUI ui = UsineATheme.getDebutUI(this);

	public boolean debutGlisseDepose(int x, int y) {
		return false;
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
		return ui.clique(x, y);
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
		return null;
	}

	// Bean

	public Debut() {
		super();
	}

	@Override
	public Timbre clonerPourDuplication() {
		Debut clone = new Debut(planche);
		super.clonerPourDuplication(clone);
		return clone;
	}
}
