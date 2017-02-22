/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StockByValue.java
 *
 * Created on 06-Sep-2009, 13:44:50
 */
package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class StockByValue_1 extends EscapeDialog {

    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/StockByValue");
    private Vector tableHeader = new Vector();
    private String product;
    private String department;
    private Double retailPrice;
    private Double wholesalePrice;
    private Integer number;
    private Integer lineTotal;
    private Double stockTotal;
    private Double departmentTotal;
    private Double overalTotal;
    private String supplier;
    private Integer value;
    private int stockValue;
    private int departmentValue;
    private boolean newDepartment = false;
    private boolean nextDepartment = false;
    private int theType;
    private int theDepartment;
    private String aDepartment = "";
    private int skuType = SkuType.INCLUDED.value();
    private NumberFormat nf = NumberFormat.getInstance();
    private String stocks = "SELECT Products.Description,Products.ID,"
            + "Products.Encoded,Departments.ID AS SD,"
            + "FORMAT((10*PackSuppliers.Price/Packs.Size+5)/10,0),"
            + "PackSuppliers.Price AS PackPrice ,"
            + "Packs.Size,Skus.ID,"
            + "Departments.Margin AS Margin,"
            + "Taxes.Rate AS Rate,"
            + "Departments.Description AS Department,"
            + "Products.Price AS RetailPrice,Skus.Quantity AS StockQuantity,"
            + "Products.Description,PackSuppliers.WhenCreated "
            + "FROM Products "
            + "LEFT JOIN Packs ON Packs.Product = Products.ID "
            + "LEFT JOIN PackSuppliers ON Packs.ID=PackSuppliers.Pack "
            + "JOIN Skus ON Products.Sku=Skus.ID "
            + "JOIN Departments ON Departments.ID=Skus.Department "
            + "JOIN Taxes ON Taxes.ID=Skus.Tax "
            + "WHERE Skus.StockType=? AND Skus.ID>2 " + //exclude A and B
            "ORDER BY Departments.Description ,Skus.ID, PackSuppliers.WhenCreated DESC";
    private boolean b0 = tableHeader.add(bundle.getString("Department"));
    private boolean b1 = tableHeader.add(bundle.getString("Department") + " (" + Main.shop.poundSymbol + ")");
    private int align[] = {SwingConstants.LEFT, SwingConstants.RIGHT};
    private Object[] line = {department, departmentTotal};
    private Vector<Object[]> rowData = new Vector<Object[]>();
    private Vector<Object[]> rowDataOld = new Vector<Object[]>();
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
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {//department
                return line[0];
            } else if (columnIndex == 1) {//departmentTotal
                NumberFormat nf = NumberFormat.getInstance();
                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);
                return nf.format(((Double) line[1]) / 100);
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
        }
    };
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private Preferences root = Preferences.userNodeForPackage(getClass());
    private String s;
    private int lastDepartmentID;
    private int departmentID;
    private boolean isByDepartment = true;//get this working first
    private Double margin;
    private Double packSupplierPrice;
    private Integer packSize;
    private int stockQuantity;
    private int lastSku;
    private int sku;
    private int encoded;
    private String productDescription;

    /** Creates new form StockByValue */
    public StockByValue_1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(0).setPreferredWidth(400);
        tc.getColumn(1).setPreferredWidth(180);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
    }

    public void execute(SkuType skuType) {
            int op = Main.operator.getOperator();//needed to clear temporary operator
            Audio.play("Tada");
            jStockValue.setText("");
            rowData.clear();
            newDepartment = false;
            stockQuantity = 0;
            //set all values initially
            departmentTotal = 0.;
            overalTotal = 0.;
            try {
                PreparedStatement ps = Main.getConnection().prepareStatement(stocks);
                int jj = skuType.value();//ours or theirs
                ps.setInt(1, skuType.value());
                ResultSet rs = ps.executeQuery();
                //get first set of values
                if (!rs.next()) {//no point if empty
                    ps.close();
                    setVisible(true);
                    return;
                }
                lastSku = rs.getInt("Skus.ID");
                sku = rs.getInt("Skus.ID");
                lastDepartmentID = rs.getInt("SD");
                packSupplierPrice = rs.getDouble("PackPrice");
                packSize = rs.getInt("Packs.Size");
                stockQuantity = rs.getInt("StockQuantity");
                if (stockQuantity < 0) {//ignore negatiive stock levels
                    stockQuantity = 0;
                }
                product = rs.getString("Products.ID");
                encoded = rs.getInt("Encoded");
                retailPrice = rs.getDouble("RetailPrice");//* 100
                productDescription = rs.getString("Products.Description");
                margin = rs.getDouble("Margin");
                wholesalePrice = retailPrice * (1 - margin / 10000);//default * 100
                if (packSupplierPrice == null || packSupplierPrice == 0 || packSize == null || packSize == 0) {
                    Regime rr = Main.shop.regimeIs();
                    if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                        wholesalePrice = retailPrice * (1 - margin / 100);
                    } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                        double taxRate = rs.getInt("Rate");
                        wholesalePrice = retailPrice * (1 - taxRate / 10000) * (1 - margin / 10000);
                    }
                } else {
                    if (encoded != NewProduct.NOTENCODE) {
                        wholesalePrice = (packSupplierPrice ) / (packSize * 1000);
                    } else {
                        wholesalePrice = (packSupplierPrice ) / packSize;
                    }
                }
                department = rs.getString("Department");
                double charge = Math.floor(wholesalePrice * stockQuantity);
                overalTotal += charge;
                departmentTotal += charge;
                while (rs.next()) {//step throgh the database
                    encoded = rs.getInt("Encoded");
                    departmentID = rs.getInt("SD");
                    sku = rs.getInt("Skus.ID");
                    //now, if there is a change in department, display
                    if (departmentID != lastDepartmentID) {
                        Object[] line1 = {department, departmentTotal};
                        rowData.add(line1);
                        departmentTotal = 0.;
                        lastDepartmentID = departmentID;
                    }
                    department = rs.getString("Department");
                    packSupplierPrice = rs.getDouble("PackPrice");
                    packSize = rs.getInt("Packs.Size");
                    stockQuantity = rs.getInt("StockQuantity");
                    if (stockQuantity < 0) {
                        stockQuantity = 0;
                    }
                    if (packSupplierPrice == null || packSupplierPrice == 0 || packSize == null || packSize == 0) {
                        retailPrice = rs.getDouble("RetailPrice");
                        margin = rs.getDouble("Margin");
                        Regime rr = Main.shop.regimeIs();
                        if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                            wholesalePrice = retailPrice * (1 - margin / 10000);
                        } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                            int tax = rs.getInt("Tax");
                            double taxRate = rs.getInt("Rate");
                            wholesalePrice = retailPrice * (1 - taxRate / 10000) * (1 - margin / 10000);
                        }
                    } else {
                        if (encoded == NewProduct.ENCODEBYWEIGHTPARITY || encoded == NewProduct.ENCODEBYWEIGHTNOPARITY) {
                            wholesalePrice = (packSupplierPrice ) / (packSize * 1000);
                        } else {
                            wholesalePrice = (packSupplierPrice ) / packSize;
                        }
                    }
                    if (sku != lastSku) {
                        charge = Math.floor(wholesalePrice * stockQuantity);
                        overalTotal += charge;
                        departmentTotal += charge;
                        lastSku = sku;
                    }
                    supplier = "";
                    value = 0;
                    jStockValue.setText(new Money(overalTotal.intValue()).toString());
                }
                Object[] line1 = {department, departmentTotal};
                rowData.add(line1);
                ps.close();
                model.fireTableDataChanged();
            } catch (SQLException ex) {
                Main.logger.log(Level.SEVERE, "Stock by value ", "SQLException" + ex);
            }
            //FormRestore.createPosition(this, root);
            setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        closeButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jStockValue = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/StockByValue"); // NOI18N
        setTitle(bundle.getString("title")); // NOI18N
        setName("StockByValue"); // NOI18N

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
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

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        jLabel1.setText(bundle.getString("StockByValue.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jStockValue.setName("jStockValue"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jStockValue, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jStockValue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    private void closeButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_closeButtonKeyReleased
}//GEN-LAST:event_closeButtonKeyReleased

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                StockByValue_1 dialog = new StockByValue_1(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jStockValue;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
