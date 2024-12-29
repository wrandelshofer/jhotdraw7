/* @(#)EditorSample.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.mini;

import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.TextAreaFigure;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.tool.DelegationSelectionTool;
import org.jhotdraw.geom.Geom;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.geom.Point2D;

/**
 * Example showing how to create an editor that can edit figures on a drawing
 * using the DelegationSelectionTool.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EditorSample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                // Create a simple drawing consisting of three
                // text areas and an elbow connection.
                TextAreaFigure ta = new TextAreaFigure();
                ta.setBounds(new Point2D.Double(10, 10), new Point2D.Double(100, 100));
                TextAreaFigure tb = new TextAreaFigure();
                tb.setBounds(new Point2D.Double(220, 120), new Point2D.Double(310, 210));
                TextAreaFigure tc = new TextAreaFigure();
                tc.setBounds(new Point2D.Double(220, 10), new Point2D.Double(310, 100));
                ConnectionFigure cf = new LineConnectionFigure();
                cf.setLiner(new ElbowLiner());
                cf.setStartConnector(ta.findConnector(Geom.center(ta.getBounds()), cf));
                cf.setEndConnector(tb.findConnector(Geom.center(tb.getBounds()), cf));
                Drawing drawing = new DefaultDrawing();
                drawing.add(ta);
                drawing.add(tb);
                drawing.add(tc);
                drawing.add(cf);

                // Create a frame with a drawing view and a drawing editor
                JFrame f = new JFrame("My Drawing");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400, 300);
                DrawingView view = new DefaultDrawingView();
                view.setDrawing(drawing);
                f.getContentPane().add(view.getComponent());
                DrawingEditor editor = new DefaultDrawingEditor();
                editor.add(view);
                editor.setTool(new DelegationSelectionTool());
                f.setVisible(true);
            }
        });
    }
}
