package org.linotte.greffons.impl.swing;

import net.miginfocom.swing.MigLayout;
import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.linotte.frame.latoile.LaToileJDialog;
import org.linotte.frame.latoile.Toile;
import org.linotte.greffons.externe.Composant;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.Image;
import java.awt.Dialog.ModalityType;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SousFormulaire extends ComposantSwing {

	private Map<String, java.awt.Image> cacheImage = new HashMap<String, java.awt.Image>();
	private JDialog sousFormulaire;
	private JPanel panel;
	private int x;
	private int y;
	private Image fond;

	public SousFormulaire() {
	}

	@Override
	public void initialisation() throws GreffonException {
		if (sousFormulaire != null) {
			if (!sousFormulaire.isVisible()) {
				sousFormulaire.pack();
				// frame.setVisible(true);

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

		setVisible(getAttributeAsBigDecimal("visible").intValue()==1);
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

		sousFormulaire = new JDialog(null, ModalityType.APPLICATION_MODAL);
		sousFormulaire.setTitle(titre);
		sousFormulaire.setIconImage(getRessourceManager().getImage("format-justify-fill.png"));
		panel = new JPanelBackGround(fond, new MigLayout());
		panel.setPreferredSize(new Dimension(largeur, hauteur));
		sousFormulaire.setContentPane(panel);
		sousFormulaire.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		sousFormulaire.setLocation(x, y);
		// frame.setResizable(false);
		sousFormulaire.pack();
		sousFormulaire.setVisible(isVisible());
		String icone = getAttributeAsString("icône");
		chargerImage(icone);
		initEvenement();
	}

	@Override
	public void ajouterComposant(final Composant pcomposant) throws GreffonException {

		ComposantSwing composant = (ComposantSwing) pcomposant;

		if (composant instanceof MenuBouton) {
			JMenuBar menuBar = null;
			if ((menuBar = sousFormulaire.getJMenuBar()) == null) {
				menuBar = new JMenuBar();
				sousFormulaire.setJMenuBar(menuBar);
			}
			menuBar.add((JButton) composant.getJComponent());
		} else if (composant instanceof Menu) {
			JMenuBar menuBar = null;
			if ((menuBar = sousFormulaire.getJMenuBar()) == null) {
				menuBar = new JMenuBar();
				sousFormulaire.setJMenuBar(menuBar);
				sousFormulaire.pack();
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
			sousFormulaire.pack();
			try {
				((Component) composantDeplacable.getJComponent()).requestFocus();
			} catch (GreffonException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void destruction() throws GreffonException {
		if (sousFormulaire != null) {
			try {
				sousFormulaire.dispose();
			} catch (Exception e) {
				System.out.println("Impossible de ferme la fenêtre !");
				e.printStackTrace();
			}
			panel = null;
			sousFormulaire = null;
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
		if (sousFormulaire != null && "visible".equals(clef)) {
			setVisible(((Acteur) getAttribute(clef)).getValeur().equals("1"));
			sousFormulaire.setVisible(isVisible());
			return true;
		}
		if (!super.fireProperty(clef)) {
			if (sousFormulaire != null && "titre".equals(clef)) {
				sousFormulaire.setTitle((String) ((Acteur) getAttribute(clef)).getValeur());
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

		sousFormulaire.addWindowListener(wl);
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
			sousFormulaire.setIconImage(ii);
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
		boolean v = sousFormulaire.isVisible();
		sousFormulaire.dispose();
		sousFormulaire.setUndecorated(!valeur);
		if (v)
			sousFormulaire.setVisible(true);
		return true;
	}

	@Slot
	public boolean audessus(boolean valeur) throws GreffonException {
		sousFormulaire.setAlwaysOnTop(valeur);
		return true;
	}

	@Slot()
	public boolean changementdimension(boolean valeur) throws GreffonException {
		sousFormulaire.setResizable(valeur);
		return true;
	}

	@Slot()
	public boolean clignoter() throws GreffonException {
		sousFormulaire.toFront();
		return true;
	}
}
