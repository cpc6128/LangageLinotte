package org.linotte.frame.cahier.timbre.entite;

import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.entite.i.NonDeplacable;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.ConditionUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class Condition extends BlocDebut {

	public Timbre timbreSuivantSecondaire = null;

	public Deposable formule;

	public Condition(PlancheUI planche) {
		super(planche);
	}

	public transient ConditionUI ui = UsineATheme.getConditionUI(this);

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

		// C'est un timbre suivant secondaire !

		timbreGlisse.position = POSITION_INIT;
		timbreGlisse.glisser = false;

		if (zoneActive == ZoneActive.CONDITION_SUIVANT_SECONDAIRE) {
			TimbresHelper.deplacerUnTimbreSecondaire(timbreGlisse, (Condition) this);
			actionConsommee = true;
		}
		zoneActive = null;

		if (actionConsommee)
			TimbresHelper.recherchePremiertimbre(this).getUI().positionnerProchainTimbre();

		return actionConsommee;
	}

	@Override
	public Timbre clonerPourCreation() {
		Condition condition = new Condition(planche);
		BlocFin blocFinCondition = new BlocFin(planche, condition);
		condition.timbreSuivant = blocFinCondition;
		condition.timbreSuivantSecondaire = blocFinCondition;
		condition.blocfin = blocFinCondition;
		condition.getUI().positionnerProchainTimbre();
		return condition;
	}

	/**
	 * Dans le cas d'une condition, est-ce un suivant secondaire ?
	 * @param timbreGraphique
	 * @return
	 */
	@Override
	public ZoneActive accepteEtQuelleZone(Timbre timbreGraphique) {
		// Formule
		if (timbreGraphique instanceof Deposable && ui.accepteZoneFormule((Deposable) timbreGraphique))
			return ZoneActive.CONDITION_FORMULE;
		// Timbre de condaire
		if (ui.accepteSecondaire(timbreGraphique))
			return ZoneActive.CONDITION_SUIVANT_SECONDAIRE;
		else
			// Timbre primaire
			return super.accepteEtQuelleZone(timbreGraphique);
	}

	@Override
	public ConditionUI getUI() {
		return ui;
	}

	// Bean

	public Condition() {
		super();
	}

	@Override
	public Timbre clonerPourDuplication() {
		Condition clone = new Condition();
		super.clonerPourDuplication(clone);
		clone.timbreSuivantSecondaire = timbreSuivantSecondaire;
		clone.formule = formule;
		return clone;
	}
}
