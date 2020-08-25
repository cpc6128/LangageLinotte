package org.linotte.frame.cahier.timbre.outils;

import org.linotte.frame.cahier.timbre.JPanelPlancheATimbre;
import org.linotte.frame.cahier.timbre.entite.*;
import org.linotte.frame.cahier.timbre.entite.e.Parfum;
import org.linotte.frame.cahier.timbre.entite.e.Role;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.theme.simple.Abaque;
import org.linotte.frame.latoile.dessinateur.ParcheminDessinateur;
import org.linotte.frame.latoile.dessinateur.RectangleDessinateur;
import org.linotte.frame.outils.FontHelper;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.PrototypeGraphique.TYPE_GRAPHIQUE;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Classe manipulant les timbres 
 * 
 * @author CPC
 *
 */
public class TimbresHelper {

	/**
	 * @param boutons_fixes 
	 * @param planche 
	 * 
	 */
	public static void constructionBoutons(Planche planche, JPanelPlancheATimbre panel) {
		planche.boutons_fixes_toujours_visibles.add(new Bouton("Actions...", Parfum.CATEGORIE_BASE, planche, panel));
		planche.boutons_fixes_toujours_visibles.add(new Bouton("Structure...", Parfum.CATEGORIE_BOUCLE, planche, panel));
		planche.boutons_fixes_toujours_visibles.add(new Bouton("Graphisme...", Parfum.CATEGORIE_GRAPHIQUE, planche, panel));
		planche.boutons_fixes_toujours_visibles.add(new Bouton("Variable texte", new Acteur(null, Role.TEXTE, "", planche), planche));
		planche.boutons_fixes_toujours_visibles.add(new Bouton("Variable nombre", new Acteur(null, Role.NOMBRE, "0", planche), planche));
		planche.boutons_fixes_toujours_visibles.add(new Bouton("Variable crayon", new Espece(null, Role.CRAYON, planche), planche));
		planche.boutons_fixes_toujours_visibles.add(new Bouton("Commentaire", new Commentaire(planche), planche));
		planche.boutons_fixes_toujours_visibles.add(new Bouton("Annuler action", Parfum.UNDO, planche, panel));
		planche.boutons_fixes_toujours_visibles.add(new Bouton("Aide", Parfum.AIDE, planche, panel));

		planche.boutons_fixes_boucle.add(new Bouton("Condition", new Condition(planche), planche));
		planche.boutons_fixes_boucle.add(new Bouton("Tant que", new TantQue(planche), planche));
		planche.boutons_fixes_boucle.add(new Bouton("Pour", new Pour(planche), planche));
		planche.boutons_fixes_boucle.add(new Bouton("Fonction", new Fonction(planche), planche));

		createButton(planche.boutons_fixes_base, planche, Parfum.AFFICHER);
		createButton(planche.boutons_fixes_base, planche, Parfum.DEMANDER);
		createButton(planche.boutons_fixes_base, planche, Parfum.VALOIR);
		createButton(planche.boutons_fixes_base, planche, Parfum.EFFACERTABLEAU);
		createButton(planche.boutons_fixes_base, planche, Parfum.RETOURNER);
		createButton(planche.boutons_fixes_base, planche, Parfum.PARCOURIR);
		createButton(planche.boutons_fixes_base, planche, Parfum.REVENIR);

		createButton(planche.boutons_fixes_graphiques, planche, Parfum.DEPLACE_HAUT);
		createButton(planche.boutons_fixes_graphiques, planche, Parfum.DEPLACE_BAS);
		createButton(planche.boutons_fixes_graphiques, planche, Parfum.DEPLACE_DROITE);
		createButton(planche.boutons_fixes_graphiques, planche, Parfum.DEPLACE_GAUCHE);
		createButton(planche.boutons_fixes_graphiques, planche, Parfum.DEPLACE_AVANT);
		//createButton(planche.boutons_fixes_graphiques, planche, Parfum.DEPLACE_VERS);
		createButton(planche.boutons_fixes_graphiques, planche, Parfum.TOURNE_DROITE);
		createButton(planche.boutons_fixes_graphiques, planche, Parfum.TOURNE_GAUCHE);
		createButton(planche.boutons_fixes_graphiques, planche, Parfum.LEVER_CRAYON);
		createButton(planche.boutons_fixes_graphiques, planche, Parfum.BAISSER_CRAYON);

		planche.recommencerCompteur();

		int y = 10;
		for (Timbre timbre : planche.boutons_fixes_toujours_visibles) {
			timbre.x = 10;
			timbre.y = y;
			y = y + timbre.hauteur + 2;
		}
		y = 10;
		for (Timbre timbre : planche.boutons_fixes_base) {
			timbre.x = 10;
			timbre.y = y;
			y = y + timbre.hauteur + 2;
		}
		y = 10;
		for (Timbre timbre : planche.boutons_fixes_graphiques) {
			timbre.x = 10;
			timbre.y = y;
			y = y + timbre.hauteur + 2;
		}
		y = 10;
		for (Timbre timbre : planche.boutons_fixes_boucle) {
			timbre.x = 10;
			timbre.y = y;
			y = y + timbre.hauteur + 2;
		}

		planche.boutons_fixes_actifs.addAll(planche.boutons_fixes_base);

	}

