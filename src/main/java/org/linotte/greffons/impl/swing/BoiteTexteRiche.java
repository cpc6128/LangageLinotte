package org.linotte.greffons.impl.swing;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import org.linotte.greffons.GreffonsChargeur;
import org.linotte.greffons.externe.Composant;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.outils.ObjetLinotteFactory;

import com.hexidec.ekit.EkitCore;
import com.hexidec.ekit.EkitCorePatch;

/**
 * 
 * @author R.M
 * 
 */
public class BoiteTexteRiche extends ComposantDeplacable {

	private JPanel boite;
	protected EkitCore ekitText;

	@Override
	public void initialisation() throws GreffonException {
		if (boite != null) {
			super.initEvenement();
			return;
		}
		boite = new JPanel();
		ekitText = new EkitCorePatch();

		boite.setLayout(new GridBagLayout());
		GridBagConstraints localGridBagConstraints = new GridBagConstraints();
		localGridBagConstraints.fill = 2;
		localGridBagConstraints.anchor = 11;
		localGridBagConstraints.gridheight = 1;
		localGridBagConstraints.gridwidth = 1;
		localGridBagConstraints.weightx = 1.0D;
		localGridBagConstraints.weighty = 0.0D;
		localGridBagConstraints.gridx = 1;

		localGridBagConstraints.gridy = 1;
		boite.add(ekitText.getToolBarMain(true), localGridBagConstraints);

		localGridBagConstraints.gridy = 2;
		boite.add(ekitText.getToolBarFormat(true), localGridBagConstraints);

		localGridBagConstraints.gridy = 3;
		boite.add(ekitText.getToolBarStyles(true), localGridBagConstraints);

		localGridBagConstraints.anchor = 15;
		localGridBagConstraints.fill = 1;
		localGridBagConstraints.weighty = 1.0D;
		localGridBagConstraints.gridy = 4;
		boite.add(ekitText, localGridBagConstraints);

		initialize();
		setVisible(getAttributeAsString("visible").equals("oui"));

		// Dimensions :
		int hauteur = getAttributeAsBigDecimal("hauteur").intValue();
		int largeur = getAttributeAsBigDecimal("largeur").intValue();

		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		ekitText.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				//setAttribute("texte", ekitText.getDocumentBody());
				//System.out.println(ekitText.getDocumentBody());
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});

		boite.setPreferredSize(new Dimension(largeur, hauteur));
		boite.setMaximumSize(new Dimension(largeur, hauteur));
		boite.setVisible(isVisible());

		if (getAttribute("infobulle") != null && getAttributeAsString("infobulle").trim().length() > 0)
			ekitText.setToolTipText(getAttributeAsString("infobulle"));
		else {
			ekitText.setToolTipText(null);
		}

		super.initEvenement();
	}

	private void initialize() {

	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return boite;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (boite != null) {
			// menu.dispose();
			boite = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@Slot(nom = "complétermenu")
	public boolean completerMenu(Espece menu) throws GreffonException {
		Acteur __prototype = menu.getAttribut(ObjetLinotteFactory.CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT);
		// Recuperation de l'instance reelle 
		Greffon greffon = GreffonsChargeur.getInstance().cache.get(((BigDecimal) __prototype.getValeur()).longValue());
		ComposantSwing composant = (ComposantSwing) greffon;
		if (composant instanceof Formulaire) {
			Vector<String> v = new Vector<>();
			v.add(EkitCore.KEY_MENU_FILE);
			v.add(EkitCore.KEY_MENU_EDIT);
			v.add(EkitCore.KEY_MENU_VIEW);
			v.add(EkitCore.KEY_MENU_FONT);
			v.add(EkitCore.KEY_MENU_FORMAT);
			v.add(EkitCore.KEY_MENU_INSERT);
			v.add(EkitCore.KEY_MENU_TABLE);
			v.add(EkitCore.KEY_MENU_FORMS);
			v.add(EkitCore.KEY_MENU_SEARCH);
			v.add(EkitCore.KEY_MENU_TOOLS);
			//v.add(EkitCore.KEY_MENU_HELP); La popup est moche ...
			JMenuBar jmenub = ekitText.getCustomMenuBar(v);
			((Formulaire) composant).ajoutMenu(jmenub);
			return true;
		} else {
			throw new GreffonException("Ne peut être utilisé seulement avec un Formulaire");
		}
	}

	@Slot
	public String retourneformathtml() {
		return ekitText.getDocumentText();
	}

	@Slot
	public boolean affectertexte(final String texte) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ekitText.setDocumentText(texte);
			}
		});
		return true;
	}

	@Slot
	public String retourneformatrtf() throws GreffonException {
		try {
			return ekitText.getRTFDocument();
		} catch (IOException | BadLocationException e) {
			e.printStackTrace();
			throw new GreffonException("impossible d'extraire le format RTF");
		}
	}
}
