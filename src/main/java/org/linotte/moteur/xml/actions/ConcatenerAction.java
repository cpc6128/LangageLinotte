/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 23, 2007                                *
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

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class ConcatenerAction extends Action implements IProduitCartesien {

	@Override
	public ETAT analyse(String param, Job linotte, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_CONCATENER, linotte, valeurs);

		int taille = acteurs.length;

		// Vérification des rôles :
		for (int i = 1; i < taille; i++)
			if (!(acteurs[i].getRole() == Role.TEXTE || acteurs[i].getRole() == Role.NOMBRE))
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CONCATENER, acteurs[i].toString());

		if (!(acteurs[taille - 1].getRole() == Role.TEXTE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_CONCATENER, acteurs[0].toString());

		StringBuilder builder = new StringBuilder();

		// A fussionne les valeurs :
		for (int i = 0; i < taille - 1; i++)
			builder.append(acteurs[i].getValeur().toString());

		// Affectation de la valeur
		acteurs[taille - 1].setValeur(builder.toString());

		return ETAT.PAS_DE_CHANGEMENT;
	}

	@Override
	public String clef() {
		return "verbe concaténer";
	}

}
