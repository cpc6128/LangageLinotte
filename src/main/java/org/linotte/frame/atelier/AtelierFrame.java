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

import org.jdesktop.swingx.JXTextField;
import org.linotte.frame.cahier.Cahier;
import org.linotte.frame.cahier.Cahier.EtatCachier;
import org.linotte.frame.cahier.Onglets;
import org.linotte.frame.cahier.sommaire.JPanelSommaire;
import org.linotte.frame.coloration.StyleBuilder;
import org.linotte.frame.coloration.StyleLinotte;
import org.linotte.frame.coloration.StyleManageur;
import org.linotte.frame.gui.JPanelBackGround;
import org.linotte.frame.gui.JTextPaneText;
import org.linotte.frame.latoile.Couleur;
import org.linotte.frame.latoile.JPanelLaToile;
import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.moteur.ConsoleProcess;
import org.linotte.frame.moteur.Debogueur;
import org.linotte.frame.moteur.Formater;
import org.linotte.frame.moteur.FrameProcess;
import org.linotte.frame.projet.ExplorateurProjet;
import org.linotte.greffons.GreffonsChargeur;
import org.linotte.greffons.api.AKMethod;
import org.linotte.greffons.java.JavaMethod;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.outils.JTextPaneToPdf;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.alize.parseur.XMLIterator;
import org.linotte.moteur.xml.alize.parseur.a.NExpression;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * Affichage de l'interface principale de L'atelier Linotte. Contient également
 * le "main" pour exécuter l'Atelier
 */
@SuppressWarnings("serial")
public class AtelierFrame extends JFrame implements WindowListener {

    private static final int TAILLE_H = 800, TAILLE_V = 650;

    public static ExplorateurProjet explorateur;
    private static LaToile toile;

    /**
     * Une icone de lien symbolique
     */
    private final ImageIcon iconeLien = Ressources.getImageIcon("emblem-symbolic-link.png");
    private final ImageIcon iconeEditeurTexte = Ressources.getImageIcon("accessories-text-editor.png");
    public JSplitPane splitPaneProjet = null;
    public JButton jButtonRanger = null;
    public UndoAction undoAction = new UndoAction(this);
    public RedoAction redoAction = new RedoAction(this);
    public int taille_split_projet = 320;
    public Clipboard clipbd = getToolkit().getSystemClipboard();
    public List<OuvrirLivreAction> listHistorique = new ArrayList<OuvrirLivreAction>();
    int position = 0;
    private JPanel jPanelAtelier = null;
    private JSplitPane jSplitPaneAtelier = null;
    private JSplitPane splitPaneSommaire = null;
    private JButton jButtonLire = null;
    private JButton jButtonTester = null;
    private JButton jButtonPasAPas = null;
    private JButton jButtonContinuer = null;
    private JButton jButtonStop = null;
    private JButton jButtonTimbre = null;
    private JButton jButtonLibrairie = null;
    private Document sortieTableau = null;
    private JTextPaneText jEditorPaneTableau = null;
    private JFileChooser fileChooser_ouvrir = null;
    private JFileChooser fileChooser_sauvegarder = null;
    private JFileChooser fileChooser_exporter = null;
    private JFileChooser fileChooser_exporterPNG = null;
    private JFileChooser fileChooser_exporterHTML = null;
    private JFileChooser fileChooser_exporterRTF = null;
    private JMenuBar jMenuBar = null;
    private JMenu jMenuEdition = null;
    private JMenu jMenuOutils = null;
    private JMenu jMenuBibliotheque = null;
    private JMenu jMenuVerbier = null;
    private JMenu jMenuCondition = null;
    private JMenu jMenuMathematiques = null;
    private JMenu jMenuBoucle = null;
    private JMenu jMenuEspeces = null;
    private JMenu jMenuHistorisque = null;
    private JMenuItem jMenuItemDisque = null;
    private JMenuItem jMenuItemNouveau = null;
    private JMenuItem jMenuItemNouveauLivreGraphique = null;
    private JMenuItem jMenuItemRangerSous = null;
    private JMenuItem jMenuItemExporter = null;
    private JMenuItem jMenuItemExporterHTML = null;
    private JMenuItem jMenuItemExporterRTF = null;
    private JMenuItem jMenuItemExporterPNG = null;
    private JMenuItem jMenuItemBonifieur = null;
    private SliderMenuItem jMenuItemDebogueur = null;
    private JMenuItem jMenuItemSaveWorkSpace = null;
    private JMenuItem jMenuItemRechercher = null;
    private JMenuItem jMenuItemFormater = null;
    private JMenuItem jMenuItemLaToile = null;
    private JMenuItem jMenuItemManageurStyle = null;
    // //////////////////////////////////////////////////////////// Maps pour le menu verbes
    private JMenu jMenuLinotte = null;
    private JMenuItem jMenuItemApropos = null;
    private JMenuItem jMenuItemMerci = null;
    private boolean saut2ligne = false;
    // ////////////////////////////////////////////////////////////////// Menus
    //private FrameIHM dialogframeihm = null;
    private Cahier cahierCourant = null;
    private CutL couper = new CutL(this);
    private Map<String, Set<String>> slots_prototype = new HashMap<String, Set<String>>();
    private Map<String, String> verbes_imperatif = new HashMap<String, String>();
    private Map<String, Boolean> verbes_icone = new HashMap<String, Boolean>();
    private Map<String, String> verbes_conditions = new HashMap<String, String>();
    private Map<String, String> verbes_mathematiques = new HashMap<String, String>();
    private Map<String, String> verbes_boucle = new HashMap<String, String>();
    private Map<String, String> verbes_especes = new HashMap<String, String>();

    // Gestion des onglets :
    private Map<String, String> verbes_aide = new HashMap<String, String>();

    private Inspecteur inspecteur;
    private CopyL copier = new CopyL(this);
    private BoiteRecherche findAndReplace;
    private Onglets cahierOnglet;
    private PasteL coller = new PasteL(this);
    private Color textBarColor = null;// Color.WHITE;
    private boolean menuBarBordure = false;// false;
    private int buttonTextHorizontale = SwingConstants.BOTTOM; // AbstractButton.TOP;
    private int buttonTextVerticale = SwingConstants.CENTER;// AbstractButton.RIGHT;
    // Théme :
    private Color menuBarColor = null;// Color.BLACK;
    private int taille_split_tableau = 400;
    private int taille_split_sommaire = 180;

