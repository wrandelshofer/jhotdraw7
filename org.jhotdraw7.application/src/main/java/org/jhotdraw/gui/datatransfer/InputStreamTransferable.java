/* @(#)InputStreamTransferable.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.gui.datatransfer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * A Transferable with an InputStream as its transfer class.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class InputStreamTransferable extends AbstractTransferable {
    private byte[] data;

    /**
     * Creates a new instance.
     */
    public InputStreamTransferable(DataFlavor flavor, byte[] data) {
        this(new DataFlavor[]{flavor}, data);
    }

    /**
     * Note: For performance reasons this method stores a reference to the
     * data array instead of cloning it. Do not modify the data array after
     * invoking this method.
     *
     * @param flavors
     * @param data
     */
    public InputStreamTransferable(DataFlavor[] flavors, byte[] data) {
        super(flavors);
        this.data = data;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }
        return new ByteArrayInputStream(data);
    }
}
