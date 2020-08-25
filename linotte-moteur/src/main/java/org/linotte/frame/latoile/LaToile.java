/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : May 5, 2004                                  *
 * Author : Mounès Ronan metalm@users.berlios.de                       *
 *                                                                     *
 *     http://jcubitainer.berlios.de/                                  *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 * metalm@users.berlios.de                                             *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

/* History & changes **************************************************
 *                                                                     *
 ******** May 5, 2004 **************************************************
 *   - First release                                                   *
 ***********************************************************************/

package org.linotte.frame.latoile;

import org.linotte.moteur.entites.PrototypeGraphique;
import org.linotte.moteur.xml.Linotte;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentListener;
import java.awt.event.WindowListener;

public interface LaToile {

	public static final int HAUTEUR = 590, LARGEUR = 600;

	public void addComponentListener(ComponentListener componentAdapter);

	public int getX();

	public int getY();

	public boolean isUndecorated();

	public void dispose();

	public void setUndecorated(boolean newvalue);

	public void setVisible(boolean b);

	public boolean isAlwaysOnTop();

	public void setAlwaysOnTop(boolean newvalueTop);

	public boolean isVisible();

	public boolean isFocused();

	public void pack();

	public Point getLocation();

	public void setLocation(int x, int y);

	public void setTitle(String titre);

	public void setLocationRelativeTo(Component object);

	public void setResizable(boolean b);

	public void addWindowListener(WindowListener closeListener);

	public boolean isAffichable();

	public JFrame getFrameParent();

	public Container getParent();

	public LaToileListener getPanelLaToile();

	// A modifier :

	//	private static boolean atelier;
	public void setAtelier(boolean atelier);

	public boolean isAtelier();

	public void setBarreDesTaches(boolean barre);

	public void setInterpreteur(Linotte pinterpreteur);

	public boolean isBarreDesTaches();

	public Linotte getInterpreteur();

	public PrototypeGraphique getElementDragAndDrop();

}