    /**
     * Crée un nouvel atelier Linotte
     *
     * @param nom Le titre de la fenêtre, usuellement "Atelier Linotte"
     */
    public AtelierFrame(String nom) {
        super(nom);
    }

    private static JButton createSimpleButton(String text) {
        JButton button = new JButton(text);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }

    private void constructionAtelier() {
        this.setContentPane(getJPanelAtelier());
        this.setJMenuBar(getxJMenuBar());
        this.setPreferredSize(new Dimension(TAILLE_H, TAILLE_V));
        this.setSize(new Dimension(TAILLE_H, TAILLE_V));
        //this.setSize(TAILLE_H, TAILLE_V);
        this.setName("L'Atelier Linotte " + Version.getVersion());
        getCahierCourant().effacerCahier();
        inspecteur = new Inspecteur(this);
        inspecteur.pack();
        findAndReplace = new BoiteRecherche(AtelierFrame.this);
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

    /**
     * This method initializes jPanelAtelier
     *
     * @return JPanel
     */
    private JPanel getJPanelAtelier() {
        if (jPanelAtelier == null) {
            jPanelAtelier = new JPanel();
            GridBagLayout gb = new GridBagLayout();

            GridBagConstraints gbc = new GridBagConstraints();
            jPanelAtelier.setLayout(gb);
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1;
            gbc.weighty = 1;
            gb.setConstraints(getJSplitPaneProjet(), gbc);
            jPanelAtelier.setPreferredSize(new Dimension(600, 490));
            jPanelAtelier.add(getJSplitPaneProjet());
            jPanelAtelier.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        }
        return jPanelAtelier;
    }

    private JSplitPane getJSplitPaneProjet() {
        if (splitPaneProjet == null) {
            splitPaneProjet = new JSplitPane();
            if (Ressources.creationEDT()) {
                // JOptionPane.showMessageDialog(this,
                // "Votre espace de travail est créé dans le répertoire :\n" +
                // Ressources.getEDT());
            }

            try {
                // try si la librairie swingX n'est pas présente :
                explorateur = new ExplorateurProjet(FileSystemView.getFileSystemView(), Ressources.getGreffons(), this, Ressources.getEDT());
                splitPaneProjet.setLeftComponent(explorateur);
                splitPaneProjet.setRightComponent(getJSplitPaneAtelier());
                splitPaneProjet.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
                splitPaneProjet.setDividerSize(8);
                splitPaneProjet.setOneTouchExpandable(true);
                splitPaneProjet.addComponentListener(new ComponentListener() {
                    public void componentHidden(ComponentEvent e) {
                    }

                    public void componentMoved(ComponentEvent e) {
                    }

                    public void componentResized(ComponentEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                splitPaneProjet.setDividerLocation(taille_split_projet);
                            }
                        });
                    }

                    public void componentShown(ComponentEvent e) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                splitPaneProjet.setDividerLocation(taille_split_projet);
                            }
                        });
                    }
                });
            } catch (Throwable e) {
                e.printStackTrace();
                splitPaneProjet = getJSplitPaneAtelier();
            }
        }
        return splitPaneProjet;
    }

    private JSplitPane getJSplitPaneAtelier() {
        if (jSplitPaneAtelier == null) {
            jSplitPaneAtelier = new JSplitPane();
            jSplitPaneAtelier.setTopComponent(getJSplitPaneSommaire());

            jSplitPaneAtelier.setBottomComponent(getJScrollPaneTableau());
            jSplitPaneAtelier.setOrientation(JSplitPane.VERTICAL_SPLIT);
            jSplitPaneAtelier.setDividerSize(8);
            jSplitPaneAtelier.setOneTouchExpandable(true);
            jSplitPaneAtelier.addComponentListener(new ComponentListener() {
                public void componentHidden(ComponentEvent e) {
                }

                public void componentMoved(ComponentEvent e) {
                }

                public void componentResized(ComponentEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            jSplitPaneAtelier.setDividerLocation(atelier.getHeight() - taille_split_tableau);
                        }
                    });
                }

                public void componentShown(ComponentEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            jSplitPaneAtelier.setDividerLocation(atelier.getHeight() - taille_split_tableau);
                        }
                    });
                }
            });

        }
        return jSplitPaneAtelier;
    }

    private JSplitPane getJSplitPaneSommaire() {

        if (splitPaneSommaire == null) {
            splitPaneSommaire = new JSplitPane();
            cahierOnglet = new Onglets();
            splitPaneSommaire.setLeftComponent(cahierOnglet);
            setSommaire(getCahierCourant().getJSommaire());
            splitPaneSommaire.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            splitPaneSommaire.setDividerSize(8);
            splitPaneSommaire.setOneTouchExpandable(true);
            splitPaneSommaire.addComponentListener(new ComponentListener() {
                public void componentHidden(ComponentEvent e) {
                }

                public void componentMoved(ComponentEvent e) {
                }

                public void componentResized(ComponentEvent e) {
                    refreshSommaireLocation();
                }

                public void componentShown(ComponentEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            splitPaneSommaire.setDividerLocation(splitPaneSommaire.getWidth() - taille_split_sommaire);
                        }
                    });
                }
            });
        }
        return splitPaneSommaire;
    }

    public void refreshSommaireLocation() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                splitPaneSommaire.setDividerLocation(splitPaneSommaire.getWidth() - taille_split_sommaire);
            }
        });
    }

    public void setSommaire(final JPanelSommaire sommaire) {
        final int location = splitPaneSommaire.getDividerLocation();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                splitPaneSommaire.setRightComponent(sommaire);
                splitPaneSommaire.setDividerLocation(location);
                if (cahierCourant.getEditorPanelCahier() != null)
                    cahierCourant.getEditorPanelCahier().requestFocusInWindow();
            }
        });
    }

    /**
     * This method initializes jButtonLire
     *
     * @return JButton
     */
    public JButton getJButtonLire() {
        if (jButtonLire == null) {
            jButtonLire = createSimpleButton("Lire !");
            jButtonLire.setVerticalTextPosition(buttonTextHorizontale);
            jButtonLire.setHorizontalTextPosition(buttonTextVerticale);
            if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
                jButtonLire.setToolTipText("<html><b>Bouton Lire</b><br>" + "<i>Description</i> : Lis le livre présent dans le cahier<br>"
                        + "<i>Action</i> : Cliquer sur le bouton [Lire] ou appuyer sur les boutons Alt+L<br>" + "</html>");
            jButtonLire.setFocusable(false);
            jButtonLire.setIcon(Ressources.getImageTheme("PLAY", 32));
            jButtonLire.setName("boutonLire");
            jButtonLire.setMnemonic(KeyEvent.VK_L);
            jButtonLire.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e0) {
                    getCahierCourant().processStyle.setStopLecture();
                    if (cahierOnglet.nbOnglet() > 0)
                        FrameProcess.go(new StringBuilder(getCahierCourant().retourneTexteCahier() + "\n"), linotte, atelier, false, -1);
                }
            });
        }
        return jButtonLire;
    }

    /**
     * This method initializes jButtonLire
     *
     * @return JButton
     */
    public JButton getJButtonTimbre() {
        if (jButtonTimbre == null) {
            jButtonTimbre = createSimpleButton("Voir code");
            jButtonTimbre.setVerticalTextPosition(buttonTextHorizontale);
            jButtonTimbre.setHorizontalTextPosition(buttonTextVerticale);
            if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
                jButtonTimbre.setToolTipText("<html><b>Bouton Blocs</b><br>" + "<i>Description</i> : Affiche le code du programme<br>"
                        + "<i>Action</i> : appuyer sur les boutons Alt+D<br>" + "</html>");
            jButtonTimbre.setFocusable(false);
            jButtonTimbre.setVisible(false);
            jButtonTimbre.setIcon(Ressources.getImageTheme("CODE", 32));
            jButtonTimbre.setName("boutonTimbre");
            jButtonTimbre.setMnemonic(KeyEvent.VK_C);
            jButtonTimbre.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    final Cahier cahier = getCahierCourant();
                    cahier.setVisualiserTimbreVoirCode(!cahier.isVisualiserTimbreVoirCode());
                    cahier.setVisualiserTimbre(true); // On force la changement
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            getCahierCourant().repaint();
                            refreshBoutonTimbre(cahier);
                        }
                    });
                }
            });
        }
        return jButtonTimbre;
    }

    public void refreshBoutonTimbre(final Cahier cahier) {
        if (jButtonTimbre != null) {
            jButtonTimbre.setBorderPainted(cahier.isVisualiserTimbreVoirCode());
            jButtonTimbre.setContentAreaFilled(cahier.isVisualiserTimbreVoirCode());
        }
    }

    /**
     * This method initializes jButtonLire
     *
     * @return JButton
     */
    public JButton getJButtonTester() {
        if (jButtonTester == null) {
            jButtonTester = new JButton();
            jButtonTester = createSimpleButton("Tester !");
            jButtonTester.setVisible(false);
            jButtonTester.setOpaque(true);
            jButtonTester.setBackground(Color.GREEN);
            jButtonTester.setVerticalTextPosition(buttonTextHorizontale);
            jButtonTester.setHorizontalTextPosition(buttonTextVerticale);
            if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
                jButtonTester.setToolTipText("<html><b>Bouton Tester</b><br>" + "<i>Description</i> : Teste le livre présent dans le cahier<br>"
                        + "<i>Action</i> : Cliquer sur le bouton [Tester] ou appuyer sur les boutons Alt+T<br>" + "</html>");
            jButtonTester.setFocusable(false);
            jButtonTester.setIcon(Ressources.getImageIcon("bandeau/start-here.png"));
            jButtonTester.setPressedIcon(Ressources.getImageIconePlusClaire("bandeau/start-here.png"));
            jButtonTester.setRolloverIcon(Ressources.getImageIconePlusClaire("bandeau/start-here.png"));
            jButtonTester.setName("boutonTester");
            jButtonTester.setMnemonic(KeyEvent.VK_T);
            jButtonTester.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e0) {
                    getCahierCourant().processStyle.setStopLecture();
                    if (cahierOnglet.nbOnglet() > 0)
                        FrameProcess.go(new StringBuilder(getCahierCourant().retourneTexteCahier() + "\n"), linotte, atelier, true, -1);
                }
            });
        }
        return jButtonTester;
    }

    public JButton getJButtonPause() {
        if (jButtonPasAPas == null) {
            jButtonPasAPas = createSimpleButton(Debogueur.LIRE_AU_RALENTI);
            jButtonPasAPas.setVerticalTextPosition(buttonTextHorizontale);
            jButtonPasAPas.setHorizontalTextPosition(buttonTextVerticale);
            if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
                jButtonPasAPas.setToolTipText(Debogueur.TOOLTIP_BOUTON_RALENTI);
            jButtonPasAPas.setFocusable(false);
            jButtonPasAPas.setVisible(true);
            jButtonPasAPas.setIcon(Ressources.getImageTheme("DEBUG", 32));
            //jButtonPasAPas.setIcon(Ressources.getImageIcon("bandeau/start-here.png"));
            //jButtonPasAPas.setPressedIcon(Ressources.getImageIconePlusClaire("bandeau/start-here.png"));
            //jButtonPasAPas.setRolloverIcon(Ressources.getImageIconePlusClaire("bandeau/start-here.png"));
            jButtonPasAPas.setName("boutonPause");
            jButtonPasAPas.setMnemonic(KeyEvent.VK_A);
            jButtonPasAPas.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e0) {
                    if (jButtonLire.isEnabled()) { // très moche
                        getCahierCourant().processStyle.setStopLecture();
                        if (cahierOnglet.nbOnglet() > 0)
                            FrameProcess.go(new StringBuilder(getCahierCourant().retourneTexteCahier() + "\n"), linotte, atelier, false,
                                    Preference.getIntance().getInt(Preference.P_PAS_A_PAS));
                    } else {
                        jButtonContinuer.setVisible(false);
                        jButtonPasAPas.setVisible(false);
                        FrameProcess.continuerDebogage();
                    }
                }
            });
        }
        return jButtonPasAPas;
    }

    public JButton getJButtonContinuer() {
        if (jButtonContinuer == null) {
            jButtonContinuer = createSimpleButton("Continuer");
            jButtonContinuer.setVerticalTextPosition(buttonTextHorizontale);
            jButtonContinuer.setHorizontalTextPosition(buttonTextVerticale);
            jButtonContinuer.setToolTipText("Continuer (Alt+C)");
            jButtonContinuer.setFocusable(false);
            jButtonContinuer.setVisible(false);
            jButtonContinuer.setIcon(Ressources.getScaledImage(Ressources.getImageIcon("go-jump.png"), 32, 32));
            jButtonContinuer.setPressedIcon(Ressources.getScaledImage(Ressources.getImageIconePlusClaire("go-jump.png"), 32, 32));
            jButtonContinuer.setRolloverIcon(Ressources.getScaledImage(Ressources.getImageIconePlusClaire("go-jump.png"), 32, 32));
            jButtonContinuer.setName("boutonPause");
            jButtonContinuer.setMnemonic(KeyEvent.VK_C);
            jButtonContinuer.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e0) {
                    jButtonContinuer.setVisible(false);
                    jButtonPasAPas.setVisible(false);
                    jButtonLire.setVisible(true);
                    FrameProcess.stopDebogage();
                }
            });
        }
        return jButtonContinuer;
    }

    /**
     * This method initializes jButtonStop
     *
     * @return JButton
     */
    public JButton getJButtonStop() {
        if (jButtonStop == null) {
            jButtonStop = createSimpleButton("Stop !");
            jButtonStop.setVerticalTextPosition(buttonTextHorizontale);
            jButtonStop.setHorizontalTextPosition(buttonTextVerticale);
            jButtonStop.setEnabled(false);
            jButtonStop.setFocusable(false);
            jButtonStop.setVisible(true);
            jButtonStop.setIcon(Ressources.getImageTheme("STOP", 32));
            jButtonStop.setMnemonic(KeyEvent.VK_S);
            if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
                jButtonStop.setToolTipText("<html><b>Bouton Stop</b><br>" + "<i>Description</i> : Stop le livre en cours de lecture<br>"
                        + "<i>Action</i> : Cliquer sur le bouton [Stop] ou appuyer sur les boutons Alt+S<br>" + "</html>");
            jButtonStop.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        FrameProcess.stopProcess();
                    } catch (StopException e1) {
                    }
                }
            });
        }
        return jButtonStop;
    }

    protected JButton getJButtonLibrairie() {
        if (jButtonLibrairie == null) {
            jButtonLibrairie = createSimpleButton("Inspecteur...");
            jButtonLibrairie.setEnabled(true);
            jButtonLibrairie.setVerticalTextPosition(buttonTextHorizontale);
            jButtonLibrairie.setHorizontalTextPosition(buttonTextVerticale);
            jButtonLibrairie.setVerticalTextPosition(buttonTextHorizontale);
            jButtonLibrairie.setHorizontalTextPosition(buttonTextVerticale);
            jButtonLibrairie.setVisible(true);
            jButtonLibrairie.setFocusable(false);
            jButtonLibrairie.setIcon(Ressources.getImageTheme("MEM", 32));
            jButtonLibrairie.setMnemonic(KeyEvent.VK_P);
            jButtonLibrairie.setToolTipText("Inspecteur (Alt+P)");
            if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
                jButtonLibrairie.setToolTipText("<html><b>Bouton Inspecteur</b><br>" + "<i>Description</i> : Affiche les acteurs de la mémoire<br>"
                        + "<i>Action</i> : Cliquer sur le bouton [Inspecteur] ou appuyer sur les boutons Alt+P<br>" + "</html>");
            jButtonLibrairie.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (inspecteur.isVisible())
                        inspecteur.setVisible(false);
                    else
                        inspecteur.setVisible(true);
                    inspecteur.refresh(null);
                }
            });
        }
        return jButtonLibrairie;
    }

    protected JButton getJButtonRanger() {
        if (jButtonRanger == null) {
            jButtonRanger = createSimpleButton("Ranger");
            jButtonRanger.setVerticalTextPosition(buttonTextHorizontale);
            jButtonRanger.setHorizontalTextPosition(buttonTextVerticale);
            jButtonRanger.setEnabled(false);
            jButtonRanger.setFocusable(false);
            jButtonRanger.setVisible(true);
            jButtonRanger.setIcon(Ressources.getImageTheme("SAVE", 32));
            jButtonRanger.setMnemonic(KeyEvent.VK_R);
            if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
                jButtonRanger.setToolTipText("<html><b>Bouton Ranger</b><br>" + "<i>Description</i> : Sauvegarder votre livre sur le disque<br>"
                        + "<i>Action</i> : Cliquer sur le bouton [Ranger] ou appuyer sur les boutons Alt+R<br>" + "</html>");
            jButtonRanger.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (getCahierCourant().getFichier() == null) {
                            if (fileChooser_sauvegarder.showSaveDialog(FrameDialog.frame) == JFileChooser.APPROVE_OPTION) {
                                getCahierCourant().sauvegarderFichier(fileChooser_sauvegarder.getSelectedFile());
                            }
                        } else {
                            getCahierCourant().sauvegarderFichier(getCahierCourant().getFichier());
                        }
                    } catch (IOException e2) {
                        JOptionPane.showMessageDialog(atelier,
                                "Impossible de ranger le livre !\nVérifiez la présence de ce chemin et si vous avez les droits d'écriture dans ce répertoire :\n"
                                        + getCahierCourant().getFichier().getParent(),
                                "Rangement impossible", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        return jButtonRanger;
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
                    this.jEditorPaneTableau.insertComponent((Component) texte);
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

    /**
     * This method initializes jScrollPaneAtelier
     *
     * @return JScrollPane
     */
    public Cahier getCahierCourant() {
        if (cahierCourant == null) {
            cahierCourant = new Cahier();
            cahierCourant.init(undoAction, redoAction, couper, copier, coller, font, this, linotte);
        }
        return cahierCourant;
    }

    public void setCahierCourant(Cahier panel) {
        cahierCourant = panel;
        setSommaire(panel.getJSommaire());
    }

    public Cahier creerCahierCourant(boolean type) {

        cahierCourant = new Cahier();
        cahierCourant.init(undoAction, redoAction, couper, copier, coller, font, this, linotte);
        setSommaire(cahierCourant.getJSommaire());
        cahierOnglet.ajouterCahier(cahierCourant);
        cahierCourant.setVisualiserTimbre(type);

        return cahierCourant;
    }

    public Cahier softCreerCahierCourant() {
        cahierCourant = new Cahier();
        cahierCourant.init(undoAction, redoAction, couper, copier, coller, font, this, linotte);
        cahierOnglet.ajouterCahier(cahierCourant);
        return cahierCourant;
    }

    /**
     * This method initializes jScrollPaneTableau
     *
     * @return JScrollPane
     */
    private JComponent getJScrollPaneTableau() {

        JPanel tableau = new JPanel();
        tableau.setLayout(new BorderLayout());
        JScrollPane jScrollPaneTableau = new JScrollPane();
        JPanelBackGround background_panel = new JPanelBackGround(null, jScrollPaneTableau);
        background_panel.setLayout(new BorderLayout());
        background_panel.add(getJEditorPaneTableau());
        jScrollPaneTableau.getViewport().setView(background_panel);
        jScrollPaneTableau.setFocusable(false);
        jScrollPaneTableau.getVerticalScrollBar().setUnitIncrement(10);
        jScrollPaneTableau.getHorizontalScrollBar().setUnitIncrement(10);
        if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
            jScrollPaneTableau
                    .setToolTipText("<html><b>Tableau</b><br>" + "<i>Description</i> : Affiche le résultat du livre ou les messages d'erreurs<br>" + "</html>");

        tableau.add(jScrollPaneTableau, BorderLayout.CENTER);
        tableau.add(getTeleType(), BorderLayout.SOUTH);
        return tableau;
    }

    /**
     * @return
     */
    private JPanel getTeleType() {
        // Prompteur :
        // pile des commandes
        final Stack<String> commandes = new Stack<String>();
        JPanel console = new JPanel();
        console.setLayout(new BorderLayout());
        final JXTextField entree = new JXTextField("Entrer une action à exécuter");
        final JButton action = new JButton("Essayer");
        action.setMnemonic(KeyEvent.VK_Y);
        if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
            entree.setToolTipText("<html><b>Télétype</b><br>" + "<i>Usage</i> : Une action Linotte<br>" + "<i>Exemple</i> : Affiche carré 3 + 12<br>"
                    + "<i>Action</i> : Touche [entrée] ou bouton [Essayer]<br>" + "</html>");
        if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
            action.setToolTipText("<html><b>Bouton Essayer</b><br>" + "<i>Description</i> : Exécute l'action entrée dans le télétype<br>"
                    + "<i>Action</i> : Cliquer sur le bouton [Apprécier]<br>" + "</html>");
        entree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                action.doClick();
            }
        });
        entree.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    if (!commandes.isEmpty() && position < commandes.size() - 1) {
                        position++;
                        entree.setText(commandes.elementAt(commandes.size() - position - 1));
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (!commandes.isEmpty() && position > 0) {
                        position--;
                        entree.setText(commandes.elementAt(commandes.size() - position - 1));
                    } else {
                        entree.setText("");
                        position = -1;
                    }
                }
            }
        });
        console.add(entree, BorderLayout.CENTER);
        action.setIcon(Ressources.getImageTheme("GO", 24));
        console.add(action, BorderLayout.EAST);
        action.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConsoleProcess.go(new StringBuilder(entree.getText() + "\n"), linotte, atelier);
                if (entree.getText().trim().length() > 0) {
                    commandes.remove(entree.getText());
                    commandes.push(entree.getText());
                }
                entree.setText("");
                entree.grabFocus();
                position = -1;
                /*
                 * ByteArrayOutputStream st = new ByteArrayOutputStream();
                 * ObjectOutputStream oos; try { oos = new
                 * ObjectOutputStream(st);
                 * oos.writeObject(((Stack)commandes.clone())); oos.close(); }
                 * catch (IOException e1) { e1.printStackTrace(); }
                 * System.out.println(st.toString());
                 */
            }
        });
        return console;
    }

    /**
     * Cette méthode initialise jEditorPaneTableau
     *
     * @return JEditorPane
     */
    private JEditorPane getJEditorPaneTableau() {
        if (jEditorPaneTableau == null) {
            DefaultStyledDocument temp = new DefaultStyledDocument();
            jEditorPaneTableau = new JTextPaneText(temp, false);
            jEditorPaneTableau.setEditable(false);
            jEditorPaneTableau.setOpaque(false);
            jEditorPaneTableau.setFont(font);
            sortieTableau = jEditorPaneTableau.getDocument();
        }
        return jEditorPaneTableau;
    }

    private void goFinTableau() {
        jEditorPaneTableau.setCaretPosition(sortieTableau.getLength());
    }

    /**
     * Initialise jMenuBar, la barre de menu.
     *
     * @return JMenuBar
     */
    private JMenuBar getxJMenuBar() {
        if (jMenuBar == null) {
            jMenuBar = new JMenuBar() {
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Color c1 = getBackground();
                    Color c2 = getBackground().darker();
                    ((Graphics2D) g).setPaint(new GradientPaint(0, 0, c1, 500, 500, c2, false));
                    Rectangle r = g.getClipBounds();
                    g.fillRect(r.x, r.y, r.width, r.height);
                }
            };
            jMenuBar.setOpaque(false);
            int hauteurMenu = 80;
            jMenuBar.setMinimumSize(new Dimension(hauteurMenu, hauteurMenu));
            jMenuBar.setPreferredSize(new Dimension(hauteurMenu, hauteurMenu));
            jMenuBar.add(getJButtonLire());
            jMenuBar.add(getJButtonPause());
            jMenuBar.add(getJButtonContinuer());
            jMenuBar.add(getJButtonTester());
            jMenuBar.add(getJButtonStop());
            jMenuBar.add(getJButtonLibrairie());
            jMenuBar.add(creationSeparator());
            jMenuBar.add(getJButtonRanger());
            jMenuBar.add(getJMenuBibliotheque());
            jMenuBar.add(creationSeparator());
            jMenuBar.add(getJMenuEdition());
            jMenuBar.add(getJMenuOutils());
            jMenuBar.add(creationSeparator());
            jMenuBar.add(getJMenuVerbier());
            jMenuBar.add(getJButtonTimbre());
        }
        return jMenuBar;
    }

    private JSeparator creationSeparator() {
        JSeparator js = new JSeparator(JSeparator.VERTICAL);
        js.setMaximumSize(new Dimension(10, 60));
        return js;
    }

    /**
     * This method initializes jMenuEdition
     *
     * @return JMenu
     */
    private JMenu getJMenuEdition() {
        if (jMenuEdition == null) {

            jMenuEdition = createJMenuNonOpaque("    Edition    ",
                    Ressources.getImageTheme("EDIT", 32),
                    Ressources.getImageTheme("EDIT", 32));
            jMenuEdition.setVerticalTextPosition(buttonTextHorizontale);
            jMenuEdition.setHorizontalTextPosition(buttonTextVerticale);
            jMenuEdition.setMnemonic(KeyEvent.VK_E);
        }
        return jMenuEdition;
    }

    private JMenu getJMenuOutils() {
        if (jMenuOutils == null) {
            jMenuOutils = createJMenuNonOpaque("    Outils    ",
                    Ressources.getImageTheme("TOOLS", 32),
                    Ressources.getImageTheme("TOOLS", 32)
            );
            jMenuOutils.setVerticalTextPosition(buttonTextHorizontale);
            jMenuOutils.setHorizontalTextPosition(buttonTextVerticale);
            jMenuOutils.setMnemonic(KeyEvent.VK_I);
        }
        return jMenuOutils;
    }

    /**
     * This method initializes jMenuBibliotheque
     *
     * @return JMenu
     */
    private JMenu getJMenuBibliotheque() {
        if (jMenuBibliotheque == null) {
            jMenuBibliotheque = createJMenuNonOpaque("Bibliothèques",
                    Ressources.getImageTheme("LIB", 32),
                    Ressources.getImageTheme("LIB", 32));
            jMenuBibliotheque.setVerticalTextPosition(buttonTextHorizontale);
            jMenuBibliotheque.setHorizontalTextPosition(buttonTextVerticale);
            jMenuBibliotheque.add(getJMenuItemNouveau());
            jMenuBibliotheque.add(getJMenuItemNouveauLivreGraphique());
            jMenuBibliotheque.addSeparator();
            jMenuBibliotheque.add(getJMenuItemDisque());
            jMenuBibliotheque.add(getJMenuItemRangerSous());
            jMenuBibliotheque.addSeparator();
            jMenuBibliotheque.add(getJMenuItemExporter());
            jMenuBibliotheque.add(getJMenuItemExporterHTML());
            jMenuBibliotheque.add(getJMenuItemExporterRTF());
            jMenuBibliotheque.add(getJMenuItemExporterPNG());

            jMenuBibliotheque.addSeparator();
            // Ajout de l'historique des derniers livres ouverts
            jMenuBibliotheque.add(getJMenuItemHistorique());
            jMenuBibliotheque.setActionCommand("Lire");
            jMenuBibliotheque.setMnemonic(KeyEvent.VK_B);
        }
        return jMenuBibliotheque;
    }

    private JMenu getJMenuVerbier() {
        if (jMenuVerbier == null) {
            jMenuVerbier = createJMenuNonOpaque("  Le verbier  ",
                    Ressources.getImageTheme("HELP", 32),
                    Ressources.getImageTheme("HELP", 32));
            VerticalGridLayout menuGrid = new VerticalGridLayout(30, 0);
            jMenuVerbier.getPopupMenu().setLayout(menuGrid);
            jMenuVerbier.setActionCommand("Le verbier");
            jMenuVerbier.setVerticalTextPosition(buttonTextHorizontale);
            jMenuVerbier.setHorizontalTextPosition(buttonTextVerticale);
            jMenuVerbier.setMnemonic(KeyEvent.VK_V);
        }
        return jMenuVerbier;
    }

    private JMenuItem getJMenuCondition() {
        if (jMenuCondition == null) {
            jMenuCondition = new JMenu();
            jMenuCondition.setActionCommand("Conditions");
            jMenuCondition.setText("Conditions");
            jMenuCondition.setBackground(menuBarColor);
            jMenuCondition.setForeground(textBarColor);
            jMenuCondition.setBorderPainted(menuBarBordure);
            jMenuCondition.setIcon(Ressources.getImageIcon("system-shutdown.png"));
        }
        return jMenuCondition;
    }

    private JMenuItem getJMenuMathematiques() {
        if (jMenuMathematiques == null) {
            jMenuMathematiques = new JMenu();
            jMenuMathematiques.setActionCommand("Mathématiques");
            jMenuMathematiques.setText("Mathématiques");
            jMenuMathematiques.setBackground(menuBarColor);
            jMenuMathematiques.setForeground(textBarColor);
            jMenuMathematiques.setBorderPainted(menuBarBordure);
            jMenuMathematiques.setIcon(Ressources.getImageIcon("system-shutdown.png"));
        }
        return jMenuMathematiques;
    }

    private JMenuItem getJMenuBoucle() {
        if (jMenuBoucle == null) {
            jMenuBoucle = new JMenu();
            jMenuBoucle.setActionCommand("Boucles");
            jMenuBoucle.setText("Boucles");
            jMenuBoucle.setBackground(menuBarColor);
            jMenuBoucle.setForeground(textBarColor);
            jMenuBoucle.setBorderPainted(menuBarBordure);
            jMenuBoucle.setIcon(Ressources.getImageIcon("system-shutdown.png"));
        }
        return jMenuBoucle;
    }

    private JMenuItem getJMenuEspeces() {
        if (jMenuEspeces == null) {
            jMenuEspeces = new JMenu();
            VerticalGridLayout menuGrid = new VerticalGridLayout(30, 0);
            jMenuEspeces.getPopupMenu().setLayout(menuGrid);
            jMenuEspeces.setActionCommand("Espèces");
            jMenuEspeces.setText("Espèces");
            jMenuEspeces.setBackground(menuBarColor);
            jMenuEspeces.setForeground(textBarColor);
            Border border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.black), "Espèces");
            jMenuEspeces.getPopupMenu().setBorder(border);
            jMenuEspeces.setIcon(Ressources.getImageIcon("system-shutdown.png"));
        }
        return jMenuEspeces;
    }

    private JMenu getJMenuCouleurs(int m) {
        JMenu jMenuCouleurs = new JMenu();
        jMenuCouleurs.setActionCommand("Couleurs");
        jMenuCouleurs.setText("Couleurs " + m);
        jMenuCouleurs.setBackground(menuBarColor);
        jMenuCouleurs.setForeground(textBarColor);
        jMenuCouleurs.setBorderPainted(menuBarBordure);
        jMenuCouleurs.setIcon(Ressources.getScaledImage(Ressources.getImageIcon("preferences-desktop-locale.png"), 16, 16));
        return jMenuCouleurs;
    }

    /**
     * This method initializes jMenuItemDisque
     *
     * @return JMenuItem
     */
    private JMenuItem getJMenuItemDisque() {
        if (jMenuItemDisque == null) {
            jMenuItemDisque = new JMenuItem();
            jMenuItemDisque.setText("Ouvrir un livre...");
            jMenuItemDisque.setMnemonic(KeyEvent.VK_O);
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
        }
        return jMenuItemDisque;
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

    // getJMenuItemNouveau
    private JMenuItem getJMenuItemNouveau() {
        if (jMenuItemNouveau == null) {
            jMenuItemNouveau = new JMenuItem();
            jMenuItemNouveau.setText("Nouveau livre");
            jMenuItemNouveau.setMnemonic(KeyEvent.VK_N);
            jMenuItemNouveau.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    creerCahierCourant(false);
                }
            });
        }
        return jMenuItemNouveau;
    }

    // getJMenuItemNouveau
    private JMenuItem getJMenuItemNouveauLivreGraphique() {
        if (jMenuItemNouveauLivreGraphique == null) {
            jMenuItemNouveauLivreGraphique = new JMenuItem();
            jMenuItemNouveauLivreGraphique.setText("Nouveau livre visuel");
            jMenuItemNouveauLivreGraphique.setMnemonic(KeyEvent.VK_G);
            jMenuItemNouveauLivreGraphique.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    creerCahierCourant(true);
                }
            });
        }
        return jMenuItemNouveauLivreGraphique;
    }

    private JMenuItem getJMenuItemRangerSous() {
        if (jMenuItemRangerSous == null) {
            jMenuItemRangerSous = new JMenuItem();
            jMenuItemRangerSous.setText("Ranger sous...");
            jMenuItemRangerSous.setMnemonic(KeyEvent.VK_S);
            jMenuItemRangerSous.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (fileChooser_sauvegarder.showSaveDialog(FrameDialog.frame) == JFileChooser.APPROVE_OPTION) {
                            if (fileChooser_sauvegarder.getSelectedFile() != null)
                                getCahierCourant().sauvegarderFichier(fileChooser_sauvegarder.getSelectedFile());
                        }
                    } catch (IOException e2) {
                        JOptionPane.showMessageDialog(atelier,
                                "Impossible de ranger le livre !\nVérifiez la présence de ce chemin et si vous avez les droits d'écriture dans ce répertoire :\n"
                                        + getCahierCourant().getFichier().getParent(),
                                "Rangement impossible", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        return jMenuItemRangerSous;
    }

    private JMenuItem getJMenuItemHistorique() {
        jMenuHistorisque = new JMenu("Derniers livres ouverts");
        return jMenuHistorisque;
    }

    public JMenuItem getJMenuItemExporter() {
        if (jMenuItemExporter == null) {
            jMenuItemExporter = new JMenuItem();
            jMenuItemExporter.setText("Exporter au format PDF...");
            jMenuItemExporter.setMnemonic(KeyEvent.VK_T);
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
            jMenuItemExporter.setVisible(JTextPaneToPdf.can());
        }
        return jMenuItemExporter;
    }

    public JMenuItem getJMenuItemExporterHTML() {
        if (jMenuItemExporterHTML == null) {
            jMenuItemExporterHTML = new JMenuItem();
            jMenuItemExporterHTML.setText("Exporter au format HTML");
            jMenuItemExporterHTML.setMnemonic(KeyEvent.VK_H);
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
        }
        return jMenuItemExporterHTML;
    }

    public JMenuItem getJMenuItemExporterRTF() {
        if (jMenuItemExporterRTF == null) {
            jMenuItemExporterRTF = new JMenuItem();
            jMenuItemExporterRTF.setText("Exporter au format RTF ( Rich Text Format )");
            jMenuItemExporterRTF.setMnemonic(KeyEvent.VK_R);
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
        }
        return jMenuItemExporterRTF;
    }

    private byte[] extractionCahier(EditorKit editorKit) {
        ByteArrayOutputStream str = new ByteArrayOutputStream();
        try {
            editorKit.write(str, getCahierCourant().getDocumentCahier(), 0, getCahierCourant().getDocumentCahier().getLength());
        } catch (Exception ex) {
        }
        return str.toByteArray();
    }

    public JMenuItem getJMenuItemExporterPNG() {
        if (jMenuItemExporterPNG == null) {
            jMenuItemExporterPNG = new JMenuItem();
            jMenuItemExporterPNG.setText("Exporter au format PNG...");
            jMenuItemExporterPNG.setMnemonic(KeyEvent.VK_G);
            jMenuItemExporterPNG.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (fileChooser_exporterPNG.showSaveDialog(FrameDialog.frame) == JFileChooser.APPROVE_OPTION) {
                            FileOutputStream fos = new FileOutputStream(fileChooser_exporterPNG.getSelectedFile());
                            BufferedImage bi = new BufferedImage(getCahierCourant().getEditorPanelCahier().getWidth(),
                                    getCahierCourant().getEditorPanelCahier().getHeight(), BufferedImage.TYPE_INT_RGB);
                            Graphics2D g2 = bi.createGraphics();
                            g2.setColor(Color.WHITE);
                            g2.fillRect(0, 0, bi.getWidth(), bi.getHeight());
                            getCahierCourant().getEditorPanelCahier().print(g2);
                            g2.dispose();
                            try {
                                ImageIO.write(bi, "png", fos);
                            } catch (IOException ioe) {
                                ioe.printStackTrace(System.err);
                            }
                            fos.close();
                        }
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        JOptionPane.showMessageDialog(atelier, "Impossible d'exporter le livre !", "Rangement impossible", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        return jMenuItemExporterPNG;
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

        itemPresent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
                        fils.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
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

    private JMenuItem getJMenuSaveWorkSpace() {
        if (jMenuItemSaveWorkSpace == null) {
            jMenuItemSaveWorkSpace = new JCheckBoxMenuItem();
            jMenuItemSaveWorkSpace.setText("Mémoriser les livres");
            jMenuItemSaveWorkSpace.setSelected(false);
            jMenuItemSaveWorkSpace.setMnemonic(KeyEvent.VK_M);
            jMenuItemSaveWorkSpace.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Preference.getIntance().setBoolean(Preference.P_MODE_SAVE_WORKSPACE, jMenuItemSaveWorkSpace.isSelected());
                }
            });
        }
        return jMenuItemSaveWorkSpace;
    }

    private JMenuItem getJMenuItemBonifieur() {
        if (jMenuItemBonifieur == null) {
            jMenuItemBonifieur = new JCheckBoxMenuItem();
            jMenuItemBonifieur.setText("Bonifier le cahier");
            jMenuItemBonifieur.setSelected(true);
            jMenuItemBonifieur.setMnemonic(KeyEvent.VK_B);
            jMenuItemBonifieur.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Preference.getIntance().setBoolean(Preference.P_MODE_BONIFIEUR, jMenuItemBonifieur.isSelected());
                    getCahierCourant().repaint();
                }
            });
        }
        return jMenuItemBonifieur;
    }

    private SliderMenuItem getJMenuDelaisDebogueur() {
        if (jMenuItemDebogueur == null) {
            jMenuItemDebogueur = new SliderMenuItem();
            jMenuItemDebogueur.setPaintLabels(true);
        }
        return jMenuItemDebogueur;
    }

    private JMenuItem getJMenuFormater() {
        if (jMenuItemFormater == null) {
            jMenuItemFormater = new JMenuItem();
            jMenuItemFormater.setText("Indenter le livre");
            jMenuItemFormater.setMnemonic(KeyEvent.VK_I);
            jMenuItemFormater.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Formater.action(getCahierCourant(), linotte);
                }
            });
        }
        return jMenuItemFormater;
    }

    private JMenuItem getJMenuRechercher() {
        if (jMenuItemRechercher == null) {
            jMenuItemRechercher = new JMenuItem();
            jMenuItemRechercher.setText("Rechercher...");
            jMenuItemRechercher.setMnemonic(KeyEvent.VK_R);
            jMenuItemRechercher.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
            jMenuItemRechercher.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    findAndReplace.setVisible(true);
                }
            });
        }
        return jMenuItemRechercher;
    }

    private JMenuItem getJMenuLaToile() {
        if (jMenuItemLaToile == null) {
            jMenuItemLaToile = new JMenuItem();
            jMenuItemLaToile.setText("Afficher la toile");
            jMenuItemLaToile.setMnemonic(KeyEvent.VK_T);
            jMenuItemLaToile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ((JPanelLaToile) toile.getPanelLaToile()).getToileParent().setVisible(true);
                }
            });
        }
        return jMenuItemLaToile;
    }

    private JMenuItem getJMenuManageurStyle() {
        if (jMenuItemManageurStyle == null) {
            jMenuItemManageurStyle = new JMenuItem();
            jMenuItemManageurStyle.setText("Polices et couleurs...");
            jMenuItemManageurStyle.setMnemonic(KeyEvent.VK_Y);
            jMenuItemManageurStyle.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    StyleManageur.ouvrir(AtelierFrame.this);
                }
            });
        }
        return jMenuItemManageurStyle;
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
        String selection = jEditorPaneTableau.getSelectedText();
        if (selection == null)
            selection = jEditorPaneTableau.getText();
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
                List<String> fichiers = cahierOnglet.getFichiersOuverts();
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
        if (cahierOnglet.verifierEtat(EtatCachier.MODIFIE)) {
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
            itemCouleur.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    getCahierCourant().ecrireInsertAuCurseurCachier(((JMenuItem) e.getSource()).getText());
                }
            });
            jMenuCouleurs.add(itemCouleur);
            i++;
        }
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

    public LaToile getToile() {
        return toile;
    }

    public Inspecteur getInspecteur() {
        return inspecteur;
    }

    /**
     * @param file
     * @throws IOException
     */
    public void ouvrirLivre(File file) throws IOException {
        if (!cahierOnglet.estIlOuverts(file)) {
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
        Cahier cahier = cahierOnglet.retourneCahier(ancien);
        if (cahier != null) {
            cahier.renommerFichier(nouveau);
        }
    }

    public Onglets getCahierOnglet() {
        return cahierOnglet;
    }

    private JMenu createJMenuNonOpaque(String titre, final Icon iconeClair, final Icon icone) {
        JMenu jMenu = new JMenuAtelier(titre, iconeClair, icone);
        if ("Windows XP".equals(System.getProperty("os.name"))) {
            jMenu.setBackground(new Color(0, 0, 0, 0)); // XXX Windows XP lnf?
        }
        return jMenu;
    }

    private void creationSousMenuVerbier() {
        getJMenuCondition();
        getJMenuMathematiques();
        getJMenuBoucle();
        getJMenuEspeces();
        creerJMenuVerbe(linotte.getGrammaire().getDefinitionsXml());

        creerJMenuEspeces(linotte);
        jMenuVerbier.addSeparator();
        jMenuVerbier.add(getJMenuEspeces());
        jMenuVerbier.add(getJMenuCondition());
        jMenuVerbier.add(getJMenuBoucle());
        jMenuVerbier.add(getJMenuMathematiques());
        jMenuVerbier.addSeparator();
        creerJMenuCouleurs();
    }

}