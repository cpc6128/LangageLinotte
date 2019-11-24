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

package org.linotte.moteur.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.linotte.moteur.xml.actions.*;
import org.linotte.moteur.xml.alize.kernel.Action;

public class RegistreDesActions {

	public RegistreDesActions() {
		initEtatsDuSysteme();
	}

	private Map<String, Action> map = null;

	private static List<String> erreurs = new ArrayList<String>();

	private TemporiserAction temporiser;
	private PauseAction pause;
	private AttendreSecondeAction attendreSeconde;
	private AttendreMilliSecondeAction attendreMilliSeconde;
	private AppelerAction etatAppeler;
	private ConditionAction etatCondition;
	private ArreterAction etatArreter;

	public void initEtatsDuSysteme() {

		if (map == null) {
			map = new HashMap<String, Action>();
			addAction(new LivreAction());
			addAction(new ActeurAction());
			addAction(new EspeceAction());
			addAction(new RoleAction());
			addAction(new ParagrapheAction());
			addAction(new AfficherAction());
			addAction(new DemanderAction());
			addAction(new CopierAction());
			addAction(new MesurerAction());
			addAction(new AjouterAction());
			addAction(new EffacerLeTableauAction());
			addAction(new QuestionnerAction());
			addAction(etatArreter = new ArreterAction());
			addAction(new PauseAction());
			addAction(new TemporiserAction());
			addAction(new AttendreSecondeAction());
			addAction(new AttendreMilliSecondeAction());
			addAction(new MultiplierAction());
			addAction(new SoustraireAction());
			addAction(new MelangerAction());
			addAction(new TrierAction());
			addAction(new ViderAction());
			addAction(new LireAction());
			addAction(new AllerAction());
			addAction(new FaireAction());
			addAction(new RevenirAction());
			addAction(new BoucleAction());
			addAction(etatCondition = new ConditionAction());
			addAction(new ConditionSinonAction());
			addAction(new DiviserAction());
			addAction(new InverserAction());
			addAction(new ExtraireAction());
			addAction(new InsererAction());
			addAction(new ChercherAction());
			addAction(new SonAction());
			addAction(new ConcatenerAction());
			addAction(new ValoirAction());
			addAction(new ProjeterAction());
			addAction(new DeplacerVersAction());
			addAction(new EffacerLaToileAction());
			addAction(new EffacerAction());
			addAction(new DeplacerDeAction());
			addAction(new DeplacerVersGaucheAction());
			addAction(new DeplacerVersDroiteAction());
			addAction(new DeplacerVersHautAction());
			addAction(new DeplacerVersBasAction());
			addAction(new ViderTouchesAction());
			addAction(new PhotographierAction());
			addAction(new StimulerAction());
			addAction(new ConvertirAction());
			addAction(new FermerAction());
			addAction(new ExplorerAction());
			addAction(new ParcourirAction());
			addAction(new ImportLivreAction());
			addAction(new ParcourirDeAction());
			addAction(new OterAction());
			addAction(new ModifierAction());
			addAction(new RafraichirAction());
			addAction(new AvancerDeAction());
			addAction(new TournerADroiteAction());
			addAction(new TournerAGaucheAction());
			addAction(new ReculerDeAction());
			addAction(new AppelerAction());
			addAction(new ObserverAction());
			addAction(new OuvrirAction());
			addAction(new FermerTubeAction());
			addAction(new ChargerAction());
			addAction(new DechargerAction());
			addAction(new ConfigurerAction());
			addAction(new EvaluerAction());
			addAction(new SouffleurAction());
			addAction(new FusionnerAction());
			addAction(new FaireReagirAction());
			addAction(new RetournerAction());
			addAction(new EssayerAction());
			addAction(new PeindreAction());
			addAction(new PlusFaireReagirAction());
			addAction(new ImportationAction());
			addAction(new AnnihilerAction());
			addAction(new PiquerAction());
			addAction(new AttacherAction());
			addAction(new EvoquerAction());
			addAction(new DecrementerAction());
			addAction(new IncrementerAction());
			addAction(new TildeAction());
			addAction(new TestUnitaireInAction());
			addAction(new TestUnitaireOutAction());
			addAction(new TestUnitaireAction());
			addAction(new CompatibiliteAction());
			addAction(new StopperAction());
			addAction(new MontrerAction());
			addAction(new ProposerAction());
			addAction(new StructureGlobaleAction());
			addAction(new StructureDebutAction());
			addAction(new RechargerAction());
			addAction(new GreffonsAction());
			addAction(new InterrompreAction());
			addAction(new AjouterSimpleAction());
			addAction(new SoustraireSimpleAction());
		}
	}

	public void addAction(Action etat) {
		if (etat instanceof AttendreSecondeAction) {
			attendreSeconde = (AttendreSecondeAction) etat;
		} else if (etat instanceof AttendreMilliSecondeAction) {
			attendreMilliSeconde = (AttendreMilliSecondeAction) etat;
		} else if (etat instanceof TemporiserAction) {
			temporiser = (TemporiserAction) etat;
		} else if (etat instanceof PauseAction) {
			pause = (PauseAction) etat;
		} else if (etat instanceof AppelerAction) {
			etatAppeler = (AppelerAction) etat;
		}
		map.put(etat.clef(), etat);
	}

	public Action retourneAction(String clef) {
		return map.get(clef);
	}

	public static void ajouterErreur(String message) {
		erreurs.add(message);
	}

	public static Iterator<String> retourneErreurs() {
		Iterator<String> i = erreurs.iterator();
		return i;
	}
	
	public static void effaceErreurs() {
		erreurs.clear();
	}


	public TemporiserAction getTemporiser() {
		return temporiser;
	}

	public PauseAction getPause() {
		return pause;
	}

	public AttendreSecondeAction getAttendreSeconde() {
		return attendreSeconde;
	}

	public AttendreMilliSecondeAction getAttendreMilliSeconde() {
		return attendreMilliSeconde;
	}

	public AppelerAction getActionAppeler() {
		return etatAppeler;
	}

	public ConditionAction getActionCondition() {
		return etatCondition;
	}

	public ArreterAction getActionArreter() {
		return etatArreter;
	}

}
