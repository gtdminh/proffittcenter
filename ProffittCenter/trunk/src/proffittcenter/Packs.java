/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PacksEscape.java
 *
 * Created on 30-Oct-2009, 17:40:46
 */
package proffittcenter;

import java.awt.Cursor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class Packs extends EscapeDialog {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Packs");
    private String productSelection = "SELECT Products.Description,Products.ID,Packs.*,PackSuppliers.ID," +
            "Suppliers.Description " +
            "FROM Products,Packs,PackSuppliers,Suppliers " +
            "WHERE Packs.Product=Products.ID AND PackSuppliers.Supplier=Suppliers.ID " +
            "AND Packs.ID=PackSuppliers.Pack AND (Products.ID=?  AND Products.Encoded = 0 OR "
            + "CONCAT(SUBSTRING(Products.ID FROM 1 FOR 7),'000000') =? AND (Products.Encoded=1 OR Products.Encoded=2) "
            + "OR CONCAT(SUBSTRING(Products.ID FROM 1 FOR 8),'00000')=? AND (Products.Encoded=3 OR Products.Encoded=4)) "
            + "ORDER BY ";
    private String allSelection = "SELECT Products.Description,Products.ID,Packs.*,PackSuppliers.ID," +
            "Suppliers.Description FROM Products,Packs,PackSuppliers, " +
            "Suppliers " +
            "WHERE Packs.ID=PackSuppliers.Pack " +
            "AND Packs.Product=Products.ID AND '0'=? " +
            "AND PackSuppliers.Supplier=Suppliers.ID " +
            "ORDER BY ";
    private String skuSelection = "SELECT Products.*,Packs.*,PackSuppliers.ID," +
            "Suppliers.Description FROM Products,Packs,PackSuppliers, " +
            "Suppliers " +
            "WHERE Packs.Product=Products.ID AND Packs.ID=PackSuppliers.Pack " +
            "AND PackSuppliers.Supplier=Suppliers.ID AND Products.Sku=?  " +
            "ORDER BY ";
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("Product"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Pack"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Code"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Case_size"));
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("Pack_supplier"));
    @SuppressWarnings("unchecked")
    private boolean b5 = tableHeader.add(bundle.getString("Supplier"));
    @SuppressWarnings("unchecked")
    private boolean b6 = tableHeader.add(bundle.getString("Barcode"));
    private String description;
    private int pack;
    private String code;
    private int caseSize;
    private int packSupplier;
    private String product;
    private String supplier;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.RIGHT,
        SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.RIGHT,
        SwingConstants.RIGHT,SwingConstants.RIGHT};
    private Object[] line = new Object[]{description, pack, code, caseSize, packSupplier,
        supplier, product };
    private Vector<Object[]> rowData = new Vector<Object[]>();
    private AbstractTableModel model = new AbstractTableModelImpl();
    private int columnSelected=0;
    private String barcode;
    private ResultSet rs;

    /** Creates new form PacksEscape */
    public Packs(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        mhr = new MyHeaderRenderer(align);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(0).setPreferredWidth(500);
        tc.getColumn(1).setPreferredWidth(100);
        tc.getColumn(2).setPreferredWidth(400);
        tc.getColumn(3).setPreferredWidth(100);
        tc.getColumn(4).setPreferredWidth(100);
        tc.getColumn(5).setPreferredWidth(400);
        tc.getColumn(6).setPreferredWidth(400);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        jTable1.setAutoCreateRowSorter(true);
        getRootPane().setDefaultButton(searchButton);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Packs", Main.mainHelpSet);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        skuText = new javax.swing.JTextField();
        productT = new javax.swing.JTextField();
        deletePackBtn = new javax.swing.JButton();
        deletePackSupplierBtn = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPackSuppliers = new javax.swing.JButton();
        searchButton = new javax.swing.JButton();
        infoButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/Packs"); // NOI18N
        setTitle(bundle.getString("title")); // NOI18N
        setName("Packs"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        skuText.setName("skuText"); // NOI18N
        skuText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                skuTextFocusLost(evt);
            }
        });

        productT.setName("productT"); // NOI18N
        productT.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                productTFocusLost(evt);
            }
        });
        productT.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                productTKeyReleased(evt);
            }
        });

        deletePackBtn.setText(bundle.getString("Packs.deletePackBtn.text_1")); // NOI18N
        deletePackBtn.setEnabled(false);
        deletePackBtn.setName("deletePackBtn"); // NOI18N
        deletePackBtn.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                deletePackBtnFocusGained(evt);
            }
        });
        deletePackBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletePackBtnActionPerformed(evt);
            }
        });

        deletePackSupplierBtn.setText(bundle.getString("Packs.deletePackSupplierBtn.text_1")); // NOI18N
        deletePackSupplierBtn.setEnabled(false);
        deletePackSupplierBtn.setName("deletePackSupplierBtn"); // NOI18N
        deletePackSupplierBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deletePackSupplierBtnActionPerformed(evt);
            }
        });

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        closeButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                closeButtonKeyReleased(evt);
            }
        });

        jLabel1.setText(bundle.getString("Packs.jLabel1.text_1")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(bundle.getString("Packs.jLabel2.text_1")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jPackSuppliers.setText(bundle.getString("Packs.jPackSuppliers.text")); // NOI18N
        jPackSuppliers.setName("jPackSuppliers"); // NOI18N
        jPackSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPackSuppliersActionPerformed(evt);
            }
        });

        searchButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/OK.png"))); // NOI18N
        searchButton.setContentAreaFilled(false);
        searchButton.setName("searchButton"); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        infoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Info.png"))); // NOI18N
        infoButton.setName("infoButton"); // NOI18N
        infoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(skuText, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(productT, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPackSuppliers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(deletePackBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(deletePackSupplierBtn)
                .addGap(18, 18, 18)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPackSuppliers, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(deletePackBtn, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addComponent(closeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(deletePackSupplierBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(skuText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(productT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)
                        .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(infoButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {deletePackBtn, infoButton, jPackSuppliers});

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 941, Short.MAX_VALUE))
                .addContainerGap(69, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    void execute() {
        columnSelected=0;
        deletePackBtn.setEnabled(false);
        deletePackSupplierBtn.setEnabled(false);
        jPackSuppliers.setEnabled(false);
        productT.setText("");
        skuText.setText("");
        drawGrid("0", allSelection);
        Audio.play("Tada");
        productT.requestFocus();
        setVisible(true);
    }

     private void drawGrid(String data, String selectionStatement) {
         setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Object[] line={description,pack,code,caseSize,packSupplier,supplier,product};
        PreparedStatement packSt;
        product = data;
        String searchBy="";
        try {
            if (columnSelected == 0) {
                searchBy = selectionStatement + "Products.Description";
            } else if (columnSelected == 1) {
                searchBy = selectionStatement + "Packs.ID";
            } else if (columnSelected == 2) {
                searchBy = selectionStatement + "Packs.Code";
            } else if (columnSelected == 3) {
                searchBy = selectionStatement + "Packs.Size";
            } else if (columnSelected == 4) {
                searchBy = selectionStatement + "PackSuppliers.ID";
            } else if (columnSelected == 5) {
                searchBy = selectionStatement + "Suppliers.Description";
            }else if (columnSelected == 6) {
                searchBy = selectionStatement + "Products.ID";
            }else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            packSt = Main.getConnection().prepareStatement(searchBy);
            if (!data.isEmpty()) {
                packSt.setString(1, data);
                if(selectionStatement == null ? productSelection == null : selectionStatement.equals(productSelection)){
                    packSt.setString(2, NewProduct.shortenForEncoded7(data));
                    packSt.setString(3, NewProduct.shortenForEncoded8(data));
                }
            } else {
                return;
            }
            rs = packSt.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            model.fireTableDataChanged();
            while (rs.next()) {//departments to display
                product = rs.getString("Products.ID");
                description = rs.getString("Products.Description");
                pack = rs.getInt("Packs.ID");
                code = rs.getString("Packs.Code");
                caseSize = rs.getInt("Packs.Size");
                packSupplier = rs.getInt("PackSuppliers.ID");
                supplier = rs.getString("Suppliers.Description");
                Object[] line1 = {description, pack, code, caseSize,
                    packSupplier, supplier, product
                };
                rowData.add(line1);
                model.fireTableDataChanged();
            }
            rs.close();
            productT.setText("");
            skuText.setText("");
        } catch (SQLException ex) {
            Logger.getLogger(Packs.class.getName()).log(Level.SEVERE, null, ex);
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    private void closeButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_closeButtonKeyReleased
//        StringOps.nextComponent(evt, closeButton, closeButton);
}//GEN-LAST:event_closeButtonKeyReleased

    private void productTKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_productTKeyReleased
        String data = productT.getText();
        if (data.length() < 3) {
            return;
        }
        if(Main.alphaLookup.isBarcode(data))return;
        if(Main.alphaLookup.isFound(data)){
            productT.setText(Main.alphaLookup.returnDataIs());
        } else {
            productT.setText("");
        }
}//GEN-LAST:event_productTKeyReleased

    private void deletePackBtnFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_deletePackBtnFocusGained
        String data;
        data = productT.getText();
        if (data.length() < 8) {
            return;
        }
        drawGrid(data, productSelection);
        data = productT.getText();
        productT.setText("");
    }//GEN-LAST:event_deletePackBtnFocusGained

    private void deletePackBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePackBtnActionPerformed
        try {
            int selection = jTable1.getSelectedRow();
            if (selection == -1) {
                //no selected row
                return;
            }
            PreparedStatement ps;
            selection=jTable1.convertRowIndexToModel(selection);
            pack = (Integer) model.getValueAt(selection, 1);
            //delete any linked packSupliers
            String packCount ="SELECT COUNT(*),ID FROM Packs WHERE Product=?";
            product = (String) model.getValueAt(selection, 6);
            ps = Main.getConnection().prepareStatement(packCount);
            ps.setLong(1, Long.parseLong(product));
            rs=ps.executeQuery();
            rs.next();
            int rowCount = rs.getInt(1);
            if(rowCount>1){
                String dps = "DELETE FROM PackSuppliers WHERE Pack=?";
                ps = Main.getConnection().prepareStatement(dps);
                ps.setInt(1, pack);
                ps.executeUpdate();
                String dp="DELETE FROM Packs WHERE ID=?";
                //Now delete the pack selected
                ps=Main.getConnection().prepareStatement(dp);
                ps.setInt(1, pack);
                ps.executeUpdate();
                drawGrid("0", allSelection);
            } else {
                Audio.play("Ring");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Packs.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_deletePackBtnActionPerformed

    private void deletePackSupplierBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deletePackSupplierBtnActionPerformed
        try {
            int selection = jTable1.getSelectedRow();
            if (selection == -1) {
                //no selected row
                return;
            }
            selection=jTable1.convertRowIndexToModel(selection);
            packSupplier = (Integer) model.getValueAt(selection, 4);
            pack = (Integer) model.getValueAt(selection, 1);
            String countPackSuppliers="(SELECT COUNT(*)FROM PackSuppliers WHERE Pack=?)";
            PreparedStatement ps = Main.getConnection().prepareStatement(countPackSuppliers);
            ps.setInt(1, pack);
            rs=ps.executeQuery();
            rs.next();
            int rowCount = rs.getInt(1);
            if(rowCount==1){
                //Delete the packSupplier record selected
                String dps = "DELETE FROM PackSuppliers WHERE ID=?";
                ps = Main.getConnection().prepareStatement(dps);
                ps.setInt(1, packSupplier);
                ps.executeUpdate();
            } else {
                Audio.play("Ring");
            }
        } catch (SQLException ex) {
            Logger.getLogger(Packs.class.getName()).log(Level.SEVERE, null, ex);
        }
        drawGrid("0", allSelection);
    }//GEN-LAST:event_deletePackSupplierBtnActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int i=jTable1.getSelectedRow();
        deletePackBtn.setEnabled(i!=-1);
        deletePackSupplierBtn.setEnabled(i!=-1);
        jPackSuppliers.setEnabled(i!=-1);
    }//GEN-LAST:event_jTable1MouseClicked

    private void jPackSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPackSuppliersActionPerformed
        int selection = jTable1.getSelectedRow();
        if(selection>=0){
            selection=jTable1.convertRowIndexToModel(selection);
            barcode = (String) model.getValueAt(selection, 6);
            Main.packSuppliers.execute(barcode);
        }
    }//GEN-LAST:event_jPackSuppliersActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String skuString = StringOps.numericOnly(skuText.getText());
        String productString = StringOps.numericOnly(productT.getText());
        if(productString.isEmpty()){
            if(skuString.length()>0){
                drawGrid(skuString, skuSelection);
                Audio.play("Beep");
                skuText.requestFocus();
            }
        }else if (skuString.isEmpty()){
            drawGrid(productString, productSelection);
            Audio.play("Beep");
            productT.requestFocus();
        }else {
            drawGrid("0", allSelection);
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void productTFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_productTFocusLost
        String skuString = StringOps.numericOnly(skuText.getText());
        String productString = StringOps.numericOnly(productT.getText());
        if(productString.isEmpty()){
            if(skuString.length()>0){
                drawGrid(skuString, skuSelection);
                Audio.play("Beep");
                skuText.requestFocus();
            }
        }else if (skuString.isEmpty()){
            drawGrid(productString, productSelection);
            Audio.play("Beep");
            productT.requestFocus();
        }else {
            drawGrid("0", allSelection);
        }
    }//GEN-LAST:event_productTFocusLost

    private void skuTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_skuTextFocusLost
         String skuString = StringOps.numericOnly(skuText.getText());
        String productString = StringOps.numericOnly(productT.getText());
        if(productString.isEmpty()){
            if(skuString.length()>0){
                drawGrid(skuString, skuSelection);
                Audio.play("Beep");
                skuText.requestFocus();
            }
        }else if (skuString.isEmpty()){
            drawGrid(productString, productSelection);
            Audio.play("Beep");
            productT.requestFocus();
        }else {
            drawGrid("0", allSelection);
        }
    }//GEN-LAST:event_skuTextFocusLost

    private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        int selection = jTable1.getSelectedRow();
        if(selection>=0){
            selection=jTable1.convertRowIndexToModel(selection);
            pack = (Integer) model.getValueAt(selection, 1);
            Main.pack.execute(pack);
            drawGrid("0", allSelection);
        }
    }//GEN-LAST:event_infoButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Packs dialog = new Packs(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton closeButton;
    private javax.swing.JButton deletePackBtn;
    private javax.swing.JButton deletePackSupplierBtn;
    private javax.swing.JButton infoButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton jPackSuppliers;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField productT;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField skuText;
    // End of variables declaration//GEN-END:variables

    private class AbstractTableModelImpl extends AbstractTableModel {

        public AbstractTableModelImpl() {
        }

        @Override
        public String getColumnName(int col) {
            return (String) tableHeader.get(col);
        }

        public int getColumnCount() {
            return tableHeader.size();
        }

        public int getRowCount() {
            return rowData.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {//description
                return line[0];
            } else if (columnIndex == 1) {//pack
                return line[1];
            } else if (columnIndex == 2) {//code
                return line[2];
            } else if (columnIndex == 3) {//caseSize
                return line[3];
            } else if (columnIndex == 4) {//packSupplier
                return line[4];
            } else if (columnIndex == 5) {//supplier
                return line[5];
            } else if (columnIndex == 6) {//barcode
                return line[6];
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            Object o = line[c];
            if(o==null){
                return String.class;
            }
            Class cl = o.getClass();
            return cl;
        }
    }

//    @Override
//    public void mouseClicked(MouseEvent evt) {
//        TableColumnModel colModel = jTable1.getColumnModel();
//        // The index of the column whose header was clicked
//        int vColIndex = colModel.getColumnIndexAtX(evt.getX());
//        //int mColIndex = jTable1.convertColumnIndexToModel(vColIndex);
//        // Return if not clicked on any column header
//        if (vColIndex == -1) {
//            return;
//        }
//        // Determine if mouse was clicked between column heads
//        Rectangle headerRect = jTable1.getTableHeader().getHeaderRect(vColIndex);
//        if (vColIndex == 0) {
//            headerRect.width -= 3;    // Hard-coded constant
//        } else {
//            headerRect.grow(-3, 0);   // Hard-coded constant
//        }
//        if (!headerRect.contains(evt.getX(), evt.getY())) {
//            // Mouse was clicked between column heads
//            // vColIndex is the column head closest to the click
//            // vLeftColIndex is the column head to the left of the click
//            int vLeftColIndex = vColIndex;
//            if (evt.getX() < headerRect.x) {
//                vLeftColIndex--;
//            }
//        }
//        columnSelected = vColIndex;
//        drawGrid("0", allSelection);
//    }
//
//    @Override
//    public void mousePressed(MouseEvent e) {
//        //do nothing
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e) {
//        //do nothing
//    }
//
//    @Override
//    public void mouseEntered(MouseEvent e) {
//        //do nothing
//    }
//
//    @Override
//    public void mouseExited(MouseEvent e) {
//        //do nothing
//    }
}
