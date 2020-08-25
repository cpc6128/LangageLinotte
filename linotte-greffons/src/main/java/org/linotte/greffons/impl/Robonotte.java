package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Graphique;

import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class Robonotte extends Graphique {

    // Attributs de l'objet Labyrinthe

    // Etat du personnage
    private int position_x = -1;
    private int position_y = -1;

    // Etat du jeu
    private BigDecimal[] modele;
    private boolean clef = false;

    // Affichage
    private String message = "J'attends vos ordres !";

    // Methodes liees a l aspect graphique
    @Override
    public void projette(Graphics2D g) throws GreffonException {
        // Quadrillage :
        int longueur = getAttributeAsBigDecimal("longueur").intValue();
        int largeur = getAttributeAsBigDecimal("largeur").intValue();

        if (modele == null)
            return;

        for (int i = 0; i < longueur; i++) {
            for (int j = 0; j < largeur; j++) {
                int piece = modele[i + longueur * j].intValue();

                // Premiere couche
                if (piece == 0)
                    // Sol
                    g.setColor(Color.GRAY);
                if (piece == 1)
                    // Mur
                    g.setColor(Color.RED);
                if (piece == 3)
                    // Clef
                    g.setColor(Color.DARK_GRAY);
                if (piece == 4)
                    // Porte
                    g.setColor(Color.BLACK);
                if (piece == 6)
                    // Sol avec un pas
                    g.setColor(Color.GRAY);
                g.fill3DRect(i * 55, j * 55, 50, 50, true);

                // Deuxieme couche
                if (piece == 6) {
                    // Affichage des pas :
                    g.setColor(Color.BLACK);
                    g.fillRect(i * 55 + 20, j * 55 + 20, 10, 10);
                }
                if (piece == 3) {
                    // Clef
                    g.setColor(Color.BLUE);
                    g.fillOval(i * 55 + 10, j * 55 + 30, 15, 15);
                    g.fillRect(i * 55 + 15, j * 55 + 30, 30, 5);
                    g.fillRect(i * 55 + 40, j * 55 + 30, 5, 15);
                    g.fillRect(i * 55 + 33, j * 55 + 30, 5, 15);
                }
                if (piece == 4) {
                    // Porte
                    g.setColor(Color.WHITE);
                    g.fillRect(i * 55 + 5, j * 55 + 20, 10, 3);
                }

                // Robot :
                g.setColor(Color.YELLOW);
                g.fillOval(position_x * 55, position_y * 55, 47, 47);
                g.setColor(Color.ORANGE);
                g.fillRect(position_x * 55, position_y * 55 + 40, 10, 10);
                g.fillRect(position_x * 55 + 37, position_y * 55 + 40, 10, 10);
                g.setColor(Color.BLACK);
                g.fillOval(position_x * 55 + 10, position_y * 55 + 10, 5, 5);
                g.fillOval(position_x * 55 + 35, position_y * 55 + 10, 5, 5);

                // Message
                g.setColor(Color.BLACK);
                g.drawString(message, 10, largeur * 55 + 20);

                // Message clef :
                if (clef) {
                    g.setColor(Color.BLACK);
                    g.drawString("J'ai une clef.", 10, largeur * 55 + 40);
                }
            }
        }
    }

    @Override
    public Shape getShape() {
        return null;
    }

    /**
     * Methodes liees au greffon Labyrinthe *
     */

    @Slot
    public boolean affiche(List<Integer> tableau) {
        modele = tableau.toArray(new BigDecimal[tableau.size()]);
        // Force le rafraichissement de la toile :
        getRessourceManager().setChangement();
        return true;
    }

    @Slot
    public boolean poserobot(int x, int y) {
        position_x = x - 1;
        position_y = y - 1;
        afficheOrdre("On peut commencer chef !");
        return true;
    }

    @Slot
    public boolean droite() {
        prepareOrdre("action : droite");
        position_x = position_x + 1;
        int longueur = getAttributeAsBigDecimal("longueur").intValue();
        int piece = modele[position_x + longueur * position_y].intValue();
        if (piece != 1) {
            if (modele[position_x - 1 + longueur * position_y].intValue() == 0)
                modele[position_x - 1 + longueur * position_y] = new BigDecimal(6);
            afficheOrdre("Ok, je vais à droite !");
        } else {
            position_x = position_x - 1;
            afficheOrdre("Pas possible !");
        }
        return true;
    }

    @Slot
    public boolean gauche() {
        prepareOrdre("action : gauche");
        position_x = position_x - 1;
        int longueur = getAttributeAsBigDecimal("longueur").intValue();
        int piece = modele[position_x + longueur * position_y].intValue();
        if (piece != 1) {
            if (modele[position_x + 1 + longueur * position_y].intValue() == 0)
                modele[position_x + 1 + longueur * position_y] = new BigDecimal(6);
            afficheOrdre("Ok, je vais à gauche !");
        } else {
            position_x = position_x + 1;
            afficheOrdre("Pas possible !");
        }
        return true;
    }

    @Slot
    public boolean haut() {
        prepareOrdre("action : haut");
        position_y = position_y - 1;
        int longueur = getAttributeAsBigDecimal("longueur").intValue();
        int piece = modele[position_x + longueur * position_y].intValue();
        if (piece != 1) {
            if (modele[position_x + longueur * (position_y + 1)].intValue() == 0)
                modele[position_x + longueur * (position_y + 1)] = new BigDecimal(6);
            afficheOrdre("Ok, je vais en haut !");
        } else {
            position_y = position_y + 1;
            afficheOrdre("Pas possible !");
        }
        return true;
    }

    @Slot
    public boolean bas() {
        prepareOrdre("action : bas");
        position_y = position_y + 1;
        int longueur = getAttributeAsBigDecimal("longueur").intValue();
        if ((position_x + longueur * position_y) > modele.length) {
            afficheOrdre("Ok, je vais en bas !");
            return true;
        }
        int piece = modele[position_x + longueur * position_y].intValue();

        if (piece != 1) {
            if (modele[position_x + longueur * (position_y - 1)].intValue() == 0)
                modele[position_x + longueur * (position_y - 1)] = new BigDecimal(6);
            afficheOrdre("Ok, je vais en bas !");
            return true;
        } else {
            position_y = position_y - 1;
            afficheOrdre("Pas possible !");
            return false;
        }

    }

    @Slot
    public boolean ouvre() {
        prepareOrdre("action : ouvre");
        int longueur = getAttributeAsBigDecimal("longueur").intValue();
        int piece = modele[position_x + longueur * position_y].intValue();
        if (piece == 4 && clef) {
            clef = false;
            modele[position_x + longueur * position_y] = new BigDecimal(0);
            afficheOrdre("Ok, j'ai ouvert la porte !");
        } else
            afficheOrdre("Pas possible !");
        return true;
    }

    @Slot
    public boolean prend() {
        prepareOrdre("action : prend");
        int longueur = getAttributeAsBigDecimal("longueur").intValue();
        int piece = modele[position_x + longueur * position_y].intValue();
        if (piece == 3) {
            modele[position_x + longueur * position_y] = new BigDecimal(0);
            clef = true;
            afficheOrdre("Ok, je prends la clef !");
        } else
            afficheOrdre("Pas possible !");
        return true;
    }

    @Slot
    public boolean quitte() {
        prepareOrdre("action : quitte");
        message = "Ok, au revoir chef !";
        getRessourceManager().setChangement();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        message = "Robot arrete";
        getRessourceManager().setChangement();
        return true;
    }

    private void afficheOrdre(String texte) {
        message = texte;
        getRessourceManager().setChangement();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        message = "J'attends vos ordres !";
        getRessourceManager().setChangement();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }

    private void prepareOrdre(String texte) {
        message = texte;
        getRessourceManager().setChangement();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
    }

}