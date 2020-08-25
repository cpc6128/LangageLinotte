package org.linotte.frame.cahier.timbre.ui.theme.simple;

import org.linotte.frame.cahier.timbre.entite.BlocFin;
import org.linotte.frame.cahier.timbre.entite.TantQue;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.i.TantQueUI;
import org.linotte.frame.cahier.timbre.ui.theme.simple.boite.BoiteActeur;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.List;

public class TantQueSimpleUI extends TimbreSimple implements TantQueUI {

	public TantQue moi;

	public Color couleur = Palette.CONDITION;

	// Pour l'affichage de la formule de la condition :
	private BoiteActeur formuleBoite = new BoiteActeur(ZoneActive.CONDITION_FORMULE);

	protected TantQueSimpleUI(TantQue tantque) {
		moi = tantque;
	}

	@Override
	public void pre_dessine(PlancheUI plancheUI) {

		int index = calculerHauteur(moi.timbreSuivant);
		moi.hauteur = (int) (Abaque.HAUTEUR_TIMBRE * (1.5 + index));

		BlocFinSimpleUI blocFinSimpleUI = (BlocFinSimpleUI) moi.blocfin.ui;
		blocFinSimpleUI.hauteurCalculee = moi.hauteur;
		blocFinSimpleUI.moi.hauteur = moi.hauteur;

		decallage_y = -25 - (int) (Abaque.HAUTEUR_TIMBRE / 2 * (index));

		((TimbreSimpleUI) ((Timbre) moi).ui).decallage_y = decallage_y;

		Planche planche = ((Planche) plancheUI);
		Graphics2D g2d = planche.graphics;

		// On précalcule la largeur on fonction du texte de la condition :
		// En premier, il faut calculer la largeur :

		formuleBoite.initialiser(moi.formule);
		formuleBoite.calculer(g2d);
		int largeur = formuleBoite.largeur + 30;
		moi.largeur = Math.max(largeur, Abaque.LARGEUR_TIMBRE);

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

		if (moi.glisser)
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

		if (moi.survol && moi.zoneActive == null)
			g2d.setColor(couleur.brighter());
		else if (moi.blocfin.survol)
			g2d.setColor(couleur.darker().darker());
		else
			g2d.setColor(couleur);

		// Timbre de la boucle Tant Que :
		g2d.fillOval(moi.x, moi.y + decallage_y, moi.largeur, moi.hauteur);

		// Attache arriere :
		_dessinerAccroche(g2d, moi);

		g2d.setColor(Palette.TEXTE);
		TimbresHelper.drawString(g2d, "Tant que", moi.x + moi.largeur / 4, moi.y - moi.hauteur / 2 + 10);

		// Zone formule de la condition :
		formuleBoite.dessine(g2d, moi.x + 10, moi.y + 7, moi.zoneActive, null);

		_finDessin(g2d);
	}

	@Override
	public boolean clique(int x, int y) {
		Deposable retour = formuleBoite.clique(x, y);
		if (retour != null) {
			moi.formule = retour;
			TimbresHelper.recherchePremiertimbre(moi).getUI().positionnerProchainTimbre();
			return true;
		}
		return false;
	}

	@Override
	public boolean contient(int x, int y) {
		Shape shape = new Rectangle2D.Double(moi.x + 2, moi.y + 30, moi.largeur - 6, 25);
		boolean b = shape.contains(x, y);
		return b;
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

	private int calculerHauteur(Timbre suivant) {
		if (suivant == null || (suivant instanceof BlocFin && ((BlocFin) suivant).blocDebut instanceof TantQue)) // <- Que des Tant que
			return 0;
		else if (suivant instanceof TantQue)
			return 1 + calculerHauteur(suivant.timbreSuivant);
		else
			return 0 + calculerHauteur(suivant.timbreSuivant);
	}

	@Override
	public boolean accepteZoneFormule(Deposable timbreGraphique) {
		return formuleBoite.accepteZoneParametres(timbreGraphique);
	}

	@Override
	public boolean finGlisseDeposeFormule(Deposable timbreGlisse) {
		return formuleBoite.finGlisseDeposeDelegue(timbreGlisse);
	}

}
