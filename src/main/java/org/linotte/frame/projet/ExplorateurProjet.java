package org.linotte.frame.projet;

/***********************************************************************
 * Langage Linotte                                                     *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 3 or       *
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

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI;
import org.jdesktop.swingx.plaf.windows.WindowsTaskPaneUI;
import org.linotte.frame.Atelier;
import org.linotte.frame.gui.RequestFocusListener;
import org.linotte.frame.gui.WindowsMetroTaskPaneUI;
import org.linotte.frame.latoile.Java6;
import org.linotte.frame.projet.NavigateurFichier.FileTreeNode;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.analyse.multilangage.Langage;
import org.linotte.web.Run;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("serial")
public class ExplorateurProjet extends JPanel {

    private static final String HTML1 = "<HTML><b>Fonction ";
    private static final String HTML2 = "<HTML><b>Attribut ";
    private static final String HTML3 = "</b> <br>";
    private static final String HTML4 = "Pas de description";
    private static Writer fw = null;
    private JXTaskPane taskTutorial = new JXTaskPane();
    private boolean metroStyle = true;

    // Cache des extensions :
    private Map<Component, JXTaskPane> extensions = new HashMap<Component, JXTaskPane>();
    // Cache des tutoriels
    private Map<Langage, NavigateurFichier> tutoriels = new HashMap<Langage, NavigateurFichier>();

    private JXTaskPaneContainer container;

    public ExplorateurProjet(FileSystemView view, File greffons, Atelier atelier, File edt) {

        changeUIdefaults();

        container = new JXTaskPaneContainer();

        if (metroStyle)
            LookAndFeel.uninstallBorder(container);

        try {
            container.add(getTaskPaneEspaceDeTravail("Espace de travail", view, atelier, edt));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            container.add(getTaskPaneTutoriel(view, atelier));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            container.add(getTaskPanePlus(atelier));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JScrollPane panelScroll = new JScrollPane(container);

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        setLayout(gb);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gb.setConstraints(panelScroll, gbc);

        add(panelScroll);

    }

    /**
     * @param atelier
     * @param fileTreePanel
     * @param visuel
     */
    private static void wizardNouveauLivre(final Atelier atelier, final NavigateurFichier fileTreePanel, boolean visuel) {
        try {

            File repertoire = null;
            TreePath path = fileTreePanel.getTree().getSelectionPath();
            FileTreeNode node = null;
            if (path != null) {
                node = (FileTreeNode) path.getLastPathComponent();
                if (!node.getFile().isDirectory()) {
                    node = (FileTreeNode) node.getParent();
                    if (node != null) {
                        repertoire = node.getFile();
                    }
                } else {
                    repertoire = node.getFile();
                }
            }
            if (repertoire == null) {
                repertoire = fileTreePanel.getRacine();
            }

            JPanel header = new JPanel();
            header.setBackground(Color.WHITE);

            JLabel titre = new JLabel("<html><i><h3>Création d'un nouveau livre " + (visuel ? "visuel" : "") + "</h3><i></html>");
            header.setLayout(new BorderLayout());
            header.add(titre, BorderLayout.WEST);

            JLabel image = new JLabel(Ressources.getScaledImage(Ressources.getImageIcon("linotte.png"), 48, 48));
            //header.setLayout(new BorderLayout());
            header.add(image, BorderLayout.EAST);

            JPanel panel_titre = new JPanel();
            panel_titre.setBorder(new DropShadowBorder());
            panel_titre.setLayout(new BoxLayout(panel_titre, BoxLayout.PAGE_AXIS));
            panel_titre.setAlignmentX(RIGHT_ALIGNMENT);

            JTextField champLivre = new JTextField(40);
            {
                JPanel jPanel = new JPanel();
                jPanel.add(new JLabel("Titre du livre: "));
                jPanel.add(champLivre);
                panel_titre.add(jPanel);
            }
            {
                JPanel jPanel = new JPanel();
                jPanel.add(new JLabel("Chemin : "));
                jPanel.add(new JLabel(repertoire.getCanonicalPath()));
                panel_titre.add(jPanel);
            }
            JXTitledPanel panel_options = new JXTitledPanel("Options :");
            panel_options.setBorder(new DropShadowBorder());
            panel_options.setLayout(new BoxLayout(panel_options, BoxLayout.PAGE_AXIS));
            final JCheckBox box_squelette = new JCheckBox();
            JCheckBox box_boucle = new JCheckBox();

            {
                if (!visuel) {
                    box_squelette.setSelected(true);
                    JPanel jPanel = new JPanel();
                    jPanel.add(new JLabel("Squelette du livre : "));
                    jPanel.add(box_squelette);
                    panel_options.add(jPanel);
                }
            }
            {
                box_boucle.setSelected(false);
                box_boucle.setEnabled(false);
                JPanel jPanel = new JPanel();
                jPanel.setEnabled(false);
                JLabel lboucle = new JLabel("Inclure une boucle : ");
                lboucle.setEnabled(false);
                jPanel.add(lboucle);
                jPanel.add(box_boucle);
                panel_options.add(jPanel);
            }
            Object complexMsg[] = {header, panel_titre, panel_options};

            JOptionPane optionPane = new JOptionPane();
            optionPane.setMessage(complexMsg);
            optionPane.setMessageType(JOptionPane.PLAIN_MESSAGE);
            optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = optionPane.createDialog(null, "Création d'un nouveau livre");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            champLivre.requestFocusInWindow();
            // http://tips4java.wordpress.com/2010/03/14/dialog-focus/
            champLivre.addAncestorListener(new RequestFocusListener());
            dialog.setVisible(true);

            if (optionPane.getValue() != null && ((Integer) optionPane.getValue()).intValue() == JOptionPane.YES_OPTION) {

                if (champLivre.getText() != null && champLivre.getText().trim().length() > 0) {
                    String fichier = champLivre.getText().trim();
                    if (!fichier.toLowerCase().endsWith(".liv")) {
                        fichier += ".liv";
                    }
                    fichier = Ressources.sanitizeFilename(fichier);
                    File acreer = new File(repertoire, fichier);

                    if (acreer.exists()) {
                        JOptionPane.showMessageDialog(atelier, "Ce livre existe déjà !", "Impossible de créer le livre !", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    atelier.creerCahierCourant(visuel);

                    if (!visuel) {
                        String structureComplexe;
                        if (box_squelette.isSelected()) {
                            structureComplexe = atelier.getStructureLivre();
                        } else {
                            structureComplexe = champLivre.getText().trim() + " :\n";
                        }
                        atelier.getCahierCourant().ecrireInsertAuCurseurCachier(structureComplexe);
                    }
                    atelier.getCahierCourant().sauvegarderFichier(acreer);
                    atelier.getCahierCourant().forceStyle();
                    if (node != null) {
                        node.recharger();
                        ((DefaultTreeModel) fileTreePanel.getTree().getModel()).reload(node);
                        fileTreePanel.getTree().expandPath(path);
                    } else {
                        fileTreePanel.rootTreeNode.recharger();
                        ((DefaultTreeModel) fileTreePanel.getTree().getModel()).reload(fileTreePanel.rootTreeNode);
                    }
                } else {
                    JOptionPane.showMessageDialog(atelier, "Le nom du livre est vide", "Impossible de créer le livre !", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(atelier, "Impossible de créer le livre !", "Rangement impossible", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param fileTreePanel
     */
    private static void wizardNouveauRepertoire(final Atelier atelier, final NavigateurFichier fileTreePanel) {
        JTextField champNombre = new JTextField();

        Object complexMsg[] = {"Nom du répertoire : ", champNombre};

        JOptionPane optionPane = new JOptionPane();
        optionPane.setMessage(complexMsg);
        optionPane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        optionPane.setOptionType(JOptionPane.OK_CANCEL_OPTION);
        optionPane.setIcon(Ressources.getImageIcon("document-open.png"));
        JDialog dialog = optionPane.createDialog(null, "Création d'un nouveau répertoire");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setVisible(true);

        if (optionPane.getValue() != null && ((Integer) optionPane.getValue()).intValue() == JOptionPane.YES_OPTION) {

            File repertoire = null;
            TreePath path = fileTreePanel.getTree().getSelectionPath();
            FileTreeNode node = null;
            if (path != null) {
                node = (FileTreeNode) path.getLastPathComponent();
                if (!node.getFile().isDirectory()) {
                    node = (FileTreeNode) node.getParent();
                    if (node != null) {
                        repertoire = node.getFile();
                    }
                } else {
                    repertoire = node.getFile();
                }
            }
            if (repertoire == null) {
                repertoire = fileTreePanel.getRacine();
            }

            if (champNombre.getText() != null && champNombre.getText().trim().length() > 0) {
                String fichier = champNombre.getText().trim();
                fichier = Ressources.sanitizeFilename(fichier);

                File acreer = new File(repertoire, fichier);

                if (acreer.exists()) {
                    JOptionPane.showMessageDialog(atelier, "Ce fichier existe déjà !", "Impossible de créer le répertoire !", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                acreer.mkdirs();
                if (node != null) {
                    node.recharger();
                    ((DefaultTreeModel) fileTreePanel.getTree().getModel()).reload(node);
                    fileTreePanel.getTree().expandPath(path);
                } else {
                    fileTreePanel.rootTreeNode.recharger();
                    ((DefaultTreeModel) fileTreePanel.getTree().getModel()).reload(fileTreePanel.rootTreeNode);
                }
            }

        }
    }

    private JXTaskPane getTaskPaneTutoriel(final FileSystemView view, final Atelier atelier) {
        taskTutorial = new JXTaskPane();
        final File exemples = Ressources.getExemples(Atelier.linotte.getLangage());
        changeTaskPaneUI(taskTutorial);
        taskTutorial.setTitle("Tutoriel");
        taskTutorial.setIcon(Ressources.getScaledImage(Ressources.getImageIcon("projet/start-here.png"), 24, 24));
        NavigateurFichier fileTreePanel = new NavigateurFichier(view, exemples, taskTutorial, atelier, false);
        taskTutorial.add(new JScrollPane(fileTreePanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        tutoriels.put(Atelier.linotte.getLangage(), fileTreePanel);
        taskTutorial.setVisible(exemples.exists());
        return taskTutorial;
    }

    private JXTaskPane getTaskPaneEspaceDeTravail(String title, final FileSystemView view, final Atelier atelier, final File edt) {
        final JXTaskPane task = new JXTaskPane();
        changeTaskPaneUI(task);
        task.setTitle(title);
        task.setIcon(Ressources.getImageIcon("projet/document-open.png"));

        final NavigateurFichier fileTreePanel = new NavigateurFichier(view, edt, task, atelier, true);

        task.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Créer un nouveau livre");
                putValue(Action.SHORT_DESCRIPTION, "Créer un livre");
                putValue(Action.SMALL_ICON, Ressources.getScaledImage(Ressources.getImageIcon("document-new.png"), 16, 16));
            }

            public void actionPerformed(ActionEvent e) {

                wizardNouveauLivre(atelier, fileTreePanel, false);

            }
        });

        task.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Créer un nouveau livre visuel");
                putValue(Action.SHORT_DESCRIPTION, "Créer un livre visuel");
                putValue(Action.SMALL_ICON, Ressources.getScaledImage(Ressources.getImageIcon("timbre/timbre.png"), 16, 16));
            }

            public void actionPerformed(ActionEvent e) {

                wizardNouveauLivre(atelier, fileTreePanel, true);

            }
        });

        task.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Créer un nouveau répertoire");
                putValue(Action.SHORT_DESCRIPTION, "Créer un répertoire");
                putValue(Action.SMALL_ICON, Ressources.getScaledImage(Ressources.getImageIcon("document-open.png"), 16, 16));
            }

            public void actionPerformed(ActionEvent e) {

                wizardNouveauRepertoire(atelier, fileTreePanel);

            }
        });

        task.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Explorer l'espace de travail");
                putValue(Action.SHORT_DESCRIPTION, "Explore l'espace de travail");
                putValue(Action.SMALL_ICON, Ressources.getScaledImage(Ressources.getImageIcon("emblem-symbolic-link.png"), 16, 16));
            }

            public void actionPerformed(ActionEvent e) {
                try {
                    Java6.getDesktop().browse(Ressources.getEDT().toURI());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        task.add(new JScrollPane(fileTreePanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

        return task;
    }

    private void changeTaskPaneUI(JXTaskPane task) {
        if (metroStyle && task.getUI() instanceof WindowsTaskPaneUI) {
            BasicTaskPaneUI ui = new WindowsMetroTaskPaneUI();
            task.setUI(ui);
            task.setSpecial(true);
            ((JComponent) task.getContentPane()).setBorder(BorderFactory.createEmptyBorder(8, 2, 5, 2));
        }
    }


    private JXTaskPane getTaskPanePlus(final Atelier atelier) {
        JXTaskPane task = new JXTaskPane();
        changeTaskPaneUI(task);
        task.setTitle("Plus loin...");
        task.setIcon(Ressources.getImageIcon("projet/internet-news-reader.png"));
        task.setSpecial(true);

        task.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Documentation");
                putValue(Action.SMALL_ICON, Ressources.getScaledImage(Ressources.getImageIcon("application-certificate.png"), 16, 16));
            }

            public void actionPerformed(ActionEvent e) {
                try {
                    final URI forum = new URI("http://langagelinotte.free.fr/wordpress/?page_id=120");
                    Java6.getDesktop().browse(forum);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        task.add(new AbstractAction() {
            {
                putValue(Action.NAME, "CodeVallée");
                putValue(Action.SMALL_ICON, Ressources.getScaledImage(Ressources.getImageIcon("post.png"), 16, 16));
            }

            public void actionPerformed(ActionEvent e) {
                try {
                    final URI forum = new URI("http://codevallee.fr");
                    Java6.getDesktop().browse(forum);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        });

        task.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Serveur Web");
                putValue(Action.SMALL_ICON, Ressources.getScaledImage(Ressources.getImageIcon("post.png"), 16, 16));
            }

            public void actionPerformed(ActionEvent e) {
                try {
                    if (Run.PORT > 0) {
                        final URI forum = new URI("http://localhost:" + Run.PORT + "/notice");
                        Java6.getDesktop().browse(forum);
                    } else {
                        JOptionPane.showMessageDialog(atelier, "Le serveur HTTP n'a pas démarré !");
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        return task;
    }

    private void changeUIdefaults() {
        UIManager.put("TaskPaneContainer.useGradient", Boolean.FALSE);
        Color brighter = new Color(240, 240, 240);
        UIManager.put("TaskPaneContainer.background", brighter);
        UIManager.put("TaskPane.titleBackgroundGradientStart", Color.WHITE);
        UIManager.put("TaskPane.titleBackgroundGradientEnd", Color.GRAY);
        UIManager.put("TaskPane.specialTitleBackground", Color.GRAY); // new
        // Color(63,
        // 72,
        // 204)
        UIManager.put("TaskPane.background", brighter);
    }

}