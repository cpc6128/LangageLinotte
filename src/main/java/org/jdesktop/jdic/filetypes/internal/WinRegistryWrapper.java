package org.jdesktop.jdic.filetypes.internal;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;

import org.jdesktop.jdic.init.InitUtility;

public class WinRegistryWrapper {
	public static final int HKEY_CLASSES_ROOT = -2147483648;
	public static final int HKEY_CURRENT_USER = -2147483647;
	public static final int HKEY_LOCAL_MACHINE = -2147483646;
	public static final int HKEY_USERS = -2147483645;
	public static final int HKEY_CURRENT_CONFIG = -2147483643;
	public static final int ERROR_SUCCESS = 0;
	public static final int ERROR_FILE_NOT_FOUND = 2;
	public static final int ERROR_ACCESS_DENIED = 5;
	public static final int ERROR_ITEM_EXIST = 0;
	public static final int ERROR_ITEM_NOTEXIST = 9;
	public static final int MAX_KEY_LENGTH = 255;
	public static final int MAX_VALUE_NAME_LENGTH = 255;
	private static final int OPENED_KEY_HANDLE = 0;
	private static final int ERROR_CODE = 1;
	private static final int SUBKEYS_NUMBER = 0;
	private static final int VALUES_NUMBER = 2;
	public static final int DELETE = 65536;
	public static final int KEY_QUERY_VALUE = 1;
	public static final int KEY_SET_VALUE = 2;
	public static final int KEY_CREATE_SUB_KEY = 4;
	public static final int KEY_ENUMERATE_SUB_KEYS = 8;
	public static final int KEY_READ = 131097;
	public static final int KEY_WRITE = 131078;
	public static final int KEY_ALL_ACCESS = 983103;

	private static native int[] RegOpenKey(int paramInt1, byte[] paramArrayOfByte, int paramInt2);

	private static native int RegCloseKey(int paramInt);

	private static native int[] RegCreateKeyEx(int paramInt, byte[] paramArrayOfByte);

	private static native int RegDeleteKey(int paramInt, byte[] paramArrayOfByte);

	private static native int RegFlushKey(int paramInt);

	private static native byte[] RegQueryValueEx(int paramInt, byte[] paramArrayOfByte);

