package org.linotte.frame.cahier.timbre.entite;

import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.i.TextuelUI;

/**
 * Timbre avec un texte
 * @author CPC
 *
 */
public abstract class Textuel extends Timbre {

	public String texte;

	public transient TextuelUI ui = UsineATheme.getTextuelUI(this);

	public Textuel(String text, PlancheUI planche) {
		super(planche);
		this.texte = text;
	}

	@Override
	public void pre_dessine() {
		ui.pre_dessine(planche);
		super.pre_dessine();
	}

	@Override
	public void dessine() {
		super.dessine();
		ui.dessine(planche);
	}

	@Override
	public boolean clique(int x, int y) {
		if (ui.clique(x, y))
			return true;
		else
			return super.clique(x, y);
	}

	// Bean
	public Textuel() {

	}

	public void clonerPourDuplication(Textuel clone) {
		super.clonerPourDuplication(clone);
		clone.texte = texte;
	}

}
