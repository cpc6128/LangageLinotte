/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class MelangerAction extends Action implements IProduitCartesien {

	public MelangerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe mélanger";
	}

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		//		linotte.debug("On mélange : ");

		Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_MELANGER, linotte, valeurs)[0];

		if (a.getRole() == Role.ESPECE)
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_MELANGER, a.toString());

		if (a.getRole() == Role.CASIER) {
			Casier c = (Casier) a;
			c.melanger();
		} else if (a.getRole() == Role.TEXTE) {
			String s = (String) a.getValeur();
			int taille = s.length();
			List<String> nouvelle = new ArrayList<String>(taille);
			for (int i = 0; i < taille; i++)
				nouvelle.add(s.substring(i, i + 1));
			Collections.shuffle(nouvelle);
			StringBuilder buffer = new StringBuilder(taille);
			Iterator<String> i = nouvelle.iterator();
			while (i.hasNext())
				buffer.append(i.next());
			a.setValeur(buffer.toString());
		} else if (a.getRole() == Role.NOMBRE)
			a.setValeur(new BigDecimal(Math.floor(((BigDecimal) a.getValeur()).doubleValue() * Math.random())));

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
