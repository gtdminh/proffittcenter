/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ReconciledCashups2.java
 *
 * Created on 30-May-2010, 12:06:12
 */

package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.prefs.Preferences;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author HP_Owner
 */
public class ReconciledCashups extends EscapeDialog {
private Preferences root = Preferences.userNodeForPackage(getClass());
    private static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/ReconciledCashups");
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("ID"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Till"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("From"));
    @SuppressWarnings("unchecked")
    private boolean b3 = tableHeader.add(bundle.getString("To"));
    @SuppressWarnings("unchecked")
    private boolean b5 = tableHeader.add(bundle.getString("Operator"));
    @SuppressWarnings("unchecked")
    private boolean b4 = tableHeader.add(bundle.getString("error"));
//    @SuppressWarnings("unchecked")
//    private boolean b6 = tableHeader.add(bundle.getString("CouponError"));
//    @SuppressWarnings("unchecked")
//    private boolean b7 = tableHeader.add(bundle.getString("ChequeError"));
//    @SuppressWarnings("unchecked")
//    private boolean b8 = tableHeader.add(bundle.getString("ReceivedError"));
//    @SuppressWarnings("unchecked")
//    private boolean b9 = tableHeader.add(bundle.getString("PaidoutError"));
    @SuppressWarnings("unchecked")
    private boolean b10 = tableHeader.add(bundle.getString("Float"));
    @SuppressWarnings("unchecked")
    private boolean b11 = tableHeader.add(bundle.getString("Reconciled"));
    private static String cashupReconcilliations = "SELECT Cashups.ID AS Cashup,Cashups.TillID AS Till," +
            "Cashups.WhenCreated AS 'To',Cashups.WhenStarted AS 'From'," +
            "Operators.Description AS Operator," +
            "Cashups.Error + Cashups.DebitsError +" +
            "Cashups.ChequeError +" +
            "Cashups.CouponsError + " +
            "Cashups.ReceivedError + Cashups.PaidOutError AS error," +
            "Cashups.FixedFloat AS 'Float' ,Cashups.Reconciled AS Reconciled " +
            "FROM Cashups,Operators " +
            "WHERE Cashups.Operator = Operators.ID AND " +
            "(?=true AND (error<>0 ) OR ?=false) " +
            "ORDER BY Cashups.ID  DESC";
    private int cashup;
    private int till;
    private Date to;
    private Date from;
    private int Net_error,theFloat;
    private boolean reconciled;
    private MyHeaderRenderer mhr = null;
    private MyTableCellRenderer mtcr = null;
    private int align[] = {SwingConstants.RIGHT, SwingConstants.RIGHT,
        SwingConstants.LEFT, SwingConstants.LEFT, SwingConstants.RIGHT,
        SwingConstants.RIGHT, SwingConstants.RIGHT,SwingConstants.RIGHT,
        SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.RIGHT, SwingConstants.CENTER};
    private String operator;
    private Object[] line = {new Integer(cashup), new Integer(till), from,to,operator,
        new Integer(Net_error),new Integer(theFloat),new Boolean(reconciled)};
    private Vector<Object[]> rowData = new Vector<Object[]>();
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
            NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumFractionDigits(2);
            if (columnIndex == 0) {//cashup
                return line[0];
            } else if (columnIndex == 1) {//till
                return line[1];
            } else if (columnIndex == 2) {//from
                return line[2];
            } else if (columnIndex == 3) {//to
                return line[3];
            } else if (columnIndex == 4) {//to
                return line[4];
            } else if (columnIndex == 5) {//Net_error
                return nf.format((new Double((Integer) line[5])) / 100);
            }else if (columnIndex == 6) {//theFloat
                return nf.format((new Double((Integer) line[6])) / 100);
            }else if (columnIndex == 7) {//reconciled
                return line[7];
            }else if (columnIndex == 8) {
                return line[8];
            } else {
                return null;
            }
        }

