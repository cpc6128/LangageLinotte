package org.linotte.greffons;

import org.linotte.greffons.api.Greffon.Attribut;
import org.linotte.greffons.impl.*;
import org.linotte.greffons.impl.swing.*;
import org.linotte.greffons.impl.swing.layout.Grille;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.api.Librairie;

import java.util.HashMap;
import java.util.Map;

/**
 * Cette classe initialise les espèces et les greffons internes
 *
 * @author CPC
 */
public final class BibliothequeGreffons {

    private static final String TEXTE = "texte";
    private static final String NOMBRE = "nombre";

    public BibliothequeGreffons() {
    }

    public void genererEspecesGraphiquesEtGreffonsInternes(Librairie<?> lib, Linotte linotte) throws Exception {

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

        LinotteFacade.creationPrototype(null, new Majordome(), "majordome");
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
        {
            Map<String, Attribut> attributs = new HashMap<String, Attribut>();
            attributs.put("largeur", new Attribut(NOMBRE, "5"));
            attributs.put("longueur", new Attribut(NOMBRE, "5"));
            LinotteFacade.creationPrototype(attributs, new Robonotte(), "robonotte");
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
            attributs.put("hauteur", new Attribut(NOMBRE, "100"));
            attributs.put("largeur", new Attribut(NOMBRE, "100"));
            LinotteFacade.creationPrototype(attributs, new Grille(), "grille");
        }
    }


}