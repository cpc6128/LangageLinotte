package org.linotte.web.serveur;

import org.alize.http.i.FichierTraitement;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class FichierVirtuel implements FichierTraitement {

	private File file;

	private String contenu;

	public FichierVirtuel(String pcontenu, File pfile) {
		file = pfile;
		contenu = pcontenu;
	}

	@Override
	public String getAbsolutePath() {
		return "/";
	}

	@Override
	public Long lastModified() {
		return 0L;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public InputStream getInputStream() throws Exception {
		return new ByteArrayInputStream(contenu.getBytes("UTF-8"));
	}

}
