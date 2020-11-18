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

import org.linotte.frame.cahier.Cahier.EtatCachier;
import org.linotte.frame.moteur.FrameProcess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Component to be used as tabComponent; Contains a JLabel to show the text and
 * a JButton to close the tab it belongs to
 */
@SuppressWarnings("serial")
public class Onglet extends JPanel implements MouseListener {


	private final JTabbedPane tabbedPan;
	private final Cahier cahier;
	private Color backupColor = null;
	// Doit être accessible depuis la classe @Cahier

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
	}

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
			SwingUtilities.invokeLater(() -> {
				tabbedPan.setTitleAt(index, cahier.getFichier().getName() + (modifie ? " *" : ""));
				revalidate();
				repaint();
			});
		}
	}

	public void setCouleur(final Color color, final boolean force) {
		int i = tabbedPan.indexOfTabComponent(this);
		if (i != -1 && cahier.getFichier() != null) {
			final int index = i;
			SwingUtilities.invokeLater(() -> {
				if (tabbedPan.getBackgroundAt(index) != FrameProcess.RUN || force) {
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