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
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.exception.XMLGroupeException;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;

public class NGroupe extends Noeud {

    private boolean controle = false;

    public NGroupe(Node n) {
        super(n);
    }

    public NGroupe(NGroupe n) {
        super(n);
    }

    @Override
    public Noeud cloner() {
        return new NGroupe(this);
    }

    public void setControle(boolean controle) {
        this.controle = controle;
    }

    public boolean isControle() {
        return controle;
    }

    @Override
    public boolean parse(ParserContext pc) throws Exception {
        // ****************************************
        // Recherche du discriminant :
        // On recherche sur la ligne un lexème qui fait office de
        // discriminant.
        // S'il n'existe pas, on recherche un autre groupe
        boolean passage = true;
        String discriminant = getAttribut("discriminant");

        if (discriminant != null) {
            passage = analyseDiscriminant(pc, discriminant, getAttribut("discriminant2"));
        }

        if (passage) {

            pc.valeurs.push(new ArrayList<Object>());
            pc.annotations.push(new ArrayList<Object>());
            pc.etats.push(new ArrayList<Object>());
            pc.phrase.push(new ArrayList<Object>());
            if (pc.mode == MODE.COLORATION) {
                pc.styles.push(new ArrayList<Object>());
                pc.styles_formules.push(new ArrayList<Object>());
            }
            pc.groupes.push(this);
            int position_courante = pc.lexer.getPosition();
            try {
                for (Noeud fils : getFils()) {
                    fils.parse(pc);
                }
            } catch (StopException e) {
                throw e;
            } catch (XMLGroupeException e) {
                pc.valeurs.pop();
                pc.annotations.pop();
                pc.etats.pop();
                pc.phrase.pop();
                if (pc.mode == MODE.COLORATION) {
                    pc.styles.pop();
                    pc.styles_formules.pop();
                }
                pc.lexer.setPosition(position_courante);
            } catch (FinException e) {
                if ((e.getErreur() == Constantes.MODE_RECHERCHE_IMPOSSIBLE)) {
                    // Bogue avec le verbe parcourir :
                    pc.groupes.pop();
                    throw e;
                }
                if (isObligatoire()) {
                    pc.lexer.setPosition(position_courante);
                    // Fin dans un groupe !
                    pc.groupes.pop();
                    throw e;
                }
            } catch (SyntaxeException e) {
                try {
                    pc.valeurs.pop();
                    pc.annotations.pop();
                    pc.etats.pop();
                    pc.phrase.pop();
                    if (pc.mode == MODE.COLORATION) {
                        pc.styles.pop();
                        pc.styles_formules.pop();
                    }
                } catch (EmptyStackException e1) {
                }
                pc.lexer.setPosition(position_courante);
                if (isObligatoire()) {
                    pc.groupes.pop();
                    pc.setDerniereErreur(e);
                    throw e;
                }
                //Fin Groupe non obligatoire !
                pc.groupes.pop();
                return false;
            }
            pc.groupes.pop();
            return true;
        } else {
            // Discriminant non présent :
            throw new SyntaxeException(Constantes.TOKEN_ABSENT_OBLIGATOIRE, pc.lexer.getPosition());
        }
    }

    private boolean analyseDiscriminant(ParserContext parserContext, String discriminant, String discriminant2) throws FinException {
        boolean passage = true;
        int position_avant_traitement = parserContext.lexer.getPosition();

        // On a déjà vérifier ce discriminant ?
        Set<String> verificationDiscriminant = parserContext.mapDiscriminants.get(position_avant_traitement);
        boolean verificationDiscriminantBoolean = false;
        if (discriminant != null && verificationDiscriminant != null)
            verificationDiscriminantBoolean = verificationDiscriminant.contains(discriminant);
        if (!verificationDiscriminantBoolean && discriminant2 != null && verificationDiscriminant != null)
            verificationDiscriminantBoolean = verificationDiscriminant.contains(discriminant2);

        if (!verificationDiscriminantBoolean) {
            parserContext.lexer.setFaireExeptionFinDeLigne(false);
            String s;
            try {
                passage = false;
                while (true) {
                    s = parserContext.lexer.motSuivant().toLowerCase();
                    if (s.indexOf(discriminant) != -1) {
                        passage = true;
                        break;
                    } else if (discriminant2 != null && s.indexOf(discriminant2) != -1) {
                        passage = true;
                        break;
                    }
                }
            } catch (FinException e2) {
            }
            if (!passage) {
                Set<String> set = parserContext.mapDiscriminants.get(position_avant_traitement);
                if (set == null)
                    set = new HashSet<String>();
                if (discriminant != null)
                    set.add(discriminant);
                if (discriminant2 != null)
                    set.add(discriminant2);
                parserContext.mapDiscriminants.put(position_avant_traitement, set);
            }
            parserContext.lexer.setPosition(position_avant_traitement);
        } else {
            // Discriminant déjà vérifier, on ne passe pas !
            passage = false;
        }
        return passage;
    }

}
