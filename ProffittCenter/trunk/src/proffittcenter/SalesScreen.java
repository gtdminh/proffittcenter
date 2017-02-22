
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * NewJFrame.java
 *
 * Created on 30-Jul-2010, 09:24:07
 */
package proffittcenter;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.help.CSH;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Dave
 */
public class SalesScreen extends javax.swing.JFrame {

    Preferences root = Preferences.userNodeForPackage(getClass());
    ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SalesScreen");
    ResourceBundle bundle1 = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SalesScreen1");
    private int qty = 1; //the quantity
    public LineList aSale;//aSale holds the information of the current sale
    private final SaleTableModel model;
//    private boolean getDescribeIt() = false;//when true, next input is to add a product description
    private int sku;//the current SKU
    private final boolean priceIt = false;
    boolean receiptRequired = false;//set true when a receipt is requested
    private long customer = 10000l * SaleType.CUSTOMER.code();//Default customer
    private String data;//holds the data in data entry
    private String s;
    private String resultsDescription;
    private Integer resultsValue;
    static String sep = File.separator;
    private final boolean isReceived = false;
    private TableColumn column;
    private HelpSet hs;
    private HelpBroker hb;
    private URL hsURL;
    Icon infoIcon = UIManager.getIcon("OptionPane.informationIcon");
    JLabel label = new JLabel("On offer",
            infoIcon,
            SwingConstants.LEFT);
    private final TableCellRenderer renderer = new SalesRenderer();
    Vector tableHeader = new Vector();
    Vector<Object[]> rowData = new Vector<Object[]>();
    @SuppressWarnings("unchecked")
    boolean b0 = tableHeader.add("");
    @SuppressWarnings("unchecked")
    boolean b1 = tableHeader.add("");
    DefaultTableCellRenderer mtcr = null;
    int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT};
    Vector<Object[]> resultsData = new Vector<Object[]>();
    Object[] resultsLine = {resultsDescription, resultsValue};
    ResultsModel resultsModel;
    private int discount = 0;
    private int supplier;
    private final boolean priceEntry = false;
    private JasperPrint jasperPrint;
    private Long product;
    private int originalPrice;
    private int month;
    private Integer year;
    private final boolean resultsComplete = false;
    private final boolean block = false;
    private Integer department;
    private int fixedFloat;
    public int delivery;
    private final boolean newSaleStarted = true;
    private final boolean trackingAlready = false;
    private String top;
    private String bottom;
    private String rawData;
    private javax.swing.Timer autoTimer;
    private int delay;
    private Connection connection;
    private String fileLocation;
    private final CashUp cu = null;
    private Calendar theDate;
    private int target = 0;
    private String dateOut;
    private int taxID;
    private int selection;
    private int showConfirmDialog;
    private Integer tillID;
    private Socket sock;
    private final javax.swing.Timer repeatTimer;
    private String gg;
    private String fileString;
    public boolean nextShift;
    private String defaultDirectory = "";
    private final String thisDirectory;
    private boolean noIncrement = false;
    private boolean sender;
    private int saleCount;
//    private SalesScreenFunctions salesScreenFunctions;
    private final Color normalColour;
    private java.sql.Date from;
    private java.sql.Date to;
//    private Log log = LogFactory.getLog(SalesScreen.class);
    private static final Logger log = Logger.getLogger(SalesScreen.class.getName());
    private java.sql.Date aDate;
    private Thread reportThread;

    /**
     * Creates a new sales screen
     *
     * @param aModel
     * @param asale
     */
    public SalesScreen(SaleTableModel aModel, LineList asale) {

        /**
         * Constructor
         */
        initComponents();
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Salesscreen", Main.mainHelpSet);
        // listen to ActionEvents from the helpItem
        if (Main.csh != null) {
            contentsMenuItem.addActionListener(Main.csh);
        }
        aSale = asale;
        Main.salesScreenFunctions.setaSale(aSale);
        model = aModel;
        Main.salesScreenFunctions.setModel(aModel);
        saleTable.setModel(model);
        saleTable.setDefaultRenderer(Integer.class, renderer);
        saleTable.setDefaultRenderer(String.class, renderer);
        column = saleTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(50); //quantity column
        column.setMinWidth(50);
        column = saleTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(600);
        column = saleTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(100); //quantity column
        column.setMinWidth(100);
        column = saleTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(100);
        TableColumnModel tc = saleTable.getTableHeader().getColumnModel();
        tc.getColumn(0).setHeaderRenderer(new SaleTableHeaderRenderer());
        tc.getColumn(1).setHeaderRenderer(new SaleTableHeaderRenderer());
        tc.getColumn(2).setHeaderRenderer(new SaleTableHeaderRenderer());
        tc.getColumn(3).setHeaderRenderer(new SaleTableHeaderRenderer());
        dataEntry.requestFocus();
        jTabbedPane1.setTitleAt(0, "2" + Main.salesScreenFunctions.getItemsSold());
        Main.salesScreenFunctions.setSaleComplete(false);
        dataEntry.setText("###########");//the default operator is 10000370000
        Main.salesScreenFunctions.setOperatorData("10000370000");
        dataEntry.setSelectionStart(0);
        dataEntry.setSelectionEnd(11);
        thisDirectory = new File(".").getAbsolutePath();
        if (thisDirectory.equalsIgnoreCase("E:\\Dave\\Documents\\NetBeansProjects\\ProffittCenter\\.")) {
            //running in NetBeans
            defaultDirectory = "E:\\Dave\\Documents\\NetBeansProjects\\ProffittCenter\\";
        } else {
            defaultDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory().getAbsolutePath() + "\\";
        }
        model.clear();
        aSale.setSelection(-1);
        setBounds(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        Object[] resultsLine0 = {"", ""};
        resultsData.add(resultsLine0);
        Object[] resultsLine1 = {bundle1.getString("Total"), Main.salesScreenFunctions.getTotal()};
        resultsData.add(resultsLine1);
        Object[] resultsLine2 = {bundle1.getString("Cash"), Main.salesScreenFunctions.getCash()};
        resultsData.add(resultsLine2);
        Object[] resultsLine3 = {bundle1.getString("Debit"), Main.salesScreenFunctions.getDebit()};
        resultsData.add(resultsLine3);
        Object[] resultsLine4 = {bundle1.getString("Cheque"), Main.salesScreenFunctions.getCoupon()};
        resultsData.add(resultsLine4);
        Object[] resultsLine5 = {bundle1.getString("Coupon"), Main.salesScreenFunctions.getCoupon()};
        resultsData.add(resultsLine5);
        Object[] resultsLine6 = {bundle1.getString("To_pay"), Main.salesScreenFunctions.getToPay()};
        resultsData.add(resultsLine6);
        Object[] resultsLine7 = {bundle1.getString("Savings"), Main.salesScreenFunctions.getSavings()};
        resultsData.add(resultsLine7);
        Object[] resultsLine8 = {bundle1.getString("Tax"), Main.salesScreenFunctions.getTax1()};
        resultsData.add(resultsLine8);
        Object[] resultsLine9 = {bundle1.getString("Cheque"), Main.salesScreenFunctions.getCheque()};
        resultsData.add(resultsLine9);
        Object[] resultsLine10 = {bundle1.getString("CustomerDiscount"), Main.salesScreenFunctions.getDiscountTotal()};
        resultsData.add(resultsLine10);
        Object[] resultsLine11 = {bundle1.getString("Status"), Main.salesScreenFunctions.getStatus()};
        resultsData.add(resultsLine11);
        Object[] resultsLine12 = {bundle1.getString("Change"), Main.salesScreenFunctions.getChange()};
        resultsData.add(resultsLine12);
        //get rid of the splash screen now

        deliveriesMenuItem.setMnemonic('D');
        ActionListener al = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (dataEntry.hasFocus()) {
                    lock();
                    Main.pole.execute(Main.customerMessages.getString("AutoLock1"), Main.customerMessages.getString("AutoLock2"));
                } else {
                    autoTimer.start();
                }
            }
        };
        ActionListener repeatTimerListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (OrderGenerate.done) {
                    greyOrdersMenu(true);
                    OrderGenerate.done = false;
                }
                if (ReportRunner.done) {
                    greyReportsMenu(true);
                    ReportRunner.done = false;
//                    Audio.play("Tada");
                }
            }
        };
        repeatTimer = new javax.swing.Timer(1000, repeatTimerListener);
        repeatTimer.start();
        delay = Main.shop.getAutoclearMinutes();//modify to suit  3 minutes
        delay *= 60000;
        if (delay > 0) {
            autoTimer = new javax.swing.Timer(delay, al);
            autoTimer.setRepeats(false);
            if (delay > 0) {
                autoTimer.start();
            }
        }
        resultsModel = new ResultsModel();
        resultsTable.setModel(resultsModel);
        Color colour = getBackground();
        resultsTable.setBackground(colour);
        resultsTable.setGridColor(colour);
        mtcr = new ResultsRenderer();
        resultsTable.setDefaultRenderer(Object.class, mtcr);
        resultsTable.setDefaultRenderer(String.class, mtcr);
        normalColour = dataEntry.getBackground();
        saleTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
//        PropertyConfigurator.configure("lib/log4j.properties");
    }

    private void aPressed(String data, KeyEvent evt) {
        if (Main.operator.getIntAuthority() <= 4 && Main.operator.getIntAuthority() >= 0 && !Main.salesScreenFunctions.getDescribeIt() && !Main.salesScreenFunctions.getPriceIt() && !Main.salesScreenFunctions.getPriceEntry() && data.endsWith(SaleType.NORMAL.codeString().substring(0, SaleType.NORMAL.codeString().length() - 1) + "a")) {
            dataEntry.setText(SaleType.RECEIVEDONACCOUNT.codeString());
        }
    }

    private boolean alphaPressed(String data, KeyEvent evt) {
        if (Main.operator.getIntAuthority() <= 4 && Main.operator.getIntAuthority() >= 0 && !Main.salesScreenFunctions.isDescribeIt() && !Main.salesScreenFunctions.isPriceIt() && !Main.salesScreenFunctions.isWeighIt() && !Main.salesScreenFunctions.isPriceEntry()
                && !Main.salesScreenFunctions.isReceived && !trackingAlready && data.length() > 2) {
            if (Main.salesScreenFunctions.isSaleComplete()) {
                Main.salesScreenFunctions.clearSale();
            }
            if (Main.alphaLookup.isBarcode(data) || data.charAt(data.length() - 1) == '/') {
                return false;
            }
            if (Main.alphaLookup.isFound(data)) {
                setS(Main.alphaLookup.returnDataIs());
            } else {
                dataEntry.setText("");
                Audio.play("Ring");
                return false;
            }
            //noIncrement=true;
            qty = 1;
            dataEntry.setText("");
            if (s.length() >= 7) {
                if (Main.shop.getTillId() == 0) {
                    Audio.play("Ring");
                    JOptionPane.showMessageDialog(this, bundle1.getString("NoSale"));
                    dataEntry.requestFocus();
                    dataEntry.setText("");
                    setS("");
                    return false;
                }
                if (Main.salesScreenFunctions.product(s)) {
                    dataEntry.requestFocus();
                    dataEntry.setText("");
                    return true;
                } else {
                    dataEntry.setText(s);
                    return false;
                }
            }
        }
        return false;
    }

    public void displayDate() {
        dateLabel.setText(dateOut);
    }

    /**
     * Backup up the database if this is the server
     *
     * @throws HeadlessException
     */
    public void backupAndClose() throws HeadlessException {
        String sd = bundle1.getString("BackingUp");
        dataEntry.requestFocus();
        dataEntry.setText(sd);
        Audio.play("ByeBye");
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            WindowSaver.saveSettings();
        } catch (IOException ex) {
            Logger.getLogger(SalesScreen.class.getName()).log(Level.SEVERE, null, ex);
        }
        fileString = Main.server.dropboxDirectoryAsString;
        if (Main.server.serverName.equalsIgnoreCase("localhost")) {
            if (fileString.isEmpty() || Main.shop.companyName.isEmpty()) {
                Main.pole.execute(Main.customerMessages.getString("Sorry"), Main.customerMessages.getString("OutOfService"));
                System.exit(0);
            }
            if (Main.shop.closeComputer) {
                Object[] options = {bundle1.getString("YesPlease"),
                    bundle1.getString("JustBackup"),
                    bundle1.getString("NoBackupJustClose")};
                switch (JOptionPane.showOptionDialog(this,
                        bundle1.getString("BackUpAndCloseComputer"), "Which?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options,
                        options[0])) {
                    case JOptionPane.CANCEL_OPTION:
                    case JOptionPane.CLOSED_OPTION:
                        break;
                    case JOptionPane.NO_OPTION:
                        backupSave();
                        break;
                    case JOptionPane.OK_OPTION:
                        backupSave();
                        String shutdownCmd = "shutdown -s ";
                        try {
                            Process child = Runtime.getRuntime().exec(shutdownCmd);
                        } catch (IOException ex) {
                            Logger.getLogger(SalesScreen.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
            } else {
                Object[] options = {bundle1.getString("YesPlease"),
                    bundle1.getString("JustClose")};
                switch (JOptionPane.showOptionDialog(this, bundle1.getString("BackUpAndCloseProgram"), "Which?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
                    case JOptionPane.CANCEL_OPTION:
                    case JOptionPane.CLOSED_OPTION:
                    case JOptionPane.NO_OPTION:
                        break;
                    case JOptionPane.OK_OPTION:
                        backupSave();
                }
            }
        }
        Main.pole.execute(Main.customerMessages.getString("Sorry"), Main.customerMessages.getString("OutOfService"));
        System.exit(0);
    }


public static FileReader getAppData() {
        return null;
    }

    /**
     * Save the database and email to specified address
     *
     * @throws HeadlessException
     */
    private void backupSave() throws HeadlessException {
        String secondLine;
        Date today = Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("dd");
        String d = df.format(today);
        File rootFile = new File(".").getAbsoluteFile();
        File localRoot = rootFile.getParentFile();
        while (localRoot.getParentFile() != null) {
            localRoot = localRoot.getParentFile();
        }
        String drive = "C:\\"; //localRoot.toString();
        fileString = Main.server.dropboxDirectoryAsString;

        if (fileString.isEmpty() || Main.shop.companyName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You need to fill in shop name, \nor set Dropbox address in Settings\n or null file>Shop");
            return;
        } else {
            fileString = fileString + "\\Dropbox" + "\\"
                    + Main.shop.companyName + "Backups\\";
            new File(fileString).mkdirs();//creates directory only if needed
            fileString = "\"" + fileString //adds quotes around name for mysqldump
                    + Main.shop.companyName
                    + d + ".sql\"";
        }
        try {
            String s1 = "c:\\" + "xampp\\mysql\\bin\\mysqldump.exe "
                    + " --no-defaults "
                    + " --databases " + Main.server.database
                    + " --host=" + Main.server.serverName
                    + " --user=" + Main.server.userName
                    + " --password=" + Main.server.password
                    + " --add-drop-database "
                    + " --routines "
                    + " --triggers "
                    + " -r " + fileString;
//            String s2= "c:\\" + "xampp\\mysql\\bin\\mysqldump.exe "
//                    + " --no-defaults "
//                    + " --databases " + Main.server.database
//                    + " --host=" + Main.server.serverName
//                    + " --user=" + Main.server.userName
//                    + " --password=" + Main.server.password
//                    + " --add-drop-database "
//                    + " --routines "
//                    + " --triggers "
//                     + " C:\\Users\\Anna Proffitt\\Documents.sql";
            int waitFor = Runtime.getRuntime().exec(s1).waitFor();
//            int waitFor = Runtime.getRuntime().exec(s1).waitFor();
            

if (waitFor != 0) {
                Logger.getLogger(SalesScreen.class
.getName()).log(Level.SEVERE, null, "mysqldump" + waitFor);
            

}
        } catch (InterruptedException ex) {
            Logger.getLogger(SalesScreen.class
.getName()).log(Level.SEVERE, null, ex);
        

} catch (IOException ex) {
            Logger.getLogger(SalesScreen.class.getName()).log(Level.SEVERE, "saveBackup -IOException: ", ex);
        }
    }

    public void restartTimer() {
        if (delay != 0) {
            autoTimer.start();
        }
    }

    private void cPressed(String data, KeyEvent evt) {
        if (Main.operator.getIntAuthority() <= 4 && Main.operator.getIntAuthority() >= 0 && !Main.salesScreenFunctions.getDescribeIt() && !Main.salesScreenFunctions.getPriceIt() && !priceEntry && data.endsWith(SaleType.NORMAL.codeString().substring(0,
                SaleType.NORMAL.codeString().length() - 1) + "c")) {
            dataEntry.setText(SaleType.CHARGED.codeString());
        }
    }

    /**
     * checks to see if Escape has been pressed in dataEntry then sets back to
     * initial state
     *
     * @param data, the contents of dataEntry
     * @param evt the key event
     */
    private void escapePressed(String data, KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {//escape
            setCursor(Cursor.getDefaultCursor());
            //if inputting description etc return to normal
            if (Main.salesScreenFunctions.isPriceEntry() || Main.salesScreenFunctions.isWeighIt() || Main.salesScreenFunctions.isAgeCheck() || Main.salesScreenFunctions.isTrackIt()) {
                Main.salesScreenFunctions.setWeighIt(false);
                Main.salesScreenFunctions.setPriceEntry(false);
                Main.salesScreenFunctions.setAgeCheck(false);
                Main.salesScreenFunctions.setTrackIt(false);
                Main.salesScreenFunctions.delete();
            }
            Main.salesScreenFunctions.setDescribeIt(false);
            Main.salesScreenFunctions.setPriceIt(false);
            Main.salesScreenFunctions.setWeighIt(false);
            Main.salesScreenFunctions.setPriceEntry(false);
            dataLabel.setText(bundle.getString("SalesScreen.dataLabel.text"));
            dataEntry.setBackground(normalColour);
            dataEntry.setText("");
        }
    }

    private void mPressed(String data, KeyEvent evt) {
        if (Main.operator.getIntAuthority() <= 4 && Main.operator.getIntAuthority() >= 0 && !Main.salesScreenFunctions.isDescribeIt() && !Main.salesScreenFunctions.isPriceIt() && data.endsWith(SaleType.NORMAL.codeString().substring(0,
                SaleType.NORMAL.codeString().length() - 1) + "m")) {
            dataEntry.setText(SaleType.CUSTOMER.codeString());
        }
    }

    private void oPressed(String data, KeyEvent evt) {
        if (Main.operator.getIntAuthority() <= 4 && Main.operator.getIntAuthority() >= 0 && !Main.salesScreenFunctions.isDescribeIt() && !Main.salesScreenFunctions.isPriceIt() && !Main.salesScreenFunctions.isPriceEntry() && data.endsWith(SaleType.NORMAL.codeString().substring(0,
                SaleType.NORMAL.codeString().length() - 1) + "o")) {
            dataEntry.setText(SaleType.OWNUSE.codeString());
        }
    }

    private void queryPressed(String data, KeyEvent evt) {
        if (Main.operator.getIntAuthority() <= 4 && Main.operator.getIntAuthority() >= 0 && !Main.salesScreenFunctions.isDescribeIt() && !Main.salesScreenFunctions.isPriceIt() && !Main.salesScreenFunctions.isPriceEntry() && data.endsWith("?")) {
            //put 100003 into data entry and leave
//            data = dataEntry.getText();
            String t = SaleType.NORMAL.codeString();
            t = t.substring(0, t.length() - 1);
            dataEntry.setText(t);
        }
    }

    private void rPressed(String data, KeyEvent evt) {
        if (Main.operator.getIntAuthority() <= 4 && Main.operator.getIntAuthority() >= 0 && !Main.salesScreenFunctions.isDescribeIt() && !Main.salesScreenFunctions.isPriceIt() && !Main.salesScreenFunctions.isPriceEntry() && data.endsWith(SaleType.NORMAL.codeString().substring(0,
                SaleType.NORMAL.codeString().length() - 1) + "r")) {
            dataEntry.setText(SaleType.RETURN.codeString());
        }
    }

    private void touchCharacter(String s) {
        int start = dataEntry.getSelectionStart();
        int end = dataEntry.getSelectionEnd();
        String dE = dataEntry.getText();
        dE = dE.substring(0, start) + s + dE.substring(end);
        dataEntry.setText(dE);
        dataEntry.requestFocus();
    }

    public static void scrollToVisible(JTable table, int rowIndex, int vColIndex) {
        if (!(table.getParent() instanceof JViewport)) {
            return;
        }
        JViewport viewport = (JViewport) table.getParent();
        Rectangle rect = table.getCellRect(rowIndex, vColIndex, true);
        Point pt = viewport.getViewPosition();
        rect.setLocation(rect.x - pt.x, rect.y - pt.y);
        viewport.scrollRectToVisible(rect);
    }

    private void wPressed(String data, KeyEvent evt) {
        if (Main.operator.getIntAuthority() <= 4 && Main.operator.getIntAuthority() >= 0 && !Main.salesScreenFunctions.isDescribeIt() && !Main.salesScreenFunctions.isPriceIt() && !Main.salesScreenFunctions.isPriceEntry() && data.endsWith(SaleType.NORMAL.codeString().substring(0,
                SaleType.NORMAL.codeString().length() - 1) + "w")) {
            dataEntry.setText(SaleType.WASTE.codeString());
        }
    }

    /**
     * @return the customer
     */
    public long getCustomer() {
        return customer;
    }

    /**
     * @param customer the customer to set
     */
    public void setCustomer(long customer) {
        this.customer = customer;
    }

    public void setTabCount() {
        jTabbedPane1.setTitleAt(0, "2 - Items: " + Main.salesScreenFunctions.getItemsSold());
    }

    /**
     * @return the discount
     */
    public int getDiscount() {
        return discount;
    }

    /**
     * @param discount the discount to set
     */
    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getFixedFloat() {
        return fixedFloat;
    }

    void setNoIncrement(boolean b) {
        noIncrement = b;
    }

    public void lock() {
        Main.salesScreenFunctions.setSelection(Main.salesScreenFunctions.getaSale().getSelection());
        if (Main.salesScreenFunctions.getaSale().getSelection() < 0) {
            //lock the screen
            Main.operator.setOperator(-1);//locked out yes
            jTabbedPane1.setTitleAt(0, bundle1.getString("Locked"));
            dataLabel.setText(bundle1.getString("LogIn"));
            dataEntry.setBackground(Color.yellow);
        } else {
            Audio.play("Ring");
        }
    }

    void displayAgncyString(String agencyString) {
        dateLabel.setText(agencyString);
    }

    public synchronized void greyOrdersMenu(boolean isEnabled) {
        if (isEnabled) {
            ordersMenu.setText(bundle.getString("SalesScreen.ordersMenu.text"));
        } else {
            ordersMenu.setText(bundle1.getString("busy"));
        }
        ordersMenu.setEnabled(isEnabled);
        ordersCreateMenuItem.setEnabled(isEnabled);
        Audio.play("Beep");
    }

    public synchronized void greyReportsMenu(boolean isEnabled) {
        if (isEnabled && ReportRunner.done) {
            reportMenuItem.setText(bundle.getString("SalesScreen.reportMenuItem.text"));
            reportMenuItem.setEnabled(true);
        } else {
            reportMenuItem.setText(bundle1.getString("busy"));
            reportMenuItem.setEnabled(false);
            Audio.play("TaDa");
        }
    }

    void setDataLabel(String string) {
        if (string.isEmpty()) {
            dataLabel.setText(bundle1.getString(("SalesScreen.dataLabel.text")));
            dataEntry.setBackground(normalColour);
        } else {
            dataLabel.setText(bundle1.getString((string)));
            dataEntry.setBackground(Color.yellow);
        }
    }

    public void openURL(String url) {
        String osName = System.getProperty("os.name");
        try {
            if (osName.startsWith("Windows")) {
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
            } else {
                String[] browsers = {"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape"};
                String browser = null;
                for (int count = 0; count < browsers.length && browser == null; count++) {
                    if (Runtime.getRuntime().exec(new String[]{"which", browsers[count]}).waitFor() == 0) {
                        browser = browsers[count];
                    }
                }
                Runtime.getRuntime().exec(new String[]{browser, url});
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error in opening browser" + ":\n" + e.getLocalizedMessage());
        } catch (InterruptedException e) {
            JOptionPane.showMessageDialog(null, "Error in opening browser" + ":\n" + e.getLocalizedMessage());
        }
    }

    private int newSale() {
        saleCount++;
        //add tab
        jTabbedPane1.addTab("" + saleCount, jScrollPane1);
        return saleCount;
    }

    public File getJasper(String jasper) {
        File fc;
        boolean exists = new File(getDefaultDirectory() + "OwnProffittCenterReports/" + jasper + ".jasper").exists();
        if (exists) {
            fc = new File(getDefaultDirectory() + "OwnProffittCenterReports/" + jasper + ".jasper");
        } else {
            fc = new File(getDefaultDirectory() + "ProffittCenterReports/" + jasper + ".jasper");
        }
        return fc;
    }

    /**
     * @param s the s to set
     */
    public void setS(String s) {
        this.s = s;
    }

    /**
     * @return the defaultDirectory
     */
    public String getDefaultDirectory() {
        return defaultDirectory; 

}

    public class SaleTableHeaderRenderer extends JLabel implements TableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        JLabel jl = (JLabel) this;
        jl.setText((String) value);
        if (column == 2 || column == 3) {
            jl.setHorizontalAlignment(RIGHT);
        } else if (column == 0) {
            jl.setHorizontalAlignment(CENTER);
        } else {
            jl.setHorizontalAlignment(LEFT);
        }
        Font plain = new Font("SansSerif", Font.BOLD, 16);
        jl.setFont(plain);//font appears bold without this
        //create one pixel space left and right
        Color colour = getBackground();
        Border border = BorderFactory.createMatteBorder(1, 1, 1, 2, colour);
        //create bottom and right border
        Border margin = BorderFactory.createMatteBorder(-1, -1, 1, 1, Color.BLACK);
        //now cobine the two
        jl.setBorder(new CompoundBorder(margin, border));
        return jl;
    }
}

private class SalesRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int column) {
        Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        Color colour;//used to store current background colour
        if (row == aSale.getSelection()) {
            colour = Color.yellow;
            //set selected row to yellow
            cell.setBackground(Color.yellow);
        } else if (row % 2 == 0) {
            colour = Color.white;
            //set all even rows white
            cell.setBackground(Color.white);
        } else {
            //set all odd rows to a darker shade
            colour = new Color(245, 245, 245);
            cell.setBackground(colour);
        }
        if (value instanceof Integer) {
            Integer amount = (Integer) value;
            if (amount < 0) {
                cell.setForeground(Color.red);
            } else {
                cell.setForeground(Color.black);
            }
        }
        JLabel jl = (JLabel) cell;
        Font plain = new Font("SansSerif", Font.PLAIN, 16);
        jl.setFont(plain);//font appears bold without this
        jl.setBorder(BorderFactory.createMatteBorder(
                -1, 3, -1, 1, colour));
        if (column == 2 || column == 3) {
            jl.setHorizontalAlignment(RIGHT);
        } else if (column == 0) {
            jl.setHorizontalAlignment(CENTER);
        } else {
            jl.setHorizontalAlignment(LEFT);
        }
        return cell;
    }
}

