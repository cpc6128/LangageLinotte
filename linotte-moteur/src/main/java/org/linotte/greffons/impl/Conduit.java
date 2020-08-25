package org.linotte.greffons.impl;

import org.linotte.greffons.externe.Greffon;
import org.linotte.greffons.externe.Greffon.DocumentationHTML;

import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@DocumentationHTML("" + "Le <i>conduit</i> est une espèce proposant des méthodes pour lire ou écrire dans un fichier.<br>"
		+ "L'encodage par défaut du conduit est UTF-8.<br>" + "<u>Les méthodes fonctionnelles proposées par l'espèce Conduit sont:</u><br>" + "<ul>"
		+ "<li>conduit.encodage(encodage&lt;texte>) : définir l'encodage de lecture/ecriture du fichier (à effectuer avant d'ouvrir le conduit)</li>"
		+ "<li>conduit.fichierexiste(encodage&lt;texte>) : vérifier l'existance du fichier</li>" + "<li>conduit.ferme() : fermer le fichier</li>"
		+ "<li><b>Pour la lecture</b></li><ul>" + "<li>conduit.ouvrir(chemin&lt;texte>) : ouvrir le fichier en lecture depuis le chemin</li>"
		+ "<li>conduit.sélectionneretouvrir() : afficher le <i>sélectionneur</i> de fichiers et ouvre le fichier sélectionné en lecture</li>"
		+ "<li>conduit.nomfichier() : retourne le nom du fichier</li>" + "<li>conduit.chemin() : retourne le chemin du fichier</li>"
		+ "<li>conduit.répertoire() : retourne vrai si le fichier est un répertoire</li>"
		+ "<li>conduit.nomfichiercomplet() : retourne le nom du fichier avec son chemin</li>" + "<li>conduit.taille() : retourne la taille du fichier</li>"
		+ "<li>conduit.lireligne() : retourner une ligne du fichier</li>" + "<li>conduit.lire() : retourner tous les caractères du fichier</li>"
		+ "<li>conduit.nlire(n&lt;nombre>) : retourner les n caractères suivants du fichier</li>"
		+ "<li>conduit.clire(caractère&lt;texte>) : retourner les caractères suivants du fichier jusqu'au caractère renseigné</li>" 
		+ "<li>conduit.clire2(caractère&lt;texte>, caractère2&lt;texte>) : retourner les caractères suivants du fichier jusqu'au premier ou jusqu'au second caractère renseigné</li>" 
		+ "</ul><li><b>Pour l'écriture</b></li><ul>"
		+ "<li>conduit.enregistrer(chemin&lt;texte>) : ouvrir le fichier destiné à l'enregistrement</li>"
		+ "<li>conduit.sélectionneretenregistrer() : afficher le <i>sélectionneur</i> de fichiers et ouvre le fichier sélectionné en écriture</li>"
		+ "<li>conduit.retourchariot() : retourne le retour chariot du système d'exploitation</li>"
		+ "<li>conduit.ecrire(chemin&lt;texte>) : écrire la valeur de l'acteur dans le fichier</li>" + "</ul>" + "</ul>")
public class Conduit extends Greffon {

	public static enum MODE {
		lecture, écriture
	};

	private static int BUFFER = 10000;

	public static String RETOUR_CHARIOT = System.getProperty("line.separator");

	public static final void main(String[] params) {
		try {
			LOG("**** Exemple 1 : ****");
			Conduit conduit = new Conduit();
			LOG(conduit.ouvrir("c:/temp/tube.txt"));
			LOG(conduit.lire());
			LOG(conduit.fermer());
		} catch (GreffonException e) {
			System.err.println(e.getMessage());
		}

		try {
			LOG("**** Exemple 2 : ****");
			Conduit conduit = new Conduit();
			conduit.encodage("iso-8859-15");
			LOG(conduit.ouvrir("c:/temp/tube.txt"));
			while (true)
				LOG((conduit.nlire(2)));
		} catch (GreffonException e) {
			System.err.println(e.getMessage());
		}

		try {
			LOG("**** Exemple 3 : ****");
			Conduit conduit = new Conduit();
			LOG(conduit.selectionneretouvrir());
			LOG((conduit.lire()));
			LOG(conduit.fermer());
		} catch (GreffonException e) {
			System.err.println(e.getMessage());
		}
	}

	private static void LOG(Object s) {
		System.out.println(s);
	}

	/**
	 * Flux
	 */

	private File file = null;
	private BufferedReader in = null;
	private Writer out = null;
	private static JFileChooser fileChooserOpen = null;
	private static JFileChooser fileChooserSave = null;

	/**
	 * Valeurs à définir par l'utilisateur :
	 * 
	 */

