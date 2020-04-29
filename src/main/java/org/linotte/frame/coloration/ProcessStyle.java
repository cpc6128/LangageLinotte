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

package org.linotte.frame.coloration;

import org.linotte.frame.Atelier;
import org.linotte.frame.cahier.Cahier;
import org.linotte.frame.outils.Process;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype;
import org.linotte.moteur.xml.alize.parseur.Parseur;

import java.awt.*;
import java.util.List;
import java.util.*;

public class ProcessStyle extends Process {

	private static final Color COULEUR_ERREUR = new Color(198, 4, 38);

	private static final int DELAY = 2000;

	Cahier cahier = null;

	private StyleBuffer styleBuffer;

	public ProcessStyle(Linotte param, Cahier ap, StyleBuffer sb) {
		super(DELAY);
		cahier = ap;
		styleBuffer = sb;
	}

	private boolean force = false;

	public void force() {
		force = true;
		interrupt();
	}

	@Override
	public void action() throws InterruptedException {
		if (cahier.getDocumentCahier() == null)
			return;

		if (!force) {
			if (!cahier.isEditeurUpdate())
				return;
		} else {
			force = false;
		}

		String s = cahier.retourneTexteCahier() + "\n";
		int size_init = s.length();

		if (s.trim().length() == 0)
			return;

		StringBuilder sb = new StringBuilder(s);
		ParserContext outils = null;
		try {
			styleBuffer.effacerStyle();
			cahier.tests = false;
			Parseur moteurXML = new Parseur();
			outils = new ParserContext(MODE.COLORATION);
			outils.styleBuffer = styleBuffer;
			outils.sommaire = cahier.getJSommaire();
			cahier.getAtelier();
			outils.linotte = Atelier.linotte;
			moteurXML.parseLivre(sb, outils);

			cahier.setCouleur(null, false);
			cahier.tests = outils.tests;
			if (cahier.getAtelier().getCahierCourant() == cahier) {
				cahier.getAtelier().getJButtonTester().setVisible(cahier.tests);
			}

		} catch (LectureException e) {
			cahier.setCouleur(COULEUR_ERREUR, false);
			int position = e.getPosition();
			if (e.getPosition() == s.length()) // Bug quand on est à la fin du
				// fichier
				while (s.charAt(position - 1) == '\n') {
					position--;
					if (position == -1) {
						position = 0;
						break;
					}
				}

			int debut = position;
			debut = s.lastIndexOf("\n", debut);
			if (debut == -1)
				debut = 0;
			int fin = position;
			fin = s.indexOf("\n", fin);
			if (fin == -1)
				fin = s.length();

			styleBuffer.ajouteStyle(new Style(debut, (fin - debut), StyleLinotte.style_error, null));

		} catch (Exception e) {
			cahier.setCouleur(COULEUR_ERREUR, false);
			//System.out.println("Grrr : erreur analyse");
			//e.printStackTrace();
		} catch (Error e) {
			cahier.setCouleur(COULEUR_ERREUR, false);
			//System.out.println("Grrr : erreur analyse");
			//e.printStackTrace();
		} finally {

			if (outils != null) {
				// Mise à jour de la complétion :
				List<String> t = cahier.getEditorPanelCahier().getMots();
				synchronized (t) {
					t.clear();
					cahier.getAtelier();
					t.addAll(Atelier.linotte.getGrammaire().getMots());
					t.addAll(outils.motsLivre);
					Collections.sort(t);
				}
				Map<String, List<String>> m = cahier.getEditorPanelCahier().getTypePrototypes();
				synchronized (m) {
					m.clear();
					m.putAll(outils.types_prototypes);
					// Ajouter les types systèmes :
					// Linotte 2.6.2
					Acteur a_texte = new Acteur(Role.TEXTE,"");
					m.put("texte", new ArrayList<String>());
					for (String string : a_texte.retourneNomsSlotsGreffons()) {
						m.get("texte").add(string);						
					}
					Acteur a_nombre = new Acteur(Role.NOMBRE,"");
					m.put("nombre", new ArrayList<String>());
					for (String string : a_nombre.retourneNomsSlotsGreffons()) {
						m.get("nombre").add(string);						
					}
					
				}
				Map<String, Set<String>> f = cahier.getEditorPanelCahier().getMethodesDoublures();
				synchronized (f) {
					f.clear();
					f.putAll(outils.methodes_doublures);
				}
				SortedSet<Prototype> p = cahier.getEditorPanelCahier().getPrototypes();
				synchronized (p) {
					p.clear();
					p.addAll(outils.prototypes);
				}
				// Mise à jour des informations liées à la structure des paragraphes :
				cahier.getEditorPanelCahier().getLineHighlighter().setConstructionPileSousParagrapheSorted(outils.constructionPileSousParagrapheSorted);

				// Mise à jour de la mise en valeur de sommaire 
				cahier.resetHighlight();
				//outils.sommaire.afficheFonction(cahier.getEditorPaneCahier().getCaretPosition());
			}

			if (size_init == (cahier.retourneTexteCahier() + "\n").length())
				cahier.setEditeurUpdate(false);
		}
	}

	public void setStopLecture() {
	}

}