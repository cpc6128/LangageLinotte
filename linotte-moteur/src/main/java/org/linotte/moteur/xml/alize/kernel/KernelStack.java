package org.linotte.moteur.xml.alize.kernel;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Livre;
import org.linotte.moteur.outils.ArrayIterator;
import org.linotte.moteur.outils.Chaine;
import org.linotte.moteur.xml.appels.*;
import org.linotte.moteur.xml.appels.Appel.APPEL;

import java.util.*;

public final class KernelStack {

	private Appel[] contents;

	private KernelStack father;

	private int size;

	private int initialSize;

	/**
	 * Creates a new empty stack.
	 */
	public KernelStack() {
		initialSize = 20;
		contents = new Appel[initialSize];
	}

	/**
	 * Returns <code>true</code> if the stack is empty, and <code>false</code>
	 * otherwise.
	 * 
	 * @return A boolean.
	 */
	boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the number of elements in the stack.
	 * 
	 * @return The element count.
	 */
	public int size() {
		return size;
	}

	/**
	 * Pushes an object onto the stack.
	 * 
	 * @param o
	 *            the object.
	 */
	public void push(Appel o) {
		final int oldSize = size;
		size += 1;
		if (contents.length == size) {
			// grow ..
			final Appel[] newContents = new Appel[size + initialSize];
			System.arraycopy(contents, 0, newContents, 0, size);
			contents = newContents;
		}
		contents[oldSize] = o;
	}

	/**
	 * Returns the object at the top of the stack without removing it.
	 * 
	 * @return The object at the top of the stack.
	 */
	public Appel peek() {
		return contents[size - 1];
	}

	/**
	 * Removes and returns the object from the top of the stack.
	 * 
	 * @return The object.
	 */
	public Appel pop() {
		if (size == 0) {
			throw new EmptyStackException();
		}
		size -= 1;
		final Appel retval = contents[size];
		contents[size] = null;
		return retval;
	}

	/**
	 * Retourne un fils de la pile.
	 * 
	 * @return A clone.
	 */
	public Object produceKernel() {
		KernelStack stack = new KernelStack();
		if (contents != null) {
			stack.father = this;
		}
		return stack;
	}

	/**
	 * Clears the stack.
	 */
	public void clear() {
		size = 0;
		Arrays.fill(contents, null);
	}

	/**
	 * Returns the item at the specified slot in the stack.
	 * 
	 * @param index
	 *            the index.
	 * 
	 * @return The item.
	 */
	public Appel get(final int index) {
		return contents[index];
	}

	public Iterator<Appel> iterator() {
		Appel[] shrink = new Appel[size];
		System.arraycopy(contents, 0, shrink, 0, size);
		return new ArrayIterator<Appel>(shrink);
	}

	public void ajouterAppel(Appel o) {
		push(o);
	}

	public Appel retirerDernierAppel() {
		return pop();
	}

	public Appel regarderDernierAppel() {
		return peek();
	}

	public Condition fermeCondition() {
		return (Condition) close(APPEL.CONDITION);
	}

	public Boucle fermeBoucle() {
		return (Boucle) close(APPEL.BOUCLE);
	}

	public Fonction fermeFonction() {
		return (Fonction) close(APPEL.FONCTION);
	}

	public SousParagraphe fermeSousParagraphe() {
		return (SousParagraphe) close(APPEL.SOUSPARAGRAPHE);
	}

	public CalqueParagraphe fermeCalqueParagraphe() {
		return (CalqueParagraphe) close(APPEL.CALQUEPARAGRAPHE);
	}

	public Condition recupereDerniereCondition() {
		return (Condition) find(APPEL.CONDITION);
	}

	public Boucle recupereDerniereBoucle() {
		return (Boucle) find(APPEL.BOUCLE);
	}

	public List<Boucle> recupereToutesLesBoucles() {
		List<Boucle> list = new ArrayList<Boucle>();
		Appel retval;
		for (int s = size; s > 0;) {
			s -= 1;
			retval = contents[s];
			if (retval.getType() == APPEL.BOUCLE)
				list.add((Boucle) retval);
		}
		return list;
	}

	public Fonction recupereDerniereFonction() {
		return (Fonction) find(APPEL.FONCTION);
	}

	public SousParagraphe recupereDernierSousParagraphe() {
		return (SousParagraphe) find(APPEL.SOUSPARAGRAPHE);
	}

	public CalqueParagraphe recupereDernierCalqueParagraphe() {
		return (CalqueParagraphe) find(APPEL.CALQUEPARAGRAPHE);
	}

	@SuppressWarnings("incomplete-switch")
	public Acteur rechercheActeurDansSousParagraphe(Chaine n) {
		Appel appel;
		for (int s = size; s > 0;) {
			appel = contents[--s];
			switch (appel.getType()) {
			case FONCTION:
				return null;
			case CALQUEPARAGRAPHE:
				return null;
			case SOUSPARAGRAPHE:
				SousParagraphe sousParagraphe = (SousParagraphe) appel;
				Acteur acteur = sousParagraphe.getActeurLocal(n);
				if (acteur != null)
					return acteur;
				break;
			}
		}
		return null;
	}

	/**
	 * Recherche un acteur visible dans le contexte d'exécution
	 * 
	 * @param nomActeur
	 * @return
	 */
	@SuppressWarnings("incomplete-switch")
	public Acteur rechercheActeur(Chaine nomActeur) {
		Appel appel;
		Acteur acteur;
		for (int s = size; s > 0;) {
			appel = contents[--s];
			switch (appel.getType()) {
			case CALQUEPARAGRAPHE:
				CalqueParagraphe paragraphe = (CalqueParagraphe) appel;
				acteur = paragraphe.getActeurLocal(nomActeur);
				if (acteur != null)
					return acteur;
				break;
			case SOUSPARAGRAPHE:
				SousParagraphe sousParagraphe = (SousParagraphe) appel;
				acteur = sousParagraphe.getActeurLocal(nomActeur);
				if (acteur != null)
					return acteur;
				break;
			case FONCTION:
				// http://langagelinotte.free.fr/forum/showthread.php?tid=996&pid=6838#pid6838
				return null;
			}
		}
		return null;
	}

