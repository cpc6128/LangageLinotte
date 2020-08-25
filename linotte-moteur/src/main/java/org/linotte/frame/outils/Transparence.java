/*
 * 09/29/2010
 *
 * Java7TranslucencyUtil.java - Utilities for translucent Windows in Java 7+.
 * Copyright (C) 2010 Robert Futrell
 * http://fifesoft.com/rtext
 * Licensed under a modified BSD license.
 * See the included license file for details.
 */
package org.linotte.frame.outils;

import org.linotte.frame.latoile.JPanelLaToile;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;

class Transparence extends ITransparence {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean isTranslucencySupported(Window w) {
		String fieldName = "PERPIXEL_TRANSLUCENT";
		boolean supported = false;

		try {

			Field transField = null;

			// An enum that should exist in Java 7.
			Class enumClazz = Class.forName("java.awt.GraphicsDevice$WindowTranslucency");
			Field[] fields = enumClazz.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fieldName.equals(fields[i].getName())) {
					transField = fields[i];
					break;
				}
			}

			if (transField != null) {

				GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice device = env.getDefaultScreenDevice();
				Class deviceClazz = device.getClass();

				// A method that should exist in Java 7
				Method m = deviceClazz.getMethod("isWindowTranslucencySupported", new Class[] { enumClazz });
				Boolean res = (Boolean) m.invoke(device, new Object[] { transField.get(null) });
				supported = res.booleanValue();

			}

		} catch (RuntimeException re) { // FindBugs - don't catch RE's
			//throw re;
		} catch (Exception e) {
			e.printStackTrace();
			supported = false; // FindBugs again - non-empty catch block
		}

		return supported;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setOpaque(Window w, boolean opaque) {
		if (!opaque && !isTranslucencySupported(w)) {
			return false;
		}
		if (opaque) {
			w.setBackground(JPanelLaToile.BG);
		} else {
			w.setBackground(new Color(0, 0, 0, 0));
		}
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setWindowOpacity(Window w, BigDecimal alpha) {
		if (!isTranslucencySupported(w)) {
			return false;
		}
		w.setOpacity(alpha.floatValue() / 100);
		return true;
	}
}