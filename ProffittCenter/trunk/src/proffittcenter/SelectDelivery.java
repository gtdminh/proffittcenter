/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SelectDelivery.java
 *
 * Created on 23-Apr-2010, 14:37:22
 */
package proffittcenter;

import java.awt.event.KeyEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

/**
 *
 * @author HP_Owner
 */
public class SelectDelivery extends EscapeDialog {

    private static ResourceBundle bundle = ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SelectDelivery");
    private String deliveries = "SELECT ID,WhenCreated,Reference FROM Deliveries "
            + "WHERE Supplier=? AND Completed=false ORDER BY WhenCreated DESC";
    private String delivery = "INSERT INTO Deliveries (ID,WhenCreated,Reference,Supplier,Total,Tax) "
            + "VALUES(null,?,?,?,0,0)";
    private String lastID = "SELECT LAST_INSERT_ID() FROM Deliveries";
    private Vector tableHeader = new Vector();
    @SuppressWarnings("unchecked")
    private boolean b0 = tableHeader.add(bundle.getString("ID"));
    @SuppressWarnings("unchecked")
    private boolean b1 = tableHeader.add(bundle.getString("Date"));
    @SuppressWarnings("unchecked")
    private boolean b2 = tableHeader.add(bundle.getString("Reference"));
    private int align[] = {SwingConstants.RIGHT, SwingConstants.LEFT,
        SwingConstants.RIGHT};
    int id;
    Date date;
    String reference;
    private Object[] line = {id, date, reference};
    private Vector<Object[]> rowData = new Vector<Object[]>();
    private AbstractTableModel model = new AbstractTableModel() {

        public int getRowCount() {
            return rowData.size();
        }

        public int getColumnCount() {
            return tableHeader.size();
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= rowData.size() || rowIndex < 0) {
                return "";
            }
            if (columnIndex < 0 || columnIndex > tableHeader.size()) {
                return "";
            }
            line = rowData.get(rowIndex);
            return line[columnIndex];
        }

        @Override
        public String getColumnName(int col) {
            return (String) tableHeader.get(col);
        }
    };
    private int returnID;
    private int supplier;
    private int selection;
    private String isDeliveryListed = "SELECT ID FROM Deliveries WHERE Reference=? AND Supplier=?";

    /** Creates new form SelectDelivery
    For the entry of delivery note numbers
     * @param parent
     * @param modal  
     */
    public SelectDelivery(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        jTable1.setModel(model);
        TableColumn column = null;
        column = jTable1.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);
        column = jTable1.getColumnModel().getColumn(2);
        column.setPreferredWidth(400);
        getRootPane().setDefaultButton(okButton);
        Main.mainHelpBroker.enableHelpKey(getRootPane(), "Selectdelivery", Main.mainHelpSet);
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
        closeButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        referenceText = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenterworkingcopy/resource/SelectDelivery"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("SelectDelivery"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jTable1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTable1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/Close24.png"))); // NOI18N
        closeButton.setContentAreaFilled(false);
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenterworkingcopy/resource/OK.png"))); // NOI18N
        okButton.setContentAreaFilled(false);
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        referenceText.setName("referenceText"); // NOI18N
        referenceText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                referenceTextKeyReleased(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText(bundle.getString("SelectDelivery.jLabel1.text")); // NOI18N
        jLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLabel1.setName("jLabel1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(referenceText, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(referenceText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        ok();
    }

    private void ok() {
        if (referenceText.getText().isEmpty()) {
            selection = jTable1.getSelectedRow();
            if (selection < 0 ) {
                //no selected row
//                Audio.play("Ring");
                return;
            }
            selection = jTable1.convertRowIndexToModel(selection);
            returnID = (Integer) model.getValueAt(selection, 0);
        } else { //create a new delivery
            try {
                reference = referenceText.getText().trim();
                if (reference.isEmpty()) {
                    //was just spaces
                    referenceText.setText("");
                    referenceText.requestFocus();
                    Audio.play("Ring");
                    return;
                }                
                //check that this delivery is not already listed
                PreparedStatement idl = Main.getConnection().prepareStatement(isDeliveryListed); 
                idl.setString(1, reference);
                idl.setInt(2, supplier);
                ResultSet idlrs = idl.executeQuery();
                if(idlrs.first()){
                    returnID = idlrs.getInt("ID");
                    idlrs.close();
                    setVisible(false);
                    return;
                }
                idlrs.close();
                date = Main.selectDate.execute(null);
                if(date == null){
                   setVisible(false);
                   return;
                }
                PreparedStatement iid = Main.getConnection().prepareStatement(delivery);
                iid.setString(1, date.toString());
                iid.setString(2, reference);
                iid.setInt(3, supplier);
                iid.executeUpdate();
                PreparedStatement last = Main.getConnection().prepareStatement(lastID);
                ResultSet rs = last.executeQuery();
                if (rs.first()) {
                    returnID = rs.getInt("LAST_INSERT_ID()");
                } else {
                    returnID = -1;
                }
                rs.close();
            }catch (SQLException ex) {
               Logger.getLogger(SelectDelivery.class.getName()).log(Level.SEVERE, null, ex);
               Audio.play("Ring");
               returnID = -1;
           }
        }
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void referenceTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_referenceTextKeyReleased
        if (referenceText.getText().length() > 50) {
            String s = StringOps.fixLength(referenceText.getText(), 50);
            referenceText.setText(s);
        }
        int xx = evt.getKeyChar();
        int xt = evt.getKeyCode();
        if (evt.getKeyCode() == KeyEvent.VK_UP) {
            selection = jTable1.getSelectedRow() - 1;
            if (selection >= 0) {
                jTable1.setRowSelectionInterval(selection, selection);
                Audio.play("Beep");
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_DOWN) {
            selection = jTable1.getSelectedRow() + 1;
            int i = jTable1.getRowCount() - 1;
            if (selection < jTable1.getRowCount()) {
                jTable1.setRowSelectionInterval(selection, selection);
                Audio.play("Beep");
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_ENTER && !referenceText.getText().isEmpty()){
            ok();
        }
    }//GEN-LAST:event_referenceTextKeyReleased

    private void jTable1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTable1KeyReleased
        if (KeyEvent.VK_ENTER == evt.getKeyCode()) {
           ok();
        }
        referenceText.requestFocus();
    }//GEN-LAST:event_jTable1KeyReleased

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        referenceText.requestFocus();
    }//GEN-LAST:event_jTable1MouseClicked

    public int execute(int theSupplier) {
        Audio.play("Tada");
        rowData.clear();
        supplier = theSupplier;
        try {
            //Look up and display all delivery notes for this supplier
            PreparedStatement dq = Main.getConnection().prepareStatement(deliveries);
            dq.setInt(1, theSupplier);
            ResultSet rs = dq.executeQuery();
            while (rs.next()) {
                //fill structure with data
                id = rs.getInt("ID");
                date = rs.getDate("WhenCreated");
                reference = rs.getString("Reference");
                Object[] line1 = {id, date, reference};
                boolean add = rowData.add(line1);
                model.fireTableDataChanged();
            }
            referenceText.setText("");
            referenceText.requestFocus();
            returnID = -1;
            setVisible(true);
            return returnID;
        } catch (SQLException ex) {
            Audio.play("Ring");
            Logger.getLogger(SelectDelivery.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                SelectDelivery dialog = new SelectDelivery(new javax.swing.JFrame(), true);
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField referenceText;
    // End of variables declaration//GEN-END:variables
}
