/***********************************************************************
 * Linotte                                                             *
 * Version release date : January 11, 2006                             *
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
 *                 Lexer                                               *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.xml.alize.lexer;

import java.util.Arrays;

import org.linotte.moteur.xml.exception.NouvelleLigne;

public class MotAMotCache {

	private static final int BUFFER_MIN = 10;

	private String[] cachesMots = new String[BUFFER_MIN]; // Mot ou reste ligne

	private int[] cachesPosition = new int[BUFFER_MIN]; // Position

	private boolean[] cachesFin = new boolean[BUFFER_MIN]; // Fin

	private int[] cachesDernierePosition = new int[BUFFER_MIN]; // derniere position

	private boolean[] cachesDebut = new boolean[BUFFER_MIN]; // Debut

	private int[] cachesLigne = new int[BUFFER_MIN]; // derniere position

	private int nombreMotsEnCache = 0;

	private int[] relationPositionMot;

	protected int[] historique;

	protected int position = 0;

	protected int dernierePosition = 0;

	protected int ligneCourante = 0;

	// Gestion fin de lignes :

	protected boolean debutDeLigne = true;

	protected boolean finDeLigne = false;

	protected boolean faireExeptionFinDeLigne = false;

	public MotAMotCache(int fichierTailleMax) {

		relationPositionMot = new int[fichierTailleMax + 1];
		historique = new int[fichierTailleMax + 1];
		Arrays.fill(relationPositionMot, -1);

	}

	public void addMot(String mot, int positionFinDuMot) {

		if (relationPositionMot[positionFinDuMot] != -1)
			return;

		cachesMots[nombreMotsEnCache] = mot;
		cachesPosition[nombreMotsEnCache] = position;
		cachesFin[nombreMotsEnCache] = finDeLigne;
		cachesDernierePosition[nombreMotsEnCache] = positionFinDuMot;
		cachesDebut[nombreMotsEnCache] = debutDeLigne;
		cachesLigne[nombreMotsEnCache] = ligneCourante;
		relationPositionMot[positionFinDuMot] = nombreMotsEnCache;
		historique[position] = positionFinDuMot;
		agrandirChaine();
	}

	public String motSuivantEnCache() {

		int positionLecture = relationPositionMot[position];

		if (positionLecture == -1)
			return null;

		String mot = cachesMots[positionLecture];
		position = cachesPosition[positionLecture];
		finDeLigne = cachesFin[positionLecture];
		dernierePosition = cachesDernierePosition[positionLecture];
		debutDeLigne = cachesDebut[positionLecture];
		ligneCourante = cachesLigne[positionLecture];

		faireExeptionFinDeLigne = finDeLigne;

		return mot;
	}

	private void agrandirChaine() {
		nombreMotsEnCache++;
		if (nombreMotsEnCache >= cachesMots.length) {
			int d = cachesMots.length, d2 = d * 2;

			String[] c1 = new String[d2];
			int[] c2 = new int[d2];
			boolean[] c3 = new boolean[d2];
			int[] c4 = new int[d2];
			boolean[] c5 = new boolean[d2];
			int[] c6 = new int[d2];

			System.arraycopy(cachesMots, 0, c1, 0, d);
			System.arraycopy(cachesPosition, 0, c2, 0, d);
			System.arraycopy(cachesFin, 0, c3, 0, d);
			System.arraycopy(cachesDernierePosition, 0, c4, 0, d);
			System.arraycopy(cachesDebut, 0, c5, 0, d);
			System.arraycopy(cachesLigne, 0, c6, 0, d);

			cachesMots = c1;
			cachesPosition = c2;
			cachesFin = c3;
			cachesDernierePosition = c4;
			cachesDebut = c5;
			cachesLigne = c6;
		}
	}

	public void verifierLigne() throws NouvelleLigne {
		if (faireExeptionFinDeLigne) {
			faireExeptionFinDeLigne = false;
			throw new NouvelleLigne();
		}
	}

}
