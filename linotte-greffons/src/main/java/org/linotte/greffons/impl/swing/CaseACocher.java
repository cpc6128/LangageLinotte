package org.linotte.greffons.impl.swing;

import org.linotte.frame.latoile.Couleur;
import org.linotte.greffons.externe.Composant;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
public class CaseACocher extends ComposantDeplacable {

	private Map<String, ImageIcon> cacheImage = new HashMap<String, ImageIcon>();
	private JCheckBox checkBox;
	private ItemListener changeListener;

	@Override
	public void initialisation() throws GreffonException {
		if (checkBox != null) {
			initEvenement();
			return;
		}
		setVisible(getAttributeAsBigDecimal("visible").intValue()==1);
		String texte = getAttributeAsString("texte");
		String icone = getAttributeAsString("icône");
		checkBox = new JCheckBox(texte);
		super.initaccessibilite(checkBox);
		checkBox.setActionCommand(texte);
		chargerImage(icone);
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		//initEvenement();
		String couleur = getAttributeAsString("couleurtexte");
		Color c = Couleur.retourneCouleur(couleur);
		checkBox.setForeground(c);

		String couleurfond = getAttributeAsString("couleurfond");
		if (couleurfond != null && couleurfond.trim().length() > 0) {
			Color c2 = Couleur.retourneCouleur(couleurfond);
			checkBox.setOpaque(true);
			checkBox.setBackground(c2);
		} else
			checkBox.setOpaque(false);

		boolean valeur = getAttributeAsBigDecimal("valeur").intValue()==1;
		checkBox.setSelected(valeur);

		checkBox.setVisible(isVisible());
		initEvenement();
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return checkBox;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (checkBox != null && "texte".equals(clef)) {
				checkBox.setText(getAttributeAsString(clef));
				checkBox.setActionCommand(getAttributeAsString(clef));
				return true;
			}
			if (checkBox != null && "valeur".equals(clef)) {
				checkBox.setSelected("vrai".equals(getAttributeAsString(clef)));
				return true;
			}
			if (checkBox != null && "icône".equals(clef)) {
				chargerImage(getAttributeAsString(clef));
				return true;
			}
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		checkBox = null;
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@Override
	public void initEvenement() throws GreffonException {
		clic_souris.clear();
		souris_entrante.clear();

		checkBox.removeItemListener(changeListener);
		changeListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				JCheckBox source = (JCheckBox) e.getSource();
				setAttribute("valeur", source.isSelected() ? "vrai" : "faux");
				for (ListenerGreffons listener : clic_souris) {
					listener.execute();
				}
			}
		};
		checkBox.addItemListener(changeListener);
	}

	/**
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
			checkBox.setIcon(ii);
	}

}
