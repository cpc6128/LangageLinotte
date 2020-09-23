package org.linotte.greffons.impl.xyz;

import org.linotte.greffons.impl.xyz.abstraction.DecorObjet3D;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Light;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class Lumiere3D extends DecorObjet3D {

    private DirectionalLight diLi;

    @Override
    public void initialisation() throws GreffonException {
        if (diLi != null) {
            return;
        }
        Light diLi = new DirectionalLight(new Color3f(1, 1, 0), new Vector3f(1, -1, -1));
        diLi.setInfluencingBounds(new BoundingSphere());
        super.initialisation(diLi);
    }

    @Override
    public void destruction() throws GreffonException {
        super.destruction();
        diLi = null;
    }

}
