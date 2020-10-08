package org.linotte.moteur.xml.alize.parseur;

import org.linotte.frame.cahier.sommaire.JPanelSommaire;
import org.linotte.frame.coloration.StyleBuffer;
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.alize.parseur.a.Lexer;
import org.linotte.moteur.xml.outils.FastStack;
import util.SortedList;

import java.util.*;

/**
 * Cette classe contient tous les objets utilisés pour parser un fichier
 * 
 */
public class ParserContext {

	public enum MODE {
		FORMATAGE, COLORATION, GENERATION_RUNTIME
	};

	/**
	 * Bean Parseur :
	 */
	public MODE mode;

	/**
	 * Pour le parseur :
	 */
	public FastStack groupes = new FastStack();
	public FastStack phrase = new FastStack();
	public FastStack valeurs = new FastStack();
	public FastStack annotations = new FastStack();
	public FastStack etats = new FastStack();
	public Lexer lexer = null;
	public Stack<Integer> constructionPileSousParagraphes = new Stack<Integer>();
	public Stack<Processus> constructionPileSousParagraphesProcessus = new Stack<Processus>();
	// Pour optimiser la prelecture
	public HashMap<Integer, Set<String>> mapDiscriminants = new HashMap<Integer, Set<String>>();
	public ParserEnvironnement environnement = new ParserEnvironnement();
	// Pour la construction de l'annuaire des paragraphes :
	public String dernierParagraphe = null;
	public String methodeFonctionnellePrototype = null;
	// Pour y mettre le premier paragraphe non fonction
	public Processus processusNonFonctionPrototype = null;

	/**
	 * Gestion des états pour le moteur d'exécution :
	 */
	public boolean premier_paragraphe = true;
	public Job jobRacine;
	public Linotte linotte;
	public Processus lastProcessus = null;

	/**
	 * Gestion des paragraphes :
	 */
	public List<Processus> awares = new ArrayList<Processus>();
	// Livre court (avec pied-de-mouche)
	public boolean piedDeMouche = false;

	/**
	 * Gestion de la couleur dans l'Atelier :
	 */
	public int stylePosition = 0;
	public FastStack styles = new FastStack();
	public FastStack styles_formules = new FastStack();
	public StyleBuffer styleBuffer = null;

	/**
	 * Pour retenir l'erreur la plus loin sur une ligne :
	 */
	public SyntaxeException derniere_erreur = null;
    private List<String> dernierePhrase = new ArrayList<String>();
    // premier caractère d'une ligne
    public Integer lastPositionLigne = 0;

	/**
	 * Pour la gestion du formatage :
	 */
	public int valeur = 0;
	public boolean saute = false;
	public StringBuilder bufferFormatage = new StringBuilder();
	public int nbParagraphes = 0;

	/**
	 * Affichage du sommaire :
	 */
	public JPanelSommaire sommaire = null;

	/**
	 * Traitement particulier pour le webonotte
	 */
	public boolean webonotte = false;

	/**
	 * Complétion et Atelier intelligent:
	 */
	public Set<String> motsLivre = new HashSet<String>();
	public Map<String, List<String>> types_prototypes = new HashMap<String, List<String>>();
	public Map<String, String[]> prototypes_attributs = new HashMap<String, String[]>();
	public Map<String, Set<String>> methodes_doublures = new HashMap<String, Set<String>>();
	public SortedSet<Prototype> prototypes = new TreeSet<Prototype>();
	public SortedSet<ActeurGlobal> acteursgloblaux = new TreeSet<ActeurGlobal>();
	public boolean bacteursgloblaux = false;

	public boolean formatageActeursgloblauxLinotte3 = true;

	public String prototype = null;
	public Stack<Set<Prototype>> constructionPileContexteVariables = new Stack<Set<Prototype>>();
	// Pour la mise en valeur des paragraphes :
	public Stack<Couple> constructionPileSousParagraphe = new Stack<Couple>();
	public List<Couple> constructionPileSousParagrapheSorted = new SortedList<Couple>(new Comparator<Couple>() {
		@Override
		public int compare(Couple o1, Couple o2) {
			return o1.compareTo(o2);
		}
	});
	public boolean prototype_attribut = false;
	public Map<String, String> prototypes_heritage = new HashMap<String, String>();

	public static class Couple implements Comparable<Couple> {

		public Integer A, B;

		@Override
		public int compareTo(Couple o) {
			return A.compareTo(o.A);
		}

		public String toString() {
			return A + "/" + B + ".";
		}

	}

	// TODO Changer le nom par acteur :
	public static class Prototype implements Comparable<Prototype> {

		public Prototype(String type, String nom, Integer pos) {
			super();
			this.type = type;
			this.nom = nom;
			this.pos = pos;
		}

		public String type;
		public String nom;
		public Integer pos;

		@Override
		public int compareTo(Prototype o) {
			return pos.compareTo(o.pos);
		}

		@Override
		public String toString() {
			return nom + pos;
		}
	}

	public static class ActeurGlobal implements Comparable<ActeurGlobal> {

		public ActeurGlobal(String type, String nom, Integer pos) {
			super();
			this.type = type;
			this.nom = nom;
			this.pos = pos;
		}

		public String type;
		public String nom;
		public Integer pos;

		@Override
		public int compareTo(ActeurGlobal o) {
			return pos.compareTo(o.pos);
		}

		@Override
		public String toString() {
			return nom;
		}
	}

	/**
	 * Test unitaire :
	 */
	public boolean tests = false;

	public ParserContext(MODE pmode) {
		mode = pmode;
	}

	/**
	 * Méthode qui permet de déterminer le message d'erreur le plus juste...
	 * 
	 * @param last
	 */
	public void setDerniereErreur(SyntaxeException last) {
		if (last == null) {
			derniere_erreur = null;
			dernierePhrase = new ArrayList<String>();
		} else {
			if (phraseTaille() > dernierePhrase.size()) {
				derniere_erreur = last;
				dernierePhrase = lisserPhrase();
				derniere_erreur.setPhrase(dernierePhrase);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> lisserPhrase() {
		List<String> tableau_phrase = new ArrayList<String>();
		Iterator<?> i = phrase.iterator();
		while (i.hasNext()) {
			Iterator<String> ii = ((List<String>) i.next()).iterator();
			while (ii.hasNext()) {
				tableau_phrase.add(ii.next().toUpperCase());
			}
		}
		return tableau_phrase;
	}

	private int phraseTaille() {
		int n = 0;
		Iterator<?> i = phrase.iterator();
		while (i.hasNext()) {
			n += ((List<?>) i.next()).size();
		}
		return n;
	}
}