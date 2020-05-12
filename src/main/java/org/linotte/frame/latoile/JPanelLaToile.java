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
 ***********************************************************************/

package org.linotte.frame.latoile;

import com.scottlogic.util.SortedList;
import org.linotte.frame.atelier.Atelier;
import org.linotte.frame.outils.ITransparence;
import org.linotte.frame.outils.ProcessMg;
import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

//import com.sun.awt.AWTUtilities;

@SuppressWarnings("serial")
public class JPanelLaToile extends JPanel {

	private static final int RAFRAICHISSEMENT_MAX = 5;

	public final static Color BG = new Color(238, 238, 238);

	// Buffer pour dessiner :
	private Graphics2D imageGraphics = null;

	private Cursor blankCursor;

	private BufferedImage image = null;

	// Cet ID est utilisé pour numéroter de façon unique les éléments sur la
	// toile.
	// Il est, notamment, utilisé pour trier les éléments sur la toile par ordre
	// de projection
	private Long projectionID = 0L;

	private List<PrototypeGraphique> acteursAAfficher = Collections.synchronizedList(new SortedList<PrototypeGraphique>(new Comparator<PrototypeGraphique>() {
		@Override
		public int compare(PrototypeGraphique o1, PrototypeGraphique o2) {
			int p1 = o1.position;
			int p2 = o2.position;
			return (p1 == p2) ?
					/* then 1 */((o1.projectionID == o2.projectionID ? 0 : (o1.projectionID > o2.projectionID ? 1 : -1)))/* else 1 */ : (p1 > p2 ? 1 : -1);
		}
	}));

	private PrototypeGraphique latoile = null;

	private ProcessMg timer = null;

	//private boolean changement = true;
	private short rafraichissementAvantArret = RAFRAICHISSEMENT_MAX;

	private long DELAY = 15;

	private Image fond;

	private String image_fond;

	private String screenShoot = null;

	private int hauteur, largeur;

	private final int HAUTEUR = 590, LARGEUR = 600;

	private final int X = -1, Y = -1;

	private String curseur = null;

	protected int decal_x = 0, decal_y = 0;

	private LaToile parent;

	/**
	 * Pour la gestion de la transparence :
	 */

	private boolean modeTransparence = false;

	private enum MODE_TRANSPARENCE {
		NON_ACTIVE, ACTIVE, ERREUR
	};

	private MODE_TRANSPARENCE etatTransparence = MODE_TRANSPARENCE.NON_ACTIVE;

	protected JPanelLaToile(LaToile latoile) {
		parent = latoile;
	}

	public LaToile getToileParent() {
		return parent;
	}

	public void init() {
		try {
			setBackground(BG);
			setPositionReferentiel(0, 0);
			setTailleToile(LARGEUR, HAUTEUR);
			setPosition(X, Y);
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));

			int v = Preference.getIntance().getInt(Preference.P_VITESSE);
			if (v == 0) {
				Preference.getIntance().setInt(Preference.P_VITESSE, (int) DELAY);
			} else {
				DELAY = v;
			}
			timer = new ProcessMg(new Timer(DELAY, this));
			timer.wakeUp();

