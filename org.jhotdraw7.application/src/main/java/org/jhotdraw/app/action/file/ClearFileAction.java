/* @(#)ClearFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.app.action.file;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractSaveUnsavedChangesAction;
import org.jhotdraw.gui.BackgroundTask;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Clears (empties) the contents of the active view.
 * <p>
 * This action is called when the user selects the Clear item in the File
 * menu. The menu item is automatically created by the application.
 * <p>
 * If you want this behavior in your application, you have to create it
 * and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 * <p>
 * This action is designed for applications which do not automatically
 * create a new view for each opened file. This action goes together with
 * {@link NewWindowAction}, {@link LoadFileAction}, {@link LoadDirectoryAction}
 * and {@link CloseFileAction}.
 * This action should not be used together with {@code NewFileAction}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ClearFileAction extends AbstractSaveUnsavedChangesAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "file.clear";

    /**
     * Creates a new instance.
     */
    public ClearFileAction(Application app, @Nullable View view) {
        super(app, view);
        ResourceBundleUtil labels = ApplicationLabels.getLabels();
        labels.configureAction(this, ID);
    }

    @Override
    public void doIt(final View view) {
        view.setEnabled(false);
        view.execute(new BackgroundTask() {
            @Override
            public void construct() {
                view.clear();
            }

            @Override
            public void finished() {
                view.setEnabled(true);
            }
        });
    }
}
