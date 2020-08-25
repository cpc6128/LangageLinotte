package org.linotte.greffons.java;

import org.linotte.greffons.api.AKMethod;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.*;
import org.linotte.greffons.outils.ObjetLinotteHelper;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class JavaMethod implements AKMethod {

	private Method method;

	public JavaMethod(Method m) {
		method = m;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ObjetLinotte appeler(Greffon greffon, final ObjetLinotte... parametres) throws Exception {
		try {

			// Transformation des paramètres si le developpeur utilise des types Java :
			Class<?>[] pTypes = method.getParameterTypes();
			if (parametres.length != pTypes.length) {
				throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
			}

			Object[] parametres_o = new Object[parametres.length];
			for (int i = 0; i < pTypes.length; i++) {
				parametres_o[i] = transformerObjet(pTypes[i], parametres[i]);
			}

			Object retour = method.invoke(greffon, parametres_o);

			if (!(retour instanceof ObjetLinotte)) {
				if (retour instanceof String) {
					retour = ObjetLinotteHelper.copy((String) retour);
				} else if (retour instanceof Boolean) {
					retour = ObjetLinotteHelper.copy((Boolean) retour);
				} else if (retour instanceof Integer) {
					retour = ObjetLinotteHelper.copy((Integer) retour);
				} else if (retour instanceof BigDecimal) {
					retour = ObjetLinotteHelper.copy((BigDecimal) retour);
				} else if (retour instanceof BigInteger) {
					retour = ObjetLinotteHelper.copy((BigInteger) retour);
				} else if (retour instanceof List) {
					retour = ObjetLinotteHelper.copy((List) retour);
				} else if (retour instanceof Double) {
					retour = ObjetLinotteHelper.copy((Double) retour);
				} else if (retour instanceof Long) {
					retour = ObjetLinotteHelper.copy((Long) retour);
				} else if (retour instanceof Float) {
					retour = ObjetLinotteHelper.copy((Float) retour);
				}
			}

			return (ObjetLinotte) retour;
		} catch (java.lang.IndexOutOfBoundsException e) {
			throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
		} catch (java.lang.IllegalArgumentException e) {
			throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
		} catch (ClassCastException e) {
			throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());

		} catch (InvocationTargetException e) {
			Throwable x = e.getTargetException();
			if (x instanceof Exception) {
				throw (Exception) x;
			} else {
				throw new GreffonException("Erreur interne au greffon !" + x.toString());
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Object transformerObjet(Class<?> class1, ObjetLinotte in) {
		Object out = null;
		if (class1.equals(String.class)) {
			out = ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur();
		} else if (class1.equals(Boolean.class)) {
			out = ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur();
		} else if (class1.equals(Integer.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).intValue();
		} else if (class1.equals(BigDecimal.class)) {
			out = ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur();
		} else if (class1.equals(long.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).longValue();
		} else if (class1.equals(BigInteger.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).toBigInteger();
		} else if (class1.equals(int.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).intValue();
		} else if (class1.equals(short.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).shortValue();
		} else if (class1.equals(double.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).doubleValue();
		} else if (class1.equals(Double.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).doubleValue();
		} else if (class1.equals(float.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).floatValue();
		} else if (class1.equals(Float.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).floatValue();
		} else if (class1.equals(boolean.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).intValue() == 1 ? true : false;
		} else if (class1.equals(Boolean.class)) {
			out = ((BigDecimal) ((org.linotte.greffons.externe.Greffon.Acteur) in).getValeur()).intValue() == 1 ? true : false;
		} else if (class1.equals(List.class)) {
			out = new ArrayList();
			Casier c = (Casier) in;
			for (Object o : c) {
				org.linotte.greffons.externe.Greffon.Acteur a = (org.linotte.greffons.externe.Greffon.Acteur) o;
				((List) out).add(transformerObjet(a.getRole() == ROLE.NOMBRE ? BigDecimal.class : String.class, (ObjetLinotte) o));
			}
		}
		// Bogue remonté par momo112
		// Ce n'est pas un type simple Java, on retourne l'objet tel quel. 
		// Permet d'être compatible avec les anciens greffons.
		if (out == null)
			out = in;
		return out;
	}

	public String nom() {
		return ((Slot) method.getAnnotation(Slot.class)).nom();
	}

	public String parametres() {
		StringBuilder out = new StringBuilder("(");
		Class<?>[] pTypes = method.getParameterTypes();
		boolean premier = true;
		for (Class<?> class1 : pTypes) {
			if (!premier)
				out.append(", ");
			if (class1.equals(String.class)) {
				out.append("texte");
			} else if (class1.equals(Boolean.class)) {
				out.append("drapeau");
			} else if (class1.equals(Integer.class)) {
				out.append("nombre");
			} else if (class1.equals(BigDecimal.class)) {
				out.append("nombre");
			} else if (class1.equals(long.class)) {
				out.append("nombre");
			} else if (class1.equals(BigInteger.class)) {
				out.append("nombre");
			} else if (class1.equals(int.class)) {
				out.append("nombre");
			} else if (class1.equals(short.class)) {
				out.append("nombre");
			} else if (class1.equals(double.class)) {
				out.append("nombre");
			} else if (class1.equals(Double.class)) {
				out.append("nombre");
			} else if (class1.equals(float.class)) {
				out.append("nombre");
			} else if (class1.equals(Float.class)) {
				out.append("nombre");
			} else if (class1.equals(boolean.class)) {
				out.append("drapeau");
			} else if (class1.equals(Boolean.class)) {
				out.append("drapeau");
			} else if (class1.equals(List.class)) {
				out.append("casier");
			}
			premier = false;
		}

		out.append(")");
		return out.toString();
	}

}