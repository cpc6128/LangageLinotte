package org.linotte.greffons.impl.swing;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

import org.linotte.greffons.externe.Composant;

public class Extension extends ComposantSwing {

	private JScrollPane scrollPane;
	private JPanel panel;
	private String titre;

	public Extension() {
	}

	@Override
	public void initialisation() throws GreffonException {
		if (scrollPane != null) {
			if (!scrollPane.isVisible()) {
				//frame.pack();
				// frame.setVisible(true);

			}
			return;
		}

		panel = new JPanel(new MigLayout());
		scrollPane = new JScrollPane(panel);

		setTitre(getAttributeAsString("titre"));

		initEvenement();
	}

	@Override
	public void ajouterComposant(final Composant pcomposant) throws GreffonException {

		ComposantSwing composant = (ComposantSwing) pcomposant;
		ComposantDeplacable composantDeplacable = (ComposantDeplacable) composant;

		try {
			panel.add(composantDeplacable.getJComponent(), "pos " + composantDeplacable.getX() + " " + composantDeplacable.getY());
		} catch (GreffonException e) {
			e.printStackTrace();
		}
		composantDeplacable.setParent(this);
		//frame.pack();
		try {
			((Component) composantDeplacable.getJComponent()).requestFocus();
		} catch (GreffonException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return scrollPane;
	}

	public String getTitre() {
		return titre;
	}

	private void setTitre(String titre) {
		this.titre = titre;
	}

	@Override
	public void destruction() throws GreffonException {
		if (scrollPane != null) {
			org.linotte.frame.Atelier.supprimeExtension(scrollPane);
			scrollPane = null;
		}
	}

}
