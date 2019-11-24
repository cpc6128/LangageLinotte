package org.linotte.moteur.entites.ecouteurs;

import org.linotte.greffons.externe.Greffon.GreffonValeurListener;
import org.linotte.moteur.entites.Prototype;
import org.linotte.moteur.exception.ErreurException;

public class GreffonValeurListenerImpl implements GreffonValeurListener {

	private Prototype miroir = null;

	public GreffonValeurListenerImpl(Prototype a) {
		miroir = a;
	}

	public void setValeur(String clef, Object valeur) {
		try {
			// Patch pour alléger la gestion des valeurs avec les greffons :
			// http://langagelinotte.free.fr/forum/showthread.php?tid=1002
			miroir.retourneAttribut(clef).setValeurSimple(valeur);
		} catch (ErreurException e) {
			e.printStackTrace();
		}
	}
}
