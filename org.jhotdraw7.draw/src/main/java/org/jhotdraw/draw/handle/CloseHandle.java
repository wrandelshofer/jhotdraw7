/* @(#)CloseHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.handle;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.draw.locator.RelativeLocator;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * A {@link Handle} which requests to remove its owning figure when clicked.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CloseHandle extends LocatorHandle {
    private boolean pressed;

    /**
     * Creates a new instance.
     */
    public CloseHandle(Figure owner) {
        this(owner, new RelativeLocator(1.0, 0.0));
    }

    /**
     * Creates a new instance.
     */
    public CloseHandle(Figure owner, Locator locator) {
        super(owner, locator);
    }

    @Override
    protected int getHandlesize() {
        return 9;
    }

    /**
     * Draws this handle.
     */
    @Override
    public void draw(Graphics2D g) {
        drawRectangle(g, (pressed) ? Color.orange : Color.white, Color.black);
        Rectangle r = getBounds();
        g.drawLine(r.x, r.y, r.x + r.width, r.y + r.height);
        g.drawLine(r.x + r.width, r.y, r.x, r.y + r.height);
    }


    /**
     * Returns a cursor for the handle.
     */
    @Override
    public Cursor getCursor() {
        return Cursor.getDefaultCursor();
    }

    @Override
    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        pressed = basicGetBounds().contains(lead);
        if (pressed) getOwner().requestRemove();
        fireAreaInvalidated(getDrawingArea());
    }

    @Override
    public void trackStart(Point anchor, int modifiersEx) {
        pressed = true;
        fireAreaInvalidated(getDrawingArea());
    }

    @Override
    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        boolean oldValue = pressed;
        pressed = basicGetBounds().contains(lead);
        if (oldValue != pressed) fireAreaInvalidated(getDrawingArea());
    }
}
