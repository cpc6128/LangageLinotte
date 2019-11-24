package org.linotte.moteur.xml.alize.kernel.processus;

import java.util.ArrayList;
import java.util.List;

import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.alize.kernel.i.ActionDynamic;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.analyse.GroupeItemXML;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.outils.Cloneur;

public class ProcessusFactory {

	@SuppressWarnings("unchecked")
	public static Processus createProcessus(String nom, List<ItemXML> items, List<?> tableau_annotations, Action etat, int lastPositionLigne) {
		Processus processus;
		ItemXML[][] tab2D = null;
		boolean produit_cartesien = false;
		if (etat instanceof IProduitCartesien) {
			for (ItemXML i : items) {
				if (i instanceof GroupeItemXML) {
					produit_cartesien = true;
					break;
				}
			}
		}
		if (produit_cartesien) {
			// A , B & C
			// A B
			// A C
			List<List<ItemXML>> matrice = new ArrayList<List<ItemXML>>();
			matrice.add(items);
			int pos = 0;
			for (ItemXML item : items) {
				if (item instanceof GroupeItemXML) {
					List<List<ItemXML>> matrice2 = Cloneur.clone(matrice);
					ItemXML[] temp = ((GroupeItemXML) item).items;
					matrice.clear();
					for (ItemXML t : temp) {
						for (List<ItemXML> l : matrice2) {
							l = Cloneur.clone(l);
							l.set(pos, t);
							matrice.add(l);
						}
					}
				}
				pos++;
			}
			// Transformation de la matrice en tableau
			// TODO : faire en sorte que la matrice soit déjà un tableau
			tab2D = new ItemXML[matrice.size()][];
			pos = 0;
			for (List<ItemXML> itemXMLs : matrice) {
				tab2D[pos++] = itemXMLs.toArray(new ItemXML[0]);
			}

		}

		if (etat instanceof ActionDispatcher) {
			processus = new ProcessusDispatcher(etat, nom, produit_cartesien ? tab2D : items.toArray(new ItemXML[0]),
					tableau_annotations != null ? (String[]) tableau_annotations.toArray(new String[0]) : null, lastPositionLigne, produit_cartesien);
		} else if (etat instanceof ActionDynamic) {
			processus = new ProcessusDynamic(etat, nom, produit_cartesien ? tab2D : items.toArray(new ItemXML[0]),
					tableau_annotations != null ? (String[]) tableau_annotations.toArray(new String[0]) : null, lastPositionLigne, produit_cartesien);
		} else {
			if (produit_cartesien)
				processus = new Processus(etat, nom, produit_cartesien ? tab2D : items.toArray(new ItemXML[0]),
						(String[]) (tableau_annotations != null ? tableau_annotations.toArray(new String[0]) : null), lastPositionLigne, produit_cartesien);
			else
				processus = new ProcessusSimple(etat, nom, items.toArray(new ItemXML[0]),
						(String[]) (tableau_annotations != null ? tableau_annotations.toArray(new String[0]) : null), lastPositionLigne);
		}

		return processus;
	}
}
