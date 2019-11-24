package org.linotte.frame.cahier.timbre.ui.theme.simple;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.linotte.frame.cahier.timbre.entite.Acteur;
import org.linotte.frame.cahier.timbre.entite.BlocFin;
import org.linotte.frame.cahier.timbre.entite.Condition;
import org.linotte.frame.cahier.timbre.entite.Fonction;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.Verbe;
import org.linotte.frame.cahier.timbre.entite.e.Parfum;
import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.entite.i.NonSupprimable;
import org.linotte.frame.cahier.timbre.entite.i.Statique;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.i.TimbreUI;

/**
 * Affichage par défaut d'un timbre
 * @author cpc
 *
 */
public class TimbreSimpleUI implements TimbreUI {

	public Timbre moi;

	public int decallage_y = 0;

	protected TimbreSimpleUI(Timbre tg) {
		this.moi = tg;
	}

	@Override
	public final Shape forme() {
		return new Rectangle2D.Double(moi.x, moi.y, moi.largeur, moi.hauteur);
	}

	@Override
	public void pre_dessine(PlancheUI plancheUI) {

		Planche planche = ((Planche) plancheUI);
		Graphics2D g2d = planche.graphics;

		Composite old = g2d.getComposite();

		if (moi.glisser)
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));

		// Attachement :
		if (moi.timbreSuivant != null && moi.parfum != Parfum.RETOURNER) {

			if (moi.timbreSuivant.deboguage)
				g2d.setColor(Palette.DEBOGAGE);
			else
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
	public void dessine(PlancheUI plancheUI) {

		Planche planche = ((Planche) plancheUI);
		Graphics2D g2d = planche.graphics;

		Composite old = g2d.getComposite();

		if (moi.glisser) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2d.setColor(Palette.OMBRE);

			float dash1[] = { 10.0f };
			BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);

			g2d.setStroke(dashed);
			g2d.drawRoundRect(moi.init_x, moi.init_y + decallage_y, moi.largeur, moi.hauteur, 10, 10);
			g2d.setStroke(new BasicStroke());
		}

		// Glisser / Coller sur timbre
		if (ZoneActive.TIMBRE == moi.zoneActive || ZoneActive.CONDITION_SUIVANT_SECONDAIRE == moi.zoneActive) {
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g2d.setColor(Palette.HALO_TIMBRE);
			g2d.setStroke(new BasicStroke(7));
			g2d.drawRoundRect(moi.x - 6, moi.y - 6 + decallage_y, moi.largeur + 10, moi.hauteur + 10, 10, 10);
			g2d.setStroke(new BasicStroke());
		}

		if (ZoneActive.TIMBRE == moi.zoneActive && !(moi instanceof Condition)) { // bof le deuxième test...
			g2d.setStroke(new BasicStroke(3));
			g2d.setColor(Color.RED);
			g2d.drawOval(moi.x + moi.largeur - 10, moi.y + 20 - 10, 20, 20);
			g2d.setStroke(new BasicStroke());
		}

		// On dessine la croix permettant d'effacer le timbre :
		if (!moi.glisser && moi.survol && !(moi instanceof Statique) && !(moi instanceof NonSupprimable)) {
			g2d.setColor(Palette.FERMETURE);
			int taille = 10;
			g2d.setStroke(new BasicStroke(3));
			g2d.drawLine(moi.x + moi.largeur - 10 + 5, //
					moi.y - 10 + decallage_y + 5, //
					moi.x + moi.largeur - 10 + taille + 5, //
					moi.y - 10 + decallage_y + taille + 5); // \
			g2d.drawLine(moi.x + moi.largeur - 10 + 5, //
					moi.y - 10 + decallage_y + taille + 5, //
					moi.x + moi.largeur - 10 + taille + 5, //
					moi.y - 10 + decallage_y + 5); // /
			g2d.setStroke(new BasicStroke());
		}

		// Si deboguage :
		if (moi.deboguage || Abaque.DEBUG) {
			g2d.setColor(Palette.DEBOGAGE);
			g2d.setStroke(new BasicStroke(2));
			g2d.drawRoundRect(moi.x - 6, moi.y - 6 + decallage_y, moi.largeur + 10, moi.hauteur + 10, 10, 10);
			g2d.setStroke(new BasicStroke());
		}

		// Si erreur :
		if (moi.erreur || moi.erreurValidation) {
			g2d.setColor(Palette.TIMBRE_NON_VALIDE);
			g2d.setStroke(new BasicStroke(1));
			g2d.drawRoundRect(moi.x - 6, moi.y - 6 + decallage_y, moi.largeur + 10, moi.hauteur + 10, 10, 10);
			g2d.setStroke(new BasicStroke());
		}

		if (Abaque.DEBUG) {
			g2d.setColor(Palette.OMBRE);
			g2d.draw(ancreAvant());
		}

		if (Abaque.DEBUG) {
			g2d.setColor(Palette.OMBRE);
			g2d.draw(ancreArriere());
		}

		g2d.setComposite(old);

	}

	@Override
	public boolean contient(int x, int y) {
		// Vérification de la croix :
		if (!moi.glisser && moi.survol && !(moi instanceof Statique)) {
			Shape shape = new Ellipse2D.Double(moi.x + moi.largeur - 10, moi.y - 10 + decallage_y, 20, 20);
			if (shape.contains(x, y))
				return true;
		}

		Shape shape = new Rectangle2D.Double(moi.x, moi.y + decallage_y, moi.largeur, moi.hauteur);
		return shape.contains(x, y);
	}

	@Override
	public boolean accepteZoneTimbre(Timbre cible) {
		// Si bloque invisible, c'est faux :
		if (!(cible instanceof BlocFin && !((BlocFinSimpleUI) ((BlocFin) cible).ui).visible)) {
			return ancreAvant().intersects(cible.ui.ancreArriere().getBounds2D());
		} else {
			return false;
		}
	}

	@Override
	public boolean clique(int x, int y) {
		if (!(moi instanceof Statique) && !(moi instanceof NonSupprimable)) {
			Shape shape = new Ellipse2D.Double(moi.x + moi.largeur - 10, moi.y - 10 + decallage_y, 20, 20);
			if (shape.contains(x, y)) {
				int result = JOptionPane.showConfirmDialog(null, "Voulez-vous supprimer ce timbre ?", "Suppresion", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					// Sur la croix rouge, on supprime le timbre :
					Timbre premier = TimbresHelper.recherchePremiertimbre(moi);
					TimbresHelper.supprimerTimbre(moi);
					if (premier instanceof Fonction) {
						//TODO a déplacer ? je le garde ici pour l'instant pour des questions d'optimisation
						if (((Fonction) premier).parametres.contains(moi)) {
							((Fonction) premier).parametres.remove(moi);
						}
					}
					if (moi instanceof Acteur) {
						List<Timbre> o = ((Planche) moi.planche).timbres;
						for (Timbre timbre : o) {
							if (timbre instanceof Verbe) {
								Verbe v = (Verbe) timbre;
								Deposable[] parametres = v.parametres;
								for (int i = 0; i < parametres.length; i++) {
									if (parametres[i] == moi)
										parametres[i] = null;
								}
							}
						}
					}
					if (premier instanceof Fonction)
						premier.ui.repositionnerTousLesTimbres();
					else
						// On decalle tous les timbres à droite !
						premier.getUI().positionnerProchainTimbre();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void positionnerProchainTimbre() {

		moi.pre_dessine(); // On force les calcules.

		if (moi.timbreSuivant == null)
			return;

		if (moi.timbreSuivant instanceof BlocFin) {
			// Traitement dans ConditionUI
			return;
		}

		moi.timbreSuivant.x = moi.x + moi.largeur + Abaque.ESPACEMENT_TIMBRE;
		if (moi.timbreSuivant instanceof BlocFin) {
			int decallage_y = moi.timbreSuivant.hauteur / 2;
			moi.timbreSuivant.y = moi.y + decallage_y;
		} else {
			moi.timbreSuivant.y = moi.y;
		}

		if (moi.timbreSuivant.timbreSuivant != null)
			moi.timbreSuivant.getUI().positionnerProchainTimbre();

	}

	@Override
	public final Shape ancreAvant() {
		return new Ellipse2D.Double(moi.x, moi.y + 20 / 2, 20, 20);
	}

	@Override
	public final Shape ancreArriere() {
		// On  recupere le decallage de la cible :
		int decal = ((TimbreSimpleUI) moi.ui).decallage_y + moi.hauteur / 2 - 20 / 2;
		if (moi instanceof Condition )
			decal = - moi.hauteur / 2 + 10;// moche
		return new Ellipse2D.Double(moi.x + moi.largeur, moi.y + decal , 20, 20);
	}

	@Override
	public void repositionnerTousLesTimbres() {
		// Méthode non optimisée afin de rendre le code plus clair... à faire plus tard l'optimisation !
		Planche planche = (Planche) moi.planche;
		Set<Timbre> premiers = new HashSet<>();
		for (Timbre timbre : planche.timbres) {
			if (!premiers.contains(timbre) && TimbresHelper.timbreSeul(timbre)) {
				premiers.add(timbre);
			}
		}
		for (Timbre timbre : premiers) {
			timbre.ui.positionnerProchainTimbre();
		}
	}

}
