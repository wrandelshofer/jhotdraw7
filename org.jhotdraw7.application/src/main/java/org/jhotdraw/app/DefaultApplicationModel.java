/* @(#)DefaultApplicationModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.action.edit.ClearSelectionAction;
import org.jhotdraw.app.action.edit.CopyAction;
import org.jhotdraw.app.action.edit.CutAction;
import org.jhotdraw.app.action.edit.DeleteAction;
import org.jhotdraw.app.action.edit.DuplicateAction;
import org.jhotdraw.app.action.edit.PasteAction;
import org.jhotdraw.app.action.edit.RedoAction;
import org.jhotdraw.app.action.edit.SelectAllAction;
import org.jhotdraw.app.action.edit.UndoAction;
import org.jhotdraw.app.action.file.CloseFileAction;
import org.jhotdraw.app.action.file.NewFileAction;
import org.jhotdraw.app.action.file.OpenFileAction;
import org.jhotdraw.app.action.file.SaveFileAction;
import org.jhotdraw.app.action.file.SaveFileAsAction;

import javax.swing.ActionMap;
import javax.swing.JToolBar;
import java.util.Collections;
import java.util.List;

/**
 * An {@link ApplicationModel} which creates a default set of {@code Action}s
 * and which does not override any of the default menu bars nor create tool bars.
 * <p>
 * The following actions are created by the {@code createActionMap} method of
 * this model:
 * <ul>
 * <li>{@link NewFileAction}</li>
 * <li>{@link OpenFileAction}</li>
 * <li>{@link SaveFileAction}</li>
 * <li>{@link SaveFileAsAction}</li>
 * <li>{@link CloseFileAction}</li>
 *
 * <li>{@link UndoAction}</li>
 * <li>{@link RedoAction}</li>
 * <li>{@link CutAction}</li>
 * <li>{@link CopyAction}</li>
 * <li>{@link PasteAction}</li>
 * <li>{@link DeleteAction}</li>
 * <li>{@link DuplicateAction}</li>
 * <li>{@link SelectAllAction}</li>
 * <li>{@link ClearSelectionAction}</li>
 * </ul>
 *
 * <p>The {@code createMenu...} methods of this model return null, resulting in
 * a set of default menu bars created by the {@link Application} which holds
 * this model.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class DefaultApplicationModel
        extends AbstractApplicationModel {
    private static final long serialVersionUID = 1L;
    @Nullable
    private MenuBuilder menuBuilder;

    /**
     * Does nothing.
     */
    @Override
    public void initView(Application a, View v) {
    }

    /**
     * Returns an {@code ActionMap} with a default set of actions (See
     * class comments).
     */
    @Override
    public ActionMap createActionMap(Application a, @Nullable View v) {
        ActionMap m = new ActionMap();
        m.put(NewFileAction.ID, new NewFileAction(a));
        m.put(OpenFileAction.ID, new OpenFileAction(a));
        m.put(SaveFileAction.ID, new SaveFileAction(a, v));
        m.put(SaveFileAsAction.ID, new SaveFileAsAction(a, v));
        m.put(CloseFileAction.ID, new CloseFileAction(a, v));

        m.put(UndoAction.ID, new UndoAction(a, v));
        m.put(RedoAction.ID, new RedoAction(a, v));
        m.put(CutAction.ID, new CutAction());
        m.put(CopyAction.ID, new CopyAction());
        m.put(PasteAction.ID, new PasteAction());
        m.put(DeleteAction.ID, new DeleteAction());
        m.put(DuplicateAction.ID, new DuplicateAction());
        m.put(SelectAllAction.ID, new SelectAllAction());
        m.put(ClearSelectionAction.ID, new ClearSelectionAction());
        return m;
    }

    /**
     * Returns an empty unmodifiable list.
     */
    @Override
    public List<JToolBar> createToolBars(Application app, @Nullable View p) {
        return Collections.emptyList();
    }

    /**
     * Creates the DefaultMenuBuilder.
     */
    protected MenuBuilder createMenuBuilder() {
        return new DefaultMenuBuilder();
    }

    @Override
    public MenuBuilder getMenuBuilder() {
        if (menuBuilder == null) {
            menuBuilder = createMenuBuilder();
        }
        return menuBuilder;
    }

    public void setMenuBuilder(@Nullable MenuBuilder newValue) {
        menuBuilder = newValue;
    }

}
