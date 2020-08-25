package org.linotte.frame.atelier;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;

public class CutL extends AbstractAction {
    private final AtelierFrame atelier;

    public CutL(AtelierFrame atelier) {
        this.atelier = atelier;
    }

    public void actionPerformed(ActionEvent e) {
        String selection = atelier.getCahierCourant().getSelectedText();
        if (selection == null)
            return;
        StringSelection clipString = new StringSelection(selection);
        atelier.clipbd.setContents(clipString, clipString);
        atelier.getCahierCourant().replaceSelection("");
    }
}
