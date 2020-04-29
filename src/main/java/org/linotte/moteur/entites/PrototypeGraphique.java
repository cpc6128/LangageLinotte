/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

package org.linotte.moteur.entites;

import org.linotte.frame.latoile.LaToile;
import org.linotte.frame.latoile.Recepteur;
import org.linotte.frame.latoile.dessinateur.*;
import org.linotte.frame.latoile.recepteur.ScribeRecepteur;
import org.linotte.frame.listener.Listener;
import org.linotte.greffons.externe.Graphique;
import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.GreffonException;
import org.linotte.greffons.java.interne.a.MethodeInterneDirecte;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.xml.actions.ConditionAction;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;
import org.linotte.moteur.xml.api.Librairie;

import java.awt.*;
import java.awt.geom.Area;
import java.math.BigDecimal;
import java.util.List;
import java.util.*;

/**
 * Cette classe représente un objet graphique sur la toile.
 *
 * @author CPC
 */
public class PrototypeGraphique extends org.linotte.moteur.entites.Prototype implements Comparable<PrototypeGraphique> {

	public static enum TYPE_GRAPHIQUE {
		GRAFFITI, POINT, LIGNE, CERCLE, RECTANGLE, IMAGE, TOILE, SCRIBE, GREFFON, CRAYON, PARCHEMIN, POLYGONE, CHEMIN, MEGALITHE, PRAXINOSCOPE, MOZAIQUE
	};

	private static Set<String> especesGraphique = new HashSet<String>();

	private static Map<PrototypeGraphique.TYPE_GRAPHIQUE, Dessinateur> dessinateurs = null;

	private Dessinateur dessinateur;

	/**
	 * Cache de niveau 1, peut être utilisé pour stocker des résultats intermédiares pour optimiser l'affichage
	 */
	private Object object;

	/**
	 * Cache de niveau 2, peut être utilisé pour stocker des résultats intermédiares pour optimiser l'affichage
	 */
	private Object object2;

	/**
	 * Cache de niveau 2, peut être utilisé pour stocker des résultats intermédiares pour optimiser l'affichage
	 */
	private Object object3;

	/**
	 * Nom de l'objet cache de niveau 1 : par exemple, le nom d'un fichier chargé
	 */
	private String nomObject;

	private Shape shape;

	private PrototypeGraphique.TYPE_GRAPHIQUE typeGraphique;

	// pour la réception des touches sur la toile :
	private Recepteur recepteur;

	private boolean modifie = true;

	private LaToile toile;

	private Area area;

	// Listener :

	private List<Listener> entreeListeners;

	private List<Listener> sortieListeners;

	private List<Listener> cliqueListeners;

	private List<Listener> clicDroitListeners;

	private List<Listener> doubleCliqueListeners;

	private List<Listener> dragAndDropListeners;

	/**
	 * Linotte 1.6.1
	 */
	private List<Listener> debutDragAndDropListeners;

	public long projectionID;

	// Optimisation :
	public float taille, transparence;
	public double x, y, rayon, x1, x2, y1, y2, angle, hauteur, largeur;
	public boolean plein, visible = true, pointe, pose, collision;
	public int position, trame;
	public Color couleur;
	public String image, texte, police, texture;

	public PrototypeGraphique(LaToile pmoteur, Librairie lib, String pnom, Role r, String t, Fichier f, PrototypeGraphique.TYPE_GRAPHIQUE type_graphique) {
		super(lib, pnom, r, t, f);
		toile = pmoteur;
		typeGraphique = type_graphique;
		dessinateur = dessinateurs.get(type_graphique);
		// Ajout d'un recepteur :
		if (type_graphique == TYPE_GRAPHIQUE.SCRIBE) {
			recepteur = new ScribeRecepteur(this, toile);
		}
	}

	public PrototypeGraphique(LaToile pmoteur, Librairie lib, String pnom, Role r, String t, Fichier f, PrototypeGraphique.TYPE_GRAPHIQUE type_graphique,
							  Greffon greffon) {
		super(lib, pnom, r, t, f, greffon);
		toile = pmoteur;
		typeGraphique = type_graphique;
		dessinateur = dessinateurs.get(type_graphique);
		// Ajout d'un recepteur :
		if (type_graphique == TYPE_GRAPHIQUE.SCRIBE) {
			recepteur = new ScribeRecepteur(this, toile);
		}
	}

