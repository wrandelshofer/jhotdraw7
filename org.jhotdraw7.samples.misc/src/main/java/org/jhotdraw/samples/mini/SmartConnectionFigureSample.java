/* @(#)SmartConnectionFigureSample.java
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
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.tool.DelegationSelectionTool;
import org.jhotdraw.geom.Geom;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.geom.Point2D;

import static org.jhotdraw.draw.AttributeKeys.STROKE_TYPE;
import static org.jhotdraw.draw.AttributeKeys.StrokeType;

/**
 * Example showing how to connect two text areas with an elbow connection.
 * <p>
 * The 'SmartConnectionFigure', that is used to connect the two areas draws
 * with double stroke, if the Figure at the start and at the end of the connection
 * is the same.
 * <p>
 * In order to prevent editing of the stroke type by the user, the
 * SmartConnectionFigure disables the stroke type attribute. Unless it needs
 * to be changed by the SmartConnectionFigure by itself.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SmartConnectionFigureSample {

    private static class SmartConnectionFigure extends LineConnectionFigure {
        private static final long serialVersionUID = 1L;

        public SmartConnectionFigure() {
            setAttributeEnabled(STROKE_TYPE, false);
        }

        @Override
        public void handleConnect(Connector start, Connector end) {
            setAttributeEnabled(STROKE_TYPE, true);
            willChange();
            set(STROKE_TYPE,
                    (start.getOwner() == end.getOwner()) ? StrokeType.DOUBLE : StrokeType.BASIC);
            changed();
            setAttributeEnabled(STROKE_TYPE, false);
        }

        @Override
        public void handleDisconnect(Connector start, Connector end) {
            setAttributeEnabled(STROKE_TYPE, true);
            willChange();
            set(STROKE_TYPE, StrokeType.BASIC);
            changed();
            setAttributeEnabled(STROKE_TYPE, false);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {

                // Create a simple drawing consisting of three
                // text areas and an elbow connection.
                TextAreaFigure ta = new TextAreaFigure();
                ta.setBounds(new Point2D.Double(10, 30), new Point2D.Double(100, 100));
                TextAreaFigure tb = new TextAreaFigure();
                tb.setBounds(new Point2D.Double(220, 130), new Point2D.Double(310, 210));
                TextAreaFigure tc = new TextAreaFigure();
                tc.setBounds(new Point2D.Double(220, 30), new Point2D.Double(310, 100));
                ConnectionFigure cf = new SmartConnectionFigure();
                cf.setLiner(new ElbowLiner());
                cf.setStartConnector(ta.findConnector(Geom.center(ta.getBounds()), cf));
                cf.setEndConnector(tb.findConnector(Geom.center(tb.getBounds()), cf));
                Drawing drawing = new DefaultDrawing();
                drawing.add(ta);
                drawing.add(tb);
                drawing.add(tc);
                drawing.add(cf);

                // Show the drawing
                JFrame f = new JFrame("'Smart' ConnectionFigure Sample");
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400, 300);

                // Set up the drawing view
                DrawingView view = new DefaultDrawingView();
                view.setDrawing(drawing);
                f.getContentPane().add(view.getComponent());

                // Set up the drawing editor
                DrawingEditor editor = new DefaultDrawingEditor();
                editor.add(view);
                editor.setTool(new DelegationSelectionTool());

                f.setVisible(true);
            }
        });
    }
}
