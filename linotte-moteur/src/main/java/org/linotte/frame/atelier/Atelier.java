/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

package org.linotte.frame.atelier;

import org.linotte.frame.cahier.Cahier;
import org.linotte.frame.cahier.Cahier.EtatCachier;
import org.linotte.frame.coloration.StyleBuilder;
import org.linotte.frame.coloration.StyleLinotte;
import org.linotte.frame.gui.JTextPaneText;
import org.linotte.frame.gui.PopupListener;
import org.linotte.frame.latoile.*;
import org.linotte.frame.moteur.ConsoleProcess;
import org.linotte.frame.moteur.Formater;
import org.linotte.frame.moteur.FrameProcess;
import org.linotte.greffons.GreffonsChargeur;
import org.linotte.greffons.api.AKMethod;
import org.linotte.greffons.java.JavaMethod;
import org.linotte.implementations.LibrairieVirtuelleSyntaxeV2;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.outils.JTextPaneToPdf;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.RegistreDesActions;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.alize.parseur.XMLIterator;
import org.linotte.moteur.xml.alize.parseur.a.NExpression;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.analyse.multilangage.Langage;
import org.linotte.web.Run;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.*;

/**
 * Affichage de l'interface principale de L'atelier Linotte.
 */
@SuppressWarnings("serial")
public class Atelier extends AtelierFrame implements WindowListener {


    // Gestion multi-langage
    public static Linotte linotte = null;
    private static Atelier atelier = null;
    private static LaToile toile;

    private Document sortieTableau = null;

    private final ImageIcon iconeLien = Ressources.getImageIcon("emblem-symbolic-link.png");
    private final ImageIcon iconeEditeurTexte = Ressources.getImageIcon("accessories-text-editor.png");

    // //////////////////////////////////////////////////////////// Maps pour le menu verbes
    private boolean saut2ligne = false;
    public List<OuvrirLivreAction> listHistorique = new ArrayList<OuvrirLivreAction>();
    // ////////////////////////////////////////////////////////////////// Menus
    //private FrameIHM dialogframeihm = null;
    private Map<String, Set<String>> slots_prototype = new HashMap<String, Set<String>>();
    private Map<String, String> verbes_imperatif = new HashMap<String, String>();
    private Map<String, Boolean> verbes_icone = new HashMap<String, Boolean>();
    private Map<String, String> verbes_conditions = new HashMap<String, String>();
    private Map<String, String> verbes_mathematiques = new HashMap<String, String>();
    private Map<String, String> verbes_boucle = new HashMap<String, String>();
    private Map<String, String> verbes_especes = new HashMap<String, String>();

    // Gestion des onglets :
    private Map<String, String> verbes_aide = new HashMap<String, String>();


    /**
     * Crée un nouvel atelier Linotte
     *
     */
    public Atelier() {
        super();
        atelier = this;
    }



