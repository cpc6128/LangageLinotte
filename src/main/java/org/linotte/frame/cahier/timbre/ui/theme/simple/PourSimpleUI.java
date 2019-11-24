package org.linotte.frame.cahier.timbre.ui.theme.simple;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.List;

import org.linotte.frame.cahier.timbre.entite.BlocFin;
import org.linotte.frame.cahier.timbre.entite.Pour;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.i.PourUI;
import org.linotte.frame.cahier.timbre.ui.theme.simple.boite.BoiteActeur;

public class PourSimpleUI extends TimbreSimple implements PourUI {

	public Pour moi;

	public Color couleur = Palette.CONDITION;

	// Pour l'affichage de l'acteur :
	private BoiteActeur acteurBoite = new BoiteActeur(ZoneActive.POUR_ACTEUR);
	// Pour l'affichage de l'index début :
	private BoiteActeur indexDebutBoite = new BoiteActeur(ZoneActive.POUR_INDEX_DEBUT);
	// Pour l'affichage de l'index de fin :
	private BoiteActeur indexFinBoite = new BoiteActeur(ZoneActive.POUR_INDEX_FIN);

	protected PourSimpleUI(Pour pour) {
		moi = pour;
	}

	@Override
	public void pre_dessine(PlancheUI plancheUI) {

		int index = calculerHauteur(moi.timbreSuivant);
		moi.hauteur = (int) (Abaque.HAUTEUR_TIMBRE * (2 + (1 + index)));

		BlocFinSimpleUI blocFinSimpleUI = (BlocFinSimpleUI) moi.blocfin.ui;
		blocFinSimpleUI.hauteurCalculee = moi.hauteur;
		blocFinSimpleUI.moi.hauteur = moi.hauteur;

		decallage_y = -40 - (int) (Abaque.HAUTEUR_TIMBRE / 2 * (1 + index));

		((TimbreSimpleUI) ((Timbre) moi).ui).decallage_y = decallage_y;

		Planche planche = ((Planche) plancheUI);
		Graphics2D g2d = planche.graphics;

		// On précalcule la largeur on fonction du texte de la condition :
		// En premier, il faut calculer la largeur :

		acteurBoite.initialiser(moi.acteur);
		acteurBoite.calculer(g2d);
		int largeur = acteurBoite.largeur + 30;
		moi.largeur = Math.max(largeur, Abaque.LARGEUR_TIMBRE);

		indexDebutBoite.initialiser(moi.indexDebut);
		indexDebutBoite.calculer(g2d);
		largeur = indexDebutBoite.largeur + 30;
		moi.largeur = Math.max(largeur, moi.largeur);

		indexFinBoite.initialiser(moi.indexFin);
		indexFinBoite.calculer(g2d);
		largeur = indexFinBoite.largeur + 30;
		moi.largeur = Math.max(largeur, moi.largeur);

		Composite old = g2d.getComposite();

		if (moi.glisser)
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

		// Attachement :
		if (moi.timbreSuivant != null && !(moi.glisser && (moi.timbreSuivant instanceof BlocFin) && moi.timbreSuivant.timbreSuivant == null)) {
			g2d.setColor(Palette.ATTACHEMENT);
			g2d.setStroke(new BasicStroke(3));
			if (moi.glisser) {
				g2d.drawLine(moi.init_x + moi.largeur, moi.init_y + 20, moi.timbreSuivant.x, moi.init_y + 20);
			} else {
				if (moi.timbreSuivant.glisser) {
					g2d.drawLine(moi.x + moi.largeur, moi.y + 20, moi.timbreSuivant.init_x, moi.y + 20);
				} else {
					g2d.drawLine(moi.x + moi.largeur, moi.y + 20, moi.timbreSuivant.x, moi.y + 20);
				}
			}
			g2d.setStroke(new BasicStroke());

		}
		g2d.setComposite(old);

	}

	@Override
	public void dessine(PlancheUI planche) {

		Graphics2D g2d = _debutDessin(moi, planche);

		// Timbre
		_dessineFond(g2d, moi, couleur);

		// Accroche après le timbre :
		_dessinerAccroche(g2d, moi);

		g2d.setColor(Palette.TEXTE);
		TimbresHelper.drawString(g2d, "Pour", moi.x + moi.largeur / 3, moi.y - moi.hauteur / 2 + 10);

		int hauteurActeur;

		// Zone ACTEUR de la boucle FOR :
		// ******************************
		hauteurActeur = 10 + decallage_y;
		acteurBoite.dessine(g2d, moi.x + 10, moi.y + hauteurActeur, moi.zoneActive, null);

		// Zone INDEX DEBUT de la boucle FOR :
		// ***********************************
		hauteurActeur = 10 + Abaque.POUR_DISTANCE_ACTEURS + decallage_y;
		indexDebutBoite.dessine(g2d, moi.x + 10, moi.y + hauteurActeur, moi.zoneActive, null);
		g2d.setColor(Palette.TEXTE);
		TimbresHelper.drawString(g2d, "allant de", moi.x + 12, moi.y + hauteurActeur + 15 - 30);

		// Zone INDEX FIN de la boucle FOR :
		// *********************************
		hauteurActeur = 10 + Abaque.POUR_DISTANCE_ACTEURS * 2 + decallage_y;
		indexFinBoite.dessine(g2d, moi.x + 10, moi.y + hauteurActeur, moi.zoneActive, null);
		TimbresHelper.drawString(g2d, "à", moi.x + 12, moi.y + hauteurActeur + 15 - 30);

		g2d.setComposite(old);
	}

