package org.linotte.frame.greffons;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.linotte.moteur.xml.actions.TildeAction.STATUS;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class BeanHandler extends DefaultHandler {

	private String nom;
	private String description;
	private String home;
	private String auteur;
	private String url;
	private String version;
	private String valeur;
	private List<BeanGreffon> beans = new ArrayList<BeanGreffon>();

	public BeanHandler() {
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		valeur = null;
		if ("greffon".equals(qName)) {
			nom = attributes.getValue("nom");
			description = attributes.getValue("description");
			home = attributes.getValue("home");
			auteur = attributes.getValue("auteur");
			url = attributes.getValue("url");
			version = attributes.getValue("version");
		}
	}

	@Override
	public void characters(char[] chars, int start, int length) {
		if (valeur == null) {
			valeur = new String(chars, start, length);
		} else {
			valeur = valeur + new String(chars, start, length);
		}
	}

	@Override
	public void endElement(String pUri, String pLocalName, String pQName) {
		if ("greffon".equals(pQName)) {
			BeanGreffon g = new BeanGreffon();
			beans.add(g);
			g.setNom(nom);
			g.setHome(home);
			g.setDescription(description);
			g.setUrl(url);
			g.setAuteur(auteur);
			g.setVersion(version);
			g.setStatut(STATUS.NOUVEAU);
		}
	}

	public List<BeanGreffon> chargerBeanGreffon(String url) {

		try {
			URLConnection conn = new URL(url).openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			final int MAX_LENGTH = 20000;
			byte[] buf = new byte[MAX_LENGTH];
			int total = 0;
			while (total < MAX_LENGTH) {
				int count = is.read(buf, total, MAX_LENGTH - total);
				if (count < 0) {
					break;
				}
				total += count;
			}
			is.close();

			String xml = new String(buf, 0, total);

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser;
			parser = factory.newSAXParser();
			parser.parse(new ByteArrayInputStream(xml.getBytes()), this);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return beans;

	}
}
