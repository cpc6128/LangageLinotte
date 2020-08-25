package org.linotte.frame.atelier;

import org.linotte.frame.outils.ArrowIcon;

import javax.swing.*;
import java.awt.*;

public class JMenuAtelier extends JMenu {

    private final Icon iconeClair;
    private final Icon icone;
    ArrowIcon iconRenderer;

    public JMenuAtelier(String titre, Icon iconeClair, Icon icone) {
        // http://java-swing-tips.blogspot.fr/2009/08/jmenubar-background-image.html
        super(titre);
        this.iconeClair = iconeClair;
        setIcon(icone);
        this.icone = icone;
        iconRenderer = new ArrowIcon(SwingConstants.SOUTH, true);
    }

    @Override
    protected void fireStateChanged() {
        ButtonModel m = getModel();
        if (m.isPressed() && m.isArmed()) {
            setIcon(iconeClair);
            setOpaque(true);
        } else if (m.isSelected()) {
            setIcon(iconeClair);
            setOpaque(true);
        } else if (isRolloverEnabled() && m.isRollover()) {
            setIcon(iconeClair);
        } else {
            setIcon(icone);
            setOpaque(false);
        }
        super.fireStateChanged();
    }

    ;

    @Override
    public void updateUI() {
        super.updateUI();
        setOpaque(false); // Motif lnf
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension d = this.getPreferredSize();
        int x = Math.max(0, d.width - iconRenderer.getIconWidth() - 3);
        iconRenderer.paintIcon(this, g, x, 15);
    }

}
