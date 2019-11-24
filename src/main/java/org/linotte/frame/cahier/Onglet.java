/*
 * Copyright (c) 1995 - 2008 Sun Microsystems, Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Sun Microsystems nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.linotte.frame.cahier;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicButtonUI;

import org.linotte.frame.Atelier;
import org.linotte.frame.cahier.Cahier.EtatCachier;
import org.linotte.frame.moteur.FrameProcess;

/**
 * Component to be used as tabComponent; Contains a JLabel to show the text and
 * a JButton to close the tab it belongs to
 */
@SuppressWarnings("serial")
public class Onglet extends JPanel implements MouseListener {

	public enum TypeButton {
		CLOSED, RUNNING
	}

	private final JTabbedPane tabbedPan;
	private final Cahier cahier;
	private Color backupColor = null;
	// Doit être accessible depuis la classe @Cahier
	JButton button;
	private JButton buttonRunning;

	public Onglet(final Cahier cahierPanel, final JTabbedPane pane) {
		// unset default FlowLayout' gaps
		super(new FlowLayout(FlowLayout.LEFT, 0, 0));
		this.cahier = cahierPanel;
		cahierPanel.setOnglet(this);
		this.tabbedPan = pane;
		setOpaque(false);

		// make JLabel read titles from JTabbedPane
		JLabel label = new JLabel() {
			@Override
			public String getText() {
				int i = pane.indexOfTabComponent(Onglet.this);
				if (i != -1) {
					return pane.getTitleAt(i);
				}
				return null;
			}
		};

		add(label);
		add(Box.createHorizontalGlue());
		// add more space between the label and the button
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		// tab button
		button = new TabButton(TypeButton.CLOSED);
		buttonRunning = new TabButton(TypeButton.RUNNING);
		buttonRunning.setVisible(false);
		add(button);
		add(buttonRunning);
		// add more space to the top of the component
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		// Ajout des évènements :
		addMouseListener(this);

	}

	private class TabButton extends JButton implements ActionListener {

		private TypeButton type;

		public TabButton(TypeButton ptype) {
			int size = 17;
			type = ptype;
			setPreferredSize(new Dimension(size, size));
			if (type == TypeButton.CLOSED) {
				setToolTipText("Fermer ce livre");
			} else {
				setToolTipText("Livre en cours d'exécution...");
			}
			// Make the button looks the same for all Laf's
			setUI(new BasicButtonUI());
			// Make it transparent
			setContentAreaFilled(false);
			// No need to be focusable
			setFocusable(false);
			if (type == TypeButton.CLOSED) {
				setBorder(BorderFactory.createEtchedBorder());
				setBorderPainted(false);
				addMouseListener(buttonMouseListener);
				// Making nice rollover effect
				// we use the same listener for all buttons
				setRolloverEnabled(true);
				// Close the proper tab by clicking the button
				addActionListener(this);

			}
		}

		public void actionPerformed(ActionEvent e) {
			int i = tabbedPan.indexOfTabComponent(Onglet.this);
			if (i != -1) {
				// Vérification :
				boolean doit = true;
				if (cahier.getEtatCahier() == EtatCachier.MODIFIE) {
					doit = false;
					int result = JOptionPane.showConfirmDialog(cahier.getAtelier(), "Voulez-vous vraiment fermer ce livre ?",
							"Un livre n'est pas sauvegardé !", JOptionPane.YES_NO_OPTION);
					if (result == JOptionPane.YES_OPTION) {
						doit = true;
					}
				}
				if (doit) {
					tabbedPan.remove(i);
					if (tabbedPan.getTabCount() == 0) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								cahier.getAtelier();
								Atelier.explorateur.setVisible(true);
								cahier.getAtelier().tabbedPaneNavigateur.setVisible(true);
								// atelier.splitPaneSommaire.getRightComponent().setVisible(true);
								cahier.getAtelier().splitPaneProjet.setDividerLocation(cahier.getAtelier().taille_split_projet);
							}
						});

					}
				}
			}
		}

		// we don't want to update UI for this button
		@Override
		public void updateUI() {
		}

		// paint the cross
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			// shift the image for pressed buttons
			if (type == TypeButton.CLOSED) {
				if (getModel().isPressed()) {
					g2.translate(1, 1);
				}
				g2.setStroke(new BasicStroke(2));
				g2.setColor(Color.BLACK);
				if (getModel().isRollover()) {
					g2.setColor(Color.MAGENTA);
				}
				int delta = 6;
				g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
				g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
			} else {
				g2.setColor(Color.RED);
				int delta = 2;
				g2.fillOval(delta, delta, 10, 10);
				g2.drawOval(delta - 1, delta - 1, 12, 12);
			}
			g2.dispose();
		}
	}

	private final static MouseListener buttonMouseListener = new MouseAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(true);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			Component component = e.getComponent();
			if (component instanceof AbstractButton) {
				AbstractButton button = (AbstractButton) component;
				button.setBorderPainted(false);
			}
		}
	};

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		cahier.getAtelier().setCahierCourant(cahier);
		int i = tabbedPan.indexOfTabComponent(this);
		if (i != -1) {
			tabbedPan.setSelectedIndex(i);
			cahier.changeAtelierEtat();
		}
	}

	public void setTitre() {
		final boolean modifie = cahier.getEtatCahier() == EtatCachier.MODIFIE;
		int i = tabbedPan.indexOfTabComponent(this);
		if (i != -1 && cahier.getFichier() != null) {
			final int index = i;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					tabbedPan.setTitleAt(index, cahier.getFichier().getName() + (modifie ? " *" : ""));
					((Onglet) tabbedPan.getTabComponentAt(index)).button.setToolTipText(cahier.getFichier().getAbsolutePath());
					// tabbedPan.setToolTipTextAt(index,
					// cahier.getFichier().getAbsolutePath());
					revalidate();
					repaint();
				}
			});
		}
	}

	public void setCouleur(final Color color, final boolean force) {
		int i = tabbedPan.indexOfTabComponent(this);
		if (i != -1 && cahier.getFichier() != null) {
			final int index = i;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (tabbedPan.getBackgroundAt(index) != FrameProcess.RUN || force) {
						button.setVisible(color != FrameProcess.RUN);
						buttonRunning.setVisible(color == FrameProcess.RUN);
						if (color != null) {
							if (backupColor == null)
								backupColor = tabbedPan.getBackgroundAt(index);
							tabbedPan.setBackgroundAt(index, color);
							revalidate();
							repaint();
						} else {
							if (backupColor != null) {
								tabbedPan.setBackgroundAt(index, backupColor);
								revalidate();
								repaint();
							}
						}
					}
				}
			});
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	public Cahier getCahierPanel() {
		return cahier;
	}
}