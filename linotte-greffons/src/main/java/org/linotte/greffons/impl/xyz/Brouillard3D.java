package org.linotte.greffons.impl.xyz;

import java.awt.Color;

import javax.media.j3d.BoundingSphere;
import javax.media.j3d.ExponentialFog;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import org.linotte.greffons.impl.xyz.abstraction.DecorObjet3D;

public class Brouillard3D extends DecorObjet3D {

	private ExponentialFog expFog;

	@Override
	public void initialisation() throws GreffonException {
		if (expFog != null) {
			return;
		}
		expFog = new ExponentialFog(new Color3f(Color.BLACK), 0.3f);
		expFog.setInfluencingBounds(new BoundingSphere(new Point3d(), 100));
		expFog.setCapability(ExponentialFog.ALLOW_DENSITY_WRITE);
		super.initialisation(expFog);
	}

	@Override
	public void destruction() throws GreffonException {
		super.destruction();
		expFog = null;
	}

}
