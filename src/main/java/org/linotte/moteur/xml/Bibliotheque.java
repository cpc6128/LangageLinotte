package org.linotte.moteur.xml;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.linotte.frame.latoile.LaToile;
import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.api.Greffon.Attribut;
import org.linotte.greffons.impl.Clavier;
import org.linotte.greffons.impl.Conduit;
import org.linotte.greffons.impl.Configuration;
import org.linotte.greffons.impl.FichierTube;
import org.linotte.greffons.impl.GreffonRobot;
import org.linotte.greffons.impl.Horodatage;
import org.linotte.greffons.impl.ImageX;
import org.linotte.greffons.impl.Imprimeur;
import org.linotte.greffons.impl.JukeBox;
import org.linotte.greffons.impl.Metronome;
import org.linotte.greffons.impl.Notification;
import org.linotte.greffons.impl.PileTube;
import org.linotte.greffons.impl.Pipette;
import org.linotte.greffons.impl.Police;
import org.linotte.greffons.impl.PontTube;
import org.linotte.greffons.impl.PressePapier;
import org.linotte.greffons.impl.Queue;
import org.linotte.greffons.impl.RepertoireTube;
import org.linotte.greffons.impl.Rss;
import org.linotte.greffons.impl.SQL;
import org.linotte.greffons.impl.TCPClient;
import org.linotte.greffons.impl.TCPServeur;
import org.linotte.greffons.impl.Tweak;
import org.linotte.greffons.impl.Webonotte;
import org.linotte.greffons.impl.swing.Atelier;
import org.linotte.greffons.impl.swing.Barre;
import org.linotte.greffons.impl.swing.BoiteTexte;
import org.linotte.greffons.impl.swing.Bouton;
import org.linotte.greffons.impl.swing.BoutonRadio;
import org.linotte.greffons.impl.swing.CaseACocher;
import org.linotte.greffons.impl.swing.Champ;
import org.linotte.greffons.impl.swing.Extension;
import org.linotte.greffons.impl.swing.Formulaire;
import org.linotte.greffons.impl.swing.GroupeBoutonRadio;
import org.linotte.greffons.impl.swing.Image;
import org.linotte.greffons.impl.swing.Indicateur;
import org.linotte.greffons.impl.swing.Label;
import org.linotte.greffons.impl.swing.Liste;
import org.linotte.greffons.impl.swing.Menu;
import org.linotte.greffons.impl.swing.MenuBouton;
import org.linotte.greffons.impl.swing.MenuCaseACocher;
import org.linotte.greffons.impl.swing.Onglet;
import org.linotte.greffons.impl.swing.Panneau;
import org.linotte.greffons.impl.swing.Popup;
import org.linotte.greffons.impl.swing.Scrolleur;
import org.linotte.greffons.impl.swing.Selecteur;
import org.linotte.greffons.impl.swing.Slider;
import org.linotte.greffons.impl.swing.SousFormulaire;
import org.linotte.greffons.impl.swing.SousMenu;
import org.linotte.greffons.impl.swing.SwingToile;
import org.linotte.greffons.impl.swing.Table;
import org.linotte.greffons.impl.swing.Tableau;
import org.linotte.greffons.impl.swing.layout.Grille;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.api.Librairie;

/**
 * Cette classe initialise les espèces et les greffons internes
 * 
 * @author CPC
 *
 */
public final class Bibliotheque {

	private static final String TEXTE = "texte";
	private static final String NOMBRE = "nombre";

	private Bibliotheque() {
		throw new SecurityException();
	}

	public static void genererEspecesGraphiquesEtGreffonsInternes(Librairie<?> lib, Linotte linotte) throws Exception {

		especesGraphiquesInternes(lib, linotte);

		greffonsFormulaires();

		greffonsInternesAutres();

	}

