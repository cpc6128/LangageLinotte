package org.linotte.greffons.impl.xyz;

import com.sun.j3d.utils.geometry.Sphere;
import org.linotte.frame.latoile.Couleur;
import org.linotte.greffons.impl.xyz.abstraction.Apparence3D;

/**
 * @author R.M
 */
public class Sphere3D extends Apparence3D {

    private Sphere sphere3d;

    @Override
    public void initialisation() throws GreffonException {
        if (sphere3d != null) {
            return;
        }
        float rayon = getAttributeAsBigDecimal("rayon").floatValue();
        sphere3d = new Sphere(rayon, Sphere.GENERATE_TEXTURE_COORDS, null);
        float transparence = getAttributeAsBigDecimal("transparence").floatValue();
        setApparenceTransparence(transparence, sphere3d);
        String couleur = getAttributeAsString("couleur");
        setApparenceColor(Couleur.retourneCouleur(couleur), sphere3d);

        String texture = getAttributeAsString("texture");
        if (texture != null && texture.trim().length() > 0)
            setApparenceTexture(texture, sphere3d);

        super.initialisation(sphere3d);

    }

    @Override
    public void destruction() throws GreffonException {
        super.destruction();
        sphere3d = null;
    }
}
