/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewProductEscape.java
 *
 * Created on 10-Nov-2009, 20:03:18
 */
package proffittcenter;

import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *
 * @author HP_Owner
 */
public class NewProduct extends EscapeDialog {

    public static int NOTENCODE = 0;
    public static int ENCODEBYPRICEPARITY = 1;
    public static int ENCODEBYWEIGHTPARITY = 2;;
    public static int ENCODEBYPRICENOPARITY = 3;
    public static int ENCODEBYWEIGHTNOPARITY = 4;
    public static int ENCODEBYPRICE5 = 5;
    public static int ENCODEBYWEIGHT5 = 6;
    private final Preferences root = Preferences.userNodeForPackage(getClass());
    private final ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/NewProduct");
    private String department;
    private String product;
    private int sku;
    private long barcode;
    private int price;//the retail price
    private String description="";
    private boolean newProduct;
    private int pack;
    private int supplier;
    private int packPrice;
    private int margin;
    private String linkedProduct;
    private ResultSet rs;
    private String supplierString;
    private PreparedStatement supplierQuery;
    private String suppliersFind = "SELECT Suppliers.Description FROM Suppliers ORDER BY Suppliers.Description";
    private static PreparedStatement productLookup;
    private static ResultSet pl;
    private int encoded;
    private String shortenedData;
    private boolean wasNewSku;
    private String departmentFind = "SELECT Departments.Description FROM Departments ORDER BY Departments.Description";
    private PreparedStatement departmentQuery;
    private int rate;
    private boolean newPackPackSupplier;
    private PreparedStatement np;
    private String aSupplierString;
    private String shortenedData8;
    private long returnBarcode;
    private String lookupString;
    private InputStreamReader inStream;
    private String nextLine;
    Locale locale = Locale.getDefault();
    private int linesToSkip;
    private String s;
    private String productDescription;
    private int retailPrice;

    /** Creates new form NewProductEscape
     * @param parent
     * @param modal  
     */
    public NewProduct(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getRootPane().setDefaultButton(okBtn);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Newproduct", Main.mainHelpSet);
    }
    
    public static String shortenForEncoded6(String data) {
        if (data.length() < 11 || !couldBeEncoded(data)) {
            return data;
        }
        if (data.startsWith("02")) {
            return data.substring(1, 7) + "0000000";
        } else if (data.startsWith("2")) {
            return data.substring(0, 6) + "0000000";
        } else {
            return data;
        }
    }

    public static String shortenForEncoded7(String data) {
        if (data.length() < 11 || !couldBeEncoded(data)) {
            return data;
        }
        if (data.startsWith("02")) {
            return data.substring(1, 8) + "000000";
        } else if (data.startsWith("2")) {
            return data.substring(0, 7) + "000000";
        } else {
            return data;
        }
    }

    public static String shortenForEncoded8(String data) {
        if (data.length() < 11 || !couldBeEncoded(data)) {
            return data;
        }
        if (data.startsWith("02")) {
            return data.substring(0, 9) + "00000";
        } else if (data.startsWith("2")) {
            return data.substring(0, 8) + "00000";
        } else {
            return data;
        }
    }

    public static int getEncodedData4(String data) {
        if (data.length() < 5) {
            return 0;
        }
        String s = data.substring(data.length() - 5, data.length() - 1);
        return Integer.parseInt(s);
    }

