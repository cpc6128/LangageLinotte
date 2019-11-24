package org.linotte.frame.cahier.timbre.ui.theme.simple;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JOptionPane;

import org.linotte.frame.cahier.timbre.entite.Acteur;
import org.linotte.frame.cahier.timbre.entite.Fonction;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.FonctionUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.theme.simple.boite.BoiteTexte;

public class FonctionSimpleUI extends TimbreSimple implements FonctionUI {

	public Fonction moi;

	public Color couleur = Palette.FONCTION;

	// Pour l'affichage du nom :
	private BoiteTexte texteBoite = new BoiteTexte(true);

	public FonctionSimpleUI(Fonction fonction) {
		moi = fonction;
		moi.largeur = Abaque.LARGEUR_TIMBRE;
		moi.hauteur = Abaque.HAUTEUR_TIMBRE;
	}

	@Override
	public void pre_dessine(PlancheUI planche) {
		Graphics2D g2d = ((Planche) planche).graphics;

		// Initialisation des boites :
		texteBoite.texte = moi.nom + " " + nomCompletMethode();
		texteBoite.calculer(g2d);

		int largeurMax = texteBoite.largeur;
		if (largeurMax > Abaque.LARGEUR_TIMBRE) {
			moi.largeur = 20 + largeurMax;
		} else {
			moi.largeur = Abaque.LARGEUR_TIMBRE;
		}

		_centrer(moi);
	}

	@Override
	public void dessine(PlancheUI planche) {

		Graphics2D g2d = _debutDessin(moi, planche);

		// fond de la fonction avec paramètre :
		List<Acteur> parametres = moi.retourneParametres();
		if (parametres.size() > 0 && !moi.glisser) {
			Acteur dernier = parametres.get(parametres.size() - 1);
			if (!dernier.glisser) {
				g2d.setColor(couleur.brighter().brighter());
				g2d.fillRoundRect(moi.x - 10, moi.y + decallage_y - 10, // 
						dernier.x + dernier.largeur - (moi.x) + 10 * 2, // 
						moi.hauteur + 10 * 2, // 
						Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);
			}
		}

		// Timbre
		_dessineFond(g2d, moi, couleur);

		// Accroche après le timbre :
		_dessinerAccroche(g2d, moi);

		texteBoite.dessine(g2d, moi.x + 10, moi.y + 50 + decallage_y, true);

		if (ZoneActive.PARAMETRES_FONCTION == moi.zoneActive) {
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(Palette.HALO_PARAMETRE_ACTIF);
			g2d.drawRect(moi.x, moi.y + 28 + decallage_y, moi.largeur - 2, 27);
			g2d.setStroke(new BasicStroke());
		} else {
			float dash1[] = { 10.0f };
			g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f));
			g2d.setColor(Palette.FOND_PLANCHE);
			g2d.drawRect(moi.x + 2, moi.y + 28 + decallage_y, moi.largeur - 4, 27);
			g2d.setStroke(new BasicStroke());
		}

		_finDessin(g2d);

	}

	/**
	 * @return
	 */
	private String nomCompletMethode() {
		List<Acteur> parametres = moi.retourneParametres();
		String label = "(";
		for (int i = 0; i < parametres.size(); i++) {
			label += parametres.get(i).texte;
			if (i + 1 != parametres.size())
				label += ", ";
		}
		label += ")";
		return label;
	}

	@Override
	public boolean clique(int x, int y) {
		Shape shape = new Rectangle2D.Double(moi.x, moi.y + decallage_y, moi.largeur, moi.hauteur);
		boolean clique = shape.contains(x, y);
		if (clique) {
			demanderNomFonction();
			((Timbre) moi).ui.repositionnerTousLesTimbres();
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

	private void demanderNomFonction() {
		String initValeur = moi.nom;
		final Object msg[] = { "Nom de la fonction :" };
		// Acteur anonyme texte :
		String valeur = (String) JOptionPane.showInputDialog(null, msg, "Modification du nom de la fonction", JOptionPane.QUESTION_MESSAGE, null, null,
				initValeur);
		if (valeur != null) {
			moi.nom = valeur;
		}
	}

	@Override
	public ZoneActive accepteZoneParametres(Acteur acteur) {
		int largeur = (moi.largeur - 6) / 2;
		{
			Shape shape = new Rectangle2D.Double(moi.x + 2 + largeur + largeur / 3, //
					moi.y + 30 + decallage_y, //
					largeur / 4, //
					25);
			boolean b = shape.intersects((Rectangle2D) ((Timbre) acteur).ui.forme());
			if (b) {
				return ZoneActive.PARAMETRES_FONCTION;
			}
		}
		return null;
	}
}
