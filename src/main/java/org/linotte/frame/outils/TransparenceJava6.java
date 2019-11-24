package org.linotte.frame.outils;

import java.awt.Window;
import java.math.BigDecimal;

import com.sun.awt.AWTUtilities;
import com.sun.awt.AWTUtilities.Translucency;

/**
 * Gestion de la transparence pour java 6
 * 
 */
class TransparenceJava6 extends ITransparence {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isTranslucencySupported(Window w) {
		try {
			// On vérifie d'abord la présence de la classe AWTUtilities :
			Class.forName("com.sun.awt.AWTUtilities");
			// On vérifie que le système permettent ces effets :
			return AWTUtilities.isTranslucencySupported(Translucency.PERPIXEL_TRANSLUCENT)
					&& AWTUtilities.isTranslucencyCapable((w).getGraphicsConfiguration());
		} catch (Exception e) {
			// AWTUtilities n'existe pas pour cette JVM ou alors, impossible de
			// la charger : on ne fait rien !
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setOpaque(Window w, boolean opaque) {
		try {
			// On vérifie d'abord la présence de la classe AWTUtilities :
			Class.forName("com.sun.awt.AWTUtilities");
			// Puis on rend la fenêtre non-opaque :
			AWTUtilities.setWindowOpaque(w, opaque);
			return true;
		} catch (ClassNotFoundException e) {
			// AWTUtilities n'existe pas pour cette JVM : on ne fait rien !
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean setWindowOpacity(Window w, BigDecimal alpha) {
		try {
			// On vérifie d'abord la présence de la classe AWTUtilities :
			Class.forName("com.sun.awt.AWTUtilities");
			// On vérifie que le système permettent ces effets :
			if (com.sun.awt.AWTUtilities.isTranslucencySupported(com.sun.awt.AWTUtilities.Translucency.PERPIXEL_TRANSLUCENT)
					&& com.sun.awt.AWTUtilities.isTranslucencyCapable(w.getGraphicsConfiguration())) {
				// Puis on rend la fenêtre non-opaque :
				com.sun.awt.AWTUtilities.setWindowOpacity(w, alpha.floatValue() / 100);
			}
			return true;
		} catch (Exception e) {
			// AWTUtilities n'existe pas pour cette JVM : on ne fait rien !
			e.printStackTrace();
		}
		return false;
	}

}