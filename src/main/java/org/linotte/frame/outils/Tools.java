package org.linotte.frame.outils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author Romain Bouleis
 */
public class Tools {
    public static void showError(Throwable e) {
        //stock la trace d'execution dans une String
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));

        //cree un JTextArea pour afficher le contenu de l'exception
        JTextArea textArea = new JTextArea(sw.toString(), 20, 40);
        textArea.setEditable(false);
        textArea.setBorder(BorderFactory.createTitledBorder("Détails de l'exception"));
        final JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVisible(false);

        //boutton details
        JButton details = new JButton("Détails...");
        details.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scrollPane.setVisible(!scrollPane.isVisible());
                JDialog dialog = (JDialog) ((JComponent) e.getSource()).getRootPane().getParent();
                dialog.pack();
            }
        });

        //boutton continuer
        JButton ok = new JButton("Quitter");
        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JDialog dialog = (JDialog) ((JComponent) e.getSource()).getRootPane().getParent();
                dialog.dispose();
            }
        });

        //placement des composants
        JPanel buttonsPanel = new JPanel(new FlowLayout());
        buttonsPanel.add(ok);
        buttonsPanel.add(details);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        //affichage du message
        JOptionPane.showOptionDialog(null, "Impossible d'initialiser l'Atelier :\n" + e.getMessage(), "",
                JOptionPane.OK_OPTION, JOptionPane.ERROR_MESSAGE, null, new Object[]{mainPanel}, ok);
    }

    /**
     * http://www.rgagnon.com/javadetails/java-0483.html
     *
     * @param path
     * @return
     */
    static public boolean deleteDirectory(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null)
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
        }
        return (path.delete());
    }

}
