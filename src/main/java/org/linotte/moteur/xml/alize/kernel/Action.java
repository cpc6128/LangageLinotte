/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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
 ***********************************************************************/

package org.linotte.moteur.xml.alize.kernel;

import java.math.BigDecimal;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.analyse.ItemXML;

public abstract class Action {

	public enum ETAT {
		PAS_DE_CHANGEMENT, SOUS_PARAGRAPHE,
		// Alizé :
		ETAT_SECONDAIRE, SAUTER_PARAGRAPHE, PARCOURIR, REVENIR, FIN_IMPORTATION
	};

	public abstract String clef();

	public abstract ETAT analyse(String param, Job runtime, ItemXML[] valeurs, String[] annotations) throws Exception;

	public Acteur[] extractionDesActeurs(int erreur, Job moteur, ItemXML[] valeurs) throws Exception {
		return extractionDesActeurs(erreur, moteur, valeurs, true);
	}

	public Acteur[] extractionDesActeursSansErreur(int erreur, Job moteur, ItemXML[] valeurs) throws Exception {
		return extractionDesActeurs(erreur, moteur, valeurs, false);
	}

	private Acteur[] extractionDesActeurs(int erreur, Job moteur, ItemXML[] valeurs, boolean error) throws Exception {
		Acteur[] acteurs = new Acteur[valeurs.length];
		Object valeur;
		int pos = 0;
		try {
			for (ItemXML item : valeurs) {
				valeur = item.retourneValeurTransformee(moteur);
				if (valeur instanceof Acteur)
					acteurs[pos++] = (Acteur) valeur;
				else if (valeur instanceof BigDecimal)
					acteurs[pos++] = new Acteur(Role.NOMBRE, valeur);
				else
					acteurs[pos++] = new Acteur(Role.TEXTE, valeur);
			}
		} catch (ErreurException ee) {
			if (error)
				throw ee;
		}
		return acteurs;
	}
}