	private static void createButton(List<Timbre> list, Planche planche, Parfum parfum) {
		list.add(new Bouton(parfum.titre, new Verbe(parfum, parfum.titre, parfum.nb, planche), planche));
	}

	/**
	 * @param moi
	 */
	public static void supprimerTimbre(final Timbre moi) {

		//((Planche) moi.planche).historique.ajouterEntree();

		// Je me supprime de l'affichage :
		((Planche) moi.planche).timbres.remove(moi);
		// Si c'est une condition :
		if (moi instanceof BlocDebut) {
			//TODO c'est assez moche... et pas optimisé. A revoir
			BlocFin blocfin = supprimerTimbreCascadeBloc(moi.timbreSuivant);
			if (moi instanceof Condition)
				supprimerTimbreCascadeBloc(((Condition) moi).timbreSuivantSecondaire);
			moi.timbreSuivant = blocfin.timbreSuivant;
			supprimerTimbre(blocfin);

		}
		// Je me supprime en tant que suivant :
		for (Timbre timbre : ((Planche) moi.planche).timbres) {
			if (timbre.timbreSuivant == moi) {
				timbre.timbreSuivant = moi.timbreSuivant;
			}
			if (timbre instanceof Condition) {
				Condition condition = (Condition) timbre;
				if (condition.timbreSuivantSecondaire == moi) {
					condition.timbreSuivantSecondaire = moi.timbreSuivant;
				}
			}
		}
	}

	private static BlocFin supprimerTimbreCascadeBloc(final Timbre timbre) {
		Timbre suivant = timbre;
		while (!(suivant instanceof BlocFin)) {
			supprimerTimbre(suivant);
			suivant = suivant.timbreSuivant;
		}
		return (BlocFin) suivant;

	}

	/**
	 * Cette méthode est appelée quand on attache un timbre à un autre timbre à la fin d'un glissé-collé.
	 * @param source
	 * @param destination
	 */
	public static void deplacerUnTimbre(final Timbre source, final Timbre destination) {
		if (!(destination.timbreSuivant == source) && !(destination instanceof BlocFin && ((BlocFin) destination).blocDebut == source)) {

			// Pas déjà attaché au même timbre

			Timbre ancienTimbreSuivantDeDestination = null; // D
			if (destination.timbreSuivant != null) {
				// Timbre qui sera remplacé
				ancienTimbreSuivantDeDestination = destination.timbreSuivant; // B
			}

			// Vérification si je ne suis pas le premier d'une série :
			for (Timbre timbre : ((Planche) source.planche).timbres) {
				if (timbre.timbreSuivant == source) {
					// Je ne suis pas premier !
					// [A] -> [source] -> [C] devient [A] -> [C]
					timbre.timbreSuivant = source.timbreSuivant;
					source.timbreSuivant = null;
					// On decalle tous les timbres à droite !
					recherchePremiertimbre(timbre).getUI().positionnerProchainTimbre();
				}
				// Cas des Conditions :
				if (timbre instanceof Condition && ((Condition) timbre).timbreSuivantSecondaire == source) {
					// Je ne suis pas premier !
					// [A] -> [source] -> [C] devient [A] -> [C]
					((Condition) timbre).timbreSuivantSecondaire = source.timbreSuivant;
					source.timbreSuivant = null;
					// On decalle tous les timbres à droite !
					recherchePremiertimbre(timbre).getUI().positionnerProchainTimbre();
				}
			}

			if (destination.timbreSuivant == null && source instanceof BlocDebut) {
				// A -> B devient B -> A
				((BlocDebut) source).blocfin.timbreSuivant = null;
			}

			if (destination.timbreSuivant == null && !(source instanceof BlocDebut)) {
				// A -> B devient B -> A
				source.timbreSuivant = null;
			}
			destination.timbreSuivant = source; // C

			if (ancienTimbreSuivantDeDestination != null) { // On décalle ce timbre à la fin des timbres insérés
				if (source.timbreSuivant == destination) {
					source.timbreSuivant = ancienTimbreSuivantDeDestination;
				} else {
					Timbre suivant = source.timbreSuivant;
					if (suivant != null) {
						while (suivant.timbreSuivant != null) {
							suivant = suivant.timbreSuivant;
						}
						suivant.timbreSuivant = ancienTimbreSuivantDeDestination;
					} else {
						source.timbreSuivant = ancienTimbreSuivantDeDestination;
					}
				}
			}

		}

	}

