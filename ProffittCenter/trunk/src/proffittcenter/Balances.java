/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Balances.java
 *
 * Created on 03-Jan-2009, 14:02:53
 */
package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JRootPane;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class Balances extends EscapeDialog {

    private static final ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Balances");
    private MyHeaderRenderer mhr = null;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT,
            SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT};
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("Customer_ID"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Customer_Name"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Charged")+" "+Main.shop.poundSymbol);
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("Received")+" "+Main.shop.poundSymbol);
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("Balance")+" "+Main.shop.poundSymbol);
    private long customer;
    private String customerName;
    private int charged = 0;
    private int received = 0;
    private int balance = 0;
    private Object[] line = {customer, customerName, balance};
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

        public Object getValueAt(int rowIndex, int columnIndex) {
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMaximumFractionDigits(2);
            nf.setMinimumFractionDigits(2);
            line = rowData.get(rowIndex);
            if (columnIndex == 0) {//customer
                return line[0];
            } else if (columnIndex == 1) {//customer name
                return line[1];
            } else if (columnIndex == 2) {//charged
                return nf.format((new Double((Integer) line[2])) / 100);
            } else if (columnIndex == 3) {//received
                return nf.format((new Double((Integer) line[3])) / 100);
            } else if (columnIndex == 4) {//balance
                return nf.format((new Double((Integer) line[4])) / 100);
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private final MyTableCellRenderer renderer;
    private final int columnSelected=0;
    private boolean ownUse;
    private String sql;
    private String fullName;

    public void execute(boolean ownUse) {
        this.ownUse=ownUse;
        drawGrid();
        Audio.play("Tada");
        setVisible(true);
        customer = SaleType.CUSTOMER.code() * 10000l;
        JTableHeader header = jTable1.getTableHeader();
    }

    private void drawGrid() {
        try {
            if(ownUse){
                sql=SQL.ownUseBalances;
            } else {
                sql = SQL.balances;
            }
            PreparedStatement balances;
            balances = Main.getConnection().prepareStatement(sql );
            balances.setBoolean(1, ownUse);
            ResultSet rs = balances.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            while (rs.next()) {//balances to display
                customer = rs.getLong("C");
                customerName = rs.getString("N1").trim() + ", " + rs.getString("N2");
                charged = rs.getInt("Ch");
                received = 0;
                balance = charged;
                if(!ownUse){
                    received = rs.getInt("Rc");
                    balance = rs.getInt("Bal");//charged - received;
                }
                int v=SaleType.RECEIVEDONACCOUNT.value();
                Object[] line1 = {customer, customerName, charged, received, balance};
                rowData.add(line1);
                model.fireTableDataChanged();
            }
            rs.close();
            balances.close();
        } catch (SQLException ex) {
            Logger.getLogger(Balances.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Creates new form Balances
     * @param parent
     * @param modal */
    public Balances(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumn column = null;
        column = jTable1.getColumnModel().getColumn(0);
        column.setPreferredWidth(200);
        column = jTable1.getColumnModel().getColumn(1);
        column.setPreferredWidth(500);
        column = jTable1.getColumnModel().getColumn(2);
        column.setPreferredWidth(200);
        column = jTable1.getColumnModel().getColumn(3);
        column.setPreferredWidth(200);
        column = jTable1.getColumnModel().getColumn(4);
        column.setPreferredWidth(200);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        mhr = new MyHeaderRenderer(align);
        tc.getColumn(0).setHeaderRenderer(mhr);
        tc.getColumn(1).setHeaderRenderer(mhr);
        tc.getColumn(2).setHeaderRenderer(mhr);
        tc.getColumn(3).setHeaderRenderer(mhr);
        tc.getColumn(4).setHeaderRenderer(mhr);
        renderer=new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, renderer);
        jTable1.setDefaultRenderer(String.class, renderer);
        jTable1.setDefaultRenderer(Long.class, renderer);
        JTableHeader header = jTable1.getTableHeader();
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Balances", Main.mainHelpSet);
        JRootPane rp = getRootPane();
        rp.setDefaultButton(detailsButton);
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
        detailsButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/Balances"); // NOI18N
        setTitle(bundle.getString("Balances")); // NOI18N
        setName("Balances"); // NOI18N

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

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        detailsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Info.png"))); // NOI18N
        detailsButton.setBorderPainted(false);
        detailsButton.setName("detailsButton"); // NOI18N
        detailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(848, Short.MAX_VALUE)
                .addComponent(detailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(detailsButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {closeButton, detailsButton});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 912, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    private void detailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailsButtonActionPerformed
        int selection = jTable1.getSelectedRow();
        String j = SaleType.OWNUSE.valueString();
        if (selection == -1) {
            Audio.play("Ring");
        } else {
            customer = (Long) jTable1.getValueAt(selection, 0);
            fullName=(String)jTable1.getValueAt(selection, 1);
            Audio.play("Beep");
            Main.balance.execute(customer,ownUse);
        }
    }//GEN-LAST:event_detailsButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Balances dialog = new Balances(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton detailsButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    
}
