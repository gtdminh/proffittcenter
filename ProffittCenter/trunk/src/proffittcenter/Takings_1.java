/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TakingsEscape.java
 *
 * Created on 24-Nov-2009, 17:22:47
 */
package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.xml.crypto.Data;
import java.awt.Cursor;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class Takings_1 extends EscapeDialog {

    public Integer excluded = 0, takings = 0, charged = 0;//needed by cashup
    ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Takings");
    Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    boolean b = tableHeader.add(bundle.getString("Date"));
    @SuppressWarnings("unchecked")
    boolean b1 = tableHeader.add(bundle.getString("Agency"));
    @SuppressWarnings("unchecked")
    boolean b2 = tableHeader.add(bundle.getString("Sales"));
    @SuppressWarnings("unchecked")
    boolean b3 = tableHeader.add(bundle.getString("Services"));
    @SuppressWarnings("unchecked")
    boolean b4 = tableHeader.add(bundle.getString("Net"));
    @SuppressWarnings("unchecked")
    boolean b5 = tableHeader.add(bundle.getString("Tax"));
    @SuppressWarnings("unchecked")
    boolean b6 = tableHeader.add(bundle.getString("Charged"));
    @SuppressWarnings("unchecked")
    boolean b7 = tableHeader.add(bundle.getString("Wastes"));
    @SuppressWarnings("unchecked")
    boolean b8 = tableHeader.add(bundle.getString("Returns"));
    @SuppressWarnings("unchecked")
    boolean b9 = tableHeader.add(bundle.getString("Losses"));
    @SuppressWarnings("unchecked")
    boolean b10 = tableHeader.add(bundle.getString("Own_use"));
    @SuppressWarnings("unchecked")
    boolean b11 = tableHeader.add(bundle.getString("Profit"));
    @SuppressWarnings("unchecked")
    boolean b12 = tableHeader.add(bundle.getString("Own_agency"));
//    @SuppressWarnings("unchecked")
//    boolean b13 = tableHeader.add(bundle.getString("Profit"));
//    @SuppressWarnings("unchecked")
//    boolean b14 = tableHeader.add(bundle.getString("Own_agency"));
    final int PRODUCT = 0;
    final int PRICE = 1;
    final int STOCK = 2;
    final int PSPRICE = 3;
    final int VAT = 4;
    final int DEPARTMENT = 5;
    final int DEFAULTMARGIN = 6;
    final int PACKSIZE = 7;
    String date = "";
    Integer sale;
    int saleLine;
    int total;
    int cash;
    int cheque;
    int debit;
    int coupon;
    int tillId;
    Integer rental = 0;
    Integer net = 0;
    Integer tax = 0;
    String department = "";
    Long product;
    int quantity;
    Integer wastes = 0;
    Integer returns = 0;
    Integer losses = 0;
    Integer ownUse = 0;
    Integer profit = 0;
    Integer ownExcluded = 0;
    int selectedDepartment;
    Date startDate;
    Date endDate;
    Date currentDate;
    Date saleDate;
    short waste = 0;
    boolean isOwnUse;
    boolean isWaste;
    int i = 1;
    Integer saleTotal = 0;
    Integer ownUseExcludedTotal = 0;
    Integer departmentTotal = 0;
    Integer rentalTotal = 0;
    Integer netTotal = 0;
    Integer departmentWasteTotal = 0;
    Integer departmentReturnsTotal = 0;
    Integer departmentLossesTotal = 0;
    Integer productTotal = 0;
    Integer productWasteTotal = 0;
    Integer productReturnsTotal = 0;
    Integer productLossesTotal = 0;
    Integer profittotal = 0;
    Integer ownUseTotal = 0;
    Integer chargedTotal = 0;
    Integer chargedTotalIncTax = 0;
    Integer taxTotal = 0;
    Integer wastesTotal = 0;
    Integer returnsTotal = 0;
    Integer lossesTotal = 0;
    Integer profitTotal = 0;
    Integer ownExcludedTotal = 0;
    int taxId;
    Integer charge = 0;
    Integer rate = 0;
    int aDepartment = 0;
    boolean isCharged = false;
    private Long selectedProduct;
    public int couponTotal;
    private final MyHeaderRenderer mhr;
    private final MyTableCellRenderer mtcr;
    private int servicesTotal;
    private int encoded;
    private int loss;
    public int excludedTaxID = 6;
    private int excludedTotal;
    private int price;
    private int wholesale;
    private int packSize;
    private int taxRate;
    private int encode;
    
    String sales =
            "SELECT Sales.*,SaleLines.*,Skus.Department,Skus.Tax,"
            + "Products.Price,Products.Encoded,Taxes.Rate, "
            + "Products.Description,Products.Price "
            + "FROM Sales,SaleLines,Products,Skus,Taxes "
            + "WHERE (Sales.TillId=? OR 0=?)  " +//changed to >= from =
            "AND Sales.WhenCreated>=? "
            + "AND Sales.WhenCreated<? "
            + "AND Sales.ID=SaleLines.Sale "
            + "AND SaleLines.Product=Products.ID  "
            + "AND Products.Sku=Skus.ID "
            + "AND Taxes.ID=Skus.Tax "
            + "ORDER BY Sales.ID";
    Integer t1 = 0, t2 = 0;
    Object[] line = {date, excluded, sale, rental, net, tax, charged,
        wastes, returns, losses, ownUse, profit, ownExcluded
//        , t1, t2
    };
    Data data;
    int align[] = {SwingConstants.LEFT, SwingConstants.RIGHT,
        SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT,
        SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT,
        SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT,
        SwingConstants.RIGHT, SwingConstants.RIGHT
//        , SwingConstants.RIGHT,
//        SwingConstants.RIGHT
    };
    @SuppressWarnings("unchecked")
    //Hashtable<Long,Object[]> hData=new Hashtable();
    Vector<Object[]> rowData = new Vector<Object[]>();
    AbstractTableModel model = new AbstractTableModel() {

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
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumFractionDigits(2);
            if (columnIndex == 0) {//date
                return line[0];
            } else if (columnIndex == 1) {//Agency
                return nf.format((new Double((Integer) line[1])) / 100);
            } else if (columnIndex == 2) {//Sales
                return nf.format((new Double((Integer) line[2])) / 100);
            } else if (columnIndex == 3) {//Rental
                return nf.format((new Double((Integer) line[3])) / 100);
            } else if (columnIndex == 4) {//Net
                return nf.format((new Double((Integer) line[4])) / 100);
            } else if (columnIndex == 5) {//tax
                return nf.format((new Double((Integer) line[5])) / 100);
            } else if (columnIndex == 6) {//charged
                return nf.format((new Double((Integer) line[6])) / 100);
            } else if (columnIndex == 7) {//wastes
                String s=nf.format((new Double((Integer) line[7])) / 100);
                return nf.format((new Double((Integer) line[7])) / 100);
            } else if (columnIndex == 8) {
                return nf.format((new Double((Integer) line[8])) / 100);
            } else if (columnIndex == 9) {
                return nf.format((new Double((Integer) line[9])) / 100);
            } else if (columnIndex == 10) {
                return nf.format((new Double((Integer) line[10])) / 100);
            } else if (columnIndex == 11) {
                return nf.format((new Double((Integer) line[11])) / 100);
            } else if (columnIndex == 12) {
                return nf.format((new Double((Integer) line[12])) / 100);
//            } else if (columnIndex == 13) {
//                return nf.format((new Double((Integer) line[13])) / 100);
//            } else if (columnIndex == 14) {
//                return nf.format((new Double((Integer) line[14])) / 100);
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private String description;
    private int productPrice;
    private short fixedProfit;
    private int calculatedProfit;

    /** Creates new form TakingsEscape */
    public Takings_1(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumn column = null;
        column = jTable1.getColumnModel().getColumn(0);
        column.setPreferredWidth(80);
        jTabbedPane1.setTitleAt(0, bundle.getString("Daily"));
        jTabbedPane1.addTab(bundle.getString("Weekly"), null);
        jTabbedPane1.addTab(bundle.getString("Monthly"), null);
        jTabbedPane1.addTab(bundle.getString("Yearly"), null);
        selectedDepartment = Main.settingsTab.selectedDepartment;
        mhr = new MyHeaderRenderer(align);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        for (int j = 0; j < tc.getColumnCount(); j++) {
            tc.getColumn(j).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        jTable1.setDefaultRenderer(Long.class, mtcr);
        jTable1.setDefaultRenderer(Date.class, mtcr);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Takings", Main.mainHelpSet);
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
        closeButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();
        calculateButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        tillIdSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Takings"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("Takings"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setName("jPanel1"); // NOI18N

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
        closeButton.setToolTipText(bundle.getString("Close")); // NOI18N
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        printButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/print_edit.gif"))); // NOI18N
        printButton.setToolTipText(bundle.getString("Print")); // NOI18N
        printButton.setBorderPainted(false);
        printButton.setContentAreaFilled(false);
        printButton.setEnabled(false);
        printButton.setName("printButton"); // NOI18N
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        printButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                printButtonKeyReleased(evt);
            }
        });

        calculateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Calculate.png"))); // NOI18N
        calculateButton.setToolTipText(bundle.getString("TakingsOld.calculateButton.tooltip.text")); // NOI18N
        calculateButton.setContentAreaFilled(false);
        calculateButton.setName("calculateButton"); // NOI18N
        calculateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calculateButtonActionPerformed(evt);
            }
        });

        jLabel1.setText(bundle.getString("Takings.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jDateChooser1.setName("jDateChooser1"); // NOI18N

        tillIdSpinner.setName("tillIdSpinner"); // NOI18N

        jLabel4.setText(bundle.getString("Takings.jLabel4.text")); // NOI18N
        jLabel4.setToolTipText(bundle.getString("Print")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tillIdSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 826, Short.MAX_VALUE)
                        .addComponent(calculateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(printButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tillIdSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(calculateButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                        .addComponent(printButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jTabbedPane1.setName(bundle.getString("Daily")); // NOI18N
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        jTabbedPane1.addTab("null", jScrollPane1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 1087, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    private void printButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_printButtonKeyReleased
        //ToDo
}//GEN-LAST:event_printButtonKeyReleased

    private void calculateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calculateButtonActionPerformed
        Audio.play("Beep");
        rowData.clear();
        Calendar startCal = jDateChooser1.getCalendar();
        Calendar endCal = Calendar.getInstance();
        if (startCal.after(endCal) || startCal.equals(endCal)) {
            if (startCal.getTimeInMillis() > endCal.getTimeInMillis()) {
                return;
            }
        }
        if (jTabbedPane1.getSelectedIndex() == 0) {//Daily
            for (Calendar currentCal = startCal;
                    currentCal.before(endCal);
                    currentCal.add(Calendar.DAY_OF_MONTH, 1)) {
                Calendar newCal = (Calendar) currentCal.clone();
                newCal.add(Calendar.DAY_OF_MONTH, 1);
                calculate(currentCal, newCal);
                model.fireTableDataChanged();
            }
        } else if (jTabbedPane1.getSelectedIndex() == 1) {//Weekly
            for (Calendar currentCal = startCal;
                    currentCal.before(endCal);
                    currentCal.add(Calendar.WEEK_OF_YEAR, 1)) {
                Calendar newCal = (Calendar) currentCal.clone();
                newCal.add(Calendar.WEEK_OF_YEAR, 1);
                calculate(currentCal, newCal);
            }
        } else if (jTabbedPane1.getSelectedIndex() == 2) {//Monthly
            for (Calendar currentCal = startCal;
                    currentCal.before(endCal);
                    currentCal.add(Calendar.MONTH, 1)) {
                Calendar newCal = (Calendar) currentCal.clone();
                newCal.add(Calendar.MONTH, 1);
                calculate(currentCal, newCal);
            }
        } else if (jTabbedPane1.getSelectedIndex() == 3) {//Yearly
            for (Calendar currentCal = startCal;
                    currentCal.before(endCal);
                    currentCal.add(Calendar.YEAR, 1)) {
                Calendar newCal = (Calendar) currentCal.clone();
                newCal.add(Calendar.YEAR, 1);
                calculate(currentCal, newCal);
            }
        }
        Audio.play("Beep");
}//GEN-LAST:event_calculateButtonActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        rowData.clear();
}//GEN-LAST:event_jTabbedPane1StateChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        // TODO add print function
    }//GEN-LAST:event_printButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Takings_1 dialog = new Takings_1(new javax.swing.JFrame(), true);
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

    public void execute(int till) {
        Audio.play("Tada");
        rowData.clear();
        jTable1.addNotify();
        Calendar c = Calendar.getInstance();
        jDateChooser1.setCalendar(c);
        jDateChooser1.requestFocus();
        tillIdSpinner.setValue(till);
        this.setVisible(true);
    }

    private void calculate(Calendar s, Calendar e) {//from start to end
        waste = 0;
        isOwnUse=false;
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            jTable1.clearSelection();
            saleTotal = 0;
            excludedTotal = 0;
            ownUseExcludedTotal = 0;
            departmentTotal = 0;
            servicesTotal = 0;
            netTotal = 0;
            departmentWasteTotal = 0;
            departmentReturnsTotal = 0;
            departmentLossesTotal = 0;
            productTotal = 0;
            productWasteTotal = 0;
            productReturnsTotal = 0;
            productLossesTotal = 0;
            profittotal = 0;
            ownUseTotal = 0;
            chargedTotal = 0;
            chargedTotalIncTax = 0;
            profitTotal = 0;
            taxTotal = 0;
            wastesTotal = 0;
            returnsTotal = 0;
            lossesTotal = 0;
            ownExcludedTotal = 0;
            taxId=0;
            product = 0L;
            charge = 0;
            rate = 0;
            aDepartment = 0;
            isCharged = false;
            //now scan though sales
            PreparedStatement salesQuery =
                    Main.getConnection().prepareStatement(sales);
            salesQuery.setInt(1, (Integer) tillIdSpinner.getValue());
            salesQuery.setInt(2, (Integer) tillIdSpinner.getValue());
            java.sql.Date d1 = new java.sql.Date(s.getTimeInMillis());//start
            salesQuery.setDate(3, d1);
            date = d1.toString();
            java.sql.Date d2 = new java.sql.Date(e.getTimeInMillis());//end
            salesQuery.setDate(4, d2);
            ResultSet rs = salesQuery.executeQuery();
            int lastSale = 0;
            if (rs.first()) {//not if there are none
                do {
                    sale = rs.getInt("Sales.ID");
                    saleLine = rs.getInt("SaleLines.ID");
                    price=rs.getInt("SaleLines.Price");
                    quantity = rs.getInt("saleLines.Quantity");
                    wholesale=rs.getInt("wholesalePrice");
                    packSize=rs.getInt("packSize");
                    description=rs.getString("Description");
                    productPrice=rs.getInt("Products.Price");
                    fixedProfit=rs.getShort("FixedProfit");
                    if(packSize==0){
                        packSize=1;
                    }
                    taxRate = rs.getInt("taxRate");
                    encode = rs.getInt("Encode");
                    saleDate = rs.getDate("Sales.WhenCreated");
                    if (lastSale != sale) {                        
                        lastSale = sale;
                        total = rs.getInt("Total");
                        tax = rs.getInt("Sales.Tax");
                        taxId = rs.getInt("Skus.Tax");
                        cash = rs.getInt("Cash");
                        cheque = rs.getInt("Cheque");
                        debit = rs.getInt("Debit");
                        coupon = rs.getInt("Coupon");
                        //tillId = rs.getInt("TillID");
                        waste = rs.getShort("Waste");
                        isCharged = (waste == SaleType.CHARGED.value());
                        isOwnUse = (waste == SaleType.OWNUSE.value());
                        isWaste = (waste == SaleType.WASTE.value()) || (waste == SaleType.RETURN.value()) || (waste == SaleType.LOSS.value());
                        if (waste == SaleType.CHARGED.value()) {
                            if (Regime.description(Main.shop.tax) == Regime.REGISTERED
                                    || Regime.description(Main.shop.tax) == Regime.WHOLESALE
                                    || Regime.description(Main.shop.tax) == Regime.SALESTAX) {
//                                taxTotal += tax;
                            }
                            if (waste == SaleType.CHARGED.value() && Regime.description(Main.shop.tax) == Regime.REGISTERED) {
                                chargedTotalIncTax += total;
                                chargedTotal += total - tax;
                            } else if (waste == SaleType.CHARGED.value() && (Regime.description(Main.shop.tax)
                                    == Regime.SALESTAX || Regime.description(Main.shop.tax)
                                    == Regime.WHOLESALE)) {
                                chargedTotalIncTax += total + tax;
                                chargedTotal += total;
                            } else if (waste == SaleType.CHARGED.value()
                                    && (Regime.description(Main.shop.tax) == Regime.UNREGISTERED)
                                    || Regime.description(Main.shop.tax) == Regime.NONE) {
                                chargedTotalIncTax += total;
                                chargedTotal += total;
                            }
                        } else if (!isOwnUse) {//all wastes
                            switch (waste) {
                                case 0: {//normal wastes
//                                    if (Regime.description(Main.shop.tax) == Regime.REGISTERED) {
//                                        wastesTotal += 1000 * total / (1000 + rate);
//                                    } else if (Regime.description(Main.shop.tax) == Regime.UNREGISTERED || Regime.description(Main.shop.tax) == Regime.WHOLESALE || Regime.description(Main.shop.tax) == Regime.SALESTAX || Regime.description(Main.shop.tax) == Regime.NONE) {
//                                        wastesTotal += total;
//                                    }
                                    break;
                                }
                                case 1: {//waste
                                    wastesTotal += total;
                                   break;
                                }
                                case 2: {//returns
                                    returnsTotal += total;
                                    break;
                                }
                                case 3:{//losses
                                    lossesTotal += total;
                                    break;
                                }
                            }
                        } else if(isOwnUse){
                            Regime tr = Regime.description(Main.shop.tax);
                            if(tr==Regime.WHOLESALE||tr==Regime.SALESTAX){
                                ownUseTotal+=total+tax;
                            }else if(tr==Regime.REGISTERED||tr==Regime.NONE){
                                ownUseTotal+=total;
                            }
                        }else{
                            total = 0;
                            tax = 0;
                            cash = 0;
                            cheque = 0;
                            debit = 0;
                            coupon = 0;
                            //tillId = 0;
                            isWaste = false;
                            isOwnUse = false;
                            waste = 0;
                            taxId = 1;
                        }
                        if(waste==0||isCharged){
                            saleTotal += total;
                            taxTotal += tax;
                        }
                        quantity = rs.getInt("Quantity");
                        price =  rs.getInt("SaleLines.Price");//took out time qty
                        rate = rs.getInt("Rate");
                        aDepartment = rs.getInt("Department");
                        if (taxId == excludedTaxID) {
                            if (waste == SaleType.NORMAL.value() || waste == SaleType.CHARGED.value()) {
                                excludedTotal += charge;
                                saleTotal -= charge;
                            }
                            if (waste == SaleType.OWNUSE.value()) {
                                ownUseExcludedTotal += charge;
                            }
                        } else if (aDepartment == Main.serviceslDepartment) {
                            if (Regime.description(Main.shop.tax) == Regime.REGISTERED) {
                                servicesTotal += 10000 * charge / (10000 + rate);
                            } else if (Regime.description(Main.shop.tax) == Regime.SALESTAX
                                    || Regime.description(Main.shop.tax) == Regime.WHOLESALE) {
                                servicesTotal += charge;
                            } else if (Regime.description(Main.shop.tax) == Regime.UNREGISTERED
                                    || Regime.description(Main.shop.tax) == Regime.NONE) {
                                servicesTotal += charge;
                            }
                        }
                        if (aDepartment == selectedDepartment && (waste == SaleType.NORMAL.value() || waste == SaleType.CHARGED.value())) {
                            if (Regime.description(Main.shop.tax) == Regime.REGISTERED) {
                                departmentTotal += 10000 * charge / (10000 + rate);
                            } else if (Regime.description(Main.shop.tax) == Regime.UNREGISTERED
                                    || Regime.description(Main.shop.tax) == Regime.SALESTAX) {
                                departmentTotal += charge;
                            }
                        }
                        if (aDepartment == selectedDepartment && waste == SaleType.WASTE.value()) {

                            if (Regime.description(Main.shop.tax) == Regime.REGISTERED) {
                                departmentWasteTotal +=
                                        10000 * charge / (10000 + rate);
                            } else if (Regime.description(Main.shop.tax)
                                    == Regime.UNREGISTERED
                                    || Regime.description(Main.shop.tax) == Regime.WHOLESALE
                                    || Regime.description(Main.shop.tax) == Regime.SALESTAX
                                    || Regime.description(Main.shop.tax) == Regime.NONE) {
                                departmentWasteTotal += charge;
                            }
                            if (aDepartment == selectedDepartment && waste == SaleType.RETURN.value()) {
                                if (Regime.description(Main.shop.tax) == Regime.REGISTERED) {
                                    departmentReturnsTotal +=
                                            10000 * charge / (10000 + rate);
                                } else if (Regime.description(Main.shop.tax)
                                        == Regime.UNREGISTERED
                                        || Regime.description(Main.shop.tax) == Regime.WHOLESALE
                                        || Regime.description(Main.shop.tax) == Regime.SALESTAX
                                        || Regime.description(Main.shop.tax) == Regime.NONE) {
                                    departmentReturnsTotal += charge;
                                }
                            }
                            if (aDepartment == selectedDepartment && waste == SaleType.LOSS.value()) {
                                if (Regime.description(Main.shop.tax) == Regime.REGISTERED) {
                                    departmentLossesTotal +=
                                            10000 * charge / (10000 + rate);
                                } else if (Regime.description(Main.shop.tax)
                                        == Regime.UNREGISTERED
                                        || Regime.description(Main.shop.tax) == Regime.WHOLESALE
                                        || Regime.description(Main.shop.tax) == Regime.SALESTAX
                                        || Regime.description(Main.shop.tax) == Regime.NONE) {
                                    departmentLossesTotal += charge;
                                }
                            }
                        }
                        product = rs.getLong("Product");
                        if (aDepartment == selectedDepartment
                                && product.compareTo(selectedProduct) == 0
                                && (!isWaste || isCharged) && !isOwnUse) {//selected product
                            //all selected department items, excludes VAT
                            if ((!isWaste && !isOwnUse) || (isCharged && !isOwnUse)) {
                                //excludes wastes and own use
                                if (Regime.description(Main.shop.tax) == Regime.REGISTERED) {
                                    productTotal += 10000 * charge / (10000 + rate);
                                } else if (Regime.description(Main.shop.tax) == Regime.UNREGISTERED
                                        || Regime.description(Main.shop.tax) == Regime.WHOLESALE
                                        || Regime.description(Main.shop.tax) == Regime.SALESTAX
                                        || Regime.description(Main.shop.tax) == Regime.NONE) {
                                    productTotal += charge;
                                }
                            } else if (!isOwnUse) {//all wastes
                                switch (waste) {
                                    case 0: {//normal wastes
                                        if (Regime.description(Main.shop.tax) == Regime.REGISTERED) {
                                            productWasteTotal += 10000 * charge / (10000 + rate);
                                        } else if (Regime.description(Main.shop.tax) == Regime.UNREGISTERED || Regime.description(Main.shop.tax) == Regime.WHOLESALE || Regime.description(Main.shop.tax) == Regime.SALESTAX || Regime.description(Main.shop.tax) == Regime.NONE) {
                                            productWasteTotal += charge;
                                        }
                                        break;
                                    }
                                    case 1: {//returns
                                        if (Regime.description(Main.shop.tax) == Regime.REGISTERED) {
                                            productReturnsTotal += 10000 * charge / (10000 + rate);
                                        } else if (Regime.description(Main.shop.tax) == Regime.UNREGISTERED || Regime.description(Main.shop.tax) == Regime.WHOLESALE || Regime.description(Main.shop.tax) == Regime.SALESTAX || Regime.description(Main.shop.tax) == Regime.NONE) {
                                            productReturnsTotal += charge;
                                        }
                                        break;
                                    }
                                    case 2: {//losses
                                        if (Regime.description(Main.shop.tax) == Regime.REGISTERED) {
                                            productLossesTotal +=
                                                    10000 * charge / (10000 + rate);
                                        } else if (Regime.description(Main.shop.tax)
                                                == Regime.UNREGISTERED
                                                || Regime.description(Main.shop.tax)
                                                == Regime.WHOLESALE
                                                || Regime.description(Main.shop.tax)
                                                == Regime.SALESTAX
                                                || Regime.description(Main.shop.tax) == Regime.NONE) {
                                            productLossesTotal += charge;
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    int aProfit = profitCalculation(quantity,price,wholesale,
                            packSize,taxRate,encode,waste,fixedProfit);
                    if(aProfit>1000){
                        int j=0;
                    }
                    if(aProfit<0){
                        String st="now";
                    }
                    profitTotal+=profitCalculation(quantity,price,wholesale,
                            packSize,taxRate,encode,waste,fixedProfit);
                } while (rs.next());
                if (Regime.description(Main.shop.tax) == Regime.REGISTERED
                        || Regime.description(Main.shop.tax) == Regime.SALESTAX) {
                    netTotal = saleTotal - taxTotal - servicesTotal + ownUseTotal;
                } else if (Regime.description(Main.shop.tax) == Regime.WHOLESALE) {
                    netTotal = saleTotal - taxTotal;
                    saleTotal = saleTotal + servicesTotal;
                } else {
                    netTotal = saleTotal - servicesTotal + ownUseTotal + ownUseTotal;
                }
//                {date, agency, sale, rental, net, tax, charged,
//                wastes, returns, losses, ownUse, profit, ownAgency, t1, t2}
                Object[] line1 = {
                    date,
                    excludedTotal,
                    saleTotal,
                    servicesTotal,
                    netTotal,
                    taxTotal,
                    chargedTotal,
                    wastesTotal,
                    returnsTotal,
                    lossesTotal,
                    ownUseTotal,
                    profitTotal,
                    ownExcludedTotal
//                    ,t1,
//                    t2
                };
                rowData.add(line1);
                model.fireTableDataChanged();
            } else {
                Object[] line1 = {date,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
//                    ,0, 0
                };
                rowData.add(line1);
                model.fireTableDataChanged();
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(Takings_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        setCursor(Cursor.getDefaultCursor());
    }
    
    public int profitCalculation(int qty,int price,int wholesale, int packSize, 
            int taxRate,int encode,int waste, short fixedProfit){
        if(!(waste==SaleType.NORMAL.value()||waste==SaleType.CHARGED.value())){
            return 0;
        }
        if(productPrice==1||fixedProfit==0){
            return 0;
        }
        if(encode==NewProduct.NOTENCODE){
            if(qty==0){
                return 0;
            }
            Regime tr = Regime.description(Main.shop.tax);
            if(tr==Regime.WHOLESALE||tr==Regime.SALESTAX||tr==Regime.NONE){
                //do nothing
            }else if(tr==Regime.REGISTERED){
                price = price*10000/(10000+taxRate);
            }
            if(wholesale==0){
                wholesale=price*80/100;
            }
            if(packSize==0){
                calculatedProfit=0;
            } else {
                calculatedProfit = (qty*(100*price-100*wholesale/packSize))/100;
            }
            return calculatedProfit;
        }
        return 0;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton calculateButton;
    private javax.swing.JButton closeButton;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton printButton;
    private javax.swing.JSpinner tillIdSpinner;
    // End of variables declaration//GEN-END:variables
}
