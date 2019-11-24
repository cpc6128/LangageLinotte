package org.linotte.frame.cahier.timbre.ui.theme.simple.boite;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.JOptionPane;

import org.linotte.frame.cahier.timbre.entite.Textuel;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.theme.simple.Abaque;
import org.linotte.frame.cahier.timbre.ui.theme.simple.Palette;

public class BoiteTexte extends Boite {

	public String texte = null;

	public int largeur = 0;

	public int hauteur = 25;

	public int x;

	public int y;

	public BoiteTexte(boolean cliquable) {
	}

	@Override
	public void calculer(Graphics2D g2d) {
		largeur = g2d.getFontMetrics().stringWidth(texte) + 20;
	}

	public void dessine(Graphics2D g2d, int x, int y, boolean afficheTexte) {
		//afficheTexte : c'est Textuel qui s'occupe de l'affichage (version 2.5.1)
		this.x = x;
		this.y = y;
		if (afficheTexte) {
			g2d.setColor(Palette.TEXTE);
			TimbresHelper.drawString(g2d, texte, x, y);
		}
		if (Abaque.DEBUG)
			g2d.fillRect(x, y, largeur, hauteur);
	}

	public boolean clique(Textuel moi, int x, int y) {
		Shape shape = new Rectangle2D.Double(this.x, this.y, largeur, hauteur);
		boolean clique = shape.contains(x, y);
		if (clique) {
			// Acteur anonyme texte :
			String initValeur = moi.texte == null ? "" : moi.texte;

			String valeur = (String) JOptionPane.showInputDialog(null, "Nom de la variable ?", "Modification d'une variable", JOptionPane.QUESTION_MESSAGE,
					null, null, initValeur);
			if (valeur != null)
				moi.texte = valeur;
			// La largeur du texte peut modifier la largeur du timbre et décaller le prochaine timbre :
			Timbre timbre = TimbresHelper.recherchePremiertimbre(moi);
			if (timbre.timbreSuivant != null)
				timbre.timbreSuivant.getUI().positionnerProchainTimbre();
			return true;
		}
		return false;
	}

}
