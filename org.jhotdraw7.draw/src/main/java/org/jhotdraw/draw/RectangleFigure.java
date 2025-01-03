/* @(#)RectangleFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */


package org.jhotdraw.draw;

import org.jhotdraw.geom.Geom;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A {@link Figure} with a rectangular shape.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RectangleFigure extends AbstractAttributedFigure {
    private static final long serialVersionUID = 1L;
    protected Rectangle2D.Double rectangle;

    /**
     * Creates a new instance.
     */
    public RectangleFigure() {
        this(0, 0, 0, 0);
    }

    public RectangleFigure(double x, double y, double width, double height) {
        rectangle = new Rectangle2D.Double(x, y, width, height);
    }

    // DRAWING
    @Override
    protected void drawFill(Graphics2D g) {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularFillGrowth(this);
        Geom.grow(r, grow, grow);
        g.fill(r);
    }

    @Override
    protected void drawStroke(Graphics2D g) {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
        Geom.grow(r, grow, grow);

        g.draw(r);
    }

    // SHAPE AND BOUNDS
    @Override
    public Rectangle2D.Double getBounds() {
        Rectangle2D.Double bounds = (Rectangle2D.Double) rectangle.clone();
        return bounds;
    }

    @Override
    public Rectangle2D.Double getDrawingArea() {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularHitGrowth(this) + 1d;
        Geom.grow(r, grow, grow);
        return r;
    }

    /**
     * Checks if a Point2D.Double is inside the figure.
     */
    @Override
    public boolean contains(Point2D.Double p) {
        Rectangle2D.Double r = (Rectangle2D.Double) rectangle.clone();
        double grow = AttributeKeys.getPerpendicularHitGrowth(this) + 1d;
        Geom.grow(r, grow, grow);
        return r.contains(p);
    }

    @Override
    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
        rectangle.x = Math.min(anchor.x, lead.x);
        rectangle.y = Math.min(anchor.y, lead.y);
        rectangle.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
        rectangle.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
    }

    /**
     * Moves the Figure to a new location.
     *
     * @param tx the transformation matrix.
     */
    @Override
    public void transform(AffineTransform tx) {
        Point2D.Double anchor = getStartPoint();
        Point2D.Double lead = getEndPoint();
        setBounds(
                (Point2D.Double) tx.transform(anchor, anchor),
                (Point2D.Double) tx.transform(lead, lead)
        );
    }

    @Override
    public void restoreTransformTo(Object geometry) {
        rectangle.setRect((Rectangle2D.Double) geometry);
    }

    @Override
    public Object getTransformRestoreData() {
        return rectangle.clone();
    }

    @Override
    public RectangleFigure clone() {
        RectangleFigure that = (RectangleFigure) super.clone();
        that.rectangle = (Rectangle2D.Double) this.rectangle.clone();
        return that;
    }
    // EVENT HANDLING
}
