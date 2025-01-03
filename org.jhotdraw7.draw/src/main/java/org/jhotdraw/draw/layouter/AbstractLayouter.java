/* @(#)AbstractLayouter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.layouter;

import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.geom.Insets2D;

/**
 * This abstract class can be extended to implement a {@link Layouter}
 * which has its own attribute set.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractLayouter implements Layouter {

    public Insets2D.Double getInsets(Figure child) {
        Insets2D.Double value = child.get(CompositeFigure.LAYOUT_INSETS);
        return (value == null) ? new Insets2D.Double() : (Insets2D.Double) value.clone();
    }
}
