/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

package org.linotte.frame.cahier.sommaire;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Noeud {

	private int position;

	private String nom;

	private String prototype;

	private boolean erreurSyntaxe = false;

	/** Est-ce que le noeud est actif ?*/
	private boolean actif;

	private List<String> doublures = new ArrayList<String>();

	public Noeud(String prototype, String nom, int position) {
		super();
		this.nom = nom;
		this.position = position;
		this.prototype = prototype;
	}

	@Override
	public String toString() {
		if (doublures.size() > 0) {
			StringBuilder builder = new StringBuilder().append(nom).append("(");
			Iterator<String> i = doublures.iterator();
			while (i.hasNext()) {
				builder.append(i.next()).append(i.hasNext() ? ", " : ")");
			}
			return erreur() + builder.toString();
		} else {
			return erreur() + nom;
		}
	}

	private String erreur() {
		return "";//erreurSyntaxe ? "\u02BC " : "";
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public void addDoublure(String string) {
		if (!doublures.contains(string)) {
			//nom = nom + ", " + string;
			doublures.add(string);
		}
	}

	public String getPrototype() {
		return prototype;
	}

	public boolean isErreurSyntaxe() {
		return erreurSyntaxe;
	}

	public void setErreurSyntaxe(boolean erreurSyntaxe) {
		this.erreurSyntaxe = erreurSyntaxe;
	}

	public boolean isActif() {
		return actif;
	}

	public void setActif(boolean actif) {
		this.actif = actif;
	}

}