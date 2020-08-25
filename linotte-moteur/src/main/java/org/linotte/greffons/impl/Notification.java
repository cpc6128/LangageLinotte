package org.linotte.greffons.impl;

import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.linotte.frame.latoile.LaToile;
import org.linotte.greffons.LinotteFacade;
import org.linotte.greffons.externe.Greffon;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.alize.ParserEnvironnement;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.ThreadLinotte;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 
 * Voici un exemple
 * 
 * <code>
 * globale 
 * 	notification :: notification , icône vaut "image.png", description vaut "voici un exemple", infobulle vaut "démonstration"
 * 
 * principale : 
 * 	début 
 * 		évoque notification.ajoutermenu("Action 1", "fonction1")
 * 		évoque notification.ajoutermenu("Action 2", "fonction2") 
 * 		évoque notification.afficher() 
 * 		tant que vrai, temporise
 * 
 * fonction1 : 
 * 	début 
 * 		évoque notification.messagesystème("Voici un message depuis Linotte ${version}")
 * 		affiche "l'exemple fonctionne !" 
 * 		reviens
 * 
 * 
 * fonction2 : 
 * 	début 
 * 		évoque notification.messageerreur("Voici un message depuis Linotte ${version}")
 * 		reviens
 * </code>
 * 
 * @author cpc
 * @version 0.1
 * 
 */
public class Notification extends Greffon implements Runnable {

	// Attributs :
	private static final String ICONE = "icône";
	private static final String DESCRIPTION = "description";

	// Autres :
	private PopupMenu popup;
	private TrayIcon trayIcon;
	private Image image;
	private String nomImage;
	private Processus processusclique;
	private String fonctionclique;
	private Runnable moi;

	public Notification() {
		popup = new PopupMenu();
		new Thread(moi, "Notification").start();
	}