        @Override
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    };
    private boolean showIt;
    /** Creates new form ReconciledCashups2 */
    public ReconciledCashups(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumnModel tc = jTable1.getTableHeader().getColumnModel();
        tc.getColumn(0).setPreferredWidth(50);
        tc.getColumn(1).setPreferredWidth(50);
        tc.getColumn(2).setPreferredWidth(150);
        tc.getColumn(3).setPreferredWidth(150);
        tc.getColumn(4).setPreferredWidth(100);
        tc.getColumn(5).setPreferredWidth(150);
        tc.getColumn(6).setPreferredWidth(150);
//        tc.getColumn(7).setPreferredWidth(150);
//        tc.getColumn(8).setPreferredWidth(150);
//        tc.getColumn(9).setPreferredWidth(150);
//        tc.getColumn(10).setPreferredWidth(100);
//        tc.getColumn(11).setPreferredWidth(150);
        mhr = new MyHeaderRenderer(align);
        for (int i = 0; i < tc.getColumnCount(); i++) {
            tc.getColumn(i).setHeaderRenderer(mhr);
        }
        mtcr = new MyTableCellRenderer(align);
        jTable1.setDefaultRenderer(Integer.class, mtcr);
        jTable1.setDefaultRenderer(String.class, mtcr);
        JTableHeader header = jTable1.getTableHeader();
        jTable1.setAutoCreateRowSorter(true);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Reconciledcashups", Main.mainHelpSet);
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
        inError = new javax.swing.JCheckBox();
        infoButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/ReconciledCashups"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("reconciledCashups"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9", "Title 10", "Title 11", "Title 12"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        inError.setText(bundle.getString("ReconciledCashups.inError.text")); // NOI18N
        inError.setName("inError"); // NOI18N
        inError.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inErrorActionPerformed(evt);
            }
        });

        infoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Info.png"))); // NOI18N
        infoButton.setToolTipText(bundle.getString("ReconciledCashups.infoButton.toolTipText")); // NOI18N
        infoButton.setContentAreaFilled(false);
        infoButton.setName("infoButton"); // NOI18N
        infoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                infoButtonActionPerformed(evt);
            }
        });

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
        closeButton.setContentAreaFilled(false);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
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
                        .addComponent(inError)
                        .addGap(18, 18, 18)
                        .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1003, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 729, Short.MAX_VALUE)
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(inError)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(infoButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inErrorActionPerformed
        drawGrid();
}//GEN-LAST:event_inErrorActionPerformed

    private void infoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_infoButtonActionPerformed
        int selection = jTable1.getSelectedRow();
        if (selection < 0) {//no selected row
            return;
        }
        selection=jTable1.convertRowIndexToModel(selection);
        cashup = (Integer) model.getValueAt(selection, 0);
        Main.cashupReconciliation.execute(cashup,true);
        drawGrid();
}//GEN-LAST:event_infoButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
}//GEN-LAST:event_closeButtonActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ReconciledCashups dialog = new ReconciledCashups(new javax.swing.JFrame(), true);
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
    private javax.swing.JCheckBox inError;
    private javax.swing.JButton infoButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

     public boolean execute() {
       showIt=false;
        inError.setSelected(true);
        drawGrid();
//        if(!showIt){
//            return false;
//        }
        Audio.play("Tada");
        setVisible(true);
        return showIt;
    }

     private void drawGrid() {
        PreparedStatement cashups;
        try {
            cashups = Main.getConnection().prepareStatement(cashupReconcilliations );
            cashups.setBoolean(1, inError.isSelected());
            cashups.setBoolean(2, inError.isSelected());
            ResultSet rs = cashups.executeQuery();
            jTable1.clearSelection();
            rowData.clear();
            while (rs.next()) {
                cashup = rs.getInt("Cashup");
                till = rs.getInt("Till");
                from = rs.getDate("From");
                to = rs.getDate("To");
                Net_error = rs.getInt("error");
                operator = rs.getString("Operator");
//                debit = rs.getInt("Debit");
//                cheque=rs.getInt("Cheque");
//                received = rs.getInt("Received");
//                paidOut = rs.getInt("PaidOut");
                theFloat=rs.getInt("Float");
                reconciled = rs.getBoolean("Reconciled");
                Object[] line1 = {new Integer(cashup), new Integer(till), from,to,
                operator,
                new Integer(Net_error),new Integer(theFloat),new Boolean(reconciled)};
                boolean b=rowData.add(line1);
                model.fireTableDataChanged();
                showIt=true;
            }
            jTable1.revalidate();
            rs.close();
            cashups.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
