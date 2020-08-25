package org.linotte.frame.cahier.timbre.entite;

import org.linotte.frame.cahier.timbre.entite.e.Role;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.ActeurUI;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class Acteur extends Textuel implements Deposable {

	public Role role;

	public String valeur;

	public transient ActeurUI ui = UsineATheme.getActeurUI(this);

	public Acteur(String nom, Role role, String valeur, PlancheUI planche) {
		super(nom, planche);
		this.role = role;
		this.valeur = valeur;
		if (texte == null && planche != null) {
			texte = "variable " + ((Planche) planche).compteurSuivant();
		}
	}

	@Override
	public void dessine() {
		ui.dessine(planche);
		super.dessine();
	}

	@Override
	public boolean clique(int x, int y) {
		if (ui.clique(x, y))
			return true;
		else
			return super.clique(x, y);
	}

	@Override
	public boolean contient(int x2, int y2) {
		boolean b = ui.contient(x2, y2);
		if (b) {
			return true;
		} else {
			return super.contient(x2, y2);
		}
	}

	@Override
	public Timbre clonerPourCreation() {
		return new Acteur(null, role, valeur, planche);
	}

	@Override
	public void pre_dessine() {
		ui.pre_dessine(planche);
		super.pre_dessine();
	}

	// Bean

	public Acteur() {
		super();
	}

	@Override
	public Timbre clonerPourDuplication() {
		Acteur clone = new Acteur(texte, role, valeur, planche);
		super.clonerPourDuplication(clone);
		return clone;
	}

	public String toString() {
		return texte;
	}

	public boolean estCeUnParametre() {
		Timbre premier = TimbresHelper.recherchePremiertimbre(this);
		if (premier instanceof Fonction) {
			return ((Fonction) premier).parametres.contains(this);
		}
		return false;
	}

}
