/***********************************************************************
 * Linotte                                                             *
 * Version release date : April 7, 2011                                *
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

package org.linotte.moteur.xml.actions;

import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.Trace;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.analyse.Mathematiques.ANGLE;

import java.math.MathContext;
import java.math.RoundingMode;

public class TildeAction extends Action implements IProduitCartesien {

    public static final String NOM_ETAT = "verbe tilde";

    private static final String PRECISION = "précision ";

    public static final String GREFFON = "greffon ";

    public static final String S_ANGLE = "angle";

	public static final String TRACE = "trace ";

	public enum STATUS {
		A_JOUR, NOUVEAU, METTRE_A_JOUR
	}

	public TildeAction() {
		super();
	}

	@Override
	public String clef() {
		return NOM_ETAT;
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
        final String commande = valeurs[0].getValeurBrute();
        RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
        if (commande.startsWith(PRECISION)) {
            try {
                int valeur = Integer.valueOf(commande.substring(PRECISION.length()).trim());
                Mathematiques.context = new MathContext(valeur, RoundingMode.HALF_UP);
            } catch (Exception e) {
                runtimeContext.getIhm().afficherErreur("Impossible de fixer la précision arithmétique !");
            }
        } else if (commande.startsWith(S_ANGLE)) {
            try {
                String valeur = commande.substring(S_ANGLE.length()).trim();
                if (valeur.equalsIgnoreCase("radian"))
					Mathematiques.angle = ANGLE.RADIAN;
				else
					Mathematiques.angle = ANGLE.DEGREE;
			} catch (Exception e) {
				runtimeContext.getIhm().afficherErreur("Impossible de fixer la précision arithmétique !");
			}
		} else if (commande.startsWith(TRACE)) {
			
			if (runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
				throw new ErreurException(Constantes.MODE_CONSOLE);			
			}		
			
			try {
				Trace.active = Integer.valueOf(commande.substring(TRACE.length()).trim()) == 1;
			} catch (Exception e) {
				runtimeContext.getIhm().afficherErreur("Impossible d'activer ou désactiver les traces !");
			}
		} else {
			runtimeContext.getIhm().afficherErreur("commande inconnue !");
		}
		return ETAT.PAS_DE_CHANGEMENT;

	}

}
