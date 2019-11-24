/***********************************************************************
 * Linotte                                                             *
 * Version release date : July 30, 2008                             *
 * Author : Mounes Ronan ronan.mounes@amstrad.eu                        *
 *                                                                     *
 *     http://langagelinotte.free.fr                                   *
 *                                                                     *
 * This code is released under the GNU GPL license, version 2 or       *
 * later, for educational and non-commercial purposes only.            *
 * If any part of the code is to be included in a commercial           *
 * software, please contact us first for a clearance at                *
 *   ronan.mounes@amstrad.eu                                            *
 *                                                                     *
 *   This notice must remain intact in all copies of this code.        *
 *   This code is distributed WITHOUT ANY WARRANTY OF ANY KIND.        *
 *   The GNU GPL license can be found at :                             *
 *           http://www.gnu.org/copyleft/gpl.html                      *
 *                                                                     *
 ***********************************************************************/

package org.linotte.frame.atelier;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * The <code>VerticalGridLayout</code> class is a layout manager that
 * lays out a container's components in a rectangular grid.
 * The container is divided into equal-sized rectangles, 
 * and one component is placed in each rectangle.
 * For example, the following is an applet that lays out six buttons 
 * into three rows and two columns: 
 * <p>
 * <hr><blockquote>
 * <pre>
 * import java.awt.*;
 * import java.applet.Applet;
 * public class ButtonGrid extends Applet {
 *     public void init() {
 *         setLayout(new VerticalGridLayout(3,2));
 *         add(new Button("1"));
 *         add(new Button("2"));
 *         add(new Button("3"));
 *         add(new Button("4"));
 *         add(new Button("5"));
 *         add(new Button("6"));
 *     }
 * }
 * </pre></blockquote><hr>     
 * <p>
 * If the container's <code>ComponentOrientation</code> property is horizontal
 * and left-to-right, the above example produces the output shown in Figure 1.
 * If the container's <code>ComponentOrientation</code> property is horizontal
 * and right-to-left, the example produces the output shown in Figure 2.
 * <p>
 * <center><table COLS=2 WIDTH=600 summary="layout">
 * <tr ALIGN=CENTER>
 * <td><img SRC="doc-files/VerticalGridLayout-1.gif"
 *      alt="Shows 6 buttons in rows of 2. Row 1 shows buttons 1 then 2.
 * Row 2 shows buttons 3 then 4. Row 3 shows buttons 5 then 6.">
 * </td>
 * 
 * <td ALIGN=CENTER><img SRC="doc-files/VerticalGridLayout-2.gif"
 *                   alt="Shows 6 buttons in rows of 2. Row 1 shows buttons 2 then 1.
 * Row 2 shows buttons 4 then 3. Row 3 shows buttons 6 then 5.">
 * </td>
 * </tr>
 * 
 * <tr ALIGN=CENTER>
 * <td>Figure 1: Horizontal, Left-to-Right</td>
 *
 * <td>Figure 2: Horizontal, Right-to-Left</td>
 * </tr>
 * </table></center>
 * <p>
 * When both the number of rows and the number of columns have 
 * been set to non-zero values, either by a constructor or 
 * by the <tt>setRows</tt> and <tt>setColumns</tt> methods, the number of 
 * columns specified is ignored.  Instead, the number of 
 * columns is determined from the specified number or rows 
 * and the total number of components in the layout. So, for
 * example, if three rows and two columns have been specified 
 * and nine components are added to the layout, they will 
 * be displayed as three rows of three columns.  Specifying
 * the number of columns affects the layout only when the 
 * number of rows is set to zero.
 *
 * @version 1.35, 01/23/03
 * @author  Arthur van Hoff
 * @since   JDK1.0
 */
