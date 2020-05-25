/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

package org.linotte.frame.cahier.sommaire;

import org.linotte.frame.cahier.Cahier;
import org.linotte.moteur.outils.Ressources;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.List;
import java.util.*;

@SuppressWarnings("serial")
public class JPanelSommaire extends JPanel implements TreeSelectionListener {

	private DefaultMutableTreeNode rootNode;
	public DefaultTreeModel treeModel;
	public JTree tree;
	private List<Noeud> noeuds = new ArrayList<Noeud>();
	private Map<String, Noeud> paragraphes = new HashMap<String, Noeud>();
	private Cahier cahier;
	private String titre = "Livre";
	private Noeud last = null;
	private Set<String> prototypes = new HashSet<String>();
	public JScrollPane scrollPane;

	public JPanelSommaire(Cahier gt2l) {
		super(new GridLayout(1, 0));
		cahier = gt2l;
		rootNode = new DefaultMutableTreeNode("Livre");
		treeModel = new DefaultTreeModel(rootNode);
		tree = new JTree(treeModel);
		tree.setMinimumSize(new java.awt.Dimension(50, 290));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(false);
		tree.setRootVisible(false);
		tree.setToggleClickCount(0);
		tree.expandRow(0);
		tree.addTreeSelectionListener(this);
		NoeudTreeCellRenderer renderer = new NoeudTreeCellRenderer();
		renderer.setLeafIcon(Ressources.getImageIconeTeintee(Color.GRAY.darker(), "list-remove.png"));
		renderer.setErreurIcon(Ressources.getImageIconeTeintee(Color.RED, "list-remove.png"));
		tree.setCellRenderer(renderer);
		scrollPane = new JScrollPane(tree);
		add(scrollPane);
		tree.setToolTipText("<html><b>Sommaire</b><br>" + "<i>Description</i> : Liste les paragraphes du livre<br>" + "</html>");
	}

	public void vider() {
		noeuds.clear();
		rootNode.removeAllChildren();
		treeModel.reload();
		prototypes.clear();
	}

	public void viderNoeuds() {
		noeuds.clear();
	}

	/**
	 * On affiche seulement Ã  la fin.
	 */
	public void afficher() {
		rootNode.removeAllChildren();
		rootNode.setUserObject(titre);

		Map<String, DefaultMutableTreeNode> map = new HashMap<String, DefaultMutableTreeNode>();

		for (String proto : prototypes) {
			map.put(proto, addObject(null, proto));
		}

		for (Noeud noeud : noeuds) {
			addObject(noeud.getPrototype() != null ? map.get(noeud.getPrototype()) : null, noeud);
		}
		treeModel.reload();
		// noeuds.clear();
		expandAll(tree);
	}

	public void ajouterParagraphe(String p, int position) {
		last = new Noeud(null, p, position);
		noeuds.add(last);
		paragraphes.put(p.toLowerCase(), last);
	}

	public void ajouterParagraphe(String prototype, String nomCourt, String visible, int position) {
		last = new Noeud(prototype, nomCourt, position);
		noeuds.add(last);
		paragraphes.put(nomCourt.toLowerCase(), last);
		prototypes.add(prototype);
	}

	public void addDoublure(String p) {
		if (last != null)
			last.addDoublure(p);
	}

	public void setTitre(String p) {
		titre = p;
	}

	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Object child) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

		if (parent == null) {
			parent = rootNode;
		}
		treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

		return childNode;
	}

	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();
		if (nodeInfo instanceof Noeud) {
			Noeud noeud = (Noeud) nodeInfo;
			try {
				cahier.setPositionCahier(noeud.getPosition());
				// Tous les autres sont non actif :
				for (Noeud temp : noeuds) {
					temp.setActif(false);
				}
				noeud.setActif(true);
			} catch (BadLocationException e1) {
			}
		}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					repaint();
				} catch (Exception ble) {
					ble.fillInStackTrace();
				}
			}
		});
	}

	public void forceParagraphe(String paragraphe) {
		try {
			cahier.setPositionCahier(paragraphes.get(paragraphe.toLowerCase()).getPosition());
		} catch (Exception e1) {
		}
	}

	public boolean isParagraphe(String paragraphe) {
		return paragraphes.get(paragraphe.toLowerCase()) != null;
	}

	public void expandAll(JTree tree) {
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}

	public void setErreurSyntaxe() {
		if (last != null)
			last.setErreurSyntaxe(true);
	}

	private class NoeudTreeCellRenderer extends SommaireTreeCellRenderer {

		Icon icone;

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			if (value instanceof DefaultMutableTreeNode && ((DefaultMutableTreeNode) value).getUserObject() instanceof Noeud) {

				Noeud noeud = (Noeud) ((DefaultMutableTreeNode) value).getUserObject();
				SommaireTreeCellRenderer label = (SommaireTreeCellRenderer) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
				if (noeud.isErreurSyntaxe()) {
					label.setIcon(icone);
				}
				//label.setForeground(Color.BLACK);
				label.setActif(noeud.isActif());
				return label;
			} else {
				return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			}
		}

		public void setErreurIcon(Icon imageIcon) {
			icone = imageIcon;
		}

	}

	public void afficheFonction(int caretPosition) {
		int index = 0;
		for (Noeud noeud : noeuds) {
			noeud.setActif(false);
			if (index != -1) {
				if ((index + 1) < noeuds.size()) {
					Noeud noeud2 = noeuds.get(index + 1);
					if (caretPosition >= noeud.getPosition() && caretPosition < noeud2.getPosition()) {
						tree.scrollRowToVisible(index);
						noeud.setActif(true);
					}
				} else {
					if (caretPosition >= noeud.getPosition()) {
						tree.scrollRowToVisible(index);
						noeud.setActif(true);
					}
				}
			}
			index++;
		}
	}

}