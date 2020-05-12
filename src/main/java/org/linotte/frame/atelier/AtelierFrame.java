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
import org.linotte.frame.cahier.Onglets;
import org.linotte.frame.cahier.sommaire.JPanelSommaire;
import org.linotte.frame.coloration.StyleManageur;
import org.linotte.frame.gui.JPanelBackGround;
import org.linotte.frame.gui.JTextPaneText;
import org.linotte.frame.moteur.Debogueur;
import org.linotte.frame.moteur.FrameProcess;
import org.linotte.frame.projet.ExplorateurProjet;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.outils.JTextPaneToPdf;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.Version;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.DefaultStyledDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

public class AtelierFrame extends JFrame {

    public static final String ATELIER_DE_PROGRAMMATION_LINOTTE = "Atelier de programmation Linotte";
    protected static final int TAILLE_H = 800, TAILLE_V = 650;
    public static ExplorateurProjet explorateur;
    protected static Font font;
    public int taille_split_projet = 320;
    public JSplitPane splitPaneProjet = null;
    public JButton jButtonRanger = null;
    public UndoAction undoAction = new UndoAction(this);
    public RedoAction redoAction = new RedoAction(this);
    public Clipboard clipbd = getToolkit().getSystemClipboard();
    // A mettre en final :
    protected Color textBarColor = null;// Color.WHITE;
    protected boolean menuBarBordure = false;// false;
    // Théme :
    protected Color menuBarColor = null;// Color.BLACK;
    protected JButton jButtonLire = null;
    protected JButton jButtonTester = null;
    protected JButton jButtonPasAPas = null;
    protected JButton jButtonContinuer = null;
    protected JMenu jMenuVerbier = null;
    protected JMenu jMenuCondition = null;
    protected JMenu jMenuMathematiques = null;
    protected JMenu jMenuBoucle = null;
    protected JMenu jMenuEspeces = null;
    protected JMenu jMenuHistorisque = null;
    protected JMenuItem jMenuItemDisque = null;
    protected JMenuItem jMenuItemExporter = null;
    protected JMenuItem jMenuItemExporterHTML = null;
    protected JMenuItem jMenuItemExporterRTF = null;
    protected JMenuItem jMenuItemBonifieur = null;
    protected SliderMenuItem jMenuItemDebogueur = null;
    protected JMenuItem jMenuItemSaveWorkSpace = null;
    protected JMenuItem jMenuItemFormater = null;
    protected JMenuItem jMenuItemLaToile = null;
    protected TeleType teleType;
    protected JFileChooser fileChooser_ouvrir = null;
    protected JFileChooser fileChooser_sauvegarder = null;
    protected JFileChooser fileChooser_exporter = null;
    protected JFileChooser fileChooser_exporterPNG = null;
    protected JFileChooser fileChooser_exporterHTML = null;
    protected JFileChooser fileChooser_exporterRTF = null;
    private int taille_split_tableau = 400;
    private int taille_split_sommaire = 180;
    private int buttonTextHorizontale = SwingConstants.BOTTOM; // AbstractButton.TOP;
    private int buttonTextVerticale = SwingConstants.CENTER;// AbstractButton.RIGHT;
    private Inspecteur inspecteur;
    private BoiteRecherche findAndReplace;
    private JPanel jPanelAtelier = null;
    private JSplitPane jSplitPaneAtelier = null;
    private JSplitPane splitPaneSommaire = null;
    private JButton jButtonStop = null;
    private JButton jButtonTimbre = null;
    private JButton jButtonLibrairie = null;
    private JMenu jMenuEdition = null;
    private JMenu jMenuOutils = null;
    private JMenu jMenuBibliotheque = null;
    // Menu :
    private JMenuBar jMenuBar = null;
    private JMenuItem jMenuItemNouveau = null;
    private JMenuItem jMenuItemNouveauLivreGraphique = null;
    private JMenuItem jMenuItemRangerSous = null;
    private JMenuItem jMenuItemExporterPNG = null;
    private JMenuItem jMenuItemRechercher = null;
    private JMenuItem jMenuItemManageurStyle = null;
    private JTextPaneText jEditorPaneTableau = null;
    private Cahier cahierCourant = null;
    private CopyL copier = new CopyL(this);
    private PasteL coller = new PasteL(this);
    private CutL couper = new CutL(this);
    private Onglets cahierOnglet;


