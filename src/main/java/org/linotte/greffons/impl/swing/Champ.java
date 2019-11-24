package org.linotte.greffons.impl.swing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.linotte.greffons.externe.Composant;

/**
 * 
 * @author R.M
 * 
 */
public class Champ extends ComposantDeplacable {

	JTextField champ;
	int taille;

	@Override
	public void initialisation() throws GreffonException {
		if (champ != null) {
			super.initEvenement();
			return;
		}
		final String mode = getAttributeAsString("mode");
		setVisible(getAttributeAsString("visible").equals("oui"));
		String texte = getAttributeAsString("texte");
		if ("secret".equals(mode)) {
			champ = new JPasswordField(texte);
		} else {
			champ = new JTextField(texte);
		}
		champ.setColumns(10);
		// Droit d'ecriture :
		champ.setEditable(!"lecture".equals(mode));

		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		taille = getAttributeAsBigDecimal("taille").intValue();
		champ.setColumns(taille);
		champ.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {			
				if ("nombre".equals(mode)) {
					String saisie = champ.getText();
					
					// On supprime tous les caracteres non numeriques
		        	if ((! Character.isDigit(e.getKeyChar())) && e.getKeyChar() != '.' && e.getKeyChar() != '-' && e.getKeyChar() != '+' ) {
		        		e.consume();
		        	}
		        	else {
		        		if ((e.getKeyChar() == '-' || e.getKeyChar() == '+') && (saisie.length() > 1)) {		        			
		        			e.consume();
		        		}
		        		else if ((e.getKeyChar() == '.') && (saisie.contains(".") || saisie.length() == 0)) {
		        			e.consume();
		        		}	        			
		        	}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				for (ListenerGreffons listener : touche) {
					listener.execute();
				}
				setAttribute("texte", champ.getText());
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}
		});
		champ.setVisible(isVisible());
		super.initEvenement();
	}

	@Override
	public void ajouterComposant(Composant composant) throws GreffonException {
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return champ;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (champ != null && "texte".equals(clef)) {
				int pos = champ.getCaretPosition();
				champ.setText((String) ((Acteur) getAttribute(clef)).getValeur());
				try {
					champ.setCaretPosition(pos);
				} catch (Exception e) {
				}
				return true;
			}
			if (champ != null && "mode".equals(clef)) {
				String mode = getAttributeAsString("mode");
				champ.setEditable(!"lecture".equals(mode));
				return true;
			}
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (champ != null) {
			// menu.dispose();
			champ = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}
}
