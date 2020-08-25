/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 13, 2008                             *
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

package org.linotte.moteur.xml.analyse;

import org.alize.kernel.AKException;
import org.alize.kernel.AKLoader;
import org.alize.kernel.AKProcessus;
import org.alize.security.Habilitation;
import org.linotte.frame.coloration.StyleItem;
import org.linotte.frame.coloration.StyleItem.STYLE;
import org.linotte.greffons.api.AKMethod;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.moteur.entites.*;
import org.linotte.moteur.exception.*;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.KernelStack;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusSlot;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.ActeurGlobal;
import org.linotte.moteur.xml.analyse.multilangage.Langage;
import org.linotte.moteur.xml.analyse.multilangage.MathematiqueOperation;
import org.linotte.moteur.xml.analyse.multilangage.TypeSyntaxe;
import org.linotte.moteur.xml.appels.CalqueParagraphe;
import org.linotte.moteur.xml.appels.Fonction;
import org.linotte.moteur.xml.operation.OPERATION;
import org.linotte.moteur.xml.operation.binaire.*;
import org.linotte.moteur.xml.operation.i.*;
import org.linotte.moteur.xml.operation.simple.Addition;
import org.linotte.moteur.xml.operation.simple.Division;
import org.linotte.moteur.xml.operation.simple.Multiplication;
import org.linotte.moteur.xml.operation.simple.Soustration;
import org.linotte.moteur.xml.operation.unaire.Clone;
import org.linotte.moteur.xml.operation.unaire.Negatif;
import org.linotte.moteur.xml.operation.unaire.Positif;
import org.linotte.moteur.xml.operation.unaire.Unicode;

import java.awt.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.linotte.frame.coloration.StyleItem.STYLE.*;

public class Mathematiques {

	public static MathContext context = MathContext.DECIMAL128;

	public static ANGLE angle = ANGLE.DEGREE;

	public static MathContext getPrecision() {
		return context;
	}

	public static enum ANGLE {
		RADIAN, DEGREE
	};

	private static enum ETAT {
		/* START, */FIN, VALEUR, OPERATION, OPERATION_SIMPLE, POINTEUR// , QUIT
	};

	private static enum TYPE {
		NORMAL, POINTEUR, CROCHET, CHEVRON, PARENTHESE, PRIORITAIRE
	};

	private static List<Character> charParticuliers = new ArrayList<Character>();

	static {
		charParticuliers.add('@');
		charParticuliers.add('+');
		charParticuliers.add('-');
		charParticuliers.add('{');
		charParticuliers.add('}');
		charParticuliers.add('*');
		charParticuliers.add('/');
		charParticuliers.add('[');
		charParticuliers.add(']');
		charParticuliers.add('<');
		charParticuliers.add('>');
		charParticuliers.add(',');
		charParticuliers.add('(');
		charParticuliers.add(')');
		charParticuliers.add('&');
		charParticuliers.add('^');
		charParticuliers.add('µ');
		charParticuliers.add('.');
		charParticuliers.add('!');
		charParticuliers.add('=');

	}

	private static Addition addition = new Addition();
	private static Soustration soustration = new Soustration();
	private static Multiplication multiplication = new Multiplication();
	private static Division division = new Division();

	private static final Pattern p_interpolation = Pattern.compile("\\$?\\{([^}]+)\\}");

	/**
	 * Optimisation : transformation de la List en tableau et des BigDecimal et
	 * en opération
	 * 
	 * @param o
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Object toArray(Object o) {
		if (o instanceof List) {
			List<Object> tab = (List<Object>) o;
			for (int i = 0; i < tab.size(); i++) {
				regrouperVisiteur(tab, i);
			}
			return ((List<?>) o).toArray();
		}
		if (o instanceof String) {
			return tryTransformeToBigDecimal((String) o);
		}
		return o;
	}

	/**
	 * Construction du chainage des opérations
	 * 
	 * @param tab
	 * @param i
	 */
	private static void regrouperVisiteur(List<Object> tab, int i) {
		Object element = tab.get(i);
		if (element instanceof OperationUnaire) {
			OperationUnaire operation = (OperationUnaire) element;
			Object temp2 = regrouperParenthese(tab, i);
			operation.valeurBrute = toArray(temp2);
		} else if (element instanceof OperationBinaire) {
			OperationBinaire operation = (OperationBinaire) element;
			Object temp1 = regrouperParenthese(tab, i);
			Object temp2 = regrouperParenthese(tab, i);
			operation.valeurBrute1 = toArray(temp1);
			operation.valeurBrute2 = toArray(temp2);
		} else if (element instanceof OperationTernaire) {
			OperationTernaire operation = (OperationTernaire) element;
			Object temp1 = regrouperParenthese(tab, i);
			Object temp2 = regrouperParenthese(tab, i);
			Object temp3 = regrouperParenthese(tab, i);
			operation.valeurBrute1 = toArray(temp1);
			operation.valeurBrute2 = toArray(temp2);
			operation.valeurBrute3 = toArray(temp3);
		} else
			tab.set(i, toArray(element));
	}

	/**
	 * Construction du chainage des opérations
	 * 
	 * @param tab
	 * @param i
	 * @return
	 */
	private static Object regrouperParenthese(List<Object> tab, int i) {
		if (tab.get(i + 1) == OPERATION.PARENTHESE) {
			Object temp2 = tab.remove(i + 1);
			temp2 = tab.remove(i + 1);
			return temp2;
		} else {
			if (tab.get(i + 1) instanceof Operation) {
				regrouperVisiteur(tab, i + 1);
			}
			return tab.remove(i + 1);
		}
	}

	/**
	 * Cette méthode analyse et parse une chaine. Ainsi, "1+2*carre3" devient
	 * une liste : 1 PLUS PARENTHESE [ 2 FOIS CARRE 3 ] ]
	 * 
	 * @param chaine
	 * @param couleurs
	 * @param debut_style
	 * @return
	 * @throws Exception
	 */
	public static final Object[] parse(String chaine, List<StyleItem> couleurs, int debut_style, Langage langage) throws Exception {
		List<Object> r = parse(new Scanner(chaine, langage), TYPE.NORMAL, couleurs, debut_style, null, null, langage);
		if (couleurs == null) {
			return (Object[]) toArray(r);
		} else {
			return null;
		}
	}

	public static final Object[] parse(String chaine, List<StyleItem> couleurs, int debut_style, ParserContext parserContext, Langage langage)
			throws Exception {
		List<Object> r = parse(new Scanner(chaine, langage), TYPE.NORMAL, couleurs, debut_style, null, parserContext, langage);
		if (couleurs == null) {
			return (Object[]) toArray(r);
		} else {
			return null;
		}
	}

