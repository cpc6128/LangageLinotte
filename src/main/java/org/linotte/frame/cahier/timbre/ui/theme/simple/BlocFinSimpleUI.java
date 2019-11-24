package org.linotte.frame.cahier.timbre.ui.theme.simple;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;

import org.linotte.frame.cahier.timbre.entite.BlocDebut;
import org.linotte.frame.cahier.timbre.entite.BlocFin;
import org.linotte.frame.cahier.timbre.entite.Condition;
import org.linotte.frame.cahier.timbre.entite.Pour;
import org.linotte.frame.cahier.timbre.entite.TantQue;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.BlocFinUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class BlocFinSimpleUI implements BlocFinUI {

	public BlocFin moi;

	public Color couleur = Palette.BLOCFIN;

	public int hauteurCalculee = 0;

	public boolean visible = true;

	public BlocFinSimpleUI(BlocFin blocfin) {
		moi = blocfin;
	}

	@Override
	public void pre_dessine(PlancheUI planche) {
		if (moi.blocDebut instanceof TantQue || moi.blocDebut instanceof Pour)
			moi.largeur = Abaque.LARGEUR_TIMBRE / 6;
		else
			moi.largeur = Abaque.LARGEUR_TIMBRE / 2;

		((TimbreSimpleUI) ((Timbre) moi).ui).decallage_y = -hauteurCalculee / 2 + 20;

		if (visible && !moi.glisser && !moi.blocDebut.glisser) {
			// Nuage  :
			Graphics2D g2d = ((Planche) planche).graphics;

			int xdecallage_x = moi.blocDebut.largeur;

			if (moi.blocDebut instanceof TantQue || moi.blocDebut instanceof Pour) {
				xdecallage_x = xdecallage_x / 2; // moche
			}

			int y_bas = trouvePositionYHaut(moi.blocDebut, moi, 0);
			int y_haut = trouvePositionYBas(moi.blocDebut, moi, 0);

			g2d.setColor(Palette.BLOCFIN_CONTOUR);

			if (moi.blocDebut.survol || moi.survol) {
				g2d.setStroke(new BasicStroke(3));
			}
			g2d.drawRect(moi.blocDebut.x + xdecallage_x, y_bas, Math.abs(moi.x - moi.blocDebut.x) - xdecallage_x, y_haut - y_bas);
			g2d.setStroke(new BasicStroke());
			Composite old = g2d.getComposite();
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.1f));
			if (moi.blocDebut instanceof TantQue)
				g2d.setColor(Palette.FOND_BLOC_TANTQUE);
			else if (moi.blocDebut instanceof Pour)
				g2d.setColor(Palette.FOND_BLOC_POUR);
			else
				g2d.setColor(Palette.FOND_BLOC);

			g2d.fillRoundRect(moi.blocDebut.x + xdecallage_x + 1, y_bas + 1, Math.abs(moi.x - moi.blocDebut.x) - xdecallage_x - 2, y_haut - y_bas - 2, 
					Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);
			g2d.setComposite(old);

		}

	}

	@Override
	public void dessine(PlancheUI planche) {

		if (visible) {

			Graphics2D g2d = ((Planche) planche).graphics;

			Composite old = g2d.getComposite();
			if (moi.glisser) {
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			}

			if (moi.survol && moi.zoneActive == null)
				g2d.setColor(couleur.brighter());
			if (moi.blocDebut.survol)
				g2d.setColor(couleur.darker().darker());
			else
				g2d.setColor(couleur);

			if (moi.blocDebut instanceof Condition) {
				Polygon triangle = new Polygon(new int[] { // 
						moi.x, // A
						moi.x, // B
						moi.x + moi.largeur // C
				}, new int[] { // 
						moi.y - hauteurCalculee / 2 + 20, // A
						moi.y + hauteurCalculee / 2 + 20, // B
						moi.y + 20// C
				}, 3);
				g2d.fillPolygon(triangle);
			} else {
				g2d.fillRoundRect(moi.x, // A
						moi.y - hauteurCalculee / 2 + 20, // B
						moi.largeur, // C
						hauteurCalculee, // D
						Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE
				);
			}

			// Attache après :
			if (!(moi.blocDebut instanceof Condition) && (moi.timbreSuivant != null)) {
				Polygon triangle = new Polygon(
						new int[] { //
								moi.x + moi.largeur, // A 
								moi.x + moi.largeur, // B
								moi.x + Abaque.ATTACHE_AVANT_TIMBRE / 2 + moi.largeur }, // C
						new int[] { // 
								moi.y + 20 - Abaque.ATTACHE_AVANT_TIMBRE / 2, // A
								moi.y + 20 + Abaque.ATTACHE_AVANT_TIMBRE / 2, // B
								moi.y + 20// C
				}, 3);
				g2d.fillPolygon(triangle);
			}

			//g2d.setColor(Palette.TEXTE);

			g2d.setComposite(old);
		}

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

	// Méthodes privées :
	// ******************

	private int trouvePositionYHaut(Timbre timbre, BlocFin fin, int profondeur) {
		if (timbre.timbreSuivant != fin && timbre.timbreSuivant != null) {
			int y = trouvePositionYHaut(timbre.timbreSuivant, fin, timbre.timbreSuivant instanceof BlocDebut ? profondeur + 1 : profondeur);
			if (y < recuperationPostionHaut(timbre, profondeur)) {
				return y;
			}
		}
		return recuperationPostionHaut(timbre, profondeur);
	}

	private int trouvePositionYBas(Timbre timbre, BlocFin fin, int profondeur) {
		if (timbre instanceof Condition && ((Condition) timbre).timbreSuivantSecondaire != fin) {
			int y = trouvePositionYBas(((Condition) timbre).timbreSuivantSecondaire, fin, profondeur + 1);
			if (y > (recuperationPostionYBas(timbre, profondeur)))
				return y;
		}
		if (timbre != null && timbre.timbreSuivant != fin && !(timbre instanceof Condition)) {
			int y = trouvePositionYBas(timbre.timbreSuivant, fin, timbre.timbreSuivant instanceof BlocDebut ? profondeur + 1 : profondeur);
			if (y > (recuperationPostionYBas(timbre, profondeur)))
				return y;
		}
		return recuperationPostionYBas(timbre, profondeur);
	}

	/**
	 * @param timbre
	 * @return
	 */
	private int recuperationPostionYBas(Timbre timbre, int profondeur) {
		if (timbre.glisser)
			return ((TimbreSimpleUI) (timbre.ui)).moi.init_y - 6 + ((TimbreSimpleUI) (timbre.ui)).decallage_y + ((TimbreSimpleUI) (timbre.ui)).moi.hauteur + 10
					+ 5 * profondeur;
		else
			return ((TimbreSimpleUI) (timbre.ui)).moi.y - 6 + ((TimbreSimpleUI) (timbre.ui)).decallage_y + ((TimbreSimpleUI) (timbre.ui)).moi.hauteur + 10
					+ 5 * profondeur;
	}

	private int recuperationPostionHaut(Timbre timbre, int profondeur) {
		if (timbre.glisser)
			return ((TimbreSimpleUI) (timbre.ui)).moi.init_y - 6 + ((TimbreSimpleUI) (timbre.ui)).decallage_y - 5 * profondeur;
		else
			return ((TimbreSimpleUI) (timbre.ui)).moi.y - 6 + ((TimbreSimpleUI) (timbre.ui)).decallage_y - 5 * profondeur;
	}

	@Override
	public void cache() {
		visible = false;
	}

}