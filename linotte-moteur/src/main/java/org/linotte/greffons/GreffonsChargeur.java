package org.linotte.greffons;

import org.linotte.greffons.api.FabriqueGreffon;
import org.linotte.greffons.api.Greffon;
import org.linotte.greffons.java.JavaFactory;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Version;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cette classe centralise les fonctions pour trouver, instancier, stocker les
 * greffons en Java, Python
 *
 * @author CPC
 */
public class GreffonsChargeur {

    private static GreffonsChargeur instance;

    private static Map<String, FabriqueGreffon> fabriques = new HashMap<>();

    public Map<Long, org.linotte.greffons.externe.Greffon> cache = new HashMap<>();

    private Map<String, Greffon> greffons = new HashMap<>();

    static {
        try {
            // Prendre en compte les .class dans les repertoires greffons
            URL urls[] = {Ressources.getGreffons().toURI().toURL()};
            fabriques.put("java", new JavaFactory(urls));

            instance = new GreffonsChargeur();

        } catch (Throwable e) {
            if (Version.isBeta())
                e.printStackTrace();
        }
    }


    /**
     * Retourne la liste de tous les greffons chargés
     *
     * @return Tous les greffons
     */
    public List<Greffon> getGreffons() {
        return new ArrayList<Greffon>(greffons.values());
    }

    /**
     * Retourne le greffon <code>nom</code>
     *
     * @param nom
     * @return un greffon
     */
    public Greffon getGreffon(String nom) {
        return greffons.get(nom);
    }

    /**
     * Retourne une nouvelle instance d'un greffon
     *
     * @param id
     * @return le greffon
     */
    public org.linotte.greffons.externe.Greffon newInstance(String id) {
        Greffon greffon = greffons.get(id);
        if (greffon != null) {
            return greffon.newInstance();
        } else
            return null;
    }

    /**
     * Fabrique un nouveau greffon dans le langage <code>lang</code>
     *
     * @param id
     * @param classe
     * @param lang
     * @return le greffon
     */
    public Greffon ajouterGreffon(String id, String classe, String lang) {
        FabriqueGreffon fabriqueGreffon = fabriques.get(lang);
        if (fabriqueGreffon == null) {
            fabriqueGreffon = fabriques.get(lang);
        }
        if (greffons.get(id) != null) {
            // Greffon déjà présent :
            return null;
        } else {
            Greffon greffon = fabriqueGreffon.produire(id, classe);
            greffons.put(id, greffon);
            return greffon;
        }
    }

    /**
     * Ajoute un nouveau jar dans le classpath
     *
     * @param jar
     * @return
     */
    public boolean ajouterJar(String jar) {

        JavaFactory fabriqueGreffonJava = (JavaFactory) fabriques.get("java");

        File fj = new File(jar.replace("\\", "/"));
        try {
            fabriqueGreffonJava.addURL(fj.toURI().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static synchronized GreffonsChargeur getInstance() {
        return instance;
    }

}