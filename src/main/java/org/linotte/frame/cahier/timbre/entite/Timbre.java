package org.linotte.frame.cahier.timbre.entite;

import java.util.ArrayList;

import org.linotte.frame.cahier.timbre.entite.e.Parfum;
import org.linotte.frame.cahier.timbre.entite.e.ZoneActive;
import org.linotte.frame.cahier.timbre.entite.i.Cloneur;
import org.linotte.frame.cahier.timbre.entite.i.Deposable;
import org.linotte.frame.cahier.timbre.entite.i.NonAttachable;
import org.linotte.frame.cahier.timbre.entite.i.NonDeplacable;
import org.linotte.frame.cahier.timbre.entite.i.PasSauvegarder;
import org.linotte.frame.cahier.timbre.outils.TimbresHelper;
import org.linotte.frame.cahier.timbre.ui.UsineATheme;
import org.linotte.frame.cahier.timbre.ui.i.PlancheUI;
import org.linotte.frame.cahier.timbre.ui.i.TimbreUI;
import org.linotte.frame.cahier.timbre.ui.i.UI;

/**
 * Affichage par défaut d'un timbre
 * @author cpc
 *
 */
public abstract class Timbre implements Cloneur {

	public transient final Integer POSITION_INIT = 1;

	public Integer position = POSITION_INIT;

	public int x = 150;

	public int y = 150;

	public Parfum parfum;

	//TODO, déplacer dans un autre objet plus métier
	public Timbre timbreSuivant = null;

	public transient int largeur;

	public transient int hauteur;

	public transient int init_x;

	public transient int init_y;

	public transient ZoneActive zoneActive = null;

	public transient boolean survol = false;

	public transient boolean glisser = false;

	public transient boolean erreur = false; // Erreur lors de l'execution

	public transient boolean erreurValidation = false; // Erreur lors de la self validation !

	public transient boolean deboguage = false;

	public transient int numeroLigne; // Numero de ligne lors de l'execution

	public transient TimbreUI ui = UsineATheme.getTimbreUI(this);

	public transient PlancheUI planche;

	public Timbre(PlancheUI planche) {
		this.planche = planche;
	}

	public void pre_dessine() {
		ui.pre_dessine(planche);
	}

	public void dessine() {
		ui.dessine(planche);
	}

	/**
	 * Est-ce que ce composant peut être attaché au composant timbreGraphiqueCible
	 * @param timbreGraphiqueCible
	 * @return
	 */
	public final boolean peutIlEtreAttache(Timbre timbreGraphiqueCible) {
		if (this instanceof NonAttachable && !(this instanceof Fonction)) // <-- si on veut glisser une fonction dans un verbe.
			return false;
		if (timbreGraphiqueCible instanceof PasSauvegarder) // Boutons systèmes
			return false;
		ZoneActive zone = timbreGraphiqueCible.accepteEtQuelleZone(this);
		if (this instanceof Fonction && (ZoneActive.TIMBRE == zone || ZoneActive.PARAMETRES_FONCTION == zone)) {
			return false; // <-- on ne peut pas attacher une fonction 
		}

		timbreGraphiqueCible.zoneActive = zone;
		return timbreGraphiqueCible.zoneActive != null;
	}

	public ZoneActive accepteEtQuelleZone(Timbre timbreGraphiqueCible) {
		boolean b = timbreGraphiqueCible.ui.accepteZoneTimbre(this);
		if (b) {
			return ZoneActive.TIMBRE;
		} else {
			return null;
		}
	}

	public boolean clique(int x2, int y2) {
		return ui.clique(x2, y2);
	}

	public boolean contient(int x2, int y2) {
		boolean b = ui.contient(x2, y2);
		return b;
	}

	public boolean debutGlisseDepose(int x, int y) {
		boolean b = ui.contient(x, y);
		if (b) {
			position = 20;
			glisser = true;
		}
		return b;
	}

	/**
	 * @param timbreGlisse peut être null !
	 */
	public boolean finGlisseDepose(Timbre timbreGlisse) {
		timbreGlisse.position = timbreGlisse.POSITION_INIT;
		timbreGlisse.glisser = false;

		// On ne déplace pas un bloc début (trop compliqué à gérer pour l'instant !)
		if (timbreGlisse instanceof NonDeplacable) {
			if (!TimbresHelper.timbresPrecedents(timbreGlisse).isEmpty()) {
				timbreGlisse.retourArriere();
				zoneActive = null;
				return false;
			}
		}

		boolean actionConsommee = false;

		if (zoneActive == ZoneActive.TIMBRE) {

			boolean ajouterParametre = false;

			if (this.timbreSuivant != null && timbreGlisse instanceof Acteur && this.timbreSuivant instanceof Acteur) {
				Acteur a = (Acteur) timbreSuivant;
				if (a.estCeUnParametre()) {
					// On ajoute cet acteur dans aux paramètres de la fonction :
					ajouterParametre = true;
				}
			}

			TimbresHelper.deplacerUnTimbre(timbreGlisse, this);

			if (ajouterParametre) {
				Fonction fonction = (Fonction) TimbresHelper.recherchePremiertimbre(this);
				fonction.parametres.remove((Acteur) timbreGlisse);
				fonction.parametres.add((Acteur) timbreGlisse);
				((Timbre) this).ui.repositionnerTousLesTimbres();
			} else {
				// On decalle tous les timbres à droite !
				TimbresHelper.recherchePremiertimbre(this).getUI().positionnerProchainTimbre();
			}
			actionConsommee = true;

		}
		timbreGlisse.zoneActive = null;

		if (timbreSuivant != null) {
			timbreSuivant.zoneActive = null;
		}

		zoneActive = null;

		return actionConsommee;
	}

	public UI getUI() {
		return ui;
	}

	// Contrôle :

	public void validation() {
		erreurValidation = false;
	}

	// Bean
	public Timbre() {
	}

	public final boolean retourArriere() {
		boolean action = false;
		// Retour case départ si le timbre est déjà attaché :
		if (!TimbresHelper.timbreSeul(this)) {
			x = init_x;
			y = init_y;
		} else {
			// S'il n'est pas attaché et il est premiere
			getUI().positionnerProchainTimbre();
			action = true;
		}
		position = POSITION_INIT;
		glisser = false;
		survol = false;
		return action;
	}

	public void clonerPourDuplication(Timbre clone) {
		clone.parfum = parfum;
		clone.position = position;
		clone.x = x;
		clone.y = y;
		clone.timbreSuivant = timbreSuivant;
	}

	public final Deposable englobeFonction(Timbre timbreGlisse) {
		if (timbreGlisse instanceof Fonction) {
			PorteurFonction porteurFonction = new PorteurFonction();
			porteurFonction.fonction = (Fonction) timbreGlisse;
			porteurFonction.parametres = new ArrayList<>();
			return porteurFonction;
		} else
			return (Deposable) timbreGlisse;
	}

}
