package org.linotte.frame.cahier.timbre.ui;

import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.e.Parfum;
import org.linotte.frame.cahier.timbre.outils.Historique;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Planche implements PlancheUI {

	public Historique historique = new Historique(this); // L'historique est associé à la planche

	public Graphics2D graphics;

	public List<Timbre> timbres = new CopyOnWriteArrayList<Timbre>();

	// Position de l'objet glissé :
	public int delta_x = 0, delta_y = 0;
	// Centre graphique de la planche
	public int centre_x = 0, centre_y = 0;

	public boolean afficheAide = false;

	// Compteur pour la numérotation des acteurs 
	private Integer compteur = 0;

	// Catégorie des boutons à afficher :
	public Parfum boutonsParfum = Parfum.CATEGORIE_BASE;

	public List<Timbre> boutons_fixes_toujours_visibles = new ArrayList<>(), //
			boutons_fixes_actifs = new ArrayList<>(), //
			boutons_fixes_base = new ArrayList<>(), //
			boutons_fixes_boucle = new ArrayList<>(), //
			boutons_fixes_graphiques = new ArrayList<>();

	public int compteurSuivant() {
		synchronized (compteur) {
			compteur++;
			return compteur;
		}

	}

	public void recommencerCompteur() {
		compteur = 0;
	}

}
