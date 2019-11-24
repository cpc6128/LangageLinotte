package org.linotte.frame.cahier.timbre.ui.theme.simple.boite;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import org.linotte.frame.cahier.timbre.entite.Acteur;
import org.linotte.frame.cahier.timbre.entite.Fonction;
import org.linotte.frame.cahier.timbre.entite.PorteurFonction;
import org.linotte.frame.cahier.timbre.entite.Timbre;
import org.linotte.frame.cahier.timbre.entite.e.Role;
import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.theme.simple.Abaque;
import org.linotte.frame.cahier.timbre.ui.theme.simple.Palette;
import org.linotte.moteur.xml.analyse.Mathematiques;
import org.linotte.moteur.xml.analyse.Mathematiques.Temoin;

public class BoiteActeur extends Boite {

	private static final int HAUTEUR_TEXTE = 20;
	// Version simple :
	public String texteSimple;
	public Role role;
	public Acteur parent;

	// Version évoluée :
	// Si Acteur et texteSimple à null ou alors, si c'est un Verbe le parent de la boite.
	public Deposable deposable;

	public int x;

	public int y;

	public int largeur = 0;

	public int hauteur = 30;

	private BoiteActeur parametresFonction[] = null;

	private int largeur_cible;

	// //////////////////////////////
	public ZoneActive zone;
	// On delegue l'activation à cette boite :
	public BoiteActeur zoneDelegue;
	// Est-ce la premiere boite du timbre ? plus grande poupée russe !
	private boolean premiereBoiteAllumee = true;
	// //////////////////////////////

	public BoiteActeur(ZoneActive zone, int zoom) {
		this.zone = zone;
		largeur_cible = (int) (Abaque.LARGEUR_TIMBRE / 1.5);
	}

	public BoiteActeur(ZoneActive zone) {
		this.zone = zone;
		largeur_cible = Abaque.LARGEUR_TIMBRE;
	}

	@Override
	public void calculer(Graphics2D g2d) {
		largeur = 0;
		if (deposable == null) {
			String texte;
			if (role == Role.TEXTE)
				texte = "\"" + (texteSimple == null ? "" : texteSimple) + "\"";
			else
				texte = texteSimple == null ? "0" : texteSimple;
			largeur = g2d.getFontMetrics().stringWidth(texte) + 10;
		} else if (deposable instanceof Acteur) {
			Acteur acteur = (Acteur) deposable;
			// Taille de la valeur :
			String texte;
			if (acteur.role == Role.TEXTE)
				texte = "\"" + (acteur.valeur == null ? "" : acteur.valeur) + "\"";
			else
				texte = acteur.valeur == null ? "0" : acteur.valeur;
			largeur = g2d.getFontMetrics().stringWidth(texte) + 10;
			// Taille du nom :
			if (acteur.texte != null) {
				int largeurActeur = g2d.getFontMetrics().stringWidth(acteur.texte) + 10;
				largeur = Math.max(largeurActeur, largeur);
			}
		} else if (deposable instanceof PorteurFonction) {
			PorteurFonction porteur = (PorteurFonction) deposable;
			// Taille du nom de la fonction :
			largeur = g2d.getFontMetrics().stringWidth(porteur.fonction.nom) + 10;
			// On ajoute les paramètres :
			int nbParametre = porteur.fonction.retourneParametres().size();
			if (!porteur.verifierFonction()) {
				parametresFonction = null;
			} else if (parametresFonction != null && parametresFonction.length != nbParametre) {
				// La liste a changé de taille
				parametresFonction = null;
			}
			// On recopie le paramètre à afficher :

			if (nbParametre > 0) {
				//largeur = Math.max(largeur_cible, largeur);

				if (parametresFonction == null)
					parametresFonction = new BoiteActeur[nbParametre];

				for (int i = 0; i < parametresFonction.length; i++) {
					if (parametresFonction[i] == null)
						parametresFonction[i] = new BoiteActeur(ZoneActive.DELEGUEE, 2);
					if (porteur.parametres.size() > i)
						parametresFonction[i].initialiser(porteur.parametres.get(i));
					else
						parametresFonction[i].initialiser(null);
					parametresFonction[i].calculer(g2d);
					largeur += parametresFonction[i].largeur + 10;
				}
			}

		}

		largeur = Math.max(largeur, largeur_cible - 16);

	}

