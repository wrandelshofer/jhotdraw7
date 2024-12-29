/* @(#)CutAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app.action.edit;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.gui.datatransfer.ClipboardUtil;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;

/**
 * Cuts the selected region and places its contents into the system clipboard.
 * <p>
 * This action acts on the last {@link org.jhotdraw.gui.EditableComponent} /
 * {@code JTextComponent} which had the focus when the {@code ActionEvent}
 * was generated.
 * <p>
 * This action is called when the user selects the Cut item in the Edit
 * menu. The menu item is automatically created by the application.
 * <p>
 * If you want this behavior in your application, you have to create an action
 * with this ID and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CutAction extends AbstractSelectionAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.cut";

    /**
     * Creates a new instance which acts on the currently focused component.
     */
    public CutAction() {
        this(null);
    }

    /**
     * Creates a new instance which acts on the specified component.
     *
     * @param target The target of the action. Specify null for the currently
     *               focused component.
     */
    public CutAction(@Nullable JComponent target) {
        super(target);
        ResourceBundleUtil labels = ApplicationLabels.getLabels();
        labels.configureAction(this, ID);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        JComponent c = target;
        if (c == null && (KeyboardFocusManager.getCurrentKeyboardFocusManager().
                getPermanentFocusOwner() instanceof JComponent)) {
            c = (JComponent) KeyboardFocusManager.getCurrentKeyboardFocusManager().
                    getPermanentFocusOwner();
        }
        if (c != null && c.isEnabled()) {
            c.getTransferHandler().exportToClipboard(
                    c,
                    ClipboardUtil.getClipboard(),
                    TransferHandler.MOVE);
        }
    }
}
