
/*
 * Copyright (C) 2006 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 */ 

package examples;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;


/**
 * An {@code @Action} that executes a background {@code Task}.
 * <p>
 * This example demonstates the use of a background {@code Task}.
 * If an {@code @Action} returns a {@code Task}, it's executed
 * on a worker thread, and monitored by the application framework.
 * <p>
 * When executed, the {@code ListFilesTask} {@code Task} class
 * recursively lists all of the files beginning with some root, and
 * {@link Task#publish publishes} the files it finds, 10 at a time.  A
 * private subclass of {@code ListFilesTask} overrides the {@code
 * Task.process} method to update a JList's ListModel with the new
 * files:
 * <pre>
 * private class DoListFiles extends <b>ListFilesTask</b> {
 *    public DoListFiles(File root) {
 *         super(root);
 *         listModel.clear();
 *     }
 *     &#064;Override protected void process(List&lt;File&gt; files) {
 *         if (!isCancelled()) {
 *             listModel.addAll(files);
 *         }
 *     }
 * }
 * </pre>
 * <p>
 * The example's {@code go} {@code @Action}, keeps a reference to the 
 * {@code DoListFiles} background {@code Task}
 * so that the {@code stop} {@code @Action} can cancel it:
 * <pre>
 * private Task doListFiles = null;
 * &#064;Action public Task go() {
 *     stop(); // maybe cancel pending Task
 *     doListFiles = new DoListFiles(getRootFile());
 *     setStopEnabled(true);
 *     return doListFiles;
 * }
 * &#064;Action(enabledProperty = "stopEnabled") public void stop() {
 *     if ((doListFiles != null) && (doListFiles.cancel(true))) {
 *         setStopEnabled(false);
 *     }
 * }
 * </pre>
 * The {@code Action's} resources are initialized from a
 * ResourceBundle, as with {@link ActionExample2 ActionExample2}.
 * Additionally, the {@code ListFilesTask's} {@code title} and
 * {@code description} properties are initialized from the
 * {@code resources/ListFilesTask.properties} ResourceBundle:
 * <pre>
 * ListFilesTask.title = List Files
 * ListFilesTask.description = List all of the files accessible from some root directory
 * ListFilesTask.directoryMessage = Listing files in {0}
 * </pre>
 * The {@code directoryMessage} resource is used by {@code ListFilesTask}
 * to format a {@link Task#message message} each time a new directory is listed.
 * 
 * @see Action
 * @see Task
 * @author Hans Muller (Hans.Muller@Sun.COM)
 */

public class ActionExample4 extends Application {
    private final PropertyChangeListener listFilesTaskPCL = new ListFilesTaskPCL();
    private JFrame appFrame = null;
    private JTextField rootTextField = null;
    private JLabel messageLabel = null;
    private FileListModel listModel = null;
    private Task doListFiles = null;
    private boolean stopEnabled = false;

    /* This GUI-specific subclass of ListFilesTask deals with updating
     * the listModel (the list of Files found so far) each time another
     * buffer of them is delivered to the process method.  Note that it's 
     * important to check Task.isCancelled() here because even though
     * this Task has been cancelled, there may be many pending process
     * requests on the event dispatching thread.  Both the DoListFiles
     * constructor, and the process method run on the EDT.
     */
    private class DoListFiles extends ListFilesTask {
	public DoListFiles(File root) {
	    super(ActionExample4.this, root);
	    listModel.clear();
	}
	@Override protected void process(List<File> files) {
	    if (!isCancelled()) {
		listModel.addAll(files);
	    }
	}
    }

    /**
     * The {@code go} {@code @Action}.
     * <p>
     * Cancel the pending DoListFiles Task and then return a new one.
     * We add a PropertyChangeListener to the new Task so that we can
     * monitor its "message" property.
     * 
     * @return the new background Task
     * @see #stop
     */
    @Action public Task go() {
        stop();
	File root = new File(rootTextField.getText());
	doListFiles = new DoListFiles(root);
	doListFiles.addPropertyChangeListener(listFilesTaskPCL);
	setStopEnabled(true);
	return doListFiles;
    }

    /**
     * The {@code stop} {@code @Action}.
     * <p>
     * Cancel the pending DoListFiles Task, if there is one.
     * 
     * @see #go
     */
    @Action(enabledProperty = "stopEnabled")
    public void stop() {
	if ((doListFiles != null) && !doListFiles.isCancelled()) {
	    if (doListFiles.cancel(true)) {
		doListFiles.removePropertyChangeListener(listFilesTaskPCL);
		doListFiles = null;
		setStopEnabled(false);
	    }
	}
    }

