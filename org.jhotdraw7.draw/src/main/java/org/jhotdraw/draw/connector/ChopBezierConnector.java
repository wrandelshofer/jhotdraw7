/* @(#)ChopBezierConnector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.connector;

import org.jhotdraw.draw.BezierFigure;
import org.jhotdraw.draw.Figure;

import java.awt.geom.Point2D;

/**
 * A {@link Connector} which locates a connection point at the bounds
 * of a {@link BezierFigure}.
 * <p>
 * <p>
 * XXX - This connector does not take the stroke width of the polygon into
 * account.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ChopBezierConnector extends ChopRectangleConnector {
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance.
     */
    public ChopBezierConnector() {
    }

    public ChopBezierConnector(BezierFigure owner) {
        super(owner);
    }

    @Override
    protected Point2D.Double chop(Figure target, Point2D.Double from) {
        BezierFigure bf = (BezierFigure) getConnectorTarget(target);
        return bf.chop(from);
    }
}
