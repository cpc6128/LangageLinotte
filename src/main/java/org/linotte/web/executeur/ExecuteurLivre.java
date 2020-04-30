package org.linotte.web.executeur;

import console.Jinotte;
import org.alize.http.i.Executeur;
import org.alize.http.i.FichierTraitement;
import org.alize.http.i.WebTransformateur;
import org.alize.kernel.AKLoader;
import org.alize.kernel.AKPatrol;
import org.alize.kernel.AKRuntime;
import org.linotte.frame.moteur.Formater;
import org.linotte.implementations.LibrairieVirtuelleSyntaxeV2;
import org.linotte.moteur.entites.Role;
import org.linotte.moteur.exception.LectureException;
import org.linotte.moteur.exception.Messages;
import org.linotte.moteur.exception.StopException;
import org.linotte.moteur.outils.FichierOutils;
import org.linotte.moteur.xml.Linotte;
import org.linotte.moteur.xml.alize.kernel.ContextHelper;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.security.Habilitation;
import org.linotte.moteur.xml.alize.parseur.ParserContext;
import org.linotte.moteur.xml.alize.parseur.ParserContext.MODE;
import org.linotte.moteur.xml.alize.parseur.Parseur;
import org.linotte.moteur.xml.analyse.multilangage.Langage;
import org.linotte.moteur.xml.api.IHM;
import org.linotte.moteur.xml.api.Librairie;
import org.linotte.web.transformateur.HTMLFrancaisTransformateur;
import org.linotte.web.transformateur.WebLivreTransformateur;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * Moteur d'exécution du Webonotte
 */
public class ExecuteurLivre implements Executeur {

