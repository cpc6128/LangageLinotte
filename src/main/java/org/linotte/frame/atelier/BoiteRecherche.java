/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 8, 2008                            *
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

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.linotte.frame.Atelier;

/**
* The class provides a means to find words in a file
* and to replace words in the main text area
* the class is two classes in one.
* A parameter boolean is passed to the constructor
* 'true' builds a Replace Frame / 'false' builds a Find Frame
* Copyright: Copyright (c) 2004 <br>
* 
* http://www.thatsjava.com/java-swing/11160/
*/

/**
 * Affichage de la boite de recherche
 *
 */

@SuppressWarnings("serial")
public class BoiteRecherche extends JDialog implements ActionListener {

	Atelier atelier;

	/**
	* textfields hold String user input
	*/

	private JTextField textfield;

	/**
	* JCheckBox cbCase, if true the search is case sensitive
	* JCheckBox cbWhole, if true the search is for the whole word only
	* @see #caseNotSelected()
	* @see #wholeWordIsSelected()

	*/

	private JCheckBox cbCase, cbWhole;

	private JButton suivant = new JButton("Suivant");

	/**
	* (boolean) JRadioButton down is the search from the start of the text toward the bottom
	* @see #isSearchDown()
	*/

	private JRadioButton down;

	/**
	* Constructor is a sharedInstance() of the MainEditor.class.
	* @param java.awt.Frame
	* @param boolean if true the class is the Replace frame /else the Find frame
	*/

