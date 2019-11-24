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

package org.linotte.frame.atelier;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.alize.kernel.AKPatrol;
import org.linotte.frame.Atelier;
import org.linotte.frame.latoile.LaToile;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.KernelStack.LMap;
import org.linotte.moteur.xml.alize.kernel.KernelStack.NotVisible;
import org.linotte.moteur.xml.appels.Appel;
import org.linotte.moteur.xml.appels.CalqueParagraphe;
import org.linotte.moteur.xml.appels.SousParagraphe;

@SuppressWarnings("serial")
public class Inspecteur extends JDialog {

	private static DefaultMutableTreeNode rootNode;
	private static DefaultTreeModel treeModel;
	private static JTree tree;
	private static JButton buttonVider;
	private static JButton buttonEffacer;
	private static JButton button;
	private static JLabel buttonDebogage;
	public boolean enAttente = false;
	private Atelier atelier;

	public Inspecteur(final Atelier patelier) {
		super(patelier, ModalityType.MODELESS);
		atelier = patelier;
		// System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		setTitle("Inspecteur");
		setSize(new java.awt.Dimension(400, 500));
		setMinimumSize(new java.awt.Dimension(400, 500));
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		rootNode = new DefaultMutableTreeNode("Inspecteur");
		treeModel = new DefaultTreeModel(rootNode);
		// tree = new JTree(treeModel);
		// http://www.java2s.com/Tutorial/Java/0240__Swing/PreventingExpansionorCollapseofaNodeinaJTreeoverrideJTreesetExpandedState.htm
		tree = new JTree(treeModel) {
			protected void setExpandedState(TreePath path, boolean state) {

				if (state) {
					super.setExpandedState(path, state);
				}
			}
		};
		tree.setSize(new java.awt.Dimension(400, 500));
		tree.setMinimumSize(new java.awt.Dimension(400, 500));
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setShowsRootHandles(false);
		tree.setRootVisible(false);
		DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
		renderer.setLeafIcon(Ressources.getImageIcon("list-remove.png"));
		tree.setCellRenderer(renderer);
		tree.expandRow(0);
		// tree.addTreeSelectionListener(this);
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = tree.getRowForLocation(e.getX(), e.getY());
				if (selRow != -1) {
					if (e.getClickCount() == 1) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

						if (node == null)
							return;

						Object nodeInfo = node.getUserObject();
						if (nodeInfo instanceof Noeud) {
							Noeud noeud = (Noeud) nodeInfo;
							try {
								atelier.getCahierCourant().setPositionCahier(noeud.position);
							} catch (BadLocationException e1) {
							}
						}
					}
				}
			}
		};
		tree.addMouseListener(ml);
		setIconImage(Ressources.getImageIcon("bandeau/utilities-system-monitor.png").getImage());
		JScrollPane scrollPane = new JScrollPane(tree);
		JMenuBar bar = new JMenuBar();
		button = new JButton(Ressources.getImageIcon("view-refresh.png"));
		button.setText("Rafraîchir");
		button.setMnemonic(KeyEvent.VK_R);
		button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e0) {
				refresh(null);
			}
		});
		bar.add(button);

		buttonVider = new JButton(Ressources.getImageIcon("mail-mark-junk.png"));
		buttonVider.setText("Vider la mémoire");
		buttonVider.setMnemonic(KeyEvent.VK_V);
		buttonVider.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e0) {
				vider();
			}
		});
		// bar.add(buttonVider);

		buttonEffacer = new JButton(Ressources.getImageIcon("edit-clear.png"));
		buttonEffacer.setText("Effacer la toile");
		buttonEffacer.setMnemonic(KeyEvent.VK_E);
		buttonEffacer.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e0) {
				effacer();
			}
		});
		// bar.add(buttonEffacer);

		buttonDebogage = new JLabel();
		bar.add(buttonDebogage);

		add(scrollPane);
		setJMenuBar(bar);

		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - getWidth()) - 50);
		int y = (int) ((dimension.getHeight() - getHeight()) / 50);
		setLocation(x, y);

	}

	protected void effacer() {
		for (Object otoile : Atelier.linotte.getLibrairie().getToiles()) {
			((LaToile) otoile).getPanelLaToile().effacer();
		}
	}

	public static void lock(boolean v) {
		buttonVider.setEnabled(v);
		buttonEffacer.setEnabled(v);
		button.setEnabled(true);
		buttonDebogage.setText("");
	}

	private void vider() {
		effacer();
		Atelier.linotte.getLibrairie().vider();
		refresh(null);
	}

	/**
	 * Mise à jour de l'inspecteur en fonction du @JobContext
	 * 
	 * @param jobContext
	 * @throws Exception
	 */
	public void refresh(JobContext jobContext) {

		if (AKPatrol.runtimes.size() > 0) {
			// AKPatrol.runtimes;
			final Job job = (Job) AKPatrol.runtimes.get(0).getJob();
			final JobContext jobContextCopie = (jobContext == null) ? ((JobContext) (job.getContext())) : jobContext;
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						rootNode.removeAllChildren();
						if (jobContextCopie != null && jobContextCopie.getLivre() != null) {
							Map<Chaine, Acteur> acteurs = jobContextCopie.getLivre().getActeurs(); // librairie.getActeurs();
							DefaultMutableTreeNode pere = null;
							DefaultMutableTreeNode courant = null;
							Set<Chaine> clefs = acteurs.keySet();
							List<Chaine> listeClefs = new ArrayList<Chaine>();
							for (Chaine nom : clefs) {
								listeClefs.add(nom);
							}

							// button.setEnabled(false);
							// Blocs imbriqués :
							List<LMap> mapActeurs = jobContextCopie.getLivre().getKernelStack().debogage(true);
							int index = 0;
							for (LMap listeActeurs : mapActeurs) {
								int position = getPosition(listeActeurs.appel);
								if (index == mapActeurs.size() - 1)
									pere = courant = addObject(pere, new Noeud(position, "<html><b>" + getNomFonction(listeActeurs.appel)
											+ "bloc courant</b></html>"));
								else
									pere = addObject(pere, new Noeud(position, "<html>" + getNomFonction(listeActeurs.appel) + "bloc imbriqué"
											+ (listeActeurs instanceof NotVisible ? " (acteurs non visibles depuis le bloc courant)" : "") + "</html>"));
								if (listeActeurs.size() == 0) {
									addObject(pere, new Noeud(position, ""));
								} else {
									for (Chaine clef : listeActeurs.keySet()) {
										try {
											addObject(pere, new Noeud(position, Inspecteur.this.toString(clef.toString(), listeActeurs.get(clef))));
										} catch (ErreurException e) {
										}
									}
								}
								index++;
							}

							pere = addObject(null, new Noeud(0, "<html><b><font color=red>mémoire globale</font></b></html>"));
							Collections.sort(listeClefs, new Comparator<Chaine>() {

								@Override
								public int compare(Chaine o1, Chaine o2) {
									return o1.toString().compareTo(o2.toString());
								}
							});
							for (Chaine nom : listeClefs) {
								Acteur acteur = acteurs.get(nom);
								try {
									addObject(pere, new Noeud(0, Inspecteur.this.toString(null, acteur)));
								} catch (ErreurException e) {
									addObject(pere, new Noeud(0, getRole(acteur) + "(" + acteur.getNom() + ")=" + e.getMessage()));
									e.printStackTrace();
								}
							}

							treeModel.reload();
							if (courant != null) {
								tree.setSelectionPath(new TreePath(courant.getPath()));
							}
							expandAll(tree);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} else {
			try {
				rootNode.removeAllChildren();
				treeModel.reload();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Retourne la position du bloc dans le livre
	 * 
	 * @param appel
	 * @return
	 */
	private int getPosition(Appel appel) {
		if (appel instanceof SousParagraphe)
			return ((SousParagraphe) appel).position;
		else if (appel instanceof CalqueParagraphe)
			return ((CalqueParagraphe) appel).position;
		return -1;
	}

	/**
	 * Retourne le nom de la fonction si l'@Appel est une fonction
	 * 
	 * @param appel
	 * @return
	 */
	private String getNomFonction(Appel appel) {
		if (appel instanceof CalqueParagraphe)
			return "<font color=green>" + ((CalqueParagraphe) appel).nomFonction + "</font> : ";
		return "";
	}

	/**
	 * Transformation d'un acteur en texte compréhensible dans l'Atelier TODO, à
	 * revoir pour les casiers
	 * 
	 * @param nom
	 * @param acteur
	 * @return
	 * @throws ErreurException
	 */
	private String toString(String nom, Acteur acteur) throws ErreurException {
		if (acteur instanceof Prototype) {
			StringBuilder builder = new StringBuilder();
			builder.append(nom != null ? nom : acteur.getNom() + " " + "(" + getRole(acteur) + ")=");
			Prototype espece = (Prototype) acteur;
			Map<?, ?> acteurs = espece.retourAttributsMap();
			Iterator<?> i = espece.retourAttributs().iterator();
			builder.append("{ ");
			for (; i.hasNext();) {
				builder.append(toString(null, (Acteur) acteurs.get(i.next()))).append(i.hasNext() ? " , " : "");
			}
			builder.append(" }");
			return builder.toString();
		} else if (acteur instanceof Casier) {
			StringBuilder builder = new StringBuilder();
			builder.append(nom != null ? nom : acteur.getNom() + " " + "(" + getRole(acteur) + ")=");
			Casier casier = (Casier) acteur;
			List<?> acteurs = (List<?>) casier.getValeur();
			builder.append("( ").append("taille=").append(acteurs.size()).append(") ").append("{ ");
			List<?> copies = new ArrayList<>(acteurs);
			for (Iterator<?> i = copies.iterator(); i.hasNext();) {
				builder.append(toString(null, (Acteur) i.next())).append(i.hasNext() ? " , " : "\n");
			}
			builder.append(" }");
			return builder.toString();
		} else {
			return (nom != null ? nom : (acteur.getNom() == null ? "?" : acteur.getNom())) + " " + "(" + getRole(acteur) + ")=" + acteur.getValeur();
		}
	}

	private String getRole(Acteur acteur) {
		if (acteur.getRole() == Role.NOMBRE)
			return "nombre";
		else if (acteur.getRole() == Role.TEXTE)
			return "texte";
		else if (acteur.getRole() == Role.CASIER) {
			return "casier de " + getRole(((Casier) acteur).roleContenant());
		} else if (acteur.getRole() == Role.ESPECE) {
			return ((Prototype) acteur).getType();
		} else
			return "?";
	}

	private String getRole(Role role) {
		if (role == Role.NOMBRE)
			return "nombre";
		else if (role == Role.TEXTE)
			return "texte";
		else if (role == Role.CASIER) {
			return "casier";
		} else if (role == Role.ESPECE) {
			return "espèce";
		} else
			return "?";
	}

	public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent, Noeud child) {
		DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);

		if (parent == null) {
			parent = rootNode;
		}
		treeModel.insertNodeInto(childNode, parent, parent.getChildCount());

		return childNode;
	}

	@Override
	public void setVisible(boolean b) {
		if (!isVisible())
			super.setVisible(b);
	}

	public JLabel getLabelDebogage() {
		return buttonDebogage;
	}

	public void expandAll(JTree tree) {
		int row = 0;
		while (row < tree.getRowCount()) {
			tree.expandRow(row);
			row++;
		}
	}

	private static class Noeud {

		public Noeud(int position, String texte) {
			this.position = position;
			this.texte = texte;
		}

		int position;
		String texte;

		public String toString() {
			return texte;
		}

	}

}