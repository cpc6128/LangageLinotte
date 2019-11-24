package org.linotte.frame.tableau;

import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

/*
 * 
 * http://tips4java.wordpress.com/2008/10/15/limit-lines-in-document/
 * 
 *  A class to control the maximum number of lines to be stored in a Document
 *
 *  Excess lines can be removed from the start or end of the Document
 *  depending on your requirement.
 *
 *  a) if you append text to the Document, then you would want to remove lines
 *     from the start.
 *  b) if you insert text at the beginning of the Document, then you would
 *     want to remove lines from the end.
 */
public class LimitLinesDocumentListener implements DocumentListener {
	private int maximumLines;

	/*
	 *  Specify the number of lines to be stored in the Document.
	 *  Extra lines will be removed from the start or end of the Document,
	 *  depending on the boolean value specified.
	 */
	public LimitLinesDocumentListener(int maximumLines) {
		this.maximumLines = maximumLines;
	}

	//  Handle insertion of new text into the Document

	public void insertUpdate(final DocumentEvent e) {
		//  Changes to the Document can not be done within the listener
		//  so we need to add the processing to the end of the EDT

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				removeLines(e);
			}
		});
	}

	public void removeUpdate(DocumentEvent e) {
	}

	public void changedUpdate(DocumentEvent e) {
	}

	/*
	 *  Remove lines from the Document when necessary
	 */
	private void removeLines(DocumentEvent e) {
		//  The root Element of the Document will tell us the total number
		//  of line in the Document.

		Document document = e.getDocument();
		Element root = document.getDefaultRootElement();

		synchronized (document) {
			while (root.getElementCount() > maximumLines) {
				removeFromStart(document, root);
			}
		}
	}

	/*
	 *  Remove lines from the start of the Document
	 */
	private void removeFromStart(Document document, Element root) {

		Element line = root.getElement(0);
		int end = line.getEndOffset();

		try {
			document.remove(0, end);
		} catch (BadLocationException ble) {
			System.out.println(ble);
		}

	}

}
