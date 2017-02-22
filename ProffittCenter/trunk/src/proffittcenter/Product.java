/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProductN.java
 *
 * Created on 13-Mar-2009, 18:19:05
 */
package proffittcenter;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

/**
 *
 * @author HP_Owner
 */
public class Product extends EscapeDialog {

    Preferences root = Preferences.userNodeForPackage(getClass());
    ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Product");
    private int encode;
    private String shortenedData7;
    private ResultSet rs;
    private String shortenedData8;
    private int packSize;
    private int retailPrice;
    private int packPrice;
    private int margin;
    private int rate;
    private int linesToSkip;
    private String shortenedData6;
    private int psid;
    private int pid;

    /** Creates new form ProductN
     * @param parent
     * @param modal
     */
    public Product(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Product", Main.mainHelpSet);
        stoppedCheckBox.setEnabled(false);
    }

    public boolean execute(String data) {
        barcode = 0l;
        multiPackSpinner.setValue(1);
        lastDeliveryText.setText("");
        if (data.length() < 7 && data.length() != 0) {
            return false;
        }//8 or more digits
        if (data.length() != 0) {
            try {
                barcode = Long.parseLong(data);
            } catch (NumberFormatException ex) {
                return false;
            }
            if (barcode < 100000) {
                barcode = 0l;//do not leave with a value
                return false;//not big enough to be a bar code
            }
            this.data = data;
            checkProduct();
        } else {//data is empty
            clearFields();
        }
        Audio.play("Tada");
        this.pack();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        barCodeEntry.requestFocus();
        setVisible(true);
        return true;
    }

    public void printShelfEdgeLabel() {//print a shelf edge label
        int linesToBePrinted = 4;
        int linesSeperatingPrintAndCut = 6;
        int shelfEdgeLabelHeight = Main.hardware.getShelfEdgeLabelHeight() - Main.hardware.getExtraLines();
        int blankLinesBeforePrinting = (shelfEdgeLabelHeight - linesToBePrinted) / 2 + Main.hardware.getExtraLines() - linesSeperatingPrintAndCut;
        blankLinesBeforePrinting = blankLinesBeforePrinting < 0 ? 0 : blankLinesBeforePrinting;
        int blankLinesAfterPrinting = (shelfEdgeLabelHeight - linesToBePrinted) / 2 - blankLinesBeforePrinting - Main.hardware.getExtraLines() + linesSeperatingPrintAndCut;
        Main.receiptPrinter.selectPrinter();
        //Main.receiptPrinter.selectPrinter();
        for (int i = 0; i < blankLinesBeforePrinting; i++) {
            Main.receiptPrinter.printLine("");
        }
        String s = description;
//        s="012345678901234567890123456789012345678901234567890123456789";
        Main.receiptPrinter.printLargeCentralLine(s);
        s = (new Money(price).toString());
        Main.receiptPrinter.printLargeCentralLine(s);
        Main.receiptPrinter.printLine(" ");
        linesToSkip=blankLinesAfterPrinting-1;
        s=""+barcode;
        s=StringOps.fixLength(s, 12);
        if(barcodeCheckBox.isSelected()){
            Main.receiptPrinter.printBarcode(s);
            linesToSkip-=3;
        }
        for (int i = 0; i < linesToSkip-1; i++) {
            Main.receiptPrinter.printLine(" ");
        }
        Main.receiptPrinter.endPrinter();
    }

