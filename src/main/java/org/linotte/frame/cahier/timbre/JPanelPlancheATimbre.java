package org.linotte.frame.cahier.timbre;

import org.linotte.frame.cahier.Cahier;
import org.linotte.frame.cahier.Cahier.EtatCachier;
import org.linotte.frame.cahier.timbre.entite.Bouton;
import org.linotte.frame.cahier.timbre.entite.Debut;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.i.PasSauvegarder;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.Planche;
import org.linotte.frame.cahier.timbre.ui.theme.simple.Abaque;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Panel graphique affichant les timbres sur le cahier
 * 
 * @author CPC
 *
 */
@SuppressWarnings("serial")
public class JPanelPlancheATimbre extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener {

	private static final class ComparateurTimbre implements Comparator<Timbre> {
		@Override
		public int compare(Timbre o1, Timbre o2) {
			return o2.position.compareTo(o1.position);
		}
	}

	//	private static final class ComparateurTimbreInverse implements Comparator<Timbre> {
	//		@Override
	//		public int compare(Timbre o1, Timbre o2) {
	//			return -1 * o2.position.compareTo(o1.position);
	//		}
	//	}

	private Timbre objetGlisse = null;
	private Timbre objetClique = null;
	private Timbre timbreAttache = null;

	// Utilisé pour aligner une premiere fois après le chargement des timbres :
	private Debut debutChargement = null;

	public Planche planche;
	public Cahier cahier;

	private Font font = new Font(Abaque.FONT, Font.PLAIN, Abaque.FONT_SIZE);
	private double scale = 0.85;

	public JPanelPlancheATimbre(Cahier cahier) {
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		planche = new Planche();
		initPanel();
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.cahier = cahier;
	}

	public void paint(Graphics g) {

		Graphics2D g2d = (Graphics2D) g;

		setBackground(Color.WHITE);
		super.paint(g);
		fixeBoutons();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(font);

		AffineTransform at = new AffineTransform();
		// https://stackoverflow.com/questions/32216929/scaling-graphics-with-affinetransform
		at.concatenate(g2d.getTransform());
		at.scale(scale, scale);
		g2d.setTransform(at);

		// On boucle et on dessine les objets :
		planche.graphics = g2d;

		// Alignement des timbres après un chargement :
		if (debutChargement != null) {
			((Timbre) debutChargement).ui.repositionnerTousLesTimbres();
			debutChargement = null;
		}

		// objets.sort(new ComparateurTimbre()); // Java 8 !
		// afin d'être compatible avec java 7 :
		List<Timbre> timbres_ = new ArrayList<>();
		timbres_.addAll(planche.timbres);
		Collections.sort(timbres_, new ComparateurTimbre());

		// Pre dessin
		for (int i = timbres_.size(); i > 0;)
			try {
				Timbre tg = timbres_.get(i-- - 1);
				tg.validation();
				tg.pre_dessine();
			} catch (Exception e) {
				e.printStackTrace();
			}

		// Dessin
		for (int i = timbres_.size(); i > 0;)
			try {
				Timbre tg = timbres_.get(i-- - 1);
				tg.dessine();
			} catch (Exception e) {
				e.printStackTrace();
			}

		if (planche.afficheAide)
			TimbresHelper.texteBienvenue(Abaque.FONT_SIZE, g2d);

	}

	@Override
	public void mouseDragged(MouseEvent ev) {

		refreshCachier();

		int egetX = (int) (ev.getX() / scale);
		int egetY = (int) (ev.getY() / scale);

		if (objetGlisse == null) {
			if (objetClique != null && objetClique.debutGlisseDepose(egetX, egetY)) {
				if (objetClique instanceof Bouton) {
					objetGlisse = ((Bouton) objetClique).derniere_copie;
					if (objetGlisse != null)
						objetGlisse.debutGlisseDepose(egetX, egetY);
				} else {
					planche.historique.prendrePhoto();
					objetGlisse = objetClique;
				}
				planche.delta_x = egetX - objetClique.x;
				planche.delta_y = egetY - objetClique.y;
				if (objetGlisse != null) { // Boutons qui ne produisent pas de
											// bouton (ex : annuler action)
					objetGlisse.init_x = egetX - planche.delta_x;
					objetGlisse.init_y = egetY - planche.delta_y;
				}
				return;
			}
			// Objet qui ne glisse pas : on bloque le drag n drop !
			if (objetGlisse == null) {
				// On glisse la planche à timbre :
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

				int dx = egetX - planche.centre_x;
				int dy = egetY - planche.centre_y;
				planche.centre_x = egetX;
				planche.centre_y = egetY;
				for (Timbre t : planche.timbres) {
					if (!planche.boutons_fixes_actifs.contains(t) && !planche.boutons_fixes_toujours_visibles.contains(t)) {
						t.init_x = t.init_x + dx;
						t.init_y = t.init_y + dy;
						t.x = t.x + dx;
						t.y = t.y + dy;
					}
				}
				refresh();
			}
		} else {
			if (objetGlisse != null) {
				objetGlisse.x = egetX - planche.delta_x;
				objetGlisse.y = egetY - planche.delta_y;
				boolean attacher = false; // Un seul peut être attacher en même
											// temps !
				timbreAttache = null;
				for (Timbre timbre : planche.timbres) {
					if (timbre != objetGlisse) {
						if (timbreAttache == null || (timbreAttache != null && timbreAttache != timbre)) {
							if (!attacher && (objetGlisse.peutIlEtreAttache(timbre))) {
								timbreAttache = timbre;
								attacher = true;
							} else {
								timbre.zoneActive = null;
							}
						}
					}
				}
				refresh();
			}
		}

	}

