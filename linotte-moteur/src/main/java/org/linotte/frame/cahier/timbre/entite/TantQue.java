package org.linotte.frame.cahier.timbre.entite;

import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.entite.i.NonDeplacable;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.i.TantQueUI;

public class TantQue extends BlocDebut {

	public Deposable formule;

	public TantQue(PlancheUI planche) {
		super(planche);
	}

	public transient TantQueUI ui = UsineATheme.getTantQueUI(this);

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
	public Timbre clonerPourCreation() {
		TantQue tantque = new TantQue(planche);
		BlocFin blocFinTantQue = new BlocFin(planche, tantque);
		tantque.timbreSuivant = blocFinTantQue;
		tantque.blocfin = blocFinTantQue;
		tantque.getUI().positionnerProchainTimbre();
		return tantque;
	}

	@Override
	public TantQueUI getUI() {
		return ui;
	}

	// Bean

	public TantQue() {
		super();
	}

	@Override
	public Timbre clonerPourDuplication() {
		TantQue clone = new TantQue();
		super.clonerPourDuplication(clone);
		clone.formule = formule;
		return clone;
	}

	@Override
	public boolean finGlisseDepose(Timbre timbreGlisse) {

		boolean actionConsommee = false;

		// On ne déplace pas un bloc début (trop compliqué à gérer !)
		if (timbreGlisse instanceof NonDeplacable) {
			if (!TimbresHelper.timbresPrecedents(timbreGlisse).isEmpty()) {
				timbreGlisse.retourArriere();
				zoneActive = null;
				return false;
			}
		}

		// On glisse la formule :
		if (zoneActive == ZoneActive.CONDITION_FORMULE) {
			if (formule == null || !(formule instanceof PorteurFonction)) {
				formule = (Deposable) englobeFonction(timbreGlisse);
			} else {
				if (!ui.finGlisseDeposeFormule((Deposable) timbreGlisse)) {
					formule = (Deposable) englobeFonction(timbreGlisse);
				}
			}
			timbreGlisse.x = timbreGlisse.init_x;
			timbreGlisse.y = timbreGlisse.init_y;
			actionConsommee = true;
		}

		// On glisse pour être le suivant :

		if (zoneActive == ZoneActive.TIMBRE) {
			return super.finGlisseDepose(timbreGlisse);
		}

		timbreGlisse.position = POSITION_INIT;
		timbreGlisse.glisser = false;

		zoneActive = null;
		if (actionConsommee)
			TimbresHelper.recherchePremiertimbre(this).getUI().positionnerProchainTimbre();
		return actionConsommee;
	}

	@Override
	public ZoneActive accepteEtQuelleZone(Timbre timbreGraphique) {
		// Formule
		if (timbreGraphique instanceof Deposable && ui.accepteZoneFormule((Deposable) timbreGraphique))
			return ZoneActive.CONDITION_FORMULE;
		// Timbre 
		return super.accepteEtQuelleZone(timbreGraphique);
	}

}