	@Slot
	public boolean ajoutermenu(String action, final String fonction) throws GreffonException {
		Job job = getCurrentJob();
		final Processus procesus = rechercher(job, fonction);

		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Job job = getCurrentJob();
					execution(job, fonction, procesus);
				} catch (Exception e1) {
					e1.printStackTrace();
					trayIcon.displayMessage("ERREUR", e1.getMessage(), TrayIcon.MessageType.ERROR);
				}
			}
		};

		MenuItem defaultItem = new MenuItem(action);
		defaultItem.addActionListener(listener);
		popup.add(defaultItem);

		return true;
	}

	@Slot
	public boolean afficher() throws GreffonException {
		synchronized (this) {
			SystemTray tray = SystemTray.getSystemTray();
			if (trayIcon == null) {
				// Récupérer l'image
				String icone = getAttributeAsString(ICONE);
				String description = getAttributeAsString(DESCRIPTION);
				chargerImage(icone);
				trayIcon = new TrayIcon(image, description, popup);
				trayIcon.setImageAutoSize(true);

				MouseListener mouseListener = new MouseListener() {
					public void mouseClicked(MouseEvent e) {
						if (e.getClickCount() == 2) {
							try {
								Job job = getCurrentJob();
								execution(job, fonctionclique, processusclique);
							} catch (Exception e1) {
								e1.printStackTrace();
								trayIcon.displayMessage("ERREUR", e1.getMessage(), TrayIcon.MessageType.ERROR);
							}
						}
					}

					public void mouseEntered(MouseEvent e) {
					}

					public void mouseExited(MouseEvent e) {
					}

					public void mousePressed(MouseEvent e) {
					}

					public void mouseReleased(MouseEvent e) {
					}
				};

				trayIcon.addMouseListener(mouseListener);

				try {
					tray.add(trayIcon);
				} catch (AWTException e) {
					throw new GreffonException("Impossible d'ajouter un icône dans la zone de notification");
				}
			} else {
				try {
					tray.add(trayIcon);
				} catch (AWTException e) {
				}
			}
		}
		return true;
	}

	@Slot(nom = "messagesystème")
	public boolean messagesysteme(String titre, String message) {
		trayIcon.displayMessage(titre, message, TrayIcon.MessageType.INFO);
		return true;
	}

	@Slot()
	public boolean messageerreur(String titre, String message) {
		trayIcon.displayMessage(titre, message, TrayIcon.MessageType.ERROR);
		return true;
	}

	@Slot
	public boolean supprimer() throws GreffonException {
		SystemTray tray = SystemTray.getSystemTray();
		tray.remove(trayIcon);
		return true;
	}

	@Slot
	public boolean actionclique(String fonction) throws GreffonException {
		Job job = getCurrentJob();
		processusclique = rechercher(job, fonction);
		fonctionclique = fonction;
		return true;
	}

	// ************
	// API Greffons
	// ************

	@Override
	public boolean fireProperty(String attribut) {
		if (attribut.equals(DESCRIPTION)) {
			if (trayIcon != null)
				trayIcon.setToolTip(getAttributeAsString(DESCRIPTION));
			return true;
		}
		if (attribut.equals(ICONE)) {
			if (trayIcon != null) {
				String icone = getAttributeAsString(ICONE);
				try {
					chargerImage(icone);
				} catch (GreffonException e) {
					e.printStackTrace();
				}
				trayIcon.setImage(image);
			}
			return true;
		}
		return false;
	}

	// ******************
	// Méthodes privées :
	// ******************

	private static Job getCurrentJob() throws GreffonException {
		try {
			for (AKRuntime runtime : AKPatrol.runtimes) {
				return (Job) runtime.getJob();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param job
	 * @param paragraphe
	 * @param runtimeContext
	 * @param processus
	 */
	private void execution(Job job, String paragraphe, Processus processus) {
		if (job.isRunning() && paragraphe != null) {
			RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
			ThreadLinotte threadLinotte = new ThreadLinotte(job, processus, null, paragraphe);
			if (runtimeContext != null) {
				Map<String, List<ThreadLinotte>> threads = runtimeContext.getThreads();
				synchronized (threads) {
					List<ThreadLinotte> list = threads.get(paragraphe);
					if (list == null) {
						list = new ArrayList<ThreadLinotte>();
					}
					list.add(threadLinotte);
					threads.put(paragraphe, list);
				}
				new Thread(threadLinotte).start();
			}
		}
	}

	private Processus rechercher(Job job, String paragraphe) throws GreffonException {
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		ParserEnvironnement environnement = runtimeContext.getEnvironnment();
		Processus processus = environnement.getParagraphe(paragraphe);
		if (processus == null) {
			throw new GreffonException("Fonction inconnue : " + paragraphe);
		}
		return processus;
	}

	/**
	 * @param fichier
	 * @return
	 * @throws GreffonException 
	 */
	private void chargerImage(String fichier) throws GreffonException {
		if (fichier != null) {
			if (image == null) {
				// On charge la première fois l'image
				image = chargementImage(fichier, LinotteFacade.getToile());
				if (image != null) {
					nomImage = fichier;
				} else {
					nomImage = null;
					image = null;
				}
			} else if (!fichier.equals(nomImage)) {
				image = chargementImage(fichier, LinotteFacade.getToile());
				if (image != null) {
					nomImage = fichier;
				} else {
					nomImage = null;
					image = null;
				}

			}
		} else {
			image = null;
			nomImage = null;
		}
	}

	private Image chargementImage(String fichier, LaToile toile) {
		Image image = Ressources.chargementImage(fichier);
		// On va attendre que l'image soit chargée.
		if (toile != null) {
			MediaTracker mediaTracker = new MediaTracker(toile.getPanelLaToile());
			mediaTracker.addImage(image, 0);
			try {
				mediaTracker.waitForID(0);
			} catch (InterruptedException e) {
			}
		}
		return image;
	}

	@Override
	public void run() {
		try {
			SystemTray tray = SystemTray.getSystemTray();
			boolean continuer = true;
			while (continuer) {
				Thread.sleep(1000);
				try {
					Job job = getCurrentJob();
					if (job == null || !job.isRunning()) {
						continuer = false;
						if (trayIcon != null) {
							tray.remove(trayIcon);
						}
					}
				} catch (GreffonException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
