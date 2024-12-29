/* @(#)LocatorConnector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.connector;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

/**
 * A LocatorConnector locates connection points with
 * the help of a Locator. It supports the definition
 * of connection points to semantic locations.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @see Locator
 * @see Connector
 */
public class LocatorConnector extends AbstractConnector {
    private static final long serialVersionUID = 1L;
    /**
     * The standard size of the connector. The display box
     * is centered around the located point.
     * <p>
     * FIXME - Why do we need a standard size?
     */
    public static final int SIZE = 2;

    private Locator locator;

    /**
     * Creates a new instance.
     * Only used for DOMStorable.
     */
    public LocatorConnector() {
    }

    public LocatorConnector(Figure owner, Locator l) {
        super(owner);
        locator = l;
    }

    public Locator getLocator() {
        return locator;
    }

    protected Point2D.Double locate(ConnectionFigure connection) {
        return locator.locate(getOwner());
    }

    /**
     * Tests if a point is contained in the connector.
     */
    @Override
    public boolean contains(Point2D.Double p) {
        return getBounds().contains(p);
    }

    /**
     * Gets the display box of the connector.
     */
    @Override
    public Rectangle2D.Double getBounds() {
        Point2D.Double p = locator.locate(getOwner());
        return new Rectangle2D.Double(
                p.x - SIZE / 2,
                p.y - SIZE / 2,
                SIZE,
                SIZE);
    }

    @Override
    public void read(DOMInput in) throws IOException {
        super.read(in);
        in.openElement("locator");
        this.locator = (Locator) in.readObject(0);
        in.closeElement();
    }

    @Override
    public void write(DOMOutput out) throws IOException {
        super.write(out);
        out.openElement("locator");
        out.writeObject(locator);
        out.closeElement();
    }
}
