package org.linotte.frame.cahier.timbre.entite;

import org.linotte.frame.cahier.timbre.entite.i.NonDeplacable;
import org.linotte.frame.cahier.timbre.entite.i.NonSupprimable;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.BlocFinUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class BlocFin extends Timbre implements NonSupprimable, NonDeplacable {

	public BlocDebut blocDebut;

	public transient BlocFinUI ui = UsineATheme.getBlocFinUI(this);

	public BlocFin(PlancheUI planche, BlocDebut blocDebut) {
		super(planche);
		this.blocDebut = blocDebut;
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
		// jamais utilisé
		return null;
	}

	@Override
	public void pre_dessine() {
		ui.pre_dessine(planche);
		super.pre_dessine();
	}

	// Bean

	public BlocFin() {
		super();
	}

	@Override
	public Timbre clonerPourDuplication() {
		BlocFin clone = new BlocFin();
		super.clonerPourDuplication(clone);
		clone.blocDebut = blocDebut;
		return clone;
	}

}
