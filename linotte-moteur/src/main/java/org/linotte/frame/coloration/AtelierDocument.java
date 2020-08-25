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

package org.linotte.frame.coloration;

import org.linotte.frame.cahier.Cahier;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Optimisation du traitement des styles dans un objet DefaultStyledDocument. Le
 * principe est d'effectuer des traitement par lots.
 * 
 */
@SuppressWarnings("serial")
public class AtelierDocument extends DefaultStyledDocument {

	private static final String UNDERLINE_COLOR = "Underline-Color";
	Cahier cahier;

	public AtelierDocument(Cahier cahier) {
		this.cahier = cahier;
	}

	/**
	 * Boolean dynamique
	 *
	 */
	private static class Bool {
		boolean value = false;
	}

	// Permet d'afficher les styles par rafales :
	private boolean onDouble = true;
	private int declencheur = 0;

	/**
	 * La stratégie d'affichage est de colorier les éléments visibles
	 * @param styles
	 */
	public void setCharacterAttributes(List<Style> styles) {
		synchronized (styles) {

			// Pour :
			boolean finAvant = false;
			// Récupération du premier élément :
			JViewport viewport = cahier.scrollPan.getViewport();
			Point startPoint = viewport.getViewPosition();
			Dimension size = viewport.getExtentSize();
			Point endPoint = new Point(startPoint.x + size.width, startPoint.y + size.height * 2);
			int startOfView = cahier.getEditorPanelCahier().viewToModel(startPoint);
			int endOfView = cahier.getEditorPanelCahier().viewToModel(endPoint);
			if (onDouble == false && startOfView > declencheur) {
				onDouble = true;
			}
			// On double si :
			if (onDouble) {
				declencheur = endOfView;
				endOfView = endOfView + (endOfView - startOfView) * 4;
			}
			boolean doIt = false;
			int maxStyles = startOfView == 0 ? 2000 : 30;

			final List<Style> paquet = new ArrayList<Style>();
			for (int i = 0; i < styles.size(); i++) {
				Style style = styles.get(i);
				// On ne traite pas les éléments avant le premier affiché dans le cahier
				if (style.getPosition() <= endOfView && style.getPosition() >= startOfView) {
					doIt = true;
					styles.remove(style);
					i--;
					if (style.getPosition() > getLength())
						continue;
					int taille = style.getTaille() == -1 ? getLength() : style.getTaille();
					if (taille != 0) {
						paquet.add(style);
						maxStyles--;
						if (maxStyles == 0) {
							finAvant = true;
							break;
						}
					}
				} else {
					if (doIt) {
						// On est sortie de la zone visible
						finAvant = true;
						break;
					}
				}
			}
			if (onDouble && finAvant == false) {
				onDouble = false;
			}
			// On efface les indicateurs d'erreur :
			if (paquet.size() > 2)
				paquet.add(0, new Style(paquet.get(0).getPosition(), paquet.get(paquet.size() - 1).getPosition() - paquet.get(0).getPosition(),
						new SimpleAttributeSet(), null, Color.white));
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setCharacterAttributesFlush(paquet);
				}
			});

		}
	}

	public void setCharacterAttributesFlush(List<Style> styles) {
		final Bool lock = new Bool();
		try {
			for (Style style : styles) {
				int taille = style.getTaille() == -1 ? getLength() : style.getTaille();
				setCharacterAttributesDelegated(style.getPosition(), taille, style.getStyle(), false, style.getUrl(), style.getSouligne(), lock);
			}
		} finally {
			if (lock.value) {
				writeUnlock();
			}
		}
	}

	/**
	 * Sets attributes for some part of the document. A write lock is held by
	 * this operation while changes are being made, and a DocumentEvent is sent
	 * to the listeners after the change has been successfully completed.
	 * <p>
	 * This method is thread safe, although most Swing methods are not. Please
	 * see <A HREF=
	 * "http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
	 * to Use Threads</A> for more information.
	 * 
	 * @param offset
	 *            the offset in the document >= 0
	 * @param length
	 *            the length >= 0
	 * @param s
	 *            the attributes
	 * @param replace
	 *            true if the previous attributes should be replaced before
	 *            setting the new attributes
	 * @param souligne
	 * @param lock 
	 */
	private void setCharacterAttributesDelegated(int offset, int length, AttributeSet s, boolean replace, String url, Color souligne, Bool lock) {
		if (length == 0) {
			return;
		}

		boolean doIt = isAttributeModify(offset, length, s, url, souligne);

		if (doIt) {

			if (!lock.value) {
				lock.value = true;
				writeLock();
			}

			DefaultDocumentEvent changes = new DefaultDocumentEvent(offset, length, DocumentEvent.EventType.CHANGE);

			// split elements that need it
			buffer.change(offset, length, changes);
			// PENDING(prinz) - this isn't a very efficient way to iterate
			int lastEnd = Integer.MAX_VALUE;
			for (int pos = offset; pos < (offset + length); pos = lastEnd) {
				Element run = getCharacterElement(pos);
				lastEnd = run.getEndOffset();
				if (pos == lastEnd) {
					// offset + length beyond length of document, bail.
					break;
				}
				MutableAttributeSet attr = (MutableAttributeSet) run.getAttributes();

				changes.addEdit(new AttributeUndoableEdit(run, s, replace));
				if (replace) {
					attr.removeAttributes(attr);
					attr.removeAttribute(HTML.Attribute.HREF);
					attr.removeAttribute(UNDERLINE_COLOR);
				}
				attr.addAttributes(s);
				if (url != null) {
					attr.addAttribute(HTML.Attribute.HREF, url);
				}
				if (souligne != null) {
					attr.addAttribute(UNDERLINE_COLOR, souligne);
				}
			}
			changes.end();
			fireChangedUpdate(changes);
			fireUndoableEditUpdate(new UndoableEditEvent(this, changes));

		}
	}

	private boolean isAttributeModify(int offset, int length, AttributeSet s, String url, Color souligne) {
		// Vérification s'il faut effectuer un changement :
		boolean doIt = false;
		// PENDING(prinz) - this isn't a very efficient way to iterate
		int lastEnd = Integer.MAX_VALUE;
		for (int pos = offset; pos < (offset + length); pos = lastEnd) {
			Element run = getCharacterElement(pos);
			lastEnd = run.getEndOffset();
			if (pos == lastEnd) {
				// offset + length beyond length of document, bail.
				break;
			}
			MutableAttributeSet attr = (MutableAttributeSet) run.getAttributes();
			if (!(attr.containsAttributes(s)
					&& ((url != null && url.equals(attr.getAttribute(HTML.Attribute.HREF))) || (url == null && null == attr.getAttribute(HTML.Attribute.HREF))) && ((souligne != null && souligne
					.equals(attr.getAttribute(UNDERLINE_COLOR))) || (souligne == null && null == attr.getAttribute(UNDERLINE_COLOR))))) {
				doIt = true;
				break;
			}
		}
		return doIt;
	}

	@Override
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offs, str, StyleLinotte.styleRacine);
	}

}