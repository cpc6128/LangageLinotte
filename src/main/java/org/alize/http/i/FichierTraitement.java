package org.alize.http.i;

import java.io.File;
import java.io.InputStream;

public interface FichierTraitement {

	String getAbsolutePath();

	Long lastModified();

	File getFile();

	InputStream getInputStream() throws Exception;

}