    public static void initialisationFrameAtelierEtToile() throws InvocationTargetException, InterruptedException {
        Preference preference = Preference.getIntance();
        // Avant chargement de la fenetre :
        boolean auto = false;
        boolean latoile = false;
        boolean maximum = true;
        int px = 0, py = 0, pl = 0, ph = 0, tx = 0, ty = 0;// bh = 0, bv = 0;
        if (preference.getInt(Preference.P_HAUTEUR) != 0) {
            px = preference.getInt(Preference.P_X);
            py = preference.getInt(Preference.P_Y);
            ph = preference.getInt(Preference.P_HAUTEUR);
            pl = preference.getInt(Preference.P_LARGEUR);
            tx = preference.getInt(Preference.P_TOILE_X);
            ty = preference.getInt(Preference.P_TOILE_Y);
            latoile = preference.getBoolean(Preference.P_TOILE);
            maximum = preference.getBoolean(Preference.P_WINDOW_MAX);
            auto = true;

        } else {
            // comportement par defaut souhaite :
            preference.setBoolean(Preference.P_WINDOW_MAX, true);
        }

        Atelier atelier = new Atelier();
        List<Image> l = new ArrayList<Image>();
        l.add(Ressources.getImageIcon("linotte_new.png").getImage());
        atelier.setIconImages(l);
        atelier.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        atelier.initialisationComposantsAtelier();
        atelier.setResizable(true);

        Java6.appliquerIcone(Ressources.getImageIcon("linotte_new.png").getImage()); ;

        int taille = TAILLE_H + 10;
        int taille2 = taille + LaToile.LARGEUR + 10;
        int width = Toolkit.getDefaultToolkit().getScreenSize().width;
        int height = (Toolkit.getDefaultToolkit().getScreenSize().height - taille) / 2;

        if (maximum) {
            atelier.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {

            if (auto) {
                atelier.setLocation(px, py);
                atelier.setPreferredSize(new Dimension(pl, ph));
            } else {
                if (width > 1000) {
                    atelier.setLocation((width - (taille2)) / 2, height);
                } else {
                    atelier.setLocationRelativeTo(null);
                }
            }
        }

        if (auto) {
            toile = Toile.initToile(Atelier.linotte, true, atelier, false, false, latoile, tx, ty);
        } else {
            toile = Toile.initToile(Atelier.linotte, true, atelier, false, false, latoile, ((width - (taille2)) / 2) + taille, height);
        }
        toile.setVisible(latoile);

        SwingUtilities.invokeAndWait(() -> {
            atelier.pack();
            atelier.setVisible(true);
        });

        // Pour l'audit :
        linotte.getLibrairie().setToile(toile);
    }

    /**
     * This method initializes this
     *
     * @return void
     */
    public void initialisationComposantsAtelier() throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait(() -> {
            chargementLangageProgrammation();
        });
        SwingUtilities.invokeLater(() -> {
            demarrageServeurHTTP();
        });
        SwingUtilities.invokeLater(() -> {
            chargementDesFonts();
        });
        SwingUtilities.invokeAndWait(() -> {
            constructionAtelier();
            sortieTableau = getjEditorPaneTableau().getDocument();
        });
        SwingUtilities.invokeAndWait(() -> {
            constructionMenu();
            valeursParDefauts();
        });
        SwingUtilities.invokeAndWait(() -> {
            ajoutListenerDeplacement();
        });
        SwingUtilities.invokeLater(() -> {
            ajoutPopupMenu(getjEditorPaneTableau());

        });
        SwingUtilities.invokeAndWait(() -> {
            affichageMessagesRegistreDesActions();
        });
        SwingUtilities.invokeAndWait(() -> {
            initFilesChooser();
        });
        SwingUtilities.invokeAndWait(() -> {
            ouvertureExemples();
        });
        SwingUtilities.invokeAndWait(() -> {
            repertoireParDefaut();
        });
        SwingUtilities.invokeAndWait(() -> {
            chargerFichiers();
        });
        SwingUtilities.invokeLater(() -> {
            chargerHistorique();
        });
        SwingUtilities.invokeLater(() -> {
            creationSousMenuVerbier();
        });
        SwingUtilities.invokeLater(() -> {
            initialisationActions();
        });
        ecrirelnTableau("Prêt");
    }

    private void valeursParDefauts() {
        jMenuItemSaveWorkSpace.setSelected(getPreference(true, Preference.P_MODE_SAVE_WORKSPACE));
        jMenuItemTheme.setSelected(getPreference(false, Preference.P_DRACULA));
        jMenuItemPetitMenu.setSelected(getPreference(false, Preference.P_PETITMENU));
        jMenuItemDebogueur.setValue(getDelaisPasApas(400));
    }

