/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                             *
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

package org.linotte.frame.latoile.recepteur;

import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.latoile.LaToileListener;
import org.linotte.frame.latoile.Recepteur;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.ErreurException;

import java.math.BigDecimal;

public class ScribeRecepteur implements Recepteur {

	private PrototypeGraphique especeGraphique;
	private LaToile toile;

	private boolean actif = false;

	public ScribeRecepteur(PrototypeGraphique p, LaToile ptoile /* null depuis multitoile*/ ) {
		especeGraphique = p;
		toile = ptoile;
	}

	public boolean evenementSouris() {
		if (!isActif())
			return false;
		// System.out.println("recepteur souris !");
		return true;
	}

	public void reveillerMoteur() {
		synchronized (toile.getInterpreteur().getRegistreDesEtats().getTemporiser()) {
			toile.getInterpreteur().getRegistreDesEtats().getTemporiser().notify();
		}
	}

	public boolean evenementTouche(String touche) {
		if (!isActif())
			return false;
		// System.out.println("recepteur touche : " + touche);
		if (touche.equals(LaToileListener.TOUCHE_ENTREE) || touche.equals(LaToileListener.SOURIS_CLIQUE)) {
			setActif(false);
			reveillerMoteur();
			return true;
		}
		if (touche.equals(LaToileListener.TOUCHE_ESPACE)) {
			ajouterCaractere(" ");
		}
		if (touche.equals(LaToileListener.TOUCHE_DEL)) {
			delCaractere();
		}
		if (touche.length() == 1) {
			// Affichage du texte :
			ajouterCaractere(touche);
		}
		return true;
	}

	private void ajouterCaractere(String touche) {
		try {
			String texte = (String) especeGraphique.retourneAttribut("texte").getValeur();
			int taquet = ((BigDecimal) especeGraphique.retourneAttribut("taquet").getValeur()).intValue();
			if (texte.length() >= taquet)
				return;
			texte += touche;
			especeGraphique.retourneAttribut("texte").setValeur(texte);
		} catch (ErreurException e) {
			e.printStackTrace();
		}
	}

	private void delCaractere() {
		try {
			String texte = (String) especeGraphique.retourneAttribut("texte").getValeur();
			if (texte.length() != 0) {
				texte = texte.substring(0, texte.length() - 1);
				especeGraphique.retourneAttribut("texte").setValeur(texte);
			}
		} catch (ErreurException e) {
			e.printStackTrace();
		}
	}

	public void setActif(boolean b) {
		actif = b;
	}

	public boolean isActif() {
		try {
			if (actif && "oui".equalsIgnoreCase((String) especeGraphique.retourneAttribut("visible").getValeur())) {
				return true;
			}
		} catch (ErreurException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof PrototypeGraphique)
			return object.equals(especeGraphique);
		return false;
	}
	
	public void setToile(LaToile toile) {
		this.toile = toile;
	}
}
