package org.linotte.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.Ressources;
import org.linotte.moteur.xml.analyse.multilangage.Langage;

/**
 * 
 * Wizard permettant de sélectionner le langage au premier démarrage de l'Atelier.
 * 
 * @author CPC
 *
 */
public class Gandalf {

	private static Integer semaphore;
	private static JFrame frame;
	private static Langage langage;
	private static JCheckBox checkBox;

	public static void addComponentsToPane(Container pane) {
		pane.setLayout(new BorderLayout());
		JPanel panel = (JPanel) pane;
		panel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 15));
		Box langagesBox = Box.createHorizontalBox();
		Box titreBox = Box.createHorizontalBox();

		Langage.values();
		for (Langage langage : Langage.values()) {
			ajouterBouton(langage, langagesBox);
		}

		checkBox = new JCheckBox(
				"Ne pas poser la question au prochain démarrage (il sera toujours possible de changer de langage de programmation directement dans l'Atelier)");
		checkBox.setFocusable(false);
		checkBox.setOpaque(false);
		checkBox.setBackground(Color.WHITE);
		checkBox.setForeground(Color.BLACK);

		JLabel logo = new JLabel(Ressources.getImageIcon("linotte_new.png"));
		JLabel question = new JLabel("<html><h1>Choisissez votre langage de programmation</h1></html>");
		JButton quitter = new JButton("Quitter");
		quitter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		quitter.setFocusable(false);
		titreBox.add(logo);
		titreBox.add(new JLabel("   "));
		titreBox.add(question);
		//titreBox.add(jquitter);

		pane.add(titreBox, BorderLayout.NORTH);
		pane.add(langagesBox, BorderLayout.CENTER);
		pane.add(checkBox, BorderLayout.SOUTH);
		checkBox.requestFocusInWindow();
		checkBox.grabFocus();
	}

	private static void ajouterBouton(final Langage plangage, Container container) {
		String h = (plangage == Langage.Linotte2 ? "h1" : "h3");
		JButton bouton = new JButton("<html><b><" + h + "><font color=green>" + plangage.getNom() + "</font></" + h + "></b><br>" + getDescription(plangage)
				+ "</html>");
		bouton.setPreferredSize(new Dimension(300 + (plangage == Langage.Linotte2 ? 70 : -40), 250));
		bouton.setMaximumSize(new Dimension(300 + (plangage == Langage.Linotte2 ? 70 : -40), 250));
		bouton.setOpaque(false);
		bouton.setBorderPainted(true);
		bouton.setBorder(BorderFactory.createEtchedBorder());
		//bouton.setFocusPainted(false);
		bouton.setContentAreaFilled(false);
		bouton.setAlignmentX(Component.CENTER_ALIGNMENT);
		bouton.setAlignmentY(Component.TOP_ALIGNMENT);
		bouton.setBackground(Color.WHITE);
		bouton.setOpaque(false);
		bouton.setIcon(Ressources.getImageIcon("langages/" + plangage.name() + ".png"));
		bouton.setPressedIcon(Ressources.getImageIconePlusClaire("langages/" + plangage.name() + ".png"));
		bouton.setRolloverIcon(Ressources.getImageIconePlusClaire("langages/" + plangage.name() + ".png"));
		bouton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		bouton.setVerticalTextPosition(SwingConstants.TOP);
		bouton.setHorizontalTextPosition(SwingConstants.CENTER);
		container.add(bouton);

		bouton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				synchronized (semaphore) {
					Gandalf.langage = plangage;
					semaphore.notify();
				}
			}
		});

	}

	private static String getDescription(Langage plangage) {
		switch (plangage) {
		case Linotte2:
			return "Le langage de programmation structuré, complet et extensible permettant de créer des applications et des jeux facilement avec sa syntaxe simple et entièrement en français !";
		case Linotte1X:
			return "Ancienne version du langage Linotte avec une syntaxe plus verbeuse que le langage Linotte 2.";
		case Linnet:
			return "Un langage de programmation simplifié permettant d'apprendre l'algorithmique et la programmation avec une syntaxe en <u>anglais</u>.";
		case Lyre:
			return "Un langage de programmation simplifié permettant d'apprendre l'algorithmique et la programmation avec une syntaxe en <u>français</u>.";
		default:
			return "Langage de programmation en cours de developpement";
		}
	}

	public static Langage choisirLangage() {

		if (!Preference.getIntance().containsKey(Preference.P_GANDALF)) {

			semaphore = 42;

			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					frame = new JFrame("Un atelier pour les programmer tous");
					//frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
					List<Image> l = new ArrayList<Image>();
					l.add(Ressources.getImageIcon("linotte_new.png").getImage());
					frame.setIconImages(l);
					//Set up the content pane.
					addComponentsToPane(frame.getContentPane());
					//Display the window.
					frame.setUndecorated(true);
					frame.pack();
					frame.getContentPane().setBackground(Color.WHITE);
					frame.setResizable(false);
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					frame.setAlwaysOnTop(true);

					frame.addWindowListener(new java.awt.event.WindowAdapter() {
						@Override
						public void windowClosing(java.awt.event.WindowEvent windowEvent) {
							synchronized (semaphore) {
								checkBox = null;
								semaphore.notify();
							}

						}
					});

				}
			});

			synchronized (semaphore) {
				try {
					semaphore.wait();
					frame.setVisible(false);
					frame.dispose();
					if (checkBox != null && checkBox.isSelected()) {
						Preference.getIntance().setBoolean(Preference.P_GANDALF, true);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return langage;

	}
}
