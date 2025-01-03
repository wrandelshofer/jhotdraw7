/* @(#)DecoratedFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw;

import org.jhotdraw.annotation.Nullable;


/**
 * A <em>decorated figure</em> can be decorated with another {@link Figure},
 * for example to draw a border around the decorated figure.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Decorator</em><br>
 * Decorated figures can be adorned with another figure.<br>
 * Component: {@link DecoratedFigure}; Decorator: {@link Figure}.
 *
 * <p><em>Strategy</em><br>
 * {@code RelativeDecoratorLocator} encapsulates a strategy for locating a
 * point on a decorator.<br>
 * Component: {@link DecoratedFigure}; Strategy: {@link org.jhotdraw.draw.locator.RelativeDecoratorLocator}.
 *
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface DecoratedFigure extends Figure {
    /**
     * Sets a decorator Figure, for example a visual adornment to this Figure.
     * Set this to null, if no decorator is desired.
     * The decorator uses the same logical bounds as this Figure plus
     * AttributeKeys.DECORATOR_INSETS. The decorator does not handle events.
     * The decorator is drawn when the figure is drawn.
     */
    public void setDecorator(@Nullable Figure newValue);

    /**
     * Gets the decorator for this figure.
     */
    @Nullable
    public Figure getDecorator();
}
