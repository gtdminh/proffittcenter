/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Wastes.java
 *
 * Created on 15-Jan-2009, 06:53:07
 */
package proffittcenter;

import java.awt.Cursor;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

/**
 *
 * @author HP_Owner
 */
public class Wastes extends EscapeDialog {

    private Preferences root = Preferences.userNodeForPackage(getClass());
    private ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Wastes");
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("Operator"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Date"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Quantity"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Product"));
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("Price"));
    @SuppressWarnings("unchecked")
    private boolean b5 = tableHeader.add(bundle.getString("Charge"));
    private String operator;
    private String date;
    private int quantity;
    private String product;
    private int price;
    private int charge;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private int align[] = {SwingConstants.LEFT, SwingConstants.RIGHT,
        SwingConstants.RIGHT, SwingConstants.LEFT, SwingConstants.RIGHT,
        SwingConstants.RIGHT};
    private Object[] line = {operator, date, quantity, product, price,charge};
    private Vector<Object[]> rowData = new Vector<Object[]>();
    private AbstractTableModel model = new AbstractTableModel() {

        @Override
        public String getColumnName(int col) {
            return (String) tableHeader.get(col);
        }
        @Override
        public int getRowCount() {
            return rowData.size();
        }

        @Override
        public int getColumnCount() {
            return tableHeader.size();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {//operator
                return line[0];
            } else if (columnIndex == 1) {// date
                return line[1];
            } else if (columnIndex == 2) {//quantity
                Integer i = (Integer) line[2];
                return i.toString();
            } else if (columnIndex == 3) {//product
                return line[3];
            } else if (columnIndex == 4) {//price
                 return ((new Double((Integer)line[4])) / 100);
            } else if (columnIndex == 5) {//charge
                 return ((new Double((Integer)line[5])) / 100);
            } else {
                return null;
            }
        }
        @Override
        public Class getColumnClass(int c) {
            if(getRowCount()==0){
                return Integer.class;
            }
            return getValueAt(0, c).getClass();
        }
    };
    private String sql = "SELECT Operators.Description AS Name," +
            "Sales.WhenCreated AS Date," +
            "SaleLines.Quantity AS Quantity," +
            "Products.Description AS Description," +
            "Products.Encoded, " +
            "SaleLines.Price AS Price, " +
            "Taxes.Rate " +
            "FROM Operators,Sales,SaleLines,Products,Departments,Skus,Taxes " +
            "WHERE (Departments.ID=? OR 0=?) " +
            "AND Sales.Operator=Operators.Id " +
            "AND Sales.Id=SaleLines.Sale " +
            "AND Skus.Id=Products.Sku " +
            "AND Skus.Department=Departments.Id " +
            "AND SaleLines.Product=Products.Id " +
            "AND SaleLines.Quantity<>0 " +
            "AND Sales.Waste=? " +
            "AND Sales.WhenCreated > ? " + //start
            "AND Sales.WhenCreated <= ? " + //end
            "AND Skus.Tax=Taxes.ID " +
            "ORDER BY ";
    private SaleType stl;
    private int departmentL;
    private Date dateL;
    private int columnSelected=0;
    private int chargeTotal;
    private int rate;
    private int tax;

    /** Creates new form Wastes */
    @SuppressWarnings("LeakingThisInConstructor")
    public Wastes(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumn column = null;
        column = jTable1.getColumnModel().getColumn(0);
        column.setPreferredWidth(200);
        column = jTable1.getColumnModel().getColumn(1);
        column.setPreferredWidth(300);
        column = jTable1.getColumnModel().getColumn(3);
        column.setPreferredWidth(450);
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        JTableHeader header = jTable1.getTableHeader();
        jTable1.setAutoCreateRowSorter(true);
//        header.addMouseListener(this);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Waste", Main.mainHelpSet);
    }

    void execute(SaleType st, int department) {
        stl = st;
        jTable1.clearSelection();
        rowData.clear();
        departmentL = department;
        if(department!=0){
            setTitle(st.toString()+" "+bundle.getString("Department"));
        } else {
            setTitle(st.toString());
        }
        Calendar c = Calendar.getInstance();
        jDateChooser2.setCalendar(c);
        c.add(Calendar.DAY_OF_MONTH, -1);
        jDateChooser1.setCalendar(c);
        jDateChooser2.requestFocus();
        totalChargeTextField.setText("");
        Audio.play("Tada");
        model.fireTableDataChanged();
        setVisible(true);
    }

