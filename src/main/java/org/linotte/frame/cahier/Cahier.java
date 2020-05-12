package org.linotte.frame.cahier;

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

import org.linotte.frame.Atelier;
import org.linotte.frame.atelier.*;
import org.linotte.frame.cahier.sommaire.JPanelSommaire;
import org.linotte.frame.cahier.timbre.JPanelPlancheATimbre;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.coloration.*;
import org.linotte.frame.coloration.StyleBuilder.STYLE;
import org.linotte.frame.gui.JTextPaneText;
import org.linotte.frame.gui.PopupListener;
import org.linotte.frame.moteur.Formater;
import org.linotte.moteur.outils.FichierOutils;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.analyse.timbre.TraducteurTimbreEnTexte;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Classe pour l'affichage du cahier
 * <p>
 * http://www.daniweb.com/software-development/java/threads/331500/how-can-i-add
 * -a-clickable-url-in-a-jtextpane
 *
 * @author ronan
 */
@SuppressWarnings("serial")
public class Cahier extends JPanel implements KeyListener, MouseListener {

    static final Cursor handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    static final Cursor defaultCursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
    static final Cursor waitCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    private static final int UPDATE_TIME = 2000;
    public ProcessStyle processStyle = null;
    // Vue package :
    public JScrollPane scrollPan;
    // Tests unitaires :
    public boolean tests;
    protected JtextAreaCompletion jEditorPaneCachier = null;
    protected UndoManager undoManager = null;
    private JPopupMenu popupCahier;
    private JPopupMenu popupTimbre;
    private JPanelPlancheATimbre jPanelTimbre = null;
    private boolean editeurUpdate = false;
    private long lastupdate = 0;
    private AtelierFrame atelier;
    private LineRenderer lineRenderer;
    private File fichier;

    // Gestion de l'affichage des couleurs dans l'Atelier
    private JPanelSommaire jPanelSommaire = null;
    private StyleBuffer styleBuffer = null;
    private Onglet onglet;
    private EtatCachier etatCahier = EtatCachier.MODIFIE;
    private boolean gestionFavoris = true;
    private Stack<Integer> pileHistorique = new Stack<Integer>();

    // Historique de navigation dans le fichier
    // Gestion des timbres :
    private boolean visualiserTimbre = false; // Livre visuel
    private String copieLivreDepuisTimbres = null; // Sinon, problème avec le formatage
    private boolean visualiserTimbreVoirCode = false; // voir le code source en lecture seule

    public Cahier() {
    }

    public void init(UndoAction undoAction, RedoAction redoAction, CutL couper, CopyL copier, PasteL coller, Font font, AtelierFrame ppere) {

        atelier = ppere;
        jPanelTimbre = new JPanelPlancheATimbre(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(getJEditorPaneCahier(), BorderLayout.CENTER);
        panel.setBackground(Color.WHITE);

        scrollPan = new JScrollPane();
        scrollPan.getVerticalScrollBar().setUnitIncrement(10);
        scrollPan.getHorizontalScrollBar().setUnitIncrement(10);

        scrollPan.addMouseWheelListener(new MouseWheelListener() {
            // Evolution :
            // http://langagelinotte.free.fr/forum/showthread.php?tid=1118&pid=7550
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isControlDown()) {
                    int unites = e.getUnitsToScroll();
                    STYLE[] styles = STYLE.values();
                    for (STYLE style : styles) {
                        StyleBean styleBean = StyleBuilder.retourneStyleBean(style);
                        if (unites < 0) {
                            styleBean.setSize(styleBean.getSize() + 1);
                        } else {
                            if (styleBean.getSize() > 5)
                                styleBean.setSize(styleBean.getSize() - 1);
                        }
                        StyleBuilder.appliquerStyle(style);
                    }
                    atelier.getCahierCourant().forceStyle();
                    e.consume();
                }
            }
        });

        lineRenderer = new LineRenderer(this);
        setLayout(new BorderLayout());
        add(lineRenderer, BorderLayout.WEST);
        add(scrollPan, BorderLayout.CENTER);
        scrollPan.getViewport().setView(panel);
        jEditorPaneCachier.setLineRenderer(lineRenderer);

        // Popup pour activer la couleur :
        popupCahier = new JPopupMenu();
        popupTimbre = new JPopupMenu();

