/* @(#)DeleteAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app.action.edit;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.beans.WeakPropertyChangeListener;
import org.jhotdraw.gui.EditableComponent;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.JComponent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import javax.swing.text.TextAction;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Deletes the region at (or after) the caret position.
 * <p>
 * This action acts on the last {@link org.jhotdraw.gui.EditableComponent} /
 * {@code JTextComponent} which had the focus when the {@code ActionEvent}
 * was generated.
 * <p>
 * This action is called when the user selects the Delete item in the Edit
 * menu. The menu item is automatically created by the application.
 * <p>
 * If you want this behavior in your application, you have to create an action
 * with this ID and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}.
 *
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Framework</em><br>
 * The interfaces and classes listed below work together:
 * <br>
 * Contract: {@link org.jhotdraw.gui.EditableComponent}, {@code JTextComponent}.<br>
 * Client: {@link org.jhotdraw.app.action.edit.AbstractSelectionAction},
 * {@link org.jhotdraw.app.action.edit.DeleteAction},
 * {@link org.jhotdraw.app.action.edit.DuplicateAction},
 * {@link org.jhotdraw.app.action.edit.SelectAllAction},
 * {@link org.jhotdraw.app.action.edit.ClearSelectionAction}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DeleteAction extends TextAction {
    private static final long serialVersionUID = 1L;
    /**
     * The ID for this action.
     */
    public static final String ID = "edit.delete";
    /**
     * The target of the action or null if the action acts on the currently
     * focused component.
     */
    @Nullable
    private JComponent target;
    /**
     * This variable keeps a strong reference on the property change listener.
     */
    private PropertyChangeListener propertyHandler;

    /**
     * Creates a new instance which acts on the currently focused component.
     */
    public DeleteAction() {
        this(null, ID);
    }

    /**
     * Creates a new instance which acts on the specified component.
     *
     * @param target The target of the action. Specify null for the currently
     *               focused component.
     */
    public DeleteAction(@Nullable JComponent target) {
        this(target, ID);
    }

    /**
     * Creates a new instance which acts on the specified component.
     *
     * @param target The target of the action. Specify null for the currently
     *               focused component.
     */
    protected DeleteAction(@Nullable JComponent target, String id) {
        super(id);
        this.target = target;
        if (target != null) {
            // Register with a weak reference on the JComponent.
            propertyHandler = new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if ("enabled".equals(evt.getPropertyName())) {
                        setEnabled((Boolean) evt.getNewValue());
                    }
                }
            };
            target.addPropertyChangeListener(new WeakPropertyChangeListener(propertyHandler));
        }
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
            if (c instanceof EditableComponent) {
                ((EditableComponent) c).delete();
            } else {
                deleteNextChar(evt);
            }
        }
    }

    /**
     * This method was copied from
     * DefaultEditorKit.DeleteNextCharAction.actionPerformed(ActionEvent).
     */
    public void deleteNextChar(ActionEvent e) {
        JTextComponent c = getTextComponent(e);
        boolean beep = true;
        if ((c != null) && (c.isEditable())) {
            try {
                javax.swing.text.Document doc = c.getDocument();
                Caret caret = c.getCaret();
                int dot = caret.getDot();
                int mark = caret.getMark();
                if (dot != mark) {
                    doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
                    beep = false;
                } else if (dot < doc.getLength()) {
                    doc.remove(dot, 1);
                    beep = false;
                }
            } catch (BadLocationException bl) {
            }
        }
        if (beep) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}

