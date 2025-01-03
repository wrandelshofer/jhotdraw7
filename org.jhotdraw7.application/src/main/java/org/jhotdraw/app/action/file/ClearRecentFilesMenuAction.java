/* @(#)ClearRecentFilesMenuAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.app.action.file;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.app.action.AbstractApplicationAction;
import org.jhotdraw.util.ResourceBundleUtil;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Clears (empties) the Recent Files sub-menu in the File menu.
 * <p>
 * This action is called when the user selects the Clear Recent Files item in
 * the Recent Files sub-menu of the File menu. The action and the menu item
 * is automatically created by the application, when the
 * {@code ApplicationModel} provides a {@code LoadFileAction} or a
 * {@code OpenFileAction}.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class ClearRecentFilesMenuAction extends AbstractApplicationAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "file.clearRecentFiles";

    private PropertyChangeListener applicationListener;

    /**
     * Creates a new instance.
     */
    public ClearRecentFilesMenuAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ApplicationLabels.getLabels();
        labels.configureAction(this, ID);
        updateEnabled();
    }

    /**
     * Installs listeners on the application object.
     */
    @Override
    protected void installApplicationListeners(Application app) {
        super.installApplicationListeners(app);
        if (applicationListener == null) {
            applicationListener = createApplicationListener();
        }
        app.addPropertyChangeListener(applicationListener);
    }

    private PropertyChangeListener createApplicationListener() {
        return new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName() == Application.RECENT_URIS_PROPERTY) { // Strings get interned
                    updateEnabled();
                }
            }
        };
    }

    /**
     * Installs listeners on the application object.
     */
    @Override
    protected void uninstallApplicationListeners(Application app) {
        super.uninstallApplicationListeners(app);
        app.removePropertyChangeListener(applicationListener);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getApplication().clearRecentURIs();
    }

    private void updateEnabled() {
        setEnabled(getApplication().getRecentURIs().size() > 0);

    }

}
