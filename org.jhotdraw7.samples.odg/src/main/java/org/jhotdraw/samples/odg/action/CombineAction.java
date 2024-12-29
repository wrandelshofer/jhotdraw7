/* @(#)CombinePathsAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.odg.action;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.GroupAction;
import org.jhotdraw.samples.odg.ODGLabels;
import org.jhotdraw.samples.odg.figures.ODGPathFigure;
import org.jhotdraw.util.ResourceBundleUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * CombinePathsAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CombineAction extends GroupAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.combinePaths";
    private ResourceBundleUtil labels =
            ODGLabels.getLabels();

    /**
     * Creates a new instance.
     */
    public CombineAction(DrawingEditor editor) {
        super(editor, new ODGPathFigure());
        labels.configureAction(this, ID);
    }

    @Override
    protected boolean canGroup() {
        boolean canCombine = getView().getSelectionCount() > 1;
        if (canCombine) {
            for (Figure f : getView().getSelectedFigures()) {
                if (!(f instanceof ODGPathFigure)) {
                    canCombine = false;
                    break;
                }
            }
        }
        return canCombine;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<Figure> ungroupFigures(DrawingView view, CompositeFigure group) {
        LinkedList<Figure> figures = new LinkedList<Figure>(group.getChildren());
        view.clearSelection();
        group.basicRemoveAllChildren();
        LinkedList<Figure> paths = new LinkedList<Figure>();
        for (Figure f : figures) {
            ODGPathFigure path = new ODGPathFigure();
            path.removeAllChildren();
            for (Map.Entry<AttributeKey<?>, Object> entry : group.getAttributes().entrySet()) {
                path.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
            }
            path.add(f);
            view.getDrawing().basicAdd(path);
            paths.add(path);
        }
        view.getDrawing().remove(group);
        view.addToSelection(paths);
        return figures;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void groupFigures(DrawingView view, CompositeFigure group, Collection<Figure> figures) {
        Collection<Figure> sorted = view.getDrawing().sort(figures);
        view.getDrawing().basicRemoveAll(figures);
        view.clearSelection();
        view.getDrawing().add(group);
        group.willChange();
        ((ODGPathFigure) group).removeAllChildren();
        for (Map.Entry<AttributeKey<?>, Object> entry : figures.iterator().next().getAttributes().entrySet()) {
            group.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
        }
        for (Figure f : sorted) {
            ODGPathFigure path = (ODGPathFigure) f;
            // XXX - We must fire an UndoableEdito for the flattenTransform!
            path.flattenTransform();
            for (Figure child : path.getChildren()) {
                group.basicAdd(child);
            }
        }
        group.changed();
        view.addToSelection(group);
    }
}
