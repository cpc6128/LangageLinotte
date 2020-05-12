package org.linotte.frame.atelier;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class OuvrirLivreAction extends AbstractAction {

    private final Atelier atelier;
    public File info;

    public OuvrirLivreAction(Atelier atelier, File info) {
        super(info.getName());
        this.atelier = atelier;
        this.info = info;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            synchronized (atelier.listHistorique) {
                atelier.listHistorique.remove(this);
            }
            atelier.ouvrirLivre(info);
        } catch (Exception e1) {
        }
    }
}
