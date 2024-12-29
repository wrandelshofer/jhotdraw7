/* @(#)HorizontalLayouter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.layouter;

import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Insets2D;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.jhotdraw.draw.AttributeKeys.Alignment;
import static org.jhotdraw.draw.AttributeKeys.COMPOSITE_ALIGNMENT;
import static org.jhotdraw.draw.AttributeKeys.LAYOUT_INSETS;

/**
 * A {@link Layouter} which lays out all children of a {@link CompositeFigure}
 * in horizontal direction.
 * <p>
 * The preferred size of the figures is used to determine the layout.
 * This may cause some figures to resize.
 * <p>
 * The HorizontalLayouter honors the LAYOUT_INSETS and the COMPOSITE_ALIGNMENT
 * AttributeKey when laying out a CompositeFigure.
 * <p>
 * If COMPOSITE_ALIGNMENT is not set on the composite figure,
 * the layout assigns the same height to all figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class HorizontalLayouter extends AbstractLayouter {

    @Override
    public Rectangle2D.Double calculateLayout(CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead) {
        Insets2D.Double layoutInsets = compositeFigure.get(LAYOUT_INSETS);

        Rectangle2D.Double layoutBounds = new Rectangle2D.Double(anchor.x, anchor.y, 0, 0);
        for (Figure child : compositeFigure.getChildren()) {
            if (child.isVisible()) {
                Dimension2DDouble preferredSize = child.getPreferredSize();
                Insets2D.Double ins = getInsets(child);
                layoutBounds.height = Math.max(layoutBounds.height, preferredSize.height + ins.top + ins.bottom);
                layoutBounds.width += preferredSize.width + ins.left + ins.right;
            }
        }
        layoutBounds.width += layoutInsets.left + layoutInsets.right;
        layoutBounds.height += layoutInsets.top + layoutInsets.bottom;

        return layoutBounds;
    }

    @Override
    public Rectangle2D.Double layout(CompositeFigure compositeFigure, Point2D.Double anchor, Point2D.Double lead) {
        Insets2D.Double layoutInsets = compositeFigure.get(LAYOUT_INSETS);
        Alignment compositeAlignment = compositeFigure.get(COMPOSITE_ALIGNMENT);

        Rectangle2D.Double layoutBounds = calculateLayout(compositeFigure, anchor, lead);
        double x = layoutBounds.x + layoutInsets.left;
        for (Figure child : compositeFigure.getChildren()) {
            if (child.isVisible()) {
                Insets2D.Double insets = getInsets(child);
                double width = child.getPreferredSize().width;
                double height = child.getPreferredSize().height;
                //--
                switch (compositeAlignment) {
                    case LEADING:
                        child.setBounds(
                                new Point2D.Double(
                                        x + insets.left,
                                        layoutBounds.y + layoutInsets.top + insets.top),
                                new Point2D.Double(
                                        x + insets.left + width,
                                        layoutBounds.y + layoutInsets.top + insets.top + height));
                        break;
                    case TRAILING:
                        child.setBounds(
                                new Point2D.Double(
                                        x + insets.left,
                                        layoutBounds.y + layoutBounds.height - layoutInsets.bottom - insets.bottom - height),
                                new Point2D.Double(
                                        x + insets.left + width,
                                        layoutBounds.y + layoutBounds.height - layoutInsets.bottom - insets.bottom));
                        break;
                    case CENTER:
                        child.setBounds(
                                new Point2D.Double(
                                        x + insets.left,
                                        layoutBounds.y + layoutInsets.top + (layoutBounds.height - height) / 2d),
                                new Point2D.Double(
                                        x + insets.left + width,
                                        layoutBounds.y + layoutInsets.top + (layoutBounds.height + height) / 2d));
                        break;
                    case BLOCK:
                    default:
                        child.setBounds(
                                new Point2D.Double(
                                        x + insets.left,
                                        layoutBounds.y + layoutInsets.top + insets.top),
                                new Point2D.Double(
                                        x + insets.left + width,
                                        layoutBounds.y + layoutBounds.height - layoutInsets.bottom - insets.bottom));
                        break;
                }
                //---
                x += width + insets.left + insets.right;
            }
        }

        return layoutBounds;
    }
}
