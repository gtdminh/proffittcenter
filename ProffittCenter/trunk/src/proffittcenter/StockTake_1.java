/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StockTakeN.java
 *
 * Created on 19-Mar-2009, 06:01:47
 */
package proffittcenter;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractCellEditor;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class StockTake_1 extends EscapeDialog {

    ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/StockTake");
    Vector tableHeader = new Vector();
    boolean b0 = tableHeader.add(bundle.getString("Sku__"));
    boolean b1 = tableHeader.add(bundle.getString("Product"));
    boolean b2 = tableHeader.add(bundle.getString("Quantity"));
    boolean b3 = tableHeader.add(bundle.getString("Date"));
    boolean b4 = tableHeader.add(bundle.getString("Barcode"));
    boolean b5 = tableHeader.add(bundle.getString("Price"));
    String sql = " SELECT S.*,P.* "
            + " FROM Skus AS S,Products AS P "
            + "WHERE S.ID=P.Sku AND S.ID<>1 AND S.ID<>2 "
            + "AND (S.StockType= " + SkuType.INCLUDED.value()
            + " OR S.StockType=" + SkuType.OTHERS.value()
            + ")"
            + "AND (S.Department=? OR 0=?)"
            + "AND (S.ID=? OR 0=?)"
            + "AND (S.WhenCreated<CURRENT_DATE()OR ?)AND S.Stopped=? "
            + "AND (S.Quantity<=? OR 0=?)"
//            + " AND P.Encoded = " + NewProduct.NOTENCODED
            + //            "AND (PackSuppliers.Supplier=? OR S.Supplier=? OR 0=?) " +
            " ORDER BY S.WhenCreated,S.ID ";
    String sqlQ = " SELECT S.*,P.* "
            + " FROM Skus AS S,Products AS P "
            + "WHERE S.ID=P.Sku AND S.ID<>1 AND S.ID<>2 "
            + "AND S.Stopped IS false "
            + "AND (S.StockType= " + SkuType.INCLUDED.value()
            + " OR S.StockType=" + SkuType.OTHERS.value()
            + ")"
            + "AND (S.WhenCreated<CURRENT_DATE()OR ?)AND S.Stopped=FALSE "
            + "AND S.Quantity<=? "
            + " AND P.Encoded = " + NewProduct.NOTENCODE
            + " ORDER BY S.WhenCreated,S.ID ";
    String sqlD = " SELECT S.*,P.* "
            + " FROM Skus AS S,Products AS P "
            + "WHERE S.ID=P.Sku AND S.ID<>1 AND S.ID<>2 "
            + "AND S.Stopped IS false "
            + "AND (S.StockType= " + SkuType.INCLUDED.value()
            + " OR S.StockType=" + SkuType.OTHERS.value()
            + ")"
            + "AND (DATE(S.WhenCreated)<CURRENT_DATE()OR ? ) AND S.Stopped=FALSE "
            + "AND S.Department=? "
            + " AND P.Encoded= " + NewProduct.NOTENCODE
            + " ORDER BY DATE(S.WhenCreated),P.Description,S.ID ";
    String sqlS = " SELECT S.*,P.* "
            + " FROM Skus AS S,Products AS P "
            + "WHERE S.ID=P.Sku AND S.ID<>1 AND S.ID<>2 AND S.ID=? "
            + "AND S.Stopped IS false "
            + "AND (S.StockType= " + SkuType.INCLUDED.value()
            + " OR S.StockType=" + SkuType.OTHERS.value()
            + ")"
            + " AND S.Stopped=FALSE "
            + " AND P.Encoded= " + NewProduct.NOTENCODE
            + " ORDER BY S.WhenCreated DESC,S.ID LIMIT 1";
    String skuY = " SELECT DISTINCT S.*,P.*,PS.Supplier " +//to be corrected
            " FROM Skus AS S,Products AS P,Packs,PackSuppliers AS PS "
            + "WHERE S.ID=P.Sku AND S.ID<>1 AND S.ID<>2 "
            + "AND S.Stopped IS false "
            + "AND (S.StockType= " + SkuType.INCLUDED.value()
            + " OR S.StockType=" + SkuType.OTHERS.value()
            + ")"
            + "AND (S.WhenCreated<CURRENT_DATE()OR ?)AND S.Stopped=FALSE "
            + "AND PS.Supplier=? "
            + " AND P.Encoded= " + NewProduct.NOTENCODE
            + " AND P.ID=Packs.Product "
            + " AND Packs.ID = PS.Pack "
            + " ORDER BY S.WhenCreated,S.ID ";
    String saleInsert1 = "INSERT INTO "
            + " Sales(ID,Operator,Customer,Total,Tax,Cash,Cheque,Debit,Coupon,"
            + "TillId,Waste,FixedProfit,Tax2 ) VALUES(?,?,";
    private String saleInsert2 = ",0,0,0,0,0,0," + Main.shop.getTillId() + ",?,0,1)";
    String lastSale = "SELECT LAST_INSERT_ID() FROM Sales";
    String insertSaleLine = "INSERT INTO SaleLines "
            + "(ID,Sale,Quantity,Product,Price,Track,Encode,PricedOver,Discounted,origPrice,taxID,taxRate)"
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
    String updateSku = "UPDATE Skus SET Quantity=?,"
            + "WhenCreated=? "
            + "WHERE ID=?";
    int sku;
    String description;
    Integer quantity;
    java.sql.Date date;
    long product;
    private int noOfLines;
    private int price;
    private int department;
    int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT,
        SwingConstants.RIGHT, SwingConstants.LEFT, SwingConstants.RIGHT, 
        SwingConstants.RIGHT};
    Object[] line = {sku, description, quantity, date, product, price};
    Vector<Object[]> rowData = new Vector<Object[]>();
    Vector<Object[]> rowDataOld = new Vector<Object[]>();
    AbstractTableModel model = new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) tableHeader.get(col);
        }

        @Override
        public int getColumnCount() {
            return tableHeader.size();
        }

        @Override
        public int getRowCount() {
            return rowData.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {//Sku
                return line[0];
            } else if (columnIndex == 1) {//Description
                return line[1];
            } else if (columnIndex == 2) {//Quantity
                return line[2];
            } else if (columnIndex == 3) {//Date
                return line[3];
            } else if (columnIndex == 4) {//product (barcode)
                return line[4];
            } else if (columnIndex == 5) {//price
                 NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                Integer i= (Integer)line[5];
                return nf.format(((Double) i.doubleValue()) / 100);
            } else if (columnIndex == 6) {//price
                return line[6];
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            return col == 2;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (col == 2) {//only for quantity
                line = rowData.get(row);
                line[col] = value;//sku,description,quantity,date,product
                rowData.set(row, line);
                fireTableCellUpdated(row, col);
            } else if (col == 3) {//for date
                line = rowData.get(row);
                line[col] = value;//sku,description,quantity,date,product
                rowData.set(row, line);
                fireTableCellUpdated(row, col);
            }
        }
    };
    private int col;
    private int row;
    private boolean firstChar = true;
    MyHeaderRenderer mhr = null;
    MyTableCellRenderer mtcr = null;
    StockLevelEditor sle = null;
    Preferences root = Preferences.userNodeForPackage(getClass());
    private int tax;
    private String getTax = "SELECT Taxes.Rate AS Tax,Taxes.ID as taxID FROM Products, Skus,Taxes WHERE Products.Sku=Skus.ID AND Skus.Tax=Taxes.ID AND Products.ID=?";
    private ResultSet rs;
    private int wholesalePrice;
    private int encode;
    private int supplier;
    private boolean stopped;
    private int rate;
    private boolean all;
    private boolean firstTimeShow;
    private int newQuantity;
    private Integer oldQuantity;
    private int qty;
    private TableCellEditor jt;

    /** Creates new form StockTakeN
     * @param parent
     * @param modal  
     */
    public StockTake_1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(1).setPreferredWidth(650);
        tc.getColumn(3).setPreferredWidth(200);
        tc.getColumn(4).setPreferredWidth(200);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
