package org.linotte.greffons.impl.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.linotte.greffons.externe.Composant;

/**
 * 
 * @author R.M
 * 
 */
public class Selecteur extends ComposantDeplacable {

	private static final String DELIMITEUR = "|";
	private JComboBox<String> combobox;
	private DefaultComboBoxModel<String> listModel;
	private ActionListener changeListener;

	@Override
	public void initialisation() throws GreffonException {
		if (combobox != null) {
			//initEvenement();
			return;
		}
		listModel = new DefaultComboBoxModel<String>();
		setVisible(getAttributeAsString("visible").equals("oui"));
		combobox = new JComboBox<String>(listModel);
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		String valeurs = getAttributeAsString("valeurs");
		mettreAjour(valeurs);
		String mode = getAttributeAsString("mode");
		combobox.setEditable("éditable".equals(mode));
		combobox.setVisible(isVisible());
		initEvenement();
	}

	private void mettreAjour(String valeurs) {
		listModel.removeAllElements();
		StringTokenizer stringTokenizer = new StringTokenizer(valeurs, DELIMITEUR);
		while (stringTokenizer.hasMoreTokens()) {
			listModel.addElement(stringTokenizer.nextToken());
		}
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return combobox;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (combobox != null && "valeurs".equals(clef)) {
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

		combobox.removeActionListener(changeListener);
		if (changeListener == null) {
			changeListener = new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					JComboBox<?> cb = (JComboBox<?>) e.getSource();
					setAttribute("valeur", (String) cb.getSelectedItem());
					for (ListenerGreffons listener : clic_souris) {
						listener.execute();
					}
				}
			};
			combobox.addActionListener(changeListener);
		}
	}

	@Override
	public void destruction() throws GreffonException {
		if (combobox != null) {
			// menu.dispose();
			combobox = null;
			changeListener = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@Slot(nom = "chargevaleurs")
	public boolean chargeValeurs(List<String> acteurs) {
		listModel.removeAllElements();
		for (Object object : acteurs) {
			String sobject = (object instanceof String) ? ((String) object) : String.valueOf(object);
			listModel.addElement(sobject);
		}
		return true;
	}

	@Slot(nom = "ajoutervaleur")
	public boolean ajouterValeurs(String acteur) {
		listModel.addElement(acteur);
		return true;
	}

}
