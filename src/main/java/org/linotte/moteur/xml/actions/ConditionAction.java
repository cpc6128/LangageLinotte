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

package org.linotte.moteur.xml.actions;

import java.awt.Shape;
import java.awt.geom.Area;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.ActionDispatcher;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.appels.Appel;
import org.linotte.moteur.xml.appels.Condition;
import org.linotte.moteur.xml.appels.Condition.ETAT_CONDITION;
import org.linotte.moteur.xml.outils.MathematiquesConstantes;

public class ConditionAction extends Action implements ActionDispatcher {

	public ConditionAction() {
		super();
	}

	@Override
	public String clef() {
		return "condition";
	}

	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();

		// Vérification du cas sinon :

		if (jobContext.getLivre().getKernelStack().size() == 0) {
			jobContext.getLivre().getKernelStack().ajouterAppel(new Condition());
		} else {
			try {
				Appel appel = jobContext.getLivre().getKernelStack().regarderDernierAppel();
				// Création d'un objet Condition :
				if (appel == null || !(appel instanceof Condition)) {
					jobContext.getLivre().getKernelStack().ajouterAppel(new Condition());
				}
			} catch (Exception e) {
			}
		}
		Processus processus = ((ProcessusDispatcher) job.getCurrentProcessus()).getProcessusSecondaire();

