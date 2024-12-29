/* @(#)AbstractApplicationAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app.action;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.Disposable;
import org.jhotdraw.beans.WeakPropertyChangeListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on an {@link Application}.
 * <p>
 * If the {@code Application} object is disabled, the
 * {@code AbstractApplicationAction} is disabled as well.
 * <p>
 * {@code AbstractApplicationAction} listens using a
 * {@link WeakPropertyChangeListener} on the {@code Application} and thus may
 * become garbage collected if it is not referenced by any other object.
 * <p>
 * Application actions are typically created by an {@link org.jhotdraw.app.ApplicationModel},
 * and can be retrieved using getAction(String) from the application model.
 * Application model typically links the actions to menu items and toolbars that
 * it creates. Applicaton model may also put actions into its {@link org.jhotdraw.app.View}s,
 * so that they can be linked to components of a view.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public abstract class AbstractApplicationAction extends AbstractAction implements Disposable {
    private static final long serialVersionUID = 1L;

    @Nullable
    private Application app;
    @Nullable
    private PropertyChangeListener applicationListener;

    /**
     * Creates a new instance.
     */
    public AbstractApplicationAction(Application app) {
        this.app = app;
        installApplicationListeners(app);
        updateApplicationEnabled();
    }

    /*
     * Installs listeners on the application object.
     */
    protected void installApplicationListeners(Application app) {
        if (applicationListener == null) {
            applicationListener = createApplicationListener();
        }
        app.addPropertyChangeListener(new WeakPropertyChangeListener(applicationListener));
    }

    /**
     * Installs listeners on the application object.
     */
    protected void uninstallApplicationListeners(Application app) {
        app.removePropertyChangeListener(applicationListener);
    }

    private PropertyChangeListener createApplicationListener() {
        return new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == "enabled") { // Strings get interned
                    updateApplicationEnabled();
                }
            }
        };
    }

    public Application getApplication() {
        return app;
    }

    /**
     * Updates the enabled state of this action depending on the new enabled
     * state of the application.
     */
    protected void updateApplicationEnabled() {
        firePropertyChange("enabled",
                Boolean.valueOf(!isEnabled()),
                Boolean.valueOf(isEnabled()));
    }

    /**
     * Returns true if the action is enabled.
     * The enabled state of the action depends on the state that has been set
     * using setEnabled() and on the enabled state of the application.
     *
     * @return true if the action is enabled, false otherwise
     * @see Action#isEnabled
     */
    @Override
    public boolean isEnabled() {
        return app != null && app.isEnabled() && enabled;
    }

    /**
     * Enables or disables the action. The enabled state of the action
     * depends on the value that is set here and on the enabled state of
     * the application.
     *
     * @param newValue true to enable the action, false to
     *                 disable it
     * @see Action#setEnabled
     */
    @Override
    public void setEnabled(boolean newValue) {
        boolean oldValue = this.enabled;
        this.enabled = newValue;

        firePropertyChange("enabled",
                Boolean.valueOf(oldValue && app.isEnabled()),
                Boolean.valueOf(newValue && app.isEnabled()));
    }

    @Override
    public final void dispose() {
        if (app != null) {
            uninstallApplicationListeners(app);
            app = null;
        }
    }
}
