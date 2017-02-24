 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DeliveryNew.java
 *
 * Created on 08-Mar-2009, 21:31:06
 */
package proffittcenter;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;

/**
 *
 * @author Dave Proffitt
 */
public class DeliveryEntry extends EscapeDialog {

    String oldPackCode;// = "");
    int supplier;
    int packPrice;
    int packSize;
    int margin;
    int rate;
    int quantity = 0;
    int sku;
    int numberOfPacks;
    int sale;
    long product;
    private String packCode;
    private int pack;
    private int packSupplier;
    private int saleDelivery = 0;
    private short dis = 0;
    private short po = 0;
    javax.swing.JComponent nextComponent;
    JTextField packSizeText, numberOfPacksText;//shortenForEncoded7
    private int originalPackPrice;
    private int delivery;
    private String reference;
    private int encode;
    private int encodedData;
    private String selectPack = "SELECT Packs.*,Products.*,Skus.Quantity,Skus.Tax,Taxes.*,Departments.Margin  "
            + "FROM Packs,Products,Skus,Taxes,Departments "
            + "WHERE (Packs.Code=? AND Packs.Encoded=0 OR "
            + "SUBSTRING(UPPER(Packs.Code) FROM 1 FOR 7)=? AND (Packs.Encoded=1 OR Packs.Encoded=2) "
            + "OR SUBSTRING(UPPER(Packs.Code) FROM 1 FOR 8)=? AND (Packs.Encoded=3 OR Packs.Encoded=4)) "
            + "AND Packs.Product=Products.ID AND Products.Sku=Skus.ID AND Skus.Tax=Taxes.ID AND SKUS.Department=Departments.ID "
            + "ORDER BY Packs.ID, Packs.Code DESC";
    private PreparedStatement packStatement;
    private String selectPackSupplier = "SELECT PackdSuppliers.* FROM PackSuppliers WHERE Pack=? AND Supplier=? "
            + "ORDER BY WhenCreated";
    private int retailPrice;
    private String createPackSupplier = "INSERT INTO PackSuppliers (ID,Pack,Price,Supplier) VALUES (?,?,?,?)";
    ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/DeliveryEntry");
    private String createPack = "INSERT INTO Packs (ID,Product,Size,Code,Encoded) VALUES(?,?,1,?,?)";
    private String productDescription;
    private String getPackId = "SELECT LAST_INSERT_ID() FROM Packs";
    private PreparedStatement ps;
    private PreparedStatement productStatement;
    private String selectProduct = "SELECT Products.*,Departments.Margin,Skus.Tax,Taxes.Rate "
            + "FROM Products,Skus,Departments,Taxes "
            + "WHERE (Products.ID=? "
            + "OR SUBSTRING(Products.ID FROM1 FOR 7)=? AND (Encoded=1 OR Encoded=2)"
            + "OR SUBSTRING(Products.ID FROM1 FOR 8)=? AND Encoded>2) AND Products.Sku=Skus.ID AND Skus.Tax=Taxes.ID AND Skus.Department=Departments.ID ORDER BY Products.ID DESC";
    private ResultSet rs;
    private int defaultMargin;
    private String getPackSupplierId = "SELECT LAST_INSERT_ID() FROM PackSuppliers";
    private int taxID;
    private String getTax = "SELECT Taxes.Rate AS Tax FROM Products, Skus,Taxes WHERE Products.Sku=Skus.ID AND Skus.Tax=Taxes.ID AND Products.ID=?";
    private int saleLoss = 0;
    private int totalExtra;
    private int taxExtra;
    private int newPackPrice;
    private int newPackSize;
    private int newRetailPrice;
    private String priceLabel;
    private int localRetailPrice;
    private int total;
    private boolean packSizeFixed = false;
    private int wholesalePrice;
    private int operator;
    private boolean shelfEdgeLabel = true;//ask if you want a label
    private int multiPack;
    private PerCent marginPerCent;
    private int newMargin;
    private int oldPackSize;
    private int linesToSkip;
    private boolean acceptZeroPrice = false;
    private boolean newProduct;

    /**
     * Creates new form DeliveryNew
     *
     * @param parent
     * @param modal
     */
    public DeliveryEntry(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) packSizeSpinner.getEditor();
        packSizeText = editor.getTextField();
        JSpinner.DefaultEditor numberOfPacksEditor = (JSpinner.DefaultEditor) numberOfPacksSpinner.getEditor();
        numberOfPacksText = numberOfPacksEditor.getTextField();
        packSizeSpinner.setRequestFocusEnabled(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Deliveryentry", Main.mainHelpSet);
        getRootPane().setDefaultButton(okButton);
    }

    void execute(int delivery) {
        jLabel2.setText(bundle.getString("PackCode"));
        saleDelivery = 0;
        sale = 0;
        operator = Main.operator.getOperator();
        packSizeSpinner.setRequestFocusEnabled(true);
        pack = 0;
        this.delivery = delivery;
        //look up the delivery
        //locate description of supplier in Suppliers
        PreparedStatement supplierName, dps;
        try {
            dps = Main.getConnection().prepareStatement(
                    "SELECT *,DATE(WhenCreated) AS theDate FROM Deliveries WHERE ID=?");
            dps.setInt(1, delivery);
            ResultSet drs = dps.executeQuery();
            if (!drs.first()) {
                drs.close();
                return;
            }
            supplier = drs.getInt("supplier");
            reference = drs.getString("Reference");
            referenceText.setText(reference);
            dateText.setText(drs.getString("theDate"));
            drs.close();
            supplierName = Main.getConnection().prepareStatement(
                    "SELECT Description FROM Suppliers WHERE ID=?");
            supplierName.setInt(1, supplier);
            rs = supplierName.executeQuery();
            if (rs.first()) {
                supplierText.setText(rs.getString("Description"));
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryEntry.class.getName()).log(Level.SEVERE, null, ex);
        }
        sale = 0;
        saleDelivery = 0;
        quantity = 0;
        oldPackCode = "";
        packSizeSpinner.setValue(1);
        numberOfPacksSpinner.setValue(1);
        productPriceText.setText("");
        productDescriptionText.setText("");
        packPriceText.setText("");
        marginText.setText("0.00%");
        packCodeText.setText("");
        packCodeText.requestFocus();
        Audio.play("Tada");
        setVisible(true);
    }

