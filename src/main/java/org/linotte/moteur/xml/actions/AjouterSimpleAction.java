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

import org.linotte.greffons.externe.Composant;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.RoleException;
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.ItemXML;

public class AjouterSimpleAction extends Action implements IProduitCartesien {

	public AjouterSimpleAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe ajouter simple";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();

		Acteur[] acteurs = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_AJOUTER, job, valeurs);
		Acteur a1 = acteurs[1];
		Acteur a2 = acteurs[0];

		if (a2.getRole() == Role.ESPECE) {
			Prototype e2 = (Prototype) a2;
			if (a1.getRole() == Role.ESPECE) {
				Prototype e1 = (Prototype) a1;

				if ((e1.getGreffon() != null && e1.getGreffon() instanceof Composant) && (e2.getGreffon() != null && e2.getGreffon() instanceof Composant)) {
					try {
						Composant pere = (Composant) e2.getGreffon();
						Composant fils = (Composant) e1.getGreffon();
						pere.initialisation();
						fils.initialisation();

						if (pere.enregistrerPourDestruction() && !runtimeContext.getComposants().contains(pere)) {
							runtimeContext.getComposants().add(pere);
						}
						if (fils.enregistrerPourDestruction() && !runtimeContext.getComposants().contains(fils)) {
							runtimeContext.getComposants().add(fils);
						}

						pere.ajouterComposant(fils);
						return ETAT.PAS_DE_CHANGEMENT;
					} catch (GreffonException e) {
						throw new ErreurException(Constantes.ERREUR_GREFFON, e.getMessage());
					} catch (Exception e) {
						e.printStackTrace();
						throw new ErreurException(Constantes.ERREUR_GREFFON, e.getMessage());
					}
				} else
					throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_AJOUTER);
			} else
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_AJOUTER);
		}

		if (!(a1.getRole() == a2.getRole())) {
			if (!((a1.getRole() == Role.NOMBRE) && a2.getRole() == Role.TEXTE))
				if (!(a2.getRole() == Role.CASIER))
					throw new RoleException(Constantes.SYNTAXE_PARAMETRE_AJOUTER,a2, Role.CASIER);
				else {
					if (!(a1.getRole() == ((Casier) a2).roleContenant()))
						throw new RoleException(Constantes.SYNTAXE_PARAMETRE_AJOUTER, a2.getNom().toString(), ((Casier) a2).roleContenant(), a1.getRole());
				}
		}

		if (a1.getRole() == Role.NOMBRE && a2.getRole() == Role.TEXTE)
			a2.setValeur(((String) a2.getValeur()) + ((BigDecimal) a1.getValeur()).toString());
		else if (a1.getRole() == Role.TEXTE && a2.getRole() == Role.TEXTE)
			a2.setValeur(((String) a2.getValeur()) + ((String) a1.getValeur()));
		else if (a1.getRole() == Role.NOMBRE && a2.getRole() == Role.NOMBRE)
			a2.setValeur(((BigDecimal) a2.getValeur()).add((BigDecimal) a1.getValeur()));
		else if (!(a1.getRole() == Role.CASIER) && a2.getRole() == Role.CASIER)
			((Casier) a2).ajouterValeur(a1, true);
		else if (a1.getRole() == Role.CASIER && a2.getRole() == Role.CASIER && ((Casier) a2).roleContenant() == Role.CASIER)
			((Casier) a2).ajouterValeur(a1, true);
		else
			try {
				((Casier) a2).addAll((Casier) a1);
			} catch (SyntaxeException e1) {
				e1.printStackTrace();
			}

		return ETAT.PAS_DE_CHANGEMENT;
	}

}
