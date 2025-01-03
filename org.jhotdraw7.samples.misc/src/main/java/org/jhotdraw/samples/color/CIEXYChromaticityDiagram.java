/* @(#)CIEXYChromaticityDiagram.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.color;

import org.jhotdraw.color.CIEXYChromaticityDiagramImageProducer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

/**
 * CIEXYChromaticityDiagram.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CIEXYChromaticityDiagram extends javax.swing.JPanel {
    private static final long serialVersionUID = 1L;
    private CIEXYChromaticityDiagramImageProducer imageProducer;
    private Image image;

    /**
     * Creates new form CIEXYChromaticityDiagram
     */
    public CIEXYChromaticityDiagram() {
        initComponents();
    }

    @Override
    public void paintComponent(Graphics gr) {
        Graphics2D g = (Graphics2D) gr;
        int side = Math.min(this.getWidth(), this.getHeight());
        if (imageProducer == null//
                || imageProducer.getWidth() != side) {

            if (image != null) {
                image.flush();
            }

            imageProducer = new CIEXYChromaticityDiagramImageProducer(side, side);
            imageProducer.generateImage();
            image = getToolkit().createImage(imageProducer);
        }

        g.drawImage(image, 0, 0, this);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame f = new JFrame("CIE xy Chromaticity Diagram");
                f.add(new CIEXYChromaticityDiagram());
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setSize(400, 400);
                f.setVisible(true);


            }
        });


    }
}
