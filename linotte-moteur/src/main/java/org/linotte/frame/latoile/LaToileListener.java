/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : May 5, 2004                                  *
 * Author : Mounès Ronan metalm@users.berlios.de                       *
 *                                                                     *
 *     http://jcubitainer.berlios.de/                                  *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 * metalm@users.berlios.de                                             *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

/* History & changes **************************************************
 *                                                                     *
 ******** May 5, 2004 **************************************************
 *   - First release                                                   *
 ******** May 6, 2004 **************************************************
 *   - Ajout du drage'n'drop pour les thèmes                           *
 ******** May 12, 2004 **************************************************
 *   - Amélioration de la gestion des touches                          *
 ***********************************************************************/

package org.linotte.frame.latoile;

import org.linotte.frame.atelier.Atelier;
import org.linotte.frame.listener.Listener;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Preference;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial")
public class LaToileListener extends JPanelLaToile implements KeyListener, Runnable, MouseMotionListener, MouseListener {

	// http://forum.java.sun.com/thread.jsp?forum=406&thread=478606

	protected LaToileListener(LaToile latoile) {
		super(latoile);
		initListener();
	}

	public static final String TOUCHE_ENTREE = "Entrée";

	public static final String TOUCHE_ESPACE = "Espace";

	public static final String TOUCHE_DEL = "Retour arrière";

	private long KEY_DELAY = 30;

	private Thread thread = null;

	private char chars[] = new char[256];
	private int keys[] = new int[256];

	private final int TOUCHE_NON_ACTIVE = 0;

	private final int TOUCHE_NON_ACTIVE_ET_NON_HONOREE = 1;

	private final int TOUCHE_ACTIVE_ET_HONOREE = 2;

	private final int TOUCHE_ACTIVE_ET_NON_HONOREE = 3;

	private int souris_x;

	private int souris_y;

	public static String SOURIS_CLIQUE = "clique";

	// Afin de savoir si le buffer est vidé :
	//private long lastTime;
	//private long lastTimeVerification;

	// Buffer des touches communs à toutes les toiles :

	private static List<String> touches = new ArrayList<String>(10);

	// Récepteur :

	private Recepteur recepteur = null;

	// Gestion des évenements

	// Ecenements présents ?
	private boolean evenement = true;

	private List<PrototypeGraphique> elementsSourisEntree = new ArrayList<PrototypeGraphique>(10);

	private int delta_souris_x = -1;
	private int delta_souris_y = -1;

	private PrototypeGraphique elementsDragAndDrop = null;

	/*
	 * Date 04/10/2010 Gestion améliorée du drag'n'drop
	 * http://www.developpez.net
	 * /forums/d217381/java/interfaces-graphiques-java/type
	 * -devennement-lance-mousedragged/
	 */
	private static final int DEFAULT_VALUE_DELAY_MOUSE_DRAGGED = -1;
	private static final long DELAY_MOUSE_DRAGGED_CLICKED = 85;

	private long delayDraggedMouse = DEFAULT_VALUE_DELAY_MOUSE_DRAGGED;
	private boolean draggedMouse = false;

	private void initListener() {
		int v = Preference.getIntance().getInt(Preference.P_VITESSE_TOUCHE);
		if (v == 0) {
			Preference.getIntance().setInt(Preference.P_VITESSE_TOUCHE, (int) KEY_DELAY);
		} else {
			KEY_DELAY = v;
		}
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		setFocusable(true);
		thread = new Thread(this);
		thread.start();
	}

	/*
	 * (non-Javadoc)
	 */
	public void run() {

		while (true) {
			// On boucle sur les 256 touches... c'est bourrin, je sais mais
			// c'est pour avoir un code générique.
			boolean b;
			String s;
			int v;
			if (this.isFocusOwner()) {
				for (int i = 0; i < keys.length; i++) {
					if (isDown(i)) {
						up(i);
						v = Integer.valueOf(chars[i]).intValue();
						b = v > 31 && v < 300;
						s = b ? String.valueOf(chars[i]) : KeyEvent.getKeyText(i);
						if (recepteur == null || !recepteur.evenementTouche(s)) {
							touches.add(s);
							reveillerMoteur();
							//lastTime = System.currentTimeMillis();
						}
					}
				}
			}
			try {
				Thread.sleep(KEY_DELAY);

				if (recepteur != null && recepteur.isActif()) {
					// On force l'actualisation de la toile :
					this.setChangement();
				}

			} catch (InterruptedException e) {
			}
		}

	}

