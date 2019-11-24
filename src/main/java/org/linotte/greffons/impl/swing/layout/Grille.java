package org.linotte.greffons.impl.swing.layout;

import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.math.BigDecimal;

import javax.swing.JPanel;

import org.linotte.greffons.GreffonsChargeur;
import org.linotte.greffons.externe.Composant;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.impl.swing.ComposantSwing;
import org.linotte.greffons.outils.ObjetLinotteFactory;

/**
 * 
 * @author R.M
 * 
 */
public class Grille extends Composant implements GestionnairePlacement {

	private GridLayout grille;
	private ComposantSwing composantAvecGrille;

	@Override
	public void initialisation() throws GreffonException {
		if (grille != null) {
			return;
		}
		int largeur = getAttributeAsBigDecimal("largeur").intValue();
		int hauteur = getAttributeAsBigDecimal("hauteur").intValue();
		grille = new GridLayout(hauteur, largeur);
	}

	@Override
	public void ajouterComposant(Composant pcomposant) throws GreffonException {
		composantAvecGrille.ajouterComposant(pcomposant);
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (!super.fireProperty(clef)) {
		}
		return false;
	}

	@Override
	public void destruction() throws GreffonException {
		if (grille != null) {
			grille = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@Override
	public LayoutManager retourneGestionnairePlacement() {
		return grille;
	}

	@Override
	public void fairePere(ComposantSwing formulaire) {
		composantAvecGrille = formulaire;
	}

	@Slot
	@Deprecated
	public boolean ajouter(Espece espece) throws GreffonException {
		// On recupere l'identifiant unique de l'espece.
		Acteur id = espece.getAttribut(ObjetLinotteFactory.CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT);
		// Recuperation de l'instance reelle 
		Greffon greffon = GreffonsChargeur.getInstance().cache.get(((BigDecimal) id.getValeur()).longValue());
		ComposantSwing composant = (ComposantSwing) greffon;
		// initialisation des composants Swing :
		composant.initialisation();
		composantAvecGrille.initialisation();
		// On l'ajoute dans le JPanel
		((JPanel) composantAvecGrille.getJComponent()).add(composant.getJComponent());
		// On force le recalcule du JPanel
		//form.pack();
		return true;
	}

}
