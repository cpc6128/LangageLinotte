/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
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
 *   - Neither the name of Oracle or the names of its
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

package org.linotte.frame.coloration;

/*
 * TableDialogEditDemo.java requires these files:
 *   ColorRenderer.java
 *   ColorEditor.java
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.linotte.frame.Atelier;
import org.linotte.frame.coloration.StyleBuilder.STYLE;
import org.linotte.moteur.outils.Ressources;

/**
 * This is like TableDemo, except that it substitutes a
 * Favorite Color column for the Last Name column and specifies
 * a custom cell renderer and editor for the color data.
 */
@SuppressWarnings("serial")
public final class StyleManageur extends JPanel {

	private Atelier atelier;

	public StyleManageur(final JDialog pere, final Atelier atelier) {
		super(new BorderLayout());

		JTable table = new JTable(new MyTableModel());
		table.setPreferredScrollableViewportSize(new Dimension(600, 300));
		table.setFillsViewportHeight(true);

		//Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		//Set up renderer and editor for the Favorite Color column.
		table.setDefaultRenderer(Color.class, new ColorRenderer(true));
		table.setDefaultEditor(Color.class, new ColorEditor());

		setFontColumn(table, table.getColumnModel().getColumn(7));

		//Add the scroll pane to this panel.
		add(scrollPane, BorderLayout.CENTER);
		JPanel bas = new JPanel();
		JButton button = new JButton("Fermer");
		JButton exporter = new JButton("Exporter");
		JButton importer = new JButton("Importer");
		JButton reinit = new JButton("Restaurer");
		bas.add(importer);
		bas.add(exporter);
		bas.add(reinit);
		bas.add(button);
		add(bas, BorderLayout.SOUTH);

		pere.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				fermer(atelier);
			}
		});

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				pere.setVisible(false);
				pere.dispose();
				//fermer(atelier);
			}
		});

		exporter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					final JFileChooser fc = new JFileChooser();
					int returnVal = fc.showSaveDialog(pere);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						XMLEncoder encoder = new XMLEncoder(new FileOutputStream(file));
						encoder.writeObject(StyleBuilder.mapStyle);
						encoder.close();
						JOptionPane.showMessageDialog(pere, "Styles exportés");
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(pere, "Impossible d'exporter les styles.", "error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});

		importer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					final JFileChooser fc = new JFileChooser();
					int returnVal = fc.showOpenDialog(pere);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File file = fc.getSelectedFile();
						StyleBuilder.importer(new FileInputStream(file));
						for (STYLE s : StyleBuilder.STYLE.values()) {
							StyleBuilder.appliquerStyle(s);
						}
						atelier.getCahierOnglet().forceMiseAJourStyle();
						JOptionPane.showMessageDialog(pere, "Styles importés");
						pere.setVisible(false);
						pere.dispose();
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(pere, "Impossible d'importer les styles.", "error", JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
		});

		reinit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				StyleBuilder.mapStyle.clear();
				for (STYLE s : StyleBuilder.STYLE.values()) {
					StyleBuilder.effacerStyle(s);
				}
				StyleLinotte.initStyles();
				for (STYLE s : StyleBuilder.STYLE.values()) {
					StyleBuilder.appliquerStyle(s);
				}
				atelier.getCahierOnglet().forceMiseAJourStyle();
				JOptionPane.showMessageDialog(pere, "Valeurs par défaut réinstallées !");
				pere.setVisible(false);
				pere.dispose();
			}
		});

		this.atelier = atelier;
	}

	@SuppressWarnings("unchecked")
	private void setFontColumn(JTable table, TableColumn column) {
		//Set up the editor for the sport cells.
		JComboBox comboBox = new JComboBox();
		String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
		for (String string : fonts) {
			comboBox.addItem(string);
		}
		column.setCellEditor(new DefaultCellEditor(comboBox));

		//Set up tool tips for the sport cells.
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for combo box");
		column.setCellRenderer(renderer);
	}

	class MyTableModel extends AbstractTableModel {

		private String[] columnNames = { "Type", "Couleur", "Italique", "Souligné", "Gras", "Barré", "Taille", "Police" };

		public MyTableModel() {
			data = new Object[STYLE.values().length][columnNames.length];
			int pos = 0;
			STYLE[] tab = getSTYLE();

			for (STYLE s : tab) {
				StyleBean styleBean = StyleBuilder.retourneStyleBean(s);
				data[pos][0] = s.titre();
				data[pos][1] = styleBean.getCouleur();
				data[pos][2] = styleBean.isItalique();
				data[pos][3] = styleBean.isSouligne();
				data[pos][4] = styleBean.isGras();
				data[pos][5] = styleBean.isBarre();
				data[pos][6] = styleBean.getSize();
				data[pos][7] = styleBean.getPolice();
				pos++;
			}
		}

		private Object[][] data;

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}

		/*
		 * JTable uses this method to determine the default renderer/ editor for
		 * each cell. If we didn't implement this method, then the last column
		 * would contain text ("true"/"false"), rather than a check box.
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public boolean isCellEditable(int row, int col) {
			//Note that the data/cell address is constant,
			//no matter where the cell appears onscreen.
			if (col < 1) {
				return false;
			} else {
				return true;
			}
		}

		public void setValueAt(Object value, int row, int col) {

			data[row][col] = value;
			fireTableCellUpdated(row, col);

			STYLE style = getSTYLE()[row];
			StyleBean styleBean = StyleBuilder.retourneStyleBean(style);

			if (col == 1)
				styleBean.setCouleur((Color) value);
			if (col == 2)
				styleBean.setItalique((Boolean) value);
			if (col == 3)
				styleBean.setSouligne((Boolean) value);
			if (col == 4)
				styleBean.setGras((Boolean) value);
			if (col == 5)
				styleBean.setBarre((Boolean) value);
			if (col == 6)
				styleBean.setSize((Integer) value);
			if (col == 7)
				styleBean.setPolice((String) value);

			StyleBuilder.appliquerStyle(style);

			atelier.getCahierCourant().forceStyle();

		}

	}

	public static void ouvrir(Atelier atelier) {
		JDialog jd = new JDialog(atelier, "Editeur de styles", true);
		jd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		jd.setIconImage(Ressources.getImageIcon("themes.png").getImage());

		JComponent newContentPane = new StyleManageur(jd, atelier);
		jd.setContentPane(newContentPane);

		jd.setResizable(false);
		jd.pack();
		jd.setLocationRelativeTo(atelier);
		jd.setVisible(true);
	}

	private STYLE[] getSTYLE() {
		STYLE[] tab = STYLE.values();
		Arrays.sort(tab, new Comparator<STYLE>() {
			@Override
			public int compare(STYLE o1, STYLE o2) {
				return o1.titre().compareTo(o2.titre());
			}
		});
		return tab;
	}

	private void fermer(final Atelier atelier) {
		atelier.getCahierOnglet().forceMiseAJourStyle();
	}

}
