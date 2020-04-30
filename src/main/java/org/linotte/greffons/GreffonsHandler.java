package org.linotte.greffons;

import org.linotte.greffons.api.Greffon;
import org.linotte.greffons.api.Greffon.Attribut;
import org.linotte.moteur.outils.Preference;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Handler SAX pour lire les fichiers greffon.xml
 *
 * @author CPC
 */
public class GreffonsHandler extends DefaultHandler {

    public static final String DIR_USER = "greffons";
    private static final String GREFFON = ".greffon.xml";
    private static GreffonsChargeur chargeur;
    private static File repertoire;
    private String classe;
    private String nom;
    private String auteur;
    private String version;
    private String description;
    private String valeur;
    private String espece;
    private String lang = null;
    private Map<String, Attribut> attributs;

    private Attribut attribut;

    private List<Greffon> greffons = new ArrayList<Greffon>();

    private GreffonsHandler() {
    }

    @SuppressWarnings("rawtypes")
    public static void loadLibrary(String newPath, String lib) throws Exception {
        // http://forums.sun.com/thread.jspa?threadID=627890

        // String javaLibraryPath = System.getProperty("java.library.path");
        // System.setProperty("java.library.path", javaLibraryPath +
        // File.pathSeparatorChar + newPath);
        // System.out.println(System.getProperty("java.library.path"));

        // Reset the "sys_paths" field of the ClassLoader to null.
        Class clazz = ClassLoader.class;
        Field field = clazz.getDeclaredField("sys_paths");
        boolean accessible = field.isAccessible();
        if (!accessible)
            field.setAccessible(true);
        Object original = field.get(clazz);
        // Reset it to null so that whenever "System.loadLibrary" is called, it
        // will be reconstructed with the changed value.
        field.set(clazz, null);
        try {
            // Change the value and load the library.
            System.setProperty("java.library.path", newPath);
            System.loadLibrary(lib);
        } finally {
            // Revert back the changes.
            field.set(clazz, original);
            field.setAccessible(accessible);
        }

    }

    public static List<Greffon> chargerGreffonGZL(File file) {
        //if (Version.isBeta())
        //	System.out.println("Extraction du fichier ZGL : " + file.getAbsolutePath());
        GreffonsHandler handler = new GreffonsHandler();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser;
            parser = factory.newSAXParser();
            GreffonsHandler.chargeur = GreffonsChargeur.getInstance();
            GreffonsHandler.repertoire = file.getParentFile();
            load(new FileInputStream(file), parser, handler, file.getName(), file.getParentFile());
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return handler.greffons;
    }

