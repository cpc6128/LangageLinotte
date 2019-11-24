/*******************************************************************************
 * Linotte * Version release date : June 06, 2007 * Author : Mounes Ronan
 * ronan.mounes@amstrad.eu * * http://langagelinotte.free.fr * * This code is
 * released under the GNU GPL license, version 2 or * later, for educational and
 * non-commercial purposes only. * If any part of the code is to be included in
 * a commercial * software, please contact us first for a clearance at *
 * ronan.mounes@amstrad.eu * * This notice must remain intact in all copies of
 * this code. * This code is distributed WITHOUT ANY WARRANTY OF ANY KIND. * The
 * GNU GPL license can be found at : * http://www.gnu.org/copyleft/gpl.html * *
 ******************************************************************************/

// http://kr.sun.com/developers/techtips/core_08_05.html
// https://forja.rediris.es/docman/view.php/92/298/Proyecto%20OpenPipe.%20Dise%C3%B1o%20Conceptual%20Controlador%20MIDI.pdf
package org.linotte.moteur.xml.actions;

import java.util.HashMap;
import java.util.List;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

import org.linotte.moteur.entites.Acteur;
import org.linotte.moteur.entites.Casier;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.outils.SonOutils;
import org.linotte.moteur.xml.alize.kernel.Action;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.IProduitCartesien;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;
import org.linotte.moteur.xml.analyse.ItemXML;

public class SonAction extends Action implements IProduitCartesien {

	private static MidiChannel midiChannel = null;

	// notes :
	private static HashMap<String, Integer> notes = null;

	static {
		notes = new HashMap<String, Integer>();
		notes.put("do", 61);
		notes.put("re", 62);
		notes.put("ré", 62);
		notes.put("mi", 64);
		notes.put("fa", 65);
		notes.put("fa#", 66);
		notes.put("sol", 67);
		notes.put("la", 69);
		notes.put("sib", 70);
		notes.put("si", 71);
		/*
		 * notes.put("Do", 72); notes.put("Do#", 73); notes.put("Re", 74);
		 * notes.put("Mi",76 );
		 */
	}

	public SonAction() {
		super();
	}

	@Override
	public String clef() {
		return "verbe jouer";
	}

	// @SuppressWarnings("unchecked")
	@Override
	public ETAT analyse(String param, Job job, ItemXML[] valeurs, String[] annotations) throws Exception {

		//		linotte.debug("On joue un son");
		RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
		if (runtimeContext.canDo(Habilitation.LITTLE_BIRD)) {
			throw new ErreurException(Constantes.MODE_CONSOLE);			
		}			

		Acteur a = extractionDesActeurs(Constantes.SYNTAXE_PARAMETRE_JOUER, job, valeurs)[0];

		if (a.getRole() == Role.TEXTE) {
			Integer note = notes.get(((String) a.getValeur()).toLowerCase());
			if (note != null)
				playNote(note);
			else
				SonOutils.jouerSon((String) a.getValeur());
			// throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_JOUER, a
			// .toString());
		} else {
			if (a.getRole() == Role.CASIER) {
				Casier casier = (Casier) a;
				if (casier.roleContenant() == Role.TEXTE) {
					List<Acteur> a_jouer = casier.extraireTout();
					for (Acteur string : a_jouer) {
						Integer note = notes.get(((String) string.getValeur()).toLowerCase());
						if (note != null)
							playNote(note);
						else
							SonOutils.jouerSon((String) string.getValeur());
						// throw new ErreurException(
						// Constantes.SYNTAXE_PARAMETRE_JOUER, a
						// .toString());
					}
				} else
					throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_JOUER, a.toString());
			} else
				throw new ErreurException(Constantes.SYNTAXE_PARAMETRE_JOUER, a.toString());
		}

		return ETAT.PAS_DE_CHANGEMENT;
	}

	public void playNote(int note) {
		if (getChannel() == null) {
			System.out.println("Le son ne marche pas sur votre système...");
			return;
		}
		// System.out.print(note);
		getChannel().noteOn(note, 70);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		getChannel().noteOff(note, 70);
	}

	public MidiChannel getChannel() {
		if (midiChannel == null) {
			try {
				Synthesizer synth = MidiSystem.getSynthesizer();
				synth.open();
				midiChannel = synth.getChannels()[0];
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return midiChannel;
	}

	public static void closeChannel() {
		if (midiChannel != null) {
			try {
				MidiSystem.getSynthesizer().close();
			} catch (MidiUnavailableException e) {
				e.printStackTrace();
			}
			midiChannel = null;
		}
	}

}