	public void dessine(Graphics2D g2d, int x, int y, ZoneActive cible, BoiteActeur zoneDelegueCible) {
		this.x = x;
		this.y = y;

		if (Abaque.DEBUG) {
			g2d.setColor(Palette.DEBUG);
			g2d.fillRect(x, y, largeur, hauteur);
		}

		if (deposable == null && texteSimple != null) {
			// Cas d'un acteur simple (timbre Acteur avec une valeur)
			g2d.setColor(Palette.VALEUR);
			if (role == Role.TEXTE)
				TimbresHelper.drawString(g2d, "\"" + (texteSimple == null ? "" : texteSimple) + "\"", x, y + 20);
			else
				TimbresHelper.drawString(g2d, texteSimple == null ? "0" : texteSimple, x, y + 20);
		} else if (deposable == null) {
			g2d.setColor(Palette.HALO_PARAMETRE);
			g2d.drawRoundRect(x, y, largeur, hauteur, Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);
			g2d.setColor(Palette.VIDE);
			TimbresHelper.drawString(g2d, "?", x + largeur / 2, y + HAUTEUR_TEXTE);
		} else if (deposable instanceof Acteur) {
			Acteur moi = (Acteur) deposable;
			if (moi.texte == null) {
				g2d.setColor(Palette.HALO_PARAMETRE);
				g2d.drawRoundRect(x, y, largeur, hauteur, Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);
				//Acteur anonyme
				g2d.setColor(Palette.TEXTE);
				if (moi.role == Role.TEXTE)
					TimbresHelper.drawString(g2d, "\"" + moi.valeur + "\"", x + 2, y + HAUTEUR_TEXTE);
				else
					TimbresHelper.drawString(g2d, moi.valeur, x + 2, y + HAUTEUR_TEXTE);
			} else {
				g2d.setColor(Palette.ACTEUR);
				g2d.fillRoundRect(x, y, largeur, hauteur, Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);
				g2d.setColor(Palette.HALO_PARAMETRE);
				g2d.drawRoundRect(x, y, largeur, hauteur, Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);
				g2d.setColor(Palette.TEXTE);
				TimbresHelper.drawString(g2d, moi.texte, x + 2, y + HAUTEUR_TEXTE);
			}

		} else if (deposable instanceof PorteurFonction) {
			// C'est une fonction :
			Fonction fonction = ((PorteurFonction) deposable).fonction;
			g2d.setColor(Palette.FONCTION);
			g2d.fillRoundRect(x, y, largeur, hauteur, Abaque.ANGLE_TIMBRE, Abaque.ANGLE_TIMBRE);
			g2d.setColor(Palette.HALO_PARAMETRE);
			//g2d.drawRect(x, y, largeur, hauteur);
			g2d.setColor(Palette.TEXTE);
			TimbresHelper.drawString(g2d, fonction.nom + " (", x + 2, y + HAUTEUR_TEXTE);
			// On dessine les paramètres de la fonction à la suite :
			int x_p = x + g2d.getFontMetrics().stringWidth(fonction.nom) + 10;

			if (parametresFonction != null)
				for (int i = 0; i < parametresFonction.length; i++) {
					parametresFonction[i].dessine(g2d, x_p, y, cible, zoneDelegue);
					x_p += parametresFonction[i].largeur;
					if (i + 1 != parametresFonction.length) {
						g2d.setColor(Palette.TEXTE);
						TimbresHelper.drawString(g2d, ",", x_p + 3, y + HAUTEUR_TEXTE);
					}
					x_p += 10;
				}
			else
				x_p += 5;

			g2d.setColor(Palette.TEXTE);
			TimbresHelper.drawString(g2d, ")", x_p - 5, y + HAUTEUR_TEXTE);
		}
		if (zoneDelegueCible == this && zoneDelegue == null) {
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(Palette.HALO_PARAMETRE_ACTIF);
			g2d.drawRect(x, y, largeur, hauteur);
			g2d.setStroke(new BasicStroke());
		} else if (zone == cible && zoneDelegue == null && premiereBoiteAllumee) {
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(Palette.HALO_PARAMETRE_ACTIF);
			g2d.drawRect(x, y, largeur, hauteur);
			g2d.setStroke(new BasicStroke());
		}
		// Ancre :
		if (Abaque.DEBUG) {
			g2d.setColor(Palette.TEXTE);
			g2d.drawRect(x + largeur / 2, y, largeur / 2, hauteur);
		}
	}

