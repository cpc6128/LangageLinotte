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

import org.linotte.frame.latoile.Couleur;
import org.linotte.moteur.entites.*;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FusionnerAction extends Action implements IProduitCartesien {

	public FusionnerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe fusionner";
	}

	@Override
	@SuppressWarnings("unchecked")
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_FUSIONNER, job, valeurs);
		Acteur a1 = acteurs[0];
		Acteur a2 = acteurs[1];

		if (!(a1.getRole() == Role.ESPECE) && !(a2.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FUSIONNER);

		Prototype e1 = (Prototype) a1;
		Prototype e2 = (Prototype) a2;
		if (!e1.isEspeceGraphique())
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FUSIONNER, e1.toString());
		if (!e2.isEspeceGraphique())
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FUSIONNER, e1.toString());

		PrototypeGraphique g1 = (PrototypeGraphique) a1;
		PrototypeGraphique g2 = (PrototypeGraphique) a2;
		if (!(g2.getTypeGraphique() == PrototypeGraphique.TYPE_GRAPHIQUE.MEGALITHE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_FUSIONNER, g2.toString());

		// Il ne faut pas attendre le refresh de l'affichage qui est plus long :
		g1.refreshShape();
		List<Monolithe> megalithes = (List<Monolithe>) g2.getObject();
		if (g2.getObject() == null) {
			megalithes = new ArrayList<Monolithe>();
			g2.setObject(megalithes);
		}
		megalithes.add(new Monolithe(g1.getShape(), Couleur.retourneCouleur((String) g1.retourneAttribut("couleur").getValeur()), ((BigDecimal) g1
				.retourneAttribut("position").getValeur()).intValue(), (BufferedImage) g1.getObject2()));
		g2.setModifie(true);
		Collections.sort(megalithes);
		if (g2.getToile() != null)
			g2.getToile().getPanelLaToile().setChangement();
		return ETAT.PAS_DE_CHANGEMENT;
	}

}
