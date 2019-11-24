package org.linotte.frame.cahier.timbre.entite;

import java.util.Arrays;

import org.linotte.frame.cahier.timbre.entite.e.Parfum;
import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.i.VerbeUI;

public class Verbe extends Textuel {

	public Deposable[] parametres;

	public int nbParametres;

	public transient VerbeUI ui = UsineATheme.getVerbeUI(this);

	public Verbe(Parfum parfum, String text, int nbParametres, PlancheUI planche) {
		super(text, planche);
		this.nbParametres = nbParametres;
		this.parfum = parfum;
		parametres = new Deposable[nbParametres];
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
	public ZoneActive accepteEtQuelleZone(Timbre timbreGraphique) {
		ZoneActive zoneActive = null;
		if (timbreGraphique instanceof Deposable) {
			zoneActive = ui.accepteZoneParametres((Deposable) timbreGraphique);
		}
		if (zoneActive == null)
			return super.accepteEtQuelleZone(timbreGraphique);
		return zoneActive;
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

		if (zoneActive == ZoneActive.PARAMETRE_1) {
			// La cible est un verbe
			if (parametres[0] == null || !(parametres[0] instanceof PorteurFonction)) {
				parametres[0] = (Deposable) englobeFonction(timbreGlisse);
			} else {
				if (!ui.finGlisseDeposeParametre1Delegue((Deposable) timbreGlisse)) {
					parametres[0] = (Deposable) englobeFonction(timbreGlisse);
				}
			}
			timbreGlisse.x = timbreGlisse.init_x;
			timbreGlisse.y = timbreGlisse.init_y;
			actionConsommee = true;
		}
		if (zoneActive == ZoneActive.PARAMETRE_2) {
			// La cible est un verbe
			if (parametres[1] == null || (!(parametres[1] instanceof PorteurFonction))) {
				parametres[1] = (Deposable) englobeFonction(timbreGlisse);
			} else {
				if (!ui.finGlisseDeposeParametre2Delegue((Deposable) timbreGlisse)) {
					parametres[1] = (Deposable) englobeFonction(timbreGlisse);
				}
			}
			timbreGlisse.x = timbreGlisse.init_x;
			timbreGlisse.y = timbreGlisse.init_y;
			actionConsommee = true;
		}

		if (actionConsommee)
			TimbresHelper.recherchePremiertimbre(this).getUI().positionnerProchainTimbre();
		
		zoneActive = null;

		return actionConsommee;
	}

	@Override
	public Timbre clonerPourCreation() {
		return new Verbe(parfum, texte, nbParametres, planche);
	}

	// Bean

	public Verbe() {
		super();
	}

	@Override
	public void validation() {
		erreurValidation = false;
		for (Deposable acteur : parametres) {
			if (acteur == null) {
				erreurValidation = true;
			}
		}

	}

	@Override
	public Timbre clonerPourDuplication() {
		Verbe clone = new Verbe();
		super.clonerPourDuplication(clone);
		clone.parametres = Arrays.asList(parametres).toArray(new Deposable[nbParametres]);
		clone.nbParametres = nbParametres;
		return clone;
	}
}
