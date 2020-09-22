package org.linotte.greffons.impl.xyz;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import org.linotte.frame.latoile.Couleur;
import org.linotte.greffons.impl.xyz.abstraction.Apparence3D;

/**
 * @author R.M
 */
public class Boite3D extends Apparence3D {

    private Box box3d;

    @Override
    public void initialisation() throws GreffonException {
        if (box3d != null) {
            return;
        }
        float dx = getAttributeAsBigDecimal("dx").floatValue();
        float dy = getAttributeAsBigDecimal("dy").floatValue();
        float dz = getAttributeAsBigDecimal("dz").floatValue();
        box3d = new Box(dx, dy, dz, Primitive.GENERATE_TEXTURE_COORDS, null);
        float transparence = getAttributeAsBigDecimal("transparence").floatValue();
        setApparenceTransparence(transparence, box3d);
        String couleur = getAttributeAsString("couleur");
        setApparenceColor(Couleur.retourneCouleur(couleur), box3d);

        String texture = getAttributeAsString("texture");
        if (texture != null && texture.trim().length() > 0)
            setApparenceTexture(texture, box3d);

        super.initialisation(box3d);

    }

    @Override
    public void destruction() throws GreffonException {
        super.destruction();
        box3d = null;
    }
}
