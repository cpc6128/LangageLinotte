package org.linotte.moteur.xml.alize.kernel;

import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.xml.analyse.ItemXML;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Trace {

	private static Logger logger = Logger.getLogger(Trace.class.getName());
	private FileHandler fileHandler;

	public static boolean active = false;

	private static Trace moi;

	public static synchronized Trace getInstance() {
		if (moi == null) {
			moi = new Trace();
		}
		return moi;
	}

	private Trace() {
		try {
			LogManager.getLogManager().reset();
			fileHandler = new FileHandler(Preference.getIntance().getHome() + File.separator + Preference.REPERTOIRE + File.separator + "trace.log", false);
			final SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss:SSS");
			fileHandler.setFormatter(new Formatter() {
				@Override
				public String format(LogRecord record) {
					StringBuilder builder = new StringBuilder("");
					builder.append(format.format(new Date())).append(":").append("alize.kernel:").append(record.getMessage()).append("\n");
					return builder.toString();
				}
			});
			// On supprime les handlers existant :
			for (Handler defaul : logger.getHandlers()) {
				logger.removeHandler(defaul);
			}
			logger.addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String toString(ItemXML[] ligne) {
		StringBuilder builder = new StringBuilder();
		if (ligne != null) {
			for (ItemXML itemXML : ligne) {
				if (itemXML != null) {
					if (itemXML.nom_acteur != null)
						builder.append(":").append(itemXML.nom_acteur).append("=").append(itemXML.getValeurBrute());
					else
						builder.append(":").append(itemXML.getValeurBrute());
				}
			}
		}
		return builder.toString();
	}

	/**
	 * @param etat 
	 * @param position 
	 * @param job 
	 * @param ligne
	 */
	public void debug(int position, Action etat, Job job, ItemXML[] ligne, String retour) {
		ecrire("pos=" + position + ":" + job.toString() + ":action=" + etat.clef() + Trace.toString(ligne) + (retour == null ? "" : "-->" + retour));
	}

	private void ecrire(String string) {
		logger.info(string);
	}

}
