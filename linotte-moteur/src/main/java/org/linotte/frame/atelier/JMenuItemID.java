package org.linotte.frame.atelier;

import javax.swing.*;

public class JMenuItemID extends JMenuItem {
    String id;

    public JMenuItemID(String texte, String pid) {
        super(texte);
        id = pid;
    }

    public String getID() {
        return id;
    }
}