	private static List<Object> parse(Scanner scanner, TYPE type, List<StyleItem> couleurs, int debut_style, Binaire virgule, ParserContext parserContext,
			Langage langage) throws Exception {
		List<Object> total = new LinkedList<Object>();
		if (scanner.jepeux()) {
			ETAT etat = ETAT.OPERATION_SIMPLE;
			boolean fin_ok = type == TYPE.NORMAL;
			// Patch pour corriger les formes 1<2 et 1<3
			boolean grouper = false;
			StyleItem styleFonction = null;
			String nomFonction = null;
			// Pour la complétion :
			StyleItem attribut_style = null;
			String attribut_couleur = null;
			while (etat != ETAT.FIN) {
				switch (etat) {
				case VALEUR:
					scanner.mangeEspace();
					// On recherche un valeur
					if (total.size() > 0 && !scanner.jepeux()) {
						throw new Exception("Il manque la valeur");
					}
					if (scanner.currentChar() == langage.getCrochetDebut() || scanner.currentChar() == '(' || scanner.currentChar() == ')'
							|| scanner.currentChar() == langage.getPointeurFin().charAt(0)) {
						etat = ETAT.OPERATION;
						break;
					}
					if (scanner.isCurrentToken("<<")) {
						etat = ETAT.OPERATION_SIMPLE;
						break;
					}
					String retour = scanner.parseValeur();
					if (retour != null && retour.length() == 0) {
						throw new Exception("il manque une valeur");
					}
					if (couleurs != null) {
						if (isActeur(retour)) { // Acteur anomyme chaine ?
							BigDecimal big = isBigDecimal(retour);
							if (big != null)
								couleur(scanner, couleurs, debut_style, NOMBRE);
							else {
								if (true) {
									if (parserContext != null) {
										String retour_lower = retour.toLowerCase();
										// Est-ce un attribut d'une espece ?
										// forme att@espece
										if (attribut_style != null) {
											org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype[] tab = parserContext.prototypes
													.toArray(new org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype[parserContext.prototypes.size()]);
											int index = tab.length - 1;

											String ptype = null;
											for (; index > -1 && ptype == null; index--) {
												org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype p = tab[index];
												if (retour_lower.equals(p.nom)) {
													ptype = p.type;
													break;
												}
											}
											if (ptype == null) {
												label: for (Set<org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype> set : parserContext.constructionPileContexteVariables) {
													for (org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype prototype : set) {
														if (prototype.nom.equals(retour_lower)) {
															ptype = prototype.type;
															break label;
														}
													}
												}
											}
											if (validerAttributColoration(parserContext, attribut_couleur, ptype)) {
												attribut_style.setSouligne(null);
												attribut_style = null;
												attribut_couleur = null;
											}
										}

										// Est-ce le nom d'un attribut lors
										// de
										// l'instanciation ?
										if (parserContext.prototype != null && parserContext.prototype_attribut) {
											boolean attribut = false;
											attribut = validerAttributColoration(parserContext, retour_lower, parserContext.prototype);
											if (attribut) {
												// Vérifié, acteur globale
												styleFonction = couleur(scanner, couleurs, debut_style, VALEUR);
											} else
												styleFonction = couleur(scanner, couleurs, debut_style, VALEUR, Color.RED);
										} else {
											boolean globale = false;
											boolean local = false;
											for (ActeurGlobal ag : parserContext.acteursgloblaux) {
												if (ag.nom.equals(retour_lower)) {
													globale = true;
													break;
												}
											}
											label: for (Set<org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype> set : parserContext.constructionPileContexteVariables) {
												for (org.linotte.moteur.xml.alize.parseur.ParserContext.Prototype prototype : set) {
													if (prototype.nom.equals(retour_lower)) {
														local = true;
														break label;
													}
												}
											}
											if (globale && !local) {
												// Vérifié, acteur globale
												styleFonction = couleur(scanner, couleurs, debut_style, VALEUR,
														parserContext.bacteursgloblaux ? Color.LIGHT_GRAY : null);
											} else if (local) {
												// Vérifié, acteur globale
												styleFonction = couleur(scanner, couleurs, debut_style, VALEUR);
											} else {
												// Derniere verification,
												// est-ce
												// un acteur système ?
												if (Livre.isActeurSystem(Chaine.produire(retour)))
													styleFonction = couleur(scanner, couleurs, debut_style, VARIABLE_SYSTEME);
												else
													styleFonction = couleur(scanner, couleurs, debut_style, VALEUR, Color.RED);
											}
										}
									} else {
										styleFonction = couleur(scanner, couleurs, debut_style, VALEUR);
									}
									nomFonction = retour;
								}
							}
						} else {
							if (retour.length() > 0 && retour.charAt(0) == ('\"'))
								couleur(scanner, couleurs, debut_style, STRING);
							else
								couleur(scanner, couleurs, debut_style, NOMBRE);
						}
					}
					total.add(retour);
					scanner.mangeEspace();

					// Patch pour corriger les formes 1<2 et 1<3
					if (grouper) {
						grouper = false;
						List<Object> mange = new ArrayList<Object>();
						boolean continuer = true;
						do {
							Object o = total.get(total.size() - 1);
							if (o instanceof OperationBinaire || (o instanceof OPERATION && o != OPERATION.PARENTHESE)) {
								mange.add(0, o);
								total.remove(total.size() - 1);
								continuer = false;
							} else {
								mange.add(0, o);
								total.remove(total.size() - 1);
							}
						} while (continuer);
						total.add(OPERATION.PARENTHESE);
						total.add(mange);
					}

					if (!scanner.jepeux()) {
						etat = ETAT.FIN;
					} else {
						etat = ETAT.OPERATION;
					}
					break;
				case OPERATION:
					scanner.mangeEspace();
					if (scanner.jepeux()) {
						char op = scanner.currentChar();
						if (styleFonction != null && op != '(') {
							styleFonction = null;
						}
						couleur(scanner, couleurs, debut_style, MATH);
						scanner.avance();

						if ((op != '.' && op != '*' && op != '/' && op != '(' && op != langage.getCrochetDebut() && op != langage.getPointeurDebut().charAt(0)) && !(

						(op == langage.getAttributEspece().getTexte().charAt(0)
								|| op == Character.toUpperCase(langage.getAttributEspece().getTexte().charAt(0)))
								&& scanner.isCurrentString(langage.getAttributEspece().getTexte().substring(1))

						) && type == TYPE.PRIORITAIRE) {
							scanner.retour();
							return total;
						}

						boolean fait = false;
						for (MathematiqueOperation operationMathematique : langage.getOperations()) {
							if (operationMathematique.getTypeSyntaxe() == TypeSyntaxe.TYPE_2) {
								if ((op == operationMathematique.getTexte().charAt(0)
										|| op == Character.toUpperCase(operationMathematique.getTexte().charAt(0)))
										&& scanner.isCurrentString(operationMathematique.getTexte().substring(1))) {
									couleur(scanner, couleurs, debut_style, operationMathematique.getStyle());
									scanner.avance(operationMathematique.getTexte().length() - 1);
									regrouper(total, (OperationBinaire) operationMathematique.getOperation().newInstance());
									grouper = true;
									etat = ETAT.VALEUR;
									fait = true;
									break;
								}
							} else if (operationMathematique.getTypeSyntaxe() == TypeSyntaxe.TYPE_3) {
								if ((op == operationMathematique.getTexte().charAt(0)
										|| op == Character.toUpperCase(operationMathematique.getTexte().charAt(0)))
										&& scanner.isCurrentString(operationMathematique.getTexte().substring(1))) {
									if (couleurs != null) {
										attribut_couleur = nomFonction;
										attribut_style = couleurs.get(couleurs.size() - 2);
										couleur(scanner, couleurs, debut_style, operationMathematique.getStyle());
									}
									scanner.avance(operationMathematique.getTexte().length() - 1);
									total.add(OPERATION.ESPECE);
									etat = ETAT.VALEUR;
									fait = true;
									break;
								}
							}

						}

						if (fait) {
							//TODO a modifier
						} else if ((op == '@')) {
							if (couleurs != null) {
								attribut_couleur = nomFonction;
								attribut_style = couleurs.get(couleurs.size() - 2);
								couleur(scanner, couleurs, debut_style, DOUBLURE);
							}
							// scanner.avance();
							total.add(OPERATION.ESPECE);
							etat = ETAT.VALEUR;
						} else if ((op == '.')) {
							couleur(scanner, couleurs, debut_style, DOUBLURE);
							// scanner.avance();
							total.add(OPERATION.MESSAGE);
							etat = ETAT.VALEUR;
						} else if (op == '+') {
							total.add(addition);
							etat = ETAT.OPERATION_SIMPLE;
						} else if (op == '-') {
							total.add(soustration);
							etat = ETAT.OPERATION_SIMPLE;
						} else if (op == '&') {
							total.add(OPERATION.NOP);
							etat = ETAT.OPERATION_SIMPLE;
						} else if (op == '*') {
							etat = priorisation(scanner, type, couleurs, debut_style, total, multiplication, parserContext, langage);
						} else if (op == '/') {
							couleur(scanner, couleurs, debut_style, MATH);
							etat = priorisation(scanner, type, couleurs, debut_style, total, division, parserContext, langage);
						} else if (op == langage.getPointeurDebut().charAt(0)) {
							total.add(OPERATION.POINTEUR);
							while (true) {
								Binaire binaire = new Binaire();
								List<Object> pointeur = parse(scanner, TYPE.POINTEUR, couleurs, debut_style, binaire, parserContext, langage);
								total.add(pointeur);
								if (binaire.valeur) {
									couleur(scanner, couleurs, debut_style, SYSTEME);
									scanner.avance();
								} else {
									break;
								}
							}
							
							// On veut gérer le cas suivant : affiche x de (liste{1}) ---> affiche x de liste{1}
							if (total.size() > 4 && total.get(total.size() - 2) == OPERATION.POINTEUR && total.get(total.size() - 4) == OPERATION.ESPECE) {
								List<Object> sub = new ArrayList<>();
								Object t;
								while (!((total.get(total.size() - 1)) == OPERATION.ESPECE)) {
									t = total.remove(total.size() - 1);
									sub.add(0, t);
								}
								total.add(OPERATION.PARENTHESE);
								total.add(sub);
							}
							
							scanner.mangeEspace();
							if (!scanner.jepeux()) {
								etat = ETAT.FIN;
							} else {
								etat = ETAT.OPERATION;
							}
						} else if (op == langage.getCrochetDebut()) {
							total.add(OPERATION.CROCHET);
							List<Object> pointeur = parse(scanner, TYPE.CROCHET, couleurs, debut_style, null, parserContext, langage);
							total.add(pointeur);
							scanner.mangeEspace();
							if (!scanner.jepeux()) {
								etat = ETAT.FIN;
							} else {
								etat = ETAT.OPERATION;
							}
						} else if (op == '(') {
							total.add(OPERATION.PARENTHESE);
							if (styleFonction != null) {
								styleFonction.setUrl(nomFonction);
								styleFonction.setType(CHAINE);
								styleFonction.setSouligne(null);
							}
							while (true) {
								Binaire binaire = new Binaire();
								List<Object> pointeur = parse(scanner, TYPE.PARENTHESE, couleurs, debut_style, binaire, parserContext, langage);
								total.add(pointeur);
								if (binaire.valeur) {
									couleur(scanner, couleurs, debut_style, SYSTEME);
									scanner.avance();
								} else {
									break;
								}
							}

							// List<Object> pointeur = parse(scanner,
							// TYPE.PARENTHESE, couleurs, debut_style);
							// total.add(pointeur);
							scanner.mangeEspace();
							if (!scanner.jepeux()) {
								etat = ETAT.FIN;
							} else {
								etat = ETAT.OPERATION;
							}
						} else if ((op == langage.getPointeurFin().charAt(0) || op == ',') && (type == TYPE.POINTEUR || type == TYPE.PARENTHESE)) {
							if (op == ',') {
								scanner.retour();
								if (virgule != null)
									virgule.valeur = true;
							}
							fin_ok = true;
							etat = ETAT.FIN;
						} else if (op == langage.getCrochetFin() && type == TYPE.CROCHET) {
							fin_ok = true;
							etat = ETAT.FIN;
						} else if (op == ')' && type == TYPE.PARENTHESE) {
							fin_ok = true;
							etat = ETAT.FIN;
						} else if (op == '>' && scanner.isCurrentToken(">") && type == TYPE.CHEVRON) {
							fin_ok = true;
							scanner.avance();
							etat = ETAT.FIN;
						} else if (op == '^') {
							couleur(scanner, couleurs, debut_style, MATH);
							if (op != '^')
								scanner.avance(4);
							regrouper(total, new Puiss());
							grouper = true;
							etat = ETAT.VALEUR;
						}
						/**
						 * Opération drapeaux
						 */
						else if ((op == '>') && !scanner.isCurrentToken("=")) {
							couleur(scanner, couleurs, debut_style, MATH);
							regrouper(total, new Sup());
							grouper = true;
							etat = ETAT.VALEUR;
						} else if ((op == '=') && scanner.isCurrentToken("=")) {
							couleur(scanner, couleurs, debut_style, MATH);
							scanner.avance(1);
							regrouper(total, new Egal());
							grouper = true;
							etat = ETAT.VALEUR;
						} else if ((op == '=') && !scanner.isCurrentToken("=")) {
							couleur(scanner, couleurs, debut_style, MATH);
							// scanner.avance();
							regrouper(total, new Egal());
							grouper = true;
							etat = ETAT.VALEUR;
						} else if ((op == '<') && !scanner.isCurrentToken("=")) {
							couleur(scanner, couleurs, debut_style, MATH);
							// scanner.avance();
							regrouper(total, new Inf());
							grouper = true;
							etat = ETAT.VALEUR;
						} else if ((op == '!') && scanner.isCurrentToken("=")) {
							couleur(scanner, couleurs, debut_style, MATH);
							scanner.avance(1);
							regrouper(total, new Diff());
							grouper = true;
							etat = ETAT.VALEUR;
						} else if ((op == '<') && scanner.isCurrentToken("=")) {
							couleur(scanner, couleurs, debut_style, MATH);
							scanner.avance();
							regrouper(total, new InfOuEgal());
							grouper = true;
							etat = ETAT.VALEUR;
						} else if ((op == '>') && scanner.isCurrentToken("=")) {
							couleur(scanner, couleurs, debut_style, MATH);
							scanner.avance();
							regrouper(total, new SupOuEgal());
							grouper = true;
							etat = ETAT.VALEUR;
						}
						/**
						 * Fin opération drapeaux
						 */
						else {
							throw new Exception("Opération inconnue " + op);
						}
					} else {
						throw new Exception("Il manque l'opération !");
					}
					break;
				case OPERATION_SIMPLE:
					scanner.mangeEspace();
					styleFonction = null;
					if (scanner.jepeux()) {
						char op = scanner.currentChar();
						if (!(op > 47 && op < 58)) {
							couleur(scanner, couleurs, debut_style, MATH);
							// if (Character.isLetter(op)) {
							if ((op > 96 && op < 123) || (op > 64 && op < 91)) {
								for (MathematiqueOperation operationMathematique : langage.getOperations()) {
									if (operationMathematique.getTypeSyntaxe() == TypeSyntaxe.TYPE_1) {
										if ((op == operationMathematique.getTexte().charAt(0)
												|| op == Character.toUpperCase(operationMathematique.getTexte().charAt(0)))
												&& scanner.isCurrentString(operationMathematique.getTexte())) {
											couleur(scanner, couleurs, debut_style, operationMathematique.getStyle());
											scanner.avance(operationMathematique.getTexte().length());
											total.add(operationMathematique.getOperation().newInstance());
											break;
										}
									}
								}
							} else if (op == 'µ') {
								couleur(scanner, couleurs, debut_style, DOUBLURE);
								scanner.avance(1);
								total.add(new Unicode());
							} else if (op == '#') {
								couleur(scanner, couleurs, debut_style, DOUBLURE);
								scanner.avance(1);
								total.add(new Clone());
							} else if (op == '<' && scanner.isCurrentToken("<<")) {
								total.add(OPERATION.CHEVRON);
								scanner.avance(2);
								List<Object> pointeur = parse(scanner, TYPE.CHEVRON, couleurs, debut_style, null, parserContext, langage);
								total.add(pointeur);
								scanner.mangeEspace();
								if (!scanner.jepeux()) {
									etat = ETAT.FIN;
								} else {
									etat = ETAT.OPERATION;
								}
								break;
							} else if (op == langage.getPointeurDebut().charAt(0)) {
								// Pour les casiers anonymes Linotte 2.0 :
								scanner.avance(1);

								total.add(OPERATION.CASIERANONYME);
								while (true) {
									Binaire binaire = new Binaire();
									List<Object> pointeur = parse(scanner, TYPE.POINTEUR, couleurs, debut_style, binaire, parserContext, langage);
									total.add(pointeur);
									if (binaire.valeur) {
										couleur(scanner, couleurs, debut_style, SYSTEME);
										scanner.avance();
									} else {
										break;
									}
								}

								scanner.mangeEspace();
								if (!scanner.jepeux()) {
									etat = ETAT.FIN;
								} else {
									etat = ETAT.OPERATION;
								}
								break;
							} else if (total.size() == 0 && op == '-') {
								couleur(scanner, couleurs, debut_style, MATH);
								scanner.avance(1);
								total.add(new Negatif());
							} else if (total.size() == 0 && op == '+') {
								couleur(scanner, couleurs, debut_style, MATH);
								scanner.avance(1);
								total.add(new Positif());
							} else {
								//throw new Exception("formule mal formée");
							}
						}
					}
					etat = ETAT.VALEUR;
					break;
				// case FIN:
				// etat = ETAT.QUIT;
				// break;
				default:
					break;
				}
			}

			if (!fin_ok) {
				if (type == TYPE.POINTEUR) {
					throw new Exception("fermeture de pointeur incorrecte !");
				} else if (type == TYPE.CROCHET) {
					throw new Exception("fermeture de crochet incorrecte !");
				} else if (type == TYPE.CHEVRON) {
					throw new Exception("fermeture de chevron incorrecte !");
				} else if (type == TYPE.PARENTHESE) {
					throw new Exception("fermeture des parenthèses incorrecte !");
				}
			}
		}
		return total;
	}

