package org.linotte.frame.cahier.timbre.entite.e;

public enum Parfum {
	AFFICHER("Afficher", 1), DEMANDER("Demander", 1), VALOIR("Prendre", 2), //
	EFFACERTABLEAU("Effacer", 0), RETOURNER("Retourner", 1), UNDO, AIDE, CATEGORIE_BASE, //
	CATEGORIE_GRAPHIQUE, CATEGORIE_BOUCLE, //
	PARCOURIR("Parcourir", 1), //
	REVENIR("Revenir", 0), //
	DEPLACE_HAUT("Vers le haut", 2), //
	DEPLACE_BAS("Vers le bas", 2), //
	DEPLACE_DROITE("Vers la droite", 2), //
	DEPLACE_GAUCHE("Vers la gauche", 2), //
	DEPLACE_VERS("Vers le point", 3), //
	@Deprecated
	PROJETTE("Projeter", 1), //
	DEPLACE_AVANT("Vers l'avant", 2), //
	TOURNE_DROITE("Tourne à droite", 2), //
	TOURNE_GAUCHE("Tourne à gauche", 2), //
	LEVER_CRAYON("Lever crayon", 1), //
	BAISSER_CRAYON("Baisser crayon", 1),//
	;

	public String titre;

	public int nb;

	Parfum() {
		// Ce n'est pas un verbe
		titre = null;
		nb = -1;
	}

	Parfum(String titre, int nb) {
		this.titre = titre;
		this.nb = nb;
	}

}
