package org.linotte.frame.cahier.timbre.entite;

import java.util.ArrayList;
import java.util.List;

import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.entite.i.NonAttachable;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.FonctionUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class Fonction extends Textuel implements NonAttachable, Deposable {

	public transient FonctionUI ui = UsineATheme.getFonctionUI(this);

	// Ne pas récupérer l'ordre de cette liste :
	public List<Acteur> parametres = new ArrayList<Acteur>();

	public String nom;

	public Fonction(PlancheUI planche) {
		super("Fonction", planche);
		nom = "f";
		position = 0;
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
		if (ui.clique(x, y))
			return true;
		else
			return super.clique(x, y);
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
		return new Fonction(planche);
	}

	@Override
	public ZoneActive accepteEtQuelleZone(Timbre timbreGraphique) {
		ZoneActive zoneActive = null;
		if (timbreGraphique instanceof Acteur) {
			zoneActive = ui.accepteZoneParametres((Acteur) timbreGraphique);
		}
		if (zoneActive == null)
			return super.accepteEtQuelleZone(timbreGraphique);
		return zoneActive;
	}

	public List<Acteur> retourneParametres() {
		List<Acteur> parametresContistues = new ArrayList<Acteur>();
		Timbre i = timbreSuivant;
		while (i != null && i instanceof Acteur && ((Acteur) i).estCeUnParametre()) {
			parametresContistues.add((Acteur) i);
			i = i.timbreSuivant;
		}
		return parametresContistues;
	}

	// Bean

	public Fonction() {
		super();
	}

	@Override
	public void validation() {
		erreurValidation = false;
	}

	@Override
	public Timbre clonerPourDuplication() {
		Fonction clone = new Fonction();
		super.clonerPourDuplication(clone);
		clone.parametres = new ArrayList<>();
		clone.parametres.addAll(parametres);
		clone.nom = nom;
		// Moche : bogue affichage si retour dans l'historique. A expliquer !
		clone.position = 1;
		return clone;
	}

	@Override
	public boolean finGlisseDepose(Timbre timbreGlisse) {

		boolean actionConsommee = false;
		// On glisse pour être le suivant :

		if (zoneActive == ZoneActive.TIMBRE) {
			actionConsommee = super.finGlisseDepose(timbreGlisse);
		}

		// C'est donc un acteur !

		timbreGlisse.position = POSITION_INIT;
		timbreGlisse.glisser = false;

		if (zoneActive == ZoneActive.PARAMETRES_FONCTION) {
			// On attache le paramètre :
			List<Acteur> ps = retourneParametres();
			// on le supprime des parametres s'il est déjà attaché :
			parametres.remove((Acteur) timbreGlisse);
			parametres.add((Acteur) timbreGlisse);
			timbreGlisse.x = timbreGlisse.init_x;
			timbreGlisse.y = timbreGlisse.init_y;
			actionConsommee = true;
			if (ps.isEmpty() || timbreSuivant == timbreGlisse) {
				// On attache le paramètre en tant que premier paramètre :
				zoneActive = ZoneActive.TIMBRE;
				super.finGlisseDepose(timbreGlisse);
			} else {
				// On attache le paramètre à la fin des autres paramètres :
				Acteur cible = ps.get(ps.size() - 1);
				cible.zoneActive = ZoneActive.TIMBRE;
				cible.finGlisseDepose(timbreGlisse);
			}
			// =================================
			// Nettoyage des parametres :
			List<Acteur> bck = retourneParametres();
			parametres.clear();
			parametres.addAll(bck);
			((Timbre) this).ui.repositionnerTousLesTimbres();
		}

		zoneActive = null;

		return actionConsommee;
	}

}
