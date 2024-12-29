/* @(#)Constrainer.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw;

import javax.swing.event.ChangeListener;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A <em>constrainer</em> constrains editing operations performed by
 * {@link org.jhotdraw.draw.tool.Tool}s and
 * {@link org.jhotdraw.draw.handle.Handle}s on a {@link DrawingView}.
 * <p>
 * {@code Constrainer} objects are associated to {@code DrawingView}'s.
 * <p>
 * Constrainers can draw themselves onto the drawing view to visualize the
 * constraints that they impose. Typically by drawing a grid of some kind.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Strategy</em><br>
 * Editing can be constrained by a constrainer which is associated to a
 * drawing view.<br>
 * Context: {@link DrawingView}; Strategy: {@link Constrainer}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Constrainer {

    /**
     * Constrains the placement of a point towards the closest constraint
     * in any direction.
     * <p>
     * This method changes the point which is passed as a parameter.
     *
     * @param p A point on the drawing.
     * @return Returns the constrained point.
     */
    public Point2D.Double constrainPoint(Point2D.Double p);

    /**
     * Moves a point to the closest constrained location in the specified
     * direction.
     * <p>
     * This method changes the point which is passed as a parameter.
     *
     * @param p   A point on the drawing.
     * @param dir A direction.
     * @return Returns the constrained point.
     */
    public Point2D.Double translatePoint(Point2D.Double p, TranslationDirection dir);

    /**
     * Constrains the placement of a rectangle towards the closest constrainment
     * in any direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method does not change the size of the rectangle.
     *
     * @param r A rectangle on the drawing.
     * @return Returns the constrained rectangle.
     */
    public Rectangle2D.Double constrainRectangle(Rectangle2D.Double r);

    /**
     * Moves a rectangle to the closest constrained location in the
     * specified direction.
     * <p>
     * This method changes the location of the rectangle which is passed as a
     * parameter. This method only performs a translation - it does not change
     * the size of the rectangle.
     *
     * @param r   A rectangle on the drawing.
     * @param dir A direction.
     * @return Returns the constrained rectangle.
     */
    public Rectangle2D.Double translateRectangle(Rectangle2D.Double r, TranslationDirection dir);

    /**
     * Constrains the given angle (in radians).
     * This method changes the angle which is passed as a parameter.
     *
     * @param angle The angle (in radians).
     * @return The closest constrained angle (in radians).
     */
    public double constrainAngle(double angle);

    /**
     * Moves the given angle (in radians) to the closest constrained orientation
     * in the specified direction.
     *
     * @param angle The angle (in radians).
     * @return The closest constrained angle (in radians) in the specified
     * direction.
     */
    public double rotateAngle(double angle, RotationDirection dir);

    /**
     * Draws the constrainer grid for the specified drawing view.
     */
    public void draw(Graphics2D g, DrawingView view);

    /**
     * Adds a change listener.
     */
    public void addChangeListener(ChangeListener listener);

    /**
     * Removes a change listener.
     */
    public void removeChangeListener(ChangeListener listener);
}