    public boolean isStopEnabled() {
	return stopEnabled;
    }

    public void setStopEnabled(boolean stopEnabled) {
	boolean oldValue = this.stopEnabled;
	this.stopEnabled = stopEnabled;
	firePropertyChange("stopEnabled", oldValue, this.stopEnabled);
    }


    @Override protected void startup() {
	String javaHome = System.getProperty("java.home");
	File defaultRoot = new File(javaHome);

	/* Lookup up the Actions for this class/object in the
	 * ApplicationContext.
	 */
	ActionMap actionMap = getContext().getActionMap();
	javax.swing.Action goAction = actionMap.get("go");
	javax.swing.Action stopAction = actionMap.get("stop");

	listModel = new FileListModel();
	JList filesList = new JList(listModel);
	filesList.setPrototypeCellValue(defaultRoot);
        filesList.setVisibleRowCount(12);
	Border border = new EmptyBorder(2, 4, 2, 4);
	JScrollPane scrollPane = new JScrollPane(filesList);
	scrollPane.setBorder(border);
	rootTextField = new JTextField(defaultRoot.toString(), 32);
	rootTextField.setAction(goAction);
	JButton goButton = new JButton(goAction);
	JButton stopButton = new JButton(stopAction);
	JPanel panel = new JPanel();
	panel.add(rootTextField);
	panel.add(goButton);
	panel.add(stopButton);
	messageLabel = new JLabel(" ");
	messageLabel.setBorder(border);

	JFrame appFrame = new JFrame("ActionExample4");
	appFrame.add(scrollPane, BorderLayout.CENTER);
	appFrame.add(panel, BorderLayout.NORTH);
	appFrame.add(messageLabel, BorderLayout.SOUTH);
	appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	appFrame.pack();
	appFrame.setLocationRelativeTo(null);
	appFrame.setVisible(true);
    }

    /* Monitor the background tasks's "message" property.  
     */
    private class ListFilesTaskPCL implements PropertyChangeListener {
	public void propertyChange(PropertyChangeEvent e) {
	    String propertyName = e.getPropertyName();
	    if ("message".equals(propertyName)) {
		messageLabel.setText(e.getNewValue().toString());
	    }
	}
    }

    /* Trivial ListModel that represents all of the files found so far.
     */
    private static class FileListModel extends AbstractListModel {
	private final ArrayList<File> files = new ArrayList<File>();
	public void addAll(List<File> newFiles) {
	    if (newFiles.size() > 0) {
		files.addAll(newFiles);
		int lastIndex = files.size() - 1;
		int firstIndex = lastIndex - (newFiles.size() - 1);
		fireIntervalAdded(this, firstIndex, lastIndex);
	    }
	}
	public void clear() {
	    if (files.size() > 0) {
		int lastListIndex = files.size() - 1;
		files.clear();
		fireIntervalRemoved(this, 0, lastListIndex);
	    }
	}
	public int getSize() {
	    return files.size();
	}
	public Object getElementAt(int index) {
	    return files.get(index);
	}
    }


    /**
     * A Task that explores the file tree beginning with root
     * and publishes all of the Files it finds, 10 at a time.
     * Calls Task.message() each time a new directory is opened:
     * message("directoryMessage", "<directory name>");
     * Requires a resource defined like this: 
     * ListFilesTask.directoryMessage = Opening new directory {0}
     */
    private static class ListFilesTask extends Task<Void, File> {
	private final File root;
	private final int bufferSize;
	private final List<File> buffer;

	public ListFilesTask(Application app, File root) {
	    super(app, "ListFilesTask");
	    this.root = root;
	    bufferSize = 10;
	    buffer = new ArrayList<File>(bufferSize);
	}
	private void expand(File root) {
	    if (isCancelled()) {
		return;
	    }
	    if (root.isDirectory()) {
		message("directoryMessage", root.toString());
		for(File file : root.listFiles()) {
		    expand(file);
		}
	    }
	    else {
		buffer.add(root);
		if (buffer.size() >= bufferSize) {
		    File bufferFiles[] = new File[buffer.size()];
		    publish(buffer.toArray(bufferFiles));
		    buffer.clear();
		}
	    }
	}
	public Void doInBackground() {
	    expand(root);
	    if (!isCancelled()) {
		File bufferFiles[] = new File[buffer.size()];
		publish(bufferFiles);
	    }
	    return null;
	}
    }

    public static void main(String[] args) {
        Application.launch(ActionExample4.class, args);
    }
}