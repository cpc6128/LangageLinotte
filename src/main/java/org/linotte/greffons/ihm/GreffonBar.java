package org.linotte.greffons.ihm;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import org.linotte.moteur.outils.Ressources;

public class GreffonBar extends JPanel {

	private static final long serialVersionUID = 1L;
	private JProgressBar progressBar;
	private JDialog pere;

	public GreffonBar(JDialog pere, int length) {
		super(new BorderLayout());
		this.pere = pere;
		init(length);
	}

	public JProgressBar getJProgressBar() {
		return progressBar;
	}

	public JDialog getJDialog() {
		return pere;
	}

	public void onAvanceDe(int pas) {
		progressBar.setValue(progressBar.getValue() + pas);
	}

	private void init(int length) {
		if (length < 0)
			length = 0;
		progressBar = new JProgressBar(0, length);
		progressBar.setValue(0);
		progressBar.setStringPainted(false);

		JPanel panel = new JPanel();
		panel.add(progressBar);

		add(panel, BorderLayout.PAGE_START);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	}

	public static GreffonBar createAndShowGUI(int length) {
		JDialog frame = new JDialog((JFrame) null, "Gestionnaires des greffons");
		frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		frame.setIconImage(Ressources.getImageIcon("applications-other.png").getImage());
		GreffonBar newContentPane = new GreffonBar(frame, length);
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);

		//Display the window.
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		return newContentPane;
	}
}
