package org.linotte.frame.cahier;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.linotte.frame.atelier.Atelier;
import org.linotte.frame.atelier.ShowParEditorKit;
import org.linotte.frame.coloration.AtelierDocument;
import org.linotte.frame.gui.JTextPaneText;
import org.linotte.greffons.api.AKMethod;
import org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

@SuppressWarnings("serial")
public class JtextAreaCompletion extends JTextPaneText {

	private static final String COMPLETE_ACTION = "autocomplete";
	private static final String COMPLETE_ACTION_PROTOTYPE = "autocompleteprototype";
	// private static final String COMPLETE_ACTION_ATTRIBUT =
	// "autocompleteattribut";

	private LineRenderer lineRenderer;

	private List<String> words;

	private Map<String, List<String>> type_prototypes = new HashMap<String, List<String>>();
	private Map<String, Set<String>> methodes_parametres = new HashMap<String, Set<String>>();
	private SortedSet<Prototype> prototypes = new TreeSet<Prototype>();
	// pour l'affichage des styles :
	private AtelierDocument atelierDocument;
	private Cahier cahier;

	public JtextAreaCompletion(Cahier cahier, AtelierDocument doc, List<String> words, boolean highlighter) {
		super(doc, highlighter);
		atelierDocument = doc;
		this.cahier = cahier;
		setEditorKit(new ShowParEditorKit());
		setStyledDocument(doc);
		this.words = words;
		InputMap im = getInputMap();
		ActionMap am = getActionMap();
		im.put(KeyStroke.getKeyStroke(' ', java.awt.event.InputEvent.CTRL_MASK), COMPLETE_ACTION);
		im.put(KeyStroke.getKeyStroke('.'), COMPLETE_ACTION_PROTOTYPE);
		am.put(COMPLETE_ACTION, new CompleteAction());
		am.put(COMPLETE_ACTION_PROTOTYPE, new CompleteActionPrototype());
	}

