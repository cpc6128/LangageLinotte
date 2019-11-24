package org.linotte.greffons.python;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.linotte.greffons.api.AKMethod;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.Casier;
import org.linotte.greffons.externe.Greffon.Espece;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.outils.ObjetLinotteHelper;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.python.core.PyObjectDerived;
import org.python.core.PyString;

public class PythonMethod implements AKMethod {

	private String method;
	private String[] params;

	public PythonMethod(String m, String[] p) {
		method = m;
		params = p;
	}

	@Override
	public ObjetLinotte appeler(Greffon greffon, ObjetLinotte... parametres) throws Exception {

		PyObject[] listPyObject = new PyObject[parametres.length];
		for (int i = 0; i < parametres.length; i++) {
			listPyObject[i] = (Py.java2py(transformerObjet((ObjetLinotte) parametres[i])));
		}

		// Transformation de l'objet java en python :
		PyObject py = Py.java2py(greffon);
		PyObject retour = null;
		try {
			retour = py.invoke(method, listPyObject);
		} catch (PyException pye) {
			pye.printStackTrace();
			handlerError(pye);
		} catch (Exception e) {
			e.printStackTrace();
			throw new GreffonException(e.getMessage());
		}
		if (retour != null) {
			return transformePyObject(retour);
		}
		return null;
	}

	private void handlerError(PyException pye) throws GreffonException {
		StringWriter errors = new StringWriter();
		pye.printStackTrace(new PrintWriter(errors));
		String message = errors.toString();
		int pos = message.indexOf("at org.python.core.PyException");
		if (pos > 0)
			message = message.substring(0, pos).trim();
		throw new GreffonException(message);
	}

	/**
	 * @param retour
	 * @return
	 * @throws GreffonException
	 */
	private ObjetLinotte transformePyObject(PyObject retour) throws GreffonException {
		if (retour instanceof PyString) {
			return ObjetLinotteHelper.copy((String) retour.asString());
		} else if (retour instanceof PyLong) {
			return ObjetLinotteHelper.copy(new BigDecimal(retour.asLong(), Mathematiques.getPrecision()));
		} else if (retour instanceof PyInteger) {
			return ObjetLinotteHelper.copy(new BigDecimal(retour.asInt(), Mathematiques.getPrecision()));
		} else if (retour instanceof PyFloat) {
			return ObjetLinotteHelper.copy(new BigDecimal(retour.asDouble(), Mathematiques.getPrecision()));
		} else if (retour instanceof PyObjectDerived) {
			List<?> pylist = (List<?>) retour.__tojava__(List.class);
			return ObjetLinotteHelper.copy(pylist);
		}
		throw new GreffonException("Type de retour non suporté :" + retour.getType());
	}

	private Object transformerObjet(ObjetLinotte in) throws GreffonException {
		if (in instanceof Casier) {
			List<Object> out = new ArrayList<Object>();
			Casier c = (Casier) in;
			for (Object o : c) {
				((List<Object>) out).add(transformerObjet((ObjetLinotte) o));
			}
			return out;
		}
		if (in instanceof Espece) {
			throw new GreffonException("Type non suporté");
		}
		Object o = ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur();
		if (o instanceof BigDecimal)
			o = ((BigDecimal) o).doubleValue();
		return o;
	}

	public String parametres() {
		String retour = "(";
		for (int i = 0; i < params.length; i++) {
			retour += params[i];
			if (i != (params.length - 1))
				retour += ", ";
		}
		return retour + ")";
	}
}