	public static boolean timbreSeul(Timbre moi) {
		for (Timbre timbre : ((Planche) moi.planche).timbres) {
			if (timbre.timbreSuivant == moi) {
				return false;
			}
			if (timbre instanceof Condition && ((Condition) timbre).timbreSuivantSecondaire == moi) {
				return false;
			}
		}
		return true;
	}

	public static Timbre recherchePremiertimbre(Timbre moi) {
		List<Timbre> timbres = timbresPrecedents(moi);
		if (timbres.isEmpty())
			return moi;
		else
			return recherchePremiertimbre(timbres.get(0));
	}

	public static List<Timbre> timbresPrecedents(Timbre moi) {
		List<Timbre> timbres = new ArrayList<Timbre>();
		for (Timbre timbre : ((Planche) moi.planche).timbres) {
			if (timbre.timbreSuivant == moi && !timbres.contains(timbre)) {
				timbres.add(timbre);
			}
			if (timbre instanceof Condition && (((Condition) timbre).timbreSuivantSecondaire) == moi && !timbres.contains(timbre)) {
				timbres.add(timbre);
			}
		}
		return timbres;
	}

	/**
	 * Cette méthode est appelée quand on attache un timbre SECONDAIRE à un autre timbre (@Condition) à la fin d'un glissé-collé.
	 * @param source
	 * @param destination
	 */
	public static void deplacerUnTimbreSecondaire(final Timbre source, final Condition destination) {

		if (!(destination.timbreSuivantSecondaire == source)) {

			//((Planche) source.planche).historique.ajouterEntree();

			// Pas déjà attaché au même timbre

			Timbre ancienTimbreSuivantDeDestination = null; // D
			if (destination.timbreSuivantSecondaire != null) {
				// Timbre qui sera remplacé
				ancienTimbreSuivantDeDestination = destination.timbreSuivantSecondaire; // B
			}

			// Vérification si je ne suis pas le premier d'une série :
			for (Timbre timbre : ((Planche) source.planche).timbres) {
				if (timbre.timbreSuivant == source) {
					// Je ne suis pas premier !
					// [A] -> [source] -> [C] devient [A] -> [C]
					timbre.timbreSuivant = source.timbreSuivant;
					source.timbreSuivant = null;
					// On decalle tous les timbres à droite !
					recherchePremiertimbre(timbre).getUI().positionnerProchainTimbre();
				}
				// Cas des Conditions :
				if (timbre instanceof Condition && ((Condition) timbre).timbreSuivantSecondaire == source) {
					// Je ne suis pas premier !
					// [A] -> [source] -> [C] devient [A] -> [C]
					((Condition) timbre).timbreSuivantSecondaire = source.timbreSuivant;
					source.timbreSuivant = null;
					// On decalle tous les timbres à droite !
					recherchePremiertimbre(timbre).getUI().positionnerProchainTimbre();
				}

			}

			destination.timbreSuivantSecondaire = source; // C

			if (ancienTimbreSuivantDeDestination != null) { // On décalle ce timbre à la fin des timbres insérés
				if (source.timbreSuivant == destination) {
					source.timbreSuivant = ancienTimbreSuivantDeDestination;
				} else {
					Timbre suivant = source.timbreSuivant;
					if (suivant != null) {
						while (suivant.timbreSuivant != null) {
							suivant = suivant.timbreSuivant;
						}
						suivant.timbreSuivant = ancienTimbreSuivantDeDestination;
					} else {
						source.timbreSuivant = ancienTimbreSuivantDeDestination;
					}
				}
			}

		}
	}

