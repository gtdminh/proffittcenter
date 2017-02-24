/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * OrderNew.java
 *
 * Created on 13-Jan-2011, 11:11:31
 */
package proffittcenter;

import java.awt.Cursor;
import java.awt.event.KeyEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author Dave
 */
public class Order extends EscapeDialog {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Order");
    private int pack;
    private String productDescription;
    private Long productID;
    private Integer sku;
    private Integer noPacks;
    private Double yield;
    private Integer caseSize;
    private Integer packPrice;
    private String department;
    private Integer tax;
    private String taxDescription;
    private Integer shelf;
    private Date sellBy;
    private Integer order;
    private Integer orderLine;
    private String orderLines = "SELECT OL.*,P.*,D.Description,Taxes.Description,"
            + "Taxes.Rate,S.Quantity "
            + "FROM OrderLines AS OL,Products AS P,Departments AS D,"
            + "Skus AS S, Taxes WHERE OrderNO=? AND OL.Product=P.ID "
            + "AND OL.Department=D.ID AND P.Sku=S.ID "
            + "AND S.Tax=Taxes.ID "
            + "ORDER BY ";
    private int total;
    private Integer oldNoPacks;
    private Integer oldShelf;
    private Object[] line = {pack, productDescription, noPacks, yield, caseSize, packPrice,
        department, tax, shelf, sellBy, order, orderLine, productID, sku, oldNoPacks, oldShelf
    };
    private final MyTableCellRenderer mtcr;
    private int sale;
    private boolean stockChecked;
    private int supplier;
    private PreparedStatement orderStatement;
    private ResultSet orderResult;
    private JDBCTableModel jtm;
    private HashSet editable = new HashSet();
    private String orderSQL = "SELECT OrderLines.ID,"
            + "Products.Description AS Product,"
            + "Departments.Description AS Department,"
            + "Roci,PackPrice AS Wholesale,"
            + "OrderLines.Quantity, "
            + "PackSize AS Pack_Size,"
            + "Taxes.Description AS Tax,"
            + "OrderLines.Code,"
            + "SellBy, "
            + "Skus.Quantity AS 'Stock_quantity'"
            + "FROM OrderLines,Departments,Taxes,Products,Skus "
            + "WHERE Skus.Department=Departments.ID "
            + "AND OrderLines.Sku=Skus.ID "
            + "AND Skus.Tax=Taxes.ID "
            + "AND OrderLines.Product=Products.ID "
            + "AND OrderNo=? ORDER BY Department,Products.Description";
    private PreparedStatement ps;
    private int row;
    private Integer id;
    private int selection;
    private int oldSelection;
    private ResultSet rs;
    private Integer stockQuantity;
    private int oldStockQuantity;
    private String product;
    private int departmentID;
    private String pspSQL = "SELECT Products.Sku,Skus.Department,Skus.Tax,Products.Price FROM Products,Skus WHERE Products.ID=? AND Products.Sku=Skus.ID";
    private String idlSQL = "INSERT INTO OrderLines "
            + "(OrderNo,Sku,Product,Department,Roci,PackSize,PackPrice,Quantity,Tax,Code,WeeklySales,SellBy)"
            + "VALUES(?,?,?,?,0,?,?,1,?,?,0,DATE(NOW()))";
    private int packSize;
    private String code;
    private long barcode;
    private String psCode = "SELECT Size,Code,Pack,PackSuppliers.ID,PackSuppliers.Price FROM Packs,PackSuppliers "
            + "WHERE (PackSuppliers.Supplier =? AND Packs.Product=?) AND Packs.ID = PackSuppliers.Pack "
            + "ORDER BY PackSuppliers.WhenCreated DESC";
    private String pCode = "SELECT Size,Code,Pack,PackSuppliers.ID,PackSuppliers.Price FROM Packs,PackSuppliers "
            + "WHERE Packs.Product=? AND Packs.ID = PackSuppliers.Pack "
            + "ORDER BY PackSuppliers.WhenCreated DESC";
    private int operator;
    private int packSupplier;
    private int price;
    private Integer quantity;
    private int taxTotal;
    private int rate;
    private Connection connection;
    private String fileLocation;
    private JasperPrint jasperPrint;
    private TableRowSorter<JDBCTableModel> sorter;
    private int rowCount;
    private int tableRow;
    private int modelRow;

