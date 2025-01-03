/* @(#)NetApplicationModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.net;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationModel;
import org.jhotdraw.app.DefaultApplicationModel;
import org.jhotdraw.app.DefaultMenuBuilder;
import org.jhotdraw.app.MenuBuilder;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.ActionUtil;
import org.jhotdraw.app.action.file.ExportFileAction;
import org.jhotdraw.app.action.view.ToggleViewPropertyAction;
import org.jhotdraw.app.action.view.ViewPropertyAction;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.tool.ConnectionTool;
import org.jhotdraw.draw.tool.TextCreationTool;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.gui.JFileURIChooser;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.filechooser.ExtensionFileFilter;
import org.jhotdraw.samples.net.figures.NodeFigure;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JToolBar;
import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Provides factory methods for creating views, menu bars and toolbars.
 * <p>
 * See {@link ApplicationModel} on how this class interacts with an application.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class NetApplicationModel extends DefaultApplicationModel {
    private static final long serialVersionUID = 1L;

    private static final double[] scaleFactors = {5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25, 0.10};

    private static class ToolButtonListener implements ItemListener {

        private Tool tool;
        private DrawingEditor editor;

        public ToolButtonListener(Tool t, DrawingEditor editor) {
            this.tool = t;
            this.editor = editor;
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                editor.setTool(tool);
            }
        }
    }

    /**
     * This editor is shared by all views.
     */
    private DefaultDrawingEditor sharedEditor;
    private HashMap<String, Action> actions;

    /**
     * Creates a new instance.
     */
    public NetApplicationModel() {
    }

    @Override
    public ActionMap createActionMap(Application a, @Nullable View v) {
        ActionMap m = super.createActionMap(a, v);
        ResourceBundleUtil drawLabels = NetLabels.getLabels();
        AbstractAction aa;

        m.put(ExportFileAction.ID, new ExportFileAction(a, v));
        m.put("view.toggleGrid", aa = new ToggleViewPropertyAction(a, v, NetView.GRID_VISIBLE_PROPERTY));
        drawLabels.configureAction(aa, "view.toggleGrid");
        for (double sf : scaleFactors) {
            m.put((int) (sf * 100) + "%",
                    aa = new ViewPropertyAction(a, v, DrawingView.SCALE_FACTOR_PROPERTY, Double.TYPE, sf));
            aa.putValue(Action.NAME, (int) (sf * 100) + " %");

        }
        return m;
    }

    public DefaultDrawingEditor getSharedEditor() {
        if (sharedEditor == null) {
            sharedEditor = new DefaultDrawingEditor();
        }
        return sharedEditor;
    }

    @Override
    public void initView(Application a, View p) {
        if (a.isSharingToolsAmongViews()) {
            ((NetView) p).setEditor(getSharedEditor());
        }
    }

    private void addCreationButtonsTo(JToolBar tb, final DrawingEditor editor) {
        // AttributeKeys for the entitie sets
        HashMap<AttributeKey<?>, Object> attributes;

        ResourceBundleUtil labels = NetLabels.getLabels();

        ButtonFactory.addSelectionToolTo(tb, editor);
        tb.addSeparator();

        attributes = new HashMap<AttributeKey<?>, Object>();
        attributes.put(AttributeKeys.FILL_COLOR, Color.white);
        attributes.put(AttributeKeys.STROKE_COLOR, Color.black);
        attributes.put(AttributeKeys.TEXT_COLOR, Color.black);
        ButtonFactory.addToolTo(tb, editor, new TextCreationTool(new NodeFigure(), attributes), "edit.createNode", labels);

        attributes = new HashMap<AttributeKey<?>, Object>();
        attributes.put(AttributeKeys.STROKE_COLOR, new Color(0x000099));
        ButtonFactory.addToolTo(tb, editor, new ConnectionTool(new LineConnectionFigure(), attributes), "edit.createLink", labels);
    }

    /**
     * Creates toolbars for the application.
     */
    @Override
    public java.util.List<JToolBar> createToolBars(Application a, @Nullable View pr) {
        ResourceBundleUtil drawLabels = NetLabels.getLabels();
        NetView p = (NetView) pr;

        DrawingEditor editor;
        if (p == null) {
            editor = getSharedEditor();
        } else {
            editor = p.getEditor();
        }

        LinkedList<JToolBar> list = new LinkedList<JToolBar>();
        JToolBar tb;
        tb = new JToolBar();
        addCreationButtonsTo(tb, editor);
        tb.setName(drawLabels.getString("window.drawToolBar.title"));
        list.add(tb);
        tb = new JToolBar();
        ButtonFactory.addAttributesButtonsTo(tb, editor);
        tb.setName(drawLabels.getString("window.attributesToolBar.title"));
        list.add(tb);
        tb = new JToolBar();
        ButtonFactory.addAlignmentButtonsTo(tb, editor);
        tb.setName(drawLabels.getString("window.alignmentToolBar.title"));
        list.add(tb);
        return list;
    }

    /**
     * Creates the MenuBuilder.
     */
    @Override
    protected MenuBuilder createMenuBuilder() {
        return new DefaultMenuBuilder() {

            @Override
            public void addOtherViewItems(JMenu m, Application app, @Nullable View v) {
                ActionMap am = app.getActionMap(v);
                JCheckBoxMenuItem cbmi;
                cbmi = new JCheckBoxMenuItem(am.get("view.toggleGrid"));
                ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get("view.toggleGrid"));
                m.add(cbmi);
                JMenu m2 = new JMenu("Zoom");
                for (double sf : scaleFactors) {
                    String id = (int) (sf * 100) + "%";
                    cbmi = new JCheckBoxMenuItem(am.get(id));
                    ActionUtil.configureJCheckBoxMenuItem(cbmi, am.get(id));
                    m2.add(cbmi);
                }
                m.add(m2);
            }
        };
    }

    @Override
    public URIChooser createOpenChooser(Application a, @Nullable View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("Net Diagram .xml", "xml"));
        return c;
    }

    @Override
    public URIChooser createSaveChooser(Application a, @Nullable View v) {
        JFileURIChooser c = new JFileURIChooser();
        c.addChoosableFileFilter(new ExtensionFileFilter("Net Diagram .xml", "xml"));
        return c;
    }
}
