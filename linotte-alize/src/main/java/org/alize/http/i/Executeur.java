/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 8, 2008                            *
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

package org.alize.http.i;

import org.alize.security.Habilitation;

import java.util.Collection;
import java.util.Map;

/**
 * Traitement des flux HTML au format WebLivre
 */

public interface Executeur {

	public boolean accepter(String nom);

	public StringBuilder traiter(FichierTraitement fichier, Map<String, String> parametres, Collection<Habilitation> habilitationsHeritees, String langage) throws Exception;

}
