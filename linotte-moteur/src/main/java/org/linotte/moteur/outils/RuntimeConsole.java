package org.linotte.moteur.outils;

import org.alize.kernel.AKRuntime;

import java.util.List;

public class RuntimeConsole {

    public static AKRuntime runtime;

    public static AKRuntime getRuntime() {
        return runtime;
    }

    public static int retourneLaLigne(List<Integer> numeros, int position) {
        int ligne = 0;
        for (int entier : numeros) {
            ligne++;
            if (entier > position)
                break;
        }
        return ligne;
    }
}
