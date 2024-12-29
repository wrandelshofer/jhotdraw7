/* @(#)SVGAttributeKeys.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.svg;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;

/**
 * SVGAttributeKeys.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SVGAttributeKeys extends AttributeKeys {
    private static final ResourceBundleUtil labels = SVGLabels.getLabels();

    public enum TextAnchor {
        START, MIDDLE, END
    }

    /**
     * Specifies the title of an SVG drawing.
     * This attribute can be null, to indicate that the drawing has no title.
     */
    public static final AttributeKey<String> TITLE = new AttributeKey<String>("title", String.class, null, true, labels);
    /**
     * Specifies the description of an SVG drawing.
     * This attribute can be null, to indicate that the drawing has no description.
     */
    public static final AttributeKey<String> DESCRIPTION = new AttributeKey<String>("description", String.class, null, true, labels);
    /**
     * Specifies the viewport-fill of an SVG viewport.
     * This attribute can be null, to indicate that the viewport has no viewport-fill.
     */
    public static final AttributeKey<Color> VIEWPORT_FILL = CANVAS_FILL_COLOR;
    /**
     * Specifies the viewport-fill-opacity of an SVG viewport.
     */
    public static final AttributeKey<Double> VIEWPORT_FILL_OPACITY = CANVAS_FILL_OPACITY;
    /**
     * Specifies the width of an SVG viewport.
     */
    public static final AttributeKey<Double> VIEWPORT_WIDTH = CANVAS_WIDTH;
    /**
     * Specifies the height of an SVG viewport.
     */
    public static final AttributeKey<Double> VIEWPORT_HEIGHT = CANVAS_HEIGHT;


    /**
     * Specifies the text anchor of a SVGText figure.
     */
    public static final AttributeKey<TextAnchor> TEXT_ANCHOR = new AttributeKey<TextAnchor>("textAnchor", TextAnchor.class, TextAnchor.START, false, labels);

    public enum TextAlign {
        START, CENTER, END
    }

    /**
     * Specifies the text alignment of a SVGText figure.
     */
    public static final AttributeKey<TextAlign> TEXT_ALIGN = new AttributeKey<TextAlign>("textAlign", TextAlign.class, TextAlign.START, false, labels);
    /**
     * Specifies the fill gradient of a SVG figure.
     */
    public static final AttributeKey<Gradient> FILL_GRADIENT = new AttributeKey<Gradient>("fillGradient", Gradient.class, null, true, labels);

    /**
     * Specifies the fill opacity of a SVG figure.
     * This is a value between 0 and 1 whereas 0 is translucent and 1 is fully opaque.
     */
    public static final AttributeKey<Double> FILL_OPACITY = new AttributeKey<Double>("fillOpacity", Double.class, 1d, false, labels);
    /**
     * Specifies the overall opacity of a SVG figure.
     * This is a value between 0 and 1 whereas 0 is translucent and 1 is fully opaque.
     */
    public static final AttributeKey<Double> OPACITY = new AttributeKey<Double>("opacity", Double.class, 1d, false, labels);


    /**
     * Specifies the stroke gradient of a SVG figure.
     */
    public static final AttributeKey<Gradient> STROKE_GRADIENT = new AttributeKey<Gradient>("strokeGradient", Gradient.class, null, true, labels);
    /**
     * Specifies the stroke opacity of a SVG figure.
     * This is a value between 0 and 1 whereas 0 is translucent and 1 is fully opaque.
     */
    public static final AttributeKey<Double> STROKE_OPACITY = new AttributeKey<Double>("strokeOpacity", Double.class, 1d, false, labels);

    /**
     * Specifies a link.
     * In an SVG file, the link is stored in a "a" element which encloses the
     * figure.
     * http://www.w3.org/TR/SVGMobile12/linking.html#AElement
     */
    public static final AttributeKey<String> LINK = new AttributeKey<String>("link", String.class, null, true, labels);
    /**
     * Specifies a link target.
     * In an SVG file, the link is stored in a "a" element which encloses the
     * figure.
     * http://www.w3.org/TR/SVGMobile12/linking.html#AElement
     */
    public static final AttributeKey<String> LINK_TARGET = new AttributeKey<String>("linkTarget", String.class, null, true, labels);


    /**
     * Gets the fill paint for the specified figure based on the attributes
     * FILL_GRADIENT, FILL_OPACITY, FILL_PAINT and the bounds of the figure.
     * Returns null if the figure is not filled.
     */
    @Nullable
    public static Paint getFillPaint(Figure f) {
        double opacity = f.get(FILL_OPACITY);
        if (f.get(FILL_GRADIENT) != null) {
            return f.get(FILL_GRADIENT).getPaint(f, opacity);
        }
        Color color = f.get(FILL_COLOR);
        if (color != null) {
            if (opacity != 1) {
                color = new Color(
                        (color.getRGB() & 0xffffff) | (int) (opacity * 255) << 24,
                        true);
            }
        }
        return color;
    }

    /**
     * Gets the stroke paint for the specified figure based on the attributes
     * STROKE_GRADIENT, STROKE_OPACITY, STROKE_PAINT and the bounds of the figure.
     * Returns null if the figure is not filled.
     */
    @Nullable
    public static Paint getStrokePaint(Figure f) {
        double opacity = f.get(STROKE_OPACITY);
        if (f.get(STROKE_GRADIENT) != null) {
            return f.get(STROKE_GRADIENT).getPaint(f, opacity);
        }
        Color color = f.get(STROKE_COLOR);
        if (color != null) {
            if (opacity != 1) {
                color = new Color(
                        (color.getRGB() & 0xffffff) | (int) (opacity * 255) << 24,
                        true);
            }
        }
        return color;
    }


    /**
     * Sets SVG default values.
     */
    public static void setDefaults(Figure f) {
        // Fill properties
        // http://www.w3.org/TR/SVGMobile12/painting.html#FillProperties
        f.set(FILL_COLOR, Color.black);
        f.set(WINDING_RULE, WindingRule.NON_ZERO);

        // Stroke properties
        // http://www.w3.org/TR/SVGMobile12/painting.html#StrokeProperties
        f.set(STROKE_COLOR, null);
        f.set(STROKE_WIDTH, 1d);
        f.set(STROKE_CAP, BasicStroke.CAP_BUTT);
        f.set(STROKE_JOIN, BasicStroke.JOIN_MITER);
        f.set(STROKE_MITER_LIMIT, 4d);
        f.set(IS_STROKE_MITER_LIMIT_FACTOR, false);
        f.set(STROKE_DASHES, null);
        f.set(STROKE_DASH_PHASE, 0d);
        f.set(IS_STROKE_DASH_FACTOR, false);
    }

    /**
     * Returns the distance, that a Rectangle needs to grow (or shrink) to
     * make hit detections on a shape as specified by the FILL_UNDER_STROKE and STROKE_POSITION
     * attributes of a figure.
     * The value returned is the number of units that need to be grown (or shrunk)
     * perpendicular to a stroke on an outline of the shape.
     */
    public static double getPerpendicularHitGrowth(Figure f) {
        double grow;
        if (f.get(STROKE_COLOR) == null && f.get(STROKE_GRADIENT) == null) {
            grow = getPerpendicularFillGrowth(f);
        } else {
            double strokeWidth = AttributeKeys.getStrokeTotalWidth(f);
            grow = getPerpendicularDrawGrowth(f) + strokeWidth / 2d;
        }
        return grow;
    }
}
