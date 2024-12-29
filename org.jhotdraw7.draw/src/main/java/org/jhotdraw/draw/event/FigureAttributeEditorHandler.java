/* @(#)FigureAttributeEditorHandler.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.event;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.gui.AttributeEditor;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * FigureAttributeEditorHandler mediates between an AttributeEditor and the
 * currently selected Figure's in a DrawingEditor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FigureAttributeEditorHandler<T> extends AbstractAttributeEditorHandler<T> {

    public FigureAttributeEditorHandler(AttributeKey<T> key, AttributeEditor<T> attributeEditor, DrawingEditor drawingEditor) {
        super(key, attributeEditor, drawingEditor);
    }

    public FigureAttributeEditorHandler(AttributeKey<T> key, AttributeEditor<T> attributeEditor, DrawingEditor drawingEditor, boolean updateDrawingEditorDefaults) {
        super(key, attributeEditor, drawingEditor, updateDrawingEditorDefaults);
    }

    public FigureAttributeEditorHandler(AttributeKey<T> key, Map<AttributeKey<?>, Object> defaultAttributes, AttributeEditor<T> attributeEditor, DrawingEditor drawingEditor, boolean updateDrawingEditorDefaults) {
        super(key, defaultAttributes, attributeEditor, drawingEditor, updateDrawingEditorDefaults);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Set<Figure> getEditedFigures() {
        return (Set<Figure>) ((activeView == null) ? Collections.emptySet() : activeView.getSelectedFigures());
    }

}
