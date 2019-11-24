package org.linotte.frame.coloration;

import java.util.List;

import org.linotte.moteur.xml.alize.parseur.ParserContext;

/**
 * Cette classe a été créée pour optimiser la coloration de l'Atelier
 * 
 * @author CPC
 * 
 */
public class StyleMathematiques {

	private String formule;
	private List<StyleItem> list;
	private int debut_style;
	private ParserContext parserContext;
	private boolean prototype_attribut;

	public StyleMathematiques(String formule, List<StyleItem> list, int debut_style, ParserContext parserContext, boolean prototype_attribut) {
		super();
		this.formule = formule;
		this.list = list;
		this.debut_style = debut_style;
		this.parserContext = parserContext;
		this.prototype_attribut = prototype_attribut;
	}

	public String getFormule() {
		return formule;
	}

	public List<StyleItem> getList() {
		return list;
	}

	public int getDebut_style() {
		return debut_style;
	}

	public ParserContext getParserContext() {
		return parserContext;
	}

	public boolean isPrototype_attribut() {
		return prototype_attribut;
	}

}
