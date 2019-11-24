/***********************************************************************
 * Linotte                                                             *
 * Version release date : January 11, 2011                             *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                           *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 *                                                                     *
 ***********************************************************************/

package org.linotte.greffons.java;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.linotte.greffons.api.AKMethod;
import org.linotte.greffons.api.Greffon;
import org.linotte.greffons.api.Greffon.Attribut;
import org.linotte.greffons.externe.Greffon.DocumentationHTML;
import org.linotte.greffons.externe.Greffon.Slot;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;

public class GreffonPrototype {

	public static class Entrée implements Entry<String, AKMethod> {

		AKMethod method;
		String key;

		public Entrée(String key, AKMethod method) {
			this.key = key;
			this.method = method;
		}

		public String getKey() {
			return key;
		}

		public AKMethod getValue() {
			return method;
		}

		public AKMethod setValue(AKMethod value) {
			return null;
		}

	}

	/**
	 * Récupère tous les slots du prototype
	 * @param classgreffon
	 * @return
	 */
	public static List<Entrée> chargerSlotJava(Greffon greffon) {

		List<Entrée> retour = new ArrayList<Entrée>();
		Class<? extends org.linotte.greffons.externe.Greffon> classgreffon = greffon.getClass_();
		// Dans le cas d'un greffon JAVA :
		for (Method method : classgreffon.getMethods()) {
			if (method.isAnnotationPresent(Slot.class)) {
				if (!method.getAnnotation(Slot.class).nom().isEmpty())
					retour.add(new Entrée(method.getAnnotation(Slot.class).nom(), new JavaMethod(method)));
				else
					retour.add(new Entrée(method.getName(), new JavaMethod(method)));
			}
		}
		return retour;
	}

	/**
	 * Récupère tous les slots du prototype
	 * @param classgreffon
	 * @return
	 */
	public static List<Entrée> chargerSlotJava(final Class<? extends org.linotte.greffons.externe.Greffon> classgreffon) {
		List<Entrée> retour = new ArrayList<Entrée>();
		// Dans le cas d'un greffon JAVA :
		for (Method method : classgreffon.getMethods()) {
			if (method.isAnnotationPresent(Slot.class)) {
				if (!method.getAnnotation(Slot.class).nom().isEmpty())
					retour.add(new Entrée(method.getAnnotation(Slot.class).nom(), new JavaMethod(method)));
				else
					retour.add(new Entrée(method.getName(), new JavaMethod(method)));
			}
		}
		return retour;
	}

	public static String vérifierDocumentation(Class<? extends org.linotte.greffons.externe.Greffon> greffon) {
		if (greffon.isAnnotationPresent(DocumentationHTML.class))
			return greffon.getAnnotation(DocumentationHTML.class).value();
		return null;
	}

	public static void chargeAttributGreffon(Prototype espece, Greffon greffon) {
		Map<String, Attribut> attributs = greffon.getAttributs();
		for (String attribut : attributs.keySet()) {
			Attribut att = attributs.get(attribut);
			if ("nombre".equalsIgnoreCase(att.getType())) {
				Acteur temp = new Acteur(null, attribut, Role.NOMBRE, new BigDecimal(att.getValeur()), null);
				espece.addAttribut(temp);
			} else {
				Acteur temp = new Acteur(null, attribut, Role.TEXTE, att.getValeur(), null);
				espece.addAttribut(temp);
			}
		}
	}

}