    public static void chargerGreffons(File repertoire) {

        if (repertoire != null) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser;
            GreffonsHandler handler = new GreffonsHandler();
            GreffonsHandler.chargeur = GreffonsChargeur.getInstance();
            GreffonsHandler.repertoire = repertoire;
            try {
                parser = factory.newSAXParser();

                // Descripteur dans un fichier greffon linotte
                List<File> greffons = new ArrayList<File>();

                {
                    File repertoire_user = new File(Preference.getIntance().getHome() + File.separator + Preference.REPERTOIRE + File.separator
                            + GreffonsHandler.DIR_USER);

                    if (repertoire_user.exists()) {
                        File[] fichiers2 = repertoire_user.listFiles(new FileFilter() {
                            public boolean accept(File pathname) {
                                return pathname.getName().endsWith(".zgl");
                            }
                        });
                        if (fichiers2 != null)
                            greffons.addAll(Arrays.asList(fichiers2));
                    }
                }

                {
                    File[] fichiers2 = repertoire.listFiles(new FileFilter() {
                        public boolean accept(File pathname) {
                            return pathname.getName().endsWith(".zgl");
                        }
                    });
                    if (fichiers2 != null)
                        greffons.addAll(Arrays.asList(fichiers2));
                }

                for (File file : greffons) {
                    //if (Version.isBeta())
                    //	System.out.println("Extraction du fichier ZGL : " + file.getAbsolutePath());
                    try {
                        load(new FileInputStream(file), parser, handler, file.getName(), file.getParentFile());
                    } catch (Exception e) {
                        e.printStackTrace();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

                // Descripteur hors du zip
                {
                    File[] fichiers = repertoire.listFiles(new FileFilter() {
                        public boolean accept(File pathname) {
                            return pathname.getName().endsWith(GREFFON);
                        }
                    });

                    if (fichiers != null)
                        for (File file : fichiers) {
                            //if (Version.isBeta())
                            //	System.out.println("Chargement du greffon : " + file.getName());
                            try {
                                parser.parse(new FileInputStream(file), handler);
                                parser.reset();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                }
                // Descripteur hors du zip dans le répertoire de l'utilisateur
                {
                    File repertoire_user = new File(Preference.getIntance().getHome() + File.separator + Preference.REPERTOIRE + File.separator
                            + GreffonsHandler.DIR_USER);
                    GreffonsHandler.repertoire = repertoire_user;
                    File[] fichiers = repertoire_user.listFiles(new FileFilter() {
                        public boolean accept(File pathname) {
                            return pathname.getName().endsWith(GREFFON);
                        }
                    });

                    if (fichiers != null)
                        for (File file : fichiers) {
                            //if (Version.isBeta())
                            //	System.out.println("Chargement du greffon : " + file.getName());
                            try {
                                parser.parse(new FileInputStream(file), handler);
                                parser.reset();
                            } catch (Exception e) {
                                e.printStackTrace();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                }
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

        }
    }

    public static void load(InputStream is, SAXParser parser, GreffonsHandler handler, String librairie, File repertoire) throws Exception {
        // Chargement de tous les fichiers
        ZipInputStream zis = new ZipInputStream(is);

        try {
            ZipEntry ze = null;
            int size = 0;
            while ((ze = zis.getNextEntry()) != null) {
                if (!ze.isDirectory() && ze.getName().toLowerCase().endsWith(GREFFON)) {
                    size = (int) ze.getSize();
                    // Extraction du fichier :

                    byte[] data = new byte[size];
                    int debut = 0;
                    int reste = 0;
                    while ((size - debut) > 0) {
                        reste = zis.read(data, debut, size - debut);
                        if (reste == -1) {
                            break;
                        }
                        debut += reste;
                    }
                    chargeur.ajouterJar(repertoire + "\\" + librairie);
                    //if (Version.isBeta())
                    //	System.out.println("Chargement du greffon : " + ze.getName());

                    parser.parse(new ByteArrayInputStream(data), handler);
                    parser.reset();

                }
            }
            zis.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotFoundException("Impossible de lire le fichier zip :" + e.toString());
        } finally {
            try {
                is.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        valeur = null;
        if ("greffon".equals(qName)) {
            auteur = "";
            version = "";
            description = "";
            espece = attributes.getValue("espece").toLowerCase();
            classe = attributes.getValue("class");
            lang = attributes.getValue("lang");
            nom = attributes.getValue("nom");
            attributs = new HashMap<String, Attribut>();
        } else if ("attribut".equals(qName)) {
            attribut = new Attribut(attributes.getValue("type"));
            attributs.put(attributes.getValue("nom"), attribut);
        }
    }

    @Override
    public void characters(char[] chars, int start, int length) {
        if (valeur == null) {
            valeur = new String(chars, start, length);
        } else {
            valeur = valeur + new String(chars, start, length);
        }
    }

    @Override
    public void endElement(String pUri, String pLocalName, String pQName) {
        if ("auteur".equals(pQName)) {
            auteur = valeur.trim();
        } else if ("version".equals(pQName)) {
            version = valeur.trim();
        } else if ("description".equals(pQName)) {
            description = valeur.trim();
        } else if ("attribut".equals(pQName)) {
            if (valeur != null)
                attribut.setValeur(valeur.trim());
        } else if ("lib".equals(pQName)) {
            chargeur.ajouterJar(repertoire + "\\" + valeur.trim());
        } else if ("jni".equals(pQName)) {
            try {
                loadLibrary(repertoire.getAbsolutePath(), valeur.trim());
            } catch (Throwable e) {
                System.err.println(e.getMessage());
            }
        } else if ("greffon".equals(pQName)) {
            Greffon g = null;
            try {
                g = chargeur.ajouterGreffon(espece, classe, lang);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            if (g != null) {
                g.setEspece(espece);
                g.setAttributs(attributs);
                g.setNom(nom);
                g.setLang(lang);
                g.setAuteur(auteur);
                g.setDescription(description);
                g.setVersion(version);
                greffons.add(g);
            } else {
                System.out.println("Impossible de charger le greffon : " + espece);
            }
        }
    }

}
