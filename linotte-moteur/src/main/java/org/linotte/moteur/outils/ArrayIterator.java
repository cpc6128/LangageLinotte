package org.linotte.moteur.outils;

import java.util.Iterator;

public class ArrayIterator<P> implements Iterator<P> {

	private P[] tab;

	private int pos = 0, size;

	public ArrayIterator(P[] p) {
		tab = p;
		size = tab.length;
	}

	@Override
	public boolean hasNext() {
		return pos < size;
	}

	@Override
	public P next() {
		return tab[pos++];
	}

	@Override
	public void remove() {
	}

}
