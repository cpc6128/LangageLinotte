/***********************************************************************
 * Linotte                                                             *
 * Version release date : Febrary 04, 2008                             *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                        *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                            *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.xml.actions;

import org.linotte.greffons.externe.Graphique;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ModifierAction extends Action implements IProduitCartesien {

	public ModifierAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe modifier";
	}

	@SuppressWarnings("deprecation")
	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_MODIFIER, linotte, valeurs);
		Acteur acteur1 = acteurs[0];
		Acteur acteur2 = acteurs[1];
		Acteur acteur3 = acteurs[2];
		Acteur acteur4 = acteurs[3];

		if (!(acteur2.getRole() == Role.TEXTE) && !(acteur3.getRole() == Role.TEXTE) && !(acteur4.getRole() == Role.TEXTE)
				&& !(acteur1.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_MODIFIER);

		Prototype e = (Prototype) acteur1;
		if (!e.isEspeceGraphique())
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_MODIFIER, e.toString());

		PrototypeGraphique eg = (PrototypeGraphique) e;
		if (!eg.getTypeGraphique().equals(PrototypeGraphique.TYPE_GRAPHIQUE.GREFFON))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_MODIFIER, e.toString());

		Greffon greffon = eg.getGreffon();
		if (greffon instanceof Graphique) {
			Graphique graphique = (Graphique) greffon;
			String value = (String) acteur2.getValeur();
			String attribute = (String) acteur3.getValeur();
			String id = (String) acteur4.getValeur();
			try {
				graphique.modifier(value, attribute, id);
			} catch (GreffonException e1) {
				throw new ErreurException(Constantes.ERREUR_GREFFON, e1.getMessage());
			}
		} else {
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_MODIFIER, e.toString());
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

}
