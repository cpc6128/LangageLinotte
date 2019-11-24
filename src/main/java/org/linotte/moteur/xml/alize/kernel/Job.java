package org.linotte.moteur.xml.alize.kernel;

import static org.linotte.moteur.xml.alize.kernel.Action.ETAT.PAS_DE_CHANGEMENT;
import static org.linotte.moteur.xml.alize.kernel.Action.ETAT.SAUTER_PARAGRAPHE;

import java.math.BigDecimal;

import org.alize.kernel.AKContextI;
import org.alize.kernel.AKException;
import org.alize.kernel.AKJob;
import org.alize.kernel.AKJobContext;
import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Livre;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.FonctionDoublureException;
import org.linotte.moteur.exception.FonctionException;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.exception.LinotteException;
import org.linotte.moteur.exception.MathematiquesException;
import org.linotte.moteur.exception.Messages;
import org.linotte.moteur.exception.PositionException;
import org.linotte.moteur.exception.RetournerException;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.Version;
import org.linotte.moteur.xml.actions.ConditionAction;
import org.linotte.moteur.xml.actions.ConditionSinonAction;
import org.linotte.moteur.xml.actions.LireAction;
import org.linotte.moteur.xml.alize.kernel.i.AKDebugger;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.kernel.processus.ProcessusDispatcher;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;
import org.linotte.moteur.xml.appels.Appel;
import org.linotte.moteur.xml.appels.CalqueParagraphe;
import org.linotte.moteur.xml.appels.Condition;
import org.linotte.moteur.xml.appels.Condition.ETAT_CONDITION;
import org.linotte.moteur.xml.appels.Ring;

public class Job extends AKJob {

	private Processus currentProcessus;

	private Job pere;

	public Job(AKJobContext pcontext) {
		super(pcontext);
	}

	private boolean debogueur = false;

	private Exception erreurAPropager;

	private static Long idTechniqueStatic = 0L;

	/**
	 * Id unique par instance, utilisé par le @Debogueur
	 */
	public Long idTechnique = 0L;

	{
		synchronized (idTechniqueStatic) {
			idTechnique = idTechniqueStatic++;
		}
	}

	// Indique que ce job est execute dans un thread (verbe appeler)
	private boolean parallelise = false;

	@Override
	public void execute(AKContextI context) throws Exception {
		int positionRetourFonction = -1;
		try {
			currentProcessus = (Processus) getFirstProcessus();
			boolean boucle = true;
			while (boucle)
				try {
					boucle = false;
					while ((currentProcessus = (Processus) currentProcessus.execute(this)) != null) {

						verifierSiTropDeBoucles();

						if (erreurAPropager != null) {
							throw erreurAPropager;
						}
						// Souffleur ?
						if (currentProcessus.getSouffleurs() != null) {
							Processus temp = currentProcessus;
							KernelStack kernelStack = ((JobContext) getContext()).getLivre().getKernelStack();
							for (Processus souffleur : currentProcessus.getSouffleurs()) {
								currentProcessus = souffleur;
								kernelStack.ajouterAppel(new Condition());
								while (currentProcessus != null)
									currentProcessus = (Processus) currentProcessus.execute(this);
								kernelStack.fermeCondition();
							}
							currentProcessus = temp;
						}

						if (!isRunning() && erreurAPropager == null) {
							throw new StopException();
						}
						// On passe les états sinon :
						passerLesConditionsSinon();

						afficheDebogueur();

						// Pour partager le temps de traitement entre les jobs
						if (isParallelise())
							Thread.yield();

						if (currentProcessus == null) {
							// Possible si on passe dans la méthode passerLesConditionsSinon
							break;
						}

					}

					// Mode debogue même à la fin du job
					afficheDebogueur();

				} catch (FonctionDoublureException e) {
					if (!isDead()) {
						fillStack(e);

						// Affiner l'affichage de l'origine de l'erreur
						JobContext jobContext = (JobContext) getContext();
						if (jobContext.getLivre().getKernelStack().recupereDerniereFonction() != null
								&& jobContext.getLivre().getKernelStack().recupereDerniereFonction().getProcessusRetour() != null)
							positionRetourFonction = jobContext.getLivre().getKernelStack().recupereDerniereFonction().getProcessusRetour().getPosition();

						/**
						 * s'il n'est pas mort, on propage l'erreur, sinon, on
						 * ne fait rien (bug sur les fonctions).
						 */
						boucle = handlerError(e);
					}
				} catch (ErreurException e) {
					if (!isDead()) {
						fillStack(e);
						/**
						 * s'il n'est pas mort, on propage l'erreur, sinon, on
						 * ne fait rien (bug sur les fonctions).
						 */
						boucle = handlerError(e);
					}
				} catch (LectureException e) {
					fillStack(e);
					// Cas remonté par Pat dans l'import d'un livre :
					// http://langagelinotte.free.fr/punbb/viewtopic.php?id=273
					if (e.getCause() != null) {
						boucle = handlerError(e.getCause());
					} else
						throw e;
				}

		} catch (FonctionDoublureException e) {
			if (positionRetourFonction == -1)
				positionRetourFonction = currentProcessus.getPosition();
			throw new LectureException(e, positionRetourFonction);
		} catch (RetournerException e) {
			throw e;
		} catch (StopException e) {
			throw e;
		} catch (LectureException e) {
			throw e;
		} catch (StackOverflowError e) {
			throw new AKException("trop de boucles ou d'appels imbriqués ! Le livre doit s'arrêter...");
		} catch (FonctionException e) {
			throw new LectureException(e.getCauseLectureException().getException(), e.getCauseLectureException().getPosition());
		} catch (LinotteException e) {
			throw new LectureException(e, currentProcessus.getPosition());
		} catch (IndexOutOfBoundsException e) {
			if (Version.isBeta())
				e.printStackTrace();
			throw new LectureException(new PositionException(), currentProcessus.getPosition());
		} catch (ArithmeticException e) {
			throw new LectureException(new MathematiquesException(), currentProcessus.getPosition());
		} finally {
			if (erreurAPropager != null) {
				throw erreurAPropager;
			}
		}
	}

