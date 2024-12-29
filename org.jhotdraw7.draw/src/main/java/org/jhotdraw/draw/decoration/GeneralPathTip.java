/* @(#)GeneralPathLineDecoration.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.decoration;

import java.awt.geom.Path2D;

/**
 * A {@link LineDecoration} which draws a general path.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GeneralPathTip extends AbstractLineDecoration {
    private static final long serialVersionUID = 1L;
    private Path2D.Double path;
    double decorationRadius;

    /**
     * Creates a new instance.
     */
    public GeneralPathTip(Path2D.Double path, double decorationRadius) {
        this(path, decorationRadius, false, true, false);
    }

    public GeneralPathTip(Path2D.Double path, double decorationRadius, boolean isFilled, boolean isStroked, boolean isSolid) {
        super(isFilled, isStroked, isSolid);
        this.path = path;
        this.decorationRadius = decorationRadius;
    }

    @Override
    protected Path2D.Double getDecoratorPath(org.jhotdraw.draw.Figure f) {
        return (Path2D.Double) path.clone();
    }

    @Override
    protected double getDecoratorPathRadius(org.jhotdraw.draw.Figure f) {
        return decorationRadius;
    }
}
