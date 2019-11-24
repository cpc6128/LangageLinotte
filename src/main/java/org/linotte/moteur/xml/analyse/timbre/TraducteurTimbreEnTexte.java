package org.linotte.moteur.xml.analyse.timbre;

import java.util.ArrayList;
import java.util.List;

import org.linotte.frame.cahier.timbre.entite.Acteur;
import org.linotte.frame.cahier.timbre.entite.BlocDebut;
import org.linotte.frame.cahier.timbre.entite.BlocFin;
import org.linotte.frame.cahier.timbre.entite.Commentaire;
import org.linotte.frame.cahier.timbre.entite.Condition;
import org.linotte.frame.cahier.timbre.entite.Debut;
import org.linotte.frame.cahier.timbre.entite.Fonction;
import org.linotte.frame.cahier.timbre.entite.PorteurFonction;
import org.linotte.frame.cahier.timbre.entite.Pour;
import org.linotte.frame.cahier.timbre.entite.TantQue;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.Verbe;
import org.linotte.frame.cahier.timbre.entite.e.Role;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.moteur.xml.analyse.multilangage.Langage;

/**
 * A réécrire, c'est juste un POC écrit très rapidement ...
 * @author CPC
 *
 */
public final class TraducteurTimbreEnTexte {

	private static final String CHAR_RETOUR = "\n";

	private int numeroLigne = 1;

	private boolean debut = true; // Pour le langage Linotte 2 et Linotte 1

	public String traiter(List<Timbre> timbresAExecuter, Langage langage) {
		StringBuilder livre = new StringBuilder();
		Debut debutTimbre = null;
		List<Fonction> fonctions = new ArrayList<>();
		for (Timbre timbreGraphique : timbresAExecuter) {
			if (timbreGraphique instanceof Debut) {
				debutTimbre = (Debut) timbreGraphique;
			}
			if (timbreGraphique instanceof Fonction) {
				fonctions.add((Fonction) timbreGraphique);
			}
		}

		// AJout du début du programme :

		ajouterDebutProgramme(livre, langage);
		if (debutTimbre != null) {
			ajouteCommandes(debutTimbre.timbreSuivant, livre, langage);
		}

		if (debut == true) { // Si vide ?
			sectionRole(livre, langage);
		}

		// Ajout des fonctions :
		for (Fonction fonction : fonctions) {
			Acteur prochaine = ajouterEnteteFonction(livre, fonction, langage);
			debut = true;
			ajouteCommandes(prochaine == null ? fonction.timbreSuivant : prochaine.timbreSuivant, livre, langage);
		}

		return livre.toString();
	}

	/**
	 * @param suivant
	 * @param livre
	 * @param langage
	 * @return
	 */
	private void ajouteCommandes(Timbre suivant, StringBuilder livre, Langage langage) {
		// Construction du programme :
		while (suivant != null && !(suivant instanceof BlocFin)) {
			suivant = ajouterCommande(suivant, livre, langage);
		}
	}