			parent.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentMoved(ComponentEvent ce) {
					Preference.getIntance().setInt(Preference.P_TOILE_X, parent.getX());
					Preference.getIntance().setInt(Preference.P_TOILE_Y, parent.getY());
				}
			});

			// Curseur vide :
			Toolkit tk = Toolkit.getDefaultToolkit();
			byte b[] = { (byte) 0 };
			try {
				blankCursor = tk.createCustomCursor(tk.createImage(b), new Point(0, 0), "sans");
			} catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			// System.exit(-1);
		}
	}

	protected void createImage() {
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		if (modeTransparence)
			image = gc.createCompatibleImage(getWidth() + Math.abs(decal_x), getHeight() + Math.abs(decal_y), BufferedImage.TYPE_INT_ARGB_PRE);
		else
			image = gc.createCompatibleImage(getWidth() + Math.abs(decal_x), getHeight() + Math.abs(decal_y));
		imageGraphics = image.createGraphics();
		imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	}

	protected void clearBackground() {
		if (imageGraphics != null) {
			if (latoile != null) {
				try {
					imageGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1f));
					if (modeTransparence) {
						imageGraphics.setColor(new Color(0, 0, 0, 0));
					} else {
						imageGraphics.setColor(latoile.couleur);
					}
					// Fond :
					String tfond = (String) latoile.retourneAttribut("image").getValeur();
					if (tfond != null && tfond.length() > 0) {
						if (fond == null) {
							// On charge la première fois l'image
							chargementImageFond(tfond);
						} else if (!tfond.equals(image_fond)) {
							// L'image a changé !
							chargementImageFond(tfond);
						}
					} else
						fond = null;

				} catch (ErreurException e) {
				}
			} else
				imageGraphics.setColor(BG);
			imageGraphics.fillRect(0, 0, getWidth() + Math.abs(decal_x), getHeight() + Math.abs(decal_y));
			if (fond != null)
				imageGraphics.drawImage(fond, 0, 0, null);
		}
	}

	private void chargementImageFond(String tfond) {
		fond = Ressources.chargementImage(tfond);
		if (fond != null) {
			image_fond = tfond;
			// On va attendre que l'image soit chargée.
			MediaTracker mediaTracker = new MediaTracker(this);
			mediaTracker.addImage(fond, 0);
			try {
				mediaTracker.waitForID(0);
			} catch (InterruptedException e) {
			}

		} else {
			image_fond = null;
		}
	}

	@Override
	public final void paint(Graphics g) {
		if (rafraichissementAvantArret > -1) {
			rafraichissementAvantArret--;
			if (latoile != null) {
				try {
					boolean oldvalue = parent.isUndecorated();
					boolean newvalue = "non".equalsIgnoreCase((String) latoile.retourneAttribut("bordure").getValeur());
					if (newvalue != oldvalue || (modeTransparence && etatTransparence == MODE_TRANSPARENCE.NON_ACTIVE)) {
						parent.setUndecorated(newvalue);
						if (newvalue && modeTransparence) {
							setTranslucencyCapable();
						}
						parent.setVisible(true);
					}
					recalculeTaille();
					setPositionReferentiel(((BigDecimal) latoile.retourneAttribut("rx").getValeur()).intValue(),
							((BigDecimal) latoile.retourneAttribut("ry").getValeur()).intValue());
					setPosition(((BigDecimal) latoile.retourneAttribut("x").getValeur()).intValue(),
							((BigDecimal) latoile.retourneAttribut("y").getValeur()).intValue());
					boolean oldvalueTop = parent.isAlwaysOnTop();
					boolean newvalueTop = "oui".equalsIgnoreCase((String) latoile.retourneAttribut("audessus").getValeur());
					if (oldvalueTop != newvalueTop) {
						parent.setAlwaysOnTop(newvalueTop);
					}
					// curseur :
					if (curseur == null) {
						if ((String) latoile.retourneAttribut("pointeur").getValeur() != null) {
							affecteCurseur((String) latoile.retourneAttribut("pointeur").getValeur());
						}
					} else {
						if (!curseur.equals(latoile.retourneAttribut("pointeur").getValeur())) {
							affecteCurseur((String) latoile.retourneAttribut("pointeur").getValeur());
						}
					}
					// Gestion du plein écran :
					GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
					boolean oldvalueFS = device.getFullScreenWindow() == null ? false : (device.getFullScreenWindow() == parent);
					boolean newvalueFS = "oui".equalsIgnoreCase((String) latoile.retourneAttribut("pleinécran").getValeur());
					if (oldvalueFS != newvalueFS) {
						if (newvalueFS) {
							if (device.isFullScreenSupported()) {
								device.setFullScreenWindow((Window) parent);
							}
						} else {
							if (device.isFullScreenSupported()) {
								device.setFullScreenWindow(null);
							}
						}
					}

				} catch (ErreurException e) {
				}
			}
			if (image != null) {
				clearBackground();
				toPaint();
				if (modeTransparence)
					((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1f));
				g.drawImage(image, decal_x, decal_y, null);
			} else {
				createImage();
				clearBackground();
				toPaint();
				if (modeTransparence)
					((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1f));
				g.drawImage(image, decal_x, decal_y, null);
			}

		} else {
			g.drawImage(image, decal_x, decal_y, null);
		}

		// Faut-il sauvegarder l'image ?
		if (screenShoot != null) {
			String fichier = screenShoot;
			if (!fichier.endsWith(".png"))
				fichier = fichier + ".png";
			screenShoot = null;
			fichier = Ressources.construireChemin(fichier);
			try {
				ImageIO.write(image, "png", new File(fichier));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Force à recalculer la taille de la toile. N'est pas fait que l'affichage n'est pas effectué.
	 * @throws ErreurException
	 */
	public void recalculeTaille() throws ErreurException {
		setTailleToile(((BigDecimal) latoile.retourneAttribut("largeur").getValeur()).intValue(),
				((BigDecimal) latoile.retourneAttribut("hauteur").getValeur()).intValue());
	}

	private void affecteCurseur(String valeur) {
		curseur = valeur;
		if (valeur == null || valeur.equals("") || valeur.equals("normal"))
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		else if (valeur.equals("main"))
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		else if (valeur.equals("flèche"))
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		else if (valeur.equals("texte"))
			setCursor(new Cursor(Cursor.TEXT_CURSOR));
		else if (valeur.equals("sans"))
			setCursor(blankCursor);
	}

	public void toPaint() {
		PrototypeGraphique[] acteursCopiesAAfficher;
		synchronized (acteursAAfficher) {
			acteursCopiesAAfficher = acteursAAfficher.toArray(new PrototypeGraphique[acteursAAfficher.size()]);
		}
		for (PrototypeGraphique prototypeGraphique : acteursCopiesAAfficher) {
			try {
				prototypeGraphique.projette(parent, imageGraphics);
			} catch (Exception e) {
				e.printStackTrace();
				try {
					if (this.getToileParent().isAtelier())
						((Atelier) this.getToileParent().getFrameParent()).ecrireErreurTableau("Erreur lors de l'affichage de la toile : " + e.getMessage());
				} catch (Exception e1) {
				}

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#repaint()
	 */
	@Override
	public void repaint() {
		super.repaint();
	}

	public void setChangement() {
		try {
			if (!(latoile != null && latoile.retourneAttribut("tampon").getValeur() != null && latoile.retourneAttribut("tampon").getValeur().equals("non"))) {
				rafraichissementAvantArret = RAFRAICHISSEMENT_MAX;
			}
		} catch (ErreurException e) {
		}
	}

	// Réinitialise la valeur à true pour le buffer au lancement d'un livre !
	public void annulerBuffer() {
		try {
			if (latoile != null && latoile.retourneAttribut("tampon").getValeur() != null && latoile.retourneAttribut("tampon").getValeur().equals("non"))
				latoile.retourneAttribut("tampon").setValeur("oui");
		} catch (Exception e) {
		}
	}

	/**
	 * @return
	 */

	public void addActeursAAfficher(PrototypeGraphique acteur, boolean forcer) {
		// On supprimer l'ancien :
		if (acteur.getType().equals("toile")) {
			latoile = acteur;
		} else {
			if (!forcer) {
				if (acteursAAfficher.contains(acteur)) {
					acteursAAfficher.remove(acteur);
				}
			}
			synchronized (projectionID) {
				acteur.projectionID = incrementeProjectionID();
				acteursAAfficher.add(acteur);
			}
		}
	}

	public void effacer() {
		try {
			Iterator<PrototypeGraphique> i = acteursAAfficher.iterator();
			while (i.hasNext()) {
				PrototypeGraphique eg = i.next();
				eg.retourneAttribut("visible").setValeur("non");
			}
		} catch (ErreurException e) {
		}
		acteursAAfficher.clear();
		setBackground(BG);
		latoile = null;
		fond = null;
		clearBackground();
		affecteCurseur(null);
		setChangement();
		repaint();
	}

	public boolean isAffiche(PrototypeGraphique especeGraphique) {
		return acteursAAfficher.contains(especeGraphique);
	}

	public void effacer(PrototypeGraphique especeGraphique) {
		acteursAAfficher.remove(especeGraphique);
	}

	public List<PrototypeGraphique> getActeursAAfficher() {
		return acteursAAfficher;
	}

	public void visible() {
		if (!parent.isVisible()) {
			parent.setVisible(true);
			Toile.fermerParent(parent);
		}
	}

	public void focus() {
		if (!parent.isFocused())
			requestFocus();
		visible();
	}

	public void setScreenShoot(String screenShoot) {
		this.screenShoot = screenShoot;
	}

	private void setTailleToile(int largeur, int hauteur) {
		if (this.hauteur == hauteur && this.largeur == largeur)
			return;
		this.hauteur = hauteur;
		this.largeur = largeur;
		setPreferredSize(new Dimension(largeur, hauteur));
		setMinimumSize(new Dimension(largeur, hauteur));
		setSize(largeur, hauteur);
		parent.pack();
		image = null;
	}

	private void setPositionReferentiel(int x, int y) {
		if (this.decal_x == x && this.decal_y == y)
			return;
		this.decal_x = x;
		this.decal_y = y;
		image = null;
	}

	private void setPosition(int x, int y) {
		if (x == X && y == Y)
			return;
		if (parent.getLocation().getX() == x && parent.getLocation().getY() == y)
			return;
		parent.setLocation(x, y);
	}

	public void setRafraichir() {
		setForceAffichage();
		repaint();
	}

	public void setForceAffichage() {
		rafraichissementAvantArret = RAFRAICHISSEMENT_MAX;
	}

	public Graphics2D getImageGraphics() {
		return imageGraphics;
	}

	public Image getImage() {
		return image;
	}

	public Color getColor(int x, int y) {
		// Méthode rapide...
		try {
			Point position = this.getLocationOnScreen();
			Robot robot = new Robot();
			return robot.getPixelColor((int) (position.getX()) + x, (int) (position.getY() + y));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return Color.BLACK;
	}

	/**
	 * Active la transparence de la toile
	 * 
	 * @return
	 */
	private boolean setTranslucencyCapable() {
		try {
			if (ITransparence.getTransparence().isTranslucencySupported((Window) getToileParent())) {
				ITransparence.getTransparence().setOpaque((Window) getToileParent(), false);
				etatTransparence = MODE_TRANSPARENCE.ACTIVE;
				return true;
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		etatTransparence = MODE_TRANSPARENCE.ERREUR;
		return false;
	}

	public void activeTranslucencyCapable() {
		modeTransparence = true;
		image = null;
		setChangement();
	}

	private long incrementeProjectionID() {
		return ++projectionID;
	}

	protected boolean isChangement() {
		return rafraichissementAvantArret > -1;
	}

}