package org.linotte.web.serveur;

import org.alize.http.i.FichierTraitement;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FichierDisque implements FichierTraitement {

	private File file;

	public FichierDisque(File pfile) {
		file = pfile;
	}

	@Override
	public String getAbsolutePath() {
		return file.getAbsolutePath();
	}

	@Override
	public Long lastModified() {
		return file.lastModified();
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public InputStream getInputStream() throws Exception {
		return new FileInputStream(file);
	}

}
