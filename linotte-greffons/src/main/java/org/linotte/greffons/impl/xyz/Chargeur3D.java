package org.linotte.greffons.impl.xyz;

import org.linotte.greffons.impl.xyz.abstraction.Apparence3D;
import org.linotte.moteur.outils.Ressources;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;

/**
 * 
 * @author R.M
 * 
 */
public class Chargeur3D extends Apparence3D {

	private Scene scene;

	@Override
	public void initialisation() throws GreffonException {
		if (scene != null) {
			return;
		}

		ObjectFile file = new ObjectFile(ObjectFile.RESIZE);
		Scene scene = null;

		String fichier = getAttributeAsString("fichier");
		String chemin = Ressources.construireChemin(fichier);

		try {
			scene = file.load(chemin);

		} catch (Exception e) {
			e.printStackTrace();
		}
		super.initialisation(scene.getSceneGroup());
	}

	@Override
	public void destruction() throws GreffonException {
		super.destruction();
		scene = null;
	}
}
