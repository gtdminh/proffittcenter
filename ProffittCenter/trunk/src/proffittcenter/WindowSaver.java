package proffittcenter;

import java.io.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;

public class WindowSaver implements AWTEventListener {

    private static WindowSaver saver;
    private static boolean noSave = false;
    private HashMap framemap;
    private static File defaultDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory();
    private static final String fileName =  defaultDirectory.getAbsolutePath() + "\\ProffittCenterReports\\"+"configuration.props";

    private WindowSaver() {
        framemap = new HashMap();
    }

    public static WindowSaver getInstance() {
        if (saver == null) {
            saver = new WindowSaver();
        }
        return saver;
    }

    public void eventDispatched(AWTEvent evt) {
        if (SettingsTabbed.isResetWindows()) {
            return;
        }
        try {
            if (evt.getID() == WindowEvent.WINDOW_OPENED) {
                ComponentEvent cev = (ComponentEvent) evt;
                if(evt.toString().contains("dialog")){
                    return;
                }
                if (cev.getComponent() instanceof Window) {
                    p("event: " + evt);
                    Window window = (Window) cev.getComponent();
                    loadSettings(window);
                }
            }
        } catch (Exception ex) {
            p(ex.toString());
        }
    }

    public static void loadSettings(Window window) throws IOException {
        Properties property = new Properties();
        // if this file does not already exist, create an empty one
        try {
            FileInputStream fis = new FileInputStream(fileName);
            property.load(fis);
            fis.close();
        } catch (FileNotFoundException fnfe) {
            FileOutputStream fos = new FileOutputStream(fileName);
            property.store(new FileOutputStream(fileName),
                    "Window settings");
            fos.close();
        }
        String name = window.getName();
        int x = getInt(property, name + ".x", -1);
        int y = getInt(property, name + ".y", -1);
        int w = getInt(property, name + ".w", -1);
        int h = getInt(property, name + ".h", -1);
        if (w == -1 || h == -1 || x == -1 || y == -1) {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            w = window.getWidth();
            h = window.getHeight();
            x = (dim.width- w)/2;
            y = (dim.height - h)/2;
            window.setLocation(x, y);
            window.setSize(new Dimension(w, h));//puts new screen in centre
        } else {
            window.setLocation(x, y);
            window.setSize(new Dimension(w, h));
        }
        saver.framemap.put(name, window);
        window.validate();
//        new File("configuration.props").delete();
    }

    public static void reset() {
        File localFile = new File(fileName);        
        boolean b = localFile.delete();
    }

    public static int getInt(Properties props, String name, int value) {
        String v = props.getProperty(name);
        if (v == null) {
            return value;
        }
        return Integer.parseInt(v);
    }

    public static void saveSettings() throws IOException {
        Properties settings = new Properties();
        try {
            settings.load(new FileInputStream(fileName));
        } catch (FileNotFoundException fnfe) {
            // quietly ignore and overwrite anyways
        }
        Iterator it = saver.framemap.keySet().iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            Window frame = (Window) saver.framemap.get(name);
            settings.setProperty(name + ".x", "" + frame.getX());
            settings.setProperty(name + ".y", "" + frame.getY());
            settings.setProperty(name + ".w", "" + frame.getWidth());
            settings.setProperty(name + ".h", "" + frame.getHeight());
        }
        settings.store(new FileOutputStream(fileName), null);
    }

    public static void p(String str) {
//        System.out.println(str);
    }

    public static void main(String[] args) throws Exception {
        Toolkit tk = Toolkit.getDefaultToolkit();
        tk.addAWTEventListener(WindowSaver.getInstance(), AWTEvent.WINDOW_EVENT_MASK);

        final JFrame frame = new JFrame("Hack X");
        frame.setName("WSTes.main");
        frame.getContentPane().add(new JButton("a button"));
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("File");
        menu.add(new AbstractAction("Quit") {

            public void actionPerformed(ActionEvent evt) {
                try {
                    WindowSaver.saveSettings();
                    System.exit(0);
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });
        mb.add(menu);
        frame.setJMenuBar(mb);
        frame.pack();
        frame.setVisible(true);
    }
}
