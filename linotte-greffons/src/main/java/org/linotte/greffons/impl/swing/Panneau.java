package org.linotte.greffons.impl.swing;

import net.miginfocom.swing.MigLayout;
import org.linotte.frame.latoile.Couleur;
import org.linotte.greffons.externe.Composant;
import org.linotte.greffons.impl.swing.layout.GestionnairePlacement;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * 
 * @author R.M
 * 
 */
public class Panneau extends ComposantDeplacable {

	JPanel panneau;

	@Override
	public void initialisation() throws GreffonException {
		if (panneau != null) {
			super.initEvenement();
			return;
		}
		setVisible(getAttributeAsBigDecimal("visible").intValue()==1);
		panneau = new JPanel(new MigLayout());
		super.initaccessibilite(panneau);

		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();
		// Dimensions :
		int hauteur = getAttributeAsBigDecimal("hauteur").intValue();
		int largeur = getAttributeAsBigDecimal("largeur").intValue();
		panneau.setPreferredSize(new Dimension(largeur, hauteur));
		panneau.setMaximumSize(new Dimension(largeur, hauteur));
		// Titre et bordure :
		String titre = getAttributeAsString("titre");
		boolean bordure = getAttributeAsBigDecimal("bordure").intValue() == 1;
		if (titre != null && titre.trim().length() > 0) {
			TitledBorder titleBorder = null;
			if (bordure) {
				titleBorder = new TitledBorder(BorderFactory.createLineBorder(changerCouleurBordure()), titre, changerPositionTitre(), changerHauteurTitre());
				titleBorder.setTitleColor(changerCouleurTitre());
				panneau.setBorder(titleBorder);
			}
			else
				panneau.setBorder(new TitledBorder(null, titre, changerPositionTitre(), changerHauteurTitre()));
		}
		else {
			if (bordure )
				panneau.setBorder(BorderFactory.createLineBorder(changerCouleurBordure()));
		}
			
		changerCouleurFond();
		super.initEvenement();
	}
	
	/**
	 * 
	 */
	private int changerPositionTitre() {
		String positiontitre = getAttributeAsString("positiontitre");
		if (positiontitre != null && positiontitre.trim().length() > 0) {
			if (positiontitre.equals("Gauche")) 
				return TitledBorder.LEFT;
			else if (positiontitre.equals("Centre")) 
				return TitledBorder.CENTER;
			else if (positiontitre.equals("Droite")) 
				return TitledBorder.RIGHT;
			else
				return TitledBorder.CENTER;
		} else 	
			return TitledBorder.CENTER;
	}
	
	/**
	 * 
	 */
	private int changerHauteurTitre() {
		String hauteurtitre = getAttributeAsString("hauteurtitre");
		if (hauteurtitre != null && hauteurtitre.trim().length() > 0) {
			if (hauteurtitre.equals("Dessus haut")) 
				return TitledBorder.ABOVE_TOP;
			else if (hauteurtitre.equals("Centre haut")) 
				return TitledBorder.TOP;
			else if (hauteurtitre.equals("Dessous haut")) 
				return TitledBorder.BELOW_TOP;
			else if (hauteurtitre.equals("Dessus bas")) 
				return TitledBorder.ABOVE_BOTTOM;
			else if (hauteurtitre.equals("Centre bas")) 
				return TitledBorder.BOTTOM ;
			else if (hauteurtitre.equals("Dessous bas")) 
				return TitledBorder.BELOW_BOTTOM;
			else
				return TitledBorder.DEFAULT_POSITION;
		} else 	
			return TitledBorder.DEFAULT_POSITION;
	}	
	
	/**
	 * 
	 */
	private Color changerCouleurTitre() {
		String couleurtitre = getAttributeAsString("couleurtitre");
		if (couleurtitre != null && couleurtitre.trim().length() > 0) {
			return Couleur.retourneCouleur(couleurtitre);
		} else
			return Color.black;
	}
	
	/**
	 * 
	 */
	private Color changerCouleurBordure() {
		String couleurbordure = getAttributeAsString("couleurbordure");
		if (couleurbordure != null && couleurbordure.trim().length() > 0) {
			return Couleur.retourneCouleur(couleurbordure);
		} else
			return Color.black;
	}

	/**
	 * 
	 */
	private void changerCouleurFond() {
		String couleurfond = getAttributeAsString("couleurfond");
		if (couleurfond != null && couleurfond.trim().length() > 0) {
			Color c2 = Couleur.retourneCouleur(couleurfond);
			panneau.setOpaque(true);
			panneau.setBackground(c2);
		} else
			panneau.setOpaque(false);
		panneau.setVisible(isVisible());
	}

	@Override
	public void ajouterComposant(Composant pcomposant) throws GreffonException {

		// Cas des gestionnaires de placement :
		if (pcomposant instanceof GestionnairePlacement) {
			GestionnairePlacement gestionnairePlacement = (GestionnairePlacement) pcomposant;
			panneau.setLayout(gestionnairePlacement.retourneGestionnairePlacement());
			gestionnairePlacement.fairePere(this);
		} else {

			ComposantSwing composant = (ComposantSwing) pcomposant;
			ComposantDeplacable composantDeplacable = (ComposantDeplacable) composant;

			try {
				panneau.add(composantDeplacable.getJComponent(), "pos " + composantDeplacable.getX() + " " + composantDeplacable.getY());
			} catch (GreffonException e) {
				e.printStackTrace();
			}
			panneau.revalidate();
			composantDeplacable.setParent(this);
		}
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return panneau;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
			if (panneau != null && "couleurfond".equals(clef)) {
				changerCouleurFond();
				return true;
			}
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (panneau != null) {
			// menu.dispose();
			panneau = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

}
