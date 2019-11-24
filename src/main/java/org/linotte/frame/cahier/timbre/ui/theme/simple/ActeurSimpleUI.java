package org.linotte.frame.cahier.timbre.ui.theme.simple;

import java.awt.Color;
import java.awt.Graphics2D;

import org.linotte.frame.cahier.timbre.entite.Acteur;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.e.Role;
import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.ActeurUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.theme.simple.boite.BoiteActeur;
import org.linotte.frame.cahier.timbre.ui.theme.simple.boite.BoiteTexte;

public class ActeurSimpleUI extends TimbreSimple implements ActeurUI {

	private final static Color couleur = Palette.ACTEUR_TEXTE;

	public Acteur moi;

	// Pour l'affichage du nom :
	private BoiteTexte texteBoite = new BoiteTexte(true);

	// Pour l'affichage de la valeur :
	private BoiteActeur acteurBoite = new BoiteActeur(ZoneActive.POUR_ACTEUR);

	protected ActeurSimpleUI(Acteur acteur) {
		moi = acteur;
	}

	@Override
	public void pre_dessine(PlancheUI planche) {
		Graphics2D g2d = ((Planche) planche).graphics;

		// Initialisation des boites :
		texteBoite.texte = moi.texte;
		texteBoite.calculer(g2d);

		acteurBoite.initialiser(moi, moi.valeur, moi.role);
		acteurBoite.calculer(g2d);

		// Calculer la largeur :

		int largeurMax = Math.max(texteBoite.largeur, acteurBoite.largeur);
		if (largeurMax > Abaque.LARGEUR_TIMBRE - 10) {
			moi.largeur = Abaque.LARGEUR_TIMBRE + 10 + (largeurMax - Abaque.LARGEUR_TIMBRE);
		} else {
			moi.largeur = Abaque.LARGEUR_TIMBRE;
		}

		// Calculer la hauteur :
		if (moi.estCeUnParametre()) {
			moi.hauteur = Abaque.HAUTEUR_TIMBRE / 2 + 10;
		} else {
			moi.hauteur = Abaque.HAUTEUR_TIMBRE + 10;
		}

		_centrer(moi);

	}

	@Override
	public void dessine(PlancheUI planche) {

		Graphics2D g2d = _debutDessin(moi, planche);

		Color couleur = changerCouleur();

		// Timbre
		_dessineFond(g2d, moi, couleur);

		// Accroche après le timbre :
		_dessinerAccroche(g2d, moi);

		// Rôle :
		g2d.setColor(Palette.ROLE);
		TimbresHelper.drawString(g2d, moi.role.name(), moi.x + 10, moi.y + moi.hauteur - 6 + decallage_y);

		texteBoite.dessine(g2d, moi.x + 5, moi.y + 5 + decallage_y, false);

		if (!moi.estCeUnParametre() && moi.role != Role.CRAYON) {
			acteurBoite.dessine(g2d, moi.x + 10, moi.y + moi.hauteur / 2 + 7 + decallage_y - 20, moi.zoneActive, null);
		}

		_finDessin(g2d);
	}

	@Override
	public boolean clique(int x, int y) {
		// Clique sur le nom
		if (!(texteBoite.clique(moi, x, y))) {
			// Clique sur la valeur
			boolean retour = acteurBoite.clique(x, y) != null;
			if (retour)
				((Timbre) moi).ui.repositionnerTousLesTimbres();
			return retour;
		} else {
			((Timbre) moi).ui.repositionnerTousLesTimbres();
			return true;
		}
	}

	@Override
	public boolean contient(int x, int y) {
		return false;
	}

	@Override
	public void positionnerProchainTimbre() {

	}

	private Color changerCouleur() {
		//TODO à déplacer ?
		// Timbre
		Color couleur = ActeurSimpleUI.couleur;
		if (Role.CRAYON == ((Acteur) moi).role) {
			couleur = Palette.ACTEUR_GRAPHIQUE.darker();
		}
		return couleur;
	}

}
