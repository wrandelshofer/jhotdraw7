/* @(#)RedoAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app.action.edit;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Redoes the last user action on the active view.
 * <p>
 * This action requires that the View returns a project
 * specific redo action when invoking getActionMap("redo") on a View.
 * <p>
 * This action is called when the user selects the Redo item in the Edit
 * menu. The menu item is automatically created by the application.
 * <p>
 * If you want this behavior in your application, you have to create an action
 * with this ID and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RedoAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.redo";
    private ResourceBundleUtil labels = ApplicationLabels.getLabels();
    private PropertyChangeListener redoActionPropertyListener = new PropertyChangeListener() {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String name = evt.getPropertyName();
            if (name == AbstractAction.NAME) {
                putValue(AbstractAction.NAME, evt.getNewValue());
            } else if (name == "enabled") {
                updateEnabledState();
            }
        }
    };

    /**
     * Creates a new instance.
     */
    public RedoAction(Application app, @Nullable View view) {
        super(app, view);
        labels.configureAction(this, ID);
    }

    protected void updateEnabledState() {
        boolean isEnabled = false;
        Action realRedoAction = getRealRedoAction();
        if (realRedoAction != null && realRedoAction != this) {
            isEnabled = realRedoAction.isEnabled();
        }
        setEnabled(isEnabled);
    }

    @Override
    protected void updateView(@Nullable View oldValue, @Nullable View newValue) {
        super.updateView(oldValue, newValue);
        if (newValue != null && //
                newValue.getActionMap().get(ID) != null && //
                newValue.getActionMap().get(ID) != this) {
            putValue(AbstractAction.NAME, newValue.getActionMap().get(ID).
                    getValue(AbstractAction.NAME));
            updateEnabledState();
        }
    }

    /**
     * Installs listeners on the view object.
     */
    @Override
    protected void installViewListeners(View p) {
        super.installViewListeners(p);
        Action redoActionInView = p.getActionMap().get(ID);
        if (redoActionInView != null && redoActionInView != this) {
            redoActionInView.addPropertyChangeListener(redoActionPropertyListener);
        }
    }

    /**
     * Installs listeners on the view object.
     */
    @Override
    protected void uninstallViewListeners(View p) {
        super.uninstallViewListeners(p);
        Action redoActionInView = p.getActionMap().get(ID);
        if (redoActionInView != null && redoActionInView != this) {
            redoActionInView.removePropertyChangeListener(redoActionPropertyListener);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Action realAction = getRealRedoAction();
        if (realAction != null && realAction != this) {
            realAction.actionPerformed(e);
        }
    }

    @Nullable
    private Action getRealRedoAction() {
        return (getActiveView() == null) ? null : getActiveView().getActionMap().get(ID);
    }
}