/**
 * display the sales screen
 */
void execute() {
        if (Main.splashFrame != null) {
            Main.splashFrame.dispose();
            Main.splashFrame = null;//in case we generate a second salesscreen
        }
//        Main.salesScreenFunctions= new SalesScreenFunctions();
        Main.salesScreenFunctions.setSaleComplete(true);
        Date today;
        Main.salesScreenFunctions.setSelection(-1);
        DateFormat dateFormatter;
        dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault());
        today = new Date();
        dateOut = dateFormatter.format(today);
        dateLabel.setText(dateOut);
        Toolkit tk = Toolkit.getDefaultToolkit();
        tk.addAWTEventListener(WindowSaver.getInstance(), AWTEvent.WINDOW_EVENT_MASK);
        makeKeyboardVisible();
        top = Main.customerMessages.getString("Sorry");
        bottom = Main.customerMessages.getString("OutOfService");
        Main.pole.execute(top, bottom);
        Main.salesScreenFunctions.setDataEntry(dataEntry);
        Main.salesScreenFunctions.setDataLabel(dataLabel);
        dataEntry.setBackground(normalColour);
        Main.salesScreenFunctions.setHidden(hidden);
        Main.salesScreenFunctions.setResultsTable(resultsTable);
        Main.salesScreenFunctions.setSalesScreen(this);
        Main.salesScreenFunctions.setjSaleTable(saleTable);
        Main.salesScreenFunctions.setjTabbedPane1(jTabbedPane1);
        tillID = Main.shop.getTillId();
        mirrorMenuItem.setEnabled(tillID == 0);
        dataLabel.setText(bundle1.getString("LogIn"));
        dataEntry.setBackground(Color.yellow);
        dataEntry.requestFocus();
        setVisible(true);
    }

    public void makeKeyboardVisible() {
        boolean touch = Main.hardware.touch;
        jButton0.setVisible(touch);
        jButton1.setVisible(touch);
        jButton2.setVisible(touch);
        jButton3.setVisible(touch);
        jButton4.setVisible(touch);
        jButton5.setVisible(touch);
        jButton6.setVisible(touch);
        jButton7.setVisible(touch);
        jButton8.setVisible(touch);
        jButton9.setVisible(touch);
        jButtonDelete.setVisible(touch);
        jButtonTimes.setVisible(touch);
        jButtonEnter.setVisible(touch);
        jButtonPerCent.setVisible(touch);
        jButtonAt.setVisible(touch);
        jButtonMinus.setVisible(touch);
    }

    private boolean deletePressed(String data, KeyEvent evt) {
        if ((evt.getKeyCode() == KeyEvent.VK_DELETE)) {
            Main.salesScreenFunctions.setShift(evt.getModifiers() == 1);//not correct - should not use constant here
            if (Main.salesScreenFunctions.isShift()) {
                Main.salesScreenFunctions.clearResult();
                Main.salesScreenFunctions.clearSale();
                Audio.play("Beep");
                Main.salesScreenFunctions.setSaleComplete(true);//needed for left arrow
            } else {
                Main.salesScreenFunctions.delete();
                if (Main.salesScreenFunctions.getaSale().getSelection() < 0) {
                    Main.salesScreenFunctions.setSaleComplete(true);
                }
            }
            evt.setKeyCode(0);
            dataEntry.requestFocus();
            dataEntry.setText("");
            dataLabel.setText(bundle1.getString("SalesScreen.dataLabel.text"));
            dataEntry.setBackground(normalColour);
            return true;
        }
        return false;
    }

    /**
     * Put data into the dataEntry textBox
     *
     * @param data the string data to be displayed
     */
    public void setDataEntry(String data) {
        dataEntry.setText(data);
    }

    /**
     * This method opens up the help viewer for specified help set and displays
     * the home ID of that help set
     */
    public void openHelp() {
        // Identify the location of the .hs file
        String pathToHS = "helpset/ProffittCenterPro.hs";
        //Create a URL for the location of the help set
        try {
            hsURL = getClass().getResource(pathToHS);
            hs = new HelpSet(null, hsURL);
        } catch (Exception ee) {
            // Print info to the console if there is an exception
            Main.logger.log(Level.SEVERE, "SalesScreen.openhelp ", "Exception: " + ee.getMessage());
            JOptionPane.showMessageDialog(null, "Exception: " + ee, "SalesScreen.OpenHelp", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Create a HelpBroker object for manipulating the help set
        hb = hs.createHelpBroker();
        //Display help set
        hb.setDisplayed(true);
        hb.enableHelpKey(getRootPane(), "salesscreen", hs);
    }

    private boolean printPressed(String data, KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_PRINTSCREEN) {//print
            Main.salesScreenFunctions.setShift(evt.getModifiers() == 1);//not correct - should not use constant here
            Audio.play("Beep");
            if (Main.salesScreenFunctions.isShift()) {
                if (Main.salesScreenFunctions.getTotal() != 0 && Main.salesScreenFunctions.isSaleComplete()) {
                    Main.salesScreenFunctions.setInvoiceRequired(true);
                    Main.invoice.print(Main.salesScreenFunctions.getSale(), getDiscount(), false);
                } else {
                    Main.salesScreenFunctions.setInvoiceRequired(true);
                }
            } else {
                if (Main.salesScreenFunctions.getTotal() != 0 && Main.salesScreenFunctions.isSaleComplete()) {
                    Main.receiptPrinter.printReceipt(Main.salesScreenFunctions.getSale());
                } else {
                    Main.salesScreenFunctions.setReceiptRequired(true);
                }
            }
            return true;
        }
        return false;
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButtonPerCent = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton0 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButtonAt = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        resultsTable = new javax.swing.JTable();
        jButtonEnter = new javax.swing.JButton();
        jButtonTimes = new javax.swing.JButton();
        jButtonMinus = new javax.swing.JButton();
        jButtonDelete = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        panel = new javax.swing.JPanel();
        dateLabel = new javax.swing.JLabel();
        dataLabel = new javax.swing.JLabel();
        dataEntry = new javax.swing.JTextField();
        hidden = new javax.swing.JTextField();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        saleTable = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jFileMenu = new javax.swing.JMenu();
        newSaleMenuItem = new javax.swing.JMenuItem();
        importMenuItem = new javax.swing.JMenuItem();
        testMenuItem = new javax.swing.JMenuItem();
        settingsMenuItem = new javax.swing.JMenuItem();
        messagesMenuItem = new javax.swing.JMenuItem();
        printCodesMenuItem = new javax.swing.JMenuItem();
        resetWindowsItem = new javax.swing.JMenuItem();
        RunSqlMenuItem = new javax.swing.JMenuItem();
        mirrorMenuItem = new javax.swing.JMenuItem();
        jPaymentMenu = new javax.swing.JMenu();
        jCash = new javax.swing.JMenuItem();
        jCheque = new javax.swing.JMenuItem();
        jCoupon = new javax.swing.JMenuItem();
        jDebit = new javax.swing.JMenuItem();
        jPaidIn = new javax.swing.JMenuItem();
        jPaidOut = new javax.swing.JMenuItem();
        jReceipt = new javax.swing.JMenuItem();
        jCashInDrawer = new javax.swing.JMenuItem();
        noSaleMenuItem = new javax.swing.JMenuItem();
        jCharge = new javax.swing.JMenuItem();
        ownUseMenuItem = new javax.swing.JMenuItem();
        jReceivedOnAccount = new javax.swing.JMenuItem();
        creditCustomerMenuItem = new javax.swing.JMenuItem();
        jRefund = new javax.swing.JMenuItem();
        jReturnableWaste = new javax.swing.JMenuItem();
        jWaste = new javax.swing.JMenuItem();
        lockMenuItem = new javax.swing.JMenuItem();
        layawayMenuItem = new javax.swing.JMenuItem();
        jTablesMenu = new javax.swing.JMenu();
        deliveriesMenuItem = new javax.swing.JMenuItem();
        deliveryAddressesItem = new javax.swing.JMenuItem();
        jDepartments = new javax.swing.JMenuItem();
        jEncodedProducts = new javax.swing.JMenuItem();
        jHotkeys = new javax.swing.JMenuItem();
        jOffers = new javax.swing.JMenuItem();
        operators1 = new javax.swing.JMenuItem();
        orderMenuItem = new javax.swing.JMenuItem();
        packSuppliers1 = new javax.swing.JMenuItem();
        packs1 = new javax.swing.JMenuItem();
        paidOuts1 = new javax.swing.JMenuItem();
        product1 = new javax.swing.JMenuItem();
        productPerformancesMenuItem = new javax.swing.JMenuItem();
        products1 = new javax.swing.JMenuItem();
        reconciledCashups = new javax.swing.JMenuItem();
        sale1 = new javax.swing.JMenuItem();
        sales = new javax.swing.JMenuItem();
        skus1 = new javax.swing.JMenuItem();
        suppliers1 = new javax.swing.JMenuItem();
        jTaxes = new javax.swing.JMenuItem();
        tracksMenu = new javax.swing.JMenuItem();
        jOffersMenu = new javax.swing.JMenu();
        createMenuItem2 = new javax.swing.JMenuItem();
        showMenuItem1 = new javax.swing.JMenuItem();
        ordersMenu = new javax.swing.JMenu();
        ordersCreateMenuItem = new javax.swing.JMenuItem();
        ordersViewMenuItem = new javax.swing.JMenuItem();
        jCustomersMenu = new javax.swing.JMenu();
        jByName = new javax.swing.JMenuItem();
        jByNumber = new javax.swing.JMenuItem();
        jBalance = new javax.swing.JMenuItem();
        jBalances = new javax.swing.JMenuItem();
        jNewCustomer = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        ownUseByName = new javax.swing.JMenuItem();
        ownUseByNumber = new javax.swing.JMenuItem();
        ownUseBalance = new javax.swing.JMenuItem();
        ownUseBalances = new javax.swing.JMenuItem();
        ownUseNew = new javax.swing.JMenuItem();
        jStockMenu = new javax.swing.JMenu();
        jDelivery = new javax.swing.JMenuItem();
        jStocktake = new javax.swing.JMenu();
        jAll = new javax.swing.JMenu();
        jAllDefault = new javax.swing.JMenuItem();
        jAllAll = new javax.swing.JMenuItem();
        jAllLessThan10 = new javax.swing.JMenu();
        jDefault10 = new javax.swing.JMenuItem();
        jAll10 = new javax.swing.JMenuItem();
        jNegatives = new javax.swing.JMenu();
        jNegativeDefault = new javax.swing.JMenuItem();
        jNegativeAll = new javax.swing.JMenuItem();
        jStop = new javax.swing.JMenu();
        jStopDefault = new javax.swing.JMenuItem();
        jStopAll = new javax.swing.JMenuItem();
        jDepartment = new javax.swing.JMenu();
        jDepartmentDefault = new javax.swing.JMenuItem();
        jDepartmentAll = new javax.swing.JMenuItem();
        jSupplier = new javax.swing.JMenu();
        jSupplierDefault = new javax.swing.JMenuItem();
        jSupplierAll = new javax.swing.JMenuItem();
        jProduct = new javax.swing.JMenuItem();
        jStockByValue = new javax.swing.JMenu();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem13 = new javax.swing.JMenuItem();
        jMenuItem12 = new javax.swing.JMenuItem();
        jMenuItem14 = new javax.swing.JMenuItem();
        jStartingStock = new javax.swing.JMenuItem();
        addToStockMenuItem = new javax.swing.JMenuItem();
        jTempStock = new javax.swing.JMenuItem();
        linkMenuItem = new javax.swing.JMenuItem();
        jCharts = new javax.swing.JMenu();
        jSalesByDepartment = new javax.swing.JMenuItem();
        jSales = new javax.swing.JMenuItem();
        salesTargetMenuItem = new javax.swing.JMenuItem();
        workloadMenuItem = new javax.swing.JMenuItem();
        reportMenuItem = new javax.swing.JMenu();
        takings = new javax.swing.JMenuItem();
        cashUp = new javax.swing.JMenuItem();
        jReconcile = new javax.swing.JMenuItem();
        agencyReconcilliationMenuItem = new javax.swing.JMenuItem();
        salesByMenu = new javax.swing.JMenu();
        jAgency = new javax.swing.JMenuItem();
        jDepartment1 = new javax.swing.JMenuItem();
        discounted = new javax.swing.JMenuItem();
        jOperator = new javax.swing.JMenuItem();
        pricedOver = new javax.swing.JMenuItem();
        jQuantity = new javax.swing.JMenuItem();
        jTax = new javax.swing.JMenuItem();
        jValue = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jWastes = new javax.swing.JMenuItem();
        jReturns = new javax.swing.JMenuItem();
        jLosses = new javax.swing.JMenuItem();
        jCharges = new javax.swing.JMenuItem();
        jReceived = new javax.swing.JMenuItem();
        jOwnUse = new javax.swing.JMenuItem();
        jDepartmentMenu = new javax.swing.JMenu();
        jDLosses = new javax.swing.JMenuItem();
        jReturnsByDepartment = new javax.swing.JMenuItem();
        jDWastes = new javax.swing.JMenuItem();
        jLosses2 = new javax.swing.JMenuItem();
        jReturns1 = new javax.swing.JMenuItem();
        jWastes1 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        agencyReport = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        catalogueItem = new javax.swing.JMenuItem();
        dailyDepartmentProductSales = new javax.swing.JMenuItem();
        dailyDepartmentSales = new javax.swing.JMenuItem();
        jMinimumStock = new javax.swing.JMenuItem();
        customerSalesReportMenuItem = new javax.swing.JMenuItem();
        lastSaleReport = new javax.swing.JMenuItem();
        marginsByDepartmentMenuItem = new javax.swing.JMenuItem();
        paidOutsMenuItem = new javax.swing.JMenuItem();
        purchaseHistory = new javax.swing.JMenuItem();
        monthlySalesByDepartmentMenuItem = new javax.swing.JMenuItem();
        monthlySalesByProduct = new javax.swing.JMenuItem();
        refundsMenuItem = new javax.swing.JMenuItem();
        salesByDepartments = new javax.swing.JMenuItem();
        stoppedProductsMenuItem = new javax.swing.JMenuItem();
        supplierStockListMenuItem = new javax.swing.JMenuItem();
        top100MenuItem = new javax.swing.JMenuItem();
        top100ByDepartmentMenuItem = new javax.swing.JMenuItem();
        top100BySupplier = new javax.swing.JMenuItem();
        weeklySalesByDepartmentMenuItem = new javax.swing.JMenuItem();
        weeklySalesByProduct = new javax.swing.JMenuItem();
        zeroPricedSalesMenuItem = new javax.swing.JMenuItem();
        jHelpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        dropboxMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SalesScreen"); // NOI18N
        setTitle(bundle.getString("SalesScreen.title")); // NOI18N
        setFont(new java.awt.Font("Andale Mono IPA", 0, 8)); // NOI18N
        setName(bundle.getString("SalesScreen.name")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setName("jPanel1"); // NOI18N

        jButtonPerCent.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButtonPerCent.setText(bundle.getString("SalesScreen.jButtonPerCent.text")); // NOI18N
        jButtonPerCent.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPerCent.setName("jButtonPerCent"); // NOI18N
        jButtonPerCent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPerCentActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButton1.setText(bundle.getString("SalesScreen.jButton1.text")); // NOI18N
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton0.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButton0.setText(bundle.getString("SalesScreen.jButton0.text")); // NOI18N
        jButton0.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton0.setName("jButton0"); // NOI18N
        jButton0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton0ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButton5.setText(bundle.getString("SalesScreen.jButton5.text")); // NOI18N
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setName("jButton5"); // NOI18N
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButton6.setText(bundle.getString("SalesScreen.jButton6.text")); // NOI18N
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setName("jButton6"); // NOI18N
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButtonAt.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButtonAt.setText(bundle.getString("SalesScreen.jButtonAt.text")); // NOI18N
        jButtonAt.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonAt.setName("jButtonAt"); // NOI18N
        jButtonAt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAtActionPerformed(evt);
            }
        });

        jButton7.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButton7.setText(bundle.getString("SalesScreen.jButton7.text")); // NOI18N
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setName("jButton7"); // NOI18N
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jScrollPane2.setName("jScrollPane2"); // NOI18N

        resultsTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        resultsTable.setFont(new java.awt.Font("Arial", 0, 20)); // NOI18N
        resultsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        resultsTable.setFocusable(false);
        resultsTable.setName("resultsTable"); // NOI18N
        resultsTable.setRowHeight(35);
        resultsTable.setRowMargin(0);
        resultsTable.setRowSelectionAllowed(false);
        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                resultsTableMouseClicked(evt);
            }
        });
        resultsTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                resultsTableFocusGained(evt);
            }
        });
        jScrollPane2.setViewportView(resultsTable);

        jButtonEnter.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jButtonEnter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Enter.png"))); // NOI18N
        jButtonEnter.setToolTipText(bundle.getString("enter")); // NOI18N
        jButtonEnter.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonEnter.setName("jButtonEnter"); // NOI18N
        jButtonEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnterActionPerformed(evt);
            }
        });

        jButtonTimes.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButtonTimes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Asterix.png"))); // NOI18N
        jButtonTimes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTimes.setName("jButtonTimes"); // NOI18N
        jButtonTimes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTimesActionPerformed(evt);
            }
        });

        jButtonMinus.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jButtonMinus.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Minus.png"))); // NOI18N
        jButtonMinus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonMinus.setName("jButtonMinus"); // NOI18N
        jButtonMinus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonMinusActionPerformed(evt);
            }
        });

        jButtonDelete.setFont(new java.awt.Font("Tahoma", 0, 8)); // NOI18N
        jButtonDelete.setText(bundle.getString("SalesScreen.jButtonDelete.text")); // NOI18N
        jButtonDelete.setToolTipText(bundle.getString("tab")); // NOI18N
        jButtonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDelete.setName("jButtonDelete"); // NOI18N
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jButton9.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButton9.setText(bundle.getString("SalesScreen.jButton9.text")); // NOI18N
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setName("jButton9"); // NOI18N
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton4.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButton4.setText(bundle.getString("SalesScreen.jButton4.text")); // NOI18N
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setName("jButton4"); // NOI18N
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButton2.setText(bundle.getString("SalesScreen.jButton2.text")); // NOI18N
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButton3.setText(bundle.getString("SalesScreen.jButton3.text")); // NOI18N
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setName("jButton3"); // NOI18N
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton8.setFont(new java.awt.Font("DejaVu Sans Mono", 0, 18)); // NOI18N
        jButton8.setText(bundle.getString("SalesScreen.jButton8.text")); // NOI18N
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setName("jButton8"); // NOI18N
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton0, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButtonMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jButtonEnter, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonAt, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonPerCent, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButtonTimes, javax.swing.GroupLayout.Alignment.LEADING))
                        .addContainerGap(59, Short.MAX_VALUE))))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton0, jButton1, jButton2, jButton3, jButton4, jButton5, jButton6, jButton7, jButton8, jButton9, jButtonAt, jButtonDelete, jButtonEnter, jButtonMinus, jButtonPerCent, jButtonTimes});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 463, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButtonPerCent, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonTimes, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonAt, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                        .addComponent(jButton0, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButtonMinus, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonEnter, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton0, jButton1, jButton2, jButton3, jButton4, jButton5, jButton6, jButton7, jButton8, jButton9, jButtonAt, jButtonDelete, jButtonEnter, jButtonMinus, jButtonPerCent, jButtonTimes});

        panel.setFocusable(false);
        panel.setName("panel"); // NOI18N
        panel.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                panelFocusGained(evt);
            }
        });
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panelMouseClicked(evt);
            }
        });

        dateLabel.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        dateLabel.setText("A");
        dateLabel.setFocusable(false);
        dateLabel.setName("dateLabel"); // NOI18N

        dataLabel.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        dataLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        dataLabel.setText(bundle.getString("SalesScreen.dataLabel.text")); // NOI18N
        dataLabel.setFocusable(false);
        dataLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        dataLabel.setName("dataLabel"); // NOI18N

        dataEntry.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        dataEntry.setText("null");
        dataEntry.setName("dataEntry"); // NOI18N
        dataEntry.setNextFocusableComponent(hidden);
        dataEntry.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                dataEntryFocusGained(evt);
            }
        });
        dataEntry.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dataEntryKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dataEntryKeyReleased(evt);
            }
        });

        hidden.setEditable(false);
        hidden.setBorder(null);
        hidden.setMargin(new java.awt.Insets(2, 0, 2, 0));
        hidden.setName("hidden"); // NOI18N
        hidden.setNextFocusableComponent(dataEntry);
        hidden.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                hiddenFocusGained(evt);
            }
        });

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane1MouseClicked(evt);
            }
        });
        jTabbedPane1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTabbedPane1FocusGained(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N
        jScrollPane1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jScrollPane1MouseClicked(evt);
            }
        });

        saleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        saleTable.setName("saleTable"); // NOI18N
        saleTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        saleTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saleTableMouseClicked(evt);
            }
        });
        saleTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                saleTableFocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(saleTable);
        if (saleTable.getColumnModel().getColumnCount() > 0) {
            saleTable.getColumnModel().getColumn(0).setHeaderValue(bundle.getString("SalesScreen.saleTable.columnModel.title0")); // NOI18N
            saleTable.getColumnModel().getColumn(1).setHeaderValue(bundle.getString("SalesScreen.saleTable.columnModel.title1")); // NOI18N
            saleTable.getColumnModel().getColumn(2).setHeaderValue(bundle.getString("SalesScreen.saleTable.columnModel.title2")); // NOI18N
            saleTable.getColumnModel().getColumn(3).setHeaderValue(bundle.getString("SalesScreen.saleTable.columnModel.title3")); // NOI18N
        }

        jTabbedPane1.addTab(bundle.getString("SalesScreen.jScrollPane1.TabConstraints.tabTitle"), jScrollPane1); // NOI18N

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addComponent(dateLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dataEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(hidden, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(dataEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13))
                    .addGroup(panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dataLabel)
                            .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(hidden))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 873, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        panelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {dataEntry, dataLabel, dateLabel});

        jMenuBar1.setName("jMenuBar1"); // NOI18N

        jFileMenu.setText(bundle.getString("SalesScreen.jFileMenu.text")); // NOI18N
        jFileMenu.setName("jFileMenu"); // NOI18N

        newSaleMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F2, 0));
        newSaleMenuItem.setText(bundle.getString("SalesScreen.newSaleMenuItem.text_1")); // NOI18N
        newSaleMenuItem.setName("newSaleMenuItem"); // NOI18N
        newSaleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSaleMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(newSaleMenuItem);

        importMenuItem.setText(bundle.getString("SalesScreen.importMenuItem.text")); // NOI18N
        importMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMenuItemActionPerformed1(evt);
            }
        });
        jFileMenu.add(importMenuItem);

        testMenuItem.setText(bundle.getString("SalesScreen.testMenuItem.text")); // NOI18N
        testMenuItem.setName("testMenuItem"); // NOI18N
        testMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(testMenuItem);

        settingsMenuItem.setText(bundle.getString("SalesScreen.jSettings.text")); // NOI18N
        settingsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(settingsMenuItem);

        messagesMenuItem.setText(bundle.getString("SalesScreen.messagesMenuItem.text_1")); // NOI18N
        messagesMenuItem.setName("messagesMenuItem"); // NOI18N
        messagesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                messagesMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(messagesMenuItem);

        printCodesMenuItem.setText(bundle.getString("SalesScreen.printCodesMenuItem.text")); // NOI18N
        printCodesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printCodesMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(printCodesMenuItem);

        resetWindowsItem.setText(bundle.getString("SalesScreen.resetWindowsItem.text")); // NOI18N
        resetWindowsItem.setToolTipText(bundle.getString("resetTooltip")); // NOI18N
        resetWindowsItem.setName("resetWindowsItem"); // NOI18N
        resetWindowsItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetWindowsItemActionPerformed(evt);
            }
        });
        jFileMenu.add(resetWindowsItem);

        RunSqlMenuItem.setText(bundle.getString("SalesScreen.RunSqlMenuItem.text_1")); // NOI18N
        RunSqlMenuItem.setName("RunSqlMenuItem"); // NOI18N
        RunSqlMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RunSqlMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(RunSqlMenuItem);

        mirrorMenuItem.setText(bundle.getString("SalesScreen.mirrorMenuItem.text_1")); // NOI18N
        mirrorMenuItem.setName("mirrorMenuItem"); // NOI18N
        mirrorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mirrorMenuItemActionPerformed(evt);
            }
        });
        jFileMenu.add(mirrorMenuItem);

        jMenuBar1.add(jFileMenu);

        jPaymentMenu.setText(bundle.getString("SalesScreen.jPaymentMenu.text")); // NOI18N
        jPaymentMenu.setName("jPaymentMenu"); // NOI18N

        jCash.setText(bundle.getString("SalesScreen.jCash.text")); // NOI18N
        jCash.setName("jCash"); // NOI18N
        jCash.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCashActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jCash);

        jCheque.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F7, 0));
        jCheque.setText(bundle.getString("SalesScreen.jCheque.text")); // NOI18N
        jCheque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChequeActionPerformed1(evt);
            }
        });
        jPaymentMenu.add(jCheque);

        jCoupon.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        jCoupon.setText(bundle.getString("SalesScreen.jCoupon.text")); // NOI18N
        jCoupon.setName("jCoupon"); // NOI18N
        jCoupon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCouponActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jCoupon);

        jDebit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jDebit.setText(bundle.getString("SalesScreen.jDebit.text")); // NOI18N
        jDebit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDebitActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jDebit);

        jPaidIn.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, java.awt.event.InputEvent.SHIFT_MASK));
        jPaidIn.setText(bundle.getString("SalesScreen.jPaidIn.text")); // NOI18N
        jPaidIn.setName("jPaidIn"); // NOI18N
        jPaidIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPaidInActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jPaidIn);

        jPaidOut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        jPaidOut.setText(bundle.getString("SalesScreen.jPaidOut.text")); // NOI18N
        jPaidOut.setName("jPaidOut"); // NOI18N
        jPaidOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPaidOutActionPerformed1(evt);
            }
        });
        jPaymentMenu.add(jPaidOut);

        jReceipt.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PRINTSCREEN, 0));
        jReceipt.setText(bundle.getString("SalesScreen.jReceipt.text")); // NOI18N
        jReceipt.setName("jReceipt"); // NOI18N
        jReceipt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReceiptActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jReceipt);

        jCashInDrawer.setText(bundle.getString("SalesScreen.jCashInDrawer.text_1")); // NOI18N
        jCashInDrawer.setName("jCashInDrawer"); // NOI18N
        jCashInDrawer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCashInDrawerActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jCashInDrawer);

        noSaleMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F8, 0));
        noSaleMenuItem.setText(bundle.getString("SalesScreen.noSaleMenuItem.text_1")); // NOI18N
        noSaleMenuItem.setName("noSaleMenuItem"); // NOI18N
        noSaleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noSaleMenuItemActionPerformed(evt);
            }
        });
        jPaymentMenu.add(noSaleMenuItem);

        jCharge.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_PERIOD, java.awt.event.InputEvent.SHIFT_MASK));
        jCharge.setText(bundle.getString("SalesScreen.jCharge.text")); // NOI18N
        jCharge.setName("jCharge"); // NOI18N
        jCharge.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChargeActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jCharge);

        ownUseMenuItem.setText(bundle.getString("SalesScreen.ownUseMenuItem.text_1")); // NOI18N
        ownUseMenuItem.setName("ownUseMenuItem"); // NOI18N
        ownUseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownUseMenuItemActionPerformed(evt);
            }
        });
        jPaymentMenu.add(ownUseMenuItem);

        jReceivedOnAccount.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COMMA, java.awt.event.InputEvent.SHIFT_MASK));
        jReceivedOnAccount.setText(bundle.getString("SalesScreen.jReceivedOnAccount.text")); // NOI18N
        jReceivedOnAccount.setName("jReceivedOnAccount"); // NOI18N
        jReceivedOnAccount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReceivedOnAccountActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jReceivedOnAccount);

        creditCustomerMenuItem.setText(bundle.getString("SalesScreen.creditCustomerMenuItem.text_1")); // NOI18N
        creditCustomerMenuItem.setActionCommand(bundle.getString("SalesScreen.creditCustomerMenuItem.actionCommand")); // NOI18N
        creditCustomerMenuItem.setName("creditCustomerMenuItem"); // NOI18N
        creditCustomerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                creditCustomerMenuItemActionPerformed(evt);
            }
        });
        jPaymentMenu.add(creditCustomerMenuItem);

        jRefund.setText(bundle.getString("SalesScreen.jRefund.text")); // NOI18N
        jRefund.setName("jRefund"); // NOI18N
        jRefund.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRefundActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jRefund);

        jReturnableWaste.setText(bundle.getString("SalesScreen.jReturnableWaste.text")); // NOI18N
        jReturnableWaste.setName("jReturnableWaste"); // NOI18N
        jReturnableWaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReturnableWasteActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jReturnableWaste);

        jWaste.setText(bundle.getString("SalesScreen.jWaste.text")); // NOI18N
        jWaste.setName("jWaste"); // NOI18N
        jWaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jWasteActionPerformed(evt);
            }
        });
        jPaymentMenu.add(jWaste);

        lockMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SCROLL_LOCK, 0));
        lockMenuItem.setText(bundle.getString("SalesScreen.lockMenuItem.text_1")); // NOI18N
        lockMenuItem.setName("lockMenuItem"); // NOI18N
        lockMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockMenuItemActionPerformed1(evt);
            }
        });
        jPaymentMenu.add(lockMenuItem);

        layawayMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F12, 0));
        layawayMenuItem.setText(bundle.getString("SalesScreen.layawayMenuItem.text_1")); // NOI18N
        layawayMenuItem.setName("layawayMenuItem"); // NOI18N
        layawayMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layawayMenuItemActionPerformed(evt);
            }
        });
        jPaymentMenu.add(layawayMenuItem);

        jMenuBar1.add(jPaymentMenu);

        jTablesMenu.setText(bundle.getString("SalesScreen.jTablesMenu.text")); // NOI18N
        jTablesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTablesMenuActionPerformed(evt);
            }
        });

        deliveriesMenuItem.setMnemonic('D');
        deliveriesMenuItem.setText(bundle.getString("SalesScreen.deliveriesMenuItem.text")); // NOI18N
        deliveriesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deliveriesMenuItemActionPerformed1(evt);
            }
        });
        jTablesMenu.add(deliveriesMenuItem);

        deliveryAddressesItem.setText(bundle.getString("SalesScreen.deliveryAddressesItem.text")); // NOI18N
        deliveryAddressesItem.setName("deliveryAddressesItem"); // NOI18N
        deliveryAddressesItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deliveryAddressesItemActionPerformed(evt);
            }
        });
        jTablesMenu.add(deliveryAddressesItem);

        jDepartments.setText(bundle.getString("SalesScreen.jDepartment.text")); // NOI18N
        jDepartments.setName("jDepartments"); // NOI18N
        jDepartments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDepartmentsActionPerformed(evt);
            }
        });
        jTablesMenu.add(jDepartments);

        jEncodedProducts.setText(bundle.getString("SalesScreen.jEncodedProducts.text")); // NOI18N
        jEncodedProducts.setName("jEncodedProducts"); // NOI18N
        jEncodedProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEncodedProductsActionPerformed(evt);
            }
        });
        jTablesMenu.add(jEncodedProducts);

        jHotkeys.setText(bundle.getString("SalesScreen.hotkeys.text")); // NOI18N
        jHotkeys.setName("jHotkeys"); // NOI18N
        jHotkeys.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jHotkeysActionPerformed(evt);
            }
        });
        jTablesMenu.add(jHotkeys);

        jOffers.setText(bundle.getString("SalesScreen.jOffers.text")); // NOI18N
        jOffers.setName("jOffers"); // NOI18N
        jOffers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOffersActionPerformed(evt);
            }
        });
        jTablesMenu.add(jOffers);

        operators1.setText(bundle.getString("SalesScreen.operators1.text")); // NOI18N
        operators1.setName("operators1"); // NOI18N
        operators1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                operators1ActionPerformed(evt);
            }
        });
        jTablesMenu.add(operators1);

        orderMenuItem.setText(bundle.getString("SalesScreen.orderMenuItem.text_1")); // NOI18N
        orderMenuItem.setName("orderMenuItem"); // NOI18N
        orderMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                orderMenuItemActionPerformed(evt);
            }
        });
        jTablesMenu.add(orderMenuItem);

        packSuppliers1.setText(bundle.getString("SalesScreen.packSuppliers1.text")); // NOI18N
        packSuppliers1.setName("packSuppliers1"); // NOI18N
        packSuppliers1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packSuppliers1ActionPerformed(evt);
            }
        });
        jTablesMenu.add(packSuppliers1);

        packs1.setText(bundle.getString("SalesScreen.packs1.text")); // NOI18N
        packs1.setName("packs1"); // NOI18N
        packs1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                packs1ActionPerformed(evt);
            }
        });
        jTablesMenu.add(packs1);

        paidOuts1.setText(bundle.getString("SalesScreen.paidOuts.text")); // NOI18N
        paidOuts1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paidOuts1ActionPerformed(evt);
            }
        });
        jTablesMenu.add(paidOuts1);

        product1.setText(bundle.getString("SalesScreen.product1.text")); // NOI18N
        product1.setName("product1"); // NOI18N
        product1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                product1ActionPerformed(evt);
            }
        });
        jTablesMenu.add(product1);

        productPerformancesMenuItem.setText(bundle.getString("SalesScreen.productPerformancesMenuItem.text_1")); // NOI18N
        productPerformancesMenuItem.setName("productPerformancesMenuItem"); // NOI18N
        productPerformancesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productPerformancesMenuItemActionPerformed(evt);
            }
        });
        jTablesMenu.add(productPerformancesMenuItem);

        products1.setText(bundle.getString("SalesScreen.products1.text")); // NOI18N
        products1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                products1ActionPerformed(evt);
            }
        });
        jTablesMenu.add(products1);

        reconciledCashups.setText(bundle.getString("SalesScreen.reconciledCashups.text")); // NOI18N
        reconciledCashups.setName("reconciledCashups"); // NOI18N
        reconciledCashups.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reconciledCashupsActionPerformed(evt);
            }
        });
        jTablesMenu.add(reconciledCashups);

        sale1.setText(bundle.getString("SalesScreen.sale1.text")); // NOI18N
        sale1.setName("sale1"); // NOI18N
        sale1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sale1ActionPerformed(evt);
            }
        });
        jTablesMenu.add(sale1);

        sales.setText(bundle.getString("SalesScreen.sales.text")); // NOI18N
        sales.setName("sales"); // NOI18N
        sales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesActionPerformed(evt);
            }
        });
        jTablesMenu.add(sales);

        skus1.setText(bundle.getString("SalesScreen.skus1.text")); // NOI18N
        skus1.setName("skus1"); // NOI18N
        skus1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skus1ActionPerformed(evt);
            }
        });
        jTablesMenu.add(skus1);

        suppliers1.setText(bundle.getString("SalesScreen.suppliers1.text")); // NOI18N
        suppliers1.setName("suppliers1"); // NOI18N
        suppliers1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                suppliers1ActionPerformed(evt);
            }
        });
        jTablesMenu.add(suppliers1);

        jTaxes.setText(bundle.getString("SalesScreen.jTaxes.text")); // NOI18N
        jTaxes.setName("jTaxes"); // NOI18N
        jTaxes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTaxesActionPerformed(evt);
            }
        });
        jTablesMenu.add(jTaxes);

        tracksMenu.setText(bundle.getString("SalesScreen.tracksMenu.text_1")); // NOI18N
        tracksMenu.setName("tracksMenu"); // NOI18N
        tracksMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tracksMenuActionPerformed(evt);
            }
        });
        jTablesMenu.add(tracksMenu);

        jMenuBar1.add(jTablesMenu);

        jOffersMenu.setText(bundle.getString("SalesScreen.jOffersMenu.text")); // NOI18N
        jOffersMenu.setName("jOffersMenu"); // NOI18N

        createMenuItem2.setText(bundle.getString("SalesScreen.createMenuItem2.text")); // NOI18N
        createMenuItem2.setName("createMenuItem2"); // NOI18N
        createMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createMenuItem2ActionPerformed(evt);
            }
        });
        jOffersMenu.add(createMenuItem2);

        showMenuItem1.setText(bundle.getString("SalesScreen.ShowMenuItem1.text")); // NOI18N
        showMenuItem1.setName("showMenuItem1"); // NOI18N
        showMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showMenuItem1ActionPerformed(evt);
            }
        });
        jOffersMenu.add(showMenuItem1);

        jMenuBar1.add(jOffersMenu);

        ordersMenu.setText(bundle.getString("SalesScreen.ordersMenu.text")); // NOI18N
        ordersMenu.setName("ordersMenu"); // NOI18N

        ordersCreateMenuItem.setText(bundle.getString("SalesScreen.ordersCreateMenuItem.text")); // NOI18N
        ordersCreateMenuItem.setName("ordersCreateMenuItem"); // NOI18N
        ordersCreateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ordersCreateMenuItemActionPerformed(evt);
            }
        });
        ordersMenu.add(ordersCreateMenuItem);

        ordersViewMenuItem.setText(bundle.getString("SalesScreen.ordersViewMenuItem.text")); // NOI18N
        ordersViewMenuItem.setName("ordersViewMenuItem"); // NOI18N
        ordersViewMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ordersViewMenuItemActionPerformed(evt);
            }
        });
        ordersMenu.add(ordersViewMenuItem);

        jMenuBar1.add(ordersMenu);

        jCustomersMenu.setText(bundle.getString("SalesScreen.jCustomersMenu.text")); // NOI18N
        jCustomersMenu.setName("jCustomersMenu"); // NOI18N

        jByName.setText(bundle.getString("SalesScreen.jByName.text")); // NOI18N
        jByName.setName("jByName"); // NOI18N
        jByName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jByNameActionPerformed(evt);
            }
        });
        jCustomersMenu.add(jByName);

        jByNumber.setText(bundle.getString("SalesScreen.jByNumber.text")); // NOI18N
        jByNumber.setName("jByNumber"); // NOI18N
        jByNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jByNumberActionPerformed(evt);
            }
        });
        jCustomersMenu.add(jByNumber);

        jBalance.setText(bundle.getString("SalesScreen.jBalance.text")); // NOI18N
        jBalance.setName("jBalance"); // NOI18N
        jBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBalanceActionPerformed(evt);
            }
        });
        jCustomersMenu.add(jBalance);

        jBalances.setText(bundle.getString("SalesScreen.jBalances.text")); // NOI18N
        jBalances.setName("jBalances"); // NOI18N
        jBalances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBalancesActionPerformed(evt);
            }
        });
        jCustomersMenu.add(jBalances);

        jNewCustomer.setText(bundle.getString("SalesScreen.jNewCustomer.text")); // NOI18N
        jNewCustomer.setName("jNewCustomer"); // NOI18N
        jNewCustomer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNewCustomerActionPerformed(evt);
            }
        });
        jCustomersMenu.add(jNewCustomer);

        jSeparator3.setName("jSeparator3"); // NOI18N
        jCustomersMenu.add(jSeparator3);

        ownUseByName.setText(bundle.getString("SalesScreen.ownUseByName.text_1")); // NOI18N
        ownUseByName.setName("ownUseByName"); // NOI18N
        ownUseByName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownUseByNameActionPerformed(evt);
            }
        });
        jCustomersMenu.add(ownUseByName);

        ownUseByNumber.setText(bundle.getString("SalesScreen.ownUseByNumber.text_1")); // NOI18N
        ownUseByNumber.setName("ownUseByNumber"); // NOI18N
        ownUseByNumber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownUseByNumberActionPerformed(evt);
            }
        });
        jCustomersMenu.add(ownUseByNumber);

        ownUseBalance.setText(bundle.getString("SalesScreen.ownUseBalance.text_1")); // NOI18N
        ownUseBalance.setName("ownUseBalance"); // NOI18N
        ownUseBalance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownUseBalanceActionPerformed(evt);
            }
        });
        jCustomersMenu.add(ownUseBalance);

        ownUseBalances.setText(bundle.getString("SalesScreen.ownUseBalances.text_1")); // NOI18N
        ownUseBalances.setName("ownUseBalances"); // NOI18N
        ownUseBalances.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownUseBalancesActionPerformed(evt);
            }
        });
        jCustomersMenu.add(ownUseBalances);

        ownUseNew.setText(bundle.getString("SalesScreen.ownUseNew.text_1")); // NOI18N
        ownUseNew.setName("ownUseNew"); // NOI18N
        ownUseNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ownUseNewActionPerformed(evt);
            }
        });
        jCustomersMenu.add(ownUseNew);

        jMenuBar1.add(jCustomersMenu);

        jStockMenu.setText(bundle.getString("SalesScreen.jStockMenu.text")); // NOI18N
        jStockMenu.setName("jStockMenu"); // NOI18N

        jDelivery.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0));
        jDelivery.setMnemonic('D');
        jDelivery.setText(bundle.getString("SalesScreen.jDelivery.text")); // NOI18N
        jDelivery.setName("jDelivery"); // NOI18N
        jDelivery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDeliveryActionPerformed(evt);
            }
        });
        jStockMenu.add(jDelivery);

        jStocktake.setText(bundle.getString("SalesScreen.jStocktake.text")); // NOI18N
        jStocktake.setName("jStocktake"); // NOI18N

        jAll.setText(bundle.getString("SalesScreen.jAll.text")); // NOI18N
        jAll.setName("jAll"); // NOI18N

        jAllDefault.setText(bundle.getString("SalesScreen.jAllDefault.text")); // NOI18N
        jAllDefault.setName("jAllDefault"); // NOI18N
        jAllDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAllDefaultActionPerformed(evt);
            }
        });
        jAll.add(jAllDefault);

        jAllAll.setText(bundle.getString("SalesScreen.jAllAll.text")); // NOI18N
        jAllAll.setName("jAllAll"); // NOI18N
        jAllAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAllAllActionPerformed(evt);
            }
        });
        jAll.add(jAllAll);

        jStocktake.add(jAll);

        jAllLessThan10.setText(bundle.getString("SalesScreen.jAllLessThan10.text")); // NOI18N
        jAllLessThan10.setName("jAllLessThan10"); // NOI18N

        jDefault10.setText(bundle.getString("SalesScreen.jAllDefault.text")); // NOI18N
        jDefault10.setName("jDefault10"); // NOI18N
        jDefault10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDefault10ActionPerformed(evt);
            }
        });
        jAllLessThan10.add(jDefault10);

        jAll10.setText(bundle.getString("SalesScreen.jAllAll.text")); // NOI18N
        jAll10.setName("jAll10"); // NOI18N
        jAll10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAll10ActionPerformed(evt);
            }
        });
        jAllLessThan10.add(jAll10);

        jStocktake.add(jAllLessThan10);

        jNegatives.setText(bundle.getString("SalesScreen.jNegatives.text")); // NOI18N
        jNegatives.setName("jNegatives"); // NOI18N

        jNegativeDefault.setText(bundle.getString("SalesScreen.jAllDefault.text")); // NOI18N
        jNegativeDefault.setName("jNegativeDefault"); // NOI18N
        jNegativeDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNegativeDefaultActionPerformed(evt);
            }
        });
        jNegatives.add(jNegativeDefault);

        jNegativeAll.setText(bundle.getString("SalesScreen.jAllAll.text")); // NOI18N
        jNegativeAll.setName("jNegativeAll"); // NOI18N
        jNegativeAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jNegativeAllActionPerformed(evt);
            }
        });
        jNegatives.add(jNegativeAll);

        jStocktake.add(jNegatives);

        jStop.setText(bundle.getString("SalesScreen.jStop.text")); // NOI18N
        jStop.setName("jStop"); // NOI18N

        jStopDefault.setText(bundle.getString("SalesScreen.jAllDefault.text")); // NOI18N
        jStopDefault.setName("jStopDefault"); // NOI18N
        jStopDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStopDefaultActionPerformed(evt);
            }
        });
        jStop.add(jStopDefault);

        jStopAll.setText(bundle.getString("SalesScreen.jAllAll.text")); // NOI18N
        jStopAll.setName("jStopAll"); // NOI18N
        jStop.add(jStopAll);

        jStocktake.add(jStop);

        jDepartment.setText(bundle.getString("SalesScreen.jDepartment.text")); // NOI18N
        jDepartment.setName("jDepartment"); // NOI18N

        jDepartmentDefault.setText(bundle.getString("SalesScreen.jAllDefault.text")); // NOI18N
        jDepartmentDefault.setName("jDepartmentDefault"); // NOI18N
        jDepartmentDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDepartmentDefaultActionPerformed(evt);
            }
        });
        jDepartment.add(jDepartmentDefault);

        jDepartmentAll.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        jDepartmentAll.setText(bundle.getString("SalesScreen.jAllAll.text")); // NOI18N
        jDepartmentAll.setName("jDepartmentAll"); // NOI18N
        jDepartmentAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDepartmentAllActionPerformed(evt);
            }
        });
        jDepartment.add(jDepartmentAll);

        jStocktake.add(jDepartment);

        jSupplier.setText(bundle.getString("SalesScreen.jSupplier.text")); // NOI18N
        jSupplier.setName("jSupplier"); // NOI18N

        jSupplierDefault.setText(bundle.getString("SalesScreen.jAllDefault.text")); // NOI18N
        jSupplierDefault.setName("jSupplierDefault"); // NOI18N
        jSupplierDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSupplierDefaultActionPerformed(evt);
            }
        });
        jSupplier.add(jSupplierDefault);

        jSupplierAll.setText(bundle.getString("SalesScreen.jAllAll.text")); // NOI18N
        jSupplierAll.setName("jSupplierAll"); // NOI18N
        jSupplierAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSupplierAllActionPerformed(evt);
            }
        });
        jSupplier.add(jSupplierAll);

        jStocktake.add(jSupplier);

        jProduct.setText(bundle.getString("SalesScreen.jProduct.text")); // NOI18N
        jProduct.setName("jProduct"); // NOI18N
        jProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jProductActionPerformed(evt);
            }
        });
        jStocktake.add(jProduct);

        jStockMenu.add(jStocktake);

        jStockByValue.setText(bundle.getString("SalesScreen.jStockByValue.text")); // NOI18N
        jStockByValue.setName("jStockByValue"); // NOI18N

        jMenuItem11.setText(bundle.getString("SalesScreen.jMenuItem11.text")); // NOI18N
        jMenuItem11.setName("jMenuItem11"); // NOI18N
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jStockByValue.add(jMenuItem11);

        jMenuItem13.setText(bundle.getString("SalesScreen.jMenuItem13.text")); // NOI18N
        jMenuItem13.setName("jMenuItem13"); // NOI18N
        jMenuItem13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jStockByValue.add(jMenuItem13);

        jMenuItem12.setText(bundle.getString("SalesScreen.jMenuItem12.text")); // NOI18N
        jMenuItem12.setName("jMenuItem12"); // NOI18N
        jMenuItem12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jStockByValue.add(jMenuItem12);

        jMenuItem14.setText(bundle.getString("SalesScreen.jMenuItem14.text")); // NOI18N
        jMenuItem14.setName("jMenuItem14"); // NOI18N
        jMenuItem14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jStockByValue.add(jMenuItem14);

        jStockMenu.add(jStockByValue);

        jStartingStock.setText(bundle.getString("SalesScreen.jStartingStock.text")); // NOI18N
        jStartingStock.setToolTipText(bundle.getString("StartingStockTip")); // NOI18N
        jStartingStock.setName("jStartingStock"); // NOI18N
        jStartingStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jStartingStockActionPerformed(evt);
            }
        });
        jStockMenu.add(jStartingStock);

        addToStockMenuItem.setText(bundle.getString("SalesScreen.addToStockMenuItem.text_1")); // NOI18N
        addToStockMenuItem.setName(bundle.getString("SalesScreen.addToStockMenuItem.name")); // NOI18N
        addToStockMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToStockMenuItemActionPerformed(evt);
            }
        });
        jStockMenu.add(addToStockMenuItem);

        jTempStock.setText(bundle.getString("SalesScreen.jTempStock.text")); // NOI18N
        jTempStock.setToolTipText(bundle.getString("jTempStockToolTip")); // NOI18N
        jTempStock.setName("jTempStock"); // NOI18N
        jTempStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTempStockActionPerformed(evt);
            }
        });
        jStockMenu.add(jTempStock);

        linkMenuItem.setText(bundle.getString("SalesScreen.linkMenuItem.text_1")); // NOI18N
        linkMenuItem.setToolTipText(bundle.getString("Link.Tip")); // NOI18N
        linkMenuItem.setName("linkMenuItem"); // NOI18N
        linkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkMenuItemActionPerformed(evt);
            }
        });
        jStockMenu.add(linkMenuItem);

        jMenuBar1.add(jStockMenu);

        jCharts.setText(bundle.getString("SalesScreen.jCharts.text")); // NOI18N

        jSalesByDepartment.setText(bundle.getString("SalesScreen.jSalesByDepartment.text")); // NOI18N
        jSalesByDepartment.setName("jSalesByDepartment"); // NOI18N
        jSalesByDepartment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSalesByDepartmentActionPerformed(evt);
            }
        });
        jCharts.add(jSalesByDepartment);

        jSales.setText(bundle.getString("SalesScreen.jSales.text")); // NOI18N
        jSales.setName("jSales"); // NOI18N
        jSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSalesActionPerformed(evt);
            }
        });
        jCharts.add(jSales);

        salesTargetMenuItem.setText(bundle.getString("SalesScreen.salesTargetMenuItem.text_1")); // NOI18N
        salesTargetMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesTargetMenuItemActionPerformed(evt);
            }
        });
        jCharts.add(salesTargetMenuItem);

        workloadMenuItem.setText(bundle.getString("SalesScreen.workloadMenuItem.text_1")); // NOI18N
        workloadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                workloadMenuItemActionPerformed(evt);
            }
        });
        jCharts.add(workloadMenuItem);

        jMenuBar1.add(jCharts);

        reportMenuItem.setText(bundle.getString("SalesScreen.reportMenuItem.text")); // NOI18N
        reportMenuItem.setName("reportMenuItem"); // NOI18N
        reportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCustomerSalesReportActionPerformed1(evt);
            }
        });

        takings.setText(bundle.getString("SalesScreen.takings.text")); // NOI18N
        takings.setName("takings"); // NOI18N
        takings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                takingsActionPerformed(evt);
            }
        });
        reportMenuItem.add(takings);

        cashUp.setText(bundle.getString("CashUp")); // NOI18N
        cashUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cashUpActionPerformed1(evt);
            }
        });
        reportMenuItem.add(cashUp);

        jReconcile.setText(bundle.getString("SalesScreen.jReconcile.actionCommand")); // NOI18N
        jReconcile.setActionCommand(bundle.getString("SalesScreen.jReconcile.actionCommand")); // NOI18N
        jReconcile.setName("jReconcile"); // NOI18N
        jReconcile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReconcileActionPerformed(evt);
            }
        });
        reportMenuItem.add(jReconcile);

        agencyReconcilliationMenuItem.setText(bundle.getString("SalesScreen.agencyReconcilliationMenuItem.text_1")); // NOI18N
        agencyReconcilliationMenuItem.setName(bundle.getString("SalesScreen.agencyReconcilliationMenuItem.name")); // NOI18N
        agencyReconcilliationMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agencyReconcilliationMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(agencyReconcilliationMenuItem);

        salesByMenu.setText(bundle.getString("SalesScreen.salesByMenu.text")); // NOI18N

        jAgency.setText(bundle.getString("SalesScreen.jAgency.text")); // NOI18N
        jAgency.setName("jAgency"); // NOI18N
        jAgency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAgencyActionPerformed(evt);
            }
        });
        salesByMenu.add(jAgency);

        jDepartment1.setText(bundle.getString("SalesScreen.jDepartment.text")); // NOI18N
        jDepartment1.setName("jDepartment1"); // NOI18N
        jDepartment1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDepartment1ActionPerformed(evt);
            }
        });
        salesByMenu.add(jDepartment1);

        discounted.setText(bundle.getString("SalesScreen.discounted.text_1")); // NOI18N
        discounted.setName("discounted"); // NOI18N
        discounted.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                discountedActionPerformed(evt);
            }
        });
        salesByMenu.add(discounted);

        jOperator.setText(bundle.getString("SalesScreen.jOperator.text")); // NOI18N
        jOperator.setName("jOperator"); // NOI18N
        jOperator.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOperatorActionPerformed(evt);
            }
        });
        salesByMenu.add(jOperator);

        pricedOver.setText(bundle.getString("SalesScreen.pricedOver.text_1")); // NOI18N
        pricedOver.setName("pricedOver"); // NOI18N
        pricedOver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pricedOverActionPerformed(evt);
            }
        });
        salesByMenu.add(pricedOver);

        jQuantity.setText(bundle.getString("SalesScreen.jQuantity.text")); // NOI18N
        jQuantity.setName("jQuantity"); // NOI18N
        jQuantity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jQuantityActionPerformed(evt);
            }
        });
        salesByMenu.add(jQuantity);

        jTax.setText(bundle.getString("SalesScreen.jTax.text")); // NOI18N
        jTax.setName("jTax"); // NOI18N
        jTax.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTaxActionPerformed(evt);
            }
        });
        salesByMenu.add(jTax);

        jValue.setText(bundle.getString("SalesScreen.jValue.text")); // NOI18N
        jValue.setName("jValue"); // NOI18N
        jValue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValueActionPerformed(evt);
            }
        });
        salesByMenu.add(jValue);

        jSeparator1.setName("jSeparator1"); // NOI18N
        salesByMenu.add(jSeparator1);

        jWastes.setText(bundle.getString("SalesScreen.jWastes.text")); // NOI18N
        jWastes.setName("jWastes"); // NOI18N
        jWastes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jWastesActionPerformed(evt);
            }
        });
        salesByMenu.add(jWastes);

        jReturns.setText(bundle.getString("SalesScreen.jReturns.text")); // NOI18N
        jReturns.setActionCommand(bundle.getString("SalesScreen.jRefunds.text")); // NOI18N
        jReturns.setName("jReturns"); // NOI18N
        jReturns.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReturnsActionPerformed(evt);
            }
        });
        salesByMenu.add(jReturns);

        jLosses.setText(bundle.getString("SalesScreen.jLosses.text")); // NOI18N
        jLosses.setName("jLosses"); // NOI18N
        jLosses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLossesActionPerformed(evt);
            }
        });
        salesByMenu.add(jLosses);

        jCharges.setText(bundle.getString("SalesScreen.jCharges.text")); // NOI18N
        jCharges.setName("jCharges"); // NOI18N
        jCharges.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jChargesActionPerformed(evt);
            }
        });
        salesByMenu.add(jCharges);

        jReceived.setText(bundle.getString("SalesScreen.jReceived.text")); // NOI18N
        jReceived.setName("jReceived"); // NOI18N
        jReceived.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReceivedActionPerformed(evt);
            }
        });
        salesByMenu.add(jReceived);

        jOwnUse.setText(bundle.getString("SalesScreen.jOwnUse.text")); // NOI18N
        jOwnUse.setName("jOwnUse"); // NOI18N
        jOwnUse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOwnUseActionPerformed(evt);
            }
        });
        salesByMenu.add(jOwnUse);

        reportMenuItem.add(salesByMenu);

        jDepartmentMenu.setText(bundle.getString("SalesScreen.jDepartment.text")); // NOI18N
        jDepartmentMenu.setName("jDepartmentMenu"); // NOI18N

        jDLosses.setText(bundle.getString("SalesScreen.jLosses.text")); // NOI18N
        jDLosses.setName("jDLosses"); // NOI18N
        jDLosses.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDLossesActionPerformed(evt);
            }
        });
        jDepartmentMenu.add(jDLosses);

        jReturnsByDepartment.setText(bundle.getString("SalesScreen.jReturns.text")); // NOI18N
        jReturnsByDepartment.setName("jReturnsByDepartment"); // NOI18N
        jReturnsByDepartment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReturnsByDepartmentActionPerformed(evt);
            }
        });
        jDepartmentMenu.add(jReturnsByDepartment);

        jDWastes.setText(bundle.getString("SalesScreen.jWastes.text")); // NOI18N
        jDWastes.setName("jDWastes"); // NOI18N
        jDWastes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDWastesActionPerformed(evt);
            }
        });
        jDepartmentMenu.add(jDWastes);

        reportMenuItem.add(jDepartmentMenu);

        jLosses2.setText(bundle.getString("SalesScreen.jLosses2.text")); // NOI18N
        jLosses2.setName("jLosses2"); // NOI18N
        jLosses2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLosses2ActionPerformed(evt);
            }
        });
        reportMenuItem.add(jLosses2);

        jReturns1.setText(bundle.getString("SalesScreen.jReturns1.text")); // NOI18N
        jReturns1.setName("jReturns1"); // NOI18N
        jReturns1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jReturns1ActionPerformed(evt);
            }
        });
        reportMenuItem.add(jReturns1);

        jWastes1.setText(bundle.getString("SalesScreen.jWastes.text")); // NOI18N
        jWastes1.setName("jWastes1"); // NOI18N
        jWastes1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jWastes1ActionPerformed(evt);
            }
        });
        reportMenuItem.add(jWastes1);

        jSeparator2.setName("jSeparator2"); // NOI18N
        reportMenuItem.add(jSeparator2);

        agencyReport.setText(bundle.getString("SalesScreen.agencyReport.text_1")); // NOI18N
        agencyReport.setName("agencyReport"); // NOI18N
        agencyReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agencyReportActionPerformed(evt);
            }
        });
        reportMenuItem.add(agencyReport);

        jMenuItem1.setText(bundle.getString("SalesScreen.jMenuItem1.text_1")); // NOI18N
        jMenuItem1.setName("jMenuItem1"); // NOI18N
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        reportMenuItem.add(jMenuItem1);

        catalogueItem.setText(bundle.getString("SalesScreen.catalogueItem.text_1")); // NOI18N
        catalogueItem.setName("catalogueItem"); // NOI18N
        catalogueItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                catalogueItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(catalogueItem);

        dailyDepartmentProductSales.setText(bundle.getString("SalesScreen.dailyDepartmentProductSales.text_1")); // NOI18N
        dailyDepartmentProductSales.setName("dailyDepartmentProductSales"); // NOI18N
        dailyDepartmentProductSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dailyDepartmentProductSalesActionPerformed(evt);
            }
        });
        reportMenuItem.add(dailyDepartmentProductSales);

        dailyDepartmentSales.setText(bundle.getString("SalesScreen.dailyDepartmentSales.text_1")); // NOI18N
        dailyDepartmentSales.setName(bundle.getString("SalesScreen.dailyDepartmentSales.name")); // NOI18N
        dailyDepartmentSales.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dailyDepartmentSalesActionPerformed(evt);
            }
        });
        reportMenuItem.add(dailyDepartmentSales);

        jMinimumStock.setText(bundle.getString("SalesScreen.jMinimumStock.text")); // NOI18N
        jMinimumStock.setName("jMinimumStock"); // NOI18N
        jMinimumStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMinimumStockActionPerformed(evt);
            }
        });
        reportMenuItem.add(jMinimumStock);

        customerSalesReportMenuItem.setText(bundle.getString("SalesScreen.customerSalesReportMenuItem.text")); // NOI18N
        customerSalesReportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customerSalesReportMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(customerSalesReportMenuItem);

        lastSaleReport.setText(bundle.getString("SalesScreen.lastSaleReport.text_1")); // NOI18N
        lastSaleReport.setToolTipText(bundle.getString("SalesScreen.lastSaleReport.toolTipText")); // NOI18N
        lastSaleReport.setName("lastSaleReport"); // NOI18N
        lastSaleReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastSaleReportActionPerformed(evt);
            }
        });
        reportMenuItem.add(lastSaleReport);

        marginsByDepartmentMenuItem.setText(bundle.getString("SalesScreen.marginsByDepartmentMenuItem.text_1")); // NOI18N
        marginsByDepartmentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marginsByDepartmentMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(marginsByDepartmentMenuItem);

        paidOutsMenuItem.setText(bundle.getString("SalesScreen.paidOutsMenuItem.text_1")); // NOI18N
        paidOutsMenuItem.setName("paidOutsMenuItem"); // NOI18N
        paidOutsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paidOutsMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(paidOutsMenuItem);

        purchaseHistory.setText(bundle.getString("SalesScreen.purchaseHistory.text_1")); // NOI18N
        purchaseHistory.setName(bundle.getString("SalesScreen.purchaseHistory.name")); // NOI18N
        purchaseHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purchaseHistoryActionPerformed(evt);
            }
        });
        reportMenuItem.add(purchaseHistory);

        monthlySalesByDepartmentMenuItem.setText(bundle.getString("SalesScreen.monthlySalesByDepartmentMenuItem.text_1")); // NOI18N
        monthlySalesByDepartmentMenuItem.setName("monthlySalesByDepartmentMenuItem"); // NOI18N
        monthlySalesByDepartmentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthlySalesByDepartmentMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(monthlySalesByDepartmentMenuItem);

        monthlySalesByProduct.setText(bundle.getString("SalesScreen.monthlySalesByProduct.text_1")); // NOI18N
        monthlySalesByProduct.setName("monthlySalesByProduct"); // NOI18N
        monthlySalesByProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                monthlySalesByProductActionPerformed(evt);
            }
        });
        reportMenuItem.add(monthlySalesByProduct);

        refundsMenuItem.setText(bundle.getString("SalesScreen.refundsMenuItem.text_1")); // NOI18N
        refundsMenuItem.setName("refundsMenuItem"); // NOI18N
        refundsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refundsMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(refundsMenuItem);

        salesByDepartments.setText(bundle.getString("SalesScreen.salesByDepartments.text")); // NOI18N
        salesByDepartments.setName("salesByDepartments"); // NOI18N
        salesByDepartments.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                salesByDepartmentsActionPerformed(evt);
            }
        });
        reportMenuItem.add(salesByDepartments);

        stoppedProductsMenuItem.setText(bundle.getString("SalesScreen.stoppedProductsMenuItem.text_1")); // NOI18N
        stoppedProductsMenuItem.setName("stoppedProductsMenuItem"); // NOI18N
        stoppedProductsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stoppedProductsMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(stoppedProductsMenuItem);

        supplierStockListMenuItem.setText(bundle.getString("SalesScreen.supplierStockListMenuItem.text_1")); // NOI18N
        supplierStockListMenuItem.setName("supplierStockListMenuItem"); // NOI18N
        supplierStockListMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                supplierStockListMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(supplierStockListMenuItem);

        top100MenuItem.setText(bundle.getString("SalesScreen.top100MenuItem.text_1")); // NOI18N
        top100MenuItem.setName("top100MenuItem"); // NOI18N
        top100MenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                top100MenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(top100MenuItem);

        top100ByDepartmentMenuItem.setText(bundle.getString("SalesScreen.top100ByDepartmentMenuItem.text_1")); // NOI18N
        top100ByDepartmentMenuItem.setName("top100ByDepartmentMenuItem"); // NOI18N
        top100ByDepartmentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                top100ByDepartmentMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(top100ByDepartmentMenuItem);

        top100BySupplier.setText(bundle.getString("SalesScreen.top100BySupplier.text_1")); // NOI18N
        top100BySupplier.setName(bundle.getString("SalesScreen.top100BySupplier.name")); // NOI18N
        top100BySupplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                top100BySupplierActionPerformed(evt);
            }
        });
        reportMenuItem.add(top100BySupplier);

        weeklySalesByDepartmentMenuItem.setText(bundle.getString("SalesScreen.weeklySalesByDepartmentMenuItem.text_1")); // NOI18N
        weeklySalesByDepartmentMenuItem.setName("weeklySalesByDepartmentMenuItem"); // NOI18N
        weeklySalesByDepartmentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weeklySalesByDepartmentMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(weeklySalesByDepartmentMenuItem);

        weeklySalesByProduct.setText(bundle.getString("SalesScreen.weeklySalesByProduct.text_1")); // NOI18N
        weeklySalesByProduct.setName("weeklySalesByProduct"); // NOI18N
        weeklySalesByProduct.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weeklySalesByProductActionPerformed(evt);
            }
        });
        reportMenuItem.add(weeklySalesByProduct);

        zeroPricedSalesMenuItem.setText(bundle.getString("SalesScreen.zeroPricedSalesMenuItem.text_1")); // NOI18N
        zeroPricedSalesMenuItem.setName("zeroPricedSalesMenuItem"); // NOI18N
        zeroPricedSalesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zeroPricedSalesMenuItemActionPerformed(evt);
            }
        });
        reportMenuItem.add(zeroPricedSalesMenuItem);

        jMenuBar1.add(reportMenuItem);

        jHelpMenu.setText(bundle.getString("SalesScreen.jHelpMenu.text")); // NOI18N
        jHelpMenu.setName("jHelpMenu"); // NOI18N
        jHelpMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jHelpMenuActionPerformed(evt);
            }
        });

        contentsMenuItem.setText(bundle.getString("SalesScreen.contentsMenuItem.text")); // NOI18N
        contentsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contentsMenuItemActionPerformed(evt);
            }
        });
        jHelpMenu.add(contentsMenuItem);

        jMenuItem9.setText(bundle.getString("SalesScreen.jMenuItem9.text")); // NOI18N
        jMenuItem9.setName("jMenuItem9"); // NOI18N
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jHelpMenu.add(jMenuItem9);

        dropboxMenuItem.setText(bundle.getString("SalesScreen.dropboxMenuItem.text_1")); // NOI18N
        dropboxMenuItem.setName("dropboxMenuItem"); // NOI18N
        dropboxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropboxMenuItemActionPerformed(evt);
            }
        });
        jHelpMenu.add(dropboxMenuItem);

        jMenuBar1.add(jHelpMenu);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void testMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testMenuItemActionPerformed
        Main.receiptPrinter.startReceipt();
        Main.receiptPrinter.printBarcode("0123456789");
        Main.receiptPrinter.endReceipt();
    }//GEN-LAST:event_testMenuItemActionPerformed

    private void settingsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsMenuItemActionPerformed
        Audio.play("Ring");
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Main.settingsTab.execute(true);
            setCursor(Cursor.getDefaultCursor());
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_settingsMenuItemActionPerformed

    private void printCodesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printCodesMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            if (Main.hardware.getBarcodePrinter().isEmpty()) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                BarcodePrinter.printCodes();
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                Long code = Long.parseLong("1000034");
                String barcodeDescription = "Charge";
                BarcodePrinter.print(code, barcodeDescription);
                code = Long.parseLong("1000035");
                barcodeDescription = "Received";
                BarcodePrinter.print(code, barcodeDescription);
                code = Long.parseLong("1000038");
                barcodeDescription = "Customer";
                BarcodePrinter.print(code, barcodeDescription);
                code = Long.parseLong("1000031");
                barcodeDescription = "Waste";
                BarcodePrinter.print(code, barcodeDescription);
                code = Long.parseLong("1000032");
                barcodeDescription = "Returnable waste";
                BarcodePrinter.print(code, barcodeDescription);
                code = Long.parseLong("1000043");
                barcodeDescription = "Credit customer";
                BarcodePrinter.print(code, barcodeDescription);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_printCodesMenuItemActionPerformed

    private void resetWindowsItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetWindowsItemActionPerformed
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            //root.getDouble("Version", 0);
            root.putDouble("Version", 0.0);
        }
    }//GEN-LAST:event_resetWindowsItemActionPerformed

    private void jCashActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCashActionPerformed
        //show help file
        CSH.setHelpIDString(getRootPane(), "cashtender");
        Main.csh.actionPerformed(evt);
        CSH.setHelpIDString(getRootPane(), "Firststeps");
    }//GEN-LAST:event_jCashActionPerformed

    private void jChequeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChequeActionPerformed
        if (Main.salesScreenFunctions.cheque()) {
        }
    }//GEN-LAST:event_jChequeActionPerformed

    private void jCouponActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCouponActionPerformed
        if (Main.salesScreenFunctions.coupon()) {
        }
    }//GEN-LAST:event_jCouponActionPerformed

    private void jDebitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDebitActionPerformed
        if (Main.salesScreenFunctions.debit()) {
        }
    }//GEN-LAST:event_jDebitActionPerformed

    private void jPaidInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPaidInActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.operator.isOwnerManager() && aSale.size() == 0) {
            PaidOut paidOut = new PaidOut(this, true);
            paidOut.execute(false, false);
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jPaidInActionPerformed

    private void jPaidOutActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPaidOutActionPerformed1
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.operator.isOwnerManager() && aSale.size() == 0) {
            PaidOut paidOut = new PaidOut(this, true);
            paidOut.execute(true, false);
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jPaidOutActionPerformed1

    private void jReceiptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jReceiptActionPerformed
        Audio.play("Beep");
        if (!newSaleStarted) {
            Main.receiptPrinter.printReceipt(Main.salesScreenFunctions.getSale());
            Main.invoice.print(Main.salesScreenFunctions.getSale(), getDiscount(), false);
        } else {
            receiptRequired = true;
        }
    }//GEN-LAST:event_jReceiptActionPerformed

    private void jChargeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChargeActionPerformed
        if (Main.operator.handleAccounts || Main.operator.isOwnerManager()) {
            Main.salesScreenFunctions.charge("1000034");
        }
    }//GEN-LAST:event_jChargeActionPerformed

    private void jReceivedOnAccountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jReceivedOnAccountActionPerformed
        if (Main.operator.handleAccounts || Main.operator.isOwnerManager()) {
            if (Main.salesScreenFunctions.getaSale().getSelection() == -1) {//needed as otherwise keypress needed to clear
                Main.salesScreenFunctions.receive("1000035");
            }
        }
    }//GEN-LAST:event_jReceivedOnAccountActionPerformed

    private void jRefundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRefundActionPerformed
        //show help file
        CSH.setHelpIDString(getRootPane(), "Refunds");
        Main.csh.actionPerformed(evt);
        CSH.setHelpIDString(getRootPane(), "Firststeps");
    }//GEN-LAST:event_jRefundActionPerformed

    private void jReturnableWasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jReturnableWasteActionPerformed
        Main.salesScreenFunctions.waste("1000032");
    }//GEN-LAST:event_jReturnableWasteActionPerformed

    private void jWasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jWasteActionPerformed
        Main.salesScreenFunctions.waste("1000031");
    }//GEN-LAST:event_jWasteActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // Deliveries
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Main.deliveries.execute(supplier);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            noIncrement = true;
            Main.operator.setOperator(op);
        } else {
            dataEntry.requestFocus();
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void deliveryAddressesItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deliveryAddressesItemActionPerformed
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.deliveryAddresses.execute();
        }
    }//GEN-LAST:event_deliveryAddressesItemActionPerformed

    private void jDepartmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDepartmentsActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.departments.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jDepartmentsActionPerformed

    private void jEncodedProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEncodedProductsActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.encodedProducts.execute();
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_jEncodedProductsActionPerformed

    private void jHotkeysActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jHotkeysActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.hotkeys.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jHotkeysActionPerformed

    private void jOffersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOffersActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.offers.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jOffersActionPerformed

    private void operators1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_operators1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && (Main.operator.isModifyOperators() || Main.operator.isOwner())) {
            Main.operators.execute();
        } else {
            Audio.play("Ring");
        }
        Main.operator.setOperator(op);
    }//GEN-LAST:event_operators1ActionPerformed

    private void packSuppliers1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packSuppliers1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.packSuppliers.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_packSuppliers1ActionPerformed

    private void packs1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_packs1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Main.packs.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_packs1ActionPerformed

    private void paidOuts1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paidOuts1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            int selectedTillID;
            selectedTillID = Main.selectTill.execute();
            if (selectedTillID < 0) {//escaped
                return;
            }
            Main.paidOuts.execute(selectedTillID);
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_paidOuts1ActionPerformed

    private void product1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_product1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete()
                && (Main.operator.isEnterProductTables() || Main.operator.isOwnerManager())) {
            Main.product.execute("");
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_product1ActionPerformed

    private void products1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_products1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete()
                && (Main.operator.isEnterProductTables() || Main.operator.isOwnerManager())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Long barcode = Main.products.execute();
            if (barcode > 0L) {
                if (Main.salesScreenFunctions.isSaleComplete()) {
                    Main.salesScreenFunctions.clearResult();
                }
                Main.salesScreenFunctions.setData(barcode.toString());
                dataEntry.setText(Main.salesScreenFunctions.getData());
                Main.salesScreenFunctions.dataEntryProcess(dataEntry.getText());
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.salesScreenFunctions.setNoIncrement(true);
            hidden.requestFocus();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_products1ActionPerformed

    private void reconciledCashupsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reconciledCashupsActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            if (Main.reconciledCashups.execute()) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                noIncrement = true;
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                noIncrement = true;
                //                Audio.play("Ring");
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_reconciledCashupsActionPerformed

    private void sale1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sale1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (aSale.getSelection() < 0 && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int till = Main.shop.getTillId();
            Main.sale1.execute(0, 0, false, till, false, true);
            setCursor(Cursor.getDefaultCursor());
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        Main.operator.setOperator(op);
    }//GEN-LAST:event_sale1ActionPerformed

    private void salesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int selectedTill = Main.selectTill.execute();
            if (selectedTill == -1) {//escape value
                Audio.play("Ring");
                setCursor(Cursor.getDefaultCursor());
                return;
            }
            from = Main.selectDate.execute(from, "From");
            if (from == null) {
                Audio.play("Ring");
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            to = Main.selectDate.execute(to, "To");
            if (to == null) {
                Audio.play("Ring");
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Main.sales.execute(selectedTill, from, to);
            setCursor(Cursor.getDefaultCursor());
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        Main.operator.setOperator(op);
    }//GEN-LAST:event_salesActionPerformed

    private void skus1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skus1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete()
                && (Main.operator.isEnterProductTables() || Main.operator.isOwnerManager())) {
            Main.sku.execute(0);
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_skus1ActionPerformed

    private void suppliers1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suppliers1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        // call to supliers,execute
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.suppliers.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_suppliers1ActionPerformed

    private void jTaxesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTaxesActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.taxes.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jTaxesActionPerformed

    private void createMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createMenuItem2ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && (Main.operator.isModifyOffers() | Main.operator.isOwnerManager())) {
            Main.offer.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        noIncrement = true;
    }//GEN-LAST:event_createMenuItem2ActionPerformed

    private void showMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showMenuItem1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && (Main.operator.isModifyOffers() | Main.operator.isOwnerManager())) {
            Main.offers.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_showMenuItem1ActionPerformed

    private void ordersCreateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ordersCreateMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && (Main.operator.handleOrders || Main.operator.isOwnerManager())) {
            supplier = 0;
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            supplier = Main.selectSupplier.execute();
            if (supplier != -1) {
                Main.orderCreate.execute(supplier);
            }
            setCursor(Cursor.getDefaultCursor());
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_ordersCreateMenuItemActionPerformed

    private void ordersViewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ordersViewMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && (Main.operator.handleOrders || Main.operator.isOwnerManager())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            supplier = Main.selectSupplier.execute();
            if (supplier > 0) {
                Main.order.execute(supplier);
            } else {
                Audio.play("Ring");
            }
            setCursor(Cursor.getDefaultCursor());
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_ordersViewMenuItemActionPerformed

    private void jByNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jByNameActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.operator.handleAccounts || Main.operator.isOwner()) {
            if (Main.salesScreenFunctions.isSaleComplete()) {
                Main.salesScreenFunctions.setCustomer(SaleType.CUSTOMER.code() * 10000L);
                Main.salesScreenFunctions.clearSale();
            }
            if (Main.salesScreenFunctions.getCustomer() != SaleType.CUSTOMER.code() * 10000l) {
                Audio.play("Ring");
                return;
            }
            long newCustomer = Main.customers.execute("ByName", false);
            Main.salesScreenFunctions.setCustomer(newCustomer);// SaleType.CUSTOMER.code() * 10000l +
            if (Main.salesScreenFunctions.getCustomer() == SaleType.CUSTOMER.code() * 10000L) {
                //no customer selected
                Main.salesScreenFunctions.setCustomer(SaleType.CUSTOMER.code() * 10000L);
                Main.salesScreenFunctions.setFirstName(Main.customers.getFirstName());
                Main.salesScreenFunctions.setSurname(Main.customers.getSurname());
                dataEntry.setText("");
                Main.salesScreenFunctions.updateResult();
                Main.operator.setOperator(op);
            } else {
                String fn = Main.customers.getFirstName();
                String sn = Main.customers.getSurname();
                if (Main.salesScreenFunctions.isSaleComplete()) {
                    Main.salesScreenFunctions.clearResult();
                }
                Main.salesScreenFunctions.setCustomer(newCustomer);
                Main.salesScreenFunctions.setFirstName(fn);
                Main.salesScreenFunctions.setSurname(sn);
                Main.salesScreenFunctions.setDiscount(Main.customer.getDiscount(Main.salesScreenFunctions.getCustomer()));
                Main.salesScreenFunctions.applyDiscount(Main.salesScreenFunctions.getDiscount());
                Main.salesScreenFunctions.setSaleComplete(false);
                Main.salesScreenFunctions.setNoIncrement(true);
                Main.salesScreenFunctions.updateResult();
                Main.salesScreenFunctions.updateSale();
                Main.operator.setOperator(op);
            }
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jByNameActionPerformed

    private void jByNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jByNumberActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.operator.handleAccounts || Main.operator.isOwner()) {
            if (Main.salesScreenFunctions.isSaleComplete()) {
                customer = SaleType.CUSTOMER.code() * 10000L;
                Main.salesScreenFunctions.clearSale();
            }
            if (customer != SaleType.CUSTOMER.code() * 10000L) {
                Audio.play("Ring");
                return;
            }
            long newCustomer = Main.customers.execute("ByNumber", false);
            setCustomer(newCustomer);
            if (getCustomer() == SaleType.CUSTOMER.code() * 10000L) {
                //no customer selected
                setCustomer(SaleType.CUSTOMER.code() * 10000L);
                Main.salesScreenFunctions.setFirstName(Main.customers.getFirstName());
                Main.salesScreenFunctions.setSurname(Main.customers.getSurname());
                Audio.play("Ring");
                dataEntry.setText("");
                resultsTable.repaint();
                Main.operator.setOperator(op);
            } else {
                String fn = Main.customers.getFirstName();
                String sn = Main.customers.getSurname();
                if (Main.salesScreenFunctions.isSaleComplete()) {
                    Main.salesScreenFunctions.clearResult();
                }
                Main.salesScreenFunctions.setCustomer(newCustomer);
                Main.salesScreenFunctions.setFirstName(fn);
                Main.salesScreenFunctions.setSurname(sn);
                Main.salesScreenFunctions.setDiscount(Main.customer.getDiscount(Main.salesScreenFunctions.getCustomer()));
                Main.salesScreenFunctions.applyDiscount(Main.salesScreenFunctions.getDiscount());
                Main.salesScreenFunctions.setSaleComplete(false);
                Main.salesScreenFunctions.setNoIncrement(true);
                Main.salesScreenFunctions.updateResult();
                Main.salesScreenFunctions.updateSale();
                Main.operator.setOperator(op);
            }
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jByNumberActionPerformed

    private void jBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBalanceActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.getaSale().getSelection() < 0 && (Main.operator.handleAccounts || Main.operator.isOwner())) {
            long customerLocal = getCustomer();
            if (getCustomer() == SaleType.CUSTOMER.code() * 10000l) {
                //need to identify the customer
                setCustomer(Main.customers.execute("ByName", false));
            }
            if (getCustomer() == SaleType.CUSTOMER.code() * 10000) {
                //no customer selected
                dataEntry.setText("");
                Main.operator.setOperator(op);
                return;
            } else {
                Main.balance.execute(getCustomer(), false);
                setCustomer(customerLocal);
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jBalanceActionPerformed

    private void jBalancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBalancesActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.getaSale().getSelection() < 0 && (Main.operator.handleAccounts || Main.operator.isOwner())) {
            //show the balances of all customers
            Main.balances.execute(false);
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jBalancesActionPerformed

    private void jNewCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNewCustomerActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.operator.handleAccounts || Main.operator.isOwner()) {
            boolean b = Main.customer.execute(-1l, false);
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jNewCustomerActionPerformed

    private void jDeliveryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDeliveryActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && (Main.operator.isDeliveries() || Main.operator.isOwnerManager())) {
            supplier = -1;
            delivery = -1;
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            supplier = Main.selectSupplier.execute();
            setCursor(Cursor.getDefaultCursor());
            if (supplier != -1) {
                if (supplier == SettingsTabbed.DEFAULTSUPPLIER) {//default supplier
                    return;
                } else {
                    delivery = Main.selectDelivery.execute(supplier);
                    if (delivery != -1) {
                        Main.deliveryEntry.execute(delivery);
                    }
                }
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jDeliveryActionPerformed

    private void jAllDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAllDefaultActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            Main.stockTake.execute(40, 0, 0, sWhenCreated, false, 0, 0);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jAllDefaultActionPerformed

    private void jAllAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAllAllActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            Main.stockTake.execute(-1, 0, 0, sWhenCreated, false, 0, 0);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jAllAllActionPerformed

    private void jDefault10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDefault10ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            Main.stockTake.execute(40, 0, 0, sWhenCreated, false, 10, 0);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jDefault10ActionPerformed

    private void jAll10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAll10ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            Main.stockTake.execute(0, 0, 0, sWhenCreated, false, 10, 0);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jAll10ActionPerformed

    private void jNegativeDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNegativeDefaultActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            Main.stockTake.execute(40, 0, 0, sWhenCreated, false, -1, 0);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jNegativeDefaultActionPerformed

    private void jNegativeAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jNegativeAllActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            Main.stockTake.execute(0, 0, 0, sWhenCreated, false, -1, 0);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jNegativeAllActionPerformed

    private void jStopDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStopDefaultActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            Main.stockTake.execute(40, 0, 0, sWhenCreated, true, 0, 0);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jStopDefaultActionPerformed

    private void jDepartmentDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDepartmentDefaultActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            department = Main.selectDepartment.execute();
            if (department == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Main.stockTake.execute(40, department, 0, sWhenCreated, false, 0, 0);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jDepartmentDefaultActionPerformed

    private void jDepartmentAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDepartmentAllActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            department = Main.selectDepartment.execute();
            if (department == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Main.stockTake.execute(0, department, 0, sWhenCreated, false, 0, 0);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jDepartmentAllActionPerformed

    private void jSupplierDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSupplierDefaultActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            supplier = Main.selectSupplier.execute();
            if (supplier == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Main.stockTake.execute(40, 0, 0, sWhenCreated, false, 0, supplier);
            Main.operator.setOperator(op);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jSupplierDefaultActionPerformed

    private void jSupplierAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSupplierAllActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            supplier = Main.selectSupplier.execute();
            if (supplier == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Main.stockTake.execute(0, 0, 0, sWhenCreated, false, 0, supplier);
            Main.operator.setOperator(op);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jSupplierAllActionPerformed

    private void jProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jProductActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isStockTake() || Main.operator.isOwnerManager()) {
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            do {
                long aBarcode = Main.selectProduct.execute();
                if (aBarcode > 0) {
                    int aSku = Main.selectProduct.getSku();
                    if (aSku > 1) {//no display for temps
                        Main.stockTake.execute(0, 0, aSku, sWhenCreated, false, 0, 0);
                    } else {
                        switch (JOptionPane.showConfirmDialog(this, bundle1.getString("AssignDepartment"))) {
                            case JOptionPane.NO_OPTION:
                            case JOptionPane.CANCEL_OPTION:
                            case JOptionPane.CLOSED_OPTION:
                                break;
                            case JOptionPane.YES_OPTION:
                                //show new product
                                product = Main.newProduct.execute(aBarcode, true);
                                if (product == 0L) {
                                    Audio.play("Ring");
                                    break;
                                } else if (product > 0L) {
                                    Audio.play("Beep");
                                } else {//closed as =-1
                                    break;
                                }
                        }
                    }
                } else {
                    break;
                }
            } while (true);
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jProductActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.stockByValue.execute(SkuType.INCLUDED);
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.stockByValue.execute(SkuType.OTHERS);
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            department = Main.selectDepartment.execute();
            if (department > 0) {
                Main.stockByValueDepartments.execute(department, SkuType.INCLUDED);
            }
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            department = Main.selectDepartment.execute();
            if (department > 0) {
                Main.stockByValueDepartments.execute(department, SkuType.OTHERS);
            }
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_jMenuItem14ActionPerformed

    private void jStartingStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jStartingStockActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && (Main.operator.enterStock || Main.operator.isOwnerManager())) {
            Main.startingStockEntry.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jStartingStockActionPerformed

    private void jTempStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTempStockActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && (Main.operator.enterStock || Main.operator.isOwnerManager())) {
            Main.tempProducts.execute();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jTempStockActionPerformed

    private void jSalesByDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSalesByDepartmentActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        fileLocation = "";
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            Audio.play("TaDa");
            parameters.put("normal", (int) (short) SaleType.NORMAL.value());
            parameters.put("charged", (int) (short) SaleType.CHARGED.value());
            from = Main.selectDate.execute(from, "From");
            if (from == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            to = Main.selectDate.execute(to, "To");
            if (to == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("REPORT_CONNECTION", connection);
            parameters.put("from", from);
            parameters.put("to", to);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("SalesByDepartment", parameters);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            Main.operator.setOperator(op);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_jSalesByDepartmentActionPerformed

    private void jSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSalesActionPerformed
        fileLocation = "";
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            int op = Main.operator.getOperator();//needed to clear temporary operator
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> params = new HashMap<String, Object>();
            Audio.play("TaDa");
            params.put("normal", "" + SaleType.NORMAL.value());
            params.put("charged", "" + SaleType.CHARGED.value());
            params.put("Orientation", "Vertical");
            File fc = getJasper("Sales");
            fileLocation = fc.getAbsolutePath();
            Connection con = Main.getConnection();
            params.put("REPORT_CONNECTION", con);
            try {
                jasperPrint = JasperFillManager.fillReport(fileLocation, params, con);
                JasperViewer.viewReport(jasperPrint, false);
            } catch (JRException ex) {
                Main.logger.log(Level.SEVERE, "SalesScreen.jReportsActionPerformed ", "JRException: " + ex.getMessage());
            }
            Main.operator.setOperator(op);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_jSalesActionPerformed

    private void takingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_takingsActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            int till = Main.selectTill.execute();
            if (till < 0) {
                return;
            }
            Main.takings.execute(till);
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_takingsActionPerformed

    private void cashUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashUpActionPerformed

    }//GEN-LAST:event_cashUpActionPerformed

    private void jReconcileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jReconcileActionPerformed
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            int cashupID = Main.selectCashup.executeCashups(0);
            if (cashupID < 0) {
                return;
            }
            if (Main.cashupReconciliation.execute(cashupID)) {
                Audio.play("Beep");
            } else {
                Audio.play("Ring");
            }
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jReconcileActionPerformed

    private void jAgencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAgencyActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            if (Main.operator.getIntAuthority() <= Operator.MANAGER) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                PreparedStatement agency;
                try {
                    agency = Main.getConnection().prepareStatement(
                            SQL.salesByAgency);
                    Main.salesBy.execute(bundle1.getString("ByAgency"), agency);
                } catch (SQLException ex) {
                    Main.closeConnection();
                    Logger

.getLogger(SalesScreen.class
.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                Audio.play("Ring");
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jAgencyActionPerformed

    private void jDepartment1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDepartment1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            Calendar start_Date = Calendar.getInstance();
            start_Date.add(Calendar.MONTH, -1);
            start_Date = Main.selectCalendarDate.execute(start_Date, bundle1.getString("From"));
            if (start_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Calendar end_Date = Main.selectCalendarDate.execute(start_Date, bundle1.getString("To"));
            if (end_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Date fromDate = start_Date.getTime();
            Date toDate = end_Date.getTime();
            parameters.put("from", fromDate);
            parameters.put("to", toDate);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("SalesByDepartments",
                    parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jDepartment1ActionPerformed

    private void jOperatorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOperatorActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            PreparedStatement operator;
            try {
                operator = Main.getConnection().prepareStatement(
                        SQL.salesByOperator);
                //"ORDER BY Totals");
                Main.salesBy.execute(bundle1.getString("ByOperator"), operator);
            } catch (SQLException ex) {
                Main.closeConnection();
                Main.logger.log(Level.SEVERE, "SalesScreen.jOperatorActionPerformed ", "SQLException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen.MenuOperator", JOptionPane.ERROR_MESSAGE);
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jOperatorActionPerformed

    private void jQuantityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jQuantityActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            if (Main.operator.getIntAuthority() <= Operator.MANAGER) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                PreparedStatement quantity;
                try {
                    quantity = Main.getConnection().prepareStatement(
                            SQL.salesByQuantity);
                    Main.salesBy.execute(bundle1.getString("ByQuantity"), quantity);
                } catch (SQLException ex) {
                    Main.closeConnection();
                    Audio.play("Ring");
                    Main.logger.log(Level.SEVERE, "SalesScreen.jQuantityActionPerformed ", "SQLException: " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen", JOptionPane.ERROR_MESSAGE);
                }
                Main.operator.setOperator(op);
            } else {
                Audio.play("Ring");
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_jQuantityActionPerformed

    private void jTaxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTaxActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            PreparedStatement byTax;
            try {
                byTax = Main.getConnection().prepareStatement(SQL.salesByTax);
                Main.salesBy.execute(bundle1.getString("ByTax"), byTax);
            } catch (SQLException ex) {
                Main.closeConnection();
                Main.logger.log(Level.SEVERE, "SalesScreen.jTaxActionPerformed ", "SQLException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen", JOptionPane.ERROR_MESSAGE);
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jTaxActionPerformed

    private void jValueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jValueActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            Calendar start_Date = Calendar.getInstance();
            start_Date.add(Calendar.MONTH, -1);
            start_Date = Main.selectCalendarDate.execute(start_Date, bundle1.getString("From"));
            if (start_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Calendar end_Date = Main.selectCalendarDate.execute(start_Date, bundle1.getString("To"));
            if (end_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Date fromDate;
            fromDate = start_Date.getTime();
            Date toDate;
            toDate = end_Date.getTime();
            parameters.put("from", fromDate);
            parameters.put("to", toDate);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("SalesByValue", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jValueActionPerformed

    private void jWastesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jWastesActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            PreparedStatement waste;
            try {
                waste = Main.getConnection().prepareStatement(SQL.salesByWastes);
                Main.salesBy.execute(bundle1.getString("ByWastes"), waste);
            } catch (SQLException ex) {
                Main.closeConnection();
                Main.logger.log(Level.SEVERE, "SalesScreen.jWastesActionPerformed ", "SQLException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen", JOptionPane.ERROR_MESSAGE);
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jWastesActionPerformed

    private void jReturnsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jReturnsActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            PreparedStatement waste;
            try {
                waste = Main.getConnection().prepareStatement(SQL.salesByReturns);
                Main.salesBy.execute(bundle1.getString("ByReturns"), waste);
            } catch (SQLException ex) {
                Main.closeConnection();
                Main.logger.log(Level.SEVERE, "SalesScreen.jRefundsActionPerformed ", "SQLException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen", JOptionPane.ERROR_MESSAGE);
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jReturnsActionPerformed

    private void jLossesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLossesActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            PreparedStatement waste;
            try {
                waste = Main.getConnection().prepareStatement(SQL.salesByLosses);
                Main.salesBy.execute(bundle1.getString("ByLosses"), waste);
            } catch (SQLException ex) {
                Main.closeConnection();
                Main.logger.log(Level.SEVERE, "SalesScreen.jRefundsActionPerformed ", "SQLException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen", JOptionPane.ERROR_MESSAGE);
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jLossesActionPerformed

    private void jChargesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChargesActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.getaSale().size() == 0) {
            if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                PreparedStatement waste;
                try {
                    waste = Main.getConnection().prepareStatement(SQL.salesByCharges);
                    Main.salesBy.execute(bundle1.getString("ByCharges"), waste);
                } catch (SQLException ex) {
                    Main.closeConnection();
                    Main.logger.log(Level.SEVERE, "SalesScreen.jRefundsActionPerformed ", "SQLException: " + ex.getMessage());
                    JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen", JOptionPane.ERROR_MESSAGE);
                }
                Main.operator.setOperator(op);
            } else {
                Audio.play("Ring");
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_jChargesActionPerformed

    private void jReceivedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jReceivedActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            PreparedStatement salesByReceivedPS;
            try {
                salesByReceivedPS = Main.getConnection().prepareStatement(SQL.salesByReceived);
                Main.salesBy.execute(bundle1.getString("ByReceived"), salesByReceivedPS);
            } catch (SQLException ex) {
                Main.closeConnection();
                Main.logger.log(Level.SEVERE, "SalesScreen.jReceivedActionPerformed ", "SQLException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen", JOptionPane.ERROR_MESSAGE);
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jReceivedActionPerformed

    private void jOwnUseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOwnUseActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            PreparedStatement waste;
            try {
                waste = Main.getConnection().prepareStatement(SQL.salesByOwnUse);
                Main.salesBy.execute(bundle1.getString("ByOwnUse"), waste);
            } catch (SQLException ex) {
                Main.closeConnection();
                Main.logger.log(Level.SEVERE, "SalesScreen.jOwnUseActionPerformed ", "SQLException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen", JOptionPane.ERROR_MESSAGE);
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_jOwnUseActionPerformed

    private void jDLossesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDLossesActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            department = Main.selectDepartment.execute();
            if (department > 0) {
                Main.wastes.execute(SaleType.LOSS, department);
            }
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_jDLossesActionPerformed

    private void jReturnsByDepartmentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jReturnsByDepartmentActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            department = Main.selectDepartment.execute();
            if (department > 0) {
                Main.wastes.execute(SaleType.RETURN, department);
            }
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_jReturnsByDepartmentActionPerformed

    private void jDWastesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDWastesActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            department = Main.selectDepartment.execute();
            if (department > 0) {
                Main.wastes.execute(SaleType.WASTE, department);
            }
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_jDWastesActionPerformed

    private void jLosses2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLosses2ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.wastes.execute(SaleType.LOSS, 0);
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jLosses2ActionPerformed

    private void jReturns1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jReturns1ActionPerformed
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            int op = Main.operator.getOperator();//needed to clear temporary operator
            Main.wastes.execute(SaleType.RETURN, 0);
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jReturns1ActionPerformed

    private void jWastes1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jWastes1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.wastes.execute(SaleType.WASTE, 0);
        } else {
            Audio.play("Ring");
        }
        Main.operator.setOperator(op);
    }//GEN-LAST:event_jWastes1ActionPerformed

    private void jMinimumStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMinimumStockActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        File fc = null;
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            supplier = Main.selectSupplier.execute();
            if (supplier < 0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            Object put = parameters.put("Supplier", supplier);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("lowStock", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jMinimumStockActionPerformed

    private void jCustomerSalesReportActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCustomerSalesReportActionPerformed1
        int op = Main.operator.getOperator();//needed to clear temporary operator
        Calendar selectedDate;
        fileLocation = "";
        // Look up a report
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> params = new HashMap<String, Object>();
            Audio.play("TaDa");
            selectedDate = Main.selectMonthYear.execute();
            month = selectedDate.get(Calendar.MONTH);
            year = selectedDate.get(Calendar.YEAR);
            if (month == 0 && year == 1) {//selectMonthYear was escaped
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            if (month == 0) {
                month = 12;
                year -= 1;
            }
            params.put("month", "" + month);
            params.put("year", "" + year);
            File fc = getJasper("CustomerSalesReport1");
            Connection con = Main.getConnection();
            try {
                fileLocation = fc.getAbsolutePath();
                jasperPrint = JasperFillManager.fillReport(fileLocation, params, con);
                JasperViewer.viewReport(jasperPrint, false);
                Object[] options = {"Yes", "No"};
                int n = JOptionPane.showOptionDialog(null,
                        "Would you like print this? ",
                        "Print?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (n == JOptionPane.YES_OPTION) {
                    JasperPrintManager.printReport(jasperPrint, true);
                    Main.operator.setOperator(op);
                

}
            } catch (JRException ex) {
                Logger.getLogger(StartingStockEntry.class
.getName()).log(Level.SEVERE, null, ex);
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.closeConnection();
        }
    }//GEN-LAST:event_jCustomerSalesReportActionPerformed1

    private void contentsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contentsMenuItemActionPerformed
        noIncrement = true;
        Main.csh.actionPerformed(evt);
    }//GEN-LAST:event_contentsMenuItemActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        Main.about.execute();
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jHelpMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jHelpMenuActionPerformed
    }//GEN-LAST:event_jHelpMenuActionPerformed

    private void dataEntryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataEntryKeyReleased
        rawData = dataEntry.getText();
        int size = Main.salesScreenFunctions.getaSale().size() - 1;
        Main.salesScreenFunctions.setData(StringOps.stripSpaces(rawData));
//        boolean b=Main.salesScreenFunctions.isSaleComplete() ;
//        if (b && evt.getKeyCode() != KeyEvent.VK_ENTER && Main.salesScreenFunctions.getData().length() > 0) {//any other character
//            Main.salesScreenFunctions.clearResult();
//        }
        printPressed(Main.salesScreenFunctions.getData(), evt);
        int i = evt.getKeyCode();
        //need to have suitable start to word
        escapePressed(Main.salesScreenFunctions.getData(), evt);
        queryPressed(Main.salesScreenFunctions.getData(), evt);
        cPressed(Main.salesScreenFunctions.getData(), evt);
        mPressed(Main.salesScreenFunctions.getData(), evt);
        rPressed(Main.salesScreenFunctions.getData(), evt);
        oPressed(Main.salesScreenFunctions.getData(), evt);
        wPressed(Main.salesScreenFunctions.getData(), evt);
        aPressed(Main.salesScreenFunctions.getData(), evt);
        if (Main.salesScreenFunctions.isPharmacistApproval()) {
            if (evt.getKeyCode() != KeyEvent.VK_Y
                    && (evt.getKeyCode() != KeyEvent.VK_ENTER)) {
                if (Main.salesScreenFunctions.getQty() > 0) {
                    Main.salesScreenFunctions.getaSale().setQuantityAt(Main.salesScreenFunctions.getSelection(), Main.salesScreenFunctions.getQty() - 1);
                } else {
                    Main.salesScreenFunctions.getaSale().setQuantityAt(Main.salesScreenFunctions.getSelection(), Main.salesScreenFunctions.getQty() + 1);
                }
                Main.salesScreenFunctions.getModel().fireTableDataChanged();
                Main.salesScreenFunctions.updateSale();
                Main.salesScreenFunctions.updateResult();
            } else {
                Audio.play("Beep");
            }
            dataEntry.setText("");
            Main.salesScreenFunctions.setPharmacistApproval(false);
            dataLabel.setText(bundle1.getString("SalesScreen.dataLabel.text"));
            dataEntry.setBackground(normalColour);
            return;
        }
        int len = dataEntry.getText().length();
        if (Main.salesScreenFunctions.isAgeCheck()) {
            if (evt.getKeyCode() != KeyEvent.VK_Y && evt.getKeyCode() != KeyEvent.VK_N) {
                dataEntry.setText("");
                Audio.play("Ring");
                return;
            } else if (evt.getKeyCode() == KeyEvent.VK_N) {
                if (qty > 0) {
                    Main.salesScreenFunctions.getaSale().setQuantityAt(Main.salesScreenFunctions.getSelection(), Main.salesScreenFunctions.getQty() - 1);
                } else {
                    Main.salesScreenFunctions.getaSale().setQuantityAt(Main.salesScreenFunctions.getSelection(), Main.salesScreenFunctions.getQty() + 1);
                }
                Main.salesScreenFunctions.getModel().fireTableDataChanged();
                Main.salesScreenFunctions.updateSale();
                Main.salesScreenFunctions.updateResult();
//                Main.salesScreenFunctions.age=Math.max(Main.Main.salesScreenFunctions.age, 0);
            } else {
                Audio.play("Beep");
                Main.salesScreenFunctions.setVerifiedAge(Math.max(Main.salesScreenFunctions.getAge(), Main.salesScreenFunctions.getVerifiedAge()));
//                Main.salesScreenFunctions.verifiedAge = age;
            }
            dataEntry.setText("");
            Main.salesScreenFunctions.setAgeCheck(false);
            dataLabel.setText(bundle1.getString("SalesScreen.dataLabel.text"));
            dataEntry.setBackground(normalColour);
            return;
        }
        if (!Main.salesScreenFunctions.isTrackIt() & alphaPressed(Main.salesScreenFunctions.getData(), evt)) {
            //noIncrement=true;
            Main.salesScreenFunctions.setAlpha(true);
            return;
        }
        if (evt.getKeyCode() == KeyEvent.VK_UP && !(Main.salesScreenFunctions.getPriceIt() || Main.salesScreenFunctions.getDescribeIt() || Main.salesScreenFunctions.getWeighIt() || Main.salesScreenFunctions.getPriceEntry())) {
            selection = Main.salesScreenFunctions.getaSale().getSelection();
            if (selection != -1) {
                selection = Math.max(selection - 1, 0);
                saleTable.setRowSelectionInterval(selection, selection);
                int j = saleTable.getSelectedRow();
                scrollToVisible(saleTable, selection, 0);
                Main.salesScreenFunctions.setSelection(selection);
                Main.salesScreenFunctions.getaSale().setSelection(selection);
                saleTable.repaint();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN && !(Main.salesScreenFunctions.getPriceIt() || Main.salesScreenFunctions.getDescribeIt() || Main.salesScreenFunctions.getWeighIt() || Main.salesScreenFunctions.getPriceEntry())) {
            selection = Main.salesScreenFunctions.getaSale().getSelection();
            if (selection != -1) {
                selection = Math.min(selection + 1, size);
                saleTable.setRowSelectionInterval(selection, selection);
                scrollToVisible(saleTable, selection, 0);
                Main.salesScreenFunctions.setSelection(selection);
                Main.salesScreenFunctions.getaSale().setSelection(selection);
                saleTable.repaint();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP && !(Main.salesScreenFunctions.getPriceIt() || Main.salesScreenFunctions.getDescribeIt() || Main.salesScreenFunctions.getWeighIt() || Main.salesScreenFunctions.getPriceEntry())) {
            selection = Main.salesScreenFunctions.getaSale().getSelection();
            if (selection != -1) {
                selection = Math.max(selection - SwingOps.getVisbleRowCount(saleTable), 0);
                saleTable.setRowSelectionInterval(selection, selection);
                scrollToVisible(saleTable, selection, 0);
                Main.salesScreenFunctions.getaSale().setSelection(selection);
                Main.salesScreenFunctions.setSelection(selection);
                saleTable.repaint();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN && !(Main.salesScreenFunctions.getPriceIt() || Main.salesScreenFunctions.getDescribeIt() || Main.salesScreenFunctions.getWeighIt() || Main.salesScreenFunctions.getPriceEntry())) {
            selection = Main.salesScreenFunctions.getaSale().getSelection();
            if (selection != -1) {
                selection = Math.min(selection + SwingOps.getVisbleRowCount(saleTable), size);
                saleTable.setRowSelectionInterval(selection, selection);
                scrollToVisible(saleTable, selection, 0);
                Main.salesScreenFunctions.setSelection(selection);
                Main.salesScreenFunctions.getaSale().setSelection(selection);
                saleTable.repaint();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_HOME && !(Main.salesScreenFunctions.getPriceIt() || Main.salesScreenFunctions.getDescribeIt() || Main.salesScreenFunctions.getWeighIt() || Main.salesScreenFunctions.getPriceEntry())) {
            selection = Main.salesScreenFunctions.getaSale().getSelection();
            if (selection != -1) {
                selection = Math.min(selection, 0);
                saleTable.setRowSelectionInterval(selection, selection);
                scrollToVisible(saleTable, selection, 0);
                Main.salesScreenFunctions.getaSale().setSelection(selection);
                Main.salesScreenFunctions.setSelection(selection);
                saleTable.repaint();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_END && !(Main.salesScreenFunctions.getPriceIt() || Main.salesScreenFunctions.getDescribeIt() || Main.salesScreenFunctions.getWeighIt() || Main.salesScreenFunctions.getPriceEntry())) {
            selection = Main.salesScreenFunctions.getaSale().getSelection();
            if (selection != -1) {
                selection = size;
                saleTable.setRowSelectionInterval(selection, selection);
                scrollToVisible(saleTable, selection, 0);
                Main.salesScreenFunctions.getaSale().setSelection(selection);
                Main.salesScreenFunctions.setSelection(selection);
                saleTable.repaint();
            }
        } else if (Main.salesScreenFunctions.isSaleComplete() && evt.getKeyCode() == KeyEvent.VK_LEFT && Main.operator.isSalesPerson()) {
            //show last sale without navigation
            int till = Main.shop.getTillId();
            Main.sale1.execute(Main.salesScreenFunctions.getSale(), 0, false, till, false, false);
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            SalesScreenFunctions.setWasEnter(true);
            Main.salesScreenFunctions.setShift(evt.getModifiers() == 1);//not correct - should not use constant here else
            hidden.requestFocus();
        } else if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (Main.salesScreenFunctions.isPriceIt() || Main.salesScreenFunctions.isWeighIt() || Main.salesScreenFunctions.isPriceEntry()) {
                Main.salesScreenFunctions.setPriceIt(false);
                Main.salesScreenFunctions.setWeighIt(false);
                Main.salesScreenFunctions.setPriceEntry(false);
                Main.salesScreenFunctions.delete();
            }
            if (aSale.getSelection() < 0) {
                Main.salesScreenFunctions.clearSale();
                Main.salesScreenFunctions.clearResult();
                Main.salesScreenFunctions.setSaleComplete(true);
                Main.pole.execute(Main.operator.operatorName,
                        Main.customerMessages.getString("ReadyToServeYou"));
            }
            Main.salesScreenFunctions.setData("");
            dataEntry.setText("");
        } else if (evt.getKeyCode() == 91 && !evt.isShiftDown()) {
            dataEntry.setText("500");
        } else if (evt.getKeyCode() == 93 && !evt.isShiftDown()) {
            dataEntry.setText("5000");
        } else if (evt.getKeyCode() == KeyEvent.VK_SEMICOLON && !evt.isShiftDown()) {
            if (dataEntry.getText().length() == 1) {
                dataEntry.setText("100");
                Audio.play("Beep");
            } else if (dataEntry.getText().length() != 0) {
                dataEntry.setText(dataEntry.getText().substring(0, dataEntry.getText().length() - 1) + "00");
                Audio.play("Beep");
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_QUOTE && !evt.isShiftDown()) {
            if (dataEntry.getText().length() == 1) {
                dataEntry.setText("1000");
                Audio.play("Beep");
            } else if (dataEntry.getText().length() != 0) {
                dataEntry.setText(dataEntry.getText().substring(0, dataEntry.getText().length() - 1) + "000");
                Audio.play("Beep");
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_NUMBER_SIGN || ((evt.getKeyCode() == KeyEvent.VK_EQUALS
                && len == 1)) && !evt.isShiftDown()) {
            if (dataEntry.getText().length() == 1) {
                dataEntry.setText("10000");
                Audio.play("Beep");
            } else if (dataEntry.getText().length() != 0 && dataEntry.getText().length() != 11) {
//                dataEntry.setText(dataEntry.getText().substring(0, dataEntry.getText().length()-1) +"0000");
                Audio.play("Beep");
            }
        } else if (dataEntry.getText().length() > 0 && evt.getKeyCode() == KeyEvent.VK_EQUALS
                && Main.salesScreenFunctions.getaSale().getSelection() != -1) {
//            s=dataEntry.getText();
//            len=s.length();
//            dataEntry.setText('@'+s.substring(0, len-1));
//            Audio.play("Beep");
//            hidden.requestFocus();
        }
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
        Main.salesScreenFunctions.showHashes(dataEntry.getText());
    }//GEN-LAST:event_dataEntryKeyReleased

    private void hiddenFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_hiddenFocusGained

        Main.salesScreenFunctions.setData(dataEntry.getText().trim());
        if (Main.salesScreenFunctions.dataEntryProcess(dataEntry.getText())) {
            if (!Main.salesScreenFunctions.isDescribeIt()) {
                dataEntry.setText("");
            }
            dataEntry.requestFocus();
            SalesScreenFunctions.setWasEnter(false);
            return;
        } else if (!Main.salesScreenFunctions.isDescribeIt() && !Main.salesScreenFunctions.isPharmacistApproval() && !Main.salesScreenFunctions.isAgeCheck()) {
            dataEntry.setText("");
        }
        //shift = evt.getModifiers() == 1;//not correct - should not use constant
        if (Main.salesScreenFunctions.isSaleComplete()) {
            Main.salesScreenFunctions.clearSale();
        }
        if (Main.salesScreenFunctions.isNoIncrement()) {//fudge to stop increasing by one after restriction
            Main.salesScreenFunctions.setAlpha(false);
            Main.salesScreenFunctions.setNoIncrement(false);
            dataEntry.requestFocus();
            SalesScreenFunctions.setWasEnter(false);
            return;
        }
        Audio.play("Ring");
        Main.salesScreenFunctions.updateSale();
        dataEntry.requestFocus();
        SalesScreenFunctions.setWasEnter(false);
    }//GEN-LAST:event_hiddenFocusGained

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (Main.server.serverName.equalsIgnoreCase("localhost")) {//only if on server
            backupAndClose();
        }
    }//GEN-LAST:event_formWindowClosing

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        dataEntry.setText(dataEntry.getText() + "7");
        dataEntry.requestFocus();
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        dataEntry.setText(dataEntry.getText() + "8");
        dataEntry.requestFocus();
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        dataEntry.setText(dataEntry.getText() + "9");
        dataEntry.requestFocus();
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        dataEntry.setText(dataEntry.getText() + "4");
        dataEntry.requestFocus();
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        dataEntry.setText(dataEntry.getText() + "5");
        dataEntry.requestFocus();
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        dataEntry.setText(dataEntry.getText() + "6");
        dataEntry.requestFocus();
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        dataEntry.setText(dataEntry.getText() + "1");
        dataEntry.requestFocus();
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        dataEntry.setText(dataEntry.getText() + "2");
        dataEntry.requestFocus();
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        dataEntry.setText(dataEntry.getText() + "3");
        dataEntry.requestFocus();
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteActionPerformed
        if (dataEntry.getText().length() > 0) {
            int l = dataEntry.getText().length();
            dataEntry.setText(dataEntry.getText().substring(0, l - 1));
        } else {
            Main.salesScreenFunctions.delete();
        }
        dataEntry.requestFocus();
    }//GEN-LAST:event_jButtonDeleteActionPerformed

    private void jButton0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton0ActionPerformed
        dataEntry.setText(dataEntry.getText() + "0");
        Main.salesScreenFunctions.addSpaces(dataEntry.getText());
        dataEntry.requestFocus();
    }//GEN-LAST:event_jButton0ActionPerformed

    private void jButtonMinusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonMinusActionPerformed
        dataEntry.setText(dataEntry.getText() + "-");
        dataEntry.requestFocus();
    }//GEN-LAST:event_jButtonMinusActionPerformed

    private void jButtonPerCentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPerCentActionPerformed
        if (dataEntry.getText().length() >= 1) {
            dataEntry.setText(dataEntry.getText() + "%");
            dataEntry.requestFocus();
        } else {
            Main.alphabetic.execute();
        }
    }//GEN-LAST:event_jButtonPerCentActionPerformed

    private void jButtonTimesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTimesActionPerformed
        dataEntry.setText(dataEntry.getText() + "*");
        dataEntry.requestFocus();
    }//GEN-LAST:event_jButtonTimesActionPerformed

    private void jButtonAtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAtActionPerformed
        dataEntry.setText(dataEntry.getText() + "@");
        dataEntry.requestFocus();
    }//GEN-LAST:event_jButtonAtActionPerformed

    private void jButtonEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnterActionPerformed
//        hidden.requestFocus();
        data = dataEntry.getText();
        data = StringOps.stripSpaces(data);
        if (Main.salesScreenFunctions.tender(data)) {
            Main.salesScreenFunctions.setSaleComplete(true);
            return;
        } else if (Main.salesScreenFunctions.newDescription(rawData)) {
        } else if (Main.salesScreenFunctions.newPrice(data)) {
        } else if (Main.salesScreenFunctions.blankEntry(rawData)) {
        }
        hidden.requestFocus();
    }//GEN-LAST:event_jButtonEnterActionPerformed

    private void orderMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_orderMenuItemActionPerformed
        if (Main.salesScreenFunctions.isSaleComplete() && (Main.operator.handleOrders || Main.operator.isOwnerManager())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            supplier = Main.selectSupplier.execute();
            if (supplier > 0) {
                Main.order.execute(supplier);
            } else {
                Audio.play("Ring");
            }
            setCursor(Cursor.getDefaultCursor());
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_orderMenuItemActionPerformed

    private void importMenuItemActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuItemActionPerformed1
        // locate the import file and then call parser.execute
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {

            if (JOptionPane.showConfirmDialog(this,
                    bundle1.getString("AreYouSure"),
                    bundle1.getString("Warning"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null) == JOptionPane.YES_OPTION) {
                //need to save backup
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "csv file", "csv");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    backupSave();//now saved
                    Parser.execute(chooser.getSelectedFile());
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }//GEN-LAST:event_importMenuItemActionPerformed1

    private void dataEntryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dataEntryKeyPressed
        //moved from keyReleased because deletes when deleting last character
        deletePressed(data, evt);
    }//GEN-LAST:event_dataEntryKeyPressed

    private void tracksMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tracksMenuActionPerformed
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.tracks.execute();
        }
    }//GEN-LAST:event_tracksMenuActionPerformed

    private void noSaleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noSaleMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Math.min(Main.salesScreenFunctions.getaSale().getSelection(), aSale.size() - 1) < 0 && (Main.operator.isNoSale() || Main.operator.isOwnerManager())) {
            PaidOut paidOut = new PaidOut(this, true);
            paidOut.execute(false, true);//no sale
            Main.operator.setOperator(op);
        } else {
            //to cope with Robbery
//            PaidOut paidOut = new PaidOut(this, true);
//            paidOut.execute(false,true);//no sale
        }
    }//GEN-LAST:event_noSaleMenuItemActionPerformed

    private void lockMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockMenuItemActionPerformed
    }//GEN-LAST:event_lockMenuItemActionPerformed

    private void dataEntryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_dataEntryFocusGained
        if (delay > 0) {
            autoTimer.start();
        }
    }//GEN-LAST:event_dataEntryFocusGained

    private void messagesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_messagesMenuItemActionPerformed
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            Main.customerMessages.execute();
        }
    }//GEN-LAST:event_messagesMenuItemActionPerformed

    private void cashUpActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cashUpActionPerformed1
        if ((Main.operator.isCashup() || Main.operator.isOwnerManager())) {
            if (Main.shop.fixedFloat == 0 && Main.shop.getShiftFloat() == 0) {
                fixedFloat = Main.selectFloat.execute();
                Main.shop.setShiftFloat(fixedFloat);
                if (fixedFloat <= 0) {
                    return;
                }
            } else if (Main.shop.fixedFloat == 0) {
                fixedFloat = Main.shop.getShiftFloat();
            }
            CashUp localCashUp;
            localCashUp = new CashUp();
            if (localCashUp.execute(fixedFloat)) {
                Audio.play("Beep");
                backupAndClose();
                System.exit(0);
            } else {
                Main.pole.execute(Main.operator.operatorName,
                        Main.customerMessages.getString("ReadyToServeYou"));
            }
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_cashUpActionPerformed1

    private void lockMenuItemActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockMenuItemActionPerformed1
        lock();
        if (aSale.getSelection() < 0) {
            Main.pole.execute(Main.customerMessages.getString("Sorry"), Main.customerMessages.getString("OutOfService"));
            Main.salesScreenFunctions.setSaleComplete(true);
        }
    }//GEN-LAST:event_lockMenuItemActionPerformed1

    private void deliveriesMenuItemActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deliveriesMenuItemActionPerformed1
        // Deliveries
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && (Main.operator.isDeliveries() || Main.operator.isOwnerManager())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Main.deliveries.execute(supplier);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            noIncrement = true;
            Main.operator.setOperator(op);
        } else {
            dataEntry.requestFocus();
            Audio.play("Ring");
        }
    }//GEN-LAST:event_deliveriesMenuItemActionPerformed1

    private void jTablesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTablesMenuActionPerformed
        // Deliveries
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Main.deliveries.execute(supplier);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            noIncrement = true;
            Main.operator.setOperator(op);
        } else {
            dataEntry.requestFocus();
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jTablesMenuActionPerformed

    private void jCashInDrawerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCashInDrawerActionPerformed
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            //
            if (Main.shop.fixedFloat == 0 && Main.shop.getShiftFloat() == 0) {
                fixedFloat = Main.selectFloat.execute();
                Main.shop.setShiftFloat(fixedFloat);
                if (fixedFloat <= 0) {
                    return;
                }
            } else {
                fixedFloat = Main.shop.getShiftFloat();
            }
            int cashInDrawer = Main.cashUp.cashInDrawer();
            Main.salesScreenFunctions.clearResult();
            Main.salesScreenFunctions.setTotal(cashInDrawer);
            Main.salesScreenFunctions.updateResult();
            Main.salesScreenFunctions.setSaleComplete(true);
            Audio.play("CashReg");
        }
    }//GEN-LAST:event_jCashInDrawerActionPerformed

    private void resultsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_resultsTableMouseClicked
        if (Main.hardware.touch) {
            Point p = evt.getPoint();
            int row = resultsTable.rowAtPoint(p);
            if (row == 3) {
                if (Main.salesScreenFunctions.debit()) {
                }
            } else if (row == 4) {
                if (Main.salesScreenFunctions.coupon()) {
                }
            } else if (row == 5) {
                if (Main.salesScreenFunctions.cheque()) {
                }
            }
        }
    }//GEN-LAST:event_resultsTableMouseClicked

    private void jChequeActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jChequeActionPerformed1
        if (Main.salesScreenFunctions.cheque()) {
        }
    }//GEN-LAST:event_jChequeActionPerformed1

    private void workloadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_workloadMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            Date selectedDate;
            selectedDate = Main.selectDate.execute(null);
            parameters.put("Date", selectedDate);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("WorkLoadWithCharts", parameters);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_workloadMenuItemActionPerformed

    private void marginsByDepartmentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marginsByDepartmentMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            department = Main.selectDepartment.execute();
            if (department == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("DepartmentID", "" + department);
            parameters.put("kgSymbol", Main.shop.getKgSymbol());
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("MarginByDepartment", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_marginsByDepartmentMenuItemActionPerformed

    private void paidOutsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paidOutsMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("poundSymbol", Main.shop.poundSymbol);
            Calendar start_Date = Calendar.getInstance();
            start_Date.add(Calendar.MONTH, -1);
            start_Date = Main.selectCalendarDate.execute(start_Date, bundle1.getString("From"));
            if (start_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Calendar end_Date = Main.selectCalendarDate.execute(start_Date, bundle1.getString("To"));
            if (end_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Date start = start_Date.getTime();
            Date end = end_Date.getTime();
            parameters.put("Start_Date", start);
            parameters.put("End_Date", end);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("PaidOuts", parameters);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_paidOutsMenuItemActionPerformed

    private void top100MenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_top100MenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            theDate = Main.selectMonthYear.execute();
            if (theDate == null) {
                Main.operator.setOperator(op);
                Main.operator.setOperator(op);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            month = theDate.get(Calendar.MONTH);
            year = theDate.get(Calendar.YEAR);
            if (month == 0 && year == 1) {//selectMonthYear was escaped
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            if (month == 0) {
                month = 12;
                year -= 1;
            }
            parameters.put("month", month);
            parameters.put("year", year);
            parameters.put("kgSymbol", Main.shop.getKgSymbol());
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("Top100", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_top100MenuItemActionPerformed

    private void customerSalesReportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customerSalesReportMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        Calendar todaysDate;
        fileLocation = "";
        // Look up a report
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            Audio.play("TaDa");
            todaysDate = Main.selectMonthYear.execute();
            if (todaysDate == null) {
                Main.operator.setOperator(op);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            month = todaysDate.get(Calendar.MONTH);
            year = todaysDate.get(Calendar.YEAR);
            if (month == 0 && year == 1) {//selectMonthYear was escaped
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            if (month == 0) {
                month = 12;
                year -= 1;
            }
            parameters.put("month", "" + month);
            parameters.put("year", "" + year);
//            File fc = new File(defaultDirectory + "ProffittCenterReports/CustomerSalesReport1.jasper");
//            File fc=getJasper("CustomerSalesReport1");
            Connection con = Main.getConnection();
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("CustomerSalesReport1", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            Main.closeConnection();
//            try {
//                fileLocation = fc.getAbsolutePath();
//                jasperPrint = JasperFillManager.fillReport(fileLocation, params, con);
//                JasperViewer.viewReport(jasperPrint, false);
//                Object[] options = {"Yes", "No"};
//                int n = JOptionPane.showOptionDialog(null,
//                        "Would you like print this? ",
//                        "Print?",
//                        JOptionPane.YES_NO_CANCEL_OPTION,
//                        JOptionPane.QUESTION_MESSAGE,
//                        null,
//                        options,
//                        options[0]);
//                if (n == JOptionPane.YES_OPTION) {
//                    JasperPrintManager.printReport(jasperPrint, true);
//                }
//            } catch (JRException ex) {
//                Logger.getLogger(StartingStockEntry.class.getName()).log(Level.SEVERE, null, ex);
//            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
            Main.closeConnection();
        }
    }//GEN-LAST:event_customerSalesReportMenuItemActionPerformed

    private void salesTargetMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesTargetMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isSalesPerson()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            parameters.put("operator", Main.operator.getOperator());
            if (target == 0) {
                target = Main.selectPrice.execute(bundle1.getString("Target"), originalPrice);
            }
            if (target < 1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("target", target);
            target = 0;//if target set elswhere, need to change with operator
//            File fc = new File(defaultDirectory + "ProffittCenterReports/SalesTarget.jasper");
            File fc = getJasper("SalesTarget");
            try {
                fileLocation = fc.getAbsolutePath();
                jasperPrint = JasperFillManager.fillReport(fileLocation, parameters, connection);
                JasperViewer.viewReport(jasperPrint, false);
            

} catch (JRException ex) {
                Logger.getLogger(SalesScreen.class
.getName()).log(Level.SEVERE, null, ex);
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        Main.operator.setOperator(op);
    }//GEN-LAST:event_salesTargetMenuItemActionPerformed

    private void panelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panelMouseClicked
        dataEntry.requestFocus();
    }//GEN-LAST:event_panelMouseClicked

    private void productPerformancesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productPerformancesMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Main.productPerformances.execute();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            noIncrement = true;
            Main.operator.setOperator(op);
        } else {
            dataEntry.requestFocus();
            Audio.play("Ring");
        }
    }//GEN-LAST:event_productPerformancesMenuItemActionPerformed

    private void saleTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saleTableMouseClicked
        int ii = saleTable.getSelectedRow();
        Main.salesScreenFunctions.getaSale().setSelection(saleTable.getSelectedRow());
        saleTable.repaint();
        dataEntry.requestFocus();
    }//GEN-LAST:event_saleTableMouseClicked

    private void jScrollPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jScrollPane1MouseClicked
        dataEntry.requestFocus();
    }//GEN-LAST:event_jScrollPane1MouseClicked

    private void jTabbedPane1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane1MouseClicked
        dataEntry.requestFocus();
    }//GEN-LAST:event_jTabbedPane1MouseClicked

    private void ownUseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownUseMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        Main.salesScreenFunctions.ownUse("1000036");
        Main.operator.setOperator(op);
    }//GEN-LAST:event_ownUseMenuItemActionPerformed

    private void weeklySalesByProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weeklySalesByProductActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            long barcode = Main.selectProduct.execute();
            if (barcode == 0L) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            theDate = Main.selectYear.execute();
            if (theDate == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            year = theDate.get(Calendar.YEAR);
            parameters.put("year", year);
            parameters.put("Barcode", barcode);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("WeeklyProductSales", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_weeklySalesByProductActionPerformed

    private void monthlySalesByProductActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthlySalesByProductActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            long barcode = Main.selectProduct.execute();
            if (barcode == 0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            sku = Main.selectProduct.getSku();
            theDate = Main.selectYear.execute();
            if (theDate == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            year = theDate.get(Calendar.YEAR);
            parameters.put("year", year);
            parameters.put("sku", sku);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("MonthlyProductSales", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_monthlySalesByProductActionPerformed

    private void monthlySalesByDepartmentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_monthlySalesByDepartmentMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            department = Main.selectDepartment.execute();
            if (department == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            theDate = Main.selectYear.execute();
            if (theDate == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            year = theDate.get(Calendar.YEAR);
            parameters.put("year", year);
            parameters.put("department", department);
            parameters.put("poundSymbol", Main.shop.poundSymbol);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("MonthlyDepartmentSales", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_monthlySalesByDepartmentMenuItemActionPerformed

    private void RunSqlMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RunSqlMenuItemActionPerformed
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            if (JOptionPane.showConfirmDialog(this,
                    bundle1.getString("AreYouSure"),
                    bundle1.getString("Warning"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null) == JOptionPane.YES_OPTION) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "SQL file", "sql");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                    File file = chooser.getSelectedFile();
                    SqlRunner.execute(file);
                    setCursor(Cursor.getDefaultCursor());
                    Audio.play("Beep");
                }
            }

        }
    }//GEN-LAST:event_RunSqlMenuItemActionPerformed

    private void weeklySalesByDepartmentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weeklySalesByDepartmentMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            department = Main.selectDepartment.execute();
            if (department == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            theDate = Main.selectYear.execute();
            if (theDate == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            year = theDate.get(Calendar.YEAR);
            parameters.put("year", year);
            parameters.put("department", department);
            parameters.put("poundSymbol", Main.shop.poundSymbol);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("WeeklyDepartmentSales", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_weeklySalesByDepartmentMenuItemActionPerformed

    private void saleTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_saleTableFocusGained
//        hidden.requestFocus();
    }//GEN-LAST:event_saleTableFocusGained

    private void jTabbedPane1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTabbedPane1FocusGained
        hidden.requestFocus();
    }//GEN-LAST:event_jTabbedPane1FocusGained

    private void mirrorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mirrorMenuItemActionPerformed
        int selectedTillID;
        selectedTillID = Main.selectTill.execute();
        if (selectedTillID == 0) {
            Integer selectedTill = Main.selectTill.execute();
            if (selectedTill != 0) {//cannot talk to itself
                try {
                    // send events
                    sock = new Socket("localhost", 6754);
                    openSender(sock, selectedTill);
                } catch (Exception ex) {
                    //must open listener first
                }
            }
        }
        //need to mirror tillID
    }//GEN-LAST:event_mirrorMenuItemActionPerformed

    public void openSender(Socket sock, Object message) throws Exception {
        final ObjectOutputStream outbox = new ObjectOutputStream(sock.getOutputStream());
        sender = true;
        outbox.writeObject(message);
    }

    private void top100ByDepartmentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_top100ByDepartmentMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            theDate = Main.selectMonthYear.execute();
            if (theDate == null) {
                Main.operator.setOperator(op);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            month = theDate.get(Calendar.MONTH);
            year = theDate.get(Calendar.YEAR);
            if (month == 0 && year == 1) {//selectMonthYear was escaped
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            if (month == 0) {
                month = 12;
                year -= 1;
            }
            department = Main.selectDepartment.execute();
            if (department == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("month", month);
            parameters.put("year", year);
            parameters.put("kgSymbol", Main.shop.getKgSymbol());
            parameters.put("department", department);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("Top100ByDepartment", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_top100ByDepartmentMenuItemActionPerformed

    private void linkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkMenuItemActionPerformed
        //select a department
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.createNewStock || Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Calendar sWhenCreated = Calendar.getInstance();
            //noOfLines,sDepartment,sSku,sWhenCreated,sStopped,sQuantity,sSupplier
            department = Main.selectDepartment.execute();
            if (department == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Main.linkProducts.execute(department);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
    }//GEN-LAST:event_linkMenuItemActionPerformed

    private void refundsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refundsMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            Calendar end_Date = Main.selectCalendarDate.execute(null, bundle1.getString("To"));
            if (end_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            end_Date.add(Calendar.MONTH, -1);
            Calendar start_Date = Main.selectCalendarDate.execute(end_Date, bundle1.getString("From"));
            if (start_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            java.sql.Date fromDate;
            fromDate = new java.sql.Date(start_Date.getTimeInMillis());
            java.sql.Date toDate;
            toDate = new java.sql.Date(end_Date.getTimeInMillis());
            parameters.put("Start_Date", fromDate);
            parameters.put("End_Date", toDate);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("Refunds", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_refundsMenuItemActionPerformed

    private void zeroPricedSalesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zeroPricedSalesMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            Calendar end_Date = Main.selectCalendarDate.execute(null, bundle1.getString("To"));
            if (end_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            java.sql.Date toDate;
            toDate = new java.sql.Date(end_Date.getTimeInMillis());
            end_Date.add(Calendar.MONTH, -1);
            Calendar start_Date = Main.selectCalendarDate.execute(end_Date, bundle1.getString("From"));
            if (start_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            java.sql.Date fromDate;
            fromDate = new java.sql.Date(start_Date.getTimeInMillis());
            parameters.put("from", fromDate);
            parameters.put("to", toDate);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("ZeroPricedSales", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_zeroPricedSalesMenuItemActionPerformed

    private void catalogueItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_catalogueItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            parameters.put("poundSymbol", Main.shop.poundSymbol);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("Catalogue", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_catalogueItemActionPerformed

private void dropboxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropboxMenuItemActionPerformed
    openURL("http://db.tt/UIS1zPah");//john bird
}//GEN-LAST:event_dropboxMenuItemActionPerformed

private void ownUseByNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownUseByNameActionPerformed
    int op = Main.operator.getOperator();//needed to clear temporary operator
    if (Main.operator.handleAccounts || Main.operator.isOwner()) {
        if (Main.salesScreenFunctions.isSaleComplete()) {
            Main.salesScreenFunctions.setCustomer(SaleType.CUSTOMER.code() * 10000L);
            Main.salesScreenFunctions.clearSale();
        }
        if (Main.salesScreenFunctions.getCustomer() != SaleType.CUSTOMER.code() * 10000l) {
            Audio.play("Ring");
            Main.operator.setOperator(op);
            return;
        }
        Main.customers.execute("OwnUse", true);
        Main.operator.setOperator(op);
    }
}//GEN-LAST:event_ownUseByNameActionPerformed

private void ownUseByNumberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownUseByNumberActionPerformed
    int op = Main.operator.getOperator();//needed to clear temporary operator
    if (Main.operator.handleAccounts || Main.operator.isOwner()) {
        if (Main.salesScreenFunctions.isSaleComplete()) {
            customer = SaleType.CUSTOMER.code() * 10000L;
            Main.salesScreenFunctions.clearSale();
        }
        if (customer != SaleType.CUSTOMER.code() * 10000L) {
            Audio.play("Ring");
            Main.operator.setOperator(op);
            return;
        }
        Main.customers.execute("OwnUse", true);
        Main.operator.setOperator(op);
    }
}//GEN-LAST:event_ownUseByNumberActionPerformed

private void ownUseBalanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownUseBalanceActionPerformed
    int op = Main.operator.getOperator();//needed to clear temporary operator
    if (Main.salesScreenFunctions.getaSale().getSelection() < 0 && (Main.operator.handleAccounts || Main.operator.isOwner())) {
        long customerLocal = 0L;
        if (getCustomer() == SaleType.CUSTOMER.code() * 10000l) {
            //need to identify the customer
            customerLocal = Main.customers.execute("OwnUse", true);
        }
        if (customerLocal == SaleType.CUSTOMER.code() * 10000) {
            //no customer selected
            dataEntry.setText("");
            Main.operator.setOperator(op);
            return;
        } else {
            Main.balance.execute(customerLocal, true);
        }
        Main.operator.setOperator(op);
    } else {
        Audio.play("Ring");
    }
}//GEN-LAST:event_ownUseBalanceActionPerformed

private void ownUseBalancesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownUseBalancesActionPerformed
    if (Main.salesScreenFunctions.getaSale().getSelection() < 0 && (Main.operator.handleAccounts || Main.operator.isOwner())) {
        //show the balances of all customers
        Main.balances.execute(true);
    } else {
        Audio.play("Ring");
    }
}//GEN-LAST:event_ownUseBalancesActionPerformed

private void ownUseNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ownUseNewActionPerformed
    int op = Main.operator.getOperator();//needed to clear temporary operator
    if (Main.operator.handleAccounts || Main.operator.isOwner()) {
        boolean b = Main.customer.execute(-1l, true);
        Main.operator.setOperator(op);
    } else {
        Audio.play("Ring");
    }
}//GEN-LAST:event_ownUseNewActionPerformed

private void creditCustomerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_creditCustomerMenuItemActionPerformed
    if (Main.operator.handleAccounts || Main.operator.isOwnerManager()) {
        if (Main.salesScreenFunctions.getaSale().getSelection() == -1) {//needed as otherwise keypress needed to clear
            Main.salesScreenFunctions.receive("1000035");
            nextShift = true;
        }
    }
}//GEN-LAST:event_creditCustomerMenuItemActionPerformed

    private void newSaleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSaleMenuItemActionPerformed
        // create a new sales screen in a new tab
        //under construction
        //if an existing sale complete, use that one
        if (Main.operator.isSalesPerson()) {
            if (Main.salesScreenFunctions.isSaleComplete()) {
                //use current tab
                Main.salesScreenFunctions.clearResult();
                Audio.play("Beep");
            } else {
                jTabbedPane1.addTab("new", null);
                saleCount = newSale();
            }
        }
    }//GEN-LAST:event_newSaleMenuItemActionPerformed

    private void agencyReconcilliationMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agencyReconcilliationMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            Calendar fromDate = Main.selectCalendarDate.execute(null, bundle1.getString("From"));
            if (fromDate == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            java.util.Date fromUtilDate = new java.util.Date(fromDate.getTimeInMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
            String ss = sdf.format(fromUtilDate);
            parameters.put("fromDate", fromUtilDate);
            Calendar date = Main.selectCalendarDate.execute(null, bundle1.getString("To"));
            if (date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            java.util.Date toUtilDate = new java.util.Date(date.getTimeInMillis());
            String ss2 = sdf.format(toUtilDate);
            parameters.put("toDate", toUtilDate);
            parameters.put("taxCode", "6");
            parameters.put("poundSymbol", Main.shop.poundSymbol);
//            File fc = new File(defaultDirectory + "ProffittCenterReports/Agency.jasper");
            File fc = getJasper("AgencyReport");
            try {
                fileLocation = fc.getAbsolutePath();
                jasperPrint = JasperFillManager.fillReport(fileLocation, parameters, connection);
                JasperViewer.viewReport(jasperPrint, false);
            } catch (JRException ex) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Logger

.getLogger(SalesScreen.class
.getName()).log(Level.SEVERE, null, ex);
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_agencyReconcilliationMenuItemActionPerformed

    private void dailyDepartmentSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dailyDepartmentSalesActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("poundSymbol", Main.shop.poundSymbol);
            department = Main.selectDepartment.execute();
            if (department == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("department", department);
            from = Main.selectDate.execute(null, "From");
            if (from == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            to = (java.sql.Date) from.clone();
            to = Main.selectDate.execute(to, "To");
            if (to == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("poundSymbol", Main.shop.poundSymbol);
            parameters.put("fromDate", from);
            parameters.put("toDate", to);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("DailyDepartmentSales", parameters);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_dailyDepartmentSalesActionPerformed

    private void addToStockMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToStockMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Main.addToStock.execute();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_addToStockMenuItemActionPerformed

    private void purchaseHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_purchaseHistoryActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            product = Main.selectProduct.execute();
            if (product == 0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            sku = Main.selectProduct.getSku();
            parameters.put("sku", sku);
            File fc;
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("purchaseHistory", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_purchaseHistoryActionPerformed

    private void top100BySupplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_top100BySupplierActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            theDate = Main.selectMonthYear.execute();
            if (theDate == null) {
                Main.operator.setOperator(op);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            month = theDate.get(Calendar.MONTH);
            year = theDate.get(Calendar.YEAR);
            if (month == 0 && year == 1) {//selectMonthYear was escaped
                Main.operator.setOperator(op);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            if (month == 0) {
                month = 12;
                year -= 1;
            }
            supplier = Main.selectSupplier.execute();
            if (supplier == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("month", month);
            parameters.put("year", year);
            parameters.put("kgSymbol", Main.shop.getKgSymbol());
            parameters.put("supplier", supplier);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("Top100BySupplier", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_top100BySupplierActionPerformed

    private void panelFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_panelFocusGained
        hidden.requestFocus();
    }//GEN-LAST:event_panelFocusGained

    private void resultsTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_resultsTableFocusGained
        hidden.requestFocus();
    }//GEN-LAST:event_resultsTableFocusGained

    private void agencyReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agencyReportActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            from = Main.selectDate.execute(null, "From");
            if (from == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            to = (java.sql.Date) from.clone();
            to = Main.selectDate.execute(to, "To");
            if (to == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("poundSymbol", Main.shop.poundSymbol);
            parameters.put("fromDate", from);
            parameters.put("toDate", to);
            parameters.put("agencyTaxID", 6);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("AgencyReport", parameters);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_agencyReportActionPerformed

    private void lastSaleReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastSaleReportActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            parameters.put("Normal", (int) SaleType.NORMAL.value());
            parameters.put("Charge", (int) SaleType.CHARGED.value());
            department = Main.selectDepartment.execute();
            if (department < 0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("department", department);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("LastSaleReport", parameters, connection);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_lastSaleReportActionPerformed

    private void layawayMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layawayMenuItemActionPerformed
        // Save a partial sale and print out result on receipt printer only
        //not for invoice printing
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.operator.isOwnerManager() && aSale.size() != 0 && !Main.hardware.isInvoicePrinter()) {
            Main.salesScreenFunctions.layaway();
        }
    }//GEN-LAST:event_layawayMenuItemActionPerformed

    private void discountedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_discountedActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            PreparedStatement operator;
            try {
                operator = Main.getConnection().prepareStatement(
                        SQL.salesByDiscounted);
                //"ORDER BY Totals");
                Main.salesBy.execute(bundle1.getString("ByDiscounted"), operator);
            } catch (SQLException ex) {
                Main.closeConnection();
                Main.logger.log(Level.SEVERE, "SalesScreen.jOperatorActionPerformed ", "SQLException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen.MenuOperator", JOptionPane.ERROR_MESSAGE);
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_discountedActionPerformed

    private void pricedOverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pricedOverActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            PreparedStatement operator;
            try {
                operator = Main.getConnection().prepareStatement(
                        SQL.salesByPricedOver);
                //"ORDER BY Totals");
                Main.salesBy.execute(bundle1.getString("ByPricedOver"), operator);
            } catch (SQLException ex) {
                Main.closeConnection();
                Main.logger.log(Level.SEVERE, "SalesScreen.jOperatorActionPerformed ", "SQLException: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "SQLxception: " + ex, "SalesScreen.MenuOperator", JOptionPane.ERROR_MESSAGE);
            }
            Main.operator.setOperator(op);
        } else {
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_pricedOverActionPerformed

    private void dailyDepartmentProductSalesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dailyDepartmentProductSalesActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("poundSymbol", Main.shop.poundSymbol);
            aDate = Main.selectDate.execute(null, "Date");
            if (aDate == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("date", aDate);
            department = Main.selectDepartment.execute();
            if (department == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("department", department);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("dailyDepartmentProductSales", parameters);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_dailyDepartmentProductSalesActionPerformed

    private void supplierStockListMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_supplierStockListMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            supplier = Main.selectSupplier.execute();
            if (supplier < 0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            parameters.put("supplier", supplier);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("supplierStockList", parameters);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_supplierStockListMenuItemActionPerformed

    private void stoppedProductsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stoppedProductsMenuItemActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("StoppedProducts", parameters);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_stoppedProductsMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("poundSymbol", Main.shop.poundSymbol);
            Calendar start_Date = Calendar.getInstance();
            start_Date.add(Calendar.MONTH, -1);
            start_Date = Main.selectCalendarDate.execute(start_Date, bundle1.getString("From"));
            if (start_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Calendar end_Date = Main.selectCalendarDate.execute(start_Date, bundle1.getString("To"));
            if (end_Date == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Date start = start_Date.getTime();
            Date end = end_Date.getTime();
            parameters.put("Start_Date", start);
            parameters.put("End_Date", end);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("Cashups", parameters);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void salesByDepartmentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_salesByDepartmentsActionPerformed
        int op = Main.operator.getOperator();//needed to clear temporary operator
        if (Main.salesScreenFunctions.isSaleComplete() && Main.operator.isOwnerManager()) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("REPORT_CONNECTION", connection);
            parameters.put("poundSymbol", Main.shop.poundSymbol);
            Calendar todaysDate;
            todaysDate = Calendar.getInstance();
            todaysDate.add(Calendar.MONTH, -1);
            todaysDate = Main.selectCalendarDate.execute(todaysDate, bundle1.getString("Date"));
            if (todaysDate == null) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                Main.operator.setOperator(op);
                return;
            }
            Date start = todaysDate.getTime();
            parameters.put("aDate", start);
            ReportRunner reportRunner = new ReportRunner();
            reportRunner.setup("Sales by departments", parameters);
            reportThread = new Thread(reportRunner);
            reportThread.start();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            Main.operator.setOperator(op);
        }
    }//GEN-LAST:event_salesByDepartmentsActionPerformed

    private static String loadStream(InputStream in) throws IOException {
        int ptr;
        in = new BufferedInputStream(in);
        StringBuilder sb = new StringBuilder();
        while ((ptr = in.read()) != -1) {
            sb.append((char) ptr);
        }
        return sb.toString();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem RunSqlMenuItem;
    private javax.swing.JMenuItem addToStockMenuItem;
    private javax.swing.JMenuItem agencyReconcilliationMenuItem;
    private javax.swing.JMenuItem agencyReport;
    private javax.swing.JMenuItem cashUp;
    private javax.swing.JMenuItem catalogueItem;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem createMenuItem2;
    private javax.swing.JMenuItem creditCustomerMenuItem;
    private javax.swing.JMenuItem customerSalesReportMenuItem;
    private javax.swing.JMenuItem dailyDepartmentProductSales;
    private javax.swing.JMenuItem dailyDepartmentSales;
    private javax.swing.JTextField dataEntry;
    private javax.swing.JLabel dataLabel;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JMenuItem deliveriesMenuItem;
    private javax.swing.JMenuItem deliveryAddressesItem;
    private javax.swing.JMenuItem discounted;
    private javax.swing.JMenuItem dropboxMenuItem;
    private javax.swing.JTextField hidden;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JMenuItem jAgency;
    private javax.swing.JMenu jAll;
    private javax.swing.JMenuItem jAll10;
    private javax.swing.JMenuItem jAllAll;
    private javax.swing.JMenuItem jAllDefault;
    private javax.swing.JMenu jAllLessThan10;
    private javax.swing.JMenuItem jBalance;
    private javax.swing.JMenuItem jBalances;
    private javax.swing.JButton jButton0;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JButton jButtonAt;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonEnter;
    private javax.swing.JButton jButtonMinus;
    private javax.swing.JButton jButtonPerCent;
    private javax.swing.JButton jButtonTimes;
    private javax.swing.JMenuItem jByName;
    private javax.swing.JMenuItem jByNumber;
    private javax.swing.JMenuItem jCash;
    private javax.swing.JMenuItem jCashInDrawer;
    private javax.swing.JMenuItem jCharge;
    private javax.swing.JMenuItem jCharges;
    private javax.swing.JMenu jCharts;
    private javax.swing.JMenuItem jCheque;
    private javax.swing.JMenuItem jCoupon;
    private javax.swing.JMenu jCustomersMenu;
    private javax.swing.JMenuItem jDLosses;
    private javax.swing.JMenuItem jDWastes;
    private javax.swing.JMenuItem jDebit;
    private javax.swing.JMenuItem jDefault10;
    private javax.swing.JMenuItem jDelivery;
    private javax.swing.JMenu jDepartment;
    private javax.swing.JMenuItem jDepartment1;
    private javax.swing.JMenuItem jDepartmentAll;
    private javax.swing.JMenuItem jDepartmentDefault;
    private javax.swing.JMenu jDepartmentMenu;
    private javax.swing.JMenuItem jDepartments;
    private javax.swing.JMenuItem jEncodedProducts;
    private javax.swing.JMenu jFileMenu;
    private javax.swing.JMenu jHelpMenu;
    private javax.swing.JMenuItem jHotkeys;
    private javax.swing.JMenuItem jLosses;
    private javax.swing.JMenuItem jLosses2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem12;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem14;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JMenuItem jMinimumStock;
    private javax.swing.JMenuItem jNegativeAll;
    private javax.swing.JMenuItem jNegativeDefault;
    private javax.swing.JMenu jNegatives;
    private javax.swing.JMenuItem jNewCustomer;
    private javax.swing.JMenuItem jOffers;
    private javax.swing.JMenu jOffersMenu;
    private javax.swing.JMenuItem jOperator;
    private javax.swing.JMenuItem jOwnUse;
    private javax.swing.JMenuItem jPaidIn;
    private javax.swing.JMenuItem jPaidOut;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JMenu jPaymentMenu;
    private javax.swing.JMenuItem jProduct;
    private javax.swing.JMenuItem jQuantity;
    private javax.swing.JMenuItem jReceipt;
    private javax.swing.JMenuItem jReceived;
    private javax.swing.JMenuItem jReceivedOnAccount;
    private javax.swing.JMenuItem jReconcile;
    private javax.swing.JMenuItem jRefund;
    private javax.swing.JMenuItem jReturnableWaste;
    private javax.swing.JMenuItem jReturns;
    private javax.swing.JMenuItem jReturns1;
    private javax.swing.JMenuItem jReturnsByDepartment;
    private javax.swing.JMenuItem jSales;
    private javax.swing.JMenuItem jSalesByDepartment;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuItem jStartingStock;
    private javax.swing.JMenu jStockByValue;
    private javax.swing.JMenu jStockMenu;
    private javax.swing.JMenu jStocktake;
    private javax.swing.JMenu jStop;
    private javax.swing.JMenuItem jStopAll;
    private javax.swing.JMenuItem jStopDefault;
    private javax.swing.JMenu jSupplier;
    private javax.swing.JMenuItem jSupplierAll;
    private javax.swing.JMenuItem jSupplierDefault;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JMenu jTablesMenu;
    private javax.swing.JMenuItem jTax;
    private javax.swing.JMenuItem jTaxes;
    private javax.swing.JMenuItem jTempStock;
    private javax.swing.JMenuItem jValue;
    private javax.swing.JMenuItem jWaste;
    private javax.swing.JMenuItem jWastes;
    private javax.swing.JMenuItem jWastes1;
    private javax.swing.JMenuItem lastSaleReport;
    private javax.swing.JMenuItem layawayMenuItem;
    private javax.swing.JMenuItem linkMenuItem;
    private javax.swing.JMenuItem lockMenuItem;
    private javax.swing.JMenuItem marginsByDepartmentMenuItem;
    private javax.swing.JMenuItem messagesMenuItem;
    private javax.swing.JMenuItem mirrorMenuItem;
    private javax.swing.JMenuItem monthlySalesByDepartmentMenuItem;
    private javax.swing.JMenuItem monthlySalesByProduct;
    private javax.swing.JMenuItem newSaleMenuItem;
    private javax.swing.JMenuItem noSaleMenuItem;
    private javax.swing.JMenuItem operators1;
    private javax.swing.JMenuItem orderMenuItem;
    private javax.swing.JMenuItem ordersCreateMenuItem;
    private javax.swing.JMenu ordersMenu;
    private javax.swing.JMenuItem ordersViewMenuItem;
    private javax.swing.JMenuItem ownUseBalance;
    private javax.swing.JMenuItem ownUseBalances;
    private javax.swing.JMenuItem ownUseByName;
    private javax.swing.JMenuItem ownUseByNumber;
    private javax.swing.JMenuItem ownUseMenuItem;
    private javax.swing.JMenuItem ownUseNew;
    private javax.swing.JMenuItem packSuppliers1;
    private javax.swing.JMenuItem packs1;
    private javax.swing.JMenuItem paidOuts1;
    private javax.swing.JMenuItem paidOutsMenuItem;
    private javax.swing.JPanel panel;
    private javax.swing.JMenuItem pricedOver;
    private javax.swing.JMenuItem printCodesMenuItem;
    private javax.swing.JMenuItem product1;
    private javax.swing.JMenuItem productPerformancesMenuItem;
    private javax.swing.JMenuItem products1;
    private javax.swing.JMenuItem purchaseHistory;
    private javax.swing.JMenuItem reconciledCashups;
    private javax.swing.JMenuItem refundsMenuItem;
    private javax.swing.JMenu reportMenuItem;
    private javax.swing.JMenuItem resetWindowsItem;
    private javax.swing.JTable resultsTable;
    private javax.swing.JMenuItem sale1;
    private javax.swing.JTable saleTable;
    private javax.swing.JMenuItem sales;
    private javax.swing.JMenuItem salesByDepartments;
    private javax.swing.JMenu salesByMenu;
    private javax.swing.JMenuItem salesTargetMenuItem;
    private javax.swing.JMenuItem settingsMenuItem;
    private javax.swing.JMenuItem showMenuItem1;
    private javax.swing.JMenuItem skus1;
    private javax.swing.JMenuItem stoppedProductsMenuItem;
    private javax.swing.JMenuItem supplierStockListMenuItem;
    private javax.swing.JMenuItem suppliers1;
    private javax.swing.JMenuItem takings;
    private javax.swing.JMenuItem testMenuItem;
    private javax.swing.JMenuItem top100ByDepartmentMenuItem;
    private javax.swing.JMenuItem top100BySupplier;
    private javax.swing.JMenuItem top100MenuItem;
    private javax.swing.JMenuItem tracksMenu;
    private javax.swing.JMenuItem weeklySalesByDepartmentMenuItem;
    private javax.swing.JMenuItem weeklySalesByProduct;
    private javax.swing.JMenuItem workloadMenuItem;
    private javax.swing.JMenuItem zeroPricedSalesMenuItem;
    // End of variables declaration//GEN-END:variables
}
