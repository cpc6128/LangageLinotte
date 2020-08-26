/***********************************************************************
 * Linotte                                                             *
 * Version release date : June 18, 2014                                *
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

package org.linotte.frame.moteur;

import org.alize.kernel.AKJob;
import org.alize.kernel.AKProcessus;
import org.linotte.frame.atelier.Atelier;
import org.linotte.frame.atelier.Inspecteur;
import org.linotte.frame.cahier.Cahier;
import org.linotte.moteur.outils.Preference;
import org.linotte.moteur.outils.RuntimeConsole;
import org.linotte.moteur.xml.alize.kernel.Job;
import org.linotte.moteur.xml.alize.kernel.JobContext;
import org.linotte.moteur.xml.alize.kernel.RuntimeContext;
import org.linotte.moteur.xml.alize.kernel.i.AKDebugger;
import org.linotte.moteur.xml.alize.kernel.processus.Processus;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Ce debogueur affiche l'inspecteur et adapte l'atelier
 *
 * @author cpc
 */
public class Debogueur implements AKDebugger {

    public static final String LIRE_AU_RALENTI = "Lire au ralenti !";
    private static final String PAS_A_PAS = "Pas à pas !";
    private static final String TOOLTIP_BOUTON_PAS_A_PAS = "<html><b>Bouton Pas à pas</b><br><i>Description</i> : Exécute la prochaine action.<br><i>Action</i> : Cliquer sur le bouton [Pas à pas !] ou appuyer sur les boutons Alt+A<br></html>";
    public static final String TOOLTIP_BOUTON_RALENTI = "<html><b>Bouton Lire au ralenti</b><br><i>Description</i> : Exécute le livre séquentiellement.<br>Vitesse modifiable à partir du menu </b>Outils</b><br><i>Action</i> : Cliquer sur le bouton [Lire au ralenti !] ou appuyer sur les boutons Alt+A<br></html>";

    private static final Lock semaphore = new ReentrantLock();

    private Atelier atelier;

    private List<String> exclusion = Arrays.asList("test unitaire", "fin importation", "§ fin", "§ début", "actions_début_fonction", "creation paragraphe");

    public Debogueur(final Atelier atelier) {
        this.atelier = atelier;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.linotte.moteur.xml.alize.kernel.i.AKDebugger#showDebugger(int,
     * org.alize.kernel.AKRuntimeContextI, org.alize.kernel.AKJobContext,
     * org.alize.kernel.AKProcessus)
     */
    @Override
    public synchronized void showDebugger(final int delay, final AKJob job, final AKProcessus akProcessus) {
        final RuntimeContext runtimeContext = (RuntimeContext) job.getRuntimeContext();
        final JobContext jobContext = (JobContext) job.getContext();
        final Processus currentProcessus = (Processus) akProcessus;
        final Cahier cahier = runtimeContext.cahier;

        if (currentProcessus == null || exclusion.contains(currentProcessus.toString())) {
            return;
        }

        semaphore.lock();

        try {
            if (runtimeContext.getLibrairie().getToilePrincipale().isAtelier()) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            final Inspecteur inspecteur = atelier.getInspecteur();
                            inspecteur.enAttente = true;
                            atelier.getJButtonContinuer().setVisible(true);
                            if (delay == -1) {
                                // Mode pas à pas
                                atelier.getJButtonPause().setVisible(true);
                                atelier.getJButtonPause().setEnabled(true);
                                atelier.getJButtonLire().setVisible(false);
                                if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
                                    atelier.getJButtonPause().setToolTipText(Debogueur.TOOLTIP_BOUTON_PAS_A_PAS);
                                atelier.getJButtonPause().setText(PAS_A_PAS);
                                atelier.getJButtonContinuer().setVisible(true);
                                atelier.getJButtonContinuer().setEnabled(true);
                            } else {
                                // Mode séquentiel
                                if (!Preference.getIntance().getBoolean(Preference.P_BULLE_AIDE_INACTIF))
                                    atelier.getJButtonPause().setToolTipText(Debogueur.TOOLTIP_BOUTON_RALENTI);
                                atelier.getJButtonPause().setText(LIRE_AU_RALENTI);
                                atelier.getJButtonContinuer().setEnabled(false);
                            }
                            if (!inspecteur.isVisible())
                                inspecteur.setVisible(true);
                            if (currentProcessus != null) {
                                inspecteur.getLabelDebogage().setText(
                                        "Prochaine action : " + currentProcessus.getAction().clef() + " - job#" + ((Job) job).idTechnique);
                            }
                            try {
                                inspecteur.refresh(jobContext);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            if (!atelier.isVisible())
                                atelier.setVisible(true);

                            // Affichage sur l'atelier :
                            // Patch pour la gestion des timbres
                            // A optimiser
                            List<Integer> numerolignes = new ArrayList<Integer>();
                            try {
                                FrameProcess.lire(runtimeContext.buffer, numerolignes);
                            } catch (IOException ei) {
                                // Tant pis pour le numéro de ligne..
                            }
                            int nbligne = RuntimeConsole.retourneLaLigne(numerolignes, currentProcessus.getPosition());

                            if (cahier.isVisualiserTimbre()) {
                                cahier.getjPanelTimbre().setTimbreDeboguage(nbligne);
                            } else {
                                cahier.setPositionCahierAndScrollToMiddle(currentProcessus.getPosition());
                                cahier.getEditorPanelCahier().getLineHighlighter().setPositionDeboguage(currentProcessus.getPosition());
                            }

                        } catch (BadLocationException e) {
                        }
                    }
                });
                synchronized (job) {
                    if (delay == -1)
                        job.wait();
                    else
                        job.wait(delay);
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            cahier.repaint();
                        }
                    });
                }
            }
        } catch (InterruptedException e) {
            // e.printStackTrace();
        } finally {
            atelier.getInspecteur().enAttente = false;
            semaphore.unlock();

        }
    }


}