/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                           *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.exception;

public abstract class Constantes {

	final public static int ERREUR_VIDE = 0;

	// Erreur grammaire d'un livre
	final public static int ERREUR_MATHEMATIQUES = 2;
	final public static int ERREUR_POSITION = 3;
	final public static int FIN_DE_FICHIER = 5;
	final public static int ALLER_INTERDIT = 18;
	final public static int RETOUR_SANS_BOUCLE = 19;
	final public static int SYNTAXE_SOUS_PARAGRAPHE = 6;
	final public static int STRUCTURE_PARAGRAPHE = 25;
	final public static int STRUCTURE_LIVRE = 28;
	final public static int SYNTAXE_NON_SUPPORTEE = 30;

	// Erreur grammaire des rôles
	final public static int SYNTAXE_ACTEUR_PARTICULIER = 7;
	final public static int SYNTAXE_ACTEUR_DEJA_PRESENT = 8;
	final public static int SYNTAXE_NOMBRE_NON_VALIDE = 17;
	public static final int SYNTAXE_CHAINE_NON_VALIDE = 26;
	public static final int SYNTAXE_CARACTERISTIQUE_INCONNUE = 27;
	final public static int SYNTAXE_SINON = 20;
	final public static int SYNTAXE_ESPECE_INCONNUE = 9;
	final public static int SYNTAXE_ACTEUR_DEJA_PRESENT_AVEC_ROLE_DIFF = 12;
	final public static int SYNTAXE_ESPECE_DEJA_PRESENTE = 14;
	final public static int SYNTAXE_MINI_FORMULE = 16;

	// Erreur grammaire des actions
	final public static int CASIER_MAUVAISE_ESPECE = 10;
	final public static int SYNTAXE_ACTEUR_INCONNU = 11;
	final public static int SYNTAXE_ACTEUR_IMPOSSIBLE = 4;
	final public static int CASIER_ATTENDU = 15;

	// Fichier
	final public static int ERREUR_MODIFICATION_INTERNET = 21;
	// Doublure
	final public static int ERREUR_DOUBLURE = 22;
	final public static int ERREUR_DOUBLURE_ROLE = 23;
	final public static int ERREUR_ACTEURS_LOCAUX = 24;
	final public static int ERREUR_SYNTAXE_DOUBLE_INTERDIT = 29;