	public void verifierSiTropDeBoucles() throws ErreurException {

		RuntimeContext runtimeContext = (RuntimeContext) getRuntimeContext();
		runtimeContext.compteurExecution++;
		if (runtimeContext.compteurExecution > 50000 && runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
			throw new ErreurException(Constantes.ERREUR_VIDE, "traitement arrêté car trop long.");
		}

	}

	/**
	 * - Cette méthode va optimiser le passage des conditions "sinon"
	 * - Et elle va vérifier que les "sinon" sont bien exécutées à la chaîne
	 * - Optimisation
	 * @throws ErreurException
	 */
	private void passerLesConditionsSinon() throws ErreurException {
		JobContext jobContext = (JobContext) getContext();
		if (currentProcessus.getAction() instanceof ConditionSinonAction) {
			Condition condition = jobContext.getLivre().getKernelStack().recupereDerniereCondition();
			if (condition != null && condition.getEtat() == ETAT_CONDITION.VRAI) {
				// Nous sautons directement les conditions "sinon" qui ne doivent pas être exétutées
				do {
					if (((ProcessusDispatcher) currentProcessus).getProcessusSecondaire() == null) {
						// bogue remonté mais impossible à reproduire :
						throw new ErreurException(Constantes.SYNTAXE_CONDITIONS_INVALIDE);
					}
					if (((ProcessusDispatcher) currentProcessus).getProcessusSecondaire().getAction() instanceof LireAction) {
						currentProcessus = (Processus) ProcessusDispatcher.directions.get(SAUTER_PARAGRAPHE).execute((ProcessusDispatcher) currentProcessus);
					} else {
						currentProcessus = (Processus) ProcessusDispatcher.directions.get(PAS_DE_CHANGEMENT).execute((ProcessusDispatcher) currentProcessus);
					}
				} while (currentProcessus != null && currentProcessus.getAction() instanceof ConditionSinonAction);
				// On ferme la condition qui n'est plus utilisée :
				jobContext.getLivre().getKernelStack().fermeCondition();
			}
		} else {
			// Vérification si la structure des conditions est correcte. (à remonter dans l'analyse syntaxique !)
			if (!currentProcessus.isProcessusSecondaire() && !jobContext.getLivre().getKernelStack().isEmpty()) {
				Appel appel = jobContext.getLivre().getKernelStack().peek();
				if (appel instanceof Condition && !(currentProcessus.getAction() instanceof ConditionAction)) {
					// Nous sortons d'une structure de conditions :
					jobContext.getLivre().getKernelStack().pop();
				}
			}
		}
	}

