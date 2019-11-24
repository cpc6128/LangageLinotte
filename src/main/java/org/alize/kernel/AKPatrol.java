/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 01, 2010                                *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                        *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                            *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.alize.kernel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.alize.kernel.util.i.AKAudit;
import org.alize.kernel.util.i.AKAuditLog;

/**
 * Cette classe fait partie de la réécriture du moteur de Linotte.
 * Le moteur Alizé doit alléger le code de Linotte, booster la vitesse de l'interprète<br>
 * <br>
 * AlizeRuntime est le moteur d'exécution des états (analyse sémantique).<br>
 * Il ne doit plus contenir de grammaire.
 * Il est produit par MoteurXML (analyse syntaxique)
 */
public final class AKPatrol {

	private Timer timer = null;

	public static boolean active = false;

	public static List<AKRuntime> runtimes = new ArrayList<AKRuntime>();

	public static List<AKAudit> auditeurs = new ArrayList<AKAudit>();

	private static AKPatrol akPatrol;

	public static AKAuditLog auditLog;

	private AKPatrol() {

		timer = new Timer("AKPatrol", false);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (active) {
					LOG_INIT();
					for (AKAudit audit : auditeurs) {
						try {
							audit.run();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		timer.schedule(task, 1000, 1000);
	}

	static void addRuntime(AKRuntime runtime) {
		getPatrol();
		AKPatrol.runtimes.add(runtime);
	}

	private static synchronized void getPatrol() {
		if (akPatrol == null) {
			akPatrol = new AKPatrol();
		}
	}

	public static void LOG_INIT() {
		if (auditLog != null)
			auditLog.init();
	}

	public static void LOG(Object audit, String message) {
		String out = "<" + audit.getClass().getSimpleName() + ">\t\t" + message + "\n";
		if (auditLog == null)
			System.out.println(out);
		else
			auditLog.print(out);
	}
}
