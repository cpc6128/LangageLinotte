package org.linotte.frame.listener;

import org.linotte.greffons.externe.Greffon.ListenerGreffons;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.xml.actions.AppelerAction;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;

public class Listener implements ListenerGreffons {

	private Processus processus;

	private String paragraphe;

	private Job job;

	private AppelerAction appeler;

	private Acteur[] acteurs;

	public Listener(Acteur acteur, String pparagraphe, Processus pprocessus, Job pjob) {
		processus = pprocessus;
		job = pjob;
		paragraphe = pparagraphe;
		this.appeler = ((RuntimeContext) job.getRuntimeContext()).getLinotte().getRegistreDesEtats().getActionAppeler();
		acteurs = new Acteur[] { acteur };
	}

	public void execute() {
		appeler.execution(job, acteurs, paragraphe, processus);
	}

}