    Librairie<LibrairieVirtuelleSyntaxeV2> lib = new LibrairieVirtuelleSyntaxeV2();
    private List<WebTransformateur> transformateurs = new ArrayList<WebTransformateur>();
    /**
     * Gestion du cache des runtimes
     */
    private Timer timer;
    private Map<String, AKRuntime> cache = Collections.synchronizedMap(new HashMap<String, AKRuntime>());
    private Map<String, Long> cache_age = new HashMap<String, Long>();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            /**
             * On supprime du cache les livres modifiés
             */
            List<String> aSupprimer = new ArrayList<String>();
            synchronized (cache) {
                for (String f : cache.keySet()) {
                    long age = cache_age.get(f);
                    if (new File(f).lastModified() != age) {
                        aSupprimer.add(f);
                    }
                }
            }
            for (String f : aSupprimer) {
                cache.remove(f);
                cache_age.remove(f);
            }
        }
    };

    /**
     * Version standalone
     */
    public ExecuteurLivre() {
        createCleaner();
    }

    public ExecuteurLivre(Librairie<?> lib) {
        createCleaner();
    }

    private void createCleaner() {
        timer = new Timer();
        timer.schedule(task, 1000, 2000);
        transformateurs.add(new WebLivreTransformateur());
        transformateurs.add(new HTMLFrancaisTransformateur());
    }

    public StringBuilder traiter(FichierTraitement plivre, final Map<String, String> pacteurs, Collection<Habilitation> habilitationsHeritees, String plangage)
            throws Exception {

        final boolean format = Boolean.parseBoolean(pacteurs.get("wbt_html"));
        boolean source = Boolean.parseBoolean(pacteurs.get("wbt_source"));
        final StringBuilder sortie = new StringBuilder();
        List<Integer> numerolignes = new ArrayList<Integer>();
        if (format)
            sortie.append("<HTML><HEAD><HEAD><BODY>");
        String retourChariot = format ? "<br/>" : "";
        if (habilitationsHeritees.contains(Habilitation.LITTLE_BIRD))
            retourChariot = "\n"; // Pour que ça s'affiche correctement dans la console javascript
        IHM ihm = new IHMWeb(pacteurs, sortie, retourChariot);
        AKRuntime runtime = cache.get(plivre.getAbsolutePath());

        Linotte linotte = new Linotte(lib, ihm, Langage.Linotte2);

        if (source) {
            runtime = null;
        }
        if (runtime == null) {
            Parseur moteurXML = new Parseur();
            ParserContext parserContext = new ParserContext(MODE.GENERATION_RUNTIME);
            parserContext.linotte = linotte;
            parserContext.webonotte = true;
            StringBuilder entree = FichierOutils.lire(plivre.getInputStream(), numerolignes);
            if (source) {
                String formater = Formater.action(entree.toString(), linotte);
                StringBuilder source_html = new StringBuilder("<html><head><title>" + plivre.getAbsolutePath() + "</title></head><body>");
                source_html.append("<script src=\"https://cdn.rawgit.com/google/code-prettify/master/loader/run_prettify.js\"></script>");//?lang=php&skin=desert
                source_html.append("<style>.str {background-color: aliceblue;}</style>");
                source_html.append("<pre class=\"prettyprint\">");
                source_html.append(formater.replaceAll("<", "&lt;").replaceAll("\n", "<br/>").replaceAll("\t", "&nbsp;&nbsp;"));
                source_html.append("</pre>");
                source_html.append("</body></html>");
                return source_html;
            }
            // On le transforme ?
            for (WebTransformateur transformateur : transformateurs) {
                if (transformateur.accepter(plivre.getAbsolutePath())) {
                    entree = transformateur.traiter(entree);
                }
            }
            try {
                runtime = moteurXML.parseLivre(entree, parserContext);
                //Patch pour ne pas être visible dans l'audit
                AKPatrol.runtimes.remove(runtime);

            } catch (LectureException e) {
                String message = "Ligne : " + Jinotte.retourneLaLigne(numerolignes, e.getPosition()) + " / "
                        + Messages.retourneErreur(String.valueOf(e.getErreur()));
                if (e.getException().getToken() != null)
                    message += " : " + e.getException().getToken();
                throw new Exception(message, e);
            }

            List<Habilitation> habilitations = new ArrayList<Habilitation>(habilitationsHeritees);
            habilitations.add(Habilitation.COOKIES_ACCESS);
            habilitations.add(Habilitation.TOILE_INVISIBLE);

            ContextHelper.populate(runtime.getContext(), linotte, plivre.getFile(), habilitations);
            if (plivre.lastModified() != 0) {
                cache.put(plivre.getAbsolutePath(), runtime);
                cache_age.put(plivre.getAbsolutePath(), plivre.lastModified());
            }
        }

        AKRuntime cloner = AKLoader.produceRuntime(runtime);

        // Patch pour mettre à jour l'IHM
        RuntimeContext runtimecontext = (RuntimeContext) cloner.getContext();
        runtimecontext.setIhm(ihm);

        try {
            cloner.execute();
        } catch (LectureException e) {
            String message = "Ligne : " + Jinotte.retourneLaLigne(numerolignes, e.getPosition()) + " / "
                    + Messages.retourneErreur(String.valueOf(e.getErreur()));
            if (e.getException().getToken() != null)
                message += " : " + e.getException().getToken();
            throw new Exception(message, e);
        } catch (StopException e) {
        } finally {

        }

        if (format)
            sortie.append("</BODY></HTML>");

        return sortie;
    }

    @Override
    public boolean accepter(String nom) {
        boolean t1 = nom.equalsIgnoreCase("/essayer") || nom.toLowerCase().endsWith(".liv") || nom.toLowerCase().endsWith(".wliv")
                || nom.equalsIgnoreCase("/notice") || nom.equalsIgnoreCase("/favicon.ico");
        boolean t2 = nom.startsWith("/b_tutoriel/") && t1;
        return t1 || t2;
    }

    private class IHMWeb implements IHM {

        Map<String, String> ihmActeurs;

        String retourChariot;

        StringBuilder sortie;

        IHMWeb(Map<String, String> pacteurs, StringBuilder psortie, String pretourChariot) {
            ihmActeurs = pacteurs;
            sortie = psortie;
            retourChariot = pretourChariot;
        }

        public String questionne(String s, Role role, String acteur) throws StopException {
            String valeur = ihmActeurs.get(acteur);
            if (role == Role.NOMBRE) {
                valeur = traiterNombre(valeur);
            }
            return valeur;
        }

        private String traiterNombre(String valeur) {
            try {
                BigDecimal result = new BigDecimal(valeur);
                return result.toString();
            } catch (Exception e1) {
                return "0";
            }
        }

        public boolean effacer() throws StopException {
            return false;
        }

        public String demander(Role role, String acteur) throws StopException {
            String valeur = ihmActeurs.get(acteur);
            if (role == Role.NOMBRE) {
                valeur = traiterNombre(valeur);
            }
            return valeur;
        }

        public boolean afficherErreur(String s) {
            return false;
        }

        public boolean afficher(String s, Role role) throws StopException {
            if (retourChariot.equals("\n")) {
                // Blocnotte
                if (sortie.length() > 0)
                    sortie.append(retourChariot);
                sortie.append(s);
            } else {
                sortie.append(s);
                sortie.append(retourChariot);
            }
            return true;
        }

    }

}