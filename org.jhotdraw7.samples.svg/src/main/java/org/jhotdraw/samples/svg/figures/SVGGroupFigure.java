/* @(#)SVGGroupFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.samples.svg.figures;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.TransformHandleKit;
import org.jhotdraw.samples.svg.SVGAttributeKeys;

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import static org.jhotdraw.samples.svg.SVGAttributeKeys.LINK;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.LINK_TARGET;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.OPACITY;
import static org.jhotdraw.samples.svg.SVGAttributeKeys.TRANSFORM;

/**
 * SVGGroupFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SVGGroupFigure extends GroupFigure implements SVGFigure {
    private static final long serialVersionUID = 1L;

    private HashMap<AttributeKey<?>, Object> attributes = new HashMap<AttributeKey<?>, Object>();

    /**
     * Creates a new instance.
     */
    public SVGGroupFigure() {
        SVGAttributeKeys.setDefaults(this);
    }

    @Override
    public <T> void set(AttributeKey<T> key, T value) {
        if (key == OPACITY) {
            attributes.put(key, value);
        } else if (key == LINK || key == LINK_TARGET) {
            attributes.put(key, value);
        } else {
            super.set(key, value);
        }
        invalidate();
    }

    @Override
    public <T> T get(AttributeKey<T> key) {
        return key.get(attributes);
    }

    @Override
    public Map<AttributeKey<?>, Object> getAttributes() {
        return new HashMap<AttributeKey<?>, Object>(attributes);
    }

    @SuppressWarnings("unchecked")
    public void setAttributes(Map<AttributeKey<?>, Object> map) {
        for (Map.Entry<AttributeKey<?>, Object> entry : map.entrySet()) {
            set((AttributeKey<Object>) entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void draw(Graphics2D g) {
        double opacity = get(OPACITY);
        opacity = Math.min(Math.max(0d, opacity), 1d);
        if (opacity != 0d) {
            if (opacity != 1d) {
                Rectangle2D.Double drawingArea = getDrawingArea();

                Rectangle2D clipBounds = g.getClipBounds();
                if (clipBounds != null) {
                    Rectangle2D.intersect(drawingArea, clipBounds, drawingArea);
                }

                if (!drawingArea.isEmpty()) {

                    BufferedImage buf = new BufferedImage(
                            Math.max(1, (int) ((2 + drawingArea.width) * g.getTransform().getScaleX())),
                            Math.max(1, (int) ((2 + drawingArea.height) * g.getTransform().getScaleY())),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D gr = buf.createGraphics();
                    gr.scale(g.getTransform().getScaleX(), g.getTransform().getScaleY());
                    gr.translate((int) -drawingArea.x, (int) -drawingArea.y);
                    gr.setRenderingHints(g.getRenderingHints());
                    super.draw(gr);
                    gr.dispose();
                    Composite savedComposite = g.getComposite();
                    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) opacity));
                    g.drawImage(buf, (int) drawingArea.x, (int) drawingArea.y,
                            2 + (int) drawingArea.width, 2 + (int) drawingArea.height, null);
                    g.setComposite(savedComposite);
                }
            } else {
                super.draw(g);
            }
        }
    }

    @Override
    public Rectangle2D.Double getBounds() {
        if (cachedBounds == null) {
            if (getChildCount() == 0) {
                cachedBounds = new Rectangle2D.Double();
            } else {
                for (Figure f : children) {
                    Rectangle2D.Double bounds = f.getBounds();
                    if (f.get(TRANSFORM) != null) {
                        bounds.setRect(f.get(TRANSFORM).createTransformedShape(bounds).getBounds2D());
                    }
                    if (cachedBounds == null) {
                        cachedBounds = bounds;
                    } else {
                        cachedBounds.add(bounds);
                    }
                }
            }
        }
        return (Rectangle2D.Double) cachedBounds.clone();
    }

    @Override
    public LinkedList<Handle> createHandles(int detailLevel) {
        LinkedList<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel) {
            case -1: // Mouse hover handles
                TransformHandleKit.addGroupHoverHandles(this, handles);
                break;
            case 0:
                TransformHandleKit.addGroupTransformHandles(this, handles);
                handles.add(new LinkHandle(this));
                break;
        }
        return handles;
    }

    @Override
    public boolean isEmpty() {
        return getChildCount() == 0;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append(getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1));
        buf.append('@');
        buf.append(hashCode());
        if (getChildCount() > 0) {
            buf.append('(');
            for (Iterator<Figure> i = getChildren().iterator(); i.hasNext(); ) {
                Figure child = i.next();
                buf.append(child);
                if (i.hasNext()) {
                    buf.append(',');
                }
            }
            buf.append(')');
        }
        return buf.toString();
    }

    @Override
    public SVGGroupFigure clone() {
        SVGGroupFigure that = (SVGGroupFigure) super.clone();
        that.attributes = new HashMap<AttributeKey<?>, Object>(this.attributes);
        return that;
    }
}
