/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

package org.linotte.frame.cahier;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * UndoManager adapté à la gestion des styles de l'Atelier
 * http://docs.liferay.com/portal/3.5/javadocs/applets/editor/com/liferay/applets/editor/Editor.java.html
 * @author ronan
 *
 */
@SuppressWarnings("serial")
public class CahierUndoManager extends UndoManager {

	public CahierUndoManager() {
		super();
		setLimit(20000);
	}

	@Override
	public synchronized void undo() throws CannotUndoException {
		boolean last = false;

		// Undo all style change(s) then single addition/deletion
		do {
			UndoableEdit edit = editToBeUndone();
			if (edit != null) {
				String presName = edit.getPresentationName();
				if (presName.toLowerCase().indexOf("style") == -1) {
					last = true;
				}
				super.undo();
			} else {
				last = true;
			}
		} while (!last);
	}

	@Override
	public synchronized void redo() throws CannotRedoException {
		boolean stop = false;

		// Redo single addition/deletion then all style change(s)
		super.redo();
		do {
			UndoableEdit edit = editToBeRedone();
			if (edit != null) {
				String presName = edit.getPresentationName();
				if (presName.toLowerCase().indexOf("style") != -1) {
					super.redo();
				} else {
					stop = true;
				}
			} else {
				stop = true;
			}
		} while (!stop);
	}

}