	// Verbes :
	final public static int SYNTAXE_PARAMETRE_AFFICHER = 50;
	final public static int SYNTAXE_PARAMETRE_COPIER = 52;
	final public static int SYNTAXE_PARAMETRE_DEMANDER = 53;
	final public static int SYNTAXE_PARAMETRE_MESURER = 55;
	final public static int SYNTAXE_PARAMETRE_VIDER = 56;
	final public static int SYNTAXE_PARAMETRE_AJOUTER = 58;
	final public static int SYNTAXE_PARAMETRE_SOUSTRAIRE = 59;
	final public static int SYNTAXE_PARAMETRE_MELANGER = 61;
	final public static int SYNTAXE_PARAMETRE_MULTIPLIER = 62;
	final public static int SYNTAXE_PARAMETRE_TRIER = 63;
	public static final int SYNTAXE_PARAMETRE_DIVISER = 64;
	public static final int SYNTAXE_PARAMETRE_MODULO = 65;
	public static final int SYNTAXE_PARAMETRE_ABSOLUE = 66;
	public static final int SYNTAXE_PARAMETRE_COSINUS = 67;
	public static final int SYNTAXE_PARAMETRE_SINUS = 68;
	public static final int SYNTAXE_PARAMETRE_LOG = 69;
	public static final int SYNTAXE_PARAMETRE_LOG_N = 70;
	public static final int SYNTAXE_PARAMETRE_RACINE_CARREE = 71;
	public static final int SYNTAXE_PARAMETRE_CARRE = 72;
	public static final int SYNTAXE_PARAMETRE_CUBE = 73;
	public static final int SYNTAXE_PARAMETRE_ARRONDI = 74;
	public static final int SYNTAXE_PARAMETRE_PUISSANCE = 75;
	public static final int SYNTAXE_PARAMETRE_ATTENDRE = 76;
	public static final int SYNTAXE_PARAMETRE_INVERSER = 77;
	public static final int SYNTAXE_PARAMETRE_EXTRAIRE = 78;
	public static final int SYNTAXE_PARAMETRE_INSERER = 79;
	public static final int SYNTAXE_PARAMETRE_CHERCHER = 80;
	public static final int SYNTAXE_PARAMETRE_JOUER = 81;
	public static final int SYNTAXE_PARAMETRE_CONCATENER = 82;
	public static final int SYNTAXE_PARAMETRE_EFFACER = 83;
	public static final int SYNTAXE_PARAMETRE_DEPLACER = 84;
	public static final int SYNTAXE_PARAMETRE_PROJETER = 85;
	public static final int SYNTAXE_PARAMETRE_PHOTOGRAPHIER = 86;
	public static final int SYNTAXE_PARAMETRE_TEMPORISER = 87;
	public static final int SYNTAXE_PARAMETRE_STIMULER = 88;
	public static final int SYNTAXE_PARAMETRE_CONVERTIR = 89;
	public static final int SYNTAXE_PARAMETRE_EXPLORER = 90;
	public static final int SYNTAXE_PARAMETRE_VALOIR = 91;
	public static final int SYNTAXE_PARAMETRE_OTER = 92;
	public static final int SYNTAXE_PARAMETRE_PARCOURIR = 93;
	public static final int SYNTAXE_PARAMETRE_MODIFIER = 94;
	public static final int SYNTAXE_PARAMETRE_QUESTIONNER = 95;
	public static final int SYNTAXE_PARAMETRE_CREER = 96;
	public static final int SYNTAXE_PARAMETRE_AVANCER = 97;
	public static final int SYNTAXE_PARAMETRE_TOURNER_A_GAUCHE = 98;
	public static final int SYNTAXE_PARAMETRE_TOURNER_A_DROITE = 99;
	public static final int SYNTAXE_PARAMETRE_RECULER = 200;
	public static final int SYNTAXE_PARAMETRE_APPELER = 201;
	public static final int SYNTAXE_PARAMETRE_OBSERVER = 202;
	public static final int SYNTAXE_PARAMETRE_OUVRIR = 203;
	public static final int SYNTAXE_PARAMETRE_FERMER = 204;
	public static final int SYNTAXE_PARAMETRE_CHARGER = 205;
	public static final int SYNTAXE_PARAMETRE_DECHARGER = 206;
	public static final int SYNTAXE_PARAMETRE_CONFIGURER = 207;
	public static final int SYNTAXE_PARAMETRE_EVALUER = 208;
	public static final int SYNTAXE_PARAMETRE_GREFFON = 209;
	public static final int SYNTAXE_PARAMETRE_FUSIONNER = 210;
	public static final int SYNTAXE_PARAMETRE_FAIRE_REAGIR = 211;
	public static final int SYNTAXE_PARAMETRE_FAIRE_REAGIR_EVENEMENT = 212;
	public static final int SYNTAXE_PARAMETRE_RETOURNER = 213;
	public static final int SYNTAXE_PARAMETRE_RETOURNER_INTERDIT = 214;
	public static final int SYNTAXE_PARAMETRE_RETOURNER_MANQUANT = 215;
	public static final int SYNTAXE_PARAMETRE_PEINDRE = 216;
	public static final int SYNTAXE_PARAMETRE_PIQUER = 217;
	public static final int SYNTAXE_PARAMETRE_ATTACHER = 218;
	public static final int SYNTAXE_PARAMETRE_EVOQUER = 219;
	public static final int SYNTAXE_PARAMETRE_INCREMENTER = 220;
	public static final int SYNTAXE_PARAMETRE_DECREMENTER = 221;
	public static final int SYNTAXE_PARAMETRE_STOPPER = 222;
	public static final int SYNTAXE_PARAMETRE_STOPPER_NUMERO = 223;
	public static final int SYNTAXE_PARAMETRE_RECHARGER = 224;

	// Conditions
	final public static int SYNTAXE_CONDITIONS_INVALIDE = 100;
	final public static int SYNTAXE_CONDITIONS_MEME_ROLE = 101;
	final public static int SYNTAXE_BOUCLE = 102;
	final public static int ECRITURE_FICHIER = 103;

	final public static int SYNTAXE_FICHIER = 104;
	final public static int LECTURE_FICHIER = 105;

	final public static int ERREUR_GREFFON = 111;

	final public static int IMPOSSIBLE_LIRE_LIVRE = 108;
	final public static int LIVRE_DEJA_PRESENT = 109;

	final public static int TRAITEMENT_PARALLELE = 110;

	public static final int STRUCTURE_ETRANGE = 114;

	// Mode console impossible
	final public static int MODE_CONSOLE = 106;
	// Verbe explorer
	final public static int OPERATION_NON_SUPORTEE = 107;
	final public static int ERREUR_SQL = 112;
	final public static int COOKIE_INTERDIT = 113;

	// Prototype :
	final public static int PROTOTYPE_ACTEUR_NON_ESPECE = 300;
	final public static int PROTOTYPE_METHODE_FONCTIONNELLE_INCONNUE = 301;
	final public static int PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE = 302;
	final public static int PROTOTYPE_METHODE_FONCTIONNELLE_RETOUR = 303;

	// Erreurs XML :
	final public static int TOKEN_ABSENT_OBLIGATOIRE = 10000;
	public static final int BALISE_FIN_INATENDUE = 10002;
	public static final int FIN_DE_LIGNE_ATTENDUE = 10003;
	public static final int MODE_RECHERCHE_IMPOSSIBLE = 10005;
}
