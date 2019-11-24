package org.linotte.greffons.impl;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Scanner;

import org.linotte.greffons.externe.Tube;

/**
 * 
 * Tube fichier
 * 
 * @author Mounès Ronan
 * 
 */
public class FichierTube extends Tube {

	private static final String RETOUR_CHARIOT = System.getProperty("line.separator");

	private Scanner in = null;

	private FileWriter out = null;

	private static final String ETAT_LECTURE = "lecture";

	private static final String ETAT_ECRITURE_AJOUT = "ajout";

	private static final String ETAT_ECRITURE_ECRASEMENT = "écrasement";

	public static final void main(String[] params) {
		// Exemples d'utilisation

		try {
			// Création du fichier vide avec 4 acteurs
			FichierTube fichierTube = new FichierTube();
			fichierTube.ouvrir(ETAT_ECRITURE_ECRASEMENT, "c:/temp/tube.txt");
			Acteur acteur1 = new Acteur(ROLE.TEXTE, "acteur1", "valeur 1");
			fichierTube.charger(acteur1);
			Acteur acteur2 = new Acteur(ROLE.TEXTE, "acteur2", "valeur 2");
			fichierTube.charger(acteur2);
			fichierTube.fermer();
		} catch (GreffonException e) {
			System.err.println(e.getMessage());
		}

		try {
			// Création du fichier vide avec 4 acteurs
			FichierTube fichierTube = new FichierTube();
			fichierTube.ouvrir(ETAT_ECRITURE_AJOUT, "c:/temp/tube.txt");
			Acteur acteur3 = new Acteur(ROLE.TEXTE, "acteur2", "valeur 3");
			Acteur acteur4 = new Acteur(ROLE.TEXTE, "acteur2", "valeur 4");
			Casier casier = new Casier(ROLE.TEXTE);
			casier.add(acteur3);
			casier.add(acteur4);
			fichierTube.charger(casier);
			fichierTube.fermer();
		} catch (GreffonException e) {
			System.err.println(e.getMessage());
		}

		try {
			// Cas fichier avec 4 acteurs :
			FichierTube fichierTube = new FichierTube();
			fichierTube.ouvrir(ETAT_LECTURE, "c:/temp/tube.txt");
			System.out.println(fichierTube.decharger(new Acteur(ROLE.TEXTE, "acteur1", null)));
			System.out.println(fichierTube.decharger(new Acteur(ROLE.TEXTE, "acteur2", null)));
			System.out.println(fichierTube.decharger(new Acteur(ROLE.TEXTE, "acteur3", null)));
			System.out.println(fichierTube.decharger(new Acteur(ROLE.TEXTE, "acteur4", null)));
			fichierTube.fermer();
		} catch (GreffonException e) {
			System.err.println(e.getMessage());
		}

		try {
			// Cas fichier avec 1 casier d'acteurs :
			FichierTube fichierTube = new FichierTube();
			fichierTube.ouvrir(ETAT_LECTURE, "c:/temp/tube.txt");
			Casier casier = new Casier(ROLE.TEXTE);
			casier.add(new Acteur(ROLE.TEXTE, null, null));
			System.out.println(fichierTube.decharger(casier));
			fichierTube.fermer();
		} catch (GreffonException e) {
			System.err.println(e.getMessage());
		}

		try {
			// Cas fichier avec 1 casier d'espèce :
			FichierTube fichierTube = new FichierTube();
			fichierTube.ouvrir(ETAT_LECTURE, "c:/temp/tube.txt");
			Casier casier = new Casier(ROLE.TEXTE);
			Espece espece = new Espece("test");
			espece.add(new Acteur(ROLE.TEXTE, "attribut 1", null));
			espece.add(new Acteur(ROLE.TEXTE, "attribut 2", null));
			casier.add(espece);
			System.out.println(fichierTube.decharger(casier));
			fichierTube.fermer();
		} catch (GreffonException e) {
			System.err.println(e.getMessage());
		}

	}

	public FichierTube() {
	}

