/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                             *
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

package org.linotte.moteur.outils;

import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.File;
import java.io.IOException;
import java.net.URL;

//http://www.jsresources.org/examples/AudioPlayer.html

public class WaveFile extends Thread {
	private Clip m_clip;
	private SourceDataLine m_source_DataLine;
	private AudioInputStream m_stream;

	private static int DEFAULT_EXTERNAL_BUFFER_SIZE = 128000;
	private static int nExternalBufferSize = DEFAULT_EXTERNAL_BUFFER_SIZE;

	/** constructor */
	public WaveFile(String path) {
		try {
			if (Ressources.getInstance().isInternetUrl(path)) {
				URL url = new URL(path);
				m_stream = AudioSystem.getAudioInputStream(url);
			} else {
				File file = new File(Ressources.construireChemin(path));
				m_stream = AudioSystem.getAudioInputStream(file);
			}

			int nSampleSizeInBits = 16;
			boolean bBigEndian = false;
			int nInternalBufferSize = AudioSystem.NOT_SPECIFIED;
			String strMixerName = null;

			AudioFormat format = m_stream.getFormat();
			Info info = new Info(Clip.class, format, ((int) m_stream.getFrameLength() * format.getFrameSize()));
			boolean bIsSupportedDirectly = AudioSystem.isLineSupported(info);

			if (!bIsSupportedDirectly) {
				AudioFormat sourceFormat = format;
				AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sourceFormat.getSampleRate(), nSampleSizeInBits,
						sourceFormat.getChannels(), sourceFormat.getChannels() * (nSampleSizeInBits / 8), sourceFormat.getSampleRate(), bBigEndian);
				m_stream = AudioSystem.getAudioInputStream(targetFormat, m_stream);
				format = m_stream.getFormat();
				m_source_DataLine = getSourceDataLine(strMixerName, format, nInternalBufferSize);
			} else {
				m_clip = (Clip) AudioSystem.getLine(info);
				m_clip.open(m_stream);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		playSound();
	}

	void play() {
		super.start();
	}

	/** play the clip synchronously */
	void playSound() {
		if (m_clip != null)
			try {
				m_clip.setFramePosition(0);
				m_clip.start();

				while (m_clip.isActive()) {
					Thread.sleep(100);
				}

				Thread.sleep(250);
				m_clip.stop();
				m_clip.close();
				m_stream.close();

				Thread[] threads = new Thread[Thread.activeCount()];
				Thread.enumerate(threads);

				for (int i = 0; i < threads.length; i++) {
					if (threads[i].getName().equalsIgnoreCase("Java Sound event dispatcher")) {
						threads[i].interrupt();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		if (m_source_DataLine != null) {
			m_source_DataLine.start();

			/*
			 *	Ok, finally the line is prepared. Now comes the real
			 *	job: we have to write data to the line. We do this
			 *	in a loop. First, we read data from the
			 *	AudioInputStream to a buffer. Then, we write from
			 *	this buffer to the Line. This is done until the end
			 *	of the file is reached, which is detected by a
			 *	return value of -1 from the read method of the
			 *	AudioInputStream.
			 */
			int nBytesRead = 0;
			byte[] abData = new byte[nExternalBufferSize];
			while (nBytesRead != -1) {
				try {
					nBytesRead = m_stream.read(abData, 0, abData.length);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (nBytesRead >= 0) {
					@SuppressWarnings("unused")
					int nBytesWritten = m_source_DataLine.write(abData, 0, nBytesRead);
				}
			}

			/*
			 *	Wait until all data is played.
			 *	This is only necessary because of the bug noted below.
			 *	(If we do not wait, we would interrupt the playback by
			 *	prematurely closing the line and exiting the VM.)
			 *
			 *	Thanks to Margie Fitch for bringing me on the right
			 *	path to this solution.
			 */
			m_source_DataLine.drain();

			/*
			 *	All data are played. We can close the shop.
			 */
			m_source_DataLine.close();

		}
	}

	private static SourceDataLine getSourceDataLine(String strMixerName, AudioFormat audioFormat, int nBufferSize) {
		/*
		*	Asking for a line is a rather tricky thing.
		*	We have to construct an Info object that specifies
		*	the desired properties for the line.
		*	First, we have to say which kind of line we want. The
		*	possibilities are: SourceDataLine (for playback), Clip
		*	(for repeated playback)	and TargetDataLine (for
		*	 recording).
		*	Here, we want to do normal playback, so we ask for
		*	a SourceDataLine.
		*	Then, we have to pass an AudioFormat object, so that
		*	the Line knows which format the data passed to it
		*	will have.
		*	Furthermore, we can give Java Sound a hint about how
		*	big the internal buffer for the line should be. This
		*	isn't used here, signaling that we
		*	don't care about the exact size. Java Sound will use
		*	some default value for the buffer size.
		*/
		SourceDataLine line = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, nBufferSize);
		try {
			if (strMixerName != null) {
				Mixer.Info mixerInfo = AudioCommon.getMixerInfo(strMixerName);
				if (mixerInfo == null) {
				}
				Mixer mixer = AudioSystem.getMixer(mixerInfo);
				line = (SourceDataLine) mixer.getLine(info);
			} else {
				line = (SourceDataLine) AudioSystem.getLine(info);
			}

			/*
			*	The line is there, but it is not yet ready to
			*	receive audio data. We have to open the line.
			*/
			line.open(audioFormat, nBufferSize);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return line;
	}

}
