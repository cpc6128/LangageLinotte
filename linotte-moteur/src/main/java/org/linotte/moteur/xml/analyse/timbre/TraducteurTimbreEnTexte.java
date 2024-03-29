package org.linotte.moteur.xml.analyse.timbre;

import org.linotte.frame.cahier.timbre.entite.*;
import org.linotte.frame.cahier.timbre.entite.e.Role;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.moteur.xml.analyse.multilangage.Langage;

import java.util.ArrayList;
import java.util.List;

/**
 * A réécrire, c'est juste un POC écrit très rapidement ...
 *
 * @author CPC
 */
public final class TraducteurTimbreEnTexte {

    private static final String CHAR_RETOUR = "\n";

    private int numeroLigne = 1;

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

        // Ajout des fonctions :
        for (Fonction fonction : fonctions) {
            Acteur prochaine = ajouterEnteteFonction(livre, fonction, langage);
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
            ajouterCommentaire((Commentaire) timbre, livre);
            return timbre.timbreSuivant;
        } else if (timbre instanceof Acteur) {
            ajouterCreationActeur((Acteur) timbre, livre);
            return timbre.timbreSuivant;
        } else if (timbre instanceof Condition) {
            ajouterCondition((Condition) timbre, livre, langage);
            return ((BlocDebut) timbre).blocfin.timbreSuivant;
        } else if (timbre instanceof TantQue) {
            ajouterTantQue((TantQue) timbre, livre, langage);
            return ((BlocDebut) timbre).blocfin.timbreSuivant;
        } else if (timbre instanceof Pour) {
            ajouterPour((Pour) timbre, livre, langage);
            return ((BlocDebut) timbre).blocfin.timbreSuivant;
        } else if (timbre instanceof Verbe) {
            switch (timbre.parfum) {
                case AFFICHER:
                    ajouterAfficher((Verbe) timbre, livre, langage);
                    break;
                case DEMANDER:
                    ajouterDemander((Verbe) timbre, livre);
                    break;
                case VALOIR:
                    ajouterValoir((Verbe) timbre, livre, langage);
                    break;
                case EFFACERTABLEAU:
                    ajouterEffacerTableau((Verbe) timbre, livre);
                    break;
                case RETOURNER:
                    ajouterRetourner((Verbe) timbre, livre);
                    break;
                case REVENIR:
                    ajouterRevenir((Verbe) timbre, livre);
                    break;
                case PARCOURIR:
                    ajouterParcourir((Verbe) timbre, livre);
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
                    ajouterProjetter((Verbe) timbre, livre);
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
                    ajouterLeverCrayon((Verbe) timbre, livre);
                    break;
                case BAISSER_CRAYON:
                    ajouterBaisserCrayon((Verbe) timbre, livre);
                    break;
                default:
                    break;
            }
            return timbre.timbreSuivant;
        }
        return timbre.timbreSuivant;
    }

    @Deprecated
    private void ajouterBaisserCrayon(Verbe timbre, StringBuilder livre) {
        livre.append("posé de ").append(ajouterActeur(timbre.parametres[0])).append(" vaut vrai");
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterLeverCrayon(Verbe timbre, StringBuilder livre) {
        livre.append("posé de ").append(ajouterActeur(timbre.parametres[0])).append(" vaut faux");
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterRevenir(Verbe timbre, StringBuilder livre) {
        livre.append("reviens");
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterParcourir(Verbe timbre, StringBuilder livre) {
        livre.append("parcours ");
        Deposable deposable = timbre.parametres[0];
        if (deposable instanceof PorteurFonction) {
            PorteurFonction porteur = (PorteurFonction) deposable;
            livre.append(ajouterFonctionPourParcourir(porteur));
        }
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterCommentaire(Commentaire timbre, StringBuilder livre) {
        livre.append("// " + timbre.texte);
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterPour(Pour timbre, StringBuilder livre, Langage langage) {
        livre.append("pour " + ajouterActeur(timbre.acteur) + " de " + ajouterActeur(timbre.indexDebut) + " à " + ajouterActeur(timbre.indexFin) + "");
        ajouterRetourChariot(livre, timbre);
        ajouteCommandes(timbre.timbreSuivant, livre, langage);
        livre.append("ferme");
        ajouterRetourChariot(livre, null);
    }

    private void ajouterCondition(Condition timbre, StringBuilder livre, Langage langage) {
        livre.append("si " + ajouterActeur(timbre.formule));
        ajouterRetourChariot(livre, timbre);
        ajouteCommandes(timbre.timbreSuivant, livre, langage);
        livre.append("ferme");
        ajouterRetourChariot(livre, null);
        if (!(timbre.timbreSuivantSecondaire instanceof BlocFin)) {
            livre.append("sinon");
            ajouterRetourChariot(livre, null);
            ajouteCommandes(timbre.timbreSuivantSecondaire, livre, langage);
            livre.append("ferme");
            ajouterRetourChariot(livre, null);
        }
    }

    private void ajouterTantQue(TantQue timbre, StringBuilder livre, Langage langage) {
        livre.append("tant que " + ajouterActeur(timbre.formule));
        ajouterRetourChariot(livre, timbre);
        ajouteCommandes(timbre.timbreSuivant, livre, langage);
        livre.append("ferme");
        ajouterRetourChariot(livre, null);
    }

    private void ajouterValoir(Verbe timbre, StringBuilder livre, Langage langage) {
        livre.append(ajouterActeur(timbre.parametres[0])).append(" prend ").append(ajouterActeur(timbre.parametres[1]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterEffacerTableau(Verbe timbre, StringBuilder livre) {
        livre.append("efface tableau");
        ajouterRetourChariot(livre, timbre);

    }

    private void ajouterCreationActeur(Acteur timbre, StringBuilder livre) {
        livre.append(timbre.texte);
        livre.append(" est un ");
        if (timbre.role == Role.CRAYON) {
            livre.append("crayon, couleur prend \"noir\", x prend 200, y prend 200, " + "taille prend 1, visible prend vrai, pointe prend vrai");
        } else {
            livre.append(timbre.role == Role.NOMBRE ? "nombre" : "texte");
            if (timbre.valeur != null) {
                livre.append(" valant ");
                livre.append(pasNulle(timbre.valeur, timbre.role));
            }
        }
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterDemander(Verbe timbre, StringBuilder livre) {
        livre.append("demande ").append(ajouterActeur(timbre.parametres[0]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterRetourner(Verbe timbre, StringBuilder livre) {
        livre.append("retourne ").append(ajouterActeur(timbre.parametres[0]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterProjetter(Verbe timbre, StringBuilder livre) {
        livre.append("projette ").append(ajouterActeur(timbre.parametres[0]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterDeplaceDroite(Verbe timbre, StringBuilder livre, Langage langage) {
        //Déplace ? vers le bas de ?
        livre.append("déplace ").append(ajouterActeur(timbre.parametres[0]));
        livre.append(" vers la droite de ").append(ajouterActeur(timbre.parametres[1]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterDeplaceGauche(Verbe timbre, StringBuilder livre, Langage langage) {
        //Déplace ? vers le bas de ?
        livre.append("déplace ").append(ajouterActeur(timbre.parametres[0]));
        livre.append(" vers la gauche de ").append(ajouterActeur(timbre.parametres[1]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterDeplaceHaut(Verbe timbre, StringBuilder livre, Langage langage) {
        //Déplace ? vers le bas de ?
        livre.append("déplace ").append(ajouterActeur(timbre.parametres[0]));
        livre.append(" vers le haut de ").append(ajouterActeur(timbre.parametres[1]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterDeplaceBas(Verbe timbre, StringBuilder livre, Langage langage) {
        //Déplace ? vers le bas de ?
        livre.append("déplace ").append(ajouterActeur(timbre.parametres[0]));
        livre.append(" vers le bas de ").append(ajouterActeur(timbre.parametres[1]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterTourneGauche(Verbe timbre, StringBuilder livre, Langage langage) {
        livre.append("tourne à gauche ").append(ajouterActeur(timbre.parametres[0]));
        livre.append(" de ").append(ajouterActeur(timbre.parametres[1]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterTourneDroite(Verbe timbre, StringBuilder livre, Langage langage) {
        livre.append("tourne à droite ").append(ajouterActeur(timbre.parametres[0]));
        livre.append(" de ").append(ajouterActeur(timbre.parametres[1]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterDeplaceAvant(Verbe timbre, StringBuilder livre, Langage langage) {
        livre.append("avance ").append(ajouterActeur(timbre.parametres[0]));
        livre.append(" de ").append(ajouterActeur(timbre.parametres[1]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterDeplaceVers(Verbe timbre, StringBuilder livre, Langage langage) {
        livre.append("déplace ").append(ajouterActeur(timbre.parametres[0]));
        livre.append(" vers ").append(ajouterActeur(timbre.parametres[1]));
        livre.append(" et ").append(ajouterActeur(timbre.parametres[2]));
        ajouterRetourChariot(livre, timbre);
    }

    private void ajouterAfficher(Verbe timbre, StringBuilder livre, Langage langage) {
        livre.append("affiche ").append(ajouterActeur(timbre.parametres[0]));
        ajouterRetourChariot(livre, timbre);
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
        //livre.append("code :");
        //ajouterRetourChariot(livre, null);
    }

    private Acteur ajouterEnteteFonction(StringBuilder livre, Fonction fonction, Langage langage) {
        List<Acteur> parametres = fonction.retourneParametres();

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