	/**
	 * Affiche le debogueur (l'inspecteur). En mode pas à pas depuis le bouton,
	 * le delais est > 0 sinon à -1 si on a utilisé le verbe deboguer
	 */
	public void afficheDebogueur() {
		if (debogueur) {
			afficheDeboggueur(-1);
		} else if (((RuntimeContext) getRuntimeContext()).delayPasAPas > 0) {
			afficheDeboggueur(((RuntimeContext) getRuntimeContext()).delayPasAPas);
		}
	}

	/**
	 * Affiche l'IHM de debogueur
	 * 
	 * @param delay
	 */
	private void afficheDeboggueur(int delay) {
		AKDebugger debogueur = ((RuntimeContext) getRuntimeContext()).debugger;
		if (debogueur != null)
			debogueur.showDebugger(delay, this, getCurrentProcessus());
	}

	private void fillStack(LinotteException e) {
		Livre livre = ((JobContext) getContext()).getLivre();
		if (livre != null) {
			KernelStack kernelStack = livre.getKernelStack();
			e.setKernelStack((KernelStack) kernelStack.produceKernel());
		}
	}

	private boolean handlerError(LinotteException e) throws LinotteException {
		// Cas standard
		Livre livre = ((JobContext) getContext()).getLivre();
		Ring codeProtege = livre.getKernelStack().fermeCodeProtege(livre);
		if (codeProtege == null)
			throw e;
		CalqueParagraphe calqueParagraphe = livre.getKernelStack().recupereDernierCalqueParagraphe();
		Acteur a1 = new Acteur(null, Chaine.produire("numéro_erreur"), Role.NOMBRE, new BigDecimal(e.getErreur()), null);
		if (calqueParagraphe == null)
			livre.addActeur(a1);
		else
			calqueParagraphe.addActeurLocal(a1);
		String message = e.getToken();
		if (message == null)
			message = Messages.retourneErreur(String.valueOf(e.getErreur()));
		Acteur a2 = new Acteur(null, Chaine.produire("message_erreur"), Role.TEXTE, message, null);
		if (calqueParagraphe == null)
			livre.addActeur(a2);
		else
			calqueParagraphe.addActeurLocal(a2);
		currentProcessus = codeProtege.getProcessus();
		livre.getKernelStack().recupereDerniereCondition().setEtat(ETAT_CONDITION.FAUX);
		return currentProcessus != null;
	}

	public Processus getCurrentProcessus() {
		return currentProcessus;
	}

	@Override
	public Object clone(boolean strict) {
		Job job = new Job((AKJobContext) ((JobContext) getContext()).clone());
		if (!strict) {
			// bug avec le webonotte :
			job.pere = this;
			getSons().add(job);
		}
		job.setFirstProcessus(getFirstProcessus());
		return job;
	}

	@Override
	public void stop() {
		setRunning(false);
		super.stop();
		if (isSon() && !pere.isDead()) {
			pere.stop();
		}
	}

	public boolean isSon() {
		return pere != null;
	}

	public void stopWithException(Exception e) {
		erreurAPropager = e;
		if (isSon()) {
			pere.stopWithException(e);
		}
		setRunning(false);
	}

	public void setDebogueur(boolean debogueur) {
		this.debogueur = debogueur;
	}

	public Job getPere() {
		return pere;
	}

	@Override
	public String toString() {
		Livre livre = ((JobContext) getContext()).getLivre();
		if (livre != null) {
			String s = livre.getNom();
			if (livre.getParagraphe() != null) {
				s += " § " + livre.getParagraphe().toString();
			}
			return s;
		} else {
			return "sans nom";
		}
	}

	public String toShortString() {
		Livre livre = ((JobContext) getContext()).getLivre();
		if (livre != null) {
			String s = "";
			if (livre.getParagraphe() != null) {
				s += livre.getParagraphe().toString();
			}
			return s;
		} else {
			return "";
		}
	}

	public boolean isParallelise() {
		return parallelise;
	}

	public void setParallelise() {
		parallelise = true;
	}

}