/* @(#)RotationDirection.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw;

/**
 * Specifies the possible directions for rotations on a two-dimensional plane.
 * <p>
 * This enumeration is used by drawing tools and handles to perform constrained
 * transforms of figures on a drawing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @see Constrainer
 */
public enum RotationDirection {
    CLOCKWISE,
    COUNTER_CLOCKWISE
}