	@Override
	public boolean ouvrir(String état, String paramètres) throws GreffonException {
		RessourceManager ressourceManager = getRessourceManager();
		String fichier;
		if (ressourceManager != null)
			fichier = ressourceManager.analyserChemin(paramètres);
		else
			fichier = paramètres;

		try {
			if (ETAT_LECTURE.equals(état)) {
				in = new Scanner(new FileReader(fichier));
				out = null;
			} else if (ETAT_ECRITURE_AJOUT.equals(état)) {
				creationRepertoire(fichier);
				out = new FileWriter(fichier, true);
				in = null;
			} else if (ETAT_ECRITURE_ECRASEMENT.equals(état)) {
				creationRepertoire(fichier);
				out = new FileWriter(fichier, false);
				in = null;
			} else {
				throw new GreffonException(
						"l'état doit être écriture '" + ETAT_ECRITURE_AJOUT + "', '" + ETAT_ECRITURE_ECRASEMENT + "' ou '" + ETAT_LECTURE + "' !");
			}
		} catch (Exception e) {
			throw new GreffonException("Impossible d'ouvrir et de créer ce fichier (" + e.getMessage() + ") : " + fichier);
		}
		return false;
	}

	private void creationRepertoire(String fichier) {
		try {
			new File(fichier).getParentFile().mkdirs();
		} catch (Exception e) {
		}
	}

	@Override
	public ObjetLinotte decharger(ObjetLinotte structure) throws GreffonException {
		try {
			if (in == null) {
				throw new GreffonException("Le tube doit être ouvert en lecture avant d'être déchargé !");
			}
			if (structure instanceof Acteur) {
				Acteur acteur = (Acteur) structure;
				if (!in.hasNext()) {
					throw new GreffonException("Le tube est vide !");
				}
				String valeur = in.nextLine();
				if (acteur.getRole() == ROLE.NOMBRE) {
					try {
						acteur.setValeur(new BigDecimal(valeur.trim()));
					} catch (Exception e) {
						throw new GreffonException("Impossible de charger le nombre !");
					}
				} else
					acteur.setValeur(valeur);
				return acteur;
			} else if (structure instanceof Casier) {
				Casier casier = (Casier) structure;
				Casier retour = new Casier(null);
				for (ObjetLinotte element : casier) {
					if (element instanceof Acteur) {
						while (in.hasNext()) {
							retour.add(decharger(element.clone()));
						}
					} else if (element instanceof Espece) {
						while (in.hasNext()) {
							retour.add(decharger(element.clone()));
						}
					} else {
						throw new GreffonException("Structure incorrecte !");
					}
				}
				return retour;
			} else if (structure instanceof Espece) {
				Espece espece = (Espece) structure;
				for (Acteur acteur : espece) {
					decharger(acteur);
				}
				return espece;
			} else {
				throw new GreffonException("Structure incorrecte !");
			}
		} catch (Exception e) {
			throw new GreffonException("Le tube a recontré un problème : " + e.getMessage());
		}
	}

	@Override
	public ObjetLinotte decharger(String paramètres, ObjetLinotte structure) throws GreffonException {
		return decharger(structure);
	}

	@Override
	public boolean charger(ObjetLinotte structure) throws GreffonException {
		try {
			if (out == null) {
				throw new GreffonException("Le tube doit être ouvert en écriture pour être chargé !");
			}
			if (structure instanceof Acteur) {
				Acteur acteur = (Acteur) structure;
				try {
					out.write(String.valueOf(acteur.getValeur()));
					out.write(RETOUR_CHARIOT);
				} catch (IOException e) {
					return false;
				}
				return true;
			} else if (structure instanceof Casier) {
				Casier casier = (Casier) structure;
				for (ObjetLinotte objet : casier) {
					charger(objet);
				}
			} else if (structure instanceof Espece) {
				Espece espece = (Espece) structure;
				for (ObjetLinotte objet : espece) {
					charger(objet);
				}
			} else {
				throw new GreffonException("Structure incorrecte !");
			}
			return false;
		} catch (Exception e) {
			throw new GreffonException("Le tube a recontré un problème : " + e.getMessage());
		}
	}

	@Override
	public boolean charger(String paramètres, ObjetLinotte valeurs) throws GreffonException {
		return charger(valeurs);
	}

	@Override
	public boolean fermer() throws GreffonException {
		if (in != null) {
			in.close();
			return true;
		} else if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
			}
			return true;
		} else {
			throw new GreffonException("Le tube doit être ouvert avant d'être fermé !");
		}
	}

	@Override
	public boolean configurer(String paramètres) throws GreffonException {
		return true;
	}

}