	private static boolean validerAttributColoration(ParserContext parserContext, String attribut, String ptype) {
		if (!validerAttributColoration_(parserContext, attribut, ptype)) {
			String pere = parserContext.prototypes_heritage.get(ptype);
			if (pere != null)
				return validerAttributColoration_(parserContext, attribut, pere);
			return false;
		}
		return true;
	}

	public static boolean validerAttributColoration_(ParserContext parserContext, String attribut, String ptype) {
		boolean attribut_valide = false;
		Prototype proto = null;
		for (Prototype i : parserContext.linotte.especeModeleMap) {
			if (i.getType().equalsIgnoreCase(ptype)) {
				proto = i;
				break;
			}
		}
		if (proto != null) {
			Set<String> slots = proto.retourAttributs();
			if (slots.contains(attribut.toLowerCase())) {
				// Attribut connu de ce
				// protoyype !
				attribut_valide = true;
			}
		} else {
			// On regarde au niveau des
			// prototypes définis dans le
			// livre :
			String[] attrib = parserContext.prototypes_attributs.get(ptype);
			if (attrib != null) {
				for (String string : attrib) {
					if (string.equalsIgnoreCase(attribut)) {
						attribut_valide = true;
						break;
					}
				}
			}
		}
		return attribut_valide;
	}

