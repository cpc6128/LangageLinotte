package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Greffon;

/**
 * Exemple d'utilisation du greffon
 * <p>
 * principale :
 * messager est un majordome, serviteur vaut "Ronan"
 * début
 * affiche messager.présentation("Jules")
 * <p>
 * Et on obtient à l'affichage :
 * "Bonjour, mon cher Jules, je suis votre serviteur Ronan"
 */

public class Majordome extends Greffon {

    @Slot(nom = "présentation")
    public String presentation(String prenom) {
        return "Bonsoir, mon cher " + prenom + ", je suis votre serviteur !";
    }

}
