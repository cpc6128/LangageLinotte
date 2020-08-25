package org.linotte.moteur.xml.alize.kernel.processus;

import org.alize.kernel.AKJob;
import org.alize.kernel.AKProcessus;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Action.ETAT;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.Trace;
import org.linotte.moteur.xml.analyse.ItemXML;
import org.linotte.moteur.xml.outils.FastEnumMap;

import java.util.Map;

import static org.linotte.moteur.xml.alize.kernel.Action.ETAT.*;

/**
 * 
 * Attention, un processus peut être partagé par plusieurs jobs
 * 
 * @author ronan
 * 
 */
public class ProcessusDispatcher extends Processus {

	public static Map<ETAT, Switch> directions = new FastEnumMap<ETAT, Switch>(ETAT.class);

	public static interface Switch {
		AKProcessus execute(ProcessusDispatcher pd) throws ErreurException;
	}

	private static class SwitchSecondaire implements Switch {

		@Override
		public AKProcessus execute(ProcessusDispatcher pd) {
			return pd.processusSecondaire;
		}

	}

	private static class SwitchSousParagraphe implements Switch {

		@Override
		public AKProcessus execute(ProcessusDispatcher pd) {
			return pd.lire.getNextProcess();
		}

	}

	private static class SwitchSauterParagraphe implements Switch {

		@Override
		public AKProcessus execute(ProcessusDispatcher pd) throws ErreurException {
			if (((ProcessusDispatcher) pd.processusSecondaire).getFermer() != null)
				return ((ProcessusDispatcher) pd.processusSecondaire).getFermer().getNextProcess();
			else
				throw new ErreurException(Constantes.SYNTAXE_SOUS_PARAGRAPHE);
		}

	}

	private static class SwitchFinImportation implements Switch {

		@Override
		public AKProcessus execute(ProcessusDispatcher pd) {
			return null;
		}

	}

	private static class SwitchParcourir implements Switch {

		@Override
		public AKProcessus execute(ProcessusDispatcher pd) {
			return pd.parcourir;
		}

	}

	private static class SwitchParDefaut implements Switch {

		@Override
		public AKProcessus execute(ProcessusDispatcher pd) {
			return pd.getNextProcess();
		}

	}

	static {
		directions.put(ETAT_SECONDAIRE, new SwitchSecondaire());
		directions.put(SOUS_PARAGRAPHE, new SwitchSousParagraphe());
		directions.put(SAUTER_PARAGRAPHE, new SwitchSauterParagraphe());
		directions.put(FIN_IMPORTATION, new SwitchFinImportation());
		directions.put(PARCOURIR, new SwitchParcourir());
		directions.put(PAS_DE_CHANGEMENT, new SwitchParDefaut());
		directions.put(REVENIR, new SwitchParDefaut());
	}

	protected Processus processusSecondaire;
	private Processus processusPrimaire;

	// Pour les sous-paragraphes :
	private Processus lire, fermer;
	// Pour le verbe parcourir :
	private Processus parcourir;
	// Pour le verbe parcourir :
	private Processus appeler;
	// Pour le verbe attacher :
	private Processus attacher;
	private String paragraphe;

	// Classe Enum

	public ProcessusDispatcher(Action petat, String pparam, Object pvaleurs, String[] pannontations, int pligne, boolean pcartesien) {
		super(petat, pparam, pvaleurs, pannontations, pligne, pcartesien);
	}

	@Override
	public AKProcessus execute(AKJob job) throws Exception {
		ETAT retour = null;
		if (produitCartesien) {
			for (ItemXML[] ligne : matrice) {
				retour = etat.analyse(param, (Job) job, ligne, annotations);
				if (Trace.active)
					Trace.getInstance().debug(getPosition(), etat, (Job) job, valeurs, retour.toString());
			}
		} else {
			retour = etat.analyse(param, (Job) job, valeurs, annotations);
			if (Trace.active)
				Trace.getInstance().debug(getPosition(), etat, (Job) job, valeurs, retour.toString());
		}
		return directions.get(retour).execute(this);
	}

	public Processus getProcessusSecondaire() {
		return processusSecondaire;
	}

	public void setProcessusSecondaire(Processus processusSecondaire) {
		this.processusSecondaire = processusSecondaire;
		this.processusSecondaire.setProcessusSecondaire();
	}

	public Processus getProcessusPrimaire() {
		return processusPrimaire;
	}

	public void setProcessusPrimaire(Processus processusPrimaire) {
		this.processusPrimaire = processusPrimaire;
	}

	public Processus getLire() {
		return lire;
	}

	public void setLire(Processus lire) {
		this.lire = lire;
	}

	public Processus getFermer() {
		return fermer;
	}

	public void setFermer(Processus fermer) {
		this.fermer = fermer;
	}

	public Processus getParcourir() {
		return parcourir;
	}

	public void setParcourir(Processus parcourir) {
		this.parcourir = parcourir;
	}

	public Processus getAppeler() {
		return appeler;
	}

	public void setAppeler(Processus appeler) {
		this.appeler = appeler;
	}

	public Processus getAttacher() {
		return attacher;
	}

	public void setAttacher(Processus attacher) {
		this.attacher = attacher;
	}

	public void setParagraphe(String paragraphe) {
		this.paragraphe = paragraphe;
	}

	public String getParagraphe() {
		return paragraphe;
	}

}