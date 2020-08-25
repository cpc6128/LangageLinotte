package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author R.M
 * 
 */
public class Popup extends ComposantDeplacable {

	private Map<String, ImageIcon> cacheImage = new HashMap<String, ImageIcon>();

	private JOptionPane pane;

	@Override
	public void initialisation() throws GreffonException {
		pane = new JOptionPane();
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return pane;
	}

	@Override
	public void destruction() throws GreffonException {
		pane = null;
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@Slot
	public boolean message(String question) throws GreffonException {
		JOptionPane.showMessageDialog(retournePere(), question, null, JOptionPane.INFORMATION_MESSAGE);
		return true;
	}

	@Slot(nom = "messageeticône")
	public boolean messageeticone(String question, String icone) throws GreffonException {
		JOptionPane.showMessageDialog(retournePere(), question, null, JOptionPane.INFORMATION_MESSAGE, chargerImage(icone));
		return true;
	}

	@Slot
	public String question(String question, List<String> valeurs) throws GreffonException {

		verifierUIetOrdre(valeurs);

		Object[] options = valeurs.toArray();
		int n = JOptionPane.showOptionDialog(retournePere(), question, null, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
				options[0]);
		if (n >= 0) {
			return (String) options[n];
		}
		return "";
	}

	private void verifierUIetOrdre(List<String> valeurs) {
		// http://programmons.forumofficiel.fr/t1200-ordre-du-casier-dans-popup-question#7961
		// http://stackoverflow.com/questions/32628324/is-there-a-way-to-determine-the-correct-order-of-okay-cancel-buttons
		boolean isYesLast = UIManager.getDefaults().getBoolean("OptionPane.isYesLast");		
		
		if (isYesLast){
			Collections.reverse(valeurs);
		}
	}

	@Slot(nom = "questioneticône")
	public String questioneticone(String question, List<String> valeurs, String icone) throws GreffonException {
		
		verifierUIetOrdre(valeurs);
		
		Object[] options = valeurs.toArray();

		int n = JOptionPane.showOptionDialog(retournePere(), question, null, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				chargerImage(icone), options, options[0]);
		if (n >= 0) {
			return (String) options[n];
		}
		return "";
	}

	/**
	* @return
	* @throws GreffonException
	*/
	private Component retournePere() throws GreffonException {
		ComposantSwing pere = (ComposantSwing) getParent();
		Component awt = null;
		if (pere != null) {
			awt = pere.getJComponent();
		}
		return awt;
	}

	/**
	 * @param hauteur
	 * @param largeur
	 * @param source
	 */
	private ImageIcon chargerImage(String source) {
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
