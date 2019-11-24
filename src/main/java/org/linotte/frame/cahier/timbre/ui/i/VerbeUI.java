package org.linotte.frame.cahier.timbre.ui.i;

import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;

public interface VerbeUI extends UI {

	ZoneActive accepteZoneParametres(Deposable moi);

	boolean finGlisseDeposeParametre1Delegue(Deposable timbreGlisse);

	boolean finGlisseDeposeParametre2Delegue(Deposable timbreGlisse);

}
