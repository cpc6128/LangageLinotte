package org.linotte.moteur.xml.outils;

/**
 * 
 * JCommon : a free Java report library
 * 
 *
 * Project Info:  http://www.jfree.org/jcommon/
 *
 * (C) Copyright 2000-2006, by Object Refinery Limited and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * $Id: FastStack.java,v 1.3 2008/09/10 09:22:05 mungady Exp $
 * ------------
 * (C) Copyright 2002-2006, by Object Refinery Limited.
 */

import java.io.Serializable;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Iterator;

import org.linotte.moteur.outils.ArrayIterator;

/**
 * A very simple unsynchronized stack. This one is faster than the
 * java.util-Version.
 * 
 * @author Thomas Morgner
 */
@SuppressWarnings("serial")
public class FastStack implements Serializable, Cloneable {

	private Object[] contents;

	private int size;

	private int initialSize;

	/**
	 * Creates a new empty stack.
	 */
	public FastStack() {
		initialSize = 30;
		contents = new Object[initialSize];
	}

	/**
	 * Returns <code>true</code> if the stack is empty, and <code>false</code>
	 * otherwise.
	 * 
	 * @return A boolean.
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the number of elements in the stack.
	 * 
	 * @return The element count.
	 */
	public int size() {
		return size;
	}

	/**
	 * Pushes an object onto the stack.
	 * 
	 * @param o
	 *          the object.
	 */
	public void push(Object o) {
		final int oldSize = size;
		size += 1;
		if (contents.length == size) {
			// grow ..
			final Object[] newContents = new Object[size + initialSize];
			System.arraycopy(contents, 0, newContents, 0, size);
			contents = newContents;
		}
		contents[oldSize] = o;
	}

	/**
	 * Returns the object at the top of the stack without removing it.
	 * 
	 * @return The object at the top of the stack.
	 */
	public Object peek() {
		//		if (size == 0) {
		//			throw new EmptyStackException();
		//		}
		return contents[size - 1];
	}

	/**
	 * Removes and returns the object from the top of the stack.
	 * 
	 * @return The object.
	 */
	public Object pop() {
		if (size == 0) {
			throw new EmptyStackException();
		}
		size -= 1;
		final Object retval = contents[size];
		contents[size] = null;
		return retval;
	}

	/**
	 * Returns a clone of the stack.
	 * 
	 * @return A clone.
	 */
	public Object clone() {
		try {
			FastStack stack = (FastStack) super.clone();
			if (contents != null) {
				stack.contents = (Object[]) contents.clone();
			}
			return stack;
		} catch (CloneNotSupportedException cne) {
			throw new IllegalStateException("Clone not supported? Why?");
		}
	}

	/**
	 * Clears the stack.
	 */
	public void clear() {
		size = 0;
		Arrays.fill(contents, null);
	}

	/**
	 * Returns the item at the specified slot in the stack.
	 * 
	 * @param index
	 *          the index.
	 * 
	 * @return The item.
	 */
	public Object get(final int index) {
		//		if (index >= size) {
		//			throw new IndexOutOfBoundsException();
		//		}
		return contents[index];
	}

	public Iterator<Object> iterator() {
		Object[] shrink = new Object[size];
		System.arraycopy(contents, 0, shrink, 0, size);
		return new ArrayIterator<Object>(shrink);
	}
	/*
		@SuppressWarnings("rawtypes")
		public Object close(Class c) {
			Object retval;
			for (; size > 0;) {
				size -= 1;
				retval = contents[size];
				contents[size] = null;
				if (retval.getClass() == c)
					return retval;
			}
			return null;
		}

		@SuppressWarnings("rawtypes")
		public Object find(Class c) {
			Object retval;
			for (int s = size; s > 0;) {
				s -= 1;
				retval = contents[s];
				if (retval.getClass() == c)
					return retval;
			}
			return null;
		}
	*/
}
