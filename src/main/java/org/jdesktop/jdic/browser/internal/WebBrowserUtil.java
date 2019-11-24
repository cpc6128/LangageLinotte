package org.jdesktop.jdic.browser.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.jdesktop.jdic.init.InitUtility;
import org.jdesktop.jdic.init.JdicInitException;
import org.jdesktop.jdic.init.JdicManager;

public class WebBrowserUtil {
	public static final String OS_NAME = System.getProperty("os.name");
	public static final String JAVA_DOT_HOME = "java.home";
	public static final boolean IS_OS_WINDOWS = isCurrentOS("Windows");
	public static final boolean IS_OS_LINUX = isCurrentOS("Linux");
	public static final boolean IS_OS_SUNOS = isCurrentOS("SunOS");
	public static final boolean IS_OS_FREEBSD = isCurrentOS("FreeBSD");
	public static final boolean IS_OS_MAC = isCurrentOS("Mac");
	private static final String JDIC_LIB_NAME = "jdic";
	private static final String LD_LIBRARY_PATH = "LD_LIBRARY_PATH";
	private static final String PATH = "PATH";
	public static final String LIB_PATH_ENV = IS_OS_WINDOWS ? "PATH" : "LD_LIBRARY_PATH";
	private static String browserPath = null;
	private static boolean nativeLibLoaded = false;
	private static boolean isDebugOn = false;

	private static native String nativeGetBrowserPath();

	private static native String nativeGetMozillaGreHome();

	private static native void nativeSetEnv();

	@SuppressWarnings("unchecked")
	public static void loadLibrary() {
		if (!nativeLibLoaded) {
			try {
				JdicManager.getManager().initShareNative();
			} catch (JdicInitException localJdicInitException) {
				localJdicInitException.printStackTrace();
				error(localJdicInitException.getMessage());
			}
			AccessController.doPrivileged(new PrivilegedAction() {
				public Object run() {
					InitUtility.initDLL();
					return null;
				}
			});
			nativeLibLoaded = true;
		}
	}

	public static String getDefaultBrowserPath() {
		if (browserPath == null) {
			loadLibrary();
			browserPath = nativeGetBrowserPath();
		}
		return browserPath;
	}

	public static String getMozillaGreHome() {
		loadLibrary();
		return nativeGetMozillaGreHome();
	}

	public static boolean isCurrentOS(String paramString) {
		if (paramString == null) {
			return false;
		}
		return OS_NAME.indexOf(paramString) >= 0;
	}

	public static void trace(String paramString) {
		if (isDebugOn) {
			System.out.println("*** Jtrace: " + paramString);
		}
	}

	public static void error(String paramString) {
		System.err.println("*** Error: " + paramString);
	}

	public static void enableDebugMessages(boolean paramBoolean) {
		isDebugOn = paramBoolean;
	}

	public static boolean getDebug() {
		return isDebugOn;
	}

	public static void nativeSetEnvironment() {
		loadLibrary();
		nativeSetEnv();
	}

	public static void copyIsToOs(InputStream paramInputStream, OutputStream paramOutputStream) {
		int i = 1024;
		byte[] arrayOfByte = new byte[i];
		int j = 0;
		try {
			while ((j = paramInputStream.read(arrayOfByte, 0, i)) > 0) {
				paramOutputStream.write(arrayOfByte, 0, j);
			}
		} catch (IOException localIOException1) {
			error(localIOException1.getMessage());
			localIOException1.printStackTrace();
		} finally {
			try {
				paramOutputStream.flush();
				paramOutputStream.close();
				paramInputStream.close();
			} catch (IOException localIOException2) {
				error(localIOException2.getMessage());
				localIOException2.printStackTrace();
			}
		}
	}
}