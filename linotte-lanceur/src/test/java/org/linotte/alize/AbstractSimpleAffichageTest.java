/**
 * 
 */
package org.linotte.alize;

import org.alize.kernel.AKRuntime;
import org.junit.Assert;
import org.junit.Before;
import org.linotte.alize.TestsErreurs.LIVRE;
import org.linotte.implementations.LibrairieVirtuelleSyntaxeV2;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.alize.kernel.ContextHelper;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.Parseur;
import org.linotte.moteur.xml.analyse.multilangage.Langage;
import org.linotte.moteur.xml.api.IHM;

import java.util.ResourceBundle;
import java.util.Set;

/**
 * @author CPC
 *
 */
abstract public class AbstractSimpleAffichageTest {

	protected Linotte linotte;

	private StringBuilder retour = new StringBuilder();

	private ResourceBundle bundle;

	public AbstractSimpleAffichageTest(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	protected IHM testIHM = new IHM() {
		@Override
		public String questionne(String question, Role type, String acteur) throws StopException {
			return null;
		}

		@Override
		public boolean effacer() throws StopException {
			return false;
		}

		@Override
		public String demander(Role type, String acteur) throws StopException {
			return null;
		}

		@Override
		public boolean afficher(String afficher, Role type) throws StopException {
			System.out.println(afficher);
			if (retour.length() != 0) {
				retour.append('\n');
			}
			retour.append(afficher);
			return true;
		}

		@Override
		public boolean afficherErreur(String afficher) {
			return false;
		}
	};

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		linotte = new Linotte(new LibrairieVirtuelleSyntaxeV2(), testIHM, Langage.Linotte2);
	}

	protected void executerLivreAvecEreur(LIVRE livre) {
		try {
			executerLivre(livre);
			Assert.fail();
		} catch (Exception e) {
			Assert.assertTrue(e.getMessage(), true);
		}
	}

	protected String executerLivre(Enum<?> flux) throws Exception {
		return executerLivre(flux, null);
	}

	protected String executerLivre(Enum<?> chaine, Set<Prototype> prototypes) throws Exception {

		retour.delete(0, retour.length());
		StringBuilder flux = new StringBuilder(entree(chaine));
		if (prototypes != null)
			for (Prototype prototype : prototypes) {
				linotte.especeModeleMap.add(prototype);
			}

		// Etape 1 : on parse le livre :
		ParserContext atelierOutils = new ParserContext(MODE.GENERATION_RUNTIME);
		atelierOutils.linotte = linotte;
		AKRuntime runtime = new Parseur().parseLivre(flux, atelierOutils);
		// Etape 2 : on prépare l'environnement d'exécution :
		ContextHelper.populate(runtime.getContext(), linotte, null, null);
		// Etape 3 : on exécute le livre :
		try {
			runtime.execute();
		} catch (StopException s) {
		}

		return retour.toString();

	}

	public String sortie(Enum<?> key) {
		return bundle.getString(key.name() + "_OUT");
	}

	public String entree(Enum<?> key) {
		return bundle.getString(key.name()) + "\n";
	}
}
