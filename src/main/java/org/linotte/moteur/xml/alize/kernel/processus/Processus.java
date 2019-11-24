package org.linotte.moteur.xml.alize.kernel.processus;

import java.util.ArrayList;
import java.util.List;

import org.alize.kernel.AKJob;
import org.alize.kernel.AKProcessus;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.Trace;
import org.linotte.moteur.xml.analyse.ItemXML;

/**
 * 
 * Attention, un processus peut être partagé par plusieurs jobs
 * 
 * @author ronan
 *
 */
public class Processus extends AKProcessus {

	protected Action etat;

	protected String param;
	protected ItemXML[] valeurs;
	protected String[] annotations;

	private int position;

	protected boolean produitCartesien = false;

	protected ItemXML[][] matrice = null;

	// Souffleurs
	private List<Processus> souffleurs = null;
	
	// Processus secondaire ?
	private boolean secondaire = false;

	Processus() {

	}

	public Processus(Action petat, String pparam, Object pvaleurs, String[] pannontations, int pligne, boolean pcartesien) {
		etat = petat;
		param = pparam;
		if (pvaleurs instanceof ItemXML[][]) {
			produitCartesien = true;
			matrice = (ItemXML[][]) pvaleurs;
		} else {
			valeurs = (ItemXML[]) pvaleurs;
		}
		annotations = pannontations;
		position = pligne;
	}

	@Override
	public AKProcessus execute(AKJob job) throws Exception {
		if (produitCartesien) {
			for (ItemXML[] ligne : matrice) {
				if (Trace.active)
					Trace.getInstance().debug(position, etat, (Job) job, ligne, null);
				etat.analyse(param, (Job) job, ligne, annotations);
			}
		} else {
			if (Trace.active)
				Trace.getInstance().debug(position, etat, (Job) job, valeurs, null);
			etat.analyse(param, (Job) job, valeurs, annotations);
		}
		return getNextProcess();
	}

	public int getPosition() {
		return position;
	}

	protected void setPosition(int pos) {
		position = pos;
	}

	public ItemXML[] getValeurs() {
		return valeurs;
	}

	public Action getAction() {
		return etat;
	}

	@Override
	public String toString() {
		return etat.clef();
	}

	public boolean isProduitCartesien() {
		return produitCartesien;
	}

	public ItemXML[][] getMatrice() {
		return matrice;
	}

	public void addSouffleurs(List<Processus> psouffleurs) {
		souffleurs = new ArrayList<Processus>(psouffleurs);
	}

	public List<Processus> getSouffleurs() {
		return souffleurs;
	}

	public String[] getAnnotations() {
		return annotations;
	}

	public String getParam() {
		return param;
	}

	public void setProcessusSecondaire() {
		secondaire = true;
	}
	
	public boolean isProcessusSecondaire() {
		return secondaire;
	}


}