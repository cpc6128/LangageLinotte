package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.DocumentationHTML;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;
import java.util.*;

@DocumentationHTML(""
		+ "Le greffon <i>\u266A\u266A\u266A Métronome </i> est une espèce offrant la possibilité d'exploiter les capacités MIDI de votre ordinateur.<br>"
		+ "<u>Les méthodes fonctionnelles proposées par l'espèce Métronome sont:</u><br>"
		+ "<ul>"
		+ "<li>métronome.instruments() : Retourne un casier de textes contenant la liste de tous les intrusments disponibles</li>"
		+ "<li>métronome.instrument(instrument&lt;texte) : Demande de jouer l'instrument sur le canal courant</li>"
		+ "<li>métronome.joue(note&lt;nombre>, &lt;nombre>) : Jouer la note (numéro midi) pendant le temps délai.</li>"
		+ "<li>métronome.pressure(pression&lt;nombre>) : Modification de la pression (entre 0 et 127).</li>"
		+ "<li>métronome.reverb(reverb&lt;nombre>) : Modification de la réverbération (entre 0 et 127).</li>"
		+ "<li>métronome.bend(bend&lt;nombre>) : Modification du pitch bend (entre 0 et 16383).</li>"
		+ "<li>métronome.nombrechannels() : Retourner le nombre de canaux disponibles.</li>"
		+ "<li>métronome.canal(canal&lt;nombre>) : Changer le canal.</li>"
		+ "<li>métronome.fermer() : Libérer les ressources MIDI.</li>"
		+ "</ul>"
		+ "La liste de notes midi est disponible sur ce site : <a href=http://www.phys.unsw.edu.au/jw/notes.html>http://www.phys.unsw.edu.au/jw/notes.html</a>. Par exemple, do = 60")
public class Metronome extends Greffon {

	final int REVERB = 91;

	private static Synthesizer synthesizer;
	private static List<Instrument> instruments = null;

	private static Map<String, Instrument> instrumentsMap = new HashMap<String, Instrument>();

	private static MidiChannel channels[];
	private MidiChannel canal;

	private int velocity = 64;

	public Metronome() {
	}

	@Slot()
	public List<String> instruments() {
		init();
		List<String> retour = new ArrayList<String>(instruments.size());
		if (instruments != null)
			for (Instrument instrument : instruments) {
				retour.add(instrument.getName());
			}
		return retour;
	}

	@Slot()
	public boolean joue(int kNum, int delai) {
		init();
		try {
			canal.noteOn(kNum, velocity);
			Thread.sleep(delai);
		} catch (InterruptedException e) {
		} finally {
			canal.noteOff(kNum);
		}
		return true;
	}

	@Slot()
	public boolean pressure(int value) {
		init();
		canal.setChannelPressure(value);
		return true;
	}

	@Slot()
	public boolean reverb(int value) {
		init();
		canal.controlChange(REVERB, value);
		return true;
	}

	@Slot()
	public boolean bend(int value) {
		init();
		canal.setPitchBend(value);
		return true;
	}

	@Slot()
	public boolean velocity(int value) {
		init();
		velocity = value;
		return true;
	}

	@Slot()
	public int nombrechannels() {
		init();
		return channels.length - 1;
	}

	@Slot()
	public boolean canal(int value) {
		init();
		MidiChannel neww = channels[value];
		neww.setPitchBend(canal.getPitchBend());
		neww.controlChange(REVERB, canal.getController(REVERB));
		neww.setChannelPressure(canal.getChannelPressure());
		canal = neww;
		return true;
	}

	@Slot()
	public boolean instrument(String instrument) {
		init();
		Instrument i = instrumentsMap.get(instrument);
		if (i == null)
			i = instruments.get(0);
		canal.programChange(i.getPatch().getProgram());
		if (i != null)
			System.out.println("\u266A  instrument new = " + i.getName());
		return true;
	}

	@Slot()
	public void fermer() {
		if (synthesizer != null) {
			synthesizer.close();
		}
		synthesizer = null;
		instruments = null;
		channels = null;
	}

	private void init() {
		if (synthesizer == null) {
			try {
				// Analyse du système :
				for (Info info : MidiSystem.getMidiDeviceInfo()) {
					System.out.println("\u266A  ****************");
					System.out.println("\u266A  " + info.getName());
					System.out.println("\u266A  " + info.getDescription());
					System.out.println("\u266A  " + info.getVendor());
					System.out.println("\u266A  " + info.getVersion());
					System.out.println("\u266A  " + "________________");
				}

				if ((synthesizer = MidiSystem.getSynthesizer()) == null) {
					System.out.println("\u266A  " + "getSynthesizer() failed !");
					return;
				}
				synthesizer.open();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			Info infoDevice = synthesizer.getDeviceInfo();
			if (infoDevice != null) {
				System.out.println("\u266A  Device chargé : ");
				System.out.println("\u266A  " + infoDevice.getName());
				System.out.println("\u266A  " + infoDevice.getDescription());
				System.out.println("\u266A  " + infoDevice.getVendor());
				System.out.println("\u266A  " + infoDevice.getVersion());
				System.out.println("\u266A  " + "________________");
			} else {
				System.out.println("\u266A  " + "Pas de device....");
			}

			Soundbank sb = synthesizer.getDefaultSoundbank();
			if (sb != null) {
				instruments = Arrays.asList(sb.getInstruments());
				for (Instrument i : instruments) {
					instrumentsMap.put(i.getName(), i);
					System.out.println("\u266A  " + "instrument chargé : " + i.getName());
				}
				System.out.println("\u266A  " + instruments.size() + " instruments chargés.");
			} else {
				System.out.println("\u266A  " + "Pas d'instrument !");
			}
			MidiChannel midiChannels[] = synthesizer.getChannels();
			channels = new MidiChannel[midiChannels.length];
			for (int i = 0; i < channels.length; i++) {
				channels[i] = midiChannels[i];
			}
			canal = channels[0];
			canal.programChange(0);
		} else {
			if (canal == null)
				canal = channels[0];

		}
	}

}
