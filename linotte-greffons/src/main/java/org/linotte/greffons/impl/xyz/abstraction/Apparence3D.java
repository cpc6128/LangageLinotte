package org.linotte.greffons.impl.xyz.abstraction;

import java.awt.Color;
import java.awt.Container;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Material;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import org.linotte.moteur.outils.Ressources;

import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;

public abstract class Apparence3D extends Object3D {

	@Deprecated
	public void setApparenceLight(Primitive obj3D) {
		Appearance app = new Appearance();// obj3D.getAppearance();
		app = newAppearance(app);
		Material mat = new Material();
		mat.setDiffuseColor(new Color3f(Color.blue));
		app.setMaterial(mat);
		obj3D.setAppearance(app);
	}

	public void setApparenceColor(Color c, Primitive obj3D) {
		Appearance app = new Appearance();// obj3D.getAppearance();
		app = newAppearance(app);
		constructAppearanceWithColor(c, app);
		obj3D.setAppearance(app);
	}

	public void setApparenceTransparence(float transparence, Primitive obj3D) {
		Appearance app = obj3D.getAppearance();
		app = newAppearance(app);
		app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 1f - transparence));
		obj3D.setAppearance(app);
	}

	public void setApparenceTransparence(float transparence, Shape3D obj3D) {
		Appearance app = obj3D.getAppearance();
		app = newAppearance(app);
		app.setTransparencyAttributes(new TransparencyAttributes(TransparencyAttributes.NICEST, 1f - transparence));
		obj3D.setAppearance(app);
	}

	public void setApparenceColor(Color c, Shape3D obj3D) {
		Appearance app = new Appearance();// obj3D.getAppearance();
		app = newAppearance(app);
		constructAppearanceWithColor(c, app);
		obj3D.setAppearance(app);
	}

	private Appearance newAppearance(Appearance app) {
		if (app == null)
			app = new Appearance();
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
		app.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_WRITE);
		app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
		app.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_WRITE);
		return app;
	}

	private void constructAppearanceWithColor(Color c, Appearance app) {
		ColoringAttributes color = new ColoringAttributes();
		Color3f color3f = new Color3f(c);
		color.setColor(color3f);
		color.setShadeModel(ColoringAttributes.SHADE_GOURAUD);
		app.setColoringAttributes(color);
	}

	public void setApparenceTexture(String texture, Primitive sphere3d2) {
		sphere3d2.setAppearance(mkAppWithTexture(texture));
	}

	private Appearance mkAppWithTexture(String textureName) {

		Appearance app = new Appearance();
		TextureLoader loader;
		textureName = Ressources.construireChemin(textureName);
		try {
			loader = new TextureLoader(textureName, new Container());

			ImageComponent2D image = loader.getImage();

			Texture2D texture = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, image.getWidth(), image.getHeight());
			texture.setImage(0, image);
			texture.setEnable(true);
			texture.setBoundaryModeS(Texture.WRAP);
			texture.setBoundaryModeT(Texture.WRAP);

			app.setTexture(texture);
			app.setTextureAttributes(new TextureAttributes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return app;

	}
}