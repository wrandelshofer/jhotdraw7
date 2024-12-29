/* @(#)ToolEvent.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.event;

import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.tool.Tool;

import java.awt.Rectangle;
import java.util.EventObject;

/**
 * An event sent to ToolListener's.
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Observer</em><br>
 * State changes of tools can be observed by other objects. Specifically
 * {@code DrawingEditor} observes area invalidations of tools and repaints
 * its active drawing view accordingly.<br>
 * Subject: {@link Tool}; Observer: {@link ToolListener}; Event:
 * {@link ToolEvent}; Concrete Observer: {@link DrawingEditor}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ToolEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    private Rectangle invalidatedArea;
    private DrawingView view;

    /**
     * Creates a new instance.
     */
    public ToolEvent(Tool src, DrawingView view, Rectangle invalidatedArea) {
        super(src);
        this.view = view;
        this.invalidatedArea = invalidatedArea;
    }

    /**
     * Gets the tool which is the source of the event.
     */
    public Tool getTool() {
        return (Tool) getSource();
    }

    /**
     * Gets the drawing view of the tool.
     */
    public DrawingView getView() {
        return view;
    }

    /**
     * Gets the bounds of the invalidated area on the drawing view.
     */
    public Rectangle getInvalidatedArea() {
        return invalidatedArea;
    }
}
