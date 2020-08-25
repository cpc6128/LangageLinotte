package org.linotte.frame.cahier.timbre.ui.theme.simple;

import org.linotte.frame.cahier.timbre.entite.Verbe;
import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.i.VerbeUI;
import org.linotte.frame.cahier.timbre.ui.theme.simple.boite.BoiteActeur;
import org.linotte.frame.cahier.timbre.ui.theme.simple.boite.BoiteTexte;

import java.awt.*;

public class VerbeSimpleUI extends TimbreSimple implements VerbeUI {

	public Verbe moi;

	private Color couleur = Palette.VERBE;

	// Pour l'affichage du nom du verbe:
	private BoiteTexte texteBoite = new BoiteTexte(false);

	// Pour l'affichage du paramètre 1 :
	private BoiteActeur parametre1Boite = new BoiteActeur(ZoneActive.PARAMETRE_1);

	// Pour l'affichage du paramètre 2 :
	private BoiteActeur parametre2Boite = new BoiteActeur(ZoneActive.PARAMETRE_2);

	protected VerbeSimpleUI(Verbe verbe) {
		moi = verbe;
	}

	@Override
	public void pre_dessine(PlancheUI planche) {
		Graphics2D g2d = ((Planche) planche).graphics;

		moi.largeur = Abaque.LARGEUR_TIMBRE;
		moi.hauteur = Abaque.HAUTEUR_TIMBRE - (moi.nbParametres == 0 ? 20 : 0) + (moi.nbParametres * 20);

		// Initialisation des boites :

		texteBoite.texte = moi.texte;
		texteBoite.calculer(g2d);

		if (moi.nbParametres > 0) {
			// Premier paramètre :
			Deposable a = moi.parametres[0];
			parametre1Boite.initialiser(a);
			parametre1Boite.calculer(g2d);
			int largeur = parametre1Boite.largeur;

			// Deuxième paramètre :
			if (moi.nbParametres > 1) {
				Deposable a2 = moi.parametres[1];
				parametre2Boite.initialiser(a2);
				parametre2Boite.calculer(g2d);
				// nous prenons la plus grande des boites :
				largeur = Math.max(largeur, parametre2Boite.largeur);
			}

			if (largeur > Abaque.LARGEUR_TIMBRE - 10) {
				moi.largeur = Abaque.LARGEUR_TIMBRE + 10 + (largeur - Abaque.LARGEUR_TIMBRE);
			}
		}

		_centrer(moi);

	}

	@Override
	public void dessine(PlancheUI planche) {

		Graphics2D g2d = _debutDessin(moi, planche);

		Color couleur = changerCouleur();

		_dessineFond(g2d, moi, couleur);

		// Attache arriere :
		_dessinerAccroche(g2d, moi);

		// Affichage des paramètres :
		if (moi.nbParametres > 0) {
			// Paramètre 1 :
			parametre1Boite.dessine(g2d, moi.x + 2, moi.y + 30 + decallage_y, moi.zoneActive, null);
			// Paramètre 2 :
			if (moi.nbParametres > 1) {
				parametre2Boite.dessine(g2d, moi.x + 2, moi.y + 30 + Abaque.decalageParametre + decallage_y, moi.zoneActive, null);
			}
		}

		_finDessin(g2d);

	}

	private Color changerCouleur() {
		//TODO à déplacer ?
		// Timbre
		Color couleur = this.couleur;
		switch (((Verbe) moi).parfum) {
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
		return couleur;
	}

	@Override
	public boolean clique(int x, int y) {
		if (moi.nbParametres > 0) {
			Deposable retour;
			// Clique sur un paramètre 1 ?
			if (!((retour = parametre1Boite.clique(x, y)) != null)) {
				// Clique sur un paramètre 2 ?
				if (moi.nbParametres > 1) {
					// On doit mettre à jour le role de référence :
					
					retour = parametre2Boite.clique(x, y, parametre1Boite);
					if (retour != null) {
						moi.parametres[1] = retour;
					}
					return retour != null;
				}
			} else {
				if (retour != null)
					moi.parametres[0] = retour;
				return true;
			}
		}
		return false;
	}

	@Override
	public ZoneActive accepteZoneParametres(Deposable acteur) {
		if (moi.nbParametres > 0) {
			if (parametre1Boite.accepteZoneParametres(acteur)) {
				// Il faut éteindre les zones allumées en parametre_2 :
				parametre2Boite.eteindreZoneActive();
				return ZoneActive.PARAMETRE_1;
			}
			if (moi.nbParametres > 1 && parametre2Boite.accepteZoneParametres(acteur)) {
				return ZoneActive.PARAMETRE_2;
			}
		}
		return null;
	}

	@Override
	public boolean contient(int x, int y) {
		return false;
	}

	@Override
	public void positionnerProchainTimbre() {

	}

	@Override
	public boolean finGlisseDeposeParametre1Delegue(Deposable timbreGlisse) {
		return parametre1Boite.finGlisseDeposeDelegue(timbreGlisse);
	}

	@Override
	public boolean finGlisseDeposeParametre2Delegue(Deposable timbreGlisse) {
		return parametre2Boite.finGlisseDeposeDelegue(timbreGlisse);
	}
}