	/**
	 * 
	 * @param total
	 * @param operation
	 */
	private static void regrouper(List<Object> total, OperationBinaire operation) {
		if (total.size() > 1 && total.get(total.size() - 2) == OPERATION.PARENTHESE) {
			if (total.size() > 2 && (total.get(total.size() - 3) instanceof Operation || total.get(total.size() - 3) instanceof String)) {
				// Cas suivant cos(3) = 2 et fonction(3) = 2
				// Fiche 118
				int n = 3;
				if (total.size() > 4 && (total.get(total.size() - 4) == OPERATION.MESSAGE)) {
					// prendre en compte le cas des méthodes fonctionnelles ( affiche "e".taille() - minute ) :
					// dans ce cas, il faut prendre le nom de la méthode et le "." (message)
					n = 5;
				}
						
				List<Object> sub = new ArrayList<Object>();
				for (; n > 0; n--) {
					sub.add(0, total.remove(total.size() - 1));
				}
				total.add(OPERATION.PARENTHESE);
				total.add(sub);
				total.add(total.size() - 2, operation);
				return;
			} 
			if (total.size() > 4 && total.get(total.size() - 4) == OPERATION.PARENTHESE && total.get(total.size() - 5) instanceof OPERATION) {
				List<Object> sub = new ArrayList<Object>();
				int n = 5;
				for (; n > 0; n--) {
					sub.add(0, total.remove(total.size() - 1));
				}
				total.add(OPERATION.PARENTHESE);
				total.add(sub);
			}
			total.add(total.size() - 2, operation);
			return;
		}
		if (total.size() > 1 && total.get(total.size() - 2) == OPERATION.POINTEUR) {
			List<Object> sub = new ArrayList<Object>();
			sub.add(0, total.remove(total.size() - 1));
			sub.add(0, total.remove(total.size() - 1));
			Object t;
			while (!((t = total.remove(total.size() - 1)) instanceof OPERATION)) {
				sub.add(0, t);
			}
			sub.add(0, t);
			total.add(OPERATION.PARENTHESE);
			total.add(sub);
			total.add(total.size() - 2, operation);
			return;
		}
		total.add(total.size() - 1, operation);
	}

