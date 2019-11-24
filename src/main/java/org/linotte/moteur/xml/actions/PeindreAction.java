/***********************************************************************
 * Linotte                                                             *
 * Version release date : August 26, 2009                           *
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.FichierOutils;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.scene.SceneHandler;

public class PeindreAction extends Action implements IProduitCartesien {

	public PeindreAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe peindre";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_PEINDRE, job, valeurs)[0];

		if (a.getRole().equals(Role.TEXTE)) {
			String chemin = Ressources.construireChemin((String) a.getValeur());
			// System.out.println(chemin);
			SceneHandler handler = new SceneHandler(runtimeContext.getLinotte().getRegistreDesEtats(), job);
			try {
				handler.chargerScene(new ByteArrayInputStream(FichierOutils.lire(new File(chemin)).getBytes()));
			} catch (IOException e) {
				e.printStackTrace();
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_PEINDRE, (String) a.getValeur());
			}
			if (handler.getErreur() != null)
				throw handler.getErreur();
		} else {
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_PEINDRE, a.toString());
		}
		return ETAT.PAS_DE_CHANGEMENT;
	}

}