	private String encodage = "UTF-8";

	static {
		fileChooserOpen = new JFileChooser();
		fileChooserSave = new JFileChooser();
	}

	@Slot()
	public boolean encodage(String structure) throws GreffonException {
		encodage = structure;
		return true;
	}

	@Slot()
	public String lire() throws GreffonException {
		if (in == null) {
			throw new GreffonException("Le conduit doit être ouvert en lecture");
		}
		int a;
		try {
			char[] buf = new char[BUFFER];
			StringBuilder builder = new StringBuilder();
			while ((a = in.read(buf)) != -1) {
				builder.append(new String(buf, 0, a));
			}
			return builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw new GreffonException("Lecture impossible : " + e.getMessage());
		}
	}

	@Slot()
	public String nlire(int buffer) throws GreffonException {
		if (in == null) {
			throw new GreffonException("Le conduit doit être ouvert en lecture");
		}
		int a;
		try {
			char[] buf = new char[buffer];
			if ((a = in.read(buf)) != -1) {
				return new String(buf, 0, a);
			} else
				throw new GreffonException("Conduit ;");
		} catch (IOException e) {
			e.printStackTrace();
			throw new GreffonException("Lecture impossible : " + e.getMessage());
		}
	}

	@Slot()
	public String clire(String caractere) throws GreffonException {
		if (in == null) {
			throw new GreffonException("Le conduit doit être ouvert en lecture");
		}
		int a;
		try {			
			char taquet = caractere.charAt(0);			
			StringBuilder builder = new StringBuilder();
			while ((a = in.read()) != -1) {
				char b = (char) a;
				if (b == taquet) {
					break;
				}
				builder.append(b);
			}
			
			if (a == -1 && builder.length() == 0)
				throw new GreffonException("Conduit vide");
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GreffonException("Lecture impossible : " + e.getMessage());
		}
	}
	
	@Slot(nom = "clire2")
	public String clire(String caractere, String caractere2) throws GreffonException {
		if (in == null) {
			throw new GreffonException("Le conduit doit être ouvert en lecture");
		}
		int a;
		try {			
			char taquet = caractere.charAt(0);
			char taquet2 = caractere2.charAt(0);			
			StringBuilder builder = new StringBuilder();
			while ((a = in.read()) != -1) {
				char b = (char) a;
				if (b == taquet || b == taquet2) {
					break;
				}
				builder.append(b);
			}
			
			if (a == -1 && builder.length() == 0)
				throw new GreffonException("Conduit vide");
			return builder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GreffonException("Lecture impossible : " + e.getMessage());
		}
	}

	@Slot()
	public boolean nonvide() throws GreffonException {
		if (in == null) {
			throw new GreffonException("Le conduit doit être ouvert en lecture");
		}
		try {
			return in.ready();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GreffonException("Lecture impossible : " + e.getMessage());
		}
	}

	@Slot()
	public boolean vide() throws GreffonException {
		if (in == null) {
			throw new GreffonException("Le conduit doit être ouvert en lecture");
		}
		try {
			return !in.ready();
		} catch (Exception e) {
			e.printStackTrace();
			throw new GreffonException("Lecture impossible : " + e.getMessage());
		}
	}

	@Slot()
	public boolean fermer() throws GreffonException {
		if (in != null) {
			try {
				in.close();
				in = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		}
		if (out != null) {
			try {
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}

	@Slot()
	public boolean ouvrir(String fichier) throws GreffonException {
		RessourceManager ressourceManager = getRessourceManager();
		if (ressourceManager != null)
			fichier = ressourceManager.analyserChemin(fichier);

		try {
			file = new File(fichier);
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), encodage));
		} catch (Exception e) {
			throw new GreffonException("Impossible d'ouvrir le fichier (" + e.getMessage() + ") : " + fichier);
		}
		return true;
	}

