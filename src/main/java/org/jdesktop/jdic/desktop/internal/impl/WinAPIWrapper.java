package org.jdesktop.jdic.desktop.internal.impl;

import org.jdesktop.jdic.init.InitUtility;

public class WinAPIWrapper {
	public static final int HKEY_CLASSES_ROOT = -2147483648;
	public static final int HKEY_CURRENT_USER = -2147483647;
	public static final int HKEY_LOCAL_MACHINE = -2147483646;
	public static final int ERROR_SUCCESS = 0;
	public static final int MAX_KEY_LENGTH = 255;
	private static final int OPENED_KEY_HANDLE = 0;
	private static final int ERROR_CODE = 1;
	private static final int SUBKEYS_NUMBER = 0;
	public static final int KEY_READ = 131097;

	private static native int[] RegOpenKey(int paramInt1, byte[] paramArrayOfByte, int paramInt2);

	private static native int RegCloseKey(int paramInt);

	private static native byte[] RegQueryValueEx(int paramInt, byte[] paramArrayOfByte);

	private static native byte[] AssocQueryString(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

	private static native byte[] ExpandEnvironmentStrings(byte[] paramArrayOfByte);

	private static native String resolveLinkFile(byte[] paramArrayOfByte);

	private static native int shellExecute(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

	private static synchronized native void openMapiMailer(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3,
			String paramString1, String paramString2, String[] paramArrayOfString4);

	protected static native void shutDown();

	private static native boolean nativeBrowseURLInIE(String paramString1, String paramString2);

	private static byte[] stringToByteArray(String paramString) {
		if (paramString == null) {
			return null;
		}
		byte[] arrayOfByte1 = paramString.getBytes();
		int i = arrayOfByte1.length;
		byte[] arrayOfByte2 = new byte[i + 1];
		System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, i);
		arrayOfByte2[i] = 0;
		return arrayOfByte2;
	}

	private static String byteArrayToString(byte[] paramArrayOfByte) {
		if (paramArrayOfByte != null) {
			String str = new String(paramArrayOfByte);
			if (str != null) {
				return str.substring(0, str.length() - 1);
			}
		}
		return null;
	}

	public static String WinRegQueryValueEx(int paramInt, String paramString1, String paramString2) {
		byte[] arrayOfByte1 = stringToByteArray(paramString1);
		int[] arrayOfInt = RegOpenKey(paramInt, arrayOfByte1, 131097);
		if (arrayOfInt == null) {
			return null;
		}
		if (arrayOfInt[1] != 0) {
			return null;
		}
		byte[] arrayOfByte3 = stringToByteArray(paramString2);
		byte[] arrayOfByte2 = RegQueryValueEx(arrayOfInt[0], arrayOfByte3);
		RegCloseKey(arrayOfInt[0]);
		if (arrayOfByte2 != null) {
			if ((arrayOfByte2.length == 1) && (arrayOfByte2[0] == 0) && (paramString2.equals(""))) {
				return null;
			}
			return byteArrayToString(arrayOfByte2);
		}
		return null;
	}

	public static String WinAssocQueryString(String paramString1, String paramString2) {
		byte[] arrayOfByte1 = stringToByteArray(paramString1);
		byte[] arrayOfByte2 = stringToByteArray(paramString2);
		byte[] arrayOfByte3 = AssocQueryString(arrayOfByte1, arrayOfByte2);
		if (arrayOfByte3 != null) {
			if ((arrayOfByte3.length == 1) && (arrayOfByte3[0] == 0) && (arrayOfByte3.equals(""))) {
				return null;
			}
			return byteArrayToString(arrayOfByte3);
		}
		return null;
	}

	public static String WinResolveLinkFile(String paramString) {
		byte[] arrayOfByte = stringToByteArray(paramString);
		return resolveLinkFile(arrayOfByte);
	}

	public static boolean WinShellExecute(String paramString1, String paramString2) {
		byte[] arrayOfByte1 = stringToByteArray(paramString1);
		byte[] arrayOfByte2 = stringToByteArray(paramString2);
		int i = shellExecute(arrayOfByte1, arrayOfByte2);
		return i > 32;
	}

	public static boolean WinBrowseURLInIE(String paramString1, String paramString2) {
		return nativeBrowseURLInIE(paramString1, paramString2);
	}

	public static synchronized void WinOpenMapiMailer(String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3,
			String paramString1, String paramString2, String[] paramArrayOfString4) {
		openMapiMailer(paramArrayOfString1, paramArrayOfString2, paramArrayOfString3, paramString1, paramString2, paramArrayOfString4);
	}

	static {
		InitUtility.initDLL();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
			}
		});
	}
}