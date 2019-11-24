package org.linotte.greffons.impl.swing;

import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.linotte.frame.atelier.FlatIHM;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.api.IHM;

/**
 * 
 * @author R.M
 * 
 */
public class Tableau extends BoiteTexte implements IHM {

	private IHM ihm;

	@Override
	public void initialisation() throws GreffonException {
		super.initialisation();
		textPane.setEditable(false);
		try {
			for (AKRuntime runtime : AKPatrol.runtimes) {
				RuntimeContext context = (RuntimeContext) runtime.getContext();
				ihm = context.getIhm();
				context.setIhm(this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean afficher(String arg0, Role arg1) throws StopException {
		if (textPane.getText().trim().length() > 0)
			textPane.append("\n");
		textPane.append(arg0);
		textPane.setCaretPosition(textPane.getText().length());
		return true;
	}

	@Override
	public boolean afficherErreur(String arg0) {
		return ihm.afficherErreur(arg0);
	}

	@Override
	public String demander(Role arg0, String arg1) throws StopException {
		// Bogue remonté par LeBou (http://langagelinotte.free.fr/forum/showthread.php?tid=1162&pid=7789) :
		if (ihm instanceof FlatIHM) {
			return ((FlatIHM) ihm).instanceCompatibilitee.demander(arg0, arg1);
		} else {
			return ihm.demander(arg0, arg1);
		}
	}

	@Override
	public boolean effacer() throws StopException {
		synchronized (textPane) {
			textPane.setText("");
		}
		return true;
	}

	@Override
	public String questionne(String arg0, Role arg1, String arg2) throws StopException {
		if (ihm instanceof FlatIHM) {
			return ((FlatIHM) ihm).instanceCompatibilitee.questionne(arg0, arg1, arg2);
		} else {
			return ihm.questionne(arg0, arg1, arg2);
		}
	}

}