//        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        sle = new StockLevelEditor();
        jTable1.setDefaultEditor(Integer.class, sle);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "StockTake", Main.mainHelpSet);
    }

    /** This method is called from within the constructor to
     * initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        closeButton2 = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        includeBarcodeCheckBox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/StockTake"); // NOI18N
        setTitle(bundle.getString("StockTake.title")); // NOI18N
        setName("StockTake"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
            }
        ));
        jTable1.setCellSelectionEnabled(true);
        jTable1.setEditingColumn(2);
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);

        jPanel1.setName("jPanel1"); // NOI18N

        closeButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
        closeButton2.setBorderPainted(false);
        closeButton2.setContentAreaFilled(false);
        closeButton2.setName("closeButton2"); // NOI18N
        closeButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButton2ActionPerformed(evt);
            }
        });
        closeButton2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                closeButton2KeyReleased(evt);
            }
        });

        printButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/print_edit.gif"))); // NOI18N
        printButton.setToolTipText(bundle.getString("printButtonToolTip")); // NOI18N
        printButton.setName("printButton"); // NOI18N
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/OK.png"))); // NOI18N
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        includeBarcodeCheckBox.setText("Include barcode");
        includeBarcodeCheckBox.setName("includeBarcodeCheckBox"); // NOI18N
        includeBarcodeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                includeBarcodeCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(printButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(includeBarcodeCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(okButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(closeButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(includeBarcodeCheckBox)
                            .addComponent(printButton))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 663, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public void execute(int noOfLines, int sDepartment, int sSku, Calendar sWhenCreated,
           
            boolean sStopped, int sQuantity, int sSupplier) {
                sku=sSku;
                department=sDepartment;
                quantity=sQuantity;
                supplier=sSupplier;
                stopped=sStopped;
        firstTimeShow=true;
        includeBarcodeCheckBox.setSelected(false);
        if (noOfLines == 0) {
            this.noOfLines = 0;
        } else if (noOfLines < 0) {
            this.noOfLines = Integer.MAX_VALUE;
        } else {
            this.noOfLines = noOfLines;
        }
        drawGrid(this.noOfLines, sDepartment, sSku,
                sStopped, sQuantity, sSupplier);
        firstTime = true;
        firstChar = true;
        jTable1.changeSelection(0, 2, false, false);
        Audio.play("Tada");
        
        printButton.setVisible(Main.receiptPrinter.port!=null);
        setVisible(true);
        //FormRestore.destroyPosition(this, root);
    }

    private void drawGrid(Integer noOfLines, int sDepartment, int sSku,
            boolean sStopped, int sQuantity, int sSupplier) {
        String limitString;
        PreparedStatement stocks;
        int ctr = 0;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (noOfLines != 0) {            
            limitString = " LIMIT " + (noOfLines).toString() + " ";
            all = false;
        } else {
            limitString = " ";
            all=true;
        }
        try {
            if (sQuantity != 0) {
                stocks = Main.getConnection().prepareStatement(sqlQ + limitString);
                stocks.setBoolean(1, all);
                stocks.setInt(2, sQuantity);
            } else if (sDepartment != 0) {
                stocks = Main.getConnection().prepareStatement(sqlD + limitString);
                stocks.setBoolean(1, all);
                stocks.setInt(2, sDepartment);
            } else if (sSku != 0) {
                stocks = Main.getConnection().prepareStatement(sqlS + limitString);
                stocks.setInt(1, sSku);
            } else if (sSupplier!=0){
                stocks = Main.getConnection().prepareStatement(skuY + limitString);
                stocks.setBoolean(1, all);
                stocks.setInt(2, sSupplier);
            } else {
                stocks = Main.getConnection().prepareStatement(sql);
                stocks.setInt(1, sDepartment);
                stocks.setInt(2, sDepartment);//need each parameter twice
                stocks.setInt(3, sSku);
                stocks.setInt(4, sSku);
                stocks.setBoolean(5, all);
                stocks.setBoolean(6, sStopped);
                stocks.setInt(7, sQuantity);
                stocks.setInt(8, sQuantity);
            }
            rs = stocks.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            rowDataOld.clear();
            while (rs.next() && (ctr < noOfLines||noOfLines==0)) {//sku,description,quantity,date,product
                sku = rs.getInt("S.ID");
                description = rs.getString("P.Description");
                quantity = rs.getInt("S.Quantity");
                date = rs.getDate("WhenCreated");
                product = rs.getLong("P.ID");
                price = rs.getInt("P.Price");
                Object[] line1 = {sku, description, quantity, date,
                    product, price
                };
                Object[] line2 = {sku, description, quantity, date,
                    product, price
                };
                rowData.add(line1);
                rowDataOld.add(line2);//make a copy
                ctr++;
            }
            rs.close();
            model.fireTableDataChanged();
        } catch (SQLException ex) {
            Logger.getLogger(StockTake_1.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void closeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        Object[] options = {bundle.getString("CloseWithoutSaving"),
                    bundle.getString("SaveAndClose")};
                switch (JOptionPane.showOptionDialog(this, bundle.getString("CloseWithoutSaving"), "Which?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
                    case JOptionPane.CLOSED_OPTION:
                        return;
                    case JOptionPane.NO_OPTION:
                        ok();
                        sku = 0;//so a new sales record created next time
                        setVisible(false);
                    case JOptionPane.OK_OPTION:
                        sku = 0;//so a new sales record created next time
                        setVisible(false);
                }
}//GEN-LAST:event_closeButton2ActionPerformed

    private void closeButton2KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_closeButton2KeyReleased
}//GEN-LAST:event_closeButton2KeyReleased

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        
        Main.receiptPrinter.startReceipt();
        Main.receiptPrinter.printDateTime();
        Main.receiptPrinter.printLine(" ");
        //scan through all of the list
        for(int i=0;i<jTable1.getModel().getRowCount();i++){
            description=(String)jTable1.getModel().getValueAt(i, 1);
            quantity = (Integer)jTable1.getModel().getValueAt(i, 2);
            product = (Long)jTable1.getModel().getValueAt(i, 4);//the barcode
            String s = ""+ quantity ; 
            s= String.format("%04d", quantity)+" ";
            s += (String) jTable1.getModel().getValueAt(i, 5)+" ";
            s +=  description;
            if(!includeBarcodeCheckBox.isSelected()){//no bacode
                s=StringOps.fixLengthUntrimmed(s, Main.hardware.getCharsOnReceipt());
            } else {
                s=StringOps.fixLengthUntrimmed(s, Main.hardware.getCharsOnReceipt()-("  "+product).length());
                s+="  "+product;
            }
            Main.receiptPrinter.printLine(s);
            if((i+1)%10==0){
                Main.receiptPrinter.printLine(" ");
            }
        }
        Main.receiptPrinter.endReceipt();
    }//GEN-LAST:event_printButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        ok();
    }//GEN-LAST:event_okButtonActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        jTable1.setColumnSelectionInterval(2, 2);
    }//GEN-LAST:event_jTable1MouseClicked

    private void includeBarcodeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_includeBarcodeCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_includeBarcodeCheckBoxActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Object[] options = {bundle.getString("CloseWithoutSaving"),
                    bundle.getString("SaveAndClose")};
                switch (JOptionPane.showOptionDialog(this, bundle.getString("CloseWithoutSaving"), "Which?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0])) {
                    case JOptionPane.CLOSED_OPTION:
                        return;
                    case JOptionPane.NO_OPTION:
                        //save and close
                        ok();
                        firstTime= true;       
                        dispose();
                    case JOptionPane.OK_OPTION:
                        //close without saving
                        firstTime= true;
                        dispose();
                }
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                StockTake_1 dialog = new StockTake_1(new javax.swing.JFrame(), true);
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

    private void ok() {
        //change all to todays date
        Audio.play("Vibes");
        Main.salesScreen.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        if (jTable1.getRowCount() > 0) {//more than a header
            try {
                for ( row = 0; row < jTable1.getRowCount(); row++) {
//                    newQuantity=(Integer)jTable1.getModel().getValueAt(row, 2);
                    String ns=jTable1.getModel().getValueAt(row, 2).toString();
                    ns = StringOps.numericOnly(ns);
                    newQuantity= Integer.parseInt(ns);
                    Object[] l = rowDataOld.elementAt(row);
                    oldQuantity = (Integer) l[2];
//                    oldQuantity = 
                    sku=(Integer) l[0];
                    //update sku
                    PreparedStatement ps =
                            Main.getConnection().prepareStatement(updateSku);
                    ps.setInt(1, newQuantity);
                    ps.setNull(2, Types.DATE);
                    ps.setInt(3, sku);
                    qty =  oldQuantity - newQuantity;
                    ps.execute();
                    if (qty != 0) {
                        //create a 'sale' for loss
                        if (firstTime) {//first time only
                            //create a sale
                            firstTime = false;
                            PreparedStatement np = Main.getConnection().prepareStatement(
                                    saleInsert1
                                    + (SaleType.CUSTOMER.code() * 10000l)
                                    + saleInsert2);
                            np.setNull(1, Types.INTEGER);
                            np.setInt(2, Main.operator.getOperator());
                            np.setShort(3, SaleType.LOSS.value());
                            np.executeUpdate();//need to get at last ID
                            PreparedStatement np1 = Main.getConnection().prepareStatement(lastSale);
                            rs = np1.executeQuery();
                            rs.first();
                            sale = rs.getInt(1);
                            rs.close();
                        }
//                        sku = (Integer) jTable1.getModel().getValueAt(row, 0);
                        description = (String) jTable1.getModel().getValueAt(row, 1);
                        quantity = qty;
                        java.util.Date today = new java.util.Date();
                        date = new java.sql.Date(today.getTime());
                        product = (Long) jTable1.getModel().getValueAt(row, 4);
                        String priceString = (String) jTable1.getModel().getValueAt(row, 5);
                        price = Integer.parseInt(StringOps.numericOnly(priceString));
                        PreparedStatement gwp = Main.getConnection().prepareStatement(
                                "SELECT Packs.Size,PackSuppliers.Price,Products.Encoded "
                                + "FROM Packs,PackSuppliers,Products "
                                + "WHERE PackSuppliers.Pack=Packs.ID "
                                + "AND Products.ID=? "
                                + "AND Packs.Product=Products.ID "
                                + "ORDER BY PackSuppliers.WhenCreated DESC");
                        gwp.setLong(1, product);
                        ResultSet rs1 = gwp.executeQuery();
                        if (rs1.first()) {   
                            encode=rs1.getInt("Encoded");
                            if(encode==NewProduct.NOTENCODE){
                                wholesalePrice = rs1.getInt("PackSuppliers.Price") / rs1.getInt("Size");
                            }else {
                                wholesalePrice = rs1.getInt("PackSuppliers.Price") / rs1.getInt("Size");
                            }
                        } else {//should not happen
                            wholesalePrice = price;
                        }                        
                        rs1.close();
                        //get the tax
                        PreparedStatement gt =
                                Main.getConnection().prepareStatement(getTax);
                        gt.setLong(1, product);
                        rs = gt.executeQuery();
                        rs.first();
                        tax = rs.getInt("TaxID");
                        rate=rs.getInt("Tax");
                        rs.close();
                        PreparedStatement np2 = Main.getConnection().prepareStatement(insertSaleLine);
                        np2.setNull(1, Types.INTEGER);
                        np2.setInt(2, sale);
                        np2.setInt(3, qty);
                        np2.setLong(4, product);
                        np2.setInt(5, wholesalePrice);
                        np2.setString(6, "");
                        np2.setInt(7,encode);
                        np2.setInt(8, 0);
                        np2.setInt(9, 0);
                        np2.setInt(10, wholesalePrice);
                        np2.setInt(11, tax);
                        np2.setInt(12, rate);
                        np2.execute();
                        //need to update total in the sale
                        String utis = "UPDATE Sales SET Total=Total+?,Tax=0 WHERE ID=?";
                        PreparedStatement psu = Main.getConnection().prepareStatement(utis);
                        if(encode==NewProduct.NOTENCODE){
                            psu.setInt(1, qty * wholesalePrice);
                        }else{
                            psu.setInt(1, qty * wholesalePrice/1000);
                        }
                        psu.setInt(2, sale);
                        psu.execute();
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(StockTake_1.class.getName()).log(Level.SEVERE, null, ex);
                Audio.play("Ring");
            }
            Audio.play("Beep");
        }
        sku = 0;//so a new sales record created next time
        Main.salesScreen.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        if(noOfLines!=0){
            drawGrid(noOfLines,department, sku,
                stopped, quantity, supplier);
        } else {
            setVisible(false);
        }
    }

    public class JT extends JTextField {

        String value;
        private int col;
        private int row;

        public JT() {
            super();
            selectAll();
        }

        
    }

    public class StockLevelEditor extends AbstractCellEditor 
        implements FocusListener,  TableCellEditor {
        // This is the component that will handle the editing of the cell value
        JT jT;

        public StockLevelEditor() {
            jT = new JT();
        }

        // This method is called when a cell value is edited by the user.
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int col) {
            jT.selectAll();
            // 'value' is value contained in the cell located at (rowIndex, vColIndex)
            if (col != 2) {
                return null;
            }
            // cell (and perhaps other cells) are selected
            Object o = jTable1.getModel().getValueAt(row, col);
            jT.setText(o.toString());
            jT.selectAll();
            return jT;
        }
        // This method is called when editing is completed.
        // It must return the new value to be stored in the cell.

        public Object getCellEditorValue() {
            jT.selectAll();
            return jT.getText();
        }

//       

        public void focusGained(FocusEvent e) {
            jT.selectAll();
        }

        public void focusLost(FocusEvent e) {
//            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton2;
    private javax.swing.JCheckBox includeBarcodeCheckBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton okButton;
    private javax.swing.JButton printButton;
    // End of variables declaration//GEN-END:variables
    private boolean firstTime = true;
    private int sale = 0;
}
