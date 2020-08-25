package org.linotte.moteur.xml.analyse;

import org.linotte.frame.latoile.Couleur;
import org.linotte.moteur.entites.Livre;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.Messages;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.alize.parseur.XMLIterator;
import org.linotte.moteur.xml.alize.parseur.a.NExpression;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.alize.parseur.noeud.NNoeud;
import org.linotte.moteur.xml.analyse.multilangage.Langage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;

public class Grammaire {

	private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

	private Element xml_definition = null;

	private NNoeud grammaire_livre;

	private String SPEC = null;

	private Set<String> findeligne = new HashSet<String>();

	private Set<String> mots = new TreeSet<String>();

	private Document documentGrammaire = null;

	private Document documentDefinitions = null;

	private Langage langage;

	public Grammaire(InputStream grammairexml, InputStream definitionsxml, Langage plangage) {
		langage = plangage;
		Element xml;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			documentGrammaire = builder.parse(grammairexml);
			documentDefinitions = builder.parse(definitionsxml);
		} catch (SAXParseException spe) {
			spe.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		xml = documentGrammaire.getDocumentElement();
		xml_definition = documentDefinitions.getDocumentElement();
		analyseDefinitions(xml_definition);
		analyseCompletion(xml);
		grammaire_livre = new NNoeud(documentGrammaire.getElementsByTagName("livre").item(0));
		new PreAnalyseXML().traduire(grammaire_livre);
	}

	public Element getDefinitionsXml() {
		return xml_definition;
	}

	private void extraction(Element racine, String clef) {
		XMLIterator l1 = new XMLIterator(racine.getElementsByTagName(clef));
		while (l1.hasNext()) {
			Noeud node = l1.next();
			if (node instanceof NExpression) {
				NExpression expression = (NExpression) node;
				String v = expression.getValeur().toString().trim().toLowerCase();
				if (v.length() > 2 && v.indexOf('\n') == -1 && !mots.contains(v) && expression.getAttribut("r") == null /*Ce n'est pas une couleur*/) {
					mots.add(v);
				}
			}
		}
	}

	private void analyseCompletion(Element racine) {
		extraction(racine, "acteur");
		extraction(racine, "valeur");
		extraction(racine, "token");
		extraction(racine, "cible");
	}

	private void analyseDefinitions(Element racine) {
		SPEC = racine.getAttribute("version");
		XMLIterator l1 = new XMLIterator(racine.getElementsByTagName("parametre"));
		while (l1.hasNext()) {
			Noeud node = l1.next();
			XMLIterator it = node.getFils();
			while (it.hasNext()) {
				NExpression n = (NExpression) it.next();
				if ("especesgraphique".equals(node.getAttribut("nom")))
					PrototypeGraphique.addEspecesGraphique(n.getValeur().toString());
				else if ("couleurs".equals(node.getAttribut("nom")))
					Couleur.ajouterCouleur(n.getValeur().toString(), new Color(Integer.valueOf(n.getAttribut("r")), Integer.valueOf(n.getAttribut("g")),
							Integer.valueOf(n.getAttribut("b"))));
				else if ("acteurs".equals(node.getAttribut("nom")))
					Livre.ajouterActeursSystemes(n.getAttribut("id"), n.getValeur().toString());
				else if ("erreurs".equals(node.getAttribut("nom")))
					Messages.creerErreur(n.getAttribut("id"), n.getValeur().toString());
				else if ("chaine".equals(node.getAttribut("nom"))) {
					ItemXML.setChaineDebut(n.getValeur().toString());
				} else if ("separateur".equals(node.getAttribut("nom"))) {
					findeligne.add(n.getAttribut("valeur"));
				} else if ("synonymes".equals(node.getAttribut("nom"))) {
					XMLIterator iterator = n.getFils();
					List<Chaine> l = new ArrayList<Chaine>();
					while (iterator.hasNext())
						l.add(((NExpression) iterator.next()).getValeur());
					Synonyme.addSynonyme(l);
				}
			}
		}
	}

	public String getSPEC() {
		if (SPEC == null)
			return " -";
		else
			return SPEC;
	}

	public Set<String> getFindeligne() {
		return findeligne;
	}

	public Set<String> getMots() {
		return mots;
	}

	public synchronized NNoeud retourneGrammaire() {
		return grammaire_livre.cloner();
	}

	public Langage getLangage() {
		return langage;
	}
	
}