	/**
	 * @param fontSize
	 * @param g2d
	 */
	public static void texteBienvenue(int fontSize, Graphics2D g2d) {
		ParcheminDessinateur dessinateur = new ParcheminDessinateur();
		RectangleDessinateur dessinateur2 = new RectangleDessinateur();
		PrototypeGraphique fond = new PrototypeGraphique(null, null, "pnom", org.linotte.moteur.entites.Role.ESPECE, "", null, TYPE_GRAPHIQUE.RECTANGLE);
		fond.x = 170;
		fond.y = 70;
		fond.largeur = 750;
		fond.hauteur = 300;
		fond.couleur = Color.GRAY.brighter();
		fond.transparence = 1f;
		fond.plein = true;

		PrototypeGraphique espece = new PrototypeGraphique(null, null, "pnom", org.linotte.moteur.entites.Role.ESPECE, "", null, TYPE_GRAPHIQUE.PARCHEMIN);
		espece.texte = "Un programme visuel commence par le bloc 'Début' (bloc vert à gauche).\n"
				+ "Un bloc represente une instruction : une variable, un verbe, une fonction, une boucle ou une condition.\n"
				+ "Ajoutez les instructions suivantes de votre programme en glissant les blocs correspondants depuis les boutons situés à droite de l'écran vers le bloc souhaité ('Début' si votre programme est vide)."
				+ " Le petit rond rouge indique la possibilité d'attachement.\n"
				+ "Quand vos blocs sont positionnés (et attachés), vous pouvez glisser les acteurs créés au-dessus des verbes afin de les utiliser.\n"
				+ "Il est également possible de cliquer sur un bloc pour spécifier directement un champ : "
				+ "une valeur d'une variable ou une formule (a > b par exemple) d'une condition par exemple."
				+ "\n\nVérifiez votre programme en cliquant sur le bouton 'Voir code' pour contrôler sa version textuelle.\n"
				+ "Si votre programme vous convient, vous pouvez l'exécuter en cliquant sur 'Lire' ou 'Lire au ralenti'.";
		espece.police = "Arial";
		espece.taille = 0;
		espece.largeur = 700;
		espece.couleur = Color.BLACK;
		espece.x = 200;
		espece.y = 100;
		espece.transparence = 1f;

		try {
			// On recycle du code qui marche plutôt pas mal :)
			dessinateur2.projette(null, g2d, fond);
			dessinateur.projette(null, g2d, espece);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Comptatibilité avec Linotte 2.5.X
	 * 
	 * @param timbre
	 */
	public static void compatibiliteAncienneVersion(Timbre timbre) {
		if (timbre instanceof Verbe) {
			Verbe v = (Verbe) timbre;
			if (v.parametres != null && v.parametres instanceof Acteur[]) {
				Acteur[] temp = (Acteur[]) v.parametres;
				v.parametres = new Deposable[temp.length];
				System.arraycopy(temp, 0, v.parametres, 0, temp.length);
			}
		}
	}

	public static void drawString(Graphics2D g2d, String texte, int x, int y) {
		if ( texte ==null || texte.length() ==0) return;
		Font font = FontHelper.createFont(Abaque.FONT, Abaque.FONT_SIZE - 16); // Récupération de code...
		FontRenderContext context = g2d.getFontRenderContext();
		TextLayout text = new TextLayout(texte, font, context);
		AffineTransform rotator = new AffineTransform();
		rotator.translate(x, y);
		g2d.fill(text.getOutline(rotator));
	}
}
