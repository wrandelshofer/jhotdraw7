/* @(#)MultiEditorSample.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.mini;

import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.action.ButtonFactory;
import org.jhotdraw.draw.io.SerializationInputOutputFormat;
import org.jhotdraw.draw.tool.CreationTool;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Example showing how to create a drawing editor which acts on four drawing
 * views.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MultiEditorSample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                ResourceBundleUtil labels = DrawLabels.getLabels();

                // Create four drawing views, each one with its own drawing
                DrawingView view1 = new DefaultDrawingView();
                view1.setDrawing(createDrawing());
                DrawingView view2 = new DefaultDrawingView();
                view2.setDrawing(createDrawing());
                DrawingView view3 = new DefaultDrawingView();
                view3.setDrawing(createDrawing());
                DrawingView view4 = new DefaultDrawingView();
                view4.setDrawing(createDrawing());

                // Create a common drawing editor for the views
                DrawingEditor editor = new DefaultDrawingEditor();
                editor.add(view1);
                editor.add(view2);
                editor.add(view3);
                editor.add(view4);

                // Create a tool bar with selection tool and a
                // creation tool for rectangle figures.
                JToolBar tb = new JToolBar();
                ButtonFactory.addSelectionToolTo(tb, editor);
                ButtonFactory.addToolTo(
                        tb, editor,
                        new CreationTool(new RectangleFigure()),
                        "edit.createRectangle",
                        labels);
                tb.setOrientation(JToolBar.VERTICAL);

                // Put all together into a JFrame
                JFrame f = new JFrame("Multi-Editor");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400, 300);

                // Set up the content pane
                // Place the toolbar on the left
                // Place each drawing view into a scroll pane of its own
                // and put them into a larger scroll pane.
                JPanel innerPane = new JPanel();
                innerPane.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
                JScrollPane sp;
                innerPane.add(sp = new JScrollPane(view1.getComponent()));
                sp.setPreferredSize(new Dimension(200, 200));
                innerPane.add(sp = new JScrollPane(view2.getComponent()));
                sp.setPreferredSize(new Dimension(200, 200));
                innerPane.add(sp = new JScrollPane(view3.getComponent()));
                sp.setPreferredSize(new Dimension(200, 200));
                innerPane.add(sp = new JScrollPane(view4.getComponent()));
                sp.setPreferredSize(new Dimension(200, 200));
                f.getContentPane().add(new JScrollPane(innerPane));

                f.getContentPane().add(tb, BorderLayout.WEST);

                f.setVisible(true);
            }
        });
    }

    /**
     * Creates a drawing with input and output formats, so that drawing figures
     * can be copied and pasted between drawing views.
     *
     * @return a drawing
     */
    private static Drawing createDrawing() {
        // Create a default drawing with
        // input/output formats for basic clipboard support.
        DefaultDrawing drawing = new DefaultDrawing();
        drawing.addInputFormat(new SerializationInputOutputFormat());
        drawing.addOutputFormat(new SerializationInputOutputFormat());
        return drawing;
    }
}
