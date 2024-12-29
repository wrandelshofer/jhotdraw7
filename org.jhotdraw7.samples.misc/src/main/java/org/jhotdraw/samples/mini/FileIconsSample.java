/* @(#)FileIconsSample.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.mini;

import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.ImageFigure;
import org.jhotdraw.draw.TextAreaFigure;
import org.jhotdraw.draw.layouter.VerticalLayouter;
import org.jhotdraw.draw.tool.DelegationSelectionTool;
import org.jhotdraw.geom.Dimension2DDouble;
import org.jhotdraw.geom.Insets2D;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileSystemView;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.jhotdraw.draw.AttributeKeys.Alignment;
import static org.jhotdraw.draw.AttributeKeys.COMPOSITE_ALIGNMENT;
import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.TEXT_ALIGNMENT;

/**
 * Example showing how to lay out composite figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class FileIconsSample {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // Let the user choose a directory
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setDialogTitle("Choose a directory");
                if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                    System.exit(0);
                }


                // Create a drawing
                Drawing drawing = new DefaultDrawing();

                // Add a figure for each file to the drawing
                File dir = fc.getSelectedFile();
                File[] files = dir.listFiles();
                FileSystemView fsv = FileSystemView.getFileSystemView();
                int maxColumn = Math.max((int) Math.sqrt(files.length), 1);
                double tx = 0;
                double ty = 0;
                double rowHeight = 0;
                int i = 0;
                for (File f : files) {
                    // Create an image figure for the file icon
                    Icon icon = fsv.getSystemIcon(f);
                    BufferedImage bimg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = bimg.createGraphics();
                    icon.paintIcon(null, g, 0, 0);
                    g.dispose();
                    ImageFigure imf = new ImageFigure();
                    imf.setBufferedImage(bimg);
                    imf.set(STROKE_COLOR, null);
                    imf.setBounds(new Point2D.Double(0, 0), new Point2D.Double(icon.getIconWidth(), icon.getIconHeight()));

                    // Creata TextAreaFigure for the file name
                    // We limit its width to 100 Pixels
                    TextAreaFigure tef = new TextAreaFigure(f.getName());
                    Dimension2DDouble dim = tef.getPreferredTextSize(100);
                    Insets2D.Double insets = tef.getInsets();
                    tef.setBounds(new Point2D.Double(0, 0),
                            new Point2D.Double(Math.max(100, dim.width) + insets.left + insets.right,
                                    dim.height + insets.top + insets.bottom));
                    tef.set(STROKE_COLOR, null);
                    tef.set(FILL_COLOR, null);
                    tef.set(TEXT_ALIGNMENT, Alignment.CENTER);


                    // Alternatively, you could just create a TextFigure
                    /*
                    TextFigure tef = new TextFigure(f.getName());
                     */

                    // Create a GraphicalCompositeFigure with vertical layout
                    // and add the icon and the text figure to it
                    GraphicalCompositeFigure gcf = new GraphicalCompositeFigure();
                    gcf.setLayouter(new VerticalLayouter());
                    gcf.set(COMPOSITE_ALIGNMENT, Alignment.CENTER);
                    gcf.add(imf);
                    gcf.add(tef);
                    gcf.layout();

                    // Lay out the graphical composite figures on the drawing
                    if (i++ % maxColumn == 0) {
                        ty += rowHeight + 20;
                        tx = 0;
                        rowHeight = 0;
                    }
                    Rectangle2D.Double b = gcf.getBounds();
                    rowHeight = Math.max(rowHeight, b.height);

                    AffineTransform at = new AffineTransform();
                    at.translate(tx, ty);
                    gcf.transform(at);

                    tx += b.width + 20;

                    drawing.add(gcf);
                }

                // Show the drawing
                JFrame f = new JFrame("Contents of directory " + dir.getName());
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400, 300);

                DrawingView view = new DefaultDrawingView();
                view.setDrawing(drawing);
                f.getContentPane().add(view.getComponent());
                DrawingEditor editor = new DefaultDrawingEditor();
                editor.setTool(new DelegationSelectionTool());
                editor.add(view);
                editor.setActiveView(view);

                f.setVisible(true);
            }
        });
    }
}
