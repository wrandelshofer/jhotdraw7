/* @(#)OrientationHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.handle;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.TriangleFigure;
import org.jhotdraw.draw.event.AttributeChangeEdit;
import org.jhotdraw.geom.Geom;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.jhotdraw.draw.AttributeKeys.ORIENTATION;

/**
 * A {@link Handle} to change the value of the figure attribute
 * {@link org.jhotdraw.draw.AttributeKeys#ORIENTATION}.
 *
 * @author Werner Randelshofer.
 * Original code by Doug Lea  (dl at gee, Sun Mar 2 19:15:28 1997)
 * @version $Id$
 */
public class OrientationHandle extends AbstractHandle {
    private Rectangle centerBox;
    private AttributeKeys.Orientation oldValue;
    private AttributeKeys.Orientation newValue;

    /**
     * Creates a new instance.
     */
    public OrientationHandle(TriangleFigure owner) {
        super(owner);
    }

    @Override
    public boolean isCombinableWith(Handle h) {
        return false;
    }

    private Point2D.Double getLocation() {
        Figure owner = getOwner();
        Rectangle2D.Double r = owner.getBounds();
        Point2D.Double p;
        double offset = getHandlesize();
        switch (owner.get(ORIENTATION)) {
            case NORTH:
            default:
                p = new Point2D.Double(r.x + r.width / 2d, r.y + offset);
                break;
            case NORTH_EAST:
                p = new Point2D.Double(r.x + r.width - offset, r.y + offset);
                break;
            case EAST:
                p = new Point2D.Double(r.x + r.width - offset, r.y + r.height / 2d);
                break;
            case SOUTH_EAST:
                p = new Point2D.Double(r.x + r.width - offset, r.y + r.height - offset);
                break;
            case SOUTH:
                p = new Point2D.Double(r.x + r.width / 2d, r.y + r.height - offset);
                break;
            case SOUTH_WEST:
                p = new Point2D.Double(r.x + offset, r.y + r.height - offset);
                break;
            case WEST:
                p = new Point2D.Double(r.x + offset, r.y + r.height / 2d);
                break;
            case NORTH_WEST:
                p = new Point2D.Double(r.x + offset, r.y + offset);
                break;
        }
        return p;
    }

    @Override
    protected Rectangle basicGetBounds() {
        Point p = view.drawingToView(getLocation());
        Rectangle r = new Rectangle(p);
        int h = getHandlesize();
        r.x -= h / 2;
        r.y -= h / 2;
        r.width = r.height = h;
        return r;
    }

    @Override
    public void trackStart(Point anchor, int modifiersEx) {
        oldValue = getOwner().get(ORIENTATION);

        centerBox = view.drawingToView(getOwner().getBounds());
        centerBox.grow(centerBox.width / -3, centerBox.height / -3);
    }

    @Override
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        Rectangle leadRect = new Rectangle(lead);

        switch (Geom.outcode(centerBox, leadRect)) {
            case Geom.OUT_TOP:
            default:
                newValue = AttributeKeys.Orientation.NORTH;
                break;
            case Geom.OUT_TOP | Geom.OUT_RIGHT:
                newValue = AttributeKeys.Orientation.NORTH_EAST;
                break;
            case Geom.OUT_RIGHT:
                newValue = AttributeKeys.Orientation.EAST;
                break;
            case Geom.OUT_BOTTOM | Geom.OUT_RIGHT:
                newValue = AttributeKeys.Orientation.SOUTH_EAST;
                break;
            case Geom.OUT_BOTTOM:
                newValue = AttributeKeys.Orientation.SOUTH;
                break;
            case Geom.OUT_BOTTOM | Geom.OUT_LEFT:
                newValue = AttributeKeys.Orientation.SOUTH_WEST;
                break;
            case Geom.OUT_LEFT:
                newValue = AttributeKeys.Orientation.WEST;
                break;
            case Geom.OUT_TOP | Geom.OUT_LEFT:
                newValue = AttributeKeys.Orientation.NORTH_WEST;
                break;
        }
        getOwner().willChange();
        getOwner().set(ORIENTATION, newValue);
        getOwner().changed();
        updateBounds();
    }

    @Override
    public void draw(Graphics2D g) {
        drawDiamond(g,
                getEditor().getHandleAttribute(HandleAttributeKeys.ATTRIBUTE_HANDLE_FILL_COLOR),
                getEditor().getHandleAttribute(HandleAttributeKeys.ATTRIBUTE_HANDLE_STROKE_COLOR)
        );
    }

    @Override
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        if (newValue != oldValue) {
            fireUndoableEditHappened(
                    new AttributeChangeEdit<AttributeKeys.Orientation>(getOwner(), ORIENTATION, oldValue, newValue)
            );
        }
    }

}