	private static ETAT priorisation(Scanner scanner, TYPE type, List<StyleItem> couleurs, int debut_style, List<Object> total, OperationSimple operation,
			ParserContext parserContext, Langage langage) throws Exception {
		ETAT etat;
		if (type != TYPE.PRIORITAIRE) {
			List<Object> prioriaire = parse(scanner, TYPE.PRIORITAIRE, couleurs, debut_style, null, parserContext, langage);
			prioriaire.add(0, operation);
			boolean ok = true;
			while (ok && total.size() > 0) {
				Object t = total.remove(total.size() - 1);
				if (t != addition && t != soustration) {
					ok = true;// !(t instanceof OPERATION);
					prioriaire.add(0, t);
				} else {
					ok = false;
					total.add(t);
				}
			}
			total.add(OPERATION.PARENTHESE);
			total.add(prioriaire);
			scanner.mangeEspace();
			if (!scanner.jepeux()) {
				etat = ETAT.FIN;
			} else {
				etat = ETAT.OPERATION;
			}
		} else {
			total.add(operation);
			etat = ETAT.OPERATION_SIMPLE;
		}
		return etat;
	}

	public static Object shrink(Object r, Job moteur, Temoin temoin) throws Exception {
		if (r instanceof BigDecimal)
			return r;
		String temp = (String) r;
		if (isActeur(temp))
			return chargerActeur(transforme(temp), null, moteur, temoin);
		else
			return transforme(temp);

	}

	public static String interpolation(Job moteur, String temp, Temoin temoin, Langage langage) {
		/**
		 * Spécification 1.5 : interpolation de chaîne
		 */

		if (temp.indexOf('{') >= 0) {
			StringBuffer sb = new StringBuffer();
			Matcher m = p_interpolation.matcher(temp);
			while (m.find()) {
				// On interdit de mettre en cache la valeur !
				temoin.setActeur(true);
				ItemXML itemXML = ItemXML.factory(m.group(1), ItemXML.VALEUR);
				try {
					Object x;
					x = itemXML.retourneValeurTransformee(moteur, langage);
					if (x instanceof Acteur) {
						x = ((Acteur) x).getValeur();
					}
					m.appendReplacement(sb, String.valueOf(x));
				} catch (Exception e) {
					//try {
					//	m.appendReplacement(sb, "?" + itemXML.getValeurBrute());
					//} catch (Exception e2) {
					//}
				}
			}
			m.appendTail(sb);
			temp = sb.toString();
		}
		return temp;
	}

