package org.linotte.frame.cahier.timbre.ui.i;

import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;

public interface PourUI extends UI {

	ZoneActive accepteZoneParametres(Deposable timbreGraphique);

	boolean finGlisseDeposeActeur(Deposable timbreGlisse);

	boolean finGlisseDeposeIndexDebut(Deposable timbreGlisse);

	boolean finGlisseDeposeIndexFin(Deposable timbreGlisse);

}
