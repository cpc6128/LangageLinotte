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
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.List;
import java.util.*;

@SuppressWarnings("serial")
public class ExplorateurProjet extends JPanel {

    private static final String HTML1 = "<HTML><b>Fonction ";
    private static final String HTML2 = "<HTML><b>Attribut ";
    private static final String HTML3 = "</b> <br>";
    private static final String HTML4 = "Pas de description";
    private static Writer fw = null;
    public Thread threadLancementAtelier1;
    public Thread threadLancementAtelier2;
    public Thread threadLancementAtelier3;
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

    private static String escapeNonAscii(String input) {
        if (input != null) {
            StringBuilder b = new StringBuilder(input.length());
            Formatter f = new Formatter(b);
            for (char c : input.toCharArray()) {
                if (c < 128) {
                    b.append(c);
                } else {
                    f.format("\\u%04X", (int) c);
                }
            }
            f.close();
            return b.toString();
        } else
            return null;
    }

    private JXTaskPane getTaskPaneTutoriel(final FileSystemView view, final Atelier atelier) {
        taskTutorial = new JXTaskPane();
        final File exemples = Ressources.getExemples(Atelier.linotte.getLangage());
        changeTaskPaneUI(taskTutorial);
        threadLancementAtelier1 = new Thread() {
            public void run() {
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException e) {
                }
                taskTutorial.setTitle("Tutoriel");
                taskTutorial.setIcon(Ressources.getScaledImage(Ressources.getImageIcon("projet/start-here.png"), 24, 24));
                NavigateurFichier fileTreePanel = new NavigateurFichier(view, exemples, taskTutorial, atelier, false);
                taskTutorial.add(new JScrollPane(fileTreePanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
                tutoriels.put(Atelier.linotte.getLangage(), fileTreePanel);
            }
        };
        threadLancementAtelier1.start();
        taskTutorial.setVisible(exemples.exists());
        return taskTutorial;
    }

    private JXTaskPane getTaskPaneEspaceDeTravail(String title, final FileSystemView view, final Atelier atelier, final File edt) {
        final JXTaskPane task = new JXTaskPane();
        changeTaskPaneUI(task);
        task.setTitle(title);
        task.setIcon(Ressources.getImageIcon("projet/document-open.png"));

        threadLancementAtelier2 = new Thread() {
            public void run() {

                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                }

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
            }
        };
        threadLancementAtelier2.start();

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

    // @SuppressWarnings("unused")
    // private void messageFonctionNonImplemente(final Atelier atelier) {
    // try {
    // JOptionPane.showMessageDialog(atelier, "Fonctionnalité non terminée !",
    // "Action impossible", JOptionPane.ERROR_MESSAGE);
    // final URI forum = new
    // URI("http://langagelinotte.free.fr/wordpress/?p=255");
    // Java6.getDesktop().browse(forum);
    // } catch (Exception e1) {
    // e1.printStackTrace();
    // }
    // }
    //
    /*
     * Classes internes
     */

    private JXTaskPane getTaskPanePlus(final Atelier atelier) {
        JXTaskPane task = new JXTaskPane();
        changeTaskPaneUI(task);
        task.setTitle("Plus loin...");
        task.setIcon(Ressources.getImageIcon("projet/internet-news-reader.png"));
        task.setSpecial(true);

        task.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Actualité");
                putValue(Action.SMALL_ICON, Ressources.getScaledImage(Ressources.getImageIcon("audio-input-microphone.png"), 16, 16));
            }

            public void actionPerformed(ActionEvent e) {
                try {
                    final URI forum = new URI("http://langagelinotte.free.fr/wordpress/");
                    Java6.getDesktop().browse(forum);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });

        task.add(new AbstractAction() {
            {
                putValue(Action.NAME, "Documentations en ligne");
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
                putValue(Action.NAME, "Forum");
                putValue(Action.SMALL_ICON, Ressources.getImageIcon("system-users.png"));
            }

            public void actionPerformed(ActionEvent e) {
                try {
                    final URI forum = new URI("http://programmons.forumofficiel.fr");
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
                putValue(Action.NAME, "Serveur HTTP");
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

    /**
     * Ajoute un clipnotte dans la zone projet de l'Atelier
     *
     * @param jComponent clipnotte à ajouter
     * @param titre
     */
    public void ajouteExtension(final Component jComponent, final String titre) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    JXTaskPane task = new JXTaskPane(titre);
                    changeTaskPaneUI(task);
                    task.add(jComponent);
                    container.add(task);
                    // On stocke dans le cache :
                    extensions.put(jComponent, task);
                    container.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Supprime un clipnotte dans la zone projet de l'Atelier
     *
     * @param jComponent clipnotte à supprimer
     */
    public void supprimeExtension(final Component jComponent) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    JXTaskPane task = extensions.get(jComponent);
                    if (task != null)
                        container.remove(task);
                    container.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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

    private class TableData {

        Object o;
        boolean editable;
        String description;

        public TableData(Object o, boolean editable, String description) {
            super();
            this.o = o;
            this.editable = editable;
            this.description = description;
        }

    }

    private class JTableModel extends AbstractTableModel {
        private String[] columnNames = {"Champ", "Valeur"};
        private List<List<TableData>> matrix = new ArrayList<List<TableData>>();

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return matrix.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            try {
                return matrix.get(row).get(col).o;
            } catch (Exception e) {
                return null;
            }
        }

        public Object getDescriptionAt(int row) {
            try {
                return matrix.get(row).get(0).description;
            } catch (Exception e) {
                return null;
            }
        }

        public TableData getValueAtData(int row, int col) {
            try {
                return matrix.get(row).get(col);
            } catch (Exception e) {
                return null;
            }
        }

        /*
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        public Class<?> getColumnClass(int c) {
            TableData data = getValueAtData(0, c);
            if (data != null) {
                Object o = data.o;
                if (o != null)
                    return o.getClass();
                else
                    return String.class;
            } else
                return String.class;
        }

        /*
         * Don't need to implement this method unless your table's editable.
         */
        public boolean isCellEditable(int row, int col) {
            return getValueAtData(row, col).editable;
        }

        // public void setValueAt(Object value, int row, int col) {
        // getValueAtData(row, col).o = value;
        // }

        /*
         * Don't need to implement this method unless your table's data can
         * change.
         */
        public void setValueAt(Object value, int row, int col) {
            if (matrix.size() < row) {
                int s = matrix.size();
                for (int i = s; i <= row; i++) {
                    matrix.add(new ArrayList<TableData>());
                }
            }
            List<TableData> ligne = matrix.get(row);
            if (ligne.size() <= col) {
                int s = ligne.size();
                for (int i = s; i <= col; i++) {
                    ligne.add(null);
                }
            }
            TableData ne_w = new TableData(value, false, null);
            TableData old = ligne.set(col, ne_w);
            if (old != null) {
                ne_w.editable = old.editable;
            }
            fireTableCellUpdated(row, col);
        }

    }

}