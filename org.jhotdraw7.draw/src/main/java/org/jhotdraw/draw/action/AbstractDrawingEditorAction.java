/* @(#)AbstractDrawingEditorAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.annotation.Nullable;
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
 * on behalf of a {@link org.jhotdraw.draw.DrawingEditor}.
 * <p>
 * By default the enabled state of this action reflects the enabled state of the
 * {@code DrawingEditor}. When many actions listen to the enabled state of the
 * drawing editor this can considerably slow down the editor. If updating the
 * enabled state is not necessary, you can prevent the action from doing so using
 * {@link #setUpdateEnabledState}.
 * <p>
 * {@code AbstractDrawingEditorAction} listens using a
 * {@link WeakPropertyChangeListener} on the {@code DrawingEditor} and thus may
 * become garbage collected if it is not referenced by any other object.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractDrawingEditorAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    protected DrawingEditor editor;

    private class EventHandler implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("enabled".equals(evt.getPropertyName())) {
                updateEnabledState();
            }
        }
    }

    ;
    @Nullable
    private EventHandler eventHandler = new EventHandler();

    /**
     * Creates a new instance.
     */
    public AbstractDrawingEditorAction(DrawingEditor editor) {
        setEditor(editor);
    }

    public void setEditor(DrawingEditor newValue) {
        if (eventHandler != null) {
            unregisterEventHandler();
        }
        editor = newValue;
        if (eventHandler != null) {
            registerEventHandler();
            updateEnabledState();
        }
    }

    protected void updateEnabledState() {
        setEnabled(editor != null && editor.isEnabled());
    }

    public DrawingEditor getEditor() {
        return editor;
    }

    protected DrawingView getView() {
        return editor.getActiveView();
    }

    protected Drawing getDrawing() {
        return getView().getDrawing();
    }

    /**
     * Updates the enabled state of this action to reflect the enabled state
     * of the active {@code DrawingView}. If no drawing view is active, this
     * action is disabled.
     */
    protected void fireUndoableEditHappened(UndoableEdit edit) {
        getDrawing().fireUndoableEditHappened(edit);
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
    }

    /**
     * Registers the event handler from the drawing editor and the
     * active drawing view.
     */
    private void registerEventHandler() {
        if (editor != null) {
            editor.addPropertyChangeListener(new WeakPropertyChangeListener(eventHandler));
        }
    }
}
