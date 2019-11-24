package org.linotte.frame.projet;

/***********************************************************************
 * Langage Linotte                                                     *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 3 or       *
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.linotte.frame.Atelier;
import org.linotte.frame.favoris.Favoris;
import org.linotte.frame.favoris.GestionnaireFavoris;

@SuppressWarnings("serial")
public class ExplorateurFavoris extends JPanel {

	private DefaultListModel<Object> model;
	private JXList list;
	private Action action_supprimer;
	private JPopupMenu m_popup;
	private Atelier atelier;
	private JXTaskPane task;

	public ExplorateurFavoris(Atelier patelier, JXTaskPane ptask) {
		super(new BorderLayout());
		atelier = patelier;
		task = ptask;
		model = new DefaultListModel<Object>();
		list = new JXList(model);
		list.setRolloverEnabled(true);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED));
		list.addHighlighter(HighlighterFactory.createSimpleStriping(HighlighterFactory.FLORAL_WHITE));
		add(list);

		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					Object path = list.getSelectedValue();
					if (path != null) {
						if (path instanceof Favoris) {
							final Favoris noeud = (Favoris) path;
							try {
								atelier.ouvrirLivre(new File(noeud.getFichierCible()));
								SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										atelier.getCahierCourant().setCaretPositionAndScroll(noeud.getPositionFichier());
									}
								});
								atelier.getCahierCourant().focus();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
					}
				}
			}
		});

		// Add a popup menu

		m_popup = new JPopupMenu();
		action_supprimer = new AbstractAction("Supprimer") {
			public void actionPerformed(ActionEvent e) {
				list.repaint();
				Object node = list.getSelectedValue();
				if (node instanceof Favoris) {
					Favoris noeud = (Favoris) node;
					GestionnaireFavoris.getInstance().supprimer(noeud);
				}
			}
		};
		m_popup.add(action_supprimer);
		list.add(m_popup);
		list.addMouseListener(new PopupTrigger());
	}

	public void ajouterParagraphe(Favoris p) {
		addObject(p);
		task.revalidate();
	}

	public void supprimer(Favoris p) {
		removeObject();
		task.revalidate();
	}

	public void addObject(Object child) {
		model.addElement(child);
	}

	public void removeObject() {
		model.removeElement(list.getSelectedValue());
	}

	class PopupTrigger extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			checkPopup(e);
		}

		public void mouseClicked(MouseEvent e) {
			checkPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			checkPopup(e);
		}

		/**
		 * @param e
		 */
		public void checkPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				int x = e.getX();
				int y = e.getY();
				int path = list.locationToIndex(e.getPoint());
				if (path > -1) {
					list.setSelectedIndex(path);
					m_popup.show(list, x, y);
				}
			}
		}
	}
}
