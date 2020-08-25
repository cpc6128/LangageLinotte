package org.linotte.frame.cahier.timbre.ui.theme.simple;

import org.linotte.frame.cahier.timbre.entite.*;
import org.linotte.frame.cahier.timbre.entite.e.Parfum;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.BoutonUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class BoutonSimpleUI implements BoutonUI {

	public Bouton moi;

	public BoutonSimpleUI(Bouton bouton) {
		moi = bouton;
		moi.largeur = Abaque.LARGEUR_TIMBRE;
		moi.hauteur = Abaque.LARGEUR_TIMBRE / 4 + 3;
	}

	@Override
	public void pre_dessine(PlancheUI planche) {
	}

	@Override
	public void dessine(PlancheUI planche) {

		Graphics2D g2d = ((Planche) planche).graphics;

		boolean accroche = false;

		Composite old = g2d.getComposite();
		if (moi.glisser)
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

		Color couleur = Palette.VERBE.darker();
		if (moi.original instanceof Verbe) {
			switch (((Verbe) moi.original).parfum) {
			case PARCOURIR:
			case REVENIR:
				couleur = Palette.VERBE.brighter();
				break;
			case DEPLACE_AVANT:
			case DEPLACE_BAS:
			case DEPLACE_DROITE:
			case DEPLACE_GAUCHE:
			case DEPLACE_HAUT:
			case DEPLACE_VERS:
			case TOURNE_DROITE:
			case TOURNE_GAUCHE:
			case LEVER_CRAYON:
			case BAISSER_CRAYON:
				couleur = Palette.VERBE_GRAPHIQUE.darker();
				break;
			}
		} else if (moi.original instanceof Acteur) {
			switch (((Acteur) moi.original).role) {
			case TEXTE:
			case NOMBRE:
				couleur = Palette.ACTEUR_NOMBRE.darker();
				break;
			case CRAYON:
				couleur = Palette.ACTEUR_GRAPHIQUE.darker();
				break;
			case FORMULE:
				couleur = Palette.ACTEUR_FORMULE.darker();
				break;
			}
		} else if (moi.original instanceof Condition) {
			couleur = Palette.CONDITION.darker();
		} else if (moi.original instanceof TantQue) {
			couleur = Palette.CONDITION.darker();
		} else if (moi.original instanceof Pour) {
			couleur = Palette.CONDITION.darker();
		} else if (moi.original instanceof Commentaire) {
			couleur = Palette.COMMENTAIRE.darker();
		} else if (moi.original instanceof Fonction) {
			couleur = Palette.FONCTION.darker();
		} else if (moi.parfum == Parfum.CATEGORIE_BOUCLE) {
			couleur = Palette.CONDITION.darker();
			accroche = ((Planche) planche).boutonsParfum == Parfum.CATEGORIE_BOUCLE;
		} else if (moi.parfum == Parfum.CATEGORIE_GRAPHIQUE) {
			couleur = Palette.VERBE_GRAPHIQUE.darker();
			accroche = ((Planche) planche).boutonsParfum == Parfum.CATEGORIE_GRAPHIQUE;
		} else if (moi.parfum == Parfum.CATEGORIE_BASE) {
			//couleur = Palette.VERBE_GRAPHIQUE.darker();
			accroche = ((Planche) planche).boutonsParfum == Parfum.CATEGORIE_BASE;
		} else if (moi.parfum == Parfum.UNDO) {
			couleur = ((Planche) planche).historique.estceVide() ? Palette.UNDO_INACTIF : Palette.UNDO;
			int nb = ((Planche) planche).historique.taille();
			moi.label = "Annuler (" + nb + ")";
		}

		if (moi.survol && moi.zoneActive == null)
			g2d.setColor(couleur.brighter());
		else
			g2d.setColor(couleur);

		g2d.fillRoundRect(moi.x, moi.y, moi.largeur, moi.hauteur, Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);

		if (accroche) {
			// Accroche après le timbre :
			Polygon triangle = new Polygon(new int[] { //
					moi.x + moi.largeur, // A 
					moi.x + moi.largeur, // B
					moi.x + Abaque.ATTACHE_AVANT_TIMBRE / 2 + moi.largeur }, // C
					new int[] { // 
							moi.y + moi.hauteur / 2 - Abaque.ATTACHE_AVANT_TIMBRE / 2, // A
							moi.y + moi.hauteur / 2 + Abaque.ATTACHE_AVANT_TIMBRE / 2, // B
							moi.y + moi.hauteur / 2// C
					}, 3);
			g2d.fillPolygon(triangle);
		}

		g2d.setColor(Palette.TEXTE);
		TimbresHelper.drawString(g2d, moi.label, moi.x + 10, moi.y + 15);

		g2d.setComposite(old);

	}

	@Override
	public boolean clique(int x, int y) {
		return false;
	}

	@Override
	public boolean contient(int x, int y) {
		Shape shape = new Rectangle2D.Double(moi.x, moi.y, moi.largeur, moi.hauteur);
		return shape.contains(x, y);
	}

	@Override
	public void positionnerProchainTimbre() {
	}
}
