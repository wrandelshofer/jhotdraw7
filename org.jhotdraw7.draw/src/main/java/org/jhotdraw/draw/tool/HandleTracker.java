/* @(#)HandleTracker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.tool;

import org.jhotdraw.draw.handle.Handle;

import java.util.Collection;

/**
 * A <em>handle tracker</em> provides the behavior for manipulating a
 * {@link Handle} of a figure to the {@link SelectionTool}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface HandleTracker extends Tool {

    public void setHandles(Handle handle, Collection<Handle> compatibleHandles);

}
