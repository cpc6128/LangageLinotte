package org.linotte.frame.atelier;

import javax.swing.*;
import javax.swing.undo.CannotUndoException;
import java.awt.event.ActionEvent;

public class UndoAction extends AbstractAction {
    private final AtelierFrame atelier;

    public UndoAction(AtelierFrame atelier) {
        super("Undo");
        this.atelier = atelier;
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            atelier.getCahierCourant().setEditeurUpdate(true);
            atelier.getCahierCourant().getUndoManager().undo();
        } catch (CannotUndoException ex) {
            ex.printStackTrace();
        }
        updateUndoState();
        atelier.redoAction.updateRedoState();
    }

    public void updateUndoState() {
        if (atelier.getCahierCourant() != null && atelier.getCahierCourant().getUndoManager() != null && atelier.getCahierCourant().getUndoManager().canUndo()) {
            setEnabled(true);
            putValue(Action.NAME, "Annuler");
        } else {
            setEnabled(false);
            putValue(Action.NAME, "Annuler");
        }
    }
}
