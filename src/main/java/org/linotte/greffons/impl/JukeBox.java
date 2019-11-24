package org.linotte.greffons.impl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javazoom.jl.player.Player;

import org.linotte.greffons.externe.Greffon;

/**
 * 
 * http://stackoverflow.com/questions/12057214/jlayer-pause-and-resume-song
 * 
 * Greffon pour jouer des fichiers MP3
 * 
 * @author Josh M
 *
 */
public class JukeBox extends Greffon {

	private Player player;
	private FileInputStream fis;
	private BufferedInputStream bis;
	private boolean canResume = false;
	private int total = 0;
	private int stopped = 0;
	private boolean valid = false;

	public boolean canResume() {
		return canResume;
	}

	@Slot(nom = "pause")
	public void pause() {
		try {
			stopped = fis.available();
			player.close();
			fis = null;
			bis = null;
			player = null;
			if (valid)
				canResume = true;
		} catch (Exception e) {

		}
	}

	@Slot(nom = "arrêter")
	public void stop() {
		try {
			stopped = fis.available();
			player.close();
			fis = null;
			bis = null;
			player = null;
			canResume = false;
		} catch (Exception e) {

		}
	}

	@Slot(nom = "continuer")
	public boolean resume() throws GreffonException {
		if (!canResume)
			return false;
		return play(total - stopped);
	}

	@Slot(nom = "jouer")
	public boolean play() throws GreffonException {
		return play(-1);
	}

	@Slot(nom = "taille")
	public int taille() {
		return total;
	}

	@Slot(nom = "position")
	public int position() {
		try {
			if (valid)
				return fis.available();
			else
				return 0;
		} catch (Exception e) {
			return 0;
		}
	}

	private boolean play(int pos) throws GreffonException {
		valid = true;
		canResume = false;
		try {
			fis = new FileInputStream(getAttributeAsString("fichier"));
			total = fis.available();
			if (pos > -1)
				fis.skip(pos);
			bis = new BufferedInputStream(fis);
			player = new Player(bis);
			player.play();
		} catch (Exception e) {
			e.printStackTrace();
			valid = false;
			throw new GreffonException("impossible de lire le fichier");
		}
		return valid;
	}

}