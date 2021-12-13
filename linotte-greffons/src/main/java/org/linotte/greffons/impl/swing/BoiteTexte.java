package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.Document;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * 
 * @author R.M
 * 
 */
public class BoiteTexte extends ComposantDeplacable {

	private JScrollPane boite;
	protected JTextArea textPane;
	private UndoManager undo = new UndoManager();

	private static final String TEXT_SUBMIT = "text-submit";

	@SuppressWarnings("serial")
	@Override
	public void initialisation() throws GreffonException {
		if (boite != null) {
			super.initEvenement();
			return;
		}
		textPane = new JTextArea();
		super.initaccessibilite(textPane);
		textPane.setLineWrap(false);
		initialize();
		setVisible(getAttributeAsBigDecimal("visible").intValue()==1);

		// Dimensions :
		int hauteur = getAttributeAsBigDecimal("hauteur").intValue();
		int largeur = getAttributeAsBigDecimal("largeur").intValue();

		boite = new JScrollPane(textPane);
		boite.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		boite.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		textPane.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				for (ListenerGreffons listener : touche) {
					listener.execute();
				}
				setAttribute("texte", textPane.getText());
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		boite.setPreferredSize(new Dimension(largeur, hauteur));
		boite.setMaximumSize(new Dimension(largeur, hauteur));
		boite.setVisible(isVisible());

		// Ajout de la gestion du undo / redo

		Document doc = textPane.getDocument();

		// Listen for undo and redo events
		doc.addUndoableEditListener(new UndoableEditListener() {
			public void undoableEditHappened(UndoableEditEvent evt) {
				undo.addEdit(evt.getEdit());
			}
		});

		// Create an undo action and add it to the text component
		textPane.getActionMap().put("Undo", new AbstractAction("Undo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canUndo()) {
						undo.undo();
					}
				} catch (CannotUndoException e) {
				}
			}
		});

		// Create a redo action and add it to the text component
		textPane.getActionMap().put("Redo", new AbstractAction("Redo") {
			public void actionPerformed(ActionEvent evt) {
				try {
					if (undo.canRedo()) {
						undo.redo();
					}
				} catch (CannotRedoException e) {
				}
			}
		});

		// Bind the undo action to ctl-Z (or command-Z on mac)
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Undo");
		// Bind the redo action to ctl-Y (or command-Y on mac)
		textPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), "Redo");

		if (getAttribute("infobulle") != null && getAttributeAsString("infobulle").trim().length() > 0)
			textPane.setToolTipText(getAttributeAsString("infobulle"));
		else {
			textPane.setToolTipText(null);
		}
		
		// Ajout du texte :
		if (getAttribute("texte") != null)
			textPane.setText(getAttributeAsString("texte"));

		super.initEvenement();
	}

	@SuppressWarnings("serial")
	private void initialize() {
		// textPane.setContentType("text/html");
		InputMap input = textPane.getInputMap();
		// KeyStroke enter = KeyStroke.getKeyStroke("ENTER");
		KeyStroke controlEnter = KeyStroke.getKeyStroke("control ENTER");
		// input.put(enter, INSERT_BREAK); // input.get(enter)) = "insert-break"
		input.put(controlEnter, TEXT_SUBMIT);

		ActionMap actions = textPane.getActionMap();
		actions.put(TEXT_SUBMIT, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (ListenerGreffons listener : clic_souris) {
					listener.execute();
				}
			}
		});
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return boite;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (textPane != null && "texte".equals(clef) &&
					// Patch de mise à jour intenpestif
					!textPane.getText().equals(((Acteur) getAttribute(clef)).getValeur())) {
				int pos = textPane.getCaretPosition();
				textPane.setText((String) ((Acteur) getAttribute(clef)).getValeur());
				try {
					textPane.setCaretPosition(pos);
				} catch (Exception e) {
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (boite != null) {
			// menu.dispose();
			boite = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@Slot(nom = "annuler")
	public boolean undo() {
		try {
			if (undo.canUndo()) {
				undo.undo();
				return true;
			}
		} catch (CannotUndoException e) {
		}
		return false;
	}

	@Slot(nom = "rétablir")
	public boolean redo() {
		try {
			if (undo.canRedo()) {
				undo.redo();
				return true;
			}
		} catch (CannotRedoException e) {
		}
		return false;
	}
}