	public BoiteRecherche(Atelier atelier) {

		super(atelier);
		this.atelier = atelier;
		setTitle("Rechercher");
		JPanel north = new JPanel();
		JPanel center = new JPanel();
		JPanel south = new JPanel();
		setFindPanel(north);
		setPanelCenter(center);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				dispose();
			}
		});

		getContentPane().add(north, BorderLayout.NORTH);
		getContentPane().add(center, BorderLayout.CENTER);
		getContentPane().add(south, BorderLayout.SOUTH);

		pack();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		int x = (atelier.getWidth() * 3 / 5) - (getWidth() / 2);
		int y = (atelier.getHeight() / 2) - (getHeight() / 2);
		setLocation(x, y); // 3/5ths accross + centred
		setVisible(false);

	}

	/**
	* setFindPanel is part of the main class constructor
	* for the construction of a Find Frame
	* This north panel uses a default layout manager
	* @param north javax.swing.JPanel
	*/

	private void setFindPanel(JPanel north) {

		suivant.setMnemonic('S');
		suivant.addActionListener(this);
		suivant.setEnabled(false);
		textfield = new JTextField(atelier.getCahierCourant().getEditorPanelCahier().getSelectedText(), 10);
		boolean state = (textfield.getDocument().getLength() > 0);
		suivant.setEnabled(state);

		textfield.addActionListener(this);
		textfield.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent ke) {
				boolean state = (textfield.getDocument().getLength() > 0);
				suivant.setEnabled(state);
			}

		});

		if (textfield.getText().length() > 0)
			suivant.setEnabled(true);

		north.add(new JLabel("Mot trouvé :"));
		north.add(textfield);
		north.add(suivant);

	}

	/**
	* setPanelCenter is part of the main class constructor
	* the construction is the same for both Replace Frame or Find Frame.
	* This center panel uses a simple GridLayout manager
	* @param center javax.swing.JPanel
	*/

	private void setPanelCenter(JPanel center) {

		JPanel east = new JPanel();
		JPanel west = new JPanel();
		center.setLayout(new GridLayout(1, 2));
		east.setLayout(new GridLayout(2, 1));
		west.setLayout(new GridLayout(2, 1));
		cbCase = new JCheckBox("Respecter la casse", false);
		cbWhole = new JCheckBox("Mot entier", false);
		ButtonGroup group = new ButtonGroup();
		JRadioButton up = new JRadioButton("Vers le haut", false);
		down = new JRadioButton("Vers le bas", true);
		cbCase.setMnemonic('C');
		cbWhole.setMnemonic('M');
		up.setMnemonic('H');
		down.setMnemonic('B');
		group.add(up);
		group.add(down);
		east.add(cbCase);
		east.add(cbWhole);
		east.setBorder(BorderFactory.createTitledBorder("Options:"));
		west.add(up);
		west.add(down);
		west.setBorder(BorderFactory.createTitledBorder("Direction:"));
		center.add(east);
		center.add(west);

	}

	/**
	* process gathers information to begin the text search the word or replace the words
	* which is handled by the class variable isReplace. When the search is ended int returns -1<br>
	* getWord() gets the word to be searched for: returns String <br>
	* getAllText() gets all the text to be searched: returns String <br>
	 * @param b 
	* @see #getWord()
	* @see #getAllText()
	* @see #search(String, String, int)
	* @see #endResultMessage(boolean, int)
	*/

	private void process(boolean b) {
		int caret = atelier.getCahierCourant().getEditorPanelCahier().getCaretPosition();
		String word = getWord();
		String text = getAllText();
		caret = search(text, word, caret, b);

	}

	/**
	* search searches through the text from the requested caret position
	* down or up in a for loop
	* @param String : text in the text area
	* @param String : word being searched for
	* @param int : the text area caret postion
	* @param follow : donner le focus au cahier si vrai 
	* @return int - the caret position if the word is found
	* @see #isSearchDown()
	* @see #wholeWordIsSelected()
	* @see #checkForWholeWord(int, String, int, int)
	*/

	private int search(String text, String word, int caret, boolean follow) {

		boolean found = false;

		int all = text.length();

		int check = word.length();

		if (isSearchDown()) {

			int add = 0;

			for (int i = caret + 1; i < (all - check); i++) {

				add++;

				String temp = text.substring(i, (i + check));

				if (temp.equals(word)) {

					if (wholeWordIsSelected()) {

						if (checkForWholeWord(check, text, add, caret)) {

							caret = i;

							found = true;

							break;

						}

					}

					else { // Not whole word

						caret = i;

						found = true;

						break;

					}

				} // temp=word

			}// for

		}// end if

		else {// else the search is up

			int add = caret;

			for (int i = caret - 1; i >= check; i--) {

				add--;

				String temp = text.substring((i - check), i);

				if (temp.equals(word)) {

					if (wholeWordIsSelected()) {

						if (checkForWholeWord(check, text, add, caret)) {

							caret = i;

							found = true;

							break;

						}

					}

					else { // Not whole word

						caret = i;

						found = true;

						break;

					}

				} // temp=word

			}// for

			atelier.getCahierCourant().getEditorPanelCahier().setCaretPosition(0);

		}// end else

		if (found) {

			if (follow)
				atelier.getCahierCourant().getEditorPanelCahier().requestFocus();

			if (isSearchDown())
				atelier.getCahierCourant().getEditorPanelCahier().select(caret, caret + check);

			else
				atelier.getCahierCourant().getEditorPanelCahier().select(caret - check, caret);

			return caret;

		}

		return -1;

	}

	/**
	* checkForWholeWord returns true if the character to the left and right of the seached word
	* is NOT a letter or a digit (number)
	* @param int check: place of the caret in the current search
	* @param String text: the text area text
	* @param int add: the place in the for loop
	* @param int caret: the last caret postion checked
	* @return boolean: true if it is a whole word
	*/

	private boolean checkForWholeWord(int check, String text, int add, int caret) {
		int offsetLeft = (caret + add) - 1;
		int offsetRight = (caret + add) + check;
		if ((offsetLeft < 0) || (offsetRight > text.length()))
			return true;
		return ((!Character.isLetterOrDigit(text.charAt(offsetLeft))) && (!Character.isLetterOrDigit(text.charAt(offsetRight))));

	}

	/**
	* getWord gets the word to be searched or replaced from the text field <br>
	* caseNotSelected() sets the word to {@see java.lang.String.toLowerCase()} lower case
	* @return String (search word)
	* @see #caseNotSelected()
	*/

	private String getWord() {
		if (caseNotSelected())
			return textfield.getText().toLowerCase();
		return textfield.getText();

	}

	/**

	* getAllText gets the all the text to be searched through from the main editor text area <br>

	* caseNotSelected() sets all text to {@see java.lang.String.toLowerCase()} lower case

	* if the search is not case sensitive

	* @return String (whole text document)

	* @see #caseNotSelected()

	*/

	private String getAllText() {
		if (caseNotSelected())
			return atelier.getCahierCourant().retourneTexteCahier().toLowerCase();
		return atelier.getCahierCourant().retourneTexteCahier();
	}

	/**

	* caseNotSelected returns the value of the radio button cbCase

	* @return true if the search is case sensitive

	*/

	private boolean caseNotSelected() {
		return !cbCase.isSelected();
	}

	/**
	* wholeWordIsSelected returns the value of the radio button cbWhole
	* @return true if the search is for a whole word
	*/

	private boolean wholeWordIsSelected() {
		return cbWhole.isSelected();
	}

	/**
	* wholeWordIsSelected() returns the value of the radio button down
	* @return true if the search is down
	*/

	private boolean isSearchDown() {
		return down.isSelected();
	}

	/**
	* actionPerformed(ActionEvent) handles actions from the buttons
	* @param java.awt.event.ActionEvent
	* @see #process()
	* @see #replaceAll()

	*/

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(textfield) || e.getActionCommand().equals("Suivant"))
			validate();
		process(e.getActionCommand().equals("Suivant"));
	}

	@Override
	public void setVisible(boolean b) {
		if (b) {
			try {
				textfield.setText(atelier.getCahierCourant().getEditorPanelCahier().getSelectedText());
				boolean state = (textfield.getDocument().getLength() > 0);
				suivant.setEnabled(state);
			} catch (NoSuchFieldError nsf) {
			}
		}
		super.setVisible(b);
	}
}
