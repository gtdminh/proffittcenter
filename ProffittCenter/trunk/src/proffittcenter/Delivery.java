/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Delivery.java
 *
 * Created on 25-Apr-2010, 18:00:59
 */
package proffittcenter;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.RowSorter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class Delivery extends EscapeDialog {

    ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Delivery");
    TableColumnModel cm;
    String deliverySQL = "SELECT DL.ID,DL.Pack AS Pack,DL.PackSupplier AS PackSupplier ,"
            + "Departments.Description AS Department,Operators.Description  AS Operator,"
            + "SUM(DL.Quantity) As Quantity,Products.Description AS Product, "
            + "DL.PackSize,Products.Encoded, "
            + "DL.PackPrice AS Price,DL.Price "
            + "AS RetailPrice, "
            + "Taxes.Rate AS Rate, "
            + "Packs.Code,DL.Marked,"
            + "count(*) AS Count "
            + "FROM DeliveryLines AS DL,Products,Skus,Taxes,Operators,Packs,Departments "
            + "WHERE Products.ID=DL.Product "
            + "AND Packs.ID=DL.Pack "
            + "AND Skus.ID=Products.Sku "
            + "AND Taxes.ID=Skus.Tax "
            + "AND Operators.ID=DL.Operator "
            + "AND Skus.Department=Departments.ID "
            + "AND DL.Delivery=?  "
            + "GROUP BY Product "
            + "ORDER BY Product,Packs.Code DESC";
    String deliveriesSQL = " SELECT Deliveries.ID, "
            + "Date(Deliveries.WhenCreated) AS Date, "
            + "Deliveries.Reference, "
            + "Suppliers.Description AS Supplier,Suppliers.ID, "
            + "Deliveries.Total,Deliveries.Tax,Deliveries.Completed "
            + "FROM Deliveries,Suppliers "
            + "WHERE Deliveries.Supplier=Suppliers.id "
            + "AND Deliveries.ID=? "
            + "ORDER BY Deliveries.WhenCreated DESC";
    String getBarcode = "SELECT Product FROM DeliveryLines,Products WHERE DeliveryLines.ID=? ";
    String getPriceAndMargin="SELECT Products.Price,Taxes.Rate AS Rate,DeliveryLines.PackSupplier,Deliverylines.Product FROM Products, DeliveryLines,Skus,Taxes " +
            "WHERE DeliveryLines.ID=? AND DeliveryLines.Product=Products.ID AND Skus.ID=Products.Sku " +
            "AND Taxes.ID=Skus.Tax  ";
    private final MyTableCellRenderer mtcr;
    private int total;
    private int taxTotal;
    private Integer delivery;
    private JDBCTableModel jtm;
    private int operator;
    private int invoiceTotal;
    private int invoiceTotalTax;
    private long barcode;
    private Integer id;
    ListSelectionEvent event;
    private Integer deliveryLine;
    private int retailPrice;
    private int margin;
    private int rate;
    private int packPrice;
    private int packSize;
    private int packSupplier;
    private int pack;
    private long product;
    private int oldSelection;
    private int charge;
    private int encoded;
    private int quantity;
    private int price;
    private int selection;
    private HashSet editable = new HashSet();
    private boolean marked;
    private PreparedStatement psc;
    private PreparedStatement psu;
    private String data;
    private ResultSet rs;
    private Integer j;
    private int supplier;
    private PreparedStatement ps;
    private int modelSelection;

    /** Creates new form Delivery
     * @param parent
     * @param modal
     */
    public Delivery(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        mtcr = new MyTableCellRenderer();
        jTable1.setDefaultRenderer(Money.class, mtcr);
        jTable1.setDefaultRenderer(PerCent.class, mtcr);
        getRootPane().setDefaultButton(okButton);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Delivery", Main.mainHelpSet);
    }

    public boolean onSelect() throws NumberFormatException {
        try {
            String smv = "UPDATE DeliveryLines SET Marked=? WHERE ID=? ";
            PreparedStatement psv = Main.getConnection().prepareStatement(smv);
            RowSorter sorter = jTable1.getRowSorter();
            selection = jTable1.getSelectedRow();
            oldSelection=selection;
            if(selection>=0){
                selection = jTable1.convertRowIndexToModel(selection);
                deliveryLine = (Integer) jTable1.getModel().getValueAt(selection, 0);
                PreparedStatement psl = Main.getConnection().prepareStatement(getPriceAndMargin);
                psl.setInt(1, deliveryLine);
                ResultSet rsl = psl.executeQuery();
                if (rsl.first()) {
                    retailPrice = rsl.getInt("Price");
                    product = rsl.getLong("Product");
                    rsl.close();
                    packPrice = ((Money) jTable1.getModel().getValueAt(selection, 9)).value;
                    packSize = (Integer) jTable1.getModel().getValueAt(selection, 7);
                    packSupplier = (Integer) jTable1.getModel().getValueAt(selection, 2);
                    pack= (Integer) jTable1.getModel().getValueAt(selection, 1);
                    rate = ((PerCent)jTable1.getModel().getValueAt(selection, 11)).getValue();
                    marked = (Boolean) jTable1.getModel().getValueAt(selection, 13);
                    psv.setBoolean(1, marked);
                    id= (Integer) jTable1.getModel().getValueAt(selection, 0);//delivery line
                    psv.setInt(2, id);
                    psv.executeUpdate();
                } else {
                    rsl.close();
                    return false;
                }
                Regime rr = Main.shop.regimeIs();
                if (rr == Regime.NONE || rr == Regime.REGISTERED || rr == Regime.UNREGISTERED) {
                    margin = (10000 - packPrice * (10000 + rate) / (packSize * retailPrice))/10;
                } else if (rr == Regime.WHOLESALE || rr == Regime.SALESTAX) {
                    margin = (10000 - packPrice * 10000 / (packSize * retailPrice))/10;
                }
                marginText.setText(StringOps.asPerCent(margin));
            } else {
                oldSelection=-1;
                marginText.setText("");
            }
            jTable1.setRowSorter(sorter);
            if(oldSelection>=0&&oldSelection<jTable1.getRowCount()){
                jTable1.setRowSelectionInterval(oldSelection, oldSelection);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Delivery.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel7 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        supplierText = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        totalText = new javax.swing.JTextField();
        closeButton = new javax.swing.JButton();
        marginText = new javax.swing.JTextField();
        totalTaxText = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jLabel8 = new javax.swing.JLabel();
        invoiceTotalTaxText = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        idText = new javax.swing.JTextField();
        invoiceTotalText = new javax.swing.JTextField();
        packCodeText = new javax.swing.JTextField();
        changeWholesalePrice = new javax.swing.JButton();
        productButton = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        changeRetailPrice = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        dateText = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        barCodeEntry = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        referenceTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        changeCaseSize = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/Delivery"); // NOI18N
        setTitle(bundle.getString("title")); // NOI18N
        setName("delivery"); // NOI18N

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText(bundle.getString("Delivery.jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(bundle.getString("Delivery.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText(bundle.getString("Delivery.jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        supplierText.setFocusable(false);
        supplierText.setName("supplierText"); // NOI18N

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText(bundle.getString("Delivery.jLabel10.text")); // NOI18N
        jLabel10.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText(bundle.getString("Delivery.jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        totalText.setEditable(false);
        totalText.setFocusable(false);
        totalText.setName("totalText"); // NOI18N

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton.setContentAreaFilled(false);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        marginText.setEditable(false);
        marginText.setName("marginText"); // NOI18N

        totalTaxText.setEditable(false);
        totalTaxText.setFocusable(false);
        totalTaxText.setName("totalTaxText"); // NOI18N

        jCheckBox1.setText(bundle.getString("Delivery.jCheckBox1.text")); // NOI18N
        jCheckBox1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jCheckBox1.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        jCheckBox1.setName("jCheckBox1"); // NOI18N
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText(bundle.getString("Delivery.jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        invoiceTotalTaxText.setName("invoiceTotalTaxText"); // NOI18N
        invoiceTotalTaxText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                invoiceTotalTaxTextFocusLost(evt);
            }
        });
        invoiceTotalTaxText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                invoiceTotalTaxTextKeyReleased(evt);
            }
        });

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/OK.png"))); // NOI18N
        okButton.setContentAreaFilled(false);
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        idText.setFocusable(false);
        idText.setName("idText"); // NOI18N

        invoiceTotalText.setName("invoiceTotalText"); // NOI18N
        invoiceTotalText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                invoiceTotalTextKeyReleased(evt);
            }
        });

        packCodeText.setName("packCodeText"); // NOI18N
        packCodeText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                packCodeTextFocusLost(evt);
            }
        });

        changeWholesalePrice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/ChangeWholesalePrice.png"))); // NOI18N
        changeWholesalePrice.setToolTipText(bundle.getString("Delivery.changeWholesalePrice.toolTipText")); // NOI18N
        changeWholesalePrice.setContentAreaFilled(false);
        changeWholesalePrice.setName("changeWholesalePrice"); // NOI18N
        changeWholesalePrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeWholesalePriceActionPerformed(evt);
            }
        });

        productButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Info.png"))); // NOI18N
        productButton.setName("productButton"); // NOI18N
        productButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                productButtonActionPerformed(evt);
            }
        });

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText(bundle.getString("Delivery.jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        changeRetailPrice.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/ChangePrice.png"))); // NOI18N
        changeRetailPrice.setToolTipText(bundle.getString("Delivery.changeRetailPrice.toolTipText")); // NOI18N
        changeRetailPrice.setContentAreaFilled(false);
        changeRetailPrice.setName("changeRetailPrice"); // NOI18N
        changeRetailPrice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeRetailPriceActionPerformed(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(bundle.getString("Delivery.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        dateText.setFocusable(false);
        dateText.setName("dateText"); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText(bundle.getString("Delivery.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        barCodeEntry.setName("barCodeEntry"); // NOI18N
        barCodeEntry.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                barCodeEntryFocusLost(evt);
            }
        });
        barCodeEntry.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                barCodeEntryKeyReleased(evt);
            }
        });

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText(bundle.getString("Delivery.jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jTable1KeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        referenceTextField.setName("referenceTextField"); // NOI18N

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText(bundle.getString("Delivery.jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        changeCaseSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/CaseSize.png"))); // NOI18N
        changeCaseSize.setToolTipText(bundle.getString("Delivery.changeCaseSize.toolTipText")); // NOI18N
        changeCaseSize.setContentAreaFilled(false);
        changeCaseSize.setName("changeCaseSize"); // NOI18N
        changeCaseSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeCaseSizeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dateText, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalText, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(referenceTextField)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idText, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(supplierText)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(totalTaxText, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 79, Short.MAX_VALUE))
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(invoiceTotalTaxText)
                            .addComponent(invoiceTotalText, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                            .addComponent(marginText))
                        .addGap(0, 222, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(productButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(10, 10, 10)
                                .addComponent(changeRetailPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changeWholesalePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(changeCaseSize, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(barCodeEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(packCodeText, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jCheckBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 880, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dateText, jLabel11, jLabel2, jLabel3, referenceTextField, supplierText});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel4, jLabel5});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dateText)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(totalText, javax.swing.GroupLayout.DEFAULT_SIZE, 41, Short.MAX_VALUE)
                    .addComponent(jLabel6)
                    .addComponent(invoiceTotalText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(supplierText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(totalTaxText)
                        .addComponent(jLabel7)
                        .addComponent(invoiceTotalTaxText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(referenceTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(idText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(marginText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(productButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeRetailPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeWholesalePrice, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeCaseSize, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(barCodeEntry, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(packCodeText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 697, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {changeWholesalePrice, totalText});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {dateText, jLabel11, jLabel2, jLabel3, referenceTextField, supplierText});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        barCodeEntry.requestFocus();
}//GEN-LAST:event_jCheckBox1ActionPerformed

    private void invoiceTotalTaxTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_invoiceTotalTaxTextFocusLost
        barCodeEntry.requestFocus();
}//GEN-LAST:event_invoiceTotalTaxTextFocusLost

    private void invoiceTotalTaxTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_invoiceTotalTaxTextKeyReleased
        Money.asMoney(invoiceTotalTaxText);
        upDown(evt);
}//GEN-LAST:event_invoiceTotalTaxTextKeyReleased

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        try {
            invoiceTotal=Integer.parseInt(StringOps.numericOnly(invoiceTotalText.getText()));
            invoiceTotalTax=Integer.parseInt(StringOps.numericOnly(invoiceTotalTaxText.getText()));
            //save the 'Marked' values to database
            String smv = "UPDATE DeliveryLines SET Marked=? WHERE ID=? ";
            PreparedStatement psmv = Main.getConnection().prepareStatement(smv);
            for (int i = 0; i < jTable1.getModel().getRowCount(); i++) {
                id = (Integer) jTable1.getModel().getValueAt(i, 0);
                marked = (Boolean) jTable1.getModel().getValueAt(i, 13);
                psmv.setBoolean(1, marked);
                psmv.setInt(2, id);
                psmv.executeUpdate();
            }
            //save the 'Completed' values back to database
            String complete = "UPDATE Deliveries SET Completed=?,Total=?,Tax=? WHERE ID=?";
            PreparedStatement psComplete = Main.getConnection().prepareStatement(complete);
            psComplete.setBoolean(1, jCheckBox1.isSelected());
            psComplete.setInt(2, invoiceTotal);
            psComplete.setInt(3, invoiceTotalTax);
            psComplete.setInt(4, delivery);
            psComplete.executeUpdate();
            psComplete.close();
            String ups = "UPDATE PackSuppliers SET Price = ? WHERE ID=? ";
            PreparedStatement pups = Main.getConnection().prepareStatement(ups);
            for (int i = 0; i < jTable1.getModel().getRowCount(); i++) {
                packSupplier = (Integer) jTable1.getModel().getValueAt(i, 2);
                pups.setInt(1, pack);
                pups.setInt(2, supplier);
                pups.executeUpdate();
            }
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(Delivery.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(false);
}//GEN-LAST:event_okButtonActionPerformed

    private void invoiceTotalTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_invoiceTotalTextKeyReleased
        Money.asMoney(invoiceTotalText);
        upDown(evt);
        if (!onSelect()) {
        }
}//GEN-LAST:event_invoiceTotalTextKeyReleased

    private void packCodeTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_packCodeTextFocusLost
        try {
            if(packCodeText.getText().isEmpty()){
                return;
            }
            String code = Main.deliveryEntry.addSupplierToCode(packCodeText.getText(), supplier);
            packCodeText.setText(code);
            String findCode = "SELECT * FROM DeliveryLines,Packs WHERE CODE=? AND Delivery=? AND DeliveryLines.Pack=Packs.ID";
            PreparedStatement psl = Main.getConnection().prepareStatement(findCode);
            psl.setString(1, code);
            psl.setInt(2, delivery);
            rs = psl.executeQuery();
            if(rs.first()){
                id=rs.getInt("ID");
                locate();
                rs.close();
                packCodeText.selectAll();
                packCodeText.requestFocus();
            }
            rs.close();
            Audio.play("Beep");
            packCodeText.setText("");
            barCodeEntry.setText("");
            packCodeText.requestFocus();
        } catch (SQLException ex) {
            Logger.getLogger(Delivery.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_packCodeTextFocusLost

    private void changeWholesalePriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeWholesalePriceActionPerformed
        try {
            RowSorter sorter = jTable1.getRowSorter();
            selection = jTable1.getSelectedRow();
            if (selection < 0) {
                //no selected row
                return;
            }
            oldSelection=selection;
            selection = jTable1.convertRowIndexToModel(selection);
            packSupplier = (Integer) jTable1.getModel().getValueAt(selection, 2);
            PackPriceChange pc = new PackPriceChange(null, true);
            //need to get barcode
            pc.execute(packSupplier);
            //need to update delivery line with new price
            String upp =
                    "UPDATE DeliveryLines SET " +
                    "PackPrice=(SELECT Price FROM PackSuppliers " +
                    "WHERE PackSuppliers.ID=DeliveryLines.PackSupplier) WHERE PackSupplier =? " +
                    "AND Delivery=?";
            PreparedStatement psl = Main.getConnection().prepareStatement(upp);
            psl.setInt(1, packSupplier);
            psl.setInt(2, delivery);
            psl.executeUpdate();
            refresh(delivery);
            jTable1.setRowSorter(sorter);
            jTable1.setRowSelectionInterval(oldSelection, oldSelection);
        } catch (SQLException ex) {
            Logger.getLogger(Delivery.class.getName()).log(Level.SEVERE, null, ex);
        }
        //        marginText.setText("");
        barCodeEntry.requestFocus();
}//GEN-LAST:event_changeWholesalePriceActionPerformed

    private void productButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_productButtonActionPerformed
        try {
            RowSorter sorter = jTable1.getRowSorter();
            selection = jTable1.getSelectedRow();

            if (selection < 0) {
                //no selected row
                return;
            }
            oldSelection = selection;
            modelSelection = jTable1.convertRowIndexToModel(selection);
            id = (Integer) jTable1.getModel().getValueAt(modelSelection, 0);
            ps = Main.getConnection().prepareStatement("Select Product FROM DeliveryLines WHERE ID = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if(rs.first()){
                product = rs.getLong("Product");
                Main.product.execute(""+product);
            }
            refresh(delivery);
            jTable1.setRowSorter(sorter);
            jTable1.setRowSelectionInterval(selection, selection);
        } catch (SQLException ex) {
            Logger.getLogger(Delivery.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_productButtonActionPerformed

    private void changeRetailPriceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeRetailPriceActionPerformed
        try {
            RowSorter sorter = jTable1.getRowSorter();
            selection = jTable1.getSelectedRow();
            if (selection < 0) {
                //no selected row
                return;
            }
            oldSelection=selection;
            selection = jTable1.convertRowIndexToModel(selection);
            id = (Integer) jTable1.getModel().getValueAt(selection, 0);
            PreparedStatement psl = Main.getConnection().prepareStatement(getBarcode);
            psl.setInt(1, id);
            ResultSet rsl = psl.executeQuery();
            if(rsl.first()){
                barcode = rsl.getLong("Product");
                PriceChange pc = new PriceChange(null, true);
                //need to get barcode
                pc.execute(barcode);
            }
            rsl.close();
            //need to update delivery line as well
            String upp =
                    "UPDATE DeliveryLines SET " +
                    "Price=(SELECT Price FROM Products " +
                    "WHERE Products.ID=DeliveryLines.Product) WHERE Product =? " +
                    "AND Delivery=?";
            psl = Main.getConnection().prepareStatement(upp);
            psl.setLong(1, product);
            psl.setInt(2, delivery);
            psl.executeUpdate();
            refresh(delivery);
            //            jTable1.getSelectionModel().setSelectionInterval(selection, selection);
            barCodeEntry.requestFocus();
            jTable1.setRowSorter(sorter);
            jTable1.setRowSelectionInterval(oldSelection, oldSelection);
        } catch (SQLException ex) {
            Logger.getLogger(Delivery.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_changeRetailPriceActionPerformed

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
        data=StringOps.numericOnly(data);
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

    private void barCodeEntryKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_barCodeEntryKeyReleased
        upDown(evt);
        data = barCodeEntry.getText();
        if (evt.getKeyCode() == KeyEvent.VK_PRINTSCREEN) {
            //            print();
            return;
        } else if(evt.getKeyChar()== KeyEvent.VK_ENTER){
            jTable1.requestFocus();
        }
        if (data.length() == 3) {
            if (Main.alphaLookup.isBarcode(data)) {
                return;
            }
            if (Main.alphaLookup.isFound(data)) {
                barCodeEntry.setText(Main.alphaLookup.returnDataIs());
            } else {
                barCodeEntry.setText("");
            }
        }
}//GEN-LAST:event_barCodeEntryKeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        barCodeEntry.requestFocus();
        if (!onSelect()) {
        }
}//GEN-LAST:event_jTable1MouseClicked

    private void jTable1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyPressed
        if (KeyEvent.VK_ENTER == evt.getKeyCode()) {
            try {
                //save the 'Marked' values to database
                String smv = "UPDATE DeliveryLines SET Marked=? " + "WHERE ID=? ";
                PreparedStatement psl = Main.getConnection().prepareStatement(smv);
                for (int i = 0; i < jTable1.getModel().getRowCount(); i++) {
                    id = (Integer) jTable1.getModel().getValueAt(i, 0);
                    boolean mark = (Boolean) jTable1.getModel().getValueAt(i, 13);
                    psl.setBoolean(1, mark);
                    psl.setInt(2, id);
                    psl.executeUpdate();
                }
                Audio.play("Beep");
            } catch (SQLException ex) {
                Audio.play("Ring");
                Logger.getLogger(Delivery.class.getName()).log(Level.SEVERE, null, ex);
            }
            setVisible(false);
        }
        if (!onSelect()) {
        }
}//GEN-LAST:event_jTable1KeyPressed

    private void changeCaseSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeCaseSizeActionPerformed
        try {
            RowSorter sorter = jTable1.getRowSorter();
            selection = jTable1.getSelectedRow();
            if (selection < 0) {
                //no selected row
                return;
            }
            oldSelection=selection;
            selection = jTable1.convertRowIndexToModel(selection);
            pack = (Integer) jTable1.getModel().getValueAt(selection, 1);
            ChangePackSize cps = new ChangePackSize(null, true);
            //need to get barcode
            cps.execute(pack);
            //need to update delivery line with new price
            String upp =
                    "UPDATE DeliveryLines SET " +
                    "PackSize=(SELECT Size FROM Packs " +
                    "WHERE Packs.ID=DeliveryLines.Pack) WHERE Pack =? " +
                    "AND Delivery=?";
            PreparedStatement psl = Main.getConnection().prepareStatement(upp);
            psl.setInt(1, pack);
            psl.setInt(2, delivery);
            psl.executeUpdate();
            refresh(delivery);
            jTable1.setRowSorter(sorter);
            jTable1.setRowSelectionInterval(oldSelection, oldSelection);
        } catch (SQLException ex) {
            Logger.getLogger(Delivery.class.getName()).log(Level.SEVERE, null, ex);
        }
        //        marginText.setText("");
        barCodeEntry.requestFocus();
    }//GEN-LAST:event_changeCaseSizeActionPerformed

    private void upDown(java.awt.event.KeyEvent evt){
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
        if (!onSelect()) {
        }
    }

    private void checkProduct() {
        //look up product in Packs, get ID
        jTable1.clearSelection();
        try{
            PreparedStatement psl = Main.getConnection().prepareStatement(
                    "SELECT ID FROM Deliverylines WHERE Product=? AND Delivery=?");
            psl.setLong(1, barcode);
            psl.setInt(2, delivery);
            rs=psl.executeQuery();
            id=0;
            if(rs.next()){
                id=rs.getInt("ID");
            }
            rs.close();
            //select displayed line where pack id =id
            locate();
//            invoiceTotalText.requestFocus();
        }catch(SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(Deliveries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void locate() {
        //select displayed line where pack id =id
        int k;
        for (int i = 0; i < jTable1.getModel().getRowCount() - 1; i++) {
            j = (Integer) jTable1.getModel().getValueAt(i, 0);
            if (id.equals(j)) {
                k = jTable1.convertRowIndexToView(i);
                jTable1.setRowSelectionInterval(k, k);
                jTable1.scrollRectToVisible(jTable1.getCellRect(k, 0, false));
                j = i;//might need this to continue search
                break;
            }
        }
    }

    private void refresh(int delivery) {
        RowSorter sorter = jTable1.getRowSorter();
        this.delivery = delivery;
        try {
            PreparedStatement psd = Main.getConnection().prepareStatement(deliveriesSQL);
            psd.setInt(1, delivery);
            rs = psd.executeQuery();
            if (rs.first()) {
                idText.setText("" + delivery);
                dateText.setText(rs.getString("Date"));
                supplierText.setText(rs.getString("Supplier"));
                invoiceTotalText.setText(new Money(rs.getInt("Total")).toString());
                invoiceTotalTaxText.setText(new Money(rs.getInt("Tax")).toString());
//                boolean b =
                jCheckBox1.setSelected(rs.getBoolean("Completed"));
                rs.close();
                psd.close();
                Audio.play("Vibes");
            } else {
                psd.close();
                rs.close();
                Audio.play("Ring");
                return;
            }
            PreparedStatement ps1 = Main.getConnection().prepareStatement(deliverySQL);
            ps1.setInt(1, delivery);
            rs = ps1.executeQuery();
            total = 0;
            taxTotal = 0;
            while (rs.next()) {
                encoded = rs.getInt("Products.Encoded");
                quantity = rs.getInt("Quantity");
                price = rs.getInt("Price");
                marked=rs.getBoolean("Marked");
                charge = Line.getCharge(rs);
                total += charge;
                rate = rs.getInt("Rate");
                if(Main.shop.regimeIs()==Regime.WHOLESALE||Main.shop.regimeIs()==Regime.REGISTERED){
                    taxTotal += Tax.calculateTax(charge,rate);
                }
            }
            rs.close();
            totalText.setText(new Money(total).toString());
            totalTaxText.setText(new Money(taxTotal).toString());
            rs.close();
            editable.add(13);
            jtm.getTableContents(ps1, bundle, jTable1, editable);
            jTable1.setRowSorter(sorter);
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(Deliveries.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Delivery dialog = new Delivery(new javax.swing.JFrame(), true);
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
    private javax.swing.JTextField barCodeEntry;
    private javax.swing.JButton changeCaseSize;
    private javax.swing.JButton changeRetailPrice;
    private javax.swing.JButton changeWholesalePrice;
    private javax.swing.JButton closeButton;
    private javax.swing.JTextField dateText;
    private javax.swing.JTextField idText;
    private javax.swing.JTextField invoiceTotalTaxText;
    private javax.swing.JTextField invoiceTotalText;
    private javax.swing.JCheckBox jCheckBox1;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField marginText;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField packCodeText;
    private javax.swing.JButton productButton;
    private javax.swing.JTextField referenceTextField;
    private javax.swing.JTextField supplierText;
    private javax.swing.JTextField totalTaxText;
    private javax.swing.JTextField totalText;
    // End of variables declaration//GEN-END:variables

    void execute(Integer delivery) {
        this.delivery = delivery;
        barCodeEntry.requestFocus();
        operator = Main.operator.getOperator();
        try {
            PreparedStatement psd = Main.getConnection().prepareStatement(deliveriesSQL);
            psd.setInt(1, delivery);
            rs = psd.executeQuery();
            if (rs.first()) {
                idText.setText("" + delivery);
                dateText.setText(rs.getString("Date"));
                supplier=rs.getInt("Suppliers.ID");
                supplierText.setText(rs.getString("Supplier"));
                invoiceTotal=rs.getInt("Total");
                invoiceTotalText.setText(new Money(invoiceTotal).toString());
                invoiceTotalTax=rs.getInt("Tax");
                invoiceTotalTaxText.setText(new Money(invoiceTotalTax).toString());
                jCheckBox1.setSelected(rs.getBoolean("Completed"));
                referenceTextField.setText(rs.getString("Reference"));
                rs.close();
                psd.close();
                Audio.play("TaDa");
            } else {
                psd.close();
                rs.close();
                Audio.play("Ring");
                return;
            }
            PreparedStatement psdel = Main.getConnection().prepareStatement(deliverySQL);
            psdel.setInt(1, delivery);
            rs = psdel.executeQuery();
            total = 0;
            taxTotal = 0;
            while (rs.next()) {
                encoded = rs.getInt("Products.Encoded");
                quantity = rs.getInt("Quantity");
                price = rs.getInt("Price");
                if (encoded == NewProduct.ENCODEBYWEIGHTPARITY || encoded == NewProduct.ENCODEBYWEIGHTNOPARITY) {
                    charge = (quantity* price )/1000;
                } else {
                    charge = (quantity * price);
                }
                total += charge;
                if(Main.shop.regimeIs()==Regime.WHOLESALE||Main.shop.regimeIs()==Regime.REGISTERED){
                    rate = rs.getInt("Rate");
                    taxTotal += Tax.calculateTax(charge, rate);
                }
            }
            rs.close();
            totalText.setText(new Money(total).toString());
            totalTaxText.setText(new Money(taxTotal).toString());
            rs.close();
            editable.add(13);
            jtm = new JDBCTableModel(psdel, bundle, jTable1, editable);
            psdel.close();
            getRootPane().setDefaultButton(okButton);
            oldSelection=-1;
            marginText.setText("");
            barCodeEntry.requestFocus();
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(Deliveries.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(true);
    }
}