	/**
	 * Cette méthode permet de remplacer un acteur dans la mémoire.
	 * Utilisez pour la boucle FOR 3.14
	 * @param acteur
	 * @param nomActeur
	 * @return
	 */
	public boolean remplacerActeur(final Acteur acteur, Chaine nomActeur) {
		Appel appel;
		for (int s = size; s > 0;) {
			appel = contents[--s];
			switch (appel.getType()) {
				case CALQUEPARAGRAPHE:
					CalqueParagraphe paragraphe = (CalqueParagraphe) appel;
					if (paragraphe.getActeurLocal(nomActeur) != null) {
						acteur.setNom(nomActeur);
						paragraphe.addActeurLocal(acteur);
						return true;
					}
					break;
				case SOUSPARAGRAPHE:
					SousParagraphe sousParagraphe = (SousParagraphe) appel;
					if (sousParagraphe.getActeurLocal(nomActeur) != null) {
						acteur.setNom(nomActeur);
						sousParagraphe.addActeurLocal(acteur);
						return true;
					}
					break;
				case FONCTION:
					// http://langagelinotte.free.fr/forum/showthread.php?tid=996&pid=6838#pid6838
					return false;
			}
		}
		return false;
	}

	/**
	 * @param degre quelle degré de parenté
	 * @return
	 */
	public Acteur rechercheJokerDansBoucle(int degre ) {
		if (size() != 0) {
			// On recherche la dernière boucle :
			Appel a = null;
			for (int i = size() - 1; i > -1; i--) {
				a = get(i);
				if (a instanceof Boucle && !(a instanceof TantQue)) {
					if (degre == 0)
						return ((Boucle) a).retourJoker();
					else
						degre--;
				}
			}
		}
		return null;
	}

	@SuppressWarnings("incomplete-switch")
	public Ring fermeCodeProtege(Livre livre) {
		Appel retval;
		for (; size > 0;) {
			retval = contents[--size];
			contents[size] = null;
			switch (retval.getType()) {
			case FONCTION:
				// Bogue remonté par Pat :
				// Ajout : cas des actions hors fonction !
				if (((Fonction) retval).getParagraphe() != null)
					livre.setParagraphe(((Fonction) retval).getParagraphe());
				break;
			case RING:
				return (Ring) retval;
			}
		}
		return null;
	}

	private Appel close(APPEL c) {
		Appel retval;
		for (; size > 0;) {
			retval = contents[--size];
			contents[size] = null;
			if (retval.getType() == c)
				return retval;
		}
		return null;
	}

	private Appel find(APPEL c) {
		Appel retval;
		for (int s = size; s > 0;) {
			retval = contents[--s];
			if (retval.getType() == c)
				return retval;
		}
		return null;
	}

	/**
	 * construction de la pile d'appels pour le debogueur
	 * 
	 * @param visible
	 *            force à rendre invisible mes fonctions
	 * @return
	 */
	public List<LMap> debogage(boolean visible) {
		Appel appel;
		List<LMap> tousLesActeurs = new ArrayList<LMap>();
		// On boucle sur contents et contents_father

		// boolean visible = true;
		for (int s = size; s > 0;) {
			appel = contents[--s];
			if (appel.getType() == APPEL.CALQUEPARAGRAPHE) {
				CalqueParagraphe paragraphe = (CalqueParagraphe) appel;
				tousLesActeurs.add(0, (visible) ? new Visible(paragraphe, paragraphe.debogage()) : new NotVisible(paragraphe, paragraphe.debogage()));
			} else if (appel.getType() == APPEL.SOUSPARAGRAPHE) {
				SousParagraphe sousParagraphe = (SousParagraphe) appel;
				tousLesActeurs.add(0,
						(visible) ? new Visible(sousParagraphe, sousParagraphe.debogage()) : new NotVisible(sousParagraphe, sousParagraphe.debogage()));
			} else if (appel.getType() == APPEL.BOUCLE && !(appel instanceof TantQue)) {
				Boucle boucle = (Boucle) appel;
				HashMap<Chaine, Acteur> debogage = boucle.debogage();
				if (debogage != null)
					tousLesActeurs.add(0, (visible) ? new Visible(boucle, debogage) : new NotVisible(boucle, debogage));
			} else if (appel.getType() == APPEL.FONCTION) {
				visible = false;
			}
		}
		if (father != null) {
			tousLesActeurs.addAll(0, father.debogage(false));
		}
		return tousLesActeurs;
	}

	@SuppressWarnings("serial")
	public static abstract class LMap extends HashMap<Chaine, Acteur> {

		public Appel appel;

		public LMap(HashMap<Chaine, Acteur> hashMap) {
			super(hashMap);
		}

	}

	@SuppressWarnings("serial")
	public static class Visible extends LMap {

		public Visible(Appel appel, HashMap<Chaine, Acteur> copie) {
			super(copie == null ? new HashMap<Chaine, Acteur>() : copie);
			this.appel = appel;
		}

	}

	@SuppressWarnings("serial")
	public static class NotVisible extends LMap {

		public NotVisible(Appel appel, HashMap<Chaine, Acteur> copie) {
			super(copie == null ? new HashMap<Chaine, Acteur>() : copie);
			this.appel = appel;
		}

	}

}