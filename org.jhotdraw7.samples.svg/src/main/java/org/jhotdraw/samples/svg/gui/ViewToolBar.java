/* @(#)CanvasToolBar.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.svg.gui;

import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.GridConstrainer;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.gui.JLifeFormattedTextField;
import org.jhotdraw.gui.plaf.palette.PaletteButtonUI;
import org.jhotdraw.gui.plaf.palette.PaletteFormattedTextFieldUI;
import org.jhotdraw.samples.svg.SVGLabels;
import org.jhotdraw.text.JavaNumberFormatter;
import org.jhotdraw.util.ResourceBundleUtil;
import org.jhotdraw.util.prefs.PreferencesUtil;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultFormatterFactory;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * ViewToolBar.
 * <p>
 * Note: you must explicitly set the view before createDisclosedComponents is
 * called for the first time.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ViewToolBar extends AbstractToolBar {
    private static final long serialVersionUID = 1L;

    private DrawingView view;

    /**
     * Creates new instance.
     */
    public ViewToolBar() {
        ResourceBundleUtil labels = SVGLabels.getLabels();
        setName(labels.getString(getID() + ".toolbar"));
        setDisclosureStateCount(3);
    }

    public void setView(DrawingView view) {
        this.view = view;
        prefs = PreferencesUtil.userNodeForPackage(getClass());
        GridConstrainer constrainer = (GridConstrainer) view.getVisibleConstrainer();
        constrainer.setHeight(prefs.getDouble("view.gridSize", 8d));
        constrainer.setWidth(prefs.getDouble("view.gridSize", 8d));
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


                ResourceBundleUtil labels = SVGLabels.getLabels();
                GridBagLayout layout = new GridBagLayout();
                p.setLayout(layout);
                GridBagConstraints gbc;
                AbstractButton btn;

                // Toggle Grid Button
                AbstractButton toggleGridButton;
                toggleGridButton = btn = ButtonFactory.createToggleGridButton(view);
                btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                labels.configureToolBarButton(btn, "alignGrid");
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                gbc.fill = GridBagConstraints.NONE;
                gbc.insets = new Insets(0, 0, 0, 0);
                p.add(btn, gbc);

                // Zoom button
                btn = ButtonFactory.createZoomButton(view);
                btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                labels.configureToolBarButton(btn, "view.zoomFactor");
                btn.setText("100 %");
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 1;
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                gbc.insets = new Insets(3, 0, 0, 0);
                gbc.weighty = 1;
                gbc.weightx = 1;
                btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, toggleGridButton.getPreferredSize().height));
                p.add(btn, gbc);
            }
            break;
            case 2: {
                p = new JPanel();
                p.setOpaque(false);
                p.setBorder(new EmptyBorder(5, 5, 5, 8));

                // Abort if no editor is set
                if (editor == null) {
                    break;
                }

                ResourceBundleUtil labels = SVGLabels.getLabels();
                GridBagLayout layout = new GridBagLayout();
                p.setLayout(layout);
                GridBagConstraints gbc;
                AbstractButton btn;

                // Grid size field and toggle grid button
                JLifeFormattedTextField gridSizeField = new JLifeFormattedTextField();
                gridSizeField.setColumns(4);
                gridSizeField.setToolTipText(labels.getString("view.gridSize.toolTipText"));
                gridSizeField.setHorizontalAlignment(JLifeFormattedTextField.RIGHT);
                gridSizeField.putClientProperty("Palette.Component.segmentPosition", "first");
                gridSizeField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(gridSizeField));
                gridSizeField.setFormatterFactory(JavaNumberFormatter.createFormatterFactory(0d, 1000d, 1d, true));
                gridSizeField.setHorizontalAlignment(JTextField.LEADING);
                final GridConstrainer constrainer = (GridConstrainer) view.getVisibleConstrainer();
                gridSizeField.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("value".equals(evt.getPropertyName())) {
                            if (evt.getNewValue() != null) {
                                constrainer.setWidth((Double) evt.getNewValue());
                                constrainer.setHeight((Double) evt.getNewValue());
                                prefs = PreferencesUtil.userNodeForPackage(getClass());
                                try {
                                    prefs.putDouble("view.gridSize", (Double) evt.getNewValue());
                                } catch (IllegalStateException e) {//ignore
                                }
                                view.getComponent().repaint();
                            }
                        }
                    }
                });
                gridSizeField.setValue(constrainer.getHeight());

                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 0;
                gbc.insets = new Insets(0, 0, 0, 0);
                gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                p.add(gridSizeField, gbc);
                btn = ButtonFactory.createToggleGridButton(view);
                btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                labels.configureToolBarButton(btn, "alignGrid");
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 0;
                gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                gbc.fill = GridBagConstraints.NONE;
                gbc.insets = new Insets(0, 0, 0, 0);
                p.add(btn, gbc);

                // Zoom factor field and zoom button
                final JLifeFormattedTextField scaleFactorField = new JLifeFormattedTextField();
                scaleFactorField.setColumns(4);
                scaleFactorField.setToolTipText(labels.getString("view.zoomFactor.toolTipText"));
                scaleFactorField.setHorizontalAlignment(JLifeFormattedTextField.RIGHT);
                scaleFactorField.putClientProperty("Palette.Component.segmentPosition", "first");
                scaleFactorField.setUI((PaletteFormattedTextFieldUI) PaletteFormattedTextFieldUI.createUI(scaleFactorField));
                JavaNumberFormatter formatter = new JavaNumberFormatter(0.01d, 50d, 100d, false, "%");
                formatter.setUsesScientificNotation(false);
                formatter.setMaximumFractionDigits(1);
                scaleFactorField.setFormatterFactory(new DefaultFormatterFactory(formatter));
                scaleFactorField.setHorizontalAlignment(JTextField.LEADING);
                scaleFactorField.setValue(view.getScaleFactor());
                scaleFactorField.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if ("value".equals(evt.getPropertyName())) {
                            if (evt.getNewValue() != null) {
                                view.setScaleFactor((Double) evt.getNewValue());
                            }
                        }
                    }
                });
                view.addPropertyChangeListener(new PropertyChangeListener() {

                    @Override
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName() == DrawingView.SCALE_FACTOR_PROPERTY) {
                            if (evt.getNewValue() != null) {
                                scaleFactorField.setValue((Double) evt.getNewValue());
                            }
                        }
                    }
                });
                gbc = new GridBagConstraints();
                gbc.gridx = 0;
                gbc.gridy = 1;
                gbc.insets = new Insets(3, 0, 0, 0);
                gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                p.add(scaleFactorField, gbc);
                btn = ButtonFactory.createZoomButton(view);
                btn.setUI((PaletteButtonUI) PaletteButtonUI.createUI(btn));
                labels.configureToolBarButton(btn, "view.zoomFactor");
                btn.setText("100 %");
                gbc = new GridBagConstraints();
                gbc.gridx = 1;
                gbc.gridy = 1;
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.anchor = GridBagConstraints.FIRST_LINE_START;
                gbc.insets = new Insets(3, 0, 0, 0);
                gbc.weighty = 1;
                btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, scaleFactorField.getPreferredSize().height));
                p.add(btn, gbc);
            }
            break;
        }
        return p;
    }

    @Override
    protected String getID() {
        return "view";
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setOpaque(false);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
