/* @(#)AttributeChangeEdit.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.event;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.Figure;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * An {@code UndoableEdit} event which can undo a change of a {@link Figure}
 * attribute.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AttributeChangeEdit<T> extends AbstractUndoableEdit {
    private static final long serialVersionUID = 1L;
    private Figure owner;
    private AttributeKey<T> name;
    private T oldValue;
    private T newValue;

    /**
     * Creates a new instance.
     */
    public AttributeChangeEdit(Figure owner, AttributeKey<T> name, T oldValue, T newValue) {
        this.owner = owner;
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @Override
    public String getPresentationName() {
        // FIXME - Localize me
        return "Eigenschaft \u00e4ndern";
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        owner.willChange();
        owner.set(name, newValue);
        owner.changed();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        owner.willChange();
        owner.set(name, oldValue);
        owner.changed();
    }
}
