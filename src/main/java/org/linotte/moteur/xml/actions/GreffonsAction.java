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

import java.util.List;

import org.linotte.frame.greffons.BeanGreffon;
import org.linotte.frame.greffons.BeanHandler;
import org.linotte.greffons.GestionDesGreffons;
import org.linotte.greffons.GreffonsChargeur;
import org.linotte.greffons.api.Greffon;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;
import org.linotte.moteur.xml.analyse.ItemXML;

public class GreffonsAction extends Action implements IProduitCartesien {

	public GreffonsAction() {
		super();
	}

	@Override
	public String clef() {
		return "vérifier greffons";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		if (runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
			throw new ErreurException(Constantes.MODE_CONSOLE);
		}

		List<BeanGreffon> beans = null;

		for (ItemXML nom : valeurs) {
			String cible = nom.getValeurBrute();
			Greffon g = GreffonsChargeur.getInstance().getGreffon(cible);
			if (g == null) {
				if (beans == null) {
					beans = new BeanHandler().chargerBeanGreffon(Version.URL_GREFFONS);
				}
				boolean trouve = false;
				for (BeanGreffon greffon : beans) {
					if (greffon.getNom().equalsIgnoreCase(cible)) {
						String commande = "1 " + greffon.getNom() + " " + greffon.getVersion() + " \"" + greffon.getUrl() + "\"";
						//System.out.println("Simulation : " + commande);
						
						try {
							trouve = GestionDesGreffons.chargerGreffon(commande, runtimeContext.getLinotte(), false);
						} catch (Exception e) {
							throw new ErreurException(Constantes.SYNTAXE_ESPECE_INCONNUE, e.toString());
						}
					}

				}

				if (!trouve)
					throw new ErreurException(Constantes.SYNTAXE_ESPECE_INCONNUE, cible);

			}
		}

		return ETAT.PAS_DE_CHANGEMENT;

	}
}
