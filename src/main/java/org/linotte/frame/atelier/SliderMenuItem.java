/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 18, 2014                                *
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

//  MenuElementExample.java
// http://www.onjava.com/pub/a/onjava/excerpt/swing_14/index6.html?page=2
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.linotte.frame.moteur.FrameProcess;
import org.linotte.moteur.outils.Preference;

// Inner class that defines our special slider menu item
@SuppressWarnings("serial")
public class SliderMenuItem extends JSlider implements MenuElement {

	public SliderMenuItem() {
		setBorder(new CompoundBorder(new TitledBorder("Délais débogage (millisecondes)"), new EmptyBorder(10, 10, 10, 10)));
		setPaintTicks(true);
		setPaintLabels(true);
		setMajorTickSpacing(500);
		setMinorTickSpacing(250);
		setMaximum(2100);
		setMinimum(100);
		setFocusable(false);
		addChangeListener(new SliderListener());
	}

	public void processMouseEvent(MouseEvent e, MenuElement path[], MenuSelectionManager manager) {
	}

	public void processKeyEvent(KeyEvent e, MenuElement path[], MenuSelectionManager manager) {
	}

	public void menuSelectionChanged(boolean isIncluded) {
	}

	public MenuElement[] getSubElements() {
		return new MenuElement[0];
	}

	public Component getComponent() {
		return this;
	}

	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			int delay = (int) source.getValue();
			Preference.getIntance().setInt(Preference.P_PAS_A_PAS, delay);
			FrameProcess.changeDelayDebogage(delay);
		}
	}
}