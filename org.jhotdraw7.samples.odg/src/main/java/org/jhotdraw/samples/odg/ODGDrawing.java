/* @(#)ODGDrawing.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.odg;

import org.jhotdraw.draw.QuadTreeDrawing;

/**
 * ODGDrawing.
 * <p>
 * XXX - This class is going away in future versions: We don't need
 * to subclass QuadTreeDrawing for ODG since we can represent all ODG-specific
 * AttributeKey's instead of using JavaBeans properties.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ODGDrawing extends QuadTreeDrawing {
    private static final long serialVersionUID = 1L;
    private String title;
    private String description;

    /**
     * Creates a new instance.
     */
    public ODGDrawing() {
    }

    public void setTitle(String newValue) {
        String oldValue = title;
        title = newValue;
        firePropertyChange("title", oldValue, newValue);
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String newValue) {
        String oldValue = description;
        description = newValue;
        firePropertyChange("description", oldValue, newValue);
    }

    public String getDescription() {
        return description;
    }
}
