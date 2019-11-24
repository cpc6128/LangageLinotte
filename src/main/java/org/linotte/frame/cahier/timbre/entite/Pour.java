package org.linotte.frame.cahier.timbre.entite;

import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.i.PourUI;

public class Pour extends BlocDebut {

	public Deposable acteur;

	public Deposable indexDebut;

	public Deposable indexFin;

	public Pour(PlancheUI planche) {
		super(planche);
	}

	public transient PourUI ui = UsineATheme.getPourUI(this);

	@Override
	public void pre_dessine() {
		ui.pre_dessine(planche);
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
	public Timbre clonerPourCreation() {
		Pour pour = new Pour(planche);
		BlocFin blocFinTantQue = new BlocFin(planche, pour);
		pour.timbreSuivant = blocFinTantQue;
		pour.blocfin = blocFinTantQue;
		pour.getUI().positionnerProchainTimbre();
		return pour;
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

		// On glisse la formule :
		if (zoneActive == ZoneActive.POUR_ACTEUR) {
			if (acteur == null || !(acteur instanceof PorteurFonction)) {
				acteur = (Deposable) englobeFonction(timbreGlisse);
			} else {
				if (!ui.finGlisseDeposeActeur((Deposable) timbreGlisse)) {
					acteur = (Deposable) englobeFonction(timbreGlisse);
				}
			}
			timbreGlisse.x = timbreGlisse.init_x;
			timbreGlisse.y = timbreGlisse.init_y;
			actionConsommee = true;
		}

		if (zoneActive == ZoneActive.POUR_INDEX_DEBUT) {
			if (indexDebut == null || !(indexDebut instanceof PorteurFonction)) {
				indexDebut = (Deposable) englobeFonction(timbreGlisse);
			} else {
				if (!ui.finGlisseDeposeIndexDebut((Deposable) timbreGlisse)) {
					indexDebut = (Deposable) englobeFonction(timbreGlisse);
				}
			}
			timbreGlisse.x = timbreGlisse.init_x;
			timbreGlisse.y = timbreGlisse.init_y;
			actionConsommee = true;
		}

		if (zoneActive == ZoneActive.POUR_INDEX_FIN) {
			if (indexFin == null || !(indexFin instanceof PorteurFonction)) {
				indexFin = (Deposable) englobeFonction(timbreGlisse);
			} else {
				if (!ui.finGlisseDeposeIndexFin((Deposable) timbreGlisse)) {
					indexDebut = (Deposable) englobeFonction(timbreGlisse);
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
	public PourUI getUI() {
		return ui;
	}

	// Bean

	public Pour() {
		super();
	}

	@Override
	public Timbre clonerPourDuplication() {
		Pour clone = new Pour();
		super.clonerPourDuplication(clone);
		clone.acteur = acteur;
		clone.indexDebut = indexDebut;
		clone.indexFin = indexFin;
		return clone;
	}

}
