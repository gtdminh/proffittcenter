/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Balance.java
 *
 * Created on 05-Jan-2009, 05:57:17
 */
package proffittcenter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class Balance extends EscapeDialog {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("proffittcenter/resource/Balance");
    private String date;
    private int charged;
    private int totalCharged;
    private int totalReceived;
    private int balance;
    private int sale;
    private MyHeaderRenderer mhr = null;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT,
        SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT,
        SwingConstants.RIGHT, SwingConstants.RIGHT};
    private final MyTableCellRenderer renderer;
    private String fullName = "";
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("Sale"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Date"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Charged") + " (" + Main.shop.poundSymbol+")");
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Received") + " (" + Main.shop.poundSymbol+")");
    private long customer;
    private String customerName;
    private int received = 0;
    private Object[] line = {sale, date, charged, received};
    private Vector<Object[]> rowData = new Vector<Object[]>();
    private final AbstractTableModel model = new AbstractTableModel() {

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
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            if(rowIndex>=rowData.size()){
                return "";
            }
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {//sale
                return line[0];
            } else if (columnIndex == 1) {//date
                if (line[1].toString().isEmpty()) {
                    return "";
                }
                return line[1];
            } else if (columnIndex == 2) {//Charge
                if (line[2].toString().isEmpty() || (Integer) line[2] == 0) {
                    return "";
                }
                return nf.format((new Double((Integer) line[2])) / 100);
            } else if (columnIndex == 3) {//Received
                if (line[3].toString().isEmpty() || (Integer) line[3] == 0) {
                    return "";
                }
                return nf.format((new Double((Integer) line[3])) / 100);
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private boolean ownUse;
    String sql;

    public int getBalance(long customer) {
        try {
            PreparedStatement selectionStatement = Main.getConnection().prepareStatement(SQL.balance);
            charged = 0;
            totalCharged = 0;
            received = 0;
            totalReceived = 0;
            selectionStatement.setLong(1, customer);
            ResultSet rs = selectionStatement.executeQuery();
            while (rs.next()) {
                charged = rs.getInt("C");
                totalCharged += charged;
                received = rs.getInt("R");
                totalReceived += received;
            }
            selectionStatement.close();
            balance = totalCharged - totalReceived;
        } catch (SQLException ex) {
            Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
        }
        return balance;
    }

    private void drawGrid() {
        try {
            sql=ownUse?SQL.ownUseBalance:SQL.balance;
            PreparedStatement selectionStatement =
                    Main.getConnection().prepareStatement(sql);
            totalCharged = 0;
            totalReceived = 0;
            selectionStatement.setLong(1, customer);
            ResultSet rs = selectionStatement.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            while (rs.next()) {
                Date aDate = rs.getDate("D");
                date = aDate.toString();
                charged = rs.getInt("C");
                if(ownUse){
                    received=0;
                } else {
                    received = rs.getInt("R");}
                
                totalCharged += charged;
                sale = rs.getInt("Sale");
                totalReceived += received;
                sale = rs.getInt("Sale");
                Object[] line1 = new Object[]{new Integer(sale), date, new Integer(charged),new Integer(received) };
                boolean add = rowData.add(line1);
                model.fireTableDataChanged();
            }
            balance = totalCharged - totalReceived;
            jCharged.setText((new Money(totalCharged)).toString());
            jReceived.setText((new Money(totalReceived)).toString());
            jBalance.setText((new Money(balance)).toString());
            rs.close();
            selectionStatement.close();
            jTable1.repaint();
        } catch (SQLException ex) {
            Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
        }
    }


    /** Creates new form Balance
     * @param parent */
    public Balance(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        getRootPane().setDefaultButton(detailsButton);
        jTable1.setModel(model);
        TableColumn column = null;
        column = jTable1.getColumnModel().getColumn(0);
        column.setPreferredWidth(30);
        column = jTable1.getColumnModel().getColumn(1);
        column.setPreferredWidth(40);
        column = jTable1.getColumnModel().getColumn(2);
        column.setPreferredWidth(30);
        column = jTable1.getColumnModel().getColumn(3);
        column.setPreferredWidth(30);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        mhr = new MyHeaderRenderer(align);
        tc.getColumn(0).setHeaderRenderer(mhr);
        tc.getColumn(1).setHeaderRenderer(mhr);
        tc.getColumn(2).setHeaderRenderer(mhr);
        tc.getColumn(3).setHeaderRenderer(mhr);
        renderer = new MyTableCellRenderer(align);
        try {
            jTable1.setDefaultRenderer(Class.forName("java.lang.Integer"), renderer);
            jTable1.setDefaultRenderer(Class.forName("java.lang.String"), renderer);
            jTable1.setDefaultRenderer(Class.forName("java.lang.Long"), renderer);
            jTable1.setDefaultRenderer(Class.forName("java.util.Date"), renderer);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Balance.class.getName()).log(Level.SEVERE, null, ex);
            Audio.play("Ring");
        }
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Balance", Main.mainHelpSet);
    }

    void execute(long customer,boolean ownUse) {
        this.ownUse=ownUse;
        this.customer = customer;
        drawGrid();
        Audio.play("Tada");
        if (fullName.isEmpty()&&!Main.customers.getFirstName(customer).isEmpty()) {
            fullName = Main.customers.getFirstName(customer) + ", "
                    + Main.customers.getSurname(customer);
        }else {
            fullName = Main.customers.getSurname(customer);
        }
        setTitle(bundle.getString("Balance.title")+": "+fullName);
        //FormRestore.createPosition(this, root);
        setVisible(true);
        //FormRestore.destroyPosition(this, root);
        this.customer = SaleType.CUSTOMER.code() * 10000l;
        fullName = "";
        Main.customers.setName("");
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
        jReceived = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jBalance = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jCharged = new javax.swing.JTextField();
        closeButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        detailsButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Balance"); // NOI18N
        setTitle(bundle.getString("Balance.title")); // NOI18N
        setModal(true);
        setName("Balance"); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("proffittcenter/resource/Balance"); // NOI18N
        jLabel2.setText(bundle1.getString("Balance.jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jReceived.setName("jReceived"); // NOI18N

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText(bundle1.getString("Balance.jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jBalance.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jBalance.setName("jBalance"); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(bundle1.getString("Balance.jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jCharged.setName("jCharged"); // NOI18N

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setIconTextGap(0);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTable1FocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        detailsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Info.png"))); // NOI18N
        detailsButton.setContentAreaFilled(false);
        detailsButton.setName("detailsButton"); // NOI18N
        detailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jReceived, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCharged, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(detailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(80, 80, 80))
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 540, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jBalance, jCharged, jReceived});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 468, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2)
                    .addComponent(jReceived, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jCharged, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(detailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {closeButton, detailsButton});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    private void detailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailsButtonActionPerformed
        int selection = jTable1.getSelectedRow();
        if (selection < 0) {//no selected row
            return;
        }
        sale = (Integer) model.getValueAt(selection, 0);
//        Main.sale1.setLocationRelativeTo(this);
        Main.sale1.execute(sale, 0, false,0, false, false);
    }//GEN-LAST:event_detailsButtonActionPerformed

    private void jTable1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTable1FocusGained
        detailsButton.requestFocus();
    }//GEN-LAST:event_jTable1FocusGained

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Balance dialog = new Balance(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton detailsButton;
    private javax.swing.JTextField jBalance;
    private javax.swing.JTextField jCharged;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JTextField jReceived;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables


}
