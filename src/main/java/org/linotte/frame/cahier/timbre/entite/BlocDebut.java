package org.linotte.frame.cahier.timbre.entite;

import org.linotte.frame.cahier.timbre.entite.i.NonDeplacable;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public abstract class BlocDebut extends Timbre implements NonDeplacable {

	public BlocFin blocfin;

	public BlocDebut(PlancheUI planche) {
		super(planche);
	}

	// Bean
	public BlocDebut() {
	}

	public void clonerPourDuplication(BlocDebut clone) {
		super.clonerPourDuplication(clone);
		clone.blocfin = blocfin;
	}

}
