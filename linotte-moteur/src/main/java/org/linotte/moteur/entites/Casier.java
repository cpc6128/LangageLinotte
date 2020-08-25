/***********************************************************************
 * Linotte                                                             *
 * Version release date : October 26, 2005                             *
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

package org.linotte.moteur.entites;

import org.linotte.moteur.entites.ecouteurs.ActeurParent;
import org.linotte.moteur.entites.ecouteurs.ActeurParentGraphique;
import org.linotte.moteur.exception.Constantes;
import org.linotte.moteur.exception.ErreurException;
import org.linotte.moteur.exception.RoleException;
import org.linotte.moteur.exception.SyntaxeException;
import org.linotte.moteur.xml.api.Librairie;

import java.math.BigDecimal;
import java.util.*;

/**
 * Un casier est un {@link Acteur} tableau stockant une liste d'acteurs.
 * Son type est {@link Role}.CASIER
 */
public class Casier extends Acteur {

	private List<Acteur> liste = new ArrayList<Acteur>();

	private Role contenant = null;

	private String typeespece = null;

	private ActeurParent casierevenement = null;

	private Comparator<Acteur> comparator_nombre = new Comparator<Acteur>() {

		//TODO revoir la gestion des exceptions !

		public int compare(Acteur arg0, Acteur arg1) {
			BigDecimal b1 = null;
			BigDecimal b2 = null;
			try {
				b1 = (BigDecimal) ((Acteur) arg0).getValeur();
				b2 = (BigDecimal) ((Acteur) arg1).getValeur();
			} catch (ErreurException e) {
				e.printStackTrace();
			}
			return b1.compareTo(b2);
		}

		@Override
		public boolean equals(Object obj) {
			BigDecimal b1 = null;
			try {
				b1 = (BigDecimal) ((Acteur) obj).getValeur();
			} catch (ErreurException e) {
				e.printStackTrace();
			}
			return b1.equals(this);
		}
	};

	Comparator<Acteur> comparator_texte = new Comparator<Acteur>() {
		public int compare(Acteur arg0, Acteur arg1) {
			String b1 = null;
			String b2 = null;
			try {
				b1 = (String) ((Acteur) arg0).getValeur();
				b2 = (String) ((Acteur) arg1).getValeur();
			} catch (ErreurException e) {
				e.printStackTrace();
			}
			return b1.compareTo(b2);
		}

		@Override
		public boolean equals(Object obj) {
			String b1 = null;
			try {
				b1 = (String) ((Acteur) obj).getValeur();
			} catch (ErreurException e) {
				e.printStackTrace();
			}
			return b1.equals(this);
		}
	};

	public Casier(Librairie<?> lib, String pnom, Role r, Fichier fichier) {
		super(lib, pnom, Role.CASIER, null, fichier);
		contenant = r;
		casierevenement = new ActeurParent(this);
	}

	public Casier(Librairie<?> lib, String pnom, Role r, String type, Fichier fichier) {
		super(lib, pnom, Role.CASIER, null, fichier);
		contenant = r;
		casierevenement = new ActeurParent(this);
		typeespece = type;
	}

	public void ajouterValeur(Acteur a, boolean mettreajour) throws ErreurException {
		if (contenant == null) {
			contenant = a.getRole();
			if (contenant == Role.ESPECE) {
				typeespece = ((Prototype) a).getType();
			}
		}
		if (a.getRole() == contenant) {
			if (contenant == Role.ESPECE) {
				if (!typeespece.equalsIgnoreCase((((Prototype) a).getType())))
					throw new RoleException(Constantes.CASIER_MAUVAISE_ESPECE, a.getNom().toString(), typeespece, (((Prototype) a).getType()));
			}
			liste.add(a);
			a.addCasierEvenement(casierevenement);
			if (mettreajour && retourneLibrairie() != null) {
				retourneLibrairie().ajouterValeur(this, a);
				retourneLibrairie().mettre(this);
			}
		} else {
			throw new RoleException(Constantes.CASIER_MAUVAISE_ESPECE, a.getNom().toString(), contenant, a.getRole());
		}
	}

	public Role roleContenant() {
		return contenant;
	}

	public int taille() {
		return liste.size();
	}

	public void toutEffacer() throws ErreurException {
		liste.clear();
		if (retourneLibrairie() != null)
			retourneLibrairie().mettre(this);
	}

	public void addAll(Casier tab) throws SyntaxeException, ErreurException {
		synchronized (tab.liste) {
			List<Acteur> copies = new ArrayList<Acteur>(tab.liste);
			Iterator<?> i = copies.iterator();
			while (i.hasNext()) {
				ajouterValeur((Acteur) i.next(), true);
			}
		}
	}

	public Acteur retourne(int pos) {
		return liste.get(pos);
	}

	public void supprime(int pos) throws ErreurException {
		liste.remove(pos);
		if (retourneLibrairie() != null)
			retourneLibrairie().mettre(this);
	}

