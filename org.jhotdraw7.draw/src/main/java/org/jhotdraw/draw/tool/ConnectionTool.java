/* @(#)ConnectionTool.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.draw.tool;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.DrawLabels;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * A tool to create a connection between two figures.
 * The  {@link ConnectionFigure} to be created is specified by a prototype.
 * The location of the start and end points are controlled by {@link Connector}s.
 * <p>
 * To create a connection using the ConnectionTool, the user does the following
 * mouse gestures on a DrawingView:
 * <ol>
 * <li>Press the mouse button inside of a Figure. If the ConnectionTool can
 * find a Connector at this location, it uses it as the starting point for
 * the connection.</li>
 * <li>Drag the mouse while keeping the mouse button pressed, and then release
 * the mouse button. This defines the end point of the connection.
 * If the ConnectionTool finds a Connector at this location, it uses it
 * as the end point of the connection and creates a ConnectionFigure.</li>
 * </ol>
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Framework</em><br>
 * Two figures can be connected using a connection figure.  The location of
 * the start or end point of the connection is handled by a connector object
 * at each connected figure.<br>
 * Contract: {@link org.jhotdraw.draw.Figure},
 * {@link org.jhotdraw.draw.ConnectionFigure},
 * {@link org.jhotdraw.draw.connector.Connector},
 * {@link org.jhotdraw.draw.tool.ConnectionTool}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ConnectionTool extends AbstractTool {
    private static final long serialVersionUID = 1L;

    /**
     * FIXME - The ANCHOR_WIDTH value must be retrieved from the DrawingEditor
     */
    private static final int ANCHOR_WIDTH = 6;
    /**
     * Attributes to be applied to the created ConnectionFigure.
     * These attributes override the default attributes of the
     * DrawingEditor.
     */
    @Nullable
    private Map<AttributeKey<?>, Object> prototypeAttributes;
    /**
     * The Connector at the start point of the connection.
     */
    @Nullable
    protected Connector startConnector;
    /**
     * The Connector at the end point of the connection.
     */
    @Nullable
    protected Connector endConnector;
    /**
     * The created figure.
     */
    @Nullable
    protected ConnectionFigure createdFigure;
    /**
     * the prototypical figure that is used to create new
     * connections.
     */
    protected ConnectionFigure prototype;
    /**
     * The figure for which we enabled drawing of connectors.
     */
    @Nullable
    protected Figure targetFigure;
    protected Collection<Connector> connectors = Collections.emptyList();
    /**
     * A localized name for this tool. The presentationName is displayed by the
     * UndoableEdit.
     */
    @Nullable
    private String presentationName;
    /**
     * If this is set to false, the CreationTool does not fire toolDone
     * after a new Figure has been created. This allows to create multiple
     * figures consecutively.
     */
    private boolean isToolDoneAfterCreation = true;

    /**
     * Creates a new instance.
     */
    public ConnectionTool(ConnectionFigure prototype) {
        this(prototype, null, null);
    }

    public ConnectionTool(ConnectionFigure prototype, @Nullable Map<AttributeKey<?>, Object> attributes) {
        this(prototype, attributes, null);
    }

    public ConnectionTool(ConnectionFigure prototype, @Nullable Map<AttributeKey<?>, Object> attributes, @Nullable String presentationName) {
        this.prototype = prototype;
        this.prototypeAttributes = attributes;
        if (presentationName == null) {
            ResourceBundleUtil labels = DrawLabels.getLabels();
            presentationName = labels.getString("edit.createConnectionFigure.text");
        }
        this.presentationName = presentationName;
    }

    public ConnectionTool(String prototypeClassName) {
        this(prototypeClassName, null, null);
    }

    public ConnectionTool(String prototypeClassName, @Nullable Map<AttributeKey<?>, Object> attributes, @Nullable String presentationName) {
        try {
            this.prototype = (ConnectionFigure) Class.forName(prototypeClassName).getConstructor().newInstance();
        } catch (Exception e) {
            InternalError error = new InternalError("Unable to create ConnectionFigure from " + prototypeClassName);
            error.initCause(e);
            throw error;
        }
        this.prototypeAttributes = attributes;
        if (presentationName == null) {
            ResourceBundleUtil labels = DrawLabels.getLabels();
            presentationName = labels.getString("edit.createConnectionFigure.text");
        }
        this.presentationName = presentationName;
    }

    public ConnectionFigure getPrototype() {
        return prototype;
    }

    protected int getAnchorWidth() {
        return ANCHOR_WIDTH;
    }

    /**
     * This method is called on the Figure, onto which the user wants
     * to start a new connection.
     *
     * @param f              The ConnectionFigure.
     * @param startConnector The Connector of the start Figure.
     * @return True, if a connection can be made.
     */
    protected boolean canConnect(ConnectionFigure f, Connector startConnector) {
        return f.canConnect(startConnector);
    }

    /**
     * This method is called on the Figure, onto which the user wants
     * to end a new connection.
     *
     * @param f              The ConnectionFigure.
     * @param startConnector The Connector of the start Figure.
     * @param endConnector   The Connector of the end Figure.
     * @return True, if a connection can be made.
     */
    protected boolean canConnect(ConnectionFigure f, Connector startConnector, Connector endConnector) {
        return f.canConnect(startConnector, endConnector);
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        repaintConnectors(evt);
    }

    /**
     * Updates the list of connectors that we draw when the user
     * moves or drags the mouse over a figure to which can connect.
     */
    public void repaintConnectors(MouseEvent evt) {
        Rectangle2D.Double invalidArea = null;
        Point2D.Double targetPoint = viewToDrawing(new Point(evt.getX(), evt.getY()));
        Figure aFigure = getDrawing().findFigureExcept(targetPoint, createdFigure);
        if (aFigure != null && !aFigure.isConnectable()) {
            aFigure = null;
        }
        if (targetFigure != aFigure) {
            for (Connector c : connectors) {
                if (invalidArea == null) {
                    invalidArea = c.getDrawingArea();
                } else {
                    invalidArea.add(c.getDrawingArea());
                }
            }
            targetFigure = aFigure;
            if (targetFigure != null) {
                connectors = targetFigure.getConnectors(getPrototype());
                for (Connector c : connectors) {
                    if (invalidArea == null) {
                        invalidArea = c.getDrawingArea();
                    } else {
                        invalidArea.add(c.getDrawingArea());
                    }
                }
            }
        }
        if (invalidArea != null) {
            getView().getComponent().repaint(
                    getView().drawingToView(invalidArea));
        }
    }

    /**
     * Manipulates connections in a context dependent way. If the
     * mouse down hits a figure start a new connection. If the mousedown
     * hits a connection split a segment or join two segments.
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        getView().clearSelection();

        Point2D.Double startPoint = viewToDrawing(anchor);
        Figure startFigure = getDrawing().findFigure(startPoint);
        startConnector = (startFigure == null) ? null : startFigure.findConnector(startPoint, prototype);

        if (startConnector != null && canConnect(prototype, startConnector)) {
            Point2D.Double anchor = startConnector.getAnchor();
            createdFigure = createFigure();
            createdFigure.setStartPoint(anchor);
            createdFigure.setEndPoint(anchor);
            getDrawing().add(createdFigure);
            Rectangle r = new Rectangle(getView().drawingToView(anchor));
            r.grow(ANCHOR_WIDTH, ANCHOR_WIDTH);
            fireAreaInvalidated(r);
        } else {
            startConnector = null;
            createdFigure = null;
        }

        endConnector = null;
    }

    /**
     * Adjust the created connection.
     */
    @Override
    public void mouseDragged(java.awt.event.MouseEvent e) {
        repaintConnectors(e);
        if (createdFigure != null) {
            createdFigure.willChange();
            Point2D.Double endPoint = viewToDrawing(new Point(e.getX(), e.getY()));
            getView().getConstrainer().constrainPoint(endPoint);

            Figure endFigure = getDrawing().findFigureExcept(endPoint, createdFigure);
            endConnector = (endFigure == null) ? null : endFigure.findConnector(endPoint, prototype);

            if (endConnector != null && canConnect(createdFigure, startConnector, endConnector)) {
                endPoint = endConnector.getAnchor();
            }
            Rectangle r = new Rectangle(getView().drawingToView(createdFigure.getEndPoint()));
            createdFigure.setEndPoint(endPoint);
            r.add(getView().drawingToView(endPoint));
            r.grow(ANCHOR_WIDTH + 2, ANCHOR_WIDTH + 2);
            getView().getComponent().repaint(r);
            createdFigure.changed();
        }
    }

    /**
     * Connects the figures if the mouse is released over another
     * figure.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (createdFigure != null
                && startConnector != null && endConnector != null
                && createdFigure.canConnect(startConnector, endConnector)) {
            createdFigure.willChange();
            createdFigure.setStartConnector(startConnector);
            createdFigure.setEndConnector(endConnector);
            createdFigure.updateConnection();
            createdFigure.changed();

            final Figure addedFigure = createdFigure;
            final Drawing addedDrawing = getDrawing();
            getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {
                private static final long serialVersionUID = 1L;

                @Override
                public String getPresentationName() {
                    return presentationName;
                }

                @Override
                public void undo() throws CannotUndoException {
                    super.undo();
                    addedDrawing.remove(addedFigure);
                }

                @Override
                public void redo() throws CannotRedoException {
                    super.redo();
                    addedDrawing.add(addedFigure);
                }
            });
            targetFigure = null;
            Point2D.Double anchor = startConnector.getAnchor();
            Rectangle r = new Rectangle(getView().drawingToView(anchor));
            r.grow(ANCHOR_WIDTH, ANCHOR_WIDTH);
            fireAreaInvalidated(r);
            anchor = endConnector.getAnchor();
            r = new Rectangle(getView().drawingToView(anchor));
            r.grow(ANCHOR_WIDTH, ANCHOR_WIDTH);
            fireAreaInvalidated(r);
            startConnector = endConnector = null;
            Figure finishedFigure = createdFigure;
            createdFigure = null;
            creationFinished(finishedFigure);
        } else {
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
        }
    }

    @Override
    public void activate(DrawingEditor editor) {
        super.activate(editor);
    }

    @Override
    public void deactivate(DrawingEditor editor) {
        if (createdFigure != null) {
            getDrawing().remove(createdFigure);
            createdFigure = null;
        }
        targetFigure = null;
        startConnector = endConnector = null;
        super.deactivate(editor);
    }

    /**
     * Creates the ConnectionFigure. By default the figure prototype is
     * cloned.
     */
    @SuppressWarnings("unchecked")
    protected ConnectionFigure createFigure() {
        ConnectionFigure f = (ConnectionFigure) prototype.clone();
        getEditor().applyDefaultAttributesTo(f);
        if (prototypeAttributes != null) {
            for (Map.Entry<AttributeKey<?>, Object> entry : prototypeAttributes.entrySet()) {
                f.set((AttributeKey<Object>) entry.getKey(), entry.getValue());
            }
        }
        return f;
    }

    @Override
    public void draw(Graphics2D g) {
        Graphics2D gg = (Graphics2D) g.create();
        gg.transform(getView().getDrawingToViewTransform());
        if (targetFigure != null) {
            for (Connector c : targetFigure.getConnectors(getPrototype())) {
                c.draw(gg);
            }
        }
        if (createdFigure != null) {
            createdFigure.draw(gg);
            Point p = getView().drawingToView(createdFigure.getStartPoint());
            Ellipse2D.Double e = new Ellipse2D.Double(
                    p.x - ANCHOR_WIDTH / 2, p.y - ANCHOR_WIDTH / 2,
                    ANCHOR_WIDTH, ANCHOR_WIDTH);
            g.setColor(Color.GREEN);
            g.fill(e);
            g.setColor(Color.BLACK);
            g.draw(e);
            p = getView().drawingToView(createdFigure.getEndPoint());
            e = new Ellipse2D.Double(
                    p.x - ANCHOR_WIDTH / 2, p.y - ANCHOR_WIDTH / 2,
                    ANCHOR_WIDTH, ANCHOR_WIDTH);
            g.setColor(Color.GREEN);
            g.fill(e);
            g.setColor(Color.BLACK);
            g.draw(e);

        }
        gg.dispose();
    }

    /**
     * This method allows subclasses to do perform additonal user interactions
     * after the new figure has been created.
     * The implementation of this class just invokes fireToolDone.
     */
    protected void creationFinished(Figure createdFigure) {
        if (isToolDoneAfterCreation()) {
            fireToolDone();
        }
    }

    /**
     * If this is set to false, the CreationTool does not fire toolDone
     * after a new Figure has been created. This allows to create multiple
     * figures consecutively.
     */
    public void setToolDoneAfterCreation(boolean newValue) {
        boolean oldValue = isToolDoneAfterCreation;
        isToolDoneAfterCreation = newValue;
    }

    /**
     * Returns true, if this tool fires toolDone immediately after a new
     * figure has been created.
     */
    public boolean isToolDoneAfterCreation() {
        return isToolDoneAfterCreation;
    }
}
