/**
 *
 */
package org.linotte.alize;

import org.junit.Ignore;
import org.junit.Test;
import org.linotte.greffon.Boulier;
import org.linotte.greffon.Majordome;
import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.api.Greffon.Attribut;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.xml.Version;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author CPC
 *
 */
public class TestsNominaux extends AbstractSimpleAffichageTest {

    private static final String TEXTE = "texte";
    //private static final String NOMBRE = "nombre";

    public TestsNominaux() {
        super(ResourceBundle.getBundle("livres.tests_nominaux", new UTF8Control()));
    }

    enum LIVRE {
        BONJOUR, VERSION, MAJORDOME, MAJORDOME_VALEUR, BOULIER, PRENDRE, RUBY, PYTHON, //
        PROTOTYPE_ACTEUR, PROTOTYPE_ACTEUR_REMPLACER, PROTOTYPE_ACTEUR_AUTRES, GREFFON_LINOTTE, //
        PROTOTYPE_ACTEUR_ANONYME, FONCTION_PARAMETRE, PARAGRAPHE_PARAMETRE, ESPECE_CASSE, ESPECE_CASIERS, //
        SYNTAXE_SIMPLE_ADD_SOUS
    }

    @Test
    public void bonjour() throws Exception {
        assertEquals(executerLivre(LIVRE.BONJOUR), sortie(LIVRE.BONJOUR));
    }

    @Test
    public void version() throws Exception {
        assertEquals(executerLivre(LIVRE.VERSION), Version.getVersion());
    }

    @Test
    public void greffon() throws Exception {
        Set<Prototype> prototypes = new HashSet<Prototype>();
        Map<String, Attribut> attributs = new HashMap<String, Attribut>();
        attributs.put("serviteur", new Attribut(TEXTE, "Nestor"));
        prototypes.add(LinotteFacade.creationPrototype(attributs, new Majordome(), "majordometest"));
        assertEquals(executerLivre(LIVRE.MAJORDOME, prototypes), sortie(LIVRE.MAJORDOME));
    }

    @Test
    public void greffonAvecValeur() throws Exception {
        Set<Prototype> prototypes = new HashSet<Prototype>();
        Map<String, Attribut> attributs = new HashMap<String, Attribut>();
        attributs.put("serviteur", new Attribut(TEXTE, "Nestor"));
        prototypes.add(LinotteFacade.creationPrototype(attributs, new Majordome(), "majordometest2"));
        assertEquals(executerLivre(LIVRE.MAJORDOME_VALEUR, prototypes), sortie(LIVRE.MAJORDOME_VALEUR));
    }

    @Test
    public void boulier() throws Exception {
        Set<Prototype> prototypes = new HashSet<Prototype>();
        prototypes.add(LinotteFacade.creationPrototype(null, new Boulier(), "boulier"));
        assertEquals(executerLivre(LIVRE.BOULIER, prototypes), sortie(LIVRE.BOULIER));
    }

    /**
     * Linotte 2.6.1
     * @throws Exception
     */
    @Test
    public void prendre() throws Exception {
        assertEquals(executerLivre(LIVRE.PRENDRE), sortie(LIVRE.PRENDRE));
    }

    /**
     * Linotte 2.6.1
     * @throws Exception
     */
    @Test
    @Ignore
    public void greffonRuby() throws Exception {
        assertEquals(executerLivre(LIVRE.RUBY), sortie(LIVRE.RUBY));
    }

    @Test
    @Ignore
    public void greffonPython() throws Exception {
        assertEquals(executerLivre(LIVRE.PYTHON), sortie(LIVRE.PYTHON));
    }

    /**
     * Linotte 2.6.2
     * @throws Exception
     */
    @Test
    public void prototypeActeur() throws Exception {
        assertEquals(executerLivre(LIVRE.PROTOTYPE_ACTEUR), sortie(LIVRE.PROTOTYPE_ACTEUR));
    }

    /**
     * Linotte 2.6.2
     * @throws Exception
     */
    @Test
    public void prototypeRemplacerActeur() throws Exception {
        assertEquals(executerLivre(LIVRE.PROTOTYPE_ACTEUR_REMPLACER), sortie(LIVRE.PROTOTYPE_ACTEUR_REMPLACER));
    }

    /**
     * Linotte 2.6.2
     * @throws Exception
     */
    @Test
    public void prototypeAutresActeur() throws Exception {
        assertEquals(executerLivre(LIVRE.PROTOTYPE_ACTEUR_AUTRES), sortie(LIVRE.PROTOTYPE_ACTEUR_AUTRES));
    }

    /**
     * Linotte 2.6.2
     * @throws Exception
     */
    @Test
    @Ignore
    public void greffonLinotte() throws Exception {
        assertEquals(executerLivre(LIVRE.GREFFON_LINOTTE), sortie(LIVRE.GREFFON_LINOTTE));
    }

    /**
     * Linotte 2.6.2
     * @throws Exception
     */
    @Test
    public void prototypeActeurAnonyme() throws Exception {
        assertEquals(executerLivre(LIVRE.PROTOTYPE_ACTEUR_ANONYME), sortie(LIVRE.PROTOTYPE_ACTEUR_ANONYME));
    }

    @Test
    public void fonctionParametres() throws Exception {
        assertEquals(executerLivre(LIVRE.FONCTION_PARAMETRE), sortie(LIVRE.FONCTION_PARAMETRE));
    }

    @Test
    public void paragrapheParametres() throws Exception {
        assertEquals(executerLivre(LIVRE.PARAGRAPHE_PARAMETRE), sortie(LIVRE.PARAGRAPHE_PARAMETRE));
    }

    /**
     * Linotte 2.6.3
     * @throws Exception
     */
    @Test
    public void especeCasse() throws Exception {
        assertEquals(executerLivre(LIVRE.ESPECE_CASSE), sortie(LIVRE.ESPECE_CASSE));
    }

    /**
     * Linotte 2.6.4
     * @throws Exception
     */
    @Test
    public void especeCasier() throws Exception {
        assertEquals(executerLivre(LIVRE.ESPECE_CASIERS), sortie(LIVRE.ESPECE_CASIERS));
    }

    /**
     * Linotte 2.7
     * @throws Exception
     */
    @Test
    public void syntaxeSimple() throws Exception {
        assertEquals(executerLivre(LIVRE.SYNTAXE_SIMPLE_ADD_SOUS), sortie(LIVRE.SYNTAXE_SIMPLE_ADD_SOUS));
    }
}