    private void checkProduct() {
        try {
            PreparedStatement productLookup = Main.getConnection().prepareStatement(
                    "SELECT Products.*,Skus.*, Taxes.* ,Departments.*,Suppliers.* "
                    + "FROM Products,Skus,Departments,Suppliers,Taxes  "
                    + "WHERE ((Products.ID=?  AND Encoded = 0) "
                    + "OR (CONCAT(SUBSTRING(Products.ID FROM 1 FOR 6),"
                            + "'0000000')=? AND (Encoded =5 OR Encoded = 6))"
                    + "OR (CONCAT(SUBSTRING(Products.ID FROM 1 FOR 7),"
                            + "'000000') =? AND (Encoded = 1 OR Encoded=2))"
                    + "OR (CONCAT(SUBSTRING(Products.ID FROM 1 FOR 8),"
                            + "'00000')=? AND (Encoded =3 OR Encoded = 4))) "
                    + "AND Products.Sku=Skus.ID "
                            + "AND Departments.ID=Skus.Department "
                    + "AND Skus.Tax=Taxes.ID ");
            //now need to check till/product table
            if (NewProduct.couldBeEncoded(data)) {
                shortenedData6 = NewProduct.shortenForEncoded6(data);
                shortenedData7 = NewProduct.shortenForEncoded7(data);
                shortenedData8 = NewProduct.shortenForEncoded8(data);
//                shortenedData = data.substring(0,data.length()-5);
                productLookup.setString(1, data);
                productLookup.setString(2, shortenedData6);                
                productLookup.setString(3, shortenedData7);
                productLookup.setString(4, shortenedData8);
                rs = productLookup.executeQuery();
                if (!rs.first()) {
                    rs.close();
                    productLookup.setLong(1, barcode);
                    rs = productLookup.executeQuery();
                } 
            } else {
                productLookup.setLong(1, barcode);
                productLookup.setLong(2, barcode);
                productLookup.setLong(3, barcode);
                productLookup.setLong(4, barcode);
                rs = productLookup.executeQuery();
            }
            if (!rs.first()) {
                clearFields();
                rs.close();
            } else {
                barcode =  rs.getLong("Products.ID");
                barCodeT.setText("" + barcode);
                description = rs.getString("Description");
                descriptionT.setText(description);
                price = rs.getInt("Price");
                priceT.setText((new Money(price)).toString());
                perText.setText(rs.getString("Per"));
                sku = rs.getInt("Sku");
                skuT.setText("" + sku);
                pDate.setText("" + rs.getDate("Products.WhenCreated"));
                quantityT.setText("" + rs.getInt("Quantity"));
                skuDateT.setText("" + rs.getDate("Skus.WhenCreated"));
                departmentIdT.setText("" + rs.getInt("Department"));
                short k = rs.getShort("StockType");
                typeIdT.setText("" + k);
                shelfRentT.setText(new Money(rs.getInt("ShelfRent")).toString());
                minimumT.setText("" + rs.getInt("Minimum"));
                int tax = rs.getInt("Tax");
                rate= rs.getInt("Rate");
                taxIdT.setText("" + tax);
                taxDescriptionT.setText(rs.getString("Taxes.Description"));
                departmentDescriptionT.setText(rs.getString("Departments.Description"));
                typeDescriptionT.setText(SkuType.description(k));
                multiPackSpinner.setValue(rs.getInt("MultiPack"));
//                supplierDescriptionT.setText(rs.getString("Suppliers.Description"));
                encode = rs.getInt("Encoded");
                if (NewProduct.couldBeEncoded("" + barcode)) {
                    jEncoded.removeAllItems();
                    jEncoded.addItem(bundle.getString("No"));
                    jEncoded.addItem(bundle.getString("ByPriceParity"));
                    jEncoded.addItem(bundle.getString("ByWeightParity"));
                    jEncoded.addItem(bundle.getString("ByPriceNoParity"));
                    jEncoded.addItem(bundle.getString("ByWeightNoParity"));
                    jEncoded.addItem(bundle.getString("ByPrice5"));
                    jEncoded.addItem(bundle.getString("ByWeight5"));
                    jEncoded.setEnabled(true);
                    jEncoded.setSelectedIndex(encode);
                } else {
                    jEncoded.removeAllItems();
                    jEncoded.setEnabled(false);
                }
                stoppedCheckBox.setSelected(rs.getBoolean("Stopped"));
                rs.close();
            }
            PreparedStatement productInSales = Main.getConnection().prepareStatement("Select ID,Product FROM SaleLines WHERE Product=?");
            productInSales.setLong(1, barcode);
            rs = productInSales.executeQuery();
            deleteBtn.setVisible(!rs.first());
            rs.close();
            String ld="SELECT Suppliers.Description,Packs.Size AS PackSize,PackSuppliers.Price,Packs.ID AS PID,PackSuppliers.ID AS PSID  "
                    + "FROM Suppliers,Packs,PackSuppliers "
                    + "WHERE PackSuppliers.Supplier=Suppliers.ID "
                    + "AND Packs.ID=PackSuppliers.Pack "
                    + "AND Packs.Product =? "
                    + "ORDER BY PackSuppliers.WhenCreated DESC";
            PreparedStatement lastDelivery = Main.getConnection().prepareStatement(ld);
            lastDelivery.setLong(1, barcode);
            rs=lastDelivery.executeQuery();
            if(rs.first()){
                String s= "";
                s +=rs.getString("Suppliers.Description")+"  Size: ";
                packSize = rs.getInt("PackSize");
                s+= packSize+" Pack price: ";
                packPrice = rs.getInt("Price");
                s += Money.asMoney(packPrice)+" Margin: ";
                margin = calculateMargin();
                s += PerCent.asPerCent(margin);
                pid=rs.getInt("PID");
                psid = rs.getInt("PSID");

                lastDeliveryText.setText(s);
            } else {
                lastDeliveryText.setText("");
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearFields() {
        //no product
        //should clear data
        barCodeT.setText("");
        barCodeEntry.setText("");
        descriptionT.setText("");
        priceT.setText("");
        skuT.setText("");
        pDate.setText("");
        quantityT.setText("");
        skuDateT.setText("");
        departmentIdT.setText("");
        typeIdT.setText("");
        shelfRentT.setText("");
        minimumT.setText("");
        taxIdT.setText("");
        taxDescriptionT.setText("");
        departmentDescriptionT.setText("");
        typeDescriptionT.setText("");
        typeDescriptionT.setText("");
        lastDeliveryText.setText("");
    }

    private int calculateMargin(){
        int m;
        if (price == 0) {
            return 0;
        }
        Regime rr = Main.shop.regimeIs();
        if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
            margin = 10000 - (packPrice * (10000 + rate)) / ((packSize) * price);
        } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
            margin = 10000 - packPrice * 10000 / (packSize * price);
        }
        return margin;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        barCodeT = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        descriptionT = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        priceT = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        skuT = new javax.swing.JTextField();
        pDate = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        skuDateT = new javax.swing.JTextField();
        departmentIdT = new javax.swing.JTextField();
        typeIdT = new javax.swing.JTextField();
        taxIdT = new javax.swing.JTextField();
        shelfRentT = new javax.swing.JTextField();
        minimumT = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        quantityT = new javax.swing.JTextField();
        departmentDescriptionT = new javax.swing.JTextField();
        typeDescriptionT = new javax.swing.JTextField();
        taxDescriptionT = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        perText = new javax.swing.JTextField();
        jEncoded = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        lastDeliveryText = new javax.swing.JTextField();
        stoppedCheckBox = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        multiPackSpinner = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        barCodeEntry = new javax.swing.JTextField();
        updateBtn = new javax.swing.JButton();
        unlinkBtn = new javax.swing.JButton();
        skubtn = new javax.swing.JButton();
        printBtn = new javax.swing.JButton();
        closeButton2 = new javax.swing.JButton();
        priceChangeButton = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        printBarcode = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();
        barcodeCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Product"); // NOI18N
        setTitle(bundle.getString("Product.title")); // NOI18N
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SalesBy"); // NOI18N
        setName(bundle1.getString("SalesBy.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel2.setText(bundle.getString("Product.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        barCodeT.setEditable(false);
        barCodeT.setName("barCodeT"); // NOI18N

        jLabel3.setText(bundle.getString("Product.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        descriptionT.setName("descriptionT"); // NOI18N
        descriptionT.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                descriptionTFocusGained(evt);
            }
        });

        jLabel4.setText(bundle.getString("Product.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        priceT.setEditable(false);
        priceT.setName("priceT"); // NOI18N
        priceT.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                priceTFocusGained(evt);
            }
        });

        jLabel5.setText(bundle.getString("Product.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText(bundle.getString("Product.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        skuT.setEditable(false);
        skuT.setName("skuT"); // NOI18N

        pDate.setEditable(false);
        pDate.setName("pDate"); // NOI18N

        jLabel7.setText(bundle.getString("Product.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        skuDateT.setEditable(false);
        skuDateT.setName("skuDateT"); // NOI18N

        departmentIdT.setEditable(false);
        departmentIdT.setName("departmentIdT"); // NOI18N

        typeIdT.setEditable(false);
        typeIdT.setName("typeIdT"); // NOI18N

        taxIdT.setEditable(false);
        taxIdT.setName("taxIdT"); // NOI18N

        shelfRentT.setEditable(false);
        shelfRentT.setName("shelfRentT"); // NOI18N

        minimumT.setEditable(false);
        minimumT.setName("minimumT"); // NOI18N

        jLabel15.setText(bundle.getString("Product.jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel16.setText(bundle.getString("Product.jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jLabel19.setText(bundle.getString("Product.jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        jLabel11.setText(bundle.getString("Product.jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setText(bundle.getString("Product.jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel17.setText(bundle.getString("Product.jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        quantityT.setEditable(false);
        quantityT.setName("quantityT"); // NOI18N

        departmentDescriptionT.setEditable(false);
        departmentDescriptionT.setName("departmentDescriptionT"); // NOI18N

        typeDescriptionT.setEditable(false);
        typeDescriptionT.setName("typeDescriptionT"); // NOI18N
        typeDescriptionT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeDescriptionTActionPerformed(evt);
            }
        });

        taxDescriptionT.setEditable(false);
        taxDescriptionT.setName("taxDescriptionT"); // NOI18N

        jLabel9.setText(bundle.getString("Product.jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setText(bundle.getString("Product.jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel13.setText(bundle.getString("Product.jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel20.setText(bundle.getString("Product.jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        perText.setText(bundle.getString("Product.perText.text")); // NOI18N
        perText.setName("perText"); // NOI18N

        jEncoded.setName("jEncoded"); // NOI18N

        jLabel8.setText(bundle.getString("Product.jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel14.setText(bundle.getString("Product.jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        lastDeliveryText.setName("lastDeliveryText"); // NOI18N

        stoppedCheckBox.setText(bundle.getString("Product.stoppedCheckBox.text")); // NOI18N
        stoppedCheckBox.setName("stoppedCheckBox"); // NOI18N

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText(bundle.getString("Product.jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        multiPackSpinner.setName("multiPackSpinner"); // NOI18N

        jLabel1.setText(bundle.getString("Product.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        barCodeEntry.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                barCodeEntryFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                barCodeEntryFocusLost(evt);
            }
        });
        barCodeEntry.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                barCodeEntryonKeyReleased(evt);
            }
        });

        updateBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/OK.png"))); // NOI18N
        updateBtn.setContentAreaFilled(false);
        updateBtn.setName("updateBtn"); // NOI18N
        updateBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateBtnActionPerformed(evt);
            }
        });

        unlinkBtn.setText(bundle.getString("Product.unlinkBtn.text")); // NOI18N
        unlinkBtn.setToolTipText(bundle.getString("Product.unlinkBtn.tooltip")); // NOI18N
        unlinkBtn.setName("unlinkBtn"); // NOI18N
        unlinkBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unlinkBtnActionPerformed(evt);
            }
        });

        skubtn.setText(bundle.getString("Product.skubtn.text")); // NOI18N
        skubtn.setName("skubtn"); // NOI18N
        skubtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skubtnActionPerformed(evt);
            }
        });

        printBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/print_edit.gif"))); // NOI18N
        printBtn.setText("null");
        printBtn.setToolTipText(bundle.getString("ShelfEdgeLabel")); // NOI18N
        printBtn.setContentAreaFilled(false);
        printBtn.setName("printBtn"); // NOI18N
        printBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBtnActionPerformed(evt);
            }
        });

        closeButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
        closeButton2.setContentAreaFilled(false);
        closeButton2.setName("closeButton2"); // NOI18N
        closeButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButton2ActionPerformed(evt);
            }
        });

        priceChangeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/ChangePrice.png"))); // NOI18N
        priceChangeButton.setToolTipText(bundle.getString("priceChange")); // NOI18N
        priceChangeButton.setContentAreaFilled(false);
        priceChangeButton.setName("priceChangeButton"); // NOI18N
        priceChangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                priceChangeButtonActionPerformed(evt);
            }
        });

        jButton2.setText(bundle.getString("jButton2")); // NOI18N
        jButton2.setName("jButton2"); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        printBarcode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Barcode.png"))); // NOI18N
        java.util.ResourceBundle bundle2 = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SalesScreen"); // NOI18N
        printBarcode.setToolTipText(bundle2.getString("SalesScreen.printBarcode.toolTipText")); // NOI18N
        printBarcode.setContentAreaFilled(false);
        printBarcode.setName("printBarcode"); // NOI18N
        printBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBarcodeActionPerformed(evt);
            }
        });

        deleteBtn.setText(bundle.getString("Product.deleteBtn.text")); // NOI18N
        deleteBtn.setName("deleteBtn"); // NOI18N
        deleteBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBtnActionPerformed(evt);
            }
        });

        barcodeCheckBox.setText(bundle.getString("Product.barcodeCheckBox.text")); // NOI18N
        barcodeCheckBox.setToolTipText(bundle.getString("Product.barcodeCheckBox.toolTipText")); // NOI18N
        barcodeCheckBox.setName("barcodeCheckBox"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barCodeEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(deleteBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(unlinkBtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(skubtn)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(printBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(barcodeCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(priceChangeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(printBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(quantityT, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(skuDateT, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(departmentDescriptionT, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(typeDescriptionT, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(departmentIdT, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel9)
                                            .addComponent(jLabel15))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel10)
                                            .addComponent(typeIdT, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel16)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(8, 8, 8)
                                .addComponent(jLabel7)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(taxDescriptionT, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jEncoded, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(stoppedCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(taxIdT, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(shelfRentT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel11))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel12)
                                            .addComponent(minimumT, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 22, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(multiPackSpinner, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(priceT, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(skuT, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel5))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(pDate, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel6))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel20)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jLabel14))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(perText, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lastDeliveryText, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(barCodeT, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel2))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel3)
                                    .addGap(514, 514, 514))
                                .addComponent(descriptionT, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE))))
                    .addGap(10, 10, 10)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(printBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(printBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(priceChangeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(barcodeCheckBox)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(barCodeEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updateBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(unlinkBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(skubtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))))
                .addGap(60, 60, 60)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(multiPackSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel15)
                    .addComponent(jLabel16)
                    .addComponent(jLabel19)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(taxIdT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(skuDateT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(departmentIdT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeIdT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(shelfRentT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minimumT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jLabel13)
                    .addComponent(jLabel8))
                .addGap(1, 1, 1)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stoppedCheckBox)
                    .addComponent(departmentDescriptionT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeDescriptionT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(taxDescriptionT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jEncoded, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(quantityT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(47, 47, 47)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel3))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(barCodeT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(descriptionT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel4)
                                .addComponent(jLabel5)
                                .addComponent(jLabel6))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(priceT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(skuT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(pDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel20)
                                .addComponent(jLabel14))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(perText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lastDeliveryText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addContainerGap(108, Short.MAX_VALUE)))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {barCodeEntry, closeButton2, deleteBtn, jButton2, priceChangeButton, printBtn, skubtn, unlinkBtn, updateBtn});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {barCodeT, descriptionT});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {departmentDescriptionT, departmentIdT, minimumT, quantityT, shelfRentT, skuDateT, taxDescriptionT, taxIdT, typeDescriptionT, typeIdT});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {pDate, priceT, skuT});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void barCodeEntryonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_barCodeEntryonKeyReleased
                data = barCodeEntry.getText();
        if (evt.getKeyCode() == KeyEvent.VK_PRINTSCREEN) {
            printShelfEdgeLabel();
            return;
        }
        if (data.length() == 3) {
            if (Main.alphaLookup.isBarcode(data)) {
                return;
            }
            if (Main.alphaLookup.isFound(data)) {
                barCodeEntry.setText(Main.alphaLookup.returnDataIs());
                updateBtn.requestFocus();
            } else {
                barCodeEntry.setText("");
            }
        }
}//GEN-LAST:event_barCodeEntryonKeyReleased

    private void deleteBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBtnActionPerformed
        if (barcode != 0l) {
            //delete if not in sale
            try {
                //need to delete any PackSuppliers
                PreparedStatement delps = Main.getConnection().prepareStatement(
                        "DELETE FROM PackSuppliers WHERE (SELECT Packs.Product "
                        + "FROM Packs WHERE Packs.Product=? "
                        + "AND Packs.ID=PackSuppliers.Pack)");
                delps.setLong(1, barcode);
                delps.executeUpdate();
                //now need to delete Pack
                PreparedStatement delp = Main.getConnection().prepareStatement(
                        "DELETE FROM Packs WHERE Packs.Product = ? ");
                delp.setLong(1, barcode);
                delp.executeUpdate();
                PreparedStatement del = Main.getConnection().prepareStatement(
                        "DELETE FROM Products WHERE ID=?");
                del.setLong(1, barcode);
                del.executeUpdate();
                PreparedStatement otherProducts = Main.getConnection().prepareStatement(
                        "SELECT ID FROM Products WHERE Products.Sku=?");
                otherProducts.setInt(1, sku);
                ResultSet ot = otherProducts.executeQuery();
                if (!ot.first()) {//no products, then delete
                    PreparedStatement delSku = Main.getConnection().prepareStatement(
                            "DELETE FROM Skus WHERE ID=? ");
                    delSku.setInt(1, sku);
                    delSku.executeUpdate();
                }
                otherProducts.close();
                checkProduct();
            } catch (SQLException ex) {
                Audio.play("Ring");
                Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
            }
            barCodeEntry.requestFocus();
        }
}//GEN-LAST:event_deleteBtnActionPerformed

    private void updateBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateBtnActionPerformed
        try {
            PreparedStatement ps = Main.getConnection().prepareStatement(
                    "UPDATE Products SET Description=?,"
                    + "Price=?,Per=?,Encoded=?,MultiPack=? WHERE ID=?");
            String s = StringOps.fixLength(StringOps.firstCaps(descriptionT.getText().trim()), 50);
            descriptionT.setText(s);
            if (s.isEmpty()) {//need a description
                Audio.play("Ring");
                descriptionT.requestFocus();
                return;
            } else if (priceT.getText().isEmpty()) {//no price
                Audio.play("Ring");
                priceT.requestFocus();
                return;
            }
            ps.setString(1, s);//Description
            if (encode != jEncoded.getSelectedIndex()) {
//                if (encode == 1) {
                    encode = jEncoded.getSelectedIndex();
//                }
            } else if (jEncoded.getSelectedIndex() != 1) {
                encode = jEncoded.getSelectedIndex();
            } else {
                //leave as was
            }
            if (encode == -1) {
                encode = 0;
            }
            ps.setInt(4, encode);
            if (encode == NewProduct.NOTENCODE || encode == NewProduct.ENCODEBYWEIGHTPARITY || encode == NewProduct.ENCODEBYWEIGHTNOPARITY || encode == -1) {
                price = Integer.parseInt(StringOps.numericOnly(priceT.getText()));
            } else {
                price = 0;
                priceT.setText(new Money(0).toString());
            }
            ps.setInt(2, price);
            String t = perText.getText();
            ps.setString(3, StringOps.fixLength(t, 5));
            ps.setInt(4, encode);
            ps.setInt(5, (Integer)multiPackSpinner.getValue());
            ps.setLong(6, barcode);
            ps.executeUpdate();
            checkProduct();
            Audio.play("Beep");
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(Product.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NumberFormatException ex) {
            //price was not an integer
            Audio.play("Ring");
            JOptionPane.showMessageDialog(null,
                    java.util.ResourceBundle.getBundle("proffittcenter/Product").getString("Price_must_be_entered_in_pence"),
                    java.util.ResourceBundle.getBundle("proffittcenter/Product").getString("Warning"),
                    JOptionPane.ERROR_MESSAGE);
        }
        barCodeEntry.requestFocus();
}//GEN-LAST:event_updateBtnActionPerformed

    private void unlinkBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unlinkBtnActionPerformed
        //do not unlink default product on 'a' or 'b'
        if (barcode != 1000001l&&barcode!=1000002l) {//not a or b
            //update the product
            try {
                PreparedStatement np = Main.getConnection().prepareStatement(
                        "UPDATE Products SET "
                        + " Sku=1 "
                        + "WHERE Products.ID=? ");
                np.setLong(1, barcode);//ID
                np.executeUpdate();
                checkProduct();
                Audio.play("Beep");
            } catch (SQLException ex) {
                Audio.play("Ring");
                System.out.println(java.util.ResourceBundle.getBundle("proffittcenter/Product").getString("SQL_Error:_") + ex.getMessage());
            }
        }
        barCodeEntry.requestFocus();
}//GEN-LAST:event_unlinkBtnActionPerformed

    private void skubtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skubtnActionPerformed
        if (barcode != 0l) {
            setVisible(false);
            Main.sku.execute(sku);
            checkProduct();
            barCodeEntry.requestFocus();
            setVisible(true);
        }
}//GEN-LAST:event_skubtnActionPerformed

    private void closeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButton2ActionPerformed

    private void printBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBtnActionPerformed
        if (barcode != 0l) {
            printShelfEdgeLabel();
        }
}//GEN-LAST:event_printBtnActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void barCodeEntryFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barCodeEntryFocusGained
        barCodeEntry.selectAll();
    }//GEN-LAST:event_barCodeEntryFocusGained

    private void printBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBarcodeActionPerformed
        if (barcode != 0l) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            int a=description.length();
            BarcodePrinter.print(barcode, description);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }//GEN-LAST:event_printBarcodeActionPerformed

    private void priceChangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_priceChangeButtonActionPerformed
        if(Main.operator.isOwnerManager()|| Main.operator.isPriceChange()){
            if (barcode != 0l) {
                PriceChange pc = new PriceChange(null, true);
                pc.execute(barcode);
                checkProduct();
                barCodeEntry.requestFocus();
            }
        }
    }//GEN-LAST:event_priceChangeButtonActionPerformed

    private void barCodeEntryFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_barCodeEntryFocusLost
        data = barCodeEntry.getText().trim();
        if (data.isEmpty()) {
            return;
        }
        if (data.length() == 1 && (data.charAt(0) >= 'a'
                && data.charAt(0) <= 'z' || data.charAt(0) >= 'A'
                && data.charAt(0) <= 'Z')) {
            //data is a hot key
            int n = 1;//n is the multiplyer, default to 1
            data = data.toUpperCase();
            char c = data.charAt(0);
            barcode = c - 65l + 1000001;
            if (data.length() > 1) {
                try {
                    n = Integer.parseInt(data.substring(1));
                } catch (NumberFormatException ex) {
                    barCodeEntry.setText("");
                    barCodeEntry.requestFocus();
                    return;
                }
            }
            data = "" + barcode;
        }
        barCodeEntry.requestFocus();
        if (data.isEmpty() || data.length() < 7) {
            return;
        }//8 or more digits
        try {
            barcode = Long.parseLong(data);
        } catch (NumberFormatException ex) {
            return;
        }
        if (barcode < 100000) {
            barcode = 0l;//do not leave with a value
            return;//not big enough to be a bar code
        }
        checkProduct();
        Audio.play("Beep");
        barCodeEntry.setText("");
        barCodeEntry.requestFocus();
    }//GEN-LAST:event_barCodeEntryFocusLost

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
         if (barcode != 0l) {
            //todo purchases
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void priceTFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_priceTFocusGained
        priceT.selectAll();
}//GEN-LAST:event_priceTFocusGained

    private void descriptionTFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_descriptionTFocusGained
        descriptionT.selectAll();
}//GEN-LAST:event_descriptionTFocusGained

    private void typeDescriptionTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeDescriptionTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_typeDescriptionTActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Product dialog = new Product(new javax.swing.JFrame(), true);
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
    Long barcode;
    String data;
    private String description;
    private int price;
    private int sku;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField barCodeEntry;
    private javax.swing.JTextField barCodeT;
    private javax.swing.JCheckBox barcodeCheckBox;
    private javax.swing.JButton closeButton2;
    private javax.swing.JButton deleteBtn;
    private javax.swing.JTextField departmentDescriptionT;
    private javax.swing.JTextField departmentIdT;
    private javax.swing.JTextField descriptionT;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox jEncoded;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField lastDeliveryText;
    private javax.swing.JTextField minimumT;
    private javax.swing.JSpinner multiPackSpinner;
    private javax.swing.JTextField pDate;
    private javax.swing.JTextField perText;
    private javax.swing.JButton priceChangeButton;
    private javax.swing.JTextField priceT;
    private javax.swing.JButton printBarcode;
    private javax.swing.JButton printBtn;
    private javax.swing.JTextField quantityT;
    private javax.swing.JTextField shelfRentT;
    private javax.swing.JTextField skuDateT;
    private javax.swing.JTextField skuT;
    private javax.swing.JButton skubtn;
    private javax.swing.JCheckBox stoppedCheckBox;
    private javax.swing.JTextField taxDescriptionT;
    private javax.swing.JTextField taxIdT;
    private javax.swing.JTextField typeDescriptionT;
    private javax.swing.JTextField typeIdT;
    private javax.swing.JButton unlinkBtn;
    private javax.swing.JButton updateBtn;
    // End of variables declaration//GEN-END:variables
}
