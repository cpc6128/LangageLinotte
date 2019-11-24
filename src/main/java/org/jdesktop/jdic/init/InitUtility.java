package org.jdesktop.jdic.init;

import java.io.File;

import org.jdesktop.jdic.browser.internal.WebBrowserUtil;

/**
 * 
 * @author CPC
 *
 */
public class InitUtility {
	public static native String getEnv(String paramString);

	public static native void setEnv(String paramString1, String paramString2);

	public static void preAppendEnv(String paramString1, String paramString2) {
		String str1 = getEnv(paramString1);
		String str2 = paramString2 + File.pathSeparator + str1;
		setEnv(paramString1, str2);
	}

	static {
		try {
			JdicManager.getManager().initShareNative();
		} catch (JdicInitException localJdicInitException) {
			localJdicInitException.printStackTrace();
			WebBrowserUtil.error(localJdicInitException.getMessage());
		}
		initDLL();
	}

	public static void initDLL() {
		/** Ajout du swich si x86 ou amd64
		 * @author CPC
		 */
		boolean is32 = "x86".equals(System.getProperty("os.arch"));
		System.out.println(System.getProperty("os.arch"));
		if (is32)
			System.loadLibrary("jdic");
		else
			System.loadLibrary("jdic_x64");
	}
}