	private class CompleteAction extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			completion(getCaretPosition());
		}
	}

	private class CompleteActionPrototype extends AbstractAction {
		public void actionPerformed(ActionEvent ev) {
			completionPrototype(getCaretPosition());
		}
	}

	private void completion(int offset) {
		String prefix = extrairePrefix();
		String[] hits = retourneListe(prefix);

		if (hits.length == 0) {
			requestFocusInWindow();
		} else if (hits.length == 1) {
			SwingUtilities.invokeLater(new CompletionTask(hits[0], offset, false));
		} else
			showPopup(offset, hits, Color.YELLOW, false);
	}

	private void completionPrototype(int offset) {
		String prototype = extrairePrefix();
		String[] hits = retourneListeMethodes(prototype);

		if (hits == null || hits.length == 0) {
			requestFocusInWindow();
			try {
				getDocument().insertString(offset, ".", null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		} else {
			try {
				getDocument().insertString(offset, ".", null);
				offset++;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			showPopup(offset, hits, Color.ORANGE, true);
		}
	}

	private String extrairePrefix() {
		int pos = getCaretPosition() - 1;
		String content = null;
		try {
			content = getText(0, pos + 1);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		// Find where the word starts
		int w;
		for (w = pos; w >= 0; w--) {
			if (!Character.isLetter(content.charAt(w)) && !Character.isDigit(content.charAt(w))) {
				break;
			}
		}

		String prefix = content.substring(w + 1).toLowerCase();
		return prefix;
	}

	private String[] retourneListe(String prefix) {
		List<String> hits = new ArrayList<String>();
		if (prefix.length() == 0)
			return hits.toArray(new String[0]);
		synchronized (words) {
			for (String word : words) {
				if ((word.startsWith(prefix)) && (!word.equals(prefix)))
					hits.add(word);
			}
			return hits.toArray(new String[hits.size()]);
		}
	}

	private String[] retourneListeMethodes(String prototype) {
		// On regarde dans les prototypes du livre
		Integer pos = getCaretPosition();
		Prototype[] tab = prototypes.toArray(new Prototype[prototypes.size()]);
		int index = tab.length - 1;

		String type = null;
		for (; index > -1 && type == null; index--) {
			Prototype p = tab[index];
			if (pos > p.pos) {
				if (prototype.equals(p.nom)) {
					type = p.type;
					break;
				}
			}
		}
		if (type != null) {
			List<String> list = type_prototypes.get(type);
			if (list != null) {
				List<String> list_retour = new ArrayList<String>();
				for (String fonction : list) {
					// On recherche les paramètres qui sont stockés dans le
					// sommaire du cahier :
					if (methodes_parametres.containsKey(type + fonction)) {
						StringBuilder builder = new StringBuilder().append("(");
						Iterator<String> i = methodes_parametres.get(type + fonction).iterator();
						while (i.hasNext()) {
							builder.append(i.next()).append(i.hasNext() ? ", " : ")");
						}
						list_retour.add(fonction + builder.toString());
					} else {
						if (fonction.endsWith(")"))
							list_retour.add(fonction);
						else
							list_retour.add(fonction + "()");
					}
				}
				return list_retour.toArray(new String[list_retour.size()]);
			}
			// On regarde dans les prototypes systèmes :

			org.linotte.moteur.entites.Prototype proto = null;
			cahier.getAtelier();
			for (org.linotte.moteur.entites.Prototype i : Atelier.linotte.especeModeleMap) {
				if (i.getType().equals(type)) {
					proto = i;
					break;
				}
			}
			if (proto != null) {
				List<String> l2 = new ArrayList<String>();
				{
					Set<String> slots = proto.retourneSlotsGreffons();
					if (slots != null) {
						for (String slt : slots) {
							// On récupère les paramètres :
							AKMethod akmethod = proto.retourneSlotGreffon(slt);
							String v = akmethod.parametres();
							l2.add(slt + v);
						}

					}
				}
				{
					Set<String> slots = proto.retourneSlotsLinotte();
					if (slots != null) {
						for (String slt : slots) {
							// On récupère les paramètres :
							//Processus akmethod = proto.retourneSlot(slt);
							String v = "()";//akmethod.parametres();
							l2.add(slt + v);
						}

					}
				}
				return l2.toArray(new String[l2.size()]);
			}
		}

		// TODO :
		// Retourner depuis le verbe attacher
		return null;
	}

	public List<String> getMots() {
		return words;
	}

	public void paint(Graphics g) {

		super.paint(g);
		if (lineRenderer != null)
			lineRenderer.repaint();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				((AtelierDocument) atelierDocument).setCharacterAttributes(cahier.getStyleBuffer().retourneStyles());
			}
		});
	}

	public void setLineRenderer(LineRenderer lineRenderer) {
		this.lineRenderer = lineRenderer;
	}

	// Nouvelle version

	private void showPopup(final int position, String[] hits, Color color, final boolean ajouter) {
		final JXList list = new JXList(hits);
		JScrollPane listScrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		list.setSelectionMode(0);
		list.setSelectedIndex(0);
		list.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		list.setBackground(color);
		listScrollPane.setPreferredSize(new Dimension(300, 100));
		list.setRolloverEnabled(true);
		list.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED));

		int x = 0;
		int y = 0;
		try {
			Rectangle r = modelToView(getCaretPosition());
			Point p = getLocationOnScreen();
			x = p.x + r.x;
			y = p.y + r.y + r.height;
		} catch (BadLocationException ex) {
			ex.printStackTrace();
		}

		PopupFactory factory = PopupFactory.getSharedInstance();
		final Popup popup = factory.getPopup(this, listScrollPane, x, y);
		popup.show();

		if (!list.requestFocusInWindow()) {
			popup.hide();
		}

		list.addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char ch = e.getKeyChar();
				switch (ch) {
				case '\n':
					SwingUtilities.invokeLater(new CompletionTask((String) list.getSelectedValue(), position, ajouter));
					break;
				case '\033':
					requestFocusInWindow();
					break;
				case '.':
					requestFocusInWindow();
					break;
				case '\b':
					requestFocusInWindow();
					try {
						getDocument().remove(position, 1);
					} catch (BadLocationException ex) {
						ex.printStackTrace();
					}
					break;
				default:
					SwingUtilities.invokeLater(new WriteTask(position, ch, ajouter));
				}
			}
		});
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				SwingUtilities.invokeLater(new CompletionTask((String) list.getSelectedValue(), position, ajouter));
			}
		});
		list.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				popup.hide();
			}
		});
	}

	public Map<String, List<String>> getTypePrototypes() {
		return type_prototypes;
	}

	public SortedSet<Prototype> getPrototypes() {
		return prototypes;
	}

	public Map<String, Set<String>> getMethodesDoublures() {
		return methodes_parametres;
	}

	private class CompletionTask implements Runnable {

		String completion;
		int position;
		boolean ajouter;

		CompletionTask(String completion, int position, boolean ajouter) {
			this.completion = completion;
			this.position = position;
			this.ajouter = ajouter;
		}

		public void run() {
			try {
				if (ajouter) {
					getDocument().insertString(position, completion, null);
				} else {
					String token = extrairePrefix();
					getDocument().insertString(position, completion.substring(token.length()), null);
				}
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
			requestFocusInWindow();
		}

	}

	private class WriteTask implements Runnable {

		int position;
		char ch;
		boolean ajouter;

		WriteTask(int position, char character, boolean ajouter) {
			this.position = position;
			this.ch = character;
			this.ajouter = ajouter;
		}

		public void run() {
			try {
				if (Character.isLetter(ch) || Character.isDigit(ch))
					if (ajouter) {
						getDocument().insertString(position, String.valueOf(ch), null);
						requestFocusInWindow();
					} else {
						getDocument().insertString(position, String.valueOf(ch), null);
						completion(getCaretPosition());
					}
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}

	}

}
