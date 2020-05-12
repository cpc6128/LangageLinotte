package org.linotte.frame.projet;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTree;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.linotte.frame.atelier.Atelier;
import org.linotte.frame.latoile.Java6;
import org.linotte.frame.outils.Tools;
import org.linotte.moteur.outils.Ressources;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Kirill Grouchnikov
 */
@SuppressWarnings("serial")
public class NavigateurFichier extends JPanel {
	/**
	 * File system view.
	 */
	protected FileSystemView fsv = null;
	protected File racine = null;
	protected JXTaskPane task;
	protected FileTreeNode rootTreeNode;
	protected JPopupMenu m_popup;
	protected FileTreeNode clickedFile;

	/**
	 * Renderer for the file tree.
	 * 
	 * @author Kirill Grouchnikov
	 */
	private class FileTreeCellRenderer extends DefaultTreeCellRenderer {
		/**
		 * Icon cache to speed the rendering.
		 */
		private Map<String, Icon> iconCache = new HashMap<String, Icon>();

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent
		 * (javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int,
		 * boolean)
		 */
		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
			FileTreeNode ftn = (FileTreeNode) value;
			File file = ftn.getFile();
			String filename = "";
			if (file != null) {
				filename = file.getName();
			}
			JLabel result = (JLabel) super.getTreeCellRendererComponent(tree, filename, sel, expanded, leaf, row, hasFocus);
			if (file != null) {
				String ext = getExtension(filename);
				Icon icon = this.iconCache.get(ext);
				if (icon == null) {
					icon = fsv.getSystemIcon(file);
					this.iconCache.put(ext, icon);
				}
				result.setIcon(icon);
			}
			task.revalidate();
			return result;
		}
	}

	/**
	 * A node in the file tree.
	 * 
	 * @author Kirill Grouchnikov
	 */
	public static class FileTreeNode implements TreeNode {
		/**
		 * Node file.
		 */
		private File file;

		/**
		 * Children of the node file.
		 */
		private File[] children;

		/**
		 * Parent node.
		 */
		private TreeNode parent;

		/**
		 * Creates a new file tree node.
		 * 
		 * @param file
		 *            Node file
		 * @param isFileSystemRoot
		 *            Indicates whether the file is a file system root.
		 * @param parent
		 *            Parent node.
		 */
		public FileTreeNode(File file, TreeNode parent) {
			this.setFile(file);
			this.parent = parent;
			recharger();
		}

		/**
		 * 
		 */
		public void recharger() {
			if (getFile() != null)
				this.children = this.getFile().listFiles();
			if (this.children == null)
				this.children = new File[0];
			Arrays.sort(this.children);
		}

		/**
		 * Creates a new file tree node.
		 * 
		 * @param children
		 *            Children files.
		 */
		public FileTreeNode(File[] children, File pere) {
			this.setFile(pere);
			this.parent = null;
			this.children = children;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeNode#children()
		 */
		public Enumeration children() {
			final int elementCount = this.children.length;
			return new Enumeration<File>() {
				int count = 0;

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.util.Enumeration#hasMoreElements()
				 */
				public boolean hasMoreElements() {
					return this.count < elementCount;
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.util.Enumeration#nextElement()
				 */
				public File nextElement() {
					if (this.count < elementCount) {
						return FileTreeNode.this.children[this.count++];
					}
					throw new NoSuchElementException("Vector Enumeration");
				}
			};

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeNode#getAllowsChildren()
		 */
		public boolean getAllowsChildren() {
			return true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeNode#getChildAt(int)
		 */
		public TreeNode getChildAt(int childIndex) {
			return new FileTreeNode(this.children[childIndex], this);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeNode#getChildCount()
		 */
		public int getChildCount() {
			return this.children.length;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeNode#getIndex(javax.swing.tree.TreeNode)
		 */
		public int getIndex(TreeNode node) {
			FileTreeNode ftn = (FileTreeNode) node;
			for (int i = 0; i < this.children.length; i++) {
				if (ftn.getFile().equals(this.children[i]))
					return i;
			}
			return -1;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeNode#getParent()
		 */
		public TreeNode getParent() {
			return this.parent;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.tree.TreeNode#isLeaf()
		 */
		public boolean isLeaf() {
			return (this.getChildCount() == 0);
		}

		public void setFile(File file) {
			this.file = file;
		}

		public File getFile() {
			return file;
		}
	}

	/**
	 * The file tree.
	 */
	private JXTree tree;
	private Action action_supprimer;
	private Action actionRenommer;
	private Action actionRafraichir;

	/**
	 * Creates the file tree panel.
	 * @param ptask 
	 */
	public NavigateurFichier(FileSystemView root, File edt, JXTaskPane ptask, final Atelier atelier, boolean popup) {
		this.setLayout(new BorderLayout());
		fsv = root;
		racine = edt;
		task = ptask;
		File[] roots = edt.listFiles();
		Arrays.sort(roots);
		rootTreeNode = new FileTreeNode(roots, edt);
		tree = new JXTree(rootTreeNode);
		tree.setMinimumSize(new Dimension(50, 20));

		tree.setCellRenderer(new FileTreeCellRenderer());

		tree.setRootVisible(false);
		tree.setToggleClickCount(1);

		tree.setRolloverEnabled(true);
		tree.addHighlighter(new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW, null, Color.RED));

		// Add a popup menu

		m_popup = new JPopupMenu();
		action_supprimer = new AbstractAction("Supprimer") {
			public void actionPerformed(ActionEvent e) {
				tree.repaint();
				int reply = JOptionPane.showConfirmDialog(atelier, "Voulez-vous VRAIMENT supprimer le livre <" + clickedFile.getFile().getName() + "> ?",
						"Supprimer un livre", JOptionPane.WARNING_MESSAGE);
				if (reply == JOptionPane.YES_OPTION) {
					Tools.deleteDirectory(clickedFile.getFile());
					rafraichirArbre();
				}

			}
		};
		m_popup.add(action_supprimer);
		actionRenommer = new AbstractAction("Renommer") {
			public void actionPerformed(ActionEvent e) {
				tree.repaint();
				if (!clickedFile.getFile().isDirectory()) {
					String newName = (String) JOptionPane.showInputDialog(atelier, "Quel nouveau nom voulez-vous donner au livre <"
							+ clickedFile.getFile().getName() + "> ?", "Renommer un livre", JOptionPane.QUESTION_MESSAGE, null, null, clickedFile.getFile()
							.getName());
					if (newName != null && newName.trim().length() > 0) {
						newName = Ressources.sanitizeFilename(newName);
						try {
							File old = new File(clickedFile.getFile().toURI());
							File newFile;
							if (old.getParentFile() != null) {
								newFile = new File(old.getParentFile(), newName);
							} else
								newFile = new File(newName);
							if (newFile.exists()) {
								JOptionPane
										.showMessageDialog(atelier, "Ce livre existe déjà !", "Impossible de renommer le livre !", JOptionPane.ERROR_MESSAGE);
								return;
							}

							if (clickedFile.getFile().renameTo(newFile)) {
								atelier.renommerLivre(old, newFile);
								rafraichirArbre();
							}
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(atelier, "Erreur : " + e1.getLocalizedMessage(), "Impossible de renommer le livre !",
									JOptionPane.ERROR_MESSAGE);
							e1.printStackTrace();
						}
					}
				}
			}
		};

		m_popup.add(actionRenommer);
		m_popup.addSeparator();
		actionRafraichir = new AbstractAction("Rafraichir") {
			public void actionPerformed(ActionEvent e) {
				rafraichirArbre();
			}
		};

		m_popup.add(actionRafraichir);
		if (popup) {
			tree.add(m_popup);
			tree.addMouseListener(new PopupTrigger());
		}

		// Add a listener

		tree.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					TreePath path = tree.getSelectionPath();
					if (path != null) {
						FileTreeNode node = (FileTreeNode) path.getLastPathComponent();
						if (node.isLeaf()) {
							try {
								String nom = node.getFile().getName().toLowerCase();
								if (nom.endsWith(".liv") || nom.endsWith(".wliv"))
									atelier.ouvrirLivre(node.getFile());
								else {
									if (!node.getFile().isDirectory())
										Java6.getDesktop().browse(node.getFile().toURI());
								}
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						} else {
						}
					}
				}
			}
		});

		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {

			}
		});

		this.add(tree, BorderLayout.CENTER);
	}

	protected JXTree getTree() {
		return tree;
	}

	protected File getRacine() {
		return racine;
	}

	/**
	 * 
	 */
	public void rafraichirArbre() {
		NavigateurFichier.this.rootTreeNode.recharger();
		((DefaultTreeModel) NavigateurFichier.this.getTree().getModel()).reload(NavigateurFichier.this.rootTreeNode);
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
				TreePath path = tree.getPathForLocation(x, y);
				if (path != null) {
					m_popup.show(tree, x, y);
					clickedFile = (FileTreeNode) path.getLastPathComponent();
					if (clickedFile.getFile().isDirectory()) {
						action_supprimer.setEnabled(false);
						actionRenommer.setEnabled(false);
						actionRafraichir.setEnabled(true);
					} else {
						action_supprimer.setEnabled(true);
						actionRenommer.setEnabled(true);
						actionRafraichir.setEnabled(true);
					}
				}
			}
		}
	}

	public static String getExtension(String name) {
		if (name.lastIndexOf(".") == -1) {
			return name;
		} else {
			int index = name.lastIndexOf(".");
			return name.substring(index + 1, name.length());
		}
	}
}
