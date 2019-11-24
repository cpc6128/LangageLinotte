/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
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

package org.linotte.moteur.xml.alize.parseur.noeud;

import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.FinException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.exception.NouvelleLigne;
import org.w3c.dom.Node;

public class NFin extends Noeud {

	public NFin(Node n) {
		super(n);
	}

	public NFin(NFin n) {
		super(n);
	}

	@Override
	public Noeud cloner() {
		return new NFin(this);
	}

	@Override
	public boolean parse(ParserContext pc) throws Exception {
		boolean boucle_tant_ligne_vide = true;
		// Il reste des mots à analyser
		while (boucle_tant_ligne_vide) {
			try {
				pc.lexer.motSuivant();
				boucle_tant_ligne_vide = false;
				throw new SyntaxeException(Constantes.BALISE_FIN_INATENDUE, pc.lexer.getPosition());
			} catch (NouvelleLigne e) {
			} catch (StopException e) {
				throw e;
			} catch (FinException e) {
				// Livre valide !;
				boucle_tant_ligne_vide = false;
			}
		}
		return true;
	}

}
