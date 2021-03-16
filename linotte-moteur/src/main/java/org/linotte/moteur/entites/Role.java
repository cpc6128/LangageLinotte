/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
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

package org.linotte.moteur.entites;

import java.math.BigDecimal;

/**
 * Un rôle est le type d'un {@link Acteur}.  
 * @see Acteur
 */
public enum Role {

	/**
	 * Une chaine de caractères {@link String}
	 */
	TEXTE,

	/**
	 * Un nombre {@link BigDecimal}
	 */
	NOMBRE,

	/**
	 * Un tableau {@link java.util.List}
	 */
	CASIER,

	/**
	 * Un objet
	 */
	ESPECE,

	/**
	 *
	 */
	INCONNU
}
