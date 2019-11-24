package org.linotte.greffons.impl;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Parser un flux RSS
 * http://www.fobec.com/CMS/java/class/lire-flux-rss-parsant-fichier-xml_1014.html
 * @author Fobec 2010
 */

public class RSSReader {

	/**
	 * Parser le fichier XML
	 * @param feedurl URL du flux RSS
	 * @throws Exception 
	 */
	public List<Article> parse(String feedurl) throws Exception {
		List<Article> articles = new ArrayList<Article>();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		URL url = new URL(feedurl);
		Document doc = builder.parse(url.openStream());
		NodeList nodes = null;
		Element element = null;
		/**
		 * Titre et date du flux
		 */
		nodes = doc.getElementsByTagName("title");
		/**
		 * Elements du flux RSS
		 **/
		nodes = doc.getElementsByTagName("item");
		for (int i = 0; i < nodes.getLength(); i++) {
			element = (Element) nodes.item(i);
			articles.add(new Article(readNode(element, "title", null), dateToFrench(DateUtils.parseDate(readNode(element, "pubDate", null))), readNode(element,
					"description", null), readNode(element, "link", null), readImage(element)));
		}
		if (articles.size() == 0) {
			// Atom ?
			nodes = doc.getElementsByTagName("entry");
			for (int i = 0; i < nodes.getLength(); i++) {
				element = (Element) nodes.item(i);
				articles.add(new Article(readNode(element, "title", null), dateToFrench(DateUtils.parseDate(readNode(element, "published", null))), readNode(
						element, "content", null), readNode(element, "link", "href"), readImage(element)));
			}
		}
		return articles;
	}

	private String readImage(Element element) {
		//<enclosure url="http://xxxxxxxxx/xxxxx.jpg" length="0" type="image/jpeg"/>
		//<enclosure url= »URL_DE_VOTRE_IMAGE » type= »image/jpeg » ></enclosure>
		Node node = getChildByName(element, "enclosure");
		if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
			Element entity = (Element) node;
			String type = entity.getAttribute("type");
			if (type.startsWith("image")) {
				return entity.getAttribute("url");
			}
		}
		return "";
	}

	/**
	 * Méthode permettant de retourner ce que contient d'un noeud
	 * @param _node le noeud principal
	 * @param _path suite des noms des noeud sans espace séparer par des "|"
	 * @return un string contenant le valeur du noeud voulut
	 */
	private String readNode(Node _node, String _path, String attribut) {

		String[] paths = _path.split("\\|");
		Node node = null;

		if (paths != null && paths.length > 0) {
			node = _node;

			for (int i = 0; i < paths.length; i++) {
				node = getChildByName(node, paths[i].trim());
			}
		}

		if (node != null) {
			if (attribut == null)
				return node.getTextContent();
			else {
				if (node.getAttributes().getNamedItem(attribut) != null) {
					return node.getAttributes().getNamedItem(attribut).getTextContent();
				} else
					return "";
			}
		} else {
			return "";
		}
	}

	/**
	 * renvoye le nom d'un noeud fils a partir de son nom
	 * @param _node noeud pricipal
	 * @param _name nom du noeud fils
	 * @return le noeud fils
	 */
	private Node getChildByName(Node _node, String _name) {
		if (_node == null) {
			return null;
		}
		NodeList listChild = _node.getChildNodes();

		if (listChild != null) {
			for (int i = 0; i < listChild.getLength(); i++) {
				Node child = listChild.item(i);
				if (child != null) {
					if ((child.getNodeName() != null && (_name.equals(child.getNodeName())))
							|| (child.getLocalName() != null && (_name.equals(child.getLocalName())))) {
						return child;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Afficher une Date GML au format francais
	 * @param gmtDate
	 * @return
	 */
	private String dateToFrench(Date gmtDate) {
		SimpleDateFormat dfFrench = new SimpleDateFormat("EEEE, d MMMM yyyy HH:mm:ss", Locale.FRANCE);
		return dfFrench.format(gmtDate.getTime());
	}

}