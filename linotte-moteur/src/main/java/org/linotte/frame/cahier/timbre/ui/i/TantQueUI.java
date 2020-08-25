package org.linotte.frame.cahier.timbre.ui.i;

import org.linotte.frame.cahier.timbre.entite.i.Deposable;

public interface TantQueUI extends UI {

	boolean accepteZoneFormule(Deposable timbreGraphique);

	boolean finGlisseDeposeFormule(Deposable timbreGlisse);

}
