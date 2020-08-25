package org.linotte.frame.atelier;

import javax.swing.*;
import javax.swing.undo.CannotRedoException;
import java.awt.event.ActionEvent;

public class RedoAction extends AbstractAction {
    private final AtelierFrame atelier;

    public RedoAction(AtelierFrame atelier) {
        super("Redo");
        this.atelier = atelier;
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        try {
            atelier.getCahierCourant().setEditeurUpdate(true);
            atelier.getCahierCourant().getUndoManager().redo();
        } catch (CannotRedoException ex) {
            ex.printStackTrace();
        }
        updateRedoState();
        atelier.undoAction.updateUndoState();
    }

    public void updateRedoState() {
        if (atelier.getCahierCourant() != null && atelier.getCahierCourant().getUndoManager() != null && atelier.getCahierCourant().getUndoManager().canRedo()) {
            setEnabled(true);
            putValue(Action.NAME, "Rétablir");
        } else {
            setEnabled(false);
            putValue(Action.NAME, "Rétablir");
        }
    }
}
