package org.linotte.greffons.impl.swing;

import org.linotte.frame.latoile.Couleur;
import org.linotte.greffons.externe.Composant;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author R.M
 * 
 */
public class BoutonRadio extends ComposantDeplacable {

	private Map<String, ImageIcon> cacheImage = new HashMap<String, ImageIcon>();
	private JRadioButton radionButton;
	private ActionListener actionListener;

	@Override
	public void initialisation() throws GreffonException {
		if (radionButton != null) {
			//initEvenement();
			return;
		}
		setVisible(getAttributeAsString("visible").equals("oui"));
		String texte = getAttributeAsString("texte");
		String icone = getAttributeAsString("icône");
		radionButton = new JRadioButton(texte);
		radionButton.setActionCommand(texte);
		chargerImage(icone);
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		//initEvenement();
		String couleur = getAttributeAsString("couleurtexte");
		Color c = Couleur.retourneCouleur(couleur);
		radionButton.setForeground(c);

		String couleurfond = getAttributeAsString("couleurfond");
		if (couleurfond != null && couleurfond.trim().length() > 0) {
			Color c2 = Couleur.retourneCouleur(couleurfond);
			radionButton.setOpaque(true);
			radionButton.setBackground(c2);
		} else
			radionButton.setOpaque(false);

		radionButton.setVisible(isVisible());
		initEvenement();
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return radionButton;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (radionButton != null && "texte".equals(clef)) {
				radionButton.setText((String) ((Acteur) getAttribute(clef)).getValeur());
				radionButton.setActionCommand((String) ((Acteur) getAttribute(clef)).getValeur());
				return true;
			}
			if (radionButton != null && "icône".equals(clef)) {
				chargerImage(getAttributeAsString(clef));
				return true;
			}
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (radionButton != null) {
			radionButton = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	/**
	 * @param hauteur
	 * @param largeur
	 * @param source
	 */
	private void chargerImage(String source) {
		source = getRessourceManager().analyserChemin(source);
		ImageIcon ii = cacheImage.get(source);
		if (ii == null)
			try {
				BufferedImage img = ImageIO.read(new File(source));
				if (img != null) {
					ii = new ImageIcon(img);
					cacheImage.put(source, ii);
				}
			} catch (IOException e1) {
			}
		if (ii != null)
			radionButton.setIcon(ii);
	}

	@Override
	public void initEvenement() throws GreffonException {
		clic_souris.clear();
		souris_entrante.clear();
		clic_droit_souris.clear();

		radionButton.removeActionListener(actionListener);
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (ListenerGreffons listener : clic_souris) {
					listener.execute();
				}
			}
		};
		radionButton.addActionListener(actionListener);
	}

	@Slot(nom = "sélectionner")
	public boolean selectionner() {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				radionButton.setSelected(true);
				radionButton.doClick();
			}
		});
		return true;
	}

}
