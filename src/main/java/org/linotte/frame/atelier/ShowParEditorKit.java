package org.linotte.frame.atelier;

import javax.swing.text.*;

/**
 * 
 * http://java-sl.com/showpar.html
 * 
 *
 */
@SuppressWarnings("serial")
public class ShowParEditorKit extends StyledEditorKit {
	public ViewFactory getViewFactory() {
		return new ShowParEditorKit.StyledViewFactory();
	}

	class StyledViewFactory implements ViewFactory {

		public View create(Element elem) {
			String kind = elem.getName();
			if (kind != null) {
				if (kind.equals(AbstractDocument.ContentElementName)) {
					return new ShowParLabelView(elem);
				} else if (kind.equals(AbstractDocument.ParagraphElementName)) {
					return new ParagraphView(elem);
				} else if (kind.equals(AbstractDocument.SectionElementName)) {
					return new BoxView(elem, View.Y_AXIS);
				} else if (kind.equals(StyleConstants.ComponentElementName)) {
					return new ComponentView(elem);
				} else if (kind.equals(StyleConstants.IconElementName)) {
					return new IconView(elem);
				}
			}

			// default to text display
			return new LabelView(elem);
		}

	}
}