        popupCahier.add(undoAction).setIcon(Ressources.getImageIcon("edit-undo.png"));
        popupCahier.add(redoAction).setIcon(Ressources.getImageIcon("edit-redo.png"));

        JMenuItem cut = new JMenuItem("Couper"), copy = new JMenuItem("Copier"), paste = new JMenuItem("Coller");

        cut.addActionListener(couper);
        copy.addActionListener(copier);
        paste.addActionListener(coller);

        copy.setIcon(Ressources.getImageIcon("edit-copy.png"));
        paste.setIcon(Ressources.getImageIcon("edit-paste.png"));
        cut.setIcon(Ressources.getImageIcon("edit-cut.png"));

        popupCahier.add(new Retour("Retour         Alt+Gauche"));
        popupCahier.add(new Max("Agrandir       Crt+M"));
        popupCahier.add(new Next("Cahier svt     Crt+Page haut"));
        popupCahier.add(new Next("Cahier prcd    Crt+Page bas"));
        popupCahier.add(copy);
        popupCahier.add(paste);
        popupCahier.add(cut);

        popupTimbre.add(new Max("Agrandir       Crt+M"));

        MouseListener popupListenerCahier = new PopupListener(popupCahier);
        MouseListener popupListenerTimbre = new PopupListener(popupTimbre);

        undoManager = new CahierUndoManager();
        getDocumentCahier().addUndoableEditListener(new UndoableEditEcouteur());