    /**
     * same as calculateMargin but getting parameters by reading text fields
     */
    private void calculateMargin() {
        int m;
        String s = StringOps.numericOnly(productPriceText.getText());
        if (s.isEmpty()) {
            productPriceText.requestFocus();
            return;
        }
        newRetailPrice = Integer.parseInt(s);
        if (newRetailPrice == 0) {
            return;
        }
        packSize = (Integer) packSizeSpinner.getValue();
        s = StringOps.numericOnly(packPriceText.getText());
        if (s.isEmpty()) {
            packPriceText.requestFocus();
            return;
        }
        newPackPrice = Integer.parseInt(s);
        Regime rr = Main.shop.regimeIs();
        if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
            margin = 10000 - (newPackPrice * (10000 + rate)) / ((packSize) * newRetailPrice);
        } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
            margin = 10000 - newPackPrice * 10000 / (packSize * newRetailPrice);
        }
        marginText.setText((new PerCent(margin)).toString());
    }

    private boolean createNewPack() throws NumberFormatException, SQLException {
        if (!packCode.equalsIgnoreCase(oldPackCode)) {
            //no need to save oldPackCode as will be done on return;
            Audio.play("Ring");
            return false;
        }
        encode = Main.selectEncoded.execute();
        if (encode < 0) {//was cancelled
            packCodeText.setText("");
            packCodeText.requestFocus();
            packCode = "";
            Audio.play("Ring");
            return true;
        }
        if (encode == NewProduct.NOTENCODE) {
//                        packCodeText.setText("");
//                        packCodeText.requestFocus();
//                        AudioPlayer.play("Ring");
            return false;
        }
        //see if product exists
        productStatement = Main.getConnection().prepareStatement(selectProduct);
        //try all three possibilities
        productStatement.setString(1, packCode);
        productStatement.setString(2, NewProduct.shortenForEncoded7(packCode));
        productStatement.setString(3, NewProduct.shortenForEncoded8(packCode));
        rs = productStatement.executeQuery();
        if (rs.first()) {
            //product exists
            product = rs.getLong("Products.ID");
            if (encode != rs.getInt("Encoded")) {
                //need to correct or abort
                packCodeText.setText("");
                packCodeText.requestFocus();
                rs.close();
                return true;
            }
            retailPrice = rs.getInt("Price");
            sku = rs.getInt("Sku");
            defaultMargin = rs.getInt("Departments.Margin");
            rs.close();
        } else {
            rs.close();
            product = Long.parseLong(StringOps.maxLength(packCode));
            product = Main.newProduct.execute(product, encode, false);
            if (product == 0L) {
                packCodeText.setText("");
                packCode = "";
                packCodeText.requestFocus();
                return true;
            }
            //need to create product
            //reopen productStatment to get values
            rs = productStatement.executeQuery();
            if (rs.first()) {
                //product exists
                defaultMargin = rs.getInt("Departments.Margin");
                retailPrice = rs.getInt("Price");
                sku = rs.getInt("Sku");
                rate = rs.getInt("Rate");
                rs.close();
            }
            rs.close();
        }
        //Create a new pack supplier with pack and supplier known
        packStatement.close();
        if (encode == NewProduct.ENCODEBYWEIGHTPARITY || encode == NewProduct.ENCODEBYWEIGHTNOPARITY) {
            priceLabel = bundle.getString("ByWeightPrice");
        } else if (encode == NewProduct.ENCODEBYPRICEPARITY || encode == NewProduct.ENCODEBYPRICENOPARITY) {
            priceLabel = bundle.getString("ByPricePrice") + Main.shop.poundSymbol + "1.00:";
        } else {
            priceLabel = bundle.getString("packPrice");
        }
        Regime rr = Main.shop.regimeIs();
        if (encode == NewProduct.ENCODEBYPRICEPARITY || encode == NewProduct.ENCODEBYPRICENOPARITY) {
            localRetailPrice = 100;
        } else {
            localRetailPrice = retailPrice;
        }
        if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
            packPrice = (localRetailPrice * 10 + 5) * (1000 - defaultMargin) / (10000 + rate);
        } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
            packPrice = (localRetailPrice * 10 + 5) * (1000 - defaultMargin) / 10000;
        }
        packPrice = Main.selectPrice.execute(priceLabel, packPrice);
        if (packPrice <= 0) {//was cancelled
            packCodeText.setText("");
            packCodeText.requestFocus();
            packCode = "";
            Audio.play("Ring");
            return true;
        }
        //need to create a pack 
        packStatement = Main.getConnection().prepareStatement(createPack);
        packStatement.setNull(1, Types.INTEGER);
        packStatement.setLong(2, product);//Product
        packStatement.setLong(3, product);//Code
        packStatement.setInt(4, encode);
        if (packStatement.executeUpdate() != 1) {
            return false;
        }
        packSize = 1;
        numberOfPacks = encodedData;
        rs.close();
        ps = Main.getConnection().prepareStatement(getPackId);
        rs = ps.executeQuery();
        rs.first();
        pack = rs.getInt(1);
        rs.close();
        packStatement = Main.getConnection().prepareStatement(createPackSupplier);
        packStatement.setNull(1, Types.INTEGER);
        packStatement.setInt(2, pack);
        packStatement.setInt(3, packPrice);
        packStatement.setInt(4, supplier);
        packStatement.executeUpdate();
        //get packSupplierID
        //need to look at product
        ps = Main.getConnection().prepareStatement(getPackSupplierId);
        rs = ps.executeQuery();
        rs.first();
        packSupplier = rs.getInt(1);
        rs.close();
        productStatement = Main.getConnection().prepareStatement(selectProduct);
        productStatement.setLong(1, product);
        productStatement.setLong(2, product);
        productStatement.setLong(3, product);
        rs = productStatement.executeQuery();
        if (rs.first()) {
            //product exists
            int e = rs.getInt("Encoded");
            if (encode != rs.getInt("Encoded")) {
                rs.close();
                packCodeText.setText("");
                packCodeText.requestFocus();
                Audio.play("Ring");
                return true;
            }
            productDescription = rs.getString("Description");
            retailPrice = rs.getInt("Price");
            packCodeText.setText("" + product);
            taxID = rs.getInt("Tax");
            rate = rs.getInt("Rate");
            rs.close();
        } else {
            //product must be created
        }
        fillInDetails();
        packSizeSpinner.requestFocus();//clears data
        packSizeText.requestFocus();
        packSizeFixed = true;
        return true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        packCodeText = new javax.swing.JTextField();
        packSizeSpinner = new javax.swing.JSpinner();
        numberOfPacksSpinner = new javax.swing.JSpinner();
        packPriceText = new javax.swing.JTextField();
        productPriceText = new javax.swing.JTextField();
        stockOnShelfSpinner = new javax.swing.JSpinner();
        marginText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        supplierText = new javax.swing.JTextField();
        productDescriptionText = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        referenceText = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        dateText = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        closeButton2 = new javax.swing.JButton();
        barcodeCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/DeliveryEntry"); // NOI18N
        setTitle(bundle.getString("DeliveryEntry.title")); // NOI18N
        setName("DeliveryEntry"); // NOI18N

        jLabel1.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel1.setText(bundle.getString("DeliveryEntry.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel2.setText(bundle.getString("DeliveryEntry.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        packCodeText.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        packCodeText.setName("packCodeText"); // NOI18N
        packCodeText.setNextFocusableComponent(packSizeSpinner);
        packCodeText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                packCodeTextFocusLost(evt);
            }
        });
        packCodeText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                packCodeTextKeyReleased(evt);
            }
        });

        packSizeSpinner.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        packSizeSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, null, 1));
        packSizeSpinner.setName("packSizeSpinner"); // NOI18N
        packSizeSpinner.setNextFocusableComponent(numberOfPacksSpinner);
        packSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                packSizeSpinnerStateChanged(evt);
            }
        });
        packSizeSpinner.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                packSizeSpinnerFocusLost(evt);
            }
        });
        packSizeSpinner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                packSizeSpinnerKeyReleased(evt);
            }
        });

        numberOfPacksSpinner.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        numberOfPacksSpinner.setModel(new javax.swing.SpinnerNumberModel());
        numberOfPacksSpinner.setName("numberOfPacksSpinner"); // NOI18N
        numberOfPacksSpinner.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                numberOfPacksSpinnerFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                numberOfPacksSpinnerFocusLost(evt);
            }
        });
        numberOfPacksSpinner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                numberOfPacksSpinnerKeyReleased(evt);
            }
        });

        packPriceText.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        packPriceText.setName("packPriceText"); // NOI18N
        packPriceText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                packPriceTextFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                packPriceTextFocusLost(evt);
            }
        });
        packPriceText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                packPriceTextKeyReleased(evt);
            }
        });

        productPriceText.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        productPriceText.setName("productPriceText"); // NOI18N
        productPriceText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                productPriceTextFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                productPriceTextFocusLost(evt);
            }
        });
        productPriceText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                productPriceTextKeyReleased(evt);
            }
        });

        stockOnShelfSpinner.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        stockOnShelfSpinner.setModel(new javax.swing.SpinnerNumberModel());
        stockOnShelfSpinner.setName("stockOnShelfSpinner"); // NOI18N

        marginText.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        marginText.setName("marginText"); // NOI18N
        marginText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                marginTextFocusLost(evt);
            }
        });
        marginText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                marginTextKeyReleased(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel3.setText(bundle.getString("DeliveryEntry.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel4.setText(bundle.getString("DeliveryEntry.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel5.setText(bundle.getString("DeliveryEntry.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel8.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel8.setText(bundle.getString("DeliveryEntry.jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel6.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel6.setText(bundle.getString("DeliveryEntry.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel7.setText(bundle.getString("DeliveryEntry.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel9.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel9.setText(bundle.getString("DeliveryEntry.jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        supplierText.setEditable(false);
        supplierText.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        supplierText.setFocusable(false);
        supplierText.setName("supplierText"); // NOI18N

        productDescriptionText.setEditable(false);
        productDescriptionText.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        productDescriptionText.setFocusable(false);
        productDescriptionText.setName("productDescriptionText"); // NOI18N

        jLabel10.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel10.setText(bundle.getString("DeliveryEntry.jLabel10.text")); // NOI18N
        jLabel10.setFocusable(false);
        jLabel10.setName("jLabel10"); // NOI18N

        referenceText.setEditable(false);
        referenceText.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        referenceText.setFocusable(false);
        referenceText.setName("referenceText"); // NOI18N

        jLabel11.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        jLabel11.setText(bundle.getString("DeliveryEntry.jLabel11.text")); // NOI18N
        jLabel11.setFocusable(false);
        jLabel11.setName("jLabel11"); // NOI18N

        dateText.setEditable(false);
        dateText.setFont(new java.awt.Font("Times New Roman", 0, 18)); // NOI18N
        dateText.setFocusable(false);
        dateText.setName("dateText"); // NOI18N

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/OK.png"))); // NOI18N
        okButton.setToolTipText(bundle.getString("DeliveryEntry.okButton.toolTipText")); // NOI18N
        okButton.setContentAreaFilled(false);
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Minus.png"))); // NOI18N
        cancelButton.setToolTipText(bundle.getString("DeliveryEntry.cancelButton.toolTipText")); // NOI18N
        cancelButton.setContentAreaFilled(false);
        cancelButton.setName("cancelButton"); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        closeButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton2.setToolTipText(bundle.getString("DeliveryEntry.closeButton2.toolTipText")); // NOI18N
        closeButton2.setContentAreaFilled(false);
        closeButton2.setName("closeButton2"); // NOI18N
        closeButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButton2ActionPerformed(evt);
            }
        });

        barcodeCheckBox.setText(bundle.getString("DeliveryEntry.barcodeCheckBox.text")); // NOI18N
        barcodeCheckBox.setToolTipText(bundle.getString("DeliveryEntry.barcodeCheckBox.toolTipText")); // NOI18N
        barcodeCheckBox.setName("barcodeCheckBox"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(numberOfPacksSpinner, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(packPriceText, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(productPriceText, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(barcodeCheckBox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(stockOnShelfSpinner)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(productDescriptionText)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(marginText, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(190, 190, 190)
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(packCodeText, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)
                            .addComponent(supplierText))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(packSizeSpinner)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel10)
                                    .addComponent(referenceText, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(dateText, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, closeButton2, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11))
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(supplierText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(referenceText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(packCodeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(packSizeSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(numberOfPacksSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(packPriceText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(productPriceText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(productDescriptionText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(barcodeCheckBox)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(stockOnShelfSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(marginText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(okButton, 0, 22, Short.MAX_VALUE)
                        .addComponent(cancelButton, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {packCodeText, packSizeSpinner});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {referenceText, supplierText});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {cancelButton, closeButton2, okButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
    }

    /**
     *
     * @param evt
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        ok();
}//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        packSizeSpinner.setValue(1);
        numberOfPacksSpinner.setValue(1);
        productPriceText.setText("");
        productDescriptionText.setText("");
        packPriceText.setText("");
        marginText.setText("");
        oldPackCode = "";
        packCodeText.setText("");
        stockOnShelfSpinner.setValue(0);
        sku = 0;
        packCodeText.requestFocus();
        Audio.play("Beep");
        jLabel2.setText(bundle.getString("PackCode"));
}//GEN-LAST:event_cancelButtonActionPerformed

    private void marginTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_marginTextFocusLost
    }//GEN-LAST:event_marginTextFocusLost

    private void packPriceTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_packPriceTextFocusGained
        numberOfPacks = (Integer) numberOfPacksSpinner.getValue();
        newPackSize = Integer.parseInt(packSizeText.getText());
        calculateMargin();
        packSizeText.selectAll();
        getRootPane().setDefaultButton(okButton);
    }//GEN-LAST:event_packPriceTextFocusGained

    private void productPriceTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_productPriceTextFocusGained
        if (productPriceText.getText().isEmpty()) {
            return;
        }
        String s = StringOps.numericOnly(packPriceText.getText().trim());
        packSize = (Integer) packSizeSpinner.getValue();
        if (s.isEmpty()) {
            return;
        }
        newPackPrice = Integer.parseInt(s);
        calculateMargin();
    }//GEN-LAST:event_productPriceTextFocusGained

    public String addSupplierToCode(String code, int supplier) {
        String returnCode = code;
        if (code.contains("+")) {
            //do not add code twice
            returnCode = code;
        } else if (returnCode.length() < 8) {
            returnCode = supplier + "+" + returnCode;
        }
        return returnCode;
    }

    private void packCodeTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_packCodeTextFocusLost
        newProduct = false;
        packSizeFixed = false;
        encode = NewProduct.NOTENCODE;
        packCode = packCodeText.getText().toUpperCase().trim();
        if (packCode.isEmpty()) {
            packCodeText.requestFocus();
            return;
        }
//        if(packCode.length()>=22){
//            packCode = packCode.substring(0, 22);
//        }
        if (StringOps.isNumericOnly(packCode)) {
            packCode = StringOps.maxLength(packCode);
            if (packCode.isEmpty()) {
                packCodeText.requestFocus();
                return;
            }
        }
        packCode = addSupplierToCode(packCode, supplier);//adds supplyer ID to short (<8) codes
        packCodeText.setText(packCode);
        if (encoded()) {
            return;
        }
        //look up pack code
        try {
            packStatement = Main.getConnection().prepareStatement(selectPack);
            packStatement.setString(1, packCode);
            packStatement.setString(2, NewProduct.shortenForEncoded7(packCode));
            packStatement.setString(3, NewProduct.shortenForEncoded8(packCode));
            rs = packStatement.executeQuery();
            if (rs.first()) {//packCode recognised
                packCode = rs.getString("Code");
                pack = rs.getInt("Packs.ID");
                packSize = rs.getInt("Packs.Size");
                packSizeSpinner.setValue(packSize);
                newPackSize = packSize;
                numberOfPacksSpinner.setValue(1);
                retailPrice = rs.getInt("Products.Price");
                productPriceText.setText((new Money(retailPrice)).toString());
                newRetailPrice = retailPrice;
                productDescription = rs.getString("Products.Description");
                productDescriptionText.setText(productDescription);
                rate = rs.getInt("Rate");
                taxID = rs.getInt("Tax");
                quantity = rs.getInt("Skus.Quantity");
                stockOnShelfSpinner.setValue(quantity);
                product = rs.getLong("Packs.Product");
                sku = rs.getInt("Products.Sku");
                encode = rs.getInt("Packs.Encoded");
                oldPackCode = "";
                multiPack = rs.getInt("MultiPack");
                rs.close();
                //look up PackSupplier
                PreparedStatement packSupplierStatement = Main.getConnection().prepareStatement(
                        "SELECT * FROM PackSuppliers WHERE Pack=? AND Supplier=?");
                packSupplierStatement.setInt(1, this.pack);
                packSupplierStatement.setInt(2, supplier);
                packSizeSpinner.requestFocus();
                packSizeText.requestFocus();
                rs = packSupplierStatement.executeQuery();
                if (rs.first()) {//PackSupplier exists
                    packPrice = rs.getInt("Price");
                    originalPackPrice = packPrice;
                    packSupplier = rs.getInt("ID");
                    packPriceText.setText((new Money(packPrice)).toString());
                    rs.close();
                    packSupplierStatement.close();
                } else {//not recognized
                    rs.close();
                    packSupplierStatement.close();
                    if (encode == NewProduct.NOTENCODE) {
                        packSupplier = Main.newPackSupplier.execute(packCode, supplier);
                        if (packSupplier == 0) {
                            oldPackCode = "";
                            packSizeSpinner.setValue(1);
                            numberOfPacksSpinner.setValue(1);
                            productPriceText.setText("");
                            productDescriptionText.setText("");
                            packPriceText.setText("");
                            marginText.setText("");
                            oldPackCode = "";
                            packCodeText.setText("");
                            stockOnShelfSpinner.setValue(0);
                            sku = 0;
                            packCodeText.requestFocus();
                            return;
                        }
                    } else {//should not be here
                        packCodeText.setText("");
                        packCodeText.requestFocus();
                        return;
//                        packSupplier = Main.newPackSupplier.execute(NewProduct.shortenForEncoded(packCode), supplier);
                    }
                    PreparedStatement newPackSupplierStatement = Main.getConnection().prepareStatement(
                            "SELECT PackSuppliers.*,Packs.Size FROM PackSuppliers,Packs WHERE PackSuppliers.Pack=Packs.ID AND PackSuppliers.ID=?");
                    newPackSupplierStatement.setInt(1, packSupplier);
                    rs = newPackSupplierStatement.executeQuery();
                    if (rs.first()) {
                        packPrice = rs.getInt("Price");
                        newPackPrice = packPrice;
                        packSize = rs.getInt("Packs.Size");
                        newPackSize = packSize;
                        packSizeSpinner.setValue(packSize);
                        packPriceText.setText((new Money(packPrice)).toString());
                        newPackSupplierStatement.close();
                    }
                    rs.close();
                    packCodeText.requestFocus();
                    Audio.play("Beep");
                    return;
                }
                calculateMargin();
                oldPackCode = "";
                packSizeSpinner.requestFocus();
                packSizeText.requestFocus();
                Audio.play("Beep");
            } else { //packCode not recognised
                if (packCode.compareToIgnoreCase(oldPackCode) == 0) {
                    //new packCode
                    oldPackCode = "";
                    jLabel2.setText(bundle.getString("PackCode"));
                    pack = Main.newPack.execute(packCode, false);//pack is the id
                    if (pack == 0) {
                        oldPackCode = "";
                        packCodeText.setText("");
                        packCodeText.requestFocus();
                        return;
                    }
                    packStatement = Main.getConnection().prepareStatement(
                            "SELECT Packs.*,Products.*,Skus.Quantity,Skus.Tax,Taxes.Rate FROM Packs,Products,Skus,Taxes WHERE (Packs.ID)=? AND Packs.Product=Products.ID AND Products.Sku=Skus.ID AND Taxes.ID=Skus.Tax");
                    packStatement.setInt(1, pack);
                    rs = packStatement.executeQuery();
                    if (rs.first()) {//packCode recognised
                        packSize = rs.getInt("Packs.Size");
                        packSizeSpinner.setValue(packSize);
                        numberOfPacksSpinner.setValue(1);
                        retailPrice = rs.getInt("Products.Price");
                        productPriceText.setText((new Money(retailPrice)).toString());
                        newRetailPrice = retailPrice;
                        productDescriptionText.setText(rs.getString("Products.Description"));
                        packSizeSpinner.requestFocus();
                        packSizeText.requestFocus();
                        taxID = rs.getInt("Tax");
                        rate = rs.getInt("Rate");
                        quantity = rs.getInt("Skus.Quantity");
                        stockOnShelfSpinner.setValue(quantity);
                        product = rs.getLong("Packs.Product");
                        sku = rs.getInt("Products.Sku");
                        oldPackCode = "";
                        multiPack = rs.getInt("MultiPack");
                        encode = rs.getInt("Packs.Encoded");
                        if (encode != NewProduct.NOTENCODE) {
                            quantity = NewProduct.getEncodedData4(packCode);
                            numberOfPacksSpinner.setValue(quantity);
                        }
                        rs.close();
                        //look up PackSupplier
                        PreparedStatement packSupplierStatement = Main.getConnection().prepareStatement(
                                "SELECT * FROM PackSuppliers WHERE Pack=? AND Supplier=?");
                        packSupplierStatement.setInt(1, pack);
                        packSupplierStatement.setInt(2, supplier);
                        packSizeSpinner.requestFocus();
                        packSizeText.requestFocus();
                        rs = packSupplierStatement.executeQuery();
                        if (rs.first()) {//PackSupplier exists
                            packPrice = rs.getInt("Price");
                            packSupplier = rs.getInt("ID");
                            packPriceText.setText((new Money(packPrice)).toString());
                            packSupplierStatement.close();
                            Audio.play("Beep");
                        } else {
                            packSupplierStatement.close();
                            packSupplier = Main.newPackSupplier.execute(packCode, supplier);
                            if (packSupplier == 0) {
                                jLabel2.setText(bundle.getString("PackCode"));
                                oldPackCode = "";
                                packSizeSpinner.setValue(1);
                                numberOfPacksSpinner.setValue(1);
                                productPriceText.setText("");
                                productDescriptionText.setText("");
                                packPriceText.setText("");
                                marginText.setText("");
                                oldPackCode = "";
                                packCodeText.setText("");
                                stockOnShelfSpinner.setValue(0);
                                sku = 0;
                                packCodeText.requestFocus();
                                return;
                            } else {
                                PreparedStatement newPackSupplierStatement = Main.getConnection().prepareStatement(
                                        "SELECT PackSuppliers.*,Packs.Size FROM PackSuppliers,Packs WHERE PackSuppliers.Pack=Packs.ID AND PackSuppliers.ID=?");
                                newPackSupplierStatement.setInt(1, packSupplier);
                                rs = newPackSupplierStatement.executeQuery();
                                if (rs.first()) {
                                    packPrice = rs.getInt("Price");
                                    packSize = rs.getInt("Packs.Size");
                                    packSizeSpinner.setValue(packSize);
                                    packPriceText.setText((new Money(packPrice)).toString());
                                    newPackSupplierStatement.close();
                                    packCodeText.setText(packCode);
                                } else {//PackSupplier not regognised
                                    //should not happen
                                    packSizeSpinner.setValue(1);
                                    numberOfPacksSpinner.setValue(1);
                                    productPriceText.setText("");
                                    productDescriptionText.setText("");
                                    packPriceText.setText("");
                                    marginText.setText("");
                                    oldPackCode = packCode;
                                    packCodeText.setText("");
                                    stockOnShelfSpinner.setValue(0);
                                    sku = 0;
                                    packCodeText.requestFocus();
                                    Audio.play("Ring");
                                    return;
                                }
                                Regime rr = Main.shop.regimeIs();
                                if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                                    margin = (10000 - packPrice * (10000 + rate) / (packSize * retailPrice));
                                } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                                    margin = 10000 - packPrice * 10000 / (packSize * retailPrice);
                                }
                                marginText.setText((new PerCent(margin)).toString());
                                packSizeSpinner.requestFocus();
                                packSizeText.requestFocus();
                                packCodeText.setText(packCode);
                            }
                        }
                    } else {
                        //should not happen
                        packSizeSpinner.setValue(1);
                        numberOfPacksSpinner.setValue(1);
                        productPriceText.setText("");
                        productDescriptionText.setText("");
                        packPriceText.setText("");
                        marginText.setText("");
                        oldPackCode = packCode;
                        packCodeText.setText("");
                        stockOnShelfSpinner.setValue(0);
                        sku = 0;
                        packCodeText.requestFocus();
                        Audio.play("Ring");
                    }
                } else {//need to scan again
                    jLabel2.setText(bundle.getString("ScanAgain"));
                    oldPackCode = packCode;
                    packCodeText.requestFocus();
                    packCodeText.setText("");
                    Audio.play("Ring");
                }
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryEntry.class.getName()).log(Level.SEVERE, null, ex);
            packCodeText.setBackground(Color.YELLOW);
        }
        if (encode != NewProduct.NOTENCODE) {
//            int noOfPacks = NewProduct.getEncodedData4(packCodeText.getText().toUpperCase().trim());
//            numberOfPacksSpinner.setValue(NewProduct.getEncodedData4(packCodeText.getText().toUpperCase().trim()));
        }
        packSizeText.requestFocus();
    }//GEN-LAST:event_packCodeTextFocusLost

    private void productPriceTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_productPriceTextFocusLost
        if (packCodeText.getText().isEmpty()) {
            return;
        }
        String s = StringOps.numericOnly(productPriceText.getText().trim());
        if (s.isEmpty()) {
            return;
        }
        newRetailPrice = Integer.parseInt(s);
        productPriceText.setText((new Money(newRetailPrice)).toString());
        calculateMargin();
    }//GEN-LAST:event_productPriceTextFocusLost

    private void closeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButton2ActionPerformed

    private void packSizeSpinnerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_packSizeSpinnerFocusLost
        if (packSizeFixed) {
            packSizeSpinner.setValue(packSize);
        }
        packSize = newPackSize;
        calculateMargin();
        packSize = oldPackSize;
    }//GEN-LAST:event_packSizeSpinnerFocusLost

    private void numberOfPacksSpinnerFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_numberOfPacksSpinnerFocusLost
        int newPackSize = Integer.parseInt(packSizeText.getText());
        if (newPackSize != packSize) {
            //need to recalculate margin
            oldPackSize = packSize;
            packSize = newPackSize;
            calculateMargin();
            packSize = oldPackSize;
        }
    }//GEN-LAST:event_numberOfPacksSpinnerFocusLost

    private void packPriceTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_packPriceTextKeyReleased
        Money.asMoney(packPriceText);
        calculateMargin();
        getRootPane().setDefaultButton(okButton);
    }//GEN-LAST:event_packPriceTextKeyReleased

    private void productPriceTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_productPriceTextKeyReleased
        Money.asMoney(productPriceText);
        calculateMargin();
    }//GEN-LAST:event_productPriceTextKeyReleased

    private void packSizeSpinnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_packSizeSpinnerKeyReleased
    }//GEN-LAST:event_packSizeSpinnerKeyReleased

    /**
     *
     * @param evt
     */
    private void numberOfPacksSpinnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numberOfPacksSpinnerKeyReleased
        ok();
    }//GEN-LAST:event_numberOfPacksSpinnerKeyReleased

    private void packCodeTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_packCodeTextKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            packCodeText.setText("");
        }
    }//GEN-LAST:event_packCodeTextKeyReleased

    private void packPriceTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_packPriceTextFocusLost
        if (!packCodeText.getText().isEmpty()) {
            String s = packPriceText.getText();
            s = StringOps.numericOnly(packPriceText.getText());
            newPackPrice = Integer.parseInt(StringOps.numericOnly(packPriceText.getText()));
            calculateMargin();
        }
    }//GEN-LAST:event_packPriceTextFocusLost

    private void numberOfPacksSpinnerFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_numberOfPacksSpinnerFocusGained
        if (packSizeFixed) {
            newPackSize = packSize;
            packSizeText.setText("" + packSize);
        }
        newPackSize = Integer.parseInt(packSizeText.getText());
        calculateMargin();
    }//GEN-LAST:event_numberOfPacksSpinnerFocusGained

    private void packSizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_packSizeSpinnerStateChanged
        if (packSizeFixed) {
            newPackSize = packSize;
            packSizeText.setText("" + packSize);
            packSizeSpinner.setValue(packSize);
        }
        calculateMargin();
    }//GEN-LAST:event_packSizeSpinnerStateChanged

    private void marginTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_marginTextKeyReleased
        PerCent.asPerCent(marginText);
        marginPerCent = new PerCent(marginText.getText());
        newMargin = marginPerCent.getValue();
        if (margin != newMargin) {
            margin = newMargin;
            //re-calclate retail price
            Regime rr = Main.shop.regimeIs();
            if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                newRetailPrice = packPrice * (10000 + rate) / ((packSize) * (10000 - margin));
            } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                newRetailPrice = 10000 * packPrice / ((packSize) * (10000 - margin));
            }
            productPriceText.setText((new Money(newRetailPrice)).toString());
            marginText.setText((new PerCent(margin)).toString());
        }
    }//GEN-LAST:event_marginTextKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                DeliveryEntry dialog = new DeliveryEntry(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
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
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton closeButton2;
    private javax.swing.JTextField dateText;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField marginText;
    private javax.swing.JSpinner numberOfPacksSpinner;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField packCodeText;
    private javax.swing.JTextField packPriceText;
    private javax.swing.JSpinner packSizeSpinner;
    private javax.swing.JTextField productDescriptionText;
    private javax.swing.JTextField productPriceText;
    private javax.swing.JTextField referenceText;
    private javax.swing.JSpinner stockOnShelfSpinner;
    private javax.swing.JTextField supplierText;
    // End of variables declaration//GEN-END:variables

    private void updateDeliveryTable() {
        //need to add total and tax
        try {
            if (delivery == 0) {
                String id = "INSERT INTO Deliveries (ID,WhenCreated,Reference,Supplier) "
                        + "VALUES(null,null,?,?,Total+?,Tax+?)";
                ps = Main.getConnection().prepareStatement(id);
                ps.setString(1, reference);
                ps.setInt(2, supplier);
                ps.executeUpdate();
                String ld = "SELECT LAST_INSERT_ID() FROM Deliveries";
                PreparedStatement gd = Main.getConnection().prepareStatement(ld);
                rs = gd.executeQuery();
                if (rs.first()) {
                    delivery = rs.getInt(1);
                }
                rs.close();
            }
            //now save the line
            String idl = "INSERT INTO DeliveryLines "
                    + "(ID,Delivery,Operator,Quantity,Product,Pack,PackSupplier,Price,PackSize,PackPrice) "
                    + "VALUES(null,?,?,?,?,?,?,?,?,?)";
            PreparedStatement idlps = Main.getConnection().prepareStatement(idl);
            idlps.setInt(1, delivery);
            operator = Main.operator.getOperator();
            idlps.setInt(2, operator);
            idlps.setInt(3, numberOfPacks);
            idlps.setLong(4, product);
            idlps.setInt(5, pack);
            idlps.setInt(6, packSupplier);
            idlps.setInt(7, newRetailPrice);
            idlps.setInt(8, packSize);
            idlps.setInt(9, packPrice);
            idlps.executeUpdate();
            //now need to upfate total and tax
            String ud = "UPDATE Deliveries SET Total=Total+?,Tax=Tax+? WHERE ID=?";
            ps = Main.getConnection().prepareStatement(ud);
            total = numberOfPacks * packPrice;
            ps.setInt(1, total);
            ps.setInt(2, Tax.calculateTax(total, rate));
            ps.setInt(3, delivery);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(DeliveryEntry.class.getName()).log(Level.SEVERE, null, ex);
            packCodeText.setBackground(Color.YELLOW);
        }
    }

    private boolean encoded() {
        String recognisedPackCode;
        if (NewProduct.couldBeEncoded(packCode)) {
            try {
                encodedData = NewProduct.getEncodedData4(packCode);
                //see if pack exists
                packStatement = Main.getConnection().prepareStatement(selectPack);
                //try three possibilities
                packStatement.setString(1, packCode);//not encoded
                packStatement.setString(2, NewProduct.shortenForEncoded7(packCode));//encoded 8 char
                packStatement.setString(3, NewProduct.shortenForEncoded8(packCode));//encoded 7 char
                rs = packStatement.executeQuery();
                if (rs.first()) {
                    //packCode recognised, product must exist
                    recognisedPackCode = rs.getString("Code");//get the recognised one
                    encode = rs.getInt("Packs.Encoded");
                    packCodeText.setText(packCode);
                    pack = rs.getInt("Packs.ID");
                    packSize = rs.getInt("Packs.Size");
                    retailPrice = rs.getInt("Products.Price");
                    productPriceText.setText((new Money(retailPrice)).toString());
                    productDescription = rs.getString("Products.Description");
                    productDescriptionText.setText(productDescription);
                    defaultMargin = rs.getInt("Departments.Margin");
                    taxID = rs.getInt("Tax");
                    rate = rs.getInt("Rate");
                    quantity = rs.getInt("Skus.Quantity");
                    stockOnShelfSpinner.setValue(quantity);
                    product = rs.getLong("Packs.Product");
                    sku = rs.getInt("Products.Sku");
                    numberOfPacks = encodedData;
                    oldPackCode = "";
                    packStatement.close();
                    //look up PackSupplier
                    packStatement = Main.getConnection().prepareStatement(selectPackSupplier);
                    packStatement.setInt(1, pack);
                    packStatement.setInt(2, supplier);
                    rs = packStatement.executeQuery();
                    if (rs.first()) {
                        //packSupplier found
                        packSupplier = rs.getInt("ID");
                        packPrice = rs.getInt("Price");
                        rs.close();
                        fillInDetails();
                        packSizeSpinner.requestFocus();
                        packSizeText.requestFocus();
                        Audio.play("Beep");
                        packSizeFixed = true;
                        return true;
                    } else {
                        //Create a new pack supplier with pack and supplier known
                        packStatement.close();
                        if (encode == NewProduct.ENCODEBYWEIGHTPARITY || encode == NewProduct.ENCODEBYWEIGHTNOPARITY) {
                            priceLabel = bundle.getString("ByWeightPrice");
                        } else if (encode == NewProduct.ENCODEBYPRICEPARITY || encode == NewProduct.ENCODEBYPRICENOPARITY) {
                            priceLabel = bundle.getString("ByPricePrice") + Main.shop.poundSymbol + "1.00:";
                        } else {
                            priceLabel = bundle.getString("packPrice");
                        }
                        if (encode == NewProduct.ENCODEBYPRICEPARITY || encode == NewProduct.ENCODEBYPRICENOPARITY) {
                            localRetailPrice = 100;

                        } else {
                            localRetailPrice = retailPrice;
                        }
                        Regime rr = Main.shop.regimeIs();
                        if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                            packPrice = (localRetailPrice * 10 + 5) * (1000 - defaultMargin) / (10000 + rate);
                        } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                            packPrice = (localRetailPrice * 10 + 5) * (1000 - defaultMargin) / 10000;
                        }
                        packPrice = Main.selectPrice.execute(priceLabel, packPrice);
                        if (packPrice <= 0) {
                            packCodeText.setText("");
                            packCodeText.requestFocus();
                            return true;
                        }
                        packStatement = Main.getConnection().prepareStatement(createPackSupplier);
                        packStatement.setNull(1, Types.INTEGER);
                        packStatement.setInt(2, pack);
                        packStatement.setInt(3, packPrice);
                        packStatement.setInt(4, supplier);
                        packStatement.execute();
                        rs.close();
                        PreparedStatement np1 = Main.getConnection().prepareStatement(
                                "SELECT LAST_INSERT_ID() FROM PackSuppliers");
                        rs = np1.executeQuery();
                        rs.first();
                        packSupplier = rs.getInt(1);//needs to be a different sale
                        rs.close();
                        numberOfPacks = encodedData;
                        fillInDetails();
                        packSizeSpinner.requestFocus();
                        packSizeText.requestFocus();
                        Audio.play("Beep");
                        packSizeText.requestFocus();
                        packSizeFixed = true;
                        return true;
                    }
                } else { //pack code not recognised
                    rs.close();
                    return createNewPack();
                }
            } catch (SQLException ex) {
                Logger.getLogger(DeliveryEntry.class.getName()).log(Level.SEVERE, null, ex);
                packCodeText.setBackground(Color.YELLOW);
                return false;
            }
        } else {
            return false;
        }
    }

    private void fillInDetails() {
//        calculateMargin(retailPrice, packPrice, 1);
        if (encode == NewProduct.NOTENCODE) {
            packSizeSpinner.setValue(packSize);
            numberOfPacksSpinner.setValue(1);
        } else {
            packSizeSpinner.setValue(1);
            numberOfPacksSpinner.setValue(numberOfPacks);
        }
        packPriceText.setText((new Money(packPrice)).toString());
        newPackPrice = packPrice;
        productPriceText.setText((new Money(retailPrice)).toString());
        productDescriptionText.setText(productDescription);
        stockOnShelfSpinner.setValue(quantity);
        calculateMargin();
//        packSizeSpinner.requestFocus();
        packSizeText.requestFocus();
    }

    /**
     * the work done on clicking OK
     */
    private void ok() {
        if (Main.operator.getIntAuthority() == 4) {//trainee
            packSizeSpinner.setValue(1);
            numberOfPacksSpinner.setValue(1);
            productPriceText.setText("");
            productDescriptionText.setText("");
            packPriceText.setText("");
            marginText.setText("");
            packCodeText.setText("");
            stockOnShelfSpinner.setValue(0);
            sku = 0;
            quantity = 0;
            packCodeText.requestFocus();
            Audio.play("Beep");
            return;//trainee
        }
        if (packSupplier == 0 || pack == 0) {
            //should have been defined
            Audio.play("Ring");
            return;
        }
        String s = StringOps.numericOnly(productPriceText.getText().trim());
        if (s.isEmpty()) {
            productPriceText.requestFocus();
            return;
        }
        acceptZeroPrice = false;
        newRetailPrice = Integer.parseInt(s);
        if (retailPrice != newRetailPrice && newRetailPrice == 0 || newProduct) {
            //check for certainty
            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(null,
                    "Zero price for this item? ",
                    "Print?",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (n == JOptionPane.YES_OPTION) {
                acceptZeroPrice = true;                
            } else {
                newRetailPrice = retailPrice;
            }
        }
        if (retailPrice != newRetailPrice && (newRetailPrice != 0 || acceptZeroPrice)) {
            try {
                //update retail price
                PreparedStatement updateProductPrice = Main.getConnection().prepareStatement("UPDATE Products SET Price=? WHERE ID=?");
                updateProductPrice.setLong(2, product);
                updateProductPrice.setInt(1, newRetailPrice);
                updateProductPrice.executeUpdate();
                retailPrice = newRetailPrice;
                //Need to print new shelf edge label
                if (shelfEdgeLabel) {
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
                        String s1 = productDescription;
                        Main.receiptPrinter.printLargeCentralLine(s1);
                        s1 = (new Money(retailPrice).toString());
                        Main.receiptPrinter.printLargeCentralLine(s1);
                        for (int i = 0; i < blankLinesAfterPrinting; i++) {
                            Main.receiptPrinter.printLine(" ");
                        }
                        Main.receiptPrinter.endPrinter();
                    } else {
                        //not again
                        shelfEdgeLabel = false;
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(DeliveryEntry.class.getName()).log(Level.SEVERE, null, ex);
                packCodeText.setBackground(Color.YELLOW);
                Audio.play("Beep");
            }
        }
        String s2 = StringOps.numericOnly(packPriceText.getText().trim());
//        packSize = (Integer) packSizeSpinner.getValue();
        if (s2.isEmpty()) {
            packPriceText.requestFocus();
            return;
        }
        int pkp = Integer.parseInt(s2);
        if (packPrice != pkp && Main.settingsTab.authority < 4) {
            packPrice = pkp;
            try {
                //update packSupplier
                PreparedStatement updatePackPrice = Main.getConnection().prepareStatement("UPDATE PackSuppliers SET Price=? WHERE ID=?");
                updatePackPrice.setLong(2, packSupplier);
                updatePackPrice.setInt(1, packPrice);
                updatePackPrice.executeUpdate();
                //update deliveryLines
                PreparedStatement updateDeliveryLines = Main.getConnection().prepareStatement("UPDATE Deliverylines SET PackPrice=? WHERE Delivery=? AND PackSupplier=?");
                updateDeliveryLines.setInt(1, packPrice);
                updateDeliveryLines.setInt(2, delivery);
                updateDeliveryLines.setInt(3, packSupplier);
                updateDeliveryLines.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(DeliveryEntry.class.getName()).log(Level.SEVERE, null, ex);
                packCodeText.setBackground(Color.YELLOW);
                Audio.play("Beep");
            }
        }
        PreparedStatement updateStockQuantity, np, np1, np2;
        if (packCodeText.getText().isEmpty()) {
            packCodeText.requestFocus();
            return;
        }
        try {
            packSize = (Integer) packSizeSpinner.getValue();
            //update pack size
            PreparedStatement updatePackSize = Main.getConnection().prepareStatement("UPDATE Packs SET Size=? WHERE ID=?");
            updatePackSize.setInt(1, packSize);
            updatePackSize.setInt(2, pack);
            updatePackSize.executeUpdate();
            int ppt = Integer.parseInt(StringOps.numericOnly(packPriceText.getText()));
            if (packPrice != ppt) {
                packPrice = ppt;
                //update pack price
                PreparedStatement updatePackPrice = Main.getConnection().prepareStatement("UPDATE PackSuppliers   SET Price=? WHERE ID=?");
                updatePackPrice.setInt(1, packPrice);
                updatePackPrice.setInt(2, packSupplier);
                updatePackPrice.executeUpdate();
            }
            //update stock on shelf
            updateStockQuantity = Main.getConnection().prepareStatement(
                    "UPDATE Skus SET Quantity=Quantity+? " + "WHERE ID=?");
            int newQuantity = (Integer) stockOnShelfSpinner.getValue();
            if (newQuantity != quantity) {//must have changed
                updateStockQuantity.setInt(2, sku);
                updateStockQuantity.setInt(1, newQuantity - quantity);//no multipack because this is a stock count
                updateStockQuantity.executeUpdate();
                if (saleLoss == 0) {//record this as a loss
                    //this is first time
                    np = Main.getConnection().prepareStatement(
                            "INSERT INTO Sales(ID,Operator,Customer,Total,Tax,Cash,Cheque,Debit,Coupon,TillID,Waste,Tax2) VALUES(?,?,?,0,1,0,0,0,0,?,?,1)");
                    np.setNull(1, Types.INTEGER); //ID
                    np.setInt(2, operator);
                    np.setLong(3, 10000380000l);
                    np.setInt(4, Main.shop.getTillId());
                    np.setInt(5, SaleType.LOSS.value());
                    np.executeUpdate();
                    //need to get at last ID
                    np1 = Main.getConnection().prepareStatement(SQL.lastSalesId);
                    rs = np1.executeQuery();
                    rs.first();
                    saleLoss = rs.getInt(1);
                    rs.close();
                }
                np2 = Main.getConnection().prepareStatement(//needs wholesalePrice,packSize
                        "INSERT INTO SaleLines (ID,Sale,Quantity,Product,Price,"
                        + "Track,Encode,PricedOver,Discounted,origPrice,taxID,taxRate,wholesalePrice,packSize) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                np2.setNull(1, Types.INTEGER);
                np2.setInt(2, saleLoss);
                np2.setInt(3, newQuantity - quantity);
                np2.setLong(4, product);
                if (packSize == 0) {
                    packSize = 1;
                }
                wholesalePrice = packPrice / packSize;
                np2.setInt(5, wholesalePrice);
                np2.setString(6, "");
                np2.setInt(7, encode);
                np2.setInt(8, 0);
                np2.setInt(9, 0);
                np2.setInt(10, wholesalePrice);
                np2.setInt(11, taxID);
                np2.setInt(12, rate);
                np2.setInt(13, wholesalePrice);
                np2.setInt(14, packSize);
                boolean execute = np2.execute();
                if (encode == NewProduct.NOTENCODE) {
                    totalExtra = (newQuantity - quantity) * wholesalePrice;
                } else {
                    //handle encoded products
                    totalExtra = ((newQuantity - quantity) * wholesalePrice) / 1000;
                }
                //need to update total in the sale
                String utis = "UPDATE Sales SET Total=Total+?,Tax=? WHERE ID=?";
                PreparedStatement psu = Main.getConnection().prepareStatement(utis);
                psu.setInt(1, totalExtra);
                psu.setInt(2, 0);//no tax on a loss
                psu.setInt(3, saleLoss);
                psu.execute();

            }
            //do a delivery sale!
            numberOfPacks = (Integer) numberOfPacksSpinner.getValue();
            if (numberOfPacks != 0) {
                packSize = (Integer) packSizeSpinner.getValue();
                if (saleDelivery == 0) {
                    np = Main.getConnection().prepareStatement(
                            "INSERT INTO Sales(ID,Operator,Customer,Total,Tax,Cash,Cheque,Debit,Coupon,TillID,Waste,Tax2) VALUES(?,?,?,0,1,0,0,0,0,?,?,1)");
                    np.setNull(1, Types.INTEGER); //ID
                    np.setInt(2, Main.operator.getOperator());
                    np.setLong(3, SaleType.CUSTOMER.code() * 10000l);
                    np.setInt(4, Main.shop.getTillId());
                    np.setInt(5, SaleType.DELIVERY.value());
                    np.executeUpdate();
                    //need to get at last ID
                    np1 = Main.getConnection().prepareStatement(
                            "SELECT LAST_INSERT_ID() FROM Sales");
                    rs = np1.executeQuery();
                    rs.first();
                    saleDelivery = rs.getInt(1);//needs to be a different sale
                    rs.close();
                }
                //now the SaleLines
                np2 = Main.getConnection().prepareStatement(//needs wholesalePrice,packSize
                        "INSERT INTO SaleLines (ID,Sale,Quantity,Product,Price,"
                        + "Track,Encode,PricedOver,Discounted,origPrice,taxID,taxRate,wholesalePrice,packSize) "
                        + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                np2.setNull(1, Types.INTEGER);
                np2.setInt(2, saleDelivery);
                np2.setInt(3, packSize * numberOfPacks);
                np2.setLong(4, product);
                np2.setInt(5, retailPrice);
                np2.setString(6, "");//track
                np2.setInt(7, encode);
                np2.setInt(8, 0);//PriceOver
                np2.setInt(9, 0);//Discount
                if (packSize == 0) {
                    packSize = 1;
                }
                wholesalePrice = packPrice / packSize;
                np2.setInt(10, wholesalePrice);
                np2.setInt(11, taxID);
                np2.setInt(12, rate);
                np2.setInt(13, wholesalePrice);
                np2.setInt(14, packSize);
                np2.execute();
                //end of delivery sale
                //now adjust stock levels due to sale
                updateStockQuantity.setInt(1, numberOfPacks * packSize * multiPack);
                updateStockQuantity.setInt(2, sku);
                updateStockQuantity.executeUpdate();
                //now update state of PackSuppliers
                np2 = Main.getConnection().prepareStatement(
                        "UPDATE PackSuppliers SET WhenCreated=? WHERE ID=?");
                np2.setNull(1, Types.DATE);
                np2.setInt(2, packSupplier);
                np2.executeUpdate();
            }
        } catch (SQLException ex) {
            Audio.play("Ring");
            packCodeText.setBackground(Color.YELLOW);
            Logger.getLogger(DeliveryEntry.class.getName()).log(Level.SEVERE, null, ex);
            packCodeText.requestFocus();
            Audio.play("Beep");
            return;
        }
        //finally do a delivery
        updateDeliveryTable();
        packSizeSpinner.setValue(1);
        numberOfPacksSpinner.setValue(1);
        productPriceText.setText("");
        productDescriptionText.setText("");
        packPriceText.setText("");
        marginText.setText("0.00%");
        packCodeText.setText("");
        stockOnShelfSpinner.setValue(0);
        sku = 0;
        quantity = 0;
        packCodeText.requestFocus();
        Audio.play("Beep");
    }
}
