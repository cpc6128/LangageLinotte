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

package org.linotte.frame.cahier;

import org.linotte.frame.cahier.Cahier.EtatCachier;
import org.linotte.moteur.outils.Ressources;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author ronan
 * 
 */
@SuppressWarnings("serial")
public class Onglets extends JPanel {

	private JTabbedPane tabbedPane = new JTabbedPane();

	public Onglets() {
        super(new GridLayout(1, 1));
        add(tabbedPane);
        tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane pane = (JTabbedPane) e.getSource();
                Cahier cahier = (Cahier) pane.getSelectedComponent();
                if (cahier != null) {
                    cahier.getAtelier().setCahierCourant(cahier);
                    cahier.changeAtelierEtat();
                }
            }
		});
		tabbedPane.putClientProperty( "JTabbedPane.tabClosable", true );
		tabbedPane.putClientProperty( "JTabbedPane.tabCloseCallback",
				(java.util.function.BiConsumer<JTabbedPane, Integer>) (tabbedPane, tabIndex) -> {
					// close tab here
					tabbedPane.removeTabAt( tabIndex );
				} );
	}

	public void ajouterCahier(Cahier cahier) {
		tabbedPane.addTab(cahier.getFichier() != null ? cahier.getFichier().getName() : "[nouveau] *", Ressources.getImageIcon("format-justify-left.png"),
				cahier, "Livre arrêté...");
		int index = tabbedPane.getTabCount() - 1;
		tabbedPane.setTabComponentAt(index, new Onglet(cahier, tabbedPane));
		tabbedPane.setSelectedIndex(index);
	}

	public boolean verifierEtat(EtatCachier etatCachier) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (((Onglet) tabbedPane.getTabComponentAt(i)).getCahierPanel().getEtatCahier() == etatCachier)
				return true;
		}
		return false;
	}

	public List<String> getFichiersOuverts() {
		List<String> fichiers = new ArrayList<String>();
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (((Onglet) tabbedPane.getTabComponentAt(i)).getCahierPanel().getFichier() != null)
				fichiers.add(((Onglet) tabbedPane.getTabComponentAt(i)).getCahierPanel().getFichier().getAbsolutePath());
		}
		return fichiers;
	}

	public void forceMiseAJourStyle() {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			((Onglet) tabbedPane.getTabComponentAt(i)).getCahierPanel().setEditeurUpdate(true);
		}
	}

	public boolean estIlOuverts(File aOuvrir) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (((Onglet) tabbedPane.getTabComponentAt(i)).getCahierPanel().getFichier() != null) {
				try {
					if (aOuvrir.getCanonicalPath().equals(((Onglet) tabbedPane.getTabComponentAt(i)).getCahierPanel().getFichier().getCanonicalPath())) {
						tabbedPane.setSelectedIndex(i);
						return true;
					}
				} catch (IOException e) {
				}
			}
		}
		return false;
	}

	public void next() {
		int pos = tabbedPane.getSelectedIndex();
		pos = pos + 1;
		if (pos >= (tabbedPane.getTabCount()))
			pos = 0;
		tabbedPane.setSelectedIndex(pos);
	}

	public void previous() {
		int pos = tabbedPane.getSelectedIndex();
		pos = pos - 1;
		if (pos < 0)
			pos = tabbedPane.getTabCount() - 1;
		tabbedPane.setSelectedIndex(pos);
	}

	public int nbOnglet() {
		return tabbedPane.getTabCount();
	}

	public Cahier retourneCahier(File aOuvrir) {
		for (int i = 0; i < tabbedPane.getTabCount(); i++) {
			if (((Onglet) tabbedPane.getTabComponentAt(i)).getCahierPanel().getFichier() != null) {
				if (aOuvrir.getAbsolutePath().equals(((Onglet) tabbedPane.getTabComponentAt(i)).getCahierPanel().getFichier().getAbsolutePath())) {
					return ((Onglet) tabbedPane.getTabComponentAt(i)).getCahierPanel();
				}
			}
		}
		return null;
	}
}
