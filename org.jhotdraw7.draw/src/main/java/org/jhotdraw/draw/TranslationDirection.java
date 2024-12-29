/* @(#)TranslationDirection.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw;

import org.jhotdraw.annotation.Nullable;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Specifies the possible directions for translations on a two-dimensional plane.
 * <p>
 * This enumeration is used by drawing tools and handles to perform constrained
 * transforms of figures on a drawing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @see Constrainer
 */
public enum TranslationDirection {

    NORTH,
    WEST,
    SOUTH,
    EAST,
    NORTH_WEST,
    SOUTH_WEST,
    NORTH_EAST,
    SOUTH_EAST;

    /**
     * Returns the direction from the provided start point to the end point.
     * Returns null, if both points are at the same location.
     */
    @Nullable
    public static TranslationDirection getDirection(Point startPoint, Point endPoint) {
        int dx = endPoint.x - startPoint.x;
        int dy = endPoint.y - startPoint.y;

        if (dx == 0) {
            if (dy == 0) {
                return null;
            } else if (dy > 0) {
                return SOUTH;
            } else {
                return NORTH;
            }
        } else if (dx > 0) {
            if (dy == 0) {
                return EAST;
            } else if (dy > 0) {
                return SOUTH_EAST;
            } else {
                return NORTH_EAST;
            }
        } else {
            if (dy == 0) {
                return WEST;
            } else if (dy > 0) {
                return SOUTH_WEST;
            } else {
                return NORTH_WEST;
            }
        }
    }

    /**
     * Returns the direction from the provided start point to the end point.
     * Returns null, if both points are at the same location.
     */
    @Nullable
    public static TranslationDirection getDirection(Point2D.Double startPoint, Point2D.Double endPoint) {
        double dx = endPoint.x - startPoint.x;
        double dy = endPoint.y - startPoint.y;

        if (dx == 0) {
            if (dy == 0) {
                return null;
            } else if (dy > 0) {
                return SOUTH;
            } else {
                return NORTH;
            }
        } else if (dx > 0) {
            if (dy == 0) {
                return EAST;
            } else if (dy > 0) {
                return SOUTH_EAST;
            } else {
                return NORTH_EAST;
            }
        } else {
            if (dy == 0) {
                return WEST;
            } else if (dy > 0) {
                return SOUTH_WEST;
            } else {
                return NORTH_WEST;
            }
        }
    }
}
