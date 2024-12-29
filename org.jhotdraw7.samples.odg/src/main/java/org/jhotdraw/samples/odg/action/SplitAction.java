/* @(#)SplitPathsAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.odg.action;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.UngroupAction;
import org.jhotdraw.samples.odg.ODGLabels;
import org.jhotdraw.samples.odg.figures.ODGPathFigure;
import org.jhotdraw.util.ResourceBundleUtil;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * SplitPathsAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SplitAction extends UngroupAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.splitPath";
    private ResourceBundleUtil labels =
            ODGLabels.getLabels();

    /**
     * Creates a new instance.
     */
    public SplitAction(DrawingEditor editor) {
        super(editor, new ODGPathFigure());

        labels.configureAction(this, ID);
    }

    @Override
    protected boolean canUngroup() {
        if (super.canUngroup()) {
            return ((CompositeFigure) getView().getSelectedFigures().iterator().next()).getChildCount() > 1;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
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

    @SuppressWarnings("unchecked")
    @Override
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
            for (Figure child : path.getChildren()) {
                group.basicAdd(child);
            }
        }
        group.changed();
        view.addToSelection(group);
    }
}
