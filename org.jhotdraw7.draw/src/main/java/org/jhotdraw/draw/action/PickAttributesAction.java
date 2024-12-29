/* @(#)PickAttributesAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import org.jhotdraw.util.ResourceBundleUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.jhotdraw.draw.AttributeKeys.TEXT;
import static org.jhotdraw.draw.AttributeKeys.TRANSFORM;

/**
 * PickAttributesAction.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PickAttributesAction extends AbstractSelectedAction {
    private static final long serialVersionUID = 1L;

    private Set<AttributeKey<?>> excludedAttributes = new HashSet<AttributeKey<?>>(
            Arrays.asList(new AttributeKey<?>[]{TRANSFORM, TEXT}));

    /**
     * Creates a new instance.
     */
    public PickAttributesAction(DrawingEditor editor) {
        super(editor);
        ResourceBundleUtil labels = DrawLabels.getLabels();
        labels.configureAction(this, "edit.pickAttributes");
        updateEnabledState();
    }

    /**
     * Set of attributes that is excluded when applying default attributes.
     * By default, the TRANSFORM attribute is excluded.
     */
    public void setExcludedAttributes(Set<AttributeKey<?>> a) {
        this.excludedAttributes = a;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        pickAttributes();
    }

    @SuppressWarnings("unchecked")
    public void pickAttributes() {
        DrawingEditor editor = getEditor();
        Collection<Figure> selection = getView().getSelectedFigures();
        if (selection.size() > 0) {
            Figure figure = selection.iterator().next();
            for (Map.Entry<AttributeKey<?>, Object> entry : figure.getAttributes().entrySet()) {
                if (!excludedAttributes.contains(entry.getKey())) {
                    editor.setDefaultAttribute((AttributeKey<Object>) entry.getKey(), entry.getValue());
                }
            }
        }
    }

    public void selectionChanged(FigureSelectionEvent evt) {
        setEnabled(getView().getSelectionCount() == 1);
    }
}