	public void reveillerMoteur() {
		synchronized (this) {
			synchronized (getToileParent().getInterpreteur().getRegistreDesEtats().getTemporiser()) {
				getToileParent().getInterpreteur().getRegistreDesEtats().getTemporiser().notifyAll();
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode() & 0xff] = TOUCHE_ACTIVE_ET_NON_HONOREE;
		chars[e.getKeyCode() & 0xff] = e.getKeyChar();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		if (keys[e.getKeyCode() & 0xff] == TOUCHE_ACTIVE_ET_NON_HONOREE)
			keys[e.getKeyCode() & 0xff] = TOUCHE_NON_ACTIVE_ET_NON_HONOREE;
		else if (keys[e.getKeyCode() & 0xff] != TOUCHE_NON_ACTIVE_ET_NON_HONOREE)
			keys[e.getKeyCode() & 0xff] = TOUCHE_NON_ACTIVE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent arg0) {

	}

	private boolean isDown(int key) {
		int valeur = keys[key & 0xff];
		boolean retour = valeur != TOUCHE_NON_ACTIVE;
		if (valeur == TOUCHE_ACTIVE_ET_NON_HONOREE)
			keys[key & 0xff] = TOUCHE_ACTIVE_ET_HONOREE;
		if (valeur == TOUCHE_NON_ACTIVE_ET_NON_HONOREE)
			keys[key & 0xff] = TOUCHE_NON_ACTIVE;
		return retour;
	}

	private void up(int key) {
		keys[key & 0xff] = TOUCHE_NON_ACTIVE;
	}

	public int getSouris_x() {
		return souris_x;
	}

	private void setSouris_x(int souris_x) {
		this.souris_x = souris_x;
	}

	public int getSouris_y() {
		return souris_y;
	}

	private void setSouris_y(int souris_y) {
		this.souris_y = souris_y;
	}

	public void mouseMoved(MouseEvent e) {
		setSouris_x(e.getX() - decal_x);
		setSouris_y(e.getY() - decal_y);

		// Optimisation 0.7.5
		if (isEvenement()) {
			setEvenement(false);
			elementsDragAndDrop = null;
			// Test
			Point point2d = new Point(e.getX() - decal_x, e.getY() - decal_y);
			List<PrototypeGraphique> copie = (List<PrototypeGraphique>) new ArrayList<PrototypeGraphique>(getActeursAAfficher());
			Collections.reverse(copie);
			boolean flag = true;
			List<Listener> executer_debut = new ArrayList<Listener>();
			List<Listener> executer_fin = new ArrayList<Listener>();
			for (PrototypeGraphique shape : copie) {
				if (shape.isListener()) {
					setEvenement(true);
					if (shape.visible && flag && shape.getShape() != null && shape.getShape().contains(point2d)) {
						// Gestion souris entrée et drag n drop
						flag = false;
						if (!elementsSourisEntree.contains(shape)) {
							elementsSourisEntree.add(shape);
							if (shape.isEntreeListener()) {
								List<Listener> listerners = shape.getEntreeListener();
								for (Listener entreeListener : listerners) {
									executer_debut.add(entreeListener);
								}
							}
						}
					} else {
						// Gestion souris sortie
						if (elementsSourisEntree.contains(shape) && !shape.getShape().contains(point2d)) {
							elementsSourisEntree.remove(shape);
							if (shape.isSortieListener()) {
								List<Listener> listerners = shape.getSortieListener();
								for (Listener sortieListener : listerners) {
									executer_fin.add(sortieListener);
								}
							}
						}
					}
				}
			}
			// Collections.reverse(executer);
			for (Listener listener : executer_fin) {
				listener.execute();
			}
			if (!executer_debut.isEmpty()) {
				for (Listener listener : executer_debut) {
					listener.execute();
				}

			}
		}
		if (recepteur == null || !recepteur.evenementSouris()) {
			reveillerMoteur();
		}
	}

	public void mouseDragged(MouseEvent e) {
		/*
		 * Gestion améliorée du drag'n'drop
		 */
		if (true) {
			if (delayDraggedMouse == DEFAULT_VALUE_DELAY_MOUSE_DRAGGED) {
				delayDraggedMouse = System.currentTimeMillis();
			} else {
				if (draggedMouse || (System.currentTimeMillis() - delayDraggedMouse > DELAY_MOUSE_DRAGGED_CLICKED)) {
					draggedMouse = true;

					setSouris_x(e.getX() - decal_x);
					setSouris_y(e.getY() - decal_y);
					if (recepteur == null || !recepteur.evenementSouris()) {
						reveillerMoteur();
					}

					// Gestion du drag and drop
					PrototypeGraphique eg_dnd = null;

					if (elementsDragAndDrop == null) {
						if (elementsSourisEntree != null) {
							for (PrototypeGraphique elementSourisEntree : elementsSourisEntree) {
								if (elementSourisEntree.visible && elementSourisEntree.isDragAndDropListener() && elementSourisEntree.getShape() != null) {
									elementsDragAndDrop = elementSourisEntree;
									eg_dnd = elementSourisEntree;
									// evenement début drag n drop
									List<Listener> listerners = elementsDragAndDrop.getDebutDragAndDropListener();
									if (listerners != null)
										for (Listener dndListener : listerners) {
											dndListener.execute();
										}
									break;
								}
							}
						}
					} else {
						eg_dnd = elementsDragAndDrop;
					}
					if (eg_dnd != null) {
						try {
							if (eg_dnd.retourneAttribut("x") != null) {
								eg_dnd.retourneAttribut("x").setValeur(new BigDecimal(e.getX() - decal_x - delta_souris_x));
								eg_dnd.retourneAttribut("y").setValeur(new BigDecimal(e.getY() - decal_y - delta_souris_y));
							}
							if (eg_dnd.retourneAttribut("x1") != null) {
								BigDecimal dif_x = ((BigDecimal) eg_dnd.retourneAttribut("x2").getValeur()).subtract((BigDecimal) eg_dnd.retourneAttribut("x1")
										.getValeur());
								BigDecimal dif_y = ((BigDecimal) eg_dnd.retourneAttribut("y2").getValeur()).subtract((BigDecimal) eg_dnd.retourneAttribut("y1")
										.getValeur());

								eg_dnd.retourneAttribut("x1").setValeur(new BigDecimal(e.getX() - decal_x - delta_souris_x));
								eg_dnd.retourneAttribut("x2").setValeur(new BigDecimal(e.getX() - decal_x - delta_souris_x).add(dif_x));

								eg_dnd.retourneAttribut("y1").setValeur(new BigDecimal(e.getY() - decal_y - delta_souris_y));
								eg_dnd.retourneAttribut("y2").setValeur(new BigDecimal(e.getY() - decal_y - delta_souris_y).add(dif_y));
							}
						} catch (ErreurException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}

	}

	public String getDerniereTouche() {
		//lastTime = 0;
		//lastTimeVerification = 0;
		synchronized (touches) {
			if (touches.size() == 0)
				return "";
			else {
				return touches.remove(0);
			}
		}
	}

	public boolean resteDesTouches() {
		synchronized (touches) {
			/*if (lastTime != 0 && lastTimeVerification == lastTime) {
				touches.clear();
				lastTime = 0;
				lastTimeVerification = 0;
				return false;
			}*/
			//synchronized (touches) {
			//lastTimeVerification = lastTime;
			return touches.size() != 0;
			//}
		}
	}

	public void videTouches() {
		synchronized (touches) {
			touches.clear();
		}
	}

	public void mouseClicked(MouseEvent e) {

		if (recepteur == null || !recepteur.evenementTouche(SOURIS_CLIQUE)) {
			//System.out.println("mouseClicked");
			int xx = e.getX() - decal_x, yy = e.getY() - decal_y;
			touches.add(SOURIS_CLIQUE);
			reveillerMoteur();
			//lastTime = System.currentTimeMillis();
			if (e.getClickCount() == 2 && getToileParent().getFrameParent() != null && getToileParent().getFrameParent() instanceof Atelier)
				((Atelier) getToileParent().getFrameParent()).ecrireDynamiquementCachier(" x vaut " + xx + ", y vaut " + yy);

			if (elementsSourisEntree.size() > 0) {

				for (PrototypeGraphique elementSourisEntree : elementsSourisEntree) {

					// Gestion du clique
					Point point2d = new Point(xx, yy);
					if (e.getClickCount() == 1 && !e.isConsumed() && (elementSourisEntree.isCliqueListener() || elementSourisEntree.isClicDroitListener())
							&& elementSourisEntree.getShape() != null && elementSourisEntree.getShape().contains(point2d)) {
						e.isConsumed();
						List<Listener> listerners;
						if (e.getButton() == MouseEvent.BUTTON1) {
							listerners = elementSourisEntree.getCliqueListener();
						} else {
							listerners = elementSourisEntree.getClicDroitListener();
						}
						synchronized (listerners) {
							for (Listener cliqueListener : listerners) {
								cliqueListener.execute();
							}
						}
					}
					if (e.getClickCount() == 2 && !e.isConsumed() && elementSourisEntree.isDoubleCliqueListener() && elementSourisEntree.getShape() != null
							&& elementSourisEntree.getShape().contains(point2d)) {
						e.isConsumed();
						List<Listener> listerners = elementSourisEntree.getDoubleCliqueListener();
						synchronized (listerners) {
							for (Listener cliqueListener : listerners) {
								cliqueListener.execute();
							}
						}
					}
				}

			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				requestFocusInWindow();
			}
		});
	}

	public void mouseExited(MouseEvent e) {
		// On sort de la toile, on exécute les évènements de sortie (Linotte
		// 1.4.0)

		List<PrototypeGraphique> copie = (List<PrototypeGraphique>) new ArrayList<PrototypeGraphique>(elementsSourisEntree);

		List<Listener> executer_sortie = new ArrayList<Listener>();

		for (PrototypeGraphique shape : copie) {
			elementsSourisEntree.remove(shape);
			if (shape.isSortieListener()) {
				List<Listener> listerners = shape.getSortieListener();
				for (Listener sortieListener : listerners) {
					executer_sortie.add(sortieListener);
				}
			}
		}

		if (!executer_sortie.isEmpty()) {
			for (Listener listener : executer_sortie) {
				listener.execute();
			}

		}

	}

	public void mousePressed(MouseEvent e) {
		if (recepteur == null || !recepteur.evenementTouche(SOURIS_CLIQUE)) {

			// Gestion du clique
			elementsDragAndDrop = null;
			delta_souris_x = -1;
			delta_souris_y = -1;
			if (elementsSourisEntree != null) {
				Point point2d = new Point(e.getX() - decal_x, e.getY() - decal_y);
				// Collections.sort(elementsSourisEntree);
				Collections.reverse(elementsSourisEntree);
				for (PrototypeGraphique elementSourisEntree : elementsSourisEntree) {
					if (elementSourisEntree.isDragAndDropListener() && elementSourisEntree.getShape() != null
							&& elementSourisEntree.getShape().contains(point2d)) {
						// Gestion du delta entre le clique et la position de
						// l'objet
						try {
							if (elementSourisEntree.retourneAttribut("x") != null) {
								delta_souris_x = souris_x - ((BigDecimal) elementSourisEntree.retourneAttribut("x").getValeur()).intValue();
								delta_souris_y = souris_y - ((BigDecimal) elementSourisEntree.retourneAttribut("y").getValeur()).intValue();
							} else if (elementSourisEntree.retourneAttribut("x1") != null) {
								delta_souris_x = souris_x - ((BigDecimal) elementSourisEntree.retourneAttribut("x1").getValeur()).intValue();
								delta_souris_y = souris_y - ((BigDecimal) elementSourisEntree.retourneAttribut("y1").getValeur()).intValue();
							}
							break;
						} catch (ErreurException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {

		// Traitement lié au drag n drop :

		if (elementsDragAndDrop != null) {
			List<Listener> listerners = elementsDragAndDrop.getDragAndDropListener();
			for (Listener dndListener : listerners) {
				dndListener.execute();
			}
			// Pour que l'element drag n drop retrouve son état normal :
			elementsDragAndDrop = null;
			setChangement();
		}

		/*
		 * Gestion améliorée du drag'n'drop
		 */
		if (!draggedMouse && delayDraggedMouse != DEFAULT_VALUE_DELAY_MOUSE_DRAGGED) {
			mouseClicked(e);
		}
		delayDraggedMouse = DEFAULT_VALUE_DELAY_MOUSE_DRAGGED;
		draggedMouse = false;

	}

	public Recepteur getRecepteur() {
		return recepteur;
	}

	public void setRecepteur(Recepteur precepteur) {
		if (getRecepteur() != null) {
			recepteur.setActif(false);
		}

		recepteur = precepteur;

		if (getRecepteur() != null) {
			recepteur.setActif(true);
		}
	}

	public boolean isEvenement() {
		return evenement;
	}

	public void setEvenement(boolean evenement) {
		this.evenement = evenement;
	}

	public PrototypeGraphique getElementDragAndDrop() {
		return elementsDragAndDrop;
	}

	public void ajouteTouche(String touche) {
		synchronized (touches) {
			touches.add(touche);
		}
		reveillerMoteur();
	}

	@Override
	public void effacer() {
		elementsSourisEntree.clear();
		super.effacer();
	}
}