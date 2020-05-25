/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : May 5, 2004                                  *
 * Author : Mounès Ronan metalm@users.berlios.de                       *
 *                                                                     *
 *     http://jcubitainer.berlios.de/                                  *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 * metalm@users.berlios.de                                             *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

/* History & changes **************************************************
 *                                                                     *
 ******** May 5, 2004 **************************************************
 *   - First release                                                   *
 ***********************************************************************/

package org.linotte.moteur.outils;

import org.jdesktop.swingx.image.ColorTintFilter;
import org.kordamp.ikonli.runestroicons.Runestroicons;
import org.kordamp.ikonli.swing.FontIcon;
import org.linotte.frame.atelier.Atelier;
import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.theme.ThemeManager;
import org.linotte.greffons.externe.Greffon.RessourceManager;
import org.linotte.moteur.xml.analyse.multilangage.Langage;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ressources implements RessourceManager {

    private static final String CHEMIN = "/";
    private static final String CHEMIN_GREFFONS = "greffons";
    private static final String ENTETE_INTERNET = "http://";
    private static final Ressources instance = new Ressources();
    private static File EDT = null;
    private static URLClassLoader urlLoader = null;
    /**
     * Cache pour les images
     */
    private static Map<String, Image> cacheImages = new HashMap<String, Image>();

    static {
        try {
            FileSystemView fsv = FileSystemView.getFileSystemView();
            EDT = new File(fsv.getDefaultDirectory(), "EspaceDeTravail");
        } catch (Throwable e) {
            // Ne rien faire, on peut être dans une applet.
        }
    }

    public LaToile toile;

    public Ressources() {
    }

    public Ressources(LaToile t) {
        toile = t;
    }

    public static Image getImageFromHome(String nom_image) {
        Image image = null;
        String chemin = construireChemin(nom_image);
        try {
            image = ImageIO.read(new File(chemin));
        } catch (Exception e) {
            // Si applet :
            try {
                // e.printStackTrace();
                java.net.URL imgURL = Ressources.class.getResource(nom_image);
                if (imgURL != null) {
                    System.out.println("Tentative mode applet pour " + nom_image);
                    image = new ImageIcon(imgURL).getImage();
                }
            } catch (Exception e1) {
            }
        }
        return image;
    }

    public static ImageIcon getImageIcon(String nom_image) {
        // On recherche dans le thème en premier :
        if (ThemeManager.getCurrent() != null) {
            Image image = ThemeManager.getCurrent().getImage(nom_image);
            if (image != null) {
                ImageIcon imageIcon = new ImageIcon(image);
                return imageIcon;
            }
        }
        // Traitement habituel :
        java.net.URL imgURL = Ressources.class.getResource(CHEMIN + nom_image);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            return null;
        }

    }

    public static FontIcon getImageTheme(String pnom, int taille) {
        return getImageTheme(pnom, taille, CouleurImage.NORMAL);
    }

    private static Color transformeCouleur(CouleurImage couleurImage) {
        switch (couleurImage) {
            case DISABLED:
                if (Preference.getIntance().themeNoir())
                    return Color.GRAY.darker();
                else
                    return Color.lightGray;
            case PRESSED:
                return Color.darkGray;
            case ACTIF:
                return Color.RED;
            case LIRE:
                return new Color(63, 72, 204);
            default:
                if (Preference.getIntance().themeNoir())
                    return Color.lightGray;
                else
                    return Color.GRAY.darker();
        }
    }

    public static FontIcon getImageTheme(String pnom, int taille, CouleurImage couleurImage) {
        Color couleur = transformeCouleur(couleurImage);
        String nom = "AFTER_EFFECTS";
        switch (pnom) {
            case "PLAY":
                nom = "PLAY";
                break;
            case "DEBUG":
                nom = "PAUSE";
                break;
            case "STOP":
                nom = "STOP";
                break;
            case "TEST":
                nom = "OK";
                break;
            case "MEM":
                nom = "SELECT_CELLS";
                break;
            case "SAVE":
                nom = "SAVE";
                break;
            case "LIB":
                nom = "BOOKS";
                break;
            case "EDIT":
                nom = "EDIT";
                break;
            case "TOOLS":
                nom = "ATTRACTION";
                break;
            case "HELP":
                nom = "SAFARI";
                break;
            case "EDT":
                nom = "HOME2";
                break;
            case "TUTO":
                nom = "SCIENCE";
                break;
            case "PLUS":
                nom = "LINK";
                break;
            case "NEW":
                nom = "NEWSPAPER";
                break;
            case "VISUEL":
                nom = "SELECT";
                break;
            case "LINK":
                nom = "GLOBE";
                break;
            case "DIR":
                nom = "ADDTHIS";
                break;
            case "EXPLORE":
                nom = "ELLIPSIS";
                break;
            case "DOC":
                nom = "INFO_CIRCLE";
                break;
            case "GO":
                nom = "TERMINAL";
                break;
            case "NEXT":
                nom = "FAST_FORWARD";
                break;
            case "CODE":
                nom = "SELECT";
                break;
        }
        FontIcon icon = FontIcon.of(Runestroicons.valueOf(nom));
        icon.setIconColor(couleur);
        icon.setIconSize(taille);
        return icon;
    }

    public static ImageIcon getImageIconePlusClaire(String nom_image) {
        // On recherche dans le thème en premier :
        if (ThemeManager.getCurrent() != null) {
            Image image = ThemeManager.getCurrent().getImage(nom_image);
            if (image != null) {
                BufferedImage bi = toBufferedImage(image);
                RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null);
                rescaleOp.filter(bi, bi);
                ImageIcon imageIcon = new ImageIcon(bi);
                return imageIcon;
            }
        }
        // Traitement habituel :
        java.net.URL imgURL = Ressources.class.getResource(CHEMIN + nom_image);
        if (imgURL != null) {
            Image image = new ImageIcon(imgURL).getImage();
            if (image != null) {
                BufferedImage bi = toBufferedImage(image);
                RescaleOp rescaleOp = new RescaleOp(1.2f, 15, null);
                rescaleOp.filter(bi, bi);
                ImageIcon imageIcon = new ImageIcon(bi);
                return imageIcon;
            }
            return null;
        } else {
            return null;
        }

    }

    public static Icon getImageIconeTeintee(Color couleur, String nom_image) {
        // On recherche dans le thème en premier :
        if (ThemeManager.getCurrent() != null) {
            Image image = ThemeManager.getCurrent().getImage(nom_image);
            if (image != null) {
                ColorTintFilter colorFilter = new ColorTintFilter(couleur, .4f);
                BufferedImage bi = toBufferedImage(image);
                colorFilter.filter(bi, bi);
                ImageIcon imageIcon = new ImageIcon(bi);
                return imageIcon;
            }
        }
        // Traitement habituel :
        java.net.URL imgURL = Ressources.class.getResource(CHEMIN + nom_image);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            return null;
        }

    }

    public static URL getURL(String nom) {
        return Ressources.class.getResource(CHEMIN + nom);
    }

    public static File getGreffons() {
        return getdirectory(CHEMIN_GREFFONS);
    }

    public static File getExemples(Langage langage) {
        return getdirectory(langage.getCheminExemple());
    }

    public static boolean creationEDT() {
        File home = EDT;
        if (!home.exists()) {
            home.mkdirs();
            return true;
        }
        return false;
    }

    public static File getEDT() {
        File home = EDT;
        if (!home.exists()) {
            home.mkdirs();
        }
        return home;
    }

    public static File getdirectory(String dir) {
        // http://www.rgagnon.com/howto.html
        try {
            File current = new File(new Ressources().getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
            if (current.getName().toLowerCase().endsWith(".jar")) {
                // Si je suis dans un jar :
                current = current.getParentFile();
            } else if (current.getName().toLowerCase().endsWith(".exe")) {
                // Si je suis dans un jar :
                current = current.getParentFile();
            } else if (current.getName().toLowerCase().endsWith("classes")) {
                // Si je suis dans le repertoire classes :
                current = current.getParentFile();
            }
            File dg = new File(current, dir);
            // System.out.println("Chemin des greffons : " + dg);
            return dg;
        } catch (Exception e) {
            return null;
        }
    }

    public static InputStream getFlux(String nom_fichier) {
        return Ressources.class.getResourceAsStream("/" + nom_fichier);
    }

    public static InputStream getFromRessources(String nom_fichier) {
        return Ressources.class.getResourceAsStream(CHEMIN + nom_fichier);
    }

    /**
     * Récupére un fichier présent dans le répertoire Ressources
     *
     * @param nom_fichier
     * @return
     * @throws URISyntaxException
     */
    public static File getFileInRessources(String nom_fichier) throws URISyntaxException {
        return new File(Ressources.class.getResource(CHEMIN + nom_fichier).getFile());
    }

    public static InputStream getFluxFromHome(String nom_fichier) {
        return Ressources.class.getResourceAsStream(construireChemin(nom_fichier));
    }

    public static File getLocal() {
        URL url = ClassLoader.getSystemResource("/");
        if (url != null)
            return new File(url.getPath());
        else
            return new File(".");
    }

    public static void setCheminReference(File fichier) {
        List<URL> urls = new ArrayList<URL>(1);
        if (fichier != null && fichier.getParent() != null)
            try {
                urls.add(new File(fichier.getParent()).toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        else
            try {
                urls.add(getLocal().toURI().toURL());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        if (urlLoader != null)
            urlLoader = new URLClassLoader(urls.toArray(new URL[1]), urlLoader);
        else
            urlLoader = new URLClassLoader(urls.toArray(new URL[1]));
    }

    public static String construireChemin(String chemin) {
        if (urlLoader != null) {
            URL f = urlLoader.getResource(chemin);
            // System.out.println(f);
            if (f != null) {
                // System.out.println(urlLoader.getResource(chemin).getPath());
                try {
                    return urlLoader.getResource(chemin).toURI().getPath();
                } catch (URISyntaxException e) {
                    return chemin;
                }
            } else {
                // On essaye de construire le chemin à la "main"
                String pere = new File(chemin).getParent();
                if (pere != null) {
                    File parent = new File(pere);
                    if (parent.isAbsolute()) {
                        return chemin;
                    }
                }
                try {
                    return urlLoader.getURLs()[0].toURI().getPath() + File.separator + chemin;
                } catch (URISyntaxException e) {
                    return chemin;
                }
            }
        } else {
            return chemin;
        }
    }

    public static void clearCacheImages() {
        cacheImages.clear();
    }

    /**
     * Cette méthode charge une image d'un disque et prend en compte un cache
     * d'image
     *
     * @param fichier
     * @return
     */
    public static Image chargementImage(String fichier) {
        Image image = null;
        /**
         * Gestion du cache :
         */
        if (cacheImages.containsKey(fichier)) {
            return cacheImages.get(fichier);
        }
        if (instance.isInternetUrl(fichier)) {
            try {
                URL url = new URL(fichier);
                image = ImageIO.read(url);
            } catch (Exception e) {
            }

        } else {
            image = getImageFromHome(fichier);
        }
        /**
         * Ajout dans le cache
         */
        cacheImages.put(fichier, image);
        if (image == null) {
            // .getInterpreteur().getIhm().afficherErreur("Impossible de charger l'image : "
            // + fichier);
            try {
                LaToile toile = getInstance().toile;
                if (toile != null && toile.getFrameParent() != null && toile.getFrameParent() instanceof Atelier)
                    ((Atelier) toile.getFrameParent()).ecrireErreurTableau("Impossible de charger l'image : " + fichier);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return image;
    }

    public static Ressources getInstance() {
        return instance;
    }

    public static ImageIcon getScaledImage(ImageIcon myIcon2, int width, int height) {
        Image img = myIcon2.getImage();
        Image newimg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newimg);
    }

    /**
     * http://www.rgagnon.com/javadetails/java-0662.html
     *
     * @param name
     * @return
     */
    public static String sanitizeFilename(String name) {
        return name.replaceAll("[:\\\\/*?|<>]", "_");
    }

    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();

        // Determine if the image has transparent pixels; for this method's
        // implementation, see Determining If an Image Has Transparent Pixels
        boolean hasAlpha = hasAlpha(image);

        // Create a buffered image with a format that's compatible with the
        // screen
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            // Determine the type of transparency of the new buffered image
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            // Create the buffered image
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }

        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }

        // Copy image to buffered image
        Graphics g = bimage.createGraphics();

        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // This method returns true if the specified image has transparent pixels
    private static boolean hasAlpha(Image image) {
        // If buffered image, the color model is readily available
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage) image;
            return bimage.getColorModel().hasAlpha();
        }

        // Use a pixel grabber to retrieve the image's color model;
        // grabbing a single pixel is usually sufficient
        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }

        // Get the image's color model
        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }

    public Image getImage(String nom_image) {
        ImageIcon ii = getImageIcon(nom_image);
        return ii == null ? null : ii.getImage();
    }

    public boolean isInternetUrl(String url) {
        return url != null && url.toLowerCase().startsWith(ENTETE_INTERNET);
    }

    public String analyserChemin(String chemin) {
        return construireChemin(chemin);
    }

    public void viderUrlLoader() {
        urlLoader = null;
    }

    public void setChangement() {
        toile.getPanelLaToile().setChangement();
    }

}