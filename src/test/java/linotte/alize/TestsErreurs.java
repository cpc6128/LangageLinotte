/**
 * 
 */
package linotte.alize;

import java.util.ResourceBundle;

import org.junit.Test;

/**
 * @author CPC
 *
 */
public class TestsErreurs extends AbstractSimpleAffichageTest {

	public TestsErreurs() {
		super(ResourceBundle.getBundle("livres.tests_avec_erreurs"));
	}

	enum LIVRE {
		FONCTION_PARAMETRE, FONCTION2_PARAMETRE, FONCTION3_PARAMETRE
	}

	@Test
	public void fonctionParametres() throws Exception {
		executerLivreAvecEreur(LIVRE.FONCTION_PARAMETRE);
	}

	@Test
	public void fonction2Parametres() throws Exception {
		executerLivreAvecEreur(LIVRE.FONCTION2_PARAMETRE);
	}

	@Test
	public void fonction3Parametres() throws Exception {
		executerLivreAvecEreur(LIVRE.FONCTION3_PARAMETRE);
	}

}
