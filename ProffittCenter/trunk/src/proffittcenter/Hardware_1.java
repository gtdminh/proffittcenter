/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
 * Hardware.java
 *
 * Created on 21-Sep-2010, 09:08:29
 */
package proffittcenter;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Dave
 */
public final class Hardware_1 extends javax.swing.JPanel {

    Preferences root = Preferences.userNodeForPackage(getClass());
    static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Settings");
    private final Integer baudRrate = root.getInt("baudRrate", 9600);
    private String receiptPort = root.get("ReceiptPort", "");
    private String polePort = root.get("PolePort", "");
    private Enumeration thePorts;
    private String aPort;
    private CommPort thePort;
    private Integer extraLines = Integer.parseInt(root.get("ExtraLines", "6"));
    private final int cor = Integer.parseInt(root.get("CharsOnReceipt", "33"));
    private Integer charsOnReceipt = cor < 20 ? 20 : cor;
    private int shelfEdgeLabelHeight;
    private boolean soundOn = root.getBoolean("SoundOn", true);
    public boolean touch = root.getBoolean("Touch", false);
    private boolean internetEnabled;
    private boolean invoicePrinter = root.getBoolean("invoicePrinter", false);
    private final boolean jasperInMyDocuments = true;
    private String barcodePrinter = root.get("BarcodePrinter", "");
    public String myLanguage;
    public final String myCountry;
    public String charset = root.get("Charset", "Cp850");
    private byte code;
    private boolean even = true;
    private final byte[] CODETABLE0 = {0x1b, 't', 0x00};
    private final byte[] CODETABLE1 = {0x1b, 't', 0x01};
    private final byte[] CODETABLE2 = {0x1b, 't', 0x02};
    private final byte[] CODETABLE3 = {0x1b, 't', 0x03};
    private final byte[] CODETABLE4 = {0x1b, 't', 0x04};
    private final byte[] CODETABLE5 = {0x1b, 't', 0x05};
    private final byte[] CODETABLE6 = {0x1b, 't', 0x06};
    private final byte[] CODETABLE7 = {0x1b, 't', 0x07};
    private final byte[] CODETABLE0C = {0x1b, 't', 0x0C};
    private final byte[] CODETABLE10 = {0x1b, 't', 0x10};
    private final byte[] CODETABLE13 = {0x1b, 't', 0x13};
    private final byte[] FONTSET0 = {0x1b, 'R', 0x00};
    private final byte[] FONTSET1 = {0x1b, 'R', 0x01};
    private final byte[] FONTSET2 = {0x1b, 'R', 0x02};
    private final byte[] FONTSET3 = {0x1b, 'R', 0x03};
    private final byte[] FONTSET4 = {0x1b, 'R', 0x04};
    private final byte[] FONTSET5 = {0x1b, 'R', 0x05};
    private final byte[] FONTSET6 = {0x1b, 'R', 0x06};
    private final byte[] FONTSET7 = {0x1b, 'R', 0x07};
    private final byte[] FONTSET8 = {0x1b, 'R', 0x08};
    private final byte[] FONTSET9 = {0x1b, 'R', 0x09};
    private final byte[] FONTSET10 = {0x1b, 'R', 0x0A};
    private final byte[] FONTSET11 = {0x1b, 'R', 0x0B};
    private final byte[] FONTSET12 = {0x1b, 'R', 0x0C};
    private final byte[] FONTSET13 = {0x1b, 'R', 0x0D};
    private final byte[] FONTSET14 = {0x1b, 'R', 0x0E};
    private byte[] NONE = {};
    public int codeTableIndex = root.getInt("codeTableIndex", 0);
    public int internationalFontSetIndex = root.getInt("internationalFontSetIndex", 0);
    private byte[] codeTable;
    private byte[] fontSet;
    private String oldPolePort;
    private String oldReceiptPort;
    private String heading = root.get("heading", "T  Qty Product                    £ ");
    private String endString = "                                                       ";
    private final byte[] RESET = {0x1b, 0x3d, 0x01, 0x1b, 0x40};//Initialize printer
    JFrame parent = new JFrame();

    /**
     * Creates new form Hardware
     */
    public Hardware_1() {
        initComponents();
        Locale l = getLocale();
        myLanguage = l.getISO3Language();
        myCountry = l.getCountry();
        getCodeTable();
        getFontSet();
    }