    private void drawGrid() {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        chargeTotal = 0;
        try {
            PreparedStatement ps;
            if (columnSelected == 0) {
                ps = Main.getConnection().prepareStatement(sql + "Name");
            } else if (columnSelected == 1) {
                ps = Main.getConnection().prepareStatement(sql + "Products.WhenCreated");
            } else if (columnSelected == 2) {
                ps = Main.getConnection().prepareStatement(sql + "SaleLines.Quantity");
            } else if (columnSelected == 3) {
                ps = Main.getConnection().prepareStatement(sql + "Description");
            } else if (columnSelected == 4) {
                ps = Main.getConnection().prepareStatement(sql + "SaleLines.Price");
            } else if (columnSelected == 5) {
                ps = Main.getConnection().prepareStatement(sql + "SaleLines.Quantity*SaleLines.Price");
            }else {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            ps.setInt(1, departmentL);
            ps.setInt(2, departmentL);
            ps.setShort(3, stl.value());
            Calendar startDate = jDateChooser1.getCalendar();
            Calendar endDate = jDateChooser2.getCalendar();
            endDate.add(Calendar.DAY_OF_MONTH, 1);
            java.sql.Date sd = new java.sql.Date(startDate.getTimeInMillis());//start
            ps.setDate(4, sd);
            java.sql.Date ed = new java.sql.Date(endDate.getTimeInMillis());//end
            ps.setDate(5, ed);
            ResultSet rs = ps.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            while (rs.next()) {//departments to display {operator, date, quantity, product, price}
                operator = rs.getString("Name");
                date = rs.getString("Date");
                if(stl==SaleType.LOSS){
                    quantity = rs.getInt("Quantity");
                    charge = Line.getCharge(rs);
                } else {
                    quantity = rs.getInt("Quantity");
                    charge = Line.getCharge(rs);
                }
                rate = rs.getInt("Rate");
                product = rs.getString("Description");
                price=rs.getInt("Price");
//                if(Regime.description(Main.shop.tax) == Regime.REGISTERED){
//                    price=price*(1000-rate)/1000;
//                    charge=charge*(1000-rate)/1000;
//                }
                Object[] line1 = {operator, date, quantity, product, price,charge};
                rowData.add(line1);
                chargeTotal += charge;
                model.fireTableDataChanged();
            }
            rs.close();
            totalChargeTextField.setText(new Money(chargeTotal).toString());
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        } catch (SQLException ex) {
            Logger.getLogger(Wastes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        closeButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        totalChargeTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("waste");
        setName("Wastes"); // NOI18N

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

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setName("jPanel1"); // NOI18N

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

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/SalesBy"); // NOI18N
        jLabel1.setText(bundle.getString("SalesBy.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(bundle.getString("SalesBy.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/OK.png"))); // NOI18N
        okButton.setBorderPainted(false);
        okButton.setContentAreaFilled(false);
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        totalChargeTextField.setName("totalChargeTextField"); // NOI18N

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("proffittcenter/resource/Wastes"); // NOI18N
        jLabel3.setText(bundle1.getString("Charge")+" ("+Main.shop.poundSymbol+")"); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jDateChooser1.setName("jDateChooser1"); // NOI18N

        jDateChooser2.setName("jDateChooser2"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 367, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(totalChargeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(totalChargeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3))
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {closeButton, okButton});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 478, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    private void closeButtonKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_closeButtonKeyReleased
        
}//GEN-LAST:event_closeButtonKeyReleased

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        drawGrid();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                Wastes dialog = new Wastes(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton closeButton;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField totalChargeTextField;
    // End of variables declaration//GEN-END:variables

//    public void mouseClicked(MouseEvent e) {
//        TableColumnModel colModel = jTable1.getColumnModel();
//        // The index of the column whose header was clicked
//        int vColIndex = colModel.getColumnIndexAtX(e.getX());
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
//        if (!headerRect.contains(e.getX(), e.getY())) {
//            // Mouse was clicked between column heads
//            // vColIndex is the column head closest to the click
//            // vLeftColIndex is the column head to the left of the click
//            int vLeftColIndex = vColIndex;
//            if (e.getX() < headerRect.x) {
//                vLeftColIndex--;
//            }
//        }
//        columnSelected = vColIndex;
//        drawGrid();
//    }
//
//    public void mousePressed(MouseEvent e) {
//        //do nothing
//    }
//
//    public void mouseReleased(MouseEvent e) {
//        //do nothing
//    }
//
//    public void mouseEntered(MouseEvent e) {
//        //do nothing
//    }
//
//    public void mouseExited(MouseEvent e) {
//        //do nothing
//    }
}