	/**
	 * @throws Exception
	 */
	private static void greffonsInternesAutres() throws Exception {
		// ***************************
		// Ajout des autres greffons :
		// ***************************

		LinotteFacade.creationPrototype(null, new TCPClient(), "tcpclient");
		LinotteFacade.creationPrototype(null, new TCPServeur(), "tcpserveur");
		LinotteFacade.creationPrototype(null, new Clavier(), "clavier");
		LinotteFacade.creationPrototype(null, new Imprimeur(), "imprimeur");
		LinotteFacade.creationPrototype(null, new Metronome(), "métronome");
		LinotteFacade.creationPrototype(null, new Pipette(), "pipette");
		LinotteFacade.creationPrototype(null, new Police(), "police");
		LinotteFacade.creationPrototype(null, new Queue(), "queue");
		try {
			LinotteFacade.creationPrototype(null, new GreffonRobot(), "robot");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		LinotteFacade.creationPrototype(null, new Rss(), "rss");
		LinotteFacade.creationPrototype(null, new SQL(), "sql");
		LinotteFacade.creationPrototype(null, new Tweak(), "tweak");
		LinotteFacade.creationPrototype(null, new Webonotte(), "webonotte");
		LinotteFacade.creationPrototype(null, new RepertoireTube(), "répertoire");
		LinotteFacade.creationPrototype(null, new PressePapier(), "pressepapier");
		LinotteFacade.creationPrototype(null, new Horodatage(), "horodatage");
		LinotteFacade.creationPrototype(null, new FichierTube(), "fichier");
		LinotteFacade.creationPrototype(null, new PileTube(), "pile");
		LinotteFacade.creationPrototype(null, new Conduit(), "conduit");

		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("adresse", new Attribut(TEXTE, ""));
			attributs.put("état", new Attribut(NOMBRE, "0"));
			attributs.put("port", new Attribut(NOMBRE, "8777"));
			LinotteFacade.creationPrototype(attributs, new PontTube(), "pont");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("domaine", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Configuration(), "configuration");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("fichier", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new JukeBox(), "jukebox");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "0"));
			attributs.put("y", new Attribut(NOMBRE, "0"));
			attributs.put("largeur", new Attribut(NOMBRE, "-1"));
			attributs.put("hauteur", new Attribut(NOMBRE, "-1"));
			attributs.put("transparence", new Attribut(NOMBRE, "100"));
			attributs.put("angle", new Attribut(NOMBRE, "0"));
			attributs.put("position", new Attribut(NOMBRE, "0"));
			attributs.put("taille", new Attribut(NOMBRE, "1"));
			attributs.put("image", new Attribut(TEXTE, ""));
			attributs.put("dx", new Attribut(NOMBRE, "-1"));
			attributs.put("dy", new Attribut(NOMBRE, "-1"));
			LinotteFacade.creationPrototype(attributs, new ImageX(), "image");
		}
		{
			try {
				Map<String, Attribut> attributs = new HashMap<String, Attribut>();
				attributs.put("icône", new Attribut(TEXTE, ""));
				attributs.put("description", new Attribut(TEXTE, ""));
				LinotteFacade.creationPrototype(attributs, new Notification(), "notification");
			} catch (Exception e) {
				// Bloc sous une console Linux
				e.printStackTrace();
			}
		}
	}

	/**
	 * @throws Exception
	 */
	private static void greffonsFormulaires() throws Exception {
		// ************************************************
		// Ajout du greffon Formulaire (composants Swing) :
		// ************************************************

		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("toile", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new SwingToile(), "xtoile");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("largeur", new Attribut(NOMBRE, "200"));
			attributs.put("hauteur", new Attribut(NOMBRE, "200"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Tableau(), "xtableau");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("largeur", new Attribut(NOMBRE, "100"));
			attributs.put("hauteur", new Attribut(NOMBRE, "100"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			LinotteFacade.creationPrototype(attributs, new Table(), "table");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("largeur", new Attribut(NOMBRE, "100"));
			attributs.put("hauteur", new Attribut(NOMBRE, "100"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("couleurfond", new Attribut(TEXTE, ""));
			attributs.put("titre", new Attribut(TEXTE, ""));
			attributs.put("positiontitre", new Attribut(TEXTE, "Gauche|Centre|Droite"));
			attributs.put("hauteurtitre", new Attribut(TEXTE, "Dessus haut|Centre haut|Dessous haut|Dessus bas|Centre bas|Dessous bas"));
			attributs.put("couleurtitre", new Attribut(TEXTE, ""));
			attributs.put("bordure", new Attribut(TEXTE, "oui|non"));
			attributs.put("couleurbordure", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Panneau(), "panneau");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("largeur", new Attribut(NOMBRE, "100"));
			attributs.put("hauteur", new Attribut(NOMBRE, "100"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			LinotteFacade.creationPrototype(attributs, new Onglet(), "onglet");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("largeur", new Attribut(NOMBRE, "100"));
			attributs.put("hauteur", new Attribut(NOMBRE, "100"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("source", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Image(), "ximage");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("texte", new Attribut(TEXTE, ""));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			attributs.put("icône", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new SousMenu(), "sousmenu");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("largeur", new Attribut(NOMBRE, "100"));
			attributs.put("hauteur", new Attribut(NOMBRE, "100"));
			attributs.put("titre", new Attribut(TEXTE, "Formulaire"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("icône", new Attribut(TEXTE, ""));
			attributs.put("image", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new SousFormulaire(), "sousformulaire");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("maximum", new Attribut(NOMBRE, "50"));
			attributs.put("minimum", new Attribut(NOMBRE, "0"));
			attributs.put("valeur", new Attribut(NOMBRE, "0"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Slider(), "slider");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("valeur", new Attribut(TEXTE, ""));
			attributs.put("valeurs", new Attribut(TEXTE, "Rouge|Vert|Bleu"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			attributs.put("mode", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Selecteur(), "sélecteur");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("largeur", new Attribut(NOMBRE, "100"));
			attributs.put("hauteur", new Attribut(NOMBRE, "100"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			LinotteFacade.creationPrototype(attributs, new Scrolleur(), "scrolleur");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			LinotteFacade.creationPrototype(attributs, new Popup(), "popup");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("texte", new Attribut(TEXTE, ""));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Menu(), "menu");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("texte", new Attribut(TEXTE, "Push !"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("couleurfond", new Attribut(TEXTE, ""));
			attributs.put("couleurtexte", new Attribut(TEXTE, ""));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			attributs.put("icône", new Attribut(TEXTE, ""));
			attributs.put("valeur", new Attribut(TEXTE, "faux"));
			LinotteFacade.creationPrototype(attributs, new MenuCaseACocher(), "menucaseàcocher");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("texte", new Attribut(TEXTE, ""));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new MenuBouton(), "menubouton");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("valeur", new Attribut(TEXTE, ""));
			attributs.put("valeurs", new Attribut(TEXTE, "Rouge|Vert|Bleu"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Liste(), "liste");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("texte", new Attribut(TEXTE, ""));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			attributs.put("couleurfond", new Attribut(TEXTE, ""));
			attributs.put("couleurtexte", new Attribut(TEXTE, ""));
			attributs.put("taille", new Attribut(NOMBRE, "0"));
			LinotteFacade.creationPrototype(attributs, new Label(), "étiquette");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("hauteur", new Attribut(NOMBRE, "20"));
			attributs.put("largeur", new Attribut(NOMBRE, "200"));
			attributs.put("maximum", new Attribut(NOMBRE, "100"));
			attributs.put("minimum", new Attribut(NOMBRE, "0"));
			attributs.put("valeur", new Attribut(NOMBRE, "0"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("orientation", new Attribut(TEXTE, "horizontale"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Indicateur(), "indicateur");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			LinotteFacade.creationPrototype(attributs, new GroupeBoutonRadio(), "groupeboutonradio");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("largeur", new Attribut(NOMBRE, "100"));
			attributs.put("hauteur", new Attribut(NOMBRE, "100"));
			attributs.put("titre", new Attribut(TEXTE, "Formulaire"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("icône", new Attribut(TEXTE, ""));
			attributs.put("image", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Formulaire(), "formulaire");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("titre", new Attribut(TEXTE, "Clipnotte"));
			LinotteFacade.creationPrototype(attributs, new Extension(), "clipnotte");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "0"));
			attributs.put("y", new Attribut(NOMBRE, "0"));
			attributs.put("texte", new Attribut(TEXTE, ""));
			attributs.put("taille", new Attribut(NOMBRE, "15"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			attributs.put("mode", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Champ(), "champ");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("texte", new Attribut(TEXTE, "Push !"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			attributs.put("couleurfond", new Attribut(TEXTE, ""));
			attributs.put("couleurtexte", new Attribut(TEXTE, ""));
			attributs.put("icône", new Attribut(TEXTE, ""));
			attributs.put("valeur", new Attribut(TEXTE, "faux"));
			LinotteFacade.creationPrototype(attributs, new CaseACocher(), "caseàcocher");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("texte", new Attribut(TEXTE, "Push !"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("touche", new Attribut(TEXTE, ""));
			attributs.put("couleurfond", new Attribut(TEXTE, ""));
			attributs.put("couleurtexte", new Attribut(TEXTE, ""));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			attributs.put("icône", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new Bouton(), "bouton");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("texte", new Attribut(TEXTE, "Push !"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("couleurfond", new Attribut(TEXTE, ""));
			attributs.put("couleurtexte", new Attribut(TEXTE, ""));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			attributs.put("icône", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new BoutonRadio(), "boutonradio");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "0"));
			attributs.put("y", new Attribut(NOMBRE, "0"));
			attributs.put("hauteur", new Attribut(NOMBRE, "200"));
			attributs.put("largeur", new Attribut(NOMBRE, "200"));
			attributs.put("texte", new Attribut(TEXTE, ""));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			attributs.put("infobulle", new Attribut(TEXTE, ""));
			LinotteFacade.creationPrototype(attributs, new BoiteTexte(), "boite");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("x", new Attribut(NOMBRE, "100"));
			attributs.put("y", new Attribut(NOMBRE, "100"));
			attributs.put("hauteur", new Attribut(NOMBRE, "100"));
			attributs.put("largeur", new Attribut(NOMBRE, "100"));
			attributs.put("visible", new Attribut(TEXTE, "oui"));
			LinotteFacade.creationPrototype(attributs, new Barre(), "barre");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			LinotteFacade.creationPrototype(attributs, new Atelier(), "atelier");
		}
		{
			Map<String, Attribut> attributs = new HashMap<String, Attribut>();
			attributs.put("hauteur", new Attribut(NOMBRE, "100"));
			attributs.put("largeur", new Attribut(NOMBRE, "100"));
			LinotteFacade.creationPrototype(attributs, new Grille(), "grille");
		}
	}

	/**
	 * @param lib
	 * @param linotte
	 */
	private static void especesGraphiquesInternes(Librairie<?> lib, Linotte linotte) {
		// *******************
		// Ajout des espèces :
		// *******************
		final LaToile toile = null;
		// Création des espèces systèmes :
		linotte.especeModeleMap.clear();
		// Graffiti :
		Acteur x = new Acteur(null, "x", Role.NOMBRE, null, null);
		Acteur y = new Acteur(null, "y", Role.NOMBRE, null, null);
		Acteur x_toile = new Acteur(null, "x", Role.NOMBRE, new BigDecimal(-1), null);
		Acteur y_toile = new Acteur(null, "y", Role.NOMBRE, new BigDecimal(-1), null);
		Acteur x1 = new Acteur(null, "x1", Role.NOMBRE, null, null);
		Acteur y1 = new Acteur(null, "y1", Role.NOMBRE, null, null);
		Acteur x2 = new Acteur(null, "x2", Role.NOMBRE, null, null);
		Acteur y2 = new Acteur(null, "y2", Role.NOMBRE, null, null);
		Acteur rayon = new Acteur(null, "rayon", Role.NOMBRE, null, null);
		Acteur texte = new Acteur(null, "texte", Role.TEXTE, null, null);
		Acteur couleur = new Acteur(null, "couleur", Role.TEXTE, "noir", null);
		Acteur police = new Acteur(null, "police", Role.TEXTE, "Serif", null);
		Acteur taille = new Acteur(null, "taille", Role.NOMBRE, new BigDecimal(1), null);
		Acteur taquet = new Acteur(null, "taquet", Role.NOMBRE, new BigDecimal(30), null);
		Acteur transparence = new Acteur(null, "transparence", Role.NOMBRE, new BigDecimal(100), null);
		Acteur visible = new Acteur(null, "visible", Role.TEXTE, "non", null);
		Acteur image = new Acteur(null, "image", Role.TEXTE, null, null);
		Acteur position = new Acteur(null, "position", Role.NOMBRE, new BigDecimal(0), null);
		Acteur attributtoile = new Acteur(null, "toile", Role.TEXTE, null, null);
		Acteur trame = new Acteur(null, "trame", Role.NOMBRE, new BigDecimal(0), null);
		Acteur largeur = new Acteur(null, "largeur", Role.NOMBRE, new BigDecimal(LaToile.LARGEUR), null);
		Acteur hauteur = new Acteur(null, "hauteur", Role.NOMBRE, new BigDecimal(LaToile.HAUTEUR), null);
		Acteur bordure = new Acteur(null, "bordure", Role.TEXTE, "oui", null);
		Acteur audessus = new Acteur(null, "audessus", Role.TEXTE, "non", null);
		Acteur plein = new Acteur(null, "plein", Role.TEXTE, "non", null);
		Acteur angle = new Acteur(null, "angle", Role.NOMBRE, null, null);
		Acteur angle2 = new Acteur(null, "angle", Role.NOMBRE, new BigDecimal(90), null);
		Acteur curseur = new Acteur(null, "pointeur", Role.TEXTE, null, null);
		Acteur tampon = new Acteur(null, "tampon", Role.TEXTE, "oui", null);
		Acteur baisser = new Acteur(null, "posé", Role.TEXTE, "oui", null);
		Acteur pointe = new Acteur(null, "pointe", Role.TEXTE, "oui", null);
		Acteur pleinecran = new Acteur(null, "pleinécran", Role.TEXTE, "non", null);
		Acteur collision = new Acteur(null, "collision", Role.TEXTE, "non", null);
		Acteur rx = new Acteur(null, "rx", Role.NOMBRE, new BigDecimal(0), null);
		Acteur ry = new Acteur(null, "ry", Role.NOMBRE, new BigDecimal(0), null);
		Acteur texture = new Acteur(null, "texture", Role.TEXTE, null, null);
		// Gestion du multitoile
		Acteur principale = new Acteur(null, "principale", Role.TEXTE, "oui", null);
		Acteur sourisx = new Acteur(null, "sourisx", Role.NOMBRE, null, null);
		sourisx.setValeurCalculee();
		Acteur sourisy = new Acteur(null, "sourisy", Role.NOMBRE, null, null);
		sourisy.setValeurCalculee();
		// Mozaïque :
		Casier modele = new Casier(null, "modèle", Role.CASIER, null);
		Casier palette = new Casier(null, "palette", Role.TEXTE, null);
		try {
			// http://www.grimware.org/doku.php/documentations/devices/gatearray#irgb.outputs
			// Couleurs de la palette de l'Amstrad CPC 6128
			palette.ajouterValeur(new Acteur(null, "Noir", Role.TEXTE, "32 1 1", null), false);
			palette.ajouterValeur(new Acteur(null, "Bleu", Role.TEXTE, "0 2 107", null), false);
			palette.ajouterValeur(new Acteur(null, "Bleu Vif", Role.TEXTE, "12 2 244", null), false);
			palette.ajouterValeur(new Acteur(null, "Rouge", Role.TEXTE, "108 2 1", null), false);
			palette.ajouterValeur(new Acteur(null, "Magenta", Role.TEXTE, "105 2 104", null), false);
			palette.ajouterValeur(new Acteur(null, "Mauve", Role.TEXTE, "108 2 242", null), false);
			palette.ajouterValeur(new Acteur(null, "Rouge Vif", Role.TEXTE, "243 5 6", null), false);
			palette.ajouterValeur(new Acteur(null, "Pourpre", Role.TEXTE, "240 2 104", null), false);
			palette.ajouterValeur(new Acteur(null, "Magenta Vif", Role.TEXTE, "243 2 244", null), false);
			palette.ajouterValeur(new Acteur(null, "Vert", Role.TEXTE, "39 128 1", null), false);
			palette.ajouterValeur(new Acteur(null, "Turquoise", Role.TEXTE, "120 104 104", null), false);
			palette.ajouterValeur(new Acteur(null, "Bleu Ciel", Role.TEXTE, "12 123 244", null), false);
			palette.ajouterValeur(new Acteur(null, "Jaune", Role.TEXTE, "110 123 1", null), false);
			palette.ajouterValeur(new Acteur(null, "Blanc", Role.TEXTE, "110 125 107", null), false);
			palette.ajouterValeur(new Acteur(null, "Bleu Pastel", Role.TEXTE, "110 123 246", null), false);
			palette.ajouterValeur(new Acteur(null, "Orange", Role.TEXTE, "243 125 13", null), false);
			palette.ajouterValeur(new Acteur(null, "Rose", Role.TEXTE, "243 125 107", null), false);
			palette.ajouterValeur(new Acteur(null, "Magenta Pastel", Role.TEXTE, "250 128 249", null), false);
			palette.ajouterValeur(new Acteur(null, "Vert Vif", Role.TEXTE, "2 240 1", null), false);
			palette.ajouterValeur(new Acteur(null, "Vert marin", Role.TEXTE, "0 243 107", null), false);
			palette.ajouterValeur(new Acteur(null, "Turquoise Vif", Role.TEXTE, "15 243 242", null), false);
			palette.ajouterValeur(new Acteur(null, "Vert citron", Role.TEXTE, "113 245 4", null), false);
			palette.ajouterValeur(new Acteur(null, "Vert Pastel", Role.TEXTE, "113 243 107", null), false);
			palette.ajouterValeur(new Acteur(null, "Turquoise Pastel", Role.TEXTE, "113 243 244", null), false);
			palette.ajouterValeur(new Acteur(null, "Jaune Vif", Role.TEXTE, "243 243 13", null), false);
			palette.ajouterValeur(new Acteur(null, "Jaune Pastel", Role.TEXTE, "243 243 109", null), false);
			palette.ajouterValeur(new Acteur(null, "Blanc brillant", Role.TEXTE, "255 243 249", null), false);
		} catch (ErreurException e1) {
			e1.printStackTrace();
		}

		// Especes graphiques :

		PrototypeGraphique graffiti = new PrototypeGraphique(toile, lib, "graffiti", Role.ESPECE, "graffiti", null, PrototypeGraphique.TYPE_GRAPHIQUE.GRAFFITI);
		graffiti.addAttribut(x);
		graffiti.addAttribut(y);
		graffiti.addAttribut(texte);
		graffiti.addAttribut(couleur);
		graffiti.addAttribut(police);
		graffiti.addAttribut(taille);
		graffiti.addAttribut(transparence);
		graffiti.addAttribut(visible);
		graffiti.addAttribut(position);
		graffiti.addAttribut(angle);
		graffiti.addAttribut(attributtoile);
		linotte.especeModeleMap.add(graffiti);

		// Le scribe
		PrototypeGraphique scribe = new PrototypeGraphique(toile, lib, "scribe", Role.ESPECE, "scribe", null, PrototypeGraphique.TYPE_GRAPHIQUE.SCRIBE);
		scribe.addAttribut(x);
		scribe.addAttribut(y);
		scribe.addAttribut(texte);
		scribe.addAttribut(taquet);
		scribe.addAttribut(couleur);
		scribe.addAttribut(police);
		scribe.addAttribut(taille);
		scribe.addAttribut(transparence);
		scribe.addAttribut(visible);
		scribe.addAttribut(position);
		scribe.addAttribut(angle);
		scribe.addAttribut(attributtoile);
		linotte.especeModeleMap.add(scribe);

		// La toile
		PrototypeGraphique latoile = new PrototypeGraphique(toile, lib, "toile", Role.ESPECE, "toile", null, PrototypeGraphique.TYPE_GRAPHIQUE.TOILE);
		latoile.addAttribut(couleur);
		latoile.addAttribut(visible);
		latoile.addAttribut(image);
		latoile.addAttribut(transparence);
		latoile.addAttribut(largeur);
		latoile.addAttribut(hauteur);
		latoile.addAttribut(x_toile);
		latoile.addAttribut(y_toile);
		latoile.addAttribut(bordure);
		latoile.addAttribut(audessus);
		latoile.addAttribut(curseur);
		latoile.addAttribut(tampon);
		latoile.addAttribut(pleinecran);
		latoile.addAttribut(principale);
		latoile.addAttribut(sourisx);
		latoile.addAttribut(sourisy);
		latoile.addAttribut(rx);
		latoile.addAttribut(ry);
		linotte.especeModeleMap.add(latoile);

		// Une image
		PrototypeGraphique uneimage = new PrototypeGraphique(toile, lib, "graphique", Role.ESPECE, "graphique", null, PrototypeGraphique.TYPE_GRAPHIQUE.IMAGE);
		uneimage.addAttribut(visible);
		uneimage.addAttribut(image);
		uneimage.addAttribut(transparence);
		uneimage.addAttribut(x);
		uneimage.addAttribut(y);
		uneimage.addAttribut(position);
		uneimage.addAttribut(angle);
		uneimage.addAttribut(taille);
		uneimage.addAttribut(attributtoile);
		linotte.especeModeleMap.add(uneimage);

		// L'anime
		PrototypeGraphique anime = new PrototypeGraphique(toile, lib, "praxinoscope", Role.ESPECE, "praxinoscope", null,
				PrototypeGraphique.TYPE_GRAPHIQUE.PRAXINOSCOPE);
		anime.addAttribut(visible);
		Acteur pict;
		for (int i = 0; i < 50; i++) {
			pict = new Acteur(null, "image" + i, Role.TEXTE, null, null);
			anime.addAttribut(pict);
		}
		// anime.addAttribut(image);
		anime.addAttribut(transparence);
		anime.addAttribut(x);
		anime.addAttribut(y);
		anime.addAttribut(position);
		anime.addAttribut(trame);
		anime.addAttribut(angle);
		anime.addAttribut(taille);
		anime.addAttribut(attributtoile);
		linotte.especeModeleMap.add(anime);

		// Un point
		PrototypeGraphique unpoint = new PrototypeGraphique(toile, lib, "point", Role.ESPECE, "point", null, PrototypeGraphique.TYPE_GRAPHIQUE.POINT);
		unpoint.addAttribut(visible);
		unpoint.addAttribut(transparence);
		unpoint.addAttribut(taille);
		unpoint.addAttribut(couleur);
		unpoint.addAttribut(x);
		unpoint.addAttribut(y);
		unpoint.addAttribut(position);
		unpoint.addAttribut(angle);
		unpoint.addAttribut(texture);
		unpoint.addAttribut(attributtoile);
		linotte.especeModeleMap.add(unpoint);

		// Un cercle
		PrototypeGraphique uncercle = new PrototypeGraphique(toile, lib, "cercle", Role.ESPECE, "cercle", null, PrototypeGraphique.TYPE_GRAPHIQUE.CERCLE);
		uncercle.addAttribut(visible);
		uncercle.addAttribut(transparence);
		uncercle.addAttribut(taille);
		uncercle.addAttribut(couleur);
		uncercle.addAttribut(x);
		uncercle.addAttribut(y);
		uncercle.addAttribut(rayon);
		uncercle.addAttribut(position);
		uncercle.addAttribut(plein);
		uncercle.addAttribut(angle);
		uncercle.addAttribut(texture);
		uncercle.addAttribut(attributtoile);
		linotte.especeModeleMap.add(uncercle);

		// Une ligne
		PrototypeGraphique uneligne = new PrototypeGraphique(toile, lib, "ligne", Role.ESPECE, "ligne", null, PrototypeGraphique.TYPE_GRAPHIQUE.LIGNE);
		uneligne.addAttribut(visible);
		uneligne.addAttribut(transparence);
		uneligne.addAttribut(taille);
		uneligne.addAttribut(couleur);
		uneligne.addAttribut(x1);
		uneligne.addAttribut(y1);
		uneligne.addAttribut(x2);
		uneligne.addAttribut(y2);
		uneligne.addAttribut(position);
		uneligne.addAttribut(angle);
		uneligne.addAttribut(texture);
		uneligne.addAttribut(attributtoile);
		linotte.especeModeleMap.add(uneligne);

		// Un rectangle
		PrototypeGraphique rectangle = new PrototypeGraphique(toile, lib, "rectangle", Role.ESPECE, "rectangle", null,
				PrototypeGraphique.TYPE_GRAPHIQUE.RECTANGLE);
		rectangle.addAttribut(visible);
		rectangle.addAttribut(transparence);
		rectangle.addAttribut(taille);
		rectangle.addAttribut(couleur);
		rectangle.addAttribut(x);
		rectangle.addAttribut(y);
		rectangle.addAttribut(hauteur);
		rectangle.addAttribut(largeur);
		rectangle.addAttribut(position);
		rectangle.addAttribut(plein);
		rectangle.addAttribut(angle);
		rectangle.addAttribut(texture);
		rectangle.addAttribut(attributtoile);
		linotte.especeModeleMap.add(rectangle);

		// Le crayon
		PrototypeGraphique crayon = new PrototypeGraphique(toile, lib, "crayon", Role.ESPECE, "crayon", null, PrototypeGraphique.TYPE_GRAPHIQUE.CRAYON);
		crayon.addAttribut(visible);
		crayon.addAttribut(transparence);
		crayon.addAttribut(taille);
		crayon.addAttribut(couleur);
		crayon.addAttribut(x);
		crayon.addAttribut(y);
		crayon.addAttribut(position);
		crayon.addAttribut(baisser);
		crayon.addAttribut(pointe);
		crayon.addAttribut(angle2);
		crayon.addAttribut(collision);
		crayon.addAttribut(texture);
		crayon.addAttribut(attributtoile);
		linotte.especeModeleMap.add(crayon);

		// Le parchemin
		PrototypeGraphique parchemin = new PrototypeGraphique(toile, lib, "parchemin", Role.ESPECE, "parchemin", null,
				PrototypeGraphique.TYPE_GRAPHIQUE.PARCHEMIN);
		parchemin.addAttribut(x);
		parchemin.addAttribut(y);
		parchemin.addAttribut(texte);
		parchemin.addAttribut(couleur);
		parchemin.addAttribut(police);
		parchemin.addAttribut(taille);
		parchemin.addAttribut(transparence);
		parchemin.addAttribut(visible);
		parchemin.addAttribut(position);
		parchemin.addAttribut(largeur);
		parchemin.addAttribut(angle);
		parchemin.addAttribut(attributtoile);
		linotte.especeModeleMap.add(parchemin);

		// Le mégalithe
		PrototypeGraphique mégalithe = new PrototypeGraphique(toile, lib, "mégalithe", Role.ESPECE, "mégalithe", null,
				PrototypeGraphique.TYPE_GRAPHIQUE.MEGALITHE);
		mégalithe.addAttribut(x);
		mégalithe.addAttribut(y);
		mégalithe.addAttribut(transparence);
		mégalithe.addAttribut(visible);
		mégalithe.addAttribut(position);
		mégalithe.addAttribut(angle);
		mégalithe.addAttribut(attributtoile);
		linotte.especeModeleMap.add(mégalithe);

		// Un polygone
		PrototypeGraphique polygone = new PrototypeGraphique(toile, lib, "polygone", Role.ESPECE, "polygone", null, PrototypeGraphique.TYPE_GRAPHIQUE.POLYGONE);
		polygone.addAttribut(visible);
		polygone.addAttribut(transparence);
		polygone.addAttribut(taille);
		polygone.addAttribut(couleur);
		polygone.addAttribut(position);
		polygone.addAttribut(plein);
		polygone.addAttribut(angle);
		polygone.addAttribut(x);
		polygone.addAttribut(y);
		polygone.addAttribut(attributtoile);
		Acteur tx, ty;
		for (int i = 1; i < 51; i++) {
			tx = new Acteur(null, "dx" + i, Role.NOMBRE, null, null);
			ty = new Acteur(null, "dy" + i, Role.NOMBRE, null, null);
			polygone.addAttribut(tx);
			polygone.addAttribut(ty);
		}
		polygone.addAttribut(texture);
		polygone.addAttribut(texture);
		linotte.especeModeleMap.add(polygone);

		// Un chemin
		PrototypeGraphique chemin = new PrototypeGraphique(toile, lib, "chemin", Role.ESPECE, "chemin", null, PrototypeGraphique.TYPE_GRAPHIQUE.CHEMIN);
		chemin.addAttribut(visible);
		chemin.addAttribut(transparence);
		chemin.addAttribut(taille);
		chemin.addAttribut(couleur);
		chemin.addAttribut(position);
		chemin.addAttribut(angle);
		chemin.addAttribut(x);
		chemin.addAttribut(y);
		for (int i = 1; i < 51; i++) {
			tx = new Acteur(null, "dx" + i, Role.NOMBRE, null, null);
			ty = new Acteur(null, "dy" + i, Role.NOMBRE, null, null);
			chemin.addAttribut(tx);
			chemin.addAttribut(ty);
		}
		chemin.addAttribut(attributtoile);
		linotte.especeModeleMap.add(chemin);

		// Espece entité
		Prototype entite = new Prototype(lib, "entité", Role.ESPECE, "entité", null);
		linotte.especeModeleMap.add(entite);

		// Espece mozaïque
		PrototypeGraphique mozaique = new PrototypeGraphique(toile, lib, "mozaïque", Role.ESPECE, "mozaïque", null, PrototypeGraphique.TYPE_GRAPHIQUE.MOZAIQUE);
		mozaique.addAttribut(visible);
		mozaique.addAttribut(transparence);
		mozaique.addAttribut(taille);
		mozaique.addAttribut(position);
		mozaique.addAttribut(angle);
		mozaique.addAttribut(modele);
		mozaique.addAttribut(palette);
		mozaique.addAttribut(x);
		mozaique.addAttribut(y);
		mozaique.addAttribut(attributtoile);
		linotte.especeModeleMap.add(mozaique);
	}

}