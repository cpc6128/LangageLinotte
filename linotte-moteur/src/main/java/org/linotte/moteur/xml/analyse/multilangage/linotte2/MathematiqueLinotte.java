package org.linotte.moteur.xml.analyse.multilangage.linotte2;

import org.linotte.frame.coloration.StyleItem;
import org.linotte.frame.coloration.StyleItem.STYLE;
import org.linotte.moteur.xml.analyse.multilangage.MathematiqueOperation;
import org.linotte.moteur.xml.analyse.multilangage.TypeSyntaxe;
import org.linotte.moteur.xml.operation.binaire.*;
import org.linotte.moteur.xml.operation.i.Operation;
import org.linotte.moteur.xml.operation.ternaire.Ter;
import org.linotte.moteur.xml.operation.unaire.*;

public enum MathematiqueLinotte implements MathematiqueOperation {

	// Type 1
	hasard("hasard", Hasard.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	carre("carré", Carre.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	sin("sin", Sin.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	asin("asin", ASin.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	acos("acos", ACos.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	cos("cos", Cos.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	entier("entier", Entier.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	decimal("décimal", Decimal.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	arrondi("arrondi", Arrondi.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	tan("tan", Tan.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	atan2("atan2", ATan2.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	atan("atan", ATan.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	log("log", Log.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	logn("logn", LogN.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	abs("abs", Abs.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	racine("racine", Racine.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	cube("cube", Cube.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	non("non", Non.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	ascii("ascii", Ascii.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	chr("chr", Chr.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	ter("ter", Ter.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	ternaire("ternaire", Ter.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	clone("clone", Clone.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_1),
	
	// Type 2
	puiss("puiss", Puiss.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	puissance("puissance", Puiss.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	mod("mod", Mod.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	modulo("modulo", Mod.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	rg("rg", Rg.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	rd("rd", Rd.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	et("et", Et.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	sup("sup", Sup.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	égal("égal", Egal.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	inf("inf", Inf.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	diff("diff", Diff.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	ou("ou", Ou.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	xou("xou", Xou.class, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_2),
	
	// Type 3 : visible de toile
	de("de", null, StyleItem.STYLE.MATH, TypeSyntaxe.TYPE_3),
	
	;
	private String texte;
	private Class<? extends Operation> operation;
	private StyleItem.STYLE style;
	private TypeSyntaxe typeSyntaxe;

	private MathematiqueLinotte(String texte, Class<? extends Operation> operation, StyleItem.STYLE style, TypeSyntaxe typeSyntaxe) {
		this.texte = texte;
		this.operation = operation;
		this.style = style;
		this.typeSyntaxe = typeSyntaxe;
	}

	public String getTexte() {
		return texte;
	}

	@Override
	public Class<? extends Operation> getOperation() {
		return operation;
	}

	@Override
	public STYLE getStyle() {
		return style;
	}

	@Override
	public TypeSyntaxe getTypeSyntaxe() {
		return typeSyntaxe;
	}

	@Override
	public MathematiqueOperation[] operations() {
		return values();
	}

}
