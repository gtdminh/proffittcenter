/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EncoddProducts.java
 *
 * Created on 09-Mar-2010, 22:08:39
 */
package proffittcenter;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class EncodedProducts extends EscapeDialog {

    /** Creates new form EncoddProducts */
    private Preferences root = Preferences.userNodeForPackage(getClass());
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/EncodedProducts");
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("Barcode"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Description"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Date"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Price") + " " + Main.shop.poundSymbol);
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("Tax"));
    @SuppressWarnings("unchecked")
    private boolean b5 = tableHeader.add(bundle.getString("Department"));
    @SuppressWarnings("unchecked")
    private boolean b6 = tableHeader.add(bundle.getString("Sku"));
    @SuppressWarnings("unchecked")
    private boolean b7 = tableHeader.add(bundle.getString("Encoded"));
    private Long barcode;
    private String description;
    private Date date;
    private int price;
    private String tax;
    private String department;
    private Integer sku;
    private int columnSelected = 0;
    private String productsString;
    private Object encoded;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private int align[] = {SwingConstants.LEFT, SwingConstants.LEFT,
        SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEFT,
        SwingConstants.LEFT, SwingConstants.RIGHT, SwingConstants.LEFT};
    private Object[] line = {barcode, description, date, price, tax, department, sku, encoded};
    private Vector<Object[]> rowData = new Vector<Object[]>();
    private AbstractTableModel model = new AbstractTableModel() {

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
            if (rowData.isEmpty()) {
                return 0;
            }
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {//barcode
                Long k = (Long) line[0];
                return k.toString();
            } else if (columnIndex == 1) {//description
                return line[1];
            } else if (columnIndex == 2) {//date
                return line[2];
            } else if (columnIndex == 3) {//price
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                return nf.format((new Double((Integer) line[3])) / 100);
            } else if (columnIndex == 4) {//tax
                return line[4];
            } else if (columnIndex == 5) {//department
                return line[5];
            } else if (columnIndex == 6) {//department
                return line[6];
            } else if (columnIndex == 7) {//encoded
                int i = (Integer) line[7];
                if (i == 2) {
                    return bundle.getString("Weight");
                } else if (i == 1) {
                    return bundle.getString("Price");
                } else if (i == 3) {
                    return bundle.getString("PriceNoParity");
                } else {
                    if (i == 4) {
                        return bundle.getString("WeightNoParity");
                    } else {
                        return "";
                    }
                }
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            if (c == 3) {
                return line[3].getClass();
            }
            return getValueAt(0, c).getClass();
        }
    };
    private String barcodeString;

    public EncodedProducts(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(0).setPreferredWidth(250);
        tc.getColumn(1).setPreferredWidth(600);
        tc.getColumn(2).setPreferredWidth(200);
        tc.getColumn(3).setPreferredWidth(200);
        tc.getColumn(4).setPreferredWidth(250);
        tc.getColumn(5).setPreferredWidth(600);
        tc.getColumn(7).setPreferredWidth(200);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        JTableHeader header = jTable1.getTableHeader();
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Embeddedbarcodes", Main.mainHelpSet);
    }

    public void execute() {
        columnSelected = 0;
        drawGrid();
        Audio.play("Tada");
        //FormRestore.createPosition(this, root);
        setVisible(true);
        //FormRestore.destroyPosition(this, root);
    }

    private void drawGrid() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        PreparedStatement products;
        try {
            productsString = "SELECT Products.ID,"
                    + "Products.Description,UPPER(Products.Description) AS PD,"
                    + "Products.WhenCreated,Products.Price,Products.Sku,"
                    + "Departments.Description,Skus.Tax,Products.Encoded,Taxes.Description "
                    + "FROM Products,Skus,Departments,Taxes "
                    + "WHERE Products.Sku=Skus.ID "
                    + "AND Skus.Department=Departments.ID "
                    + "AND Products.Encoded>0 "
                    + "AND Skus.Tax=Taxes.ID "
                    + "ORDER BY ";
            products = Main.getConnection().prepareStatement(productsString + "Products.ID");
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
                encoded = rs.getInt("Encoded");
                Object[] line1 = {barcode, description, date, price, tax, department, sku, encoded};
                rowData.add(line1);
                model.fireTableDataChanged();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(EncodedProducts.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
        jDetailsBtn = new javax.swing.JButton();
        jPackSuppliers = new javax.swing.JButton();
        closeButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/EncodedProducts"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("EncodedProducts"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        jDetailsBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Info.png"))); // NOI18N
        jDetailsBtn.setContentAreaFilled(false);
        jDetailsBtn.setName("jDetailsBtn"); // NOI18N
        jDetailsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jDetailsBtnActionPerformed(evt);
            }
        });

        jPackSuppliers.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Info.png"))); // NOI18N
        jPackSuppliers.setText(bundle.getString("jPackSuppliers.text")); // NOI18N
        jPackSuppliers.setContentAreaFilled(false);
        jPackSuppliers.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPackSuppliers.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jPackSuppliers.setName("jPackSuppliers"); // NOI18N
        jPackSuppliers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPackSuppliersActionPerformed(evt);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jDetailsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPackSuppliers)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1102, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPackSuppliers, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jDetailsBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jDetailsBtn, jPackSuppliers});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jDetailsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jDetailsBtnActionPerformed
        int selection = jTable1.getSelectedRow();
        if (selection < 0) {//no selected row
            return;
        }
        selection = jTable1.convertRowIndexToModel(selection);
        barcodeString = (String) model.getValueAt(selection, 0);
//        Main.product.setLocationRelativeTo(this);
        boolean b;
        b = Main.product.execute(barcodeString);
        selection = -1;
        drawGrid();
}//GEN-LAST:event_jDetailsBtnActionPerformed

    private void jPackSuppliersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPackSuppliersActionPerformed
        int selection = jTable1.getSelectedRow();
        if (selection == -1) {
            //no selected row
            return;
        }
        selection = jTable1.convertRowIndexToModel(selection);
        barcodeString = (String) model.getValueAt(selection, 0);
//        Main.packSuppliers.setLocationRelativeTo(this);
        Main.packSuppliers.execute(barcodeString);
        selection = -1;
        drawGrid();
}//GEN-LAST:event_jPackSuppliersActionPerformed

    private void closeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                EncodedProducts dialog = new EncodedProducts(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton closeButton2;
    private javax.swing.JButton jDetailsBtn;
    private javax.swing.JButton jPackSuppliers;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
