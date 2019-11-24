package org.linotte.frame.cahier.timbre.ui.i;

import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;

public interface ConditionUI extends UI {

	boolean accepteSecondaire(Timbre moi);

	boolean accepteZoneFormule(Deposable timbreGraphique);

	boolean finGlisseDeposeFormule(Deposable timbreGlisse);

}
