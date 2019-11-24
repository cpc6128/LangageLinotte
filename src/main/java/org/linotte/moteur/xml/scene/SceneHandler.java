package org.linotte.moteur.xml.scene;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.RegistreDesActions;
import org.linotte.moteur.xml.actions.EspeceAction;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SceneHandler extends DefaultHandler {

	private RegistreDesActions registreDesEtats;

	private Job moteur;

	private String espece;

	private List<ItemXML> attributs = new ArrayList<ItemXML>();

	private ErreurException erreur = null;

	public SceneHandler(RegistreDesActions pregistreDesEtats, Job pMoteur) {
		registreDesEtats = pregistreDesEtats;
		moteur = pMoteur;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) {
		espece = qName;
		attributs.clear();
		try {
			// System.out.print("Espece = " + espece + " : ");
			if (attributes.getValue("identifiant") == null)
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_PEINDRE, "Identifiant obligatoire");

			attributs.add(ItemXML.factory(attributes.getValue("identifiant"), ItemXML.ACTEUR));
			attributs.add(ItemXML.factory(espece, ItemXML.ACTEUR));

			Prototype modele = ((RuntimeContext) moteur.getRuntimeContext()).getLibrairie().getEspece(espece);

			if (modele == null)
				throw new ErreurException(Constantes.SYNTAXE_ESPECE_INCONNUE, espece);

			int max = attributes.getLength();
			for (int i = 0; i < max; i++) {
				if (!attributes.getQName(i).equals("identifiant")) {
					String att = attributes.getQName(i).toLowerCase();
					Acteur a = modele.retourneAttributSimple(att);
					if (a == null)
						throw new ErreurException(Constantes.SYNTAXE_ACTEUR_INCONNU, att);
					attributs.add(ItemXML.factory(att, ItemXML.ACTEUR));
					// On force le typage :
					if (a.getRole() == Role.NOMBRE)
						attributs.add(ItemXML.factory(attributes.getValue(i), ItemXML.VALEUR));
					else
						attributs.add(ItemXML.factory("\"" + attributes.getValue(i) + "\"", ItemXML.VALEUR));
				}
			}
			// System.out.println(attributs);
			Action etat = registreDesEtats.retourneAction(EspeceAction.NOM_ETAT);
			try {
				etat.analyse("instantiation", moteur, (ItemXML[]) attributs.toArray(new ItemXML[attributs.size()]), null);
			} catch (StopException e) {
			} catch (ErreurException e) {
				if (erreur == null)
					erreur = e;
			}
		} catch (ErreurException e) {
			if (erreur == null)
				erreur = e;
		} catch (Exception e) {
			if (erreur == null)
				erreur = new ErreurException(Constantes.SYNTAXE_PARAMETRE_PEINDRE, espece);
		}
	}

	@Override
	public void endElement(String pUri, String pLocalName, String pQName) {
	}

	public void chargerScene(ByteArrayInputStream flux) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser;
		try {
			parser = factory.newSAXParser();

			try {
				parser.parse(flux, this);
				parser.reset();
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Throwable e) {
				e.printStackTrace();
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

	}

	public ErreurException getErreur() {
		return erreur;
	}
}
