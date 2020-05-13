package org.linotte.frame.atelier;

import org.jdesktop.swingx.JXTextField;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

public class TeleType extends JPanel {

    // Prompteur :
    // pile des commandes
    final public Stack<String> commandes = new Stack<String>();
    final public JXTextField entree;
    public int position = 0;
    JButton action = null;

    public TeleType() {

        setLayout(new BorderLayout());
        action = new JButton();
        action.setMnemonic(KeyEvent.VK_Y);
        entree = new JXTextField("Entrer une action à exécuter");
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
        add(entree, BorderLayout.CENTER);
        action.setIcon(Ressources.getImageTheme("GO", 24));
        add(action, BorderLayout.EAST);
    }

    public void addActionListener(ActionListener actionListener) {
        action.addActionListener(actionListener);
    }

}