        jEditorPaneCachier.getActionMap().put("Undo", undoAction);
        jEditorPaneCachier.getActionMap().put("Redo", redoAction);
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("control Y"), "Redo");

        // createActionTable(jEditorPaneCachier);

        jEditorPaneCachier.getActionMap().put("Copier", copier);
        jEditorPaneCachier.getActionMap().put("Coller", coller);
        jEditorPaneCachier.getActionMap().put("Couper", couper);
        jEditorPaneCachier.getActionMap().put("Save", new Save());
        jEditorPaneCachier.getActionMap().put("Close", new Close());
        jEditorPaneCachier.getActionMap().put("Next", new Next());
        jEditorPaneCachier.getActionMap().put("Previous", new Previous());
        jEditorPaneCachier.getActionMap().put("Max", new Max());
        jEditorPaneCachier.getActionMap().put("Retour", new Retour());
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("control X"), "Couper");
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("control V"), "Coller");
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("control C"), "Copier");
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("control S"), "Save");
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("control W"), "Close");
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("control M"), "Max");
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("control PAGE_UP"), "Next");
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("control PAGE_DOWN"), "Previous");
        jEditorPaneCachier.getInputMap().put(KeyStroke.getKeyStroke("alt LEFT"), "Retour");

        setEditorPaneTabs(jEditorPaneCachier, 2);
        jEditorPaneCachier.setDragEnabled(true);
        jEditorPaneCachier.setOpaque(false);
        jEditorPaneCachier.setForeground(Color.BLUE);
        jEditorPaneCachier.getStyledDocument().addDocumentListener(new DocumentListenerImpl());

        jEditorPaneCachier.addStyle("normal", StyleLinotte.styleRacine);

        jEditorPaneCachier.setFont(font);

        jEditorPaneCachier.addMouseListener(popupListenerCahier);
        jEditorPaneCachier.addKeyListener(this);
        jEditorPaneCachier.addMouseListener(this);

        jPanelTimbre.addMouseListener(popupListenerTimbre);

        new DropTarget(jEditorPaneCachier, new DragAndDropBoardListener((Atelier) atelier));

        jPanelSommaire = new JPanelSommaire(this);

        styleBuffer = new StyleBuffer();

        processStyle = new ProcessStyle(this, styleBuffer);
        processStyle.start();

        LinkController handler = new LinkController();
        jEditorPaneCachier.addMouseMotionListener(handler);
        jEditorPaneCachier.addMouseListener(handler);

    }

    /**
     * This method initializes jEditorPaneCachier
     *
     * @return JEditorPane
     */
    private JEditorPane getJEditorPaneCahier() {
        // TODO remplacer l'appel grammaire
        List<String> t = new ArrayList<String>();
        t.addAll(Atelier.linotte.getGrammaire().getMots());
        jEditorPaneCachier = new JtextAreaCompletion(this, new AtelierDocument(this), t, true);
        return jEditorPaneCachier;
    }

    public String getSelectedText() {
        return jEditorPaneCachier.getSelectedText();
    }

    public void replaceSelection(final String param) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jEditorPaneCachier.replaceSelection(param);
            }
        });
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public void setCaretPositionAndScroll(final int pos) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jEditorPaneCachier.setCaretPosition(pos);
                Rectangle r;
                try {
                    r = jEditorPaneCachier.modelToView(pos);
                    if (r != null) {
                        Rectangle vis = jEditorPaneCachier.getVisibleRect(); // to
                        // get
                        // the
                        // actual
                        // height
                        r.height = vis.height;
                        jEditorPaneCachier.scrollRectToVisible(r);
                    }
                } catch (BadLocationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } // scroll to position 0, i.e. top
            }
        });
    }

    public void focus() {
        jEditorPaneCachier.grabFocus();
    }

    public int getCaretPosition() {
        return jEditorPaneCachier.getCaretPosition();
    }

    public void setCaretPosition(final int pos) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jEditorPaneCachier.setCaretPosition(pos);
            }
        });
    }

    public Document getDocumentCahier() {
        if (jEditorPaneCachier != null)
            return jEditorPaneCachier.getDocument();
        else
            return null;
    }

    public JtextAreaCompletion getEditorPanelCahier() {
        return jEditorPaneCachier;
    }

    public void setSelectionCachier(final int debut, final int fin) throws BadLocationException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    jEditorPaneCachier.setCaretPosition(debut);
                    jEditorPaneCachier.setSelectionStart(debut);
                    jEditorPaneCachier.setSelectionEnd(fin);
                    jEditorPaneCachier.grabFocus();
                } catch (Exception e) {
                }
            }
        });
    }

    public void setPositionCahier(final int debut) throws BadLocationException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    jEditorPaneCachier.setCaretPosition(debut);
                    // http://forum.java.sun.com/thread.jspa?threadID=5300883&tstart=90
                    Rectangle mRectangle2 = jEditorPaneCachier.modelToView(debut);
                    Rectangle mRectangle3 = new Rectangle(mRectangle2.x, mRectangle2.y, mRectangle2.width, jEditorPaneCachier.getVisibleRect().height);
                    jEditorPaneCachier.scrollRectToVisible(mRectangle3);
                } catch (Exception e) {
                    // goFinTableau();
                }
                jEditorPaneCachier.grabFocus();
            }
        });
    }

    public void setPositionCahierAndScrollToMiddle(final int debut) throws BadLocationException {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    jEditorPaneCachier.setCaretPosition(debut);
                    // http://forum.java.sun.com/thread.jspa?threadID=5300883&tstart=90
                    Rectangle mRectangle2 = jEditorPaneCachier.modelToView(debut);
                    Rectangle mRectangle3 = new Rectangle(mRectangle2.x, mRectangle2.y - (jEditorPaneCachier.getVisibleRect().height / 3), mRectangle2.width,
                            jEditorPaneCachier.getVisibleRect().height);
                    jEditorPaneCachier.scrollRectToVisible(mRectangle3);

                    // Mise à jour de la mise en valeur de sommaire
                    resetHighlight();

                } catch (Exception e) {
                    // goFinTableau();
                }
                // jEditorPaneCachier.grabFocus(); // Pas besoin en mode debug !
            }
        });
    }

    public void effacerCahier() {
        copieLivreDepuisTimbres = null;
        Document doc = getDocumentCahier();
        synchronized (doc) {
            try {
                getDocumentCahier().remove(0, getDocumentCahier().getLength());
            } catch (BadLocationException e2) {
                e2.printStackTrace();
            }
        }
        undoManager.discardAllEdits();
        jEditorPaneCachier.setCaretPosition(0);
    }

    public String retourneTexteCahier() {

        if (isVisualiserTimbre()) {
            if (copieLivreDepuisTimbres != null)
                return copieLivreDepuisTimbres;
            TraducteurTimbreEnTexte traducteur = new TraducteurTimbreEnTexte();
            copieLivreDepuisTimbres = traducteur.traiter(jPanelTimbre.getTimbresAExecuter(), Atelier.linotte.getLangage());
            return copieLivreDepuisTimbres;
        } else {
            try {
                Document in = getDocumentCahier();
                return in.getText(0, in.getLength());
            } catch (BadLocationException e) {
                return "";
            }
        }
    }

    public boolean isEditeurUpdate() {
        return editeurUpdate
                // Soulage le CPU...
                && (System.currentTimeMillis() - lastupdate) > UPDATE_TIME;
    }

    public void setEditeurUpdate(boolean editeurUpdate) {
        if (editeurUpdate) {
            lastupdate = System.currentTimeMillis();
            processStyle.setStopLecture();
        }
        this.editeurUpdate = editeurUpdate;
    }

    public void ecrireInsertCachier(String texte) {
        try {
            this.getDocumentCahier().insertString(getDocumentCahier().getLength(), texte, null);
            copieLivreDepuisTimbres = texte; // Pour les gestions des timbres, sinon problème coloration.
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void ecrireInsertAuCurseurCachier(String texte) {
        try {
            this.getDocumentCahier().insertString(getCaretPosition(), texte, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // http://forum.java.sun.com/thread.jspa?forumID=57&threadID=585006
    public void setEditorPaneTabs(JTextPaneText textPane, int charactersPerTab) {
        FontMetrics fm = textPane.getFontMetrics(textPane.getFont());
        int charWidth = fm.charWidth('w');
        int tabWidth = charWidth * charactersPerTab;
        ShowParLabelView.decallage = tabWidth;

        TabStop[] tabs = new TabStop[20];

        for (int j = 0; j < tabs.length; j++) {
            int tab = j + 1;
            tabs[j] = new TabStop(tab * tabWidth);
        }

        TabSet tabSet = new TabSet(tabs);
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        StyleConstants.setTabSet(attributes, tabSet);
        int length = textPane.getDocument().getLength();
        textPane.getStyledDocument().setParagraphAttributes(0, length, attributes, false);
    }

    public File getFichier() {
        return fichier;
    }

    public void sauvegarderFichier(File fichier) throws IOException {
        this.fichier = fichier;
        if (isVisualiserTimbre()) {
            FichierOutils.ecrireLivreGraphique(fichier, jPanelTimbre.getTimbresASauvegarder());
        } else {
            FichierOutils.ecrire(fichier, retourneTexteCahier());
        }
        setEtatCahier(EtatCachier.SAUVEGARDE);
        changeAtelierEtat();
    }

    public void chargerFichier(File fichier) throws IOException {
        effacerCahier();
        this.fichier = fichier;

        // Est-ce un livre graphique ?
        PushbackInputStream pis = new PushbackInputStream(new FileInputStream(fichier), 2);
        if (FichierOutils.estCeUnLivreVisuel(pis)) {
            List<Timbre> timbres = FichierOutils.lireLivreGraphique(pis);
            setVisualiserTimbre(true);
            jPanelTimbre.chargerTimbresSurPlanche(timbres);
        } else {
            ecrireInsertCachier(FichierOutils.lire(pis));
            setCaretPosition(0);
        }

        getJSommaire().vider();
        setEtatCahier(EtatCachier.SAUVEGARDE);
        changeAtelierEtat();
    }

    public void renommerFichier(File fichier) throws IOException {
        this.fichier = fichier;
        onglet.setTitre();
    }

    public void forceStyle() {
        processStyle.force();
    }

    public void changeAtelierEtat() {
        SwingUtilities.invokeLater(() -> {
            boolean modifie = etatCahier == EtatCachier.MODIFIE;
            if (getFichier() == null) {
                Preference.getIntance().setProperty(Preference.P_FICHIER, "");
                atelier.setTitle("(nouveau livre) - " + AtelierFrame.ATELIER_DE_PROGRAMMATION_LINOTTE);
            } else {
                Preference.getIntance().setProperty(Preference.P_FICHIER, getFichier().getAbsolutePath());
                atelier.setTitle(getFichier().getAbsolutePath() + " - " + AtelierFrame.ATELIER_DE_PROGRAMMATION_LINOTTE);
            }
            atelier.jButtonRanger.setEnabled(modifie);
            if (modifie) {
                atelier.jButtonRanger.setIcon(Ressources.getImageTheme("SAVE", 32, Color.RED));
            } else {
                atelier.jButtonRanger.setIcon(Ressources.getImageTheme("SAVE", 32));
            }

            atelier.getJButtonTester().setVisible(tests);
            atelier.undoAction.updateUndoState();
            atelier.redoAction.updateRedoState();
            if (!visualiserTimbre) {
                atelier.refreshSommaireLocation();
                atelier.getJButtonTimbre().setVisible(false);
                atelier.refreshBoutonTimbre(Cahier.this);
            } else {
                atelier.getJButtonTimbre().setVisible(true);
                atelier.refreshBoutonTimbre(Cahier.this);
            }

            // Temporaire :
            atelier.getJMenuItemExporter().setVisible(!visualiserTimbre);
            atelier.getJMenuItemExporterHTML().setVisible(!visualiserTimbre);
            atelier.getJMenuItemExporterRTF().setVisible(!visualiserTimbre);
            atelier.getJMenuItemExporterPNG().setVisible(!visualiserTimbre);


        });
    }

    public JPanelSommaire getJSommaire() {
        return jPanelSommaire;
    }

    public StyleBuffer getStyleBuffer() {
        return styleBuffer;
    }

    public Onglet getOnglet() {
        return onglet;
    }

    public void setOnglet(Onglet buttonTabComponent) {
        onglet = buttonTabComponent;
    }

    public EtatCachier getEtatCahier() {
        return etatCahier;
    }

    public void setEtatCahier(EtatCachier etat) {
        atelier.jButtonRanger.setEnabled(etat == EtatCachier.MODIFIE);
        if (etat == EtatCachier.MODIFIE) {
            atelier.jButtonRanger.setIcon(Ressources.getImageTheme("SAVE", 32, Color.RED));
        } else {
            atelier.jButtonRanger.setIcon(Ressources.getImageTheme("SAVE", 32));
        }
        copieLivreDepuisTimbres = null;
        etatCahier = etat;
        onglet.setTitre();
        if (!visualiserTimbre && !jPanelSommaire.isVisible()) {
            jPanelSommaire.setVisible(true);
            atelier.refreshSommaireLocation();
        }
    }

    public AtelierFrame getAtelier() {
        return atelier;
    }

    public void setCouleur(Color couleur, boolean force) {
        if (onglet != null)
            onglet.setCouleur(couleur, force);
    }

    public void setGestionFavoris(boolean gestionFavoris) {
        this.gestionFavoris = gestionFavoris;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        resetHighlight();
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        resetHighlight();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void resetHighlight() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    jPanelSommaire.afficheFonction(getCaretPosition());
                    jPanelSommaire.repaint();
                } catch (Exception ble) {
                    // ble.fillInStackTrace();
                }
            }
        });
    }

    public void scroller() {
        JViewport vue = scrollPan.getViewport();
        int heightViewport = vue.getViewSize().height;
        int mov = 0;
        while (mov < heightViewport) {
            mov = mov + 10;
            vue.setViewPosition(new Point(0, mov));
            vue.updateUI();
        }
    }

    public boolean isVisualiserTimbre() {
        return visualiserTimbre;
    }

    public void setVisualiserTimbre(boolean visualiserTimbre) {
        this.visualiserTimbre = visualiserTimbre;
        if (visualiserTimbre && !visualiserTimbreVoirCode) {
            lineRenderer.setDraw(false);
            lineRenderer.setVisible(false);
            remove(scrollPan);
            add(jPanelTimbre, BorderLayout.CENTER);
            jPanelSommaire.setVisible(false);

        } else {
            lineRenderer.setDraw(true);
            lineRenderer.setVisible(true);
            remove(jPanelTimbre);
            add(scrollPan, BorderLayout.CENTER);
            jPanelSommaire.setVisible(true);
            atelier.refreshSommaireLocation();
        }

    }

    public JPanelPlancheATimbre getjPanelTimbre() {
        return jPanelTimbre;
    }

    public boolean isVisualiserTimbreVoirCode() {
        return visualiserTimbreVoirCode;
    }

    public void setVisualiserTimbreVoirCode(final boolean visualiserTimbreVoirCode) {
        this.visualiserTimbreVoirCode = visualiserTimbreVoirCode;
        if (visualiserTimbreVoirCode) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    effacerCahier();
                    ecrireInsertCachier(retourneTexteCahier());
                    setCaretPosition(0);
                    // On format :
                    Formater.action(Cahier.this, Atelier.linotte);
                    jEditorPaneCachier.setEditable(false);
                    lineRenderer.setVisible(!visualiserTimbreVoirCode);
                    atelier.getCahierCourant().forceStyle();
                }
            });
        }
    }

    public enum EtatCachier {
        SAUVEGARDE, MODIFIE
    }

    protected class DocumentListenerImpl implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            if (e.getType() != DocumentEvent.EventType.CHANGE) {
                if (!isVisualiserTimbre())
                    setEtatCahier(EtatCachier.MODIFIE);
            }
            setEditeurUpdate(true);
        }

        public void removeUpdate(DocumentEvent e) {
            if (e.getType() != DocumentEvent.EventType.CHANGE) {
                if (!isVisualiserTimbre())
                    setEtatCahier(EtatCachier.MODIFIE);
            }
            setEditeurUpdate(true);
            // processApplicationStyle.appliquer(30, 0);
        }

        public void changedUpdate(DocumentEvent e) {
            // processApplicationStyle.appliquer(30, 0);
        }
    }

    protected class UndoableEditEcouteur implements UndoableEditListener {
        public void undoableEditHappened(UndoableEditEvent e) {
            getUndoManager().addEdit(e.getEdit());
            atelier.undoAction.updateUndoState();
            atelier.redoAction.updateRedoState();
        }
    }

    class Save extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            atelier.jButtonRanger.doClick();
        }
    }

    class Close extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            onglet.button.doClick();
        }
    }

    class Next extends AbstractAction {
        public Next() {
            super();
        }

        public Next(String string) {
            super(string);
        }

        public void actionPerformed(ActionEvent e) {
            atelier.getCahierOnglet().next();
        }
    }

    class Previous extends AbstractAction {
        public Previous() {
            super();
        }

        public Previous(String string) {
            super(string);
        }

        public void actionPerformed(ActionEvent e) {
            atelier.getCahierOnglet().previous();
        }
    }

    class Max extends AbstractAction {
        public Max() {
            super();
        }

        public Max(String string) {
            super(string);
        }

        public void actionPerformed(ActionEvent e) {

            if (Atelier.explorateur.isVisible()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Atelier.explorateur.setVisible(false);
                    }
                });
            } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Atelier.explorateur.setVisible(true);
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                atelier.splitPaneProjet.setDividerLocation(atelier.taille_split_projet);
                            }
                        });
                    }
                });
            }
        }
    }

    class Retour extends AbstractAction {

        public Retour() {
            super();
        }

        public Retour(String string) {
            super(string);
        }

        public void actionPerformed(ActionEvent e) {
            if (!pileHistorique.isEmpty()) {
                jEditorPaneCachier.setCaretPosition(pileHistorique.pop());
            }
        }
    }

    public class LinkController extends MouseAdapter implements MouseMotionListener {

        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2 && !e.isConsumed()) {

                e.consume();
                JTextPane editor = (JTextPane) e.getSource();
                Document doc = editor.getDocument();
                Point pt = new Point(e.getX(), e.getY());
                int pos = editor.viewToModel(pt);

                if (pos >= 0) {
                    if (doc instanceof DefaultStyledDocument) {
                        DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
                        Element el = hdoc.getCharacterElement(pos);
                        AttributeSet a = el.getAttributes();
                        String href = (String) a.getAttribute(HTML.Attribute.HREF);

                        if (href != null) {
                            try {
                                pileHistorique.push(jEditorPaneCachier.getCaretPosition());
                                getJSommaire().forceParagraphe(href);
                            } catch (Exception ev) {
                                System.err.println(ev.getMessage());
                            }
                        }
                    } // if (doc instanceof DefaultStyledDocument)
                } // if (pos >= 0)
                // handle double click.
            }

        }// public void mouseClicked(MouseEvent e)

        public void mouseMoved(MouseEvent ev) {

            JTextPane editor = (JTextPane) ev.getSource();
            Point pt = new Point(ev.getX(), ev.getY());
            int pos = editor.viewToModel(pt);

            if (pos >= 0) {
                Document doc = editor.getDocument();

                if (doc instanceof DefaultStyledDocument) {
                    DefaultStyledDocument hdoc = (DefaultStyledDocument) doc;
                    Element e = hdoc.getCharacterElement(pos);
                    AttributeSet a = e.getAttributes();
                    String href = (String) a.getAttribute(HTML.Attribute.HREF);

                    if (href != null) {
                        if (getCursor() != handCursor) {
                            if (getJSommaire().isParagraphe(href))
                                editor.setCursor(handCursor);
                            else
                                editor.setCursor(defaultCursor);
                        }
                    } else {
                        editor.setCursor(defaultCursor);
                    }
                } // (doc instanceof DefaultStyledDocument)
            } // pos >=0
            else {
                setToolTipText(null);
            }
        }// mouseMoved
    }// LinkController

}