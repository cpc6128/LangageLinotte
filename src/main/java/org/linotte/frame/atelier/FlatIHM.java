/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 13, 2015                                *
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.linotte.frame.Atelier;
import org.linotte.frame.moteur.FrameProcess;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.StopException;

/**
 * Implémentation de l'interface IHM en mode FLAT : fin de la popup !
 */
public class FlatIHM extends FrameIHM {

	public FrameIHM instanceCompatibilitee;
	
	class Container {
		String message = null;
	}

	public FlatIHM(Atelier p) {
		super(p);
		instanceCompatibilitee = new FrameIHM(p);
	}

	@Override
	public String demander(final Role type, final String acteur) throws StopException {
		return demander(type, acteur, null);
	}

	public String demander(final Role type, final String acteur, final String question) throws StopException {

		final Container container = new Container();

		@SuppressWarnings("serial")
		JTextField input = new JTextField(30) {
			// http://stackoverflow.com/questions/6478394/how-to-set-focus-on-specific-jtextfield-inside-joptionpane-when-created
			public void addNotify() {
				super.addNotify();
				requestFocus();
			}
		};
		input.setMaximumSize(new Dimension(400, 30));
		input.setPreferredSize(new Dimension(400, 30));
		
		BasicStroke dotted = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {1,2}, 0);
		input.setBorder(BorderFactory.createStrokeBorder(dotted, Color.RED));

		input.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
			}

			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (type == Role.NOMBRE && ((c < 48 || c > 57) && (c != 45 && c != 43 && c != '.'))) {
					// Si c'est un nombre, on n'autorise que les chiffres ...
					e.consume();
				} else {
					super.keyTyped(e);
				}
			}
		});

		input.setPreferredSize(new Dimension(200, 25));

		input.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (container) {
					container.notify();
				}
			}
		});

		JButton bouton = new JButton("valider");
		bouton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (container) {
					container.notify();
				}
			}
		});

		JButton plus = new JButton("...");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (container) {
					try {
						String m;
						if (question == null) {
							m = FlatIHM.super.demander(type, acteur);
						} else {
							m = FlatIHM.super.questionne(question, type, acteur);
						}
						container.message = m;
					} catch (StopException e1) {
					}
					container.notify();
				}
			}
		});

		getTableauBatch().ecrireTableau(input);
		getTableauBatch().ecrireTableau(plus);
		getTableauBatch().ecrireTableau(new JLabel("     "));
		getTableauBatch().ecrireTableau(bouton);
		JLabel fin = new JLabel("     ");
		getTableauBatch().ecrireTableau(fin);

		String message = null;

		synchronized (container) {
			try {
				initInput(input, container);
				if (container.message != null) {
					message = container.message;
					input.setText(message);
				} else {
					message = input.getText();
				}
				if (message.trim().isEmpty() && type == Role.NOMBRE) {
					message = "0";
				}
			} catch (InterruptedException e1) {
			}
		}
		if (message == null)
			FrameProcess.stopProcess();
		effaceComposants(input, bouton, plus);
		return message;
	}

	private void initInput(JTextField input, Container container) throws InterruptedException {
		container.wait();
	}

	@Override
	public String questionne(String question, Role type, String acteur) throws StopException {
		if (applet != null) {
			getTableauBatch().ecrireTableauEnLigne(question);
			getTableauBatch().ecrireTableau(new JLabel("     "));
		}
		return demander(type, acteur, question);
	}

	private void effaceComposants(JTextField input, JButton bouton, JButton plus) {
		input.setBorder(BorderFactory.createEmptyBorder());
		input.setEnabled(false);
		bouton.setEnabled(false);
		bouton.setVisible(false);
		plus.setEnabled(false);
		plus.setVisible(false);
		getTableauBatch().ecrireTableau("");
	}

	@Override
	public boolean afficher(String afficher, Role type) throws StopException {
		if (applet != null)
			getTableauBatch().ecrireTableau(afficher);
		return true;
	}

	@Override
	public void close() {
	}

}
