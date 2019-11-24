package org.linotte.frame.cahier.timbre.ui.theme.simple;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;

import org.linotte.frame.cahier.timbre.entite.Textuel;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.i.TextuelUI;

public class TextuelSimpleUI implements TextuelUI {

	public Textuel moi;

	public int decallage_y = 0;

	protected TextuelSimpleUI(Textuel timbreAvecRenduText) {
		moi = timbreAvecRenduText;
	}

	@Override
	public void dessine(PlancheUI planche) {

		Graphics2D g2d = ((Planche) planche).graphics;

		Composite old = g2d.getComposite();
		if (moi.glisser)
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

		g2d.setColor(Palette.TEXTE);
		String label = moi.texte;
		TimbresHelper.drawString(g2d, label, moi.x + 10, moi.y + 20 + decallage_y);

		g2d.setComposite(old);

	}

	@Override
	public boolean clique(int x, int y) {
		return false;
	}

	@Override
	public boolean contient(int x, int y) {
		return false;
	}

	@Override
	public void pre_dessine(PlancheUI planche) {
	}

	@Override
	public void positionnerProchainTimbre() {
	}
}
