package org.linotte.frame.atelier;

import org.linotte.frame.Atelier;
import org.linotte.moteur.outils.Preference;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Action pour changer le Look & Feel Swing.
 */
public class LFAction extends AbstractAction {

    private final Atelier atelier;
    private UIManager.LookAndFeelInfo info;

    public LFAction(Atelier atelier, UIManager.LookAndFeelInfo info) {
        super("Habillage " + info.getName());
        this.atelier = atelier;
        this.info = info;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            UIManager.setLookAndFeel(info.getClassName());
            SwingUtilities.updateComponentTreeUI(atelier);
            Preference.getIntance().setProperty(Preference.P_STYLE, UIManager.getLookAndFeel().getClass().getName());
        } catch (Exception eX) {
            eX.printStackTrace();
        }
    }
}
