/* @(#)MoveConstrainedAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.TranslationDirection;
import org.jhotdraw.draw.event.TransformEdit;
import org.jhotdraw.undo.CompositeEdit;
import org.jhotdraw.util.ResourceBundleUtil;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;

/**
 * Moves the selected figures by one constrained unit.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class MoveConstrainedAction extends AbstractSelectedAction {
    private static final long serialVersionUID = 1L;

    private TranslationDirection dir;

    /**
     * Creates a new instance.
     */
    public MoveConstrainedAction(DrawingEditor editor, TranslationDirection dir) {
        super(editor);
        this.dir = dir;
        updateEnabledState();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (getView().getSelectionCount() > 0) {

            Rectangle2D.Double r = null;
            HashSet<Figure> transformedFigures = new HashSet<Figure>();
            for (Figure f : getView().getSelectedFigures()) {
                if (f.isTransformable()) {
                    transformedFigures.add(f);
                    if (r == null) {
                        r = f.getBounds();
                    } else {
                        r.add(f.getBounds());
                    }
                }
            }
            if (transformedFigures.isEmpty()) {
                return;
            }
            Point2D.Double p0 = new Point2D.Double(r.x, r.y);
            if (getView().getConstrainer() != null) {
                getView().getConstrainer().translateRectangle(r, dir);
            } else {
                switch (dir) {
                    case NORTH:
                        r.y -= 1;
                        break;
                    case SOUTH:
                        r.y += 1;
                        break;
                    case WEST:
                        r.x -= 1;
                        break;
                    case EAST:
                        r.x += 1;
                        break;
                }
            }

            AffineTransform tx = new AffineTransform();
            tx.translate(r.x - p0.x, r.y - p0.y);
            for (Figure f : transformedFigures) {
                f.willChange();
                f.transform(tx);
                f.changed();
            }
            CompositeEdit edit;
            fireUndoableEditHappened(new TransformEdit(transformedFigures, tx));
        }
    }

    public static class East extends MoveConstrainedAction {
        private static final long serialVersionUID = 1L;

        public static final String ID = "edit.moveConstrainedEast";

        public East(DrawingEditor editor) {
            super(editor, TranslationDirection.EAST);
            ResourceBundleUtil labels = DrawLabels.getLabels();
            labels.configureAction(this, ID);
        }
    }

    public static class West extends MoveConstrainedAction {
        private static final long serialVersionUID = 1L;

        public static final String ID = "edit.moveConstrainedWest";

        public West(DrawingEditor editor) {
            super(editor, TranslationDirection.WEST);
            ResourceBundleUtil labels = DrawLabels.getLabels();
            labels.configureAction(this, ID);
        }
    }

    public static class North extends MoveConstrainedAction {
        private static final long serialVersionUID = 1L;

        public static final String ID = "edit.moveConstrainedNorth";

        public North(DrawingEditor editor) {
            super(editor, TranslationDirection.NORTH);
            ResourceBundleUtil labels = DrawLabels.getLabels();
            labels.configureAction(this, ID);
        }
    }

    public static class South extends MoveConstrainedAction {
        private static final long serialVersionUID = 1L;

        public static final String ID = "edit.moveConstrainedSouth";

        public South(DrawingEditor editor) {
            super(editor, TranslationDirection.SOUTH);
            ResourceBundleUtil labels = DrawLabels.getLabels();
            labels.configureAction(this, ID);
        }
    }
}
