package org.linotte.moteur.xml.alize.kernel;

import org.alize.kernel.AKJob;
import org.alize.kernel.AKJobContext;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Livre;

public class JobContext extends AKJobContext {

	private Livre livre = new Livre();

	private boolean creationespece = false;

	private boolean chargementImport = false;

	private Acteur[] doublure = null;

	// Linotte 2.1
	private Acteur prototype = null;

	public Acteur[] getDoublure() {
		return doublure;
	}

	public void setDoublure(Acteur[] doublure) {
		this.doublure = doublure;
	}

	public Acteur getDoublure(int i) {
		if (doublure == null)
			return null;
		if (i >= doublure.length)
			return null;
		else
			return doublure[i];
	}

	public Livre getLivre() {
		return livre;
	}

	public void setLivre(Livre livre) {
		this.livre = livre;
	}

	public boolean isCreationespece() {
		return creationespece;
	}

	public void setCreationespece(boolean creationespece) {
		this.creationespece = creationespece;
	}

	public boolean isChargementImport() {
		return chargementImport;
	}

	public void setChargementImport(boolean chargementImport) {
		this.chargementImport = chargementImport;
	}

	@Override
	public void closeJob(AKJob akJob) {
		Job job = (Job) akJob;
		if (job.isSon()) {
			job.getPere().getSons().remove(akJob);
		}
	}

	@Override
	public void initializeJob(AKJob akRuntime) {

	}

	@Override
	public Object clone() {
		JobContext context = new JobContext();
		context.livre = (Livre) getLivre().clone();
		return context;
	}

	public Acteur getPrototype() {
		return prototype;
	}

	public void setPrototype(Acteur prototype) {
		this.prototype = prototype;
	}

	public void nettoyerMemoire() {
		if (livre != null)
			livre.nettoyerMemoire();
		doublure = null;
	}
}
