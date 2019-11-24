/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
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

package org.linotte.frame.tableau;

import static org.linotte.frame.tableau.TableauBatch.Type.effacer;
import static org.linotte.frame.tableau.TableauBatch.Type.erreur;
import static org.linotte.frame.tableau.TableauBatch.Type.info;
import static org.linotte.frame.tableau.TableauBatch.Type.enligne;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.linotte.frame.Atelier;
import org.linotte.frame.outils.Process;

public class TableauBatch extends Process {

	private List<Entree> entrees = new CopyOnWriteArrayList<Entree>();

	private Atelier atelier;

	protected enum Type {
		info, erreur, effacer, enligne
	};

	private class Entree {

		Type type;
		Object message;

		public Entree(Type type, Object message) {
			this.type = type;
			this.message = message;
		}

		public Entree(Type type) {
			this.type = type;
		}

		public Type getType() {
			return type;
		}

		public Object getMessage() {
			return message;
		}

		public String toString() {
			return message.toString();
		}

	}

	public TableauBatch(long pwait, Atelier pAtelier) {
		super(pwait);
		atelier = pAtelier;
		setPriority(MIN_PRIORITY);
	}

	@Override
	public void action() throws InterruptedException {
		while (entrees.size() > 0) {
			StringBuilder builder = new StringBuilder();
			for (Entree entree : entrees) {
				if (entree.getType() == info
						&& entree.message instanceof String) {
					builder.append(entree);
					builder.append("\n");
				} else if (entree.getType() == enligne) {
					builder.append(entree);
				} else if (entree.getType() == effacer) {
					// On affiche ce qu'il y a a afficher !
					if (builder.length() > 0) {
						atelier.ecrireTableau(builder.toString());
						builder.setLength(0);
					}
					// Et on l'efface !
					atelier.effacerTableau();
				} else {
					if (builder.length() > 0) {
						atelier.ecrireTableau(builder.toString());
						builder.setLength(0);
					}
					atelier.ecrireErreurTableau_(entree.getMessage());
					if (entree.message instanceof String)
						atelier.ecrireErreurTableau_("\n");
				}
				entrees.remove(entree);
			}
			if (builder.length() > 0)
				atelier.ecrireTableau(builder.toString());
		}
	}

	public void ecrireTableau(Object s) {
		entrees.add(new Entree(info, s));
	}

	public void ecrireTableauEnLigne(String s) {
		entrees.add(new Entree(enligne, s));
	}

	public void ecrireErreur(String s) {
		entrees.add(new Entree(erreur, s));
	}

	public void effacerTableau() {

		entrees.add(new Entree(effacer));
		// synchronized (copytoDisplay) {
		// toDisplay.clear();
		// }
	}
}