    public final byte[] getCodeTable() {
        switch (codeTableIndex) {
            case 0:
                codeTable = CODETABLE0;
                break;
            case 1:
                codeTable = CODETABLE1;
                break;
            case 2:
                codeTable = CODETABLE2;
                break;
            case 3:
                codeTable = CODETABLE3;
                break;
            case 4:
                codeTable = CODETABLE4;
                break;
            case 5:
                codeTable = CODETABLE5;
                break;
            case 6:
                codeTable = CODETABLE6;
                break;
            case 7:
                codeTable = CODETABLE7;
                break;
            case 8:
                codeTable = CODETABLE13;
                break;
            case 9:
                codeTable = CODETABLE10;
                break;
            case 10:
                codeTable = CODETABLE0C;
                break;
            case 11:
                codeTable = NONE;
                break;
        }
        return codeTable;
    }

    public byte[] getFontSet() {
        switch (internationalFontSetIndex) {
            case 0:
                fontSet = FONTSET0;
                break;
            case 1:
                fontSet = FONTSET1;
                break;
            case 2:
                fontSet = FONTSET2;
                break;
            case 3:
                fontSet = FONTSET3;
                break;
            case 4:
                fontSet = FONTSET4;
                break;
            case 5:
                fontSet = FONTSET5;
                break;
            case 6:
                fontSet = FONTSET6;
                break;
            case 7:
                fontSet = FONTSET7;
                break;
            case 8:
                fontSet = FONTSET8;
                break;
            case 9:
                fontSet = FONTSET9;
                break;
            case 10:
                fontSet = FONTSET10;
                break;
            case 11:
                fontSet = FONTSET11;
                break;
            case 12:
                fontSet = FONTSET12;
                break;
            case 13:
                fontSet = FONTSET13;
                break;
            case 14:
                fontSet = FONTSET14;
                break;
            case 15:
                fontSet = NONE;
                break;
        }
        return fontSet;
    }

