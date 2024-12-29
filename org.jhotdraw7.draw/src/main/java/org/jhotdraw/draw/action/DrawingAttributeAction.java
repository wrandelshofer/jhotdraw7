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

/**
 * AttributeAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawingAttributeAction extends AbstractDrawingViewAction {
    private static final long serialVersionUID = 1L;

    protected Map<AttributeKey<?>, Object> attributes;

    /** Creates a new instance. */
    /**
     * Creates a new instance.
     */
    public <T> DrawingAttributeAction(DrawingEditor editor, AttributeKey<T> key, @Nullable T value) {
        this(editor, key, value, null, null);
    }

    /**
     * Creates a new instance.
     */
    public <T> DrawingAttributeAction(DrawingEditor editor, AttributeKey<T> key, @Nullable T value, @Nullable Icon icon) {
        this(editor, key, value, null, icon);
    }

    /**
     * Creates a new instance.
     */
    public <T> DrawingAttributeAction(DrawingEditor editor, AttributeKey<T> key, @Nullable T value, @Nullable String name) {
        this(editor, key, value, name, null);
    }

    public <T> DrawingAttributeAction(DrawingEditor editor, AttributeKey<T> key, @Nullable T value, @Nullable String name, @Nullable Icon icon) {
        this(editor, key, value, name, icon, null);
    }

    public <T> DrawingAttributeAction(DrawingEditor editor, AttributeKey<T> key, @Nullable T value, @Nullable String name, @Nullable Icon icon, @Nullable Action compatibleTextAction) {
        super(editor);
        this.attributes = new HashMap<AttributeKey<?>, Object>();
        attributes.put(key, value);

        putValue(AbstractAction.NAME, name);
        putValue(AbstractAction.SMALL_ICON, icon);
        setEnabled(true);
    }

    public DrawingAttributeAction(DrawingEditor editor, Map<AttributeKey<?>, Object> attributes, String name, Icon icon) {
        super(editor);
        this.attributes = attributes;

        putValue(AbstractAction.NAME, name);
        putValue(AbstractAction.SMALL_ICON, icon);
        updateEnabledState();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {
        final ArrayList<Object> restoreData = new ArrayList<Object>();
        final Figure drawing = getView().getDrawing();
        restoreData.add(drawing.getAttributesRestoreData());
        drawing.willChange();
        for (Map.Entry<AttributeKey<?>, Object> entry : attributes.entrySet()) {
            drawing.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
        }
        drawing.changed();

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

                drawing.willChange();
                drawing.restoreAttributesTo(iRestore.next());
                drawing.changed();
            }

            @Override
            @SuppressWarnings("unchecked")
            public void redo() {
                super.redo();
                //restoreData.add(drawing.getAttributesRestoreData());
                drawing.willChange();
                for (Map.Entry<AttributeKey<?>, Object> entry : attributes.entrySet()) {
                    drawing.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
                }
                drawing.changed();
            }
        };
        fireUndoableEditHappened(edit);
    }
}
