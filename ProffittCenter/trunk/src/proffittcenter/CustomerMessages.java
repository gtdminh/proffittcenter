/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CustomerMessages.java
 *
 * Created on 06-Jan-2011, 11:55:19
 */

package proffittcenter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Dave
 */
public class CustomerMessages extends EscapeDialog {

    private JDBCTableModel jtm;
    String customerMessagesSQL = "SELECT Message,MessageShown FROM CustomerMessages ORDER BY Message";
    private ResourceBundle bundle= ResourceBundle.getBundle("proffittcenter/resource/CustomerMessages");
    private ResultSet rs;
    private String messageShown;

    /** Creates new form CustomerMessages
     * @param parent
     * @param modal
     */
    public CustomerMessages(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
            initComponents();
            Main.mainHelpBroker.enableHelpKey(getRootPane(), "Customermessages", Main.mainHelpSet);
            getRootPane().setDefaultButton(okButton);
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
        closeButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("proffittcenter/resource/CustomerMessages"); // NOI18N
        setTitle(bundle.getString("Title")); // NOI18N
        setName("customerMessages"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jTable1.setName("jTable1"); // NOI18N
        jScrollPane1.setViewportView(jTable1);

        closeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/Close24.png"))); // NOI18N
        closeButton.setName("closeButton"); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        okButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/proffittcenter/resource/OK.png"))); // NOI18N
        okButton.setName("okButton"); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(closeButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        try {
            String s1,s2;
            //save all the values
            String saveMessagesSQL = "UPDATE CustomerMessages SET " + "MessageShown=? WHERE Message = ?";
            PreparedStatement ps = Main.getConnection().prepareStatement(saveMessagesSQL);
            for(int i=0;i<jTable1.getModel().getRowCount();i++){
                s1=(String) jTable1.getModel().getValueAt(i, 0);
                s2=(String) jTable1.getModel().getValueAt(i, 1);
                if(i == 29){
                    int xx=0;
                }
                ps.setString(2, s1);
                ps.setString(1, s2);
                ps.executeUpdate();
            }
            ps.close();
        } catch (SQLException ex) {
            Logger.getLogger(CustomerMessages.class.getName()).log(Level.SEVERE, null, ex);
        }
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    void execute(){
        Audio.play("TaDa");
        try{
            PreparedStatement ps = Main.getConnection().prepareStatement(customerMessagesSQL);
            HashSet  editable=new HashSet();
            editable.add(1);
            jtm = new JDBCTableModel( ps, bundle, jTable1,editable);
            setVisible(true);
        }catch (SQLException ex) {
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
                CustomerMessages dialog = new CustomerMessages(new javax.swing.JFrame(), true);
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    String getString(String string) {
        try {
            //return customer message for string
            PreparedStatement ps = Main.getConnection().prepareStatement("SELECT MessageShown FROM CustomerMessages "
                    + "WHERE Message=?");
            ps.setString(1, string);
            rs=ps.executeQuery();
            if(rs.first()){
                messageShown=rs.getString("MessageShown");
            } else {
                messageShown="";
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(CustomerMessages.class.getName()).log(Level.SEVERE, null, ex);
            messageShown="";
        }
            return messageShown;
    }

}
