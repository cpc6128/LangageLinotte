package linotte.greffon;

import org.linotte.greffons.externe.Greffon;

public class Majordome extends Greffon {

	@Slot(nom = "présentation")
	public String presentation(String prenom) {
		String serviteur = getAttributeAsString("serviteur");
		return "Bonsoir, mon cher " + prenom + ", je suis votre serviteur " + serviteur;
	}

}