	public Deposable clique(final int x, final int y) {
		return clique(x, y, null);
	}

	public Deposable clique(final int x, final int y, BoiteActeur reference) {

		if (parametresFonction != null) {

			for (int i = 0; i < parametresFonction.length; i++) {
				BoiteActeur boiteActeur = parametresFonction[i];
				Deposable retour;
				if ((retour = boiteActeur.clique(x, y)) != null) {
					// On affecte l'acteur au bean persistant
					while (((PorteurFonction) deposable).parametres.size() < (i + 1)) {
						((PorteurFonction) deposable).parametres.add(null);
					}
					((PorteurFonction) deposable).parametres.set(i, retour);
					(parametresFonction[i] = new BoiteActeur(ZoneActive.DELEGUEE, 2)).initialiser(retour);
					return deposable;
				}

			}
		}

		Shape shape = new Rectangle2D.Double(this.x, this.y, largeur, hauteur);
		boolean clique = shape.contains(x, y);
		if (clique) {
			if (deposable == null && texteSimple != null) {
				// Pour les timbres : Acteur
				// *************************
				Acteur acteur = (Acteur) parent;
				// Acteur anonyme texte :
				String initValeur = acteur.valeur == null ? "" : acteur.valeur;

				String valeur = (String) JOptionPane.showInputDialog(null, "Valeur de la variable ?", "Modification d'une variable",
						JOptionPane.QUESTION_MESSAGE, null, null, initValeur);
				if (valeur != null) {
					if (acteur.role == Role.NOMBRE) {
						BigDecimal r = valeur.length() == 0 ? null : isBigDecimal(valeur);
						if (r != null) {
							acteur.valeur = valeur;
						} else {
							JOptionPane.showMessageDialog(null, "Valeur incorrecte !");
						}
					} else {
						acteur.valeur = valeur;
					}
				}
				return acteur;
			} else {
				final Role[] roleDefault = { Role.TEXTE, Role.NOMBRE, Role.FORMULE };
				final Role[] roleCond = { Role.FORMULE, Role.NOMBRE, Role.TEXTE };
				final Role[] roleNombre = { Role.NOMBRE, Role.FORMULE, Role.TEXTE};
				final Role[] roleText = { Role.TEXTE, Role.FORMULE, Role.NOMBRE};
				final JComboBox<Role> roles;
				if ( zone == ZoneActive.CONDITION_FORMULE) {
					roles = new JComboBox<>(roleCond);
				} else {
					if ( reference != null) {
						if ( reference.role == Role.TEXTE ) {
							roles = new JComboBox<>(roleText);		
						} else if ( reference.role == Role.NOMBRE ) {
							roles = new JComboBox<>(roleNombre);		
						}  else {
							roles = new JComboBox<>(roleDefault);
						}
					} else
						roles = new JComboBox<>(roleDefault);
				}
				String initValeur = null;
				if (deposable != null && deposable instanceof Acteur && ((Acteur) deposable).texte == null) {
					// acteur anonyme
					Acteur a = ((Acteur) deposable);
					
					roles.setSelectedItem(a.role);
					initValeur = a.valeur;
				}
				final Object msg[] = { "Valeur de la variable ?", roles };
				// Acteur anonyme texte :
				String valeur = (String) JOptionPane.showInputDialog(null, msg, "Création d'une variable anonyme", JOptionPane.QUESTION_MESSAGE, null, null,
						initValeur);
				if (valeur != null) {
					boolean correcte = true;

					if ((Role) roles.getSelectedItem() == Role.NOMBRE) {
						BigDecimal r = isBigDecimal(valeur);
						if (r == null) {
							JOptionPane.showMessageDialog(null, "Valeur incorrecte !");
							correcte = false;
						}
					}

					if (correcte) {
						deposable = new Acteur(null, (Role) roles.getSelectedItem(), valeur, null);
						return deposable;
					}
				}
				return deposable;
			}
		}
		return null;
	}