	/**
	 * Cette méthode parse un flux de type 1 PLUS PARENTHESE [ 2 FOIS CARRE 3 ]
	 * ] Ce type de flux est généré par la méthode parse
	 * 
	 * @param valeurs
	 * @param jobCourant
	 * @param temoin
	 * @return
	 * @throws Exception
	 */
	public static Object shrink(final Object[] valeurs, Job jobCourant, Temoin temoin) throws Exception {
		int size = valeurs.length;

		if (size == 0) {
			return null;
		}

		if (size == 1 && !(valeurs[0] instanceof Operation)) {
			// Optimisation
			// C'est obligatoirement un acteur anonyme :
			return shrink(valeurs[0], jobCourant, temoin);
		}
		int position = 0;
		boolean pointeur = true;
		Object total = null, token_courant = null, valeur_transformee = null, prototype = null, attribut_espece = null;
		OperationSimple operation_simple_en_cours = null;

		do {
			/**
			 * Extraction du token à analyser
			 */
			token_courant = valeurs[position];

			/**
			 * Optimisation
			 */
			if (token_courant instanceof OperationSimple) {
				operation_simple_en_cours = (OperationSimple) token_courant;
				position++;
				// Optimisation :
				token_courant = valeurs[position];
				// continue;
			}
			if (token_courant instanceof OPERATION) {
				if (token_courant == OPERATION.ESPECE) {
					attribut_espece = valeur_transformee;
					valeur_transformee = null;
					position++;
					continue;
				} else if (token_courant == OPERATION.MESSAGE) {
					if (valeur_transformee == null) // http://programmons.forumofficiel.fr/t1173-linotte-2-6-1#7839
						// Gestion du cas suivant : affiche 2. 5 // doit produire une erreur
						throw new ErreurException(Constantes.SYNTAXE_MINI_FORMULE);
					attribut_espece = valeur_transformee;
					prototype = valeur_transformee;
					valeur_transformee = null;
					position++;
					continue;
				}
			}

			if (token_courant instanceof BigDecimal) {
				if (operation_simple_en_cours == null) {
					if (attribut_espece != null)
						throw new ErreurException(Constantes.SYNTAXE_CARACTERISTIQUE_INCONNUE, ((BigDecimal) token_courant).toPlainString());
					total = token_courant;
					position++;
					continue;
				}
				valeur_transformee = (BigDecimal) token_courant;
			} else
			/**
			 * C'est une chaine, ça peut être un acteur, une fonction : il faut
			 * extraire la valeur
			 */
			if (token_courant instanceof String) {

				valeur_transformee = token_courant; //transforme((String) token_courant); <-- "toto".majuscule()

				/**
				 * C'est un acteur (ou attribut d'espece ) ou une chaine :
				 */
				// if (valeur_transformee instanceof String) { C'est toujours
				// vrai !

				Object test = ((position + 1) < size) ? valeurs[position + 1] : null;
				/**
				 * Est-ce une fonction ?
				 */
				if (test == OPERATION.PARENTHESE) {
					temoin.setActeur(true);
					String nomFonction = ((String) valeur_transformee).toLowerCase();
					position++;
					// ***
					Acteur[] tableauActeursParametres = new Acteur[20];
					Object moi = null;
					int indexTableauActeurs = 0;
					if (prototype != null) {
						if (prototype instanceof String) {
							Object o = shrink(prototype, jobCourant, temoin);
							tableauActeursParametres[0] = (o instanceof Acteur ? (Acteur) o
									: new Acteur(o instanceof BigDecimal ? Role.NOMBRE : Role.TEXTE, o));
							moi = tableauActeursParametres[0];
						} else {
							Object o = prototype;
							tableauActeursParametres[0] = (o instanceof Acteur ? (Acteur) o
									: new Acteur(o instanceof BigDecimal ? Role.NOMBRE : Role.TEXTE, o));
							moi = prototype;
						}
						indexTableauActeurs++;
					}
					while (position + 1 < size) {
						position++;
						Object temp2 = valeurs[position];
						if (!(temp2 instanceof Object[])) {
							position--;
							break;
						}
						if ((temp2 instanceof Object[] && ((Object[]) temp2).length == 0)) {
							// position--;
							break;
						}
						Object o = shrink((Object[]) temp2, jobCourant, temoin);
						tableauActeursParametres[indexTableauActeurs] = (o instanceof Acteur ? (Acteur) o
								: new Acteur(o instanceof BigDecimal ? Role.NOMBRE : Role.TEXTE, o));
						indexTableauActeurs++;
					}

					// Il faut retrouver le processus du paragraphe !
					RuntimeContext runtimeContext = (RuntimeContext) jobCourant.getRuntimeContext();
					AKProcessus processus;
					JobContext jc = null;// pour récupérer la stack
					Fonction fonction = null;
					;
					try {
						if (moi != null) {
							// Version Linotte :
							processus = ((Acteur) moi).retourneSlot(nomFonction);
						} else
							// Une fonction du livre :
							processus = runtimeContext.getEnvironnment().getParagraphe(nomFonction);
						if (processus == null) {
							if (moi == null) {
								// Fonction mathématique inconnue :
								throw new ErreurException(Constantes.SYNTAXE_MINI_FORMULE, "fonction '" + token_courant + "' inconnue");
							}
							// On recherche dans le greffon :
							AKMethod method = ((Acteur) moi).retourneSlotGreffon(nomFonction);
							if (method == null)
								throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_INCONNUE, nomFonction);
							processus = new ProcessusSlot(method, Arrays.copyOfRange(tableauActeursParametres, 1, tableauActeursParametres.length),
									(Acteur) moi, jobCourant.getCurrentProcessus().getPosition());
						}

						if (!jobCourant.isRunning()) {
							// Dans le cas des appels récursifs, on ne passe
							// pas dans les traitements du job :
							// Job.execute.Mathematiques.Job.execute.Mathematique.Job.execute
							// ...
							// http://langagelinotte.free.fr/forum/showthread.php?tid=1021&pid=6907#pid6907
							throw new StopException();
						}

						Job job = (Job) AKLoader.produceAKjob(jobCourant);
						job.setFirstProcessus(processus);
						// Nous sommes sur le même thread :
						// TODO, à améliorer :
						job.idTechnique = jobCourant.idTechnique;
						jc = ((JobContext) job.getContext());

						if (tableauActeursParametres.length > 0) {
							fonction = new Fonction(job.getCurrentProcessus(), jc.getLivre().getParagraphe(), tableauActeursParametres);
						} else {
							fonction = new Fonction(job.getCurrentProcessus(), jc.getLivre().getParagraphe());
						}
						jc.getLivre().getKernelStack().ajouterAppel(fonction);

						// jc.setDoublure(t.length == 0 ? null : t);
						if (moi != null)
							jc.setPrototype((Acteur) moi);
						job.execute(runtimeContext);
						// Interdit d'arriver ici !
						// Peut arriver quand le job a été tué.
						throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_RETOURNER_MANQUANT, null);
					} catch (RetourException re) {
						throw new ErreurException(Constantes.RETOUR_SANS_BOUCLE, "la fonction '" + token_courant + "' doit retourner une valeur !");
					} catch (RetournerException re) {
						// Récupération de la valeur retournée
						valeur_transformee = re.getActeur();
					} catch (LectureException le) {
						fillStack(le, jc);
						if ( le.getCause() instanceof FonctionDoublureException) {
							le.forcePosition(jobCourant.getCurrentProcessus().getPosition());
						}
						throw le;
					} catch (LinotteException le) {
						fillStack(le, jc);
						throw le;
					} catch (GreffonException re) {
						throw re;
					} catch (AKException re) {
						re.printStackTrace();
						throw re;
					} catch (Exception re) {
						re.printStackTrace();
						throw new GreffonException("Erreur interne au greffon !");
					}
					attribut_espece = null; // affiche "e".taille() - minute 
					if (moi == null) {
						prototype = null;
					}
					/**
					 * Erreur dans l'algorithme, si fonction encapsulée dans des
					 * paranthèses, elle se retrouve dans total, du coup,
					 * valeur_transformee est oubliée...
					 */
					if (operation_simple_en_cours == null) {
						total = null;
					}
					continue;
				}

				// Est-ce un attribut ?
				if (test == OPERATION.ESPECE || test == OPERATION.MESSAGE) {
					position++;
					continue;
				} else {
					// <-- "toto".majuscule()
					valeur_transformee = transforme((String) token_courant);
				}
				if (isActeur((String) token_courant)) {
					if (attribut_espece == null || attribut_espece instanceof String)
						valeur_transformee = chargerActeur((String) token_courant, (String) attribut_espece, jobCourant, temoin);
					else
						throw new Exception("Attribut d'espèce invalide " + attribut_espece);
					attribut_espece = null;
				}
			}
			// }
			/**
			 * Est-ce une opération de subtitution ? "carré 3" ou "2 puiss 3"
			 * par exemple
			 */
			do {
				pointeur = false;
				if (token_courant instanceof Operation) {
					Operation opera = (Operation) token_courant;
					valeur_transformee = opera.calculer(jobCourant, temoin, token_courant);
					if (total == null) {
						total = valeur_transformee;
					}
				} else if (token_courant instanceof OPERATION && ((OPERATION) token_courant).isOperationSubtitution()) {
					switch ((OPERATION) token_courant) {
					case PARENTHESE: {
						valeur_transformee = shrink((Object[]) valeurs[++position], jobCourant, temoin);
						if (attribut_espece != null) {
							if (valeur_transformee instanceof Prototype) {
								valeur_transformee = ((Prototype) valeur_transformee).retourneAttribut((String) attribut_espece);
							} else {
								throw new Exception("Syntaxe impossible avec les especes");
							}
						}
						if (operation_simple_en_cours == null && total != null) {
							// On force la multiplication : 3(5)
							operation_simple_en_cours = new Multiplication();
						}
						break;
					}
					case POINTEUR: {
						boolean continuer;
						do {
							Object o = shrink((Object[]) valeurs[++position], jobCourant, temoin);
							if (o instanceof Acteur) {
								o = ((Acteur) o).getValeur();
							}
							if (!(o instanceof BigDecimal)) {
								throw new Exception("Le pointeur doit être un nombre : " + o);
							}
							BigDecimal bd = (BigDecimal) o;
							if (valeur_transformee instanceof Acteur) {
								Acteur tmp = (Acteur) valeur_transformee;
								if (tmp.getRole() == Role.CASIER) {
									valeur_transformee = ((Casier) tmp).retourne(bd.intValue());
								} else if (tmp.getRole() == Role.TEXTE) {
									String s = (String) tmp.getValeur();
									valeur_transformee = s.substring(bd.intValue(), bd.intValue() + 1);
								} else
									throw new Exception("Pointeur interdit avec " + total);
							} else if (valeur_transformee instanceof String) {
								String s = (String) valeur_transformee;
								valeur_transformee = s.substring(bd.intValue(), bd.intValue() + 1);
							} else
								throw new Exception("Pointeur interdit avec " + valeur_transformee);
							// Doit-on continuer
							continuer = ((position + 1) < size) && valeurs[position + 1] instanceof Object[];
						} while (continuer);
						if (operation_simple_en_cours == null)
							total = valeur_transformee;
						// dx1 de <vsExc>= <vsNomlox>{1}-<vsNomlox> {0}
						break;
					}
					case CASIERANONYME: {
						boolean continuer;
						Casier casier = null;
						do {
							Object o = shrink((Object[]) valeurs[++position], jobCourant, temoin);
							if (o instanceof Acteur) {
								// o = ((Acteur) o).getValeur();
								if (casier == null) {
									casier = new Casier(null, null, ((Acteur) o).getRole(), ((o instanceof Prototype) ? ((Prototype) o).getType() : null),
											null);
								}
								casier.ajouterValeur((Acteur) o, true);
							} else if (o instanceof BigDecimal) {
								if (casier == null) {
									casier = new Casier(null, null, Role.NOMBRE, null);
								}
								casier.ajouterValeur(new Acteur(Role.NOMBRE, o), true);
							} else if (o instanceof String) {
								if (casier == null) {
									casier = new Casier(null, null, Role.TEXTE, null);
								}
								casier.ajouterValeur(new Acteur(Role.TEXTE, o), true);
							}
							// Doit-on continuer
							continuer = ((position + 1) < size) && valeurs[position + 1] instanceof Object[];
						} while (continuer);
						if (casier == null) {
							// pour le cas casier vide :
							casier = new Casier(null, null, Role.TEXTE, null);
						}
						valeur_transformee = casier;
						if (operation_simple_en_cours == null)
							total = valeur_transformee;
						break;
					}
					case CROCHET: {
						Object o = shrink((Object[]) valeurs[++position], jobCourant, temoin);
						if (o instanceof Acteur) {
							valeur_transformee = ((Acteur) o).getNom().toString();
						} else
							throw new Exception("Crochet interdit avec " + o);
					}
						break;
					case CHEVRON: {
						position++;
						Object temp2 = valeurs[position];
						Object o = shrink((Object[]) temp2, jobCourant, temoin);
						RuntimeContext runtimeContext = (RuntimeContext) jobCourant.getRuntimeContext();
						if (o instanceof Acteur) {
							Acteur a_c2 = (Acteur) o;
							if (a_c2.getRole() == Role.TEXTE && (attribut_espece == null || attribut_espece instanceof String)) {
								String v = (String) a_c2.getValeur();
								ItemXML itemXML = ItemXML.factory(v, ItemXML.VALEUR);
								valeur_transformee = itemXML.retourneValeurTransformee(jobCourant, runtimeContext.getLinotte().getLangage());
								if (attribut_espece != null) {
									if (valeur_transformee instanceof Prototype) {
										valeur_transformee = ((Prototype) valeur_transformee).retourneAttribut((String) attribut_espece);
									} else {
										throw new Exception("Syntaxe impossible avec les especes");
									}
								}
								attribut_espece = null;
							} else
								throw new Exception("Impossible de charger l'acteur avec " + a_c2.getNom());
						} else if (o instanceof String && attribut_espece == null) {
							valeur_transformee = chargerActeur((String) o, null, jobCourant, temoin);
						} else if (o instanceof String && attribut_espece != null) {
							if (attribut_espece instanceof String)
								valeur_transformee = chargerActeur((String) o, (String) attribut_espece, jobCourant, temoin);
							else
								throw new Exception("Attribut d'espèce invalide avec " + attribut_espece);
						} else
							throw new Exception("Chevron interdit avec " + o);

						break;
					}
					}

					if (total == null) {
						total = valeur_transformee;
					}

				}
				// Vérification s'il n'y a pas un pointeur pour le mode
				// subtitution...
				if (((position + 1) < size) && valeurs[position + 1] == OPERATION.POINTEUR) {
					token_courant = valeurs[++position];
					pointeur = true;
				}

			} while (pointeur);