	/**
	 * 
	 */
	private void refreshCachier() {
		cahier.setEtatCahier(EtatCachier.MODIFIE);
	}

	@Override
	public void mouseMoved(MouseEvent ev) {

		int egetX = (int) (ev.getX() / scale);
		int egetY = (int) (ev.getY() / scale);

		boolean refresh = false;
		for (int i = planche.timbres.size(); i > 0; i--) {
			Timbre timbre = planche.timbres.get(i - 1);
			boolean ancien = timbre.survol;
			if (timbre.contient(egetX, egetY)) {
				timbre.survol = true;
			} else {
				timbre.survol = false;
			}

			if (ancien != timbre.survol)
				refresh = true;
		}
		if (refresh)
			refresh();
	}

	@Override
	public void mouseClicked(MouseEvent ev) {

		int egetX = (int) (ev.getX() / scale);
		int egetY = (int) (ev.getY() / scale);

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		refreshCachier();
		ev.consume();

		planche.historique.prendrePhoto();

		List<Timbre> timbres_ = new ArrayList<>();
		timbres_.addAll(planche.timbres);
		Collections.sort(timbres_, new ComparateurTimbre());

		boolean refresh = false;
		for (int i = 0; i < timbres_.size(); i++) {
			Timbre timbre = timbres_.get(i);
			if (timbre.contient(egetX, egetY)) {
				boolean r = timbre.clique(egetX, egetY);
				refresh = true;
				if (r) {
					planche.historique.archiverPhoto();
					break;
				}
			}
		}
		if (refresh)
			refresh();
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void mousePressed(MouseEvent ev) {

		int egetX = (int) (ev.getX() / scale);
		int egetY = (int) (ev.getY() / scale);

		planche.centre_x = egetX;
		planche.centre_y = egetY;
		if (planche.afficheAide) {
			planche.afficheAide = false;
			refresh();
		}
		// Si j'ai un objet, je peux lancer le drag n drop

		List<Timbre> timbres_ = new ArrayList<>();
		timbres_.addAll(planche.timbres);
		Collections.sort(timbres_, new ComparateurTimbre());

		for (int i = 0; i < timbres_.size(); i++) {
			Timbre timbre = timbres_.get(i);
			if (timbre.contient(egetX, egetY)) {
				objetClique = timbre;
				return;
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent ev) {

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		refreshCachier();
		if (objetGlisse != null) {
			boolean action = false;
			if (timbreAttache != null) {
				action = timbreAttache.finGlisseDepose(objetGlisse);
			} else {
				action = objetGlisse.retourArriere();
			}
			if (action)
				planche.historique.archiverPhoto();
			else
				planche.historique.annulerPhoto();
			objetGlisse = null;
			refresh();
		}

		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	// Méthodes privées :
	// ******************

	private void initPanel() {

		TimbresHelper.constructionBoutons(planche, this);

		chargerTimbresSurPlanche(new ArrayList<Timbre>() {
			{
				add(new Debut(planche));
			}
		});

	}

	private void fixeBoutons() {
		for (Timbre timbre : planche.boutons_fixes_actifs) {
			timbre.x = (int) ((getSize().width / scale) - 100);
		}
		for (Timbre timbre : planche.boutons_fixes_toujours_visibles) {
			timbre.x = (int) ((getSize().width / scale) - 200);
		}
	}

	private void refresh() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					repaint();
				} catch (Exception ble) {
				}
			}
		});
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		double delta = 0.05f * e.getPreciseWheelRotation();
		scale += delta;
		revalidate();
		refresh();
	}

	// Méthodes publiques :
	// ********************

	public void chargerTimbresSurPlanche(List<Timbre> nouveauxtimbres) {

		planche.afficheAide = false;

		for (Timbre timbre : nouveauxtimbres) {
			timbre.planche = planche;
			if (timbre instanceof Debut) {
				debutChargement = (Debut) timbre;
			}
			TimbresHelper.compatibiliteAncienneVersion(timbre);
		}

		planche.timbres.clear();
		planche.timbres.addAll(planche.boutons_fixes_toujours_visibles);
		planche.timbres.addAll(planche.boutons_fixes_actifs);
		planche.timbres.addAll(nouveauxtimbres);

	}

	public List<Timbre> getTimbresASauvegarder() {
		List<Timbre> aSauvegarder = new ArrayList<>();
		for (Timbre timbre : planche.timbres) {
			if (!(timbre instanceof PasSauvegarder))
				aSauvegarder.add(timbre);
			// On efface certaines états :
			timbre.erreur = false;
			timbre.survol = false;
			timbre.glisser = false;
			timbre.deboguage = false;
		}
		return aSauvegarder;
	}

	public List<Timbre> getTimbresAExecuter() {
		List<Timbre> aExecuter = new ArrayList<>();
		for (Timbre timbre : planche.timbres) {
			aExecuter.add(timbre);
			timbre.erreur = false;
			timbre.deboguage = false;
		}
		return aExecuter;
	}

	public void setTimbreErreur(int nbligne) {
		for (Timbre timbre : planche.timbres) {
			timbre.erreur = false;
			if (timbre.numeroLigne == nbligne) {
				timbre.erreur = true;
			}
		}
		refresh();
	}

	public void setTimbreDeboguage(int nbligne) {
		for (Timbre timbre : planche.timbres) {
			timbre.deboguage = false;
			if (timbre.numeroLigne == nbligne) {
				timbre.deboguage = true;
			}
		}
		refresh();
	}

}