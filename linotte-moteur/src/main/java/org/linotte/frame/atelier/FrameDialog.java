/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : November 15, 2005                            *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                           *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.frame.atelier;

import org.linotte.frame.latoile.Java6;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Fenêtre de dialogue principale pour 'demander' ou 'afficher' un message à l'utilisateur
 */
@SuppressWarnings("serial")
public class FrameDialog extends JDialog implements WindowListener, PropertyChangeListener {

	private String login = null;

	private JLabel champ_label;

	private JScrollPane boite;

	private JCheckBox toucheEntree;

	private JTextArea champTexte;

	private JTextField champNombre;

	private JOptionPane fenetre;

	private String[] options_on = { "Valider" };

	private String[] options_off = { "X" };

	private int[] mnemonic = { java.awt.event.KeyEvent.VK_V };

	private String message = "Entrez la valeur";

	public static Frame frame = null;

	private Role type = Role.TEXTE;

	private boolean etatToucheEntree;

	public FrameDialog(Frame fram) {

		super(fram, true);

		setIconImage();

		addWindowListener(this);

		frame = fram;

		// Pour le texte :
		champTexte = new JTextArea();
		boite = new JScrollPane(champTexte);
		boite.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		// Pour les nombres :
		champNombre = new JTextField();
		// Label
		champ_label = new JLabel();

		// CheckBox pour la touche entrée :
		if (!Preference.getIntance().containsKey(Preference.P_TOUCHE_ENTREE)) {
			// On initialise la préférence
			Preference.getIntance().setBoolean(Preference.P_TOUCHE_ENTREE, true);
		}

		toucheEntree = new JCheckBox("Touche [entrée] pour valider", Preference.getIntance().getBoolean(Preference.P_TOUCHE_ENTREE));
		etatToucheEntree = Preference.getIntance().getBoolean(Preference.P_TOUCHE_ENTREE);

		ItemListener changeListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				Preference.getIntance().setBoolean(Preference.P_TOUCHE_ENTREE, etatToucheEntree = source.isSelected());
			}
		};
		toucheEntree.addItemListener(changeListener);

		champTexte.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (etatToucheEntree && KeyEvent.VK_ENTER == e.getKeyCode()) {
					login = getCurrentComponent().getText();
					quit();
				}
			}

			public void keyReleased(KeyEvent e) {
			}

		});

		Object[] tableau = { message, boite, toucheEntree };

		fenetre = new JOptionPane(tableau, JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_OPTION, Ressources.getImageIcon("audio-input-microphone.png"),
				options_on, options_on[0]);

		setContentPane(fenetre);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(ComponentEvent ce) {
				getCurrentComponent().requestFocusInWindow();
			}
		});

		champNombre.addKeyListener(new KeyListener() {

			public void keyTyped(KeyEvent e) {
				check();
			}

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				check();
			}

		});

		dispose();
		initialize();

		fenetre.addPropertyChangeListener(this);
	}

	private void setIconImage() {
		Java6.setIconImage(this, Ressources.getImageIcon("audio-card.png").getImage());
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		int y = 300, x = 200;
		this.setSize(y, x);
		// this.setPreferredSize(new java.awt.Dimension(y, x));
		// this.setMaximumSize(new java.awt.Dimension(y, x));
		this.setMinimumSize(new java.awt.Dimension(y, x));
	}

	public void setSimpleMessage(String pMessage) {
		setType(Role.TEXTE);
		pMessage = toHTML(pMessage);
		System.out.println(pMessage);
		champ_label.setText(pMessage);
		Object[] tableau = { champ_label };
		fenetre.setMessage(tableau);
		fenetre.setIcon(Ressources.getImageIcon("audio-volume-high.png"));
		pack();
	}

	private String toHTML(String pMessage) {
		if (pMessage.indexOf('\n') != -1) {
			pMessage = "<HTML>" + pMessage + "</HTML>";
			pMessage = pMessage.replace("\n", "<BR>");
		}
		return pMessage;
	}

	public void initialiserFenetre(String pMessage, Role role) {
		login = null;
		setType(role);
		if (role == Role.TEXTE) {
			Object[] tableau = { pMessage, boite, toucheEntree };
			fenetre.setMessage(tableau);
		} else {
			Object[] tableau = { pMessage, champNombre };
			fenetre.setMessage(tableau);
		}
		fenetre.setIcon(Ressources.getImageIcon("audio-input-microphone.png"));
		pack();
	}

	public void initialiserFenetre(Role role) {
		login = null;
		setType(role);
		if (role == Role.TEXTE) {
			Object[] tableau = { message, boite, toucheEntree };
			fenetre.setMessage(tableau);
		} else {
			Object[] tableau = { message, champNombre };
			fenetre.setMessage(tableau);
		}
		fenetre.setIcon(Ressources.getImageIcon("audio-input-microphone.png"));
		pack();
	}

	private boolean check() {
		String s = champNombre.getText();
		boolean retour = false;
		if (s == null || (type == Role.NOMBRE && !checkNumber(s))) {
			fenetre.setOptions(options_off);
			//http://www.devx.com/tips/Tip/13718
			addMnemonicsToButtons(fenetre, options_on, mnemonic);
		} else {
			fenetre.setOptions(options_on);
			//http://www.devx.com/tips/Tip/13718
			addMnemonicsToButtons(fenetre, options_on, mnemonic);
			retour = true;
		}

		champNombre.requestFocusInWindow();
		return retour;
	}

	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();

		if (isVisible() && (e.getSource() == fenetre) && (JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
			Object value = fenetre.getValue();

			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				return;
			}

			fenetre.setValue(JOptionPane.UNINITIALIZED_VALUE);

			if (options_on[0].equals(value)) {
				login = getCurrentComponent().getText();
				if (check())
					quit();
			} else
				getCurrentComponent().requestFocusInWindow();
		}
	}

	private void quit() {
		setVisible(false);
	}

	public String retourneValeur() {
		return login;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			check();
			getCurrentComponent().setText("");
			if (type == Role.NOMBRE) {
				fenetre.setOptions(options_off);
				//http://www.devx.com/tips/Tip/13718
				addMnemonicsToButtons(fenetre, options_on, mnemonic);
			} else {
				fenetre.setOptions(options_on);
				//http://www.devx.com/tips/Tip/13718
				addMnemonicsToButtons(fenetre, options_on, mnemonic);
			}
			this.setLocationRelativeTo(frame);
		}
		super.setVisible(visible);
	}

	public boolean checkNumber(String message) {
		try {
			new BigDecimal(message);
		} catch (NumberFormatException e1) {
			return false;
		}
		return true;
	}

	private void setType(Role i) {
		type = i;
	}

	public void windowClosing(WindowEvent arg0) {
		//TODO Alizé
		//Atelier.linotte.getMoteurXMLRRacine().setStopLecture(true);
		// On confirme la fermeture : http://programmons.forumofficiel.fr/t1174-2-bogues-lorsque-le-programme-est-lance-avec-exe
		int result = JOptionPane.showConfirmDialog(this, "Voulez-vous arrêter votre programmer ?", "Quitter", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION)
			quit();
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	private JTextComponent getCurrentComponent() {
		if (type == Role.NOMBRE)
			return champNombre;
		else
			return champTexte;
	}

	public static void addMnemonicsToButtons(Component parentComponent, String[] text, int[] mnemonic) {
		Component[] component = getAllSubComponents(parentComponent);
		int count = component.length;
		for (int i = 0; i < count; i++) {
			if (component[i] instanceof AbstractButton) {
				AbstractButton b = (AbstractButton) component[i];
				String btnText = b.getText();
				if (btnText == null) {
					btnText = "";
				}
				int textCount = text.length;
				for (int j = 0; j < textCount; j++) {
					if (btnText.equals(text[j])) {
						b.setMnemonic(mnemonic[j]);
					}
				}
			}
		}
	}

	public static Component[] getAllSubComponents(Component c) {
		List<Component> v = new ArrayList<Component>();
		addSubComponentsToVector(c, v);
		int count = v.size();
		Component[] children = new Component[count];
		for (int i = 0; i < count; i++) {
			children[i] = (Component) v.get(i);
		}
		v.clear();
		return children;
	}

	private static void addSubComponentsToVector(Component c, List<Component> v) {
		if (c == null) {
			return;
		}

		if (c instanceof Container) {
			Component[] children = ((Container) c).getComponents();
			for (int i = 0; i < children.length; i++) {
				v.add(children[i]);
				if (children[i] instanceof Container) {
					addSubComponentsToVector(children[i], v);
				}
			}
		}
	}

}
