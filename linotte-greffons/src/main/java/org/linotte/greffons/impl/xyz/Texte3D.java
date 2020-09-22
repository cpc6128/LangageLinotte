package org.linotte.greffons.impl.xyz;

import javax.media.j3d.Font3D;
import javax.media.j3d.FontExtrusion;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3f;

import org.linotte.frame.latoile.Couleur;
import org.linotte.frame.outils.FontHelper;
import org.linotte.greffons.impl.xyz.abstraction.Apparence3D;

/**
 * 
 * @author R.M
 * 
 */
public class Texte3D extends Apparence3D {

	private Shape3D texte3d;

	@Override
	public void initialisation() throws GreffonException {
		if (texte3d != null) {
			return;
		}

		String police = getAttributeAsString("police");
		String texte = getAttributeAsString("texte");
		int taille = getAttributeAsBigDecimal("taille").intValue();

		Font3D my_font = new Font3D(FontHelper.createFont(police, taille), new FontExtrusion());

		Text3D textGeom = new Text3D(my_font, texte, new Point3f(3.0f, -3.0f, 0.0f));
		textGeom.setAlignment(Text3D.ALIGN_CENTER);

		texte3d = new Shape3D();
		texte3d.setGeometry(textGeom);

		String couleur = getAttributeAsString("couleur");
		setApparenceColor(Couleur.retourneCouleur(couleur), texte3d);

		Transform3D scale3D = new Transform3D();
		scale3D.setScale(0.1f);
		TransformGroup scale = new TransformGroup(scale3D);
		scale.addChild(texte3d);
		super.initialisation(scale);

	}

	@Override
	public void destruction() throws GreffonException {
		super.destruction();
		texte3d = null;
	}

}