	@Override
	public Object getValeur() {
		return liste;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void setValeur(Object object) throws ErreurException {
		// vérification du type :
		if (object instanceof ArrayList)
			liste = (ArrayList<Acteur>) object;
		else {
			throw new ErreurException(Constantes.CASIER_MAUVAISE_ESPECE, getNom().toString());
		}
	}

	@Override
	public boolean estIlVide() {
		return taille() == 0;
	}

	@Override
	public Acteur copierCommeAttribut(Prototype p) {
		Casier temp = new Casier(librairie, getNom().toString(), getRole(), typeespece, retourneFichier());
		if (evenement != null) {
			temp.evenement = new ArrayList<ActeurParent>(1);
			temp.evenement.add(new ActeurParentGraphique(getNom().toString().toLowerCase(), p));
			for (ActeurParent e : temp.evenement) {
				if (e instanceof ActeurParentGraphique) {
					try {
						if (getNom() != null)
							//https://code.google.com/p/langagelinotte/issues/detail?id=73
							e.mettreAjour(getNom().toString().toLowerCase(), valeur);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		temp.slots = p.slots;
		temp.slotsGreffon = p.slotsGreffon;
		temp.contratsPrototype = p.contratsPrototype;

		temp.contenant = contenant;
		for (Acteur a : liste) {
			try {
				temp.ajouterValeur(a.clone(), false);
			} catch (ErreurException e) {
				e.printStackTrace();
			}
		}

		return temp;
	}

	@Override
	public void copier(Acteur a) {
		super.copier(a);
		Casier c = (Casier) a;
		this.liste = c.liste;
		this.casierevenement = c.casierevenement;
		this.contenant = c.contenant;
	}

	@Override
	public Acteur clone() {
		Casier temp = new Casier(librairie, null, getRole(), null);
		//temp.librairie = null;
		temp.setNom(null);
		temp.contenant = contenant;
		for (Acteur a : liste) {
			try {
				temp.ajouterValeur(a.clone(), false);
			} catch (ErreurException e) {
				e.printStackTrace();
			}
		}
		return temp;
	}

	public int recherche(Acteur a) throws ErreurException {
		synchronized (liste) {
			List<Acteur> copies = new ArrayList<Acteur>(liste);
			Iterator<Acteur> i = copies.iterator();
			int pos = 0;
			while (i.hasNext()) {
				pos++;
				if (a.getRole() == Role.NOMBRE) {
					if (a.getValeur().equals(((Acteur) i.next()).getValeur()))
						return pos;
				} else if (a.getRole() == Role.TEXTE) {
					if (((String) a.getValeur()).equalsIgnoreCase((String) ((Acteur) i.next()).getValeur()))
						return pos;
				} else
					i.next();
			}
		}
		return 0;
	}

	public void melanger() throws ErreurException {
		Collections.shuffle(liste);
		if (retourneLibrairie() != null)
			retourneLibrairie().mettre(this);
	}

	public void inverse() throws ErreurException {
		Collections.reverse(liste);
		if (retourneLibrairie() != null)
			retourneLibrairie().mettre(this);
	}

	public void trier() throws ErreurException {
		if (roleContenant() == Role.NOMBRE)
			Collections.sort(liste, comparator_nombre);
		if (roleContenant() == Role.TEXTE)
			Collections.sort(liste, comparator_texte);
		if (retourneLibrairie() != null)
			retourneLibrairie().mettre(this);
	}

	public void extraire(Acteur a, int pos1, int pos2) throws ErreurException {
		// super.clone(a);
		Casier c = (Casier) a;
		liste = new ArrayList<Acteur>(c.liste.subList(pos1, pos2));
		if (retourneLibrairie() != null)
			retourneLibrairie().mettre(this);
	}

	public List<Acteur> extraireTout() {
		return liste = new ArrayList<Acteur>(liste);
	}

	public void inserer(Acteur a, int pos1) throws ErreurException {
		// super.clone(a);
		// 
		if (a instanceof Casier && contenant == Role.CASIER) {
			Casier c = (Casier) a;
			liste.addAll(pos1, c.liste);
		} else
			liste.add(pos1, a);

		if (retourneLibrairie() != null)
			retourneLibrairie().mettre(this);
	}

	public int recherche(Acteur a, int pos1) throws ErreurException {
		synchronized (liste) {
			List<Acteur> copies = new ArrayList<Acteur>(liste);
			int pos = 1;
			Iterator<Acteur> i = copies.iterator();
			while (i.hasNext()) {
				Acteur temp = (Acteur) i.next();
				if (temp.getValeur().equals(a.getValeur())) {
					return pos;
				}
				pos++;
			}
		}
		return 0;
	}

	public String getTypeespece() {
		return typeespece;
	}

	public int oterActeur(Acteur a) throws ErreurException {
		int pos = liste.indexOf(a);
		if (pos != -1) {
			liste.remove(a);
			//			a.oterDe(a);
			if (retourneLibrairie() != null)
				retourneLibrairie().mettre(this);
		}
		//		/**
		//		 * Pour éviter les regressions, on n'ajoute que EvenementSQL
		//		 */
		//		for (ActeurParent e : a.evenement) {
		//			if (e instanceof EvenementSQL) {
		//				e.oter(null);
		//			}
		//		}

		return pos;
	}
}