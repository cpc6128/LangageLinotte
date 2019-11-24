package org.linotte.frame.cahier.timbre.entite;

import java.util.List;

import org.linotte.frame.cahier.timbre.JPanelPlancheATimbre;
import org.linotte.frame.cahier.timbre.entite.e.Parfum;
import org.linotte.frame.cahier.timbre.entite.i.PasSauvegarder;
import org.linotte.frame.cahier.timbre.entite.i.Statique;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.BlocFinUI;
import org.linotte.frame.cahier.timbre.ui.i.BoutonUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class Bouton extends Timbre implements Statique, PasSauvegarder {

	public transient BoutonUI ui = UsineATheme.getBoutonUI(this);

	public String label;

	public Timbre original;

	public JPanelPlancheATimbre panel;

	public Timbre derniere_copie;

	private final Integer POSITION_INIT = 10;

	public Bouton(String text, Timbre premier, PlancheUI plancheUI) {
		super(plancheUI);
		label = text;
		original = premier;
		position = POSITION_INIT;
	}

	public Bouton(String text, Parfum parfum, PlancheUI plancheUI, JPanelPlancheATimbre panel) {
		super(plancheUI);
		label = text;
		this.parfum = parfum;
		position = POSITION_INIT;
		this.panel = panel;
	}

	public boolean debutGlisseDepose(int x, int y) {
		boolean b = ui.contient(x, y);
		if (b) {
			// On commit l'historique :
			//((Planche) planche).historique.prendrePhoto();
			//((Planche) planche).historique.archiverPhoto();
			clique(x, y);
			if (derniere_copie != null) {
				derniere_copie.x = x;
				derniere_copie.y = y;
				// Enlever le bloc fin :
				if (derniere_copie instanceof BlocDebut) {
					// On cache le bloc de fin lors d'un glissé initial :
					((BlocFinUI) ((BlocFin) ((BlocDebut) derniere_copie).blocfin).ui).cache();
				}
			}

			return true;
		}
		return false;
	}

	@Override
	public void pre_dessine() {
		ui.pre_dessine(planche);
		super.pre_dessine();
	}

	@Override
	public void dessine() {
		ui.dessine(planche);
		super.dessine();
	}

	@Override
	public boolean clique(int x, int y) {
		if (original != null) {

			((Planche) planche).historique.prendrePhoto();

			// Il faut créer le bouton
			derniere_copie = (Timbre) original.clonerPourCreation();
			// L'ajouter à la planche à timbre :
			((Planche) planche).timbres.add(0, derniere_copie);
			// Cas particulier des conditions, il faut ajouter le timbre de fin de bloc :
			if (derniere_copie instanceof BlocDebut) {
				// On ajoute ce timbre sur la planche :
				((Planche) planche).timbres.add(0, ((BlocDebut) derniere_copie).blocfin);
			}
		} else {
			if (parfum == Parfum.UNDO) {
				List<Timbre> l = ((Planche) planche).historique.depilerPhoto();
				if (l != null)
					panel.chargerTimbresSurPlanche(l);
			} else if (parfum == Parfum.AIDE) {
				((Planche) planche).historique.annulerPhoto();
				((Planche) planche).afficheAide = true;
			} else if (parfum == Parfum.CATEGORIE_BASE) {
				((Planche) planche).historique.annulerPhoto();
				synchronized (((Planche) planche).timbres) {
					((Planche) planche).boutonsParfum = Parfum.CATEGORIE_BASE;
					((Planche) planche).boutons_fixes_actifs.clear();
					((Planche) planche).boutons_fixes_actifs.addAll(((Planche) planche).boutons_fixes_base);
					// On supprime les autres boutons. moche...
					((Planche) planche).timbres.removeAll(((Planche) planche).boutons_fixes_graphiques);
					((Planche) planche).timbres.removeAll(((Planche) planche).boutons_fixes_boucle);
					((Planche) planche).timbres.addAll(((Planche) planche).boutons_fixes_base);
				}
			} else if (parfum == Parfum.CATEGORIE_GRAPHIQUE) {
				((Planche) planche).historique.annulerPhoto();
				((Planche) planche).boutonsParfum = Parfum.CATEGORIE_GRAPHIQUE;
				synchronized (((Planche) planche).timbres) {
					((Planche) planche).boutons_fixes_actifs.clear();
					((Planche) planche).boutons_fixes_actifs.addAll(((Planche) planche).boutons_fixes_graphiques);
					// On supprime les autres boutons. moche...
					((Planche) planche).timbres.removeAll(((Planche) planche).boutons_fixes_base);
					((Planche) planche).timbres.removeAll(((Planche) planche).boutons_fixes_boucle);
					((Planche) planche).timbres.addAll(((Planche) planche).boutons_fixes_graphiques);
				}
			} else if (parfum == Parfum.CATEGORIE_BOUCLE) {
				((Planche) planche).historique.annulerPhoto();
				synchronized (((Planche) planche).timbres) {
					((Planche) planche).boutonsParfum = Parfum.CATEGORIE_BOUCLE;
					((Planche) planche).boutons_fixes_actifs.clear();
					((Planche) planche).boutons_fixes_actifs.addAll(((Planche) planche).boutons_fixes_boucle);
					// On supprime les autres boutons. moche...
					((Planche) planche).timbres.removeAll(((Planche) planche).boutons_fixes_base);
					((Planche) planche).timbres.removeAll(((Planche) planche).boutons_fixes_graphiques);
					((Planche) planche).timbres.addAll(((Planche) planche).boutons_fixes_boucle);
				}
			}
		}
		return true;
	}

	@Override
	public boolean contient(int x, int y) {
		boolean b = ui.contient(x, y);
		if (b) {
			return true;
		} else {
			return super.contient(x, y);
		}
	}

	@Override
	public Timbre clonerPourCreation() {
		return null;
	}

	@Override
	public Timbre clonerPourDuplication() {
		return null;
	}

}
