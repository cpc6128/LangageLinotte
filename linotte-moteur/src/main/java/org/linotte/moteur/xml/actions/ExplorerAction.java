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

import org.alize.security.Habilitation;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;

public class ExplorerAction extends Action implements IProduitCartesien {

	private final String CLEF_INTERNET = "internet ";

	private final String CLEF_FICHIER = "fichier ";

	private final String CLEF_COURRIER = "courrier ";

	private final String CLEF_OUVRIR = "ouvrir ";

	private final String CLEF_IMPRIMER = "imprimer ";

	private final String CLEF_EDITER = "editer ";

	private final String CLEF_COLORISER = "coloriser ";

	public ExplorerAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe explorer";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {

		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		if (runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
			throw new ErreurException(Constantes.MODE_CONSOLE);			
		}			
		
		Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_EXPLORER, job, valeurs)[0];

		if (!(a.getRole() == Role.TEXTE))
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXPLORER, a.toString());

		String adresse = a.getValeur().toString();
		String decoupe = adresse;

		try {
			if (adresse.toLowerCase().startsWith(CLEF_INTERNET)) {
				decoupe = adresse.substring(CLEF_INTERNET.length());
				java.awt.Desktop.getDesktop().browse(new URI(decoupe));
			} else if (adresse.toLowerCase().startsWith(CLEF_FICHIER)) {
				decoupe = adresse.substring(CLEF_FICHIER.length());
				java.awt.Desktop.getDesktop().open(new File(Ressources.construireChemin(decoupe)));
			} else if (adresse.toLowerCase().startsWith(CLEF_COURRIER)) {
				decoupe = adresse.substring(CLEF_COURRIER.length());
				java.awt.Desktop.getDesktop().mail(new URI("mailto:" + decoupe));
			} else if (adresse.toLowerCase().startsWith(CLEF_OUVRIR)) {
				decoupe = adresse.substring(CLEF_OUVRIR.length());
				java.awt.Desktop.getDesktop().open(new File(Ressources.construireChemin(decoupe)));
			} else if (adresse.toLowerCase().startsWith(CLEF_IMPRIMER)) {
				decoupe = adresse.substring(CLEF_IMPRIMER.length());
				java.awt.Desktop.getDesktop().print(new File(Ressources.construireChemin(decoupe)));
			} else if (adresse.toLowerCase().startsWith(CLEF_EDITER)) {
				decoupe = adresse.substring(CLEF_EDITER.length());
				java.awt.Desktop.getDesktop().edit(new File(Ressources.construireChemin(decoupe)));
			} else if (adresse.toLowerCase().startsWith(CLEF_COLORISER)) {
				decoupe = adresse.substring(CLEF_COLORISER.length());
				try {
					UIManager.setLookAndFeel(decoupe);
					JFrame.setDefaultLookAndFeelDecorated(true);
					JDialog.setDefaultLookAndFeelDecorated(true);
					for (Frame e : Frame.getFrames()) {
						try {
							SwingUtilities.updateComponentTreeUI(e);
							Thread.sleep(200);
						} catch (Exception e1) {
							// e1.printStackTrace();
						}
						e.pack();
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
			} else {
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXPLORER, decoupe);
			}
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			throw new ErreurException(Constantes.OPERATION_NON_SUPORTEE, decoupe);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_EXPLORER, decoupe);
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
