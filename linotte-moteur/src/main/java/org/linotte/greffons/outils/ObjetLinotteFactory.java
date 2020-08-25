/***********************************************************************
 * Linotte                                                             *
 * Version release date : January 11, 2009                             *
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

package org.linotte.greffons.outils;

import org.linotte.greffons.externe.Greffon.Espece;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.greffons.externe.Greffon.ObjetLinotte;
import org.linotte.greffons.externe.Greffon.ROLE;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.api.Librairie;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ObjetLinotteFactory {

	public static final String CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT = "___CLEF_PROTOTYPE_IDENTIFIANT";

	@SuppressWarnings({"unchecked"})
	public static ObjetLinotte copy(Acteur a) throws ErreurException, GreffonException {
		if (a instanceof Casier) {
			Casier c = (Casier) a;
			org.linotte.greffons.externe.Greffon.Casier casier = new org.linotte.greffons.externe.Greffon.Casier(role(c.roleContenant()));
			Iterator<Acteur> i = ((List<Acteur>) c.getValeur()).iterator();
			while (i.hasNext()) {
				Acteur temp = i.next();
				casier.add(copy(temp));
			}
			return casier;
		} else if (a instanceof Prototype) {
			Prototype e = (Prototype) a;
			org.linotte.greffons.externe.Greffon.Espece espece = new org.linotte.greffons.externe.Greffon.Espece(e.getType());
			// Si c'est une espèce, on écrit les attributs par ordre
			// alphabétique :
			Map<String, Acteur> m = e.retourAttributsMap();
			Object[] clefs = m.keySet().toArray();
			Arrays.sort(clefs);
			for (Object clef : clefs) {
				espece.add((org.linotte.greffons.externe.Greffon.Acteur) copy(e.retourneAttribut((String) clef)));
			}

			org.linotte.greffons.externe.Greffon.Acteur levrai = new org.linotte.greffons.externe.Greffon.Acteur(ROLE.NOMBRE,
					CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT, new BigDecimal(e.prototypeID));
			espece.add(levrai);

			espece.porteur = a;
			return espece;
		} else {
			// Acteur simple 
			org.linotte.greffons.externe.Greffon.Acteur acteur = new org.linotte.greffons.externe.Greffon.Acteur(role(a.getRole()));
			if (a.getNom() != null)
				acteur.setClef(a.getNom().toString());
			acteur.setValeur(a.getValeur());//String.valueOf(
			return acteur;
		}
	}

	private static ROLE role(Role roleContenant) {
		switch (roleContenant) {
		case CASIER:
			return ROLE.CASIER;
		case ESPECE:
			return ROLE.ESPECE;
		case NOMBRE:
			return ROLE.NOMBRE;
		case TEXTE:
			return ROLE.TEXTE;
		}
		return null;
	}

	public static ObjetLinotte createStructure(Acteur a, Librairie<?> librairie) throws ErreurException, GreffonException {
		if (a instanceof Casier) {
			Casier c = (Casier) a;
			org.linotte.greffons.externe.Greffon.Casier casier = new org.linotte.greffons.externe.Greffon.Casier(role(c.roleContenant()));
			Role role = c.roleContenant();
			if (role == Role.ESPECE) {
				casier.add(createStructure(librairie.creationEspece(c.getTypeespece(), "", null), librairie));
			} else {
				casier.add(createStructure(new Acteur(c.roleContenant(), null), librairie));
			}
			return casier;
		} else if (a instanceof Prototype) {
			Prototype e = (Prototype) a;
			org.linotte.greffons.externe.Greffon.Espece espece = new org.linotte.greffons.externe.Greffon.Espece(e.getType());
			// Si c'est une espèce, on écrit les attributs par ordre
			// alphabétique :
			Map<String, Acteur> m = e.retourAttributsMap();
			Object[] clefs = m.keySet().toArray();
			Arrays.sort(clefs);
			for (Object clef : clefs) {
				espece.add((org.linotte.greffons.externe.Greffon.Acteur) createStructure(e.retourneAttribut((String) clef), librairie));
			}

			return espece;
		} else {
			// Acteur simple 
			org.linotte.greffons.externe.Greffon.Acteur acteur = new org.linotte.greffons.externe.Greffon.Acteur(role(a.getRole()));
			acteur.setClef(a.getNom() != null ? a.getNom().toString() : null);
			acteur.setValeur(a.getValeur());
			return acteur;
		}
	}

	public static void copy(ObjetLinotte aller, Acteur a, Librairie<?> librairie) throws ErreurException {
		if (aller instanceof org.linotte.greffons.externe.Greffon.Casier) {
			if (!(a instanceof Casier)) {
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_GREFFON, a.toString());
			}
			Casier c = (Casier) a;
			org.linotte.greffons.externe.Greffon.Casier casier = (org.linotte.greffons.externe.Greffon.Casier) aller;
			for (ObjetLinotte object : casier) {
				Acteur temp;
				if (c.roleContenant() == Role.ESPECE) {
					temp = librairie.creationEspece(c.getTypeespece(), "", null);
				} else {
					temp = new Acteur(c.roleContenant(), null);
				}
				copy(object, temp, librairie);
				c.ajouterValeur(temp, true);
			}
		} else if (aller instanceof org.linotte.greffons.externe.Greffon.Espece) {
			if (!(a instanceof Prototype)) {
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_GREFFON, a.toString());
			}
			Prototype e = (Prototype) a;
			org.linotte.greffons.externe.Greffon.Espece espece = (org.linotte.greffons.externe.Greffon.Espece) aller;
			for (org.linotte.greffons.externe.Greffon.Acteur object : espece) {
				copy(object, e.retourneAttribut(object.getClef()), librairie);
			}
		} else {
			// Acteur simple 
			org.linotte.greffons.externe.Greffon.Acteur acteur = (org.linotte.greffons.externe.Greffon.Acteur) aller;
			if (acteur != null) {
				if (a.getRole() == Role.NOMBRE) {
					BigDecimal r = Mathematiques.isBigDecimal(String.valueOf(acteur.getValeur()));
					if (r != null)
						a.setValeur(r);
					else
						throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_GREFFON, a.toString());
				} else {
					a.setValeur(acteur.getValeur());
				}
			} else {
				a.setValeur(null);
			}
		}
	}

	public static Acteur copy(ObjetLinotte retour, Librairie<?> librairie) throws ErreurException {
		if (retour instanceof org.linotte.greffons.externe.Greffon.Casier) {
			// Casier
			org.linotte.greffons.externe.Greffon.Casier casier = (org.linotte.greffons.externe.Greffon.Casier) retour;
			Casier a = new Casier(null, null, casier.getRoleContenant() == ROLE.TEXTE ? Role.TEXTE : Role.NOMBRE, null);
			for (ObjetLinotte o : casier) {
				a.ajouterValeur(copy(o, librairie), true);
			}
			return a;
		} else if (retour instanceof org.linotte.greffons.externe.Greffon.Espece) {
			org.linotte.greffons.externe.Greffon.Espece espece = (Espece) retour;
			Prototype modele = librairie.getEspece(espece.getType());
			for (org.linotte.greffons.externe.Greffon.Acteur acteur : espece) {
				if (acteur.getClef() != null && !acteur.getClef().equals(CLEF_PROTOTYPE_ORIGINAL_IDENTIFIANT)) {
					Acteur a = modele.retourneAttribut(acteur.getClef());
					if (a == null)
						throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_GREFFON, acteur.getClef());
					a.setValeur(acteur.getValeur());
				}
			}
			return modele;
		} else {
			// Acteur simple 
			org.linotte.greffons.externe.Greffon.Acteur acteur = (org.linotte.greffons.externe.Greffon.Acteur) retour;
			if (acteur != null) {
				if (acteur.getRole() == ROLE.NOMBRE || acteur.getRole() == ROLE.DRAPEAU) {
					// C'est un nombre :
					Acteur a = new Acteur(Role.NOMBRE, null);
					BigDecimal r = Mathematiques.isBigDecimal(String.valueOf(acteur.getValeur()));
					if (r != null)
						a.setValeur(r);
					else
						throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_GREFFON, a.toString());
					return a;
				} else {
					Acteur a = new Acteur(Role.TEXTE, null);
					a.setValeur(acteur.getValeur());
					return a;
				}
			} else {
				Acteur a = new Acteur(Role.NOMBRE, null);
				a.setValeur(null);
				return a;
			}
		}
	}
}