	@Slot(nom = "sélectionneretouvrir")
	public boolean selectionneretouvrir() throws GreffonException {
		int returnVal = fileChooserOpen.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				file = fileChooserOpen.getSelectedFile();
				return ouvrir(file.toString());
			} catch (Exception e1) {
			}
		}
		return false;
	}

	@Slot(nom = "sélectionner")
	public String selectionner() throws GreffonException {
		int returnVal = fileChooserOpen.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				file = fileChooserOpen.getSelectedFile();
				return file.getCanonicalPath();
			} catch (Exception e1) {
			}
		}
		return "";
	}

	@Slot()
	public boolean enregistrer(String fichier) throws GreffonException {
		RessourceManager ressourceManager = getRessourceManager();
		if (ressourceManager != null)
			fichier = ressourceManager.analyserChemin(fichier);

		try {
			file = new File(fichier);
			file.getParentFile().mkdirs();
			out = new OutputStreamWriter(new FileOutputStream(file), encodage);
		} catch (Exception e) {
			throw new GreffonException("Impossible d'enregistrer le fichier (" + e.getMessage() + ") : " + fichier);
		}
		return true;
	}

	@Slot()
	public boolean fichierexiste(String fichier) throws GreffonException {
		RessourceManager ressourceManager = getRessourceManager();
		if (ressourceManager != null)
			fichier = ressourceManager.analyserChemin(fichier);
		return new File(fichier).exists();
	}

	@Slot(nom = "répertoire")
	public boolean estcerépertoire(String fichier) throws GreffonException {
		RessourceManager ressourceManager = getRessourceManager();
		if (ressourceManager != null)
			fichier = ressourceManager.analyserChemin(fichier);
		return new File(fichier).isDirectory();
	}

	@Slot(nom = "écrire")
	public boolean ecrire(String flux) throws GreffonException {
		if (out == null) {
			throw new GreffonException("Le conduit doit être ouvert en écriture");
		}
		if (flux != null) {
			try {
				out.append(flux);
			} catch (IOException e) {
				e.printStackTrace();
				throw new GreffonException("Ecriture impossible : " + e.getMessage());
			}
		}
		return true;
	}

	@Slot(nom = "écrirenombre")
	public boolean ecrirenombre(double flux) throws GreffonException {
		if (out == null) {
			throw new GreffonException("Le conduit doit être ouvert en écriture");
		}
		try {
			out.append(String.valueOf(flux));
		} catch (IOException e) {
			e.printStackTrace();
			throw new GreffonException("Ecriture impossible : " + e.getMessage());
		}
		return true;
	}

	@Slot(nom = "sélectionneretenregistrer")
	public boolean selectionneretecrire() throws GreffonException {
		int returnVal = fileChooserSave.showSaveDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			try {
				file = fileChooserSave.getSelectedFile();
				return enregistrer(file.toString());
			} catch (Exception e1) {
			}
		}
		return false;
	}

	@Slot()
	public String nomfichier() throws GreffonException {
		if (file != null) {
			return file.getName();
		}
		throw new GreffonException("Aucun fichier d'ouvert");
	}

	@Slot()
	public String chemin() throws GreffonException {
		if (file != null) {
			try {
				return file.getParentFile().getCanonicalPath();
			} catch (IOException e) {
				throw new GreffonException("Impossible de déterminer le chemin : " + e.toString());
			}
		}
		throw new GreffonException("Aucun fichier d'ouvert");
	}

	@Slot()
	public String nomfichiercomplet() throws GreffonException {
		if (file != null) {
			try {
				return file.getCanonicalPath();
			} catch (IOException e) {
				throw new GreffonException("Impossible de déterminer le chemin : " + e.toString());
			}
		}
		throw new GreffonException("Aucun fichier d'ouvert");
	}

	@Slot()
	public Long taille() throws GreffonException {
		if (file != null) {
			return file.length();
		}
		throw new GreffonException("Aucun fichier d'ouvert");
	}

	@Slot()
	public String lireligne() throws GreffonException {
		if (in == null) {
			throw new GreffonException("Le conduit doit être ouvert en lecture");
		}
		try {
			String ligne = in.readLine();
			if (ligne == null) {
				throw new GreffonException("Conduit vide");
			}
			return ligne;
		} catch (IOException e) {
			throw new GreffonException("Lecture impossible : " + e.getMessage());
		}
		// BufferedReader reader = new BufferedReader(in);
		// return reader.readLine();

	}

	@Slot(nom = "chargecasiernombres")
	public List<BigDecimal> chargeCasierNombres(int nb) throws GreffonException {
		if (in == null) {
			throw new GreffonException("Le conduit doit être ouvert en lecture");
		}
		try {
			List<BigDecimal> casier = new ArrayList<BigDecimal>();
			for (int i = 0; i < nb; i++) {
				casier.add(new BigDecimal(in.readLine()));
			}
			return casier;
		} catch (IOException e) {
			throw new GreffonException("Lecture impossible : " + e.getMessage());
		}
	}

	@Slot(nom = "chargecasiertextes")
	public List<String> chargeCasierTextes(int nb) throws GreffonException {
		if (in == null) {
			throw new GreffonException("Le conduit doit être ouvert en lecture");
		}
		try {
			List<String> casier = new ArrayList<String>();
			for (int i = 0; i < nb; i++) {
				casier.add(in.readLine());
			}
			return casier;
		} catch (IOException e) {
			throw new GreffonException("Lecture impossible : " + e.getMessage());
		}
	}

	@Slot()
	public String retourchariot() {
		return RETOUR_CHARIOT;
	}
}