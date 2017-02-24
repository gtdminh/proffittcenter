/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Products.java
 *
 * Created on 10-Mar-2009, 13:38:14
 */
package proffittcenter;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class Products extends EscapeDialog {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Products");
    Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("Barcode"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Description"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Date"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Price") + " " + Main.shop.poundSymbol + " ");
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("Tax"));
    @SuppressWarnings("unchecked")
    private boolean b5 = tableHeader.add(bundle.getString("Department"));
    @SuppressWarnings("unchecked")
    private boolean b6 = tableHeader.add(bundle.getString("Sku"));
    @SuppressWarnings("unchecked")
    private boolean b7 = tableHeader.add(bundle.getString("Quantity"));
    private Long barcode;
    private String description;
    private Date date;
    private Integer price;
    private String tax;
    private String department;
    private Integer sku;
    private int quantity;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private int columnSelected;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT,
        SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEFT,
        SwingConstants.LEFT, SwingConstants.RIGHT,SwingConstants.RIGHT};
    private Object[] line = {barcode, description, date, price, tax, department, sku,quantity};
    private Vector<Object[]> rowData = new Vector<Object[]>();
    private NumberFormat nf = NumberFormat.getInstance();
    private AbstractTableModel model = new AbstractTableModel() {

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
            if (columnIndex == 0) {//barcode
                return line[0];
            } else if (columnIndex == 1) {//description
                return line[1];
            } else if (columnIndex == 2) {//date
                return line[2];
            } else if (columnIndex == 3) {//price
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMinimumFractionDigits(2);
                return nf.format((new Double((Integer) line[3])) / 100);
            } else if (columnIndex == 4) {//tax
                return line[4];
            } else if (columnIndex == 5) {//department
                return line[5];
            } else if (columnIndex == 6) {//sku
                return line[6];
            } else if (columnIndex == 7) {//quantity
                return line[7];
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private int selection;

    /** Creates new form Products */
    public Products(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(0).setPreferredWidth(300);
        tc.getColumn(1).setPreferredWidth(550);
        tc.getColumn(2).setPreferredWidth(240);
        tc.getColumn(3).setPreferredWidth(200);
        tc.getColumn(4).setPreferredWidth(250);
        tc.getColumn(5).setPreferredWidth(250);
        tc.getColumn(6).setPreferredWidth(150);
        tc.getColumn(7).setPreferredWidth(150);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        jTable1.setDefaultRenderer(Long.class, mtcr);
        jTable1.setDefaultRenderer(Date.class, mtcr);
        JTableHeader header = jTable1.getTableHeader();
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Products", Main.mainHelpSet);
        JRootPane rp = getRootPane();
        rp.setDefaultButton(sellButton);
    }

   

    public long execute() {
        selection=-1;
        columnSelected = 1;//description
        mhr.setHeader(jTable1);
        drawGrid();
        barcode=0l;
        Audio.play("Tada");
        setVisible(true);
        return barcode;
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
        detailsButton = new javax.swing.JButton();
        closeButton2 = new javax.swing.JButton();
        printBarcode = new javax.swing.JButton();
        sellButton = new javax.swing.JButton();
        jPackSuppliers = new javax.swing.JButton();
        printBtn = new javax.swing.JButton();
        skuButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/Products"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("Products"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setName("jPanel1"); // NOI18N

        detailsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Info.png"))); // NOI18N
        detailsButton.setContentAreaFilled(false);
        detailsButton.setName("detailsButton"); // NOI18N
        detailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailsButtonActionPerformed(evt);
            }
        });

        closeButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton2.setContentAreaFilled(false);
        closeButton2.setName("closeButton2"); // NOI18N
        closeButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButton2ActionPerformed(evt);
            }
        });

        printBarcode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Barcode.png"))); // NOI18N
        printBarcode.setContentAreaFilled(false);
        printBarcode.setName("printBarcode"); // NOI18N
        printBarcode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBarcodeActionPerformed(evt);
            }
        });

        sellButton.setText(bundle.getString("sell")); // NOI18N
        sellButton.setName("sellButton"); // NOI18N
        sellButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sellButtonActionPerformed(evt);
            }
        });

        jPackSuppliers.setText(bundle.getString("Products.jPackSuppliers.text")); // NOI18N
        jPackSuppliers.setName("jPackSuppliers"); // NOI18N
        jPackSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPackSuppliersActionPerformed(evt);
            }
        });

        printBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/print_edit.gif"))); // NOI18N
        printBtn.setText("null");
        printBtn.setToolTipText(bundle.getString("Products.printBtn.toolTipText")); // NOI18N
        printBtn.setContentAreaFilled(false);
        printBtn.setName("printBtn"); // NOI18N
        printBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printBtnActionPerformed(evt);
            }
        });

        skuButton.setText(bundle.getString("Products.skuButton.text")); // NOI18N
        skuButton.setName("skuButton"); // NOI18N
        skuButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                skuButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(printBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(printBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 611, Short.MAX_VALUE)
                .addComponent(sellButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(skuButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(detailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPackSuppliers)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(skuButton)
                        .addComponent(sellButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(printBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(closeButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(printBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPackSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(detailsButton, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {closeButton2, detailsButton, printBarcode});

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 994, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 613, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void detailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailsButtonActionPerformed
        selection = jTable1.getSelectedRow();
        if (selection == -1) {//no selected row
            return;
        }
        selection=jTable1.convertRowIndexToModel(selection);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        barcode =(Long) model.getValueAt(selection, 0);
        boolean b = Main.product.execute(barcode.toString());
        drawGrid();
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        barcode=0l;
}//GEN-LAST:event_detailsButtonActionPerformed

    private void closeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        barcode=0l;
        setVisible(false);
}//GEN-LAST:event_closeButton2ActionPerformed

    private void printBarcodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBarcodeActionPerformed
        int selections[] = jTable1.getSelectedRows();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        for (int i = 0; i < selections.length; i++) {
            barcode = (Long) model.getValueAt(selections[i], 0);
            description=(String) model.getValueAt(selections[i], 1);
            if(!BarcodePrinter.print(barcode, description)){
                break;
            }
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        barcode=0l;
}//GEN-LAST:event_printBarcodeActionPerformed

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if(KeyEvent.VK_ENTER==evt.getKeyCode()){
            selection = jTable1.getSelectedRow();
            selection=jTable1.convertRowIndexToModel(selection);
            barcode = (Long) model.getValueAt(selection, 0);
            evt.setKeyCode(KeyEvent.VK_SPACE);
            setVisible(false);
        } else if (KeyEvent.VK_PRINTSCREEN==evt.getKeyCode()){
            //print shelf edge labels on receipt printer
            int selections[] = jTable1.getSelectedRows();
            for (int i = 0; i < selections.length; i++) {
                barcode = (Long) model.getValueAt(selections[i], 0);
                description=(String) model.getValueAt(selections[i], 1);
                BarcodePrinter.print(barcode, description);
            }
        }
    }//GEN-LAST:event_jTable1KeyPressed

    private void sellButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sellButtonActionPerformed
        selection = jTable1.getSelectedRow();
        if(selection>=0){
            selection=jTable1.convertRowIndexToModel(selection);
            barcode = (Long) model.getValueAt(selection, 0);
            setVisible(false);
        }
    }//GEN-LAST:event_sellButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        barcode=0l;
    }//GEN-LAST:event_formWindowClosing

    private void jPackSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPackSuppliersActionPerformed
        String product;
        selection = jTable1.getSelectedRow();
        if (selection >= 0) {
            selection=jTable1.convertRowIndexToModel(selection);
            product = ""+(Long) model.getValueAt(selection, 0);
            Main.packSuppliers.execute(product);
            barcode = 0l;
        }
    }//GEN-LAST:event_jPackSuppliersActionPerformed

    private void printBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printBtnActionPerformed
        selection = jTable1.getSelectedRow();
        if (selection >= 0) {
            selection=jTable1.convertRowIndexToModel(selection);
            barcode = (Long) model.getValueAt(selection, 0);
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
            description = (String) model.getValueAt(selection, 1);
            String s = description;
    //        s="012345678901234567890123456789012345678901234567890123456789";
            Main.receiptPrinter.printLargeCentralLine(s);
            Float f=(Float.parseFloat((String) model.getValueAt(selection, 3)))*100;
            price=f.intValue();
            s = (new Money(price).toString());
            Main.receiptPrinter.printLargeCentralLine(s);
            for (int i = 0; i < blankLinesAfterPrinting; i++) {
                Main.receiptPrinter.printLine(" ");
            }
            Main.receiptPrinter.endPrinter();
        }
}//GEN-LAST:event_printBtnActionPerformed

    private void skuButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_skuButtonActionPerformed
        try {
            selection = jTable1.getSelectedRow();
            if (selection == -1) {//no selected row
                return;
            }
            selection=jTable1.convertRowIndexToModel(selection);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            barcode =(Long) model.getValueAt(selection, 0);
            PreparedStatement pssku = Main.getConnection().prepareStatement("SELECT Sku FROM Products WHERE ID=? ");
            pssku.setLong(1, barcode);
            ResultSet rs=pssku.executeQuery();
            if(rs.first()){
                sku=rs.getInt("Sku");
            }
            Main.sku.execute(sku);            
            drawGrid();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            barcode=0l;
        } catch (SQLException ex) {
            Logger.getLogger(Products.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_skuButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Products dialog = new Products(new javax.swing.JFrame(), true);
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

    private void drawGrid() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PreparedStatement products;
        try {
            if (columnSelected == 0) {
                products = Main.getConnection().prepareStatement(SQL.productsSQL + SQL.bc);
            } else if (columnSelected == 1) {
                products = Main.getConnection().prepareStatement(SQL.productsSQL + SQL.pd);
            } else if (columnSelected == 2) {
                products = Main.getConnection().prepareStatement(SQL.productsSQL + SQL.dt);
            } else if (columnSelected == 3) {
                products = Main.getConnection().prepareStatement(SQL.productsSQL + SQL.pr);
            } else if (columnSelected == 5) {
                products = Main.getConnection().prepareStatement(SQL.productsSQL + SQL.dp);
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            ResultSet rs = products.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            while (rs.next()) {//departments to display
                barcode = rs.getLong("Products.ID");
                description = rs.getString("Description");
                date = rs.getDate("WhenCreated");
                price = rs.getInt("Price");
                tax = rs.getString("Taxes.Description");
                department = rs.getString("Departments.Description");
                sku = rs.getInt("Products.Sku");
                quantity = rs.getInt("Quantity");
                Object[] line1 = {barcode, description, date, price, tax, department, sku,quantity};
                rowData.add(line1);
            }
            rs.close();
            products.close();
                model.fireTableDataChanged();
            barcode = 0l;
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(Products.class.getName()).log(Level.SEVERE, null, ex);
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        jTable1.repaint();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton closeButton2;
    private javax.swing.JButton detailsButton;
    private javax.swing.JButton jPackSuppliers;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton printBarcode;
    private javax.swing.JButton printBtn;
    private javax.swing.JButton sellButton;
    private javax.swing.JButton skuButton;
    // End of variables declaration//GEN-END:variables
}
