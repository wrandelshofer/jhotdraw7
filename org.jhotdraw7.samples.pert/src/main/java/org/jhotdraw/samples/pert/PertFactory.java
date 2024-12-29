/* @(#)PertFactory.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.pert;

import org.jhotdraw.draw.DefaultDrawing;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.ListFigure;
import org.jhotdraw.draw.TextAreaFigure;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.decoration.ArrowTip;
import org.jhotdraw.draw.locator.RelativeLocator;
import org.jhotdraw.samples.pert.figures.DependencyFigure;
import org.jhotdraw.samples.pert.figures.SeparatorLineFigure;
import org.jhotdraw.samples.pert.figures.TaskFigure;
import org.jhotdraw.xml.DefaultDOMFactory;

/**
 * PertFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PertFactory extends DefaultDOMFactory {
    private static final Object[][] classTagArray = {
            {DefaultDrawing.class, "PertDiagram"},
            {TaskFigure.class, "task"},
            {DependencyFigure.class, "dep"},
            {ListFigure.class, "list"},
            {TextFigure.class, "text"},
            {GroupFigure.class, "g"},
            {TextAreaFigure.class, "ta"},
            {SeparatorLineFigure.class, "separator"},

            {ChopRectangleConnector.class, "rectConnector"},
            {LocatorConnector.class, "locConnector"},
            {RelativeLocator.class, "relativeLocator"},
            {ArrowTip.class, "arrowTip"}
    };

    /**
     * Creates a new instance.
     */
    public PertFactory() {
        for (Object[] o : classTagArray) {
            addStorableClass((String) o[1], (Class) o[0]);
        }
    }
}
