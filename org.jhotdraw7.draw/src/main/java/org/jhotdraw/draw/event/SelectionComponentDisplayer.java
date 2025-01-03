/* @(#)SelectionComponentDisplayer.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.event;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.tool.SelectionTool;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

/**
 * Calls setVisible(true/false) on components, which show attributes of the
 * drawing editor and of its views based on the current selection.
 * <p>
 * Holds a {@code WeakReference} on the component. Automatically disposes
 * itself if the component no longer exists.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SelectionComponentDisplayer
        implements PropertyChangeListener, FigureSelectionListener {

    @Nullable
    protected DrawingView view;
    @Nullable
    protected DrawingEditor editor;
    protected WeakReference<JComponent> weakRef;
    protected int minSelectionCount = 1;
    protected boolean isVisibleIfCreationTool = true;

    public SelectionComponentDisplayer(DrawingEditor editor, JComponent component) {
        this.editor = editor;
        this.weakRef = new WeakReference<JComponent>(component);
        if (editor.getActiveView() != null) {
            view = editor.getActiveView();
            view.addPropertyChangeListener(this);
            view.addFigureSelectionListener(this);
        }
        editor.addPropertyChangeListener(this);
        updateVisibility();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        if (name == DrawingEditor.ACTIVE_VIEW_PROPERTY) {
            if (view != null) {
                view.removePropertyChangeListener(this);
                view.removeFigureSelectionListener(this);
            }
            view = (DrawingView) evt.getNewValue();
            if (view != null) {
                view.addPropertyChangeListener(this);
                view.addFigureSelectionListener(this);
            }
            updateVisibility();
        } else if (name == DrawingEditor.TOOL_PROPERTY) {
            updateVisibility();
        }
    }

    @Override
    public void selectionChanged(FigureSelectionEvent evt) {
        updateVisibility();
    }

    public void updateVisibility() {
        boolean newValue = editor != null &&
                editor.getActiveView() != null &&
                (isVisibleIfCreationTool && editor.getTool() != null && !(editor.getTool() instanceof SelectionTool) ||
                        editor.getActiveView().getSelectionCount() >= minSelectionCount);
        JComponent component = weakRef.get();
        if (component == null) {
            dispose();
            return;
        }
        if (newValue != component.isVisible()) {
            component.setVisible(newValue);

            // The following is needed to trick BoxLayout
            if (newValue) {
                component.setPreferredSize(null);
            } else {
                component.setPreferredSize(new Dimension(0, 0));
            }

            component.revalidate();
        }
    }

    @Nullable
    protected JComponent getComponent() {
        return weakRef.get();
    }

    public void dispose() {
        if (editor != null) {
            editor.removePropertyChangeListener(this);
            editor = null;
        }
        if (view != null) {
            view.removePropertyChangeListener(this);
            view.removeFigureSelectionListener(this);
            view = null;
        }
    }

    public void setMinSelectionCount(int newValue) {
        minSelectionCount = newValue;
        updateVisibility();
    }

    public void setVisibleIfCreationTool(boolean newValue) {
        isVisibleIfCreationTool = newValue;
    }
}

