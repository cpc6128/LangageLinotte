/***********************************************************************
 * Linotte                                                             *
 * Version release date : September 01, 2006                           *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                           *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.moteur.xml.analyse;

import org.linotte.moteur.xml.alize.parseur.XMLIterator;
import org.linotte.moteur.xml.alize.parseur.a.NExpression;
import org.linotte.moteur.xml.alize.parseur.a.Noeud;
import org.linotte.moteur.xml.alize.parseur.noeud.NEtat;
import org.linotte.moteur.xml.alize.parseur.noeud.NNoeud;
import org.linotte.moteur.xml.alize.parseur.noeud.NToken;

import java.util.*;

public class PreAnalyseXML {

	private Map<String, List<Noeud>> map_id = new HashMap<String, List<Noeud>>();

	private Noeud abuilder = null;

	private Noeud pere_abuilder = null;

	private int positionARetenir = 0;

	public PreAnalyseXML() {
	}

	public void traduire(NNoeud pere) {
		passe1(pere);
		pere.getFils().recommencer();
		passe2(pere);
		pere.getFils().recommencer();
		try {
			passe3(pere);
		} catch (CloneException e) {
			// On arrive jamais ici
		}
		pere.getFils().recommencer();
	}

	private void passe1(Noeud pere) {
		XMLIterator i = pere.getFils();
		String s, a;
		Noeud node;
		while (i.hasNext()) {
			node = i.next();
			s = node.getNom();
			if (s.equals("p:build")) {
			} else if (s.equals("p:copynode")) {
			} else {
				a = node.getAttribut("id");
				if (a != null) {
					poserNode(a, node);
				}
			}
			passe1(node);
		}
	}

	private void passe2(Noeud pere) {
		XMLIterator i = pere.getFils();
		String s, target;
		Noeud node;
		while (i.hasNext()) {
			node = i.next();
			int ancienne_position = i.getPosition();
			s = node.getNom();
			if (s.equals("p:build")) {
				// Découverte d'un build
				abuilder = node;
				pere_abuilder = pere;
				// on supprime le build original
				pere_abuilder.supprimer(ancienne_position - 1);
				positionARetenir = ancienne_position - 1;
			} else if (s.equals("p:copynode")) {
				// Découverte d'un build
				i.supprimer(i.getPosition() - 1);
				target = node.getAttribut("target");
				Iterator<?> list = ((List<?>) map_id.get(target)).iterator();
				while (list.hasNext()) {
					int position = i.getPosition();
					Noeud fils = ((Noeud) list.next()).cloner();
					Noeud clone = abuilder.cloner();
					clone.getFils().hasNext();
					XMLIterator clone_fils = clone.getFils().next().getFils();
					// on insere a la place de copynode dans le build cloné un
					// clone des fils des nodes dans list
					XMLIterator fils_de_fils = fils.getFils();
					while (fils_de_fils.hasNext()) {
						Noeud r = fils_de_fils.next();
						clone_fils.insererAvant(r, position);
						position++;
					}
					clone.getFils().recommencer();
					// Lancement de l'initialisation :
					clone.getFils().hasNext();
					pere_abuilder.insererAvant(clone.getFils().next(), positionARetenir);
					// on insere cette node build resultat apres le build
					// original.
				}
			}
			passe2(node);
		}
	}

	/**
	 * Pour autoriser le format : "si 1<3 affiche vrai" en plus de "si 1<3, affiche vrai" 
	 * hack_analyse=suppressionvirgule
	 * Linotte 2.3
	 * @param pere
	 * @throws CloneException 
	 */
	private void passe3(Noeud pere) throws CloneException {
		XMLIterator i = pere.getFils();
		Noeud nodeSource;
		Noeud nodeAtraiter;
		while (i.hasNext()) {
			nodeSource = i.next();
			if (nodeSource.getAttribut("hack_analyse") != null) {
				NEtat etat_a_hacker = (NEtat) i.next();
				//i.next();
				nodeAtraiter = i.next();
				if (nodeAtraiter instanceof NToken) {
					// On clone cette ligne pour l'ancienne syntaxe
					Noeud clone = pere.cloner();
					clone.getFils().hasNext(); // <- force le chargement des attributs
					NExpression source = (NExpression) nodeSource;
					NExpression aSupprimer = (NExpression) nodeAtraiter;
					source.setValeur(aSupprimer.getValeur());
					source.copieAttributs(aSupprimer);
					//i.supprimer(i.getPosition() - 1); <- on le desactive
					aSupprimer.hack = true;
					etat_a_hacker.hack = true;
					// On recupere l'etat :
					throw new CloneException(clone);
				}
			}
			try {
				passe3(nodeSource);
			} catch (CloneException e) {
				// On ajoute le clone :
				i.insererAvant(e.clone, i.getPosition() - 1);
			}
		}
	}

	private void poserNode(String a, Noeud node) {
		List<Noeud> list = (List<Noeud>) map_id.get(a);
		if (list == null)
			list = new ArrayList<Noeud>();
		map_id.put(a, list);
		list.add(0, node);
	}

}