		if (analyseCondition(param, job, valeurs)) {
			return lancerEtat(job, processus);
		} else {
			return nePasLancerEtat(job, processus);
		}
	}

	public static boolean analyseCondition(String param, Job job, ItemXML[] valeurs) throws Exception {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		ConditionAction etatCondition = runtimeContext.getLinotte().getRegistreDesEtats().getActionCondition();

		if (param == null || valeurs.length == 0) {
			// Cas du verbe Essayer
			return true;
		} else if (param.startsWith("est plus petit ou")) { // est plus petit ou égal à
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			if (moins(a1, a2) || equals(a1, a2)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("est plus grand ou")) { // est plus petit ou égal à
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			if (plus(a1, a2) || equals(a1, a2)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("col")) { // lision
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			if (collision(a1, a2)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("co")) {// ntient
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			if (contient(a1, a2)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("est égal à v")) {// rai
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			if (equals(a1, new Acteur(Role.NOMBRE, MathematiquesConstantes.VRAI))) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("est é")) {// gal à
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			if (equals(a1, a2)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("est d")) {// ifférent de
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			if (!equals(a1, a2)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("est moins p")) {// etit que
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			if (plus(a1, a2)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("est plus p")) {// etit que
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			if (moins(a1, a2)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("est moins g")) {// rand que
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			if (moins(a1, a2)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("est plus g")) {// rand que
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			Acteur a2 = acteurs[1];
			if (plus(a1, a2)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("si v")) {// ide
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			if (vide(a1)) {
				return true;
			} else {
				return false;
			}
		} else if (param.startsWith("si n")) {// on vide
			Acteur[] acteurs = etatCondition.extractionDesActeurs(Constantes.SYNTAXE_CONDITIONS_INVALIDE, job, valeurs);
			Acteur a1 = acteurs[0];
			if (!vide(a1)) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	private static boolean plus(Acteur a1, Acteur a2) throws ErreurException {
		if (!(a1.getRole() == a2.getRole()))
			throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_MEME_ROLE);
		if (a1.getRole() == Role.NOMBRE) {
			BigDecimal i1 = (BigDecimal) a1.getValeur();
			BigDecimal i2 = (BigDecimal) a2.getValeur();
			return i1.compareTo(i2) > 0;
		}
		if (a1.getRole() == Role.TEXTE) {
			String i1 = (String) a1.getValeur();
			String i2 = (String) a2.getValeur();
			return i1.compareTo(i2) > 0;
		}
		throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_INVALIDE);
	}

	private static boolean moins(Acteur a1, Acteur a2) throws ErreurException {

		if (!(a1.getRole() == a2.getRole()))
			throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_MEME_ROLE);
		if (a1.getRole() == Role.NOMBRE) {
			BigDecimal i1 = (BigDecimal) a1.getValeur();
			BigDecimal i2 = (BigDecimal) a2.getValeur();
			return i1.compareTo(i2) < 0;
		}
		if (a1.getRole() == Role.TEXTE) {
			String i1 = (String) a1.getValeur();
			String i2 = (String) a2.getValeur();
			return i1.compareTo(i2) < 0;
		}
		throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_INVALIDE);

	}

	private static boolean collision(Acteur a1, Acteur a2) throws Exception {

		if (!(a1.getRole() == Role.ESPECE) || !(a2.getRole() == Role.ESPECE))
			throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_INVALIDE);
		Prototype e1 = (Prototype) a1;
		Prototype e2 = (Prototype) a2;
		if (!e1.isEspeceGraphique() || !e2.isEspeceGraphique())
			throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_INVALIDE);
		PrototypeGraphique eg1 = (PrototypeGraphique) e1;
		PrototypeGraphique eg2 = (PrototypeGraphique) e2;
		// Il ne faut pas attendre le refresh de l'affichage qui est plus long :
		if (eg1.refreshShape() == null || eg2.refreshShape() == null)
			return false;
		// On regarde déjà l'intersection des deux rectangles :

		// Les deux éléments doivent être visibles (bogue remonté par Wam) :
		String v1 = (String) eg1.retourneAttribut("visible").getValeur();
		String v2 = (String) eg2.retourneAttribut("visible").getValeur();
		if ("oui".equals(v1) && "oui".equals(v2)) {
			if (eg1.getGreffon() != null || eg2.getGreffon() != null) {
				if (eg1.getShape().getBounds2D().intersects(eg2.getShape().getBounds2D())) {
					// Pour les greffons !!!
					Shape s1 = eg1.getShape();
					Shape s2 = eg2.getShape();
					if (s1 == null || s2 == null)
						return false;
					Area commun = (Area) new Area(s1).clone();
					commun.intersect(new Area(s2));
					return !commun.isEmpty();
				} else
					return false;
			} else {
				if (eg1.getShape().getBounds2D().intersects(eg2.getShape().getBounds2D())) {
					// Si oui, on affine le teste !
					Area area1 = eg1.refreshArea();
					Area area2 = eg2.refreshArea();
					// area1 est en cache, il faut donc le cloner :
					Area commun = (Area) area1.clone();
					commun.intersect(area2);
					return !commun.isEmpty();
				} else
					return false;
			}
		} else
			return false;
	}

	private static boolean vide(Acteur a1) throws ErreurException {
		if (a1.getRole() == Role.NOMBRE)
			return new BigDecimal(0).equals(a1.getValeur());
		if (a1.getRole() == Role.TEXTE)
			return a1.estIlVide();
		if (a1.getRole() == Role.CASIER)
			return ((Casier) a1).estIlVide();
		throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_INVALIDE);
	}

	@SuppressWarnings({ "unchecked" })
	public static boolean equals(Acteur a1, Acteur a2) throws ErreurException {
		if (!(a1.getRole() == a2.getRole()))
			throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_MEME_ROLE);

		if (a1.getRole() == Role.NOMBRE) {
			BigDecimal i1 = (BigDecimal) a1.getValeur();
			BigDecimal i2 = (BigDecimal) a2.getValeur();
			return i1.compareTo(i2) == 0;
		} else if (a1.getRole() == Role.TEXTE) {
			String i1 = (String) a1.getValeur();
			String i2 = (String) a2.getValeur();
			return i1.equalsIgnoreCase(i2);
		} else if (a1.getRole() == Role.ESPECE) {
			Prototype e1 = (Prototype) a1;
			Prototype e2 = (Prototype) a2;
			if (e1.getType().equals(e2.getType())) {
				if (e1.retourAttributs().size() != e2.retourAttributs().size())
					return false;
				Map m = (e1).retourAttributsMap();
				Iterator<String> s = m.keySet().iterator();
				while (s.hasNext()) {
					String clef = s.next();
					Acteur a = e1.retourneAttribut(clef);
					Acteur e = e2.retourneAttribut(clef);
					if (e == null)
						return false;
					else if (!equals(a, e))
						return false;
				}
				return true;
			}
		} else if (a1.getRole() == Role.CASIER) {
			Casier e1 = (Casier) a1;
			Casier e2 = (Casier) a2;
			if (e1.roleContenant() == e2.roleContenant()) {
				List<Acteur> l1 = e1.extraireTout();
				List<Acteur> l2 = e2.extraireTout();
				if (l1.size() == l2.size()) {
					int i = 0;
					for (Acteur acteur : l2) {
						if (!equals(acteur, l1.get(i))) {
							return false;
						}
						i++;
					}
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_INVALIDE);
	}

	private static boolean contient(Acteur a1, Acteur a2) throws ErreurException {
		if (a2.getRole() == Role.NOMBRE && a1.getRole() == Role.CASIER && ((Casier) a1).roleContenant() == Role.NOMBRE) {
			Casier casier = (Casier) a1;
			return casier.recherche(a2) != 0;
		} else if (a2.getRole() == Role.TEXTE && a1.getRole() == Role.CASIER && ((Casier) a1).roleContenant() == Role.TEXTE) {
			Casier casier = (Casier) a1;
			return casier.recherche(a2) != 0;
		} else if (a2.getRole() == Role.TEXTE && a1.getRole() == Role.TEXTE) {
			return ((String) a1.getValeur()).toLowerCase().indexOf(((String) a2.getValeur()).toLowerCase()) != -1;
		} else
			throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_INVALIDE);
	}

	private ETAT lancerEtat(Job job, Processus processus) throws Exception {
		JobContext jobContext = (JobContext) job.getContext();

		Condition condition = jobContext.getLivre().getKernelStack().recupereDerniereCondition();

		if (condition == null)
			throw new ErreurException(Constantes.STRUCTURE_ETRANGE);

		condition.setEtat(ETAT_CONDITION.VRAI);

		if (processus == null) // Bogue syntaxique si la ligne contient seulement "sinon" 
			throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_INVALIDE);

		return ETAT.ETAT_SECONDAIRE;

	}

	private ETAT nePasLancerEtat(Job job, Processus processus) throws StopException, ErreurException {
		JobContext jobContext = (JobContext) job.getContext();

		Condition condition = jobContext.getLivre().getKernelStack().recupereDerniereCondition();

		if (condition == null)
			throw new ErreurException(Constantes.STRUCTURE_ETRANGE);

		condition.setEtat(ETAT_CONDITION.FAUX);

		if (processus == null) // Bogue syntaxique si la ligne contient seulement "sinon"
			throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_INVALIDE);

		if (processus.getAction() instanceof LireAction) {
			return ETAT.SAUTER_PARAGRAPHE;
		}

		return ETAT.PAS_DE_CHANGEMENT;

	}

}