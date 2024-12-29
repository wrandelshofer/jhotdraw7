/* @(#)CloseFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app.action.file;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Closes the active view after letting the user save unsaved changes.
 * {@code DefaultSDIApplication} automatically exits when the user
 * closes the last view.
 * <p>
 * This action is called when the user selects the Close item in the File
 * menu. The menu item is automatically created by the application.
 * <p>
 * If you want this behavior in your application, you have to create it
 * and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 * <p>
 * You should include this action in applications which use at least
 * one of the following actions, so that the user can close views that he/she
 * created: {@link NewFileAction}, {@link NewWindowAction},
 * {@link OpenFileAction}, {@link OpenDirectoryAction}.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CloseFileAction extends AbstractSaveUnsavedChangesAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "file.close";

    /**
     * Creates a new instance.
     */
    public CloseFileAction(Application app, @Nullable View view) {
        super(app, view);
        ResourceBundleUtil labels = ApplicationLabels.getLabels();
        labels.configureAction(this, ID);
    }

    @Override
    protected void doIt(View view) {
        if (view != null && view.getApplication() != null) {
            Application app = view.getApplication();
            app.dispose(view);
        }
    }
}
