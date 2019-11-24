package org.linotte.frame.cahier.timbre.ui.theme.simple;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;

import org.linotte.frame.cahier.timbre.entite.Textuel;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.e.Parfum;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

/**
 * Méthodes communes à tous les timbres :
 * 
 * @author Ronan MOUNES
 *
 */
public abstract class TimbreSimple {

	int decallage_y;

	Composite old;

	/**
	 * @param g2d
	 * @param decallage_y 
	 * @param moi 
	 */
	void _dessinerAccroche(Graphics2D g2d, Timbre moi) {
		if (moi.timbreSuivant != null && moi.parfum != Parfum.RETOURNER) {
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
		}
	}

	/**
	 * Centrer la zone tactile
	 * @param moi
	 */
	public void _centrer(Timbre moi) {
		// Centrer la zone tactile :
		decallage_y = -moi.hauteur / 2 + 20;
		((TimbreSimpleUI) ((Timbre) moi).ui).decallage_y = decallage_y;
		((TextuelSimpleUI) ((Textuel) moi).ui).decallage_y = decallage_y;
	}

	public void _dessineFond(Graphics2D g2d, Timbre moi, Color couleur) {
		if (moi.survol && moi.zoneActive == null) {
			g2d.setColor(couleur.brighter());
		} else
			g2d.setColor(couleur);
		g2d.fillRoundRect(moi.x, moi.y + decallage_y, moi.largeur, moi.hauteur, Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);

	}

	public Graphics2D _debutDessin(Timbre moi, PlancheUI planche) {
		Graphics2D g2d = ((Planche) planche).graphics;
		old = g2d.getComposite();
		if (moi.glisser) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		}
		return g2d;

	}

	public void _finDessin(Graphics2D g2d) {
		g2d.setComposite(old);
	}

}
