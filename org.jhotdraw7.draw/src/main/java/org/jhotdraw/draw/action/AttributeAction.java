/* @(#)AttributeAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * {@code AttributeAction} applies attribute values on the selected figures of
 * the current {@code DrawingView} of a {@code DrawingEditor}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AttributeAction extends AbstractSelectedAction {
    private static final long serialVersionUID = 1L;

    protected Map<AttributeKey<?>, Object> attributes;

    /** Creates a new instance. */
    /**
     * Creates a new instance.
     */
    public <T> AttributeAction(DrawingEditor editor, AttributeKey<T> key, @Nullable T value) {
        this(editor, key, value, null, null);
    }

    /**
     * Creates a new instance.
     */
    public <T> AttributeAction(DrawingEditor editor, AttributeKey<T> key, @Nullable T value, @Nullable Icon icon) {
        this(editor, key, value, null, icon);
    }

    /**
     * Creates a new instance.
     */
    public <T> AttributeAction(DrawingEditor editor, AttributeKey<T> key, @Nullable T value, @Nullable String name) {
        this(editor, key, value, name, null);
    }

    public <T> AttributeAction(DrawingEditor editor, AttributeKey<T> key, @Nullable T value, @Nullable String name, @Nullable Icon icon) {
        this(editor, key, value, name, icon, null);
    }

    public <T> AttributeAction(DrawingEditor editor, AttributeKey<T> key, @Nullable T value, @Nullable String name, @Nullable Icon icon, @Nullable Action compatibleTextAction) {
        super(editor);
        this.attributes = new HashMap<AttributeKey<?>, Object>();
        attributes.put(key, value);

        putValue(AbstractAction.NAME, name);
        putValue(AbstractAction.SMALL_ICON, icon);
        putValue(ActionUtil.UNDO_PRESENTATION_NAME_KEY, key.getPresentationName());
        updateEnabledState();
    }

    public AttributeAction(DrawingEditor editor, @Nullable Map<AttributeKey<?>, Object> attributes, @Nullable String name, @Nullable Icon icon) {
        super(editor);
        this.attributes = (attributes == null) ? new HashMap<AttributeKey<?>, Object>() : attributes;

        putValue(AbstractAction.NAME, name);
        putValue(AbstractAction.SMALL_ICON, icon);
        updateEnabledState();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        applyAttributesTo(attributes, getView().getSelectedFigures());
    }

    /**
     * Applies the specified attributes to the currently selected figures
     * of the drawing.
     *
     * @param a       The attributes.
     * @param figures The figures to which the attributes are applied.
     */
    @SuppressWarnings("unchecked")
    public void applyAttributesTo(final Map<AttributeKey<?>, Object> a, Set<Figure> figures) {
        for (Map.Entry<AttributeKey<?>, Object> entry : a.entrySet()) {
            getEditor().setDefaultAttribute((AttributeKey<Object>) entry.getKey(), entry.getValue());
        }

        final ArrayList<Figure> selectedFigures = new ArrayList<Figure>(figures);
        final ArrayList<Object> restoreData = new ArrayList<Object>(selectedFigures.size());
        for (Figure figure : selectedFigures) {
            restoreData.add(figure.getAttributesRestoreData());
            figure.willChange();
            for (Map.Entry<AttributeKey<?>, Object> entry : a.entrySet()) {
                figure.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
            }
            figure.changed();
        }
        UndoableEdit edit = new AbstractUndoableEdit() {
            private static final long serialVersionUID = 1L;

            @Override
            public String getPresentationName() {
                String name = (String) getValue(ActionUtil.UNDO_PRESENTATION_NAME_KEY);
                if (name == null) {
                    name = (String) getValue(AbstractAction.NAME);
                }
                if (name == null) {
                    ResourceBundleUtil labels = DrawLabels.getLabels();
                    name = labels.getString("attribute.text");
                }
                return name;
            }

            @Override
            public void undo() {
                super.undo();
                Iterator<Object> iRestore = restoreData.iterator();
                for (Figure figure : selectedFigures) {
                    figure.willChange();
                    figure.restoreAttributesTo(iRestore.next());
                    figure.changed();
                }
            }

            @Override
            public void redo() {
                super.redo();
                for (Figure figure : selectedFigures) {
                    //restoreData.add(figure.getAttributesRestoreData());
                    figure.willChange();
                    for (Map.Entry<AttributeKey<?>, Object> entry : a.entrySet()) {
                        figure.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
                    }
                    figure.changed();
                }
            }
        };
        getDrawing().fireUndoableEditHappened(edit);
    }

    @Override
    protected void updateEnabledState() {
        if (getEditor() != null) {
            setEnabled(getEditor().isEnabled());
        }
    }
}
