package org.linotte.frame.cahier.timbre.ui.theme.simple;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.linotte.frame.cahier.timbre.entite.BlocFin;
import org.linotte.frame.cahier.timbre.entite.Condition;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.ConditionUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.theme.simple.boite.BoiteActeur;

public class ConditionSimpleUI extends TimbreSimple implements ConditionUI {

	public Condition moi;

	public Color couleur = Palette.CONDITION;

	public int hauteurCalculee;

	// Pour l'affichage de la formule de la condition :
	private BoiteActeur formuleBoite = new BoiteActeur(ZoneActive.CONDITION_FORMULE);

	public ConditionSimpleUI(Condition condition) {
		moi = condition;
		moi.largeur = Abaque.LARGEUR_TIMBRE;
		moi.hauteur = Abaque.HAUTEUR_TIMBRE * 3;
	}

	@Override
	public void pre_dessine(PlancheUI plancheUI) {

		int index = calculerHauteur(moi.timbreSuivant) + calculerHauteur(moi.timbreSuivantSecondaire);
		hauteurCalculee = Abaque.HAUTEUR_TIMBRE * (3 + index);
		moi.hauteur = hauteurCalculee;
		BlocFinSimpleUI blocFinSimpleUI = (BlocFinSimpleUI) moi.blocfin.ui;
		blocFinSimpleUI.hauteurCalculee = hauteurCalculee;
		blocFinSimpleUI.moi.hauteur = hauteurCalculee;

		((TimbreSimpleUI) ((Timbre) moi).ui).decallage_y = -hauteurCalculee / 2 + 20;

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

		// Lien du timbreSuivant du SI

		// Attachement primaire :
		if (moi.timbreSuivant != null && !(moi.glisser && (moi.timbreSuivant instanceof BlocFin) && moi.timbreSuivant.timbreSuivant == null)) {

			if (moi.timbreSuivant.deboguage)
				g2d.setColor(Palette.DEBOGAGE);
			else
				g2d.setColor(Palette.ATTACHEMENT);

			g2d.setStroke(new BasicStroke(3));

			int decallage_y = -hauteurCalculee / 2 + Abaque.CONDITION_HAUTEUR_LIEN * 2;

			if (moi.glisser) {
				g2d.drawLine(moi.init_x + moi.largeur, moi.init_y + decallage_y, // A
						moi.timbreSuivant.x, moi.init_y + decallage_y);// B
			} else {
				if (moi.timbreSuivant.glisser) {
					g2d.drawLine(moi.x + moi.largeur, moi.y + decallage_y, // A
							moi.timbreSuivant.init_x, moi.y + decallage_y); // B
				} else {
					g2d.drawLine(moi.x + moi.largeur, moi.y + decallage_y, // A 
							moi.timbreSuivant.x, moi.y + decallage_y); // B
				}
			}
			g2d.setStroke(new BasicStroke());

		}

		// Attachement secondaire :
		if (moi.timbreSuivantSecondaire != null
				&& !(moi.glisser && (moi.timbreSuivantSecondaire instanceof BlocFin) && moi.timbreSuivantSecondaire.timbreSuivant == null)) {

			if (moi.timbreSuivantSecondaire.deboguage)
				g2d.setColor(Palette.DEBOGAGE);
			else
				g2d.setColor(Palette.ATTACHEMENT);

			g2d.setStroke(new BasicStroke(3));

			int decallage_y = hauteurCalculee / 2 + Abaque.CONDITION_HAUTEUR_LIEN_SECONDAIRE;

			if (moi.glisser) {
				g2d.drawLine(moi.init_x + moi.largeur, moi.init_y + decallage_y, // A
						moi.timbreSuivantSecondaire.x, moi.init_y + decallage_y);// B
			} else {
				if (moi.timbreSuivantSecondaire.glisser) {
					g2d.drawLine(moi.x + moi.largeur, moi.y + decallage_y, // A
							moi.timbreSuivantSecondaire.init_x, moi.y + decallage_y); // B
				} else {
					g2d.drawLine(moi.x + moi.largeur, moi.y + decallage_y, // A 
							moi.timbreSuivantSecondaire.x, moi.y + decallage_y); // B
				}
			}

			g2d.setStroke(new BasicStroke());

		}

		g2d.setComposite(old);
	}

	private int calculerHauteur(Timbre suivant) {
		if (suivant == null || (suivant instanceof BlocFin && ((BlocFin) suivant).blocDebut instanceof Condition)) // <- Que des Conditions
			return 0;
		else if (suivant instanceof Condition)
			return 1 + calculerHauteur(suivant.timbreSuivant) + calculerHauteur(((Condition) suivant).timbreSuivantSecondaire);
		else
			return 0 + calculerHauteur(suivant.timbreSuivant);
	}

