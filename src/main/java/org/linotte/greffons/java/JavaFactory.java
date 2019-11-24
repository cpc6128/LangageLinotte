package org.linotte.greffons.java;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.linotte.greffons.api.FabriqueGreffon;
import org.linotte.greffons.api.Greffon;
import org.linotte.greffons.java.GreffonPrototype.Entrée;

/**
 * Usine à greffons Java
 * @author CPC
 *
 */
public class JavaFactory extends URLClassLoader implements FabriqueGreffon {

	// http://snippets.dzone.com/posts/show/3574

	public JavaFactory(URL[] urls) {
		super(urls);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public org.linotte.greffons.api.Greffon produire(String id, String classe) {
		try {
			Class c = loadClass(classe);
			Object o = c.newInstance();
			if (o instanceof org.linotte.greffons.externe.Greffon) {
				Greffon greffon = new Greffon();
				greffon.setClass_(c);
				greffon.setId(id);
				greffon.setObjet(o);
				// Ajout des méthodes fonctionnelles :
				List<Entrée> slots = GreffonPrototype.chargerSlotJava(greffon);
				greffon.setSlots(slots);
				return greffon;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see java.net.URLClassLoader#addURL(java.net.URL)
	 */
	public void addURL(URL url) {
		// On surcharge la visibilité
		super.addURL(url);
	}

}