    /** Creates new form OrderNew
     * @param parent
     * @param modal
     */
    public Order(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        mtcr = new MyTableCellRenderer();
        jTable1.setDefaultRenderer(Money.class, mtcr);
        jTable1.setDefaultRenderer(PerCent.class, mtcr);
        getRootPane().setDefaultButton(okButton);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Order", Main.mainHelpSet);
        jTable1.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    private boolean addProduct() {
        try {
            ok();
            // Add a product to the lsit
            barcode = Main.selectProduct.execute();
            if (barcode == 0L) {
                Audio.play("Ring");
                return true;
            }
            PreparedStatement psp = Main.getConnection().prepareStatement(pspSQL);
            psp.setLong(1, barcode);
            rs = psp.executeQuery();
            if (rs.first()) {
                sku = rs.getInt("Sku");
                departmentID = rs.getInt("Department");
                tax = rs.getInt("Tax");
                price = rs.getInt("Products.Price");
                rs.close();
            } else {
                rs.close();
                Audio.play("Ring");
                return true;
            }
            //now get packSize and Code if available
            PreparedStatement pspscode = Main.getConnection().prepareStatement(psCode);
            pspscode.setInt(1, supplier);
            pspscode.setLong(2, barcode);
            rs = pspscode.executeQuery();
            if (rs.first()) {
                packSize = rs.getInt("Size");
                code = rs.getString("Code");
                pack = rs.getInt("Pack");
                packSupplier = rs.getInt("PackSuppliers.ID");
                packPrice = rs.getInt("PackSuppliers.Price");
                rs.close();
            } else {
                //try other suppliers
                PreparedStatement pspcode = Main.getConnection().prepareStatement(pCode);
                pspcode.setLong(1, barcode);
                rs = pspcode.executeQuery();
                if (rs.first()) {
                    packSize = rs.getInt("Size");
                    code = rs.getString("Code");
                    pack = rs.getInt("Pack");
                    packSupplier = rs.getInt("PackSuppliers.ID");
                    packPrice = rs.getInt("PackSuppliers.Price");
                    rs.close();
                } else {
                    rs.close();
                    Audio.play("Ring");
                    return true;
                }
            }
            PreparedStatement idl = Main.getConnection().prepareStatement(idlSQL);
            //OrderNo,Sku,Product,Department,PackSize,Tax,Code
            idl.setInt(1, order);
            idl.setInt(2, sku);
            idl.setLong(3, barcode);
            idl.setInt(4, departmentID);
            idl.setInt(5, packSize);
            idl.setInt(6, packPrice);
            idl.setInt(7, tax);
            idl.setString(8, code);
            idl.executeUpdate();
            ps.setInt(1, order);
            editable.add(9);
            editable.add(5);
            jtm.getTableContents(ps, bundle, jTable1, editable);
            fillTotals();
        } catch (SQLException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private boolean delete() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        ok();
        TableRowSorter tableRowSorter=(TableRowSorter) jTable1.getRowSorter();
        try {
            String deleteOrderLine = "DELETE FROM OrderLines WHERE ID=?";
            PreparedStatement psd = Main.getConnection().prepareStatement(deleteOrderLine);
            int[] selectedRows=jTable1.getSelectedRows();
            if (selectedRows.length==0) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return true;
            }
            for(int i =0;i<selectedRows.length;i++){
                tableRow=selectedRows[i];
//                modelRow=jTable1.convertRowIndexToModel(tableRow);
//                id=(Integer) jTable1.getModel().getValueAt(modelRow, 0);
                 id=(Integer) jTable1.getValueAt(tableRow, 0);
                psd.setInt(1, id);
                psd.executeUpdate();
//                tableRowSorter.rowsDeleted(jTable1.convertRowIndexToModel(selectedRows[i]),
//                    jTable1Del.convertRowIndexToModel( selectedRows[i]));
//                jtm.fireTableRowsDeleted(tableRow, tableRow);
            }
            int a1=selectedRows[0];
            int a2 = selectedRows[selectedRows.length-1];
            int j1=jtm.getRowCount();
            int j=jTable1.getRowCount();
            refresh();
            j1=jtm.getRowCount();
            j=jTable1.getRowCount();
            refresh();
            fillTotals();
        }catch (SQLException ex) {
           Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
       }setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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

        jPanel1 = new javax.swing.JPanel();
        invoicePrintButton = new javax.swing.JButton();
        receiptPrintButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        totalText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        orderNumberText = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        dateText = new javax.swing.JTextField();
        closeButton2 = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        keyText = new javax.swing.JTextField();
        addProductButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        taxTotalText = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/Order"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("order"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        invoicePrintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Receipt.png"))); // NOI18N
        invoicePrintButton.setToolTipText(bundle.getString("invoicePrint")); // NOI18N
        invoicePrintButton.setName("invoicePrintButton"); // NOI18N
        invoicePrintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invoicePrintButtonActionPerformed(evt);
            }
        });

        receiptPrintButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/print_edit.gif"))); // NOI18N
        receiptPrintButton.setToolTipText(bundle.getString("receiptPrint")); // NOI18N
        receiptPrintButton.setName("receiptPrintButton"); // NOI18N
        receiptPrintButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                receiptPrintButtonActionPerformed(evt);
            }
        });

        deleteButton.setText(bundle.getString("deleteButton")); // NOI18N
        deleteButton.setToolTipText(bundle.getString("DeleteButtonTip")); // NOI18N
        deleteButton.setContentAreaFilled(false);
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(bundle.getString("Order.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        totalText.setEditable(false);
        totalText.setFocusable(false);
        totalText.setName("totalText"); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(bundle.getString("Order.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        orderNumberText.setEditable(false);
        orderNumberText.setFocusable(false);
        orderNumberText.setName("orderNumberText"); // NOI18N

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(bundle.getString("Order.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        dateText.setEditable(false);
        dateText.setToolTipText(bundle.getString("Order.dateText.toolTipText")); // NOI18N
        dateText.setFocusable(false);
        dateText.setName("dateText"); // NOI18N

        closeButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton2.setContentAreaFilled(false);
        closeButton2.setName("closeButton2"); // NOI18N
        closeButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButton2ActionPerformed(evt);
            }
        });

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/OK.png"))); // NOI18N
        okButton.setToolTipText(bundle.getString("OK")); // NOI18N
        okButton.setContentAreaFilled(false);
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        keyText.setName("keyText"); // NOI18N
        keyText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                keyTextKeyReleased(evt);
            }
        });

        addProductButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/add_obj.gif"))); // NOI18N
        addProductButton.setToolTipText(bundle.getString("addTip")); // NOI18N
        addProductButton.setName("addProductButton"); // NOI18N
        addProductButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addProductButtonActionPerformed(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(bundle.getString("Order.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        taxTotalText.setEditable(false);
        taxTotalText.setFocusable(false);
        taxTotalText.setName("taxTotalText"); // NOI18N

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Info.png"))); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(invoicePrintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(receiptPrintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addProductButton, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, 0, 1, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(totalText, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(taxTotalText, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 108, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(orderNumberText, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(keyText, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(dateText, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel4});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {taxTotalText, totalText});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addProductButton, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(invoicePrintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(closeButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(okButton, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(totalText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(receiptPrintButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(deleteButton)
                        .addComponent(keyText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(orderNumberText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel4)
                        .addComponent(taxTotalText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {deleteButton, jButton1, receiptPrintButton});

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7"
            }
        ));
        jTable1.setCellSelectionEnabled(true);
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                jTable1MouseDragged(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 887, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void invoicePrintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invoicePrintButtonActionPerformed
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Map<String, Object> parameters = new HashMap<String, Object>();
            connection = Main.getConnection();
            parameters.put("REPORT_CONNECTION", connection);
            parameters.put("order", order);
            File defaultDirectory = new JFileChooser().getFileSystemView().getDefaultDirectory();
            File fc = new File(Main.salesScreen.getDefaultDirectory() + "ProffittCenterReports/Order.jasper");
            try {
                fileLocation = fc.getAbsolutePath();
                jasperPrint = JasperFillManager.fillReport(fileLocation, parameters, connection);
                JasperViewer.viewReport(jasperPrint, false);
                setVisible(false);
            } catch (JRException ex) {
                Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_invoicePrintButtonActionPerformed

    private void closeButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButton2ActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButton2ActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        ok();
        this.setVisible(false);
}

    private void ok() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        String updateStockQuantity = "UPDATE Skus SET Quantity=? WHERE ID=?";
        String getStockQuantity = "SELECT Skus.Quantity,Skus.ID FROM OrderLines,Skus WHERE Skus.ID=OrderLines.Sku AND OrderLines.ID=?";
        String getQuantity = "SELECT Quantity FROM OrderLines WHERE ID=?";
        String updateOrderLine = "UPDATE OrderLines SET Quantity=? WHERE ID=?";
        try {
            // set all Skus to today.
            String stockUpdate = "UPDATE Skus SET WhenCreated=DATE(NOW()) WHERE ID IN(SELECT Sku FROM Orderlines WHERE OrderNo=?)";
            PreparedStatement su = Main.getConnection().prepareStatement(stockUpdate);
            su.setInt(1, order);
            int executeUpdate = su.executeUpdate();
            PreparedStatement usq = Main.getConnection().prepareStatement(updateStockQuantity);
            PreparedStatement gsq = Main.getConnection().prepareStatement(getStockQuantity);
            PreparedStatement gq = Main.getConnection().prepareStatement(getQuantity);
            PreparedStatement uol = Main.getConnection().prepareStatement(updateOrderLine);
            //now set all stockQuantities to table value
            for (int i = 0; i < jTable1.getModel().getRowCount(); i++) {
                id = (Integer) jTable1.getModel().getValueAt(i, 0);
                stockQuantity = (Integer) jTable1.getValueAt(i, 10);
                quantity = (Integer) jTable1.getModel().getValueAt(i, 5);
                gsq.setInt(1, id);
                rs = gsq.executeQuery();
                if (rs.first()) {
                    oldStockQuantity = rs.getInt("Quantity");
                    sku = rs.getInt("ID");
                    rs.close();
                    if (oldStockQuantity != stockQuantity) {
                        usq.setInt(2, sku);
                        usq.setInt(1, stockQuantity);
                        usq.executeUpdate();
                    }
                }
                rs.close();
                uol.setInt(1, quantity);
                uol.setInt(2, id);
                uol.executeUpdate();
//                }
                rs.close();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
}//GEN-LAST:event_okButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if (delete()) {
            return;
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

     private void refresh() {
         try {
            ps.setInt(1, order);
//            jtm.getTableContents(ps, bundle, jTable1, editable);
            jtm = new JDBCTableModel(ps, bundle, jTable1, editable);
            int k=jtm.getRowCount();
            if(jtm.getRowCount()==0){
                setVisible(false);
            }
//            jtm.fireTableRowsUpdated(0, jTable1.getRowCount());
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(Deliveries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void keyTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_keyTextKeyReleased
        upDown(evt);
        int i = evt.getKeyCode();
        if (evt.getKeyCode() == KeyEvent.VK_ADD||evt.getKeyCode() == KeyEvent.VK_EQUALS) {
            if (addProduct()) {
            }
        }
        if (evt.getKeyCode() == KeyEvent.VK_DELETE) {
            delete();
        }
        keyText.setText("");
        keyText.requestFocus();
    }//GEN-LAST:event_keyTextKeyReleased

    private void addProductButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addProductButtonActionPerformed
        if (addProduct()) {
            return;
        }
}//GEN-LAST:event_addProductButtonActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(jTable1.getSelectedColumn()<9){
            jTable1.setColumnSelectionInterval(5, 5);
        }
        fillTotals();
        keyText.requestFocus();
    }//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        if(KeyEvent.VK_ENTER==evt.getKeyCode()){
            fillTotals();
            keyText.requestFocus();
        }
    }//GEN-LAST:event_jTable1KeyReleased

    private void receiptPrintButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_receiptPrintButtonActionPerformed
        try {
            Main.receiptPrinter.startReceipt();
            PreparedStatement pso = Main.getConnection().prepareStatement(orderSQL);
            pso.setInt(1, order);
            rs=pso.executeQuery();
            int d = Main.hardware.getCharsOnReceipt()-12;
            Main.receiptPrinter.printLine("Qty Pack Product     Stock");
            while(rs.next()){
                String quantityString = rs.getString("Quantity");
                quantityString = StringOps.fixLengthUntrimmed(quantityString, 4);
                String packSizeString = rs.getString("Pack_Size");
                packSizeString = StringOps.fixLengthUntrimmed(packSizeString, 4);
                String productString = rs.getString("Product");
                productString = StringOps.fixLengthUntrimmed(productString, d);
                String stockQuantityString = rs.getString("Stock_quantity");
                stockQuantityString = StringOps.fixLengthUntrimmed(stockQuantityString, 4);
                String S = quantityString+packSizeString+productString+stockQuantityString;
                Main.receiptPrinter.printLine(quantityString+packSizeString+productString+stockQuantityString);
            }
            rs.close();
            Main.receiptPrinter.endReceipt();
        } catch (SQLException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_receiptPrintButtonActionPerformed

    private void jTable1MouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseDragged
        if (jTable1.getSelectedColumn() < 9) {
            jTable1.setColumnSelectionInterval(5, 5);
        }
        if (jTable1.getSelectedColumn() == 10) {
            jTable1.setColumnSelectionInterval(10, 10);
            row = jTable1.getSelectedRow();
            jTable1.setRowSelectionInterval(row, row);
        }
        keyText.requestFocus();
    }//GEN-LAST:event_jTable1MouseDragged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //display information about other suppliers for selected line
        tableRow = jTable1.getSelectedRow();
        id=(Integer) jTable1.getValueAt(tableRow, 0);
        String otherPacks = "SELECT PackSuppliers.Price AS Wholesale,"
                + "Packs.Size AS PackSize,"
                + "PackSuppliers.Price/Packs.Size AS PriceEach,"
                + "DATE(PackSuppliers.WhenCreated) AS DATE "
                + "FROM Packs,PackSuppliers,OrderLines "
                + "WHERE Packs.ID=PackSuppliers.Pack "
                + "AND Packs.Product=Products.ID "
                + "ORDER BY PackSuppliers.WhenCreated DESC";
    }//GEN-LAST:event_jButton1ActionPerformed

    private void upDown(java.awt.event.KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            selection = jTable1.getSelectedRow() - 1;
            if (selection >= 0) {
                jTable1.setRowSelectionInterval(selection, selection);
                jTable1.scrollRectToVisible(jTable1.getCellRect(selection, 0, false));
                Audio.play("Beep");
            } else {
                selection = 0;
                jTable1.setRowSelectionInterval(selection, selection);
                jTable1.scrollRectToVisible(jTable1.getCellRect(selection, 0, false));
                Audio.play("Beep");
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            selection = jTable1.getSelectedRow() + 1;
            int i = jTable1.getRowCount() - 1;
            if (selection < jTable1.getRowCount()) {
                jTable1.setRowSelectionInterval(selection, selection);
                jTable1.scrollRectToVisible(jTable1.getCellRect(selection, 0, false));
                Audio.play("Beep");
            } else {
                selection = jTable1.getRowCount()-1;
                jTable1.setRowSelectionInterval(selection, selection);
                jTable1.scrollRectToVisible(jTable1.getCellRect(selection, 0, false));
                Audio.play("Beep");
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_UP) {
            selection = jTable1.getSelectedRow() - 35;
            if (selection >= 0) {
                jTable1.setRowSelectionInterval(selection, selection);
                jTable1.scrollRectToVisible(jTable1.getCellRect(selection, 0, false));
                Audio.play("Beep");
            } else {
                selection = 0;
                jTable1.setRowSelectionInterval(selection, selection);
                jTable1.scrollRectToVisible(jTable1.getCellRect(selection, 0, false));
                Audio.play("Beep");
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
            selection = jTable1.getSelectedRow() + 35;
            int i = jTable1.getRowCount() - 1;
            if (selection < jTable1.getRowCount()) {
                jTable1.setRowSelectionInterval(selection, selection);
                jTable1.scrollRectToVisible(jTable1.getCellRect(selection, 0, false));
                Audio.play("Beep");
            } else {
                selection = jTable1.getRowCount()-1;
                jTable1.setRowSelectionInterval(selection, selection);
                jTable1.scrollRectToVisible(jTable1.getCellRect(selection, 0, false));
                Audio.play("Beep");
            }
        }
    }

    private void onSelect() {
//        RowSorter sorter = jTable1.getRowSorter();
//        try {
//            selection = jTable1.getSelectedRow();
//            oldSelection = selection;
//            if (selection >= 0) {
//                orderLine = (Integer) jTable1.getModel().getValueAt(selection, 0);
//                ps.setInt(1, orderLine);
//                rs = ps.executeQuery();
//                if (rs.first()) {
//                    //
//                }
//                rs.close();
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        jTable1.setRowSorter(sorter);
    }

    void execute(int supplier) {
        try {
            sale = 0;
            stockChecked = false;
            this.supplier = supplier;
            Audio.play("Tada");
            orderStatement = Main.getConnection().prepareStatement("SELECT * FROM Orders WHERE Supplier=? " + "ORDER BY WhenCreated DESC " + "LIMIT 1");
            orderStatement.setInt(1, supplier);
            orderResult = orderStatement.executeQuery();
            if (orderResult.next()) {
                order = orderResult.getInt("ID");
                orderNumberText.setText(order.toString());
                dateText.setText(orderResult.getString("WhenCreated"));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            editable.add(10);
            editable.add(5);
            ps = Main.getConnection().prepareStatement(orderSQL);
            int a1=jTable1.getModel().getRowCount();
            ps.setInt(1, order);
            jtm = new JDBCTableModel(ps, bundle, jTable1, editable);
            int rowCount = jTable1.getModel().getRowCount();
            sorter = new TableRowSorter<JDBCTableModel>(jtm);
//            ps.close();
            keyText.requestFocus();
            fillTotals();
            setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Order dialog = new Order(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton addProductButton;
    private javax.swing.JButton closeButton2;
    private javax.swing.JTextField dateText;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton invoicePrintButton;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField keyText;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField orderNumberText;
    private javax.swing.JButton receiptPrintButton;
    private javax.swing.JTextField taxTotalText;
    private javax.swing.JTextField totalText;
    // End of variables declaration//GEN-END:variables

    private void fillTotals() {
        //fill in details of total and tax
        try {
            total = 0;
            taxTotal = 0;
            String gr = "SELECT Taxes.Rate FROM Skus,OrderLines,Taxes WHERE OrderLines.Sku=Skus.ID AND Skus.Tax=Taxes.ID AND OrderLines.ID=?";
            PreparedStatement psgr = Main.getConnection().prepareStatement(gr);
            for (int i = 0; i < jTable1.getModel().getRowCount(); i++) {
                quantity = (Integer) jTable1.getModel().getValueAt(i, 5);
//                packPrice =Integer.parseInt( StringOps.numericOnly( (String)jTable1.getModel().getValueAt(i, 4)));
                packPrice = ((Money)jTable1.getModel().getValueAt(i, 4)).value;
                id  = (Integer) jTable1.getModel().getValueAt(i, 0);
                total += quantity * packPrice;
                psgr.setInt(1, id);
                rs = psgr.executeQuery();
                if(rs.first()){
                    rate = rs.getInt("Rate");
                    taxTotal += Tax.calculateTax(quantity * packPrice, rate);
                }
                rs.close();
            }
            totalText.setText(new Money(total).toString());
            taxTotalText.setText(new Money(taxTotal).toString());
        } catch (SQLException ex) {
            Logger.getLogger(Order.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
