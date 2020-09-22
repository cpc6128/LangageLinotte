package org.linotte.greffons.impl.xyz.abstraction;

import com.sun.j3d.utils.geometry.Primitive;

import javax.media.j3d.Node;

public abstract class DecorObjet3D extends Composant3D {

    protected Node node = null;

    public void initialisation(Node pnode) throws GreffonException {
        node = pnode;
    }

    @Override
    public boolean fireProperty(String clef) throws GreffonException {
        if (!super.fireProperty(clef)) {
        }
        return false;
    }

    @Override
    public Node getComposant3D() {
        return node;
    }

    @Override
    public Primitive getNode() {
        return null;
        //throw new Error("comportement interdit");
    }


    @Override
    public void destruction() throws GreffonException {
        node = null;
    }
}