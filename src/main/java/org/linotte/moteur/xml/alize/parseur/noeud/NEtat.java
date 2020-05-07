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

import org.linotte.frame.coloration.Style;
import org.linotte.frame.coloration.StyleItem;
import org.linotte.frame.coloration.StyleItem.STYLE;
import org.linotte.frame.coloration.StyleLinotte;
import org.linotte.frame.coloration.StyleMathematiques;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.actions.ConditionSinonAction;
import org.linotte.moteur.xml.actions.FermerAction;
import org.linotte.moteur.xml.actions.LireAction;
import org.linotte.moteur.xml.actions.ParagrapheAction;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.i.ActionTest;
import org.linotte.moteur.xml.alize.kernel.i.ParserHandler;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusFactory;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.ActeurGlobal;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.analyse.GroupeItemXML;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.outils.FastEnumMap;
import org.w3c.dom.Node;

import javax.swing.text.AttributeSet;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.linotte.frame.coloration.StyleItem.STYLE.*;

public class NEtat extends Noeud {

	private static FastEnumMap<STYLE, AttributeSet> styles = new FastEnumMap<StyleItem.STYLE, AttributeSet>(StyleItem.STYLE.class);

	static {
		styles.put(ARTICLE, StyleLinotte.style_article);
		styles.put(SYSTEME, StyleLinotte.style_systeme);
		styles.put(CHAINE, StyleLinotte.style_texte);
		styles.put(VALEUR, StyleLinotte.styleRacine);
		styles.put(DOUBLURE, StyleLinotte.style_doublure);
		styles.put(VARIABLE_LOCALE, StyleLinotte.style_variable_locale);
		styles.put(VARIABLE_SYSTEME, StyleLinotte.style_variable_systeme);
		styles.put(MATH, StyleLinotte.style_math);
		styles.put(STRUCTURE, StyleLinotte.style_structure);
		styles.put(VARIABLE_DOUBLURE, StyleLinotte.style_variable_doublure);
		styles.put(LIVRE, StyleLinotte.style_livre);
		styles.put(SOUS_PARAGRAPHE, StyleLinotte.style_sous_paragraphe);
		styles.put(STRING, StyleLinotte.style_string);
		styles.put(NOMBRE, StyleLinotte.style_nombre);
	}

	public NEtat(Node n) {
		super(n);
	}

	public NEtat(NEtat n) {
		super(n);
	}

