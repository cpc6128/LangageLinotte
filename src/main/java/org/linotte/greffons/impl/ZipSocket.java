package org.linotte.greffons.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

class ZipSocket extends Socket {

	private CompressedBlockInputStream in;
	private CompressedBlockOutputStream out;

	public ZipSocket() {
		super();
	}

	public ZipSocket(String host, int port) throws IOException {
		super(host, port);
	}

	public InputStream getInputStream() throws IOException {
		if (in == null) {
			in = new CompressedBlockInputStream(super.getInputStream());
		}
		return in;
	}

	public OutputStream getOutputStream() throws IOException {
		if (out == null) {
			out = new CompressedBlockOutputStream(super.getOutputStream(), 1024);
		}
		return out;
	}

	public synchronized void close() throws IOException {
		OutputStream o = getOutputStream();
		o.flush();
		super.close();
	}

}