    public static int getEncodedData5(String data) {
        if (data.length() < 6) {
            return 0;
        }
        String s = data.substring(data.length() - 6, data.length() - 1);
        return Integer.parseInt(s);
    }
    public boolean createPackPackSupplier() throws SQLException {
        np = Main.getConnection().prepareStatement("INSERT INTO Packs(ID,WhenCreated,Product,Size,Code,Encoded)VALUES(?,?,?,1,?,?)");
        np.setNull(1, Types.INTEGER);
        np.setNull(2, Types.DATE);
        np.setLong(3, barcode);
        np.setLong(4, barcode);
        np.setInt(5, encoded);
        np.executeUpdate();
        np = Main.getConnection().prepareStatement("SELECT LAST_INSERT_ID() FROM Packs");
        rs = np.executeQuery();
        rs.first();
        pack = rs.getInt(1);
        rs.close();
        np = Main.getConnection().prepareStatement("INSERT INTO PackSuppliers(ID,WhenCreated,Pack,Price,Supplier)" + "VALUES(?,?,?,?,?)");
        np.setNull(1, Types.INTEGER);
        np.setNull(2, Types.DATE);
        np.setInt(3, pack);
        //calculate packPrice
        PreparedStatement ppp = Main.getConnection().prepareStatement("SELECT Departments.Margin,Taxes.Rate FROM Departments,Skus,Taxes "
                + "WHERE Skus.Department=Departments.ID AND Skus.Tax=Taxes.ID AND Skus.ID=?");
        ppp.setInt(1, sku);
        rs = ppp.executeQuery();
        rs.first();
        margin = rs.getInt("Margin");
        rate = rs.getInt("Rate");
        rs.close();
        Regime rr = Main.shop.regimeIs();
        if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
            packPrice = (price * 10 + 5) * (10000 - margin) / (10000  + rate);
        } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
            packPrice = (price * 10 + 5) * (1000 - margin) / 10000;
        }
        np.setInt(4, packPrice);
        supplierString = (String) jSupplierCombo.getSelectedItem();
        if (supplierString.isEmpty()) {
            supplierString = "Default";
            supplier = 1;
        }
        PreparedStatement fs = Main.getConnection().prepareStatement("SELECT ID FROM Suppliers WHERE Description=?");
        fs.setString(1, supplierString);
        ResultSet fsr = fs.executeQuery();
        if (!fsr.first()) {
            return true;
        }
        supplier = fsr.getInt("ID");
        fsr.close();
        //need to set supplier to default supplier of product
        np.setInt(5, supplier);
        np.executeUpdate();
        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jBarcode = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jEncoded = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        departmentCombo = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        productsCombo = new javax.swing.JComboBox();
        priceLabel = new javax.swing.JLabel();
        jPrice = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        perText = new javax.swing.JTextField();
        printBarcode = new javax.swing.JButton();
        okBtn = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jSupplierCombo = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jDescription = new javax.swing.JTextField();
        parityCheckBox = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        multiPackSpinner = new javax.swing.JSpinner();
        barcodeCheckBox = new javax.swing.JCheckBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/NewProduct"); // NOI18N
        jTextField1.setText(bundle.getString("NewProduct.jTextField1.text")); // NOI18N
        jTextField1.setName("jTextField1"); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("Title")); // NOI18N
        setName("NewProduct"); // NOI18N

        jBarcode.setEditable(false);
        jBarcode.setFocusable(false);
        jBarcode.setName("jBarcode"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(bundle.getString("NewProduct.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(bundle.getString("NewProduct.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jEncoded.setName("jEncoded"); // NOI18N
        jEncoded.setNextFocusableComponent(parityCheckBox);
        jEncoded.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEncodedActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(bundle.getString("NewProduct.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        departmentCombo.setName("departmentCombo"); // NOI18N
        departmentCombo.setNextFocusableComponent(productsCombo);
        departmentCombo.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                departmentComboFocusLost(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(bundle.getString("NewProduct.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        productsCombo.setName("productsCombo"); // NOI18N
        productsCombo.setNextFocusableComponent(jPrice);

        priceLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        priceLabel.setText(bundle.getString("NewProduct.priceLabel.text")); // NOI18N
        priceLabel.setName("priceLabel"); // NOI18N

        jPrice.setName("jPrice"); // NOI18N
        jPrice.setNextFocusableComponent(jSupplierCombo);
        jPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                jPriceFocusLost(evt);
            }
        });
        jPrice.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jPriceKeyReleased(evt);
            }
        });

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(bundle.getString("NewProduct.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        perText.setName("perText"); // NOI18N
        perText.setNextFocusableComponent(okBtn);

        printBarcode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Barcode.png"))); // NOI18N
        printBarcode.setContentAreaFilled(false);
        printBarcode.setName("printBarcode"); // NOI18N
        printBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBarcodeActionPerformed(evt);
            }
        });

        okBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/OK.png"))); // NOI18N
        okBtn.setContentAreaFilled(false);
        okBtn.setName("okBtn"); // NOI18N
        okBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okBtnActionPerformed(evt);
            }
        });

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton.setContentAreaFilled(false);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText(bundle.getString("NewProduct.jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jSupplierCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jSupplierCombo.setName("jSupplierCombo"); // NOI18N
        jSupplierCombo.setNextFocusableComponent(perText);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText(bundle.getString("NewProduct.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jDescription.setName("jDescription"); // NOI18N
        jDescription.setNextFocusableComponent(departmentCombo);
        jDescription.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jDescriptionFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                jDescriptionFocusLost(evt);
            }
        });
        jDescription.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jDescriptionKeyReleased(evt);
            }
        });

        parityCheckBox.setText(bundle.getString("NewProduct.parityCheckBox.text")); // NOI18N
        parityCheckBox.setEnabled(false);
        parityCheckBox.setName("parityCheckBox"); // NOI18N
        parityCheckBox.setNextFocusableComponent(jDescription);
        parityCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                parityCheckBoxActionPerformed(evt);
            }
        });

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(bundle.getString("NewProduct.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        multiPackSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 1000, 1));
        multiPackSpinner.setName("multiPackSpinner"); // NOI18N

        barcodeCheckBox.setText(bundle.getString("NewProduct.barcodeCheckBox.text")); // NOI18N
        barcodeCheckBox.setToolTipText(bundle.getString("NewProduct.barcodeCheckBox.toolTipText")); // NOI18N
        barcodeCheckBox.setName("barcodeCheckBox"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(priceLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 81, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSupplierCombo, 0, 519, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(perText, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(barcodeCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(printBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPrice, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                    .addComponent(productsCombo, 0, 519, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(parityCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 130, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jEncoded, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jDescription, javax.swing.GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(departmentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(multiPackSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jEncoded, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(parityCheckBox)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jDescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jLabel3)
                    .addComponent(departmentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(multiPackSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(productsCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(priceLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jSupplierCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(perText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6))
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(barcodeCheckBox)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(printBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(okBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {closeButton, okBtn, printBarcode});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void internetLookup(long barcode, JTextField jDescription) {
//        if (Main.server.internetEnabled && locale.getLanguage().equalsIgnoreCase(Locale.ENGLISH.getLanguage())) {
//                if (barcode > 100009999L) {//no short barcodes
//                    Thread idl = new InternetDescriptionLookup("" + barcode, jDescription);
//                    idl.start();
//                    jDescription.setText(description);
//                    jDescription.selectAll();
//                } else {
//                    jDescription.setText("");
//                }
//        }
        jDescription.setText("");
    }

    private void printBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBarcodeActionPerformed
        BarcodePrinter.print(barcode, description);
}//GEN-LAST:event_printBarcodeActionPerformed

    private void okBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okBtnActionPerformed
        //save the new product
        //trim the description
        barcode = Long.parseLong(jBarcode.getText());
        department = (String) departmentCombo.getSelectedItem();
        String s = jDescription.getText().trim();
        jDescription.setText(s);
        if (s.isEmpty()) {//need a description
            Audio.play("Ring");
            jDescription.setText(s);
            jDescription.requestFocus();
            return;
        } else if (jPrice.getText().isEmpty()) {//no price
            Audio.play("Ring");
            jPrice.requestFocus();
            return;
        }
        if (departmentCombo.getSelectedItem() == ""
                || departmentCombo.getSelectedItem() == bundle.getString("Default")) {
            department = bundle.getString("Default");
//            productDescription = bundle.getString("Default");
        } else if (productsCombo.getSelectedItem() == "") {
//            product = bundle.getString("Default");
        }
        jDescription.requestFocus();//needed so focus not on price initially next time
        //find the sku
        linkedProduct = (String) productsCombo.getSelectedItem();
        if (linkedProduct == null) {
            linkedProduct = bundle.getString("Default");
        }
        if (linkedProduct.equalsIgnoreCase(bundle.getString("Default"))) {
            sku = 0;
        } else {//get the sku of selected product
            try {
                PreparedStatement skuQuery = Main.getConnection().prepareStatement(
                        "SELECT Products.Sku FROM Products,Skus,Departments WHERE Products.Description=? "
                        + "AND Departments.Description=? "
                        + "AND Products.Sku=Skus.ID "
                        + "AND Skus.Department=Departments.ID ");
//                productDescription = jDescription.getText();
                if (jEncoded.getItemCount() > 1) {
                    encoded = jEncoded.getSelectedIndex();
                }
                skuQuery.setString(1, linkedProduct);
                skuQuery.setString(2, department);
                rs = skuQuery.executeQuery();
                if (rs.first()) {//not if empty
                    sku = rs.getInt("Sku");
                } else {
                    sku = 0;
                }
                rs.close();
                skuQuery.close();
            } catch (SQLException ex) {
                Audio.play("Ring");
                Logger.getLogger(NewProduct.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (saveNewProduct()) {
            int response = JOptionPane.showConfirmDialog(this, bundle.getString("ShelfEdgeLabel"),
                            bundle.getString("Information"),
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (response == JOptionPane.YES_OPTION) {
                        int linesToBePrinted = 4;
                        int linesSeperatingPrintAndCut = 6;
                        int shelfEdgeLabelHeight = Main.hardware.getShelfEdgeLabelHeight() - Main.hardware.getExtraLines();
                        int blankLinesBeforePrinting = (shelfEdgeLabelHeight - linesToBePrinted) / 2 + Main.hardware.getExtraLines() - linesSeperatingPrintAndCut;
                        blankLinesBeforePrinting = blankLinesBeforePrinting < 0 ? 0 : blankLinesBeforePrinting;
                        int blankLinesAfterPrinting = (shelfEdgeLabelHeight - linesToBePrinted) / 2 - blankLinesBeforePrinting - Main.hardware.getExtraLines() + linesSeperatingPrintAndCut;
                        Main.receiptPrinter.selectPrinter();
                        //Main.receiptPrinter.selectPrinter();
                        linesToSkip = blankLinesAfterPrinting - 1;
                        s = "" + product;
                        s = StringOps.fixLength(s, 12);
                        if (barcodeCheckBox.isSelected()) {
                            Main.receiptPrinter.printBarcode(s);
                            linesToSkip -= 3;
                        }
                        for (int i = 0; i < blankLinesBeforePrinting; i++) {
                            Main.receiptPrinter.printLine("");
                        }
                        productDescription=jDescription.getText();
                        Main.receiptPrinter.printLargeCentralLine(productDescription);
                        retailPrice=Integer.parseInt(StringOps.numericOnly(jPrice.getText()));
                        String s1 = (new Money(retailPrice).toString());
                        Main.receiptPrinter.printLargeCentralLine(s1);
                        for (int i = 0; i < blankLinesAfterPrinting; i++) {
                            Main.receiptPrinter.printLine(" ");
                        }
                        Main.receiptPrinter.endPrinter();
                    }
            returnBarcode = barcode;
            Audio.play("Beep");
            setVisible(false);
        }
}//GEN-LAST:event_okBtnActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.barcode = -1L;
        Audio.play("Beep");
        setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    private void jPriceFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jPriceFocusLost
        try {
            String s = StringOps.numericOnly(jPrice.getText());
            if (s.isEmpty()) {
                if (jDescription.getText().isEmpty()) {
                    jDescription.requestFocus();
                } else {
                    jPrice.requestFocus();
                }
                return;
            }
            price = Integer.parseInt(StringOps.numericOnly(jPrice.getText()));
            jPrice.setText((new Money(price)).toString());
        } catch (NumberFormatException ex) {
            Logger.getLogger(NewProduct.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
            jPrice.setText("");
            jPrice.requestFocus();
            return;
        }
    }//GEN-LAST:event_jPriceFocusLost

    private void departmentComboFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_departmentComboFocusLost
        department = (String) departmentCombo.getSelectedItem();
        productsCombo.removeAllItems();
        productsCombo.addItem("");
        try {
            PreparedStatement productQuery = Main.getConnection().prepareStatement(
                    "SELECT Products.Description,Suppliers.Description FROM Products,"
                    + "Skus,Departments,Suppliers "
                    + "WHERE Departments.Description=? AND Products.Sku=Skus.ID "
                    + "AND Skus.Department=Departments.ID "
                    + "AND Departments.Supplier=Suppliers.ID "
                    + "AND Skus.ID<>1 ORDER BY Products.Description ");
            productQuery.setString(1, department);
            ResultSet rsp = productQuery.executeQuery();
            if (rsp.first()) {//not if empty
                supplierString = rsp.getString("Suppliers.Description");
//                jSupplierCombo.addItem(supplierString);
                do {//Products to display
                    String S = rsp.getString("Products.Description");
                    productsCombo.addItem(S);
                } while (rsp.next());
            } else {
//                jSupplierCombo.removeAllItems();
                productsCombo.removeAllItems();
            }
            rsp.close();
            jSupplierCombo.removeAllItems();
            PreparedStatement sq = Main.getConnection().prepareStatement(
                    "SELECT Suppliers.Description FROM Suppliers,Departments WHERE Departments.Description=? AND Departments.Supplier=Suppliers.ID");
            sq.setString(1, department);
            ResultSet d = sq.executeQuery();
            if (d.first()) {
                aSupplierString = d.getString("Suppliers.Description");
//                jSupplierCombo.addItem(aSupplierString);
            }
            sq = Main.getConnection().prepareStatement(suppliersFind);
            rs = sq.executeQuery();
            if (rs.first()) {//not if empty
                do {//Suppliers to display
                    String S = rs.getString("Description");
                    jSupplierCombo.addItem(S);
                } while (rs.next());
            }
            rs.close();
            jSupplierCombo.setSelectedItem(aSupplierString);
            productsCombo.setSelectedItem((Object) "");
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(NewProduct.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_departmentComboFocusLost

    //jDescription.setText(StringOps.firstCaps(jDescription.getText().trim()));
//    //limit here to 50 characters
//        int len = jDescription.getText().length();
//        if(len>50){
//            jDescription.setText(jDescription.getText().substring(0, 50));
//            Audio.play("Ring");
//        }
    private void jPriceKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jPriceKeyReleased
        Money.asMoney(jPrice);
    }//GEN-LAST:event_jPriceKeyReleased

    private void jEncodedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEncodedActionPerformed
        int selection = jEncoded.getSelectedIndex();
        if (selection == ENCODEBYPRICEPARITY || selection == ENCODEBYPRICENOPARITY 
                || selection == ENCODEBYPRICE5 ) { //encoded by weight, change price label
            jPrice.setText("0");
            priceLabel.setText(bundle.getString("SetToZero"));
        } else if (selection == ENCODEBYWEIGHTPARITY||selection == ENCODEBYWEIGHTNOPARITY 
                || selection == ENCODEBYWEIGHT5) { //encoded by price, change price label
            priceLabel.setText(bundle.getString("PricePer") + " " + Main.shop.getKgSymbol() + ":");
        }
//        parityCheckBox.setEnabled(selection<=ENCODEBYWEIGHTPARITY);
    }//GEN-LAST:event_jEncodedActionPerformed

    private void jDescriptionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jDescriptionKeyReleased
        //limit here to 50 characters
        int len = jDescription.getText().length();
        if (len > 50) {
            jDescription.setText(jDescription.getText().substring(0, 50));
            Audio.play("Ring");
        }
    }//GEN-LAST:event_jDescriptionKeyReleased

    private void jDescriptionFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jDescriptionFocusLost
        if(!InternetDescriptionLookup.description.isEmpty()&&description.isEmpty()){                
            jDescription.setText(InternetDescriptionLookup.description);
            jDescription.selectAll();
        }
        if(jDescription.getText().isEmpty()){
            jDescription.requestFocus();
            return;
        }
        jDescription.setText(StringOps.firstCaps(jDescription.getText().trim()));
    }//GEN-LAST:event_jDescriptionFocusLost

    private void parityCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_parityCheckBoxActionPerformed
        jDescription.requestFocus();
        if (parityCheckBox.isSelected()) {
            product = NewProduct.shortenForEncoded7("" + barcode);
        } else {
            product = NewProduct.shortenForEncoded8("" + barcode);
        }
        jBarcode.setText(product);
    }//GEN-LAST:event_parityCheckBoxActionPerformed

    private void jDescriptionFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jDescriptionFocusGained
            if(!InternetDescriptionLookup.description.isEmpty()&&description.isEmpty()){                
                jDescription.setText(InternetDescriptionLookup.description);
                jDescription.selectAll();
            }
    }//GEN-LAST:event_jDescriptionFocusGained

    private void jTextField2FocusLost(java.awt.event.FocusEvent evt) {
        //limit here to 50 characters
        int len = jDescription.getText().length();
        if (len > 50) {
            jDescription.setText(jDescription.getText().substring(0, 50));
            Audio.play("Ring");
        }
    }

    /**
     *
     * @param barcode
     * @param newPackPackSupplier true if pack and packSuplier required
     * @return barcode or 0L if none created
     */
    public long execute(long barcode, boolean newPackPackSupplier) {
        if (Main.operator.createNewStock || Main.operator.isOwnerManager()) {
            try {
                if (("" + barcode).length() < 6 && barcode != 0) {
                    return 0L; //8 or more digits
                }
                this.barcode = barcode;
                this.newPackPackSupplier = newPackPackSupplier;
                jBarcode.setText("" + barcode);
                wasNewSku = false;
                jDescription.setText("");
                priceLabel.setText(bundle.getString("NewProduct.priceLabel.text"));
                parityCheckBox.setSelected(false);
                parityCheckBox.setEnabled(false);
                PreparedStatement productExists = Main.getConnection().prepareStatement("SELECT * FROM Products WHERE ID=?");
                productExists.setLong(1, barcode);
                rs = productExists.executeQuery();
                if (rs.first()) {
                    //existing
                    newProduct = false; //existing
                    productDescription = rs.getString("Description").trim();
                    jDescription.setText(productDescription);
                    jPrice.setText(new Money(rs.getInt("Price")).toString());
                    perText.setText(rs.getString("Per"));
                    jDescription.selectAll();
                } else {
                    newProduct = true;
                    description = "";
                    internetLookup(barcode, jDescription);
                    jDescription.selectAll();
                    jPrice.setText("");
                    perText.setText("");
                }
                rs.close();
                departmentCombo.removeAllItems();
                departmentQuery = Main.getConnection().prepareStatement(departmentFind);
                rs = departmentQuery.executeQuery();
                if (rs.first()) {
                    //departments to display
                    do {
                        //departments to display
                        String S = rs.getString("Description");
                        departmentCombo.addItem(S);
                    } while (rs.next());
                }
                rs.close();
                departmentCombo.setSelectedItem((Object) bundle.getString("Default"));
                productsCombo.removeAllItems();
                Audio.play("Tada");
                jSupplierCombo.removeAllItems();
                jSupplierCombo.addItem("");
                try {
                    supplierQuery = Main.getConnection().prepareStatement(suppliersFind);
                    rs = supplierQuery.executeQuery();
                    if (rs.first()) {
                        //not if empty
                        do {
                            //Suppliers to display
                            String S = rs.getString("Description");
                            jSupplierCombo.addItem(S);
                        } while (rs.next());
                    }
                    rs.close();
                    jSupplierCombo.setSelectedItem((Object) bundle.getString("Default"));
                } catch (SQLException ex) {
                    Logger.getLogger(NewProduct.class.getName()).log(Level.SEVERE, null, ex);
                    Audio.play("Ring");
                }
                if (couldBeEncoded("" + barcode)) {
                    jEncoded.removeAllItems();
                    jEncoded.addItem(bundle.getString("No"));
                    jEncoded.addItem(bundle.getString("ByPriceParity"));
                    jEncoded.addItem(bundle.getString("ByWeightParity"));
                    jEncoded.addItem(bundle.getString("ByPriceNoParity"));
                    jEncoded.addItem(bundle.getString("ByWeightNoParity"));
                    jEncoded.addItem(bundle.getString("ByPrice5"));
                    jEncoded.addItem(bundle.getString("ByWeight5"));
//                    jEncoded.setEnabled(true);
                    jEncoded.requestFocus();
                } else {
                    jEncoded.removeAllItems();
                    jEncoded.setEnabled(false);
                    jDescription.requestFocus();
                }
                jDescription.setEditable(true);
                returnBarcode = 0L;
                if(Main.server.internetEnabled){
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NewProduct.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if(!InternetDescriptionLookup.description.isEmpty()&&description.isEmpty()){                
                    jDescription.setText(InternetDescriptionLookup.description);
                    jDescription.selectAll();
                }
                setVisible(true);
                return returnBarcode;
            } catch (SQLException ex) {
                Logger.getLogger(NewProduct.class.getName()).log(Level.SEVERE, null, ex);
                return -1L;
            }
        }
        return 0L;
    }

    /**
     * 
     * @param data 
     * @return true if could be encoded
     */
    public static boolean couldBeEncoded(String data) {
        if (data.length() > 10
                && (data.charAt(0) == '0' && data.charAt(1) == '2' || data.charAt(0) == '2')) {
            if (data.charAt(0) == '2') {//may be a multiplier
                try {
                    //may be a multiplier
                    String restOfData = data.substring(1);
                    productLookup = Main.getConnection().prepareStatement(SQL.selectProduct);
                    productLookup.setString(1, restOfData);
                    productLookup.setString(2, restOfData);
                    productLookup.setString(3, restOfData);
                    productLookup.setString(4, restOfData);
                    pl = productLookup.executeQuery();
                    if (pl.first()) {
                        productLookup.close();
                        return false;//was a multiplier
                    }
                    productLookup.close();
                } catch (SQLException ex) {
                    Logger.getLogger(NewProduct.class.getName()).log(Level.SEVERE, null, ex);//2003794044999 
                }
                //not a multiplier
                //not a multiplier
            }
            return true;
        }
        return false;
    }

    private boolean saveNewProduct() {
        //if sku==1, need to call new sku
        if (sku == 0 && !department.equalsIgnoreCase(bundle.getString("Default"))) {
            sku = Main.newSku.execute(department);
            wasNewSku = true;
        } else if (sku == 0) {
            sku = 1;
        }
        if (sku < 0) {
            returnBarcode = 0l;
            return false;
        }
        //now save the product record
        try {
            product = jBarcode.getText();
            this.barcode = Long.parseLong(jBarcode.getText());
            if (newProduct) {
                np = Main.getConnection().prepareStatement(
                        "INSERT INTO Products(ID,Description,Price,Sku,Per,Encoded,MultiPack) VALUES(?,?,?,?,?,?,?)");
                encoded = jEncoded.getSelectedIndex();
                if (encoded == -1) {
                    encoded = NewProduct.NOTENCODE;
                }
                np.setInt(6, encoded);//0 - no, 1 - by price. 2 - by weight
                if (encoded == NewProduct.NOTENCODE || encoded == -1) {
                    np.setLong(1, this.barcode);//ID
                } else {
                    if (parityCheckBox.isSelected()) {
                        shortenedData = shortenForEncoded7(product);
                        np.setString(1, shortenedData);//ID
                    } else {
                        shortenedData = shortenForEncoded8(product);
                        np.setString(1, shortenedData);//ID
                    }
                    barcode = Long.parseLong(shortenedData);
                }
                np.setString(2, StringOps.fixLength(jDescription.getText().trim(), 50).trim());//description
                if (encoded == NewProduct.NOTENCODE || encoded == NewProduct.ENCODEBYWEIGHTPARITY || encoded == NewProduct.ENCODEBYWEIGHTNOPARITY || encoded == -1) {
                    price = Integer.parseInt(StringOps.numericOnly(jPrice.getText()));
                } else {
                    price = 0;
                    jPrice.setText(new Money(0).toString());
                }
                np.setString(3, StringOps.numericOnly(jPrice.getText()));//price
                np.setInt(4, sku);
                np.setString(5, StringOps.fixLength(perText.getText(), 5));
                np.setInt(6, encoded);
                np.setInt(7, (Integer)multiPackSpinner.getValue());
                np.executeUpdate();
                np.close();
//                Connection connectionOnline = Main.getConnectionOnline();
//                if (connectionOnline != null) {//save new product online
//                    np = connectionOnline.prepareStatement(
//                            "INSERT INTO Products(ID,Description,theLocale) VALUES(?,?,?)");
//                    np.setLong(1, this.barcode);//ID
//                    np.setString(2, StringOps.fixLength(jDescription.getText().trim(), 50));//description
//                    np.setString(3, Main.hardware.myLanguage);//local
//                    np.executeUpdate();
//                    np.close();
//                }
//                if (wasNewSku && newPackPackSupplier) {//only if a new SKU was created
//                    wasNewSku = false;
//                    if (createPackPackSupplier()) {
//                        return false;
//                    }
//                }
            } else {//need to update
                np = Main.getConnection().prepareStatement(
                        "UPDATE Products SET  Description=?,Price=?,Sku=?,Per=?,Encoded=?,MultiPack=? WHERE ID=?");
                np.setString(1, StringOps.fixLength(jDescription.getText().trim(), 50));//description
                price = Integer.parseInt(StringOps.numericOnly(jPrice.getText()));
                np.setString(2, StringOps.numericOnly(jPrice.getText()));//price
                np.setInt(3, sku);
                np.setString(4, StringOps.fixLength(perText.getText(), 4));
                np.setInt(5, encoded);
                np.setInt(6, (Integer)multiPackSpinner.getValue());
                np.setLong(7, this.barcode);//ID
                np.executeUpdate();
                if (wasNewSku) {//only if a new SKU was created
                    wasNewSku = false;
                    if (newPackPackSupplier) {
                        newPackPackSupplier = false;
                        if (createPackPackSupplier()) {
                            returnBarcode = 0L;
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(NewProduct.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        setVisible(false);
        return true;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                NewProduct dialog = new NewProduct(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox barcodeCheckBox;
    private javax.swing.JButton closeButton;
    private javax.swing.JComboBox departmentCombo;
    private javax.swing.JTextField jBarcode;
    private javax.swing.JTextField jDescription;
    private javax.swing.JComboBox jEncoded;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField jPrice;
    private javax.swing.JComboBox jSupplierCombo;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JSpinner multiPackSpinner;
    private javax.swing.JButton okBtn;
    private javax.swing.JCheckBox parityCheckBox;
    private javax.swing.JTextField perText;
    private javax.swing.JLabel priceLabel;
    private javax.swing.JButton printBarcode;
    private javax.swing.JComboBox productsCombo;
    // End of variables declaration//GEN-END:variables

    /**
     * Creates a new product
     * @param barcode
     * @param encoded 
     * @param newPackPackSupplier true if you want a default pack and packSupplier
     * @return
     */
    long execute(long barcode, int encoded, boolean newPackPackSupplier) {
        this.newPackPackSupplier = newPackPackSupplier;
        return execute(barcode, encoded);
    }

    /**
     * Creates a new product
     * @param barcode
     * @param encoded
     * @return
     */
    long execute(long barcode, int encoded) {
        if (Main.operator.createNewStock || Main.operator.isOwnerManager()) {
            //like execute but for known encoded product
            if (!couldBeEncoded("" + barcode)) {
                //this is only for encoded products, making sure
                return 0L;
            }
            if (encoded == NewProduct.NOTENCODE) {
                //this is only for encoded products
                return 0L;
            }
            if (encoded == ENCODEBYPRICENOPARITY || encoded == ENCODEBYPRICEPARITY) { //encoded by weight, change price label
                jPrice.setText("0");
                priceLabel.setText(bundle.getString("SetToZero"));
            } else if (encoded == ENCODEBYWEIGHTNOPARITY || encoded == ENCODEBYWEIGHTPARITY) { //encoded by price, change price label
                priceLabel.setText(bundle.getString("PricePer") + " " + Main.shop.getKgSymbol() + ":");
            }
            parityCheckBox.setSelected(encoded<=ENCODEBYWEIGHTPARITY);//then parity
            parityCheckBox.setEnabled(false);
//            parityCheckBox.requestFocus();
            //make sur it is the shortened data
            this.barcode = barcode;
            if(encoded == ENCODEBYPRICENOPARITY||encoded == ENCODEBYWEIGHTNOPARITY){
                jBarcode.setText(NewProduct.shortenForEncoded8("" + barcode));
            }else{
                jBarcode.setText(NewProduct.shortenForEncoded7("" + barcode));
            }
            wasNewSku = false;
            jDescription.setText("");
            jPrice.setText("");
            try {
                PreparedStatement productExists = Main.getConnection().prepareStatement(
                        "SELECT * FROM Products WHERE ID=? "
                        + "OR SUBSTRING(ID FROM 1 FOR 7)=? AND (Encoded=1 OR Encoded=2) "
                        + "OR SUBSTRING(ID FROM 1 FOR 8)=? AND(Encoded=3 OR Encoded=4)");
                productExists.setLong(1, barcode);
                productExists.setString(2, NewProduct.shortenForEncoded7("" + barcode));
                productExists.setString(3, NewProduct.shortenForEncoded8("" + barcode));
                rs = productExists.executeQuery();
                if (rs.first()) {//product exists
                    this.barcode = rs.getLong("ID");
                    newProduct = false;//existing
                    productDescription = rs.getString("Description").trim();
                    jDescription.setText(productDescription);
                    retailPrice = rs.getInt("Price");
                    jPrice.setText((new Money(retailPrice).toString()));
                    perText.setText(rs.getString("Per"));
                    multiPackSpinner.setValue(rs.getInt("MultiPack"));
                } else {//product does not exist
                    newProduct = true;
                    internetLookup(barcode, jDescription);
                    jDescription.selectAll();
                    jPrice.setText("");
                    perText.setText("");
                    multiPackSpinner.setValue(1);
                }
                rs.close();
                departmentCombo.removeAllItems();
                departmentQuery = Main.getConnection().prepareStatement(departmentFind);
                rs = departmentQuery.executeQuery();
                if (rs.first()) {//not if empty
                    do {//departments to display
                        String S = rs.getString("Description");
                        departmentCombo.addItem(S);
                    } while (rs.next());
                }
                rs.close();
                departmentCombo.setSelectedItem((Object) bundle.getString("Default"));
                productsCombo.removeAllItems();
            } catch (SQLException ex) {
                Logger.getLogger(NewProduct.class.getName()).log(Level.SEVERE, null, ex);
                Audio.play("Ring");
            }
            Audio.play("Tada");
            jSupplierCombo.removeAllItems();
            jSupplierCombo.addItem("");
            try {
                supplierQuery = Main.getConnection().prepareStatement(suppliersFind);
                rs = supplierQuery.executeQuery();
                if (rs.first()) {//not if empty
                    do {//Suppliers to display
                        String S = rs.getString("Description");
                        jSupplierCombo.addItem(S);
                    } while (rs.next());
                }
                rs.close();
                jSupplierCombo.setSelectedItem((Object) bundle.getString("Default"));
            } catch (SQLException ex) {
                Logger.getLogger(NewProduct.class.getName()).log(Level.SEVERE, null, ex);
                Audio.play("Ring");
            }
            jEncoded.removeAllItems();
            jEncoded.addItem(bundle.getString("No"));
            jEncoded.addItem(bundle.getString("ByPriceParity"));
            jEncoded.addItem(bundle.getString("ByWeightParity"));
            jEncoded.addItem(bundle.getString("ByPriceNoParity"));
            jEncoded.addItem(bundle.getString("ByWeightNoParity"));
            jEncoded.addItem(bundle.getString("ByPrice5"));
            jEncoded.addItem(bundle.getString("ByWeight5"));
            jEncoded.setSelectedIndex(encoded);
            jEncoded.setEnabled(false);
            returnBarcode = 0L;            
            if(!InternetDescriptionLookup.description.isEmpty()&&description.isEmpty()){                
                jDescription.setText(InternetDescriptionLookup.description);
                jDescription.selectAll();
            }
            if(!description.isEmpty()){                
                jDescription.setText(description);
                jDescription.selectAll();
            }
            setVisible(true);
            return returnBarcode;
        }
        return 0L;
    }

    /**
     * creates a new product without display
     * @param barcode
     * @param description
     * @param price
     * @return true if successful
     */
    public boolean execute(long barcode, String description, int price) {
        try {
            np = Main.getConnection().prepareStatement(
                    SQL.insertIntoProducts);
            this.description = description.trim();
            np.setLong(1, barcode);
            np.setString(2, this.description);
            np.setInt(3, price);
            np.executeUpdate();
            np.close();
        } catch (SQLException ex) {
            Main.closeConnection();
            Main.logger.log(Level.SEVERE, "NewProduct.execute ", "SQLException: " + ex.getMessage());
            return false;
        }
        return true;
    }
}
