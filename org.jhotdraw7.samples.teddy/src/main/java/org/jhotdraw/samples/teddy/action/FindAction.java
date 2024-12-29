/* @(#)AbstractFindAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.teddy.action;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.OSXApplication;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.edit.AbstractFindAction;
import org.jhotdraw.samples.teddy.FindDialog;

import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * AbstractFindAction shows the find dialog.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FindAction extends AbstractFindAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = AbstractFindAction.ID;
    private FindDialog findDialog;

    /**
     * Creates a new instance.
     */
    public FindAction(Application app, @Nullable View v) {
        super(app, v);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (findDialog == null) {
            findDialog = new FindDialog(getApplication());
            if (getApplication() instanceof OSXApplication) {
                findDialog.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent evt) {
                        if (findDialog != null) {
                            ((OSXApplication) getApplication()).removePalette(findDialog);
                            findDialog.setVisible(false);
                        }
                    }
                });
            }
        }
        findDialog.setVisible(true);
        if (getApplication() instanceof OSXApplication) {
            ((OSXApplication) getApplication()).addPalette(findDialog);
        }
    }
}
