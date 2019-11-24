package org.linotte.greffons.python;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.linotte.greffons.GreffonsHandler;
import org.linotte.greffons.api.FabriqueGreffon;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.java.GreffonPrototype.Entrée;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.RegistreDesActions;
import org.python.core.PyDictProxy;
import org.python.core.PyException;
import org.python.core.PyFunction;
import org.python.core.PyMethod;
import org.python.core.PyObject;
import org.python.core.PyTableCode;
import org.python.core.PyType;
import org.python.util.PythonInterpreter;

public class PythonFactory implements FabriqueGreffon {

	public static final String PREFIX = "slot_";

	private PyObject pyGreffon;

	private PyObject jyGreffonClass;

	static {
		Properties props = new Properties();
		try {
			File repertoire_user = new File(Preference.getIntance().getHome() + File.separator + Preference.REPERTOIRE + File.separator
					+ GreffonsHandler.DIR_USER);
			props.setProperty("python.path", Ressources.getGreffons().getCanonicalPath() + File.pathSeparator + repertoire_user.getCanonicalPath());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Opération assez longue, peut durer plusieurs secondes
		PythonInterpreter.initialize(System.getProperties(), props, null);

	}

	public PythonFactory() {
	}

	@Override
	public org.linotte.greffons.api.Greffon produire(String id, String classe) {
		try {
			PythonFactory engine = new PythonFactory(classe);
			org.linotte.greffons.externe.Greffon o = engine.create();
			org.linotte.greffons.api.Greffon greffon = new org.linotte.greffons.api.Greffon();
			greffon.setClass_(o.getClass());
			greffon.setId(id);
			greffon.setObjet(o);
			greffon.setSlots(engine.getSlots());
			return greffon;
		} catch (PyException pye) {
			pye.printStackTrace();
			handlerError(pye);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private PythonFactory(String nom) {
		PythonInterpreter interpreter = new PythonInterpreter();
		// Pour charger la classe en mémoire :
		interpreter.exec("from " + nom + " import " + nom);
		jyGreffonClass = interpreter.get(nom);
	}

	/**
	 * Retourne l'instance Java de l'objet Python
	 * @return
	 */
	private Greffon create() {
		pyGreffon = jyGreffonClass.__call__();
		return (Greffon) pyGreffon.__tojava__(Greffon.class);
	}

	@SuppressWarnings("unchecked")
	private List<Entrée> getSlots() {
		PyType type = pyGreffon.getType();
		List<String> all = (List<String>) ((PyDictProxy) type.getDict()).dictproxy_keys();
		List<Entrée> retour = new ArrayList<Entrée>();
		for (String functionPy : all) {
			if (functionPy.startsWith(PREFIX)) {
				PyMethod poo = (PyMethod) pyGreffon.__findattr__(functionPy);
				String[] params = ((PyTableCode) ((PyFunction) poo.__func__).__code__).co_varnames;
				int nb_args = ((PyTableCode) ((PyFunction) poo.__func__).__code__).co_argcount - 1; // Moins l'objet self
				String[] copie = new String[nb_args];
				if (nb_args != 0)
					System.arraycopy(params, 1, copie, 0, nb_args);
				retour.add(new Entrée(functionPy.substring(PREFIX.length()), new PythonMethod(functionPy, copie)));
			}
		}
		return retour;
	}

	/**
	 * On recupère l'erreur Python pour l'afficher au programmeur.
	 * @param pyException
	 */
	private void handlerError(PyException pyException) {
		StringWriter errors = new StringWriter();
		pyException.printStackTrace(new PrintWriter(errors));
		String message = errors.toString();
		int pos = message.indexOf("at org.python.core.PyException");
		if (pos > 0)
			message = message.substring(0, pos).trim();
		RegistreDesActions.ajouterErreur(message);
	}

}