package org.linotte.greffons.impl.xyz;

import org.linotte.frame.latoile.Couleur;
import org.linotte.greffons.impl.xyz.abstraction.Apparence3D;

import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Primitive;

/**
 * 
 * @author R.M
 * 
 */
public class Cone3D extends Apparence3D {

	private Cone cone3d;

	@Override
	public void initialisation() throws GreffonException {
		if (cone3d != null) {
			return;
		}
		float angle = getAttributeAsBigDecimal("angle").floatValue();
		float hauteur = getAttributeAsBigDecimal("hauteur").floatValue();
		cone3d = new Cone(angle, hauteur, Primitive.GENERATE_TEXTURE_COORDS, null);
		float transparence = getAttributeAsBigDecimal("transparence").floatValue();
		setApparenceTransparence(transparence, cone3d);
		String couleur = getAttributeAsString("couleur");
		setApparenceColor(Couleur.retourneCouleur(couleur), cone3d);

		String texture = getAttributeAsString("texture");
		if (texture != null && texture.trim().length() > 0)
			setApparenceTexture(texture, cone3d);

		super.initialisation(cone3d);
	}

	@Override
	public void destruction() throws GreffonException {
		super.destruction();
		cone3d = null;
	}
}