	private Timbre ajouterCommande(Timbre timbre, StringBuilder livre, Langage langage) {
		if (timbre instanceof Commentaire) {
			ajouterCommentaire((Commentaire) timbre, livre, langage);
			return timbre.timbreSuivant;
		} else if (timbre instanceof Acteur) {
			ajouterCreationActeur((Acteur) timbre, livre, langage);
			return timbre.timbreSuivant;
		} else if (timbre instanceof Condition) {
			if (debut == true) {
				sectionRole(livre, langage);
			}
			ajouterCondition((Condition) timbre, livre, langage);
			return ((BlocDebut) timbre).blocfin.timbreSuivant;
		} else if (timbre instanceof TantQue) {
			if (debut == true) {
				sectionRole(livre, langage);
			}
			ajouterTantQue((TantQue) timbre, livre, langage);
			return ((BlocDebut) timbre).blocfin.timbreSuivant;
		} else if (timbre instanceof Pour) {
			if (debut == true) {
				sectionRole(livre, langage);
			}
			ajouterPour((Pour) timbre, livre, langage);
			return ((BlocDebut) timbre).blocfin.timbreSuivant;
		} else if (timbre instanceof Verbe) {
			if (debut == true) {
				sectionRole(livre, langage);
			}
			switch (timbre.parfum) {
			case AFFICHER:
				ajouterAfficher((Verbe) timbre, livre, langage);
				break;
			case DEMANDER:
				ajouterDemander((Verbe) timbre, livre, langage);
				break;
			case VALOIR:
				ajouterValoir((Verbe) timbre, livre, langage);
				break;
			case EFFACERTABLEAU:
				ajouterEffacerTableau((Verbe) timbre, livre, langage);
				break;
			case RETOURNER:
				ajouterRetourner((Verbe) timbre, livre, langage);
				break;
			case REVENIR:
				ajouterRevenir((Verbe) timbre, livre, langage);
				break;
			case PARCOURIR:
				ajouterParcourir((Verbe) timbre, livre, langage);
				break;
			case DEPLACE_DROITE:
				ajouterDeplaceDroite((Verbe) timbre, livre, langage);
				break;
			case DEPLACE_GAUCHE:
				ajouterDeplaceGauche((Verbe) timbre, livre, langage);
				break;
			case DEPLACE_HAUT:
				ajouterDeplaceHaut((Verbe) timbre, livre, langage);
				break;
			case DEPLACE_BAS:
				ajouterDeplaceBas((Verbe) timbre, livre, langage);
				break;
			case PROJETTE:
				ajouterProjetter((Verbe) timbre, livre, langage);
				break;
			case DEPLACE_AVANT:
				ajouterDeplaceAvant((Verbe) timbre, livre, langage);
				break;
			case DEPLACE_VERS:
				ajouterDeplaceVers((Verbe) timbre, livre, langage);
				break;
			case TOURNE_DROITE:
				ajouterTourneDroite((Verbe) timbre, livre, langage);
				break;
			case TOURNE_GAUCHE:
				ajouterTourneGauche((Verbe) timbre, livre, langage);
				break;
			case LEVER_CRAYON:
				ajouterLeverCrayon((Verbe) timbre, livre, langage);
				break;
			case BAISSER_CRAYON:
				ajouterBaisserCrayon((Verbe) timbre, livre, langage);
				break;
			default:
				break;
			}
			return timbre.timbreSuivant;
		}
		return timbre.timbreSuivant;
	}

