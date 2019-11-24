package org.linotte.frame.cahier.timbre.ui.theme.simple;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;

import org.linotte.frame.cahier.timbre.entite.Commentaire;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.CommentaireUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class CommentaireSimpleUI implements CommentaireUI {

	public Commentaire moi;

	public Color couleur = Palette.COMMENTAIRE;

	private int decallage_y;

	public CommentaireSimpleUI(Commentaire commentaire) {
		moi = commentaire;
		moi.largeur = Abaque.LARGEUR_TIMBRE * 2;
		moi.hauteur = Abaque.HAUTEUR_TIMBRE;
	}

	@Override
	public void pre_dessine(PlancheUI planche) {
		Graphics2D g2d = ((Planche) planche).graphics;
		decallage_y = -moi.hauteur / 2 + 20;
		((TimbreSimpleUI) ((Timbre) moi).ui).decallage_y = decallage_y;

		int largeurMax = g2d.getFontMetrics().stringWidth(moi.texte);
		if (largeurMax > Abaque.LARGEUR_TIMBRE * 2 - 10) {
			moi.largeur = 20 + largeurMax;
		} else {
			moi.largeur = Abaque.LARGEUR_TIMBRE * 2;
		}
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

		// Timbre :
		g2d.fillRoundRect(moi.x, moi.y + decallage_y, moi.largeur, moi.hauteur, Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);

		if (moi.timbreSuivant != null) {

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
		}
		g2d.setComposite(old);

	}

	@Override
	public boolean clique(int x, int y) {
		Shape shape = new Rectangle2D.Double(moi.x, moi.y + decallage_y, moi.largeur, moi.hauteur);
		boolean clique = shape.contains(x, y);
		if (clique) {
			demanderValeur();
			return true;
		}
		return false;
	}

	@Override
	public boolean contient(int x, int y) {
		return false;
	}

	@Override
	public void positionnerProchainTimbre() {
	}

	private void demanderValeur() {
		String initValeur = moi.texte;
		final Object msg[] = { "Commentaire :" };
		// Acteur anonyme texte :
		String valeur = (String) JOptionPane.showInputDialog(null, msg, "Modification du commentaire", JOptionPane.QUESTION_MESSAGE, null, null, initValeur);
		if (valeur != null) {
			moi.texte = valeur;
		}
	}

}
