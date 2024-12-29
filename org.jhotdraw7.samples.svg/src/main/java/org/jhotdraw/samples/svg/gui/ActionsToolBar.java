/* @(#)ActionsToolBar.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.svg.gui;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.action.edit.ClearSelectionAction;
import org.jhotdraw.app.action.edit.CopyAction;
import org.jhotdraw.app.action.edit.CutAction;
import org.jhotdraw.app.action.edit.DeleteAction;
import org.jhotdraw.app.action.edit.DuplicateAction;
import org.jhotdraw.app.action.edit.PasteAction;
import org.jhotdraw.app.action.edit.SelectAllAction;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.action.GroupAction;
import org.jhotdraw.draw.action.SelectSameAction;
import org.jhotdraw.draw.action.UngroupAction;
import org.jhotdraw.gui.JPopupButton;
import org.jhotdraw.gui.plaf.palette.PaletteButtonUI;
import org.jhotdraw.samples.svg.SVGLabels;
import org.jhotdraw.samples.svg.figures.SVGGroupFigure;
import org.jhotdraw.undo.UndoRedoManager;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ActionsToolBar.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ActionsToolBar extends AbstractToolBar {
    private static final long serialVersionUID = 1L;
    @Nullable
    private UndoRedoManager undoManager;
    private ArrayList<Action> actions;
    private JPopupButton popupButton;

    /**
     * Creates new instance.
     */
    public ActionsToolBar() {
        ResourceBundleUtil labels = SVGLabels.getLabels();
        setName(labels.getString(getID() + ".toolbar"));
    }

    @Override
    public void setEditor(@Nullable DrawingEditor newValue) {
        if (this.editor != null && undoManager != null) {
            this.removePropertyChangeListener(getEventHandler());
        }
        this.editor = newValue;
        if (editor != null && undoManager != null) {
            init();
            updatePopupMenu();
            setDisclosureState(prefs.getInt(getID() + ".disclosureState", 1));
            this.addPropertyChangeListener(getEventHandler());
        }
    }

    public void setUndoManager(UndoRedoManager newValue) {
        if (this.editor != null && newValue != null) {
            this.removePropertyChangeListener(getEventHandler());
        }
        this.undoManager = newValue;
        if (editor != null && newValue != null) {
            init();
            setDisclosureState(prefs.getInt(getID() + ".disclosureState", 1));
            this.addPropertyChangeListener(getEventHandler());
        }
    }

    /**
     * Sets the actions for the "Action" popup menu in the toolbar.
     * <p>
     * This list may contain null items which are used to denote a
     * separator in the popup menu.
     * <p>
     * Set this to null to set the drop down menus to the default actions.
     */
    public void setPopupActions(List<Action> actions) {
        if (actions == null) {
            this.actions = null;
        } else {
            this.actions = new ArrayList<Action>();
            this.actions.addAll(actions);
        }
    }

    /**
     * Gets the actions of the "Action" popup menu in the toolbar.
     * This list may contain null items which are used to denote a
     * separator in the popup menu.
     *
     * @return An unmodifiable list with actions.
     */
    public List<Action> getPopupActions() {
        if (actions == null) {
            actions = new ArrayList<Action>();
        }
        return Collections.unmodifiableList(actions);
    }

    @Override
    protected JComponent createDisclosedComponent(int state) {
        JPanel p = null;

        switch (state) {
            case 1: {
                p = new JPanel();
                p.setOpaque(false);
                p.setBorder(new EmptyBorder(5, 5, 5, 8));

                // Abort if no editor is set
                if (editor == null) {
                    break;
                }

                // Preferences prefs = PreferencesUtil.userNodeForPackage(getClass());

                ResourceBundleUtil labels = SVGLabels.getLabels();

                GridBagLayout layout = new GridBagLayout();
                p.setLayout(layout);

                GridBagConstraints gbc;
                AbstractButton btn;

                btn = new JButton(undoManager.getUndoAction());
                btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                btn.setText(null);
                labels.configureToolBarButton(btn, "edit.undo");
                btn.putClientProperty("hideActionText", Boolean.TRUE);
                gbc = new GridBagConstraints();
                gbc.gridy = 0;
                gbc.gridx = 0;
                p.add(btn, gbc);

                btn = new JButton(undoManager.getRedoAction());
                btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                btn.setText(null);
                labels.configureToolBarButton(btn, "edit.redo");
                btn.putClientProperty("hideActionText", Boolean.TRUE);
                gbc = new GridBagConstraints();
                gbc.gridy = 0;
                gbc.insets = new Insets(0, 3, 0, 0);
                p.add(btn, gbc);


                btn = ButtonFactory.createPickAttributesButton(editor, disposables);
                btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                labels.configureToolBarButton(btn, "attributesPick");
                gbc = new GridBagConstraints();
                gbc.gridy = 1;
                gbc.insets = new Insets(3, 0, 0, 0);
                p.add(btn, gbc);

                btn = ButtonFactory.createApplyAttributesButton(editor, disposables);
                btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                labels.configureToolBarButton(btn, "attributesApply");
                gbc = new GridBagConstraints();
                gbc.gridy = 1;
                gbc.insets = new Insets(3, 3, 0, 0);
                p.add(btn, gbc);

                JPopupButton pb = new JPopupButton();
                pb.setUI((PaletteButtonUI) PaletteButtonUI.createUI(pb));
                pb.setItemFont(UIManager.getFont("MenuItem.font"));
                labels.configureToolBarButton(pb, "actions");
                popupButton = pb;
                updatePopupMenu();

                gbc = new GridBagConstraints();
                gbc.gridy = 2;
                gbc.insets = new Insets(3, 0, 0, 0);
                p.add(pb, gbc);
                break;
            }
        }
        return p;
    }

    private void updatePopupMenu() {
        if (popupButton != null) {
            AbstractSelectedAction d;
            JPopupButton pb = popupButton;
            pb.removeAll();
            pb.add(new DuplicateAction());
            pb.addSeparator();
            pb.add(d = new GroupAction(editor, new SVGGroupFigure()));
            disposables.add(d);
            pb.add(d = new UngroupAction(editor, new SVGGroupFigure()));
            disposables.add(d);
            pb.addSeparator();
            pb.add(new CutAction());
            pb.add(new CopyAction());
            pb.add(new PasteAction());
            pb.add(new DeleteAction());
            pb.addSeparator();
            pb.add(new SelectAllAction());
            pb.add(d = new SelectSameAction(editor));
            disposables.add(d);
            pb.add(new ClearSelectionAction());
            if (!getPopupActions().isEmpty()) {
                pb.addSeparator();
                for (Action a : getPopupActions()) {
                    if (a == null) {
                        pb.addSeparator();
                    } else {
                        pb.add(a);
                    }
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected String getID() {
        return "actions";
    }

    @Override
    protected int getDefaultDisclosureState() {
        return 1;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
