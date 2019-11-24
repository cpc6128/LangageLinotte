package org.linotte.frame.favoris;

import java.net.URI;

/***********************************************************************
 * Langage Linotte                                                     *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 3 or       *
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

public class Favoris implements java.lang.Comparable<Favoris> {

	// Pas l'objet File sinon, il ne peut être sérialisé.
	private URI fichierCible;

	private int positionFichier;

	private String nom;

	public Favoris() {
	}

	public Favoris(URI fichierCible, int positionFichier, String nom) {
		super();
		this.fichierCible = fichierCible;
		this.positionFichier = positionFichier;
		this.nom = nom;
	}

	public URI getFichierCible() {
		return fichierCible;
	}

	public void setFichierCible(URI fichierCible) {
		this.fichierCible = fichierCible;
	}

	public int getPositionFichier() {
		return positionFichier;
	}

	public void setPositionFichier(int positionFichier) {
		this.positionFichier = positionFichier;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public void insert(int length, int offset) {
		if (offset < positionFichier) {
			positionFichier = positionFichier + length;
		}
	}

	public void remove(int length, int offset) {
		if (offset < positionFichier) {
			positionFichier = positionFichier - length;
		}
	}

	public String toString() {
		return nom;
	}

	@Override
	public int compareTo(Favoris o) {
		return o.toString().compareTo(toString());
	}

}