	@Deprecated
	private void ajouterBaisserCrayon(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("posé de ").append(ajouterActeur(timbre.parametres[0])).append(" vaut \"oui\"");
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterLeverCrayon(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("posé de ").append(ajouterActeur(timbre.parametres[0])).append(" vaut \"non\"");
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterRevenir(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("reviens");
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterParcourir(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			//			livre.append("print ").append(ajouterActeur(timbre.parametres[0]));
			//			ajouterRetourChariot(livre, timbre);
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("parcours ");
			Deposable deposable = timbre.parametres[0];
			if (deposable instanceof PorteurFonction) {
				PorteurFonction porteur = (PorteurFonction) deposable;
				livre.append(ajouterFonctionPourParcourir(porteur));
			}
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterCommentaire(Commentaire timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linotte1X:
			livre.append("(\"" + timbre.texte + "\")");
			break;
		default:
			livre.append("// " + timbre.texte);
		}
		ajouterRetourChariot(livre, timbre);
	}

	private void ajouterPour(Pour timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Lyre:
			livre.append("pour " + ajouterActeur(timbre.acteur) + ", " + ajouterActeur(timbre.indexDebut) + " à " + ajouterActeur(timbre.indexFin) + " {");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("}");
			ajouterRetourChariot(livre, null);
			break;
		case Linotte1X:
			livre.append("pour " + ajouterActeur(timbre.acteur) + " de " + ajouterActeur(timbre.indexDebut) + " à " + ajouterActeur(timbre.indexFin) + ", lis");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("ferme");
			ajouterRetourChariot(livre, null);
			break;
		case Linotte2:
			livre.append("pour " + ajouterActeur(timbre.acteur) + " de " + ajouterActeur(timbre.indexDebut) + " à " + ajouterActeur(timbre.indexFin) + " lis");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("ferme");
			ajouterRetourChariot(livre, null);
			break;
		case Linnet:
			livre.append("for " + ajouterActeur(timbre.acteur) + " , " + ajouterActeur(timbre.indexDebut) + " to " + ajouterActeur(timbre.indexFin) + " {");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("}");
			ajouterRetourChariot(livre, null);
			break;
		}
	}

	private void ajouterCondition(Condition timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Lyre:
			livre.append("si " + ajouterActeur(timbre.formule) + "  {");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("}");
			ajouterRetourChariot(livre, null);
			if (!(timbre.timbreSuivantSecondaire instanceof BlocFin)) {
				livre.append("sinon {");
				ajouterRetourChariot(livre, null);
				ajouteCommandes(timbre.timbreSuivantSecondaire, livre, langage);
				livre.append("}");
				ajouterRetourChariot(livre, null);
			}
			break;
		case Linotte1X:
			livre.append("si " + ajouterActeur(timbre.formule) + " alors lis");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("ferme");
			ajouterRetourChariot(livre, null);
			if (!(timbre.timbreSuivantSecondaire instanceof BlocFin)) {
				livre.append("sinon, lis");
				ajouterRetourChariot(livre, null);
				ajouteCommandes(timbre.timbreSuivantSecondaire, livre, langage);
				livre.append("ferme");
				ajouterRetourChariot(livre, null);
			}
			break;
		case Linotte2:
			livre.append("si " + ajouterActeur(timbre.formule) + " lis");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("ferme");
			ajouterRetourChariot(livre, null);
			if (!(timbre.timbreSuivantSecondaire instanceof BlocFin)) {
				livre.append("sinon lis");
				ajouterRetourChariot(livre, null);
				ajouteCommandes(timbre.timbreSuivantSecondaire, livre, langage);
				livre.append("ferme");
				ajouterRetourChariot(livre, null);
			}
			break;
		case Linnet:
			livre.append("if " + ajouterActeur(timbre.formule) + " {");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("}");
			ajouterRetourChariot(livre, null);
			if (!(timbre.timbreSuivantSecondaire instanceof BlocFin)) {
				livre.append("else {");
				ajouterRetourChariot(livre, null);
				ajouteCommandes(timbre.timbreSuivantSecondaire, livre, langage);
				livre.append("}");
				ajouterRetourChariot(livre, null);
			}
			break;
		}
	}

	private void ajouterTantQue(TantQue timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Lyre:
			livre.append("tant que " + ajouterActeur(timbre.formule) + "  {");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("}");
			ajouterRetourChariot(livre, null);
			break;
		case Linotte1X:
			livre.append("tant que " + ajouterActeur(timbre.formule) + " , lis");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("ferme");
			ajouterRetourChariot(livre, null);
			break;
		case Linotte2:
			livre.append("tant que " + ajouterActeur(timbre.formule) + " lis");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("ferme");
			ajouterRetourChariot(livre, null);
			break;
		case Linnet:
			livre.append("while " + ajouterActeur(timbre.formule) + " {");
			ajouterRetourChariot(livre, timbre);
			ajouteCommandes(timbre.timbreSuivant, livre, langage);
			livre.append("}");
			ajouterRetourChariot(livre, null);
			break;
		}
	}

	private void sectionRole(StringBuilder livre, Langage langage) {
		debut = false;
		switch (langage) {
		case Linnet:
		case Lyre:
			break;
		case Linotte1X:
			livre.append("actions :");
			ajouterRetourChariot(livre, null);
			break;
		case Linotte2:
			livre.append("début");
			ajouterRetourChariot(livre, null);
			break;
		default:
			break;
		}
	}

