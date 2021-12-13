package org.linotte.greffons.impl.swing;

import org.linotte.greffons.externe.Composant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public abstract class ComposantSwing extends Composant {

	private boolean visible;

	protected List<ListenerGreffons> clic_souris = new ArrayList<ListenerGreffons>();

	protected List<ListenerGreffons> clic_droit_souris = new ArrayList<ListenerGreffons>();

	protected List<ListenerGreffons> souris_entrante = new ArrayList<ListenerGreffons>();
	
	protected List<ListenerGreffons> touche = new ArrayList<ListenerGreffons>();

	private MouseListener mouseListener;

	private ComposantSwing composantPere;

	public void initaccessibilite(JComponent xxx) throws GreffonException {
		Component component = getJComponent();
		xxx.setFocusable(true);
	}
	public void initEvenement() throws GreffonException {
		clic_souris.clear();
		souris_entrante.clear();
		clic_droit_souris.clear();
		touche.clear();

		Component component = getJComponent();
		removeMouseListener();

		// Action de l'action avec la touche entrée :

		mouseListener = new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {

				try {
					if (getJComponent() != null && getJComponent().isEnabled()) {

						if (e.getButton() == MouseEvent.BUTTON1) {
							for (ListenerGreffons listener : clic_souris) {
								listener.execute();
							}
						} else {
							for (ListenerGreffons listener : clic_droit_souris) {
								listener.execute();
							}
						}
					}
				} catch (GreffonException ge) {
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				try {
					if (getJComponent() != null && getJComponent().isEnabled()) {
						for (ListenerGreffons listener : souris_entrante) {
							listener.execute();
						}
					}
				} catch (GreffonException ge) {
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		};
		component.addMouseListener(mouseListener);
		//component.addKeyListener(keyListener);
		
		if (getAttribute("infobulle") != null && getAttributeAsString("infobulle").trim().length() > 0)
			((JComponent) component).setToolTipText(getAttributeAsString("infobulle"));
		else {
			if (component instanceof JComponent)
				((JComponent) component).setToolTipText(null);
		}
	}

	/**
	 * @return
	 * @throws GreffonException
	 */
	public void removeMouseListener() throws GreffonException {
		Component component = getJComponent();
		component.removeMouseListener(mouseListener);
		//component.removeKeyListener(keyListener);
	}

	@Override
	public void addClicListener(ListenerGreffons listener) {
		// Linotte est un langage de programmation simple :
		cleanClicListener();
		clic_souris.add(listener);
	}

	@Override
	public void addEnterListeners(ListenerGreffons listener) {
		// Linotte est un langage de programmation simple :
		cleanEnterListeners();
		souris_entrante.add(listener);
	}

	@Override
	public void addClicDroitListener(ListenerGreffons listener) {
		// Linotte est un langage de programmation simple : 
		cleanClicDroitListener();
		clic_droit_souris.add(listener);
	}

	@Override
	public void ajouterToucheListeners(ListenerGreffons listener) {
		cleanToucheListeners();
		touche.add(listener);
	}

	@Override
	public void cleanClicListener() {
		clic_souris.clear();
	}

	@Override
	public void cleanEnterListeners() {
		souris_entrante.clear();
	}

	@Override
	public void cleanClicDroitListener() {
		clic_droit_souris.clear();
	}

	@Override
	public void cleanToucheListeners() {
		touche.clear();
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public boolean fireProperty(String clef) throws GreffonException {
		if (getJComponent() != null && "visible".equals(clef)) {
			setVisible(getAttributeAsBigDecimal(clef).intValue()==1);
			Component component = getJComponent();
			component.setVisible(isVisible());
			return true;
		}
		if (getJComponent() != null && "infobulle".equals(clef)) {
			JComponent component = (JComponent) getJComponent();
			if (getAttributeAsString("infobulle").trim().length() > 0)
				component.setToolTipText(getAttributeAsString("infobulle"));
			else
				component.setToolTipText(null);
			component.setToolTipText(getAttributeAsString("infobulle"));
			return true;
		}
		return false;
	}

	public void setParent(ComposantSwing composant) {
		composantPere = composant;
	}

	public ComposantSwing getParent() {
		return composantPere;
	}

	/**
	 * Retourne le composant à afficher
	 * 
	 * @return
	 * @throws GreffonException
	 */
	public abstract Component getJComponent() throws GreffonException;

	@Slot(nom = "désactiver")
	public boolean desactiver(boolean etat) {
		try {
			getJComponent().setEnabled(!etat);
		} catch (Exception e) {
		}
		return true;
	}
}
