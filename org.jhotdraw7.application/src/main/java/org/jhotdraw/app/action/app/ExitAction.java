/* @(#)ExitAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw.app.action.app;

import org.jhotdraw.annotation.Nullable;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.ApplicationLabels;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractApplicationAction;
import org.jhotdraw.gui.BackgroundTask;
import org.jhotdraw.gui.JSheet;
import org.jhotdraw.gui.URIChooser;
import org.jhotdraw.gui.event.SheetEvent;
import org.jhotdraw.gui.event.SheetListener;
import org.jhotdraw.net.URIUtil;
import org.jhotdraw.util.ResourceBundleUtil;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;

/**
 * Exits the application after letting the user review all unsaved views.
 * <p>
 * This action is called when the user selects the Exit item in the Application
 * menu, or when the application receives a Quit event from Mac OS X Finder.
 * The menu item is automatically created by the application.
 * <p>
 * This action is automatically created by the application and put into
 * the {@code ApplicationModel} before {@link org.jhotdraw.app.ApplicationModel#initApplication}
 * is called.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ExitAction extends AbstractApplicationAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "application.exit";
    private Component oldFocusOwner;
    private View unsavedView;

    /**
     * Creates a new instance.
     */
    public ExitAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ApplicationLabels.getLabels();
        labels.configureAction(this, ID);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        final Application app = getApplication();
        if (app.isEnabled()) {
            app.setEnabled(false);
            int unsavedViewsCount = 0;
            View documentToBeReviewed = null;
            URI unsavedURI = null;
            for (View p : app.views()) {
                if (p.hasUnsavedChanges()) {
                    if (p.isEnabled()) {
                        documentToBeReviewed = p;
                    }
                    unsavedURI = p.getURI();
                    unsavedViewsCount++;
                }
            }
            if (unsavedViewsCount > 0 && documentToBeReviewed == null) {
                // Silently abort, if no view can be reviewed.
                app.setEnabled(true);
                return;
            }

            final ResourceBundleUtil labels = ApplicationLabels.getLabels();
            switch (unsavedViewsCount) {
                case 0: {
                    doExit();
                    break;
                }
                case 1: {
                    unsavedView = documentToBeReviewed;
                    oldFocusOwner = SwingUtilities.getWindowAncestor(unsavedView.getComponent()).getFocusOwner();
                    unsavedView.setEnabled(false);
                    JOptionPane pane = new JOptionPane(
                            "<html>" + UIManager.getString("OptionPane.css")
                                    + "<b>" + labels.getFormatted("application.exit.doYouWantToSave.message",//
                                    (unsavedURI == null) ? labels.getString("unnamedFile") : URIUtil.getName(unsavedURI)) + "</b><p>"
                                    + labels.getString("application.exit.doYouWantToSave.details"),
                            JOptionPane.WARNING_MESSAGE);
                    Object[] options = {labels.getString("application.exit.saveOption"), labels.getString("application.exit.cancelOption"), labels.getString("application.exit.dontSaveOption")};
                    pane.setOptions(options);
                    pane.setInitialValue(options[0]);
                    pane.putClientProperty("Quaqua.OptionPane.destructiveOption", 2);
                    JSheet.showSheet(pane, unsavedView.getComponent(), new SheetListener() {

                        @Override
                        public void optionSelected(SheetEvent evt) {
                            Object value = evt.getValue();
                            if (value == null || value.equals(labels.getString("application.exit.cancelOption"))) {
                                unsavedView.setEnabled(true);
                                app.setEnabled(true);
                            } else if (value.equals(labels.getString("application.exit.dontSaveOption"))) {
                                doExit();
                                unsavedView.setEnabled(true);
                            } else if (value.equals(labels.getString("application.exit.saveOption"))) {
                                saveChanges();
                            }
                        }
                    });

                    break;
                }
                default: {
                    JOptionPane pane = new JOptionPane(
                            "<html>" + UIManager.get("OptionPane.css")
                                    + "<b>" + labels.getFormatted("application.exit.doYouWantToReview.message", unsavedViewsCount) + "</b><p>"
                                    + labels.getString("application.exit.doYouWantToReview.details"),
                            JOptionPane.QUESTION_MESSAGE);
                    Object[] options = {labels.getString("application.exit.reviewChangesOption"), labels.getString("application.exit.cancelOption"), labels.getString("application.exit.discardChangesOption")};
                    pane.setOptions(options);
                    pane.setInitialValue(options[0]);
                    pane.putClientProperty(
                            "Quaqua.OptionPane.destructiveOption", 2);
                    JDialog dialog = pane.createDialog(app.getComponent(), null);
                    Rectangle screenBounds = dialog.getGraphicsConfiguration().getBounds();
                    if (app.getComponent() == null || !screenBounds.contains(app.getComponent().getBounds())) {
                        // place dialog in center and in upper third of screen
                        dialog.setLocation((screenBounds.width - dialog.getWidth()) / 2, (screenBounds.height - dialog.getHeight()) / 3);
                    }
                    dialog.setVisible(true);
                    Object value = pane.getValue();
                    if (value == null || value.equals(labels.getString("application.exit.cancelOption"))) {
                        app.setEnabled(true);
                    } else if (value.equals(labels.getString("application.exit.discardChangesOption"))) {
                        doExit();
                        app.setEnabled(true);
                    } else if (value.equals(labels.getString("application.exit.reviewChangesOption"))) {
                        unsavedView = documentToBeReviewed;
                        reviewChanges();
                    }
                }
            }
        }
    }

    protected URIChooser getChooser(View view) {
        URIChooser chsr = (URIChooser) (view.getComponent()).getClientProperty("saveChooser");
        if (chsr == null) {
            chsr = getApplication().getModel().createSaveChooser(getApplication(), view);
            view.getComponent().putClientProperty("saveChooser", chsr);
        }
        return chsr;
    }

    protected void saveChanges() {
        View v = unsavedView;
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            //int option = fileChooser.showSaveDialog(this);
            JSheet.showSaveSheet(chooser, v.getComponent(), new SheetListener() {

                @Override
                public void optionSelected(final SheetEvent evt) {
                    if (evt.getOption() == JFileChooser.APPROVE_OPTION) {
                        final URI uri = evt.getChooser().getSelectedURI();
                        saveToFile(uri, evt.getChooser());
                    } else {
                        unsavedView.setEnabled(true);
                        if (oldFocusOwner != null) {
                            oldFocusOwner.requestFocus();
                        }
                        getApplication().setEnabled(true);
                    }
                }
            });
        } else {
            saveToFile(v.getURI(), null);
        }
    }

    protected void reviewChanges() {
        if (unsavedView.isEnabled()) {
            final ResourceBundleUtil labels = ApplicationLabels.getLabels();
            oldFocusOwner = SwingUtilities.getWindowAncestor(unsavedView.getComponent()).getFocusOwner();
            unsavedView.setEnabled(false);
            URI unsavedURI = unsavedView.getURI();
            JOptionPane pane = new JOptionPane(
                    "<html>" + UIManager.getString("OptionPane.css")
                            + labels.getFormatted("application.exit.doYouWantToSave.message", //
                            (unsavedURI == null) ? unsavedView.getTitle() : URIUtil.getName(unsavedURI)),
                    JOptionPane.WARNING_MESSAGE);
            Object[] options = {labels.getString("application.exit.saveOption"), labels.getString("application.exit.cancelOption"), labels.getString("application.exit.dontSaveOption")};
            pane.setOptions(options);
            pane.setInitialValue(options[0]);
            pane.putClientProperty("Quaqua.OptionPane.destructiveOption", 2);
            JSheet.showSheet(pane, unsavedView.getComponent(), new SheetListener() {

                @Override
                public void optionSelected(SheetEvent evt) {
                    Object value = evt.getValue();
                    if (value == null || value.equals(labels.getString("application.exit.cancelOption"))) {
                        unsavedView.setEnabled(true);
                        getApplication().setEnabled(true);
                    } else if (value.equals(labels.getString("application.exit.dontSaveOption"))) {
                        getApplication().dispose(unsavedView);
                        reviewNext();
                    } else if (value.equals(labels.getString("application.exit.saveOption"))) {
                        saveChangesAndReviewNext();
                    }
                }
            });
        } else {
            getApplication().setEnabled(true);
        }
    }

    protected void saveChangesAndReviewNext() {
        final View v = unsavedView;
        if (v.getURI() == null) {
            URIChooser chooser = getChooser(v);
            JSheet.showSaveSheet(chooser, unsavedView.getComponent(), new SheetListener() {

                @Override
                public void optionSelected(final SheetEvent evt) {
                    if (evt.getOption() == URIChooser.APPROVE_OPTION) {
                        final URI uri = evt.getChooser().getSelectedURI();
                        saveToFileAndReviewNext(uri, evt.getChooser());
                    } else {
                        v.setEnabled(true);
                        if (oldFocusOwner != null) {
                            oldFocusOwner.requestFocus();
                        }
                        getApplication().setEnabled(true);
                    }
                }
            });
        } else {
            saveToFileAndReviewNext(v.getURI(), null);
        }
    }

    protected void reviewNext() {
        int unsavedViewsCount = 0;
        View documentToBeReviewed = null;
        for (View p : getApplication().views()) {
            if (p.hasUnsavedChanges()) {
                if (p.isEnabled()) {
                    documentToBeReviewed = p;
                }
                unsavedViewsCount++;
            }
        }
        if (unsavedViewsCount == 0) {
            doExit();
        } else if (documentToBeReviewed != null) {
            unsavedView = documentToBeReviewed;
            reviewChanges();
        } else {
            getApplication().setEnabled(true);
            //System.out.println("exit silently aborted");
        }
    }

    protected void saveToFile(final URI uri, @Nullable final URIChooser chooser) {
        final View v = unsavedView;
        v.execute(new BackgroundTask() {

            @Override
            protected void construct() throws IOException {
                v.write(uri, chooser);
            }

            @Override
            protected void done() {
                v.setURI(uri);
                doExit();
            }

            @Override
            protected void failed(Throwable error) {
                ResourceBundleUtil labels = ApplicationLabels.getLabels();
                JSheet.showMessageSheet(v.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css")
                                + "<b>" + labels.format("file.save.couldntSave.message", URIUtil.getName(uri)) + "</b><p>"
                                + error,
                        JOptionPane.ERROR_MESSAGE);
            }

            @Override
            public void finished() {
                v.setEnabled(true);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
                getApplication().setEnabled(true);
            }
        });
    }

    protected void saveToFileAndReviewNext(final URI uri, @Nullable final URIChooser chooser) {
        final View v = unsavedView;
        v.execute(new BackgroundTask() {

            @Override
            protected void construct() throws IOException {
                v.write(uri, chooser);
            }

            @Override
            protected void done() {
                v.setURI(uri);
                getApplication().dispose(unsavedView);
                reviewNext();
            }

            @Override
            protected void failed(Throwable error) {
                ResourceBundleUtil labels = ApplicationLabels.getLabels();
                JSheet.showMessageSheet(v.getComponent(),
                        "<html>" + UIManager.getString("OptionPane.css")
                                + "<b>" + labels.format("file.save.couldntSave.message", uri) + "</b><p>"
                                + error,
                        JOptionPane.ERROR_MESSAGE);
                v.setEnabled(true);
                if (oldFocusOwner != null) {
                    oldFocusOwner.requestFocus();
                }
                getApplication().setEnabled(true);
            }
        });
    }

    protected void doExit() {
        getApplication().destroy();
    }
}
