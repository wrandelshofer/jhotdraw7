/* @(#)EmptyMenuBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.app;

import org.jhotdraw.annotation.Nullable;

import javax.swing.JMenu;
import java.util.List;

/**
 * {@code EmptyMenuBuilder} provides empty implementations of the
 * {@code MenuBuilder} interface.
 *
 * @author Werner Randelshofer
 * @version 1.0 2010-11-14 Created.
 */
public class EmptyMenuBuilder implements MenuBuilder {

    @Override
    public void addPreferencesItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addExitItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addClearFileItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addNewWindowItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addNewFileItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addLoadFileItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addOpenFileItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addCloseFileItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addSaveFileItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addExportFileItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addPrintFileItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addOtherFileItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addUndoItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addClipboardItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addSelectionItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addFindItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addOtherEditItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addOtherViewItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addOtherMenus(List<JMenu> m, Application app, @Nullable View v) {
    }

    @Override
    public void addOtherWindowItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addHelpItems(JMenu m, Application app, @Nullable View v) {
    }

    @Override
    public void addAboutItems(JMenu m, Application app, @Nullable View v) {
    }
}