	private void ajouterValoir(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
		case Lyre:
			livre.append(ajouterActeur(timbre.parametres[0])).append(" = ").append(ajouterActeur(timbre.parametres[1]));
			ajouterRetourChariot(livre, timbre);
			break;
		case Linotte1X:
			livre.append(ajouterActeur(timbre.parametres[0])).append(" vaut ").append(ajouterActeur(timbre.parametres[1]));
			ajouterRetourChariot(livre, timbre);
			break;
		case Linotte2:
			livre.append(ajouterActeur(timbre.parametres[0])).append(" prend ").append(ajouterActeur(timbre.parametres[1]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterEffacerTableau(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			livre.append("clean");
			ajouterRetourChariot(livre, timbre);
			break;
		case Lyre:
			livre.append("efface");
			ajouterRetourChariot(livre, timbre);
			break;
		case Linotte1X:
			livre.append("efface le tableau");
			ajouterRetourChariot(livre, timbre);
			break;
		case Linotte2:
			livre.append("efface tableau");
			ajouterRetourChariot(livre, timbre);
			break;
		}

	}

	private void ajouterCreationActeur(Acteur timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
		case Lyre:
			livre.append(timbre.texte);
			livre.append(" := ");
			livre.append(pasNulle(timbre.valeur, timbre.role));
			ajouterRetourChariot(livre, timbre);
			break;
		case Linotte1X:
		case Linotte2:
			livre.append(timbre.texte);
			livre.append(" est un ");
			if (timbre.role == Role.CRAYON) {
				livre.append("crayon, couleur prend \"noir\", x prend 200, y prend 200, " + "taille prend 1, visible prend \"oui\", pointe prend \"oui\"");
			} else {
				livre.append(timbre.role == Role.NOMBRE ? "nombre" : "texte");
				if (timbre.valeur != null) {
					livre.append(" valant ");
					livre.append(pasNulle(timbre.valeur, timbre.role));
				}
			}
			ajouterRetourChariot(livre, timbre);
			break;
		default:
			break;
		}
	}

	private void ajouterDemander(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			livre.append("input ").append(ajouterActeur(timbre.parametres[0]));
			ajouterRetourChariot(livre, timbre);
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("demande ").append(ajouterActeur(timbre.parametres[0]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterRetourner(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			livre.append("return ").append(ajouterActeur(timbre.parametres[0]));
			ajouterRetourChariot(livre, timbre);
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("retourne ").append(ajouterActeur(timbre.parametres[0]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterProjetter(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("projette ").append(ajouterActeur(timbre.parametres[0]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterDeplaceDroite(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			//Déplace ? vers le bas de ?
			livre.append("déplace ").append(ajouterActeur(timbre.parametres[0]));
			livre.append(" vers la droite de ").append(ajouterActeur(timbre.parametres[1]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterDeplaceGauche(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			//Déplace ? vers le bas de ?
			livre.append("déplace ").append(ajouterActeur(timbre.parametres[0]));
			livre.append(" vers la gauche de ").append(ajouterActeur(timbre.parametres[1]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterDeplaceHaut(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			//Déplace ? vers le bas de ?
			livre.append("déplace ").append(ajouterActeur(timbre.parametres[0]));
			livre.append(" vers le haut de ").append(ajouterActeur(timbre.parametres[1]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterDeplaceBas(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			//Déplace ? vers le bas de ?
			livre.append("déplace ").append(ajouterActeur(timbre.parametres[0]));
			livre.append(" vers le bas de ").append(ajouterActeur(timbre.parametres[1]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterTourneGauche(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("tourne à gauche ").append(ajouterActeur(timbre.parametres[0]));
			livre.append(" de ").append(ajouterActeur(timbre.parametres[1]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterTourneDroite(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("tourne à droite ").append(ajouterActeur(timbre.parametres[0]));
			livre.append(" de ").append(ajouterActeur(timbre.parametres[1]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterDeplaceAvant(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("avance ").append(ajouterActeur(timbre.parametres[0]));
			livre.append(" de ").append(ajouterActeur(timbre.parametres[1]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterDeplaceVers(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("déplace ").append(ajouterActeur(timbre.parametres[0]));
			livre.append(" vers ").append(ajouterActeur(timbre.parametres[1]));
			livre.append(" et ").append(ajouterActeur(timbre.parametres[2]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private void ajouterAfficher(Verbe timbre, StringBuilder livre, Langage langage) {
		switch (langage) {
		case Linnet:
			livre.append("print ").append(ajouterActeur(timbre.parametres[0]));
			ajouterRetourChariot(livre, timbre);
			break;
		case Linotte1X:
		case Linotte2:
		case Lyre:
			livre.append("affiche ").append(ajouterActeur(timbre.parametres[0]));
			ajouterRetourChariot(livre, timbre);
			break;
		}
	}

	private String ajouterActeur(Deposable deposable) {
		StringBuilder text = new StringBuilder();
		if (deposable != null) {

			if (deposable instanceof Acteur) {
				Acteur acteur = (Acteur) deposable;
				if (acteur.texte == null) { // Anomyme :
					// ROLE
					switch (acteur.role) {
					default:
						text.append(pasNulle(acteur.valeur, acteur.role));
						break;
					}
				} else {
					text.append(acteur.texte); // Nom de l'acteur
				}
				return text.toString();
			} else if (deposable instanceof PorteurFonction) {
				PorteurFonction porteur = (PorteurFonction) deposable;
				return ajouterFonction(porteur);
			} else {
				return " ? ";
			}

		} else
			return "";
	}

	private String ajouterFonction(PorteurFonction porteur) {
		StringBuilder texte = new StringBuilder(porteur.fonction.nom);
		texte.append("(");
		int nbParametre = porteur.fonction.retourneParametres().size();
		if (porteur.parametres != null) {
			for (int i = 0; i < nbParametre; i++) {
				if (porteur.parametres.size() > i) {
					Deposable deposable = porteur.parametres.get(i);
					texte.append(ajouterActeur(deposable));
				} else {
					texte.append(" ? ");
				}
				if ((i + 1) < nbParametre)
					texte.append(", ");
			}
		}
		texte.append(")");
		return texte.toString();
	}

	private String ajouterFonctionPourParcourir(PorteurFonction porteur) {
		StringBuilder texte = new StringBuilder(porteur.fonction.nom);
		texte.append(" avec ");
		int nbParametre = porteur.fonction.retourneParametres().size();
		if (porteur.parametres != null) {
			for (int i = 0; i < nbParametre; i++) {
				if (porteur.parametres.size() > i) {
					Deposable deposable = porteur.parametres.get(i);
					texte.append(ajouterActeur(deposable));
				} else {
					texte.append(" ? ");
				}
				if ((i + 1) < nbParametre)
					texte.append(", ");
			}
		}
		return texte.toString();
	}

	private String pasNulle(String valeur, Role role) {
		if (valeur == null) {
			switch (role) {
			case NOMBRE:
				return "0";
			case TEXTE:
				return "\"\"";
			default:
				return "";
			}
		} else {
			switch (role) {
			case TEXTE:
				return "\"" + valeur + "\"";
			default:
				return valeur;
			}
		}
	}

	private void ajouterRetourChariot(StringBuilder livre, Timbre suivant) {
		livre./*append("//").append(numeroLigne).*/append(CHAR_RETOUR);
		if (suivant != null)
			suivant.numeroLigne = numeroLigne;
		numeroLigne++;
	}

	private void ajouterDebutProgramme(StringBuilder livre, Langage langage) {
		if (langage == Langage.Linotte2) {
			livre.append("principale :");
			ajouterRetourChariot(livre, null);
			return;
		} else if (langage == Langage.Linotte1X) {
			livre.append("livre graphique :");
			ajouterRetourChariot(livre, null);
			livre.append("principale :");
			ajouterRetourChariot(livre, null);
			livre.append("rôles :");
			ajouterRetourChariot(livre, null);
			return;
		}
	}

	private Acteur ajouterEnteteFonction(StringBuilder livre, Fonction fonction, Langage langage) {
		List<Acteur> parametres = fonction.retourneParametres();
		switch (langage) {
		case Linotte1X: {
			Acteur dernier = null;
			livre.append(fonction.nom + " : ");
			ajouterRetourChariot(livre, null);
			for (int i = 0; i < parametres.size(); i++) {
				dernier = parametres.get(i);
				livre.append("* ");
				ajouterCreationActeur(parametres.get(i), livre, langage);
				if (i + 1 != parametres.size())
					ajouterRetourChariot(livre, null);
			}
			ajouterRetourChariot(livre, null);
			return dernier;
		}
		default: {
			Acteur dernier = null;
			livre.append(fonction.nom + " : ");
			for (int i = 0; i < parametres.size(); i++) {
				dernier = parametres.get(i);
				livre.append(parametres.get(i).texte);
				if (i + 1 != parametres.size())
					livre.append(", ");
			}
			ajouterRetourChariot(livre, null);
			return dernier;
		}
		}
	}

}