	@Override
	public Noeud cloner() {
		return new NEtat(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean parse(ParserContext pc) throws Exception {

		List<ItemXML> tableau = new ArrayList<ItemXML>();
		Iterator<?> i = pc.valeurs.iterator();
		while (i.hasNext()) {
			List<ItemXML> l = (List<ItemXML>) i.next();
			while (!l.isEmpty()) {
				tableau.add(l.remove(0));
			}
		}

		List<String> tableau_annotations = new ArrayList<String>();
		Iterator<?> i2 = pc.annotations.iterator();
		while (i2.hasNext()) {
			List<String> l = (List<String>) i2.next();
			while (!l.isEmpty()) {
				tableau_annotations.add(l.remove(0));
			}
		}

		// Début Style :
		if (pc.mode == MODE.COLORATION && pc.lexer.isFinDeLigne()) {
			colorierAtelier(pc);
			Action etat = pc.linotte.getRegistreDesEtats().retourneAction(getAttribut("nom"));
			if (etat instanceof ActionTest) {
				pc.tests = true;
			}
		}
		if (pc.mode == MODE.COLORATION) {
			// completion
			if ("videracteurs".equals(getAttribut("completion"))) {
				pc.acteursgloblaux.clear();
				pc.bacteursgloblaux = true;
			}
		}
		// Fin Style :

		// Gestion de la complétion des prototypes :

		if (pc.mode == MODE.COLORATION) {
			if (tableau_annotations.size() > 0) {
				if (tableau_annotations.contains("prototype")) {
					pc.types_prototypes.put(tableau.get(0).toString().toLowerCase(), new ArrayList<String>());
					if (tableau_annotations.contains("atributespece")) {
						int debut = 1;
						String[] tab_a = new String[tableau.size() - debut];
						int max_tab_a = tableau.size();
						for (int ia = debut; ia < max_tab_a; ia++) {
							tab_a[ia - debut] = tableau.get(ia).toString().toLowerCase();
						}
						pc.prototypes_attributs.put(tableau.get(0).toString().toLowerCase(), tab_a);
					}
				}
				if (tableau_annotations.contains("instanceprototype")) {
					// Il faut mettre dans un pile ?
					String nom = tableau.get(0).toString().toLowerCase();
					if (nom.startsWith("* "))
						nom = nom.substring(2);
					pc.prototypes.add(new Prototype(tableau.get(1).toString().toLowerCase(), nom, pc.lexer.getLastPosition()));
				}

				if (tableau_annotations.contains("nomacteur")) {
					ItemXML itxml = tableau.get(0);
					if (itxml instanceof GroupeItemXML) {
						ItemXML[] temp = ((GroupeItemXML) itxml).items;
						int bogue = 0;
						for (ItemXML t : temp) {
							String nom = t.toString().toLowerCase();
							if (tableau.size() > 1)
								pc.acteursgloblaux.add(new ActeurGlobal(tableau.get(1).toString().toLowerCase(), nom, pc.lexer.getLastPosition() + bogue));
							else
								pc.acteursgloblaux.add(new ActeurGlobal("", nom, pc.lexer.getLastPosition() + bogue));
							bogue++;
							// Linote 2.6.2 : complétion des types simples 
							pc.prototypes.add(new Prototype(getAttribut("param"), nom, pc.lexer.getLastPosition()));
						}
					} else {
						String nom = itxml.toString().toLowerCase();
						if (tableau.size() > 1)
							pc.acteursgloblaux.add(new ActeurGlobal(tableau.get(1).toString().toLowerCase(), nom, pc.lexer.getLastPosition()));
						else
							pc.acteursgloblaux.add(new ActeurGlobal("", nom, pc.lexer.getLastPosition()));
						// Linote 2.6.2 : complétion des types simples 
						pc.prototypes.add(new Prototype(getAttribut("param"), nom, pc.lexer.getLastPosition()));
					}
				}

				if (tableau_annotations.contains("héritage")) {
					pc.prototypes_heritage.put(tableau.get(0).toString().toLowerCase(), tableau.get(1).toString().toLowerCase());
				}

				if (tableau_annotations.contains("joker")) {
					//TODO A OPTIMISER //
					//URGENT
					String acteur_boucle = tableau.get(0).toString().toLowerCase();
					String type = null;
					// On recherche l'acteur :
					label: for (Set<org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype> set : pc.constructionPileContexteVariables) {
						for (org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype prototype : set) {
							if (prototype.nom.equals(acteur_boucle)) {
								type = prototype.type;
								break label;
							}
						}
					}
					if (type == null)
						for (ActeurGlobal ag : pc.acteursgloblaux) {
							if (ag.nom.equals(acteur_boucle)) {
								type = ag.type;
								break;
							}
						}

					if (!pc.constructionPileContexteVariables.isEmpty()) {
						pc.constructionPileContexteVariables.peek().add(new Prototype(type, "joker", pc.lexer.getLastPosition()));
					}
				}

				if (tableau_annotations.contains("nomacteurfonction")) {
					ItemXML itxml = tableau.get(0);
					ajouterActeursFonction(pc, tableau, itxml);
					String nom = itxml.toString().toLowerCase();
					// Linote 2.6.2 : complétion des types simples 
					pc.prototypes.add(new Prototype(getAttribut("param"), nom, pc.lexer.getLastPosition()));
				}
				// Paramètres simplifiés :
				if (tableau_annotations.contains("nomacteurfonctionsimple")) {
					for (int idx=1;idx< tableau.size();idx++) {
						ItemXML itxml = tableau.get(idx);
						ajouterActeursFonction(pc, tableau, itxml);					
					}
				}
			}
		}

		if (pc.mode == MODE.GENERATION_RUNTIME) {

			Action etat = pc.linotte.getRegistreDesEtats().retourneAction(getAttribut("nom"));
			if (etat != null) {
				// boolean souffleur_activer =
				// !"non".equals(node.getAttribut("souffleur"));

				boolean syntaxev2 = pc.etats.size() == 0; // "compatiblev2".equals(getAttribut("param"));
				boolean hackCondition = getAttribut("hack_bloc")!=null;

				if ((!pc.lexer.isFinDeLigne() || hack || hackCondition || 
						etat instanceof ConditionSinonAction // Pour gérer le cas de la phrase ne contenant que "sinon"
						) && !syntaxev2) {
					// debug("==> C'est pas l'état de fin de ligne !");
					Processus processusPrincipal = ProcessusFactory.createProcessus(getAttribut("param"), tableau, tableau_annotations, etat,
							pc.lastPositionLigne);
					((List<Processus>) pc.etats.peek()).add(processusPrincipal);
					if (etat instanceof ParserHandler) {
						pc.awares.add(processusPrincipal);
					}
				} else {
					Iterator<?> it = pc.etats.iterator();
					List<Processus> tabetat = new ArrayList<Processus>();
					while (it.hasNext()) {
						List<Processus> temp = (List<Processus>) it.next();
						tabetat.addAll(temp);
						temp.clear();
					}
					Processus processus = ProcessusFactory.createProcessus(getAttribut("param"), tableau, tableau_annotations, etat, pc.lastPositionLigne);

					if (etat instanceof ParserHandler) {
						pc.awares.add(processus);
					}

					Processus processusPrimaire = tabetat.size() > 0 ? tabetat.get(0) : null;

					if (processusPrimaire != null) {

						// Peut être un début de paragraphe ?
						if (etat instanceof LireAction) {
							pc.constructionPileSousParagraphesProcessus.push(processus);
						}

						
						if (processusPrimaire instanceof ProcessusDispatcher)
							((ProcessusDispatcher) processusPrimaire).setProcessusSecondaire(processus);
						if (processus instanceof ProcessusDispatcher)
							((ProcessusDispatcher) processus).setProcessusPrimaire(processusPrimaire);

						processus = processusPrimaire;

					} else {
						// Peut être une fin de paragraphe ?
						if (etat instanceof FermerAction) {
							if (pc.constructionPileSousParagraphesProcessus.size() == 0) {
								throw new ErreurException(Constantes.SYNTAXE_SOUS_PARAGRAPHE);
							}
							Processus lire = pc.constructionPileSousParagraphesProcessus.pop();
							// TODO Contrôler erreur
							pc.environnement.getSousParagraphes().put(lire, processus);
						}

					}

					if (processus != null) {
						if (pc.lastProcessus == null) {
							pc.jobRacine.setFirstProcessus(processus);
							pc.lastProcessus = processus;
						} else {
							if (!(etat instanceof ParagrapheAction) || (pc.premier_paragraphe)) {
								// Si c'est le premier paragraphe mais c'est une
								// fontion d'un prototype, on n execute pas.
								if (pc.premier_paragraphe && pc.methodeFonctionnellePrototype != null && etat instanceof ParagrapheAction) {
									// Il doit être exécuté mais on ne veut pas
									// !
									// On va garder le pointeur pour y mettre la
									// premiere fonction exécutée.
									pc.processusNonFonctionPrototype = pc.lastProcessus;
								} else {
									pc.lastProcessus.setNextProcess(processus);
								}
							}
							// Même action pour le processus secondaire
							// :
							if (!(etat instanceof ParagrapheAction) && (pc.lastProcessus instanceof ProcessusDispatcher)
									&& ((ProcessusDispatcher) pc.lastProcessus).getProcessusSecondaire() != null) {
								((ProcessusDispatcher) pc.lastProcessus).getProcessusSecondaire().setNextProcess(processus);
							}

							pc.lastProcessus = processus;
						}

					}

					if (etat instanceof ParagrapheAction) {
						if (pc.processusNonFonctionPrototype != null && pc.processusNonFonctionPrototype.getNextProcess() == null
								&& pc.methodeFonctionnellePrototype == null) {
							// C'est la premiere fonction à exécuter !
							pc.processusNonFonctionPrototype.setNextProcess(processus);
							pc.processusNonFonctionPrototype = null;
						}
						pc.premier_paragraphe = false;
						if (pc.dernierParagraphe == null)
							pc.dernierParagraphe = "§";
						// Mettre dans prototype ???
						if (pc.methodeFonctionnellePrototype != null) {
							pc.environnement.addParagraphe(pc.methodeFonctionnellePrototype, pc.dernierParagraphe, pc.lastProcessus);
							pc.methodeFonctionnellePrototype = null;
						} else
							pc.environnement.setParagraphes(pc.dernierParagraphe, pc.lastProcessus);
					}
				}
			}
		}
		return true;
	}

	private void ajouterActeursFonction(ParserContext pc, List<ItemXML> tableau, ItemXML itxml) {
		if (itxml instanceof GroupeItemXML) {
			ItemXML[] temp = ((GroupeItemXML) itxml).items;
			for (ItemXML t : temp) {
				ajouterActeurFonction(pc, tableau, t);
			}
		} else {
			ajouterActeurFonction(pc, tableau, itxml);
		}
	}

	private void ajouterActeurFonction(ParserContext pc, List<ItemXML> tableau, ItemXML itxml) {
		String nom = itxml.toString().toLowerCase();
		if (nom.startsWith("* "))
			nom = nom.substring(2);
		if (!pc.constructionPileContexteVariables.isEmpty()) {
			if (tableau.size() > 1)
				pc.constructionPileContexteVariables.peek().add(new Prototype(tableau.get(1).toString().toLowerCase(), nom, pc.lexer.getLastPosition()));
			else
				pc.constructionPileContexteVariables.peek().add(new Prototype("??", nom, pc.lexer.getLastPosition()));
		}
	}

	@SuppressWarnings("unchecked")
	private void colorierAtelier(ParserContext pc) {
		// On traite les éléments liés au formule :

		List<StyleMathematiques> tableau = new ArrayList<StyleMathematiques>();
		Iterator<?> i = pc.styles_formules.iterator();
		while (i.hasNext()) {
			List<StyleMathematiques> l = (List<StyleMathematiques>) i.next();
			while (!l.isEmpty()) {
				tableau.add(l.remove(0));
			}
		}

		for (StyleMathematiques styleMathematiques : tableau) {
			pc.prototype_attribut = styleMathematiques.isPrototype_attribut();
			try {
				Mathematiques.parse(styleMathematiques.getFormule(), styleMathematiques.getList(), styleMathematiques.getDebut_style(),
						styleMathematiques.getParserContext(), pc.linotte.getLangage());
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}

		List<StyleItem> Lstyles = new ArrayList<StyleItem>();
		Iterator<?> is = pc.styles.iterator();
		int style_maximun = -1;
		while (is.hasNext()) {
			List<StyleItem> l = (List<StyleItem>) is.next();
			while (!l.isEmpty()) {
				StyleItem item = l.remove(0);
				if ((item.getDebut() + item.getTaille()) > style_maximun)
					style_maximun = item.getDebut() + item.getTaille();
				if (item.getSouligne() == Color.RED) {
					pc.sommaire.setErreurSyntaxe();
				}
				Lstyles.add(item);
			}
		}

		// On applique le style par défaut au reste du document
		if (style_maximun != -1) {
			pc.stylePosition = style_maximun;
		}

		Iterator<StyleItem> boucle = Lstyles.iterator();
		while (boucle.hasNext()) {
			StyleItem item = boucle.next();
			AttributeSet simpleAttributeSet = styles.get(item.getType());
			if (simpleAttributeSet == null) {
				simpleAttributeSet = StyleLinotte.style_token;
			}
			pc.styleBuffer.ajouteStyle(new Style(item.getDebut(), item.getTaille(), simpleAttributeSet, item.getUrl(), item.getSouligne()));
		}
	}
}