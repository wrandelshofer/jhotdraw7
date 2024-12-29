/* @(#)AbstractDrawingViewAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Disposable;
import org.jhotdraw.beans.WeakPropertyChangeListener;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;

import javax.swing.AbstractAction;
import javax.swing.undo.UndoableEdit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on behalf of a {@link org.jhotdraw.draw.DrawingView}.
 * <p>
 * By default the enabled state of this action reflects the enabled state of the
 * {@code DrawingView}. If no drawing view is active, this action is
 * disabled. When many actions listen to the enabled state this can considerably
 * slow down the editor. If updating the enabled state is not necessary, you can
 * disable it using {@link #setUpdateEnabledState}.
 * <p>
 * If the {@code AbstractDrawingEditorAction} acts on the currently active
 * {@code DrawingView} it listens for property changes in the
 * {@code DrawingEditor}. It listens using a {@link WeakPropertyChangeListener}
 * on the {@code DrawingEditor} and thus may become garbage collected if it is
 * not referenced by any other object.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractDrawingViewAction extends AbstractAction implements Disposable {
    private static final long serialVersionUID = 1L;

    @Nullable
    private DrawingEditor editor;
    @Nullable
    private DrawingView specificView;
    @Nullable
    transient private DrawingView activeView;

    private class EventHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("enabled".equals(evt.getPropertyName())) {
                updateEnabledState();
            } else if (evt.getPropertyName() == DrawingEditor.ACTIVE_VIEW_PROPERTY) {
                if (activeView != null) {
                    activeView.removePropertyChangeListener(eventHandler);
                    activeView = null;
                }
                if (evt.getNewValue() != null) {
                    activeView = ((DrawingView) evt.getNewValue());
                    activeView.addPropertyChangeListener(eventHandler);
                    updateEnabledState();
                }
                updateViewState();
            }
        }

        @Override
        public String toString() {
            return AbstractDrawingViewAction.this + "^$EventHandler";
        }
    }

    ;
    @Nullable
    private EventHandler eventHandler = new EventHandler();

    /**
     * Creates a view action which acts on the current view of the editor.
     */
    public AbstractDrawingViewAction(@Nullable DrawingEditor editor) {
        setEditor(editor);
    }

    /**
     * Creates a view action which acts on the specified view.
     */
    public AbstractDrawingViewAction(@Nullable DrawingView view) {
        this.specificView = view;
        registerEventHandler();
    }

    protected void setEditor(@Nullable DrawingEditor newValue) {
        if (eventHandler != null) {
            unregisterEventHandler();
        }
        editor = newValue;
        if (eventHandler != null) {
            registerEventHandler();
            updateEnabledState();
        }
    }

    @Nullable
    protected DrawingEditor getEditor() {
        return editor;
    }

    @Nullable
    protected DrawingView getView() {
        return (specificView != null || editor == null) ? specificView : editor.getActiveView();
    }

    protected Drawing getDrawing() {
        return getView().getDrawing();
    }

    protected void fireUndoableEditHappened(UndoableEdit edit) {
        getDrawing().fireUndoableEditHappened(edit);
    }

    /**
     * Updates the enabled state of this action to reflect the enabled state
     * of the active {@code DrawingView}. If no drawing view is active, this
     * action is disabled.
     */
    protected void updateEnabledState() {
        if (getView() != null) {
            setEnabled(getView().isEnabled());
        } else {
            setEnabled(false);
        }
    }

    /**
     * This method is called when the active drawing view of the
     * drawing editor changed. The implementation in this class does nothing.
     */
    protected void updateViewState() {
    }

    /**
     * Frees all resources held by this object, so that it can be garbage
     * collected.
     */
    @Override
    public void dispose() {
        setEditor(null);
    }

    /**
     * By default, the enabled state of this action is updated to reflect
     * the enabled state of the active {@code DrawingView}.
     * Since this is not always necessary, and since many listening actions
     * may considerably slow down the drawing editor, you can switch this
     * behavior off here.
     *
     * @param newValue Specify false to prevent automatic updating of the
     *                 enabled state.
     */
    public void setUpdateEnabledState(boolean newValue) {
        // Note: eventHandler != null yields true, if we are currently updating
        // the enabled state.
        if (eventHandler != null != newValue) {
            if (newValue) {
                eventHandler = new EventHandler();
                registerEventHandler();
            } else {
                unregisterEventHandler();
                eventHandler = null;
            }
        }
        if (newValue) {
            updateEnabledState();
        }
    }

    /**
     * Returns true, if this action automatically updates its enabled
     * state to reflect the enabled state of the active {@code DrawingView}.
     */
    public boolean isUpdatEnabledState() {
        return eventHandler != null;
    }

    /**
     * Unregisters the event handler from the drawing editor and the
     * active drawing view.
     */
    private void unregisterEventHandler() {
        if (editor != null) {
            editor.removePropertyChangeListener(eventHandler);
        }
        if (activeView != null) {
            activeView.removePropertyChangeListener(eventHandler);
            activeView = null;
        }
        if (specificView != null) {
            specificView.removePropertyChangeListener(eventHandler);
        }
    }

    /**
     * Registers the event handler from the drawing editor and the
     * active drawing view.
     */
    private void registerEventHandler() {
        if (specificView != null) {
            specificView.addPropertyChangeListener(eventHandler);
        } else {
            if (editor != null) {
                editor.addPropertyChangeListener(new WeakPropertyChangeListener(eventHandler));
                if (activeView != null) {
                    activeView.removePropertyChangeListener(eventHandler);
                }
                activeView = editor.getActiveView();
                if (activeView != null) {
                    activeView.addPropertyChangeListener(eventHandler);
                }
            }
        }
    }
}
