/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                       *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                           *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.frame.atelier;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Updater extends Thread {

	public interface SuccessAction {

		public void testOK(String message);

	}

	private String version;
	private String url;
	private SuccessAction action;

	public Updater(String version, String url, SuccessAction action) {
		this.version = version;
		this.url = url;
		this.action = action;
		this.start();
	}

	private String verifierVersion() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}

		String retour = null;
		// 1 = mise à jour
		// 0 = pas de mise à jour
		// -1 = erreur de détection.

		try {
			URLConnection conn = new URL(url).openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			final int MAX_LENGTH = 200;
			byte[] buf = new byte[MAX_LENGTH];
			int total = 0;
			while (total < MAX_LENGTH) {
				int count = is.read(buf, total, MAX_LENGTH - total);
				if (count < 0) {
					break;
				}
				total += count;
			}
			is.close();

			String reply = new String(buf, 0, total);

			// reply = "VL.00.00.10.00.Message";

			Pattern pattern = Pattern.compile("VL.[0-9][0-9].[0-9][0-9].[0-9][0-9].[0-9][0-9]");
			Matcher m = pattern.matcher(reply.substring(0, 14));
			boolean b = m.matches();
			if (b) {
				if (reply.substring(0, 14).equals(version))
					retour = null;
				else {
					retour = reply.substring(15);
				}
			} else
				retour = null;

		} catch (MalformedURLException e) {
		} catch (IOException e) {
		}

		if (retour != null) {
			action.testOK(retour);
		}

		return retour;

	}

	@Override
	public void run() {
		verifierVersion();
	}

}
