package console;

/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                                *
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

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.jdesktop.jdic.filetypes.Action;
import org.jdesktop.jdic.filetypes.Association;
import org.jdesktop.jdic.filetypes.AssociationAlreadyRegisteredException;
import org.jdesktop.jdic.filetypes.AssociationNotRegisteredException;
import org.jdesktop.jdic.filetypes.AssociationService;

public class Installateur {

	public static final String exe = "\\Linotte.exe";

	public static void main(String[] params) {

		String chemin = (params != null && params.length > 0) ? params[0] : null;

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
		}
		try {
			AssociationService assocService = new AssociationService();
			Association assoc = getAssociationFields(chemin);
			try {
				assocService.unregisterUserAssociation(assoc);
			} catch (AssociationNotRegisteredException e1) {
			}
			try {
				assocService.unregisterSystemAssociation(assoc);
			} catch (AssociationNotRegisteredException e) {
			}
			assocService.registerSystemAssociation(assoc);
		} catch (AssociationAlreadyRegisteredException e) {
		} catch (Exception e) {
			e.printStackTrace();
			erreur();
		}

		if (chemin == null)
			JOptionPane.showMessageDialog(null, "Une linotte est installée dans votre ordinateur !\nLes fichiers .liv sont associés à Linotte.");

	}

	public static void erreur() {
		JOptionPane
				.showMessageDialog(null, "Impossible d'installer Linotte dans votre ordinateur !\nLes fichiers .liv ne peuvent pas être associés à Linotte.");
		System.exit(0);
	}

	private static Association getAssociationFields(String chemin) {
		Association assoc = new Association();

		String description = "Livre Linotte";
		if ((description != null) && (description.length() != 0)) {
			assoc.setDescription(description);
		}

		String name = "Livre Linotte";
		if ((name != null) && (name.length() != 0)) {
			assoc.setName(name);
		}

		String fileExtensionListString = ".liv";
		if ((fileExtensionListString != null) && (fileExtensionListString.length() != 0)) {
			String leftExtString = fileExtensionListString;
			int startIndex = 0;
			int nextSpacePos = fileExtensionListString.indexOf(' ');

			while (nextSpacePos != -1) {
				String oneExt = leftExtString.substring(startIndex, nextSpacePos);

				assoc.addFileExtension(oneExt);

				String tempString = leftExtString.substring(nextSpacePos, leftExtString.length());

				leftExtString = tempString.trim();
				nextSpacePos = leftExtString.indexOf(' ');
			}

			if ((leftExtString != null) && (leftExtString.length()) != 0) {
				// one last file extension.
				assoc.addFileExtension(leftExtString);
			}
		}

		// Vérification si exe existe :
		File linotte = new File((chemin != null ? chemin : "") + "Linotte.exe");
		if (!linotte.exists()) {
			if (chemin == null)
				JOptionPane.showMessageDialog(null, "Je ne trouve pas : " + linotte.toString() + "\nLes fichiers .liv ne peuvent pas être associés à Linotte.");
			System.exit(1);
		}

		String file = null;
		try {
			file = linotte.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			erreur();
		}

		// Icon :
		File icon = new File((chemin != null ? chemin : "") + "Installateur.exe");
		String icon_text = file;
		try {
			icon_text = icon.getCanonicalPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assoc.setIconFileName(icon_text);

		Action oneAction = new Action("open", "\"" + file + "\" -xw \"%1\"");

		assoc.addAction(oneAction);

		return assoc;
	}

}
