package org.linotte.greffons.impl.xyz.abstraction;


import org.linotte.greffons.externe.Composant;

import javax.media.j3d.Node;

public abstract class Composant3D extends Composant {

    @Override
    public void ajouterComposant(Composant composant) throws GreffonException {

    }

    public abstract Node getComposant3D();

    public abstract Node getNode();

    @Override
    public boolean enregistrerPourDestruction() {
        return true;
    }

}