	private BigDecimal isBigDecimal(String valeur) {
		try {
			return Mathematiques.isBigDecimal(valeur);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Pour les verbes :
	 * @param a
	 */
	public void initialiser(Deposable a) {
		//parametresFonction = null;
		if (a != null) {
			deposable = a;
			texteSimple = null;
			role = (a instanceof Acteur) ? ((Acteur) a).role : null;
		} else {
			deposable = null;
			texteSimple = null;
			role = null;
		}
	}

	/**
	 * Pour les textes :
	 * @param valeur
	 * @param role 
	 */
	public void initialiser(Acteur a, String valeur, Role role) {
		//parametresFonction = null;
		// Compatibilité 2.5.1
		if (valeur == null && role == Role.NOMBRE) {
			valeur = "0";
		} else if (valeur == null && role == Role.TEXTE) {
			valeur = "";
		}
		deposable = null;
		texteSimple = valeur;
		this.role = role;
		parent = a;
	}

	public boolean accepteZoneParametres(Deposable acteur) {
		Temoin temoinActivation = new Temoin();
		boolean active = accepteZoneParametres(this, acteur, temoinActivation);
		premiereBoiteAllumee = !temoinActivation.isActeur();
		return active;
	}

	private boolean accepteZoneParametres(BoiteActeur parent, Deposable acteur, Temoin temoin) {
		zoneDelegue = null;
		// Regardons si zone des paramètres des fonctions actifs;
		if (parametresFonction != null) {
			for (BoiteActeur acteurBoite : parametresFonction) {
				if (acteurBoite.accepteZoneParametres(parent, acteur, temoin)) {
					if (!temoin.isActeur())
						zoneDelegue = acteurBoite;
					temoin.setActeur(true);
					return true;
				}
			}
		}
		Shape shape = new Rectangle2D.Double(x, y, largeur, hauteur);
		boolean contient = shape.intersects(((Timbre) acteur).ui.ancreAvant().getBounds2D());
		return contient;
	}

	public boolean finGlisseDeposeDelegue(Deposable timbreGlisse) {
		if (zoneDelegue != null) {
			// On depose l'acteur ici :
			// On recherche quel acteur de la fonction :
			for (int i = 0; i < parametresFonction.length; i++) {
				BoiteActeur acteurBoite = parametresFonction[i];
				if (acteurBoite == zoneDelegue) {
					while (((PorteurFonction) deposable).parametres.size() < (i + 1)) {
						((PorteurFonction) deposable).parametres.add(null);
					}
					((PorteurFonction) deposable).parametres.set(i, englobeFonction((Timbre) timbreGlisse));
					parametresFonction[i] = new BoiteActeur(ZoneActive.DELEGUEE, 2);
					return true;
				}
			}
		} else {
			if (parametresFonction != null) {
				for (BoiteActeur acteurBoite : parametresFonction) {
					if (acteurBoite.finGlisseDeposeDelegue(timbreGlisse))
						return true;
				}
			}
		}
		return false;
	}

	private Deposable englobeFonction(Timbre timbreGlisse) {
		if (timbreGlisse instanceof Fonction) {
			PorteurFonction porteurFonction = new PorteurFonction();
			porteurFonction.fonction = (Fonction) timbreGlisse;
			porteurFonction.parametres = new ArrayList<>();
			return porteurFonction;
		} else
			return (Deposable) timbreGlisse;
	}

	public void eteindreZoneActive() {
		zoneDelegue = null;
		if (parametresFonction != null)
			for (BoiteActeur acteurBoite : parametresFonction)
				acteurBoite.eteindreZoneActive();
	}
}
