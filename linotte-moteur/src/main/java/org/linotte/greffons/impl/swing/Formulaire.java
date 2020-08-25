package org.linotte.greffons.impl.swing;

import net.miginfocom.swing.MigLayout;
import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.linotte.frame.latoile.LaToileJDialog;
import org.linotte.frame.latoile.Toile;
import org.linotte.frame.outils.ITransparence;
import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.externe.Composant;
import org.linotte.greffons.impl.swing.layout.GestionnairePlacement;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Image;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Formulaire extends ComposantSwing {

	private Map<String, java.awt.Image> cacheImage = new HashMap<String, java.awt.Image>();
	private JFrame frame;
	private JPanel panel;
	private int x;
	private int y;
	private Image fond;

	public Formulaire() {
	}

	@Override
	public void initialisation() throws GreffonException {
		if (frame != null) {
			if (!frame.isVisible()) {
				pack();

			}
			return;
		}

		// Bogue remonté par Wam :
		// http://langagelinotte.free.fr/forum/showthread.php?tid=985&pid=6526
		// initEvenement();

		try {
			LaToileJDialog toile = null;
			for (AKRuntime runtime : AKPatrol.runtimes) {
				RuntimeContext context = (RuntimeContext) runtime.getContext();
				toile = (LaToileJDialog) context.getLibrairie().getToilePrincipale();
			}
			Toile.supprimeBarre(toile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		setVisible(getAttributeAsString("visible").equals("oui"));
		// Dimensions :
		int hauteur = getAttributeAsBigDecimal("hauteur").intValue();
		int largeur = getAttributeAsBigDecimal("largeur").intValue();

		// Position :
		x = getAttributeAsBigDecimal("x").intValue();
		y = getAttributeAsBigDecimal("y").intValue();

		// Image de fond
		String backgrd = getAttributeAsString("image");
		fond = chargerImageFond(backgrd);

		String titre = getAttributeAsString("titre");

		frame = new JFrame(titre);
		frame.setIconImage(getRessourceManager().getImage("format-justify-fill.png"));
		panel = new JPanelBackGround(fond, new MigLayout());
		panel.setPreferredSize(new Dimension(largeur, hauteur));
		frame.setContentPane(panel);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setLocation(x, y);
		pack();
		frame.setVisible(isVisible());
		String icone = getAttributeAsString("icône");
		chargerImage(icone);
		initEvenement();
	}

	@Override
	public void ajouterComposant(final Composant pcomposant) throws GreffonException {

		// Cas des gestionnaires de placement :
		if (pcomposant instanceof GestionnairePlacement) {

			GestionnairePlacement gestionnairePlacement = (GestionnairePlacement) pcomposant;
			frame.setLayout(gestionnairePlacement.retourneGestionnairePlacement());
			gestionnairePlacement.fairePere(this);

		} else {

			ComposantSwing composant = (ComposantSwing) pcomposant;

			if (composant instanceof MenuBouton) {
				Component c = composant.getJComponent();
				ajoutMenu(c);
			} else if (composant instanceof Menu) {
				JMenuBar menuBar = null;
				if ((menuBar = frame.getJMenuBar()) == null) {
					menuBar = new JMenuBar();
					frame.setJMenuBar(menuBar);
					pack();
				}
				menuBar.add((JMenu) composant.getJComponent());
			} else {

				ComposantDeplacable composantDeplacable = (ComposantDeplacable) composant;

				try {
					panel.add(composantDeplacable.getJComponent(), "pos " + composantDeplacable.getX() + " " + composantDeplacable.getY());
				} catch (GreffonException e) {
					e.printStackTrace();
				}
				composantDeplacable.setParent(this);
				pack();
				try {
					((Component) composantDeplacable.getJComponent()).requestFocus();
				} catch (GreffonException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @param composant
	 */
	void ajoutMenu(Component composant) {
		JMenuBar menuBar;
		if ((menuBar = frame.getJMenuBar()) == null) {
			menuBar = new JMenuBar();
			frame.setJMenuBar(menuBar);
		}
		if (composant instanceof JMenuBar) {
			JMenuBar temporaire = ((JMenuBar) composant);
			while (temporaire.getMenuCount() > 0) {
				JMenu element = temporaire.getMenu(0);
				menuBar.add(element);
			}
			temporaire.removeAll();
			pack();
		} else
			menuBar.add(composant);
	}

	public void pack() {
		frame.pack();
	}

	@Override
	public void destruction() throws GreffonException {
		if (frame != null) {
			try {
				frame.dispose();
			} catch (Exception e) {
				System.out.println("Impossible de ferme la fenêtre !");
				e.printStackTrace();
			}
			panel = null;
			frame = null;
		}
	}

	@Override
	public boolean enregistrerPourDestruction() {
		return true;
	}

	@Override
	public JComponent getJComponent() throws GreffonException {
		return panel;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		// Patch pour cacher la fenêtre si visible est à fause :
		if (frame != null && "visible".equals(clef)) {
			setVisible(((Acteur) getAttribute(clef)).getValeur().equals("oui"));
			frame.setVisible(isVisible());
			return true;
		}
		if (!super.fireProperty(clef)) {
			if (frame != null && "titre".equals(clef)) {
				frame.setTitle((String) ((Acteur) getAttribute(clef)).getValeur());
				return true;
			}
		}
		return false;
	}

	@Override
	public void initEvenement() throws GreffonException {
		clic_souris.clear();
		souris_entrante.clear();

		final WindowListener wl = new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				for (ListenerGreffons listener : clic_souris) {
					listener.execute();
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		};

		frame.addWindowListener(wl);
	}

	/**
	 * @param hauteur
	 * @param largeur
	 * @param source
	 */
	private void chargerImage(String source) {
		source = getRessourceManager().analyserChemin(source);
		java.awt.Image ii = cacheImage.get(source);
		if (ii == null)
			try {
				BufferedImage img = ImageIO.read(new File(source));
				if (img != null) {
					ii = img;
					cacheImage.put(source, ii);
				}
			} catch (IOException e1) {
			}
		if (ii != null)
			frame.setIconImage(ii);
	}

	private Image chargerImageFond(String source) {
		source = getRessourceManager().analyserChemin(source);
		java.awt.Image ii = cacheImage.get(source);
		if (ii == null)
			try {
				BufferedImage img = ImageIO.read(new File(source));
				if (img != null) {
					ii = img;
					cacheImage.put(source, ii);
				}
			} catch (IOException e1) {
			}
		return ii;
	}

	@Slot
	public boolean bordure(boolean valeur) throws GreffonException {
		boolean v = frame.isVisible();
		frame.dispose();
		frame.setUndecorated(!valeur);
		if (v)
			frame.setVisible(true);
		return true;
	}

	@Slot
	public boolean audessus(boolean valeur) throws GreffonException {
		frame.setAlwaysOnTop(valeur);
		return true;
	}

	@Slot
	public boolean transparence(BigDecimal alpha) throws GreffonException {
		try {
			ITransparence transparence = LinotteFacade.getTransparence();
			if (transparence.isTranslucencySupported(frame)) {
				transparence.setWindowOpacity(frame, alpha);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Slot(nom = "àpropos")
	public boolean apropos() throws GreffonException {
		Apropos apropos = new Apropos(frame);
		apropos.pack();
		apropos.setVisible(true);
		return true;
	}

	@Slot()
	public boolean changementdimension(boolean valeur) throws GreffonException {
		frame.setResizable(valeur);
		return true;
	}

	@Slot()
	public boolean clignoter() throws GreffonException {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.toFront();
				frame.repaint();
			}
		});
		// frame.toFront();
		return true;
	}
}