public class VerticalGridLayout implements LayoutManager, java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5689450146746779791L;
	/**
	 * This is the horizontal gap (in pixels) which specifies the space
	 * between columns.  They can be changed at any time.
	 * This should be a non-negative integer.
	 *
	 * @serial
	 * @see #getHgap()
	 * @see #setHgap(int)
	 */
	int hgap;
	/**
	 * This is the vertical gap (in pixels) which specifies the space
	 * between rows.  They can be changed at any time.
	 * This should be a non negative integer.
	 *
	 * @serial
	 * @see #getVgap()
	 * @see #setVgap(int)
	 */
	int vgap;
	/**
	 * This is the number of rows specified for the grid.  The number
	 * of rows can be changed at any time.
	 * This should be a non negative integer, where '0' means
	 * 'any number' meaning that the number of Rows in that
	 * dimension depends on the other dimension.
	 *
	 * @serial
	 * @see #getRows()
	 * @see #setRows(int)
	 */
	int rows;
	/**
	 * This is the number of columns specified for the grid.  The number
	 * of columns can be changed at any time.
	 * This should be a non negative integer, where '0' means
	 * 'any number' meaning that the number of Columns in that
	 * dimension depends on the other dimension.
	 *
	 * @serial
	 * @see #getColumns()
	 * @see #setColumns(int)
	 */
	int cols;

	/**
	 * Creates a grid layout with a default of one column per component,
	 * in a single row.
	 * @since JDK1.1
	 */
	public VerticalGridLayout() {
		this(1, 0, 0, 0);
	}

	/**
	 * Creates a grid layout with the specified number of rows and 
	 * columns. All components in the layout are given equal size. 
	 * <p>
	 * One, but not both, of <code>rows</code> and <code>cols</code> can 
	 * be zero, which means that any number of objects can be placed in a 
	 * row or in a column. 
	 * @param     rows   the rows, with the value zero meaning 
	 *                   any number of rows.
	 * @param     cols   the columns, with the value zero meaning 
	 *                   any number of columns.
	 */
	public VerticalGridLayout(int rows, int cols) {
		this(rows, cols, 0, 0);
	}

	/**
	 * Creates a grid layout with the specified number of rows and 
	 * columns. All components in the layout are given equal size. 
	 * <p>
	 * In addition, the horizontal and vertical gaps are set to the 
	 * specified values. Horizontal gaps are placed at the left and 
	 * right edges, and between each of the columns. Vertical gaps are 
	 * placed at the top and bottom edges, and between each of the rows. 
	 * <p>
	 * One, but not both, of <code>rows</code> and <code>cols</code> can 
	 * be zero, which means that any number of objects can be placed in a 
	 * row or in a column. 
	 * <p>
	 * All <code>VerticalGridLayout</code> constructors defer to this one.
	 * @param     rows   the rows, with the value zero meaning 
	 *                   any number of rows
	 * @param     cols   the columns, with the value zero meaning
	 *                   any number of columns
	 * @param     hgap   the horizontal gap
	 * @param     vgap   the vertical gap
	 * @exception   IllegalArgumentException  if the value of both
	 *			<code>rows</code> and <code>cols</code> is 
	 *			set to zero
	 */
	public VerticalGridLayout(int rows, int cols, int hgap, int vgap) {
		if ((rows == 0) && (cols == 0)) {
			throw new IllegalArgumentException("rows and cols cannot both be zero");
		}
		this.rows = rows;
		this.cols = cols;
		this.hgap = hgap;
		this.vgap = vgap;
	}

	/**
	 * Gets the number of rows in this layout.
	 * @return    the number of rows in this layout
	 * @since     JDK1.1
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Sets the number of rows in this layout to the specified value.
	 * @param        rows   the number of rows in this layout
	 * @exception    IllegalArgumentException  if the value of both 
	 *               <code>rows</code> and <code>cols</code> is set to zero
	 * @since        JDK1.1
	 */
	public void setRows(int rows) {
		if ((rows == 0) && (this.cols == 0)) {
			throw new IllegalArgumentException("rows and cols cannot both be zero");
		}
		this.rows = rows;
	}

	/**
	 * Gets the number of columns in this layout.
	 * @return     the number of columns in this layout
	 * @since      JDK1.1
	 */
	public int getColumns() {
		return cols;
	}

	/**
	 * Sets the number of columns in this layout to the specified value. 
	 * Setting the number of columns has no affect on the layout 
	 * if the number of rows specified by a constructor or by 
	 * the <tt>setRows</tt> method is non-zero. In that case, the number
	 * of columns displayed in the layout is determined by the total 
	 * number of components and the number of rows specified.
	 * @param        cols   the number of columns in this layout
	 * @exception    IllegalArgumentException  if the value of both 
	 *               <code>rows</code> and <code>cols</code> is set to zero
	 * @since        JDK1.1
	 */
	public void setColumns(int cols) {
		if ((cols == 0) && (this.rows == 0)) {
			throw new IllegalArgumentException("rows and cols cannot both be zero");
		}
		this.cols = cols;
	}

	/**
	 * Gets the horizontal gap between components.
	 * @return       the horizontal gap between components
	 * @since        JDK1.1
	 */
	public int getHgap() {
		return hgap;
	}

	/**
	 * Sets the horizontal gap between components to the specified value.
	 * @param        hgap   the horizontal gap between components
	 * @since        JDK1.1
	 */
	public void setHgap(int hgap) {
		this.hgap = hgap;
	}

	/**
	 * Gets the vertical gap between components.
	 * @return       the vertical gap between components
	 * @since        JDK1.1
	 */
	public int getVgap() {
		return vgap;
	}

	/**
	 * Sets the vertical gap between components to the specified value.
	 * @param         vgap  the vertical gap between components
	 * @since        JDK1.1
	 */
	public void setVgap(int vgap) {
		this.vgap = vgap;
	}

	/**
	 * Adds the specified component with the specified name to the layout.
	 * @param name the name of the component
	 * @param comp the component to be added
	 */
	public void addLayoutComponent(String name, Component comp) {
	}

	/**
	 * Removes the specified component from the layout.
	 * @param comp the component to be removed
	 */
	public void removeLayoutComponent(Component comp) {
	}

	/**
	 * Determines the preferred size of the container argument using
	 * this grid layout.
	 * <p>
	 * The preferred width of a grid layout is the largest preferred
	 * width of any of the components in the container times the number of
	 * columns, plus the horizontal padding times the number of columns
	 * plus one, plus the left and right insets of the target container.
	 * <p>
	 * The preferred height of a grid layout is the largest preferred
	 * height of any of the components in the container times the number of
	 * rows, plus the vertical padding times the number of rows plus one,
	 * plus the top and bottom insets of the target container.
	 *
	 * @param     parent   the container in which to do the layout
	 * @return    the preferred dimensions to lay out the
	 *                      subcomponents of the specified container
	 * @see       org.linotte.frame.atelier.VerticalGridLayout#minimumLayoutSize
	 * @see       java.awt.Container#getPreferredSize()
	 */
	public Dimension preferredLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = rows;
			int ncols = cols;

			//  following code was replaced 3 times
			//	if (nrows > 0) {
			//	    ncols = (ncomponents + nrows - 1) / nrows;
			//	} else {
			//	    nrows = (ncomponents + ncols - 1) / ncols;
			//	}
			if (ncols > 0) {
				nrows = (ncomponents + ncols - 1) / ncols;
				if (nrows == 1)
					ncols = ncomponents; // this limits the width
			} else {
				ncols = (ncomponents + nrows - 1) / nrows;
				if (ncols == 1)
					nrows = ncomponents; // this limits the height
			}
			int w = 0;
			int h = 0;
			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				Dimension d = comp.getPreferredSize();
				if (w < d.width) {
					w = d.width;
				}
				if (h < d.height) {
					h = d.height;
				}
			}
			return new Dimension(insets.left + insets.right + ncols * w + (ncols - 1) * hgap, insets.top + insets.bottom + nrows * h + (nrows - 1) * vgap);
		}
	}

	/**
	 * Determines the minimum size of the container argument using this
	 * grid layout.
	 * <p>
	 * The minimum width of a grid layout is the largest minimum width
	 * of any of the components in the container times the number of columns,
	 * plus the horizontal padding times the number of columns plus one,
	 * plus the left and right insets of the target container.
	 * <p>
	 * The minimum height of a grid layout is the largest minimum height
	 * of any of the components in the container times the number of rows,
	 * plus the vertical padding times the number of rows plus one, plus
	 * the top and bottom insets of the target container.
	 *
	 * @param       parent   the container in which to do the layout
	 * @return      the minimum dimensions needed to lay out the
	 *                      subcomponents of the specified container
	 * @see         org.linotte.frame.atelier.VerticalGridLayout#preferredLayoutSize
	 * @see         java.awt.Container#doLayout
	 */
	public Dimension minimumLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = rows;
			int ncols = cols;

			if (ncols > 0) {
				nrows = (ncomponents + ncols - 1) / ncols;
				if (nrows == 1)
					ncols = ncomponents;
			} else {
				ncols = (ncomponents + nrows - 1) / nrows;
				if (ncols == 1)
					nrows = ncomponents;
			}

			int w = 0;
			int h = 0;
			for (int i = 0; i < ncomponents; i++) {
				Component comp = parent.getComponent(i);
				Dimension d = comp.getMinimumSize();
				if (w < d.width) {
					w = d.width;
				}
				if (h < d.height) {
					h = d.height;
				}
			}
			return new Dimension(insets.left + insets.right + ncols * w + (ncols - 1) * hgap, insets.top + insets.bottom + nrows * h + (nrows - 1) * vgap);
		}
	}

	/**
	 * Lays out the specified container using this layout.
	 * <p>
	 * This method reshapes the components in the specified target
	 * container in order to satisfy the constraints of the
	 * <code>VerticalGridLayout</code> object.
	 * <p>
	 * The grid layout manager determines the size of individual
	 * components by dividing the free space in the container into
	 * equal-sized portions according to the number of rows and columns
	 * in the layout. The container's free space equals the container's
	 * size minus any insets and any specified horizontal or vertical
	 * gap. All components in a grid layout are given the same size.
	 *
	 * @param      parent   the container in which to do the layout
	 * @see        java.awt.Container
	 * @see        java.awt.Container#doLayout
	 */
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = parent.getComponentCount();
			int nrows = rows;
			int ncols = cols;
			boolean ltr = parent.getComponentOrientation().isLeftToRight();

			if (ncomponents == 0) {
				return;
			}

			if (ncols > 0) {
				nrows = (ncomponents + ncols - 1) / ncols;
				if (nrows == 1)
					ncols = ncomponents;
			} else {
				ncols = (ncomponents + nrows - 1) / nrows;
				if (ncols == 1)
					nrows = ncomponents;
			}

			//	int w = parent.width - (insets.left + insets.right);
			//	int h = parent.height - (insets.top + insets.bottom);
			int w = parent.getSize().width - (insets.left + insets.right);
			int h = parent.getSize().height - (insets.top + insets.bottom);
			w = (w - (ncols - 1) * hgap) / ncols;
			h = (h - (nrows - 1) * vgap) / nrows;

			if (ltr) {
				for (int c = 0, x = insets.left; c < ncols; c++, x += w + hgap) {
					for (int r = 0, y = insets.top; r < nrows; r++, y += h + vgap) {
						//		    int i = r * ncols + c;
						int i = c * nrows + r;
						if (i < ncomponents) {
							parent.getComponent(i).setBounds(x, y, w, h);
						}
					}
				}
			} else {
				//	    for (int c = 0, x = parent.width - insets.right - w; c < ncols ; c++, x -= w + hgap) {
				for (int c = 0, x = parent.getSize().width - insets.right - w; c < ncols; c++, x -= w + hgap) {
					for (int r = 0, y = insets.top; r < nrows; r++, y += h + vgap) {
						//		    int i = r * ncols + c;
						int i = c * nrows + r;
						if (i < ncomponents) {
							parent.getComponent(i).setBounds(x, y, w, h);
						}
					}
				}
			}
		}

	}

	/**
	 * Returns the string representation of this grid layout's values.
	 * @return     a string representation of this grid layout
	 */
	@Override
	public String toString() {
		return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + ",rows=" + rows + ",cols=" + cols + "]";
	}
}