    private void initialisationActions() {
        jMenuItemDisque.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // if
                // (verifierLivre("Voulez-vous vraiment ouvrir un autre livre ?"))
                // {
                int returnVal = fileChooser_ouvrir.showOpenDialog(FrameDialog.frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        ouvrirLivre(new File(fileChooser_ouvrir.getSelectedFile().getAbsolutePath()));
                    } catch (Exception e1) {
                    }
                }
            }
        });

        jButtonLire.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e0) {
                getCahierCourant().processStyle.setStopLecture();
                if (getCahierOnglet().nbOnglet() > 0)
                    FrameProcess.go(new StringBuilder(getCahierCourant().retourneTexteCahier() + "\n"), linotte, atelier, false, -1);
            }
        });

        jButtonTester.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e0) {
                getCahierCourant().processStyle.setStopLecture();
                if (getCahierOnglet().nbOnglet() > 0)
                    FrameProcess.go(new StringBuilder(getCahierCourant().retourneTexteCahier() + "\n"), linotte, atelier, true, -1);
            }
        });

        jButtonPasAPas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e0) {
                if (jButtonLire.isEnabled()) { // très moche
                    getCahierCourant().processStyle.setStopLecture();
                    if (getCahierOnglet().nbOnglet() > 0)
                        FrameProcess.go(new StringBuilder(getCahierCourant().retourneTexteCahier() + "\n"), linotte, atelier, false,
                                Preference.getIntance().getInt(Preference.P_PAS_A_PAS));
                } else {
                    jButtonContinuer.setVisible(false);
                    jButtonPasAPas.setVisible(false);
                    FrameProcess.continuerDebogage();
                }
            }
        });

        jMenuItemExporter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (fileChooser_exporter.showSaveDialog(FrameDialog.frame) == JFileChooser.APPROVE_OPTION) {
                        FileOutputStream fos = new FileOutputStream(fileChooser_exporter.getSelectedFile());
                        JTextPaneToPdf.paintToPDF(getCahierCourant().getEditorPanelCahier(), fos, "L'Atelier Linotte " + Version.getVersion());
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    JOptionPane.showMessageDialog(atelier, "Impossible d'exporter le livre !", "Rangement impossible", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        jMenuItemExporterHTML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (fileChooser_exporterHTML.showSaveDialog(FrameDialog.frame) == JFileChooser.APPROVE_OPTION) {
                        FileOutputStream fos = new FileOutputStream(fileChooser_exporterHTML.getSelectedFile());
                        getCahierCourant().scroller();
                        Thread.sleep(1000);
                        byte[] extractionCahier = extractionCahier(new HTMLEditorKit());
                        String flux = new String(extractionCahier);
                        flux = flux.replaceAll("\t", "&nbsp;&nbsp;");
                        fos.write(flux.getBytes());
                        fos.flush();
                        fos.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    JOptionPane.showMessageDialog(atelier, "Impossible d'exporter le livre !", "Rangement impossible", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        jMenuItemExporterRTF.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (fileChooser_exporterRTF.showSaveDialog(FrameDialog.frame) == JFileChooser.APPROVE_OPTION) {
                        FileOutputStream fos = new FileOutputStream(fileChooser_exporterRTF.getSelectedFile());
                        getCahierCourant().scroller();
                        fos.write(extractionCahier(new RTFEditorKit()));
                        fos.flush();
                        fos.close();
                    }
                } catch (Exception e2) {
                    e2.printStackTrace();
                    JOptionPane.showMessageDialog(atelier, "Impossible d'exporter le livre !", "Rangement impossible", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        jMenuItemFormater.addActionListener(e -> Formater.action(getCahierCourant(), linotte));

        jMenuItemLaToile.addActionListener(e -> ((JPanelLaToile) toile.getPanelLaToile()).getToileParent().setVisible(true));

        teleType.addActionListener(e -> {
            ConsoleProcess.go(new StringBuilder(teleType.entree.getText() + "\n"), linotte, atelier);
            if (teleType.entree.getText().trim().length() > 0) {
                teleType.commandes.remove(teleType.entree.getText());
                teleType.commandes.push(teleType.entree.getText());
            }
            teleType.entree.setText("");
            teleType.entree.grabFocus();
            teleType.position = -1;
        });

    }

    private void repertoireParDefaut() {
        File exemples = Ressources.getExemples(linotte.getLangage());
        if (exemples.isDirectory()) {
        } else {
            exemples = new File("/usr/share/langagelinotte/" + linotte.getLangage().getCheminExemple());
        }

        File temp = null;
        if (Preference.getIntance().getProperty(Preference.P_DIRECTORY) != null) {
            temp = new File(Preference.getIntance().getProperty(Preference.P_DIRECTORY));
        }
        if (temp == null && exemples.isDirectory()) {
            temp = exemples;
        }
        if (temp == null) {
            Ressources.getLocal();
        }
        fileChooser_ouvrir.setCurrentDirectory(temp);
        fileChooser_sauvegarder.setCurrentDirectory(temp);
        fileChooser_sauvegarder.setSelectedFile(new File(temp, "Nouveau.liv"));
    }

    private void ouvertureExemples() {
        // Si premier lancement :
        if (!Preference.getIntance().isExiste()) {
            Preference.getIntance().setProperty(Preference.P_FICHIER + "_1",
                    linotte.getLangage().getCheminExemple() + "/00_quoideneuf.liv");
            Preference.getIntance().setProperty(Preference.P_FICHIER + "_2",
                    linotte.getLangage().getCheminExemple() + "/01_bonjour.liv");

        }
    }

    private void initFilesChooser() {
        fileChooser_ouvrir = new JFileChooser(Ressources.getLocal());
        FiltreLivre filtre = new FiltreLivre(new String[]{"liv"}, "Livre Linotte (*.liv)");
        fileChooser_ouvrir.addChoosableFileFilter(filtre);
        fileChooser_ouvrir.setFileFilter(filtre);
        fileChooser_ouvrir.setDialogTitle("Ouvrir un livre Linotte");
        fileChooser_ouvrir.setApproveButtonText("Ouvrir le livre");
        // Ranger
        fileChooser_sauvegarder = new JFileChooser(Ressources.getLocal());
        FiltreLivre filtre2 = new FiltreLivre(new String[]{"liv"}, "Livre Linotte (*.liv)");
        fileChooser_sauvegarder.addChoosableFileFilter(filtre2);
        fileChooser_sauvegarder.setFileFilter(filtre2);
        fileChooser_sauvegarder.setDialogTitle("Ranger un livre Linotte");
        fileChooser_sauvegarder.setApproveButtonText("Ranger le livre");

        // Exporter PDF
        fileChooser_exporter = new JFileChooser(Ressources.getLocal());
        fileChooser_exporter.setSelectedFile(new File(Ressources.getLocal(), "Nouveau.pdf"));
        FiltreLivre filtre3 = new FiltreLivre(new String[]{"pdf"}, "Format PDF (*.pdf)");
        fileChooser_exporter.addChoosableFileFilter(filtre3);
        fileChooser_exporter.setFileFilter(filtre3);
        fileChooser_exporter.setDialogTitle("Exporter votre livre au format PDF");
        fileChooser_exporter.setApproveButtonText("Exporter le livre");

        // Exporter PNG
        fileChooser_exporterPNG = new JFileChooser(Ressources.getLocal());
        fileChooser_exporterPNG.setSelectedFile(new File(Ressources.getLocal(), "Nouveau.png"));
        FiltreLivre filtre4 = new FiltreLivre(new String[]{"png"}, "Format PNG (*.png)");
        fileChooser_exporterPNG.addChoosableFileFilter(filtre4);
        fileChooser_exporterPNG.setFileFilter(filtre4);
        fileChooser_exporterPNG.setDialogTitle("Exporter votre livre au format PNG");
        fileChooser_exporterPNG.setApproveButtonText("Exporter le livre");

        // Exporter HTML
        fileChooser_exporterHTML = new JFileChooser(Ressources.getLocal());
        fileChooser_exporterHTML.setSelectedFile(new File(Ressources.getLocal(), "Export.html"));
        FiltreLivre filtre5 = new FiltreLivre(new String[]{"html"}, "Format HTML (*.HTML)");
        fileChooser_exporterHTML.addChoosableFileFilter(filtre5);
        fileChooser_exporterHTML.setFileFilter(filtre5);
        fileChooser_exporterHTML.setDialogTitle("Exporter votre livre au format HTML");
        fileChooser_exporterHTML.setApproveButtonText("Exporter le livre");

        // Exporter RTF
        fileChooser_exporterRTF = new JFileChooser(Ressources.getLocal());
        fileChooser_exporterRTF.setSelectedFile(new File(Ressources.getLocal(), "Export.rtf"));
        FiltreLivre filtre6 = new FiltreLivre(new String[]{"rtf"}, "Format RTF (*.RTF)");
        fileChooser_exporterRTF.addChoosableFileFilter(filtre6);
        fileChooser_exporterRTF.setFileFilter(filtre6);
        fileChooser_exporterRTF.setDialogTitle("Exporter votre livre au format RTF");
        fileChooser_exporterRTF.setApproveButtonText("Exporter le livre");
    }

    private void affichageMessagesRegistreDesActions() {
        // Affichage des messages d'erreur
        Iterator<String> erreurs = RegistreDesActions.retourneErreurs();
        while (erreurs.hasNext()) {
            ecrireErreurTableau_(erreurs.next());
            ecrirelnTableau("");
        }
        RegistreDesActions.effaceErreurs();
    }

    private void ajoutPopupMenu(JTextPaneText jEditorPaneTableau) {
        // Popup pour effacer le tableau :
        JPopupMenu popupTableau = new JPopupMenu();
        JMenuItem itemPopupTableau = new JMenuItem("Effacer le tableau");
        itemPopupTableau.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                effacerTableau();
            }

        });

        // Popup pour copier le tableau :
        JMenuItem itemPopupTableau2 = new JMenuItem("Copier le tableau");
        itemPopupTableau2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                copierTableau();
            }

        });

        popupTableau.add(itemPopupTableau);
        popupTableau.add(itemPopupTableau2);
        MouseListener popupListener = new PopupListener(popupTableau);
        jEditorPaneTableau.addMouseListener(popupListener);
    }


    private boolean getPreference(boolean saveWorkSpace, String pModeSaveWorkspace) {
        if (Preference.getIntance().get(pModeSaveWorkspace) != null) {
            saveWorkSpace = Preference.getIntance().getBoolean(pModeSaveWorkspace);
        } else {
            Preference.getIntance().setBoolean(pModeSaveWorkspace, saveWorkSpace);
        }
        return saveWorkSpace;
    }

    private int getDelaisPasApas(int delais_pas_a_pas) {
        if (Preference.getIntance().get(Preference.P_PAS_A_PAS) != null) {
            delais_pas_a_pas = Preference.getIntance().getInt(Preference.P_PAS_A_PAS);
        } else {
            Preference.getIntance().setInt(Preference.P_PAS_A_PAS, delais_pas_a_pas);
        }
        return delais_pas_a_pas;
    }

    private void ajoutListenerDeplacement() {
        addComponentListener(new ComponentAdapter() {
            public void componentMoved(ComponentEvent ce) {
                Preference.getIntance().setInt(Preference.P_X, getX());
                Preference.getIntance().setInt(Preference.P_Y, getY());
                Preference.getIntance().setInt(Preference.P_HAUTEUR, getHeight());
                Preference.getIntance().setInt(Preference.P_LARGEUR, getWidth());
                // Preference.getIntance().setBoolean(Preference.P_WINDOW_MAX,
                // false);
            }

            public void componentResized(ComponentEvent e) {
                Preference.getIntance().setInt(Preference.P_X, getX());
                Preference.getIntance().setInt(Preference.P_Y, getY());
                Preference.getIntance().setInt(Preference.P_HAUTEUR, getHeight());
                Preference.getIntance().setInt(Preference.P_LARGEUR, getWidth());
            }

        });

        addWindowStateListener(new WindowStateListener() {
            public void windowStateChanged(WindowEvent e) {
                Preference.getIntance().setBoolean(Preference.P_WINDOW_MAX, (e.getNewState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH);
            }
        });
        addWindowListener(this);

    }

    private void chargementDesFonts() {
        // Gestion de la police :
        String sfont = Preference.getIntance().getProperty(Preference.P_FONT);
        if (sfont == null) {
            sfont = "Consolas";
            Preference.getIntance().setProperty(Preference.P_FONT, sfont);
        }
        int sftaille = Preference.getIntance().getInt(Preference.P_TAILLE_FONT);
        if (sftaille == 0) {
            sftaille = 14;
            Preference.getIntance().setInt(Preference.P_TAILLE_FONT, sftaille);
        }
        font = new Font(sfont, Font.PLAIN, sftaille);
    }

    private void chargementLangageProgrammation() {
        linotte = new Linotte(new LibrairieVirtuelleSyntaxeV2(), new FlatIHM(this), Langage.Linotte2);
    }

    private void demarrageServeurHTTP() {
        // Lancement du serveur HTTP :
        try {
            int port_webonotte = 7777;

            if (0 != Preference.getIntance().getInt(Preference.P_WEBONOTTE_PORT)) {
                port_webonotte = Preference.getIntance().getInt(Preference.P_WEBONOTTE_PORT);
            }
            String root = Preference.getIntance().getWebRoot();
            if (null != Preference.getIntance().getProperty(Preference.P_WEBONOTTE_DIR)) {
                root = Preference.getIntance().getProperty(Preference.P_WEBONOTTE_DIR);
            }
            Run.runStandEmbededServer("jetty", "Webonotte", linotte.getLibrairie(), port_webonotte, new File(root));
        } catch (Throwable e) {
            if (Version.isBeta())
                e.printStackTrace();
        }
    }

    private void chargerFichiers() {
        /*
         * Chargement des fichiers sauvegardés :
         */
        final List<String> fichiers = Preference.getIntance().getKeyFilter(Preference.P_FICHIER);
        /*
         * On affiche dans l'ordre enregistré :
         */
        Collections.sort(fichiers, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    return (new Integer(o1.substring(Preference.P_FICHIER.length() + 1)))
                            .compareTo(new Integer(o2.substring(Preference.P_FICHIER.length() + 1)));
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        if (!fichiers.isEmpty()) {
            for (String f : fichiers) {
                File fichier = new File(Preference.getIntance().getProperty(f));
                // splashWindow1.setProgressValue(8,
                // "Chargement du livre : " + fichier.getName());
                if (fichier.exists()) {
                    softCreerCahierCourant();
                    try {
                        getCahierCourant().chargerFichier(fichier);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            getCahierCourant().forceStyle();
        } else {
            // Initialisation de l'onglet vide :
            getCahierOnglet().ajouterCahier(getCahierCourant());
        }
    }

    /**
     * Chargement des fichiers historisés :
     */
    private void chargerHistorique() {
        List<String> historiques = Preference.getIntance().getKeyFilter(Preference.P_HISTORIQUE);

        /**
         * On affiche dans l'ordre enregistré :
         */
        Collections.sort(historiques, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                try {
                    return (new Integer(o1.substring(Preference.P_HISTORIQUE.length() + 1)))
                            .compareTo(new Integer(o2.substring(Preference.P_HISTORIQUE.length() + 1)));
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        if (!historiques.isEmpty()) {
            for (String f : historiques) {
                File fichier = new File(Preference.getIntance().getProperty(f));
                ajouterHistorique(fichier.getAbsoluteFile(), false);
            }
            rechargerHistorique();
        }
    }

    /**
     * Chargement des fichiers historisés :
     */
    private void rechargerHistorique() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jMenuHistorisque.removeAll();
                synchronized (listHistorique) {
                    if (!listHistorique.isEmpty()) {
                        for (OuvrirLivreAction f : listHistorique) {
                            jMenuHistorisque.add(new JMenuItem(f), 0);
                        }
                    }
                }
            }
        });
    }


    public void ecrirelnTableau(String texte) {
        if (saut2ligne) {
            ecrireSimpleTableau("\n", null);
            saut2ligne = false;
        }
        ecrireSimpleTableau(texte + "\n", null);
        goFinTableau();
    }

    public void ecrireTableau(String texte) {
        if (saut2ligne) {
            ecrireSimpleTableau("\n", null);
            saut2ligne = false;
        }
        ecrireSimpleTableau(texte, null);
        goFinTableau();
    }

    public void ecrireErreurTableau(String texte) {
        ((FlatIHM) linotte.getIhm()).getTableauBatch().ecrireErreur(texte);
    }

    public void ecrireErreurTableau_(String texte) {
        if (saut2ligne) {
            ecrireSimpleTableau("\n", null);
            saut2ligne = false;
        }
        ecrireSimpleTableau(texte, StyleLinotte.style_error);
        goFinTableau();
    }

    public void ecrireErreurTableau_(Object texte) {
        if (saut2ligne) {
            ecrireSimpleTableau("\n", null);
            saut2ligne = false;
        }
        ecrireSimpleTableau(texte, StyleLinotte.style_error);
        goFinTableau();
    }

    private void ecrireSimpleTableau(Object texte, AttributeSet attributeSet) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Suppression du curseur.
                if (sortieTableau.getLength() > 0) {
                    String carret = this.sortieTableau.getText(sortieTableau.getLength() - 1, 1);
                    if (carret.equals("\u2588")) {
                        this.sortieTableau.remove(sortieTableau.getLength() - 1, 1);
                    }
                }
                if (texte instanceof Component)
                    getjEditorPaneTableau().insertComponent((Component) texte);
                else {
                    this.sortieTableau.insertString(sortieTableau.getLength(), (String) texte + "", attributeSet);
                    // Ajout du curseur.
                    this.sortieTableau.insertString(sortieTableau.getLength(), "\u2588", null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void effacerTableau() {
        synchronized (sortieTableau) {
            try {
                sortieTableau.remove(0, sortieTableau.getLength());
                // Ajout du curseur.
                this.sortieTableau.insertString(sortieTableau.getLength(), "\u2588", null);
                saut2ligne = false;
            } catch (BadLocationException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void ecrireDynamiquementCachier(String texte) {
        try {
            if (getJButtonLire().isEnabled())
                getCahierCourant().getDocumentCahier().insertString(getCahierCourant().getCaretPosition(), texte, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void goFinTableau() {
        getjEditorPaneTableau().setCaretPosition(sortieTableau.getLength());
    }


    public void setLivreFromDragNDrop(File livre) {
        if (verifierLivre("Voulez-vous vraiment ouvrir un autre livre ?")) {
            try {
                ouvrirLivre(livre);
                // creerCahierCourant();
                // getCahierCourant().chargerFichier(livre);
            } catch (Exception e1) {
            }
        }
    }

    private byte[] extractionCahier(EditorKit editorKit) {
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        try {
            editorKit.write(str, getCahierCourant().getDocumentCahier(), 0, getCahierCourant().getDocumentCahier().getLength());
        } catch (Exception ex) {
        }
        return str.toByteArray();
    }


    private String capitalizeFirstLetter(String value) {
        if (value == null) {
            return null;
        }
        if (value.length() == 0) {
            return value;
        }
        StringBuilder result = new StringBuilder(value);
        result.replace(0, 1, result.substring(0, 1).toUpperCase());
        return result.toString();
    }

    /**
     * TODO: Méthode à réécrire.... très moche !
     *
     * @param verbe
     * @param id
     */
    private void creerJMenuVerbe(String verbe, String id) {
        verbe = capitalizeFirstLetter(verbe);

        JMenuItemID itemPresent = new JMenuItemID(verbe, id);

        if (!verbes_icone.get(id)) {
            itemPresent.setIcon(iconeLien);
        } else {
            itemPresent.setIcon(iconeEditeurTexte);
        }

        itemPresent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                getCahierCourant().ecrireInsertAuCurseurCachier(verbes_imperatif.get(((JMenuItemID) e.getSource()).getID()));
            }
        });
        if (verbes_conditions.get(id) == null) {
            if (verbes_especes.get(id) != null) {
                if (slots_prototype.get(id) != null) {
                    JMenuItem menu = creerMenu(verbe + "...");
                    jMenuEspeces.add(menu);
                    menu.add(itemPresent);
                    for (final String s : slots_prototype.get(id)) {
                        JMenuItem fils = new JMenuItem("." + s);
                        if (!verbes_icone.get(id)) {
                            fils.setIcon(iconeLien);
                        } else {
                            fils.setIcon(iconeEditeurTexte);
                        }
                        menu.add(fils);
                        fils.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent e) {
                                getCahierCourant().ecrireInsertAuCurseurCachier("." + s);
                            }
                        });
                    }
                } else {
                    jMenuEspeces.add(itemPresent);
                }
            } else if (verbes_mathematiques.get(id) == null) {
                if (verbes_boucle.get(id) == null)
                    jMenuVerbier.add(itemPresent);
                else
                    jMenuBoucle.add(itemPresent);
            } else {
                jMenuMathematiques.add(itemPresent);
            }
        } else {
            jMenuCondition.add(itemPresent);
        }
        if (verbes_aide.get(id) != null) {
            itemPresent.setToolTipText(verbes_aide.get(id));
        }
    }


    /**
     * Créée un menu contenant tout les verbes du langage
     *
     * @param racine L'élément XML racine
     */
    private void creerJMenuVerbe(Element racine) {

        XMLIterator l1 = new XMLIterator(racine.getElementsByTagName("parametre"));

        List<String> liste = new ArrayList<String>();
        while (l1.hasNext()) {
            Noeud node = l1.next();
            XMLIterator it = node.getFils();
            while (it.hasNext()) {
                NExpression n = (NExpression) it.next();
                if ("aide".equals(node.getAttribut("nom"))) {
                    String clef = n.getValeur().toString();
                    liste.add(clef);
                    if (n.getAttribut("aide") != null) {
                        verbes_aide.put(clef, n.getAttribut("aide"));
                    }
                    if (n.getAttribut("texte") == null && n.getAttribut("math") == null && n.getAttribut("boucle") == null) {
                        verbes_imperatif.put(clef, filtre(n.getAttribut("imperatif")));
                        verbes_icone.put(clef, n.getAttribut("icone") != null);
                    } else {
                        if (n.getAttribut("texte") != null) {
                            verbes_conditions.put(clef, filtre(n.getAttribut("texte")));
                            verbes_icone.put(clef, n.getAttribut("icone") != null);
                            verbes_imperatif.put(clef, filtre(n.getAttribut("texte")));
                        } else {
                            if (n.getAttribut("math") != null) {
                                verbes_mathematiques.put(clef, filtre(n.getAttribut("math")));
                                verbes_icone.put(clef, n.getAttribut("icone") != null);
                                verbes_imperatif.put(clef, filtre(n.getAttribut("math")));
                            } else {
                                if (n.getAttribut("boucle") != null) {
                                    verbes_boucle.put(clef, filtre(n.getAttribut("boucle")));
                                    verbes_icone.put(clef, n.getAttribut("icone") != null);
                                    verbes_imperatif.put(clef, filtre(n.getAttribut("boucle")));
                                }
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(liste);
        Iterator<String> i = liste.iterator();
        while (i.hasNext()) {
            String s = i.next();
            creerJMenuVerbe(s, s);
        }
    }

    private String filtre(String f) {
        String user = System.getProperty("user.name");
        return f.replaceAll(":auteur:", user).replaceAll(":ligne:", "\n").replaceAll(":tab:", "\t").replaceAll(":version:", linotte.getGrammaire().getSPEC())
                .replaceAll(":g:", "\"");
    }

    public void copierTableau() {
        String selection = getjEditorPaneTableau().getSelectedText();
        if (selection == null)
            selection = getjEditorPaneTableau().getText();
        StringSelection clipString = new StringSelection(selection);
        clipbd.setContents(clipString, clipString);
    }

    public void windowActivated(WindowEvent arg0) {
    }

    public void windowClosed(WindowEvent arg0) {
    }

    public void windowClosing(WindowEvent arg0) {
        if (verifierLivre("Voulez-vous vraiment quitter l'Atelier Linotte ?")) {
            // Sauvegarder les onglets automatiques ?
            Preference.getIntance().nettoyer(Preference.P_FICHIER);
            if (Preference.getIntance().getBoolean(Preference.P_MODE_SAVE_WORKSPACE)) {
                List<String> fichiers = getCahierOnglet().getFichiersOuverts();
                int i = 0;
                for (String fichier : fichiers) {
                    i++;
                    Preference.getIntance().setProperty(Preference.P_FICHIER + "_" + i, fichier);
                }
            }
            // Sauvegarde de l'historique des fichiers
            Preference.getIntance().nettoyer(Preference.P_HISTORIQUE);
            int i = 0;
            for (OuvrirLivreAction fichier : listHistorique) {
                i++;
                Preference.getIntance().setProperty(Preference.P_HISTORIQUE + "_" + i, fichier.info.getAbsolutePath());
            }

            Preference.getIntance().save();
            StyleBuilder.enregistrer();
            System.exit(0);
        }
    }

    public void windowDeactivated(WindowEvent arg0) {
    }

    public void windowDeiconified(WindowEvent arg0) {
    }

    public void windowIconified(WindowEvent arg0) {
    }

    public void windowOpened(WindowEvent arg0) {
    }

    public boolean verifierLivre(String message) {
        if (getCahierOnglet().verifierEtat(EtatCachier.MODIFIE)) {
            int result = JOptionPane.showConfirmDialog(this, message, "Un livre n'est pas sauvegardé !", JOptionPane.YES_NO_OPTION);
            return result == JOptionPane.YES_OPTION;
        } else {
            return true;
        }
    }

    private void creerJMenuEspeces(Linotte linotte) {
        List<String> noms = new ArrayList<String>();
        Map<String, Prototype> map = new HashMap<String, Prototype>();
        for (Prototype e : linotte.especeModeleMap) {
            String n = e.getNom().toString();
            noms.add(n);
            map.put(n, e);
            Set<String> slots = e.retourneSlotsGreffons();
            Set<String> slotsLinotte = e.retourneSlotsLinotte();
            Set<String> slots_complet = new HashSet<String>();
            for (String slt : slotsLinotte) {
                slots_complet.add(slt + "()");
            }
            for (String slt : slots) {
                // On récupère les paramètres :
                AKMethod akmethod = e.retourneSlotGreffon(slt);
                if (akmethod instanceof JavaMethod) {
                    JavaMethod method = (JavaMethod) akmethod;
                    slt += method.parametres();
                }
                slots_complet.add(slt);
            }
            if (slots_complet != null && slots_complet.size() > 0)
                slots_prototype.put(n, slots_complet);
        }
        Collections.sort(noms);
        for (String string : noms) {
            Prototype e = map.get(string);
            String texte = e.getGreffon() == null ? e.toTexte() : GreffonsChargeur.getInstance().getGreffon(string).toTexte();
            String aide = e.getGreffon() == null ? null : GreffonsChargeur.getInstance().getGreffon(string).toString();
            verbes_especes.put(string, string);
            verbes_icone.put(string, false);
            verbes_imperatif.put(string, texte);
            if (aide != null)
                verbes_aide.put(string, aide);
        }
        for (String n : noms) {
            creerJMenuVerbe(n + ((map.get(n).getGreffon() == null) ? "" : ""), n);
        }

    }

    private void creerJMenuCouleurs() {
        List<String> couleurs = Couleur.retourneCouleurs();
        Collections.sort(couleurs);
        int i = 0;
        int m = 1;
        JMenu jMenuCouleurs = null;
        for (String c : couleurs) {
            if (i % 40 == 0) {
                jMenuCouleurs = getJMenuCouleurs(m);
                jMenuVerbier.add(jMenuCouleurs);
                m++;
            }
            JMenuItem itemCouleur = new JMenuItem(c);
            Image ic = createImage(15, 15);
            Graphics graphics = ic.getGraphics();
            graphics.setColor(Couleur.retourneCouleur(c));
            graphics.fillRect(0, 0, 15, 15);
            itemCouleur.setIcon(new ImageIcon(ic));
            itemCouleur.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    getCahierCourant().ecrireInsertAuCurseurCachier(((JMenuItem) e.getSource()).getText());
                }
            });
            jMenuCouleurs.add(itemCouleur);
            i++;
        }
    }

    public LaToile getToile() {
        return toile;
    }

    /**
     * @param file
     * @throws IOException
     */
    public void ouvrirLivre(File file) throws IOException {
        if (!getCahierOnglet().estIlOuverts(file)) {
            creerCahierCourant(false);
            getCahierCourant().chargerFichier(file);
            getCahierCourant().forceStyle();
            Preference.getIntance().setProperty(Preference.P_DIRECTORY, file.getAbsolutePath());
            ajouterHistorique(file, true);
        }
    }

    private void ajouterHistorique(File file, boolean recharger) {
        synchronized (listHistorique) {
            while (listHistorique.size() > 19)
                listHistorique.remove(0);
            supprimerHistorique(file);
            listHistorique.add(new OuvrirLivreAction(this, file));
            if (recharger)
                rechargerHistorique();
        }
    }

    private void supprimerHistorique(File file) {
        OuvrirLivreAction ouvrirLivreAction = null;
        for (OuvrirLivreAction element : listHistorique) {
            try {
                if (element.info.getCanonicalPath().equals(file.getCanonicalPath())) {
                    ouvrirLivreAction = element;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ouvrirLivreAction != null) {
            listHistorique.remove(ouvrirLivreAction);
        }
    }

    public String getStructureLivre() {
        return verbes_imperatif.get("+Livre");
    }

    public void renommerLivre(File ancien, File nouveau) throws IOException {
        Cahier cahier = getCahierOnglet().retourneCahier(ancien);
        if (cahier != null) {
            cahier.renommerFichier(nouveau);
        }
    }

    protected void creationSousMenuVerbier() {
        super.creationSousMenuVerbier();
        creerJMenuVerbe(linotte.getGrammaire().getDefinitionsXml());
        creerJMenuEspeces(linotte);
        creerJMenuCouleurs();
    }

    private JMenuItem creerMenu(String commande) {
        JMenuItem jMenu = new JMenu();
        jMenu.setActionCommand(commande);
        jMenu.setText(commande);
        jMenu.setBackground(menuBarColor);
        jMenu.setForeground(textBarColor);
        jMenu.setBorderPainted(menuBarBordure);
        jMenu.setIcon(Ressources.getImageIcon("system-shutdown.png"));
        return jMenu;
    }


}