/* @(#)ColorIcon.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.action;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.util.Images;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * ColorIcon.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ColorIcon implements javax.swing.Icon {

    @Nullable
    private Color fillColor;
    private static BufferedImage noColorImage;
    private int width;
    private int height;
    private String name;

    /**
     * Creates a new instance.
     */
    public ColorIcon(int rgb) {
        this(new Color(rgb));
    }

    public ColorIcon(@Nullable Color color) {
        this(color, (color == null) ? DrawLabels.getLabels().getToolTipTextProperty("attribute.color.noColor") : color.getRed() + "," + color.getGreen() + "," + color.getBlue(), 14, 14);
    }

    public ColorIcon(int rgb, String name) {
        this(new Color(rgb), name, 14, 14);
    }

    public ColorIcon(@Nullable Color color, String name) {
        this(color, name, 14, 14);
    }

    public ColorIcon(@Nullable Color color, String name, int width, int height) {
        this.fillColor = color;
        this.name = name;
        this.width = width;
        this.height = height;
        if (noColorImage == null) {
            noColorImage = Images.toBufferedImage(Images.createImage(ColorIcon.class, "/org/jhotdraw/draw/action/images/attribute.color.noColor.png"));
        }
    }

    public Color getColor() {
        return fillColor;
    }

    public String getName() {
        return name;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        //Graphics2D g = (Graphics2D) gr;
        if (fillColor == null || fillColor.getAlpha() == 0) {
            if (width == noColorImage.getWidth() && height == noColorImage.getHeight()) {
                g.drawImage(noColorImage, x, y, c);
            } else {
                g.setColor(Color.WHITE);
                g.fillRect(x + 1, y + 1, width - 2, height - 2);
                g.setColor(Color.red);
                int[] xpoints = new int[]{x + 2, x + width - 5, x + width - 3, x + width - 3, x + 4, x + 2};
                int[] ypoints = new int[]{y + height - 5, y + 2, y + 2, y + 4, y + height - 3, y + height - 3};
                g.fillPolygon(xpoints, ypoints, xpoints.length);
            }
        } else {
            //           g.setColor(Color.WHITE);
            //           g.fillRect(x + 1, y + 1, width - 2, height - 2);
            g.setColor(fillColor);
//            g.fillRect(x + 2, y + 2, width - 4, height - 4);
            g.fillRect(x + 1, y + 1, width - 2, height - 2);
        }
        g.setColor(new Color(0x666666));

        // Draw the rectangle using drawLine to work around a drawing bug in
        // Apples MRJ for Java 1.5
        // g.drawRect(x, y, getIconWidth() - 1, getIconHeight() - 1);
        g.drawLine(x, y, x + width - 1, y);
        g.drawLine(x + width - 1, y, x + width - 1, y + width - 1);
        g.drawLine(x + width - 1, y + height - 1, x, y + height - 1);
        g.drawLine(x, y + height - 1, x, y);

    }
}