	@Override
	public boolean clique(int x, int y) {

		Deposable retour = acteurBoite.clique(x, y);
		if (retour != null) {
			moi.acteur = retour;
			TimbresHelper.recherchePremiertimbre(moi).getUI().positionnerProchainTimbre();
			return true;
		}
		retour = indexDebutBoite.clique(x, y, acteurBoite);
		if (retour != null) {
			moi.indexDebut = retour;
			TimbresHelper.recherchePremiertimbre(moi).getUI().positionnerProchainTimbre();
			return true;
		}
		retour = indexFinBoite.clique(x, y, acteurBoite);
		if (retour != null) {
			moi.indexFin = retour;
			TimbresHelper.recherchePremiertimbre(moi).getUI().positionnerProchainTimbre();
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

		moi.pre_dessine(); // On force les calcules.

		if (moi.timbreSuivant != null) {

			moi.timbreSuivant.x = moi.x + moi.largeur + Abaque.ESPACEMENT_TIMBRE;
			moi.timbreSuivant.y = moi.y;

			if (moi.timbreSuivant.timbreSuivant != null)
				moi.timbreSuivant.getUI().positionnerProchainTimbre();

			if (moi.timbreSuivant instanceof BlocFin) {
				((BlocFinSimpleUI) ((BlocFin) moi.timbreSuivant).ui).visible = true;
				return;
			}

		}

		// Les blocs ont été décallés, on s'occupe du blocFin :
		BlocFin blocfin = moi.blocfin;
		List<Timbre> precedents = TimbresHelper.timbresPrecedents(blocfin);
		if (!precedents.isEmpty()) {
			// On prend celui le plus à droite :
			Timbre plusADroite = precedents.get(0);
			for (Timbre timbre : precedents) {
				if ((timbre.x + timbre.largeur) > (plusADroite.x + plusADroite.largeur)) {
					plusADroite = timbre;
				}
			}
			blocfin.x = plusADroite.x + plusADroite.largeur + Abaque.ESPACEMENT_TIMBRE;
			blocfin.y = moi.y;
		}
		blocfin.getUI().positionnerProchainTimbre();
	}

	@Override
	public ZoneActive accepteZoneParametres(Deposable acteur) {

		if (acteurBoite.accepteZoneParametres(acteur)) {
			// Il faut éteindre les autres zones allumées  :
			indexDebutBoite.eteindreZoneActive();
			indexFinBoite.eteindreZoneActive();
			return ZoneActive.POUR_ACTEUR;
		} else if (indexDebutBoite.accepteZoneParametres(acteur)) {
			// Il faut éteindre les autres zones allumées  :
			indexFinBoite.eteindreZoneActive();
			return ZoneActive.POUR_INDEX_DEBUT;
		} else if (indexFinBoite.accepteZoneParametres(acteur)) {
			return ZoneActive.POUR_INDEX_FIN;
		}

		return null;
	}

	private int calculerHauteur(Timbre suivant) {
		if (suivant == null || (suivant instanceof BlocFin && ((BlocFin) suivant).blocDebut instanceof Pour)) // <- Que des "Pour"
			return 0;
		else if (suivant instanceof Pour)
			return 1 + calculerHauteur(suivant.timbreSuivant);
		else
			return 0 + calculerHauteur(suivant.timbreSuivant);
	}

	@Override
	public boolean finGlisseDeposeActeur(Deposable timbreGlisse) {
		return acteurBoite.finGlisseDeposeDelegue(timbreGlisse);
	}

	@Override
	public boolean finGlisseDeposeIndexDebut(Deposable timbreGlisse) {
		return indexDebutBoite.finGlisseDeposeDelegue(timbreGlisse);
	}

	@Override
	public boolean finGlisseDeposeIndexFin(Deposable timbreGlisse) {
		return indexFinBoite.finGlisseDeposeDelegue(timbreGlisse);
	}

}