	private static native int RegSetValueEx(int paramInt, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

	private static native int RegDeleteValue(int paramInt, byte[] paramArrayOfByte);

	private static native int[] RegQueryInfoKey(int paramInt);

	private static native byte[] RegEnumKeyEx(int paramInt1, int paramInt2, int paramInt3);

	private static native byte[] RegEnumValue(int paramInt1, int paramInt2, int paramInt3);

	private static native byte[] FindMimeFromData(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

	private static native byte[] ExpandEnvironmentStrings(byte[] paramArrayOfByte);

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

	public static int WinRegCreateKeyEx(int paramInt, String paramString) {
		byte[] arrayOfByte = stringToByteArray(paramString);
		int[] arrayOfInt = RegCreateKeyEx(paramInt, arrayOfByte);
		if (arrayOfInt == null) {
			return -1;
		}
		if (arrayOfInt[1] == 0) {
			RegCloseKey(arrayOfInt[0]);
		}
		return arrayOfInt[1];
	}

	public static int WinRegDeleteKey(int paramInt, String paramString) {
		byte[] arrayOfByte = stringToByteArray(paramString);
		int i = RegDeleteKey(paramInt, arrayOfByte);
		if (i == 0) {
			return i;
		}
		int j = WinRegSubKeyExist(paramInt, paramString);
		if (j == 9) {
			return i;
		}
		String[] arrayOfString = WinRegGetSubKeys(paramInt, paramString, 255);
		for (int k = 0; k < arrayOfString.length; k++) {
			String str = paramString + "\\" + arrayOfString[k];
			if (str != null) {
				WinRegDeleteKey(paramInt, str);
			}
		}
		i = RegDeleteKey(paramInt, arrayOfByte);
		return i;
	}

	public static int WinRegFlushKey(int paramInt, String paramString) {
		byte[] arrayOfByte = stringToByteArray(paramString);
		int[] arrayOfInt = RegOpenKey(paramInt, arrayOfByte, 131078);
		if (arrayOfInt == null) {
			return -1;
		}
		if (arrayOfInt[1] != 0) {
			return arrayOfInt[1];
		}
		int i = RegFlushKey(arrayOfInt[0]);
		RegCloseKey(arrayOfInt[0]);
		return i;
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

	public static int WinRegSetValueEx(int paramInt, String paramString1, String paramString2, String paramString3) {
		byte[] arrayOfByte1 = stringToByteArray(paramString1);
		int[] arrayOfInt = RegOpenKey(paramInt, arrayOfByte1, 2);
		if (arrayOfInt == null) {
			return -1;
		}
		if (arrayOfInt[1] != 0) {
			return arrayOfInt[1];
		}
		byte[] arrayOfByte2 = stringToByteArray(paramString2);
		byte[] arrayOfByte3 = stringToByteArray(paramString3);
		int i = RegSetValueEx(arrayOfInt[0], arrayOfByte2, arrayOfByte3);
		RegCloseKey(arrayOfInt[0]);
		return i;
	}

	public static int WinRegDeleteValue(int paramInt, String paramString1, String paramString2) {
		byte[] arrayOfByte1 = stringToByteArray(paramString1);
		int[] arrayOfInt = RegOpenKey(paramInt, arrayOfByte1, 131078);
		if (arrayOfInt == null) {
			return -1;
		}
		if (arrayOfInt[1] != 0) {
			return arrayOfInt[1];
		}
		byte[] arrayOfByte2 = stringToByteArray(paramString2);
		int i = RegDeleteValue(arrayOfInt[0], arrayOfByte2);
		RegCloseKey(arrayOfInt[0]);
		return i;
	}

	public static int[] WinRegQueryInfoKey(int paramInt, String paramString) {
		byte[] arrayOfByte = stringToByteArray(paramString);
		int[] arrayOfInt1 = RegOpenKey(paramInt, arrayOfByte, 131097);
		if (arrayOfInt1 == null) {
			return null;
		}
		if (arrayOfInt1[1] != 0) {
			return arrayOfInt1;
		}
		int[] arrayOfInt2 = RegQueryInfoKey(arrayOfInt1[0]);
		RegCloseKey(arrayOfInt1[0]);
		return arrayOfInt2;
	}

	public static String WinRegEnumKeyEx(int paramInt1, String paramString, int paramInt2, int paramInt3) {
		byte[] arrayOfByte1 = stringToByteArray(paramString);
		int[] arrayOfInt = RegOpenKey(paramInt1, arrayOfByte1, 131097);
		if (arrayOfInt == null) {
			return null;
		}
		if (arrayOfInt[1] != 0) {
			return null;
		}
		byte[] arrayOfByte2 = RegEnumKeyEx(arrayOfInt[0], paramInt2, paramInt3);
		RegCloseKey(arrayOfInt[0]);
		if (arrayOfByte2 != null) {
			return byteArrayToString(arrayOfByte2);
		}
		return null;
	}

	public static String WinRegEnumValue(int paramInt1, String paramString, int paramInt2, int paramInt3) {
		byte[] arrayOfByte1 = stringToByteArray(paramString);
		int[] arrayOfInt = RegOpenKey(paramInt1, arrayOfByte1, 131097);
		if (arrayOfInt == null) {
			return null;
		}
		if (arrayOfInt[1] != 0) {
			return null;
		}
		byte[] arrayOfByte2 = RegEnumValue(arrayOfInt[0], paramInt2, paramInt3);
		RegCloseKey(arrayOfInt[0]);
		if (arrayOfByte2 != null) {
			return byteArrayToString(arrayOfByte2);
		}
		return null;
	}

	public static String[] WinRegGetSubKeys(int paramInt1, String paramString, int paramInt2) {
		byte[] arrayOfByte1 = stringToByteArray(paramString);
		int[] arrayOfInt1 = RegOpenKey(paramInt1, arrayOfByte1, 131097);
		if (arrayOfInt1 == null) {
			return null;
		}
		if (arrayOfInt1[1] != 0) {
			return null;
		}
		int[] arrayOfInt2 = RegQueryInfoKey(arrayOfInt1[0]);
		int i = arrayOfInt2[0];
		if (i == 0) {
			RegCloseKey(arrayOfInt1[0]);
			return null;
		}
		String[] arrayOfString = new String[i];
		for (int j = 0; j < i; j++) {
			byte[] arrayOfByte2 = RegEnumKeyEx(arrayOfInt1[0], j, paramInt2);
			arrayOfString[j] = byteArrayToString(arrayOfByte2);
		}
		RegCloseKey(arrayOfInt1[0]);
		return arrayOfString;
	}

	public static String[] WinRegGetValues(int paramInt1, String paramString, int paramInt2) {
		byte[] arrayOfByte1 = stringToByteArray(paramString);
		int[] arrayOfInt1 = RegOpenKey(paramInt1, arrayOfByte1, 131097);
		if (arrayOfInt1 == null) {
			return null;
		}
		if (arrayOfInt1[1] != 0) {
			return null;
		}
		int[] arrayOfInt2 = RegQueryInfoKey(arrayOfInt1[0]);
		int i = arrayOfInt2[2];
		if (i == 0) {
			RegCloseKey(arrayOfInt1[0]);
			return null;
		}
		String[] arrayOfString = new String[i];
		for (int j = 0; j < i; j++) {
			byte[] arrayOfByte2 = RegEnumValue(arrayOfInt1[0], j, paramInt2);
			arrayOfString[j] = byteArrayToString(arrayOfByte2);
		}
		RegCloseKey(arrayOfInt1[0]);
		return arrayOfString;
	}

	public static int WinRegSubKeyExist(int paramInt, String paramString) {
		byte[] arrayOfByte = stringToByteArray(paramString);
		int[] arrayOfInt = RegOpenKey(paramInt, arrayOfByte, 131097);
		if (arrayOfInt == null) {
			return 9;
		}
		if (arrayOfInt[1] != 0) {
			return 9;
		}
		RegCloseKey(arrayOfInt[0]);
		return 0;
	}

	public static int WinRegValueExist(int paramInt, String paramString1, String paramString2) {
		if (paramString1.trim().equals("")) {
			return 9;
		}
		byte[] arrayOfByte1 = stringToByteArray(paramString1);
		int[] arrayOfInt = RegOpenKey(paramInt, arrayOfByte1, 131097);
		if (arrayOfInt == null) {
			return 9;
		}
		if (arrayOfInt[1] != 0) {
			return 9;
		}
		byte[] arrayOfByte2 = stringToByteArray(paramString2);
		byte[] arrayOfByte3 = RegQueryValueEx(arrayOfInt[0], arrayOfByte2);
		RegCloseKey(arrayOfInt[0]);
		if (arrayOfByte3 == null) {
			return 9;
		}
		if ((arrayOfByte3.length == 1) && (arrayOfByte3[0] == 0) && (paramString2.equals(""))) {
			return 9;
		}
		return 0;
	}

	public static String WinFindMimeFromData(URL paramURL) {
		String str1 = paramURL.toString();
		byte[] arrayOfByte1 = stringToByteArray(str1);
		byte[] arrayOfByte2 = FindMimeFromData(arrayOfByte1, null);
		if (arrayOfByte2 != null) {
			return byteArrayToString(arrayOfByte2);
		}
		byte[] arrayOfByte3 = new byte[256];
		DataInputStream localDataInputStream = null;
		try {
			localDataInputStream = new DataInputStream(paramURL.openStream());
			localDataInputStream.read(arrayOfByte3, 0, 256);
			localDataInputStream.close();
		} catch (IOException localIOException1) {
			String str2 = null;
			return str2;
		} finally {
			if (localDataInputStream != null) {
				try {
					localDataInputStream.close();
				} catch (IOException localIOException2) {
				}
			}
		}
		arrayOfByte2 = FindMimeFromData(null, arrayOfByte3);
		if (arrayOfByte2 != null) {
			return byteArrayToString(arrayOfByte2);
		}
		return null;
	}

	public static String WinExpandEnvironmentStrings(String paramString) {
		byte[] arrayOfByte1 = stringToByteArray(paramString);
		byte[] arrayOfByte2 = ExpandEnvironmentStrings(arrayOfByte1);
		return byteArrayToString(arrayOfByte2);
	}

	static {
		InitUtility.initDLL();
	}
}