package org.linotte.frame.cahier.timbre.outils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linotte.frame.cahier.timbre.entite.Acteur;
import org.linotte.frame.cahier.timbre.entite.BlocDebut;
import org.linotte.frame.cahier.timbre.entite.BlocFin;
import org.linotte.frame.cahier.timbre.entite.Condition;
import org.linotte.frame.cahier.timbre.entite.Fonction;
import org.linotte.frame.cahier.timbre.entite.PorteurFonction;
import org.linotte.frame.cahier.timbre.entite.Pour;
import org.linotte.frame.cahier.timbre.entite.TantQue;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.Verbe;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.entite.i.PasSauvegarder;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;

public class Historique {

	private int MAX = 20;

	private List<List<Timbre>> historiques = new ArrayList<>();

	private Planche planche;

	private List<Timbre> photoPlanche;

	private boolean debug = false;

	public Historique(PlancheUI plancheUI) {
		planche = (Planche) plancheUI;
	}

	public void prendrePhoto() {
		if (debug)
			System.out.println("preparationfixe");
		photoPlanche = clone(planche.timbres);
	}

	public void annulerPhoto() {
		photoPlanche = null;
	}

	public void archiverPhoto() {
		if (photoPlanche != null) {
			if (debug)
				System.out.println("fixe");
			historiques.add(photoPlanche);
			if (historiques.size() > MAX) {
				historiques.remove(0);
			}
			photoPlanche = null;
		} else {
			if (debug)
				System.out.println("fixe vide");
		}
	}

	public List<Timbre> depilerPhoto() {
		photoPlanche = null;
		if (!historiques.isEmpty())
			return historiques.remove(historiques.size() - 1);
		else
			return null;
	}

	public boolean estceVide() {
		return historiques.isEmpty();
	}

	public int taille() {
		return historiques.size();
	}

	private List<Timbre> clone(List<Timbre> timbres) {
		Map<Timbre, Timbre> livretFamille = new HashMap<>();
		// On clone :
		List<Timbre> clones = new ArrayList<Timbre>();
		for (Timbre timbre : timbres) {
			if (!(timbre instanceof PasSauvegarder)) {
				Timbre clone = (Timbre) timbre.clonerPourDuplication();
				livretFamille.put(timbre, clone);
				clones.add(clone);
			}
		}
		// Reconstruction des nouveaux liens avec les clones :
		for (Timbre clone : clones) {
			if (clone instanceof BlocDebut) {
				BlocDebut timbre = (BlocDebut) clone;
				if (timbre.blocfin != null)
					timbre.blocfin = (BlocFin) livretFamille.get(timbre.blocfin);
				else {
					System.out.println("blocdebut erreur " + timbre);
				}
			}
			if (clone instanceof BlocFin) {
				BlocFin timbre = (BlocFin) clone;
				if (timbre.blocDebut != null)
					timbre.blocDebut = (BlocDebut) livretFamille.get(timbre.blocDebut);
				else
					System.out.println("blocfin erreur");
			}
			if (clone instanceof Condition) {
				Condition timbre = (Condition) clone;
				if (timbre.timbreSuivantSecondaire != null)
					timbre.timbreSuivantSecondaire = (Timbre) livretFamille.get(timbre.timbreSuivantSecondaire);
				if (timbre.formule != null)
					if (livretFamille.containsKey(timbre.formule))
						timbre.formule = cloneDeposable(livretFamille, (Deposable) livretFamille.get(timbre.formule));
					else
						timbre.formule = cloneDeposable(livretFamille, (Deposable) timbre.formule.clonerPourDuplication());
			}
			if (clone instanceof Pour) {
				Pour timbre = (Pour) clone;
				//
				if (timbre.acteur != null)
					if (livretFamille.containsKey(timbre.acteur))
						timbre.acteur = cloneDeposable(livretFamille, (Deposable) livretFamille.get(timbre.acteur));
					else
						timbre.acteur = cloneDeposable(livretFamille, (Deposable) timbre.acteur.clonerPourDuplication());
				//
				if (timbre.indexDebut != null)
					if (livretFamille.containsKey(timbre.indexDebut))
						timbre.indexDebut = cloneDeposable(livretFamille, (Deposable) livretFamille.get(timbre.indexDebut));
					else
						timbre.indexDebut = cloneDeposable(livretFamille, (Deposable) timbre.indexDebut.clonerPourDuplication());
				//
				if (timbre.indexFin != null)
					if (livretFamille.containsKey(timbre.indexFin))
						timbre.indexFin = cloneDeposable(livretFamille, (Deposable) livretFamille.get(timbre.indexFin));
					else
						timbre.indexFin = cloneDeposable(livretFamille, (Deposable) timbre.indexFin.clonerPourDuplication());
			}
			if (clone instanceof TantQue) {
				TantQue timbre = (TantQue) clone;
				if (timbre.formule != null)
					if (livretFamille.containsKey(timbre.formule))
						timbre.formule = cloneDeposable(livretFamille, (Deposable) livretFamille.get(timbre.formule));
					else
						timbre.formule = cloneDeposable(livretFamille, (Deposable) timbre.formule.clonerPourDuplication());
			}
			if (clone instanceof Timbre) {
				Timbre timbre = (Timbre) clone;
				if (timbre.timbreSuivant != null)
					timbre.timbreSuivant = (Timbre) livretFamille.get(timbre.timbreSuivant);
			}
			if (clone instanceof Verbe) {
				Verbe timbre = (Verbe) clone;
				for (int i = 0; i < timbre.nbParametres; i++) {
					if (timbre.parametres[i] != null) {
						if (livretFamille.containsKey(timbre.parametres[i]))
							timbre.parametres[i] = cloneDeposable(livretFamille, (Deposable) livretFamille.get(timbre.parametres[i]));
						else
							timbre.parametres[i] = cloneDeposable(livretFamille, (Deposable) timbre.parametres[i].clonerPourDuplication());
					}
				}
			}
			if (clone instanceof Fonction) {
				Fonction timbre = (Fonction) clone;
				for (int i = 0; i < timbre.parametres.size(); i++) {
					if (livretFamille.containsKey(timbre.parametres.get(i)))
						timbre.parametres.set(i, (Acteur) livretFamille.get(timbre.parametres.get(i)));
					else
						timbre.parametres.set(i, (Acteur) timbre.parametres.get(i).clonerPourDuplication());
				}
			}
		}
		return clones;
	}

	private Deposable cloneDeposable(Map<Timbre, Timbre> livretFamille, Deposable deposable) {
		if (deposable instanceof Acteur)
			return deposable;
		else if (deposable instanceof PorteurFonction) {
			PorteurFonction porteur = (PorteurFonction) ((PorteurFonction) deposable).clonerPourDuplication(); // Ce n'est pas un timbre, il n'était donc pas dupliqué, mais est-ce utile ?

			if (livretFamille.containsKey(porteur.fonction))
				porteur.fonction = (Fonction) livretFamille.get(porteur.fonction);
			else
				porteur.fonction = (Fonction) porteur.fonction.clonerPourDuplication();

			// On clone les parametres :
			int index = 0;
			for (Deposable d : porteur.parametres) {
				if (d instanceof PorteurFonction)
					porteur.parametres.set(index, cloneDeposable(livretFamille, d));
				else // C'est un acteur {
				{
					if (d != null)
						if (livretFamille.containsKey(d))
							porteur.parametres.set(index, (Deposable) livretFamille.get(d));
						else
							porteur.parametres.set(index, (Deposable) d.clonerPourDuplication());
				}
				index++;
			}
			return porteur;
		} else
			return deposable;
	}

}