	public void projette(LaToile toile, Graphics2D g2) throws Exception {
		// System.out.println("Je projette : " + getNom());
		// Est-il visible ?
		if (visible) {
			if (dessinateur != null) {
				dessinateur.projette(toile, g2, this);
			} else if (getGreffon() != null) {
				Graphique graphique = (Graphique) getGreffon();
				try {
					graphique.projette(g2);
					shape = graphique.getShape();
				} catch (GreffonException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Méthodes statics

	public static synchronized void init() {
		if (dessinateurs == null) {
			dessinateurs = new HashMap<PrototypeGraphique.TYPE_GRAPHIQUE, Dessinateur>();
			Dessinateur dessinateur = new TexteDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new ImageDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new PointDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new CercleDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new LigneDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new RectangleDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new ScribeDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new CrayonDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new ParcheminDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new PolygoneDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new CheminDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new MegalitheDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new PraxinoscopeDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
			dessinateur = new MozaiqueDessinateur();
			dessinateurs.put(dessinateur.clef(), dessinateur);
		}
	}

	public static void addEspecesGraphique(String type) {
		especesGraphique.add(type);
	}

	public static boolean isEspecesGraphique_(String type) {
		return especesGraphique.contains(type);
	}

	/**
	 * Retourne le nom du cache niveau 1
	 * @return
	 */
	public String getNomObject() {
		return nomObject;
	}

	/**
	 * Affecte un nom au cache niveau 1
	 * @param o
	 */
	public void setNomObject(String nom_Object) {
		this.nomObject = nom_Object;
	}

	/**
	 * Stocke un objet dans le cache niveau 1
	 * @param o
	 */
	public void setObject(Object o) {
		this.object = o;
	}

	/**
	 * Retourne la cache niveau 1
	 * @return
	 */
	public Object getObject() {
		return object;
	}

	/**
	 * Stocke un objet dans le cache niveau 2
	 * @param o
	 */
	public void setObject2(Object o) {
		this.object2 = o;
	}

	/**
	 * Retourne la cache niveau 2
	 * @return
	 */
	public Object getObject2() {
		return object2;
	}

	/**
	 * Stocke un objet dans le cache niveau 3
	 * @param o
	 */
	public void setObject3(Object o) {
		this.object3 = o;
	}

	/**
	 * Retourne la cache niveau 3
	 * @return
	 */
	public Object getObject3() {
		return object3;
	}

	@Override
	public int compareTo(PrototypeGraphique o) {
		//Linotte2.2
		return position == o.position ? 0 : position > o.position ? 1 : -1;
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape pshape) {
		shape = pshape;
		area = null;
	}

	/**
	 * Force à recalculer le {@link Shape}, utilisé pour vérifier les collisions.
	 * @return la {@link Shape} calculé
	 * @throws Exception
	 */
	public Shape refreshShape() throws Exception {
		try {
			if (dessinateur != null)
				dessinateur.projette(toile, null, this);
			else if (getGreffon() != null) {
				Graphique graphique = (Graphique) getGreffon();
				try {
					graphique.projette(null);
					shape = graphique.getShape();
				} catch (GreffonException e) {
					e.printStackTrace();
				}
			}

		} catch (ErreurException e) {
			e.printStackTrace();
		}
		return shape;
	}

	@Override
	public boolean isEspeceGraphique() {
		return true;
	}

	public TYPE_GRAPHIQUE getTypeGraphique() {
		return typeGraphique;
	}

	public Recepteur getRecepteur() {
		return recepteur;
	}

	public void setModifie(boolean modifie) {
		this.modifie = modifie;
	}

	public boolean isModifie() {
		return modifie;
	}

	@Override
	public void copier(Acteur a) {
		super.copier(a);
		if (typeGraphique == TYPE_GRAPHIQUE.MEGALITHE) {
			object = ((PrototypeGraphique) a).object;
		}
	}

	@Override
	public void viderActeur() throws ErreurException {
		super.viderActeur();
		if (typeGraphique == TYPE_GRAPHIQUE.MEGALITHE) {
			object = null;
		}
	}

	@Override
	public Prototype clone() {
		PrototypeGraphique espece = new PrototypeGraphique(toile, null, null, getRole(), getType(), null, typeGraphique);
		espece.dessinateur = dessinateurs.get(typeGraphique);
		// Ajout d'un recepteur :
		if (typeGraphique == TYPE_GRAPHIQUE.SCRIBE) {
			espece.recepteur = new ScribeRecepteur(this, toile);
		}
		if (typeGraphique == TYPE_GRAPHIQUE.MEGALITHE) {
			espece.object = object;
		}
		espece.greffon = creationGreffon(greffon);
		espece.slots = slots;
		espece.slotsGreffon = slotsGreffon;
		espece.contratsPrototype = contratsPrototype;
		espece.copierAttributs(this);
		toile.getPanelLaToile().addActeursAAfficher(espece, true);
		toile.getPanelLaToile().setChangement();
		return espece;
	}

	public LaToile getToile() {
		return toile;
	}

	public void setToile(final LaToile toile) {
		this.toile = toile;
		// Ajout d'un recepteur :
		if (recepteur != null) {
			recepteur.setToile(toile);
		}
	}

	public Area refreshArea() {
		if (area == null && shape != null) {
			area = new Area(shape);
		}
		return area;
	}

	public void ajouterEntreeListeners(Job job, String paragraphe, Processus processus) {
		if (entreeListeners == null) {
			entreeListeners = Collections.synchronizedList(new ArrayList<Listener>());
		}
		// Linotte est un langage de programmation simple : 
		entreeListeners.clear();
		entreeListeners.add(new Listener(this, paragraphe, processus, job));
	}

	public boolean isEntreeListener() {
		return entreeListeners != null && entreeListeners.size() > 0;
	}

	public List<Listener> getEntreeListener() {
		return entreeListeners;
	}

	public void ajouterSortieListeners(Job job, String paragraphe, Processus processus) {
		if (sortieListeners == null) {
			sortieListeners = Collections.synchronizedList(new ArrayList<Listener>());
		}
		// Linotte est un langage de programmation simple : 
		sortieListeners.clear();
		sortieListeners.add(new Listener(this, paragraphe, processus, job));
	}

	public boolean isSortieListener() {
		return sortieListeners != null && sortieListeners.size() > 0;
	}

	public List<Listener> getSortieListener() {
		return sortieListeners;
	}

	// CliqueListeners
	public void ajouterCliqueListeners(Job job, String paragraphe, Processus processus) {
		if (cliqueListeners == null) {
			cliqueListeners = Collections.synchronizedList(new ArrayList<Listener>());
		}
		// Linotte est un langage de programmation simple : 
		cliqueListeners.clear();
		cliqueListeners.add(new Listener(this, paragraphe, processus, job));
	}

	public boolean isCliqueListener() {
		return cliqueListeners != null && cliqueListeners.size() > 0;
	}

	public List<Listener> getCliqueListener() {
		return cliqueListeners;
	}

	// CliqueDroitListeners
	public void ajouterClicDroitListeners(Job job, String paragraphe, Processus processus) {
		if (clicDroitListeners == null) {
			clicDroitListeners = Collections.synchronizedList(new ArrayList<Listener>());
		}
		// Linotte est un langage de programmation simple : 
		clicDroitListeners.clear();
		clicDroitListeners.add(new Listener(this, paragraphe, processus, job));
	}

	public boolean isClicDroitListener() {
		return clicDroitListeners != null && clicDroitListeners.size() > 0;
	}

	public List<Listener> getClicDroitListener() {
		return clicDroitListeners;
	}

	// DoubleCliqueListeners
	public void ajouterDoubleCliqueListeners(Job job, String paragraphe, Processus processus) {
		if (doubleCliqueListeners == null) {
			doubleCliqueListeners = Collections.synchronizedList(new ArrayList<Listener>());
		}
		// Linotte est un langage de programmation simple : 
		doubleCliqueListeners.clear();
		doubleCliqueListeners.add(new Listener(this, paragraphe, processus, job));
	}

	public boolean isDoubleCliqueListener() {
		return doubleCliqueListeners != null && doubleCliqueListeners.size() > 0;
	}

	public List<Listener> getDoubleCliqueListener() {
		return doubleCliqueListeners;
	}

	public void ajouterDragAndDropListeners(Job job, String paragraphe, Processus processus) {
		if (dragAndDropListeners == null) {
			dragAndDropListeners = Collections.synchronizedList(new ArrayList<Listener>());
		}
		// Linotte est un langage de programmation simple : 
		dragAndDropListeners.clear();
		dragAndDropListeners.add(new Listener(this, paragraphe, processus, job));
	}

	public boolean isDragAndDropListener() {
		return dragAndDropListeners != null && dragAndDropListeners.size() > 0;
	}

	public List<Listener> getDragAndDropListener() {
		return dragAndDropListeners;
	}

	public void ajouterDebutDragAndDropListeners(Job job, String paragraphe, Processus processus) {
		if (debutDragAndDropListeners == null) {
			debutDragAndDropListeners = Collections.synchronizedList(new ArrayList<Listener>());
		}
		// Linotte est un langage de programmation simple : 
		debutDragAndDropListeners.clear();
		debutDragAndDropListeners.add(new Listener(this, paragraphe, processus, job));
	}

	public boolean isDebutDragAndDropListener() {
		return debutDragAndDropListeners != null && debutDragAndDropListeners.size() > 0;
	}

	public List<Listener> getDebutDragAndDropListener() {
		return debutDragAndDropListeners;
	}

	public boolean isListener() {
		return isEntreeListener() || isSortieListener() || isCliqueListener() || isDragAndDropListener() || isDebutDragAndDropListener();
	}

	public void cleanListener() {
		cliqueListeners = null;
		clicDroitListeners = null;
		entreeListeners = null;
		sortieListeners = null;
		doubleCliqueListeners = null;
		dragAndDropListeners = null;
		debutDragAndDropListeners = null;
	}

	public List<Listener> cleanCliqueListeners() {
		List<Listener> temp = cliqueListeners;
		cliqueListeners = null;
		return temp;
	}

	public List<Listener> cleanClicDroitListeners() {
		List<Listener> temp = clicDroitListeners;
		clicDroitListeners = null;
		return temp;
	}

	public List<Listener> cleanEntreeListeners() {
		List<Listener> temp = entreeListeners;
		entreeListeners = null;
		return temp;
	}

	public List<Listener> cleanSortieListeners() {
		List<Listener> temp = sortieListeners;
		sortieListeners = null;
		return temp;
	}

	public List<Listener> cleanDoubleCliqueListeners() {
		List<Listener> temp = doubleCliqueListeners;
		doubleCliqueListeners = null;
		return temp;
	}

	public List<Listener> cleanDragAndDropListeners() {
		List<Listener> temp = dragAndDropListeners;
		dragAndDropListeners = null;
		return temp;
	}

	public List<Listener> cleanDebutDragAndDropListeners() {
		List<Listener> temp = debutDragAndDropListeners;
		debutDragAndDropListeners = null;
		return temp;
	}

	protected void initPrototypeGraphique() {

		ajouterSlotGreffon(new MethodeInterneDirecte(this) {

			@Override
			public Acteur appeler(Greffon greffon, Acteur... parametres) throws Exception {
				try {
					if (parametres.length != 1)
						throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
					Acteur cible = parametres[0];
					if (!(cible instanceof Prototype) && (!((Prototype) cible).isEspeceGraphique()))
						throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());

					return new Acteur(Role.NOMBRE, new BigDecimal(ConditionAction.collision(acteur, cible) ? 1 : 0));

				} catch (Exception e) {
					throw new ErreurException(Constantes.PROTOTYPE_METHODE_FONCTIONNELLE_PARAMETRE, nom());
				}
			}

			@Override
			public String nom() {
				return "collision";
			}
		});

	}

}