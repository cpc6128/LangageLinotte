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
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.XMLIterator;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.w3c.dom.Node;

import java.util.ArrayList;

public class NLigne extends Noeud {

    public NLigne(Node n) {
        super(n);
    }

    public NLigne(NLigne n) {
        super(n);
    }

    @Override
    public NLigne cloner() {
        return new NLigne(this);
    }

    @Override
    public boolean parse(ParserContext parserContext) throws Exception {
        int position_temp = parserContext.lexer.getPosition();
        parserContext.lastPositionLigne = position_temp;

        parserContext.valeurs.push(new ArrayList<Object>());
        parserContext.annotations.push(new ArrayList<Object>());
        parserContext.etats.push(new ArrayList<Object>());
        parserContext.phrase.push(new ArrayList<Object>());
        if (parserContext.mode == MODE.COLORATION) {
            parserContext.styles.push(new ArrayList<Object>());
            parserContext.styles_formules.push(new ArrayList<Object>());
        }

        int copieValeur = parserContext.valeur;
        boolean copieSaute = parserContext.saute;

        parserContext.lexer.setFaireExeptionFinDeLigne(false);

        try {
            //boolean noeudEtat = false;
            XMLIterator iterator = getFils();
            for (Noeud fils : iterator) {
                //if (noeudEtat && parserContext.lexer.isFinDeLigne() && fils instanceof NToken) {
                //	if ("sinon".equalsIgnoreCase(parserContext.lexer.getMot()))
                //		throw new SyntaxeException(Constantes.FIN_DE_LIGNE_ATTENDUE, parserContext.lexer.getLastPosition());
                //}
                fils.parse(parserContext);
                //noeudEtat = fils instanceof NEtat;
            }
        } catch (StopException e) {
            throw e;
        } catch (FinException e) {
            // Avant fin de ligne, fin de fichier !
            parserContext.lexer.setPosition(position_temp);
            throw e;
        } catch (SyntaxeException e) {
            // Avant fin de ligne, erreur de syntaxe !
            parserContext.lexer.setPosition(position_temp);
            parserContext.setDerniereErreur(e);
            throw e;
        } finally {
            parserContext.valeurs.clear();
            parserContext.annotations.clear();
            parserContext.etats.clear();
            parserContext.phrase.clear();
            if (parserContext.mode == MODE.COLORATION) {
                parserContext.styles.clear();
                parserContext.styles_formules.clear();
                // pour la complétion :
                parserContext.prototype = null;
            }
        }

        // La ligne est analysée, donc, plus d'erreur !
        parserContext.setDerniereErreur(null);

        if (!parserContext.lexer.isFinDeLigne()) {

            if (parserContext.mode == MODE.FORMATAGE) {
                parserContext.valeur = copieValeur;
                parserContext.saute = copieSaute;
            }

            // Je devrais rencontrer un fin de ligne !
            throw new SyntaxeException(Constantes.FIN_DE_LIGNE_ATTENDUE, parserContext.lexer.getPosition());
        }
        StringBuilder buftemp = new StringBuilder();
        if (parserContext.mode == MODE.FORMATAGE) {
            formaterLigne(parserContext, position_temp, buftemp);
        }
        return true;

    }

    private void formaterLigne(ParserContext parserContext, int position_temp, StringBuilder buftemp) {
        try {
            buftemp.append(formatage(parserContext, getAttribut("tab"))
                    + parserContext.lexer.subString(position_temp, parserContext.lexer.getPosition()).trim())
                    .append("\n");
        } catch (StringIndexOutOfBoundsException e) {
            /**
             * Cas où l'on est à la fin du fichier : on récupère tout :
             */
            buftemp.append(formatage(parserContext, getAttribut("tab"))
                    + parserContext.lexer.subString(position_temp, parserContext.lexer.getLastPosition()).trim())
                    .append("\n");
        }
        /**
         * Gestion des retours à la ligne :
         */
        if (buftemp.toString().trim().length() != 0) {
            if (getAttribut("espace") != null) {
                // Patch moche pour Linotte 2.0.
                // Si c'est le premier paragraphe, on n'ajoute pas l'espace.
                if (parserContext.nbParagraphes > 1 || !parserContext.bufferFormatage.toString().trim().isEmpty())
                    parserContext.bufferFormatage.append("\n");
            }
            parserContext.bufferFormatage.append(buftemp);
        }
    }

    /**
     * Cette méthode formate le texte suivant la position dans le livre
     *
     * @param pc
     * @param s
     * @return
     */
    public String formatage(ParserContext pc, String s) {
        int i = Integer.valueOf(s) + pc.valeur;
        // Cas des acteurs locaux :
        /*
         * if (!pc.linotte.getLangage().isForceParametreEnligne()) { if
         * (s.equals("1") && pc.valeur > 0) { i++; } }
         */
        if (pc.saute) {
            i--;
            pc.saute = false;
        }

        if (pc.linotte.getLangage().isForceParametreEnligne() && i > 0 && pc.nbParagraphes == 0) {
            // Est-ce que je suis hors un paragraphe ??
            i--;
        }

        StringBuilder builder = new StringBuilder();
        for (int e = 0; e < i; e++) {
            builder.append("\t");
        }
        return builder.toString();
    }
}