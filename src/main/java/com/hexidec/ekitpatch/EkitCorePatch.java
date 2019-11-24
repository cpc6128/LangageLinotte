/*
GNU Lesser General Public License

EkitCore - Base Java Swing HTML Editor & Viewer Class (Core)
Copyright (C) 2000 Howard Kistler

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.hexidec.ekitpatch;

import com.hexidec.ekit.EkitCore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

/** EkitCore
 * Main application class for editing and saving HTML in a Java text component
 *
 * @author Howard Kistler
 * @author Ronan MOUNES : patch pour l'export RTF
 * @version 1.1-1
 *
 * REQUIREMENTS
 * Java 2 (JDK 1.5 or higher)
 * Swing Library
 */

@SuppressWarnings("serial")
public class EkitCorePatch extends EkitCore {

	public EkitCorePatch(boolean isParentApplet, String sDocument, String sStyleSheet, String sRawDocument, StyledDocument sdocSource, URL urlStyleSheet,
			boolean includeToolBar, boolean showViewSource, boolean showMenuIcons, boolean editModeExclusive, String sLanguage, String sCountry, boolean base64,
			boolean debugMode, boolean hasSpellChecker, boolean multiBar, String toolbarSeq, boolean keepUnknownTags, boolean enterBreak) {
		super(isParentApplet, sDocument, sStyleSheet, sRawDocument, sdocSource, urlStyleSheet, includeToolBar, showViewSource, showMenuIcons, editModeExclusive,
				sLanguage, sCountry, base64, debugMode, hasSpellChecker, multiBar, toolbarSeq, keepUnknownTags, enterBreak);
	}

	public EkitCorePatch(boolean isParentApplet, String sDocument, String sStyleSheet, String sRawDocument, StyledDocument sdocSource, URL urlStyleSheet,
			boolean includeToolBar, boolean showViewSource, boolean showMenuIcons, boolean editModeExclusive, String sLanguage, String sCountry, boolean base64,
			boolean debugMode, boolean hasSpellChecker, boolean multiBar, String toolbarSeq, boolean enterBreak) {
		super(isParentApplet, sDocument, sStyleSheet, sRawDocument, sdocSource, urlStyleSheet, includeToolBar, showViewSource, showMenuIcons, editModeExclusive,
				sLanguage, sCountry, base64, debugMode, hasSpellChecker, multiBar, toolbarSeq, enterBreak);
	}

	public EkitCorePatch(boolean isParentApplet, String sRawDocument, URL urlStyleSheet, boolean includeToolBar, boolean showViewSource, boolean showMenuIcons,
			boolean editModeExclusive, String sLanguage, String sCountry, boolean base64, boolean hasSpellChecker, boolean multiBar, String toolbarSeq,
			boolean enterBreak) {
		super(isParentApplet, sRawDocument, urlStyleSheet, includeToolBar, showViewSource, showMenuIcons, editModeExclusive, sLanguage, sCountry, base64,
				hasSpellChecker, multiBar, toolbarSeq, enterBreak);
	}

	public EkitCorePatch(boolean isParentApplet) {
		super(isParentApplet);
	}

	public EkitCorePatch() {
	}

	public String getRTFDocument() throws IOException, BadLocationException {
		StyledDocument doc = (StyledDocument) (getTextPane().getStyledDocument());
		// http://stackoverflow.com/questions/19188447/javax-swing-text-rtf-rtfeditorkit-rtf-is-an-8-bit-format
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		RTFEditorKit rtfKit = new RTFEditorKit();
		rtfKit.write(baos, doc, 0, doc.getLength());
		return baos.toString();
	}
}
