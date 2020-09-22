package org.linotte.greffons.impl.xyz;

import org.linotte.greffons.impl.xyz.abstraction.Apparence3D;

import com.sun.j3d.utils.geometry.ColorCube;

/**
 * 
 * @author R.M
 * 
 */
public class Cube3D extends Apparence3D {

	private ColorCube cube3d;

	@Override
	public void initialisation() throws GreffonException {
		if (cube3d != null) {
			return;
		}
		double rayon = getAttributeAsBigDecimal("rayon").doubleValue();
		cube3d = new ColorCube(rayon);
		float transparence = getAttributeAsBigDecimal("transparence").floatValue();
		setApparenceTransparence(transparence, cube3d);
		super.initialisation(cube3d);

	}

	@Override
	public void destruction() throws GreffonException {
		super.destruction();
		cube3d = null;
	}
}