    public AtelierFrame() {
        super();
    }

    protected static JButton createSimpleButton(String text) {
        JButton button = new JButton(text);
        button.setOpaque(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        return button;
    }

    protected void constructionAtelier() {
        this.setContentPane(getJPanelAtelier());
        this.setJMenuBar(getxJMenuBar());
        this.setPreferredSize(new Dimension(TAILLE_H, TAILLE_V));
        this.setSize(new Dimension(TAILLE_H, TAILLE_V));
        //this.setSize(TAILLE_H, TAILLE_V);
        this.setName("L'Atelier Linotte " + Version.getVersion());
        getCahierCourant().effacerCahier();
        inspecteur = new Inspecteur(this);
        inspecteur.pack();
        findAndReplace = new BoiteRecherche(this);
    }

    /**
     * Mettre la partie Frame de la classe Atelier.java ici !!
     */

    public JTextPaneText getjEditorPaneTableau() {
        return jEditorPaneTableau;
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
            jPanelAtelier.setPreferredSize(new java.awt.Dimension(600, 490));
            jPanelAtelier.add(getJSplitPaneProjet());
            jPanelAtelier.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
        }
        return jPanelAtelier;
    }

    private JSplitPane getJSplitPaneProjet() {
        if (splitPaneProjet == null) {
            splitPaneProjet = new JSplitPane();
            Ressources.creationEDT();
            try {
                // try si la librairie swingX n'est pas présente :
                explorateur = new ExplorateurProjet(FileSystemView.getFileSystemView(), this, Ressources.getEDT());
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
                            jSplitPaneAtelier.setDividerLocation(getHeight() - taille_split_tableau);
                        }
                    });
                }

                public void componentShown(ComponentEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            jSplitPaneAtelier.setDividerLocation(getHeight() - taille_split_tableau);
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
            jButtonTimbre.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
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
            jButtonContinuer.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e0) {
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
            jButtonStop.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
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
            jButtonLibrairie.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
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
            jButtonRanger.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        if (getCahierCourant().getFichier() == null) {
                            if (fileChooser_sauvegarder.showSaveDialog(FrameDialog.frame) == JFileChooser.APPROVE_OPTION) {
                                getCahierCourant().sauvegarderFichier(fileChooser_sauvegarder.getSelectedFile());
                            }
                        } else {
                            getCahierCourant().sauvegarderFichier(getCahierCourant().getFichier());
                        }
                    } catch (IOException e2) {
                        JOptionPane.showMessageDialog(AtelierFrame.this,
                                "Impossible de ranger le livre !\nVérifiez la présence de ce chemin et si vous avez les droits d'écriture dans ce répertoire :\n"
                                        + getCahierCourant().getFichier().getParent(),
                                "Rangement impossible", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        return jButtonRanger;
    }

    /**
     * This method initializes jScrollPaneAtelier
     *
     * @return JScrollPane
     */
    public Cahier getCahierCourant() {
        if (cahierCourant == null) {
            cahierCourant = new Cahier();
            cahierCourant.init(undoAction, redoAction, couper, copier, coller, font, this);
        }
        return cahierCourant;
    }

    public void setCahierCourant(Cahier panel) {
        cahierCourant = panel;
        setSommaire(panel.getJSommaire());
    }

    public Cahier creerCahierCourant(boolean type) {

        cahierCourant = new Cahier();
        cahierCourant.init(undoAction, redoAction, couper, copier, coller, font, this);
        setSommaire(cahierCourant.getJSommaire());
        cahierOnglet.ajouterCahier(cahierCourant);
        cahierCourant.setVisualiserTimbre(type);

        return cahierCourant;
    }

    public Cahier softCreerCahierCourant() {
        cahierCourant = new Cahier();
        cahierCourant.init(undoAction, redoAction, couper, copier, coller, font, this);
        cahierOnglet.ajouterCahier(cahierCourant);
        return cahierCourant;
    }

    public Onglets getCahierOnglet() {
        return cahierOnglet;
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
        }
        return jEditorPaneTableau;
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
    protected JMenu getJMenuEdition() {
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

    protected JMenu getJMenuOutils() {
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
        teleType = new TeleType();
        tableau.add(teleType, BorderLayout.SOUTH);
        return tableau;
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

    protected JMenu getJMenuCouleurs(int m) {
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
            jMenuItemDisque.setMnemonic(java.awt.event.KeyEvent.VK_O);
        }
        return jMenuItemDisque;
    }

    // getJMenuItemNouveau
    private JMenuItem getJMenuItemNouveau() {
        if (jMenuItemNouveau == null) {
            jMenuItemNouveau = new JMenuItem();
            jMenuItemNouveau.setText("Nouveau livre");
            jMenuItemNouveau.setMnemonic(java.awt.event.KeyEvent.VK_N);
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
            jMenuItemNouveauLivreGraphique.setMnemonic(java.awt.event.KeyEvent.VK_G);
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
            jMenuItemRangerSous.setMnemonic(java.awt.event.KeyEvent.VK_S);
            jMenuItemRangerSous.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (fileChooser_sauvegarder.showSaveDialog(FrameDialog.frame) == JFileChooser.APPROVE_OPTION) {
                            if (fileChooser_sauvegarder.getSelectedFile() != null)
                                getCahierCourant().sauvegarderFichier(fileChooser_sauvegarder.getSelectedFile());
                        }
                    } catch (IOException e2) {
                        JOptionPane.showMessageDialog(AtelierFrame.this,
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
            jMenuItemExporter.setMnemonic(java.awt.event.KeyEvent.VK_T);
            jMenuItemExporter.setVisible(JTextPaneToPdf.can());
        }
        return jMenuItemExporter;
    }

    public JMenuItem getJMenuItemExporterHTML() {
        if (jMenuItemExporterHTML == null) {
            jMenuItemExporterHTML = new JMenuItem();
            jMenuItemExporterHTML.setText("Exporter au format HTML");
            jMenuItemExporterHTML.setMnemonic(java.awt.event.KeyEvent.VK_H);
        }
        return jMenuItemExporterHTML;
    }

    public JMenuItem getJMenuItemExporterRTF() {
        if (jMenuItemExporterRTF == null) {
            jMenuItemExporterRTF = new JMenuItem();
            jMenuItemExporterRTF.setText("Exporter au format RTF ( Rich Text Format )");
            jMenuItemExporterRTF.setMnemonic(java.awt.event.KeyEvent.VK_R);
        }
        return jMenuItemExporterRTF;
    }

    protected JMenuItem getJMenuRechercher() {
        if (jMenuItemRechercher == null) {
            jMenuItemRechercher = new JMenuItem();
            jMenuItemRechercher.setText("Rechercher...");
            jMenuItemRechercher.setMnemonic(java.awt.event.KeyEvent.VK_R);
            jMenuItemRechercher.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
            jMenuItemRechercher.addActionListener(e -> findAndReplace.setVisible(true));
        }
        return jMenuItemRechercher;
    }

    private JMenuItem getJMenuSaveWorkSpace() {
        if (jMenuItemSaveWorkSpace == null) {
            jMenuItemSaveWorkSpace = new JCheckBoxMenuItem();
            jMenuItemSaveWorkSpace.setText("Mémoriser les livres");
            jMenuItemSaveWorkSpace.setSelected(false);
            jMenuItemSaveWorkSpace.setMnemonic(java.awt.event.KeyEvent.VK_M);
            jMenuItemSaveWorkSpace.addActionListener(e -> Preference.getIntance().setBoolean(Preference.P_MODE_SAVE_WORKSPACE, jMenuItemSaveWorkSpace.isSelected()));
        }
        return jMenuItemSaveWorkSpace;
    }

    private JMenuItem getJMenuItemBonifieur() {
        if (jMenuItemBonifieur == null) {
            jMenuItemBonifieur = new JCheckBoxMenuItem();
            jMenuItemBonifieur.setText("Bonifier le cahier");
            jMenuItemBonifieur.setSelected(true);
            jMenuItemBonifieur.setMnemonic(java.awt.event.KeyEvent.VK_B);
            jMenuItemBonifieur.addActionListener(e -> {
                Preference.getIntance().setBoolean(Preference.P_MODE_BONIFIEUR, jMenuItemBonifieur.isSelected());
                getCahierCourant().repaint();
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
            jMenuItemFormater.setMnemonic(java.awt.event.KeyEvent.VK_I);
        }
        return jMenuItemFormater;
    }

    private JMenuItem getJMenuLaToile() {
        if (jMenuItemLaToile == null) {
            jMenuItemLaToile = new JMenuItem();
            jMenuItemLaToile.setText("Afficher la toile");
            jMenuItemLaToile.setMnemonic(java.awt.event.KeyEvent.VK_T);
        }
        return jMenuItemLaToile;
    }

    private JMenuItem getJMenuManageurStyle() {
        if (jMenuItemManageurStyle == null) {
            jMenuItemManageurStyle = new JMenuItem();
            jMenuItemManageurStyle.setText("Polices et couleurs...");
            jMenuItemManageurStyle.setMnemonic(java.awt.event.KeyEvent.VK_Y);
            jMenuItemManageurStyle.addActionListener(e -> StyleManageur.ouvrir(AtelierFrame.this));
        }
        return jMenuItemManageurStyle;
    }

    private JMenu createJMenuNonOpaque(String titre, final Icon iconeClair, final Icon icone) {
        JMenu jMenu = new JMenuAtelier(titre, iconeClair, icone);
        if ("Windows XP".equals(System.getProperty("os.name"))) {
            jMenu.setBackground(new Color(0, 0, 0, 0)); // XXX Windows XP lnf?
        }
        return jMenu;
    }

    protected void creationSousMenuVerbier() {
        getJMenuCondition();
        getJMenuMathematiques();
        getJMenuBoucle();
        getJMenuEspeces();
        jMenuVerbier.addSeparator();
        jMenuVerbier.add(getJMenuEspeces());
        jMenuVerbier.add(getJMenuCondition());
        jMenuVerbier.add(getJMenuBoucle());
        jMenuVerbier.add(getJMenuMathematiques());
        jMenuVerbier.addSeparator();
    }

    public JMenuItem getJMenuItemExporterPNG() {
        if (jMenuItemExporterPNG == null) {
            jMenuItemExporterPNG = new JMenuItem();
            jMenuItemExporterPNG.setText("Exporter au format PNG...");
            jMenuItemExporterPNG.setMnemonic(java.awt.event.KeyEvent.VK_G);
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
                        JOptionPane.showMessageDialog(AtelierFrame.this, "Impossible d'exporter le livre !", "Rangement impossible", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
        return jMenuItemExporterPNG;
    }

    protected void constructionMenu() {
        getJMenuEdition().add(undoAction);
        getJMenuEdition().add(redoAction);
        getJMenuEdition().addSeparator();
        // http://penserenjava.free.fr/pens_2.4/indexMaind0ce.html?chp=14&pge=10
        JMenuItem cut = new JMenuItem("Couper"), copy = new JMenuItem("Copier"), paste = new JMenuItem("Coller");

        couper = new CutL(this);
        copier = new CopyL(this);
        coller = new PasteL(this);

        cut.addActionListener(couper);
        copy.addActionListener(copier);
        paste.addActionListener(coller);

        copy.setMnemonic(KeyEvent.VK_P);
        paste.setMnemonic(KeyEvent.VK_O);
        cut.setMnemonic(KeyEvent.VK_C);

        getJMenuEdition().add(copy);
        getJMenuEdition().add(paste);
        getJMenuEdition().add(cut);
        getJMenuEdition().addSeparator();
        getJMenuEdition().add(getJMenuRechercher());

        getJMenuOutils().add(getJMenuFormater());
        getJMenuOutils().add(getJMenuLaToile());

        getJMenuOutils().addSeparator();
        JMenu options = new JMenu("Options");
        getJMenuOutils().add(options);
        options.add(getJMenuSaveWorkSpace());

        options.add(getJMenuItemBonifieur());
        options.add(getJMenuManageurStyle());

        options.addSeparator();
        options.add(getJMenuDelaisDebogueur());

    }

    public Inspecteur getInspecteur() {
        return inspecteur;
    }


}