/***********************************************************************
 * JCubitainer                                                         *
 * Version release date : May 6, 2004                                  *
 * Author : Moun�s Ronan metalm@users.berlios.de                       *
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

package org.linotte.frame.cahier;

import org.linotte.frame.atelier.Atelier;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.StringTokenizer;

public class DragAndDropBoardListener implements DropTargetListener {

	Atelier parent;

	public DragAndDropBoardListener(Atelier gT2L) {
		parent = gT2L;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#dragEnter(java.awt.dnd.DropTargetDragEvent
	 * )
	 */
	public void dragEnter(DropTargetDragEvent dtde) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#dragExit(java.awt.dnd.DropTargetEvent)
	 */
	public void dragExit(DropTargetEvent dte) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#dragOver(java.awt.dnd.DropTargetDragEvent
	 * )
	 */
	public void dragOver(DropTargetDragEvent dtde) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.dnd.DropTargetListener#drop(java.awt.dnd.DropTargetDropEvent)
	 */
	public void drop(DropTargetDropEvent dtde) {
		// http://forum.clubic.com/forum2.php3?post=13326&cat=13

		final boolean LINUX = System.getProperty("os.name").equals("Linux");

		if (LINUX) {
			// http://stackoverflow.com/questions/811248/how-can-i-use-drag-and-drop-in-swing-to-get-file-path
			try {
				@SuppressWarnings("unused")
				DataFlavor nixFileDataFlavor = new DataFlavor("text/uri-list;class=java.lang.String");

				String data = (String) dtde.getTransferable().getTransferData(DataFlavor.stringFlavor);
				for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
					String token = st.nextToken().trim();
					if (token.startsWith("#") || token.isEmpty()) {
						// comment line, by RFC 2483
						continue;
					}
					try {
						File file = new File(new URI(token));
						setLivreFromDragNDrop(file);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (ClassNotFoundException e1) {
				e1.printStackTrace();
			} catch (UnsupportedFlavorException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			dtde.dropComplete(true);
		} else {
			DataFlavor[] tab = dtde.getCurrentDataFlavors();
			if (tab[0].isFlavorJavaFileListType())
				try {
					dtde.acceptDrop(dtde.getDropAction());
					File f = (File) ((List) dtde.getTransferable().getTransferData(dtde.getCurrentDataFlavors()[0])).get(0);
					setLivreFromDragNDrop(f);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			dtde.dropComplete(true);
		}
	}

	/**
	 * Affecte le livre à l'Atelier
	 * 
	 * @param livre
	 */
	private void setLivreFromDragNDrop(File livre) {
		parent.setLivreFromDragNDrop(livre);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.awt.dnd.DropTargetListener#dropActionChanged(java.awt.dnd.
	 * DropTargetDragEvent)
	 */
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}
}