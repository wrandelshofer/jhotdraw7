/* @(#)ODGBezierFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.odg.figures;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.BezierFigure;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.handle.BezierNodeHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.geom.BezierPath;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;

import static org.jhotdraw.samples.odg.ODGAttributeKeys.TRANSFORM;
import static org.jhotdraw.samples.odg.ODGAttributeKeys.UNCLOSED_PATH_FILLED;

/**
 * ODGBezierFigure is not an actual ODG element, it is used by ODGPathFigure to
 * represent a single BezierPath segment within an ODG path.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ODGBezierFigure extends BezierFigure {
    private static final long serialVersionUID = 1L;

    @Nullable
    private transient Rectangle2D.Double cachedDrawingArea;

    /**
     * Creates a new instance.
     */
    public ODGBezierFigure() {
        this(false);
    }

    public ODGBezierFigure(boolean isClosed) {
        super(isClosed);
        set(UNCLOSED_PATH_FILLED, true);
    }

    public Collection<Handle> createHandles(ODGPathFigure pathFigure, int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel % 2) {
            case 0:
                for (int i = 0, n = path.size(); i < n; i++) {
                    handles.add(new BezierNodeHandle(this, i, pathFigure));
                }
                break;
            case 1:
                TransformHandleKit.addTransformHandles(this, handles);
                break;
            default:
                break;
        }
        return handles;
    }

    @Override
    public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
        if (evt.getClickCount() == 2/* && view.getHandleDetailLevel() == 0*/) {
            willChange();
            final int index = splitSegment(p, (float) (5f / view.getScaleFactor()));
            if (index != -1) {
                final BezierPath.Node newNode = getNode(index);
                fireUndoableEditHappened(new AbstractUndoableEdit() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void redo() throws CannotRedoException {
                        super.redo();
                        willChange();
                        addNode(index, newNode);
                        changed();
                    }

                    @Override
                    public void undo() throws CannotUndoException {
                        super.undo();
                        willChange();
                        removeNode(index);
                        changed();
                    }
                });
                changed();
                evt.consume();
                return true;
            }
        }
        return false;
    }

    @Override
    public void transform(AffineTransform tx) {
        if (get(TRANSFORM) != null
                || (tx.getType() & (AffineTransform.TYPE_TRANSLATION)) != tx.getType()) {
            if (get(TRANSFORM) == null) {
                TRANSFORM.setClone(this, tx);
            } else {
                AffineTransform t = TRANSFORM.getClone(this);
                t.preConcatenate(tx);
                set(TRANSFORM, t);
            }
        } else {
            super.transform(tx);
        }
    }

    @Override
    public Rectangle2D.Double getDrawingArea() {
        if (cachedDrawingArea == null) {

            if (get(TRANSFORM) == null) {
                cachedDrawingArea = path.getBounds2D();
            } else {
                BezierPath p2 = path.clone();
                p2.transform(get(TRANSFORM));
                cachedDrawingArea = p2.getBounds2D();
            }
        }
        return (Rectangle2D.Double) cachedDrawingArea.clone();
    }

    /**
     * Transforms all coords of the figure by the current TRANSFORM attribute
     * and then sets the TRANSFORM attribute to null.
     */
    public void flattenTransform() {
        if (get(TRANSFORM) != null) {
            path.transform(get(TRANSFORM));
            set(TRANSFORM, null);
        }
        invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        cachedDrawingArea = null;
    }
}
