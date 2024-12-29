/* @(#)OSXOpenFileAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app.action.app;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.app.PrintableView;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.file.PrintFileAction;
import org.jhotdraw.gui.BackgroundTask;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

/**
 * Prints a file for which a print request was sent to the application.
 * <p>
 * The file name is passed in the action command of the action event.
 * <p>
 * This action is called when {@code DefaultOSXApplication} receives a print
 * request from another application. The file name is passed in the action
 * command of the action event.
 * <p>
 * If you want this behavior in your application, you have to create an action
 * with this ID and put it in your {@code ApplicationModel} in method
 * {@link org.jhotdraw.app.ApplicationModel#initApplication}. The views created by
 * {@code ApplicationModel} must implement the {@link PrintableView} interface.
 * <p>
 * You should also create a {@link PrintFileAction} when you create this action.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PrintApplicationFileAction extends PrintFileAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "application.printFile";
    private JFileChooser fileChooser;
    private int entries;

    /**
     * Creates a new instance.
     */
    public PrintApplicationFileAction(Application app) {
        super(app, null);
        putValue(Action.NAME, "OSX Print File");
    }

    public void actionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        final String filename = evt.getActionCommand();
        View v = app.createView();
        if (!(v instanceof PrintableView)) {
            return;
        }
        final PrintableView p = (PrintableView) v;
        p.setEnabled(false);
        app.add(p);
//            app.show(p);
        p.execute(new BackgroundTask() {

            @Override
            public void construct() throws IOException {
                p.read(new File(filename).toURI(), null);
            }

            @Override
            protected void done() {
                p.setURI(new File(filename).toURI());
                p.setEnabled(false);
                if ("true".equals(System.getProperty("apple.awt.graphics.UseQuartz", "false"))) {
                    printQuartz(p);
                } else {
                    printJava2D(p);
                }
                p.setEnabled(true);
                app.dispose(p);
            }

            @Override
            protected void failed(Throwable value) {
                ResourceBundleUtil labels = ApplicationLabels.getLabels();
                app.dispose(p);
                JOptionPane.showMessageDialog(
                        null,
                        "<html>" + UIManager.getString("OptionPane.css")
                                + "<b>" + labels.getFormatted("file.open.couldntOpen.message", new File(filename).getName()) + "</b><p>"
                                + value,
                        "",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