    /**
     * This method is called from within the constructor to initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jExtraLines = new javax.swing.JSpinner();
        jCharsOnReceipt = new javax.swing.JSpinner();
        receiptCombo = new javax.swing.JComboBox();
        poleCombo = new javax.swing.JComboBox();
        barcodeCombo = new javax.swing.JComboBox();
        jShelfEdgeLabelHeight = new javax.swing.JSpinner();
        receiptTest = new javax.swing.JButton();
        poleTest = new javax.swing.JButton();
        jLabel34 = new javax.swing.JLabel();
        touchCheckBox = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        soundCheckBox = new javax.swing.JCheckBox();
        jLabel29 = new javax.swing.JLabel();
        invoiceCheckBox = new javax.swing.JCheckBox();
        codeTableComboBox = new javax.swing.JComboBox();
        internationalFontSetComboBox = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        headingTextField = new javax.swing.JTextField();
        speedTestButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        baudRateLabel = new javax.swing.JLabel();
        baudRateCombo = new javax.swing.JComboBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/Settings"); // NOI18N
        setName(bundle.getString("Hardware.name")); // NOI18N

        jExtraLines.setName("jExtraLines"); // NOI18N

        jCharsOnReceipt.setName("jCharsOnReceipt"); // NOI18N
        jCharsOnReceipt.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCharsOnReceiptStateChanged(evt);
            }
        });
        jCharsOnReceipt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jCharsOnReceiptFocusLost(evt);
            }
        });

        receiptCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        receiptCombo.setName("receiptCombo"); // NOI18N

        poleCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        poleCombo.setName("poleCombo"); // NOI18N

        barcodeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        barcodeCombo.setName("barcodeCombo"); // NOI18N

        jShelfEdgeLabelHeight.setName("jShelfEdgeLabelHeight"); // NOI18N

        receiptTest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Test.png"))); // NOI18N
        receiptTest.setToolTipText(bundle.getString("Test")); // NOI18N
        receiptTest.setContentAreaFilled(false);
        receiptTest.setName("receiptTest"); // NOI18N
        receiptTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiptTestActionPerformed(evt);
            }
        });

        poleTest.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Test.png"))); // NOI18N
        poleTest.setToolTipText(bundle.getString("TestPole")); // NOI18N
        poleTest.setContentAreaFilled(false);
        poleTest.setName("poleTest"); // NOI18N
        poleTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                poleTestActionPerformed(evt);
            }
        });

        jLabel34.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel34.setText(bundle.getString("Settings.Server.jLabel34.text")); // NOI18N
        jLabel34.setName("jLabel34"); // NOI18N

        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("proffittcenter/resource/CashupReconciliation"); // NOI18N
        touchCheckBox.setText(bundle1.getString("Settings.touchCheckBox.text")); // NOI18N
        touchCheckBox.setName("touchCheckBox"); // NOI18N

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText(bundle.getString("Settings.Server.jLabel12.text_1")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText(bundle.getString("Settings.Server.jLabel13.text_1")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel14.setText(bundle.getString("Settings.Server.jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText(bundle.getString("Settings.Server.jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel24.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel24.setText(bundle.getString("Settings.Server.jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText(bundle1.getString("Settings.Server.jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel30.setText(bundle.getString("Settings.Server.jLabel30.text")); // NOI18N
        jLabel30.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel30.setName("jLabel30"); // NOI18N

        soundCheckBox.setName("soundCheckBox"); // NOI18N

        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel29.setText(bundle.getString("Settings.Server.jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N

        invoiceCheckBox.setName("invoiceCheckBox"); // NOI18N

        codeTableComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Page 0 (pc437:USA,standard Europe)", "Page 1 (Katakana for Japan)", "Page 2 (PC858: multilanguage)", "Page 3 (PC860: Portugese)", "Page 4 (PC863: Canadian-French)", "Page 5 (PC865: Nordic)", "Page 6 (RUSSIA)", "Page 7 (SLAVONIC)", "Page 19 (PC858 EURO)", "Page 9 (WPC1252)", "Page 12 (Greek)", " " }));
        codeTableComboBox.setName("codeTableComboBox"); // NOI18N
        codeTableComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codeTableComboBoxActionPerformed(evt);
            }
        });

        internationalFontSetComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "USA", "FRANCE", "GERMANY", "UK", "DENMARK I", "SWEDEN", "ITALY", "SPAIN", "JAPAN", "NORWAY", "DENMARK II", "SLAVONIC", "RUSSIA", "PORTUGESE", "GREEK", " " }));
        internationalFontSetComboBox.setName("internationalFontSetComboBox"); // NOI18N
        internationalFontSetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                internationalFontSetComboBoxActionPerformed(evt);
            }
        });

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText(bundle.getString("Settings.Server.jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText(bundle.getString("Settings.Server.jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText(bundle.getString("Hardware.jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jLabel1.setText("12345678901234567890123456789012345678901234567890");
        jLabel1.setName("jLabel1"); // NOI18N

        headingTextField.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        headingTextField.setText(bundle.getString("Hardware.headingTextField.text")); // NOI18N
        headingTextField.setName("headingTextField"); // NOI18N
        headingTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                headingTextFieldKeyReleased(evt);
            }
        });

        speedTestButton.setText(bundle.getString("Hardware.speedTestButton.text")); // NOI18N
        speedTestButton.setName("speedTestButton"); // NOI18N
        speedTestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                speedTestButtonActionPerformed(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(255, 0, 51));
        jLabel2.setText(bundle.getString("Hardware.jLabel2.text")); // NOI18N
        jLabel2.setName(bundle.getString("Hardware.jLabel2.name")); // NOI18N

        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText(bundle.getString("Hardware.jLabel3.text")); // NOI18N
        jLabel3.setName(bundle.getString("Hardware.jLabel3.name")); // NOI18N

        baudRateLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        baudRateLabel.setText(bundle.getString("Hardware.baudRateLabel.text")); // NOI18N
        baudRateLabel.setName("baudRateLabel"); // NOI18N

        baudRateCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        baudRateCombo.setName("baudRateCombo"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(baudRateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCharsOnReceipt)
                    .addComponent(jShelfEdgeLabelHeight)
                    .addComponent(jExtraLines)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(codeTableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(internationalFontSetComboBox, 0, 194, Short.MAX_VALUE)
                            .addComponent(baudRateCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(receiptCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(poleCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(barcodeCombo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(headingTextField)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(poleTest, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(receiptTest, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(speedTestButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel34, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(touchCheckBox)
                                    .addComponent(soundCheckBox)
                                    .addComponent(invoiceCheckBox))
                                .addGap(166, 166, 166)))
                        .addContainerGap())))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {invoiceCheckBox, soundCheckBox, touchCheckBox});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {codeTableComboBox, internationalFontSetComboBox});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {baudRateLabel, jLabel12, jLabel13, jLabel14, jLabel20, jLabel21, jLabel23, jLabel24, jLabel25});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jExtraLines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15)
                            .addComponent(headingTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jCharsOnReceipt, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel13))))
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(baudRateLabel)
                    .addComponent(baudRateCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(receiptCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel14)
                        .addComponent(jLabel2))
                    .addComponent(receiptTest, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(speedTestButton)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(poleTest, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel34)
                                .addComponent(touchCheckBox))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(soundCheckBox)
                                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel29)
                                .addComponent(invoiceCheckBox)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(poleCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel21))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(barcodeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel24))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jShelfEdgeLabelHeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel20))
                            .addGap(7, 7, 7)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(codeTableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel23))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(internationalFontSetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel25)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {invoiceCheckBox, jCharsOnReceipt, jExtraLines, jLabel12, jLabel29, jLabel30, jLabel34, soundCheckBox, touchCheckBox});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {receiptCombo, receiptTest});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {poleCombo, poleTest});

    }// </editor-fold>//GEN-END:initComponents

    private void receiptTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiptTestActionPerformed
        oldPolePort = "";
        if (Serial.isSame()) {
            oldPolePort = polePort;
            Serial.POLEPORT.closePort();
        }
        int oldcodeTableIndex = codeTableIndex;
        int oldinternationalFontSetIndex = internationalFontSetIndex;
        if (receiptPort != null && !receiptPort.equals("")) {
            oldReceiptPort = receiptPort;
            Serial.RECEIPTPORT.closePort();
        }
        receiptPort = (String) receiptCombo.getSelectedItem();
        Serial.RECEIPTPORT.openPort("ReceiptPort");
        codeTableIndex = codeTableComboBox.getSelectedIndex();
        internationalFontSetIndex = internationalFontSetComboBox.getSelectedIndex();
        Main.receiptPrinter.startReceipt();
        Main.receiptPrinter.printLine(bundle.getString("Working"));
        Main.receiptPrinter.printLine("Dollar $ Pound £ Euro €");
        Main.receiptPrinter.printLine("Use line below to count '"
                + bundle.getString("Settings.Server.jLabel13.text_1") + "'");
        String s = "012345678901234567890123456789012345678901234567890123456789";
        Main.receiptPrinter.printLine(s);
        Main.receiptPrinter.endReceipt();
        Main.receiptPrinter.cashDrawerOpen();
        Serial.RECEIPTPORT.closePort();
        setReceiptPort(oldReceiptPort);
        Serial.RECEIPTPORT.openPort("ReceiptPort");
        if (!oldPolePort.isEmpty()) {
            setPolePort(oldPolePort);
            Serial.POLEPORT.openPort("PolePort");
        }
        codeTableIndex = oldcodeTableIndex;//revert back
        internationalFontSetIndex = oldinternationalFontSetIndex;//revert back
}//GEN-LAST:event_receiptTestActionPerformed

    private void poleTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_poleTestActionPerformed
        if (polePort != null && !polePort.equals("")) {
            oldPolePort = polePort;
        }
        int oldcodeTableIndex = codeTableIndex;
        int oldinternationalFontSetIndex = internationalFontSetIndex;
        Serial.POLEPORT.closePort();
        polePort = (String) poleCombo.getSelectedItem();
        if (polePort.isEmpty()) {
            even = true;
            return;
        }
        Serial.POLEPORT.openPort("PolePort");
        codeTableIndex = codeTableComboBox.getSelectedIndex();
        internationalFontSetIndex = internationalFontSetComboBox.getSelectedIndex();
        if (even) {
            Main.pole.execute(bundle.getString("Working"), "Dollar$Pound£Euro€");
        } else {
            Main.pole.execute(bundle.getString("StillWorking"), "Dollar$Pound£Euro€");
        }
        even = !even;
        Serial.POLEPORT.closePort();
        setPolePort(oldPolePort);
        Serial.POLEPORT.openPort("PolePort");
        codeTableIndex = oldcodeTableIndex;//revert back
        internationalFontSetIndex = oldinternationalFontSetIndex;//revert back
}//GEN-LAST:event_poleTestActionPerformed

    private void codeTableComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codeTableComboBoxActionPerformed
        codeTableIndex = codeTableComboBox.getSelectedIndex();
        getCodeTable();
    }//GEN-LAST:event_codeTableComboBoxActionPerformed

    private void internationalFontSetComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_internationalFontSetComboBoxActionPerformed
        internationalFontSetIndex = internationalFontSetComboBox.getSelectedIndex();
        getFontSet();
    }//GEN-LAST:event_internationalFontSetComboBoxActionPerformed

    private void jCharsOnReceiptFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jCharsOnReceiptFocusLost

    }//GEN-LAST:event_jCharsOnReceiptFocusLost

    private void headingTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_headingTextFieldKeyReleased
        int cp = headingTextField.getCaretPosition();
        charsOnReceipt = (Integer) jCharsOnReceipt.getValue();
        String s = headingTextField.getText() + endString;
        String sh = s.substring(0, charsOnReceipt + 1);
        headingTextField.setText(sh);
        endString = s.substring(charsOnReceipt + 1, s.length());
        if (cp <= charsOnReceipt) {
            headingTextField.setCaretPosition(cp);
        }
    }//GEN-LAST:event_headingTextFieldKeyReleased

    private void jCharsOnReceiptStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCharsOnReceiptStateChanged
        charsOnReceipt = (Integer) jCharsOnReceipt.getValue();
        String s = headingTextField.getText() + endString;
        String sh = s.substring(0, charsOnReceipt + 1);
        headingTextField.setText(sh);
        endString = s.substring(charsOnReceipt + 1, s.length());
    }//GEN-LAST:event_jCharsOnReceiptStateChanged

    private void speedTestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_speedTestButtonActionPerformed
        oldPolePort = "";
        if (Serial.isSame()) {
            oldPolePort = polePort;
            Serial.POLEPORT.closePort();
        }
        int oldcodeTableIndex = codeTableIndex;
        int oldinternationalFontSetIndex = internationalFontSetIndex;
        if (receiptPort != null && !receiptPort.equals("")) {
            oldReceiptPort = receiptPort;
            Serial.RECEIPTPORT.closePort();
        }
        receiptPort = (String) receiptCombo.getSelectedItem();
        Serial.RECEIPTPORT.openPort("ReceiptPort");
        codeTableIndex = codeTableComboBox.getSelectedIndex();
        internationalFontSetIndex = internationalFontSetComboBox.getSelectedIndex();
        Main.receiptPrinter.startReceipt();
        String s = "01234567890123456789\n";
        byte[] line = new byte[s.length()];
        try {
            line = s.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Hardware_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < 25; i++) {
            Serial.RECEIPTPORT.write(RESET);
            Serial.RECEIPTPORT.write(line);
        }
        Main.receiptPrinter.endReceipt();
        Serial.RECEIPTPORT.closePort();
        setReceiptPort(oldPolePort);
        Serial.RECEIPTPORT.openPort("ReceiptPort");
        if (!oldPolePort.isEmpty()) {
            setPolePort(oldPolePort);
            Serial.POLEPORT.openPort("PolePort");
        }
        codeTableIndex = oldcodeTableIndex;//revert back
        internationalFontSetIndex = oldinternationalFontSetIndex;//revert back
    }//GEN-LAST:event_speedTestButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox barcodeCombo;
    private javax.swing.JComboBox baudRateCombo;
    private javax.swing.JLabel baudRateLabel;
    private javax.swing.JComboBox codeTableComboBox;
    private javax.swing.JTextField headingTextField;
    private javax.swing.JComboBox internationalFontSetComboBox;
    private javax.swing.JCheckBox invoiceCheckBox;
    private javax.swing.JSpinner jCharsOnReceipt;
    private javax.swing.JSpinner jExtraLines;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JSpinner jShelfEdgeLabelHeight;
    private javax.swing.JComboBox poleCombo;
    private javax.swing.JButton poleTest;
    private javax.swing.JComboBox receiptCombo;
    private javax.swing.JButton receiptTest;
    private javax.swing.JCheckBox soundCheckBox;
    private javax.swing.JButton speedTestButton;
    private javax.swing.JCheckBox touchCheckBox;
    // End of variables declaration//GEN-END:variables

    public void setReceiptPort(String receiptPort) {
        this.receiptPort = receiptPort;
        if (receiptPort != null) {
            root.put("ReceiptPort", receiptPort);
        }
    }

    public String getReceiptPort() {
        return receiptPort;
    }

    /**
     * @param polePort the polePort to set
     */
    public void setPolePort(String polePort) {
        this.polePort = polePort;
        root.put("PolePort", polePort);
    }

