package org.linotte.frame.atelier;

import javax.swing.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;

public class PasteL extends AbstractAction {
    private final AtelierFrame atelier;

    public PasteL(AtelierFrame atelier) {
        this.atelier = atelier;
    }

    public void actionPerformed(ActionEvent e) {
        Transferable clipData = atelier.clipbd.getContents(atelier);
        try {
            String clipString = (String) clipData.getTransferData(DataFlavor.stringFlavor);
            atelier.getCahierCourant().replaceSelection(clipString);
        } catch (Exception ex) {
        }
    }
}
