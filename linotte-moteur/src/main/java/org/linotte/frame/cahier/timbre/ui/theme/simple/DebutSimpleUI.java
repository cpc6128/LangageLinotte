package org.linotte.frame.cahier.timbre.ui.theme.simple;

import org.linotte.frame.cahier.timbre.entite.Debut;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.DebutUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

import java.awt.*;

public class DebutSimpleUI implements DebutUI {

	public Debut moi;

	public Color couleur = Palette.DEBUT;

	private int decallage_y;

	public DebutSimpleUI(Debut debut) {
		moi = debut;
		moi.largeur = Abaque.LARGEUR_TIMBRE / 2;
		moi.hauteur = Abaque.HAUTEUR_TIMBRE;
	}

	@Override
	public void pre_dessine(PlancheUI planche) {
		decallage_y = -moi.hauteur / 2 + 20;

		((TimbreSimpleUI) ((Timbre) moi).ui).decallage_y = decallage_y;
	}

	@Override
	public void dessine(PlancheUI plancheUI) {

		Planche planche = ((Planche) plancheUI);
		Graphics2D g2d = planche.graphics;

		Composite old = g2d.getComposite();
		if (moi.glisser)
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

		if (moi.survol && moi.zoneActive == null)
			g2d.setColor(couleur.brighter());
		else
			g2d.setColor(couleur);

		g2d.fillRoundRect(moi.x, moi.y + decallage_y, moi.largeur, moi.hauteur, Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);
		
		// Attache avant :
		Polygon triangle = new Polygon(
				new int[] { //
						moi.x + moi.largeur, // A 
						moi.x + moi.largeur, // B
						moi.x + Abaque.ATTACHE_AVANT_TIMBRE / 2 + moi.largeur }, // C
				new int[] { // 
						moi.y + decallage_y + moi.hauteur / 2 - Abaque.ATTACHE_AVANT_TIMBRE / 2, // A
						moi.y + decallage_y + moi.hauteur / 2 + Abaque.ATTACHE_AVANT_TIMBRE / 2, // B
						moi.y + decallage_y + moi.hauteur / 2// C
		}, 3);
		g2d.fillPolygon(triangle);
		
		g2d.setColor(Palette.TEXTE);
		TimbresHelper.drawString(g2d, "Début", moi.x + 10, moi.y + 20 + decallage_y);

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
	public void positionnerProchainTimbre() {
	}
}