    /**
     * @return the polePort
     */
    public String getPolePort() {
        return polePort;
    }

    void execute() {
        try {
            Logger.getLogger(Hardware_1.class.getName()).log(Level.SEVERE, "\ngetPortIdentifiers defore", "");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, e);
        }
        try {
            thePorts = CommPortIdentifier.getPortIdentifiers();//never completed     
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, e);
        }
   
        Logger.getLogger(Hardware_1.class.getName()).log(Level.SEVERE, "\ngetPortIdentifiers after", "");
        baudRateCombo.removeAllItems();
        baudRateCombo.addItem(1200);
        baudRateCombo.addItem(2400);
        baudRateCombo.addItem(4800);
        baudRateCombo.addItem(9600);

        baudRateCombo.addItem(19200);
        baudRateCombo.addItem(38400);
        baudRateCombo.addItem(57600);
        baudRateCombo.addItem(115200);
        baudRateCombo.setSelectedItem(getBaudRrate());
        receiptCombo.removeAllItems();
        poleCombo.removeAllItems();
        barcodeCombo.removeAllItems();
        receiptCombo.addItem("");
        poleCombo.addItem("");
        //JOptionPane.showMessageDialog(this, "This far.");
        if (thePorts != null) {
            while (thePorts.hasMoreElements()) {
                CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();
                switch (com.getPortType()) {
                    case CommPortIdentifier.PORT_SERIAL:
                        try {
                            
                            aPort = com.getName();
                            if (com.isCurrentlyOwned()) {
                                break;
                            }
                            thePort = com.open("CommUtil", 50);
                            thePort.close();
                            receiptCombo.addItem(com.getName());
                            poleCombo.addItem(com.getName());
                        } catch (PortInUseException e) {
                            
                            if (com.getName().equalsIgnoreCase(getPolePort()) && !com.getName().equalsIgnoreCase(receiptPort)) {
                                receiptCombo.addItem(com.getName());
                                poleCombo.addItem(com.getName());
                            } else if (com.getName().equalsIgnoreCase(getReceiptPort()) && !com.getName().equalsIgnoreCase(polePort)) {
                                poleCombo.addItem(com.getName());
                                receiptCombo.addItem(com.getName());
                            } else if (com.getName().equalsIgnoreCase(getReceiptPort()) && com.getName().equalsIgnoreCase(getPolePort())) {
                                receiptCombo.addItem(com.getName());
                                poleCombo.addItem(com.getName());
                            }
                        } catch (Exception e) {
                            Main.logger.log(Level.SEVERE, null, "Failed to open port " + com.getName());
                            System.err.println("Failed to open port " + com.getName());
                        }
                }
            }

        }
        receiptCombo.setSelectedItem(getReceiptPort());
        poleCombo.setSelectedItem(getPolePort());
        barcodeCombo.addItem("");
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printer : printServices) {
            barcodeCombo.addItem(printer.getName());
        }
        barcodeCombo.setSelectedItem(barcodePrinter);
        String s = root.get("ExtraLines", "6");
        extraLines = Integer.parseInt(s);
        jExtraLines.setValue(extraLines);
        s = root.get("CharsOnReceipt", "40");
        charsOnReceipt = Integer.parseInt(s);
        charsOnReceipt = (charsOnReceipt < 20 || charsOnReceipt > 100) ? 40 : charsOnReceipt;
        jCharsOnReceipt.setValue(charsOnReceipt);
        s = root.get("ShelfEdgeLabelHeight", "20");
        shelfEdgeLabelHeight = (Integer.parseInt(s));
        jShelfEdgeLabelHeight.setValue(shelfEdgeLabelHeight);
        setSoundOn(root.getBoolean("SoundOn", true));
        soundCheckBox.setSelected(isSoundOn());
        touch = root.getBoolean("Touch", false);
        touchCheckBox.setSelected(touch);
        invoicePrinter = root.getBoolean("invoicePrinter", false);
        invoiceCheckBox.setSelected(invoicePrinter);
        codeTableIndex = root.getInt("codeTableIndex", codeTableIndex);
        codeTableComboBox.setSelectedIndex(codeTableIndex);
        internationalFontSetIndex = root.getInt("internationalFontSetIndex", internationalFontSetIndex);
        internationalFontSetComboBox.setSelectedIndex(internationalFontSetIndex);
        heading = StringOps.fixLength("T Qty Product Price(£) Charge(£)", charsOnReceipt)
                + "";
        heading = root.get("heading", "T Qty Product Price(£) Charge(£)");
        headingTextField.setText(heading);
        jLabel3.setText(convertToMultiline(" * An installed Windows printer driver for a\n receipt printer will permanently disable a port"));

    }

    /**
     * @return the soundOn
     */
    public boolean isSoundOn() {
        return soundOn;
    }

    /**
     * @return the barcodePrinter
     */
    public String getBarcodePrinter() {
        return barcodePrinter;
    }

    public int getBarcodePrinterIndex() {
        int i;
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
//            System.out.println("Number of print services: " + printServices.length);
        for (i = 0; i < printServices.length; i++) {
//                System.out.println(printServices[i].getName());
            if (printServices[i].getName().equalsIgnoreCase(barcodePrinter)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @return the charsOnReceipt
     */
    public Integer getCharsOnReceipt() {
        return charsOnReceipt;
    }

    public boolean ok() {
//        if (Serial.RECEIPTPORT != null) {
//            Serial.RECEIPTPORT.closePort();
//            Serial.RECEIPTPORT.setPort("");
//        }
//        if (Serial.POLEPORT != null) {
//            Serial.POLEPORT.closePort();
//            Serial.POLEPORT.setPort("");
//        }
//        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        root.putInt("baudRrate", (Integer) baudRateCombo.getSelectedItem());
        setReceiptPort((String) receiptCombo.getSelectedItem());
        root.put("ReceiptPort", getReceiptPort());
        setPolePort((String) poleCombo.getSelectedItem());
        root.put("PolePort", getPolePort());
        barcodePrinter = (String) barcodeCombo.getSelectedItem();
        root.put("BarcodePrinter", barcodePrinter);
        invoicePrinter = invoiceCheckBox.isSelected();
        root.putBoolean("invoicePrinter", invoicePrinter);
        extraLines = (Integer) jExtraLines.getValue();
        root.putInt("ExtraLines", extraLines);
        charsOnReceipt = (Integer) jCharsOnReceipt.getValue();
        root.putInt("CharsOnReceipt", getCharsOnReceipt());
        root.putInt("ShelfEdgeLabelHeight", (Integer) jShelfEdgeLabelHeight.getValue());
        setSoundOn(soundCheckBox.isSelected());
        root.putBoolean("SoundOn", isSoundOn());
        touch = touchCheckBox.isSelected();
        root.putBoolean("Touch", touch);
        codeTableIndex = codeTableComboBox.getSelectedIndex();
        root.putInt("codeTableIndex", codeTableIndex);
        internationalFontSetIndex = internationalFontSetComboBox.getSelectedIndex();
        root.putInt("internationalFontSetIndex", internationalFontSetIndex);
        heading = headingTextField.getText();
        root.put("heading", heading);
        return true;
    }

    /**
     * @return the invoicePrinter
     */
    public boolean isInvoicePrinter() {
        return invoicePrinter;
    }

    /**
     * @return the code
     */
    public byte getCode() {
        return code;
    }

    /**
     * @return the extraLines
     */
    public Integer getExtraLines() {
        return extraLines;
    }

    /**
     * @return the shelfEdgeLabelHeight
     */
    public int getShelfEdgeLabelHeight() {
        return shelfEdgeLabelHeight;
    }

    /**
     * @return the heading
     */
    public String getHeading() {
        return heading;
    }

    /**
     * @param soundOn the soundOn to set
     */
    public void setSoundOn(boolean soundOn) {
        this.soundOn = soundOn;
        soundCheckBox.setSelected(soundOn);
    }

    private String convertToMultiline(String orig) {
        return "<html>" + orig.replaceAll("\n", "<br>");
    }

    /**
     * @return the baudRrate
     */
    public Integer getBaudRrate() {
        return baudRrate;
    }

}