	@Override
	public void dessine(PlancheUI planche) {

		Graphics2D g2d = _debutDessin(moi, planche);

		if (moi.survol && moi.zoneActive == null)
			g2d.setColor(couleur.brighter());
		else if (moi.blocfin.survol)
			g2d.setColor(couleur.darker().darker());
		else
			g2d.setColor(couleur);

		// Forme de la condition :

		Polygon triangle = new Polygon(
				new int[] { //
						moi.x + moi.largeur, // A 
						moi.x + moi.largeur, // B
						moi.x }, // C
				new int[] { // 
						moi.y - hauteurCalculee / 2 + 20, // A
						moi.y + hauteurCalculee / 2 + 20, // B
						moi.y + 20// C
		}, 3);
		g2d.fillPolygon(triangle);

		g2d.setColor(Color.BLACK);
		TimbresHelper.drawString(g2d, "si", moi.x - 3, moi.y + 5);
		{
			int decallage_y = -hauteurCalculee / 2 + Abaque.CONDITION_HAUTEUR_LIEN * 2;
			TimbresHelper.drawString(g2d, "alors", moi.x + moi.largeur - 40, moi.y + decallage_y + 10);
		}
		{
			int decallage_y = hauteurCalculee / 2 + Abaque.CONDITION_HAUTEUR_LIEN_SECONDAIRE;
			TimbresHelper.drawString(g2d, "sinon", moi.x + moi.largeur - 40, moi.y + decallage_y);
		}

		if (ZoneActive.TIMBRE == moi.zoneActive) {
			g2d.setStroke(new BasicStroke(3));
			g2d.setColor(Color.RED);
			int decallage_y = -hauteurCalculee / 2 + Abaque.CONDITION_HAUTEUR_LIEN * 2;
			g2d.drawOval(moi.x + moi.largeur - 10, moi.y + decallage_y - 10, 20, 20);
			g2d.setStroke(new BasicStroke());
		}

		if (ZoneActive.CONDITION_SUIVANT_SECONDAIRE == moi.zoneActive) {
			g2d.setStroke(new BasicStroke(3));
			g2d.setColor(Color.RED);
			int decallage_y = hauteurCalculee - +hauteurCalculee / 2;
			g2d.drawOval(moi.x + moi.largeur - 10, moi.y + decallage_y + 10, 20, 20);
			g2d.setStroke(new BasicStroke());
		}

		// Zone formule de la condition :
		formuleBoite.dessine(g2d, moi.x + 20, moi.y + 7 + decallage_y, moi.zoneActive, null);

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
		int decallage_y = -hauteurCalculee / 2;
		Shape shape = new Rectangle2D.Double(moi.x, moi.y + decallage_y, moi.largeur, hauteurCalculee / 2);
		return shape.contains(x, y);
	}

	@Override
	public boolean accepteSecondaire(Timbre cible) {
		return ancreArriereSecondaire().intersects(cible.ui.ancreAvant().getBounds2D());
	}

	public final Shape ancreArriereSecondaire() {
		int decallage_y = hauteurCalculee - +hauteurCalculee / 2 + Abaque.CONDITION_HAUTEUR_LIEN_SECONDAIRE - 5;
		return new Ellipse2D.Double(moi.x + moi.largeur - 10, moi.y + decallage_y, 20, 20);
	}

	@Override
	public void positionnerProchainTimbre() {

		moi.pre_dessine(); // On force les calcules.

		if (moi.timbreSuivant != null) {
			moi.timbreSuivant.x = moi.x + moi.largeur + Abaque.ESPACEMENT_TIMBRE;
			if (moi.timbreSuivant instanceof BlocFin) {
				moi.timbreSuivant.y = moi.y;
			} else {
				int decallage_y = -hauteurCalculee / 2;
				moi.timbreSuivant.y = moi.y + decallage_y;
			}

			if (moi.timbreSuivant.timbreSuivant != null)
				moi.timbreSuivant.getUI().positionnerProchainTimbre();
		}

		positionnerProchainTimbreSecondaire();

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

	private void positionnerProchainTimbreSecondaire() {

		if (moi.timbreSuivantSecondaire instanceof BlocFin) {
			((BlocFinSimpleUI) ((BlocFin) moi.timbreSuivantSecondaire).ui).visible = true;
			// Traitement dans ConditionUI
			return;
		}

		moi.timbreSuivantSecondaire.x = moi.x + moi.largeur + 10;
		if (moi.timbreSuivantSecondaire instanceof BlocFin) {
			int decallage_y = moi.timbreSuivantSecondaire.hauteur / 2;
			moi.timbreSuivantSecondaire.y = moi.y + decallage_y;
		} else {
			int decallage_y = hauteurCalculee / 2 - Abaque.CONDITION_HAUTEUR_LIEN + 10;
			moi.timbreSuivantSecondaire.y = moi.y + decallage_y;
		}

		if (moi.timbreSuivantSecondaire.timbreSuivant != null)
			moi.timbreSuivantSecondaire.getUI().positionnerProchainTimbre();
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
