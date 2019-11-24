package org.linotte.greffons.impl.swing;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JTabbedPane;

import org.linotte.greffons.externe.Composant;

/**
 * 
 * @author R.M
 * 
 */
public class Onglet extends ComposantDeplacable {

	JTabbedPane tabbedPane;
	private Map<String, ImageIcon> cacheImage = new HashMap<String, ImageIcon>();

	@Override
	public void initialisation() throws GreffonException {
		if (tabbedPane != null) {
			super.initEvenement();
			return;
		}
		setVisible(getAttributeAsString("visible").equals("oui"));
		tabbedPane = new JTabbedPane();
		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		// Dimensions :
		int hauteur = getAttributeAsBigDecimal("hauteur").intValue();
		int largeur = getAttributeAsBigDecimal("largeur").intValue();
		tabbedPane.setPreferredSize(new Dimension(largeur, hauteur));
		tabbedPane.setMaximumSize(new Dimension(largeur, hauteur));

		tabbedPane.setVisible(isVisible());
		super.initEvenement();
	}

	@Override
	public void ajouterComposant(Composant pcomposant) throws GreffonException {
		ComposantSwing composant = (ComposantSwing) pcomposant;
		tabbedPane.addTab(String.valueOf(tabbedPane.getTabCount()), composant.getJComponent());
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return tabbedPane;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (tabbedPane != null) {
			// menu.dispose();
			tabbedPane = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	/*
	 * Méthodes fonctionnelles :
	 */

	/**
	 * Changer le titre d'un onglet
	 * @param titre
	 * @param numero
	 * @return
	 * @throws GreffonException
	 */
	@Slot
	public boolean titre(String titre, int numero) throws GreffonException {
		try {
			tabbedPane.setTitleAt(numero, titre);
		} catch (Exception e) {
			throw new GreffonException("Pas d'onglet numéro " + numero);
		}
		return true;
	}

	/**
	 * Changer le titre d'un onglet
	 * @param titre
	 * @param numero
	 * @return
	 * @throws GreffonException
	 */
	@Slot(nom = "icône")
	public boolean icone(String icone, int numero) throws GreffonException {
		try {
			Icon icon = chargerImage(icone);
			tabbedPane.setIconAt(numero, icon);
		} catch (Exception e) {
			throw new GreffonException("Pas d'onglet numéro " + numero);
		}
		return true;
	}

	private Icon chargerImage(String source) {
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
		return ii;
	}
}
