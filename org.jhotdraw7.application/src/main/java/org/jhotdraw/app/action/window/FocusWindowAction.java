/* @(#)FocusWindowAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app.action.window;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.app.View;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.net.URI;

/**
 * Requests focus for a Frame.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FocusWindowAction extends AbstractAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "window.focus";
    @Nullable
    private View view;
    private PropertyChangeListener ppc;

    /**
     * Creates a new instance.
     */
    public FocusWindowAction(@Nullable View view) {
        this.view = view;
        ResourceBundleUtil labels = ApplicationLabels.getLabels();
        labels.configureAction(this, ID);
        //setEnabled(false);
        setEnabled(view != null);

        ppc = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals(View.TITLE_PROPERTY)) {
                    putValue(Action.NAME, evt.getNewValue());
                }
            }
        };
        if (view != null) {
            view.addPropertyChangeListener(ppc);
        }
    }

    public void dispose() {
        setView(null);
    }

    public void setView(@Nullable View newValue) {
        if (view != null) {
            view.removePropertyChangeListener(ppc);
        }
        view = newValue;
        if (view != null) {
            view.addPropertyChangeListener(ppc);
        }
    }

    @Override
    public Object getValue(String key) {
        if (Action.NAME.equals(key) && view != null) {
            return getTitle();
        } else {
            return super.getValue(key);
        }
    }

    private String getTitle() {
        ResourceBundleUtil labels = ApplicationLabels.getLabels();
        String title = labels.getString("unnamedFile");
        if (view != null) {
            URI uri = view.getURI();
            if (uri == null) {
                title = labels.getString("unnamedFile");
            } else {
                title = URIUtil.getName(uri);
            }
            if (view.hasUnsavedChanges()) {
                title += "*";
            }
            title = (labels.getFormatted("internalFrame.title", title,
                    view.getApplication() == null ? "" : view.getApplication().getName(), view.getMultipleOpenId()));
        }
        return title;

    }

    private JFrame getFrame() {
        return (JFrame) SwingUtilities.getWindowAncestor(
                view.getComponent());
    }

    private Component getRootPaneContainer() {
        return SwingUtilities.getRootPane(
                view.getComponent()).getParent();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        /*
        JFrame frame = getFrame();
        if (frame != null) {
        frame.setExtendedState(frame.getExtendedState() & ~Frame.ICONIFIED);
        frame.toFront();
        frame.requestFocus();
        JRootPane rp = SwingUtilities.getRootPane(view.getComponent());
        if (rp != null && (rp.getParent() instanceof JInternalFrame)) {
        ((JInternalFrame) rp.getParent()).toFront();
        }
        view.getComponent().requestFocus();
        } else {
        Toolkit.getDefaultToolkit().beep();
        }*/
        Component rpContainer = getRootPaneContainer();
        if (rpContainer instanceof Frame) {
            Frame frame = (Frame) rpContainer;
            frame.setExtendedState(frame.getExtendedState() & ~Frame.ICONIFIED);
            frame.toFront();
        } else if (rpContainer instanceof JInternalFrame) {
            JInternalFrame frame = (JInternalFrame) rpContainer;
            frame.toFront();
            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e) {
                // Don't care.
            }
        }
        view.getComponent().requestFocusInWindow();
    }
}
