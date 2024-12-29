/* @(#)ViewPropertyAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.app.action.view;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.app.action.ActionUtil;

import org.jhotdraw.annotation.Nullable;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * ViewPropertyAction.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class ViewPropertyAction extends AbstractViewAction {
    private static final long serialVersionUID = 1L;
    private String propertyName;
    private Class<?>[] parameterClass;
    private Object propertyValue;
    private String setterName;
    private String getterName;

    private PropertyChangeListener viewListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (propertyName.equals(evt.getPropertyName())) { // Strings get interned
                updateSelectedState();
            }
        }
    };

    /**
     * Creates a new instance.
     */
    public ViewPropertyAction(Application app, @Nullable View view, String propertyName, Object propertyValue) {
        this(app, view, propertyName, propertyValue.getClass(), propertyValue);
    }

    public ViewPropertyAction(Application app, @Nullable View view, String propertyName, Class<?> propertyClass, Object propertyValue) {
        super(app, view);
        this.propertyName = propertyName;
        this.parameterClass = new Class<?>[]{propertyClass};
        this.propertyValue = propertyValue;
        setterName = "set" + Character.toUpperCase(propertyName.charAt(0)) +
                propertyName.substring(1);
        getterName = ((propertyClass == Boolean.TYPE || propertyClass == Boolean.class) ? "is" : "get") +
                Character.toUpperCase(propertyName.charAt(0)) +
                propertyName.substring(1);
        updateSelectedState();
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        View p = getActiveView();
        try {
            p.getClass().getMethod(setterName, parameterClass).invoke(p, new Object[]{propertyValue});
        } catch (Throwable e) {
            InternalError error = new InternalError("Method invocation failed. setter:" + setterName + " object:" + p);
            error.initCause(e);
            throw error;
        }
    }

    @Override
    protected void installViewListeners(View p) {
        super.installViewListeners(p);
        p.addPropertyChangeListener(viewListener);
        updateSelectedState();
    }

    /**
     * Installs listeners on the view object.
     */
    @Override
    protected void uninstallViewListeners(View p) {
        super.uninstallViewListeners(p);
        p.removePropertyChangeListener(viewListener);
    }

    private void updateSelectedState() {
        boolean isSelected = false;
        View p = getActiveView();
        if (p != null) {
            try {
                Object value = p.getClass().getMethod(getterName, (Class[]) null).invoke(p);
                isSelected = value == propertyValue ||
                        value != null && propertyValue != null &&
                                value.equals(propertyValue);
            } catch (Throwable e) {
                InternalError error = new InternalError("Method invocation failed. getter:" + getterName + " object:" + p);
                error.initCause(e);
                throw error;
            }
        }
        putValue(ActionUtil.SELECTED_KEY, isSelected);
    }
}
