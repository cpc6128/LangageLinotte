package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 
 * @author R.M
 * 
 */
public class Liste extends ComposantDeplacable {

	private static final String DELIMITEUR = "|";
	private JListX list;
	private DefaultListModel listModel;
	private ListSelectionListener changeListener;

	@Override
	public void initialisation() throws GreffonException {
		if (list != null) {
			//initEvenement();
			return;
		}
		listModel = new DefaultListModel();
		setVisible(getAttributeAsBigDecimal("visible").intValue()==1);
		list = new JListX(listModel);
		super.initaccessibilite(list);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		String valeurs = getAttributeAsString("valeurs");
		mettreAjour(valeurs);
		list.setVisible(isVisible());
		initEvenement();
	}

	@SuppressWarnings("unchecked")
	private void mettreAjour(String valeurs) {
		listModel = new DefaultListModel();
		StringTokenizer stringTokenizer = new StringTokenizer(valeurs, DELIMITEUR);
		while (stringTokenizer.hasMoreTokens()) {
			listModel.addElement(stringTokenizer.nextToken());
		}
		list.setModel(listModel);
		list.setSelectedIndex(0);
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return list;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (list != null && "valeurs".equals(clef)) {
				mettreAjour((String) ((Acteur) getAttribute(clef)).getValeur());
				return true;
			}
		}
		return false;
	}

	@Override
	public void initEvenement() throws GreffonException {
		clic_souris.clear();
		souris_entrante.clear();

		list.removeListSelectionListener(changeListener);
		changeListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					if (list.getSelectedIndex() != -1) {
						setAttribute("valeur", (String) list.getSelectedValue());
						for (ListenerGreffons listener : clic_souris) {
							listener.execute();
						}
					}
				}
			}

		};
		list.addListSelectionListener(changeListener);
	}

	@Override
	public void destruction() throws GreffonException {
		if (list != null) {
			// menu.dispose();
			list = null;
			changeListener = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Slot(nom = "chargevaleurs")
	public boolean chargeValeurs(List<String> acteurs) {
		listModel = new DefaultListModel();
		for (Object object : acteurs) {
			listModel.addElement(object);
		}
		list.setModel(listModel);
		list.setSelectedIndex(0);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Slot(nom = "ajoutervaleur")
	public boolean ajouterValeurs(String acteur) {
		listModel.addElement(acteur);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Slot(nom = "ajoutervaleuretinfobulle")
	public boolean ajouterValeurs(String acteur, String infobulle) {
		listModel.addElement(acteur);
		infobulle(listModel.size() - 1, infobulle);
		return true;
	}

	@Slot(nom = "sélectionmultiple")
	public boolean selectionmultiple() {
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		return false;
	}

	@Slot(nom = "sélectionunique")
	public boolean selectionunique() {
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return false;
	}

	@SuppressWarnings("deprecation")
	@Slot()
	public List<String> valeurs() {
		Object[] valeurs = list.getSelectedValues();
		List<String> retour = new ArrayList<String>();
		for (Object object : valeurs) {
			retour.add((String) object);
		}
		return retour;
	}

	@Slot(nom = "infobulle")
	public boolean infobulle(int pos, String texte) {
		list.setToolTip(pos, texte);
		return true;
	}
}