			/**
			 * Si c'est une opération simple : + * / -
			 */

			if (operation_simple_en_cours instanceof OperationSimple) {

				/**
				 * Si c'est un acteur, on extrait la valeur
				 */
				if (valeur_transformee instanceof Acteur) {
					Acteur a = ((Acteur) valeur_transformee);
					if (a.getRole() == Role.NOMBRE || a.getRole() == Role.TEXTE) {
						valeur_transformee = a.getValeur();
					} else {
						if (!(a.getRole() == Role.CASIER && operation_simple_en_cours instanceof Addition))
							throw new Exception("Opérations + - / * interdites avec " + a.getNom());
					}
				}
				total = operation_simple_en_cours.action(total, valeur_transformee, context);

				operation_simple_en_cours = null;

			}

			if (total == null) {
				total = valeur_transformee;
			}

			/**
			 * On avance
			 */
			position++;

		} while (position < size);
		return total;
	}

	private static boolean isActeur(String temp) {
		return /* affiche {} */temp.length() > 0 && temp.charAt(0) != ('\"');
	}

	protected static Object tryTransformeToBigDecimal(String valeur) {
		if (valeur.length() > 0 /* affiche {} */) {
			BigDecimal b = isBigDecimal(valeur);
			if (b != null)
				return b;
		}
		return valeur;
	}

	protected static String transforme(String valeur) {
		if ( /* affiche {} */valeur.length() > 0 && valeur.charAt(0) == '\"') {
			return valeur.substring(1, valeur.length() - 1);
		}
		return valeur;
	}

	public static BigDecimal isBigDecimal(String valeur) {
		int c = valeur.charAt(0);
		if ((c > 47 && c < 58) || c == 45 || c == 43 || c == 46)
			try {
				// Possibilité d'afficher un grand nombre :
				BigDecimal bd = new BigDecimal(valeur, MathContext.UNLIMITED);
				return stripTrailingZerosIfNecessary(bd);
			} catch (Exception e) {
			}
		return null;
	}

	/**
	 * Cette méthode essaye de retourner les entiers sans virgule et sans puissance
	 * Par exemple, 30.0 devient 30 et 3E1 devient 30
	 * @param bd
	 * @return
	 */
	public static BigDecimal stripTrailingZerosIfNecessary(BigDecimal bd) {
		if (bd.scale() == 0) {
			return bd;
		} else {
			BigDecimal stripped = bd.stripTrailingZeros();
			if (stripped.scale() < 0)
				return stripped.setScale(0);
			else
				return stripped;
		}
	}

	public static MathContext ameliorerPrecision(MathContext context, BigDecimal n1, BigDecimal n2) {
		//		System.out.println(context.getPrecision());
		//		System.out.println(n1.precision());
		//		System.out.println(n2.precision());
		if (n1.precision() > context.getPrecision() || n2.precision() > context.getPrecision()) {
			context = new MathContext(n1.precision() + n2.precision());
		}
		return context;
	}

	public static class Temoin {

		boolean acteur = false;

		public boolean isActeur() {
			return acteur;
		}

		public void setActeur(boolean acteur) {
			this.acteur = acteur;
		}

	}

	private static class Binaire {
		boolean valeur = false;
	}

	private static class Scanner {

		int position_debut = 0;

		int position_fin = 0;

		int position = 0;

		String chaine;

		Langage langage;

		Scanner(String s, Langage plangage) {
			chaine = s;
			langage = plangage;
		}

		char currentChar() {
			position_debut = position;
			position_fin = position + 1;
			return chaine.charAt(position);
		}

		boolean isCurrentString(String test) {
			int s = test.length();
			int max = position + s;
			int position2 = 0;
			if (max >= chaine.length()) {
				return false;
			}
			int i = position;
			for (; i < max; i++) {
				if (!(Character.toLowerCase(chaine.charAt(i)) == Character.toLowerCase(test.charAt(position2)))) {
					return false;
				}
				position2++;
			}
			if (chaine.charAt(i) != ' ' && chaine.charAt(i) != '(') {
				return false;
			}
			position_debut = position;
			position_fin = position + s;
			return true;
		}

		boolean isCurrentToken(String test) {
			int s = test.length();
			int max = position + s;
			int position2 = 0;
			if (max > chaine.length()) {
				return false;
			}
			int i = position;
			for (; i < max; i++) {
				if (!(Character.toLowerCase(chaine.charAt(i)) == Character.toLowerCase(test.charAt(position2)))) {
					return false;
				}
				position2++;
			}
			position_debut = position;
			position_fin = position + s;
			return true;
		}

		void avance() {
			position++;
		}

		void avance(int i) {
			position += i;
		}

		void retour() {
			position--;
		}

		boolean jepeux() {
			return position < chaine.length();
		}

		String parseValeur() {
			int position_debut = position;
			StringBuilder valeur = new StringBuilder();
			char c;
			boolean chaine = false, nombre = true, exposant = false;
			while (jepeux()) {
				c = currentChar();
				if (valeur.length() == 0 && c == '\"') {
					valeur.append(c);
					avance();
					chaine = true;
					nombre = false;
					continue;
				}

				// Patch pour prendre en compte les nombres de la forme :
				// 4.346655768693745643568852767504065E+208
				if (nombre && valeur.length() > 0 && (c == 'E' || c == 'e')) {
					valeur.append(c);
					avance();
					exposant = true;
					continue;
				}

				if (exposant && nombre && c == ' ') {
					avance();
					continue;
				}

				if (exposant && nombre && (c == '+' || c == '-')) {
					valeur.append(c);
					avance();
					continue;
				}

				exposant = false;

				// Fin patch sur les nombres

				if (valeur.length() == 0 && (c == '+' || c == '-')) {
					valeur.append(c);
					avance();
					// chaine = true;
					continue;
				}

				if (nombre && ((c < 48 || c > 57) && (c != 45 && c != 43 && c != '.'))) {
					nombre = false;
				}

				if (isCurrentString("@") && !chaine) {
					break;
				}
				if (isCurrentString(".") && !chaine && !nombre) {
					break;
				}

				boolean fait = false;
				if (!chaine)
					for (MathematiqueOperation operationMathematique : langage.getOperations()) {
						if (operationMathematique.getTypeSyntaxe() == TypeSyntaxe.TYPE_2 || operationMathematique.getTypeSyntaxe() == TypeSyntaxe.TYPE_3) {
							if (isCurrentString(" " + operationMathematique.getTexte())) {
								fait = true;
								break;
							}
						}
					}

				if (fait) {
					break;
				}

				if (c == '\"' && chaine) {
					valeur.append(c);
					avance();
					break;
				}
				if ((c == '.' && nombre) || !charParticuliers.contains(c) || (charParticuliers.contains(c) && chaine) || Character.isWhitespace(c)
						|| (c == ',' && chaine)) {
					valeur.append(c);
					avance();
					continue;
				}
				break;
			}
			this.position_debut = position_debut;
			position_fin = position;
			return valeur.toString().trim();
		}

		void mangeEspace() {
			while (jepeux() && Character.isWhitespace(currentChar())) {
				avance();
			}
		}

		public String toString() {
			return chaine.substring(position);
		}

	}

	private static Acteur chargerActeur(String acteur_valide, String attribut, Job job, Temoin temoin) throws ErreurException {
		if (job == null)
			throw new ErreurException(Constantes.SYNTAXE_ACTEUR_IMPOSSIBLE, acteur_valide);
		temoin.setActeur(true);
		JobContext jobContext = (JobContext) job.getContext();
		Livre livre = jobContext.getLivre();
		Chaine n = Chaine.produire(acteur_valide);
		Acteur acteur = null;

		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		// Si syntaxe Linotte 1.X
		boolean version1 = !runtimeContext.canDo(Habilitation.STACK_MEMORY_MANAGEMENT);

		if (version1) {
			// Linotte 1.X
			CalqueParagraphe calqueParagraphe = livre.getKernelStack().recupereDernierCalqueParagraphe();
			if (calqueParagraphe != null)
				acteur = calqueParagraphe.getActeurLocal(n);
			if (acteur == null) {
				if (livre.getParagraphe() != null)
					acteur = livre.getParagraphe().getActeur(n);
				if (acteur == null) {
					acteur = livre.getActeur(n, job);
					if (acteur == null) {
						/**
						 * Pour des raisons de performance, la recherche
						 * d'acteurs locaux à des sous-paragraphes est placée
						 * ici :
						 */

						acteur = livre.getKernelStack().rechercheActeurDansSousParagraphe(n);

						if (acteur == null) {
							/**
							 * Si le context contient l'habilitation pour
							 * accéder aux acteurs sans les avoir préalablement
							 * déclarés :
							 */
							if (runtimeContext.canDo(Habilitation.MEMORY_FULL_ACCESS)) {
								acteur = (Acteur) runtimeContext.getLibrairie().getActeurs().get(n.toString().toLowerCase());
								if (acteur != null)
									runtimeContext.getLibrairie().reCharger(acteur);
							}
							if (acteur == null)
								throw new ErreurException(Constantes.SYNTAXE_ACTEUR_INCONNU, acteur_valide);
						}
					}
				}
			}
		} else {
			// Linotte 2.0
			acteur = livre.getKernelStack().rechercheActeur(n);
			if (acteur == null) {
				acteur = livre.getActeur(n, job);
				if (acteur == null) {
					/**
					 * Si le context contient l'habilitation pour accéder aux
					 * acteurs sans les avoir préalablement déclarés :
					 */
					if (runtimeContext.canDo(Habilitation.MEMORY_FULL_ACCESS)) {
						acteur = (Acteur) runtimeContext.getLibrairie().getActeurs().get(n.toString().toLowerCase());
						if (acteur != null)
							runtimeContext.getLibrairie().reCharger(acteur);
					}
					if (acteur == null)
						throw new ErreurException(Constantes.SYNTAXE_ACTEUR_INCONNU, acteur_valide);
				}
			}
		}
		if (attribut != null) {
			if (!(acteur.getRole() == Role.ESPECE)) {
				throw new ErreurException(Constantes.SYNTAXE_ESPECE_INCONNUE, acteur_valide);
			} else {
				acteur = ((Prototype) acteur).retourneAttribut(attribut.toLowerCase());
				if (acteur == null) {
					throw new ErreurException(Constantes.SYNTAXE_CARACTERISTIQUE_INCONNUE, attribut);
				}
			}
		}
		return acteur;
	}

	private static StyleItem couleur(Scanner scanner, List<StyleItem> couleurs, int debut_style2, STYLE style) {
		return couleur(scanner, couleurs, debut_style2, style, null);
	}

	private static StyleItem couleur(Scanner scanner, List<StyleItem> couleurs, int debut_style2, STYLE style, Color souligne) {
		if (couleurs != null) {
			int delta = 0;
			if (scanner.chaine.length() > (scanner.position_fin - 1) && scanner.chaine.charAt(scanner.position_fin - 1) == ' ')
				delta = 1;
			StyleItem item = new StyleItem(style, scanner.position_debut + debut_style2, scanner.position_fin - scanner.position_debut - delta, souligne);
			couleurs.add(item);
			return item;
		}
		return null;
	}

	public static double mod(double x, double y) {
		double ans = x % y;
		if (ans < 0)
			ans = ans + y;
		return ans;
	}

	private static void fillStack(LinotteException e, JobContext jc) {
		if (jc != null) {
			Livre livre = (jc).getLivre();
			if (livre != null) {
				KernelStack kernelStack = livre.getKernelStack();
				e.setKernelStack((KernelStack) kernelStack.produceKernel());
			}
		}
	}
}